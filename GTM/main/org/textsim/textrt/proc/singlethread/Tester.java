
package org.textsim.textrt.proc.singlethread;

import java.io.IOException;
import java.nio.file.Paths;

class Tester {

    // TODO Delete
    public static void main(String[] args) throws NumberFormatException, IOException{
    	double textsimilarity;
    	String textADoc = "C:/Users/Vivian/Desktop/Vivian_project/testdata/doc/ACM.title-abst.tag-00001-doc.ic";
    	String textARej = "C:/Users/Vivian/Desktop/Vivian_project/testdata/rej/ACM.title-abst.tag-00001-rej.gc";
    	String textBDoc ="C:/Users/Vivian/Desktop/Vivian_project/testdata/doc/ACM.title-abst.tag-00002-doc.ic";
    	String textBRej = "C:/Users/Vivian/Desktop/Vivian_project/testdata/rej/ACM.title-abst.tag-00002-rej.gc";
    	String binaryFile = "binaryTrigram";
    	TextInstance textA;
    	TextInstance textB;
    	textA = new TextInstance(textADoc, textARej);
    	textB = new TextInstance(textBDoc, textBRej );
    	
    	SinglethreadTextRtProcessor sim = new SinglethreadTextRtProcessor(Paths.get(binaryFile));
    	textsimilarity=sim.sim(textA, textB);
    	System.out.println(textsimilarity);
    
	}
}
