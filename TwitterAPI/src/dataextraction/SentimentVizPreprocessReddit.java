package dataextraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.memes.MemesJSON;
import com.github.jreddit.memes.Nodes;
import com.google.gson.Gson;

import Utils.PropertyUtils;
import examples.Twitter;
import sentiment.SentimentString;

public class SentimentVizPreprocessReddit {
//	public static Boolean exists(Nodes node, List<SentimentNode> sentimentNodes)
//	{
//		for (SentimentNode sentimentNode : sentimentNodes)
//			if (sentimentNode.getNickname().equals(node.getTitle()))
//				return true;
//		
//		for (SentimentNode sentimentNode : sentimentNodes)
//			if (sentimentNode.getChildren() != null && !sentimentNode.getChildren().isEmpty())
//				return exists(node, sentimentNode.getChildren());
//				
//		return false;
//	}
//	
//	public static Boolean addElement(SentimentNode node, List<SentimentNode> sentimentNodes)
//	{
//		for (SentimentNode sentimentNode : sentimentNodes)
//			if (sentimentNode.getNickname().equals(node.getParentId()))
//				sentimentNode.getChildren().add(node);
//			
//		for (SentimentNode sentimentNode : sentimentNodes)
//			if (sentimentNode.getChildren() != null && !sentimentNode.getChildren().isEmpty())
//				return addElement(node, sentimentNode.getChildren());
//				
//		return false;
//	}
	
	public static List<SentimentNode> nodeToSentimentNode(List<Nodes> nodes) {
		List<SentimentNode> sentimentNodes = new ArrayList<SentimentNode>();
		
		for (Nodes node : nodes) {
			SentimentNode newNode = new SentimentNode(node.getTimestamp(), node.getTitle(), node.getSentiment(), node.getLabel(), new ArrayList<SentimentNode>(), "comment", node.getParentID().getParentID() != null? node.getParentID().getTitle() : node.getParentID().getLabel());
			sentimentNodes.add(newNode);
		}
		
		return sentimentNodes;		
	}
	
	public static Boolean exists(Nodes node, SentimentNode root) {
		if ((root.getNickname().toLowerCase().equals(node.getTitle().toLowerCase()) || root.getNickname().toLowerCase().equals(node.getLabel().toLowerCase())) && (root.getTweet().equals(node.getTitle()) || root.getTweet().equals(node.getLabel()))) {
			return true;
		}
		 
		Boolean ret = false;
		for (int i = 0; ret == false && i < root.getChildren().size(); i++) {
			ret = exists(node, root.getChildren().get(i));
		}
		 
		return ret;
	}
	
	public static Boolean addNode(SentimentNode node, SentimentNode root) {
		if (root.getNickname().toLowerCase().equals(node.getParentId().toLowerCase())) {
			root.getChildren().add(node);
			return true;
		}
		
		Boolean ret = false;
		for (int i = 0; ret == false && i < root.getChildren().size(); i++) {
			ret = addNode(node, root.getChildren().get(i));
		}
		
		return false;
	}
	
	public static void preprocess(String inputDir, String keyword) throws IOException {
		String json = FileUtils.readFileToString(new File(inputDir + keyword + ".json"));
		
		Gson gson = new Gson();
		MemesJSON posts = gson.fromJson(json, MemesJSON.class);
		List<Nodes> nodes = posts.getUserNodes();
		
		List<Nodes> nodesToRemove = new ArrayList<Nodes>();
		for (Nodes node : nodes) {
			if (node.getTitle().toLowerCase().equals(node.getLabel())) {
				nodesToRemove.add(node);
			}
		}
		
		nodes.removeAll(nodesToRemove);
		
		List<Nodes> submissions = posts.getNodes();
		List<SentimentNode> firstLevel = new ArrayList<SentimentNode>();
		
		for (Nodes submission : submissions) {
			SentimentNode newNode = new SentimentNode(submission.getTimestamp(), submission.getLabel(), SentimentString.getSentimentString(submission.getTitle()), submission.getTitle(), new ArrayList<SentimentNode>(), "root", "-1");
			firstLevel.add(newNode);
		}
		
		SentimentNode root = new SentimentNode(0.0, "-", "-", "-", firstLevel, "-", "-");
		
		Stack<Nodes> stack;
		for (Nodes node : nodes) {
			stack = new Stack<Nodes>();
			
			while (node.getParentID() != null) {
				stack.push(node);
				node = node.getParentID();
			}
			
			while (!stack.isEmpty()) {
				Nodes curNode = stack.pop();
				
				if (!exists(curNode, root)) {
					SentimentNode newNode = new SentimentNode(curNode.getTimestamp(), curNode.getTitle(), curNode.getSentiment(), curNode.getLabel(), new ArrayList<SentimentNode>(), "comment", curNode.getParentID().getParentID() != null? curNode.getParentID().getTitle() : curNode.getParentID().getLabel());
					addNode(newNode, root);
				}
			}
		}
		
		String saveJson = gson.toJson(root);
		File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sentimentVis.json");
		FileUtils.writeStringToFile(file3, saveJson, false);
		
//		for (Nodes node : nodes)
//		{
//			if (node.getParentID() != null)
//			{
//				if (!exists(node.getParentID(), sentimentNodes))
//				{
//					SentimentNode son = new SentimentNode(node.getTimestamp(), node.getTitle(), node.getSentiment(), node.getLabel(), new ArrayList<SentimentNode>(), "comment", node.getParentID().getTitle());
//					
//					List<SentimentNode> children = new ArrayList<SentimentNode>();
//					children.add(son);
//					
//					sentimentNodes.add(new SentimentNode(node.getParentID().getTimestamp(), node.getParentID().getTitle(), node.getParentID().getSentiment().equals("N/A")? "Neutral" : node.getParentID().getSentiment(),
//									   node.getParentID().getLabel(), children, node.getParentID().getParentID() != null? "comment" : "root",
//									   node.getParentID().getParentID() != null? node.getParentID().getTitle() : "-1"));
//				}
//				else
//				{
//					SentimentNode element = new SentimentNode(node.getTimestamp(), node.getTitle(), node.getSentiment(), node.getLabel(), new ArrayList<SentimentNode>(), "comment", node.getParentID().getTitle());
//					addElement(element, sentimentNodes);
//				}
//			}
//			else if (!exists(node, sentimentNodes))
//				sentimentNodes.add(new SentimentNode(node.getTimestamp(), node.getTitle(), "Neutral", node.getLabel(), new ArrayList<SentimentNode>(), "root", "-1"));
//		}
//		
//		SentimentNode rootNode = new SentimentNode(0.0, null, null, null, sentimentNodes, "hidden", null);
//
//		String saveJson = gson.toJson(rootNode);
//		File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sentimentVis.json");
//		FileUtils.writeStringToFile(file3, saveJson, false);
	}
	
	public static void main(String[] args) throws IOException
	{
		String keyword = "papers global warming myth_reddit";
		String inputDir = new StringBuilder().append(PropertyUtils.getCommentPath()).toString();
		
		preprocess(inputDir, keyword);
		
		System.out.println(keyword + " file saved.");
	}
}
