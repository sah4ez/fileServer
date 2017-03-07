package com.github.sah4ez;

import org.apache.commons.codec.digest.DigestUtils;
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

/**
 * Hello world!
 */
public class Server {
    private static Server server;
    private static Logger log = LoggerFactory.getLogger(Server.class);
    private final String path;
    private final ConcurrentHashMap<String, String> md5ToFileNameMap = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private Socket fromClient;

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

    public void initMap(){

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
        if (getServerSocket() == null) {
            log.warn("Server socket socket NULL!");
            return;
        }

        try {
            fromClient = getServerSocket().accept();
            log.info("Accept client...");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Accept error: {}", e);
        }
    }

    public byte[] read() {
        if (getFromClient() == null) {
            log.warn("Client socket NULL!");
            return null;
        }

        byte[] bytes = null;
        try (InputStream inputStream = getFromClient().getInputStream()) {
            if (inputStream != null) {
                bytes = IOUtils.toByteArray(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error creating buffer reader.");
        }

        if (bytes == null || bytes.length <= 1) return null;

        return bytes;
    }

    private void send(byte[] read) {
        if (read == null || read.length == 0) return;

        int command = read[0];

        if (Integer.compare(command, Command.UPLOAD.getId()) == 0) {
            upload(read);
        } else if (Integer.compare(command, Command.FIND.getId()) == 0) {
            find(read);
        } else if (Integer.compare(command, Command.DOWNLOAD.getId()) == 0) {
            download(read);
        } else if (Integer.compare(command, Command.DELETE.getId()) == 0) {
            delete(read);
        }
    }

    public void upload(byte[] bytes) {
        File file = file(bytes);
        if (file == null) {
            log.info("Create nullable file.");
            return;
        }

        String name = path + file.getFileName();
        if (!getMd5ToFileNameMap().containsKey(file.getMd5HEX())) {
            save(outStream(name), file.getBytes());

            getMd5ToFileNameMap().putIfAbsent(file.getMd5HEX(), path + file.getFileName());
            log.info("Upload file {} with md5 {}", path + file.getFileName(), file.getMd5HEX());
        }
    }

    public void find(byte[] bytes) {
        String request = new String(bytes, File.OFFSET, bytes.length - File.OFFSET);

        if ("".equals(request)) {
            log.info("Found empty name.");
            return;
        }

        log.info("Find file with name {}", request);
        HashMap<String, String> result = new HashMap<>(25);
        getMd5ToFileNameMap().forEach((id, name) -> {
            if (name.contains(request) && result.size() < 25) {
                result.putIfAbsent(id, name);
            }
        });
        response(result.toString());
    }

    public void download(byte[] bytes) {
        String request = new String(bytes, File.OFFSET, bytes.length - File.OFFSET);
        if (getMd5ToFileNameMap().containsKey(request)) {
            try {
                File load = new File(getMd5ToFileNameMap().get(request), fileByte(request), getMd5(bytes));
                response(load);
            } catch (NoSuchAlgorithmException | DigestException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(byte[] bytes) {
        String request = new String(bytes, File.OFFSET, bytes.length - File.OFFSET);
        if (getMd5ToFileNameMap().containsKey(request)) {
            java.io.File load = new java.io.File(getMd5ToFileNameMap().get(request));
            if (load.delete()) {
                getMd5ToFileNameMap().remove(request);
            }
        }
    }

    public FileOutputStream outStream(String name) {
        if (name == null) {
            log.error("Name file is nullable.");
            return null;
        }
        try {
            return new FileOutputStream(name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("Can't create file output stream for write to disk.");
        }
        return null;
    }

    public FileInputStream inStream(String name) {
        if (name == null) {
            log.error("Name file is nullable.");
            return null;
        }
        try {
            return new FileInputStream(name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("Can't create file input stream for read from disk.");
        }
        return null;
    }

    public File file(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        try {
            return new File(bytes);
        } catch (NoSuchAlgorithmException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("Don't load file from byte.");
        }
        return null;
    }

    public void save(FileOutputStream fos, byte[] bytes) {
        if (fos == null) return;

        try {
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Can't save file.");
        }
    }

    public void response(String result) {
        try {
            if (getFromClient() == null || getFromClient().getOutputStream() == null) return;

            getFromClient().getOutputStream().write(result.getBytes());
            log.info("Response {}", result);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Can't response found file.");
        }
    }

    public void response(File result) {
        try {
            if (getFromClient() == null || getFromClient().getOutputStream() == null) return;

            getFromClient().getOutputStream().write(result.getBytes());
            log.info("Response {}", result);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Can't response file.");
        }
    }

    public byte[] fileByte(String request) {
        if (request == null) {
            log.error("Request is null.");
            return null;
        }
        String path = getMd5ToFileNameMap().get(request);

        if (path == null) {
            log.error("Path to file is null.");
            return null;
        }

        try {
            return IOUtils.toByteArray(inStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Can't serialize input stream to byte array.");
        }
        return null;
    }

    public String getMd5(byte[] bytes) {
        String algorithmName = "MD5HEX";
        MessageDigest md5Algorithm = null;

        if (bytes == null || bytes.length == 0) {
            log.error("Byte array for algorithm {} empty or null.", algorithmName);
            return "0x0000";
        }

        String result = DigestUtils.md5Hex(bytes);
        log.info("Get {} file: {}", algorithmName, result);
        return result;
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

    public ConcurrentHashMap<String, String> getMd5ToFileNameMap() {
        return md5ToFileNameMap;
    }
}
