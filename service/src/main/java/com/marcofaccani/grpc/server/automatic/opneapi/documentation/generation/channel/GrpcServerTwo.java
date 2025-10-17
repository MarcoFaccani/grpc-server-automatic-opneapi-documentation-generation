package com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.channel;

import com.marcofaccani.grpc.server.v1.GrpcServerServiceTwoGrpc;
import com.marcofaccani.grpc.server.v1.TranslateRequest;
import com.marcofaccani.grpc.server.v1.TranslateResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class GrpcServerTwo extends GrpcServerServiceTwoGrpc.GrpcServerServiceTwoImplBase {

  @Override
  public void translate(TranslateRequest request, StreamObserver<TranslateResponse> streamObserver) {
    final var reply = TranslateResponse.newBuilder().setMessage("Hi!").build();
    streamObserver.onNext(reply);
    streamObserver.onCompleted();
  }

}