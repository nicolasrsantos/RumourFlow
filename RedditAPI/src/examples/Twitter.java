package examples;

import java.util.ArrayList;
import java.util.List;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.memes.Link;
import com.github.jreddit.memes.Nodes;
import com.github.jreddit.memes.Topic;
import com.github.jreddit.memes.UserLink;

public class Twitter
{
	List<Tweet> tweets;
	List<Nodes> nodes;
	List<String> sentiments = new ArrayList<String>();
	List<Link> links;
	List<Nodes> userNodes = new ArrayList<Nodes>();
	List<UserLink> userLinks = new ArrayList<UserLink>();
	List<Submission> submissions = new ArrayList<Submission>();
	List<Topic> sankeyNodes = new ArrayList<Topic>();
	String keyword;
	
	public List<String> getSentiments() {
		return sentiments;
	}

	public void setSentiments(List<String> sentiments) {
		this.sentiments = sentiments;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public List<Nodes> getNodes() {
		return nodes;
	}

	public void setNodes(List<Nodes> nodes) {
		this.nodes = nodes;
	}

	public List<Nodes> getUserNodes() {
		return userNodes;
	}

	public void setUserNodes(List<Nodes> userNodes) {
		this.userNodes = userNodes;
	}

	public List<UserLink> getUserLinks() {
		return userLinks;
	}

	public void setUserLinks(List<UserLink> userLinks) {
		this.userLinks = userLinks;
	}

	public List<Submission> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(List<Submission> submissions) {
		this.submissions = submissions;
	}

	public List<Topic> getSankeyNodes() {
		return sankeyNodes;
	}

	public void setSankeyNodes(List<Topic> sankeyNodes) {
		this.sankeyNodes = sankeyNodes;
	}
	
	public Twitter() {
		
	}

	public Twitter(List<Nodes> nodes, List<Link> links, List<Tweet> tweets) {
		this.nodes = nodes;
		this.links = links;
		this.tweets = tweets;
	}
	
	public Twitter(List<Nodes> nodes, List<Link> links, List<Tweet> tweets, List<String> sentiments) {
		this.nodes = nodes;
		this.links = links;
		this.tweets = tweets;
		this.sentiments = sentiments;
	}
	
	public Twitter(List<Topic> nodes, List<UserLink> links) {
		this.sankeyNodes = nodes;
		this.userLinks = links;
	}
	
	public Twitter(List<Nodes> nodes, List<Nodes> userNodes, List<Link> links, List<UserLink> userLinks,List<Tweet> tweets, String keyword) {
		this.nodes = nodes;
		this.userNodes = userNodes;
		this.links = links;
		this.userLinks = userLinks;
		this.tweets = tweets;
		this.keyword = keyword;
	}
}
