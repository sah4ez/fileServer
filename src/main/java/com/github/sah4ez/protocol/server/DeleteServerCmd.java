package com.github.sah4ez.protocol.server;

import com.github.sah4ez.protocol.Command;
import com.github.sah4ez.Server;
import com.github.sah4ez.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by aleksandr on 18.03.17.
 */
public class DeleteServerCmd implements Command {
    private static final String NOT_FILE = "not file";
    private static final String FILE_FOUND = "ok";
    private static Logger log = LoggerFactory.getLogger(DeleteServerCmd.class);
    private final Server server;

    public DeleteServerCmd(Server server) {
        this.server = server;
    }

    @Override
    public String execute(Map<String, String> parameters) throws Exception {
        String request = parameters.get(Protocol.DELETE);

        if (!server.getMd5ToFileNameMap().containsKey(request)) return NOT_FILE;

        java.io.File load = new java.io.File(server.getMd5ToFileNameMap().get(request));

        if (load.delete()) {
            server.getMd5ToFileNameMap().remove(request);
            log.debug("Delete file {}", request);
            return FILE_FOUND;
        }

        return NOT_FILE;
    }
}
