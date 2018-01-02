package org.textsim.textrt.preproc;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.textsim.exception.ProcessRuntimeException;
import org.textsim.textrt.preproc.tokenizer.Tokenizer;
import org.textsim.textrt.proc.singlethread.BinaryCorpus;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.util.token.TokenFilter;

/**
 * The multi-thread implementation of the text pre-process.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class MultithreadTextrtPreprocessor
        extends AbstractTextrtPreprocessor {

    // Record information
    private File unigramFile;
    private File stopWordListFile;
    private File excepWordListFile;
    // Processing data structure
    private TObjectIntHashMap<String> unigramIDMap;
    private HashSet<String> stopWordList;
    private HashSet<String> excepWordList;
    // Tokenizer
    Tokenizer tokenizer;
    // threading data
    private int numOfThread;
    private ArrayList<TextrtPreprocThread> threadPool;
    
    /**
     * Constructs the object by given files.
     *
     * @param  unigramPrepPathname    the pathname string of pre-processed unigram data file.
     * @param  stopwordPathname       the pathname string of stop word list file.
     * @param  exceptionWordPathname  the pathname string of exception word list file.
     * @param  tokenizer              a {@code Tokenizer}.
     * @param  tokenFilter            a {@code TokenFilter}.
     *
     * @throws IOException  if I/O error occurs.
     */
    public MultithreadTextrtPreprocessor(String unigramPrepPathname, String stopwordPathname,
            String exceptionWordPathname, Tokenizer tokenizer, TokenFilter tokenFilter) {
        
        super(unigramPrepPathname, stopwordPathname, exceptionWordPathname, tokenizer, tokenFilter);
        
        // Set the number of thread equals to the number of cores
        numOfThread = Runtime.getRuntime().availableProcessors();
        threadPool  = new ArrayList<TextrtPreprocThread>();
        
        for (int i = 0; i < this.numOfThread; i++) {
            try {
                this.threadPool.add(
                        new TextrtPreprocThread(
                                this.unigramFile,
                                this.stopWordListFile,
                                this.excepWordListFile,
                                this.unigramIDMap,
                                this.stopWordList,
                                this.excepWordList,
                                this.tokenizer,
                                this.tokenFilter));
            } catch (IOException e) {
                throw new ProcessRuntimeException(e);
            }
        }
    }
    
    /**
     * Pre-processed the text file(s) with pathname, and put the output files in the given directory.
     *
     * @param  textPathname  the pathname string of the input file or directory.
     * @param  outputDir     the pathname string of the output directory.
     *
     * @throws IOException          if I/O error occurs.
     * @throws InterruptedException if any thread has interrupted the current thread. The interrupted status of the
     *                              current thread is cleared when this exception is thrown.
     */
    @Override
    public void preprocess(File[] docs, File outputDir) {
        
        // Assign file equally to the threads
        for (int i = 0; i < this.numOfThread - 1; i++)
            this.threadPool.get(i).assignWork(
                    Arrays.copyOfRange(
                            docs,
                            i / this.numOfThread * docs.length,
                            i / this.numOfThread * docs.length),
                    outputDir);
        // The last thread get one equal share of the files and the remainder
        this.threadPool.get(this.numOfThread - 1).assignWork(
                Arrays.copyOfRange(
                        docs,
                        (this.numOfThread - 1) / this.numOfThread * docs.length,
                        docs.length),
                outputDir);

        // Start multi-threading
        for (TextrtPreprocThread thread : this.threadPool)
            thread.start();
        
        // End multi-threading
        for (TextrtPreprocThread thread : this.threadPool)
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new ProcessRuntimeException(e);
            }
    }

    @Override
    public List<TextInstance> preprocess(File[] docs) {

        // TODO Change it to be a multithreaded approach.
        ArrayList<TextInstance> list = new ArrayList<TextInstance>();
        for (File doc : docs) {
            list.add(createTextInstance(doc));
        }
        return list;
    } 

    @Override
    public List<TextInstance> createTextInstances(File[] docs) {

        return preprocess(docs);
    } 

    @Override
    public File writeBinaryCorpus(File[] docs, File outFile) {

        return new BinaryCorpus(createTextInstances(docs)).writeBinaryText(outFile);
    }

}