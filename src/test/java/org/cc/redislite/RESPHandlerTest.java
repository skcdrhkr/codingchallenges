package org.cc.redislite;

import org.cc.redislite.resp.RESPHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RESPHandlerTest {

    //todo: Write tests for set expiry
    public static RESPHandler respHandler;

    @BeforeAll
    public static void initialize() {
        respHandler = new RESPHandler();
    }

    @Test
    public void testDeserialize() {
        String request1 = "*1\r\n$4\r\nping\r\n";
        String[] response1 = respHandler.deserialize(request1);
        Assertions.assertEquals("ping", String.join(" ", response1));

        String request2 = "*2\r\n$4\r\necho\r\n$11\r\nhello world\r\n";
        String[] response2 = respHandler.deserialize(request2);
        Assertions.assertEquals("echo hello world", String.join(" ", response2));

        String request3 = "*2\r\n$3\r\nget\r\n$3\r\nkey\r\n";
        String[] response3 = respHandler.deserialize(request3);
        Assertions.assertEquals("get key", String.join(" ", response3));
    }
}
