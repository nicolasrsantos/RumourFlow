package org.textsim.wordrt.preproc;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.textsim.exception.ProcessException;
import org.textsim.util.BinaryFileBufferedWriter;
import org.textsim.util.FileUtil;
import org.textsim.util.IOUtil;
import org.textsim.util.Unigram;
import org.textsim.wordrt.preproc.WordrtPreproc;

/**
 * This class defines the pre-processing step which convert the unigram text file to be binary.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class UnigramBinaryConversion {

    /**
     * The suffix {@code String} of the output file.
     */
    public static final String SUFFIX = "ubc";

    /**
     * {@code UnigramBinaryConversion} reads the output of {@code UnigramPreprocess} in the directory, convert its data
     * into binary version and output to the same directory.
     * 
     * @param tempDir  The pathname {@code String} of the processing temporate directory.
     *
     * @throws ProcessException
     */
    public static void process(String tempDir)
            throws ProcessException {
        
        try {
            // Find unigram files in the directory
            File unigramDataFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.DATA_SUFFIX);
            File unigramCountFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.COUNT_SUFFIX);
            String name = FileUtil.getFilenameWithoutSuffix(unigramDataFile.getAbsolutePath(), UnigramPreprocess.DATA_SUFFIX);
            File unigramBinary = new File(tempDir, name + '.' + SUFFIX);
            
            // Convert to binary
            convertToBinary(unigramDataFile, unigramCountFile, unigramBinary);

        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }

    /**
     * Convert the pre-processed unigram text file to binary file.
     * 
     * @param textDataFile   The {@code File} of the pre-processed unigram count.
     * @param textCountFile  The {@code File} of the pre-processed unigram count.
     * @param binFile        The {@code File} of the output binary file.
     *
     * @throws IOException
     * @throws ProcessException
     */
    private static void convertToBinary(File textDataFile, File textCountFile, File binFile)
            throws IOException, ProcessException {
        
        BinaryFileBufferedWriter bfbw = null;
        try {
            bfbw = new BinaryFileBufferedWriter(binFile);
            // TODO Add header
            writeUnigramTotalCount(bfbw, textCountFile);
            writeUnigramData(bfbw, textDataFile);
            bfbw.writeInt(0);
        } finally {
            if (bfbw != null) {
                bfbw.close();
            }
        }
    }

    /**
     * Reads the pre-processed unigram text count file and writes into the output file.
     * 
     * @param dos           The {@code DataOutputStream} of the output file.
     * @param textCountFile  The {@code File} of the pre-processed unigram count.
     *
     * @throws IOException
     * @throws ProcessException
     */
    private static void writeUnigramTotalCount(DataOutputStream dos, File textCountFile)
            throws IOException, ProcessException {
        
        int size = Unigram.readCountFile(WordrtPreproc.TEXT, textCountFile);
        dos.writeInt(size);
    }

    /**
     * Reads the pre-processed unigram text data file and writes into the output file.
     * 
     * @param dos           The {@code DataOutputStream} of the output file.
     * @param textDataFile  The {@code File} of the pre-processed unigram data.
     *
     * @throws IOException
     * @throws ProcessException
     */
    private static void writeUnigramData(DataOutputStream dos, File textDataFile)
            throws IOException, ProcessException {

        BufferedReader textData = null;
        try {
            textData = IOUtil.getBufferedFileReader(textDataFile, "uni");
            String line = null;
            while ((line = textData.readLine()) != null) {
                convertAndWriteRecord(dos, line);
            }
        } finally {
            if (textData != null) {
                textData.close();
            }
        }
    }
    
    /**
     * Convert the gram record form the input line to binary and write it to the output file.
     * 
     * @param dos   The {@code DataOutputStream} of the output file.
     * @param line  The {@code String} of the output of input file contains the gram record.
     *
     * @throws IOException
     */
    private static void convertAndWriteRecord(DataOutputStream dos, String line)
            throws IOException {
        
        StringTokenizer stk = new StringTokenizer(line, "\t");
        String gram = stk.nextToken();
        int id = Integer.parseInt(stk.nextToken());
        long count = Long.parseLong(stk.nextToken());
        writeRecord(dos, gram, id, count);
    }
     
    /**
     * Write the gram record to the output file.
     * 
     * @param dos    The {@code DataOutputStream} of the output file.
     * @param gram   The {@code String} of the gram
     * @param id     The {@code int} value of the gram id
     * @param count  The {@code long} value of the number of the gram occurance.
     *
     * @throws IOException
     */
    private static void writeRecord(DataOutputStream dos, String gram, int id, long count)
            throws IOException {

        byte[] gramBytes = gram.getBytes();
        int recordSize = gramBytes.length + 16;
        dos.writeInt(recordSize);
        dos.write(gramBytes);
        dos.writeInt(id);
        dos.writeLong(count);
    }
}
