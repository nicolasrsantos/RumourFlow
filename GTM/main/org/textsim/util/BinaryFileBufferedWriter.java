package org.textsim.util;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Customized {@code DataOutputStrem} for writing big binary file.
 */
public class BinaryFileBufferedWriter
        extends DataOutputStream {

    /**
     * Constructs the object with pathname {@code String}.
     */
	public BinaryFileBufferedWriter(String pathname)
	        throws FileNotFoundException {

		super(new BufferedOutputStream(new FileOutputStream(pathname),65536));
	}

    /**
     * Constructs the object with {@code File}.
     */
	public BinaryFileBufferedWriter(File file)
	        throws FileNotFoundException {
	    
		super(new BufferedOutputStream(new FileOutputStream(file),65536));
	}
}
