package org.textsim.wordrt.preproc;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.textsim.util.IOUtil;

/**
 * The first step of pre-process.
 * This program reads 1-gram dataset, compute and output the new files with unique ID for each 1-gram.
 * The format of the output files are:
 * <pre>
 * 1gram <TAB> 1gram_ID <TAB> match_count <NEWLINE>
 * </pre>
 * total number of all words
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class UnigramPreprocess {

    /**
     * The suffix {@code String} for the output data file.
     */
    public static final String DATA_SUFFIX = "upd";

    /**
     * The suffix {@code String} for the output count file.
     */
    public static final String COUNT_SUFFIX = "upc";

    /**
     * The suffix {@code String} for the output unaccepted file.
     */
    public static final String UNACCPTED_DATA_SUFFIX = "upu";

    /**
     * Store the maximum frequence among all the unigrams.
     */
    private static long cMax;

    /**
     * Generates pre-process unigram text file.
     * 
     * @param  unigramPath  The pathname {@code String} of the unigram original data.
     *                      It could either be a folder or a file.
     * @param  tempDir      The pathname {@code String} to the temporary directory.
     * @param  regex        The {@code String} of regular expression used for filting grams.
     *
     * @throws FileNotFoundException
     * @throws NoSuchElementException
     * @throws IOException
     */
    public static void unigramGen(String unigramPath, String tempDir, String regex)
            throws FileNotFoundException, NoSuchElementException, IOException {
	
        File unigramOrigDataFile = new File(unigramPath);
		String unigramDataFile           = tempDir + File.separator + unigramOrigDataFile.getName() + '.' + DATA_SUFFIX;
		String unigramUnacceptedDataFile = tempDir + File.separator + unigramOrigDataFile.getName() + '.' + UNACCPTED_DATA_SUFFIX;
		String unigramCountFile          = tempDir + File.separator + unigramOrigDataFile.getName() + '.' + COUNT_SUFFIX;

        cMax = 0;
        int id = 1;
        boolean hasInputFile = false;

	    BufferedWriter unigramData = null;
	    BufferedWriter unigramUnacceptedData = null;
		try {
    	    unigramData = new BufferedWriter(new FileWriter(unigramDataFile));
    	    unigramUnacceptedData = new BufferedWriter(new FileWriter(unigramUnacceptedDataFile));
    	    
    	    // Add header to the files
    	    IOUtil.writePreprocUnigramTextHeader(unigramData, unigramOrigDataFile, "data");
    	    IOUtil.writePreprocUnigramTextHeader(unigramUnacceptedData, unigramOrigDataFile, "unaccept");
    	    
            if (unigramOrigDataFile.isDirectory()) {
                File[] listOfFiles = unigramOrigDataFile.listFiles();
                for (int i = 0; i < listOfFiles.length; i++) {
                    // Skip the hidden files
                    if (!listOfFiles[i].isHidden()) {
                        id = recordUnigramFile(listOfFiles[i], unigramData, unigramUnacceptedData, regex, id);
                        // Change flag to true
                        hasInputFile = true;
                    }
                }
            } else {
                id = recordUnigramFile(unigramOrigDataFile, unigramData, unigramUnacceptedData, regex, id);
                hasInputFile = true;
            }

            // If no readable file (except hidden file) in the specified unigram folder, throws exception
    		if (!hasInputFile)
    		    throw new FileNotFoundException("No readable file in the specified unigram folder.");

		} finally {
            if (unigramData != null) {
                unigramData.close();
            }
            if (unigramUnacceptedData != null) {
                unigramUnacceptedData.close();
            }
		}

	    BufferedWriter unigramCount = null;
        try {
    	    unigramCount = new BufferedWriter(new FileWriter(unigramCountFile));
    	    
    	    // add header to the file
    	    IOUtil.writePreprocUnigramTextHeader(unigramCount, unigramOrigDataFile, "count");
    	    // write the count to the file
            unigramCount.write(Integer.toString(id - 1) + "\n");
            unigramCount.write(Long.toString(cMax));
		} finally {
            if (unigramCount != null) {
                unigramCount.close();
            }
        }
    }

    /**
     * Record one unigram raw file into pre-processed file.
     * 
     * @param  unigramOrigDataFile    The recording unigram raw {@code File}.
     * @param  unigramData            The {@code BufferedWriter} of unigram pre-processed data.
     * @param  unigramUnacceptedData  The {@code BufferedWriter} of unigram unaccepted data.
     * @param  regex                  The {@code String} of regular expression for filting grams.
     * @param  id                     The minimum available {@code int} id for the remaining unigram.
     *
     * @return The minimum available {@code int} id for the remaining unigram.
     *
     * @throws IOException
     */
    private static int recordUnigramFile(File unigramOrigDataFile, BufferedWriter unigramData, BufferedWriter unigramUnacceptedData, String regex, int id)
            throws IOException {

        BufferedReader unigramOrigData = null;
        try {
            unigramOrigData = new BufferedReader(new FileReader(unigramOrigDataFile));

            for (String inputLine = null; (inputLine = unigramOrigData.readLine()) != null; ) {
                Pattern pattern = Pattern.compile("^" + regex + "\t.*");
                if (pattern.matcher(inputLine).matches()) {
                    StringTokenizer line = new StringTokenizer(inputLine);
                    String gram = line.nextToken();
                    String freq = line.nextToken();
                    long freqVal = Long.parseLong(freq);
                    if (freqVal > cMax) cMax = freqVal;
                    unigramData.write(gram + "\t" + id + "\t" + freq + "\n");
                    id++;
                } else {
                    unigramUnacceptedData.write(inputLine);
                }
            }
        } finally {
            if (unigramOrigData != null) {
                unigramOrigData.close();
            }
        }

        return id;
    }
}
