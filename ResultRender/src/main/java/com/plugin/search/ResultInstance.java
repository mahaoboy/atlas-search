package com.plugin.search;

import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.search.v2.SearchResult;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultInstance {
	private String attachmentTypeString;
	private SearchResult searchResult;
	private ThumbnailInfo thumbnailInfo;
	
	
	public ResultInstance(SearchResult searchResult) {
		this.searchResult = searchResult;
	}
	
	public String getAttachmentTypeString() {
		return attachmentTypeString;
	}
	public void setAttachmentTypeString(String attachmentTypeString) {
		this.attachmentTypeString = attachmentTypeString;
	}
	public SearchResult getSearchResult() {
		return searchResult;
	}
	public void setSearchResult(SearchResult searchResult) {
		this.searchResult = searchResult;
	}
	public ThumbnailInfo getThumbnailInfo() {
		return thumbnailInfo;
	}
	public void setThumbnailInfo(ThumbnailInfo thumbnailInfo) {
		this.thumbnailInfo = thumbnailInfo;
	}

}
