package org.textsim.wordrt.preproc;

import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import org.textsim.exception.ProcessException;
import org.textsim.util.FileUtil;
import org.textsim.util.IOUtil;
import org.textsim.util.Unigram;
import org.textsim.wordrt.preproc.WordrtPreproc;

/**
 * Second step of preprocess to process 3gms files.
 * <p>
 * First the program reads the Unigram file from step one and store each gram and it's id in a hash map.
 * Then the program reads 3gms files and write every pair of grams with its frequency in a file.
 * The gram with smaller id appears first.
 * </p><p>
 * The trigramInterDataput has the following format:
 * <pre>
 * gram1id1 gram3id1 count
 * gram1id2 gram3id2 count
 * ...
 * </pre>
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class TrigramCountSummation
{
    public static final String DATA_SUFFIX = "tcsd";
    public static final String UNACCEPTED_DATA_SUFFIX = "tcsu";
    
	public static void TrigramGen(String trigramDir, String tempDir, String regex)
	        throws IOException, FileNotFoundException, ProcessException {

		String unigramDataPathname =
                FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.DATA_SUFFIX).getAbsolutePath();
		TObjectIntHashMap<String> unigramTable = Unigram.readIDMap(WordrtPreproc.TEXT, new File(unigramDataPathname));
		processTrigram(trigramDir, unigramTable, tempDir, regex);
	}
	
	/**
     * Sum the counts of trigram with the same pattern.
	 * 
	 * @param  trigramDir    The pathname {@code String} of the parent directory of trigram raw data.
	 * @param  unigramTable  The {@code TObjectIntHashMap<String>} provide unigram id looking up.
	 * @param  tempDir       The pathname {@code String} of the processing temporary directory.
	 * @param  regex         The {@code String} of regular expression used for filting grams.
     *
	 * @throws FileNotFoundException
	 * @throws NoSuchElementException
	 * @throws IOException
	 */
	private static void processTrigram(String trigramDir,TObjectIntHashMap<String> unigramTable, String tempDir,
            String regex)
			throws FileNotFoundException, NoSuchElementException, IOException {
		
		File folder = new File(trigramDir);
		File[] listOfFiles = folder.listFiles();
		
		String trigramInterDataFile = tempDir + File.separator + folder.getName() + '.' + DATA_SUFFIX;
		String trigramUnaccepedDataFile = tempDir + File.separator + folder.getName() + '.' + UNACCEPTED_DATA_SUFFIX;
		
		BufferedReader trigramOrigData = null;
		BufferedWriter trigramInterData = null;
		BufferedWriter trigramUnaccepedData = null;
		try {
    		trigramInterData = new BufferedWriter(new FileWriter(trigramInterDataFile));
    		trigramUnaccepedData = new BufferedWriter(new FileWriter(trigramUnaccepedDataFile));
    		
    		File unigramFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, UnigramPreprocess.DATA_SUFFIX);
    		// Add header to the files
    		IOUtil.writePreprocTrigramHeader(trigramUnaccepedData, unigramFile, folder, "unaccept");
    
    		int id = 0, check_id, key = 0;
    		boolean isFirstGram = true;
    		TIntLongHashMap recordMap = null;
    		String gram1, gram3;
    		Long freq;
    		
            System.out.print("\rSTEP 2: Trigram Count Summation..0/" + listOfFiles.length);
            
            Pattern pattern = Pattern.compile("^" + regex + " " + regex + " " + regex + "\t.*");
                    
    		for (int i = 0; i < listOfFiles.length; i++) {
    			
    		    if (listOfFiles[i].isHidden())
    		        continue;
    		    
    			trigramOrigData = new BufferedReader(new FileReader(listOfFiles[i]));
                
                for (String inputLine = null; (inputLine = trigramOrigData.readLine()) != null; ) {
                	
                    if (pattern.matcher(inputLine).matches()) {
                    	StringTokenizer line = new StringTokenizer(inputLine);
                    	
                    	gram1 = line.nextToken();
                    	line.nextToken();
                    	gram3 = line.nextToken();
                    	freq = Long.parseLong(line.nextToken());
                   
                        if(unigramTable.containsKey(gram3) && unigramTable.containsKey(gram1)) {                	    		                
                        	check_id = unigramTable.get(gram1);
                        	key = unigramTable.get(gram3);
        	                
        	                if(isFirstGram) {
        	                	id = check_id;
        	                	recordMap = new TIntLongHashMap(); 
        	                	isFirstGram = false;
        	                } else if(id != check_id) {
        	                	writeMap(recordMap, id, trigramInterData);
        	                	
        	                  	id = check_id;
        	                  	recordMap = new TIntLongHashMap(); 
        	                }
        	                recordMap.put(key, recordMap.get(key) + freq);
                        } else {
                        	// Write to the unaccepted data file, if unexisted unigram appears
                        	trigramUnaccepedData.write(gram1 + ' ' + gram3 + '\n');
                        }
                    } else {
                        trigramUnaccepedData.write(inputLine);
                    }
                }
                writeMap(recordMap, id, trigramInterData);
                
	            System.out.print("\rSTEP 2: Trigram Count Summation.." + (i + 1) + "/" + listOfFiles.length);
    	    }
            System.out.print("\rSTEP 2: Trigram Count Summation..");
    		if (trigramOrigData == null) {
                throw new FileNotFoundException();
            }
		} finally {
		    if (trigramOrigData != null) {
                trigramOrigData.close();
            }
		    if (trigramInterData != null) {
                trigramInterData.close();
            }
		    if (trigramUnaccepedData != null) {
                trigramUnaccepedData.close();
            }
		}
	}
	 
	
	/**
     * Writes the data of trigram with specific id into output file.
	 * 
	 * @param  mp                         The {@TIntLongHashMap} of the trigram to be written.
	 * @param  id                         The {@code int} id of the trigram to be written.
	 * @param  trigramInterDataputBuffer  The {@code BufferedWriter} of output file.
     *
	 * @throws IOException
	 */
	private static void writeMap(TIntLongHashMap mp, int id, BufferedWriter trigramInterDataputBuffer)
			throws IOException {
		
		int gram2;
		long count;
		
		if (mp != null) {
    		String str1 = mp.toString();
    		StringTokenizer line;
    		
    		str1 = str1.replaceAll(" ", "");
    		str1 = str1.replaceAll(",", "\t");
    		str1 = str1.replaceAll("=", "\t");
    		str1 = str1.replace("{", "");
    		str1 = str1.replace("}", "");
    		
    		line = new StringTokenizer(str1);
    		
    		while(line.hasMoreTokens()) {
        		gram2 = Integer.parseInt(line.nextToken());
        		count = Long.parseLong(line.nextToken());
        		if(id <= gram2) {
            		trigramInterDataputBuffer.write(id + "\t" + gram2 + "\t" + count + "\n");
            	} else {
            		trigramInterDataputBuffer.write(gram2 +"\t"+ id + "\t" + count + "\n");
                }
        	}
		}
	}
}
