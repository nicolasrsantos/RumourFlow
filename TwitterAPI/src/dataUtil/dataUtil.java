package dataUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import sentiment.SentimentString;

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
	
	public static void main(String[] args)
	{
		try {
			extractSentiment("/home/nicolas/Downloads/twitter_gt.csv", "/home/nicolas/Downloads/test.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
