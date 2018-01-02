package org.textsim.textrt.preproc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.textsim.exception.ProcessException;
import org.textsim.exception.ProcessRuntimeException;
import org.textsim.textrt.preproc.tokencheck.AcceptTokenCheck;
import org.textsim.textrt.preproc.tokencheck.NumericTokenCheck;
import org.textsim.textrt.preproc.tokencheck.StopwordTokenCheck;
import org.textsim.textrt.preproc.tokenizer.Tokenizer;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.util.FileUtil;
import org.textsim.util.IOUtil;
import org.textsim.util.Unigram;
import org.textsim.util.WordSet;
import org.textsim.util.token.TokenFilter;
import org.textsim.wordrt.preproc.WordrtPreproc;

/**
 * Implementation of text pre-process algorithm for single file.
 * This class could provide algorithm for single/multiple thread implementation.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
abstract class AbstractTextrtPreprocessor
        implements TextrtPreprocessor {
    
    /**
     * The suffix {@code String} of the text file.
     */
    public static final String TEXT_SUFFIX = "text";

    /**
     * The suffix {@code String} of the reject file.
     */
    public static final String REJ_SUFFIX = "rej";

    /**
     * The suffix {@code String} of the numeric file.
     */
    public static final String NUM_SUFFIX = "num";

    /**
     * The suffix {@code String} of the stop word file.
     */
    public static final String SW_SUFFIX = "sw";

    // Recording information for the header of out files
    private File unigramFile;
    private File stopWordListFile;
    private File excepWordListFile;
    // Processing data structure
    public TObjectIntHashMap<String> unigramIDMap;
    private HashSet<String> stopWordList;
    @SuppressWarnings("unused")
    private HashSet<String> excepWordList;
    // Token preprocessors
    Tokenizer tokenizer;
    TokenFilter tokenFilter;
    // Checking token type
    AcceptTokenCheck acceptToken;
    NumericTokenCheck numericToken;
    StopwordTokenCheck stopwordToken;

    /**
     * Constructs the object by given files.
     *
     * @param  unigramPrepPathname    the pathname string of pre-processed unigram data file.
     * @param  stopwordPathname       the pathname string of stop word list file.
     * @param  exceptionWordPathname  the pathname string of exception word list file.
     * @param  tokenizer              a tokenizer.
     * @param  tokenFilter            a token filter.
     * 
     * @see Tokenizer
     * @see TokenFilter
     *
     * @throws ProcessException  if error occurs in the process.
     */ 
    protected AbstractTextrtPreprocessor(String unigramPrepPathname, String stopwordPathname, String exceptionWordPathname,
            Tokenizer tokenizer, TokenFilter tokenFilter) {

        this(new File(unigramPrepPathname), FileUtil.getFileSafely(stopwordPathname),
                FileUtil.getFileSafely(exceptionWordPathname), tokenizer, tokenFilter);
    }

    /**
     * Construct client-specific preprocessors.
     * <p>
     * Unigram data structure is copied from the server's preprocessor. Stopwords and reject words are passed as Strings.
     * </p>
     * 
     * @param  unigram      a {@code TObjectIntHashMap<String>} object for unigram ID looking up.
     * @param  stopWords    a {@code HashSet<String>} stores stop word list.
     * @param  excepWords   a {@code HashSet<String>} stores exception word list.
     * @param  tokenizer    a tokenizer.
     * @param  tokenFilter  a tokenFilter.
     * 
     * @see Tokenizer
     * @see TokenFilter
     */
    protected AbstractTextrtPreprocessor (TObjectIntHashMap<String> unigram, String stopWords, String rejWords,
            Tokenizer tokenizer, TokenFilter tokenFilter) {

        try {
            unigramIDMap  = unigram;
            stopWordList  = WordSet.loadListString(stopWords);
            excepWordList = WordSet.loadListString(rejWords);
        } catch (IOException e) {
            throw new ProcessRuntimeException(e);
        }
        this.tokenizer     = tokenizer;
        this.tokenFilter   = tokenFilter;
        this.numericToken  = new NumericTokenCheck();
        this.stopwordToken = new StopwordTokenCheck(stopWordList);
        this.acceptToken   = new AcceptTokenCheck(stopWordList, unigramIDMap);
	}
    
    /**
     * Constructs the object by given files.
     *
     * @param  unigramPrepPathname    the pathname string of pre-processed unigram data file.
     * @param  stopwordPathname       the pathname string of stop word list file.
     * @param  exceptionWordPathname  the pathname string of exception word list file.
     * @param  tokenizer              a tokenizer.
     * @param  tokenFilter            a token filter.
     * 
     * @see Tokenizer
     * @see TokenFilter
     *
     * @throws ProcessException  if error occurs in the process.
     */
    protected AbstractTextrtPreprocessor(File unigramFile, File stopWordListFile, File excepWordListFile, Tokenizer tokenizer, TokenFilter tokenFilter) {

        this.unigramFile       = unigramFile;
        this.stopWordListFile  = stopWordListFile;
        this.excepWordListFile = excepWordListFile;
        try {
            unigramIDMap  = Unigram.readIDMap(WordrtPreproc.BINARY, unigramFile);
            stopWordList  = WordSet.loadList(stopWordListFile);
            excepWordList = WordSet.loadList(excepWordListFile);
        } catch (IOException e) {
            throw new ProcessRuntimeException(e);
        } catch (ProcessException e) {
            throw new ProcessRuntimeException(e);
        }
        this.tokenizer     = tokenizer;
        this.tokenFilter   = tokenFilter;
        this.numericToken  = new NumericTokenCheck();
        this.stopwordToken = new StopwordTokenCheck(stopWordList);
        this.acceptToken   = new AcceptTokenCheck(stopWordList, unigramIDMap);
    }

    /**
     * Constructs the object by the given files.
     * <p>
     * This constructors receives the reference of the data structure from parameters. It will not create its own data
     * structure. The given {@code File} objects are used to generate header information.
     * </p>
     *
     * @param  unigramFile        the {@code File} object of the pre-processed unigram file.
     * @param  stopWordListFile   the {@code File} object of the Stop word list file.
     * @param  excepWordListFile  the {@code File} object of the exception word list file.
     * @param  unigramIDMap       a {@code TObjectIntHashMap<String>} object for unigram ID looking up.
     * @param  stopWordList       a {@code HashSet<String>} stores stop word list.
     * @param  excepWordList      a {@code HashSet<String>} stores exception word list.
     * @param  tokenizer          a tokenizer.
     * @param  tokenFilter        a token filter.
     * 
     * @see Tokenizer
     * @see TokenFilter
     *
     * @throws ProcessException  if error occurs in the process.
     */
    protected AbstractTextrtPreprocessor(File unigramFile, File stopWordListFile, File excepWordListFile,
            TObjectIntHashMap<String> unigramIDMap, HashSet<String> stopWordList, HashSet<String> excepWordList,
            Tokenizer tokenizer, TokenFilter tokenFilter) {
        
        this.unigramFile       = unigramFile;
        this.stopWordListFile  = stopWordListFile;
        this.excepWordListFile = excepWordListFile;
        this.unigramIDMap      = unigramIDMap;
        this.stopWordList      = stopWordList;
        this.excepWordList     = excepWordList;
        this.tokenizer         = tokenizer;
        this.tokenFilter       = tokenFilter;
        this.numericToken  = new NumericTokenCheck();
        this.stopwordToken = new StopwordTokenCheck(stopWordList);
        this.acceptToken   = new AcceptTokenCheck(stopWordList, unigramIDMap);
    }
    
    /**
     * Pre-processed the text file with its pathname, and put the output files in the given directory.
     *
     * @param  textPathname  the pathname string of the input file.
     * @param  outputDir     the pathname string of the output directory.
     */
    @Override
    public void preprocess(File[] docs, File outputDir) {
        
        for (File doc : docs) {
            preprocessImpl(doc, outputDir);
        }
    }
    
    /**
     * Pre-processed the {@code File} object, and put the output files in the given directory.
     *
     * @param  textFile   the {@code File} object of the input file.
     * @param  outputDir  the pathname string of the output directory.
     */
    public void preprocessImpl(File textFile, File outputDir) {

        // Initializes ArrayLists to store the pre-processing data
        TObjectIntHashMap<String> prepList = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> stopList = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> numList  = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> rejList  = new TObjectIntHashMap<String>();

        BufferedReader textOrig = null;
        try {
            textOrig = new BufferedReader(new FileReader(textFile));

            String prev = null, curr = null, next1 = null, next2 = null;
            this.tokenizer.tokenize(textFile);
            while (this.tokenizer.hasMoreTokens() || (curr != null || next1 != null || next2 != null)) {

                // Process sequence build
                // Move the token processing sequence
                prev = curr;
                curr = next1;
                next1 = next2;
                // Add new token to sequence
                if (this.tokenizer.hasMoreTokens()) {
                    next2 = this.tokenFilter.filter(this.tokenizer.nextToken());
                } else {
                    next2 = null;
                }

                // Pattern check
                if (curr != null) {
                    if (numericToken.matches(curr)) {
                        // if the processing token (curr) contains any numeric character, store its context in list
                        addToMap(numList, curr + " " + prev + " " + next1 + " " + next2);
                    } else {
                        if (stopwordToken.matches(curr)) {
                            addToMap(stopList, curr);
                        } else if (acceptToken.matches(curr)) {
                            addToMap(prepList, curr);
                        } else {
                            addToMap(rejList, curr);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ProcessRuntimeException(e);
        } finally {
            try {
                if (textOrig != null) {
                    textOrig.close();
                }
                if (this.tokenizer != null) {
                    this.tokenizer.close();
                }
            } catch (IOException e) {
                throw new ProcessRuntimeException(e);
            }
        }

        // Record List to the file
        recordMap(prepList, outputDir, textFile, "doc");
        recordMap(stopList, outputDir, textFile, "sw");
        recordMap(numList,  outputDir, textFile, "num");
        recordMap(rejList,  outputDir, textFile, "rej");
    }

    public TextInstance createTextInstance(File textFile) {

        // Initializes ArrayLists to store the pre-processing data
        TObjectIntHashMap<String> prepList = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> stopList = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> numList  = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> rejList  = new TObjectIntHashMap<String>();

        BufferedReader textOrig = null;
        try {
            textOrig = new BufferedReader(new FileReader(textFile));

            String prev = null, curr = null, next1 = null, next2 = null;
            this.tokenizer.tokenize(textFile);
            while (this.tokenizer.hasMoreTokens() || (curr != null || next1 != null || next2 != null)) {

                // Process sequence build
                // Move the token processing sequence
                prev = curr;
                curr = next1;
                next1 = next2;
                // Add new token to sequence
                if (this.tokenizer.hasMoreTokens()) {
                    next2 = this.tokenFilter.filter(this.tokenizer.nextToken());
                } else {
                    next2 = null;
                }

                // Pattern check
                if (curr != null) {
                    if (numericToken.matches(curr)) {
                        // if the processing token (curr) contains any numeric character, store its context in list
                        addToMap(numList, curr + " " + prev + " " + next1 + " " + next2);
                    } else {
                        if (stopwordToken.matches(curr)) {
                            addToMap(stopList, curr);
                        } else if (acceptToken.matches(curr)) {
                            addToMap(prepList, curr);
                        } else {
                            addToMap(rejList, curr);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ProcessRuntimeException(e);
        } finally {
            try {
                if (textOrig != null) {
                    textOrig.close();
                }
                if (this.tokenizer != null) {
                    this.tokenizer.close();
                }
            } catch (IOException e) {
                throw new ProcessRuntimeException(e);
            }
        }

        return new TextInstance(textFile, getTIntIntHashMap(prepList), rejList);
    }//createTextInstance
    
    /**
     * Creates a TextInstance object based on a String of text rather than a file
     * @param text 
     * @return TextInstance object
     */
    public TextInstance createTextInstance(String text) {

        // Initializes ArrayLists to store the pre-processing data
        TObjectIntHashMap<String> prepList = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> stopList = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> numList  = new TObjectIntHashMap<String>();
        TObjectIntHashMap<String> rejList  = new TObjectIntHashMap<String>();
        
        BufferedReader textOrig = null;
        try {
            textOrig =  new BufferedReader(new StringReader(text));

            String prev = null, curr = null, next1 = null, next2 = null;
            this.tokenizer.tokenize(text);
            while (this.tokenizer.hasMoreTokens() || (curr != null || next1 != null || next2 != null)) {

                // Process sequence build
                // Move the token processing sequence
                prev = curr;
                curr = next1;
                next1 = next2;
                // Add new token to sequence
                if (this.tokenizer.hasMoreTokens()) {
                    next2 = this.tokenFilter.filter(this.tokenizer.nextToken());
                } else {
                    next2 = null;
                }

                // Pattern check
                if (curr != null) {
                	//System.out.println(curr+"    &&&&&&&&&&&&");
                    if (numericToken.matches(curr)) {
                        // if the processing token (curr) contains any numeric character, store its context in list
                        addToMap(numList, curr + " " + prev + " " + next1 + " " + next2);
                    }//if 
                    else {
                        if (stopwordToken.matches(curr)) {
                            addToMap(stopList, curr);
                        }//if 
                        else if (acceptToken.matches(curr)) {
                            addToMap(prepList, curr);
                        }//else if
                        else{
                        	addToMap(rejList, curr);
                        }//else
                    }//else
                }//if
            }//while
        } finally {
            try {
                if (textOrig != null) {
                    textOrig.close();
                }//if
                if (this.tokenizer != null) {
                    this.tokenizer.close();
                }//if
            } catch (IOException e) {
                throw new ProcessRuntimeException(e);
            }//catch
        }//finally

        return new TextInstance(text, getTIntIntHashMap(prepList), rejList);
    }//createTextInstance

    /**
     * Write the data from hash map to File.
     * A header is added to specify the information of the file.
     * 
     * @param  hashmap           a {@code TObjectIntHashMap<String>} object stores the pre-processed data.
     * @param  unigramIDMap      a {@code TObjectIntHashMap<String>} object stores the unigram and ID information.
     * @param  outputDir         the pathname string of the output directory.
     * @param  textFile          the {@code File} object of the input text file.
     * @param  stopWordListFile  the {@code File} object of the stop word list file.
     * @param  unigramFile       the {@code File} object of the pre-processed unigram file.
     * @param  fileType          a string specified the type of the processing file.
     *
     * @throws ProcessException  if error occurs in the process.
     */
    private void recordMap(TObjectIntHashMap<String> hashMap, File outputDir, File textFile, String fileType) {

        String suffix = null;
        if (fileType == "doc") {
            suffix = TEXT_SUFFIX;
        } else if (fileType == "sw") {
            suffix = SW_SUFFIX;
        } else if (fileType == "num") {
            suffix = NUM_SUFFIX;
        } else if (fileType == "rej") {
            suffix = REJ_SUFFIX;
        }
        File outFile = new File(outputDir, textFile.getName() + '.' + suffix);
        
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(outFile));
            
            // Add header to the file
            IOUtil.writePreprocDocHeader(bw, textFile, stopWordListFile, excepWordListFile, unigramFile, fileType);
            
            // Record the data into file
            if (fileType == "rej") {
                for (String word: hashMap.keySet())
                    if (word != "")
                        bw.write(word + "\t" + hashMap.get(word) + "\n");
            } else if (fileType == "num") {
                for (String word: hashMap.keySet())
                    bw.write(word + "\n");
            } else {
                for (String word: hashMap.keySet())
                    bw.write(unigramIDMap.get(word) + "\t" + hashMap.get(word) + "\n");
            }
        } catch (IOException e) {
            throw new ProcessRuntimeException(e);
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                throw new ProcessRuntimeException(e);
            }
        }
    }
    
    /**
     * Record the word to HashMap.
     *
     * @param  hashMap  a {@code TObjectIntHashMap<String>} for word count looking up.
     * @param  word     a word string.
     */
    private void addToMap(TObjectIntHashMap<String> hashMap, String word) {

        if (hashMap.containsKey(word)) {
            hashMap.put(word, hashMap.get(word) + 1);
        } else {
            hashMap.put(word, 1);
        }
    }
    
    private TIntIntHashMap getTIntIntHashMap(TObjectIntHashMap<String> hashMap) {
        
        String[] keys = new String[hashMap.keys().length];
        hashMap.keys(keys);
        TIntIntHashMap newMap = new TIntIntHashMap((int)(keys.length * 1.25), 0.8f);
        for (String key : keys) {
            newMap.put(unigramIDMap.get(key), hashMap.get(key));
        }
        return newMap;
    }
    
    /**
     * Get integer id for a word.
     * 
     * @param  word  a word.
     */
    // TODO merge the function to wordrt.WordRtProcessor.
    @Deprecated
    public int getWordID(String word){
		Integer out = this.unigramIDMap.get(word);
    	if (out == null) return -1;
    	else return out;
    }

}