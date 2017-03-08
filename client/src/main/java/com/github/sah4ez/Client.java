package com.github.sah4ez;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;

import java.io.*;
import java.io.File;
import java.net.Socket;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author sah4ez
 */
public class Client {
    private static Client client;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String host, int port) throws IOException {
        try (
                Socket kkSocket = new Socket(host, port);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()));
        ) {
            this.socket = kkSocket;
            this.in = in;
            this.out = out;
            System.out.println("Start.");
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        client = new Client("localhost", 8080);
    }

    public void run() throws IOException {
        Console console = System.console();
        String command = "";
        BufferedReader stdIn = null;
        String[] commands = new String[0];

        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            if ("bye".equals(fromServer)) break;
            System.out.println(fromServer);
            if (console != null) {
                command = console.readLine().trim();
            } else {
                stdIn = new BufferedReader(new InputStreamReader(System.in));
            }
            if (console == null && stdIn != null) {
                try {
                    commands = stdIn.readLine().split(" ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                commands = command.split(" ");
            }

            switch (commands[0]) {
                case "upload": {
                    upload(commands[1]);
                    break;
                }
                case "find": {
                    find(commands[1]);
                    break;
                }
                case "download": {
                    download(commands[1]);
                    break;
                }
                case "delete": {
                    delete(commands[1]);
                    break;
                }
                case "quit": {
                    break;
                }
                default:
                    System.out.println("Unknown command.");
            }
        }


        try {
            System.out.println("Close...");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public com.github.sah4ez.File sendFile(String path) {
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
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return receive;
    }

    public void upload(String path){
        com.github.sah4ez.File file = sendFile(path);
        byte[] serialize = SerializationUtils.serialize(file);
        out.println("upload " + Arrays.toString(serialize));
    }

    public void find(String command){
//        try {
            out.println("find " + command);

//            byte[] hex = IOUtils.toByteArray(socket.getInputStream());
//            System.out.println("Find files: \n" + new String(hex));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void download(String command){
//        try {
            out.println("download " + command);
//            com.github.sah4ez.File file = (com.github.sah4ez.File)
//                    SerializationUtils.deserialize(socket.getInputStream());
//            System.out.println("File: " + file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void delete(String command){
            out.println("delete " + command);
    }
}
