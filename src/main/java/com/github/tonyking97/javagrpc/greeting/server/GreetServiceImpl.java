package com.github.tonyking97.javagrpc.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();

        String result = "Hello " + firstName + " " + lastName + ".!";
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        //Send response
        responseObserver.onNext(response);

        //complete rpc call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();
        String lastName = request.getGreeting().getLastName();

        for(int i = 0; i < 10; i++){
            String result = "Hello " + firstName + " " + lastName + ", response number - " + i + ".";
            GreetResponse response = GreetResponse.newBuilder()
                    .setResult(result)
                    .build();
            responseObserver.onNext(response);
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetRequest> longGreet(StreamObserver<GreetResponse> responseObserver) {
        //we create a requestObserver that we'll return in this function
        StreamObserver<GreetRequest> requestObserver = new StreamObserver<GreetRequest>() {

            String result = "";

            @Override
            public void onNext(GreetRequest value) {
                //client sends a message
                result += " Hello " + value.getGreeting().getFirstName() + " " + value.getGreeting().getLastName() + "! ";
            }

            @Override
            public void onError(Throwable t) {
                //client sends an error
            }

            @Override
            public void onCompleted() {
                //client is done
                responseObserver.onNext(
                        GreetResponse.newBuilder()
                        .setResult(result)
                        .build()
                );
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    @Override
    public StreamObserver<GreetRequest> greetEveryone(StreamObserver<GreetResponse> responseObserver) {
        StreamObserver<GreetRequest> requestObserver = new StreamObserver<GreetRequest>() {
            @Override
            public void onNext(GreetRequest value) {
                String result = "Hello " + value.getGreeting().getFirstName() + " " + value.getGreeting().getLastName() + ".!";
                GreetResponse greetResponse = GreetResponse.newBuilder()
                        .setResult(result)
                        .build();
                responseObserver.onNext(greetResponse);
            }

            @Override
            public void onError(Throwable t) {
                //client sends an error
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }
}
