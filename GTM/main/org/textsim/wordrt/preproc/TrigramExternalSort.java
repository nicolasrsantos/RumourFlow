package org.textsim.wordrt.preproc;

import java.io.IOException;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.textsim.util.FileUtil;

import com.google.code.externalsorting.ExternalSort;

/**
 * {@code TrigramExternalSort} reads the trigram temporary file and sort each line in an ascending order, according
 * primarily on the first unigram ID, and secondly on the second unigram ID.
 * <p>
 * The format of the input/output file is:
 * <pre>
 * gram1_ID <TAB> gram2_ID <TAB> match_count<NEWLINE>
 * </pre></p>
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class TrigramExternalSort {
    
    /**
     * The suffix {@String} of the output data file.
     */
    public static final String DATA_SUFFIX = "tsd";
	
    /**
     * Received the output of {@code TrigramCountSummation} from the {@code tempDir}, sort and output a new file with
     * {@link DATA_SUFFIX}.
     * 
     * @param tempDir  The pathname {@code String} of the processing temporary directory.
     *
     * @throws IOException
     */
	public static void sortFile(String tempDir)
            throws IOException {
		
	    File trigramUnsortedFile = FileUtil.getFileInDirectoryWithSuffix(tempDir, TrigramCountSummation.DATA_SUFFIX);
	    String trigramSorted = tempDir + File.separator
            + FileUtil.getFilenameWithoutSuffix(trigramUnsortedFile) + '.' + DATA_SUFFIX;
	    File trigramSortedFile = new File(trigramSorted);
		
	    Comparator<String> comparator = new Comparator<String>() {
			
            @Override
            public int compare(String r1, String r2) {
            	
                // Empty line check
            	StringTokenizer line1 = new StringTokenizer(r1);
            	StringTokenizer line2 = new StringTokenizer(r2);
            	if(!(line1.hasMoreTokens() && line2.hasMoreTokens())) {
            		if(line1.hasMoreTokens()) {
            			return 1;
            		} else if (line2.hasMoreTokens()) {
            			return -1;
            		} else {
            			return 0;
            		}
            	}
            	
            	int var1x = Integer.parseInt(line1.nextToken());
            	int var1y = Integer.parseInt(line1.nextToken());
            	int var2x = Integer.parseInt(line2.nextToken());
            	int var2y = Integer.parseInt(line2.nextToken());
            	
            	if(var1x == var2x) {
            		if(var1y == var2y) {
            			return 0;
            		} else if(var1y > var2y) {
            			return 1;
            		} else {
            			return -1;
            		}
            	} else if (var1x < var2x) {
            		return -1;
            	} else {
            		return 1;
            	}
            }
        };
        
        File sortTempDir = new File(tempDir + File.separator + "sort" + File.separator);
        FileUtils.forceMkdir(sortTempDir);
	    List<File> l = ExternalSort.sortInBatch(trigramUnsortedFile, comparator, 1024, Charset.defaultCharset(), sortTempDir, false);
	    ExternalSort.mergeSortedFiles(l, trigramSortedFile, comparator);
	}
}
