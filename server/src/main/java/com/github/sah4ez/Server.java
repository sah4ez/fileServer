package com.github.sah4ez;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 */
public class Server {
    private static Server server;
    private static Logger log = LoggerFactory.getLogger(Server.class);
    private ServerSocket serverSocket;
    private Socket fromClient;
    private BufferedReader in = null;
    private OutputStream out = null;

    public Server() {
    }

    public static void main(String[] args) {
        server = new Server();
        server.listen(8080);
        server.accept();
        server.IO();
    }

    public void listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
            log.info("Start listen port: {}", port);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error creating socket: \n {}", e);
        }
    }

    public void accept() {
        if (serverSocket == null) {
            log.warn("Server socket socket NULL!");
            return;
        }

        try {
            fromClient = serverSocket.accept();
            log.info("Accept client...");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Accept error: {}", e);
        }
    }

    public void IO() {
        if (fromClient == null) {
            log.warn("Client socket NULL!");
            return;
        }

        try {
            in = new BufferedReader(new InputStreamReader(fromClient.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error creating buffer reader.");
        }

        try {
            out = fromClient.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error creating buffer output.");
        }
    }

    public void getMssage() {
        if (in == null || out == null) {
            log.warn("I/O not initialized.");
            return;
        }

        String input = "";
        String output = "";

        while (true) {
            try {
                input = in.readLine();

                if (Command.DOWNLOAD.getName().equals(input)){
                   upload();
                }
            } catch (IOException e) {
                log.error("Error read line: {} ", (Object) e.getStackTrace());
            }
        }
    }

    private void upload() {
        String message = "200";
        try {
            out.write(message.getBytes());
            out.flush();

            byte[] buff = new byte[8000];

            int bytesRead = 0;

            FileOutputStream bao = new FileOutputStream("file.name");

            while((bytesRead = fromClient.getInputStream().read(buff)) != -1) {
                bao.write(buff, 0, bytesRead);
            }

            bao.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getFromClient() {
        return fromClient;
    }

    public void setFromClient(Socket fromClient) {
        this.fromClient = fromClient;
    }
}
