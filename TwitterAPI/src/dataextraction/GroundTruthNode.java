package dataextraction;

public class GroundTruthNode {
	private String post;
	private String beliefClass;
	private String sentiment;
	
	public GroundTruthNode(){
		// Nothing to do here
	}
	
	public GroundTruthNode(String post, String beliefClass, String sentiment) {
		super();
		this.post = post;
		this.beliefClass = beliefClass;
		this.sentiment = sentiment;
	}
	
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getBeliefClass() {
		return beliefClass;
	}
	public void setBeliefClass(String beliefClass) {
		this.beliefClass = beliefClass;
	}
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
}
