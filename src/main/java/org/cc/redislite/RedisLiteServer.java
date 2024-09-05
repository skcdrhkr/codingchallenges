package org.cc.redislite;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisLiteServer {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(60);
    private static final RedisProcessor redisProcessor = new RedisProcessor();

    public static void main(String[] args) {
        int port = 6379;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started and is listening:");

            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> handleRedisClients(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleRedisClients(Socket socket) {
        RedisStorage redisStorage = new RedisStorage();
        redisProcessor.setRedisStorage(redisStorage);
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
