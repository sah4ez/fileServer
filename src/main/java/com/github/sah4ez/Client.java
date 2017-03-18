package com.github.sah4ez;


import com.github.sah4ez.protocol.Command;
import com.github.sah4ez.protocol.Protocol;
import com.github.sah4ez.protocol.client.DeleteClientCmd;
import com.github.sah4ez.protocol.client.DowloadClientCmd;
import com.github.sah4ez.protocol.client.FindClientCmd;
import com.github.sah4ez.protocol.client.UploadClientCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author sah4ez
 */
public class Client {
    public static final Logger log = LoggerFactory.getLogger(Client.class);
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, Command> commands = new HashMap<>();

    public Client(String host, int port) throws Exception {
        try (
                Socket kkSocket = new Socket(host, port);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()));
        ) {
            this.socket = kkSocket;
            this.in = in;
            this.out = out;
            log.info("Connect to {}:{} successful", host, port);
            run();
        } finally {
            commands.put(Protocol.UPLOAD, new UploadClientCmd());
            commands.put(Protocol.FIND, new FindClientCmd());
            commands.put(Protocol.DOWNLOAD, new DowloadClientCmd());
            commands.put(Protocol.DELETE, new DeleteClientCmd());
        }
    }

    public static void main(String[] args) throws Exception {
        new Client("localhost", 8080);
    }

    public void run() throws Exception {
        BufferedReader stdIn = null;

        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            if (Protocol.QUIT.equals(fromServer)) break;
            log.info("Response: {} ", fromServer);

            stdIn = new BufferedReader(new InputStreamReader(System.in));

            execute(stdIn.readLine().split(" "));
        }
        assert stdIn != null;
        stdIn.close();
    }

    public void execute(String[] parameters) throws Exception {
        if (parameters == null || parameters.length != 2) {
            log.error("Error parameters");
            return;
        }

        if (parameters[0].equals(Protocol.QUIT)){
            log.info("Close ...");
            socket.close();
            return;
        }

        Map<String, String> parameter = new HashMap<>();
        parameter.put(parameters[0], parameters[1]);

        out.print(commands.get(parameters[0]).execute(parameter));
    }

    public static String join(String command, String value) {
        final String SPACE = " ";
        StringJoiner stringJoiner = new StringJoiner(SPACE);
        stringJoiner.add(command).add(value);
        return stringJoiner.toString();
    }
}
