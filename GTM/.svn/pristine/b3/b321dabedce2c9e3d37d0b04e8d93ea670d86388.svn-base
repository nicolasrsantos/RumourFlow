package org.textsim.textrt.proc.grouptexts;

//import java.io.BufferedWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor;

/**
 * Thread used to compute the text similarity between a pair of texts in the queue
 * @author Vivian
 * @version 0.2
 * @since June 10/2013
 */
public class GThreads extends Thread {

	private ConcurrentLinkedQueue<Pair> queue;
	private SinglethreadTextRtProcessor sim;
	private String textdir;
	private String rejdir;
	private BufferedWriter bw;
	/**
	 * Constructor: pass queue, trigram in the memory, text directory and rejection text directory to thread
	 * @param queue
	 * @param sim
	 * @param textdir
	 * @param rejdir
	 */
	public GThreads(ConcurrentLinkedQueue<Pair> queue, SinglethreadTextRtProcessor sim, String textdir,String rejdir, BufferedWriter bw)
	{
		this.queue=queue;
		this.sim = sim;
		this.textdir = textdir;
		this.rejdir = rejdir;
		this.bw = bw;
	}
	/**
	 * overwritten run() method to do the computation part
	 */
	public void run()
	{
		Pair pair;
		TextInstance textA = null , tempA;
		TextInstance textB = null , tempB;
		String line = null;

		while((pair=queue.poll())!=null)
		{
				try {
					textA = new TextInstance(textdir, rejdir, pair.getTextA(), pair.getRejA());
					textB = new TextInstance(textdir, rejdir, pair.getTextB(), pair.getRejB());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			
			tempA = textA.deepClone();	
			tempB = textB.deepClone();
			
			line = textA.getFileName() + " " + textB.getFileName() + " " + sim.computeTextRT(tempA, tempB);
			try {
				bw.write(line+"\n");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}