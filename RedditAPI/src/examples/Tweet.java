package examples;

import java.sql.Date;
import java.util.List;

import com.github.jreddit.memes.Topic;

public class Tweet
{
	String createdAt;
	String tweetId;
	String replyToUserId;
	String tweet;
	String replied_user;
	String replied_status_id;
	String quoted_user;
	String quoted_tweet;
	String quoted_tweet_id;
	String retweeted_user;
	String retweeted_tweet;
	String retweeted_tweet_id;
	
	Double timestamp;
	
	Integer quoteCount;
	Integer replyCount;
	Integer favoriteCount;
	Integer retweetCount;
	
	UserTwitter user;
	
	List<String> topics;
	List<String> mentioned_users;
	List<String> hashtags;
	List<CommentTwitter> comments;
	
	boolean isReply;
	boolean isRetweet;
	boolean isQuote;
	boolean isNormalTweet;
	boolean hasComments;
	
	public Tweet()
	{
		
	}
	
	public Tweet(UserTwitter user, String tweetId, Integer retweetCount)
	{
		this.user = user;
		this.tweetId = tweetId;
		this.retweetCount = retweetCount;
	}
	
	public List<String> getTopics() {
		return topics;
	}
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	public List<String> getMentioned_users() {
		return mentioned_users;
	}
	public void setMentioned_users(List<String> mentioned_users) {
		this.mentioned_users = mentioned_users;
	}
	public boolean isReply() {
		return isReply;
	}
	public void setReply(boolean isReply) {
		this.isReply = isReply;
	}
	public String getReplied_user() {
		return replied_user;
	}
	public void setReplied_user(String replied_user) {
		this.replied_user = replied_user;
	}
	public String getReplied_status_id() {
		return replied_status_id;
	}
	public void setReplied_status_id(String replied_status_id) {
		this.replied_status_id = replied_status_id;
	}
	public String getTweet() {
		return tweet;
	}
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
	public String getQuoted_user() {
		return quoted_user;
	}
	public void setQuoted_user(String quoted_user) {
		this.quoted_user = quoted_user;
	}
	public String getQuoted_tweet() {
		return quoted_tweet;
	}
	public void setQuoted_tweet(String quoted_tweet) {
		this.quoted_tweet = quoted_tweet;
	}
	public String getRetweeted_user() {
		return retweeted_user;
	}
	public void setRetweeted_user(String retweeted_user) {
		this.retweeted_user = retweeted_user;
	}
	public String getRetweeted_tweet() {
		return retweeted_tweet;
	}
	public void setRetweeted_tweet(String retweeted_tweet) {
		this.retweeted_tweet = retweeted_tweet;
	}
	public boolean isRetweet() {
		return isRetweet;
	}
	public void setRetweet(boolean isRetweet) {
		this.isRetweet = isRetweet;
	}
	public boolean isQuote() {
		return isQuote;
	}
	public void setQuote(boolean isQuote) {
		this.isQuote = isQuote;
	}
	public List<CommentTwitter> getComments() {
		return comments;
	}
	public void setComments(List<CommentTwitter> comments) {
		this.comments = comments;
	}
	public boolean hasComments() {
		return hasComments;
	}
	public void setHasComments(boolean hasComments) {
		this.hasComments = hasComments;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getTweetId() {
		return tweetId;
	}
	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}
	public String getReplyToUserId() {
		return replyToUserId;
	}
	public void setReplyToUserId(String replyToUserId) {
		this.replyToUserId = replyToUserId;
	}
	public Integer getQuoteCount() {
		return quoteCount;
	}
	public void setQuoteCount(Integer quoteCount) {
		this.quoteCount = quoteCount;
	}
	public Integer getRetweetCount() {
		return retweetCount;
	}
	public void setRetweetCount(Integer retweetCount) {
		this.retweetCount = retweetCount;
	}
	public Integer getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(Integer favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public Integer getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}
	public Double getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Double timestamp) {
		this.timestamp = timestamp;
	}
	public List<String> getHashtags() {
		return hashtags;
	}
	public void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}
	public boolean isHasComments() {
		return hasComments;
	}
	public void setUser(UserTwitter user) {
		this.user = user;
	}
	public UserTwitter getUser() {
		return user;
	}
	public boolean isNormalTweet() {
		return isNormalTweet;
	}
	public void setNormalTweet(boolean isNormalTweet) {
		this.isNormalTweet = isNormalTweet;
	}
	public String getQuoted_tweet_id() {
		return quoted_tweet_id;
	}
	public void setQuoted_tweet_id(String quoted_tweet_id) {
		this.quoted_tweet_id = quoted_tweet_id;
	}
	public String getRetweeted_tweet_id() {
		return retweeted_tweet_id;
	}
	public void setRetweeted_tweet_id(String retweeted_tweet_id) {
		this.retweeted_tweet_id = retweeted_tweet_id;
	}
	
}
