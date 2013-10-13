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
import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import org.apache.velocity.VelocityContext;
  
  
public class SearchResult extends HttpServlet
{

    private final TemplateRenderer templateRenderer;
    private static final String TEMPLATE = "templates/searchresult.vm";
    private static final String CONTENT = "searchcontent";
    private static Logger log = LoggerFactory.getLogger(SearchResult.class);
    
    public SearchResult(TemplateRenderer templateRenderer)
    {
        this.templateRenderer = templateRenderer;
    }
         
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	/*String referer = request.getHeader("referer");
    	log.warn(referer);
    	*/
    	response.setContentType("text/html;charset=utf-8");
  	    Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
  	   	templateRenderer.render(TEMPLATE, contextMap, response.getWriter());
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String value = request.getParameter("searchcontent");
    	String key = request.getParameter("spacekey");
    	//log.warn(value+"Starting====================================================="+value+"Starting=====================================================");
        response.setContentType("text/html;charset=utf-8");
  	    Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
  	    if(!value.isEmpty()){
  	    	contextMap.put(CONTENT, value);
  	    	contextMap.put(CONTENT, key);
  	    }
  	    templateRenderer.render(TEMPLATE, contextMap, response.getWriter());
    }
}