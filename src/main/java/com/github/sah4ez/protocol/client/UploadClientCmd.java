package com.github.sah4ez.protocol.client;

import com.github.sah4ez.Client;
import com.github.sah4ez.protocol.Command;
import com.github.sah4ez.protocol.File;
import com.github.sah4ez.protocol.Protocol;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;


/**
 * Created by aleksandr on 18.03.17.
 */
public class UploadClientCmd implements Command {
    public static final Logger log = LoggerFactory.getLogger(Client.class);

    @Override
    public String execute(Map<String, String> parameters) throws Exception {
        String path = parameters.get(Protocol.UPLOAD);

        File file = sendFile(path);
        byte[] serialize = SerializationUtils.serialize(file);

        log.info("Upload file {}", path);

        return Client.join(Protocol.UPLOAD, Arrays.toString(serialize));
    }

    public File sendFile(String path) throws DigestException, NoSuchAlgorithmException, IOException {
        java.io.File file = new java.io.File(path);
        File receive;
        try (InputStream fis = new FileInputStream(file)) {

            byte[] bytes = IOUtils.toByteArray(fis);
            receive = new File(file.getName(), bytes, DigestUtils.md5Hex(bytes));
        }
        return receive;
    }
}
