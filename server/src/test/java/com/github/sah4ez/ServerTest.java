package com.github.sah4ez;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple Server.
 */
public class ServerTest
{
    private Server server;
    @Before
    public void setUp() throws Exception {
        server = new Server();
    }

    @Test
    public void testListenAddress() throws Exception {
        //Assign
        int port = 8080;

        //Act
        server.listen(port);

        //Assert
    }
}
