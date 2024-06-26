package com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.channel;

import com.google.protobuf.Empty;
import com.marcofaccani.grpc.server.v1.EdgeCaseEmptyRequest;
import com.marcofaccani.grpc.server.v1.GetUserByIdRequest;
import com.marcofaccani.grpc.server.v1.GetUserByIdResponse;
import com.marcofaccani.grpc.server.v1.GreetingRequest;
import com.marcofaccani.grpc.server.v1.GreetingResponse;
import com.marcofaccani.grpc.server.v1.GrpcServerServiceGrpc;
import com.marcofaccani.grpc.server.v1.IntroduceRequest;
import com.marcofaccani.grpc.server.v1.IntroduceResponse;
import com.marcofaccani.grpc.server.v1.ServerServiceStatus;
import com.marcofaccani.grpc.server.v1.ServerStatusReply;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class GrpcServer extends GrpcServerServiceGrpc.GrpcServerServiceImplBase {

  @Override
  public void getServerStatus(Empty request, StreamObserver<ServerStatusReply> streamObserver) {
    ServerStatusReply reply = ServerStatusReply.newBuilder()
        .setStatus(ServerServiceStatus.UP)
        .setService("GRPC-SERVER")
        .setNewField("A new field!")
        .build();
    streamObserver.onNext(reply);
    streamObserver.onCompleted();
  }

  @Override
  public void greeting(GreetingRequest request, StreamObserver<GreetingResponse> streamObserver) {
    final var message = String.format("Hello %s %s!", request.getFirstname(), request.getLastname());
    final var reply = GreetingResponse.newBuilder()
        .setMessage(message)
        .build();
    streamObserver.onNext(reply);
    streamObserver.onCompleted();
  }

  @Override
  public void introduce(IntroduceRequest request, StreamObserver<IntroduceResponse> streamObserver) {
    final var message = String.format("Hi, my name is %s, I am from %s!", request.getFirstname(), request.getCity());
    final var reply = IntroduceResponse.newBuilder()
        .setReply(message)
        .build();
    streamObserver.onNext(reply);
    streamObserver.onCompleted();
  }

  @Override
  public void edgeCaseEmptyResponse(EdgeCaseEmptyRequest request, StreamObserver<Empty> streamObserver) {
    streamObserver.onNext(Empty.getDefaultInstance());
    streamObserver.onCompleted();
  }

  @Override
  public void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> streamObserver) {
    final var response = GetUserByIdResponse.newBuilder()
        .setFirstname("Marco")
        .setLastname("Rossi")
        .build();
    streamObserver.onNext(response);
    streamObserver.onCompleted();
  }

}
