package org.cc.redislite.resp;

import org.cc.redislite.RedisCommand;
import org.cc.redislite.cache.RedisStorage;
import org.cc.redislite.exception.SyntaxErrorException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisProcessor {

    private static final String OK = "+OK\r\n";
    private static final String NULL = "$-1\r\n";
    private static final String WRONG_ARGS = "ERR wrong number of arguments for '%s' command";

    private final RESPHandler respHandler = new RESPHandler();

    private final ThreadLocal<RedisStorage> threadLocalStorage = ThreadLocal.withInitial(RedisStorage::new);

    private RedisStorage getRedisStorage() {
        return threadLocalStorage.get();
    }

    public void setRedisStorage(RedisStorage redisStorage) {
        threadLocalStorage.set(redisStorage);
    }

    public String handleRedisCommand(String message) {
        String[] inputCommand = respHandler.deserializeBulkStringArrayRequest(message.toCharArray());

        try {
            RedisCommand command = RedisCommand.valueOf(inputCommand[0].toUpperCase());
            return switch (command) {
                case COMMAND -> OK;
                case PING -> handlePing(inputCommand);
                case ECHO -> handleEcho(inputCommand);
                case SET -> handleSetEntry(inputCommand);
                case GET -> handleGetEntry(inputCommand);
                case EXISTS -> handleExists(inputCommand);
                case DEL -> handleDelEntry(inputCommand);
                case INCR -> handleIncr(inputCommand);
                case DECR -> handleDecr(inputCommand);
            };
        } catch (IllegalArgumentException e) {
            return respHandler.serializedErrorMessage("ERR unknown command '" + inputCommand[0] + "', with args beginning with: " + Arrays.stream(inputCommand).skip(1).map("'%s'"::formatted).collect(Collectors.joining(" ")));
        }
    }

    private String handleDecr(String[] inputCommand) {
        RedisStorage storage = getRedisStorage();
        if (inputCommand.length != 2) {
            return respHandler.serializedErrorMessage(WRONG_ARGS.formatted("decr"));
        }
        String key = inputCommand[1];
        String value = storage.getValue(key);
        long longValue;
        try {
            longValue = (value == null) ? 0 : Long.parseLong(value);
            longValue -= 1;
            storage.setEntry(key, Long.toString(longValue));
        } catch (NumberFormatException e) {
            return respHandler.serializedErrorMessage("ERR value is not an integer or out of range");
        }
        return respHandler.serializedInteger(longValue);
    }

    private String handleIncr(String[] inputCommand) {
        RedisStorage storage = getRedisStorage();
        if (inputCommand.length != 2) {
            return respHandler.serializedErrorMessage(WRONG_ARGS.formatted("incr"));
        }
        String key = inputCommand[1];
        String value = storage.getValue(key);
        long longValue;
        try {
            longValue = (value == null) ? 0 : Long.parseLong(value);
            longValue += 1;
            storage.setEntry(key, Long.toString(longValue));
        } catch (NumberFormatException e) {
            return respHandler.serializedErrorMessage("ERR value is not an integer or out of range");
        }
        return respHandler.serializedInteger(longValue);
    }

    private String handleDelEntry(String[] inputCommand) {
        RedisStorage storage = getRedisStorage();
        Set<String> cacheKeys = storage.getKeySet();
        if (inputCommand.length < 2) {
            return respHandler.serializedErrorMessage(WRONG_ARGS.formatted("del"));
        }
        int keyCount = 0;
        for (int ind = 1; ind < inputCommand.length; ind++) {
            if (cacheKeys.contains(inputCommand[ind])) {
                storage.removeCacheKey(inputCommand[ind]);
                keyCount += 1;
            }
        }
        return respHandler.serializedInteger((long) keyCount);
    }

    private String handleExists(String[] inputCommand) {
        Set<String> storage = getRedisStorage().getKeySet();
        if (inputCommand.length < 2) {
            return respHandler.serializedErrorMessage(WRONG_ARGS.formatted("exists"));
        }
        int keyCount = 0;
        for (int ind = 1; ind < inputCommand.length; ind++) {
            if (storage.contains(inputCommand[ind])) {
                keyCount += 1;
            }
        }
        return respHandler.serializedInteger((long) keyCount);
    }

    private String handleGetEntry(String[] inputCommand) {
        if (inputCommand.length != 2) {
            return respHandler.serializedErrorMessage(WRONG_ARGS.formatted("get"));
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
            return respHandler.serializedErrorMessage(WRONG_ARGS.formatted("set"));
        }
        try {
            return processSetWithParameters(inputCommand);
        } catch (NumberFormatException e) {
            return respHandler.serializedErrorMessage("ERR value is not an integer or out of range");
        } catch (SyntaxErrorException e) {
            return respHandler.serializedErrorMessage("ERR syntax error");
        }
    }

    private String processSetWithParameters(String[] inputCommand) throws SyntaxErrorException, NumberFormatException {

        RedisStorage redisStorage = getRedisStorage();
        String key = inputCommand[1];
        String value = inputCommand[2];
        String oldValue = OK;
        long curTime = System.currentTimeMillis();
        long expiryTime = Long.MAX_VALUE;
        boolean expirySet = false;

        if (inputCommand.length == 3) {
            redisStorage.setEntry(inputCommand[1], inputCommand[2]);
        }

        for (int ind = 3; ind < inputCommand.length; ) {
            String param = inputCommand[ind];
            if ("GET".equalsIgnoreCase(param)) {
                oldValue = redisStorage.getValue(key) == null ? NULL : respHandler.serializedBulkString(redisStorage.getValue(key));
                ind += 1;
            } else if ("EX".equalsIgnoreCase(param) || "PX".equalsIgnoreCase(param) || "EXAT".equalsIgnoreCase(param) || "PXAT".equalsIgnoreCase(param)) {
                if (expirySet && (ind + 1) >= inputCommand.length)
                    throw new SyntaxErrorException();

                long expiry = Long.parseLong(inputCommand[ind + 1]);

                expiryTime = switch (param.toUpperCase()) {
                    case "EX" -> curTime + (1000 * expiry);
                    case "PX" -> curTime + expiry;
                    case "EXAT" -> expiry * 1000;
                    default -> expiry;
                };
                ind += 2;
                expirySet = true;
            } else {
                throw new SyntaxErrorException();
            }
        }
        redisStorage.setEntry(key, value, curTime, expiryTime);
        return oldValue;
    }

    private String handleEcho(String[] inputCommand) {
        if (inputCommand.length != 2) {
            return respHandler.serializedErrorMessage(WRONG_ARGS.formatted("echo"));
        } else {
            return respHandler.serializedBulkString(inputCommand[1]);
        }
    }

    private String handlePing(String[] inputCommand) {
        if (inputCommand.length > 2) {
            return respHandler.serializedErrorMessage(WRONG_ARGS.formatted("ping"));
        } else if (inputCommand.length > 1) {
            return respHandler.serializedBulkString(inputCommand[1]);
        } else {
            return respHandler.serliaizedSimpleString("PONG");
        }
    }
}
