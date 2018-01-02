package org.textsim.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class BinaryFileFastRecordReader {

    private  FileChannel fileChannel;
    private  FileInputStream fileInputStream;
    private final long bufferSize;   // The maximum size of the buffer

    private MappedByteBuffer readBuffer;
    private long refillSize;         // The actual number of byte of the refilling buffer
    private long fileSize;           // The number of bytes of the whole file
    private long fileRemainSize;     // The number of bytes of the remaining of the file
    private long position;           // The position of the buffer pointer
    private long loadFactor;         // The number of fixed size records the buffer will refill when refillbuffer() is called
                                     // This value is non-zero only if the record is fix size

    public BinaryFileFastRecordReader(String pathname)
            throws IOException {

        this(new File(pathname), 0);
    }

    public BinaryFileFastRecordReader(File file)
            throws IOException {

        this(file, 0);
    }

    public BinaryFileFastRecordReader(String pathname, long recordSize)
            throws IOException {

        this(new File(pathname), recordSize, 50 * 1024 * 1024l);
    }

    public BinaryFileFastRecordReader(File file, long recordSize)
            throws IOException {

        this(file, recordSize, 50 * 1024 * 1024l);
    }

    public BinaryFileFastRecordReader(String pathname, long recordSize, long bufferSize)
            throws IOException {

        this(new File(pathname), recordSize, bufferSize);
    }

    public BinaryFileFastRecordReader(File file, long recordSize, long bufferSize)
            throws IOException {

//        System.out.println(bufferSize);
        this.fileInputStream = new FileInputStream(file);
        this.fileChannel = fileInputStream.getChannel();
        this.fileSize = fileChannel.size();
        this.fileRemainSize = fileSize;

        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size should be an positive number.");
        } else {
            this.bufferSize = bufferSize;
        }
        if (recordSize < 0) {
            throw new IllegalArgumentException("Record size should be an positive number.");
        } else if (recordSize > 0) {
            this.loadFactor = this.bufferSize / recordSize;
            this.refillSize = recordSize * loadFactor;
        } else {
            this.loadFactor = 0;
            if (this.bufferSize > fileSize) {
                this.refillSize = fileSize;
            } else {
                this.refillSize = this.bufferSize;
            }
        }

        this.position = 0;
        this.readBuffer = fileChannel.map(MapMode.READ_ONLY, position, refillSize);
    }

    public MappedByteBuffer getBuffer() {

        return readBuffer;
    }

    public static int REFILLED = 1;
    public static int NORMAL = 0;
    public static int EOT = -1;

    public int getBufferStatus(int recordSize)
            throws IOException {
        
        return getBufferStatus((long) recordSize);
    }

    public int getBufferStatus(long recordSize)
            throws IOException {
        
        return getBufferStatus(recordSize, 0);
    }

    public int getBufferStatus(int recordSize, int valueEOF)
            throws IOException {
        
        return getBufferStatus((long) recordSize, valueEOF);
    }

    public int getBufferStatus(long recordSize, int valueEOF)
            throws IOException {
        
        return getBufferStatus(recordSize, valueEOF, valueEOF);
    }

    public int getBufferStatus(int recordSize, int valueEOF, int valueEOT)
            throws IOException {

        return getBufferStatus((long) recordSize, valueEOF, valueEOT);
    }
        
    public int getBufferStatus(long recordSize, int valueEOF, int valueEOT)
            throws IOException {
        
        if (recordSize == valueEOF) {
            throw new EOFException("Reach the end of the file.");
            
        } else if (recordSize == valueEOT) {
            return EOT;
            
        } else if (readBuffer.remaining() < recordSize) {
            // Check if buffer remaining size is bigger than record length
            // Refill buffer and check if buffer can be refill
            if(this.refillBuffer()) {
                return REFILLED;
            } else {
                throw new EOFException("Reach the end of the file.");
            }
        }
        return NORMAL;
    }
    
    /**
     * Refill Buffer
     * @return boolean true for success, false for end of file 
     * @throws IOException
     */
    public boolean refillBuffer()
            throws IOException{

        position += refillSize;
        fileRemainSize -= refillSize;

        long remain = readBuffer.remaining();

        position -= remain;
        fileRemainSize += remain;

        if(fileRemainSize > 0) {
            if(refillSize > fileRemainSize) {
                refillSize = fileRemainSize;
            }
            readBuffer = fileChannel.map(MapMode.READ_ONLY,position, refillSize);
            return true;

        }else{
            return false;
        }
    }

    public void resetReader(){

        position = 0;
        fileRemainSize = fileSize;
        readBuffer = null;
    }

    public void close()
            throws IOException{

        fileChannel.close();
        fileInputStream.close();
    }
}
