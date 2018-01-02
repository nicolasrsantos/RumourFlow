package org.textsim.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 * This class creates a matrix and fill it with the similarity between each pair of labels.
 * First, read the output result of similarity between a group of files and put the similarity into the corresponding place
 * Second, fill the other half of the matrix
 * Third, write out the matrix.
 *
 * @author <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class DataFormatConverter {

    /**
     * The input file.
     */
    private File input;

    /**
     * The output file.
     */
    private File output;

    /**
     * The converted matrix.
     */
    private double[][] matrix;
    
    /**
     * Constructs the object with the input file and output directory.
     * 
     * @param  filePathname     The pathname of the input file.
     * @param  outputDirectory  The pathname of the output directory.
     */
    public DataFormatConverter(String filePathname, String outputDirectory) {
        
        this.input = new File(filePathname);
        this.output = new File(outputDirectory, input.getName() + ".graph");
    }
    
    /**
     * Constructs the object with the input file. It will output the file in the same directory.
     * 
     * @param  filePathname  The pathname of the input file.
     */
    public DataFormatConverter(String filePathname) {
        
        this(filePathname, new File(filePathname).getParent());
    }

/**
 * 
 * @param numOfDoc
 * @throws IOException
 */
    public void run(int numOfDoc)
            throws IOException {
    	
        read(numOfDoc);
        fillMatrix();
        writeOut();
    }

    /**
     * 
     * @param numOfDoc
     * @throws IOException
     */
    private void read(int numOfDoc)
            throws IOException {
        
        matrix = new double[numOfDoc][numOfDoc];
        BufferedReader in = null;
    	try {
            in = new BufferedReader(new FileReader(input));
            
            String line = null;          // The line of the document
            double val = 0;              // The similarity of the pairwise documents
            int    index1;               // The index of the first document in the document pair, starts form 0
            int    index2;               // The index of the second document in the document pair.
                                         // This value can not be smaller than the index1 of the pair.
            StringTokenizer token;
            while((line = in.readLine()) != null) {

            	token = new StringTokenizer(line,"\t");
            	index1 = Integer.parseInt(token.nextToken());
            	index2 = Integer.parseInt(token.nextToken());
                // Read value from the line
                val = Float.parseFloat(token.nextToken());
                val += Float.parseFloat(token.nextToken());
                
                // Record the value to the matrix
                matrix[index1][index2] = val;
            }

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Fill the matrix.
     */
    private void fillMatrix() {
        
        int numOfDoc = matrix[0].length;

        // Fill the diagonal with 1 (the similarity of the document itself is 1)
        for (int i = 0; i < numOfDoc; i++) {
            matrix[i][i] = 1;
        }
        
        // File the another half with the mirror value
        for (int i = 0; i < numOfDoc; i++) {
            for (int j = 0; j < numOfDoc; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][j] = matrix[j][i];
                }
            }
        }
    }

    /**
     * Write the result out.
     * 
     * @throws IOException
     */
    private void writeOut()
            throws IOException {

        int numOfDoc = matrix[0].length;
        DecimalFormat df = new DecimalFormat("#.##############"); 
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(output), 1024 * 100);
            out.write(numOfDoc + "\n");
            StringBuilder sb;
            for (int i = 0; i < numOfDoc; i++) {
            	sb = new StringBuilder();
                for (int j = 0; j < numOfDoc; j++) {
                    sb.append(df.format(matrix[i][j])).append(" ");
                }
                sb.deleteCharAt(sb.length()-1);
                out.write(sb.append('\n').toString());
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
