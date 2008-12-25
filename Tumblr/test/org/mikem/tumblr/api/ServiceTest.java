package org.mikem.tumblr.api;

import junit.framework.Assert;

import org.junit.Test;
import org.mikem.tumblr.api.http.TumblrConnectionOptions;
import org.mikem.tumblr.api.http.TumblrHttpReader;
import org.mikem.tumblr.api.model.TumbleFeed;
import org.mikem.tumblr.api.model.TumbleLog;

public class ServiceTest {

	@Test
	public void testRead() throws Exception {
		TumblrConnectionOptions connectionOptions = new TumblrConnectionOptions();
		connectionOptions.setName("ensonik");
		
		TumblrHttpReader reader = new TumblrHttpReader();
		reader.setTumblrConnectionOptions(connectionOptions);
		
		TumblrService service = new TumblrService();
		service.setReader(reader);
		TumbleLog log = service.read(null);
		
		Assert.assertEquals(log.getName(), "ensonik");
		Assert.assertNull(log.getCname());
		Assert.assertEquals(log.getTitle(), "the higher you fly");
		Assert.assertEquals(log.getTimezone(), "US/Eastern");
		Assert.assertTrue(log.getDescription().startsWith("The future you have "));
		Assert.assertEquals(new Integer(5), log.getTotal());
		Assert.assertEquals(new Integer(0), log.getStart());
		Assert.assertEquals(2, log.getFeeds().size());
		
		TumbleFeed feed = log.findFeedById("336714");
		Assert.assertNotNull(feed.getId());
		Assert.assertEquals(feed.getUrl(), "http://feeds.delicious.com/v2/rss/ensonik?count=15");
		Assert.assertEquals(feed.getTitle(), "Delicious/ensonik");
		Assert.assertEquals(feed.getType(), "link");
	}
	
}
