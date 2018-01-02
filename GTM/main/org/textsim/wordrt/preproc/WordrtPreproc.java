package org.textsim.wordrt.preproc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.textsim.exception.ProcessException;
import org.textsim.util.FileUtil;
import org.textsim.util.IOUtil;
import org.textsim.util.Trigram;
import org.textsim.util.Unigram;

/**
 * {@code WordrtPreproc} is the entry for creating pre-processed data.
 * <p>
 * It provides generation methods for both text and binary files.
 * </p><p>
 * For text data generation, procedures are executed in order:
 * <ul>
 * <li>STEP 1: <tt>UnigramPreprocess</tt> - Pre-process unigram
 * <li>STEP 2: <tt>TrigramCountSummation</tt> - Combine trigram count
 * <li>STEP 3: <tt>TrigramExternalSort</tt> - Sort trigram
 * <li>STEP 4: <tt>WordrtComputation</tt> - compute word relatedness
 * </ul>
 * For binary data generation, one extra procedure is needed:
 * <ul>
 * <li>STEP 5: <tt>BinaryConversion</tt> - convert unigram and trigram data into binary version
 * </ul>
 * </p>
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 * 
 * @see UnigramPreprocess
 * @see TrigramCountSummation
 * @see TrigramExternalSort
 * @see WordrtComputation
 * @see UnigramBinaryConversion
 * @see TrigramBinaryConvertion
 */
public class WordrtPreproc {

    /**
     * The typing value of binary pre-process.
     */
    public static final int BINARY = 0;

    /**
     * The typing value of text pre-process.
     */
    public static final int TEXT = 1;

    /**
     * Pre-processes the unigram and trigram data.
     * 
     * @param  unigramDir  The pathname {@code String} of the parent directory of unigram data.
     *                     This parameter is requisite and can not be null.
     * @param  trigramDir  The pathname {@code String} of the parent directory of trigram data.
     *                     This parameter is requisite and can not be null.
     * @param  outputDir   The pathname {@code String} of the parent directory of storing temporary pre-process data.
     *                     If the directory does not exists, the program will create it before pre-processing. If this
     *                     parameter receives null value, a new folder name {@code out_} with current data and time will be
     *                     used while creating the folder.
     * @param  tempDir     The pathname {@code String} of the parent directory of storing temporary pre-process data.
     *                     If the directory does not exists, the program will create it before pre-processing. This
     *                     directory will automatically be deleted after pre-process. If this parameter receives null
     *                     value, a new folder name {@code temp_} with current data and time will be created for this usage.
     * @param  regex       The {@code String} of regular expression used for filtering the gram.
     *                     The gram which does not match this regular expression will be ignored and will not be written in
     *                     the output data. If this parameter receives null value, a regular expression {@code .*} will be
     *                     applied in processing.
     *
     * @throws Exception 
     */
    public static void preprocessCorpus(int preprocType, String unigramDir, String trigramDir, String outputDir,
            String tempDir, String name, String regex)
            throws Exception {

        unigramDir = new File(unigramDir).getAbsolutePath();
        trigramDir = new File(trigramDir).getAbsolutePath();
        // Naming with current time could avoid name duplication
        String currentTime = IOUtil.getCurrentTime(false);
        if (outputDir == null) {
            outputDir = "." + File.separator + "corpus_" + currentTime + File.separator;
        } else {
            outputDir = new File(outputDir).getAbsolutePath();
        }
        if (tempDir == null) {
            tempDir = "." + File.separator + "temp_" + currentTime + File.separator;
        } else {
            tempDir = new File(tempDir).getAbsolutePath();
        }
        if (name == null) {
            name = "corpus";
        }
        if (regex == null) {
            regex = ".*";
        }
        
        try {
            System.out.println();
            System.out.println("CORPUS PREPROCESS");
            System.out.println("------------------------------------------------------------------------------");
            
            long initTime, endTime;
            initTime = System.currentTimeMillis();
            
            if (!new File(tempDir).exists())
                FileUtils.forceMkdir(new File(tempDir));
            if (!new File(outputDir).exists())
                FileUtils.forceMkdir(new File(outputDir));
            
            if (preprocType == TEXT) {
                textProcess(unigramDir, trigramDir, tempDir, regex);
                textCleanUp(tempDir, outputDir, name);
            } else if (preprocType == BINARY) {
                binaryProcess(unigramDir, trigramDir, tempDir, regex);
                binaryCleanUp(tempDir, outputDir, name);
            } else {
                new ProcessException("Invalid pre-process type is given.");
            }
            
            FileUtils.forceDelete(new File(tempDir));

            endTime = System.currentTimeMillis();
            
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("PREPROCESS Done! Total time taken: " + ((endTime - initTime) / 1000.0) + " second.");
            System.out.println();

        } catch (Exception e) {
            System.out.println();
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("PREPROCESS fail");
            System.out.println();
            throw e;
        }
    }

    /**
     * Steps of generating text unigram and trigram.
     * 
     * @param  unigramDir  The pathname of the parent directory of unigram raw data
     * @param  trigramDir  The pathname of the parent directory of trigram raw data
     * @param  tempDir     The pathname of the process temporary directory
     * @param  regex       The regular expression used to filting the data
     *
     * @throws ProcessException
     * @throws IOException
     */
    private static void textProcess(String unigramDir, String trigramDir, String tempDir, String regex)
            throws ProcessException, IOException {
        
        long startTime, endTime;

        /* STEP1 Unigram Pre-process */
        startTime = System.currentTimeMillis();
        System.out.print("STEP 1: Unigram Preprocess..");
        UnigramPreprocess.unigramGen(unigramDir, tempDir, regex);
        endTime = System.currentTimeMillis();
        System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");

        /* STEP2 Trigram Count Summation */
        startTime = endTime;
        System.out.print("STEP 2: Trigram Count Summation..");
        TrigramCountSummation.TrigramGen(trigramDir, tempDir, regex);
        endTime = System.currentTimeMillis();
        System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");

        /* STEP3 External sort */
        startTime = endTime;
        System.out.print("STEP 3 : External Sorting..");
        TrigramExternalSort.sortFile(tempDir);
        endTime = System.currentTimeMillis();
        System.out.println("DONE!\n\tTime Taken: " + ((endTime - startTime) / 1000.0) + " second.");

        /* STEP4 Word Relatedness Calculation */
        startTime = endTime;
        System.out.print("STEP 4: Word Relateness Calculation..");
        WordrtComputation.processT(tempDir);
        endTime = System.currentTimeMillis();
        System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");
    }
    
    /**
     * Steps of generating text unigram and trigram.
     * 
     * @param  unigramDir  The pathname of the parent directory of unigram raw data.
     * @param  trigramDir  The pathname of the parent directory of trigram raw data.
     * @param  tempDir     The pathname of the process temporary directory.
     * @param  regex       The regular expression used to filting the data.
     *
     * @throws ProcessException
     * @throws IOException
     */
    public static void binaryProcess(String unigramDir, String trigramDir, String tempDir, String regex)
            throws ProcessException, IOException {
        
        long startTime, endTime;

        // Pipe the text generation
        textProcess(unigramDir, trigramDir, tempDir, regex);

        /* STEP5 Binary Conversion */
        startTime = System.currentTimeMillis();
        System.out.print("STEP 5: Binary Conversion...");
        UnigramBinaryConversion.process(tempDir);
        TrigramBinaryConvertion.process(tempDir);
        endTime = System.currentTimeMillis();
        System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");
    }
    
    /**
     * Moving the final output into output folder and cleaning up the temporary files.
     * 
     * @param  tempDir    The pathname of the process temporary directory.
     * @param  outputDir  The pathname of the output directory.
     * @param  name       The name of the output files.
     *
     * @throws IOException
     */
    public static void textCleanUp(String tempDir, String outputDir, String name)
            throws IOException {

        FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.DATA_SUFFIX).renameTo(
                new File(outputDir, name + '.' + Unigram.TEXT_DATA_SUFFIX));
        FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.COUNT_SUFFIX).renameTo(
                new File(outputDir, name + '.' + Unigram.TEXT_COUNT_SUFFIX));
        FileUtil.getFileInDirectoryWithSuffix(tempDir, WordrtComputation.DATA_SUFFIX).renameTo(
                new File(outputDir, name + '.' + Trigram.TEXT_DATA_SUFFIX));
        FileUtil.getFileInDirectoryWithSuffix(tempDir, WordrtComputation.COUNT_SUFFIX).renameTo(
                new File(outputDir, name + '.' + Trigram.TEXT_COUNT_SUFFIX));
    }

    /**
     * Moving the final output into output folder and cleaning up the temporary files.
     * 
     * @param  tempDir    The pathname of the process temporary directory.
     * @param  outputDir  The pathname of the output directory.
     * @param  name       The name of the output files.
     *
     * @throws IOException
     */
    public static void binaryCleanUp(String tempDir, String outputDir, String name)
            throws IOException {

        FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramBinaryConversion.SUFFIX).renameTo(
                new File(outputDir, name + '.' + Unigram.BINARY_SUFFIX));
        FileUtil.getFileInDirectoryWithSuffix(tempDir, TrigramBinaryConvertion.SUFFIX).renameTo(
                new File(outputDir, name + '.' + Trigram.BINARY_SUFFIX));
    }
}
