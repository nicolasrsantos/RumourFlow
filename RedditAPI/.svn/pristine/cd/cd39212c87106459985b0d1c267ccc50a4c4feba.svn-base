package examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import Utils.PropertyUtils;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.memes.MemesJSON;
import com.github.jreddit.memes.Nodes;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class ExtractUserComment {
	public static String getComments(String submissionID) {		
		String json;
		try {
			json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + submissionID + ".json"));
			return json;
		} catch (IOException e) {
			return "";
		}			
	}
		
	public static String getCommentsFromUser(String keyword, String user,String title) {
		String[] keys = keyword.split(",");
		List<String> listComments = new ArrayList<String>();
		Gson gson = new Gson();
		for (String key : keys) {			

			String json = "";
			
			try {
				json = FileUtils.readFileToString(
						new File(PropertyUtils.getRumourPath() + key.replaceAll("\"", "").trim() + ".json"));
				MemesJSON memes = gson.fromJson(json, MemesJSON.class);

				for (Submission s : memes.getSubmissions()) {
					if (s.getTitle().equalsIgnoreCase(title)){
						try {
							json = FileUtils.readFileToString(new File(PropertyUtils.getCommentPath() + s.getFullName() + ".json"));
							MemesJSON memes1 = gson.fromJson(json, MemesJSON.class);
							for (int i = 0;i < memes1.getNodes().size();i++){
								Nodes node = memes1.getNodes().get(i);
								if (node.getTitle().equalsIgnoreCase((user)))
									listComments.add(node.getLabel());
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}									
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}		
		return gson.toJson(listComments);
	}
	
	public static String getSentiments(String keyword) {
		Gson gson = new Gson();

		String json;
		try {
			json = FileUtils.readFileToString(new File(PropertyUtils
					.getRumourPath() + keyword + ".json"));
			MemesJSON memes = gson.fromJson(json, MemesJSON.class);
			List<Submission>listSubmissions = memes.getSubmissions();
			JsonArray arrCloud = new JsonArray();
			for (int i = 0;i < listSubmissions.size();i++){
				Submission sub = listSubmissions.get(i);				
				json = FileUtils.readFileToString(new File(PropertyUtils
						.getCommentPath() + sub.getFullName() + ".json"));
				MemesJSON comment = gson.fromJson(json, MemesJSON.class);
				int pos = 0, neg = 0;
				for (Nodes node : comment.getNodes()){
					if (node.getSentiment().trim().toLowerCase().equals("negative")){
						neg++;
					}else if (node.getSentiment().trim().toLowerCase().equals("positive")){
						pos++;
					}
				}
				
				JsonObject obj = new JsonObject();
				obj.addProperty("index", i);
				obj.addProperty("sent", (float)(pos-neg)/(pos+neg));
				arrCloud.add(obj);				
			}	
			return arrCloud.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return "";
	}
}

