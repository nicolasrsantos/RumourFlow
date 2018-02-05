package statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.memes.MemesJSON;
import com.github.jreddit.memes.Nodes;
import com.google.gson.Gson;

import Utils.PropertyUtils;
import dataextraction.ProcessTwitterData;
import examples.ExtractSubmissionByKeyWord;
import examples.Tweet;
import examples.UserTwitter;
import sentiment.SentimentString;

public class extractStatistics
{
	private static boolean checkIfExist(List<String> listSubs, String id)
	{
		if (listSubs == null) return false;
		
		for (String str: listSubs)
			if (str.equals(id))
				return true;
		return false;
	}
	
	private static Integer checkIfExistMap(List<MapUsers> map, String name)
	{
		if (map == null) return -1;
		
		for (Integer i = 0; i < map.size(); i++)
			if (map.get(i).getName().equals(name))
				return i;
		return -1;
	}
	
	public static int countWords(String s)
	{
		String trim = s.trim();
		if (trim.isEmpty())
		    return 0;
		return trim.split("\\s+").length;
	}
	
	public static void getUniqueUserSize(List<Tweet> tweets) throws IOException
	{
		Tweet mostRetweetedTweet = null;
		Integer averageSum, mostRetweetedCount, lowestAmmountOfTweets, highestAmmountOfTweets, mostActiveUserCount;
		Integer averageSumWords, lowestAmmountOfWords, highestAmmountOfWords, positive, negative, neutral;
		
		mostRetweetedCount = highestAmmountOfTweets = mostActiveUserCount = highestAmmountOfWords = Integer.MIN_VALUE;
		lowestAmmountOfTweets = lowestAmmountOfWords = Integer.MAX_VALUE;
		
		List<MapUsers> map = new ArrayList<MapUsers>();
		List<String> uniqueUsers = new ArrayList<String>();
		List<String> ids = new ArrayList<String>();
		
		Map<String, Integer> sentimentMap = new HashMap<String, Integer>();
		
		averageSum = averageSumWords = negative = positive = neutral = 0;
		ArrayList medianVec = new ArrayList<Integer>();
		ArrayList medianWordVec = new ArrayList<Integer>();
		Integer i = 0;
		
		for (Tweet tweet : tweets)
		{
			if (!checkIfExist(ids, tweet.getTweetId()))
			{
				ids.add(tweet.getTweetId());
				
				if (!checkIfExist(uniqueUsers, tweet.getUser().getScreenName()) && uniqueUsers != null)
					uniqueUsers.add(tweet.getUser().getScreenName());
				
				if (tweet.getTweet().length() > 140)
				{
					averageSum += 140;
					medianVec.add(140);
				}
				else
				{
					averageSum += tweet.getTweet().length();
					medianVec.add(tweet.getTweet().length());
				}
				
				if (countWords(tweet.getTweet()) == 2)
				{
					averageSumWords += 1;
					medianWordVec.add(1);
				}
				else
				{
					averageSumWords += countWords(tweet.getTweet());
					medianWordVec.add(countWords(tweet.getTweet()));
				}
				
				Integer index = checkIfExistMap(map, tweet.getUser().getScreenName()); 
				if (index != -1)
				{
					String name = map.get(index).getName();
					Integer ammount = map.get(index).getTweetAmmount() + 1;
					List<Tweet> userTweets = map.get(index).getTweets();
					userTweets.add(tweet);
					
					MapUsers newMap = new MapUsers(name, ammount, userTweets);
					map.set(index, newMap);
				}
				else
				{
					List<Tweet> tweetList = new ArrayList<Tweet>();
					tweetList.add(tweet);
					
					MapUsers newMap = new MapUsers(tweet.getUser().getScreenName(), 1, tweetList);
					map.add(newMap);
				}
				
				if (tweet.getFavoriteCount() + tweet.getRetweetCount() > mostRetweetedCount)
				{
					mostRetweetedTweet = tweet;
					mostRetweetedCount = tweet.getFavoriteCount() + tweet.getRetweetCount();
				}
				
				if (tweet.getTweet().length() > highestAmmountOfTweets)
					highestAmmountOfTweets = tweet.getTweet().length();
				
				if (tweet.getTweet().length() < lowestAmmountOfTweets)
					lowestAmmountOfTweets = tweet.getTweet().length();
				
				if (countWords(tweet.getTweet()) > highestAmmountOfWords)
					highestAmmountOfWords = countWords(tweet.getTweet());
				
				if (countWords(tweet.getTweet()) < lowestAmmountOfWords)
					lowestAmmountOfWords = countWords(tweet.getTweet());
			}
			/*String sentiment = SentimentString.getSentimentString(tweet.getTweet());
			if (sentimentMap.containsKey(sentiment))
				sentimentMap.put(sentiment, sentimentMap.get(sentiment) + 1);
			else
				sentimentMap.put(sentiment, 1);*/
			i++;
		}
		
		if (mostRetweetedCount != null)
		{
			/*System.out.println(SentimentString.getSentimentString(mostRetweetedTweet.getTweet()));
			System.out.println(ids.size());
			System.out.println("Unique users: " + uniqueUsers.size());
			System.out.println("Most RT user: " + mostRetweetedTweet.getUser().getScreenName());
			System.out.println("Status ID: " + mostRetweetedTweet.getTweetId());
			System.out.println("Most RT user fav: " + mostRetweetedTweet.getFavoriteCount());
			System.out.println("Most RT user rt: " + mostRetweetedTweet.getRetweetCount());
			System.out.println("Most RT user followers: " + mostRetweetedTweet.getUser().getFollowers());
			System.out.println("Most RT user friends: " + mostRetweetedTweet.getUser().getFriends());
			System.out.println("Most RT user tweets: " + mostRetweetedTweet.getUser().getNumberOfTweets());
			System.out.println("Most RT in words: " + countWords(mostRetweetedTweet.getTweet()));
			System.out.println("Size: " + mostRetweetedTweet.getTweet().length());
			System.out.println("Highest tweet size: " + highestAmmountOfTweets);
			System.out.println("Lowest tweet size: " + lowestAmmountOfTweets);
			System.out.println("Average Tweet size: " + averageSum / ids.size());
			System.out.println("Max Words: " + highestAmmountOfWords);
			System.out.println("Min words: " + lowestAmmountOfWords);
			System.out.println("Average number of words: " + averageSumWords / ids.size());*/
		}
		
		/*System.out.println("Positive: " + positive);
		System.out.println("Negative: " + negative);
		System.out.println("Neutral: " + neutral);*/
		
		/*for (MapUsers user : map)
			if (user.getTweetAmmount() > mostActiveUserCount)
				mostActiveUserCount = user.getTweetAmmount();
		
		System.out.println("Most active user count: " + mostActiveUserCount);
		
		averageSum = averageSumWords = 0;
		Integer sum = 0;
		for (MapUsers user : map)
		{
			if (user.getTweetAmmount() == mostActiveUserCount)
			{
				System.out.println("Most active user: " + user.getName());
				System.out.println("Most active user tweets: " + user.getTweetAmmount());
				
				for (Tweet tweet : user.getTweets())
				{
					if(tweet.getTweet().length() > 140)
						averageSum += 140;
					else
						averageSum += tweet.getTweet().length();
					
					if (countWords(tweet.getTweet()) == 2)
						averageSumWords += 1;
					else
						averageSumWords += countWords(tweet.getTweet());
						
					String sentiment = SentimentString.getSentimentString(tweet.getTweet());
					System.out.println(sentiment);
					System.out.println(tweet.getTweet().length());
					System.out.println(countWords(tweet.getTweet()));
					
					sum += 1;
				}
			}
		}
		System.out.println(averageSum / sum);
		System.out.println(averageSumWords / sum);*/
		/*for (String key : sentimentMap.keySet())
		{
			System.out.println(key + " " + sentimentMap.get(key));
		}*/
		
		Collections.sort(medianVec, new Comparator<Integer>() {
	        @Override
	        public int compare(Integer a, Integer b)
	        {

	            return  a.compareTo(b);
	        }
	    });
		
		Collections.sort(medianWordVec, new Comparator<Integer>() {
	        @Override
	        public int compare(Integer a, Integer b)
	        {

	            return  a.compareTo(b);
	        }
	    });
		
		
		if (medianVec.size() % 2 == 0)
		{
			System.out.println("Median char: " + medianVec.get(medianVec.size() / 2) + " " + medianVec.get(medianVec.size() / 2 - 1));
			System.out.println("Median word: " + medianWordVec.get(medianWordVec.size() / 2) + " " + medianWordVec.get(medianWordVec.size() / 2 - 1));
		}
		else
		{
			System.out.println("Median char: " + medianVec.get(medianVec.size() / 2));
			System.out.println("Median word: " + medianWordVec.get(medianWordVec.size() / 2));
		}
	}

	public static void extract(String dir, String keyword) throws IOException, JSONException
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
		
		System.out.println(keyword);
		System.out.println("Tweets: " + tweets.size());
		getUniqueUserSize(tweets);
	}
	
	public static void twitter () throws IOException, JSONException
	{
		//String keyword = "papers global warming myth";
		//String keyword = "trump biggest tax cut";
		//String keyword = "sandra bullock hillary clinton";
		String keyword = "Cholera Puerto Rico";
		String inputDir = new StringBuilder().append(PropertyUtils.getCommentPath()).toString();
		extract(inputDir, keyword);
	}
	
	public static void reddit () throws IOException
	{
		Gson gson = new Gson();
		String json;
		String currentRumor = "cholera_puerto_rico\\";
		
		File folder = new File(PropertyUtils.getCommentPath() + "\\reddit\\" + currentRumor);
		String[] files  = folder.list();
		
		Integer i, numberOfPosts;
		i = numberOfPosts = 0;
		
		ArrayList<Integer> medianChar, medianWord;
		medianChar = new ArrayList<Integer>();
		medianWord = new ArrayList<Integer>();
		
		for (String file : files)
		{
			if (file.endsWith(".json") && file.startsWith("t3"))
			{
				json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + "\\reddit\\" + currentRumor + file));
				MemesJSON memes = gson.fromJson(json, MemesJSON.class);
				
				for (Nodes node : memes.getNodes())
				{
					if (i == 0)
					{
						medianChar.add(node.getTitle().length());
						medianWord.add(countWords(node.getTitle()));
						i++;
					}
					else
					{
						medianChar.add(node.getLabel().length());
						medianWord.add(countWords(node.getLabel()));
					}
				}
				
				numberOfPosts += memes.getNodes().size();
			}
		}
		
		System.out.println("Posts: " + numberOfPosts);
		
		Collections.sort(medianChar, new Comparator<Integer>() {
	        @Override
	        public int compare(Integer a, Integer b)
	        {

	            return  a.compareTo(b);
	        }
	    });
		
		Collections.sort(medianWord, new Comparator<Integer>() {
	        @Override
	        public int compare(Integer a, Integer b)
	        {

	            return  a.compareTo(b);
	        }
	    });
		
		if (medianChar.size() % 2 == 0)
		{
			System.out.println("Median char: " + medianChar.get(medianChar.size() / 2) + " " + medianChar.get(medianChar.size() / 2 - 1));
			System.out.println("Median word: " + medianWord.get(medianWord.size() / 2) + " " + medianWord.get(medianWord.size() / 2 - 1));
		}
		else
		{
			System.out.println("Median char: " + medianChar.get(medianChar.size() / 2));
			System.out.println("Median word: " + medianWord.get(medianWord.size() / 2));
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		//twitter();
		reddit();
	}
}
