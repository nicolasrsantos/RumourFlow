package org.textsim.textrt.preproc;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;

import org.textsim.exception.ProcessRuntimeException;
import org.textsim.textrt.preproc.tokenizer.Tokenizer;
import org.textsim.util.token.TokenFilter;

/**
 * One thread of multi-thread implementation.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
class TextrtPreprocThread
        extends Thread {
    
    private SinglethreadTextrtPreprocessor textrtPreprocess;
    private File[] listOfFile;
    private File outputDir;
        
    /**
     * Constructs the object by the given data structures.
     * <p>
     * This constructors receives the reference of the data structure from parameters. It will not create its own data
     * structure. The given {@code File} objects are used to generate header information.
     * </p>
     *
     * @param  unigramFile        the {@code File} object of the pre-processed unigram file.
     * @param  stopWordListFile   the {@code File} object of the Stop word list file.
     * @param  excepWordListFile  the {@code File} object of the exception word list file.
     * @param  unigramIDMap       a {@code TObjectIntHashMap} for unigram ID looking up.
     * @param  stopWordList       a {@code HashSet} stores stop word list.
     * @param  excepWordList      a {@code HashSet} stores exception word list.
     *
     * @throws IOException  if I/O error occurs.
     */
    protected TextrtPreprocThread(File unigramFile, File stopWordListFile, File excepWordListFile,
            TObjectIntHashMap<String> unigramIDMap, HashSet<String> stopWordList, HashSet<String> excepWordList,
            Tokenizer tokenizer, TokenFilter tokenFilter)
            throws IOException {
        
        this.textrtPreprocess = new SinglethreadTextrtPreprocessor(
                unigramFile, stopWordListFile, excepWordListFile, unigramIDMap, stopWordList, excepWordList, tokenizer, tokenFilter);
        
        this.listOfFile = null;
        this.outputDir  = null;
    }
    
    /**
     * Assign work to the object.
     *
     * @param  listOfFile  an array of {@code File} to be pre-process.
     * @param  outputDir   the pathname string of the output directory.
     */
    protected void assignWork(File[] listOfFile, File outputDir) {
        
        this.listOfFile = listOfFile;
        this.outputDir  = outputDir;
    }
    
    /**
     * Single thread run for multi-threading call.
     */
    @Override
    public void run() {
        
       if (listOfFile != null) {
            for (File file : listOfFile) {
                // Skip the hidden files and the directories
                if (file.isHidden() || file.isDirectory())
                    continue;
                
                try {
                    textrtPreprocess.preprocessImpl(file, outputDir);
                } catch (Exception e) {
                    // Handle the 
                    if (e instanceof NumberFormatException || e instanceof NoSuchElementException
                            || e instanceof IOException) {
                        deleteGeneratedFiles(file);
                        throw new ProcessRuntimeException(e);
                    } else {
                        try {
                            throw e;
                        } catch (Exception e1) {
                            throw new ProcessRuntimeException(e);
                        }
                    }
                }
            }
        }
    } 
    
    /**
     * Delete the output files produced by the processing file.
     * This method will be called when error occurs while processing.
     *
     * @param  processingFile  the File object of the processing text.
     */
    private void deleteGeneratedFiles(File processingFile) {
        
        String[] listOfSuffix = new String[] {"-doc.ic", "-sw.ic", "-num.gg", "-rej.gc"};
        for (String suffix : listOfSuffix)
            new File(outputDir + File.separator + processingFile.getName() + suffix).delete();
    }
}
