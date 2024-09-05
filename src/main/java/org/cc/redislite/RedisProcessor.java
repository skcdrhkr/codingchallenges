package org.cc.redislite;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.cc.redislite.RedisCommand.*;

public class RedisProcessor {

    private static final String OK = "+OK\r\n";
    private static final String NULL = "$-1\r\n";

    private final RESPHandler respHandler = new RESPHandler();

    private final ThreadLocal<RedisStorage> threadLocalStorage = ThreadLocal.withInitial(RedisStorage::new);

    public RedisStorage getRedisStorage() {
        return threadLocalStorage.get();
    }

    public void setRedisStorage(RedisStorage redisStorage) {
        threadLocalStorage.set(redisStorage);
    }

    public String handleRedisCommand(String message) {
        String[] inputCommand = respHandler.deserializeBulkStringArrayRequest(message.toCharArray());
        if (inputCommand[0].equalsIgnoreCase(COMMAND.toString()))
            return OK;
        else if (inputCommand[0].equalsIgnoreCase(PING.toString())) {
            return handlePing(inputCommand);
        } else if (inputCommand[0].equalsIgnoreCase(ECHO.toString())) {
            return handleEcho(inputCommand);
        } else if (inputCommand[0].equalsIgnoreCase(SET.toString())) {
            return handleSetEntry(inputCommand);
        } else if (inputCommand[0].equalsIgnoreCase(GET.toString())) {
            return handleGetEntry(inputCommand);
        } else {
            return respHandler.serializedErrorMessage("ERR unknown command '" + inputCommand[0] + "', with args beginning with: " + Arrays.stream(inputCommand).skip(1).map("'%s'"::formatted).collect(Collectors.joining(" ")));
        }
    }

    private String handleGetEntry(String[] inputCommand) {
        if (inputCommand.length != 2) {
            return respHandler.serializedErrorMessage("ERR wrong number of arguments for 'get' command");
        }
        RedisStorage storage = getRedisStorage();
        String value = storage.getValue(inputCommand[1]);
        if (value == null) {
            return NULL;
        }
        return respHandler.serializedBulkString(value);
    }

    private String handleSetEntry(String[] inputCommand) {
        if (inputCommand.length < 3) {
            return respHandler.serializedErrorMessage("ERR wrong number of arguments for 'set' command");
        }
        if (inputCommand.length > 3) {
            return respHandler.serializedErrorMessage("ERR syntax error");
        }
        RedisStorage redisStorage = getRedisStorage();
        redisStorage.setEntry(inputCommand[1], inputCommand[2]);
        return OK;
    }

    private String handleEcho(String[] inputCommand) {
        if (inputCommand.length != 2) {
            return respHandler.serializedErrorMessage("ERR wrong number of arguments for 'echo'");
        } else {
            return respHandler.serializedBulkString(inputCommand[1]);
        }
    }

    private String handlePing(String[] inputCommand) {
        if (inputCommand.length > 2) {
            return respHandler.serializedErrorMessage("ERR wrong number of arguments for 'ping'");
        } else if (inputCommand.length > 1) {
            return respHandler.serializedBulkString(inputCommand[1]);
        } else {
            return respHandler.serliaizedSimpleString("PONG");
        }
    }
}
