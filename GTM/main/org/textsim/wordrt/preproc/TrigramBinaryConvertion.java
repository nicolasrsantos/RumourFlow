package org.textsim.wordrt.preproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.textsim.exception.ProcessException;
import org.textsim.exception.ProcessRuntimeException;
import org.textsim.util.BinaryFileBufferedWriter;
import org.textsim.util.FileUtil;
import org.textsim.util.IOUtil;
import org.textsim.util.Trigram;
import org.textsim.util.Unigram;
import org.textsim.wordrt.preproc.WordrtPreproc;

/**
 * This class defines the pre-processing step which convert the trigram text file to be binary.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class TrigramBinaryConvertion {
    
    public static final String SUFFIX = "tbc";

    public static void process(String tempDir)
            throws ProcessException {
        
        try {
            // Find unigram files in the directory
            File textDataFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, WordrtComputation.DATA_SUFFIX);
            File textUnigramCountFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.COUNT_SUFFIX);
            File textTrigramCountFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, WordrtComputation.COUNT_SUFFIX);
            String name = FileUtil.getFilenameWithoutSuffix(textDataFile.getAbsolutePath(), UnigramPreprocess.DATA_SUFFIX);
            File binFile = new File(tempDir, name + '.' + SUFFIX);
            
            // Get unigram and trigram total count
            int unigramCount = Unigram.readCountFile(WordrtPreproc.TEXT, textUnigramCountFile);
            int trigramCount = Trigram.readCountFile(WordrtPreproc.TEXT, textTrigramCountFile);

            // Convert to binary
            convertToBinary(textDataFile, unigramCount, trigramCount, binFile);

        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }

	private static void convertToBinary(File textDataFile, int unigramCount, int trigramCount, File binFile)
	        throws IOException, ProcessException {

	    int[] tListStr = null;  // The start index of trigram sublist (inclusive)
	    int[] tListEnd = null;  // The end index of trigram sublist (exclusive)
	    int[] tListID = null;   // The ID of the third word in trigram
	    float[] rt = null;      // The word relatedness of the trigram
	    
	    BufferedReader trigramFile = null;
        try {
            trigramFile = IOUtil.getBufferedFileReader(textDataFile, "tri");

            tListStr = new int[unigramCount+1];
            tListEnd = new int[unigramCount+1];
            
            tListID  = new int[trigramCount];
            rt       = new float[trigramCount];
    
            int word1ID;         // The ID of word1 in trigram
            int word3Index = 0;  // The index of tListID and rt
                                 // The variable used for positioning when storing data.
           
            for (String inputLine; (inputLine = trigramFile.readLine()) != null; ) {
            	  
                StringTokenizer line = new StringTokenizer(inputLine);
                word1ID = Integer.parseInt(line.nextToken());
                try {
                tListStr[word1ID] = word3Index;
                } catch (Exception e) {
                    System.out.println(inputLine);
                    throw new ProcessRuntimeException(e);
                }
              
                
                while (line.hasMoreElements()) {
    
                    tListID[word3Index] = Integer.parseInt(line.nextToken());
                    
                    rt[word3Index] = Float.parseFloat(line.nextToken());
                   
                    word3Index++;
                }
                tListEnd[word1ID] = word3Index;
            }

        } finally {
            trigramFile.close();
        }
        
        BinaryFileBufferedWriter bwt = null;
        try {
            bwt = new BinaryFileBufferedWriter(binFile);
    
        	int i,j=0;
        	int start=0, end , totalLength;
      
        	long recordSize = 0; //size in byte
        	
        	totalLength = tListEnd.length;
        	
        	recordSize = 8;
        	recordSize += 8;
        	bwt.writeLong(recordSize);
        	bwt.writeInt(trigramCount);
        	bwt.writeInt(unigramCount);
        	
        	for(i=0;i<totalLength;i++){
        		if((end = tListEnd[i])> 0){
        			
        			recordSize = 8;
        			
        			start = tListStr[i];
        			
        			recordSize += 3*4;
        			recordSize += (end-start)*12;
        			
        			bwt.writeLong(recordSize);
        			/*write word1ID*/
        			bwt.writeInt(i);
        			/*write word1Start*/
        			bwt.writeInt(start);
        			/*write word1End*/
        			bwt.writeInt(end);
        			/*write word2Contents*/
        			for(j=start;j<end;j++){
        				bwt.writeInt(tListID[j]);
        				bwt.writeDouble(rt[j]);  //change to float
        			}
        		}
        	}
        } finally {
        	bwt.writeLong(0);
        	bwt.close();
        }
	}
}
