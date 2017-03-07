package com.github.sah4ez;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by aleksandr on 07.03.17.
 */
public class ServerTest {
    private static final String PATH = "/Users/aleksandr/test";
    private Server server;
    private Server spyServer;
    private ConcurrentHashMap map = mock(ConcurrentHashMap.class);
    private Socket from = mock(Socket.class);
    private OutputStream outputStream = mock(OutputStream.class);


    @Before
    public void setUp() throws Exception {
        when(from.getOutputStream()).thenReturn(outputStream);

        server = new Server(PATH);
        spyServer = mock(Server.class, CALLS_REAL_METHODS);
        when(spyServer.getFromClient()).thenReturn(from);
    }

    @After
    public void treadOff() {
        server = null;
    }

    @Test
    public void listen() throws Exception {
        //Assign
        int port = 8080;

        //Act
        server.listen(port);

        //Assert
        ServerSocket serverSocket = null;
        try {

            serverSocket = new ServerSocket(port);
        } catch (BindException ignored) {

        }
        assertNull(serverSocket);

    }

    @Test
    public void accept() throws Exception {
        //Assign
        ServerSocket mock = mock(ServerSocket.class);
        server.setServerSocket(mock);

        //Act
        server.accept();

        //Assert
        verify(mock).accept();
    }

    @Test
    public void read() throws Exception {
        //Assign
//        InputStream inputStream = mock(InputStream.class);
        Socket mock = mock(Socket.class);
//        when(mock.getInputStream()).thenReturn(inputStream);
        server.setFromClient(mock);

        //Act
        byte[] bytes = server.read();

        //Assert
        verify(mock).getInputStream();
        assertNull(bytes);
    }

    @Test
    public void upload() throws Exception {
        //Assign
        byte[] bytes = new byte[]{0, 1, 2, 3};
        File file = mock(File.class);
        when(file.getMd5HEX()).thenReturn("1");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});

        when(map.containsKey("1")).thenReturn(true).thenReturn(false);
        when(spyServer.file(any())).thenReturn(file);
        when(spyServer.getMd5ToFileNameMap()).thenReturn(map);

        //Act
        spyServer.upload(bytes);

        //Assert
        verify(map, never()).putIfAbsent(anyString(), anyString());

        spyServer.upload(bytes);
        verify(map).putIfAbsent(anyString(), anyString());
    }

    @Test
    public void find() throws Exception {
        //Assign
        byte[] command = new byte[]{1};
        String name = "File name.txt";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(command);
        baos.write(name.getBytes());

        byte[] bytes = baos.toByteArray();

        String hex = "1";
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put(hex, name);

        when(spyServer.getMd5ToFileNameMap()).thenReturn(map);

        //Act
        spyServer.find(bytes);

        //Assert
        verify(spyServer).response(anyString());
        verify(outputStream).write(any());

    }

    @Test
    public void download() throws Exception {
        //Assign
        byte[] file = new byte[]{1, 2, 3, 4};
        byte[] command = new byte[]{2};
        String md5 = "1";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(command);
        baos.write(md5.getBytes());

        byte[] bytes = baos.toByteArray();

        when(map.containsKey("1")).thenReturn(false).thenReturn(true);
        when(spyServer.getMd5ToFileNameMap()).thenReturn(map);
        //Act
        spyServer.download(bytes);

        //Assert
        verify(map, never()).get("1");

        //Act
        spyServer.download(bytes);

        //Assert
        verify(spyServer).response(any(File.class));
        verify(outputStream).write(any());
    }

    @Test
    public void delete() throws Exception {
        //Assign
        byte[] file = new byte[]{1, 2, 3, 4};
        byte[] command = new byte[]{3};
        String md5 = "1";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(command);
        baos.write(md5.getBytes());

        byte[] bytes = baos.toByteArray();

        when(map.containsKey("1")).thenReturn(false).thenReturn(true);
        when(map.get("1")).thenReturn("nullnull");
        when(spyServer.getMd5ToFileNameMap()).thenReturn(map);
        //Act
        spyServer.delete(bytes);

        //Assert
        verify(map, never()).remove("1");

        //Act
        spyServer.delete(bytes);

        //Assert
        verify(map).remove("1");
    }

    @Test
    public void outStream() throws Exception {
        //Assign

        //Act

        //Assert
    }

    @Test
    public void inStream() throws Exception {
        //Assign

        //Act

        //Assert
    }

    @Test
    public void file() throws Exception {
        //Assign

        //Act

        //Assert
    }

    @Test
    public void save() throws Exception {
        //Assign

        //Act

        //Assert
    }

    @Test
    public void response() throws Exception {
        //Assign

        //Act

        //Assert
    }

    @Test
    public void response1() throws Exception {
        //Assign

        //Act

        //Assert
    }

    @Test
    public void fileByte() throws Exception {
        //Assign

        //Act

        //Assert
    }

    @Test
    public void getMd5() throws Exception {
        //Assign

        //Act

        //Assert
    }

    @Test
    public void getServerSocket() throws Exception {
        //Assign

        //Act

        //Assert
    }

}