package org.textsim.textrt.preproc;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.textsim.exception.ProcessException;
import org.textsim.textrt.preproc.tokenizer.Tokenizer;
import org.textsim.textrt.proc.singlethread.BinaryCorpus;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.util.token.TokenFilter;

/**
 * The single-thread implementation of the text pre-process.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class SinglethreadTextrtPreprocessor
        extends AbstractTextrtPreprocessor {

//    private TextrtPreprocessorBase textrtPreprocess;  // The TextrtPreprocBase object used for text pre-process
        
    /**
     * Constructs the object by given files.
     *
     * @param unigramPrepPathname    the pathname string of pre-processed unigram data file.
     * @param stopwordPathname       the pathname string of stop word list file.
     * @param exceptionWordPathname  the pathname string of exception word list file.
     *
     * @throws ProcessException  if error occurs in the process.
     */
    public SinglethreadTextrtPreprocessor(String unigramPrepPathname, String stopwordPathname,
            String exceptionWordPathname, Tokenizer tokenizer, TokenFilter tokenFilter) {
        
        super(unigramPrepPathname, stopwordPathname, exceptionWordPathname, tokenizer, tokenFilter);
    }

    public SinglethreadTextrtPreprocessor(File unigramFile, File stopWordListFile, File excepWordListFile,
            TObjectIntHashMap<String> unigramIDMap, HashSet<String> stopWordList, HashSet<String> excepWordList,
            Tokenizer tokenizer, TokenFilter tokenFilter) {

        super(unigramFile, stopWordListFile, excepWordListFile, unigramIDMap, stopWordList, excepWordList, tokenizer,
                tokenFilter);
    }
    
    public SinglethreadTextrtPreprocessor (TObjectIntHashMap<String> unigram, String stopWords, String rejWords,
            Tokenizer tokenizer, TokenFilter tokenFilter) {
    	super(unigram, stopWords, rejWords, tokenizer, tokenFilter);
    }
    
    /**
     * Pre-processed the text file(s) with pathname, and put the output files in the given directory.
     *
     * @param  textPathname  the pathname string of the input file or directory.
     * @param  outputDir     the pathname string of the output directory.
     *
     * @throws ProcessException  if error occurs in the process.
     */
    @Override
    public void preprocess(File[] docs, File outputDir) {
        
        for (File doc : docs) {
            preprocessImpl(doc, outputDir);
        }
    }

    @Override
    public List<TextInstance> preprocess(File[] docs) {

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
    
    public TextInstance createSingleTextInstance(File doc) {

       return createTextInstance(doc);
    }
    
    public TextInstance createSingleTextInstance(String text) {

        return createTextInstance(text);
    }

    @Override
    public File writeBinaryCorpus(File[] docs, File outFile) {

        return new BinaryCorpus(createTextInstances(docs)).writeBinaryText(outFile);
    }
    
    /**
     * Get unigram map.
     */
    @Deprecated
    public TObjectIntHashMap<String> getUnigramMap(){
        return this.unigramIDMap;
    }
}