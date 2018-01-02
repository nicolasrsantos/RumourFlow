package org.textsim.wordrt.preproc;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.text.DecimalFormat;

import org.textsim.exception.ProcessException;
import org.textsim.util.FileUtil;
import org.textsim.util.IOUtil;
import org.textsim.util.Unigram;

/**
 * Forth step of pre-process to output two words with their relatedness.
 * <p>
 * <ol>
 * <li>read the output of step 3 which has sorted words id and their trigram frequency.
 * <li>read each word and its count from the Unigram file and store them into an array.
 * <li>add the frequency of two same entries in the output of step 3.
 * <li>compute the relatedness of words and store them in a file.
 * </ol>
 * The output does not have repeated entries, the pattern is:
 * <pre>
 * word1id	word2id	relatedness 
 * </pre></p>
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class  WordrtComputation {

    /**
     * The suffix {@code String} of the outptut data file.
     */
    public static final String DATA_SUFFIX = "wcd";

    /**
     * The suffix {@code String} of the outptut count file.
     */
    public static final String COUNT_SUFFIX = "wcc";

    /**
     * The maximum unigram frequence.
     */
    private static double cMax = 0;

    /**
     * {@code WordrtComputation} reads the output of {@code TrigramExternalSort} in the directory, compute and output to
     * the same directory.
     * 
     * @param  tempDir    The pathname {@code String} of the processing temporary directory.
     * @param  outputDir  The pathname {@code String} of the output directory.
     *
     * @throws IOException
     * @throws ProcessException 
     */
    public static void processT(String tempDir)
            throws FileNotFoundException, NoSuchElementException, IOException, ProcessException {
		
        File trigramSortedInterDataFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, TrigramExternalSort.DATA_SUFFIX);
    	File unigramDataFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.DATA_SUFFIX);
    	File unigramCountFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.COUNT_SUFFIX);
    	String trigramDataFilePathname = tempDir + File.separator + FileUtil.getFilenameWithoutSuffix(trigramSortedInterDataFile) + '.' + DATA_SUFFIX;
    	String trigramCountFilePathname = tempDir + File.separator + FileUtil.getFilenameWithoutSuffix(trigramSortedInterDataFile) + '.' + COUNT_SUFFIX;
    	 	
    	long count = 0;
    
    	BufferedReader trigramSortedInterData = null;
    	BufferedWriter trigramData = null;
    	BufferedWriter trigramCount = null;
    	try {
    	 	trigramSortedInterData = new BufferedReader(new FileReader(trigramSortedInterDataFile));
    	 	trigramData = new BufferedWriter(new FileWriter(trigramDataFilePathname));
    	 	trigramCount = new BufferedWriter(new FileWriter(trigramCountFilePathname));
    	 	
    	 	String unigramDataName = FileUtil.getFilenameWithoutSuffix(unigramDataFile.getCanonicalPath(), UnigramPreprocess.DATA_SUFFIX);
    	 	String trigramDataName = FileUtil.getFilenameWithoutSuffix(trigramDataFilePathname, TrigramCountSummation.DATA_SUFFIX);
    	 	// Add header to the files
    	 	IOUtil.writePreprocTrigramHeader(trigramData, new File(unigramDataName), new File(trigramDataName), "data");
    	 	IOUtil.writePreprocTrigramHeader(trigramCount, new File(unigramDataName), new File(trigramDataName), "count");
    		
    		int gram1ID = 0, gram2ID = 0, prevGram1ID = 0, prevGram2ID = 0;
    		long freq = 0;
    		boolean isFirstGram = true;
    		double rt;
    		long[] uCount = Unigram.readCounts(WordrtPreproc.TEXT, unigramDataFile, unigramCountFile);
    		cMax = Unigram.readCMax(unigramCountFile);
    		DecimalFormat df = new DecimalFormat("#.###");
    
    		for (String inputLine; (inputLine = trigramSortedInterData.readLine()) != null; ) {
    			StringTokenizer line = new StringTokenizer(inputLine);
    
    			gram1ID = Integer.parseInt(line.nextToken());
    			gram2ID = Integer.parseInt(line.nextToken());
    
    			if (isFirstGram) {
    				prevGram1ID = gram1ID;
    				prevGram2ID = gram2ID;
    				freq = Long.parseLong(line.nextToken());
    				trigramData.write(Integer.toString(gram1ID));
    				isFirstGram = false;
    			} else if (gram1ID == prevGram1ID && gram2ID == prevGram2ID) {
    				freq += Long.parseLong(line.nextToken());
    			} else {
    				rt = computeRT(uCount[prevGram1ID], uCount[prevGram2ID], freq);
    				 
    				if (rt > 0.001) {
    					count++;
    					trigramData.write("\t" + prevGram2ID + "\t" + df.format(rt));
    				}
    				
    				if (prevGram1ID != gram1ID)
    					trigramData.write("\n" + Integer.toString(gram1ID));
    				
    				prevGram1ID = gram1ID;
    				prevGram2ID = gram2ID;
    				freq = Long.parseLong(line.nextToken());
    			}
    		}
    		rt = computeRT(uCount[prevGram1ID], uCount[prevGram2ID], freq);
    
    		if (prevGram1ID != gram1ID) {
    			trigramData.write("\n" + gram1ID);
    		}
    
    		if (rt > 0.001) {
    			trigramData.write("\t" + prevGram2ID + "\t" + df.format(rt));
    			count++;		
    		}
	 	}
	 	finally {
 	        if (trigramSortedInterData != null) {
        		trigramSortedInterData.close();
            }
 	        if (trigramData != null) {
        	    trigramData.close();
            }
 	        if (trigramCount != null) {
        	    trigramCount.write(Long.toString(count));
        	    trigramCount.close();
 	        }
	 	}
	 	
	}
	
    /**
     * Compute the relatedness of two grams.
     * 
     * @param  gram1Count  The {@code long} count of the gram in either first or third postion of the trigram.
     * @param  gram2Count  The {@code long} count of the another gram in either first or third postion of the trigram.
     * @param  freq        The {@code long} count of this trigram.
     *
     * @return The relatedness value of two grams.
     *
     * @throws NumberFormatException
     * @throws IOException
     * 
     * Need DEBUG
     */
	 private static float computeRT(long gram1Count, long gram2Count, long freq) throws NumberFormatException, IOException {
		 
		 double gramMinCount = Math.min(gram1Count, gram2Count);
	     //double cMax = 19401194714.0;  // maximum frequency among all unigram
	     double cMaxSquared = cMax * cMax;
	     
	     if(freq == 0) {
	       	 return 0;
	     } else {
	    	 double condition = (freq / 2 * cMaxSquared) / (gram1Count * gram2Count * gramMinCount);
	  	  
	  	     if (condition > 1) {
	  	       	 return (float)(Math.log(condition) / (-2 * Math.log(gramMinCount /cMax)));
	  	     } else {
	  	         return (float)(Math.log(1.01) / (-2 * Math.log(gramMinCount /cMax)));
		     }
	     }
	 }
	 
//	 public static void main(String[] args) throws NumberFormatException, IOException{
//		 // word1 occurs 10000 times
//		 //word1 (X) word1 occurs 5 times
//		 System.out.println(WordrtComputation.computeRT(10000,10000,2));
//	 }
}
