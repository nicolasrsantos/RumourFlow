package org.textsim.textrt.preproc;

import java.io.File;
import java.util.List;

import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.util.RtPreprocessor;

/**
 * Interface for the text pre-process.
 * A running method should be defined in the implementation classes.
 * 
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public interface TextrtPreprocessor
        extends RtPreprocessor {
    
    public void preprocess(File[] docs, File outputDir);

    public List<TextInstance> preprocess(File[] docs);

    /**
     * Deprecated. Use {@link #preprocess(File[]) preprocess(File[])} instead.
     * 
     * @param docs
     * @return
     */
    @Deprecated
    public List<TextInstance> createTextInstances(File[] docs);
    
    /**
     * Deprecated. Use {@link org.textsim.textrt.proc.singlethread.BinaryCorpus#writeBinaryText(File) writeBinaryText(File)} instead.
     * 
     * @param docs
     * @return
     */
    @Deprecated
    public File writeBinaryCorpus(File[] docs, File outFile);
}