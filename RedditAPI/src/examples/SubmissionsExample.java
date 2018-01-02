package examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.github.jreddit.entity.Comment;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.exception.RedditError;
import com.github.jreddit.exception.RetrievalFailedException;
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


public class SubmissionsExample {
	private static String GOOGLE_IMAGE="https://images.google.com/searchbyimage?site=search&image_url=";
	private static String searchMode = "";
	public static String google1TFolder = "";
	
	public static void main(String[] args) {
		
		// Initialize REST Client
	    RestClient restClient = new HttpRestClient();
	    restClient.setUserAgent("bot/1.0 by name");

		// Connect the user 
	    User user = new User(restClient, "hellovn", "111111");
		try {
			user.connect();
		} catch (IOException e1) {
			System.err.println("I/O Exception occured when attempting to connect user.");
			e1.printStackTrace();
			return;
		} catch (ParseException e1) {
			System.err.println("I/O Exception occured when attempting to connect user.");
			e1.printStackTrace();
			return;
		}
		String subreddit = "";
		String searchTerm = "";
		String stopWordFile = "";
		//get subreddit;
		for (int i=0;i<args.length;i++){
			if (args[i].equals("-topic")){
				searchTerm = args[i+1];
			}
			if (args[i].equals("-subreddit")){
				subreddit = args[i+1];
			}
			if (args[i].equals("-stop-word")){
				stopWordFile = args[i+1];
			}
			if (args[i].equals("-mode")){
				searchMode = args[i+1];
			}
			if (args[i].equals("-google")){
				  google1TFolder = args[i+1];
			  }
		}
		try {
			
			/***************************************************************************************************
			 * First: basic API functionality
			*/
			
			// Handle to Submissions, which offers the basic API functionality
			Submissions subms = new Submissions(restClient, user);
			
			// Retrieve submissions of a submission
//			System.out.println("\n============== Basic subreddit submissions ==============");
//			List<Submission> submissionsSubreddit = subms.ofSubreddit("all", SubmissionSort.TOP, -1, 1000, null, null, true);
//			printSubmissionsList(submissionsSubreddit);
			
//			// Search for submissions
//			System.out.println("\n============== Basic search submissions ==============");
//			List<Submission> submissionsSearch = subms.search("flowers", null, null, TimeSpan.MONTH, -1, 100, null, null, true);
//			printSubmissionsList(submissionsSearch);
//			
//			// Retrieve submissions of a user
//			System.out.println("\n============== Basic user submissions ==============");
//			List<Submission> submissionsUser = subms.ofUser("Unidan", UserSubmissionsCategory.SUBMITTED, UserOverviewSort.TOP, -1, 100, null, null, true);
//			printSubmissionsList(submissionsUser);
//			
//			
//			/***************************************************************************************************
//			 * Second: extended API functionality
//			*/
//			
//			// Handle to ExtendedSubmissions, which offers functionality beyond the Reddit API
			if (searchMode.equals("top")){
				ExtendedSubmissions extendedSubms = new ExtendedSubmissions(subms);
				
				System.out.println("\n============== Top search ==============");
				List<Submission> submissionsSubredditExtra = extendedSubms.ofSubreddit(subreddit, SubmissionSort.NONE, 50000, null);
				processSubmissions(restClient, user, submissionsSubredditExtra,subreddit, stopWordFile);	
			}else{
				// Search for submissions
				System.out.println("\n============== Extended search submissions retrieval ==============");
				ExtendedSubmissions extendedSubms = new ExtendedSubmissions(subms);
				List<Submission> submissionsSearchExtra = extendedSubms.searchTerm(searchTerm,  subreddit, SearchSort.RELEVANCE, TimeSpan.ALL, 1000,null);
				processSubmissions(restClient, user, submissionsSearchExtra, searchTerm + "_" + subreddit, stopWordFile);
			}
			
//			// Retrieve the top 532 submissions of query "valentine", user is not given (which is optional)
//			System.out.println("\n============== Extended search submissions retrieval ==============");
			//List<Submission> submissionsSearchExtra = extendedSubms.search("valentine", SearchSort.RELEVANCE, TimeSpan.ALL, 532);
//			printSubmissionsList(submissionsSearchExtra);
//			
//			// Retrieve the top 233 submissions of a user
//			System.out.println("\n============== Extended user submissions retrieval ==============");
//			List<Submission> submissionsUserExtra = extendedSubms.ofUser("Unidan", UserSubmissionsCategory.SUBMITTED, UserOverviewSort.HOT, 233);
//			printSubmissionsList(submissionsUserExtra);
			
		} catch (RetrievalFailedException e) {
			e.printStackTrace();
		} catch (RedditError e) {
			e.printStackTrace();
		}

	}
	
	public static void processSubmissions(RestClient restClient, User user, List<Submission> subs, String subreddit, String stopWordFile) {
		//get a list of stop word
		List<String> listStopWords = new ArrayList<String>();
		File stopword = new File(stopWordFile);
		 FileReader fr;
		try {
			fr = new FileReader(stopword);
			  BufferedReader br = new BufferedReader(fr);
		        String line;
		        while((line = br.readLine()) != null){
		        	listStopWords.add(line);
		        }
		        br.close();
		        fr.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*
		Summarizer sum = null;
		try {
			sum = new Summarizer(new File(google1TFolder + "/googleGramCorpus.uni"), new File(google1TFolder + "/googleGramCorpus.tri"), null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ProcessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
		*/
		for (Submission s : subs) {	
			//for each submission
			if (s.getCommentCount() > 1 && (s.getUpVotes() > 1 || s.getDownVotes() > 1)){
				try {
					
					File fileTitle = new File("./misinformation/titles/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit + ".txt");
					if (fileTitle.exists()){
						continue;
					}		
					//write the title to a file
					
					FileUtils.writeStringToFile(fileTitle, "");
					File fileComment = new File("./misinformation/comments/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit + ".txt");
					FileUtils.writeStringToFile(fileComment, "");
					File fileUrl = new File("./misinformation/urls/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit + ".txt");
					FileUtils.writeStringToFile(fileUrl, "");
					File fileImageUrl = new File("./misinformation/image_urls/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit  + ".txt");
					FileUtils.writeStringToFile(fileImageUrl, "");
					
					String titleString = s.getTitle();
					FileUtils.writeStringToFile(fileTitle, titleString.trim().replaceAll(" +"," ") + " " + s.getSelftext().trim().replaceAll(" +"," "),true);
					//if there is an url, write url content to a file.
										
					if (!s.getUrl().isEmpty() && s.getUrl().charAt(s.getUrl().length()-4) != '.'){
						if (!s.getUrl().contains("reddit")){
							File file = new File("./misinformation/urls/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit + ".txt");
							try{
								Document document = Jsoup.connect(s.getUrl()).get();
								Elements els = document.getElementsByTag("p");							
								System.out.print("wrinting image url");
								//String sumText = sum.summarize(removeStopWords(els.text(),listStopWords), 10);
								FileUtils.write(file, removeStopWords(els.text(),listStopWords),true);
							}catch (Exception ex){
								FileUtils.write(file, "",true);
							}
						}						
					}else{
						try{
							if (!s.getUrl().isEmpty() && s.getUrl().charAt(s.getUrl().length()-4) == '.'){
								WebDriver driver = new FirefoxDriver();
								driver.get(GOOGLE_IMAGE + s.getUrl());
								Thread.sleep(2000);
								 // Find the text input element by its name
						        List<WebElement> elements = driver.findElements(By.className("r"));
						        String url = "";
						        boolean isEmptyFile = true;
						        for (int i=0;i< elements.size();i++){
						        	WebElement el = elements.get(i);
						        	WebElement aEl = el.findElement(By.tagName("a"));
						        	url = aEl.getAttribute("href");
						        	
						        	if (!url.contains("reddit")){
						        		File file = new File("./misinformation/image_urls/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit + ".txt");
						        		try{      			
						        			Document imageUrl = Jsoup.connect(url).get();
											if (imageUrl.body().text().length() >= 200){
												System.out.print("wrinting image content");
												Elements imageEl = imageUrl.getElementsByTag("p");
												String imageString = imageEl.text();
												//String sumText = sum.summarize(removeStopWords(imageString,listStopWords), 1);
												try{
													FileUtils.write(file, removeStopWords(imageString,listStopWords),true);
													isEmptyFile = false;
												}catch (Exception ex){
													
												}
												
												break;
											}
						        		}catch(Exception ex){
						        			System.out.print(ex.getMessage());
						        		}
						        		
						        	}
						        }
			
						        if (isEmptyFile){
						        	System.out.print("wrinting empty image url");
						        	File file = new File("./misinformation/image_urls/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit + ".txt");
						        	FileUtils.write(file, "",true);
						        }

						        driver.quit();

							}else{
								System.out.print("wrinting empty url");
								File file = new File("./misinformation/urls/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit + ".txt");
								try{
									FileUtils.write(file, "",true);
								}catch (Exception ex){
									System.out.print(ex.getMessage());
								}
							}
							
						}catch(Exception ex){
							System.out.print(ex.getMessage());
						}
						
					}
					
					//get comments
					// Handle to Comments, which offers the basic API functionality
					Comments coms = new Comments(restClient, user);
					ExtendedComments extendedComs = new ExtendedComments(coms);
					
					System.out.println("\n============== Extended submission comments retrieval ==============");
					List<Comment> commentsSubmissionExtra = extendedComs.ofSubmission(s.getIdentifier(), CommentSort.TOP, -1, null);
					System.out.print("wrinting comment content");
					File file = new File("./misinformation/comments/" + subreddit + "/" + s.getIdentifier() + "_" + subreddit + ".txt");
					Comments.printCommentTree(commentsSubmissionExtra, file, listStopWords);
					//sumarize comments
					String content = IOUtils.toString(new FileReader(file.getAbsoluteFile()));
					//String sumText = sum.summarize(removeStopWords(content,listStopWords), 1);
					FileUtils.writeStringToFile(file, removeStopWords(content,listStopWords), false);
					
				} catch (Exception e) {
					System.out.print(e.getMessage());
				}
			}			
			break;
		}
	}
	private static String removeStopWords(String text, List<String> stopWords){
		for (int i=0;i<stopWords.size();i++){
			text = text.replace(" " + stopWords.get(i) + " ", " ");
		}
		return text.trim().replaceAll(" +"," ");
	}
}
