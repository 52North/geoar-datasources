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
package org.n52.geoar.data.opensearch.descriptiondocument;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.n52.geoar.data.opensearch.OpenSearchConstants;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
public class UrlTemplate {

	protected static class ParameterHolder {
		public final String name;
		public final boolean optional;

		public ParameterHolder(String name, boolean optional) {
			this.name = name;
			this.optional = optional;
		}
	}

	private final String template;
	private final Map<String, Boolean> requirementMap = new HashMap<String, Boolean>();
	private final List<ParameterHolder> parameters = new ArrayList<ParameterHolder>();
	private final List<String> optionalParameters = new ArrayList<String>();
	private final List<String> requiredParameters = new ArrayList<String>();

	public UrlTemplate(Map<String, String> namespacePrefixes, String template) {
		Pattern paramPattern = Pattern.compile("\\{[^\\}]*\\}");
		Matcher matcher = paramPattern.matcher(template);

		while (matcher.find()) {
			String parameter = matcher.group().trim();
			parameter = parameter.substring(1, parameter.length() - 1);

			// init ParameterHolder
			ParameterHolder parameterHolder;
			if (parameter.endsWith("?"))
				parameterHolder = new ParameterHolder(parameter.substring(0,
						parameter.length() - 1), true);
			else
				parameterHolder = new ParameterHolder(parameter.substring(0,
						parameter.length()), false);

			String encodedUrlParameter = null;
			try {
				if (parameterHolder.name.contains(":")) {
					int index = parameterHolder.name.indexOf(":");
					String namespacePrefix = parameterHolder.name.substring(0,
							index);
					String namespaceUrl = null;
					for (Map.Entry<String, String> e : namespacePrefixes
							.entrySet())
						if (e.getValue().equals(namespacePrefix))
							namespaceUrl = e.getKey();
					if (namespaceUrl != null)
						encodedUrlParameter = URLEncoder.encode(namespaceUrl,
								OpenSearchConstants.DEFAULT_ECODING)
								+ ":"
								+ parameterHolder.name.substring(index + 1);
				} else {
					encodedUrlParameter = URLEncoder.encode(
							OpenSearchConstants.OPENSEARCH_NAMESPACE,
							OpenSearchConstants.DEFAULT_ECODING)
							+ ":" + parameterHolder.name;
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			StringBuilder builder = new StringBuilder();
			builder.append("{");
			builder.append(parameterHolder.name);
			if (parameterHolder.optional)
				builder.append("?");
			builder.append("}");
			String oldEntry = builder.toString();

			builder = new StringBuilder();
			builder.append("{");
			builder.append(encodedUrlParameter);
			if (parameterHolder.optional)
				builder.append("?");
			builder.append("}");
			String newEntry = builder.toString();

			template.replace(oldEntry, newEntry);

			parameters.add(parameterHolder);
			requirementMap.put(parameterHolder.name, parameterHolder.optional);
			if (parameterHolder.optional)
				optionalParameters.add(parameterHolder.name);
			else
				requiredParameters.add(parameterHolder.name);
		}
		this.template = template;
	}

	public boolean hasParameter(String parameter) {
		if (requiredParameters.contains(parameter)
				|| optionalParameters.contains(parameter))
			return true;
		return false;
	}

	public List<ParameterHolder> getParameters() {
		return parameters;
	}

	public List<String> getOptionalParameters() {
		return optionalParameters;
	}

	public List<String> getRequiredParameters() {
		return requiredParameters;
	}

	public String getTemplate() {
		return template;
	}

	public boolean isRequiredParameter(String key){
		return requirementMap.get(key);
	}
}
