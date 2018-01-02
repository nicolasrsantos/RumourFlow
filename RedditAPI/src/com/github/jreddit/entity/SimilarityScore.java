package com.github.jreddit.entity;


public enum SimilarityScore {
	TITLE ("TITLE"),
    COMMENTS   ("COMMENTS"),
    MAX   ("MAX"),
    AVG    ("AVG");
    
	private final String name;
	private SimilarityScore(String aName) {
		name = aName;
    }

	public String geName() {
		return name;
	}
	
}

    