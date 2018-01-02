package com.github.jreddit.memes;

import java.util.ArrayList;
import java.util.List;

import com.github.jreddit.entity.Submission;

public class MemesJSON {
	private List<Nodes> nodes = new ArrayList<Nodes>();
	private List<Nodes> userNodes = new ArrayList<Nodes>();
	private List<Link> links = new ArrayList<Link>();
	private List<UserLink> userLinks = new ArrayList<UserLink>();
	private List<Submission> submissions = new ArrayList<Submission>();
	private List<Topic> sankeyNodes = new ArrayList<Topic>();
	private List<String> sentiments = new ArrayList<String>();
	private String keyword;
	
	
	public List<String> getSentiments() {
		return sentiments;
	}
	public void setSentiments(List<String> sentiments) {
		this.sentiments = sentiments;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public List<Topic> getSankeyNodes() {
		return sankeyNodes;
	}
	public void setSankeyNodes(List<Topic> sankeyNodes) {
		this.sankeyNodes = sankeyNodes;
	}
	public List<Nodes> getNodes() {
		return nodes;
	}
	public void setNodes(List<Nodes> nodes) {
		this.nodes = nodes;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public List<Submission> getSubmissions() {
		return submissions;
	}
	public void setSubmissons(List<Submission> submissions) {
		this.submissions = submissions;
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
	public MemesJSON(List<Nodes> nodes, List<Link> links, List<Submission> submissions) {
		super();
		this.nodes = nodes;
		this.links = links;
		this.submissions = submissions;
	}
	
	public MemesJSON(List<Nodes> nodes, List<Link> links, List<Submission> submissions, List<String> sentiments) {
		super();
		this.nodes = nodes;
		this.links = links;
		this.submissions = submissions;
		this.sentiments = sentiments;
	}
	
	public MemesJSON(List<Topic> nodes, List<UserLink> links) {
		super();
		this.sankeyNodes = nodes;
		this.userLinks = links;
	}
	
	public MemesJSON(List<Nodes> nodes, List<Nodes> userNodes, List<Link> links, List<UserLink> userLinks,List<Submission> submissions, String keyword) {
		super();
		this.nodes = nodes;
		this.userNodes = userNodes;
		this.links = links;
		this.userLinks = userLinks;
		this.submissions = submissions;
		this.keyword = keyword;
	}
	
}
