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
        String firstname = greeting.getFirstName();
        String lastname = greeting.getLastName();

        String result = "Hello " + firstname + " " + lastname + ".!";
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        //Send response
        responseObserver.onNext(response);

        //complete rpc call
        responseObserver.onCompleted();
    }

}
