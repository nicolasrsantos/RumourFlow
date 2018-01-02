package org.textsim.textrt.proc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor;
import org.textsim.util.FileUtil;

/**
 * This class defines the command line interface for the {@code org.textsim.textt.proc} package.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class TextrtProcEntry {
    
    public static void runSinglethread(String text1Path, String text2Path, String binTrigram) 
            throws IOException {
        
        // Find -rej.gc files
        String text1DocPathname = null, text2DocPathname = null, text1RejPathname = null, text2RejPathname = null;
        if (new File(text1Path).isDirectory()) {
            text1DocPathname = FileUtil.getFileInDirectoryWithSuffix(text1Path, "-doc.ic").getAbsolutePath();
            text1RejPathname = FileUtil.getFileInDirectoryWithSuffix(text1Path, "-rej.gc").getAbsolutePath();
        } else if(new File(text1Path).isFile()) {
            text1DocPathname = text1Path;
            text1RejPathname = FileUtil.getFileAbsolutePathnameWithoutSuffix(text1Path, "-doc.ic") + "-rej.gc";
        } else {
            text1DocPathname = new File(text1Path) + "-doc.ic";
            text1RejPathname = new File(text1Path) + "-rej.gc";
        }
        if (new File(text1Path).isDirectory()) {
            text2DocPathname = FileUtil.getFileInDirectoryWithSuffix(text1Path, "-doc.ic").getAbsolutePath();
            text2RejPathname = FileUtil.getFileInDirectoryWithSuffix(text1Path, "-rej.gc").getAbsolutePath();
        } else if(new File(text1Path).isFile()) {
            text2DocPathname = text2Path;
            text2RejPathname = FileUtil.getFileAbsolutePathnameWithoutSuffix(text2Path, "-doc.ic") + "-rej.gc";
        } else {
            text2DocPathname = new File(text1Path) + "-doc.ic";
            text2RejPathname = new File(text1Path) + "-rej.gc";
        }
        
        runSinglethread(text1DocPathname, text1RejPathname, text2DocPathname, text2RejPathname, binTrigram);
        
    }
        
    public static void runSinglethread(String text1DocPathname, String text1RejPathname, String text2DocPathname,
            String text2RejPathname, String binTrigram)
            throws IOException {
        
        try {
            System.out.println();
            System.out.println("TEXT RELATEDNESS REPROCESS");
            System.out.println("------------------------------------------------------------------------------");
            
            long initTime, startTime, endTime;
            initTime = System.currentTimeMillis();
            
            startTime = initTime;
            System.out.print("Loading word relatedness data..");
            SinglethreadTextRtProcessor sim = new SinglethreadTextRtProcessor(Paths.get(binTrigram));
            TextInstance text1 = new TextInstance(text1DocPathname, text1RejPathname);
            TextInstance text2 = new TextInstance(text2DocPathname, text2RejPathname);
            endTime = System.currentTimeMillis();
            System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");
            
            startTime = endTime;
            System.out.print("The relatedness is ");
            // Print the result in the console
            System.out.println(sim.computeTextRT(text1, text2));
            endTime = System.currentTimeMillis();
            System.out.println("DONE!\n\tTime taken: " + ((endTime - startTime) / 1000.0) + " second.");
            
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("PROCESS Done! Total time taken: " + ((endTime - initTime) / 1000.0) + " second.");
            System.out.println();
        } catch (IOException e) {
            System.out.println();
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("PROCESS fail");
            System.out.println();
            throw e;
        }
        
    }

}
