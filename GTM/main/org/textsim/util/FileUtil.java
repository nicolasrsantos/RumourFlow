package org.textsim.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.regex.Pattern;

/**
 * Provide utilities for {@code File} processing.
 * 
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class FileUtil {
    
    public static String getSuffix(File file) {

        String fileName = file.getName();
        int dotPosition = fileName.lastIndexOf('.');
        return fileName.substring(dotPosition + 1, fileName.length());
    }
    
    public static String getNameWithoutSuffix(File file) {

        String fileName = file.getName();
        int dotPosition = fileName.lastIndexOf('.');
        return fileName.substring(0, dotPosition);
    }
    
    public static String getParentName(String filePathname) {
        
        return new File(filePathname).getParentFile().getName();
    }

    public static String getFilePathSafely(File file) {
        
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }
    
    public int getTotalLineCount(String file)
            throws IOException {
        
        int lineCount = 0;
        LineNumberReader lnr = null;

        try {
            lnr = new LineNumberReader(new FileReader(file));
            lnr.skip(Long.MAX_VALUE);
            lineCount = lnr.getLineNumber();

        } catch (IOException e) {
            if (lnr != null) {
                lnr.close();
            }
        }
        
        return lineCount;
    }

    /**
     * Get the {@code File} object from given pathname.
     * <p>
     * If {@code null} is given as pathname, return {@code null} instead of throwing exception.
     * </p>
     * 
     * @param  filePathname  the pathname string of a file.
     *
     * @return The file object of the given pathname, return {@code null} if the given pathname is {@code null}.
     */
    public static File getFileSafely(String filePathname) {
    
        try {
            return new File(filePathname);
        } catch (NullPointerException npe) {
            return null;
        }
    
    }

    /**
     * Get the file or directory name from given pathname.
     * <p>
     * If {@code null} is given as pathname, return {@code null} instead of throwing exception.
     * </p>
     * 
     * @param  file  a {@code File} object.
     *
     * @return The file object of the given pathname, return {@code null} if the given pathname is {@code null}.
     */
    public static String getFileNameSafely(File file) {
    
        try {
            return file.getName();
        } catch (NullPointerException npe) {
            return "";
        }
    
    }

    /**
     * Get a file with a specific suffix in the given directory.
     * <p>
     * If there is more than one file in the given directory, return one of the satisfied files.
     * </p>
     *
     * @param  directory  the pathname string of a directory.
     * @param  suffix     a suffix string.
     *
     * @return A {@code File} object in the given directory with a specific prefix.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static File getFileInDirectoryWithSuffix(String directory, String suffix)
            throws IOException {
        
        File dir = new File(directory);
        // Throws IOException if the given directory is not a valid directory
        if (!dir.isDirectory()) {
            throw new IOException(directory + " is not a valid directory.");
        }
        Pattern suffixPattern = Pattern.compile(".*" + suffix + "$");
        for (File file : dir.listFiles()) {
            // Skip the hidden file
            if (file.isHidden())
                continue;
            // Return the first file with the matching suffix
            if (suffixPattern.matcher(file.getName()).matches())
                return file;
        }
        return null;
        
    }

    /**
     * Get a file with a specific prefix in the given directory.
     * <p>
     * If there is more than one file in the given directory, return one of the satisfied files.
     * </p>
     *
     * @param  directory  the pathname string of a directory.
     * @param  prefix     a prefix string.
     *
     * @return A {@code File} object in the given directory with a specific prefix.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static File getFileInDirectoryWithPrefix(String directory, String prefix)
            throws IOException {
        
        File dir = new File(directory);
        // Throws IOException if the given directory is not a valid directory
        if (!dir.isDirectory()) {
            throw new IOException(directory + " is not a valid directory.");
        }
        Pattern suffixPattern = Pattern.compile("^" + prefix + ".*");
        for (File file : dir.listFiles()) {
            // Skip the hidden file
            if (file.isHidden())
                continue;
            // Return the first file with the matching suffix
            if (suffixPattern.matcher(file.getName()).matches())
                return file;
        }
        return null;
        
    }

    /**
     * Get the name of a file string with its prefix deleted in the given directory.
     *
     * @param  file    a {@code File} object.
     * @param  prefix  a prefix string.
     *
     * @return A file name string without prefix.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static String getFilenameWithoutPrefix(File file, String prefix) {
    
        return file.getName().substring(prefix.length(), file.getName().length());
    
    }

    /**
     * Get a file name string with its suffix deleted in the given directory.
     * <p>
     * If there is more than one file in the given directory, return one of the satisfied files.
     * </p>
     *
     * @param  file    a {@code File} object.
     * @param  suffix  a suffix string.
     *
     * @return A file name string without suffix.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static String getFilenameWithoutSuffix(File file) {
        
        String name = file.getName();
        int index = name.indexOf('.');
        if (index < 0) {
            return name;
        } else {
            return name.substring(0, index);
        }
        
    }

    /**
     * Get a file name string with its prefix deleted in the given directory.
     * <p>
     * If there is more than one file in the given directory, return one of the satisfied files.
     * </p>
     *
     * @param  filePathname  the pathname string of a file.
     * @param  prefix        a prefix string.
     *
     * @return A file name string without prefix.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static String getFilenameWithoutPrefix(String filePathname, String prefix) {
        
        File file = new File(filePathname);
        return file.getName().substring(prefix.length(), file.getName().length());
        
    }

    /**
     * Get a file name string with its suffix deleted in the given directory.
     * <p>
     * If there is more than one file in the given directory, return one of the satisfied files.
     * </p>
     *
     * @param  filePathname  the pathname string of a file.
     * @param  suffix        a suffix string.
     *
     * @return A file name string without suffix.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static String getFilenameWithoutSuffix(String filePathname, String suffix) {
        
        File file = new File(filePathname);
        return file.getName().substring(0, file.getName().length() - suffix.length());
        
    }

    /**
     * Get a file name string with its prefix deleted in the given directory.
     * <p>
     * If there is more than one file in the given directory, return one of the satisfied files.
     * </p>
     *
     * @param  filePathname  the pathname string of a file.
     * @param  suffix        a suffix string.
     *
     * @return A file name string without suffix.
     *
     * @throws IOException  if I/O error occurs.
     */
    public static String getFileAbsolutePathnameWithoutSuffix(String filePathname, String suffix) {
        
        File file = new File(filePathname);
        return file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - suffix.length());
        
    }

}
