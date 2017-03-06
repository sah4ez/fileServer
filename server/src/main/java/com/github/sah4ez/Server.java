package com.github.sah4ez;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.github.sah4ez.File.OFFSET;

/**
 * Hello world!
 */
public class Server {
    private static Server server;
    private static Logger log = LoggerFactory.getLogger(Server.class);
    private final String path;
    private final ConcurrentHashMap<Integer, String> hashMap = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private Socket fromClient;
    private ByteArrayInputStream in = null;
    private OutputStream out = null;

    public Server(String path) {
        this.path = path;
    }

    public static void main(String[] args) {
        server = new Server("/Users/aleksandr/testio/");
        server.listen(8080);
        while (true) {
            server.accept();
            byte[] read = server.read();
            server.send(read);
        }
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

    public byte[] read() {
        if (fromClient == null) {
            log.warn("Client socket NULL!");
            return null;
        }

        byte[] bytes = null;
        try (InputStream inputStream = fromClient.getInputStream()) {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error creating buffer reader.");
        }

        if (bytes.length <= 1) return null;

        return bytes;
    }

    private void send(byte[] read) {
        if (read == null || read.length == 0) return;

        int command = read[0];

        int offset = 1;
        File file = null;

        if (Integer.compare(command, Command.UPLOAD.getId()) == 0) {
            try {
                String name = path + "test.txt";
                try {
                    file = new File(read);
                } catch (NoSuchAlgorithmException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (file == null) return;

                if (!hashMap.containsKey(file.getMd5())) {
                    FileOutputStream fos = new FileOutputStream(name);
                    fos.write(file.getBytes());
                    fos.close();

                    hashMap.putIfAbsent(file.getMd5(), file.getFileName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Integer.compare(command, Command.FIND.getId()) == 0) {
            String request = new String(read, offset, read.length);
            HashMap<Integer, String> result = new HashMap<>(25);
            hashMap.forEach((id, name) -> {
                if (name.contains(request) && result.size() < 25) {
                    result.putIfAbsent(id, name);
                }
            });
            try {
                fromClient.getOutputStream().write(result.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Integer.compare(command, Command.DOWNLOAD.getId()) == 0) {
            Integer request = Integer.valueOf(new String(read, offset, read.length));
            if (hashMap.containsKey(request)) {
                try {
                    FileInputStream fis = new FileInputStream(hashMap.get(request));
                    MessageDigest md5Algorithm = MessageDigest.getInstance("MD5");
                    byte[] bytes = IOUtils.toByteArray(fis);
                    File load = new File(hashMap.get(request),
                            bytes,
                            md5Algorithm.digest(new byte[8000], OFFSET, bytes.length));
                    fromClient.getOutputStream().write(load.getBytes());
                } catch (IOException | NoSuchAlgorithmException | DigestException e) {
                    e.printStackTrace();
                }
            }
        } else if (Integer.compare(command, Command.DELETE.getId()) == 0) {
            Integer request = Integer.valueOf(new String(read, offset, read.length));
            if (hashMap.containsKey(request)) {
                java.io.File load = new java.io.File(hashMap.get(request));
                if (load.delete()) {
                    hashMap.remove(request);
                }
            }
        }
    }

    private Function<? super String, ? extends String> search() {

        return null;
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
