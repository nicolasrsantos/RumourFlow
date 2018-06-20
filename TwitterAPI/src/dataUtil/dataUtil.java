package dataUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.github.jreddit.memes.MemesJSON;
import com.github.jreddit.memes.Nodes;
import com.google.gson.Gson;

import Utils.PropertyUtils;
import sentiment.SentimentString;
import com.github.jreddit.*;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;

public class dataUtil
{
	public static void extractSentiment(String inputFile, String outputFile) throws IOException
	{
		try {
			Scanner scanner = new Scanner(new File(inputFile));
			scanner.useDelimiter("\n");
			
			List<String> sentiments = new ArrayList<String>();
			
			while (scanner.hasNext())
			{
				String[] values = scanner.next().split(",");
			    sentiments.add(SentimentString.getSentimentString(values[0]));
			}
			
			FileWriter fileWriter = new FileWriter(outputFile);
			StringBuilder stringBuilder = new StringBuilder();
			
			for (String sentiment : sentiments)
				stringBuilder.append(sentiment + "\n");
			
			fileWriter.write(stringBuilder.toString());
			fileWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getRootMessage(String user, List<Nodes> nodes)
	{
		for (Nodes node : nodes)
			if (node.getLabel().equals(user))
				return node.getTitle();
		
		return null;
	}
	
	public static void dataToCsv(String inputDir, String keyword) throws IOException
	{
		String json = FileUtils.readFileToString(new File(inputDir + keyword + ".json"));
		
		Gson gson = new Gson();
		MemesJSON posts = gson.fromJson(json, MemesJSON.class);
		List<Nodes> nodes = posts.getNodes();
		List<Nodes> userNodes = posts.getUserNodes();
		
		PrintWriter fileWriter = new PrintWriter(inputDir + keyword + ".csv");
		
		for (Nodes node : userNodes)
		{
			if (node.getLabel().equals(node.getTitle()))
				fileWriter.println(getRootMessage(node.getLabel(), nodes));
			else
				fileWriter.println(node.getLabel());
		}
		
		fileWriter.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		String keyword = "trump biggest tax cut_reddit";
		String inputDir = new StringBuilder().append(PropertyUtils.getCommentPath()).toString();
		
		dataToCsv(inputDir, keyword);
	}
}
