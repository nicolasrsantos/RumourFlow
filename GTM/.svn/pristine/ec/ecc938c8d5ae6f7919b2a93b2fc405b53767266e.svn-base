package org.textsim.util;

import java.io.File;
import java.io.IOException;

public abstract class PreprocessedData {
    
    private File preprocFile;   // File object which contains the pre-processed data
    private File originalFile;  // File object which contains the original (raw) information

    protected PreprocessedData() {

    //------------------------------------------------ Private Variables ------------------------------------------------\\
    
        this.preprocFile = null;
        this.originalFile = null;
    }
    
    //-------------------------------------------------- Constructors ---------------------------------------------------\\
    
    protected PreprocessedData(File preprocFile, File orignalFile) {

        this.preprocFile = preprocFile;
        this.originalFile = orignalFile;
    }

    //---------------------------------------------------- Accessors ----------------------------------------------------\\
    
    public File getFile() {

        return preprocFile;
    }

    public File getOrigFile() {

        return originalFile;
    }

    //---------------------------------------------------- Mutators -----------------------------------------------------\\
    
    protected void setFile(File file) {

        this.preprocFile = file;
    }

    protected void setOrigFile(File file) {

        this.originalFile = file;
    }

    //----------------------------------------------------- Others ------------------------------------------------------\\

    // Write data structure to file
    public abstract void write()
            throws IOException;
    
    // Read file to be data structure
    public abstract void read()
            throws IOException;

}
