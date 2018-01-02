package org.textsim.textrt.preproc;

import gnu.trove.map.hash.TCharObjectHashMap;

import java.io.File;
import java.util.ArrayList;

import org.textsim.exception.ProcessException;
import org.textsim.textrt.preproc.tokenizer.PennTreeBankTokenizer;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.util.FileUtil;
import org.textsim.util.Unigram;
import org.textsim.util.token.DefaultTokenFilter;
import org.textsim.util.token.TokenFilter;

/**
 * Provide command line interface for the text pre-process.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class TextrtPreproc {

    public static final int singleThreded = 0;
    public static final int multiThreded = 0;

    /**
     * Run the single-thread implementation of the text pre-process.
     *
     * @param  textPathname           the pathname string of the input text file.
     * @param  corpusDir              the pathname string of the pre-processed corpus directory.
     * @param  outFile                the pathname string of the output directory.
     * @param  stopWordPathname       the pathname string of the stop word list file.
     * @param  exceptionWordPathname  the pathname string of the exception word list file.
     * @param  tokenFilters           the tokenFilter options.
     *
     * @throws ProcessException  if error occurs in the process.
     */
    public static void runSinglethreaded(String textPathname, String corpusDir, String outFile, String stopWordPathname,
            String exceptionWordPathname, String tokenFilters)
            throws ProcessException {

        process(singleThreded, textPathname, corpusDir, outFile, stopWordPathname, exceptionWordPathname, tokenFilters);

    }

    /**
     * Run the multi-thread implementation of the text pre-process.
     *
     * @param  textPathname           the pathname string of the input text file.
     * @param  corpusDir              the pathname string of the pre-processed corpus directory.
     * @param  outFile                the pathname string of the output directory.
     * @param  stopWordPathname       the pathname string of the stop word list file.
     * @param  exceptionWordPathname  the pathname string of the exception word list file.
     * @param  tokenFilters           the tokenFilter options.
     *
     * @throws ProcessException  if error occurs in the process.
     */
    public static void runMultithreaded(String textPathname, String corpusDir, String outFile, String stopWordPathname,
            String exceptionWordPathname, String tokenFilters)
            throws ProcessException {

        process(multiThreded, textPathname, corpusDir, outFile, stopWordPathname, exceptionWordPathname, tokenFilters);

    }
    
    private static TCharObjectHashMap<String> tfMap = new TCharObjectHashMap<String>();
    static {
        tfMap.put('D', "org.textsim.util.token.DefaultTokenFilter");
        tfMap.put('T', "org.textsim.util.token.StanfordTokenFilter");
    }

    /**
     * Create {@code TokenFilter} by user input.
     * <p>
     * Different characters represents different {@code TokenFilter}s. The filting pipe is constructed in the order of
     * characters:
     * <ul>
     * <li> <tt>D</tt>: {@link DefaultTokenFilter}
     * <li> <tt>T</tt>: {@link StanfordTokenFilter}
     * </ul>
     * 
     * @param  para  The user input string.
     * 
     * @return A piped {@code TokenFilter}.
     */
    private static TokenFilter getTokenFilter(String para)
            throws ProcessException {

        DefaultTokenFilter tf = new DefaultTokenFilter();
        for (int i = 0; i < para.length(); i++) {
            char identifier = para.charAt(i);
            try {
                tf.append((TokenFilter)Class.forName(tfMap.get(identifier)).newInstance());
            } catch (InstantiationException e) {
                throw new ProcessException("'" + identifier + "' is not a pre-defined token filter.");
            } catch (IllegalAccessException e) {
                throw new ProcessException("'" + identifier + "' is not a pre-defined token filter.");
            } catch (ClassNotFoundException e) {
                throw new ProcessException("'" + identifier + "' is not a pre-defined token filter.");
            }
        }
        return tf;
    }

//    /**
//     * Run the text pre-process.
//     *
//     * @param  type                   the string indicate the implementation type, the possible value are
//     *                                {@code single-thread} and {@code multi-thread}.
//     * @param  textPathname           the pathname string of the input text file.
//     * @param  corpusDir              the pathname string of the pre-processed corpus directory.
//     * @param  outFile              the pathname string of the output directory.
//     * @param  stopWordPathname       the pathname string of the stop word list file.
//     * @param  exceptionWordPathname  the pathname string of the exception word list file.
//     * 
//     * @throws ProcessException  if error occurs in the process.
//     */
//    public static void process(int type, String textPathname, String corpusDir, String outFile,
//            String stopWordPathname, String exceptionWordPathname)
//            throws ProcessException {
//
//        String currentTime = IOUtil.getCurrentTime(false);
//        if (outFile == null) {
//            outFile = "corpus_" + currentTime + File.separator;
//        } else {
//            outFile = new File(outFile).getAbsolutePath();
//        }
//            
//        try {
//            System.out.println();
//            System.out.println("TEXT PREPROCESS");
//            System.out.println("------------------------------------------------------------------------------");
//            
//            long initTime, startTime, endTime;
//            initTime = System.currentTimeMillis();
//            
//            // TODO Add wordrt pre-process type check
//            String unigramPrepPathname;
//            if (!new File(corpusDir).isFile()) {
//                unigramPrepPathname = FileUtil.getFileInDirectoryWithSuffix(corpusDir, Unigram.BINARY_SUFFIX).getAbsolutePath();
//            } else {
//                unigramPrepPathname = corpusDir;
//            }
//
//            TextrtPreprocessor preproc = null;
//            
//            startTime = initTime;
//            System.out.print("Loading unigram data & lists..");
//            if (type == singleThreded) {
//                preproc = new SinglethreadTextrtPreprocessor(
//                        unigramPrepPathname, stopWordPathname, exceptionWordPathname, new PennTreeBankTokenizer());
//            } else if (type == multiThreded){
//                preproc = new MultithreadTextrtPreprocessor(
//                        unigramPrepPathname, stopWordPathname, exceptionWordPathname, new PennTreeBankTokenizer());
//            }
//            endTime = System.currentTimeMillis();
//            System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");
//            
//            startTime = endTime;
//            System.out.print("Preprocessing file..");
//            File path = new File(textPathname);
//            File[] inFile;
//            if (path.isDirectory()) {
//                inFile = path.listFiles();
//                ArrayList<File> list = new ArrayList<File>();
//                for (File file : inFile) {
//                    if (!file.isHidden()) {
//                        list.add(file);
//                    }
//                }
//                inFile = list.toArray(new File[list.size()]);
//            } else {
//                inFile = new File[]{path};
//            }
//            preproc.writeBinaryCorpus(inFile, new File(outFile));
//            
//            endTime = System.currentTimeMillis();
//            System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");
//            
//            //Write the contents of idName to a file for use in the processing stage
//            String outFilePath = outFile.substring(0, outFile.lastIndexOf("\\"));
//            long startWrite, endWrite = 0;
//            startWrite = System.currentTimeMillis();
//            TextInstance.writeIdNametoFile(outFilePath, textPathname.substring(textPathname.lastIndexOf("/")));
//            endWrite = System.currentTimeMillis();
//            System.out.println("Time taken to write idName to file: " + (endWrite-startWrite));
//        
//            System.out.println("------------------------------------------------------------------------------");
//            System.out.println("PREPROCESS Done! Total time taken: " + ((endTime - initTime) / 1000.0) + " second.");
//            System.out.println();
//        } catch (Exception e) {
//            System.out.println();
//            System.out.println("------------------------------------------------------------------------------");
//            System.out.println("PREPROCESS fail");
//            System.out.println();
//            throw new ProcessException(e);
//        }
//    }//process
    
    
    
    /**
     * Run the text pre-process.
     *
     * @param  type                   the string indicate the implementation type, the possible value are
     *                                {@code single-thread} and {@code multi-thread}.
     * @param  textPathname           the path of the input text files.
     * @param  corpusDir              the pathname string of the pre-processed corpus directory.
     * @param  outDir                 the output directory.
     * @param  stopWordPathname       the pathname string of the stop word list file.
     * @param  exceptionWordPathname  the pathname string of the exception word list file.
     * @param  tokenFilters           the tokenFilter options.
     * 
     * @throws ProcessException  if error occurs in the process.
     */
    public static void process(int type, String textPathname, String corpusDir, String outDir,
            String stopWordPathname, String exceptionWordPathname, String tokenFilters)
            throws ProcessException {

        File outFile;
        if (outDir == null) {
        	outDir = "./";
        }//if
        outFile = new File(outDir+"/output.out");    
        
        try {
            System.out.println();
            System.out.println("TEXT PREPROCESS");
            System.out.println("------------------------------------------------------------------------------");
            
            long initTime, startTime, endTime;
            initTime = System.currentTimeMillis();
            
            // TODO Add wordrt pre-process type check
            String unigramPrepPathname;
            if (!new File(corpusDir).isFile()) {
                unigramPrepPathname = FileUtil.getFileInDirectoryWithSuffix(corpusDir, Unigram.BINARY_SUFFIX).getAbsolutePath();
            } else {
                unigramPrepPathname = corpusDir;
            }

            TextrtPreprocessor preproc = null;
            
            startTime = initTime;
            System.out.print("Loading unigram data & lists..");
            if (type == singleThreded) {
                preproc = new SinglethreadTextrtPreprocessor(
                        unigramPrepPathname, stopWordPathname, exceptionWordPathname, new PennTreeBankTokenizer(), getTokenFilter(tokenFilters));
            } else if (type == multiThreded){
                preproc = new MultithreadTextrtPreprocessor(
                        unigramPrepPathname, stopWordPathname, exceptionWordPathname, new PennTreeBankTokenizer(), getTokenFilter(tokenFilters));
            }
            endTime = System.currentTimeMillis();
            System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");
            
            startTime = endTime;
            System.out.print("Preprocessing file..");
            File path = new File(textPathname);
            File[] inFile;
            if (path.isDirectory()) {
                inFile = path.listFiles();
                ArrayList<File> list = new ArrayList<File>();
                for (File file : inFile) {
                    if (!file.isHidden()) {
                        list.add(file);
                    }
                }
                inFile = list.toArray(new File[list.size()]);
            } else {
                inFile = new File[]{path};
            }
            preproc.writeBinaryCorpus(inFile, outFile);
            
            endTime = System.currentTimeMillis();
            System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");
            
            //Write the contents of idName to a file for use in the processing stage
            //String outFilePath = outFile.getAbsolutePath().substring(0, outFile.getAbsolutePath().lastIndexOf("\\"));
            long startWrite, endWrite = 0;
            startWrite = System.currentTimeMillis();
            TextInstance.writeIdNametoFile(outDir, textPathname.substring(textPathname.lastIndexOf("/")));
            endWrite = System.currentTimeMillis();
            System.out.println("Time taken to write idName to file: " + (endWrite-startWrite));
        
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("PREPROCESS Done! Total time taken: " + ((endTime - initTime) / 1000.0) + " second.");
            System.out.println();
        } catch (Exception e) {
            System.out.println();
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("PREPROCESS fail");
            System.out.println();
            throw new ProcessException(e);
        }
    }//process
}
