package com.github.sah4ez;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertNotNull;

/**
 * Unit test for simple Server.
 */
public class ServerTest
{
    private Server server;
    @Before
    public void setUp() throws Exception {
        server = new Server("/Users/aleksandr/");
    }

    @Test
    public void testListenAddress() throws Exception {
        //Assign
        int port = 8080;

        //Act
        server.listen(port);

        //Assert
        assertNotNull(server.getServerSocket());
    }

    @Test
    public void testFindKey() throws Exception {
        //Assign
        ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<>();
        hashMap.put("1", "1");
        hashMap.put("11", "2");
        hashMap.put("22", "3");
        hashMap.put("33", "4");
        hashMap.put("13", "5");
        hashMap.put("44", "6");

        //Act

        //Assert
        System.out.println(hashMap.toString());
    }


}
