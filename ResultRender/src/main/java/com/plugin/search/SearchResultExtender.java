package com.plugin.search;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import antlr.collections.List;

import com.plugin.search.SearchComponent;
 
import java.net.URI;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import org.apache.velocity.VelocityContext;

import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResults;
import com.plugin.search.ResultInstance;

import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;

public class SearchResultExtender extends HttpServlet
{

    private final TemplateRenderer templateRenderer;
    private final SearchManager searchManager;
    private static final String TEMPLATE = "templates/searchresult.vm";
    private static final String CONTENT = "searchcontent";
    private static final String SPACEKEY = "spacekey";
    private static final String URLENCODER = "urlEncoder";
    private static final String SEARCHRESULTS = "searchresultcontainer";
    private static Logger log = LoggerFactory.getLogger(SearchResultExtender.class);
    public SearchComponent searchcom;
    public static  ArrayList<ResultInstance> resultInstances = new ArrayList<ResultInstance>();
    private URLEncoder urlEncoder;

    public SearchResultExtender(TemplateRenderer templateRenderer, SearchManager searchManager, ThumbnailManager thumbnailManager, AnyTypeDao anyTypeDao)
    {
        this.templateRenderer = templateRenderer;
        this.searchManager = searchManager;
        this.searchcom = new SearchComponent(searchManager, thumbnailManager, anyTypeDao);
    }
         
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	response.setContentType("text/html;charset=utf-8");
  	    Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
  	    
  	   	templateRenderer.render(TEMPLATE, contextMap, response.getWriter());
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String value = request.getParameter("searchcontent").isEmpty()?"":request.getParameter("searchcontent");
    	String key = request.getParameter("spacekey").isEmpty()?"":request.getParameter("spacekey");

    	searchcom.setSearchContentString(value);
    	searchcom.setSearchSpaceKeyString(key);
    	SearchResults searchResultContainer = searchcom.searchInComponent();
    	
        
    	response.setContentType("text/html;charset=utf-8");
  	    Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
  	    resultInstances.clear();
    	for (SearchResult searchResult : searchResultContainer.getAll()){

    		ResultInstance resultInstance = new ResultInstance(searchResult);
    		
    		String attachmentTypeString = searchcom.getAttachmentTypeString(searchResult);
    		resultInstance.setAttachmentTypeString(attachmentTypeString);

    		if(attachmentTypeString == "Content"){
    			continue;
    		}
    		//log.warn("\n mimeType ====== 5 " + attachmentTypeString);
    		if(attachmentTypeString == "Image"){
    			ThumbnailInfo thumbnailInfoInstance = searchcom.getThumbnailInfo(searchResult);
    			resultInstance.setThumbnailInfo(thumbnailInfoInstance);
    		}
    		
    		try{
    			resultInstances.add(resultInstance);
    		}
    		catch(Exception e){
    			log.warn("\n  Null instance " + e);
    		}
    	}
  	    
    	contextMap.put(CONTENT, searchcom.getSearchContentString());
    	contextMap.put(SPACEKEY, searchcom.getSearchSpaceKeyString());
    	contextMap.put(SEARCHRESULTS, resultInstances);
    	contextMap.put(URLENCODER, urlEncoder);
    	//contextMap.put(SEARCHRESULTS, searchResultContainer.getAll());
  	    
  	    templateRenderer.render(TEMPLATE, contextMap, response.getWriter());
    }
}