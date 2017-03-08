package com.github.sah4ez;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by aleksandr on 08.03.17.
 */
public class Protocol {
    private static Logger log = LoggerFactory.getLogger(Protocol.class);
    private Server server;

    public Protocol(Server server) {
        this.server = server;
    }

    public String getResponse(String command, String body) {

        String result = "fail";

        switch (command) {
            case "upload": {
                String text = body.replace("upload", "");
                String[] byteValues = text.substring(2, text.length() - 1).split(",");
                byte[] bytes = new byte[byteValues.length];

                for (int i = 0, len = bytes.length; i < len; i++) {
                    bytes[i] = Byte.parseByte(byteValues[i].trim());
                }
                result = upload(bytes);
                break;
            }
            case "find": {
                String text = body.replace("find", "");
                result = find(text.trim());
                break;
            }
            case "download": {
                String text = body.replace("download", "");
                result = download(text.trim());
                break;
            }
            case "delete": {
                String text = body.replace("delete", "");
                result = delete(text.trim());
                break;
            }
        }
        return result;
    }

    public String upload(byte[] bytes) {
        File file = file(bytes);
        if (file == null) {
            log.info("Create nullable file.");
            return "fail";
        }

        String name = server.getPath() + file.getFileName();
        if (!server.getMd5ToFileNameMap().containsKey(file.getMd5HEX())) {
            save(outStream(name), file.getBytes());

            server.getMd5ToFileNameMap().putIfAbsent(file.getMd5HEX(), server.getPath() + file.getFileName());
            log.info("Upload file {} with md5 {}", server.getPath() + file.getFileName(), file.getMd5HEX());
        }
        return file.getMd5HEX();
    }

    public String find(String request) {
        if ("".equals(request)) {
            log.info("Found empty name.");
            return "{}";
        }

        log.info("Find file with name {}", request);
        HashMap<String, String> result = new HashMap<>(25);
        server.getMd5ToFileNameMap().forEach((id, name) -> {
            if (name.contains(request) && result.size() < 25) {
                result.putIfAbsent(id, name);
            }
        });
        return result.toString();
    }

    public String download(String request) {
        File load = null;
        if (server.getMd5ToFileNameMap().containsKey(request)) {
            try {
                load = new File(server.getMd5ToFileNameMap().get(request), fileByte(request), getMd5(request));
            } catch (NoSuchAlgorithmException | DigestException e) {
                e.printStackTrace();
            }
        }
        if (load == null) return "fail";
        return Arrays.toString(SerializationUtils.serialize(load));
    }

    public String delete(String request) {
        if (server.getMd5ToFileNameMap().containsKey(request)) {
            java.io.File load = new java.io.File(server.getMd5ToFileNameMap().get(request));
            if (load.delete()) {
                server.getMd5ToFileNameMap().remove(request);
                return "ok";
            }
        }
        return "not file";
    }


    public byte[] fileByte(String request) {
        if (request == null) {
            log.error("Request is null.");
            return null;
        }
        String path = server.getMd5ToFileNameMap().get(request);

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

    public String getMd5(String request) {
        String algorithmName = "MD5HEX";

        String result = DigestUtils.md5Hex(request.getBytes());
        log.info("Get {} file: {}", algorithmName, result);
        return result;
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
}
