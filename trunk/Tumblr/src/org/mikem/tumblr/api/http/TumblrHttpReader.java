package org.mikem.tumblr.api.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.mikem.tumblr.api.model.TumbleLog;
import org.mikem.tumblr.api.model.User;
import org.mikem.tumblr.api.util.TumblrJProperties;
import org.mikem.tumblr.api.util.TumblrReadOptions;

public class TumblrHttpReader implements ITumblrReader {
    private Log logger = LogFactory.getLog(TumblrHttpReader.class);
	private TumblrConnectionOptions connectionOptions;
	private TumblrJProperties properties;
	
	public User getUserInformation() throws Exception {
		if (this.connectionOptions == null) {
			throw new Exception("Can't connect up because reader doesn't have a configured TumblrConnectionOptions");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Retreiving user information through http post");
		}
		
		HttpClient client = setupHttpClient();
		PostMethod post = setupPostMethod(properties.getAuthenticationPath());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Sending post request to: " + post.getPath());
		}
		
		setAuthenticationInformation(post);
		client.executeMethod(post);
		String response = post.getResponseBodyAsString();
		
		if (logger.isTraceEnabled()) {
			logger.trace("Received response: " + response);
		}
		
		post.releaseConnection();
		Document doc = DocumentHelper.parseText(response);
		return new User(doc);
	}
	
	public void delete(String postId) throws Exception {
		if (this.connectionOptions == null) {
			throw new Exception("Can't connect up because reader doesn't have a configured TumblrConnectionOptions");
		}
		
		HttpClient client = setupHttpClient();
		PostMethod post = setupPostMethod(properties.getDeletePath());
		setAuthenticationInformation(post);
		post.addParameter("post-id", postId);
		client.executeMethod(post);
		post.releaseConnection();		
	}
	
	public TumbleLog read(TumblrReadOptions readOptions) throws Exception {
		if (this.connectionOptions == null) {
			throw new Exception("Can't connect up because reader doesn't have a configured TumblrConnectionOptions");
		}
		
	    HttpClient client = setupHttpClient();
        PostMethod post = setupPostMethod(properties.getReadPath());
        setReadQueryStringParams(post, readOptions);
        System.out.println(post.getParameters());
        client.executeMethod(post);
        String response = post.getResponseBodyAsString();
        post.releaseConnection();

        Document doc = DocumentHelper.parseText(response);
        
        TumbleLog log = new TumbleLog(doc);
		
		return log;
	}
	
	private PostMethod setupPostMethod(String path) {
		String baseurl = StringUtils.replace(properties.getBaseUrl(), "{0}", this.connectionOptions.getName());
		return new PostMethod(baseurl + path);
	}
	
	private HttpClient setupHttpClient() {
		HttpClient client = new HttpClient();
	    if (this.connectionOptions.getHttpClientParams() != null) {
	    	client.setParams(this.connectionOptions.getHttpClientParams());
	    }
	    return client;
	}
	
	private void setAuthenticationInformation(PostMethod post) {
		post.addParameter("username", this.connectionOptions.getEmail());
		post.addParameter("password", this.connectionOptions.getPassword());
	}

	
	private void setReadQueryStringParams(PostMethod post, TumblrReadOptions readOptions) {
		if (readOptions == null) {
			return;
		}
		
		boolean idUsed = false;
		
		if (readOptions.getId() != null && !("").equals(readOptions.getId())) {
			post.addParameter("id", readOptions.getId());
		}
		
		if (readOptions.getNum() > 0 && !idUsed) {
			post.addParameter("num", String.valueOf(readOptions.getNum()));
		}

		if (readOptions.getStart() > 0 && !idUsed) {
			post.addParameter("start", String.valueOf(readOptions.getStart()));
		}

		if (readOptions.getType() != null && !idUsed) {
			post.addParameter("type", readOptions.getType().getValue());
		}

		if (readOptions.isReadPrivate()) {
			post.addParameter("private", "true");
			setAuthenticationInformation(post);
		}
	}
	
	public void setTumblrConnectionOptions(TumblrConnectionOptions connectionOptions) {
		this.connectionOptions = connectionOptions;
	}
	
	public void setProperties(TumblrJProperties properties) {
		this.properties = properties;
	}
	
}
