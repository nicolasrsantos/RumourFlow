package org.textsim.textrt.proc.singlethread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

// TODO Delete
class GroupSimTester {

	public static void main(String[] args) throws NumberFormatException, IOException{
		Double textSim;
		//file path
		String textDoc = "C:/Users/Vivian/Desktop/Vivian_project/testdata/doc/ACM.title-abst.tag-00001-doc.ic";
		String textRej = "C:/Users/Vivian/Desktop/Vivian_project/testdata/rej/ACM.title-abst.tag-00001-rej.gc";
		//folder path
		String docDir = "";
		String rejDir = "";
		String binaryFile = "binaryTrigram";
		TextInstance textA;
		TextInstance textB;
		TextInstance copyA;
		textA = new TextInstance(textDoc, textRej);
		SinglethreadTextRtProcessor sim = new SinglethreadTextRtProcessor(Paths.get(binaryFile));
		
		
		File textFolder = new File(docDir);//directory name
		String[] listOftext = textFolder.list();
		File rejFolder = new File(rejDir);
		String[] listOfrej = rejFolder.list();
		
		for (int i = 0; i <listOftext.length; i++)
		{
			copyA = textA.deepClone();
			textB = new TextInstance(docDir,rejDir,listOftext[i],listOfrej[i]);
			textSim=sim.computeTextRT(copyA, textB);
			System.out.println(i+" "+textSim);
		}
		
	}
}
