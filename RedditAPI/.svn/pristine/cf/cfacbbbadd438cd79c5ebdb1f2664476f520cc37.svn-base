package com.github.jreddit.retrieval;

import static com.github.jreddit.utils.restclient.JsonUtils.safeJsonToString;
import static examples.ExtractSubmissionByKeyWord.index;
import static examples.ExtractSubmissionByKeyWord.centIndex;
import static examples.ExtractSubmissionByKeyWord.listCommentLinks;
import static examples.ExtractSubmissionByKeyWord.listCommentNodes;
import static examples.ExtractSubmissionByKeyWord.listCentLinks;
import static examples.ExtractSubmissionByKeyWord.listCentNodes;
import static examples.ExtractSubmissionByKeyWord.listUsers;
import static examples.ExtractSubmissionByKeyWord.mapUsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sentiment.SentimentString;

import com.github.jreddit.entity.Comment;
import com.github.jreddit.entity.Kind;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.exception.RedditError;
import com.github.jreddit.exception.RetrievalFailedException;
import com.github.jreddit.memes.Link;
import com.github.jreddit.memes.Nodes;
import com.github.jreddit.memes.UserLink;
import com.github.jreddit.retrieval.params.CommentSort;
import com.github.jreddit.retrieval.params.TimeSpan;
import com.github.jreddit.retrieval.params.UserOverviewSort;
import com.github.jreddit.utils.ApiEndpointUtils;
import com.github.jreddit.utils.ParamFormatter;
import com.github.jreddit.utils.restclient.RestClient;

import examples.ExtractSubmissionByKeyWord;

/**
 * This class offers the following functionality: 1) Parsing the results of a
 * request into Comment objects (see <code>Comments.parseBreadth()</code> and
 * <code>Comments.parseDepth()</code>). 2) The ability to get comments of a user
 * (see <code>Commments.ofUser()</code>). 3) The ability to get comments of a
 * submission/article (see <code>Comments.ofSubmission()</code>).
 * 
 * @author Raul Rene Lepsa
 * @author Simon Kassing
 */
public class Comments implements ActorDriven {
	private static String[] colors  = {"green","lightblue","grey", "yellow", "brown", "purple", "black","pink","lightblue","darkblue"};
	private RestClient restClient;
	private User user;

	/**
	 * Constructor. Global default user (null) is used.
	 * 
	 * @param restClient
	 *            REST Client instance
	 */
	public Comments(RestClient restClient) {
		this.restClient = restClient;
		this.user = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param restClient
	 *            REST Client instance
	 * @param actor
	 *            User instance
	 */
	public Comments(RestClient restClient, User actor) {
		this.restClient = restClient;
		this.user = actor;
	}

	/**
	 * Switch the current user for the new user who will be used when invoking
	 * retrieval requests.
	 * 
	 * @param new_actor
	 *            New user
	 */
	public void switchActor(User new_actor) {
		this.user = new_actor;
	}

	/**
	 * Parses a JSON feed of comments from Reddit (URL) into a nice list of
	 * Comment objects maintaining the order. This parses ONLY the first depth
	 * of comments. Only call this function to parse shallow comment listings
	 * (e.g. of the user overview).
	 * 
	 * @param url
	 *            URL for the request
	 * 
	 * @return Parsed list of comments.
	 */
	public List<Comment> parseBreadth(String url)
			throws RetrievalFailedException, RedditError {

		// Determine cookie
		String cookie = (user == null) ? null : user.getCookie();

		// List of submissions
		List<Comment> comments = new LinkedList<Comment>();

		// Send request to reddit server via REST client
		Object response = restClient.get(url, cookie).getResponseObject();

		if (response instanceof JSONObject) {

			JSONObject object = (JSONObject) response;
			if (object.get("error") != null) {
				throw new RedditError("Comments response contained error code "
						+ object.get("error") + ".");
			}

			JSONArray array = (JSONArray) ((JSONObject) object.get("data"))
					.get("children");

			// Iterate over the submission results
			JSONObject data;
			Comment comment;
			for (Object anArray : array) {
				data = (JSONObject) anArray;

				// Make sure it is of the correct kind
				String kind = safeJsonToString(data.get("kind"));
				if (kind.equals(Kind.COMMENT.value())) {

					// Contents of the comment
					data = ((JSONObject) data.get("data"));

					// Create and add the new comment
					comment = new Comment(data);
					comments.add(comment);

				}

			}

		} else {
			throw new IllegalArgumentException(
					"Parsing failed because JSON is not from a shallow comment listing.");
		}

		// Finally return list of submissions
		return comments;

	}

	/**
	 * Parses a JSON feed of comments from Reddit (URL) into a nice list of
	 * Comment objects maintaining the order. This parses all comments that are
	 * defined with their associated values, those that fall outside the
	 * (default) limit are omitted.
	 * 
	 * @param user
	 *            User
	 * @param url
	 *            URL for the request
	 * 
	 * @return Parsed list of comments.
	 */
	public List<Comment> parseDepth(String url)
			throws RetrievalFailedException, RedditError {

		// Determine cookie
		String cookie = (user == null) ? null : user.getCookie();

		// List of submissions
		List<Comment> comments = new LinkedList<Comment>();

		// Send request to reddit server via REST client
		Object response = restClient.get(url, cookie).getResponseObject();

		if (response instanceof JSONArray) {

			JSONObject object = (JSONObject) ((JSONArray) response).get(1);
			parseRecursive(comments, object);

		} else {
			throw new IllegalArgumentException(
					"Parsing failed because JSON input is not from a submission.");
		}

		return comments;

	}

	/**
	 * Parse a JSON object consisting of comments and add them to the already
	 * existing list of comments. This does NOT create a new comment list.
	 * 
	 * @param comments
	 *            List of comments
	 * @param object
	 *            JSON Object
	 */
	protected void parseRecursive(List<Comment> comments, JSONObject object)
			throws RetrievalFailedException, RedditError {
		assert comments != null : "List of comments must be instantiated.";
		assert object != null : "JSON Object must be instantiated.";

		// Get the comments in an array
		JSONArray array = (JSONArray) ((JSONObject) object.get("data"))
				.get("children");

		// Iterate over the submission results
		JSONObject data;
		Comment comment;
		for (Object anArray : array) {
			data = (JSONObject) anArray;

			// Make sure it is of the correct kind
			String kind = safeJsonToString(data.get("kind"));
			if (kind.equals(Kind.COMMENT.value())) {

				// Contents of the comment
				data = ((JSONObject) data.get("data"));

				// Create and add the new comment
				comment = new Comment(data);
				comments.add(comment);

				Object o = data.get("replies");
				if (o instanceof JSONObject) {

					// Dig towards the replies
					JSONObject replies = (JSONObject) o;
					parseRecursive(comment.getReplies(), replies);

				}

			} else if (kind.equals(Kind.MORE.value())) {

				// data = (JSONObject) data.get("data");
				// JSONArray children = (JSONArray) data.get("children");
				// System.out.println("\t+ More children: " + children);

			}

		}

	}

	/**
	 * Get the comment tree of the given user. In this variant all parameters
	 * are Strings.
	 *
	 * @param username
	 *            Username of the user you want to retrieve from.
	 * @param sort
	 *            (Optional, set null if not used) Sorting method.
	 * @param time
	 *            (Optional, set null is not used) Time window
	 * @param count
	 *            (Optional, set null if not used) Number at which the counter
	 *            starts
	 * @param limit
	 *            (Optional, set null if not used) Integer representing the
	 *            maximum number of comments to return
	 * @param after
	 *            (Optional, set null if not used) After which comment to
	 *            retrieve
	 * @param before
	 *            (Optional, set null if not used) Before which comment to
	 *            retrieve
	 * @param show
	 *            (Optional, set null if not used) Show parameter ('given' is
	 *            only acceptable value)
	 * 
	 * @return Comments of a user.
	 */
	public List<Comment> ofUser(String username, String sort, String time,
			String count, String limit, String after, String before, String show)
			throws RetrievalFailedException, RedditError {

		// Format parameters
		String params = "";
		params = ParamFormatter.addParameter(params, "sort", sort);
		params = ParamFormatter.addParameter(params, "time", time);
		params = ParamFormatter.addParameter(params, "count", count);
		params = ParamFormatter.addParameter(params, "limit", limit);
		params = ParamFormatter.addParameter(params, "after", after);
		params = ParamFormatter.addParameter(params, "before", before);
		params = ParamFormatter.addParameter(params, "show", show);

		// Retrieve submissions from the given URL
		return parseBreadth(String.format(ApiEndpointUtils.USER_COMMENTS,
				username, params));

	}

	/**
	 * Get the comment tree of the given user (username).
	 *
	 * @param username
	 *            Username of the user you want to retrieve from.
	 * @param sort
	 *            (Optional, set null if not used) Sorting method.
	 * @param time
	 *            (Optional, set null is not used) Time window
	 * @param count
	 *            (Optional, set -1 if not used) Number at which the counter
	 *            starts
	 * @param limit
	 *            (Optional, set -1 if not used) Integer representing the
	 *            maximum number of comments to return
	 * @param after
	 *            (Optional, set null if not used) After which comment to
	 *            retrieve
	 * @param before
	 *            (Optional, set null if not used) Before which comment to
	 *            retrieve
	 * @param show_given
	 *            (Optional, set false if not used) Only show the given comments
	 * 
	 * @return Comments of a user.
	 */
	public List<Comment> ofUser(String username, UserOverviewSort sort,
			TimeSpan time, int count, int limit, Comment after, Comment before,
			boolean show_given) throws RetrievalFailedException, RedditError {

		if (username == null || username.isEmpty()) {
			throw new IllegalArgumentException("The username must be set.");
		}

		return ofUser(username, (sort != null) ? sort.value() : null,
				(time != null) ? time.value() : null, String.valueOf(count),
				String.valueOf(limit), (after != null) ? after.getFullName()
						: null, (before != null) ? before.getFullName() : null,
				(show_given) ? "given" : null);

	}

	/**
	 * Get the comment tree of the given user (object).
	 *
	 * @param target
	 *            User you want to retrieve from.
	 * @param sort
	 *            (Optional, set null if not used) Sorting method.
	 * @param time
	 *            (Optional, set null is not used) Time window
	 * @param count
	 *            (Optional, set -1 if not used) Number at which the counter
	 *            starts
	 * @param limit
	 *            (Optional, set -1 if not used) Integer representing the
	 *            maximum number of comments to return
	 * @param after
	 *            (Optional, set null if not used) After which comment to
	 *            retrieve
	 * @param before
	 *            (Optional, set null if not used) Before which comment to
	 *            retrieve
	 * @param show_given
	 *            (Optional, set false if not used) Only show the given comments
	 * 
	 * @return Comments of a user.
	 */
	public List<Comment> ofUser(User target, UserOverviewSort sort,
			TimeSpan time, int count, int limit, Comment after, Comment before,
			boolean show_given) throws RetrievalFailedException, RedditError {

		if (target == null) {
			throw new IllegalArgumentException("The user targeted must be set.");
		}

		return ofUser(target.getUsername(), sort, time, count, limit, after,
				before, show_given);

	}

	/**
	 * Get the comment tree from a given submission. In this variant all
	 * parameters are Strings.
	 *
	 * @param submissionId
	 *            Submission ID36 identifier
	 * @param commentId
	 *            (Optional, set null if not used) ID of a comment. If
	 *            specified, this comment will be the focal point of the
	 *            returned view.
	 * @param parentsShown
	 *            (Optional, set null is not used) An integer between 0 and 8
	 *            representing the number of parents shown for the comment
	 *            identified by <code>commentId</code>
	 * @param depth
	 *            (Optional, set null if not used) Integer representing the
	 *            maximum depth of subtrees in the thread
	 * @param limit
	 *            (Optional, set null if not used) Integer representing the
	 *            maximum number of comments to return
	 * @param sort
	 *            (Optional, set null if not used) CommentSort enum indicating
	 *            the type of sorting to be applied (e.g. HOT, NEW, TOP, etc)
	 * @return Comments for an article.
	 */
	public List<Comment> ofSubmission(String submissionId, String commentId,
			String parentsShown, String depth, String limit, String sort) {

		// Format parameters
		String params = "";
		params = ParamFormatter.addParameter(params, "comment", commentId);
		params = ParamFormatter.addParameter(params, "context", parentsShown);
		params = ParamFormatter.addParameter(params, "depth", depth);
		params = ParamFormatter.addParameter(params, "limit", limit);
		params = ParamFormatter.addParameter(params, "sort", sort);

		// Retrieve submissions from the given URL
		return parseDepth(String.format(ApiEndpointUtils.SUBMISSION_COMMENTS,
				submissionId, params));

	}

	/**
	 * Get the comment tree from a given submission (ID36)
	 *
	 * @param submissionId
	 *            Submission ID36 identifier
	 * @param commentId
	 *            (Optional, set null if not used) ID of a comment. If
	 *            specified, this comment will be the focal point of the
	 *            returned view.
	 * @param parentsShown
	 *            (Optional, set -1 is not used) An integer between 0 and 8
	 *            representing the number of parents shown for the comment
	 *            identified by <code>commentId</code>
	 * @param depth
	 *            (Optional, set -1 if not used) Integer representing the
	 *            maximum depth of subtrees in the thread
	 * @param limit
	 *            (Optional, set -1 if not used) Integer representing the
	 *            maximum number of comments to return
	 * @param sort
	 *            (Optional, set null if not used) CommentSort enum indicating
	 *            the type of sorting to be applied (e.g. HOT, NEW, TOP, etc)
	 * @return Comments for an article.
	 */
	public List<Comment> ofSubmission(String submissionId, String commentId,
			int parentsShown, int depth, int limit, CommentSort sort)
			throws RetrievalFailedException, IllegalArgumentException {

		if (submissionId == null || submissionId.isEmpty()) {
			throw new IllegalArgumentException(
					"The identifier of the submission must be set.");
		}

		if (depth < -1 || depth > 8) {
			throw new IllegalArgumentException(
					"The depth must be between 1 and 8 (or for default -1).");
		}

		return ofSubmission(submissionId, commentId,
				String.valueOf(parentsShown), String.valueOf(depth),
				String.valueOf(limit), sort.value());

	}

	/**
	 * Get the comment tree from a given submission (object).
	 *
	 * @param submission
	 *            Submission object
	 * @param commentId
	 *            (Optional, set null if not used) ID of a comment. If
	 *            specified, this comment will be the focal point of the
	 *            returned view.
	 * @param parentsShown
	 *            (Optional, set -1 is not used) An integer between 0 and 8
	 *            representing the number of parents shown for the comment
	 *            identified by <code>commentId</code>
	 * @param depth
	 *            (Optional, set -1 if not used) Integer representing the
	 *            maximum depth of subtrees in the thread
	 * @param limit
	 *            (Optional, set -1 if not used) Integer representing the
	 *            maximum number of comments to return
	 * @param sort
	 *            (Optional, set null if not used) CommentSort enum indicating
	 *            the type of sorting to be applied (e.g. HOT, NEW, TOP, etc)
	 * @return Comments for an article.
	 */
	public List<Comment> ofSubmission(Submission submission, String commentId,
			int parentsShown, int depth, int limit, CommentSort sort)
			throws RetrievalFailedException, RedditError {

		if (submission == null) {
			throw new IllegalArgumentException(
					"The submission must be defined.");
		}

		return ofSubmission(submission.getIdentifier(), commentId,
				parentsShown, depth, limit, sort);
	}

	/**
	 * Flatten the comment tree. The original comment tree is not overwritten.
	 * 
	 * @param cs
	 *            List of comments that you get returned from one of the other
	 *            methods here
	 * @param target
	 *            List in which to place the flattend comment tree.
	 */
	public static void flattenCommentTree(List<Comment> cs, List<Comment> target) {
		for (Comment c : cs) {
			target.add(c);
			flattenCommentTree(c.getReplies(), target);
		}
	}

	/**
	 * Print the given comment tree.
	 * 
	 * @param cs
	 *            List of comments that you get returned from one of the other
	 *            methods here
	 */
	public static void printCommentTree(List<Comment> cs, File file) {
		for (Comment c : cs) {
			printCommentTree(c, 0, file);
		}
	}

	public static void printCommentJson(Submission s, List<Comment> cs,
			List<String> stopWords, Nodes parentNode,List<String> users) throws IOException {
		for (Comment c : cs) {
			if (c.getAuthor().equals("[deleted]")){
				continue;
			}
			
			//user comment
			if (!mapUsers.containsKey(c.getAuthor().toLowerCase())){
				List<String> arrComments = new ArrayList<>();
				arrComments.add(c.getBody());
				mapUsers.put(c.getAuthor().toLowerCase(), arrComments);
			}else{
				List<String> arrComments = mapUsers.get(c.getAuthor().toLowerCase());
				arrComments.add(c.getBody());
				mapUsers.put(c.getAuthor().toLowerCase(), arrComments);
			}
		
			//semantic score
			Double sim = 0.0;		

			sim = 1 - ExtractSubmissionByKeyWord.docDocComparison(ExtractSubmissionByKeyWord.removeStopWords(c.getBody(), stopWords), ExtractSubmissionByKeyWord.removeStopWords(s.getTitle(), stopWords));
			if (Double.isNaN(sim)){
				sim = 0.0001;
			}
			
			//sentiment score
			String sen = SentimentString.getSentimentString(c.getBody());
			List<Integer> ints = new ArrayList<Integer>();
			ints.add(parentNode.getIndex());
			Nodes subNode = new Nodes(index, ints, 5.0, 5,
					c.getAuthor().toLowerCase(), c.getBody(), index,"blue",c.getFullName(),"true", parentNode, c.getCreated(), sim, sen);
		
			if (!listUsers.contains(c.getAuthor())){
				Nodes centNode = new Nodes(centIndex, ints, 5.0, 5,
						c.getAuthor().toLowerCase(), c.getBody(), centIndex++,"blue",c.getFullName(),"true", parentNode, c.getCreated(), sim, sen);
				
				listUsers.add(c.getAuthor().toLowerCase());
				listCentNodes.add(centNode);				
			}
			users.add(c.getAuthor().toLowerCase());
			UserLink uLink = new UserLink(c.getAuthor().toLowerCase(), s.getAuthor().toLowerCase(), sim);
			listCentLinks.add(uLink);
			
			listCommentNodes.add(subNode);			
			Link link1 = new Link(index++, parentNode.getIndex(), sim);
			listCommentLinks.add(link1);			
			printCommentTreeJson(s, c, 0, stopWords, subNode, users);		
		}
		
	}
	
	private static void printCommentTreeJson(Submission s, Comment c, int level,
			List<String> stopWords, Nodes parentNode, List<String> users) throws IOException {
		System.out.println(c);
		for (Comment child : c.getReplies()) {
			if (child.getAuthor().equals("[deleted]")){
				continue;
			}
						
			//user comment
			if (!mapUsers.containsKey(child.getAuthor().toLowerCase())){
				List<String> arrComments = new ArrayList<>();
				arrComments.add(child.getBody());
				mapUsers.put(child.getAuthor().toLowerCase(), arrComments);
			}else{
				List<String> arrComments = mapUsers.get(child.getAuthor().toLowerCase());
				arrComments.add(child.getBody());
				mapUsers.put(child.getAuthor().toLowerCase(), arrComments);
			}
			
			Double sim = 0.0;
			sim = 1 - ExtractSubmissionByKeyWord.docDocComparison(ExtractSubmissionByKeyWord.removeStopWords(child.getBody(), stopWords), ExtractSubmissionByKeyWord.removeStopWords(c.getBody(), stopWords));
			if (Double.isNaN(sim)){
				sim = 0.00001;
			}

			//sentiment score
			String sen = SentimentString.getSentimentString(c.getBody());
			
			List<Integer> ints = new ArrayList<Integer>();
			ints.add(parentNode.getIndex());
			
			Nodes node1 = new Nodes(index, ints, 5.0, 5,
					child.getAuthor().toLowerCase(), child.getBody(),
					index,colors[level],child.getFullName(),"true", parentNode, child.getCreated(), sim,sen);
			
			if (!listUsers.contains(c.getAuthor().toLowerCase())){
				listUsers.add(c.getAuthor().toLowerCase());
				listCentNodes.add(node1);				
			}
			users.add(child.getAuthor());
			UserLink uLink = new UserLink(child.getAuthor().toLowerCase(), c.getAuthor().toLowerCase(), sim);
			listCentLinks.add(uLink);
			
			listCommentNodes.add(node1);
			Link link1 = new Link(index++, parentNode.getIndex(), sim);
			listCommentLinks.add(link1);
			printCommentTreeJson(s,child,level + 1, node1, users,stopWords);

		
		}
	}
	
	private static void printCommentTreeJson(Submission s, Comment c, int level, Nodes parentNode, List<String> users, List<String> stopWords) {
	
		for (Comment child : c.getReplies()) {
			if (child.getAuthor().equals("[deleted]")){
				continue;
			}
			
			//user comment
			if (!mapUsers.containsKey(child.getAuthor().toLowerCase())){
				List<String> arrComments = new ArrayList<>();
				arrComments.add(child.getBody());
				mapUsers.put(child.getAuthor().toLowerCase(), arrComments);
			}else{
				List<String> arrComments = mapUsers.get(child.getAuthor().toLowerCase());
				arrComments.add(child.getBody());
				mapUsers.put(child.getAuthor().toLowerCase(), arrComments);
			}
			
			System.out.println(child);
			Double sim = 0.0;
			Link link1 = null;
			try {
				sim = 1 - ExtractSubmissionByKeyWord.docDocComparison(ExtractSubmissionByKeyWord.removeStopWords(child.getBody(), stopWords), ExtractSubmissionByKeyWord.removeStopWords(c.getBody(), stopWords));

				if (Double.isNaN(sim)){
					sim = 0.0001;
				}
				
				//sentiment score
				String sen = SentimentString.getSentimentString(c.getBody());
				
				List<Integer> ints = new ArrayList<Integer>();
				ints.add(parentNode.getIndex());
				
				Nodes node1 = new Nodes(index, ints, 5.0, 5,
						child.getAuthor(), child.getBody(),
						index,colors[level], child.getFullName(),"true", parentNode, c.getCreated(), sim, sen);
				
				if (!listUsers.contains(c.getAuthor().toLowerCase())){
					listUsers.add(c.getAuthor().toLowerCase());
					listCentNodes.add(node1);					
				}				
				users.add(child.getAuthor().toLowerCase());
				UserLink uLink = new UserLink(child.getAuthor().toLowerCase(), c.getAuthor().toLowerCase(), sim);
				listCentLinks.add(uLink);
				
				listCommentNodes.add(node1);
				link1 = new Link(index++, parentNode.getIndex(), sim);
				listCommentLinks.add(link1);
				printCommentTreeJson(s, child, level + 1, node1, users, stopWords);
			
				
			} catch (Exception e) {
				sim = 0.0;
			}
						
		}
	}

	
	public static void printCommentTree(List<Comment> cs, File file,
			List<String> listStopWords) {
		for (Comment c : cs) {
			printCommentTree(c, 0, file, listStopWords);
		}
	}

	public static void printCommentTree(List<Comment> cs) {
		for (Comment c : cs) {
			printCommentTree(c, 0);
		}
	}

	/**
	 * Print the comment at a specific level. Recursive function.
	 * 
	 * @param c
	 *            Comment
	 * @param level
	 *            Level to place at
	 */
	private static void printCommentTree(Comment c, int level) {
		for (int i = 0; i < level; i++) {
			System.out.print("\t");
		}
		System.out.println(c);
		for (Comment child : c.getReplies()) {
			printCommentTree(child, level + 1);
		}
	}

	private static void printCommentTree(Comment c, int level, File file) {
		for (int i = 0; i < level; i++) {
			// System.out.print("\t");
			try {
				FileUtils.write(file, "\t", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileUtils.write(file, c.getBody(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(c);
		for (Comment child : c.getReplies()) {
			printCommentTree(child, level + 1);
		}
	}

	private static String removeStopWords(String text, List<String> stopWords) {
		for (int i = 0; i < stopWords.size(); i++) {
			text = text.replace(" " + stopWords.get(i) + " ", " ");
		}
		return text.trim().replaceAll(" +", " ");
	}

	private static void printCommentTree(Comment c, int level, File file,
			List<String> stopWords) {
		for (int i = 0; i < level; i++) {
			// System.out.print("\t");
			try {
				FileUtils.write(file, "\t", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String comment = removeStopWords(c.getBody(), stopWords);
			if (comment.length() >= 10 && c.getScore() > 1)
				FileUtils.write(file, comment.replaceAll("[^A-Za-z0-9 ]", ""),
						true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(c);
		for (Comment child : c.getReplies()) {
			printCommentTree(child, level + 1);
		}
	}		
}
