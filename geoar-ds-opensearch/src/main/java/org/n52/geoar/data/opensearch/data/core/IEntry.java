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
public interface IEntry {
    
    public String getTitle();
//    public IEntry setTitle(String title);
    
//    public String getUri();
//    public IEntry setUri(String uri);
    
    public String getId();
    
    public String getLink();
//    public IEntry setLink(String link);
    
    public List<String> getLinks();
//    public IEntry setLinks(List<String> links);
//    public IEntry addLink(String link);
    
    public String getDescription();
//    public IEntry setDescription(String description);
    
    public String getContents();
//    public IEntry setContents(List<IContent> contents);
//    public IEntry addContent(List<IContent> contents);
    
    public Date getPublisedDate();
//    public IEntry setPublishedDate(Date publishedDate);
    
    public Date getUpdatedDate();
//    public IEntry setUpdatedDate(Date updatedDate);
    
    public List<FeedElement> getCategorys();
//    public IEntry setCategorys(List<ICategory> categories);
//    public IEntry addCategory(ICategory category);
    
    public IFeed getRelatedFeed();
//    public IEntry setRelatedFeed(IFeed feed);
    
    public FeedElement getAuthor();
//    public IEntry setAuthor(String author);
    
    public FeedElement getBbox();
    
    public String getRights();
    
    //enclosures
//    updateddate
//    
//    getauthors
//    
//    getauthor
//    
//    contributors
    

}
