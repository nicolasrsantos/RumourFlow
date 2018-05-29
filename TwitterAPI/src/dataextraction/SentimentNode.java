package dataextraction;

import java.util.List;

public class SentimentNode {
	private Double timestamp;
	private String nickname;
	private String sentiment;
	private String tweet;
	private String type;
	private String parentId;
	private Integer id;
	private List<SentimentNode> children;
	
	public Double getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Double timestamp) {
		this.timestamp = timestamp;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	public String getTweet() {
		return tweet;
	}
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
	public List<SentimentNode> getChildren() {
		return children;
	}
	public void setChildren(List<SentimentNode> children) {
		this.children = children;
	}
	
	public SentimentNode(Double timestamp, String nickname, String sentiment, String tweet,
						 List<SentimentNode> children, String type, String parentId) {
		super();
		this.timestamp = timestamp;
		this.nickname = nickname;
		this.sentiment = sentiment;
		this.tweet = tweet;
		this.children = children;
		this.type = type;
		this.parentId = parentId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public SentimentNode(Double timestamp, String nickname, String sentiment, String tweet,
			 List<SentimentNode> children, String type, String parentId, Integer id) {
		super();
		this.timestamp = timestamp;
		this.nickname = nickname;
		this.sentiment = sentiment;
		this.tweet = tweet;
		this.type = type;
		this.parentId = parentId;
		this.id = id;
		this.children = children;
	}
}
