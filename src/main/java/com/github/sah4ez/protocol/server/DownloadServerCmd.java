package com.github.sah4ez.protocol.server;

import com.github.sah4ez.protocol.Command;
import com.github.sah4ez.protocol.File;
import com.github.sah4ez.Server;
import com.github.sah4ez.protocol.Protocol;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.github.sah4ez.protocol.Protocol.FAIL;

/**
 * Created by aleksandr on 18.03.17.
 */
public class DownloadServerCmd implements Command {
    private static Logger log = LoggerFactory.getLogger(DownloadServerCmd.class);

    private final Server server;

    public DownloadServerCmd(final Server server) {
        this.server = server;
    }

    @Override
    public String execute(Map<String, String> parameters) throws Exception {
        String request = parameters.get(Protocol.DOWNLOAD);
        File load = null;
        if (server.getMd5ToFileNameMap().containsKey(request)) {
            load = new File(server.getMd5ToFileNameMap().get(request), fileByte(request), getMd5(request));
        }

        if (load == null) {
            log.error("Not found file with ID {} ", request);
            return FAIL;
        }

        return Arrays.toString(SerializationUtils.serialize(load));
    }

    private byte[] fileByte(String request) throws IOException {
        if (request == null) {
            log.error("Request is null.");
            return null;
        }
        String path = server.getMd5ToFileNameMap().get(request);

        if (path == null) {
            log.error("Path to file is null.");
            return null;
        }

        return IOUtils.toByteArray(inStream(path));
    }

    private String getMd5(String request) {
        String algorithmName = "MD5HEX";

        String result = DigestUtils.md5Hex(request.getBytes());
        log.info("Get {} file: {}", algorithmName, result);
        return result;
    }

    private FileInputStream inStream(String name) throws FileNotFoundException {
        if (name == null) {
            log.error("Name file is nullable.");
            return null;
        }

        return new FileInputStream(name);
    }
}
