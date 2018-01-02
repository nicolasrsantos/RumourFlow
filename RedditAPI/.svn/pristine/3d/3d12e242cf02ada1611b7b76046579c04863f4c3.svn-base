package examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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
import com.github.jreddit.retrieval.params.TimeSpan;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Utils.PropertyUtils;

public class RumourSpread {
	private static SinglethreadTextRtProcessor sim = null;
	private static MultithreadTextrtPreprocessor preprocessor = null;
	public static List<Submission> listSubmissions = new ArrayList<Submission>();
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

	public static Boolean LOCAL_DATA = false;
	public static Double THRESHOLD_VALUE = 0.0;

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

	public static String getSubmissions(String keyWord) {
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
				return gson.toJson(memes);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
							SearchSort.RELEVANCE, TimeSpan.ALL, MAX_SUBMISSIONS * 2, null);
					return processSubmissions(restClient, user, submissionsSearchExtra, keyWord, stopWordFile);
				}

		} catch (RetrievalFailedException e) {
			e.printStackTrace();
		} catch (RedditError e) {
			e.printStackTrace();
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
					
					json = FileUtils.readFileToString(
							new File(PropertyUtils.getRumourPath() + key.replaceAll("\"", "") + ".json"));
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

	public static String processSubmissions(RestClient restClient, User user, List<Submission> subs,
			String subreddit, String stopWordFile) {
		// reset variables
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
			List<String> users = new ArrayList<String>();
			if (s.getCommentCount() > 0 && (s.getUpVotes() > 0 || s.getDownVotes() > 0)) {
				currentIndex++;				
				try {
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

	private static boolean checkIfSpreader(List<List<String>> spreaders, String user) {
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
	private static boolean checkIfStifler(List<List<String>> spreaders, String user) {
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
			return FileUtils
					.readFileToString(new File(PropertyUtils.getCommentPath() + keyword + "_" + type + "_cent.json"));
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

	public static String getSankey(String keyword) {
		Gson gson = new Gson();
		String json;
		try {
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

	public static String getSankeyUser(String keyword, Integer users) {
		Gson gson = new Gson();
		String json;
		try {
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
}
