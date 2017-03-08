package com.github.sah4ez;

import org.apache.commons.lang.SerializationUtils;

import java.io.*;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by aleksandr on 06.03.17.
 */
public class File implements Serializable {
    public static final long serialVersionUID = 1334423175652732159L;
    private final String fileName;
    private final byte[] bytes;
    private final String md5HEX;

    public static final int OFFSET = 1;

    public File(String fileName, byte[] bytes, String md5HEX) throws NoSuchAlgorithmException, DigestException {
        this.fileName = fileName;
        this.bytes = bytes;
        this.md5HEX = md5HEX;
    }

    public File(byte[] bytes) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes, OFFSET, bytes.length);
//        ObjectInput in = new ObjectInputStream(inputStream);
        File file = (File) SerializationUtils.deserialize(bytes);
        this.fileName = file.getFileName();
        this.md5HEX = file.getMd5HEX();
        this.bytes = file.getBytes();
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getMd5HEX() {
        return md5HEX;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("File{");
        sb.append("fileName='").append(fileName).append('\'');
        sb.append(", md5HEX='").append(md5HEX).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

