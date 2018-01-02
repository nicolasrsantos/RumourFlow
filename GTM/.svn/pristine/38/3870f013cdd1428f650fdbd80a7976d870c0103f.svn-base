package org.textsim.textrt.proc.SMP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.textsim.textrt.proc.SMP.UptriangularMatrix.Cell;
import org.textsim.textrt.proc.singlethread.BinaryCorpus;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor_Hash;
import org.apache.commons.lang3.text.StrBuilder;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.IntegerSchedule;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;

public class SMP_Hashing {

    static int n;     // Number of threads
    static int range; // Number of tasks

    static private SinglethreadTextRtProcessor_Hash sim = null;
    static List<TextInstance> textList;
    static BufferedWriter bw;
    static UptriangularMatrix matrix;
    
    
    public static void main(String[] args)
            throws Exception {
        
        n = Integer.parseInt(args[0]);
        long initTime, endTime;
		initTime = System.currentTimeMillis();
        parallelTest(
                IntegerSchedule.guided(),
                args[1],                     // Binary file
                args[2],                     // Output directory
                args[3]);                     // Binary trigram
        endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime - initTime) / 1000.0);
    }
    
    public static void parallelTest(final IntegerSchedule schedule, final String binaryFile, final String outputdir, final String binaryTri)
            throws Exception {

        
        new ParallelTeam(n).execute(new ParallelRegion() {
            
                public void start()
                        throws Exception {

                    // Read text corpus
                    textList = BinaryCorpus.readBinaryText(binaryFile);

                    // Build matrix for job look up
                    range = textList.size() * (textList.size() - 1) / 2;
                    matrix = new UptriangularMatrix(range);

                 

                    // Open writer for data record
                    bw = new BufferedWriter(new FileWriter(outputdir));
                    
            		long initTime, startTime, endTime;
            		initTime = System.currentTimeMillis();
            		startTime = initTime;
                    // Create computation object
                    sim = new SinglethreadTextRtProcessor_Hash(Paths.get(binaryTri));
            		endTime = System.currentTimeMillis();
            		System.out.println("Trigram Loading time: " + (endTime - startTime) / 1000.0);
            }

            @Override
            public void run()
                    throws Exception {
                
                execute(1, range, new IntegerForLoop() {
                        
                    @Override
                    public IntegerSchedule schedule() {
        
                        return schedule;
                    }
                    
                    @Override
                    public void run(int first, int last) {
                        
                        for(int i=first; i<=last; i++) {
                                    
                            Cell cell = matrix.getPosition(i);
                            TextInstance copyA, copyB;
                            //String entry;
                            StrBuilder sb = new StrBuilder();
                            copyA = textList.get(cell.row).deepClone();
                            copyB = textList.get(cell.col).deepClone();
                            
                            try {
                            	sb.append(copyA.getFileID()).append("\t").append(copyB.getFileID()).append("\t").append(sim.computeTextRT(copyA, copyB));    
                            	//entry= (copyA.getFileID())+"\t"+ (copyB.getFileID())+"\t"+sim.computeTextRT(copyA, copyB);
                                    bw.write(sb.toString());
                                    bw.newLine();
                            } catch (IOException e) {
                                    e.printStackTrace();
                            }
                        }
                    }
                    
                    
                    
                });
            }
            
            @Override
            public void finish()
                    throws Exception {

                bw.close();
            }
        });
    }
}
