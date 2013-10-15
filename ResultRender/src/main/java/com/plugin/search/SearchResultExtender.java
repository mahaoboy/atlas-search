package com.plugin.search;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.plugin.search.SearchComponent;
 
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import org.apache.velocity.VelocityContext;

import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResults;

public class SearchResultExtender extends HttpServlet
{

    private final TemplateRenderer templateRenderer;
    private final SearchManager searchManager;
    private static final String TEMPLATE = "templates/searchresult.vm";
    private static final String CONTENT = "searchcontent";
    private static final String SPACEKEY = "spacekey";
    private static final String SEARCHRESULTS = "searchresultcontainer";
    private static Logger log = LoggerFactory.getLogger(SearchResultExtender.class);
    public SearchComponent searchcom;
    
    public SearchResultExtender(TemplateRenderer templateRenderer, SearchManager searchManager)
    {
        this.templateRenderer = templateRenderer;
        this.searchManager = searchManager;
        this.searchcom = new SearchComponent(searchManager);
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
    	log.warn(searchcom.getSearchContentString()+"--------String-------------------------------------------------------------\n");
    	log.warn(searchcom.getSearchSpaceKeyString()+"-------------Key--------------------------------------------------------\n");
    	SearchResults searchResultContainResult = searchcom.searchInComponent();
    	
        
    	response.setContentType("text/html;charset=utf-8");
  	    Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
  
    	contextMap.put(CONTENT, searchcom.getSearchContentString());
    	contextMap.put(SPACEKEY, searchcom.getSearchSpaceKeyString());
    	contextMap.put(SEARCHRESULTS, searchResultContainResult.getAll());

  	    
  	    templateRenderer.render(TEMPLATE, contextMap, response.getWriter());
    }
}