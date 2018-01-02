package org.textsim.textrt.preproc.tokencheck;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.textsim.exception.ProcessException;
import org.textsim.util.FileUtil;
import org.textsim.util.Unigram;
import org.textsim.util.WordSet;
import org.textsim.wordrt.preproc.WordrtPreproc;

/**
 * Provide judgment rule for accept token (which will be stored in pre-processed word file (<code>-doc.ic</code>) for
 * further processing).
 * 
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class AcceptTokenCheck
        implements TokenCheckable {

    private HashSet<String> stopList;
    private TObjectIntHashMap<String> unigramIDMap;

    /**
     * Initializes an object with the in-memory data structure.
     * 
     * @param  stopList      an {@code HashSet<String>} contains all the stop word as elements all the stop word as element.
     * @param  unigramIDMap  a {@code TObjectIntHashMap<String>} contains all the ID information for looking up.
     */
    public AcceptTokenCheck(HashSet<String> stopList, TObjectIntHashMap<String> unigramIDMap) {

        this.stopList = stopList;
        this.unigramIDMap = unigramIDMap;

    }

    /**
     * Initializes an object with the file pathnames.
     * 
     * @param  stopWordListPathname  the pathname of the stop word list file.
     * @param  unigramFilePathname   the pathname of the pre-processed unigram file.
     *                              
     * @throws IOException  if I/O error occurs.
     * @throws ProcessException 
     */
    public AcceptTokenCheck(String stopWordListPathname, String unigramFilePathname)
            throws NullPointerException, IOException, ProcessException {

        this(FileUtil.getFileSafely(stopWordListPathname), new File(unigramFilePathname));

    }

    /**
     * Initializes an object with the File objects.
     * 
     * @param  stopWordListFile  the {@code File} object of the stop word list file.
     * @param  unigramFile       the pathname of the pre-processed unigram file.
     * 
     * @throws IOException  if I/O error occurs.
     * @throws ProcessException 
     */
    public AcceptTokenCheck(File stopWordList, File unigramFile)
            throws IOException, ProcessException {

        stopList = WordSet.loadList(stopWordList);
        unigramIDMap = Unigram.readIDMap(WordrtPreproc.BINARY, unigramFile);

    }

    /**
     * Checks whether a token can be accepted.
     * 
     * @param  token  the token string.
     *
     * @return {@code true} if the token string meets the requirement.
     */
    @Override
    public boolean matches(String token) {

        if (unigramIDMap.contains(token)) {
            if (stopList.contains(token)) {
                return false;
            } else {
                return true;
            }
        }
        else
            return false;

    }

}
