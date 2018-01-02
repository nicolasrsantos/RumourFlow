package org.textsim.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;

import org.apache.commons.lang3.NotImplementedException;
import org.textsim.exception.ProcessException;
import org.textsim.exception.ProcessRuntimeException;
import org.textsim.wordrt.preproc.WordrtPreproc;

/**
 * Provide method for loading pre-processed trigram data to memory.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class Trigram {
    
    public static String TEXT_DATA_SUFFIX = "td";
    public static String TEXT_UNACCEPTED_DATA_SUFFIX = "tn";
    public static String TEXT_COUNT_SUFFIX = "tc";
    public static String BINARY_SUFFIX = "tri";
    
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

    public static Trigram load(int preprocType, Path file)
        throws ProcessException {
        
        try {
            if (preprocType == WordrtPreproc.BINARY) {
                return new Trigram().read(file);
            } else if (preprocType == WordrtPreproc.TEXT) {
                throw new NotImplementedException("TODO");
            } else {
                throw new ProcessRuntimeException("Invalid preprocess type is given.");
            }
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }

    public static int readCountFile(int preprocType, File file)
            throws IOException {

        if (preprocType == WordrtPreproc.TEXT) {
            int count;
            BufferedReader reader = null;
            try {
                reader = IOUtil.getBufferedFileReader(file, "tri");
                count = Integer.parseInt(reader.readLine());
            } finally {
                reader.close();
            }
            return count;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public Trigram read(Path binTrigramFile)
            throws IOException {
        
        BinaryFileFastRecordReader fastReader = new BinaryFileFastRecordReader(binTrigramFile.toFile());
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
        return this;
    }
    
        public void write(Path binaryFileName)
                throws IOException {

            BinaryFileBufferedWriter bwt = null;
            try {
                bwt = new BinaryFileBufferedWriter(binaryFileName.toFile());
                // Count size in byte.
                long recordSize = 0;
                int totalLength = this.tListEnd.length;
                recordSize = 16; // = 8 + 8
                bwt.writeLong(recordSize);
                bwt.writeInt(this.tNum);
                bwt.writeInt(this.uNum);
                for (int i = 0; i < totalLength; i++) {
                int end = this.tListEnd[i];
                if (end > 0) {
                    recordSize = 20; // = 8 + 3 * 4
                    int start = this.tListStr[i];
                    recordSize += (end - start) * 12;
                    bwt.writeLong(recordSize);
                    // write word1 information.
                    bwt.writeInt(i);
                    bwt.writeInt(start);
                    bwt.writeInt(end);
                    // write related word2 information.
                    for (int j = start; j < end; j++) {
                        bwt.writeInt(this.tListID[j]);
                        bwt.writeDouble(this.rt[j]);
                    }
                }
            }
            bwt.writeLong(0);
        } finally {
            bwt.close();
        }
    }

}
