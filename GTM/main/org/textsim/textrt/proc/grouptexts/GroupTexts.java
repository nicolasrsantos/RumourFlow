package org.textsim.textrt.proc.grouptexts;

//import java.io.BufferedWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor;


/**
 * GroupTexts class reads files from two folders( texts folders and rejection texts folders) and put each file names into a queue;
 * then call multiple threads to compute the text similarity between each pair of texts
 * @author Vivian
 * @version 0.3
 * @since June 10/2013
 */

public class GroupTexts {
	
	private ConcurrentLinkedQueue<Pair> queue;
    private SinglethreadTextRtProcessor sim = null;
	/**
	 * Constructor specifies how many threads be used
	 * @param numofGthread
	 * @throws IOException 
	 */
	public GroupTexts(String TrigramFile) throws IOException
	{
		queue = new ConcurrentLinkedQueue<Pair>();
		System.out.println("Loading trigram...");
		sim = new SinglethreadTextRtProcessor(Paths.get(TrigramFile));
	}
	/**
	 * Read from text folder and rejection text folder; put two text name and rejection text name in a pair;
	 * put every pair in a queue
	 * @param textdir  the directory contains texts
	 * @param rejdir   the directory contains rejection texts
	 */
	public void readFiles(String textdir, String rejdir)
	{
		File textFolder = new File(textdir);//directory name
		String[] listOftxt = textFolder.list();
		File rejFolder = new File(rejdir);
		String[] listOfrej = rejFolder.list();
		Pair pair;
	
		for (int i = 0; i < 100; i++) 
		{
			for(int g = i+1; g < 100;g++)
			{
				pair = new Pair();
				pair.setElementA(listOftxt[i], listOfrej[i]);
				pair.setElementB(listOftxt[g], listOfrej[g]);
				queue.add(pair);
			}
		}
	}
	/**
	 * Compute the similarity between each pair of texts using dynamic multithreads
	 * @param textdir
	 * @param rejdir
	 * @param trigramFile
	 * @param trigramNum
	 * @param unigramNum
	 * @param numofthreads
	 */
	public void computeGRT(String outputDir, String textdir, String rejdir, int numofthreads)
	{
		
		BufferedWriter bw = null;
	
		ArrayList<GThreads> threadPool = new ArrayList<GThreads>();
        String pathname = outputDir + File.separator + "Similarity";
		
        try {
			bw =  new BufferedWriter(new FileWriter(pathname));
		} catch (IOException e) {
			e.printStackTrace();
		}
      
        long initTime, startTime, endTime;
		initTime = System.currentTimeMillis();
	    startTime = initTime;
		System.out.println("Computing the text similarity...");
		
		
		for(int i=1;i<=numofthreads;i++){
			threadPool.add(new GThreads(queue,sim,textdir,rejdir,bw));
		}
		

		for(GThreads gr : threadPool){
			gr.start();
		}
		
		try { // wait for completion of all thread and then sum
			for(GThreads gr : threadPool){
				gr.join();
			}

		}
		catch(InterruptedException IntExp) {
			System.out.println("Thread failed");
		}
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		endTime = System.currentTimeMillis();
		System.out.println("Done!\nTime taken: " + (endTime - startTime) / 1000.0);
		
	}
}