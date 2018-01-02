package org.textsim.wordrt.proc;

import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.textsim.util.BinaryFileFastRecordReader;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;

public class HashedWordRtProcessor
        extends AbstractWordRtProcessor {

    private TIntObjectHashMap<TIntDoubleHashMap> rtMap;
    
    // TODO Construct the object with pre-processed text files.
    
    /**
     * Construct the object with pre-processed binary trigram file.
     *
     * @param binTrigramFile  the pre-processed binary trigram file.
     *
     * @throws IOException  when I/O error occurs.
     */
    public HashedWordRtProcessor(Path binaryFileName)
            throws IOException {
    	
    	this.rtMap = new TIntObjectHashMap<TIntDoubleHashMap>();
		BinaryFileFastRecordReader fastReader = null;
		MappedByteBuffer readerBuffer = null;
		try {
			fastReader = new BinaryFileFastRecordReader(binaryFileName.toFile());
			readerBuffer = fastReader.getBuffer();
			long recordSize = readerBuffer.getLong();
		    readerBuffer.getInt();  // Omit the total trigram count.
		    readerBuffer.getInt();  // Omit the total unigram count.
			while(true){
				try {
					//get next record length
					recordSize = readerBuffer.getLong();
					//check if EOF is reached
					if(recordSize == 0){
						break;
					}else if(readerBuffer.remaining() < recordSize){
						//check if buffer remaining size is bigger than record length
						//refill buffer and check if buffer can be refill
						if(! fastReader.refillBuffer()){
							break;
						}
						readerBuffer = fastReader.getBuffer();
					}
					//read record
					int w1ID = readerBuffer.getInt();
					int start = readerBuffer.getInt();
					int end = readerBuffer.getInt();
					TIntDoubleHashMap w3Map = new TIntDoubleHashMap();
		    		for (int i = start; i < end; i++) {
		    			w3Map.put(readerBuffer.getInt(), readerBuffer.getDouble());
		    		}
		    		this.rtMap.put(w1ID, w3Map);
				} catch (IOException e) {
				}
			}
		} finally {
		    close(fastReader);
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
            int temp = word1; word1 = word2; word2 = temp;
        } else if (word1 == word2) {
            return 1;
        }
        try {
            return rtMap.get(word1).get(word2);
    	} catch (NullPointerException e) {
    		return 0;
        }
    }
}
