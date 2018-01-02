package org.textsim.textrt.proc.multithreads;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor;
/**
 * GThreads calculates the similarity between each pair of texts in the queue
 * @author Vivian
 * @since 10 June, 2013
 */


class GThreads
        extends Thread {

	private ConcurrentLinkedQueue<Pair> queue;
	private List<TextInstance> textList;
	private SinglethreadTextRtProcessor sim;
	private BufferedWriter bw;

	/**
	 * Constructor: pass queue, trigram in the memory, text directory and rejection text directory to thread
	 * @param queue2
	 * @param sim
	 * @param textdir
	 * @param rejdir
	 */
	public GThreads(ConcurrentLinkedQueue<Pair> queue, SinglethreadTextRtProcessor sim,  List<TextInstance> textList, BufferedWriter bw) {
		this.textList = textList;
		this.queue=queue;
		this.sim = sim;
		this.bw = bw;
	}

	public void run() {
		Pair pair;
		TextInstance copyA, copyB;
		while((pair=queue.poll())!=null) {
			copyA = textList.get(pair.getA()).deepClone();
			copyB = textList.get(pair.getB()).deepClone();
			try {
				String entry= (copyA.getFileID())+"\t"+ (copyB.getFileID())+"\t"+sim.sim(copyA, copyB);
				bw.write(entry+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}


		}
	}
}