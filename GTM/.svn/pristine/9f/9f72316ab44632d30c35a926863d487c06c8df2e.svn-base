package org.textsim.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.textsim.exception.ProcessException;
import org.textsim.exception.ProcessRuntimeException;
import org.textsim.wordrt.preproc.WordrtPreproc;

import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * Provide method for loading pre-processed unigram data to memory.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class Unigram {

    public static String TEXT_DATA_SUFFIX = "ud";
    public static String TEXT_UNACCEPTED_DATA_SUFFIX = "un";
    public static String TEXT_COUNT_SUFFIX = "uc";
    public static String BINARY_SUFFIX = "uni";
    private static int EOF = 0;

    public static Data read(int preprocType, File... files)
            throws ProcessException {
        
        try {
            if (preprocType == WordrtPreproc.TEXT) {
                return readText(files);
            } else if (preprocType == WordrtPreproc.BINARY) {
                return readBinary(files);
            } else {
                throw new ProcessRuntimeException("Invalid preprocess type is given.");
            }
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }
    
    public static TObjectIntHashMap<String> readIDMap(int preprocType, File... files)
            throws ProcessException {
        
        try {
            if (preprocType == WordrtPreproc.BINARY) {
                return readIDMapBinary(files);

            } else if (preprocType == WordrtPreproc.TEXT) {
                return readIDMapText(files);
                
            } else {
                throw new ProcessRuntimeException("Invalid preprocess type is given.");
            }
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }
    
    public static long[] readCounts(int preprocType, File... files)
            throws ProcessException {
        
        try {
            if (preprocType == WordrtPreproc.BINARY) {
                return readCountBinary(files);

            } else if (preprocType == WordrtPreproc.TEXT) {
                return readCountText(files);
                
            } else {
                throw new ProcessRuntimeException("Invalid preprocess type is given.");
            }
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }
    
    public static int readCountFile(int preprocType, File file)
        throws ProcessException {
        
        try {
            if (preprocType == WordrtPreproc.BINARY) {
                return readCountFileBinary(file);
    
            } else if (preprocType == WordrtPreproc.TEXT) {
                return readCountFileText(file);
                
            } else {
                throw new ProcessRuntimeException("Invalid preprocess type is given.");
            }
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }

    /**
     * Loading unigram data to memory for ID and count look up.
     *
     * @param  unigramFile       the {@code File} object of the pre-processed unigram data file (<tt>-uni.gic</tt>).
     * @param  unigramCountFile  the {@code File} object of the pre-processed unigram count file (<tt>-uni.c</tt>).
     *
     * @return A {@code Data} contains a {@code TObjectIntHashMap<String>} object and a {@code Long[]} object.
     * 
     * @throws IOException  if I/O error occurs.
     */
    private static Data readText(File... files)
            throws IOException {

        // Read unigram count file
        int uCount;
        if (files.length == 2) {
            uCount = readCountFileText(files[1]);
        } else if (files.length == 1) {
            uCount = 0;
        } else {
            throw new ProcessRuntimeException("Invalid number of unigram files is given.");
        }

        // Initialize the HashMap
        TObjectIntHashMap<String> unigramIDMap;
        Longs unigramCount;
        if (uCount != 0) {
            unigramIDMap = new TObjectIntHashMap<String>((int)(uCount * 1.25), 0.8f);
            unigramCount = new Longs(uCount + 1);
        } else {
            unigramIDMap = new TObjectIntHashMap<String>(20000000,(float)0.8);
            unigramCount = new Longs();
        }

        // Read unigram corpus file
        BufferedReader unigram = null;
        try {
            unigram = IOUtil.getBufferedFileReader(files[0], "uni");
//            unigram = new BufferedReader(new FileReader(files[0]));

            String inputLine  = null;
            String gram = null;
            int id;
            long count;
            while ((inputLine = unigram.readLine()) != null) {
                StringTokenizer line = new StringTokenizer(inputLine, "\t");
                gram = line.nextToken();
                id = Integer.parseInt(line.nextToken());
                count = Long.parseLong(line.nextToken());
                unigramIDMap.put(gram, id);
                unigramCount.add(id, count);
            }
        } finally {
            unigram.close();
        }

        return new Data(unigramIDMap, unigramCount.toLongArray());
    }

   private static Data readBinary(File... files)
            throws IOException {

        if (files.length == 1) {
            return read(files[0]);
        } else {
            throw new ProcessRuntimeException("Invalid number of unigram files is given.");
        }
    }

    private static Data read(File file)
            throws IOException {
        
        TObjectIntHashMap<String> uIDMap = null;
        long[] uCount = null;
        
        BinaryFileFastRecordReader bffrr = null;
        try {
            bffrr = new BinaryFileFastRecordReader(file);

            // TODO Add header to file
            
            // Initialize data structure
            int count = readUnigramTotalCount(bffrr);
            uIDMap = new TObjectIntHashMap<String>((int)(count * 1.25), 0.8f);
            uCount = new long[count + 1];
            
            // Read data
            while(true) {
                readAndStoreRecord(bffrr, uIDMap, uCount);
            }
            
        } catch (EOFException e) {

        } finally {
            if (bffrr != null) {
                bffrr.close();
            }
        }
        return new Unigram.Data(uIDMap, uCount);
    }
    
    private static void readAndStoreRecord(BinaryFileFastRecordReader bffrr, TObjectIntHashMap<String> uIDMap, long[] uCount)
            throws IOException {
        
        MappedByteBuffer buffer = bffrr.getBuffer();
        int recordSize = buffer.getInt();
        if (bffrr.getBufferStatus(recordSize, EOF) == BinaryFileFastRecordReader.REFILLED) {
            buffer = bffrr.getBuffer();
        }
        int gramSize = recordSize - 16;
        byte[] gramBytes = new byte[gramSize]; 
        buffer.get(gramBytes);
        String gram = StringUtils.toEncodedString(gramBytes, null);
        int id = buffer.getInt();
        long count = buffer.getLong();
        uIDMap.put(gram, id);
        uCount[id] = count;
    }
    
    private static int readUnigramTotalCount(BinaryFileFastRecordReader bffrr)
            throws IOException {
        
        return readInt(bffrr);
    }
    
    private static int readInt(BinaryFileFastRecordReader bffrr)
            throws IOException {
        
        MappedByteBuffer buffer = bffrr.getBuffer();
        int value = buffer.getInt();
        if (bffrr.getBufferStatus(12, EOF) == BinaryFileFastRecordReader.REFILLED) {
            buffer = bffrr.getBuffer();
        }
        return value;
    }

    /**
     * Loading unigram data to memory for ID look up.
     *
     * @param  unigramFile  the {@code File} object of the pre-processed unigram data file (<tt>-uni.gic</tt>).
     *
     * @return A {@code TObjectIntHashMap<String>}.
     * 
     * @throws IOException  if I/O error occurs.
     */
    private static TObjectIntHashMap<String> readIDMapText(File... files)
            throws IOException {

        // Read unigram count file
        int uCount;
        if (files.length == 2) {
            uCount = readCountFileText(files[1]);
        } else if (files.length == 1) {
            uCount = 0;
        } else {
            throw new ProcessRuntimeException("Invalid number of unigram files is given.");
        }

        // Initialize the HashMap
        TObjectIntHashMap<String> unigramIDMap;
        if (uCount != 0) {
            unigramIDMap = new TObjectIntHashMap<String>((int)(uCount * 1.25) , 0.8f);
        } else {
            unigramIDMap = new TObjectIntHashMap<String>(20000000,(float)0.8);
        }

        // Read unigram corpus file
        BufferedReader unigram = null;
        try {
            unigram = IOUtil.getBufferedFileReader(files[0], "uni");

            String inputLine  = null;
            while ((inputLine = unigram.readLine()) != null) {
                StringTokenizer line = new StringTokenizer(inputLine, "\t");
                unigramIDMap.put(line.nextToken(), Integer.parseInt(line.nextToken()));
            }
        } finally {
            unigram.close();
        }

        return unigramIDMap;
    }

    private static TObjectIntHashMap<String> readIDMapBinary(File... files)
            throws IOException {

        return readBinary(files).unigramIDMap;
    }
    
    private static long[] readCountText(File... files)
            throws IOException {
        
        // Read unigram count file
        int uCount;
        if (files.length == 2) {
            uCount = readCountFileText(files[1]);
        } else if (files.length == 1) {
            uCount = 0;
        } else {
            throw new ProcessRuntimeException("Invalid number of unigram files is given.");
        }

        // Initialize the ArrayList<Long>
        Longs unigramCount;
        if (uCount != 0) {
            unigramCount = new Longs(uCount + 1);
        } else {
            unigramCount = new Longs();
        }

        // Read unigram corpus file
        BufferedReader unigram = null;
        try {
            unigram = IOUtil.getBufferedFileReader(files[0], "uni");

            String inputLine  = null;
            while ((inputLine = unigram.readLine()) != null) {
                StringTokenizer line = new StringTokenizer(inputLine, "\t");
                line.nextToken();
                unigramCount.add(Integer.parseInt(line.nextToken()), Long.parseLong(line.nextToken()));
            }
        } finally {
            if (unigram != null)
                unigram.close();
        }

        return unigramCount.toLongArray();
    }
    
    private static long[] readCountBinary(File... files)
            throws IOException {
        
        return readBinary(files).unigramCount;
    }

    private static int readCountFileText(File file)
            throws IOException {

        int count;

        BufferedReader reader = null;
        try {
            reader = IOUtil.getBufferedFileReader(file, "uni");
            count = Integer.parseInt(reader.readLine());
        } finally {
            reader.close();
        }

        return count;
    }
    
    public static long readCMax(File unigramCount)
            throws IOException {

        long cMax;

        BufferedReader reader = null;
        try {
            reader = IOUtil.getBufferedFileReader(unigramCount, "uni");
            reader.readLine();
            cMax = Long.parseLong(reader.readLine());
        } finally {
            reader.close();
        }

        return cMax;
    }

    private static int readCountFileBinary(File file)
            throws IOException {
        
        int count;

        BinaryFileFastRecordReader reader = null;
        try {
            reader = new BinaryFileFastRecordReader(file);

            MappedByteBuffer buffer = reader.getBuffer();
    
            // Read header
            new Header().read(reader, 0);
            
            // read count information
            count = buffer.getInt();

        } finally {
            reader.close();
        }

        return count;
    }
    
    
    @SuppressWarnings("serial")
    static private class Longs
            extends ArrayList<Long> {
        
        private Longs() {
            
            super();
        }

        private Longs(int initialCapacity) {
            
            super(initialCapacity);
        }
        
        
        // Make sure that insertion will not throw Exception.
        @Override
        public void add(int index, Long element) {
            
            ensureRangeForRandomAdd(index);
            super.add(index, element);
        }

        public long[] toLongArray() {
            
            trimToSize();
            
            long[] a = new long[size()];
            for (int i = 0; i < a.length; i++) {
                a[i] = get(i).longValue();
            }
    
            return a;
        }
        
        private void ensureRangeForRandomAdd(int index) {
            
            if (index < 0) {
                throw new IndexOutOfBoundsException();
            } else if (index > size()) {
                while (index != size()) {
                    add(0l);
                }
            }
        }
    }
    
    /**
     * Provide output data template.
     */
    public static final class Data {
    
        public TObjectIntHashMap<String> unigramIDMap;
        public long[] unigramCount;
        
        public Data(TObjectIntHashMap<String> unigramIDMap, long[] unigramCount) {
            
            this.unigramIDMap = unigramIDMap;
            this.unigramCount = unigramCount;
        }
        
        public TObjectIntHashMap<String> getIDMap() {
            
            return unigramIDMap;
        }
        
        public long[] getCount() {
            
            return unigramCount;
        }
    }
} 
