package org.cc.redislite;

import org.cc.redislite.resp.RESPHandler;
import org.cc.redislite.resp.RedisProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RESPHandlerTest {

    //todo: Write tests for set expiry
    public static RESPHandler respHandler;
    public static RedisProcessor redisProcessor;

    @BeforeAll
    public static void initialize() {
        respHandler = new RESPHandler();
        redisProcessor = new RedisProcessor();
    }

    @Test
    public void testRESPProcessing() {
        String request1 = "*1\r\n$4\r\nping\r\n";
        String[] response1 = respHandler.deserialize(request1);
        Assertions.assertEquals("ping", String.join(" ", response1));

        String request2 = "*2\r\n$4\r\necho\r\n$11\r\nhello world\r\n";
        String[] response2 = respHandler.deserialize(request2);
        Assertions.assertEquals("echo hello world", String.join(" ", response2));

        String request3 = "*2\r\n$3\r\nget\r\n$3\r\nkey\r\n";
        String[] response3 = respHandler.deserialize(request3);
        Assertions.assertEquals("get key", String.join(" ", response3));

        String request4 = "-Error message\r\n";
        String response4 = respHandler.serializedErrorMessage("Error message");
        Assertions.assertEquals(request4, response4);

        String request5 = "$0\r\n\r\n";
        String response5 = respHandler.serializedBulkString("");
        Assertions.assertEquals(request5, response5);

        String request6 = "+hello world\r\n";
        String response6 = respHandler.serliaizedSimpleString("hello world");
        Assertions.assertEquals(request6, response6);
    }

    @Test
    public void testPing() {
        String expectedResponse = "+PONG\r\n";
        String response = redisProcessor.handleRedisCommand("*1\r\n$4\r\nPING\r\n");
        Assertions.assertEquals(expectedResponse, response);

        String expectedResponse2 = "$5\r\nWorld\r\n";
        String response2 = redisProcessor.handleRedisCommand("*2\r\n$4\r\nPING\r\n$5\r\nWorld\r\n");
        Assertions.assertEquals(expectedResponse2, response2);

        String failureResponse = "-ERR wrong number of arguments for 'ping' command\r\n";
        String response3 = redisProcessor.handleRedisCommand("*3\r\n$4\r\nPING\r\n$5\r\nWorld\r\n$5\r\nhello\r\n");
        Assertions.assertEquals(failureResponse, response3);
    }

    @Test
    public void testExists() {
        String expectedResponse1 = ":1\r\n";
        redisProcessor.handleRedisCommand("*3\r\n$3\r\nset\r\n$3\r\nkey\r\n$5\r\nvalue\r\n");
        String response1 = redisProcessor.handleRedisCommand("*3\r\n$6\r\nexists\r\n$3\r\nkey\r\n$8\r\nnotexist\r\n");
        Assertions.assertEquals(expectedResponse1, response1);

        String expectedResponse2 = ":2\r\n";
        String response2 = redisProcessor.handleRedisCommand("*4\r\n$6\r\nexists\r\n$3\r\nkey\r\n$8\r\nnotexist\r\n$3\r\nkey\r\n");
        Assertions.assertEquals(expectedResponse2, response2);
    }

    @Test
    public void testDelEntry() {
        redisProcessor.handleRedisCommand("*3\r\n$3\r\nset\r\n$3\r\nkey\r\n$5\r\nvalue\r\n");
        redisProcessor.handleRedisCommand("*3\r\n$3\r\nset\r\n$5\r\ndummy\r\n$10\r\ndummyvalue\r\n");

        String expectedResponse1 = ":0\r\n";
        String response1 = redisProcessor.handleRedisCommand("*2\r\n$3\r\ndel\r\n$8\r\nnotexist\r\n");
        Assertions.assertEquals(expectedResponse1, response1);

        String expectedResponse2 = ":2\r\n";
        String response2 = redisProcessor.handleRedisCommand("*4\r\n$3\r\ndel\r\n$3\r\nkey\r\n$5\r\ndummy\r\n$8\r\nnotexist\r\n");
        Assertions.assertEquals(expectedResponse2, response2);
    }

    @Test
    public void testCounterCommands() {
        String expectedResponse1 = "-ERR value is not an integer or out of range\r\n";
        redisProcessor.handleRedisCommand("*3\r\n$3\r\nset\r\n$4\r\nmine\r\n$21\r\n239840239840283094283\r\n");
        String response1 = redisProcessor.handleRedisCommand("*2\r\n$4\r\nincr\r\n$4\r\nmine\r\n");
        Assertions.assertEquals(expectedResponse1, response1);

        String expectedResponse2 = ":-1\r\n";
        String response2 = redisProcessor.handleRedisCommand("*2\r\n$4\r\ndecr\r\n$3\r\nnew\r\n");
        Assertions.assertEquals(expectedResponse2, response2);
    }

}
