package dataextraction;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class SearchReplies implements Runnable
{
	public static String dbName;
	public static String collectionName;
	public static String keyword;
	public static int max;
	public static ConfigurationBuilder configurationBuilder;
	public static String screenName;
	public static long tweetID;
	
	@Override
	public void run() {
		Configuration configuration = configurationBuilder.build();
		TwitterFactory factory = new TwitterFactory(configuration);
		Twitter twitter = factory.getInstance();
				
		AccessToken accessToken = new AccessToken("61642611-7s1TG2UixsXtSyTOQkl0e4f1bcYpHPFbZGzirTv4H", "t7iqUkg32d7uy6NgnIrhojxnvDCeI3rfOjm8LY2h7nNBR");
		twitter.setOAuthAccessToken(accessToken);	
		
		ArrayList<Status> replies = new ArrayList<>();

	    try
	    {
	        Query query = new Query("to:" + screenName + " since_id:" + tweetID);
	        query.setCount(100);
	        
	        QueryResult results;

	        do
	        {
	            results = twitter.search(query);
	            System.out.println("Results: " + results.getTweets().size());
	            List<Status> tweets = results.getTweets();

	            for (Status tweet : tweets) 
	                if (tweet.getInReplyToStatusId() == tweetID)
	                    replies.add(tweet);
	            
	            Thread.sleep(60000);
	        } while ((query = results.nextQuery()) != null);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public SearchReplies(String dbName, String collectionName, ConfigurationBuilder configurationBuilder)
	{
		SearchReplies.dbName = dbName;
		SearchReplies.collectionName = collectionName;
		SearchReplies.configurationBuilder = configurationBuilder;
	}
	
	public static void main(String[] args)
	{
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		
	    configurationBuilder.setDebugEnabled(true);
		configurationBuilder.setOAuthConsumerKey("DKAvJEgggEg2j43XprzvNnqzy");
		configurationBuilder.setOAuthConsumerSecret("f9O4jVUUATMryuzNHuunQUrAnrdrSQgVaehv7iJJRXnRA13A1W");
		configurationBuilder.setOAuthAccessToken("61642611-7s1TG2UixsXtSyTOQkl0e4f1bcYpHPFbZGzirTv4H");
		configurationBuilder.setOAuthAccessTokenSecret("t7iqUkg32d7uy6NgnIrhojxnvDCeI3rfOjm8LY2h7nNBR");
		configurationBuilder.setJSONStoreEnabled(true);
		
		Runnable r = new SearchReplies("twitter", "equifax", configurationBuilder);
		new Thread(r).start();
	}
}
