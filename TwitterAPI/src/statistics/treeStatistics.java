package statistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import Utils.PropertyUtils;
import dataextraction.SentimentNode;
import examples.Tweet;

public class treeStatistics {
	public static void getLeavesSentiment(SentimentNode tree, Integer[] sentiment) {
		if (tree.getChildren() == null || tree.getChildren().isEmpty()) {
			if (tree.getSentiment().equals("Very positive")) {
				sentiment[0] += 1;
			} else if (tree.getSentiment().equals("Positive")) {
				sentiment[1] += 1;
			} else if (tree.getSentiment().equals("Neutral")) {
				sentiment[2] += 1;
			} else if (tree.getSentiment().equals("Negative")) {
				sentiment[3] += 1;
			} else if (tree.getSentiment().equals("Very negative")) {
				sentiment[4] += 1;
			} 
		}
		
		for (int i = 0; i < tree.getChildren().size(); i++) {
			getLeavesSentiment(tree.getChildren().get(i), sentiment);
		}
	}
	
	public static void leafStatistic(SentimentNode tree) {
		Integer[] sentiment = new Integer[] {0, 0, 0, 0, 0};
		
		getLeavesSentiment(tree, sentiment);
		
		for (int i = 0; i < sentiment.length; i++) {
			System.out.println(sentiment[i]);
		}
	}
	
	public static Integer getHighestValueIndex(Integer[] array) {
		Integer index = -1;
		Integer highest = Integer.MIN_VALUE;
		
		for (int i = 0; i < array.length; i++) {
			if (array[i] > highest) {
				index = i;
				highest = array[i];
			}
		}
		
		return index;
	}
	
	public static void getLevelSentiment(SentimentNode tree, List<Integer[]> level) {
		Queue<SentimentNode> queue = new LinkedList<SentimentNode>();
		for (SentimentNode child : tree.getChildren()) {
			queue.add(child);
		}
		
		// Temp node to mark the end of a level
		SentimentNode tmp = new SentimentNode(null, "tmp", "tmp", "tmp", null, "tmp", "tmp");
		queue.add(tmp);
		
		Integer[] levelSentiment = new Integer[] {0, 0, 0, 0, 0};
		
		while (!queue.isEmpty()) {
			SentimentNode cur = queue.poll();
			
			if (cur != tmp) {
				if (cur.getSentiment().equals("Very positive")) {
					levelSentiment[0] += 1;
				} else if (cur.getSentiment().equals("Positive")) {
					levelSentiment[1] += 1;
				} else if (cur.getSentiment().equals("Neutral")) {
					levelSentiment[2] += 1;
				} else if (cur.getSentiment().equals("Negative")) {
					levelSentiment[3] += 1;
				} else if (cur.getSentiment().equals("Very negative")) {
					levelSentiment[4] += 1;
				}
				
				if (cur.getChildren() != null && !cur.getChildren().isEmpty()) {
					for (SentimentNode child : cur.getChildren()) {
						queue.add(child);
					}
				}
			} else {
				level.add(levelSentiment);
				levelSentiment = new Integer[] {0, 0, 0, 0, 0};
				
				if (!queue.isEmpty()) {
					queue.add(tmp);
				}
			}
		}
	}
	
	public static void levelStatistic(SentimentNode tree) {
		List<Integer[]> level = new ArrayList<Integer[]>();
		
		getLevelSentiment(tree, level);
		
		for (Integer[] item : level) {
			System.out.println(item[0] + " " + item[1] + " " + item[2] + " " + item[3] + " " + item[4]);
		}
	}
	
	public static void checkPattern(SentimentNode tree, List<Integer[]> sum) {
		Queue<SentimentNode> queue = new LinkedList<SentimentNode>();
		for (SentimentNode child : tree.getChildren()) {
			queue.add(child);
		}
		
		// Temp node to mark the end of a level
		SentimentNode tmp = new SentimentNode(null, "tmp", "tmp", "tmp", null, "tmp", "tmp");
		queue.add(tmp);
		
		List<Integer[]> level = new ArrayList<Integer[]>();
		Integer[] levelSentiment = new Integer[] {0, 0, 0, 0, 0};
		
		while (!queue.isEmpty()) {
			SentimentNode cur = queue.poll();
			
			if (cur != tmp) {
				if (cur.getSentiment().equals("Very positive")) {
					levelSentiment[0] += 1;
				} else if (cur.getSentiment().equals("Positive")) {
					levelSentiment[1] += 1;
				} else if (cur.getSentiment().equals("Neutral")) {
					levelSentiment[2] += 1;
				} else if (cur.getSentiment().equals("Negative")) {
					levelSentiment[3] += 1;
				} else if (cur.getSentiment().equals("Very negative")) {
					levelSentiment[4] += 1;
				}
				
				if (cur.getChildren() != null && !cur.getChildren().isEmpty()) {
					for (SentimentNode child : cur.getChildren()) {
						queue.add(child);
					}
				}
			} else {
				level.add(levelSentiment);
				levelSentiment = new Integer[] {0, 0, 0, 0, 0};
				
				if (!queue.isEmpty()) {
					queue.add(tmp);
				}
			}
		}
		
		for (int i = 0; i < level.size(); i++) {
			if (sum.size() < i + 1) {
				sum.add(level.get(i));
			} else {
				for (int j = 0; j < level.get(i).length; j++) {
					sum.get(i)[j] += level.get(i)[j];
				}
			}
		}
	}
	
	public static void getPattern(SentimentNode tree, String sentimentType) {
		System.out.println(sentimentType + " pattern");
		
		List<Integer[]> sum = new ArrayList<Integer[]>();
		for (int i = 0; i < tree.getChildren().size(); i++) {
			SentimentNode child = tree.getChildren().get(i);
			if (child.getSentiment().equals(sentimentType)) {
				checkPattern(child, sum);
			}
		}
		
		for (Integer[] item : sum) {
			for (int i = 0; i < item.length; i++) {
				System.out.print(item[i] + " ");
			}
			System.out.println();
		}
	}
	
	public static void calculateTreeDistribution(SentimentNode tree, Integer[] sentiments) {
		Queue<SentimentNode> queue = new LinkedList<SentimentNode>();
		for (SentimentNode child : tree.getChildren()) {
			queue.add(child);
		}
		
		while (!queue.isEmpty()) {
			SentimentNode cur = queue.poll();
			
			for (int i = 0; i < cur.getChildren().size(); i++) {
				SentimentNode curChild = cur.getChildren().get(i); 
				if (curChild.getSentiment().equals("Very positive")) {
					sentiments[0] += 1;
				} else if (curChild.getSentiment().equals("Positive")) {
					sentiments[1] += 1;
				} else if (curChild.getSentiment().equals("Neutral")) {
					sentiments[2] += 1;
				} else if (curChild.getSentiment().equals("Negative")) {
					sentiments[3] += 1;
				} else if (curChild.getSentiment().equals("Very negative")) {
					sentiments[4] += 1;
				}
				
				queue.add(curChild);
			}
		}
	}
	
	public static void getChildrenDistribution(SentimentNode tree) {
		Integer[] sentiments = new	 Integer[] {0, 0, 0, 0, 0};
		
		System.out.println("Calculating tree distribution");
		calculateTreeDistribution(tree, sentiments);
		
		System.out.println(sentiments[0] + " " + sentiments[1] + " " + sentiments[2] + " " + sentiments[3] + " " + sentiments[4]);
	}
	
	public static void getStatistics(String inputDir, String keyword) throws IOException {
		String json = FileUtils.readFileToString(new File(inputDir + keyword + ".json"));
		
		Gson gson = new Gson();
		SentimentNode tree = gson.fromJson(json, SentimentNode.class);
		
		leafStatistic(tree);
		levelStatistic(tree);
		getPattern(tree, "Negative");
		getPattern(tree, "Positive");
		getPattern(tree, "Neutral");
		getChildrenDistribution(tree);
	}
	
	public static void main(String[] args) throws IOException {
		String keyword = "papers global warming myth_reddit_sentimentVis";
		String inputDir = new StringBuilder().append(PropertyUtils.getCommentPath()).toString();
		
		getStatistics(inputDir, keyword);
	}

}
