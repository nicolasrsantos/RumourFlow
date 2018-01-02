package org.textsim.textrt.proc.singlethread;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.textsim.exception.ProcessRuntimeException;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.util.BinaryFileBufferedWriter;
import org.textsim.util.BinaryFileFastRecordReader;

import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.lang3.StringUtils;

/**
 * BinaryTextReadWrite provides methods to load regular texts into an arrayList, write all texts into a file with binary representation,
 * and load binary texts file into memory as text instance
 */
public class BinaryCorpus {

    private List<TextInstance> list;
    
    public BinaryCorpus() {

        this.list = new ArrayList<TextInstance>();
    }
    
    public BinaryCorpus(List<TextInstance> list) {
        
        this.list = list;
    }
    
    /**
     * Write all the text instances into a binary file
     * 
     * @param binaryFile        The path of output binary file
     * @throws IOException
     */
    public File writeBinaryText(File binaryFile) {
        
        long recordSize;
        byte[] str;  
        int[] idkey;
        String[] strkey;
        Object[] obj;
        
        BinaryFileBufferedWriter bw = null;
        try {
            bw = new BinaryFileBufferedWriter(binaryFile);
            for(int i=0; i<list.size(); i++) {

                //Increment the recordSize; each record represents a text instance
                recordSize = 20; // 8+4+4+4 //next record length + file id + content map length + the mark of the end of the record

                //content map length
                idkey = list.get(i).getCont().keySet().toArray();
                for(int j=0; j<idkey.length; j++)
                    recordSize += idkey.length*8;
                
//                idkey = list.get(i).getCont()// TODO

                //rejection map length
//                obj = list.get(i).getRej().keySet().toArray();
//                strkey = new String[obj.length];
//                for (int g=0;g<strkey.length;g++) 
//                    strkey[g]=obj[g].toString();
                TObjectIntHashMap<String> rejMap = list.get(i).getRej();
                strkey = rejMap.keys(new String[rejMap.size()]);

                //add the length of all string in the rejmap
                for(int g=0; g<strkey.length; g++) {
                    str = strkey[g].getBytes();
                    recordSize+=str.length;
                }
                recordSize += 8*strkey.length;
                
                //write the size of a record
                bw.writeLong(recordSize);
                idkey = list.get(i).getCont().keySet().toArray();
                obj = list.get(i).getRej().keySet().toArray();
                strkey = new String[obj.length];
                for (int g=0;g<strkey.length;g++) 
                    strkey[g]=obj[g].toString();
                
                //file id;
                bw.writeInt(i);
                //content map length
                bw.writeInt(idkey.length);
                //id count
                for(int g=0; g<idkey.length; g++) {
                    bw.writeInt(idkey[g]);
                    bw.writeInt(list.get(i).getCont().get(idkey[g]));
                }
                //string count
                for(int g=0; g<strkey.length; g++) {
                    str = strkey[g].getBytes();
                    bw.writeInt(str.length);
                    //System.out.println("rejSize:"+str.length);
                    bw.write(str, 0, str.length);
                    bw.writeInt(list.get(i).getRej().get(strkey[g]));
                }
                bw.writeInt(-1);// mark the end of a record
            }
            
            //mark the end of a file
            bw.writeLong(0);
            
        } catch (IOException e) {
            throw new ProcessRuntimeException(e);
            
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new ProcessRuntimeException(e);
                }
            }
        }
        
        return binaryFile;
    }

    /**
     * Load all the texts from the input binary file into memory and store them as textInstances in an arraylist 
     * @param binaryFile        The binaryFile consisted of all the texts 
     * @return ArrayList        An ArrayList stores all the textInstances
     * @throws IOException
     */
    public static List<TextInstance> readBinaryText(String binaryFile)
            throws IOException {

        //create an arraylist to store all the text instances
        ArrayList<TextInstance> list = new ArrayList<TextInstance>();
        byte[] bytes;
        long recordSize = 0;
        int contSize = 0;
        int rejSize =0;
        int fileID;
        int wid, count;
        int totalcount;
        String rejword = null;

        BinaryFileFastRecordReader fastReader = null;
        MappedByteBuffer readerBuffer = null;
        try {
            fastReader = new BinaryFileFastRecordReader(binaryFile);
            readerBuffer = fastReader.getBuffer();
            TextInstance newtext;
            
            while(true) {
                recordSize = readerBuffer.getLong();
                //how many words in a text
                totalcount = 0;
                
                //the mark of the end of all the texts
                if(recordSize == 0){
                    break;
                }else if(readerBuffer.remaining() < recordSize){
    
                    //check if buffer remaining size is bigger than record length
                    //refill buffer and check if buffer can be refill
                    if(! fastReader.refillBuffer()){
                        break;
                    }
                    readerBuffer = fastReader.getBuffer();
                }

                //create a new text instance
                newtext = new TextInstance();
                fileID = readerBuffer.getInt();
                newtext.setFileId(fileID);
                contSize = readerBuffer.getInt();
                
                //read in content list and put id, count into a intint hashmap
                for(int i=0; i<contSize; i++){
                    wid = readerBuffer.getInt();
                    count =  readerBuffer.getInt();
                    totalcount += count;
                    newtext.getCont().put(wid, count);
                }
                
                //read in rejection list and put string , count into a objectint hashmap
                while(true) {
                    rejSize = readerBuffer.getInt();
                    //reach the end of a record
                    if(rejSize==-1) {
                        break;
                    } else {
                        bytes = new byte[rejSize];
                        readerBuffer.get(bytes);
                        rejword = StringUtils.toEncodedString(bytes, null);
                        count = readerBuffer.getInt();
                        totalcount += count;
                        newtext.getRej().put(rejword, count);
                    }
                }
                    
                newtext.setTotalcount(totalcount);
                //add the new text into an arraylist;
                list.add(newtext);
            }
        } finally {
            if (fastReader != null) {
                fastReader.close();
            }
        }

        return list;
    }
}
