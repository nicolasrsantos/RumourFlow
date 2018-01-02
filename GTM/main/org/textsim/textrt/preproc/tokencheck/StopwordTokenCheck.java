package org.textsim.textrt.preproc.tokencheck;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.textsim.util.FileUtil;
import org.textsim.util.WordSet;

/**
 * Provide judgment rule for <i>stop word token</i> (which will be stored in pre-processed stop word file (<tt>-sw.ic</tt>)
 * for further processing).
 * 
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class StopwordTokenCheck
        implements TokenCheckable {

    private HashSet<String> stopList;

    /**
     * Initializes an object with the in-memory data structure.
     * 
     * @param  stopList  an {@code HashSet<String>} contains all the stop word as elements all the stop word as element.
     */
    public StopwordTokenCheck(HashSet<String> stopList) {

        this.stopList = stopList;

    }

    /**
     * Initializes an object with the file pathname.
     * 
     * @param  stopWordListPathname  the pathname of the stop word list file.
     *                              
     * @throws IOException  if I/O error occurs.
     */
    public StopwordTokenCheck(String stopWordListPathname)
            throws IOException {

        this(FileUtil.getFileSafely(stopWordListPathname));

    }

    /**
     * Initializes an object with the File object.
     * 
     * @param  stopWordListFile  the {@code File} object of the stop word list file.
     * 
     * @throws IOException  if I/O error occurs.
     */
    public StopwordTokenCheck(File stopWordList)
            throws IOException {

        stopList = WordSet.loadList(stopWordList);

    }

    /**
     * Checks whether the token is a stop word.
     * 
     * @param token  the token string.
     * 
     * @return {@code true} if the token string meets the requirement.
     */
    @Override
    public boolean matches(String token) {

        if (stopList.contains(token))
            return true;
        else
            return false;

    }

}
