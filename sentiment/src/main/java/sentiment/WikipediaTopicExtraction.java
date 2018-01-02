package sentiment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WikipediaTopicExtraction {
	public static void main(String[] args){
		List<String> wikipediaTopics = new ArrayList<String>();
		wikipediaTopics = getWikis("hello world from obama and trump");
		System.out.println(wikipediaTopics);
	}
	public static List<String> getWikis(String text){
		List<String> wikis = new ArrayList<String>();
		try {
			URL urlRest = new URL("http://localhost:8080/dexter-webapp/api/rest/annotate?");
			String query = "text=" + text.replaceAll("[^ A-Za-z]+", "")
					+ "&n=50&wn=false&debug=false&format=text&min-conf=0.5";

			// make connection
			URLConnection urlc = urlRest.openConnection();

			// use post mode
			urlc.setDoOutput(true);
			urlc.setAllowUserInteraction(false);

			// send query
			PrintStream ps;
			ps = new PrintStream(urlc.getOutputStream());
			ps.print(query);
			ps.close();

			// get result
			BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
			String l = null;

			while ((l = br.readLine()) != null) {
				// System.out.println(l);
				// get document
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(l);
				JSONObject jsonObject = (JSONObject) obj;
				JSONArray msg = (JSONArray) jsonObject.get("spots");

				for (Object obje : msg) {
					JSONObject jObj = (JSONObject) obje;
					// increase entities with Wikipedia linking
					String entity = jObj.get("mention").toString().toLowerCase();
					if (wikis != null && !wikis.contains(entity)) {
						wikis.add(entity);
					}
				}
			}			
			br.close();			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return wikis;
	}
	
}
