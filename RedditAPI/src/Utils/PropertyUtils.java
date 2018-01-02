package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class PropertyUtils {

	private static Properties config = new Properties();

	static {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String gtmProps = "gtm.properties";

		try {
			InputStream is = classLoader.getResourceAsStream(gtmProps);
			config.load(is);
		} catch (IOException e) {
			//throw new RMVValidationException(e);
		}
	}
		
	public static String getUnigramPath() {
		return getString("gtm.unigram.path");
	}
	
	public static String getTrigramPath() {
		return getString("gtm.trigram.path");
	}
	
	public static String getStopwordPath() {
		return getString("gtm.stopwords.path");
	}
	
	private static String getString(String key) {
		return config.getProperty(key);
	}
	
	public static String getRumourPath() {
		return getString("gtm.rumour.path");
	}
	
	public static String getCommentPath() {
		return getString("gtm.comments.path");
	}
}