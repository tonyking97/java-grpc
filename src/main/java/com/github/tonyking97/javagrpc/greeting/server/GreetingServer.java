package com.github.tonyking97.javagrpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting gRPC Server.");
        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetServiceImpl())
                .build();

        server.start();

        System.out.println("gRPC Server Started Successfully.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shut Down Request.");
            server.shutdown();
            System.out.println("Successfully stopped the gRPC Server");
        }));

        server.awaitTermination();
    }
}
