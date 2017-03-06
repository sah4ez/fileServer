package com.github.sah4ez;

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
    private final int md5;

    public static final int OFFSET = 1;

    public File(String fileName, byte[] bytes, int md5) throws NoSuchAlgorithmException, DigestException {
        this.fileName = fileName;
        this.bytes = bytes;
        this.md5 = md5;
    }

    public File(byte[] bytes) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes, OFFSET, bytes.length);
        ObjectInput in = new ObjectInputStream(inputStream);
        File file = (File) in.readObject();
        this.fileName = file.getFileName();
        this.md5 = file.getMd5();
        this.bytes = file.getBytes();
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
//        ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
//        ObjectInputStream oos = null;
//        try {
//            oos = new ObjectInputStream(baos);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (oos == null) return new byte[0];

//        return IOUtils.toByteArray();
        return bytes;
    }

    public int getMd5() {
        return md5;
    }

    public byte[] removeOffset(byte[] from, byte[] to){
        for (int i = OFFSET; i < from.length; i++){
            to[i-1] = from[i];
        }
        return to;
    }
}

