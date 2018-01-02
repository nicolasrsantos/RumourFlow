package org.textsim.util;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.textsim.exception.ProcessException;
import org.textsim.textrt.preproc.TextrtPreprocessor;

@Deprecated
public class Corpus 
    extends PreprocessedData {
    
    //------------------------------------------------ Private Variables ------------------------------------------------\\
    
    ArrayList<Document> docs;

    //-------------------------------------------------- Constructors ---------------------------------------------------\\
    
    // Construct with pre-processed binary file
    public Corpus (File preprocFile)
            throws IOException {

        // Corpus does not have original file, while individual documents do
        super(preprocFile, null);
        read();
    }

    // Construct with data structure
    public Corpus (File preprocFile, ArrayList<Document> docs) {
        
        super(preprocFile, null);
        this.docs = docs;
    }

    // Construct with original a file set (raw data)
    public Corpus (File preprocFile, File[] originalFiles, TextrtPreprocessor preproc)
            throws ProcessException {
        
        super(preprocFile, null);
        this.docs = new ArrayList<Document>();
        for (File file: originalFiles) {
            docs.add(new Document(preprocFile, file, preproc));
        }
    }

    //---------------------------------------------------- Accessors ----------------------------------------------------\\
    
    
    public final ArrayList<Document> getDocs() {
        
        return docs;
    }
    
    //----------------------------------------------------- Others ------------------------------------------------------\\
    
    public final String getCorpusName() {
        
        return getFile().getName();
    }

    @Override
    public void write()
            throws IOException {
        
        byte[] bytes;
        int recordSize;

        BinaryFileBufferedWriter out = null;
        try {
            out = new BinaryFileBufferedWriter(getFile());
            
            // Write file header
            // Header record {recordSize(int), preprocPathname(String)}
            bytes = getFile().getAbsolutePath().getBytes();
            recordSize = bytes.length + 4;
            out.writeInt(recordSize);
            out.write(bytes);

            // Write document information
            // Document record {textMap(TIntIntHashMap)}, -1(int), rejMap(TObjectIntHashMap<String>), -1(int)}
            for (int i = 0; i < docs.size(); i++) {
                
                // Write document orginalPathname(String)
                // Record {recordSize(int), orginalPathname(String)}
                bytes = docs.get(i).getOrigFile().getAbsolutePath().getBytes();
                recordSize = bytes.length + 4;
                out.writeInt(recordSize);
                out.write(bytes);
                
                // Write textMap(TIntIntHashMap)
                // Key-value record (the inner record of the document record) {id(int), count(int)}
                // When reading, recordSize implicitly equals to 8
                int[] id = new int[docs.get(i).getTextMap().size()];
                docs.get(i).getTextMap().keys(id);
                for (int j = 0; j < id.length; j++) {
                    out.writeInt(id[j]);
                    out.writeInt(docs.get(i).getTextMap().get(id[j]));
                }
                
                // Write EOT value (-1)
                out.writeInt(-1);
                
                // Write rejMap(TObjectIntHashMap<String>)
                // Key-value record (the inner record of the document record) {recordSize(int), gram(String), count(int)}
                String[] grams = new String[docs.get(i).getRejMap().size()];
                docs.get(i).getRejMap().keys(grams);
                for (int j = 0; j < id.length; j++) {
                    bytes = grams[j].getBytes();
                    recordSize = bytes.length + 8;
                    out.writeInt(recordSize);
                    out.write(bytes);
                    out.writeInt(docs.get(i).getRejMap().get(grams[j]));
                }

                // Write EOT value (-1)
                out.writeInt(-1);
            }
            // Write EOF value (0)
            out.writeInt(0);

        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void read()
            throws IOException {
        
        docs = new ArrayList<Document>();
        
        BinaryFileFastRecordReader in = null;
        try {
            in = new BinaryFileFastRecordReader(getFile());
            
            MappedByteBuffer buffer = in.getBuffer();
            
            int recordSize;
            byte[] bytes;
            int status;

            // Read file header
            // Header record {recordSize(int), preprocPathname(String)}
            recordSize = buffer.getInt();
            if ((status = in.getBufferStatus(recordSize, 0)) == BinaryFileFastRecordReader.REFILLED) {
                buffer = in.getBuffer();
            }
            bytes = new byte[recordSize - 4];
            buffer.get(bytes);
            String preprocPathname = StringUtils.toString(bytes, null);
            setOrigFile(new File(preprocPathname));
            
            // Read document information
            // Document record {id(int), {recordSize(int), path(String)}, textMap(TIntIntHashMap)}, -1(int),
            // rejMap(TObjectIntHashMap<String>), -1(int)}
            String orginalPathname;
            TIntIntHashMap textMap;
            TObjectIntHashMap<String> rejMap;
            while (true) {

                orginalPathname = null;
                textMap = new TIntIntHashMap();
                rejMap = new TObjectIntHashMap<String>();

                // Read document orginalPathname(String)
                // Record {recordSize(int), orginalPathname(String)}
                recordSize = buffer.getInt();
                if ((status = in.getBufferStatus(recordSize, 0)) == BinaryFileFastRecordReader.REFILLED) {
                    buffer = in.getBuffer();
                }
                bytes = new byte[recordSize - 4];
                buffer.get(bytes);
                orginalPathname = StringUtils.toString(bytes, null);
                
                // Write textMap(TIntIntHashMap)
                // Key-value record (the inner record of the document record) {id(int), count(int)}
                // When reading, recordSize implicitly equals to 8
                int gramID = 0;
                int gramCount = 0;
                while (true) {
                    if ((status = in.getBufferStatus(8, 0)) == BinaryFileFastRecordReader.REFILLED) {
                        buffer = in.getBuffer();
                    }
                    if ((gramID = buffer.getInt()) == -1) {
                        // If EOT occurs, make sure the next recordSize(int) is reachable
                        if ((status = in.getBufferStatus(4, 0)) == BinaryFileFastRecordReader.REFILLED) {
                            buffer = in.getBuffer();
                        }
                        break;
                    }
                    gramCount = buffer.getInt();
                    textMap.put(gramID, gramCount);
                }

                // Write rejMap(TObjectIntHashMap<String>)
                // Key-value record (the inner record of the document record) {recordSize(int), gram(String), count(int)}
                String gram = "";
                while (true) {
                    recordSize = buffer.getInt();
                    if ((status = in.getBufferStatus(recordSize, 0, -1)) == BinaryFileFastRecordReader.REFILLED) {
                        buffer = in.getBuffer();
                    } else if (status == BinaryFileFastRecordReader.EOT) {
                        // If EOT occurs, make sure the next recordSize(int) is reachable
                        if ((status = in.getBufferStatus(4, 0)) == BinaryFileFastRecordReader.REFILLED) {
                            buffer = in.getBuffer();
                        }
                        break;
                    }
                    bytes = new byte[recordSize - 8];
                    buffer.get(bytes);
                    gram = StringUtils.toString(bytes, null);
                    gramCount = buffer.getInt();
                    rejMap.put(gram, gramCount);
                }
                
                docs.add(new Document(getFile(), new File(orginalPathname), textMap, rejMap));
            }
            
        } catch (EOFException e) {
            
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
}
