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
package org.n52.geoar.data.opensearch.data.core;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public interface IFeed {
    public String getTitle();
//    public IFeed setTitle(String title);
//    
    public String getFeedType();
//    public IFeed setFeedType(String feedType);
//    
//    public String getEncoding();
//    public IFeed setEncoding(String encoding);
//    
    public String getUri();
//    public IFeed setUri(String uri);
//    
//    public String getLink();
//    public IFeed setLink(String link);
//    
    public String getDescription();
//    public IFeed setDescription(String description);
//    
    public Date getPublishedDate();
//    public IFeed setPublishedDate(Date publishedDate);
//    
//    public String getAuthor();
//    public IFeed setAuthor();
//    public List<String> getAuthors();
//    public IFeed setAuthors(List<String> authors);
//    
//    public IImage getImage();
//    public IFeed setImage(IImage image);
//    
    public List<FeedElement> getCategories();
//    public IFeed setCategories(List<ICategory> categories);
//    public IFeed addCategory(ICategory category);
//    
    public List<FeedElement> getEntrys();
//    public IFeed setEntrys(List<IEntry> entries);
//    public IFeed addEntry(IEntry entry);
//    
//    public String getLanguage(String language);
//    public IFeed setLangauge(String language);
    
    // TODO Modules!?
}
