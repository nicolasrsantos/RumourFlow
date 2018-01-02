package com.github.jreddit.memes;

public class UserLink {
	private String source = "";
	private String target = "";
	private Double weight = 0.0;
	private String type;
	private Double sourceTimestamp;
	private Double targetTimestamp;
	
	public UserLink(String source, String target, Double weight) {
		super();
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
	public UserLink(String source, String target, Double weight, String type, Double sourceTimestamp, Double targetTimestamp) {
		super();
		this.source = source;
		this.target = target;
		this.weight = weight;
		this.type = type;
		this.sourceTimestamp = sourceTimestamp;
		this.targetTimestamp = targetTimestamp;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getSourceTimestamp() {
		return sourceTimestamp;
	}
	public void setSourceTimestamp(Double sourceTimestamp) {
		this.sourceTimestamp = sourceTimestamp;
	}
	public Double getTargetTimestamp() {
		return targetTimestamp;
	}
	public void setTargetTimestamp(Double targetTimestamp) {
		this.targetTimestamp = targetTimestamp;
	}	
}
