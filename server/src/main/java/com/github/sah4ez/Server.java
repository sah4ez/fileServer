package com.github.sah4ez;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class Server
{
    private static Server server;
    private ServerSocket serverSocket;
    private Socket fromClient;
    private static Logger log = LoggerFactory.getLogger(Server.class);
    public static void main( String[] args )
    {
        server = new Server();
        server.listen(8080);
        server.accept();
    }

    public Server() {
    }

    public void listen(int port){
        try {
            serverSocket = new ServerSocket(port);
            log.info("Start listen port: {}", port);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error creating socket: \n {}", e);
        }
    }

    public void accept(){
        try {
            fromClient = serverSocket.accept();
            log.info("Accept client...");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Accept error: {}", e);
        }
    }
}
