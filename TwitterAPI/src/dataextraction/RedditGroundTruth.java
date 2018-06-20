package dataextraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Utils.PropertyUtils;
import sentiment.SentimentString;

public class RedditGroundTruth {
	public static List<GroundTruthNode> parseCsv(String filename) throws IOException{
		List<GroundTruthNode> submissions = new ArrayList<GroundTruthNode>();
		
		Scanner scanner = new Scanner(new File(filename));
		scanner.useDelimiter("\n");
        
        while(scanner.hasNext()) {
        	String[] values = scanner.next().split("\t");
        	
        	GroundTruthNode newSub = new GroundTruthNode(values[0], values[1], null);
        	submissions.add(newSub);
        }
        scanner.close();
		
		return submissions;
	}
	
	public static void processSentiment(List<GroundTruthNode> submissions) throws IOException {
		for (GroundTruthNode sub : submissions) {
			sub.setSentiment(SentimentString.getSentimentString(sub.getPost()));
		}
	}
	
	public static void saveToCsv(List<GroundTruthNode> submissions, String outputFile) throws IOException {
		FileWriter fileWriter = new FileWriter(outputFile);
		StringBuilder stringBuilder = new StringBuilder();
		
		for (GroundTruthNode sub : submissions)
	 		stringBuilder.append("\"" + sub.getPost() + "\"," + sub.getBeliefClass().replaceAll("\r", "") + "," + sub.getSentiment() + "\n");
		
		fileWriter.write(stringBuilder.toString());
		fileWriter.close();
	}
	
	public static void main(String[] args) throws IOException {
		String filename = "C:\\Users\\nico\\Downloads\\trump biggest tax cut_reddit - Sheet1.tsv";
		
		List<GroundTruthNode> submissions = new ArrayList<GroundTruthNode>();
		submissions = parseCsv(filename);
		
		processSentiment(submissions);
		saveToCsv(submissions, "C:\\Users\\nico\\Downloads\\trump biggest tax cut_reddit - Sheet1.csv");
	}

}
