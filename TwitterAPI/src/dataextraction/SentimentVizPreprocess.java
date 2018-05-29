package dataextraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.memes.Link;
import com.github.jreddit.memes.MemesJSON;
import com.github.jreddit.memes.Nodes;
import com.github.jreddit.memes.UserLink;
import com.google.gson.Gson;

import Utils.PropertyUtils;
import examples.CommentTwitter;
import examples.ExtractSubmissionByKeyWord;
import examples.Tweet;
import examples.Twitter;
import examples.UserTwitter;
import sentiment.SentimentString;
import sentiment.WikipediaTopicExtraction;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.conf.Configuration;

public class SentimentVizPreprocess {
	public static List<Tweet> readTweetFile(String keyword, String dir) throws JSONException, IOException
	{
		File currentFolder = new File(dir);
		File[] files = currentFolder.listFiles();
		List<Tweet> tweets = new ArrayList<Tweet>();
		
		for (File file : files)
		{
			if (file.isFile() && file.getName().equals(keyword + "_raw.json"))
			{
				JSONArray newArray = new JSONArray(ProcessTwitterData.readJsonFile(file.getAbsolutePath()));
				
				for (int i = 0; i < newArray.length(); i++)
				{		
					JSONObject objects = newArray.getJSONObject(i);
					
					if (!objects.has("user")) continue;
					
					Tweet newTweet = ProcessTwitterData.parseTweet(objects);
					tweets.add(newTweet);
				}
			}
		}
		
		return tweets;
	}
	
	public static List<Tweet> getTweets(String keyword, String dir) throws JSONException, IOException
	{
		List<Tweet> tweets = readTweetFile(keyword, dir);
		
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
		
		return tweets;
	}
	
	public static List<Tweet> getBranches(List<Tweet> tweets)
	{
		List<Tweet> branches = new ArrayList<Tweet>();
		for (Tweet tweet : tweets)
			if (tweet.isReply() || tweet.isRetweet() || tweet.isQuote() || tweet.hasComments())
				branches.add(tweet);
		
		return branches;
	}
	
	public static Tweet getTweetById(String id, List<Tweet> branches)
	{
		for (Tweet tweet : branches)
			if (tweet.getTweetId().equals(String.valueOf(id)))
				return tweet;
		
		return null;
	}
	
	public static Tweet getParent(String id, List<Tweet> tweets)
	{
		for (Tweet t : tweets)
			if (t.getTweetId().equals(id))
				return t;
		
		return null;
	}
	
	public static Boolean parentExists(String nickname, Double timestamp, List<SentimentNode> nodes)
	{
		for (SentimentNode node : nodes)
			if (node.getNickname().equals(nickname) && node.getTimestamp().equals(timestamp))
				return true;
		
		return false;
	}
	
	public static Boolean checkIfCommentNodeExists(List<SentimentNode> nodes)
	{
		for (SentimentNode node : nodes)
			if (!node.getType().equals("normal"))
				return true;
		
		return false;
	}
	
	public static List<SentimentNode> connectNodeToParent(List<SentimentNode> nodes, List<Tweet> branches) throws IOException
	{
		List<SentimentNode> retNodes = new ArrayList<SentimentNode>();
		
		for (SentimentNode node : nodes)
		{
			if (node.getType().equals("reply"))
			{
				Tweet parent = getParent(node.getParentId(), branches);
				
				if (!parentExists(parent.getUser().getScreenName(), parent.getTimestamp(), retNodes))
				{
					List<SentimentNode> children = new ArrayList<SentimentNode>();
					children.add(node);
					
					if (parent.isReply())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "reply", parent.getReplied_status_id()));
					else if (parent.isRetweet())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "retweet", parent.getRetweeted_tweet_id()));
					else if (parent.isQuote())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "quote", parent.getQuoted_tweet_id()));
					else
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "normal", null));
				}
				else
				{
					for (SentimentNode curnode : retNodes)
						if (curnode.getNickname().equals(parent.getUser().getScreenName()) && curnode.getTimestamp().equals(parent.getTimestamp()))
							curnode.getChildren().add(node);
				}
			}
			else if (node.getType().equals("quote"))
			{
				Tweet parent = getParent(node.getParentId(), branches);
				
				if (!parentExists(parent.getUser().getScreenName(), parent.getTimestamp(), retNodes))
				{
					List<SentimentNode> children = new ArrayList<SentimentNode>();
					children.add(node);
					
					if (parent.isReply())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "reply", parent.getReplied_status_id()));
					else if (parent.isRetweet())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "retweet", parent.getRetweeted_tweet_id()));
					else if (parent.isQuote())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "quote", parent.getQuoted_tweet_id()));
					else
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "normal", null));
				}
				else
				{
					for (SentimentNode curnode : retNodes)
						if (curnode.getNickname().equals(parent.getUser().getScreenName()) && curnode.getTimestamp().equals(parent.getTimestamp()))
							curnode.getChildren().add(node);
				}
			}
			else if (node.getType().equals("retweet"))
			{
				Tweet parent = getParent(node.getParentId(), branches);
				
				if (!parentExists(parent.getUser().getScreenName(), parent.getTimestamp(), retNodes))
				{
					List<SentimentNode> children = new ArrayList<SentimentNode>();
					children.add(node);
					
					if (parent.isReply())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "reply", parent.getReplied_status_id()));
					else if (parent.isRetweet())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "retweet", parent.getRetweeted_tweet_id()));
					else if (parent.isQuote())
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "quote", parent.getQuoted_tweet_id()));
					else
						retNodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "normal", null));
				}
				else
				{
					for (SentimentNode curnode : retNodes)
						if (curnode.getNickname().equals(parent.getUser().getScreenName()) && curnode.getTimestamp().equals(parent.getTimestamp()))
							curnode.getChildren().add(node);
				}
			}
			else
				retNodes.add(node);
		}
		
		return retNodes;
	}
	
	public static void preprocess(String inputDir, String keyword) throws JSONException, IOException, InterruptedException
	{
		List<Tweet> tweets = getTweets(keyword, inputDir);
		List<Tweet> branches = getBranches(tweets);
		
		List<SentimentNode> nodes = new ArrayList<SentimentNode>();
		List<Tweet> tweetsToRemove = new ArrayList<Tweet>();
		for (Tweet tweet : branches)
		{
			if (tweet.isReply() && !tweet.hasComments())
			{
				Tweet parent = getParent(tweet.getReplied_status_id(), branches);
				if (parent == null)
				{
					tweetsToRemove.add(tweet);
					continue;
				}

				SentimentNode son = new SentimentNode(tweet.getTimestamp(), tweet.getUser().getScreenName(), SentimentString.getSentimentString(tweet.getTweet()), tweet.getTweet(), new ArrayList<SentimentNode>(), "reply", tweet.getReplied_status_id());
				
				if (!parentExists(parent.getUser().getScreenName(), parent.getTimestamp(), nodes))
				{
					List<SentimentNode> children = new ArrayList<SentimentNode>();
					children.add(son);
					
					if (parent.isReply())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "reply", parent.getReplied_status_id()));
					else if (parent.isRetweet())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "retweet", parent.getRetweeted_tweet_id()));
					else if (parent.isQuote())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "quote", parent.getQuoted_tweet_id()));
					else
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "normal", null));
				}
				else
				{
					for (SentimentNode node : nodes)
						if (node.getNickname().equals(parent.getUser().getScreenName()) && node.getTimestamp().equals(parent.getTimestamp()))
							node.getChildren().add(son);
				}
			}
			else if (tweet.isRetweet() && !tweet.hasComments())
			{
				Tweet parent = getParent(tweet.getRetweeted_tweet_id(), branches);
				if (parent == null)
				{
					tweetsToRemove.add(tweet);
					continue;
				}
				
				SentimentNode son = new SentimentNode(tweet.getTimestamp(), tweet.getUser().getScreenName(), SentimentString.getSentimentString(tweet.getTweet()), tweet.getTweet(), new ArrayList<SentimentNode>(), "retweet", tweet.getRetweeted_tweet_id());
				
				if (!parentExists(parent.getUser().getScreenName(), parent.getTimestamp(), nodes))
				{
					List<SentimentNode> children = new ArrayList<SentimentNode>();
					children.add(son);
					
					if (parent.isReply())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "reply", parent.getReplied_status_id()));
					else if (parent.isRetweet())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "retweet", parent.getRetweeted_tweet_id()));
					else if (parent.isQuote())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "quote", parent.getQuoted_tweet_id()));
					else
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "normal", null));
				}
				else
				{
					for (SentimentNode node : nodes)
						if (node.getNickname().equals(parent.getUser().getScreenName()) && node.getTimestamp().equals(parent.getTimestamp()))
							node.getChildren().add(son);
				}
			}
			else if (tweet.isQuote() && !tweet.hasComments())
			{
				Tweet parent = getParent(tweet.getQuoted_tweet_id(), branches);
				if (parent == null)
				{
					tweetsToRemove.add(tweet);
					continue;
				}
				
				SentimentNode son = new SentimentNode(tweet.getTimestamp(), tweet.getUser().getScreenName(), SentimentString.getSentimentString(tweet.getTweet()), tweet.getTweet(), new ArrayList<SentimentNode>(), "quote", tweet.getQuoted_tweet_id());
				
				if (!parentExists(parent.getUser().getScreenName(), parent.getTimestamp(), nodes))
				{
					List<SentimentNode> children = new ArrayList<SentimentNode>();
					children.add(son);
					
					if (parent.isReply())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "reply", parent.getReplied_status_id()));
					else if (parent.isRetweet())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "retweet", parent.getRetweeted_tweet_id()));
					else if (parent.isQuote())
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "quote", parent.getQuoted_tweet_id()));
					else
						nodes.add(new SentimentNode(parent.getTimestamp(), parent.getUser().getScreenName(), SentimentString.getSentimentString(parent.getTweet()), parent.getTweet(), children, "normal", null));
				}
				else
				{
					for (SentimentNode node : nodes)
						if (node.getNickname().equals(parent.getUser().getScreenName()) && node.getTimestamp().equals(parent.getTimestamp()))
							node.getChildren().add(son);
				}
			}
		}
		
		branches.removeAll(tweetsToRemove);
		
		while(checkIfCommentNodeExists(nodes))
		{
			nodes = connectNodeToParent(nodes, branches);
		}
		
		SentimentNode rootNode = new SentimentNode(0.0, null, null, null, nodes, "hidden", null);
		
		Gson gson = new Gson();
		String json = gson.toJson(rootNode);
		File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sentimentVis.json");
		FileUtils.writeStringToFile(file3, json, false);
	}
	
	public static void main(String[] args) throws JSONException, IOException, InterruptedException
	{
		String keyword = "trump biggest tax cut";
		String inputDir = new StringBuilder().append(PropertyUtils.getCommentPath()).toString();
		
		preprocess(inputDir, keyword);
		
		System.out.println(keyword + " file saved.");
	}
}
