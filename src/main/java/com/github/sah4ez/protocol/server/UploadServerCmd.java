package com.github.sah4ez.protocol.server;

import com.github.sah4ez.protocol.Command;
import com.github.sah4ez.protocol.File;
import com.github.sah4ez.Server;
import com.github.sah4ez.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by aleksandr on 18.03.17.
 */
public class UploadServerCmd implements Command {
    private static Logger log = LoggerFactory.getLogger(UploadServerCmd.class);
    private final Server server;

    public UploadServerCmd(Server server) {
        this.server = server;
    }

    @Override
    public String execute(Map<String, String> parameters) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        String body = parameters.get(Protocol.UPLOAD);
        String text = body.replaceFirst(Protocol.UPLOAD, "");
        byte[] bytes = Protocol.getBytes(text);

        File file = new File(bytes);

        String name = server.getPath() + file.getFileName();
        if (!server.getMd5ToFileNameMap().containsKey(file.getMd5HEX())) {
            save(outStream(name), file.getBytes());

            server.getMd5ToFileNameMap().putIfAbsent(file.getMd5HEX(), server.getPath() + file.getFileName());
            log.info("Upload file {} with md5 {}", server.getPath() + file.getFileName(), file.getMd5HEX());
        }
        return file.getMd5HEX();

    }

    private void save(FileOutputStream fos, byte[] bytes) throws IOException {
        if (fos == null) return;

        fos.write(bytes);
        fos.close();
    }

    private FileOutputStream outStream(String name) throws FileNotFoundException {
        if (name == null) {
            log.error("Name file is nullable.");
            return null;
        }

        log.info("Upload file {}", name);
        return new FileOutputStream(name);
    }
}
