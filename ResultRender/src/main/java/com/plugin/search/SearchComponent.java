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
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.query.*;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.search.v2.lucene.LuceneSearchResults;
import com.atlassian.confluence.search.v2.lucene.LuceneSearchManager;

public class SearchComponent{

	private static Logger log = LoggerFactory.getLogger(SearchComponent.class);
    private AnyTypeDao anyTypeDao;
    private ThumbnailManager thumbnailManager;
    private SearchManager searchManager;
    private String searchContentString;
    private String searchSpaceKeyString;
    
    public SearchComponent(SearchManager searchManager){
    	this.searchManager = searchManager;
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
    	
    	SearchQuery query = BooleanQuery.andQuery( new LabelQuery(searchContentString), new InSpaceQuery(searchSpaceKeyString));
    	SearchSort sort = new ModifiedSort(SearchSort.Order.DESCENDING); // latest modified content first
    	SearchFilter securityFilter = SiteSearchPermissionsSearchFilter.getInstance();
    	ResultFilter resultFilter = new SubsetResultFilter(100);
    	
    	ISearch search = new ContentSearch(query, sort, securityFilter, resultFilter);
    	 
    	SearchResults searchResults;
    	
    	try
    	{
    	    searchResults = searchManager.search(search);
    	}
    	catch (InvalidSearchException e)
    	{
    	    // discard search and assign empty results
    	    searchResults = LuceneSearchResults.EMPTY_RESULTS;
    	}
    	 
    	// iterating over search results
    	for (SearchResult searchResult : searchResults.getAll())
    	{
    	    System.out.println("Title: " + searchResult.getDisplayTitle());
    	    System.out.println("Content: " + searchResult.getContent());
    	    System.out.println("SpaceKey: " + searchResult.getSpaceKey());
    	}
    	 
    	// total number of results found
    	System.out.println("Total number of results: " + searchResults.getUnfilteredResultsCount()); /**/
    	
    	return searchResults;
    }

}
