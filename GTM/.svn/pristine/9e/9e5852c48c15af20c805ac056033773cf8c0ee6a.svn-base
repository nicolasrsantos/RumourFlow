package org.textsim.textrt.preproc.tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.WhitespaceTokenizer;

/**
 * {@code WhiteSpaceTokenizer} tokenize the file by <it>space</it>.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
@SuppressWarnings("rawtypes")
public class WhiteSpaceTokenizer
        implements Tokenizer {
    
    /**
     * The underlying file reader.
     */
    private Reader reader;
    
    /**
     * The implementation of the tokenizer.
     */
    private WhitespaceTokenizer wt;

    /**
     * Default constructor.
     * 
     * @throws FileNotFoundException
     */
    public WhiteSpaceTokenizer() {

        this.reader = null;
        this.wt = null;
    }

    @Override
    public void tokenize(File file)
            throws FileNotFoundException {
        
        this.reader = new FileReader(file);
        this.wt = new WhitespaceTokenizer(
                new CoreLabelTokenFactory(),
                this.reader,
                false);
    }

    @Override
    public boolean hasMoreTokens() {

        ensureOpen();
        return this.wt.hasNext();
    }

    @Override
    public String nextToken() {

        ensureOpen();
        return this.wt.next().toString();
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

	@Override
	public void tokenize(String text) {
		// TODO Auto-generated method stub
		
	}
}
