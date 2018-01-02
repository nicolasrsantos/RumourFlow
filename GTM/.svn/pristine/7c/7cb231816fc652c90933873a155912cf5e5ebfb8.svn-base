package org.textsim.util;

import java.io.File;
import java.io.IOException;

import org.textsim.exception.ProcessException;
import org.textsim.textrt.preproc.TextrtPreprocessor;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class Document
        extends PreprocessedData {
    
    //------------------------------------------------ Private Variables ------------------------------------------------\\
    
    private TIntIntHashMap textMap;
    private TObjectIntHashMap<String> rejMap;
    
    //-------------------------------------------------- Constructors ---------------------------------------------------\\
    
    // Construct with pre-processed binary file
    public Document(File preprocFile, File originalFile)
            throws IOException {
        
        super(preprocFile, originalFile);
        read();
    }
    
    // Construct with data structure
    public Document(File preprocFile, File originalFile, TIntIntHashMap textMap, TObjectIntHashMap<String> rejMap) {
        
        super(preprocFile, originalFile);
        this.textMap = textMap;
        this.rejMap = rejMap;
    }
    
    // Construct with original file (raw data)
    public Document(File preprocFile, File originalFile, TextrtPreprocessor preproc)
            throws ProcessException {
        
        super(preprocFile, originalFile);
//        Document document = preproc.createDocument(originalFile.getAbsolutePath(), preprocFile.getParent());
//        this.textMap = document.getTextMap();
//        this.rejMap = document.getRejMap();
    }

    //---------------------------------------------------- Accessors ----------------------------------------------------\\
    
    public final TIntIntHashMap getTextMap() {

        return textMap;
    }

    public final TObjectIntHashMap<String> getRejMap() {

        return rejMap;
    }
    
    //----------------------------------------------------- Others ------------------------------------------------------\\
    
    public String getName() {
        
        return getFile().getName();
    }

    @Override
    public void write() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void read() throws IOException {
        // TODO Auto-generated method stub
        
    }
}
