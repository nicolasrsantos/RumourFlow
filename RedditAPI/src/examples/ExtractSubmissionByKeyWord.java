package examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.textsim.textrt.preproc.MultithreadTextrtPreprocessor;
import org.textsim.textrt.preproc.tokenizer.PennTreeBankTokenizer;
import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.util.token.DefaultTokenFilter;

import com.github.jreddit.entity.Comment;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.exception.RedditError;
import com.github.jreddit.exception.RetrievalFailedException;
import com.github.jreddit.memes.Link;
import com.github.jreddit.memes.MemesJSON;
import com.github.jreddit.memes.Nodes;
import com.github.jreddit.memes.Spreader;
import com.github.jreddit.memes.Topic;
import com.github.jreddit.memes.UserLink;
import com.github.jreddit.retrieval.Comments;
import com.github.jreddit.retrieval.ExtendedComments;
import com.github.jreddit.retrieval.ExtendedSubmissions;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.CommentSort;
import com.github.jreddit.retrieval.params.SearchSort;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.retrieval.params.TimeSpan;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import Utils.PropertyUtils;
import sentiment.WikipediaTopicExtraction;

public class ExtractSubmissionByKeyWord {
	private static String searchMode = "";
	private static SinglethreadTextRtProcessor sim = null;
	private static MultithreadTextrtPreprocessor preprocessor = null;
	public static List<Submission> listSubmissions = new ArrayList<Submission>();
	public static List<Tweet> listTwitterSubmissions = new ArrayList<Tweet>();
	public static Gson gson = new Gson();
	public static List<Nodes> listNodes = new ArrayList<Nodes>();
	public static List<Link> listLinks = new ArrayList<Link>();
	public static Integer index = 0;
	public static Integer centIndex = 0;

	// comment nodes
	public static List<Nodes> listCommentNodes = new ArrayList<Nodes>();
	public static List<Link> listCommentLinks = new ArrayList<Link>();

	// centrality nodes
	public static List<Nodes> listCentNodes = new ArrayList<Nodes>();
	public static List<UserLink> listCentLinks = new ArrayList<UserLink>();
	public static List<String> listUsers = new ArrayList<String>();
	public static Map<String, List<String>> mapUsers = new HashMap<>();

	public static Integer MAX_SUBMISSIONS = 500;
	public static Integer MAX_COMMENTS = 10000;
	public static Integer MAX_WORD_CLOUD = 50;
	private static List<String> listStopWords = new ArrayList<String>();

	public static Boolean LOCAL_DATA = true;
	public static Double THRESHOLD_VALUE = 0.0;
	public static List<String> listSubs;
	public static MultithreadTextrtPreprocessor getProcessor() {
		// text similarity
		String unigramPath = PropertyUtils.getUnigramPath();

		if (preprocessor == null) {
			preprocessor = new MultithreadTextrtPreprocessor(unigramPath, null, null, new PennTreeBankTokenizer(),
					new DefaultTokenFilter());
		}
		return preprocessor;
	}

	public static SinglethreadTextRtProcessor getSim() {
		String trigramPath = PropertyUtils.getTrigramPath();

		if (sim == null) {
			try {
				sim = new SinglethreadTextRtProcessor(trigramPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		return sim;
	}
	
	public static String removeNonwords(String text) {
		text = text.toLowerCase().replaceAll("https?://\\S+\\s?", "").replaceAll("[^A-Za-z ]+", "");
		return text;
	}

	public static String getSubmissions(String mode, String keyWord) {
		listStopWords = new ArrayList<String>();
		//
		String stopWordFile = "";
		stopWordFile = PropertyUtils.getStopwordPath();

		// get a list of stop word
		File stopword = new File(stopWordFile);
		FileReader fr;
		try {
			fr = new FileReader(stopword);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				try {
					listStopWords.add(line);
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
			br.close();
			fr.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (LOCAL_DATA == true) {
			try {
				Gson gson = new Gson();
				String json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyWord + ".json"));
				MemesJSON memes = gson.fromJson(json, MemesJSON.class);
				listSubmissions = memes.getSubmissions();
				listLinks = memes.getLinks();
				listNodes = memes.getNodes();
				List<String> sens = new ArrayList<String>();
				if (memes.getSentiments().isEmpty()){
					for (int i = 0; i < listSubmissions.size(); i++) {
						Submission sub = listSubmissions.get(i);
						json = FileUtils
								.readFileToString(new File(PropertyUtils.getCommentPath() + sub.getFullName() + ".json"));
						MemesJSON comment = gson.fromJson(json, MemesJSON.class);
						int pos = 0, neg = 0;
						for (Nodes node : comment.getNodes()) {
							if (node.getSentiment().trim().toLowerCase().equals("negative")) {
								neg++;
							} else if (node.getSentiment().trim().toLowerCase().equals("positive")) {
								pos++;
							}
						}
						sens.add(String.valueOf((float) (pos - neg) / (pos + neg)));
					}
					memes.setSentiments(sens);
					FileUtils.writeStringToFile(new File(PropertyUtils.getRumourPath() + keyWord + ".json"), gson.toJson(memes));					
				}
				Gson gson1 = new Gson();
				String json1;
				Map<String, List<String>> mapUsers1 = new HashMap();
				Map<String, List<String>> activeUsers = new HashMap();
				try {
					json1 = FileUtils.readFileToString(new File(new String("C:\\Users\\nico\\workspace\\RumourFlow\\RedditAPI\\data\\reddit\\cholera puerto ricocommnents.json")));
					mapUsers1 = gson1.fromJson(json1, Map.class);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
//				//keep active users
//				int count = 0;
//				String number = "";
//				for (Map.Entry<String, List<String>> entry : mapUsers1.entrySet()){
//					String user = entry.getKey();
//					List<String> comments = entry.getValue();
//					if (comments.size() >= 5){
//						activeUsers.put(user, comments);
//						if (count < 10)
//							number = "00" + count;
//						else if (count < 100)
//							number = "0" + count;
//						else
//							number = String.valueOf(count);
//						count++;
//						for (String comment : comments){
//							FileUtils.writeStringToFile(new File("/Volumes/Data/Reddit/RedditAPI/data/comments/" + number + "_" + user + ".txt"), comment + System.getProperty("line.separator"), true);
//						}
//						
//					}
//				}
//				FileUtils.writeStringToFile(new File("/Volumes/Data/Reddit/RedditAPI/data/actives.json"), gson1.toJson(activeUsers));
				return gson.toJson(memes);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String subreddit = "";

		subreddit = "all";
		searchMode = "search";

		// Initialize REST Client
		RestClient restClient = new HttpRestClient();
		restClient.setUserAgent("bot/1.0 by name");

		// Connect the user
		User user = new User(restClient, "hellovn100", "111111");
		try {
			user.connect();
		} catch (IOException e1) {
			System.err.println("I/O Exception occured when attempting to connect user.");
			e1.printStackTrace();
			return "";
		} catch (ParseException e1) {
			System.err.println("I/O Exception occured when attempting to connect user.");
			e1.printStackTrace();
			return "";
		}

		try {
			// Handle to Submissions, which offers the basic API functionality
			Submissions subms = new Submissions(restClient, user);

			if (searchMode.equals("top")) {
				ExtendedSubmissions extendedSubms = new ExtendedSubmissions(subms);

				System.out.println("\n============== Top search ==============");
				List<Submission> submissionsSubredditExtra = extendedSubms.ofSubreddit(subreddit, SubmissionSort.NONE,
						50000, null);
				return processSubmissions(mode, restClient, user, submissionsSubredditExtra, subreddit, stopWordFile);
			} else {
				// Search for submissions
				System.out.println("\n============== Extended search submissions retrieval ==============");
				ExtendedSubmissions extendedSubms = new ExtendedSubmissions(subms);
				if (LOCAL_DATA == true) {
					try {
						Gson gson = new Gson();
						String json = FileUtils
								.readFileToString(new File(PropertyUtils.getRumourPath() + keyWord + ".json"));
						MemesJSON memes = gson.fromJson(json, MemesJSON.class);
						listSubmissions = memes.getSubmissions();
						listLinks = memes.getLinks();
						listNodes = memes.getNodes();
						return json;

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					List<Submission> submissionsSearchExtra = extendedSubms.searchTerm(keyWord, "all",
							SearchSort.RELEVANCE, TimeSpan.ALL, MAX_SUBMISSIONS, null);
					return processSubmissions(mode, restClient, user, submissionsSearchExtra, keyWord, stopWordFile);
				}
			}

		} catch (RetrievalFailedException e) {
			e.printStackTrace();
		} catch (RedditError e) {
			e.printStackTrace();
		}
		return "";

	}
	
	public static String getSubmissionsTwitter(String keyWord) {		
		listStopWords = new ArrayList<String>();
		//
		String stopWordFile = "";
		stopWordFile = PropertyUtils.getStopwordPath();

		// get a list of stop word
		File stopword = new File(stopWordFile);
		FileReader fr;
		try {
			fr = new FileReader(stopword);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				try {
					listStopWords.add(line);
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
			br.close();
			fr.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (LOCAL_DATA == true) {
			try {
				Gson gson = new Gson();
				String json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyWord + ".json"));
				
				Twitter twitter = gson.fromJson(json, Twitter.class);
				
				List<Tweet> listTweets = twitter.getTweets();
				listLinks = twitter.getLinks();
				listNodes = twitter.getNodes();
				
				List<String> sens = new ArrayList<String>();
				if (twitter.getSentiments().isEmpty()){
					for (int i = 0; i < listTweets.size(); i++) {
						Tweet sub = listTweets.get(i);
						
						json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + sub.getUser().getScreenName() + ".json"));
						Twitter comment = gson.fromJson(json, Twitter.class);
						
						int pos = 0, neg = 0;
						for (Nodes node : comment.getNodes()) {
							if (node.getSentiment().trim().toLowerCase().equals("negative")) {
								neg++;
							} else if (node.getSentiment().trim().toLowerCase().equals("positive")) {
								pos++;
							}
						}
						sens.add(String.valueOf((float) (pos - neg) / (pos + neg)));
					}
					twitter.setSentiments(sens);
					FileUtils.writeStringToFile(new File(PropertyUtils.getRumourPath() + keyWord + ".json"), gson.toJson(twitter));					
				}
				
				/*Gson gson1 = new Gson();
				String json1;
				Map<String, List<String>> mapUsers1 = new HashMap();
				Map<String, List<String>> activeUsers = new HashMap();
				try {
					json1 = FileUtils.readFileToString(new File(new String("C:\\Users\\Nicolas\\workspace\\RedditAPI\\data\\Obama & Muslimcommnents.json")));
					mapUsers1 = gson1.fromJson(json1, Map.class);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}*/

				return gson.toJson(twitter);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "";
	}

	public static String removeStopWords(String text, List<String> stopWords) {

		for (int i = 0; i < stopWords.size(); i++) {
			try {
				text = text.toLowerCase().replace(" " + stopWords.get(i).toLowerCase() + " ", " ");
			} catch (Exception ex) {
			}
		}
		return text.trim().replaceAll(" +", " ");
	}

	public static String getWordClouds(String keyword, String redditID) {
		if (redditID.equals("undefined")) {
			File f1 = new File(PropertyUtils.getRumourPath() + keyword + "_cloud.json");

//			if (f1.exists()) {
//				String json = "";
//				try {
//					json = FileUtils.readFileToString(f1);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return json;  
//			}
			Gson gson = new Gson();
			String json;
			try {
				json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyword + ".json"));
				MemesJSON memes = gson.fromJson(json, MemesJSON.class);
				Map<String, Integer> mapCloud = new HashMap<String, Integer>();
				for (Submission s : memes.getSubmissions()) {
					// title
					String title = s.getTitle().replaceAll("[^A-Za-z0-9\\s]", "");
					String[] lsTitle = removeStopWords(title.toLowerCase(), listStopWords).split(" ");
					if (lsTitle.length > 1) {
						for (int i = 0; i < lsTitle.length; i++) {
							if (mapCloud.get(lsTitle[i].trim()) == null) {
								mapCloud.put(lsTitle[i].trim(), 1);
							} else {
								mapCloud.put(lsTitle[i].trim(), mapCloud.get(lsTitle[i].trim()) + 1);
							}
						}
					}

					String comment = s.getCommentContent().replaceAll("[^A-Za-z0-9\\s]", "");

					// comments
					String[] lsComments = removeStopWords(comment.toLowerCase(), listStopWords).split(" ");
					if (lsComments.length > 1) {
						for (int i = 0; i < lsComments.length; i++) {
							if (mapCloud.get(lsComments[i].trim()) == null) {
								mapCloud.put(lsComments[i].trim(), 1);
							} else {
								mapCloud.put(lsComments[i].trim(), mapCloud.get(lsComments[i].trim()) + 1);
							}
						}
					}
				}
				int count = 0;
				mapCloud = MapUtils.sortByValue(mapCloud);
				JsonArray arrCloud = new JsonArray();
				for (Map.Entry<String, Integer> entry : mapCloud.entrySet()) {
					if (entry.getKey().length() >= 3 && count < MAX_WORD_CLOUD
							&& !listStopWords.contains(entry.getKey()) && !entry.getKey().contains("'")) {
						JsonObject obj = new JsonObject();
						obj.addProperty("text", entry.getKey().trim());
						obj.addProperty("size", entry.getValue());
						arrCloud.add(obj);
						count++;
					}
				}
				FileUtils.writeStringToFile(new File(PropertyUtils.getRumourPath() + keyword + "_cloud.json"),
						arrCloud.toString());
				return arrCloud.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			String json;
			try {
				json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + redditID + ".json"));
				MemesJSON memes = gson.fromJson(json, MemesJSON.class);
				Map<String, Integer> mapCloud = new HashMap<String, Integer>();

				for (int i = 1; i < memes.getNodes().size(); i++) {
					Nodes node = memes.getNodes().get(i);

					// title
					String title = node.getLabel().replaceAll("[^A-Za-z0-9\\s]", "");
					title = removeStopWords(node.getLabel().toLowerCase(), listStopWords).replaceAll("[^A-Za-z0-9\\s]", "");

					String[] lsTitle = title.split(" ");
					for (int j = 0; j < lsTitle.length; j++) {
						if (mapCloud.get(lsTitle[j].trim()) == null) {
							mapCloud.put(lsTitle[j].trim(), 1);
						} else {
							mapCloud.put(lsTitle[j].trim(), mapCloud.get(lsTitle[j].trim()) + 1);
						}
					}
				}
				int count = 0;
				mapCloud = MapUtils.sortByValue(mapCloud);
				JsonArray arrCloud = new JsonArray();
				for (Map.Entry<String, Integer> entry : mapCloud.entrySet()) {
					if (entry.getKey().length() >= 3 && count < MAX_WORD_CLOUD
							&& !listStopWords.contains(entry.getKey()) && !entry.getKey().contains("'")) {
						JsonObject obj = new JsonObject();
						obj.addProperty("text", entry.getKey().trim());
						obj.addProperty("size", entry.getValue());
						arrCloud.add(obj);
						count++;
					}
				}
				return arrCloud.toString();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public static String getTopicClouds(String keyword) {
		String[] keys = keyword.split(",");
		if (keys.length > 1) {
			Gson gson = new Gson();
			String json;
			Map<String, Integer> mapCloud = new HashMap<String, Integer>();
			try {
				JsonArray arrCloud = new JsonArray();
				for (String key : keys) {
					
					json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + key.replaceAll("\"", "") + ".json"));
					MemesJSON memes = gson.fromJson(json, MemesJSON.class);

					for (Submission s : memes.getSubmissions()) {
						// topic
						for (int i = 0; i < s.getWikis().size(); i++) {
							if (mapCloud.get(s.getWikis().get(i).trim()) == null) {
								mapCloud.put(s.getWikis().get(i).trim(), 1);
							} else {
								mapCloud.put(s.getWikis().get(i).trim(), mapCloud.get(s.getWikis().get(i).trim()) + 1);
							}
						}
					}													
				}
				for (Map.Entry<String, Integer> entry : mapCloud.entrySet()) {
					JsonObject obj = new JsonObject();
					obj.addProperty("text", entry.getKey().trim());
					obj.addProperty("size", entry.getValue());
					arrCloud.add(obj);
				}		
				return arrCloud.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Gson gson = new Gson();
			String json;
			try {
				json = FileUtils
						.readFileToString(new File(PropertyUtils.getRumourPath() + keyword.replace(",", "") + ".json"));
				MemesJSON memes = gson.fromJson(json, MemesJSON.class);
				Map<String, Integer> mapCloud = new HashMap<String, Integer>();
				for (Submission s : memes.getSubmissions()) {
					// topic
					for (int i = 0; i < s.getWikis().size(); i++) {
						if (mapCloud.get(s.getWikis().get(i).trim()) == null) {
							mapCloud.put(s.getWikis().get(i).trim(), 1);
						} else {
							mapCloud.put(s.getWikis().get(i).trim(), mapCloud.get(s.getWikis().get(i).trim()) + 1);
						}
					}

				}
				JsonArray arrCloud = new JsonArray();
				for (Map.Entry<String, Integer> entry : mapCloud.entrySet()) {
					JsonObject obj = new JsonObject();
					obj.addProperty("text", entry.getKey().trim());
					obj.addProperty("size", entry.getValue());
					arrCloud.add(obj);
				}
				return arrCloud.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return "";
	}
	
	public static String getTopicCloudsTwitter(String keyword, boolean local)
	{
		Gson gson = new Gson();
		String json;
		JsonArray arrCloud = new JsonArray();
		
		try
		{
			if (local == true)
			{
				json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyword + "_topic_cloud_twitter.json"));
				return json;
			}
			
			json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyword.replace(",", "") + "_processed.json"));
			Twitter twitter = gson.fromJson(json, Twitter.class);
			
			Map<String, Integer> mapCloud = new HashMap<String, Integer>();
			for (Tweet t : twitter.getTweets())
			{
				// topic
				for (int i = 0; i < t.getTopics().size(); i++)
				{
					if (t.getTopics().get(i).length() > 3)
					{
						if (mapCloud.get(t.getTopics().get(i).trim()) == null)
							mapCloud.put(t.getTopics().get(i).trim(), 1);
						else
							mapCloud.put(t.getTopics().get(i).trim(), mapCloud.get(t.getTopics().get(i).trim()) + 1);
					}
				}
			}

			for (Map.Entry<String, Integer> entry : mapCloud.entrySet())
			{
				if (arrCloud.size() <= 50)
				{
					JsonObject obj = new JsonObject();
					obj.addProperty("text", entry.getKey().trim());
					obj.addProperty("size", entry.getValue());
					arrCloud.add(obj);
				}
			}
			
			FileUtils.writeStringToFile(new File(PropertyUtils.getRumourPath() + keyword + "_topic_cloud_twitter.json"), arrCloud.toString());
			
			return arrCloud.toString();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static String getUsers(String keyword) {
		if (keyword.equals("all")) {
			Gson gson = new Gson();
			String json;
			Map<String, Integer> mapCloud = new HashMap<String, Integer>();
			try {
				List<String> rumours = Arrays.asList("HIV & Cancer");
				for (String key : rumours) {
					json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + key + ".json"));
					MemesJSON memes = gson.fromJson(json, MemesJSON.class);

					for (Submission s : memes.getSubmissions()) {
						// comments
						String[] comments = s.getCommentContent().split(" ");
						for (int i = 0; i < comments.length; i++) {
							if (mapCloud.get(comments[i].trim()) == null) {
								mapCloud.put(comments[i].trim(), 1);
							} else {
								mapCloud.put(comments[i].trim(), mapCloud.get(comments[i].trim()) + 1);
							}
						}
					}

					JsonArray arrCloud = new JsonArray();
					for (Map.Entry<String, Integer> entry : mapCloud.entrySet()) {
						if (entry.getValue() >= 50) {
							JsonObject obj = new JsonObject();
							obj.addProperty("text", entry.getKey().trim());
							obj.addProperty("size", entry.getValue());
							arrCloud.add(obj);
						}
					}
					return arrCloud.toString();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	public static Double docDocComparison(String text1, String text2) throws IOException {

		// Create preprocessor
		MultithreadTextrtPreprocessor clientPreprocessor = getProcessor();
		text1 = removeNonwords(text1);
		text2 = removeNonwords(text2);
		// Compute similarity
		TextInstance text1Instance = clientPreprocessor.createTextInstance(text1);
		TextInstance text2Instance = clientPreprocessor.createTextInstance(text2);
		double similarity = getSim().sim(text1Instance, text2Instance);
		return similarity;
	}// textTextComparison

	private static boolean checkIfExist(List<String> listSubs, String id){
		for (String str: listSubs){
			if (id.contains(str)){
				return true;
			}
		}
		return false;
	}
	public static String processSubmissions(String mode, RestClient restClient, User user, List<Submission> subs,
			String subreddit, String stopWordFile) {
		// reset variables
		listSubs = Arrays.asList("3tl2ld","3tl2ld","12smar","3weumr","d8gnv","qsyk4","uf3if","76u6f","7c1b1","128ujy","3jaf09","3lfufc","10mrmt","6amuu","qt14b","6lkq9","daknr","60y0a","i2pkc","3kvuho","12a9ma","bsgxx","34feyi","2zaho6","1pxjed","6phzc","3jnmff","3vza8p","22aqsy","10o0dz","1qlyo4","2ptzcw","61v2o","7aalv","gwhox","2lih0s","759pf","12a9lw","d2zu2","3j88cy","11p4yh","1qjl6f","fhhxa","dqsse","3jcy3k","2soc33","7c98c","3z23gn","6elhh","d5ulg","2x5drf","6wtip","6an8j","x77o2","68vti","ec2bh","30hplo","6bzy9","d8x5f","d409s","1xz317","d371v","10nu0s","ekkdg","3sgeab","d344q","2c2x2r","3lpeav","6lkkh","6js0e","x7dqw","1bsx5d","27tjof","1tlmmm","336juf","3g7nnd","6dnwt","uhc04","3j7vml","3lessc","1seppx","278oqx","278oqx","11964u","o8aak","o8aak");
		gson = new Gson();

		// node-link graph
		listNodes = new ArrayList<Nodes>();
		listLinks = new ArrayList<Link>();
		listSubmissions = new ArrayList<Submission>();
		
		// centrality graph
		listCentNodes = new ArrayList<Nodes>();
		listCentLinks = new ArrayList<UserLink>();
		listUsers = new ArrayList<String>();
		
		int currentIndex = 0;
		centIndex = 0;
		index = 0;
		// add nodes
		for (Submission s : subs) {
			if (currentIndex >= MAX_SUBMISSIONS) {
				System.out.println("Submission max");
				break;
			}
			if (!checkIfExist(listSubs, s.getIdentifier())){
				continue;
			}
			List<String> users = new ArrayList<String>();
			if (s.getCommentCount() > 0 && s.getScore() > 0) {
				currentIndex++;		
				
				try {
					FileUtils.writeStringToFile(new File("/Volumes/Data/Reddit/RedditAPI/data/titles/" + s.getTitle()), s.getTitle());
					// comment tree
					listCommentNodes = new ArrayList<Nodes>();
					listCommentLinks = new ArrayList<Link>();
					index = 0;
					// add author for centrality graph
					List<String> userCent = new ArrayList<String>();
					if (!userCent.contains(s.getAuthor().toLowerCase())) {
						userCent.add(s.getAuthor().toLowerCase());
						Nodes subNode = new Nodes(centIndex, new ArrayList<Integer>(), 5.0, 5,
								s.getAuthor().toLowerCase(), s.getAuthor().toLowerCase(), centIndex, "red",
								s.getAuthor().toLowerCase(), "true", null, s.getCreated(), 0.0, "N/A");
						// insert users
						listCentNodes.add(subNode);
					}
					
					if (!mapUsers.containsKey(s.getAuthor().toLowerCase())){
						List<String> arrComments = new ArrayList<>();
						arrComments.add(s.getTitle());
						mapUsers.put(s.getAuthor().toLowerCase(), arrComments);
					}else{
						List<String> arrComments = mapUsers.get(s.getAuthor().toLowerCase());
						arrComments.add(s.getTitle());
						mapUsers.put(s.getAuthor().toLowerCase(), arrComments);
					}

					centIndex++;

					users.add(s.getAuthor().toLowerCase());

					Comments coms = new Comments(restClient, user);
					ExtendedComments extendedComs = new ExtendedComments(coms);

					Thread.sleep(3000);
					System.out.println("Get Comments Start: " + s.getIdentifier());
					List<Comment> commentsSubmissionExtra = extendedComs.ofSubmission(s.getIdentifier(),
							CommentSort.TOP, -1, null);

					System.out.println("Get Comments Done: " + s.getIdentifier());
					// create a central submission node
					Nodes oriNode = new Nodes(index, new ArrayList<Integer>(), 4.0, 5, s.getTitle(),
							s.getAuthor().toLowerCase(), index, "red", s.getFullName(), "true", null, s.getCreated(),
							0.0, "N/A");

					// insert sub into the map
					listNodes.add(oriNode);
					listCommentNodes.add(oriNode);

					// calculate comment
					index = 1;
					try {
						Comments.printCommentJson(s, commentsSubmissionExtra, listStopWords, oriNode, users);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<Submission> currentSub = new ArrayList<Submission>();
					currentSub.add(s);
					MemesJSON memes = new MemesJSON(listCommentNodes, listCommentLinks, currentSub);
					String json = gson.toJson(memes);
					File file3 = new File(PropertyUtils.getCommentPath() + s.getFullName() + ".json");
					FileUtils.writeStringToFile(file3, json, false);

					s.setCommentContent("");
					s.setUsers(users);
					listSubmissions.add(s);
				} catch (Exception e) {
					System.out.print(e.getMessage());
				}
			}
		}
		System.out.println("Submission end");
		// add links
		for (Nodes sub : listNodes) {
			for (Nodes subLink : listNodes) {
				if (!subLink.getTitle().equals(sub.getTitle())) {
					Double simScore = 0.0;

					try {
						simScore = docDocComparison(sub.getTitle(), subLink.getTitle());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (Double.isNaN(simScore)) {
						simScore = 0.00001;
					}
					Link link1 = new Link(sub.getIndex(), subLink.getIndex(), simScore);
					listLinks.add(link1);
				}
			}
		}

		try {
			MemesJSON memes = new MemesJSON(listNodes, listCentNodes, listLinks, listCentLinks, listSubmissions,
					subreddit);
			String json = gson.toJson(memes);
			File file3 = new File(PropertyUtils.getCommentPath() + subreddit + ".json");
			FileUtils.writeStringToFile(file3, json, false);
			
			json = gson.toJson(mapUsers);
			File file4 = new File(PropertyUtils.getCommentPath() + subreddit + "commnents.json");
			FileUtils.writeStringToFile(file4, json, false);
			
			return json;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String processTwitterSubmissions(List<Tweet> tweets, String subreddit) {
		// reset variables
		gson = new Gson();
		
		listStopWords = new ArrayList<String>();
		//
		String stopWordFile = "";
		stopWordFile = PropertyUtils.getStopwordPath();

		// get a list of stop word
		File stopword = new File(stopWordFile);
		FileReader fr;
		try {
			fr = new FileReader(stopword);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				try {
					listStopWords.add(line);
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
			br.close();
			fr.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// node-link graph
		listNodes = new ArrayList<Nodes>();
		listLinks = new ArrayList<Link>();
		listTwitterSubmissions = new ArrayList<Tweet>();
		
		// centrality graph
		listCentNodes = new ArrayList<Nodes>();
		listCentLinks = new ArrayList<UserLink>();
		listUsers = new ArrayList<String>();
		
		centIndex = 0;
		index = 0;
		
		// add nodes
		for (Tweet s : tweets) {
			// add author for centrality graph
			List<String> userCent = new ArrayList<String>();
			if (!userCent.contains(s.getUser().getScreenName().toLowerCase()))
			{
				Nodes subNode = new Nodes();
				
				userCent.add(s.getUser().getScreenName().toLowerCase());
				
				if (s.isNormalTweet())
				{
					subNode = new Nodes(centIndex, new ArrayList<Integer>(), 5.0, 5,
							s.getUser().getScreenName().toLowerCase(), s.getUser().getScreenName().toLowerCase(), centIndex, "red",
							s.getUser().getScreenName().toLowerCase(), "true", null, s.getTimestamp(), 0.0, "N/A", "normal");
				}
				else if (s.isReply())
				{
					subNode = new Nodes(centIndex, new ArrayList<Integer>(), 5.0, 5,
							s.getUser().getScreenName().toLowerCase(), s.getUser().getScreenName().toLowerCase(), centIndex, "red",
							s.getUser().getScreenName().toLowerCase(), "true", null, s.getTimestamp(), 0.0, "N/A", "reply");
				}
				else if (s.isRetweet())
				{
					subNode = new Nodes(centIndex, new ArrayList<Integer>(), 5.0, 5,
							s.getUser().getScreenName().toLowerCase(), s.getUser().getScreenName().toLowerCase(), centIndex, "red",
							s.getUser().getScreenName().toLowerCase(), "true", null, s.getTimestamp(), 0.0, "N/A", "retweet");
				}
				else if (s.isQuote())
				{
					subNode = new Nodes(centIndex, new ArrayList<Integer>(), 5.0, 5,
							s.getUser().getScreenName().toLowerCase(), s.getUser().getScreenName().toLowerCase(), centIndex, "red",
							s.getUser().getScreenName().toLowerCase(), "true", null, s.getTimestamp(), 0.0, "N/A", "quote");
				}
				
				// insert users
				listCentNodes.add(subNode);
			}			
			
			if (s.hasComments()) {
				Submission sub = new Submission("twitter", s.getUser().getScreenName());
				
				sub.setCreated(s.getTimestamp() / 1000);
				sub.setAuthor(s.getUser().getScreenName());
				sub.setPermalink("twitter.com/" + sub.getAuthor() + "/status/" + s.getTweetId());
				sub.setTitle(s.getTweet());
				sub.setSubreddit(subreddit);
				sub.setWikis(WikipediaTopicExtraction.getWikis(s.getTweet()));
				sub.setCommentContent("");
				
				List<String> users = new ArrayList<String>();
				users.add(new String(sub.getAuthor()));
				sub.setCommentCount(new Long(s.getComments().size()));
				sub.setUpVotes(new Long(s.getFavoriteCount() + s.getComments().size()));
				
				try {
					// comment tree
					listCommentNodes = new ArrayList<Nodes>();
					listCommentLinks = new ArrayList<Link>();
					index = 0;
					
					Integer indexComment = 0;
					for (CommentTwitter c : s.getComments())
					{
						if (!c.getUser().equals(c.getParentUser()))
						{
							Double sim = 0.0;		
							sim = 1 - ExtractSubmissionByKeyWord.docDocComparison(ExtractSubmissionByKeyWord.removeStopWords(c.getTweet(), listStopWords), ExtractSubmissionByKeyWord.removeStopWords(c.getOriginalTweet(), listStopWords));
							
							if (Double.isNaN(sim))
								sim = 0.0001;
														
							listCentLinks.add(new UserLink(c.getUser(), c.getParentUser(), sim, c.getType(), c.getSourceTimestamp(), c.getTargetTimestamp()));
							
							if (!users.contains(c.getUser()))
								users.add(new String(c.getUser()));
							
							listCommentLinks.add(new Link(indexComment, index, sim));
							listCommentNodes.add(new Nodes(indexComment, new ArrayList<Integer>(), 5.0, 5,
									c.getTweet(), c.getTweet(), centIndex, "red",
									c.getUser().toLowerCase(), "true", null, s.getTimestamp(), 0.0, "N/A", c.getType()));
							indexComment++;
						}
					}
					
					sub.setUsers(users);
					
					if (!mapUsers.containsKey(s.getUser().getScreenName().toLowerCase()))
					{
						List<String> arrComments = new ArrayList<>();
						arrComments.add(s.getTweet());
						mapUsers.put(s.getUser().getScreenName().toLowerCase(), arrComments);
					}
					else
					{
						List<String> arrComments = mapUsers.get(s.getUser().getScreenName().toLowerCase());
						arrComments.add(s.getTweet());
						mapUsers.put(s.getUser().getScreenName().toLowerCase(), arrComments);
					}

					centIndex++;

					users.add(s.getUser().getScreenName().toLowerCase());

					/*Comments coms = new Comments(restClient, user);
					ExtendedComments extendedComs = new ExtendedComments(coms);

					Thread.sleep(3000);
					System.out.println("Get Comments Start: " + s.getIdentifier());
					List<Comment> commentsSubmissionExtra = extendedComs.ofSubmission(s.getIdentifier(),
							CommentSort.TOP, -1, null);

					System.out.println("Get Comments Done: " + s.getIdentifier());*/

					// create a central submission node
					Nodes oriNode = new Nodes(index, new ArrayList<Integer>(), 4.0, 5, s.getTweet(), s.getUser().getScreenName().toLowerCase(), index, "red", s.getUser().getScreenName(), "true", null, s.getTimestamp(), 0.0, "N/A");

					// insert sub into the map
					listNodes.add(oriNode);
					listCommentNodes.add(oriNode);

					List<Tweet> currentTweet = new ArrayList<Tweet>();
					currentTweet.add(s);
					
					Twitter twitter = new Twitter(listCommentNodes, listCommentLinks, currentTweet);
					String json = gson.toJson(twitter);
					File file3 = new File(PropertyUtils.getCommentPath() + "twitter_" + s.getUser().getScreenName() + ".json");
					FileUtils.writeStringToFile(file3, json, false);

					listTwitterSubmissions.add(s);
					listSubmissions.add(sub);
				} catch (Exception e) {
					System.out.print(e.getMessage());
				}
			}
			else
			{
				listCentLinks.add(new UserLink(s.getUser().getScreenName(), s.getUser().getScreenName(), 0.0001, "normal", s.getTimestamp(), s.getTimestamp()));
			}
			
			
		}
		
		// add links
		for (Nodes sub : listNodes) {
			for (Nodes subLink : listNodes) {
				if (!subLink.getTitle().equals(sub.getTitle()))
				{
					Double simScore = 0.0;

					try {
						simScore = docDocComparison(sub.getTitle(), subLink.getTitle());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (Double.isNaN(simScore)) {
						simScore = 0.00001;
					}
					Link link1 = new Link(sub.getIndex(), subLink.getIndex(), simScore);
					listLinks.add(link1);
				}
			}
		}
		
		try {
			MemesJSON memes = new MemesJSON(listNodes, listCentNodes, listLinks, listCentLinks, listSubmissions, subreddit);
			String json = gson.toJson(memes);
			File file3 = new File(PropertyUtils.getCommentPath() + subreddit + ".json");
			FileUtils.writeStringToFile(file3, json, false);
			
			json = gson.toJson(mapUsers);
			File file4 = new File(PropertyUtils.getCommentPath() + subreddit + "commnents.json");
			FileUtils.writeStringToFile(file4, json, false);
			
			return json;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getCentralUsers(String keyword) {
		String json;
		try {
			json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + "central" + ".json"));
			return json;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}

	public static boolean checkIfSpreader(List<List<String>> spreaders, String user) {
		// only check the last submissions
		int spreader_threshold = 5;
		for (int k = 0; k < spreader_threshold; k++) {
			int index = spreaders.size() - 1 - k;
			if (index >= 0) {
				List<String> lsUsers = spreaders.get(index);
				for (int i = 0; i < lsUsers.size(); i++) {
					if (user.equals(lsUsers.get(i))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	// use to spread rumour once
	public static boolean checkIfStifler(List<List<String>> spreaders, String user) {
		// only check the last submissions
		for (int i = 0; i < spreaders.size(); i++) {
			List<String> lsUsers = spreaders.get(i);
			for (int j = 0; j < lsUsers.size(); j++) {
				if (user.equals(lsUsers.get(j))) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getRumourUsers(String keyword) {
		String json;
		try {
			json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + keyword + ".json"));
			Gson gson = new Gson();
			MemesJSON memes = gson.fromJson(json, MemesJSON.class);
			// calculate spreaders, ignorants, and stiflers
			List<List<String>> spreaders = new ArrayList<>();
			List<List<String>> ignorants = new ArrayList<>();
			List<List<String>> stiflers = new ArrayList<>();

			List<List<String>> spreaders_temp = new ArrayList<>();
			List<Submission> listSubs = memes.getSubmissions();
			
			Comparator<Submission> comparator = new Comparator<Submission>() {
			    public int compare(Submission c1, Submission c2) {
			        return (int) (c1.getCreated() - c2.getCreated()); // use your logic
			    }
			};
			
			Collections.sort(listSubs, comparator);
			
			for (int i = 0; i < listSubs.size(); i++) {
				List<String> spreader = new ArrayList<>();
				List<String> ignorant = new ArrayList<>();
				List<String> stifler = new ArrayList<>();

				List<String> spreader_temp = new ArrayList<>();

				Submission sub = listSubs.get(i);
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
							if (checkIfSpreader(spreaders_temp, user)) {
								if (!spreader.contains(user)) {
									spreader.add(user);
								}
							} else if (checkIfStifler(spreaders_temp, user)) {
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
			
			json = gson.toJson(new Spreader(spreaders, ignorants, stiflers));
			return json;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}

	public static void postCentrality(String keyword, String type, String json) {
		try {
			FileUtils.writeStringToFile(new File(PropertyUtils.getCommentPath() + keyword + "_" + type + "_cent.json"),
					json);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static String getCentrality(String keyword, String type) {
		try {
			return FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + keyword + "_" + type + "_cent.json"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}

	private static Boolean checkExist(List<Topic> topics, String str) {
		for (int i = 0; i < topics.size(); i++) {
			if (topics.get(i).getName().equals(str)) {
				return true;
			}
		}
		return false;
	}

	private static Boolean containKeywords(String topics, String[] words) {
		for (int i = 0; i < words.length; i++) {
			if (topics.contains(words[i])) {
				return true;
			}
		}
		return false;
	}

	public static String getSankey(String keyword, boolean local) {
		Gson gson = new Gson();
		String json;
		try {
			
			if (local == false)
			{
				json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + keyword + "_sankey.json"));
				return json;
			}
			
			json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyword + ".json"));
			MemesJSON memes = gson.fromJson(json, MemesJSON.class);
			List<Topic> nodes = new ArrayList<Topic>();
			List<UserLink> topicLinks = new ArrayList<UserLink>();
			int k = 0;
			for (int i = 0; i < memes.getSubmissions().get(0).getWikis().size(); i++) {
				nodes.add(new Topic(memes.getSubmissions().get(0).getWikis().get(i)));
			}
			while (k < memes.getSubmissions().size() - 1) {
				Submission sub = memes.getSubmissions().get(k);
				Submission nextSub = memes.getSubmissions().get(k + 1);
				for (int i = 0; i < sub.getWikis().size(); i++) {
					String curTopic = sub.getWikis().get(i);
					for (int j = 0; j < nextSub.getWikis().size(); j++) {
						String nextTopic = nextSub.getWikis().get(j);
						if (!nextTopic.equals(curTopic)) {
							if (nextTopic.length() > 3 && nodes.size() <= 20) {
								if (checkExist(nodes, nextTopic) && checkExist(nodes, curTopic)) {
									continue;
								}
								if (!checkExist(nodes, curTopic)) {
									nodes.add(new Topic(curTopic));
								}
								if (!checkExist(nodes, nextTopic)) {
									nodes.add(new Topic(nextTopic));
								}

								// add links
								Double sim = docDocComparison(nextTopic, curTopic);
								if (sim == 0.0) {
									sim = 0.0001;
								}
								UserLink link = new UserLink(curTopic, nextTopic, sim);
								topicLinks.add(link);
							}
						}
					}
				}
				k++;
			}

			MemesJSON m = new MemesJSON(nodes, topicLinks);
			json = gson.toJson(m);
			File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sankey.json");
			FileUtils.writeStringToFile(file3, json, false);
			return json;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}
	
	public static String getTwitterSankey(String keyword, boolean local)
	{
		Gson gson = new Gson();
		String json;
		try
		{
			if (local == false)
			{
				json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyword + "_sankey.json"));
				return json;
			}
			
			json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + keyword + "_processed.json"));
			Twitter twitter  = gson.fromJson(json, Twitter.class);
			
			List<UserLink> topicLinks = new ArrayList<UserLink>();
			List<Topic> nodes = new ArrayList<Topic>();
			
			for (int i = 0; i < twitter.getTweets().get(0).getTopics().size(); i++)
				nodes.add(new Topic(twitter.getTweets().get(0).getTopics().get(i)));
			
			int k = 0;
			while (k < twitter.getTweets().size() - 1)
			{
				Tweet tweet = twitter.getTweets().get(k);
				Tweet nextTweet = twitter.getTweets().get(k + 1);
				for (int i = 0; i < tweet.getTopics().size(); i++)
				{
					String curTopic = tweet.getTopics().get(i);
					for (int j = 0; j < nextTweet.getTopics().size(); j++)
					{
						String nextTopic = nextTweet.getTopics().get(j);
						if (nextTopic.length() > 3 && nodes.size() <= 20)
						{
							if (checkExist(nodes, nextTopic) && checkExist(nodes, curTopic))
								continue;
							
							if (!checkExist(nodes, curTopic))
								nodes.add(new Topic(curTopic));
							
							if (!checkExist(nodes, nextTopic))
								nodes.add(new Topic(nextTopic));
							
							// add links
							Double sim = docDocComparison(nextTopic, curTopic);
							if (sim == 0.0)
								sim = 0.0001;
							
							UserLink link = new UserLink(curTopic, nextTopic, sim);
							topicLinks.add(link);
						}
					}
				}
				k++;
			}
						
			MemesJSON m = new MemesJSON(nodes, topicLinks);
			json = gson.toJson(m);
			File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sankey.json");
			FileUtils.writeStringToFile(file3, json, false);
			return json;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}

	public static String getSankeyUser(String keyword, Integer users, boolean local) {
		Gson gson = new Gson();
		String json;
		try {
			
			if (local == false)
			{
				json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + keyword + "_sankey_user.json"));
				return json;
			}
			
			json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyword + ".json"));
			MemesJSON memes = gson.fromJson(json, MemesJSON.class);
			List<Topic> nodes = new ArrayList<Topic>();
			List<UserLink> topicLinks = new ArrayList<UserLink>();
			for (int i = 0; i < memes.getSubmissions().size(); i++) {
				for (int j = i + 1; j < memes.getSubmissions().size(); j++) {
					Submission sub = memes.getSubmissions().get(i);
					Submission nextSub = memes.getSubmissions().get(j);
					List<String> nextUsers = memes.getSubmissions().get(j).getUsers();
					List<String> curUsers = memes.getSubmissions().get(i).getUsers();
					int count = 0;
					for (int l = 0; l < curUsers.size(); l++) {
						if (nextUsers.contains(curUsers.get(l))) {
							count++;
						}
					}
					if (count >= users) {
						for (int m = 0; m < sub.getWikis().size(); m++) {
							String curTopic = sub.getWikis().get(m);
							for (int n = 0; n < nextSub.getWikis().size(); n++) {
								if (m != n) {
									String nextTopic = nextSub.getWikis().get(n);
									if (checkExist(nodes, curTopic) && checkExist(nodes, nextTopic)) {
										continue;
									}
									if (!nextTopic.equals(curTopic)) {
										// count commmon users
										if (!checkExist(nodes, curTopic)) {
											nodes.add(new Topic(curTopic));
										}
										if (!checkExist(nodes, nextTopic)) {
											nodes.add(new Topic(nextTopic));
										}
										UserLink link = new UserLink(curTopic, nextTopic, 1.0);
										topicLinks.add(link);
									}
								}
							}
						}

					}
				}
			}

			MemesJSON m = new MemesJSON(nodes, topicLinks);
			json = gson.toJson(m);
			File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sankey_user.json");
			FileUtils.writeStringToFile(file3, json, false);
			return json;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}
	
	public static String getSankeyUserTwitter(String keyword, Integer users)
	{
		Gson gson = new Gson();
		String json;
	
		try
		{
			json = FileUtils.readFileToString(new File(PropertyUtils.getRumourPath() + keyword + "_processed.json"));
			Twitter twitter = gson.fromJson(json, Twitter.class);
			
			List<Topic> nodes = new ArrayList<Topic>();
			List<UserLink> topicLinks = new ArrayList<UserLink>();
			
			for (int i = 0; i < twitter.getTweets().size(); i++)
			{
				for (int j = i + 1; j < twitter.getTweets().size(); j++)
				{
					Tweet tweet = twitter.getTweets().get(i);
					Tweet nextTweet = twitter.getTweets().get(j);
					
					List<String> nextUsers = twitter.getTweets().get(j).getMentioned_users();
					nextUsers.add(twitter.getTweets().get(j).getUser().getScreenName());
					
					List<String> curUsers = twitter.getTweets().get(i).getMentioned_users();
					if (!curUsers.contains(twitter.getTweets().get(i).getUser()))
						curUsers.add(twitter.getTweets().get(i).getUser().getScreenName());
					
					int count = 0;
					for (int l = 0; l < curUsers.size(); l++)
						if (nextUsers.contains(curUsers.get(l)))
							count++;
					
					if (count >= users) {
						for (int m = 0; m < tweet.getTopics().size(); m++) {
							String curTopic = tweet.getTopics().get(m);
							for (int n = 0; n < nextTweet.getTopics().size(); n++) {
								if (m != n) {
									if (nodes.size() > 20)
									{
										MemesJSON out = new MemesJSON(nodes, topicLinks);
										json = gson.toJson(out);
										File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sankey_user.json");
										FileUtils.writeStringToFile(file3, json, false);
										return json;
									}
										
									String nextTopic = nextTweet.getTopics().get(n);
									
									if (checkExist(nodes, curTopic) && checkExist(nodes, nextTopic)) continue;
									
									if (!nextTopic.equals(curTopic))
									{
										// count commmon users
										if (!checkExist(nodes, curTopic)) nodes.add(new Topic(curTopic));
										
										if (!checkExist(nodes, nextTopic)) nodes.add(new Topic(nextTopic));
										
										UserLink link = new UserLink(curTopic, nextTopic, 1.0);
										topicLinks.add(link);
									}
								}
							}
						}

					}
				}
			}
			
			MemesJSON m = new MemesJSON(nodes, topicLinks);
			json = gson.toJson(m);
			File file3 = new File(PropertyUtils.getCommentPath() + keyword + "_sankey_user.json");
			FileUtils.writeStringToFile(file3, json, false);
			return json;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}
	
	public static String getCollectedRumours() {
		String json;
		try {
			json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + "collectedRumours" + ".json"));
			return json;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "";
	}
	
	public static String getUserTypes(){
		Gson gson = new Gson();
		
		List<String> users = new ArrayList<>();
		File dir = new File("/Volumes/Data/Reddit/RedditAPI/data/comments/");
		List<String> others = new ArrayList<>();
		List<String> rejects = new ArrayList<>();
		List<String> approves = new ArrayList<>();
		List<String> jokes = new ArrayList<>();
		List<File> files = (List<File>) FileUtils.listFilesAndDirs(dir,TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		JsonArray arr = new JsonArray();
		
		for (File file: files){			
			String[] names;
			try{								
				if (file.getName().startsWith(".")){
					continue;
				}
				names = file.getName().split("_");
				String username = "";
				for (int i = 1;i < names.length-1;i++){
					 if (i == names.length-2){
						 username += names[i];
					 }else{
						 username += names[i] + "_";
					 }
					 
				}
				String category = names[names.length-1];
				if (category.contains("OTHER")){
					JsonObject obj = new JsonObject();
					obj.addProperty("username", username);
					obj.addProperty("category", "OTHER");
					arr.add(obj);
				}else if (category.contains("REJECT")){
					JsonObject obj = new JsonObject();
					obj.addProperty("username", username);
					obj.addProperty("category", "REJECT");
					arr.add(obj);
				}else if (category.contains("APPROVE")){
					JsonObject obj = new JsonObject();
					obj.addProperty("username", username);
					obj.addProperty("category", "APPROVE");
					arr.add(obj);
				}else if (category.contains("JOKE")){
					JsonObject obj = new JsonObject();
					obj.addProperty("username", username);
					obj.addProperty("category", "JOKE");
					arr.add(obj);
				}
			}catch(Exception ex){
			}
		}
		return gson.toJson(arr);
	}
}
