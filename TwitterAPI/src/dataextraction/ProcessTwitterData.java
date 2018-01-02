package dataextraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.jreddit.memes.MemesJSON;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import Utils.PropertyUtils;
import examples.CommentTwitter;
import examples.ExtractSubmissionByKeyWord;
import examples.Tweet;
import examples.Twitter;
import examples.UserTwitter;
import sentiment.WikipediaTopicExtraction;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ProcessTwitterData
{
	public static String readJsonFile(String filename) throws IOException
	{
	    try (InputStream is = new FileInputStream(filename))
	    {
	        return IOUtils.toString(is, StandardCharsets.UTF_8);
	    }    
	}
	
	// Returns the user field from Twitter's JSON file (passed as JSONObject)
	public static UserTwitter getUser(JSONObject object) throws JSONException
	{
		UserTwitter newUser = new UserTwitter();
		
		if (object.has("id"))
			newUser.setUserId(object.get("id").toString());
		
		if (object.has("screenName"))
			newUser.setScreenName(object.getString("screenName"));
		
		if (object.has("friendsCount"))
			newUser.setFriends(object.getInt("friendsCount"));
		
		if (object.has("followersCount"))
			newUser.setFollowers(object.getInt("followersCount"));
		
		if (object.has("statusesCount"))
			newUser.setNumberOfTweets(object.getInt("statusesCount"));
		
		return newUser;
	}
	
	// Returns the text field from Twitter's JSON file (passed as JSONObject)
	public static String getText(JSONObject object) throws JSONException
	{
		if (object.has("text")) return object.getString("text");
		return "";
	}
	
	// Returns the full text field from Twitter's JSON file (passed as JSONObject)
	// full text fields exists when the tweet is too long for the normal text field
	public static String getFullText(JSONObject object) throws JSONException
	{
		if (object.has("full_text")) return object.getString("full_text");
		return "";
	}
	
	// Returns the users that were mentioned in a tweet
	public static List<String>getMentionedUsers(JSONObject object) throws JSONException
	{
		List<String> mentionedUsers = new ArrayList<String>();
		JSONArray array = new JSONArray();
		
		array = object.getJSONArray("user_mentions");
		
		for (int i = 0; i < array.length(); i++)
			mentionedUsers.add(array.getJSONObject(i).getString("screen_name"));
		
		return mentionedUsers;
	}
	
	public static List<String> getHashtags(JSONObject object) throws JSONException
	{
		List<String> hashtags = new ArrayList<String>();
		JSONArray array = new JSONArray();
		
		array = object.getJSONArray("hashtags");
		
		for (int i = 0; i < array.length(); i++)
			hashtags.add(array.getJSONObject(i).getString("text"));
		
		return hashtags;
	}
	
	public static Tweet parseTweet(JSONObject objects) throws JSONException
	{
		Tweet newTweet = new Tweet();
		
		// Adds the user that created the tweet
		JSONObject user = objects.getJSONObject("user");
		newTweet.setUser(getUser(user));
		
		// Full text is used when the Text field isn't big enough to store the tweet
		if (objects.has("extended_tweet"))
		{
			//newTweet.setTopics(WikipediaTopicExtraction.getWikis(getFullText(objects.getJSONObject("extended_tweet"))));
			newTweet.setTweet(getFullText(objects.getJSONObject("extended_tweet")));
		}
		else
		{
			//newTweet.setTopics(WikipediaTopicExtraction.getWikis(getText(objects)));
			newTweet.setTweet(getText(objects));
		}
		
		// Adds users that were mentioned
		/*JSONObject entities = new JSONObject();
		entities = objects.getJSONObject("entities");
		
		newTweet.setMentioned_users(getMentionedUsers(entities));*/
		newTweet.setTimestamp(Double.parseDouble(objects.getString("timestamp_ms")));
		
		if (!objects.get("inReplyToStatusId").equals(-1))
		{
			newTweet.setReply(true);
			newTweet.setReplied_user(objects.get("inReplyToScreeName").toString());
			newTweet.setReplied_status_id(objects.get("inReplyToStatusId").toString());
			newTweet.setReplyToUserId(objects.get("inReplyToUserId").toString());
		}
		else if (objects.has("retweetedStatus"))
		{
			JSONObject retweet = objects.getJSONObject("retweetedStatus");
			
			newTweet.setRetweet(true);
			newTweet.setRetweeted_user(retweet.getJSONObject("user").getString("screenName"));
			newTweet.setRetweeted_tweet_id(retweet.get("id").toString());
			
			if (retweet.has("extended_tweet"))
				newTweet.setRetweeted_tweet((getFullText(retweet.getJSONObject("extended_tweet"))));
			else
				newTweet.setRetweeted_tweet(getText(retweet));
		}
		else if (objects.has("quotedStatus"))
		{
			JSONObject quote = objects.getJSONObject("quotedStatus");
			
			newTweet.setQuote(true);
			newTweet.setQuoted_user(quote.getJSONObject("user").getString("screenName"));
			newTweet.setQuoted_tweet_id(quote.get("id").toString());
			
			if (quote.has("extended_tweet"))
				newTweet.setQuoted_tweet((getFullText(quote.getJSONObject("extended_tweet"))));
			else
				newTweet.setQuoted_tweet(getText(quote));
		}
		else
		{
			newTweet.setNormalTweet(true);
		}
		
		newTweet.setCreatedAt(objects.getString("createdAt"));
		newTweet.setTweetId(objects.getString("id"));
		newTweet.setRetweetCount(objects.getInt("retweetCount"));
		newTweet.setFavoriteCount(objects.getInt("favoriteCount"));
		
		//newTweet.setHashtags(getHashtags(entities));
		
		return newTweet;
	}
	
	public static int checkIfUserExists (List<Tweet> tweets, String user)
	{
		for (int i = 0; i < tweets.size(); i++)
		{
			if (tweets.get(i).getUser().getScreenName().equals(user))
				return i;
		}
		
		return -1;
	}
	
	public static CommentTwitter linkComment (String parentUser, String originalTweet, Double targetTimestamp, String user, String tweet, Double sourceTimestamp, String type)
	{
		CommentTwitter comment = new CommentTwitter();
		
		comment.setParentUser(parentUser);
		comment.setOriginalTweet(originalTweet);
		comment.setTargetTimestamp(targetTimestamp);
		
		comment.setUser(user);
		comment.setTweet(tweet);
		comment.setSourceTimestamp(sourceTimestamp);
		
		comment.setType(type);
		
		return comment;
	}
	
	public static void parseTweets(String dir, String keyword) throws JSONException, IOException
	{
		File currentFolder = new File(dir);
		File[] files = currentFolder.listFiles();
		List<Tweet> tweets = new ArrayList<Tweet>();
		
		for (File file : files)
		{
			if (file.isFile() && file.getName().equals(keyword + "_raw.json"))
			{
				JSONArray newArray = new JSONArray(readJsonFile(file.getAbsolutePath()));
				
				for (int i = 0; i < newArray.length(); i++)
				{		
					JSONObject objects = newArray.getJSONObject(i);
					
					if (!objects.has("user")) continue;
					
					Tweet newTweet = parseTweet(objects);
					
					tweets.add(newTweet);
				}
			}
		}
		
		if (!tweets.isEmpty())
		{
			for (int i = 0; i < tweets.size(); i++)
			{
				List<CommentTwitter> comments = new ArrayList<CommentTwitter>();
				Tweet tweet = tweets.get(i);
				
				for (int j = 0; j < tweets.size(); j++)
				{
					Tweet nextTweet = tweets.get(j);
					
					if (nextTweet.isReply() && tweet.getUser().getScreenName().equals(nextTweet.getReplied_user()))
					{
						CommentTwitter comment = new CommentTwitter();
						
						comment.setParentUser(tweet.getUser().getScreenName());
						comment.setUser(nextTweet.getUser().getScreenName());
						comment.setTweet(nextTweet.getTweet());
						comment.setOriginalTweet(tweet.getTweet());
						comment.setType("reply");
						comment.setSourceTimestamp(nextTweet.getTimestamp());
						comment.setTargetTimestamp(tweet.getTimestamp());
						
						comments.add(comment);
					}
					else if (nextTweet.isRetweet() && tweet.getUser().getScreenName().equals(nextTweet.getRetweeted_user()))
					{
						CommentTwitter comment = new CommentTwitter();
						
						comment.setParentUser(tweet.getUser().getScreenName());
						comment.setUser(nextTweet.getUser().getScreenName());
						comment.setTweet(nextTweet.getTweet());
						comment.setOriginalTweet(tweet.getTweet());
						comment.setType("retweet");
						comment.setSourceTimestamp(nextTweet.getTimestamp());
						comment.setTargetTimestamp(tweet.getTimestamp());
						
						comments.add(comment);
					}
					else if (nextTweet.isQuote() && tweet.getUser().getScreenName().equals(nextTweet.getQuoted_user()))
					{
						CommentTwitter comment = new CommentTwitter();
						
						comment.setParentUser(tweet.getUser().getScreenName());
						comment.setUser(nextTweet.getUser().getScreenName());
						comment.setTweet(nextTweet.getTweet());
						comment.setOriginalTweet(tweet.getTweet());
						comment.setType("quote");
						comment.setSourceTimestamp(nextTweet.getTimestamp());
						comment.setTargetTimestamp(tweet.getTimestamp());
						
						comments.add(comment);
					}
				}
				
				if (!comments.isEmpty())
				{
					tweet.setHasComments(true);
					tweet.setComments(comments);
					tweets.set(i, tweet);
				}
			}
			
			
		}
		
		ExtractSubmissionByKeyWord.processTwitterSubmissions(tweets, keyword);
	}
	
	public static List<Status> getRelatedTweets(Configuration configuration, JSONObject tweetObject) throws JSONException, InterruptedException
	{
		Tweet tweet = parseTweet(tweetObject);
		List<Status> relatedTweets = new ArrayList<Status>();

		// to the tweet collection
		if (tweet.isReply())
		{
			Status searchedTweet = SearchTweet.getTweet(tweet.getReplied_status_id(), configuration);
			
			if (searchedTweet != null)
			{
				relatedTweets.add(searchedTweet);
			
				ResponseList<Status> retweets = SearchTweet.getRetweets(searchedTweet.getId(), configuration);
				if (retweets != null)
					relatedTweets.addAll(retweets);
				
				// Searching all replies to the current tweet
				ArrayList<Status> replies = SearchTweet.getReplies(searchedTweet.getUser().getScreenName(), searchedTweet.getId(), configuration);
				if (replies != null)
					relatedTweets.addAll(replies);
			}
		}
		else if (tweet.isRetweet())
		{
			Status searchedTweet = SearchTweet.getTweet(tweet.getRetweeted_tweet_id(), configuration);
			
			if (searchedTweet != null)
			{
				relatedTweets.add(searchedTweet);
			
				ResponseList<Status> retweets = SearchTweet.getRetweets(searchedTweet.getId(), configuration);
				if (retweets != null)
					relatedTweets.addAll(retweets);
				
				// Searching all replies to the current tweet
				ArrayList<Status> replies = SearchTweet.getReplies(searchedTweet.getUser().getScreenName(), searchedTweet.getId(), configuration);
				if (replies != null)
					relatedTweets.addAll(replies);
			}
		}
		else
		{
			Status searchedTweet = SearchTweet.getTweet(tweet.getQuoted_tweet_id(), configuration);
			
			if (searchedTweet != null)
			{
				relatedTweets.add(searchedTweet);
			
				ResponseList<Status> retweets = SearchTweet.getRetweets(searchedTweet.getId(), configuration);
				if (retweets != null)
					relatedTweets.addAll(retweets);
				
				// Searching all replies to the current tweet
				ArrayList<Status> replies = SearchTweet.getReplies(searchedTweet.getUser().getScreenName(), searchedTweet.getId(), configuration);
				if (replies != null)
					relatedTweets.addAll(replies);
			}
		}
		
		// Searching all retweets related to the current tweet
		ResponseList<Status> retweets = SearchTweet.getRetweets(new Long(tweet.getTweetId()), configuration);
		if (retweets != null)
			relatedTweets.addAll(retweets);
		
		// Searching all replies to the current tweet
		ArrayList<Status> replies = SearchTweet.getReplies(tweet.getUser().getScreenName(), new Long(tweet.getTweetId()), configuration);
		if (replies != null)
			relatedTweets.addAll(replies);

		return relatedTweets;
	}
	
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
	
	public static void main(String[] args) throws Exception
	{
		String keyword = "cholera puerto rico";
		String inputDir = new StringBuilder().append(PropertyUtils.getCommentPath()).toString();
		
		parseTweets(inputDir, keyword);
		
		System.out.println(keyword + " file saved.");
	}
}
