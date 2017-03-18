package com.github.sah4ez.protocol.client;

import com.github.sah4ez.Client;
import com.github.sah4ez.protocol.Command;
import com.github.sah4ez.protocol.Protocol;

import java.util.Map;

/**
 * Created by aleksandr on 18.03.17.
 */
public class DowloadClientCmd implements Command{
    @Override
    public String execute(Map<String, String> parameters) throws Exception {
        return Client.join(Protocol.DOWNLOAD, parameters.get(Protocol.DOWNLOAD));
    }
}
