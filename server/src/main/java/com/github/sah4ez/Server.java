package com.github.sah4ez;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hello world!
 */
public class Server {
    private static Logger log = LoggerFactory.getLogger(Server.class);
    private final String path;
    private final ConcurrentHashMap<String, String> md5ToFileNameMap = new ConcurrentHashMap<>();
    Protocol protocol;

    public Server(Integer port, String path) {
        this.path = path;
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
        ) {
            protocol = new Protocol(this);
            out.println("OK");
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] command = inputLine.split(" ");
                String fromServer = protocol.getResponse(command[0], inputLine);
                out.println(fromServer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server(8080, "/Users/aleksandr/testio/");
    }

    public ConcurrentHashMap<String, String> getMd5ToFileNameMap() {
        return md5ToFileNameMap;
    }

    public String getPath() {
        return path;
    }
}
