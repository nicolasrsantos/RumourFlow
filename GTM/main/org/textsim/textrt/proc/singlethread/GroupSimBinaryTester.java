package org.textsim.textrt.proc.singlethread;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

// TODO Delete
public class GroupSimBinaryTester {

	public static void main(String[] args) throws NumberFormatException, IOException{
		Double textSim;
		//file path
		String textDoc = "C:/Users/Vivian/Desktop/Vivian_project/testdata/doc/ACM.title-abst.tag-00001-doc.ic";
		String textRej = "C:/Users/Vivian/Desktop/Vivian_project/testdata/rej/ACM.title-abst.tag-00001-rej.gc";
		String binaryFile ="binary100"; 
		String binaryTrigram = "binaryTrigram";
		List<TextInstance> list;
		TextInstance textA;
		TextInstance copyA;
		TextInstance textB;
		textA = new TextInstance(textDoc, textRej);
		System.out.println(textA.getCont().size());
		SinglethreadTextRtProcessor sim = new SinglethreadTextRtProcessor(Paths.get(binaryTrigram));
		list = BinaryCorpus.readBinaryText(binaryFile);
		for (int i = 0; i <2; i++)
		{
			copyA = textA.deepClone();
			textB = list.get(i);
			textSim=sim.sim(copyA, textB);
			System.out.println(textSim);
		}
		
	}
}
