package org.textsim.wordrt.proc;

import org.textsim.util.BinaryFileFastRecordReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.util.StringTokenizer;

public class DefaultWordRtProcessor
        extends AbstractWordRtProcessor {

    private int[]    tListStr;  // The start index of trigram sublist (inclusive)
    private int[]    tListEnd;  // The end index of trigram sublist (exclusive)
    private int[]    tListID;   // The ID of the third word in trigram
    private double[] rt;        // The word relatedness of the trigram
    private int      uNum;      // The number of unigrams.
                                // This variable is the length when construct tListStr and tListEnd.
                                // Therefore the index of the arrrays represents the ID of the unigram.
    private int      tNum;      // The number of unordered trigram pairs
                                // This variable is the length when construct rt and tListID.
                                // The index can be looked up in the tListStr and tListEnd.

    /**
     * Construct the object with pre-processed text files.
     *
     * @param uCountFile  the pathname of the unigram count file.
     * @param tDataFile   the pathname of the trigram data file.
     * @param tCountFile  the pathname of the trigram count file.
     *
     * @throws IOException  when I/O error occurs.
     */
    public DefaultWordRtProcessor(Path uCountFile, Path tDataFile, Path tCountFile)
            throws IOException {

        this.tNum = readCount(tCountFile);
        this.uNum = readCount(uCountFile);
        BufferedReader trigramFile = null;
        try {
        	trigramFile = new BufferedReader(new FileReader(tDataFile.toFile()));
            this.tListStr = new int[this.uNum + 1];
            this.tListEnd = new int[this.uNum + 1];
            this.tListID  = new int[this.tNum];
            this.rt       = new double[this.tNum];
            int w3Index = 0;  // The index of tListID and rt. This variable is used for positioning when storing data.
            for (String inputLine; (inputLine = trigramFile.readLine()) != null; ) {
                StringTokenizer line = new StringTokenizer(inputLine);
                int w1ID = Integer.parseInt(line.nextToken());
                this.tListStr[w1ID] = w3Index;
                while (line.hasMoreElements()) {
                    this.tListID[w3Index] = Integer.parseInt(line.nextToken());
                    this.rt[w3Index] = Double.parseDouble(line.nextToken());
                    w3Index++;
                }
                this.tListEnd[w1ID] = w3Index;
            }
        } finally {
            close(trigramFile);
        }
    }

    public DefaultWordRtProcessor(Path binTrigramFile)
            throws IOException {
        this(binTrigramFile.toFile());
    }
    
    /**
     * Construct the object with pre-processed binary trigram file.
     *
     * @param binTrigramFile  the pre-processed binary trigram file.
     *
     * @throws IOException  when I/O error occurs.
     */
    public DefaultWordRtProcessor(File binTrigramFile)
            throws IOException {
    	
		BinaryFileFastRecordReader fastReader = new BinaryFileFastRecordReader(binTrigramFile);
        try {
            fastReader = new BinaryFileFastRecordReader(binTrigramFile);
    		MappedByteBuffer readerBuffer = fastReader.getBuffer();
    		long recordSize = readerBuffer.getLong();
    	    this.tNum = readerBuffer.getInt();
    	    this.uNum = readerBuffer.getInt();
        	this.tListStr = new int[this.uNum+1];
            this.tListEnd = new int[this.uNum+1];
            this.tListID  = new int[this.tNum];
            this.rt       = new double[this.tNum];
    		while (true) {
    			try {
    				//get next record length
    				recordSize = readerBuffer.getLong();
    				//check if EOF is reached
    				if (recordSize == 0) {
    					break;
    				} else if (readerBuffer.remaining() < recordSize) {
    					//check if buffer remaining size is bigger than record length
    					//refill buffer and check if buffer can be refill
    					if(! fastReader.refillBuffer()) {
    						break;
    					}
    					readerBuffer = fastReader.getBuffer();
    				}
    				//read record
    				int word1ID = readerBuffer.getInt();
    				int start = readerBuffer.getInt();
    				int end = readerBuffer.getInt();
    				this.tListStr[word1ID] = start;
    	    		this.tListEnd[word1ID] = end;
    	    		for (int i = start; i < end; i++) {
    	    			this.tListID[i] = readerBuffer.getInt();
    	    			this.rt[i] = readerBuffer.getDouble();
    	    		}
    			} catch (IOException e) {
    			}
    		}
        } finally {
            close(fastReader);
        }
    }

    private int readCount(Path countFile)
            throws IOException {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(countFile.toFile()));
            return Integer.parseInt(br.readLine());
        } finally {
            close(br);
        }
    }
    
    private void close(BufferedReader br)
            throws IOException {

        if (br != null) {
            br.close();
        }
    }
    
    private void close(BinaryFileFastRecordReader bffrr)
            throws IOException {

        if (bffrr != null) {
            bffrr.close();
        }
    }

	/*
	 * Look-up the relatedness of two given words/grams from in-memory data.
	 * 
	 * @see org.textsim.wordrt.proc.AbstractWordRtProcessor#sim(int, int)
	 */
	@Override
    public double sim(int word1, int word2) {
        
        if (word1 > word2) {
            int temp = word1;
            word1 = word2;
            word2 = temp;
        } else if (word1 == word2) {
            return 1;
        }
        // Binary search
        int indStr = tListStr[word1],
            indEnd = tListEnd[word1],
            indMid = 0;
        while (indStr <= indEnd) {
        	indMid = (indStr + indEnd) / 2;
        	if (tListID[indMid] == word2)
            	return rt[indMid];
            else if (tListID[indMid] > word2)
                indEnd = indMid - 1;
            else
                indStr = indMid + 1;
        }
        return 0;
    }
}
