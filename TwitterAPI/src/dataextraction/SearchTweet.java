package dataextraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Queue;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import Utils.PropertyUtils;
import examples.Tweet;
import examples.UserTwitter;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

public class SearchTweet implements Runnable
{
	public static String dbName;
	public static String collectionName;
	public static String keyword;
	public static int max;
	public static ConfigurationBuilder configurationBuilder;
	
	
	// Parse Twitter4j's Status Object to a Document object to save on MongoDB
	public static Document parseStatus (Status status)
	{
		Gson gson = new Gson();
		String json = gson.toJson(status);
				
		Document document = Document.parse(json);
		document.put("timestamp_ms", new String(String.valueOf(status.getCreatedAt().getTime())));
		document.put("isRetweet", status.isRetweet());
		document.put("inReplyToScreeName", status.getInReplyToScreenName());
		
		if (status.getRetweetedStatus() != null) document.put("target timestamp", new String(String.valueOf(status.getRetweetedStatus().getCreatedAt().getTime())));
		else if (status.getQuotedStatus() != null)
		{
			document.put("target timestamp", new String(String.valueOf(status.getQuotedStatus().getCreatedAt().getTime())));
			document.put("isQuote", true);
		}
		
		return document;
	}
	
	// Collect tweets related to a keyword
	public static void search()
	{
		TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
		Twitter twitter = twitterFactory.getInstance();
		
		Query query = new Query(keyword);
		query.setCount(100);
		
		List<Status> tweets = new ArrayList<Status>();
	    long lastID = Long.MAX_VALUE;
	    int count = 0;
	    
	    while (true)
		{
			try
		    {
		    	QueryResult result = twitter.search(query);
		    	
		    	if (count < max && result.getCount() != 0)
		    	{
		    		count += result.getCount();
		    		tweets.addAll(result.getTweets());
			    	
		    		System.out.println("Number of tweets gathered: " + tweets.size());
			    
			    	for (Status t: tweets) 
			    		if(t.getId() < lastID) 
			    			lastID = t.getId();
			    	
			    	query.setMaxId(lastID - 1);
		    	}
		    	else break;
		    }
		
		    catch (TwitterException te)
		    {
		    	System.out.println("Couldn't connect: " + te);
		    }; 
		}
	    
	    // Establishing connection to the DB
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
	    
		int i = 0;
	    for (Status status : tweets)
	    {
	    	collection.insertOne(parseStatus(status));
	    	System.out.println("Saving tweet number: " + i);
	    	
	    	i++;
	    }
	    
		mongoClient.close();
	}
	
	// Returns a single tweet by its ID
	public static Status getTweet(String tweet_id, Configuration configuration) throws InterruptedException
	{
		TwitterFactory factory = new TwitterFactory(configuration);
		Twitter twitter = factory.getInstance();
				
		AccessToken accessToken = new AccessToken("61642611-7s1TG2UixsXtSyTOQkl0e4f1bcYpHPFbZGzirTv4H", "t7iqUkg32d7uy6NgnIrhojxnvDCeI3rfOjm8LY2h7nNBR");
		twitter.setOAuthAccessToken(accessToken);
	    
	    Status status;
		try
		{
			if (tweet_id != null)
			{
				status = twitter.showStatus(Long.parseLong(tweet_id));
				Thread.sleep(4000);
				return status;
			}
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TwitterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return null;
	}
	
	// Return replies to a tweet by the tweet's id
	public static ArrayList<Status> getReplies(String screenName, long tweetID, Configuration configuration) {
		TwitterFactory factory = new TwitterFactory(configuration);
		Twitter twitter = factory.getInstance();
				
		AccessToken accessToken = new AccessToken("61642611-7s1TG2UixsXtSyTOQkl0e4f1bcYpHPFbZGzirTv4H", "t7iqUkg32d7uy6NgnIrhojxnvDCeI3rfOjm8LY2h7nNBR");
		twitter.setOAuthAccessToken(accessToken);	
		
		ArrayList<Status> replies = new ArrayList<>();
		System.out.println(screenName + " " + tweetID);
	    try
	    {
	        Query query = new Query("to:" + screenName + " since_id:" + tweetID);
	        query.setCount(100);
	        
	        QueryResult results;

	        do
	        {
	        	results = twitter.search(query);
	            List<Status> tweets = results.getTweets();

	            for (Status tweet : tweets) 
	                if (tweet.getInReplyToStatusId() == tweetID)
	                    replies.add(tweet);
	            
	            Thread.sleep(5000);
	        } while ((query = results.nextQuery()) != null);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return replies;
	}
	
	public static ResponseList<Status> getRetweets(long tweetID, Configuration configuration) {
		TwitterFactory factory = new TwitterFactory(configuration);
		
		AccessToken accessToken = new AccessToken("61642611-7s1TG2UixsXtSyTOQkl0e4f1bcYpHPFbZGzirTv4H", "t7iqUkg32d7uy6NgnIrhojxnvDCeI3rfOjm8LY2h7nNBR");
		
		Twitter twitter = factory.getInstance();
		twitter.setOAuthAccessToken(accessToken);	
		
		ResponseList<Status> retweets;
		try
		{
			retweets = twitter.getRetweets(tweetID);
			return retweets;
		}
		catch (TwitterException e)
		{
			return null;
		}
	}
	
	public SearchTweet(String keyword, String dbName, String collectionName, int max, ConfigurationBuilder configurationBuilder)
	{
		SearchTweet.keyword = keyword;
		SearchTweet.dbName = dbName;
		SearchTweet.collectionName = collectionName;
		SearchTweet.max = max;
		SearchTweet.configurationBuilder = configurationBuilder;
	}
	
	@Override
	public void run() {
		TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
		Twitter twitter = twitterFactory.getInstance();
		
		Query query = new Query(keyword);
		query.setCount(100);
		
		List<Status> tweets = new ArrayList<Status>();
	    long lastID = Long.MAX_VALUE;
	    int count = 0;
	    
	    while (true)
		{
			try
		    {
		    	QueryResult result = twitter.search(query);
		    	
		    	if (count < max && result.getTweets().size() != 0)
		    	{
		    		count += result.getCount();
		    		tweets.addAll(result.getTweets());
			    	
		    		System.out.println("Number of tweets gathered: " + tweets.size());
			    
			    	for (Status t: tweets) 
			    		if(t.getId() < lastID) 
			    			lastID = t.getId();
			    	
			    	query.setMaxId(lastID - 1);
			    	
			    	Thread.sleep(5000);
		    	}
		    	else break;
		    }
		
		    catch (TwitterException te)
		    {
		    	System.out.println("Couldn't connect: " + te);
		    } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}; 
		}
	    
	    // Establishing connection to the DB
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
	    
		int i = 1;
	    for (Status status : tweets)
	    {
	    	collection.insertOne(parseStatus(status));
	    	System.out.println("Saving tweet number: " + i);
	    	i++;
	    }
	    
		mongoClient.close();
	}
	
	public static Tweet buildParentInformation (long id, Configuration configuration) throws InterruptedException
	{
		Status tweet = getTweet(String.valueOf(id), configuration);
		if (tweet == null) return null;		
		
		if (tweet.getRetweetedStatus() != null)
			return buildParentInformation(tweet.getRetweetedStatus().getId(), configuration);
		else if (tweet.getQuotedStatus() != null)
			return buildParentInformation(tweet.getQuotedStatus().getId(), configuration);
		else if (tweet.getInReplyToStatusId() != -1)
			return buildParentInformation(tweet.getInReplyToStatusId(), configuration);
		else
		{
			Tweet highestTweet = new Tweet();
			UserTwitter user = new UserTwitter();
			
			user.setScreenName(tweet.getUser().getScreenName());
			
			highestTweet.setUser(user);
			highestTweet.setTweetId(String.valueOf(tweet.getId()));
			highestTweet.setRetweetCount(tweet.getRetweetCount());
			
			return highestTweet;
		}
	}
	
	public static boolean containsParent (Tweet newParent, List<Tweet> parentsList)
	{
		for (Tweet tweet : parentsList)
			if (tweet.getUser().getScreenName().equals(newParent.getUser().getScreenName()) && tweet.getTweetId().equals(newParent.getTweetId()))
				return true;
		
		return false;
	}
	
	public static List<Tweet> getParents (String dir, String keyword, Configuration configuration) throws JSONException, IOException, NumberFormatException, InterruptedException
	{
		File currentFolder = new File(dir);
		File[] files = currentFolder.listFiles();
		
		List<Tweet> parents = new ArrayList<Tweet>();
		for (File file : files)
		{
			if (file.isFile() && file.getName().equals(keyword + "_raw.json"))
			{
				JSONArray newArray = new JSONArray(ProcessTwitterData.readJsonFile(file.getAbsolutePath()));
				
				for (int i = 0; i < newArray.length(); i++)
				{		
					JSONObject objects = newArray.getJSONObject(i);
					
					if (!objects.has("user")) continue;
					
					Tweet currentTweet = ProcessTwitterData.parseTweet(objects);
					
					if (currentTweet.isReply())
					{
						Tweet parent = buildParentInformation(new Long(currentTweet.getReplied_status_id()), configuration);
						
						if (parent == null)
							parents.add(currentTweet);
						else if (!containsParent(parent, parents))
							parents.add(parent);
					}
					else if (currentTweet.isQuote())
					{
						Tweet parent = buildParentInformation(new Long(currentTweet.getQuoted_tweet_id()), configuration);
						
						if (parent == null)
							parents.add(currentTweet);
						else if (!containsParent(parent, parents))
							parents.add(parent);
					}
					else if (currentTweet.isRetweet())
					{
						Tweet parent = buildParentInformation(new Long(currentTweet.getRetweeted_tweet_id()), configuration);
						
						if (parent == null)
							parents.add(currentTweet);
						else if (!containsParent(parent, parents))
							parents.add(parent);
					}
					else
						parents.add(currentTweet);
				}
			}
		}
		
		return parents;
	}
	
	public static List<Status> bfsTweets(List<Tweet> parents, Configuration configuration) throws InterruptedException
	{
		List<Status> tweets = new ArrayList<Status>();
		Queue<Tweet> queue = new LinkedList<Tweet>();
		
		for (Tweet tweet : parents)
		{
			queue.add(tweet);
			
			Status parentTweet = getTweet(tweet.getTweetId(), configuration);
			if (parentTweet != null)
				tweets.add(parentTweet);
			Thread.sleep(5000);
		}
			
		
		while (queue.size() != 0)
		{
			System.out.println("Current queue size: " + queue.size());
			Tweet currentTweet = queue.poll();
			
			if (currentTweet.getRetweetCount() > 0)
			{
				ResponseList<Status> retweets = getRetweets(new Long(currentTweet.getTweetId()), configuration);
				if (retweets != null)
				{
					tweets.addAll(retweets);
					for (Status status : retweets)
					{
						UserTwitter user = new UserTwitter();
						user.setScreenName(status.getUser().getScreenName());
						
						queue.add(new Tweet(user, String.valueOf(status.getId()), status.getRetweetCount()));
					}
				}
			}
			
			// Searching all replies to the current tweet
			ArrayList<Status> replies = getReplies(currentTweet.getUser().getScreenName(), new Long(currentTweet.getTweetId()), configuration);
			if (replies != null)
			{
				tweets.addAll(replies);
				for (Status status : replies)
				{
					UserTwitter user = new UserTwitter();
					user.setScreenName(status.getUser().getScreenName());
					
					queue.add(new Tweet(user, String.valueOf(status.getId()), status.getRetweetCount()));
				}
			}
		}
		
		return tweets;
	}
	
	public static void enrichTweetsRecursive (String dir, String keyword, String dbName, String collectionName, Configuration configuration) throws JSONException, IOException, NumberFormatException, InterruptedException
	{
		System.out.println("Building parents.");
		List<Tweet> parents = getParents(dir, keyword, configuration);
		
		System.out.println("Parents built. Building comment tree.");
		List<Status> result = bfsTweets(parents, configuration);		
		
		// Establishing connection to the DB
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
	    
		int i = 0;
	    for (Status status : result)
	    {
	    	collection.insertOne(parseStatus(status));
	    	System.out.println("Saving tweet number: " + i);
	    	
	    	i++;
	    }
	    
		mongoClient.close();
	}
	
	public static void main(String[] args) throws JSONException, IOException, NumberFormatException, InterruptedException
	{
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		
	    configurationBuilder.setDebugEnabled(true);
		configurationBuilder.setOAuthConsumerKey("DKAvJEgggEg2j43XprzvNnqzy");
		configurationBuilder.setOAuthConsumerSecret("f9O4jVUUATMryuzNHuunQUrAnrdrSQgVaehv7iJJRXnRA13A1W");
		configurationBuilder.setOAuthAccessToken("61642611-7s1TG2UixsXtSyTOQkl0e4f1bcYpHPFbZGzirTv4H");
		configurationBuilder.setOAuthAccessTokenSecret("t7iqUkg32d7uy6NgnIrhojxnvDCeI3rfOjm8LY2h7nNBR");
		configurationBuilder.setJSONStoreEnabled(true);
		/*
		Runnable r = new SearchTweet("skt", "twitter", "banco para testes", 50, configurationBuilder);
		new Thread(r).start();
	*/
		//GetTweets("923283615659765762");
		//ArrayList<Status> lul = getReplies("TSMDoublelift", Long.parseLong("924836493008314368"), configurationBuilder.build());
		//getRetweets(new Long("925082986730737664"), configurationBuilder);
		//search();
		String inputDir = new StringBuilder().append(PropertyUtils.getCommentPath()).toString();
		enrichTweetsRecursive (inputDir, "sandra bullock hillary clinton", "twitter", "sandra bullock hillary clinton_enriched", configurationBuilder.build());
	}
}
