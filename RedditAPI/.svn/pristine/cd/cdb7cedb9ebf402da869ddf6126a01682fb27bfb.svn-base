package examples;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class UserFeedback {
	public static void updateUserFeedbackValues(String redditID, String title, String comment, String image, String url){
		File file = new File("/Library/WebServer/Documents/memes/vis_data/userfeedbacks.txt");
		try {
			String separator = System.getProperty( "line.separator" );
			FileUtils.writeStringToFile(file, redditID + " " + title + " " + comment + " " + image + " " + url + separator, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
