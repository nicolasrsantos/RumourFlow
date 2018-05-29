package dataextraction;

import java.util.ArrayList;
import java.util.List;

import com.github.jreddit.memes.Link;
import com.github.jreddit.memes.Nodes;
import com.github.jreddit.memes.UserLink;

public class SentimentJSON {
	private List<SentimentNode> links = new ArrayList<SentimentNode>();

	public List<SentimentNode> getLinks() {
		return links;
	}

	public void setLinks(List<SentimentNode> links) {
		this.links = links;
	}
}
