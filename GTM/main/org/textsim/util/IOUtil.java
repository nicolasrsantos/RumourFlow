package org.textsim.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Provide utilities for I/O.
 * 
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class IOUtil {

    /**
     * Check whether a pathname string has suffix.
     *
     * @param  filePathname  the pathname string of a file.
     *
     * @return A boolean indicate whether the file has suffix.
     */
    public static boolean hasSuffix(String filePathname) {
        
        return filePathname.matches(".*-.*$");
        
    }
    
    /**
     * Write header for the pre-processed text files.
     * <p>
     * Header for the following files:
     * <ul>
     * <li>pre-processed document file (<tt>-doc.ic</tt>)
     * <li>pre-processed stop word file (<tt>-sw.ic</tt>)
     * <li>pre-processed numeric word list (<tt>-num.gg</tt>)
     * <li>pre-processed reject word file (<tt>-rej.ic</tt>)
     * </ul>
     * </p>
     * 
     * @param  bw             the {@code BufferedWriter} to be written.
     * @param  doc            the {@code File} object of the pre-processing document.
     * @param  stopWordList   the {@code File} object of the stop word list.
     * @param  excepWordList  the {@code File} object of the exception word list.
     * @param  unigramFile    the {@code File} object of the pre-processed unigram corpus.
     * @param  fileType       the string indicate the type of the writing file, the value could be <tt>doc</tt>, <tt>sw</tt>
     *                        or <tt>rej</tt>.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static void writePreprocDocHeader(BufferedWriter bw, File doc, File stopWordList, File excepWordList,
            File unigramFile, String fileType)
            throws IOException {
        
        bw.write("# File Type:   ");
        if (fileType.equals("doc")) {
            bw.write("doc.ic\n");
        } else if (fileType.equals("sw")) {
            bw.write("sw.ic\n");
        } else if (fileType.equals("num")) {
            bw.write("num.gg\n");
        } else if (fileType.equals("rej")) {
            bw.write("rej.gc\n");
        }
        bw.write("# Document:    " + doc.getName() + "\n");
        bw.write("# Stop Word:   " + FileUtil.getFileNameSafely(stopWordList) + "\n");
        bw.write("# excep Word:  " + FileUtil.getFileNameSafely(excepWordList) + "\n");
        bw.write("# Unigram:     " + unigramFile.getName() + "\n");
        bw.write("# Date:        " + getCurrentTime(true) + "\n");

    }
    
    /**
     * Write header for the pre-processed unigram corpus files.
     * <p>
     * Header for the following files:
     * <ul>
     * <li>pre-processed unigram data file (<tt>-uni.gic</tt>)
     * <li>pre-processed unigram count file (<tt>-uni.c</tt>)
     * <li>unaccepted unigram data file (<tt>-uni.un</tt>)
     * </ul>
     * </p>
     * 
     * @param  bw           the {@code BufferedWriter} to be written.
     * @param  unigramData  the pre-processing unigram data File.
     * @param  fileType     the string indicate the type of the writing file, the value could be <tt>data</tt>,
     *                      <tt>count</tt> or <tt>unaccept</tt>.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static void writePreprocUnigramTextHeader(BufferedWriter bw, File unigramData, String fileType)
            throws IOException {

        bw.write("# File Type:     ");
        if (fileType.equals("data")) {
            bw.write("uni.gic\n");
        } else if (fileType.equals("count")) {
            bw.write("uni.c\n");
        } else if (fileType.equals("unaccept")) {
            bw.write("uni.un\n");
        }
        bw.write("# Unigram Data:  " + unigramData.getName() + "\n");
        bw.write("# Date:          " + getCurrentTime(true) + "\n");
    }
    
    public static void writePreprocUnigramBinaryHeader(DataOutputStream dos, File unigramData, String fileType)
            throws IOException {
    
        String headerStr = "";
        headerStr += "# File Type:     ";
        if (fileType.equals("data")) {
            headerStr += "uni.gic\n";
        } else if (fileType.equals("count")) {
            headerStr += "uni.c\n";
        } else if (fileType.equals("unaccept")) {
            headerStr += "uni.un\n";
        }
        headerStr += "# Unigram Data:  " + unigramData.getName() + "\n";
        headerStr += "# Date:          " + getCurrentTime(true) + "\n";

        byte[] headerBytes = headerStr.getBytes();

        dos.writeLong(headerBytes.length + 8);
        dos.write(headerBytes, 0, headerBytes.length);
    }

    /**
     * Write header for the pre-processed unigram corpus files.
     * <p>
     * Header for the following files:
     * <ul>
     * <li>pre-processed trigram data file (<tt>-tri.icp</tt>})
     * <li>pre-processed trigram count file (<tt>-tri.c</tt>)
     * <li>unaccepted trigram data file (<tt>-tri.c</tt>)
     * </ul>
     * </p>
     * 
     * @param  bw           the {@code BufferedWriter} to be written.
     * @param  unigramData  the {@code File} object of the pre-processing trigram data.
     * @param  fileType     the string indicate the type of the writing file, the value could be <tt>data</tt>, <tt>count</tt>
     *                      or <tt>unaccept</tt>.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static void writePreprocTrigramHeader(BufferedWriter bw, File unigramData, File trigramData, String fileType)
            throws IOException {

        bw.write("# File Type:     ");
        if (fileType.equals("data")) {
            bw.write("tri.icp\n");
        } else if (fileType.equals("count")) {
            bw.write("tri.c\n");
        } else if (fileType.equals("unaccept")) {
            bw.write("tri.un\n");
        }
        bw.write("# Unigram Data:  " + unigramData.getName() + "\n");
        bw.write("# Trigram Data:  " + trigramData.getName() + "\n");
        bw.write("# Date:          " + getCurrentTime(true) + "\n");
    }

    /**
     * Read pre-processed file with header.
     *
     * @param  file  the {@code File} object of a pre-processed file.
     *
     * @return A {@code BufferedReader} of the given file with header omitted.
     */
    public static BufferedReader getBufferedFileReader(File file, String fileType)
            throws IOException {
        
        // Get the number of lines of the header
        int linesOmit = 0;
        if (fileType.equals("doc"))     linesOmit = 6;
        else if(fileType.equals("tri")) linesOmit = 4;
        else if(fileType.equals("uni")) linesOmit = 3;
        
        // Omit the header at the beginning of the file
        BufferedReader br = new BufferedReader(new FileReader(file));
        for (int i = 0; i < linesOmit; i++)
            br.readLine();
        
        return br;
    }
    
    /**
     * Read pre-processed file with header.
     *
     * @param  filePathname  the pathname string of a pre-processed file.
     *
     * @return A {@code BufferedReader} of the given file with header omitted.
     */
    public static BufferedReader getBufferedFileReader(String filePathname, String fileType)
            throws IOException {
        
        return getBufferedFileReader(new File(filePathname), fileType);
    }
    
    /**
     * Get current date and time.
     * 
     * @param   withSpace  a boolean indicate whether the output string contains space. If this parameter is false,
     *                     it will use '_' replace space.
     *
     * @return The string contains current data and time information.
     */
    public static String getCurrentTime(boolean withSpace) {

        SimpleDateFormat dateFormat;
        if (withSpace) {
            dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
        }
        return dateFormat.format(Calendar.getInstance().getTime());

    }

}
