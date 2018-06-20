package dataextraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.memes.MemesJSON;
import com.github.jreddit.memes.Nodes;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Utils.PropertyUtils;
import examples.ExtractSubmissionByKeyWord;
import sentiment.WikipediaTopicExtraction;

public class RedditExtraction {
	public static void saveToCsv(List<GroundTruthNode> submissions, String outputFile) throws IOException {
		FileWriter fileWriter = new FileWriter(outputFile);
		StringBuilder stringBuilder = new StringBuilder();
		
		for (GroundTruthNode sub : submissions)	{
			System.out.println("Saving new string");
			stringBuilder.append("\"" + sub.getPost() + "\"," + sub.getBeliefClass().replaceAll("\r", "") + "," + sub.getSentiment() + "\n");
			System.out.println("String saved");
		}
	 		
		System.out.println("Saving file");
		fileWriter.write(stringBuilder.toString());
		System.out.println("File saved");
		fileWriter.close();
	}
	
	public static void getUserKarma(String inputDir, String keyword) throws IOException {
		String json = FileUtils.readFileToString(new File(inputDir + keyword + ".json"));
		Gson gson = new Gson();
		MemesJSON posts = gson.fromJson(json, MemesJSON.class);
		
		List<Nodes> nodes = posts.getUserNodes();
		String sURL = null;
		List<GroundTruthNode> karmaNodes = new ArrayList<GroundTruthNode>();
		for (Nodes node : nodes) {
			if (node.getTitle() == node.getLabel()) {
				System.out.println("Processing new node");
				
				sURL = "https://www.reddit.com/user/" + node.getTitle() + "/about.json";
				
				URL url = new URL(sURL);
				URLConnection request = url.openConnection();
				request.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
				request.connect();
				
				GroundTruthNode newNode = new GroundTruthNode();
				newNode.setPost(node.getLabel().replaceAll("\n", " "));
				newNode.setBeliefClass(node.getTitle());
				
				try {
					JsonParser jp = new JsonParser(); //from gson
					JsonObject root = jp.parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
					JsonObject rootData = root.get("data").getAsJsonObject();
					
					if (rootData.has("link_karma") && rootData.has("comment_karma")) {
						int linkKarma = rootData.get("link_karma").getAsInt();
						int commentKarma = rootData.get("comment_karma").getAsInt();
						
						newNode.setSentiment(Integer.toString(linkKarma + commentKarma));
					} else {
						newNode.setSentiment(Integer.toString(0));
					}
				} catch (FileNotFoundException e) {
					newNode.setSentiment(Integer.toString(0));
				}
				
				karmaNodes.add(newNode);
				System.out.println("New node processed");
			}
		}
		
		System.out.println("Saving to csv file");
		saveToCsv(karmaNodes, "C:\\Users\\nico\\Desktop\\posts.csv");
	}
	
	public static Boolean isChild(Nodes dad, Nodes child) {
		while (child.getParentID() != null) {
			child = child.getParentID();
		}
		
		if (child.getLabel().equals(dad.getLabel())) {
			return true;
		}
		
		return false;
	}
	
	public static void getRealSubmissions(String inputDir, String keyword) throws IOException {
		String json = FileUtils.readFileToString(new File(inputDir + keyword + ".json"));
		Gson gson = new Gson();
		MemesJSON posts = gson.fromJson(json, MemesJSON.class);
		
		List<Nodes> nodes = posts.getNodes();
		List<Nodes> userNodes = posts.getUserNodes();
		List<FakeSubmission> listSubs = new ArrayList<FakeSubmission>();
		
		for (Nodes node : nodes) {
			 FakeSubmission newSub = new FakeSubmission();
			 newSub.setCreated(node.getTimestamp());
			 
			 List<String> users = new ArrayList<String>();
			 users.add(node.getLabel());
			 
		 	 for (Nodes userNode : userNodes) {
				 if (isChild(node, userNode)) {
					 users.add(userNode.getTitle());	 
				 }
			 }
			 
			newSub.setUsers(users);
			listSubs.add(newSub);
		}
		
		List<List<String>> spreaders = new ArrayList<>();
		List<List<String>> ignorants = new ArrayList<>();
		List<List<String>> stiflers = new ArrayList<>();

		List<List<String>> spreaders_temp = new ArrayList<>();
		
		Comparator<FakeSubmission> comparator = new Comparator<FakeSubmission>() {
		    public int compare(FakeSubmission c1, FakeSubmission c2) {
		        return (int) (c1.getCreated() - c2.getCreated()); // use your logic
		    }
		};
		
		Collections.sort(listSubs, comparator);
		
		for (int i = 0; i < listSubs.size(); i++) {
			List<String> spreader = new ArrayList<>();
			List<String> ignorant = new ArrayList<>();
			List<String> stifler = new ArrayList<>();

			List<String> spreader_temp = new ArrayList<>();

			FakeSubmission sub = listSubs.get(i);
			// at t0, one spreader and the rest are ignorants
			if (i == 0) {
				for (int j = 0; j < sub.getUsers().size(); j++) {
					if (j == 0) {
						if (!spreader.contains(sub.getUsers().get(j)))
							spreader.add(sub.getUsers().get(j));
					} else {
						if (!ignorant.contains(sub.getUsers().get(j)))
							ignorant.add(sub.getUsers().get(j));
					}

				}
				spreaders.add(spreader);
				ignorants.add(ignorant);
				stiflers.add(stifler);

				// ignorant become spreaders now
				for (int j = 0; j < sub.getUsers().size(); j++) {
					spreader_temp.add(sub.getUsers().get(j));
				}

				spreaders_temp.add(spreader_temp);
			} else {
				// at t1..tn, calculate spreader, ignorant, and stifler
				for (int j = 0; j < sub.getUsers().size(); j++) {
					String user = sub.getUsers().get(j);
					if (j == 0) {
						spreader.add(user);
					} else {
						// active spreader
						if (ExtractSubmissionByKeyWord.checkIfSpreader(spreaders_temp, user)) {
							if (!spreader.contains(user)) {
								spreader.add(user);
							}
						} else if (ExtractSubmissionByKeyWord.checkIfStifler(spreaders_temp, user)) {
							if (!stifler.contains(user)) {
								stifler.add(user);
							}
						} else if (!ignorant.contains(user)) {
							ignorant.add(user);
						}
					}

				}

				spreaders.add(spreader);
				ignorants.add(ignorant);
				stiflers.add(stifler);

				// at t1..tn, calculate spreader, ignorant, and stifler
				for (int j = 0; j < sub.getUsers().size(); j++) {
					// active spreader
					String user = sub.getUsers().get(j);
					spreader_temp.add(user);
				}
				spreaders_temp.add(spreader_temp);
			}
		}
		
		for (List<String> list : spreaders) {
			for (String name : list) {
				System.out.println(name + ",spreader");
			}
		}
		for (List<String> list : ignorants) {
			for (String name : list) {
				System.out.println(name + ",ignorant");
			}
		}
		for (List<String> list : stiflers) {
			for (String name : list) {
				System.out.println(name + ",stifler");
			}
		}
	}
	
	public static Integer getNumberOfTopics(GroundTruthNode user, List<GroundTruthNode> users) {
		int numberOfTopics = 0;
		for (GroundTruthNode cur : users) {
			if (cur.getBeliefClass().equals(user.getBeliefClass())) {
				numberOfTopics += WikipediaTopicExtraction.getWikis(cur.getPost()).size();
			}
		}
		
		return numberOfTopics;
	}
	
	public static void getTopics(String filename) throws IOException {
		List<GroundTruthNode> users = new ArrayList<GroundTruthNode>();
		users = RedditGroundTruth.parseCsv(filename);
		
		for (GroundTruthNode user : users) {
			System.out.println(getNumberOfTopics(user, users));
		}
	}
	
	public static void main(String[] args) throws IOException {
		String filename = "C:\\Users\\nico\\Downloads\\postUser.tsv";
		
		getTopics(filename);
	}
}
