package org.textsim.textrt.preproc.tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class defines the tokenizer for text pre-processing.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public interface Tokenizer {

    /**
     * Tokenize the file.
     * 
     * @param  file  The file to be tokenize.
     * 
     * @throws FileNotFoundException
     */
    public void tokenize(File file)
            throws FileNotFoundException;

    /**
     * Tokenize the text.
     */
    public void tokenize(String text);
    
    /**
     * Check the whether there are any remaining data in the tokenizer.
     * 
     * @return True if there are tokens available in the tokenizer.
     */
    public boolean hasMoreTokens();
    
    /**
     * Pop out the next token.
     * 
     * @return the next available token in the tokenizer.
     */
    public String nextToken();
    
    /**
     * Close the object, to avoid the memory leak.
     */
    public void close()
            throws IOException;
}
