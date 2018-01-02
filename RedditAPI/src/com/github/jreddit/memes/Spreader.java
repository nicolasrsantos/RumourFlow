package com.github.jreddit.memes;

import java.util.List;

public class Spreader {
	private List<List<String>> spreaders;
	private List<List<String>> ignorants;
	private List<List<String>> stiflers;
	public List<List<String>> getSpreaders() {
		return spreaders;
	}
	public void setSpreaders(List<List<String>> spreaders) {
		this.spreaders = spreaders;
	}
	public List<List<String>> getIgnorants() {
		return ignorants;
	}
	public void setIgnorants(List<List<String>> ignorants) {
		this.ignorants = ignorants;
	}
	public List<List<String>> getStiflers() {
		return stiflers;
	}
	public void setStifler(List<List<String>> stiflers) {
		this.stiflers = stiflers;
	}
	public Spreader(List<List<String>> spreaders, List<List<String>> ignorants, List<List<String>> stiflers){
		this.spreaders = spreaders;
		this.ignorants = ignorants;
		this.stiflers = stiflers;
	}
}
