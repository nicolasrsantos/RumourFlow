package org.textsim.textrt.preproc.tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

/**
 * Tokenizer implements the <it>Penn Tree Bank</it> tokenization.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
@SuppressWarnings({
    "unchecked",
    "rawtypes"})
public class PennTreeBankTokenizer
        implements Tokenizer {
    
    /**
     * The underlying file reader.
     */
    private Reader reader;
    
    /**
     * The implementation of the tokenizer.
     */
    private PTBTokenizer ptbt;

    /**
     * Default constructor.
     * 
     * @throws FileNotFoundException
     */
    public PennTreeBankTokenizer() {
        
        this.reader = null;
        this.ptbt = null;
    }

    @Override
    public void tokenize(File file)
            throws FileNotFoundException {
        
        this.reader = new FileReader(file);
        this.ptbt = new PTBTokenizer(
                this.reader,
                new CoreLabelTokenFactory(),
                "");
    }
    
    @Override
    public void tokenize(String text) {
        
        this.reader = new StringReader(text);
        this.ptbt = new PTBTokenizer(
                this.reader,
                new CoreLabelTokenFactory(),
                "");
    }
    

    @Override
    public boolean hasMoreTokens() {
        
        ensureOpen();
        return this.ptbt.hasNext();
    }

    @Override
    public String nextToken() {

        ensureOpen();
        return this.ptbt.next().toString();
    }
    
    private void ensureOpen() {
        
        if (this.reader == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public void close()
            throws IOException {
        
        if (this.reader != null) {
            this.reader.close();
        }
    }
}
