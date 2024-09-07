package org.cc.redislite;

import org.cc.redislite.cache.RedisStorage;
import org.cc.redislite.resp.RedisProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisLiteServer {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(60);
    private static final RedisProcessor redisProcessor = new RedisProcessor();
    private static final Map<Thread, RedisStorage> redisCaches = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 6378;

        Thread staleCheckThread = new Thread(() -> {
            try {
                while (true) {
                    checkForStaleValue();
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        staleCheckThread.start();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started and is listening:");

            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> handleRedisClients(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    private static void checkForStaleValue() {
        for (Map.Entry<Thread, RedisStorage> cache : redisCaches.entrySet()) {
            Thread thread = cache.getKey();
            if (!thread.isAlive()) {
                System.out.println("Thread is dead " + thread.getName() + ". Removing its cache.");
                redisCaches.remove(thread);
            } else {
                RedisStorage storage = cache.getValue();
                long curTime = System.currentTimeMillis();
                for (String cacheKey : storage.getKeySet()) {
                    long expiryTime = storage.getExpiryTime(cacheKey);
                    if (curTime >= expiryTime) {
                        System.out.println(thread.getName() + ": Removing stale key: " + cacheKey + ". at " + System.currentTimeMillis());
                        storage.removeCacheKey(cacheKey);
                    }
                }
            }
        }
    }

    private static void handleRedisClients(Socket socket) {
        RedisStorage redisStorage = new RedisStorage();
        redisProcessor.setRedisStorage(redisStorage);
        redisCaches.putIfAbsent(Thread.currentThread(), redisStorage);
        try (InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            // Continuously read data from the client
            while ((bytesRead = input.read(buffer)) != -1) {
                // Process the input data
                String message = new String(Arrays.copyOf(buffer, bytesRead));
                System.out.println("Received: " + message.replace("\r", "\\r").replace("\n", "\\n"));
                String response = redisProcessor.handleRedisCommand(message);
                System.out.println("Responding with: " + response.replace("\r", "\\r").replace("\n", "\\n"));
                // Send the response back to the client
                output.write(response.getBytes());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
