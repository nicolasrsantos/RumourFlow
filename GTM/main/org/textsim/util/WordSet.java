package org.textsim.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Provide method for loading file which have <i>only one</i> token per line to memory.
 * 
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class WordSet {

    /**
     * Loading a word list file to be HashSet with the file pathname as the parameter.
     *
     * @param  listFilePathname  the pathname of the list file.
     *
     * @return An HashSet contains all the word as elements.
     * 
     * @throws IOException  if I/O error occurs.
     */
    public static HashSet<String> loadList(String listFilePathname)
            throws IOException {

        return loadList(FileUtil.getFileSafely(listFilePathname));

    }
    
    /**
     * Loading a word list to be HashSet with the File object as the parameter.
     * @param  listFile  the {@code File} object of the list file.
     * @return An {@code HashSet<String>} contains all the word as elements.
     * @throws IOException  if I/O error occurs.
     */
    public static HashSet<String> loadList(File listFile)
            throws IOException {
        
        BufferedReader list = null;
        try {
            HashSet<String> hashSet = new HashSet<String>();

            if (listFile != null) {
                list = new BufferedReader(new FileReader(listFile));
                for (String inputLine; (inputLine = list.readLine()) != null;) {
                    StringTokenizer line = new StringTokenizer(inputLine);
                    hashSet.add(line.nextToken());
                }
            }
            return hashSet;
        }//try 
        finally {
            if (list != null)
                list.close();
        }//finally
        
    }//loadList
    
    /**
     * Loading a word list to be HashSet with the String object as the parameter.
     * @param  listFile  the {@code File} object of the list file.
     * @return An {@code HashSet<String>} contains all the word as elements.
     * @throws IOException  if I/O error occurs.
     */
    public static HashSet<String> loadListString(String list)
            throws IOException {
        
        BufferedReader listReader = null;
        try {
            HashSet<String> hashSet = new HashSet<String>();

            if (list != null) {
                listReader = new BufferedReader(new StringReader(list));
                for (String inputLine; (inputLine = listReader.readLine()) != null;) {
                    StringTokenizer line = new StringTokenizer(inputLine);
                    hashSet.add(line.nextToken());
                }//for
            }//if
            return hashSet;
        }//try 
        finally {
            if (list != null)
                listReader.close();
        }//finally
        
    }//loadList
    
}//WordSet
