package com.github.sah4ez.protocol.server;

import com.github.sah4ez.protocol.Command;
import com.github.sah4ez.Server;
import com.github.sah4ez.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aleksandr on 18.03.17.
 */
public class FindServerCmd implements Command {
    private static Logger log = LoggerFactory.getLogger(FindServerCmd.class);

    private final Server server;

    public FindServerCmd(final Server server) {
        this.server = server;
    }

    @Override
    public String execute(Map<String, String> parameters) throws Exception {
        String request = parameters.get(Protocol.FIND);
        if (request.isEmpty()) {
            log.error("Found empty name.");
            return "{}";
        }

        log.debug("Find file with name {}", request);
        HashMap<String, String> result = new HashMap<>(25);
        server.getMd5ToFileNameMap().forEach((id, name) -> {
            if (name.contains(request) && result.size() <= 25) {
                result.putIfAbsent(id, name);
            }
        });
        log.debug("Found {} files", result.toString());
        return result.toString();
    }
}
