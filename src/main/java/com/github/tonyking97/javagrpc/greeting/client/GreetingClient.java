package com.github.tonyking97.javagrpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public void run() {
        System.out.println("Creating Stub.");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051 )
                .usePlaintext()
                .build();

        System.out.println("-------------------------------------------- \n Unary Call Example \n--------------------------------------------");
        doUnaryCall(channel);

        System.out.println("-------------------------------------------- \n Server Streaming Example \n--------------------------------------------");
        doServerStreamingCall(channel);

        System.out.println("-------------------------------------------- \n Client Streaming Example \n--------------------------------------------");
        doClientStreamingCall(channel);

        System.out.println("-------------------------------------------- \n Bi Directional Streaming Example \n--------------------------------------------");
        doBiDiStreamingCall(channel);

        System.out.println("Shutting down channel.");
        channel.shutdown();
    }

    private void doUnaryCall(ManagedChannel channel) {
        //Created a greet service Client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bill")
                .setLastName("Gates")
                .build();

        //created greet request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //Unary call - call the rpc and get back a GreetResponse (protocol buffers)
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println(greetResponse.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        //Created a greet service Client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //created greet request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Mark")
                        .setLastName("Zuckerberg")
                        .build())
                .build();

        //Server Streaming - Stream the response in blocking manner
        greetClient.greetManyTimes(greetRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        //Created a greet service Client (non blocking - Asynchronous)
        GreetServiceGrpc.GreetServiceStub asyncGreetClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        //Client Streaming
        StreamObserver<GreetRequest> requestObserver =  asyncGreetClient.longGreet(new StreamObserver<GreetResponse>() {
            @Override
            public void onNext(GreetResponse value) {
                //we get a response from a server
                System.out.println("Received the response from the server.");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //we get a error from the server
            }

            @Override
            public void onCompleted() {
                //the server is done and sending us the data
                //onCompleted will be called right after onNext()
                System.out.println("Server has completed sending us response.");
                latch.countDown();
            }
        });

        //Streaming message #1
        System.out.println("Sending Message 1");
        requestObserver.onNext(GreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                    .setFirstName("Bill")
                    .setLastName("Gates")
                    .build())
                .build());

        //Streaming message #2
        System.out.println("Sending Message 2");
        requestObserver.onNext(GreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Mark")
                        .setLastName("Zuckerberg")
                        .build())
                .build());

        //Streaming message #3
        System.out.println("Sending Message 3");
        requestObserver.onNext(GreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Jeff")
                        .setLastName("Bezos")
                        .build())
                .build());

        //we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        //Created a greet service Client (non blocking - Asynchronous)
        GreetServiceGrpc.GreetServiceStub asyncGreetClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetRequest> requestObserver = asyncGreetClient.greetEveryone(new StreamObserver<GreetResponse>() {
            @Override
            public void onNext(GreetResponse value) {
                System.out.print("Response from server: ");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data.");
                latch.countDown();
            }
        });

        Arrays.asList("Bill", "Mark", "Jeff", "Larry").forEach(
                name -> {
                    System.out.println("Sending : " + name);
                    requestObserver.onNext(
                            GreetRequest.newBuilder()
                                    .setGreeting(Greeting.newBuilder()
                                            .setFirstName(name)
                                            .build())
                                    .build()
                    );
                }
        );

        //we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting gRPC Client.");
        GreetingClient client = new GreetingClient();

        client.run();
    }
}
