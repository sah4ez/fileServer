package com.github.sah4ez;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;

import java.io.File;
import java.io.*;
import java.net.Socket;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

import static com.github.sah4ez.File.OFFSET;

/**
 * @author sah4ez
 */
public class Client {
    private static Client client;
    private Socket socket;

    public Client(String host, int port) {
        try {
            String path = "/Users/aleksandr/app.log";
            socket = new Socket(host, port);
            com.github.sah4ez.File file = send(path);
            byte[] serialize = SerializationUtils.serialize(file);
            byte[] bytes = new byte[serialize.length +
                    OFFSET];
            socket.getOutputStream().write(copy(serialize, bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        client = new Client("localhost", 8080);
    }

    public byte[] copy(byte[] from, byte[] to) {
        for (int i = 0; i < from.length; i++) {
            to[i + OFFSET] = from[i];
        }
        return to;
    }

    public com.github.sah4ez.File send(String path) {
        File file = new File(path);
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fis == null) return null;

        com.github.sah4ez.File receive = null;
        try {
            byte[] bytes = IOUtils.toByteArray(fis);
            receive = new com.github.sah4ez.File(file.getName(),
                    bytes,
                    DigestUtils.md5Hex(bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (DigestException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receive;
    }

}
