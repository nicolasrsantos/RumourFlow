package com.github.jreddit.memes;

import java.util.ArrayList;
import java.util.List;

public class Nodes { 
	private Integer index = 0;
	List<Integer> links = new ArrayList<Integer>();
	private Double score = 0.0;
	private Integer level = 0;
	private String title = "";
	private String label = "";
	private String duration = "";
	private Integer id = 0;
	private String color = "";
	private String redditID;
	private String isTrueCluster;
	private Nodes parentID;
	private Double timestamp;
	private Double simScore;
	private String sentiment;
	private String tweetType;
	
	
	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	public Double getSimScore() {
		return simScore;
	}

	public void setSimScore(Double simScore) {
		this.simScore = simScore;
	}

	public Nodes getParentID() {
		return parentID;
	}

	public void setParentID(Nodes parentID) {
		this.parentID = parentID;
	}

	public String getIsTrueCluster() {
		return isTrueCluster;
	}

	public void setIsTrueCluster(String isTrueCluster) {
		this.isTrueCluster = isTrueCluster;
	}

	public String getRedditID() {
		return redditID;
	}

	public void setRedditID(String redditID) {
		this.redditID = redditID;
	}
	
	public Double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Double timestamp) {
		this.timestamp = timestamp;
	}
	
	public Nodes(Integer index, List<Integer> links, Double score,
			Integer level, String title, String label,
			Integer id, String color, String redditID,
			String isTrueCluster, Nodes aParentID, Double aTime, Double simScore, String sen) {
		super();
		this.index = index;
		this.links = links;
		this.score = score;
		this.level = level;
		this.title = title;
		this.label = label;
		this.id = id;
		this.color = color;
		this.redditID = redditID;
		this.isTrueCluster = isTrueCluster;
		this.parentID = aParentID;
		this.timestamp = aTime;
		this.simScore = simScore;
		this.sentiment = sen;
	}
	
	public Nodes(Integer index, List<Integer> links, Double score,
			Integer level, String title, String label,
			Integer id, String color, String redditID,
			String isTrueCluster, Nodes aParentID, Double aTime, Double simScore, String sen, String tweetType) {
		super();
		this.index = index;
		this.links = links;
		this.score = score;
		this.level = level;
		this.title = title;
		this.label = label;
		this.id = id;
		this.color = color;
		this.redditID = redditID;
		this.isTrueCluster = isTrueCluster;
		this.parentID = aParentID;
		this.timestamp = aTime;
		this.simScore = simScore;
		this.sentiment = sen;
		this.tweetType = tweetType;
	}

	public List<Integer> getLinks() {
		return links;
	}

	public void setLinks(List<Integer> links) {
		this.links = links;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Nodes(){
		
	}

	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getTweetType() {
		return tweetType;
	}

	public void setTweetType(String tweetType) {
		this.tweetType = tweetType;
	}
	
}
