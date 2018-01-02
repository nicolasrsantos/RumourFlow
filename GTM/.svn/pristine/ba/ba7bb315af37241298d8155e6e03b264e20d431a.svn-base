package org.textsim.wordrt.proc;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

import org.textsim.textrt.preproc.SinglethreadTextrtPreprocessor;
import org.textsim.textrt.preproc.tokenizer.PennTreeBankTokenizer;
import org.textsim.util.token.DefaultTokenFilter;

public class WordSim {

  private static final Charset CHARSET = Charset.forName("UTF-8");

  private static final String SPLIT_DELIMITER = "\\s+";
  private static final String CONCAT_DELIMITER = " ";

  private Path inputPath;
  private Path outputPath;
  private Path unigramPath;
  private Path trigramPath;

  private DefaultWordRtProcessor similarityTable = null;
  private BufferedReader reader = null;
  private BufferedWriter writer = null;

  SinglethreadTextrtPreprocessor preprocessor = null;
  
  private Path toPath(String str) {
      return Paths.get(str);
  }
  
  public void setInputPath(Path inputPath) {
    this.inputPath = inputPath;
  }

  public void setInputString(String inputString) {
    this.inputPath = toPath(inputString);
  }

  /*
   * Output to the same directory as the input file by default.
   */
  public void setOutputPath(Path outputPath) {
    this.outputPath = outputPath;
  }

  public void setOutputString(String outputString) {
    this.outputPath = toPath(outputString);
  }

  public void setTrigramPath(Path trigramPath) {
    this.trigramPath = trigramPath;
  }

  public void setTrigramString(String trigramString) {
    this.trigramPath = toPath(trigramString);
  }

  public void setUnigramPath(Path unigramPath) {
    this.unigramPath = unigramPath;
  }

  public void setUnigramString(String unigramString) {
    this.unigramPath = toPath(unigramString);
  }

  public void generateOutputFile() throws IOException {
    
    assertAllNeededMembersAreSet();
    
    try {
      initializeSimilarityObjects();
      
      createInputReader();
      createOutputWriter();
      
      processHeaderToOutputFile();
      processLinesToOutputFile();
      
    } catch (IOException x) {
      System.err.format("IOException: %s%n", x);
    } finally {
      cleanUpOpenFileHandles();     
    }
  }

  private void assertAllNeededMembersAreSet() {
    if(inputPath == null) {
      throw new IllegalArgumentException("inputPath is not set");
    }
    
    if(unigramPath == null) {
      throw new IllegalArgumentException("unigramPath is not set");
    }
  }

  private void initializeSimilarityObjects() throws IOException {
    similarityTable = new DefaultWordRtProcessor(this.trigramPath);
    preprocessor = new SinglethreadTextrtPreprocessor(unigramPath.toString(),null,null,new PennTreeBankTokenizer(), new DefaultTokenFilter());
  }

  private void createInputReader() throws IOException {
    this.reader = Files.newBufferedReader(this.inputPath, CHARSET);
  }

  private void createOutputWriter() throws IOException {
    if (outputPath == null) {
      setDefaultOutputPath();
    }
    
    this.writer = Files.newBufferedWriter(outputPath, CHARSET);
  }

  private void setDefaultOutputPath() {
    String workDirectory = this.inputPath.getParent().toString();
    String outFileName = this.inputPath.getFileName().toString()
        + ".out";
    this.outputPath = Paths.get(workDirectory, outFileName);
  }

  private void processHeaderToOutputFile() throws IOException {
    String line = this.reader.readLine();
    if (line != null) {
      writer.write(line.trim() + CONCAT_DELIMITER + "Calculated\n");
    }
  }

  private void processLinesToOutputFile() throws IOException {
    String line = null;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      String[] lineTokens = line.split(SPLIT_DELIMITER);
      
      writer.write(line + CONCAT_DELIMITER + getWordSimilarity(lineTokens[0], lineTokens[1]) + "\n");
    }
  }

  private double getWordSimilarity(String word1, String word2) {
    //Lookup words in unigram file
    int word1ID = preprocessor.getWordID(word1);
    int word2ID = preprocessor.getWordID(word2);
    
    double similarity = 0;
    
    if(word1ID == word2ID){
            similarity = 1;
    } else { 
            similarity = similarityTable.sim(word1ID, word2ID);
    }
    
    return similarity;
  }

  private void cleanUpOpenFileHandles() throws IOException {
    if (reader != null)
      reader.close();
    if (writer != null)
      writer.close();
  }

}
