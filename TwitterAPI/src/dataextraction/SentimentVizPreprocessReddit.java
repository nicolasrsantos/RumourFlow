package dataextraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.github.jreddit.memes.MemesJSON;
import com.github.jreddit.memes.Nodes;
import com.google.gson.Gson;

import Utils.PropertyUtils;
import examples.Twitter;

public class SentimentVizPreprocessReddit {
	public static Boolean exists(Nodes node, List<SentimentNode> sentimentNodes)
	{
		for (SentimentNode sentimentNode : sentimentNodes)
			if (sentimentNode.getId().toString().equals(node.getId().toString()))
				return true;
		
		for (SentimentNode sentimentNode : sentimentNodes)
			if (sentimentNode.getChildren() != null && !sentimentNode.getChildren().isEmpty())
				return exists(node, sentimentNode.getChildren());
				
		return false;
	}
	
	public static Boolean addElement(SentimentNode node, List<SentimentNode> sentimentNodes)
	{
		for (SentimentNode sentimentNode : sentimentNodes)
			if (sentimentNode.getId().toString().equals(node.getParentId()))
				sentimentNode.getChildren().add(node);
			
		for (SentimentNode sentimentNode : sentimentNodes)
			if (sentimentNode.getChildren() != null && !sentimentNode.getChildren().isEmpty())
				return addElement(node, sentimentNode.getChildren());
				
		return false;
	}
	
	public static void preprocess(String inputDir, String keyword) throws IOException
	{
		String json = FileUtils.readFileToString(new File(inputDir + keyword + ".json"));
		
		Gson gson = new Gson();
		MemesJSON posts = gson.fromJson(json, MemesJSON.class);
		List<Nodes> nodes = posts.getUserNodes();
		List<SentimentNode> sentimentNodes = new ArrayList<SentimentNode>();
		
		for (Nodes node : nodes)
		{
			if (node.getParentID() != null)
			{
				if (!exists(node.getParentID(), sentimentNodes))
				{
					SentimentNode son = new SentimentNode(node.getTimestamp(), node.getTitle(), node.getSentiment(), node.getLabel(), new ArrayList<SentimentNode>(), "comment", node.getParentID().getId().toString(), node.getId());
					
					List<SentimentNode> children = new ArrayList<SentimentNode>();
					children.add(son);
					
					sentimentNodes.add(new SentimentNode(node.getParentID().getTimestamp(), node.getParentID().getTitle(), node.getParentID().getSentiment().equals("N/A")? "Neutral" : node.getParentID().getSentiment(),
									   node.getParentID().getLabel(), children, node.getParentID().getParentID() != null? "comment" : "root",
									   node.getParentID().getParentID() != null? node.getParentID().getId().toString() : "-1", node.getParentID().getId()));
				}
				else
				{
					SentimentNode element = new SentimentNode(node.getTimestamp(), node.getTitle(), node.getSentiment(), node.getLabel(), new ArrayList<SentimentNode>(), "comment", node.getParentID().getId().toString(), node.getId());
					addElement(element, sentimentNodes);
				}
			}
			else if (!exists(node, sentimentNodes))
				sentimentNodes.add(new SentimentNode(node.getTimestamp(), node.getTitle(), "Neutral", node.getLabel(), new ArrayList<SentimentNode>(), "root", "-1", node.getId()));
		}
		
		SentimentNode rootNode = new SentimentNode(0.0, null, null, null, sentimentNodes, "hidden", null);

		String saveJson = gson.toJson(rootNode);
		File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sentimentVis.json");
		FileUtils.writeStringToFile(file3, saveJson, false);
	}
	
	public static void main(String[] args) throws IOException
	{
		String keyword = "trump biggest tax cut_reddit";
		String inputDir = new StringBuilder().append(PropertyUtils.getCommentPath()).toString();
		
		preprocess(inputDir, keyword);
		
		System.out.println(keyword + " file saved.");
	}
}
