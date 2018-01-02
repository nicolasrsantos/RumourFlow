package org.textsim.textrt.proc.SMP;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.text.StrBuilder;
import org.textsim.textrt.proc.SMP.UptriangularMatrix.Cell;
import org.textsim.textrt.proc.singlethread.*;
import org.textsim.util.DataFormatConverter;

import edu.rit.pj.*;

public class SMP_OneFile {

    private static int n;        // Number of threads
    private static int range;    // Number of tasks
    @SuppressWarnings("unused")
    private static float w1, w2; // Weights

//    static private TextSim_Hash sim = null;
    static private SinglethreadTextRtProcessor sim = null;
    static List<TextInstance> textList;
    static BufferedWriter bw;
    static UptriangularMatrix matrix;
    
    
    //debug info
	static long start, end ,pass;
    
    public static void main(String[] args)
            throws Exception {

        process(args);
    }
    
    public static void process(String[] args)
            throws Exception {
    	
        
        n = Integer.parseInt(args[0]);       // num of threads
        w1 = Float.parseFloat(args[4]);
        w2 = Float.parseFloat(args[5]);
        
        
        /*loading necessary support data structures*/
	 	start = System.currentTimeMillis();
        
        //Load the contents of idName from the preprocessing stage
        Path binaryFile = Paths.get(args[1]);
        Path idNamePath = Paths.get(binaryFile.getParent().toString(), "idName.txt");
        TextInstance.readIdNamefromFile(idNamePath.toFile());
        
    	end = System.currentTimeMillis();
		pass = end-start;
		System.out.println("Data Structure Loading Time : "+pass);
        
		
		/*loading necessary support data structures*/
	 	
	 	
        parallelTest(
                IntegerSchedule.guided(),
                args[1],                     // Binary file
                args[2],                     // Output file 
                args[3]);                    // Binary trigram
  
		System.out.println("Processing Time : "+pass);
        
        
	 	DataFormatConverter convert = new  DataFormatConverter(args[2]);
		convert.run(textList.size());

//		new File(args[2]).delete();
    }
    
    public static void parallelTest(final IntegerSchedule schedule, final String binaryFile, final String outputPath, final String binaryTri)
            throws Exception {
        
        new ParallelTeam(n).execute(new ParallelRegion() {
        	
        	 @Override
             public void start()
                 throws Exception {

                    // Read text corpus
                 textList = BinaryCorpus.readBinaryText(binaryFile);

                    // Build matrix for job look up
                 range = textList.size() * (textList.size() - 1) / 2;
                 matrix = new UptriangularMatrix(range);

                 

                    // Open writer for data record
                 bw = new BufferedWriter(new FileWriter(outputPath));
                    
                    // Create computation object
//                    sim = new TextSim_Hash(binaryTri);
                 sim = new SinglethreadTextRtProcessor(Paths.get(binaryTri));
                 
                 start = System.currentTimeMillis();
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
                        public void run(int first, int last)
                                throws IOException {
                        	
                            
                            for(int i=first; i<=last; i++) {
                                        
                                Cell cell = matrix.getPosition(i);
                                
                                /*Bug Fix, passing deepclone copies instead of original text 
                                 * Date Oct 21, 2013
                                 * Author: Zhimin Yao
                                 * */
                                //Deep cloning is now done elsewhere, not necessary here
                                TextInstance t1 = textList.get(cell.row);
                                TextInstance t2 = textList.get(cell.col);
                                
                                StrBuilder sb = new StrBuilder();
                                
                                //Single Result
//                                float similarity = (float)sim.compute(t1, t2, w1, w2);
//                                sb.append(t1.getFileID()).append("\t")
//                            		.append(t2.getFileID()).append("\t")
//                            		.append(similarity).append("\n"); 
                                
                                
                                //Split Result
                                SplitResult result = sim.simSp(t1, t2);
                                //String nameA = TextInstance.idName.get(t1.getFileID());
                                //String nameB = TextInstance.idName.get(t2.getFileID());
                                
                                sb.append(t1.getFileID()).append("\t")
                                	.append(t2.getFileID()).append("\t")
                                	.append(result.resultA).append("\t")
                                	.append(result.resultB).append("\n");
                            	   
                                bw.write(sb.toString());
                            }
                        }
                });
            }
            
            @Override
            public void finish()
                    throws Exception {

                bw.close();
                
        	 	
            	end = System.currentTimeMillis();
        		pass = end-start;
            }
        });
    }
}
