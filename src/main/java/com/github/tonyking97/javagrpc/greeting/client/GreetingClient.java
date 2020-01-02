package com.github.tonyking97.javagrpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC Client.");

        System.out.println("Creating Stub.");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051 )
                .usePlaintext()
                .build();

        //Created a greet service Client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Tony")
                .setLastName("King")
                .build();

        //created greet request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //Unary call - call the rpc and get back a GreetResponse (protocol buffers)
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println(greetResponse.getResult());

        System.out.println("Shutting down channel.");
        channel.shutdown();
    }
}
