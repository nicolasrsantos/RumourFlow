package org.textsim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;


/**
 * SmallTextFastLineReader is a tool which uses the Java NIO features to read through text files
 *
 * @author 		YAO
 * @version    	0.1
 * @since		0.1
 */
public class SmallTextFastLineReader {
	private File binaryFile;
	private FileChannel fileChannel;
	private FileInputStream fileStream;
	private MappedByteBuffer readBuffer;
	/*buffer limit is defined as the maximum buffersize can be allowed
	 * this is used to avoid outOfMemory error while mapping files from disk to memory
	 * */
	private final long bufferLimit = 50*1024*1024; //50MB Buffer Limit
	private long bufferSize;
	private long fileSize;
	private long fileRemainSize;
	private long position;
	
	/**
	 * Constructors a file reader with a given file path
	 * @param fileName : input file path
	 * @throws IOException
	 */
	public SmallTextFastLineReader(String fileName) throws IOException {
		binaryFile = new File(fileName);
		fileStream = new FileInputStream(binaryFile);
		fileChannel = fileStream.getChannel();
		fileSize = fileChannel.size();
		resetReader();
		getByteBuffer(fileSize);
	}
	
	/**
	 * Read a line from input file.
	 *  This method will always return lines which are not empty
	 * @return line  	a string which represents the line contents, or null if reached the end of file
	 * @throws IOException
	 */
	public String readLine() throws IOException{
		String line = null;
		char ch;
		
		StringBuilder nStr;
		/*check if the reader reached the end of file*/
		try{
			ch=(char) readBuffer.get();
			nStr = new StringBuilder();
		}catch(BufferUnderflowException e){
			return  null;
		}
		/*read charactres until hit a change line char*/
		while(ch != '\n' ){
			try{				
				nStr.append(ch);
				ch=(char) readBuffer.get();
			}catch(BufferUnderflowException e){
				if(!refillBuffer()){
					break;
				}
			}
		}
		/*build output string*/
		line = nStr.toString();
		/*check if the readin line is empty, if it is, re-read another line until a none empty line or EOF*/
		while(line!=null && line.trim().isEmpty()){
			line = readLine();
		}
		
		return line.trim();
	}
	
	/**
	 * Create ByteBuffer which maps data from disk to memory
	 * @param recordSize : define a single record size to read
	 * @return readBuffer 	a MappedByteBuffer for reading
	 * @throws IOException
	 */
	private MappedByteBuffer getByteBuffer(long recordSize) throws IOException{
		long loadFactor;
		resetReader();
		
		/*determine buffer size*/
		if(fileSize < bufferLimit){
			bufferSize = fileSize;
		}else{
			loadFactor = bufferLimit/recordSize;
			bufferSize = recordSize*loadFactor;
		}
		/*map data from filechannel to buffer*/
		readBuffer = fileChannel.map(MapMode.READ_ONLY,position, bufferSize);
		
		return readBuffer;
	}
	
	/**
	 * refill buffer if the content in buffer has been all retrieved
	 * @return a boolean check if the reader reaches the end of file
	 * @throws IOException
	 */
	private boolean refillBuffer() throws IOException{
		position += bufferSize;
		fileRemainSize -= bufferSize;
		
		if(fileRemainSize > 0){
			
			if(bufferSize > fileRemainSize){
				bufferSize = fileRemainSize;
			}
			
			readBuffer = fileChannel.map(MapMode.READ_ONLY,position, bufferSize);
			
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Reset the reader read from the beginning of the file
	 */
	private void resetReader(){
		position = 0;
		fileRemainSize = fileSize;
		readBuffer = null;
	}
	
	/**
	 * Close the read in file
	 */
	public void close() throws IOException{
		fileChannel.close();
		fileStream.close();
	}

}
