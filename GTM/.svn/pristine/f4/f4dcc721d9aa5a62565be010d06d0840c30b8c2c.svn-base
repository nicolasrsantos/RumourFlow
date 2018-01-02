package org.textsim.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class Header {

    private ArrayList<HeaderInfo> infos;

    public Header() {
        
        this.infos = new ArrayList<HeaderInfo>();
    }
    
    public Header(HeaderInfo... infos) {
        
        this();
        for (HeaderInfo info : infos) {
            this.infos.add(info);
        }
    }

    public void read(BinaryFileFastRecordReader bffrr, int eof)
            throws IOException {
        
        int count = readCount(bffrr, eof);
        for (int i = 0; i < count; i++) {
            infos.add(readHeaderInfo(bffrr, eof));
        }
    }

    private int readCount(BinaryFileFastRecordReader bffrr, int eof)
            throws IOException {
        
        MappedByteBuffer buffer = bffrr.getBuffer();
        int value = buffer.getInt();
        if (bffrr.getBufferStatus(8, eof) == BinaryFileFastRecordReader.REFILLED) {
            buffer = bffrr.getBuffer();
        }
        return value;
    }

    private HeaderInfo readHeaderInfo(BinaryFileFastRecordReader bffrr, int eof)
            throws IOException {
        
        return new HeaderInfo(
                readString(bffrr, eof),
                readString(bffrr, eof));
    }

    private String readString(BinaryFileFastRecordReader bffrr, int eof)
            throws IOException {
        
        MappedByteBuffer buffer = bffrr.getBuffer();
        int size = buffer.getInt();
        if (bffrr.getBufferStatus(size, eof) == BinaryFileFastRecordReader.REFILLED) {
            buffer = bffrr.getBuffer();
        }
        byte[] bytes = new byte[size - 4];
        buffer.get(bytes);
        return StringUtils.toEncodedString(bytes, null);
    }
    
    public void write(DataOutputStream dos)
            throws IOException {
        
        dos.writeInt(this.infos.size());
        for (HeaderInfo info : this.infos) {
            writeHeaderInfo(dos, info);
        }
    }
    
    private void writeHeaderInfo(DataOutputStream dos, HeaderInfo info)
            throws IOException {
        
            writeString(dos, info.key);
            writeString(dos, info.value);
    }
    
    private void writeString(DataOutputStream dos, String str)
            throws IOException {
        
        byte[] bytes = str.getBytes();
        dos.writeInt(bytes.length + 4);
        dos.write(bytes);
    }
    
    public String toString() {
        
        String display = new String();
        for (HeaderInfo info : infos) {
            display += info.key + "  " + info.value + '\n';
        }
        return display;
    }
    
    public static final class HeaderInfo {
        
        private String key;
        private String value;
        
        public HeaderInfo(String key, String value) {
            
            this.key = key;
            this.value = value;
        }
    }
}
