package statistics;

import java.util.List;

import examples.Tweet;

public class MapUsers
{
	private String name;
	private Integer tweetAmmount;
	private List<Tweet> tweets;
	
	MapUsers(String name, Integer ammount, List<Tweet> tweets)
	{
		setName(name);
		setTweetAmmount(ammount);
		setTweets(tweets);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getTweetAmmount() {
		return tweetAmmount;
	}
	public void setTweetAmmount(Integer tweetAmmount) {
		this.tweetAmmount = tweetAmmount;
	}
	public List<Tweet> getTweets() {
		return tweets;
	}
	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
}
