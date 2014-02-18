/**
 * Copyright 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.geoar.data.sos.view;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.n52.geoar.data.R;
import org.n52.geoar.data.sos.SOSResponse;
import org.n52.geoar.data.sos.SOSResponse.CapabilitiesResult;
import org.n52.geoar.data.sos.SOSResponse.Observation;
import org.n52.geoar.data.sos.SOSResponse.Offering;
import org.n52.geoar.utils.DataSourceLoggerFactory;
import org.n52.geoar.utils.DataSourceLoggerFactory.Logger;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class SOSObservationView extends ViewFlipper {

	public abstract static class ObservationCallable implements
			Callable<List<Observation>> {

		private Date begin;
		private Date end;
		private Offering offering;

		@Override
		public final List<Observation> call() throws Exception {
			return call(offering, begin, end);
		}

		protected abstract List<Observation> call(Offering offering,
				Date begin, Date end) throws Exception;

	}

	private class DateTimeViewWrapper implements OnTimeSetListener,
			OnDateSetListener {
		private Calendar calendar;
		private EditText dateEditText;
		private EditText timeEditText;

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (calendar == null)
				calendar = Calendar.getInstance();
			calendar.set(year, month, day);
			updateViews();
			predefindIntervalSpinner.setSelection(0);
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (calendar == null)
				calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			updateViews();
			predefindIntervalSpinner.setSelection(0);
		}

		public DateTimeViewWrapper(int dateId, int timeId) {
			dateEditText = (EditText) findViewById(dateId);
			dateEditText.setInputType(InputType.TYPE_NULL);
			dateEditText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (calendar == null)
						calendar = Calendar.getInstance();

					DatePickerDialog dialog = new DatePickerDialog(
							activityContext, DateTimeViewWrapper.this, calendar
									.get(Calendar.YEAR), calendar
									.get(Calendar.MONTH), calendar
									.get(Calendar.DAY_OF_MONTH));
					dialog.show();
				}
			});

			timeEditText = (EditText) findViewById(timeId);
			timeEditText.setInputType(InputType.TYPE_NULL);
			timeEditText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (calendar == null)
						calendar = Calendar.getInstance();

					TimePickerDialog dialog = new TimePickerDialog(
							activityContext, DateTimeViewWrapper.this, calendar
									.get(Calendar.HOUR_OF_DAY), calendar
									.get(Calendar.MINUTE), true);
					dialog.show();
				}
			});
		}

		private void updateViews() {
			timeEditText.setText(calendar != null ? DateFormat.getTimeFormat(
					getContext()).format(calendar.getTime()) : "");

			dateEditText.setText(calendar != null ? DateFormat.getDateFormat(
					getContext()).format(calendar.getTime()) : "");
		}

		public boolean isSet() {
			return calendar != null;
		}

		public void setError(String msg) {
			dateEditText.setError(msg);
		}

		public void setDate(Date date) {
			if (calendar == null)
				calendar = Calendar.getInstance();
			calendar.setTime(date);
			updateViews();
		}

		public void setDate(Calendar calendar) {
			this.calendar = calendar;
			updateViews();
		}
	}

	private Spinner offeringSpinner;
	private ArrayAdapter<Offering> offeringAdapter;
	private GetCapabilitiesTask getCapabilitiesTask;
	private GetObservationTask getObservationTask;
	private ProgressBar progressOffering;
	private CapabilitiesResult capabilitiesResult;
	private Callable<CapabilitiesResult> capabilitiesCallable;
	private ObservationCallable observationCallable;
	private DateTimeViewWrapper beginDateTime;
	private DateTimeViewWrapper endDateTime;
	private Context activityContext;
	private Button showObservationButton;
	private Offering offering;
	private FrameLayout chartLayout;
	private Button settingsButton;
	private ProgressBar progressObservation;
	private TextView offeringDetails;
	private GraphicalView currentChartView;
	private Spinner predefindIntervalSpinner;

	private static final Logger LOG = DataSourceLoggerFactory
			.getLogger(SOSObservationView.class);

	private class GetCapabilitiesTask
			extends
			AsyncTask<Callable<CapabilitiesResult>, Void, SOSResponse.CapabilitiesResult> {

		private Throwable exception;

		@Override
		protected void onPreExecute() {
			progressOffering.setVisibility(View.VISIBLE);
		}

		@Override
		protected CapabilitiesResult doInBackground(
				Callable<CapabilitiesResult>... params) {
			try {
				return params[0].call();
			} catch (Throwable e) {
				exception = e;
				LOG.error("Error requesting capabilities", exception);
			}
			return null;
		}

		protected void onPostExecute(CapabilitiesResult result) {
			progressOffering.setVisibility(View.GONE);

			if (exception != null) {
				Toast.makeText(getContext(),
						R.string.error_requesting_capabilities,
						Toast.LENGTH_LONG).show();
				return;
			}

			setCapabilitiesResult(result);
		};
	};

	private class GetObservationTask extends
			AsyncTask<Callable<List<Observation>>, Void, List<Observation>> {

		private Throwable exception;

		@Override
		protected void onPreExecute() {
			progressObservation.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<Observation> doInBackground(
				Callable<List<Observation>>... params) {
			try {
				return params[0].call();
			} catch (Throwable e) {
				LOG.error("Error requesting observation", e);
				exception = e;
			}
			return null;
		}

		protected void onPostExecute(List<Observation> result) {
			progressObservation.setVisibility(View.GONE);

			if (exception != null) {
				Toast.makeText(getContext(),
						R.string.error_requesting_observation,
						Toast.LENGTH_LONG).show();
				return;
			}
			setObservations(result);

		};
	};

	public SOSObservationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	private void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.sosobservationview,
				this);

		progressOffering = (ProgressBar) findViewById(R.id.progressBarOffering);
		progressObservation = (ProgressBar) findViewById(R.id.progressBarObservation);

		offeringSpinner = (Spinner) findViewById(R.id.spinnerOffering);
		offeringAdapter = new ArrayAdapter<Offering>(getContext(),
				android.R.layout.simple_spinner_item);
		offeringAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		offeringSpinner.setAdapter(offeringAdapter);
		offeringSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				offering = (Offering) offeringSpinner.getSelectedItem();
				java.text.DateFormat dateFormat = java.text.DateFormat
						.getDateTimeInstance(java.text.DateFormat.SHORT,
								java.text.DateFormat.SHORT);

				offeringDetails.setText(getResources().getString(
						R.string.offering_details, offering.getId(),
						dateFormat.format(offering.getBegin()),
						dateFormat.format(offering.getEnd())));
				predefindIntervalSpinner.setSelection(0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				offering = null;
				offeringDetails.setText("");
			}
		});

		offeringDetails = (TextView) findViewById(R.id.textViewOfferingDetails);

		beginDateTime = new DateTimeViewWrapper(R.id.editTextFromDate,
				R.id.editTextFromTime);
		endDateTime = new DateTimeViewWrapper(R.id.editTextToDate,
				R.id.editTextToTime);

		predefindIntervalSpinner = (Spinner) findViewById(R.id.spinnerInterval);
		predefindIntervalSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					private void setInterval(int calendarField, int shiftValue) {
						endDateTime.setDate(offering.getEnd());
						Calendar beginCalendar = Calendar.getInstance();
						beginCalendar.setTime(offering.getEnd());
						beginCalendar.add(calendarField, shiftValue);
						if (offering.getBegin().after(beginCalendar.getTime())) {
							beginCalendar.setTime(offering.getBegin());
						}
						beginDateTime.setDate(beginCalendar);
					}

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if (offering == null || offering.getBegin() == null
								|| offering.getEnd() == null) {
							return;
						}
						switch (position) {
						case 1:
							setInterval(Calendar.DAY_OF_MONTH, -1);
							break;
						case 2:
							setInterval(Calendar.DAY_OF_MONTH, -7);
							break;
						case 3:
							setInterval(Calendar.MONTH, -1);
							break;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		showObservationButton = (Button) findViewById(R.id.buttonUpdateObservation);
		showObservationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (loadObservationFromCallable()) {
					setDisplayedChild(1);
				}
			}
		});

		settingsButton = (Button) findViewById(R.id.buttonSettings);
		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setDisplayedChild(0);
			}
		});

		findViewById(R.id.buttonZoomIn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentChartView != null) {
							currentChartView.zoomIn();
						}
					}
				});

		findViewById(R.id.buttonZoomOut).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentChartView != null) {
							currentChartView.zoomOut();
						}
					}
				});

		findViewById(R.id.buttonZoomReset).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentChartView != null) {
							currentChartView.zoomReset();
						}
					}
				});

		chartLayout = (FrameLayout) findViewById(R.id.layoutChartObservation);
	}

	public void setCapabilitiesCallable(
			Callable<CapabilitiesResult> capabilitiesCallable) {
		this.capabilitiesCallable = capabilitiesCallable;
	}

	public void setObservationCallable(ObservationCallable observationCallable) {
		this.observationCallable = observationCallable;
	}

	private void loadCapabilitiesFromCallable() {
		if (getCapabilitiesTask != null
				&& getCapabilitiesTask.getStatus() != Status.FINISHED) {
			getCapabilitiesTask.cancel(true);
		}
		if (capabilitiesCallable != null) {
			getCapabilitiesTask = new GetCapabilitiesTask();
			getCapabilitiesTask.execute(capabilitiesCallable);
		}
	}

	private boolean loadObservationFromCallable() {
		if (getObservationTask != null
				&& getObservationTask.getStatus() != Status.FINISHED) {
			getObservationTask.cancel(true);
		}
		if (observationCallable != null) {
			if (offering == null) {
				return false;
			}

			boolean changes = false;
			if (offering.getBegin() != null) {
				if (!beginDateTime.isSet()) {
					beginDateTime.setError("Required");
					return false;
				}
				if (beginDateTime.calendar.getTime()
						.before(offering.getBegin())) {
					beginDateTime.setError("Must be after "
							+ offering.getBegin());
					return false;
				}

				if (!beginDateTime.calendar.getTime().equals(
						observationCallable.begin)) {
					changes = true;
				}
				beginDateTime.setError(null);
			}

			if (offering.getEnd() != null) {
				if (!endDateTime.isSet()) {
					endDateTime.setError("Required");
					return false;
				}
				if (endDateTime.calendar.getTime().after(offering.getEnd())) {
					endDateTime.setError("Must be before " + offering.getEnd());
					return false;
				}

				if (!endDateTime.calendar.getTime().equals(
						observationCallable.end)) {
					changes = true;
				}
				endDateTime.setError(null);
			}

			if (offering.getBegin() != null && offering.getEnd() != null) {
				if (endDateTime.calendar.getTime().before(
						beginDateTime.calendar.getTime())) {
					endDateTime.setError("Must be after begin");
					return false;
				}
			}

			if (!offering.equals(observationCallable.offering)) {
				changes = true;
			}

			if (changes) {
				observationCallable.offering = offering;
				observationCallable.begin = beginDateTime.calendar.getTime();
				observationCallable.end = endDateTime.calendar.getTime();

				getObservationTask = new GetObservationTask();
				getObservationTask.execute(observationCallable);
			} else {
				return true;
			}
		}
		return false;
	}

	private void setCapabilitiesResult(CapabilitiesResult capabilitiesResult) {
		this.capabilitiesResult = capabilitiesResult;
		offering = null;
		offeringAdapter.clear();
		if (capabilitiesResult == null) {
			return;
		}
		for (Offering offering : capabilitiesResult.getOfferings()) {
			offeringAdapter.add(offering);
		}
	}

	public void setObservations(List<Observation> result) {
		if (result == null) {
			// TODO
			return;
		}
		if (result.isEmpty()) {
			Toast.makeText(getContext(), R.string.empty_result,
					Toast.LENGTH_LONG).show();
		}
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		for (Observation obs : result) {
			TimeSeries series = new TimeSeries(offering.getName() + " ["
					+ obs.getUom() + "]");
			XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();

			for (Observation.Value value : obs.getValues()) {
				series.add(value.getSamplingTime(), value.getQuantity());
			}
			dataset.addSeries(series);

			seriesRenderer.setPointStyle(PointStyle.CIRCLE);
			seriesRenderer.setColor(Color.GREEN);
			renderer.addSeriesRenderer(seriesRenderer);

		}

		renderer.setShowGrid(true);
		renderer.setXLabels(4);
		renderer.setYLabels(6);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.CENTER);
		renderer.setZoomButtonsVisible(false); // Use own buttons
		renderer.setExternalZoomEnabled(true);
		renderer.setBackgroundColor(Color.TRANSPARENT);
		renderer.setMarginsColor(Color.argb(0, 1, 1, 1)); // TODO get real color
															// of actual
															// background
															// dynamically
		renderer.setAntialiasing(true);
		renderer.setFitLegend(true);
		renderer.setYLabelsAlign(Align.LEFT);

		// achartengine does not scale font sizes, so apply correct value
		// manually here
		float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				12, getResources().getDisplayMetrics());
		renderer.setLegendTextSize(textSize);
		renderer.setLabelsTextSize(textSize);

		// achartengine does not scale margins correctly, apply correct values
		// here
		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
						.getDisplayMetrics());
		renderer.setMargins(new int[] { margin, margin, margin, margin });

		chartLayout.removeAllViews();
		currentChartView = ChartFactory.getTimeChartView(getContext(), dataset,
				renderer, null);
		chartLayout.addView(currentChartView,
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		setDisplayedChild(1);
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (this.isShown() && capabilitiesResult == null
				&& capabilitiesCallable != null && getCapabilitiesTask == null) {

			loadCapabilitiesFromCallable();
		}
	}

	public void setActivityContext(Context activityContext) {
		this.activityContext = activityContext;
	}

}
