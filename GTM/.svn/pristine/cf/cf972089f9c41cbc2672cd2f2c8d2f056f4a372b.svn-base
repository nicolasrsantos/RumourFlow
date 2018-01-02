package org.textsim.textrt.proc.singlethread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * This class creates text instance which has a file name, file id, total count of words, same words with another file
 * @author Vivian
 * @version 0.3
 * @since June 10/2013
 */
public class TextInstance {
	
    private static int id = 0;

    //Maps id numbers to filenames
    public static TreeMap<Integer, String> idName = new TreeMap<Integer, String>();
    
	private TIntIntHashMap textCont; 
	private TObjectIntHashMap<String> textRej;
	private int fileID;
	private int totalcount = 0;
	private int numofSame = 0;
	private String fileName;
	
	String text;

	/**
	 * Null constructor: initialize every variables
	 * @throws IOException 
	 */
	public TextInstance() {

	    this.textCont = new TIntIntHashMap();
	    this.textRej = new TObjectIntHashMap<String>();
	}

	/**
	 * This constructor creates a new textInstance and content
	 * 
	 * @param textdir      the directory of texts
	 * @param rejdir       the directory of rejection texts
	 * @param contentFile  the name of text
	 * @param rejectFile   the name of rejection text
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	// TODO Delete. Used only by tester.
	@Deprecated
	public TextInstance(String textdir, String rejdir, String contentFile, String rejectFile)
	        throws IOException {

	    this (textdir + File.separator + contentFile, rejdir + File.separator + rejectFile);
	}

	public TextInstance(String contentFile, String rejectFile)
	        throws IOException {

	    this(null, contentFile, rejectFile);
	}

	public TextInstance(File file, TIntIntHashMap contMap, TObjectIntHashMap<String> rejMap) {

	    this.fileID = getID();
	    this.fileName = file.getName();
	    this.textCont = contMap;
	    this.textRej = rejMap;
	    this.totalcount = sum(contMap.values()) + sum(rejMap.values());
	    
	    //Add entry to idName
	    //System.out.println("Entering id");
	    idName.put(this.fileID, this.fileName);
	}
	
	/*
	 * Create a TextInstance using a String rather than a file
	 */
	public TextInstance(String text, TIntIntHashMap contMap, TObjectIntHashMap<String> rejMap) {

	    this.fileID = getID();
	    this.text = text;
	    this.textCont = contMap;
	    this.textRej = rejMap;
	    this.totalcount = sum(contMap.values()) + sum(rejMap.values());
	    
	    //Add entry to idName
	    //System.out.println("Entering id");
	    //idName.put(this.fileID, this.fileName);
	}
	
	public TextInstance(String fileName, String contentFile, String rejectFile)
	        throws IOException {

	    this.fileName = fileName;
	    this.fileID = getID();
	    if (contentFile != null) {
    		readinTextContent(contentFile);
	    } else {
	        this.textCont = new TIntIntHashMap();
	    }
	    if (rejectFile != null) {
    		readinRejectList(rejectFile);
	    } else {
	        this.textRej = new TObjectIntHashMap<String>();
	    }
	    
	    //Add entry to idName
	    //System.out.println("Entering id");
	    idName.put(this.fileID, this.fileName);
	}

	/**
	 * Set File Name
	 * @para fileName
	 */
	protected void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Set File ID
	 * @param fileID
	 */
	protected void setFileId(int fileID) {

	    this.fileID = fileID;
	}
	
	/**
	 * Generate an id for newly constructed TextInstances
	 * Not currently used
	 * @return id
	 */
	private static int getID() {
	    
	    return id++;
	}
	
	/**
	 * Get File ID
	 * @return fileID
	 */
	public int getFileID() {

		return fileID;
	}

	/**
	 * Set the total count of words in this file
	 * @param totalcount
	 */
	protected void setTotalcount(int totalcount) {

		this.totalcount = totalcount;
	}

	/**
	 * Get the total count of words in this file
	 * @return totalcount
	 */
	public int getTotalcount() {

		return totalcount;
	}

	/**
	 * Get the Hash Map which stores the information of the text 
	 * @return textCont hashmap
	 */
	protected TIntIntHashMap getCont() {

		return textCont;
	}

	/**
	 *  Get the Hash Map which stores the information of the rejection text
	 * @return textRej hashmap
	 */
	protected TObjectIntHashMap<String> getRej() {

		return textRej;
	}

	/**
	 * Get the number of same words between two files
	 * @return number of same words
	 */
	public int getSameword() {

		return numofSame;
	}

	/**
	 * Set the number of same words
	 * @param numofSame
	 */
	protected void setSameword(int numofSame) {

		this.numofSame = numofSame;
	}

	/**
	 * Read in text content from files
	 * @param dir          the directory stores text files
	 * @param contentFile  the name of the text file
	 */
	private void readinTextContent(String contPath)
	        throws IOException {

		textCont = new TIntIntHashMap();
		BufferedReader text = new BufferedReader(new FileReader(contPath));
		StringTokenizer token;
		String line;
		int id,count;

		//skips the comments
		for (int i = 0; i < 6; i++)
    		text.readLine();
		
		while ((line = text.readLine()) != null) {
			token = new StringTokenizer(line);
			id = Integer.parseInt(token.nextToken());
			count = Integer.parseInt(token.nextToken());
			//record = new Record(id,count);
			textCont.put(id,count);
			totalcount += count;
		}
		text.close();
	}
	
	
	/**
	 * Read in rejection text file
	 * @param dir         the name of the directory stores text rejection files
	 * @param rejectFile  the name of the rejection text file
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void readinRejectList(String rejPath)
	        throws NumberFormatException, IOException {

		textRej = new TObjectIntHashMap<String>();
		BufferedReader text = new BufferedReader(new FileReader(rejPath));
		StringTokenizer token;
		String line,str;
		int count;

		for (int i = 0; i < 6; i++)
    		text.readLine();

		while((line=text.readLine())!=null) {
			token = new StringTokenizer(line);
			str = token.nextToken();
			count = Integer.parseInt(token.nextToken());
			textRej.put(str,count);
			totalcount += count;
		}
		text.close();
	}//readinRejectList

	/**
	 * Deep clone a text instance
	 * @return a text instance
	 * @throws IOException 
	 */
	public TextInstance deepClone() {

		TextInstance copy = new TextInstance();
		copy.setFileId(fileID);
		copy.setTotalcount(totalcount);
		
		int[] keyCollection;
		Object[] keyCollection2;

		/*clone textcont hashMap*/
		copy.textCont =  new TIntIntHashMap();
		
		keyCollection = textCont.keys();
		for(int i=0; i < keyCollection.length; i++) {
			copy.textCont.put(keyCollection[i], textCont.get(keyCollection[i]));
		}

		/*clone textRej hashMap*/
		if(textRej != null) {
			copy.textRej= new TObjectIntHashMap<String>();
			keyCollection2 =textRej.keys();
			for(int i=0; i < keyCollection2.length; i++) {
				copy.textRej.put((String) keyCollection2[i], textRej.get(keyCollection2[i]));
			}
		}
		return copy;
	}

	/**
	 * Compares two text and deletes the same words and decides if a record is added to another text instance
	 * @param record
	 * @return boolean
	 */
	public void compareTexts(TextInstance textB) {
		
		int countInA,countInB;
		/* Bug fixed, initialize numofSame for each text comparison*/
	//	numofSame = 0;
		
		int[] idKeyB = textB.getCont().keySet().toArray();
		TObjectIntHashMap<String> rejMap = textB.getRej();
		String[] strKeyB = rejMap.keys(new String[rejMap.size()]);

		for (int i = 0; i < idKeyB.length; i++) {
			if (textCont.containsKey(idKeyB[i])) {
				countInA = textCont.get(idKeyB[i]);
				countInB = textB.getCont().get(idKeyB[i]);
				if (countInA < countInB) {
					textCont.remove(idKeyB[i]);
					textB.getCont().put(idKeyB[i],countInB-countInA);
					numofSame += countInA;
				} else if (countInA > countInB) {
					textCont.put(idKeyB[i], countInA - countInB);
					numofSame += countInB;
					textB.getCont().remove(idKeyB[i]);
				} else {
					textCont.remove(idKeyB[i]);
					textB.getCont().remove(idKeyB[i]);
					numofSame += countInA;
				}
			}
		}
		
		for (int i = 0; i < strKeyB.length; i++) {
			if (textRej.containsKey(strKeyB[i])) {
				countInA=textRej.get(strKeyB[i]);
				countInB=textB.getRej().get(strKeyB[i]);
				if(countInA < countInB) {
					textRej.remove(strKeyB[i]);
					textB.getRej().put(strKeyB[i], countInB-countInA);
					numofSame += countInA;
				} else if (countInA > countInB) {
					textB.getRej().remove(strKeyB[i]);
					textRej.put(strKeyB[i], countInA-countInB);
					numofSame += countInB;
				} else {
					textRej.remove(strKeyB[i]);
					textB.getRej().put(strKeyB[i], countInB-countInA);
					numofSame += countInA;
				}
			}
		}//for
	}//compareTexts
	
	
	/**
	 * Write the contents of idName to the specified file, one entry per line, in the format:
	 * id,filename
	 */
	/*public static void writeIdNametoFile(File file){
		try {
			FileWriter writer = new FileWriter(file);
			
			for (Map.Entry<Integer, String> entry : idName.entrySet()){
				writer.append(entry.getKey() + "," + entry.getValue() + "\n");
			}//for
			
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//writeIdNametoFile*/
	
	/**
	 * Write the contents of idName to two separate files in the directory specified by outputdir.
	 * The first file, idName.txt, contains ids paired with names in the form:
	 * id,filename
	 * With one entry per line. The second file, <inputdirectory>.rlabel, contains names only, one entry per line.
	 * @param outputdir the directory to place the files in
	 * @param inputdir the input directory name. Used for naming of the rlabel file.
	 */
	public static void writeIdNametoFile(String outputDir, String inputDirName){
		try {
			FileWriter idNameWriter = new FileWriter(new File(outputDir+"/idName.txt"));
			FileWriter nameWriter = new FileWriter(new File(outputDir+inputDirName+".rlabel"));
			for (Map.Entry<Integer, String> entry : idName.entrySet()){
				idNameWriter.append(entry.getKey() + "," + entry.getValue() + "\n");
				nameWriter.append(entry.getValue() + "\n");
			}
			idNameWriter.close();
			nameWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve the contents of idName from the specified file 
	 */
	public static void readIdNamefromFile(File file) {

		Scanner reader;
		try {
			reader = new Scanner(file);
			while (reader.hasNext()){
				StringTokenizer tokenizer = new StringTokenizer(reader.nextLine(), ",");
				idName.put(Integer.parseInt(tokenizer.nextToken()), tokenizer.nextToken());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	private int sum(int[] values) {

		int counter = 0;
		for(int i = 0; i < values.length; i++){
			counter += values[i];
		}
		return counter;
	}
	
}
