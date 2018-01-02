package org.textsim.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Customized {@code DataInputStrem} for reading big binary file.
 */
public class BinaryFileBufferedReader
        extends DataInputStream {

    /**
     * Constructs the object with pathname {@code String}.
     */
	public BinaryFileBufferedReader(String pathname)
	        throws FileNotFoundException {

		super(new BufferedInputStream(new FileInputStream(pathname), 65536));
	}

    /**
     * Constructs the object with {@code File}.
     */
	public BinaryFileBufferedReader(File file)
	        throws FileNotFoundException {

		super(new BufferedInputStream(new FileInputStream(file), 65536));
	}
}
