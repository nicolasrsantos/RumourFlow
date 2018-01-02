package org.textsim.textrt.proc.grouptexts;

import java.io.IOException;

public class Tester {

	public static void main(String[] args){
		
		int Gthreads =  8 ;
		String textdir = "C:/Users/Vivian/Desktop/Vivian_project/testdata/doc";
		String rejdir = "C:/Users/Vivian/Desktop/Vivian_project/testdata/rej";
		String trigramDir ="C:/Users/Vivian/Desktop/Vivian_project/testdata/output";
		String outputDir= "C:/Users/Vivian/Desktop/Vivian_project/testdata";
		
		System.out.println("Calculating the text similarity...");
		long initTime, startTime, endTime;
		initTime = System.currentTimeMillis();
		startTime = initTime;
		GroupTexts group = null;
		
		try {
			group = new GroupTexts(trigramDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		group.readFiles(textdir, rejdir);
		
		group.computeGRT(outputDir, textdir, rejdir, Gthreads);
		
		endTime = System.currentTimeMillis();
		System.out.println("Total time: " + (endTime - startTime) / 1000.0);
	}
}
