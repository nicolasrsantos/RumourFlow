package com.github.jreddit.memes;

public class Link {
	private Integer source = 0;
	private Integer target = 0;
	public Link(Integer source, Integer target, Double weight) {
		super();
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	public Integer getTarget() {
		return target;
	}
	public void setTarget(Integer target) {
		this.target = target;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	private Double weight = 0.0;
}
