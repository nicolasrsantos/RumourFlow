package com.bigdata;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import examples.ExtractRumours;
import examples.ExtractSubmissionByKeyWord;
import examples.ExtractUserComment;
import examples.UserFeedback;


@Path("/RedditData")
public class RedditWebService {
	public RedditWebService()
	{
		
	}
	@GET @Path("search/{mode}/{keyword}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String searchByKeyWord(@PathParam("mode") String mode, @PathParam("keyword") String keyword)
	{
		return ExtractSubmissionByKeyWord.getSubmissions(mode, keyword);
	}
	

	@GET @Path("search/central/{keyword}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String searchCentral(@PathParam("keyword") String keyword)
	{
		return ExtractSubmissionByKeyWord.getCentralUsers(keyword);
	}
	
	@GET @Path("search/users/{keyword}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String searchUsers(@PathParam("keyword") String keyword)
	{
		return ExtractSubmissionByKeyWord.getRumourUsers(keyword);
	}
	
	@POST @Path("post/users/{keyword}/{type}")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public Response postCentrality(@PathParam("keyword") String keyword,@PathParam("type") String type,String json)
	{
		ExtractSubmissionByKeyWord.postCentrality(keyword, type, json);
		return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.allow("OPTIONS").build();
	}
	
	@GET @Path("get/users/{keyword}/{type}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String getCentrality(@PathParam("keyword") String keyword,@PathParam("type") String type)
	{
		return ExtractSubmissionByKeyWord.getCentrality(keyword, type);
	}
	
	@GET @Path("get/users/comments/{keyword}/{username}/{title}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String getUserComment(@PathParam("keyword") String keyword,@PathParam("username") String user,@PathParam("title") String title)
	{
		return ExtractUserComment.getCommentsFromUser(keyword, user, title);
	}
	
	@GET @Path("search/sankey/{keyword}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String searchSankey(@PathParam("keyword") String keyword)
	{
		return ExtractSubmissionByKeyWord.getSankey(keyword);
	}
	
	@GET @Path("search/sankey/user/{keyword}/{users}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String searchSankeyUser(@PathParam("keyword") String keyword, @PathParam("users") Integer users)
	{
		return ExtractSubmissionByKeyWord.getSankeyUser(keyword,users);
	}


	@GET @Path("rumour/keyword")
	@Produces({ MediaType.APPLICATION_JSON})
	public String getCollectedRumours()
	{
		return ExtractSubmissionByKeyWord.getCollectedRumours();
	}
	
	@GET @Path("search/rumour/sentiment/{keyword}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String getSentiment(@PathParam("keyword")String keyword)
	{
		return ExtractUserComment.getSentiments(keyword);
	}
	
	@GET @Path("search/submission/{submissionID}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String searchBySubmissionID(@PathParam("submissionID") String submissionID)
	{
		return ExtractUserComment.getComments(submissionID);
	}
	
	
	@GET @Path("search/cloud/{keyword}/{redditID}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String getCloud(@PathParam("keyword") String keyword, @PathParam("redditID") String redditID)
	{
		return ExtractSubmissionByKeyWord.getWordClouds(keyword, redditID);
	}
	
	@GET @Path("search/tcloud/{keyword}")
	@Produces({ MediaType.APPLICATION_JSON})
	public String getTopicCloud(@PathParam("keyword") String keyword)
	{
		return ExtractSubmissionByKeyWord.getTopicClouds(keyword);
	}
	
	
	@GET @Path("search/category/users")
	@Produces({ MediaType.APPLICATION_JSON})
	public String getUserSpreading()
	{
		return ExtractSubmissionByKeyWord.getUserTypes().toString();
	}
	
	@GET @Path("search/snopes")
	@Produces({ MediaType.APPLICATION_JSON})
	public String getSnopes()
	{
		return ExtractRumours.getRumours();
	}	
	
	@GET @Path("update/user/feedback/{redditID}/{title}/{comment}/{image}/{url}")
	@Produces({ MediaType.APPLICATION_JSON})
	public Response updateUserFeedbackValues(@PathParam("redditID") String redditID, @PathParam("title") String title, @PathParam("comment") String comment, @PathParam("image") String image, @PathParam("url") String url)
	{
		UserFeedback.updateUserFeedbackValues(redditID, title, comment, image, url);
		return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.allow("OPTIONS").build();
	}
}
