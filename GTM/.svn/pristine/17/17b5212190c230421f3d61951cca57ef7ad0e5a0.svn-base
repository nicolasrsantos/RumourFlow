
package org.textsim.textrt.proc.SMP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.text.StrBuilder;
import org.textsim.textrt.proc.SMP.UptriangularMatrix.Cell;
import org.textsim.textrt.proc.singlethread.BinaryCorpus;
import org.textsim.textrt.proc.singlethread.SplitResult;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor;

import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.IntegerSchedule;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
/**
 * This SMP_MultiFiles class makes use of smp construct to calculate the similarity of a group of texts.
 * 
 * Uses parallel region and parallel for loop to make each thread work on different tasks(each task is to calculate the similarity of a pair of texts
 * in the uptriangular Matrix. The output is a folder of multiple files which have the naming convention: docname_ 
 * 
 * @author Vivian
 * @since
 * 
 */
public class SMP {

    static int n;     // Number of threads
    static int range; // Number of tasks
    
    static BufferedWriter[] bwPool;
    static private SinglethreadTextRtProcessor sim = null;
    static List<TextInstance> textList;
    static UptriangularMatrix matrix;
    
    /**
     * The entry to processing
     * @param args		Number of threads Binary documents and Binary trigram
     * @throws Exception
     */
    public static void main(String[] args)
            throws Exception {
        
        n = Integer.parseInt(args[0]);
        long initTime, endTime;
		initTime = System.currentTimeMillis();
        parallelTest(
                IntegerSchedule.guided(),	 // Guided Schedule
                args[1],                     // Binary file
                args[2]                      // Binary trigram
               ); 
        endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime - initTime) / 1000.0);
    }
   /**
    * Makes use of the construct in Parallel Java Library
    * @param schedule		With guided schedule, PJ divides the set of loop iterations into chunks that start out large and get progressively smaller	
    * @param binaryFile		One binary file consisted of all documents
    * @param binaryTri		Binary Trigram
    * @throws Exception
    */
    public static void parallelTest(final IntegerSchedule schedule, final String binaryFile, final String binaryTri)
            throws Exception {

        new ParallelTeam(n).execute(new ParallelRegion() {
            
                public void start()
                        throws Exception {

                	// Initialize the buffered writer pool
			    	bwPool = new BufferedWriter[n];
			    	File newDir = new File(binaryFile+"_Sim");
			    	if(newDir.exists()){
			    		for (int i = 0; i < n; i++) {
			    			bwPool[i] = new BufferedWriter(new FileWriter(newDir+File.separator+binaryFile+"_"+i));
			    		}
			    	}
			    	else{
			    		
			    		if(newDir.mkdir())
				    	{
				    		for (int i = 0; i < n; i++) {
				    			bwPool[i] = new BufferedWriter(new FileWriter(newDir+File.separator+binaryFile+"_"+i));
				    		}
				    	}
			    	}

                    // Read text corpus
                    textList = BinaryCorpus.readBinaryText(binaryFile);

                    // Build matrix for job look up
                    range = textList.size() * (textList.size() - 1) / 2;
                    matrix = new UptriangularMatrix(range);
            		long initTime, startTime, endTime;
            		initTime = System.currentTimeMillis();
            		startTime = initTime;
            		System.out.println("Loading Trigram...");
                    sim = new SinglethreadTextRtProcessor(Paths.get(binaryTri));
            		endTime = System.currentTimeMillis();
            		System.out.println("Trigram Loading time: " + (endTime - startTime) / 1000.0);
            }
             /**
              * Each thread starts from do the calculation in run()
              */
            @Override
            public void run()
                    throws Exception {

                execute(1, range, new IntegerForLoop() {
                        
                    @Override
                    public IntegerSchedule schedule() {
        
                        return schedule;
                    }
                    
                    @Override
                    public void run(int first, int last) throws IOException {
                        
                    	
                        for(int i=first; i<=last; i++) {
                                    
                            Cell cell = matrix.getPosition(i);
                            TextInstance copyA, copyB;
                            StrBuilder sb = new StrBuilder();
                            copyA = textList.get(cell.row).deepClone();
                            copyB = textList.get(cell.col).deepClone();
                        
                            try {
                            	//Calculate the similarity between a pair of documents and then write them into the file
                            	
                            	//single result
                            	//sb.append(copyA.getFileID()).append("\t").append(copyB.getFileID()).append("\t").append(sim.computeTextRT(copyA, copyB));
                            	
                            	//split result
                            	SplitResult result = sim.simSp(copyA, copyB);
                            	sb.append(copyA.getFileID()).append("\t").append(copyB.getFileID()).append("\t").append(result.resultA).append("\t").append(result.resultB);
                            	
                                bwPool[getThreadIndex()].write(sb.toString());
                                bwPool[getThreadIndex()].newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }//for
                    }//run
                });//execute

	            bwPool[getThreadIndex()].close();
            }//run
        });//execute
    }//parallelTest
}//SMP
