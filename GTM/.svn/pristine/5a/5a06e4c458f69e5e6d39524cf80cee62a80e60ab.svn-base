package org.textsim.textrt.proc.multithreads;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.textsim.textrt.proc.singlethread.BinaryCorpus;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor;

/**
 * GroupTexts class reads files from two folders( texts folders and rejection texts folders) and put each file names into a queue;
 * then call multiple threads to compute the text similarity between each pair of texts
 * @author Vivian
 * @version 0.3
 * @since June 10/2013
 */

public class GroupSim_Multithreads {
	
    public static void main(String[] args) throws IOException{
		
		int threads =  Integer.parseInt(args[0]) ;
		String binaryTrigramFile= args[1];
		String binaryTextFile = args[2];
		
		System.out.println("Calculating the text similarity...");
		long initTime, startTime, endTime;
		initTime = System.currentTimeMillis();
		startTime = initTime;
		System.out.println("Loading Binary Trigram...");
		SinglethreadTextRtProcessor sim = new SinglethreadTextRtProcessor(Paths.get(binaryTrigramFile));
		endTime = System.currentTimeMillis();
		startTime = endTime;
		System.out.println("Trigram Loading time: " + (endTime - initTime) / 1000.0);
		groupSim(binaryTextFile,threads, sim);
		endTime = System.currentTimeMillis();
		System.out.println("Done! Total time: " + (endTime - startTime) / 1000.0);
	}

	/**
	 * Read from text folder and rejection text folder; put two text name and rejection text name in a pair;
	 * put every pair in a queue
	 * @param textdir  the directory contains texts
	 * @param rejdir   the directory contains rejection texts
	 * @throws IOException 
	 */
	public static void groupSim(String binaryTextFile, int numofthds, SinglethreadTextRtProcessor sim)
	        throws IOException {

		ConcurrentLinkedQueue<Pair> queue = new ConcurrentLinkedQueue<Pair>();
		List<TextInstance> textList = BinaryCorpus.readBinaryText(binaryTextFile);
		BufferedWriter bw = new BufferedWriter(new FileWriter(binaryTextFile+"_result"));
		Pair pair;
		//When the number of elements in the queue reaches this constant, threads process the queue
		int constant = 10000000;
		long counter = 0;
		for (int i = 0; i < textList.size(); i++) {
			for(int g = i+1; g<textList.size();g++) {
				pair = new Pair(i,g);
				queue.add(pair);
				counter ++;
				if (counter >= constant) {
					threading(queue, numofthds, textList, bw, sim);
					counter=0;
				}
			}
		}
		threading(queue, numofthds, textList, bw, sim);
		bw.close();
		
	}

    /**
     * Assigns work to threads
     * 
     * @param queue			A ConcurrentLinkedQueue stores pairs of text instances
     * @param numofthds		Number of threads
     * @param textList		An ArrayList stores all the text instances
     * @param bw			A ConcurrentBufferedWriter
     * @param sim			TextSim instance with Trigram
     */
	private static void threading(ConcurrentLinkedQueue<Pair> queue, int numofthds, List<TextInstance> textList, BufferedWriter bw, SinglethreadTextRtProcessor sim) {
		
		ArrayList<GThreads> threadPool = new ArrayList<GThreads>();
			
		for(int i=1;i<=numofthds;i++) {
			threadPool.add(new GThreads(queue,sim,textList,bw));
		}
		for(GThreads nr : threadPool) {
			nr.start();
		}
		
		try { 
			// wait for completion of all thread and then sum
			for(GThreads nr : threadPool){
				nr.join();
			}
		} catch(InterruptedException IntExp) {
			System.out.println("Thread failed");
		}			
	}
}