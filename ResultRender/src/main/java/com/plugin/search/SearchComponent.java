package com.plugin.search;

import antlr.collections.List;

import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.plugin.SearchResultRenderer;
import com.atlassian.confluence.search.SearchResultRenderContext;
import com.atlassian.confluence.search.v2.SearchResult;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.confluence.search.v2.searchfilter.SiteSearchPermissionsSearchFilter;
import com.atlassian.confluence.search.v2.filter.SubsetResultFilter;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.*;
import com.atlassian.confluence.search.v2.query.*;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.search.v2.lucene.LuceneSearchResult;
import com.atlassian.confluence.search.v2.lucene.LuceneSearchResults;
import com.atlassian.confluence.search.v2.lucene.LuceneSearchManager;

public class SearchComponent{

	private static Logger log = LoggerFactory.getLogger(SearchComponent.class);
    private AnyTypeDao anyTypeDao;
    private ThumbnailManager thumbnailManager;
    private SearchManager searchManager;
    private String searchContentString;
    private String searchSpaceKeyString;
    private static final Set<String> SUPPORTED_MIMETYPES = new HashSet<String>();
    static
    {
        SUPPORTED_MIMETYPES.add("image/png");
        SUPPORTED_MIMETYPES.add("image/jpeg");
        SUPPORTED_MIMETYPES.add("image/gif");
        SUPPORTED_MIMETYPES.add("application/png");
        SUPPORTED_MIMETYPES.add("application/gif");
        SUPPORTED_MIMETYPES.add("application/jpeg");
        SUPPORTED_MIMETYPES.add("image/pjpeg"); 
        SUPPORTED_MIMETYPES.add("image/x-png");
        SUPPORTED_MIMETYPES.add("video/mp4");
    }
    private static final Set<String> IMAGE_MIMETYPES = new HashSet<String>();
    static
    {
    	IMAGE_MIMETYPES.add("image/png");
    	IMAGE_MIMETYPES.add("image/jpeg");
    	IMAGE_MIMETYPES.add("image/gif");
    	IMAGE_MIMETYPES.add("application/png");
    	IMAGE_MIMETYPES.add("application/gif");
    	IMAGE_MIMETYPES.add("application/jpeg");
    	IMAGE_MIMETYPES.add("image/pjpeg"); 
    	IMAGE_MIMETYPES.add("image/x-png");
    }
    private static final Set<String> VEDIO_MIMETYPES = new HashSet<String>();
    static
    {
    	VEDIO_MIMETYPES.add("video/mp4");
    }
    
    public SearchComponent(SearchManager searchManager, ThumbnailManager thumbnailManager, AnyTypeDao anyTypeDao){
    	this.searchManager = searchManager;
    	this.thumbnailManager = thumbnailManager;
    	this.anyTypeDao = anyTypeDao;
    }
    
    public void setSearchContentString(String searchContentString){
    	this.searchContentString = searchContentString;
    }
    
    public String getSearchContentString(){
    	return this.searchContentString;
    }
    
    public void setSearchSpaceKeyString(String searchSpaceKeyString){
    	this.searchSpaceKeyString = searchSpaceKeyString;
    }
    
    public String getSearchSpaceKeyString(){
    	return this.searchSpaceKeyString;
    }
    
    public SearchResults searchInComponent(){

    	SearchQuery query = searchSpaceKeyString.isEmpty()?BooleanQuery.andQuery( new LabelQuery(searchContentString)):BooleanQuery.andQuery( new LabelQuery(searchContentString), new InSpaceQuery(searchSpaceKeyString));
    	SearchSort sort = new ModifiedSort(SearchSort.Order.DESCENDING); // latest modified content first
    	SearchFilter securityFilter = SiteSearchPermissionsSearchFilter.getInstance();
    	ResultFilter resultFilter = new SubsetResultFilter(100);
    	
    	ISearch search = new ContentSearch(query, sort, securityFilter, resultFilter);
    	 
    	SearchResults searchResults = null;
    	
    	try
    	{
    	    searchResults = searchManager.search(search);
    	}
    	catch (InvalidSearchException e)
    	{
    	    // discard search and assign empty results
    	    searchResults = LuceneSearchResults.EMPTY_RESULTS;
    	}
    	 
    	// total number of results found
    	System.out.println("Total number of results: " + searchResults.getUnfilteredResultsCount()); /**/
    	
    	return searchResults;
    }
    
    public String getAttachmentTypeString(SearchResult searchResult) {
		String attachmentType = "Content";
		
		if (searchResult.getHandle() == null || searchResult.getExtraFields() == null){
			return attachmentType;
	    }
		else{
			String mimeType = searchResult.getExtraFields().get("attachmentMimeType");
			
			if (!SUPPORTED_MIMETYPES.contains(StringUtils.defaultString(mimeType).toLowerCase())){
 	        	return attachmentType;
 	        }
 	        else if (IMAGE_MIMETYPES.contains(StringUtils.defaultString(mimeType).toLowerCase())){
 	        	return "Image";
 	        }
 	        else if (VEDIO_MIMETYPES.contains(StringUtils.defaultString(mimeType).toLowerCase())){
 	        	return "Vedio";
 	        }
 	        else{
 	        	return attachmentType;
 	        }
 	    }
    }

    public ThumbnailInfo getThumbnailInfo(SearchResult searchResult){
		Object o = null;
		ThumbnailInfo info = null;
		
		if (searchResult.getHandle() == null || searchResult.getExtraFields() == null){
			return info;
	    }
		else{
			final String mimeType = searchResult.getExtraFields().get("attachmentMimeType");
			//log.warn("\n mimeType ====== " + mimeType);
 	        if (!SUPPORTED_MIMETYPES.contains(StringUtils.defaultString(mimeType).toLowerCase())){
 	        	return info;
 	        }
		}
		
		try	{
			 o = anyTypeDao.findByHandle(searchResult.getHandle());
		} catch (NullPointerException exception){
			log.warn("Exception thrown when generating thumbnail for attachment", exception);
		}
		 
		if (o  instanceof Attachment && thumbnailManager.isThumbnailable((Attachment) o))
	        { 
	            try
	            {
	                info = thumbnailManager.getThumbnailInfo((Attachment) o);
	                //log.warn("\n thumbUrl url: " +info.getThumbnailUrlPath());
	                //log.warn("\n page url: " +searchResult.getExtraFields().get("containingContentUrlPath"));
	                return info;
	            } catch (CannotGenerateThumbnailException e)
	            {
	                log.warn("Exception thrown when generating thumbnail for attachment", e);
	            }
	        }
		else{
			log.warn("\nNot a Thumbnailable or Not an attachment\n");
		}
		return info;
    }
    
	public AnyTypeDao getAnyTypeDao() {
		return anyTypeDao;
	}

	public void setAnyTypeDao(AnyTypeDao anyTypeDao) {
		this.anyTypeDao = anyTypeDao;
	}

}
