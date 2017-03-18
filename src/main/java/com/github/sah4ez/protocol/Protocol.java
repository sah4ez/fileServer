package com.github.sah4ez.protocol;

import com.github.sah4ez.Server;
import com.github.sah4ez.protocol.server.DeleteServerCmd;
import com.github.sah4ez.protocol.server.DownloadServerCmd;
import com.github.sah4ez.protocol.server.FindServerCmd;
import com.github.sah4ez.protocol.server.UploadServerCmd;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aleksandr on 08.03.17.
 */
public class Protocol {
    public static final String UPLOAD = "upload";
    public static final String FIND = "find";
    public static final String DOWNLOAD = "download";
    public static final String DELETE = "delete";
    public static final String FAIL = "fail";
    public static final String QUIT = "quit";

    private final Map<String, Command> commands = new HashMap<>();

    public Protocol(final Server server) {
        commands.put(UPLOAD, new UploadServerCmd(server));
        commands.put(FIND, new FindServerCmd(server));
        commands.put(DOWNLOAD, new DownloadServerCmd(server));
        commands.put(DELETE, new DeleteServerCmd(server));
    }

    public static byte[] getBytes(String string) {
        String[] byteValues = string.substring(2, string.length() - 1).trim().split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i = 0, len = bytes.length; i < len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }

    public String getResponse(String command, String body) throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(command, body);

        return commands.get(command).execute(parameters);
    }

}
