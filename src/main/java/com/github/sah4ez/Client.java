package com.github.sah4ez;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * @author sah4ez
 */
public class Client {
    public static final Logger log = LoggerFactory.getLogger(Client.class);
    public static final String SPACE = " ";
    private static Client client;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private StringJoiner stringJoiner;

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
            log.info("Connect to {}:{} successful", host, port);
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        client = new Client("localhost", 8080);
    }

    public void run() throws IOException {
        BufferedReader stdIn = null;

        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            if (Protocol.QUIT.equals(fromServer)) break;
            log.info("Response: {} ", fromServer);

            stdIn = new BufferedReader(new InputStreamReader(System.in));
            try {
                execute(stdIn.readLine().split(" "));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        assert stdIn != null;
        stdIn.close();
    }

    public void execute(String[] commands){
        if (commands == null || commands.length !=2 ) {
            log.error("Error commands");
            return;
        }

        switch (commands[0]) {
            case Protocol.UPLOAD: {
                upload(commands[1]);
                break;
            }
            case Protocol.FIND: {
                find(commands[1]);
                break;
            }
            case Protocol.DOWNLOAD: {
                download(commands[1]);
                break;
            }
            case Protocol.DELETE: {
                delete(commands[1]);
                break;
            }
            case Protocol.QUIT: {
                close();
                break;
            }
            default:
                log.info("Unknown command");
        }
    }

    public void close() {
        try {
            System.out.println("Close...");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File sendFile(String path) {
        java.io.File file = new java.io.File(path);
        com.github.sah4ez.File receive = null;
        try (InputStream fis = new FileInputStream(file)) {

            byte[] bytes = IOUtils.toByteArray(fis);
            receive = new com.github.sah4ez.File(file.getName(), bytes, DigestUtils.md5Hex(bytes));
        } catch (IOException | DigestException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return receive;
    }

    public void upload(String path) {
        com.github.sah4ez.File file = sendFile(path);
        byte[] serialize = SerializationUtils.serialize(file);
        out.println(join(Protocol.UPLOAD, Arrays.toString(serialize)));
    }

    public void find(String command) {
        out.println(join(Protocol.FIND, command));
    }

    public void download(String command) {
        out.println(join(Protocol.DOWNLOAD, command));
//        File file = (File) SerializationUtils.deserialize(Protocol.getBytes(command));
//        TODO: write saving file
//        file.save();
    }

    public void delete(String command) {
        out.println(join(Protocol.DELETE, command));
    }

    public String join(String command, String value) {
        stringJoiner = new StringJoiner(SPACE);
        stringJoiner.add(command).add(value);
        return stringJoiner.toString();
    }
}
