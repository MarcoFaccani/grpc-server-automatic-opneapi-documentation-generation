package com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation;

import com.google.protobuf.Empty;
import com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.channel.GrpcServer;
import com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.library.GrpcRequestJsonExtension;
import com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.library.WriteGrpcRequestAndResponseAsJsonToFile;
import com.marcofaccani.grpc.server.v1.EdgeCaseEmptyRequest;
import com.marcofaccani.grpc.server.v1.GreetingRequest;
import com.marcofaccani.grpc.server.v1.GreetingResponse;
import com.marcofaccani.grpc.server.v1.IntroduceRequest;
import com.marcofaccani.grpc.server.v1.IntroduceResponse;
import com.marcofaccani.grpc.server.v1.ServerServiceStatus;
import com.marcofaccani.grpc.server.v1.ServerStatusReply;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(GrpcRequestJsonExtension.class)
class GrpcServerTest {

  @InjectMocks
  GrpcServer underTest;

  @Nested
  class GetServerStatus {

    Empty grpcRequest;

    ServerStatusReply grpcResponse;

    @Mock
    StreamObserver<ServerStatusReply> streamObserver;

    @Test
    @WriteGrpcRequestAndResponseAsJsonToFile
    void happyPath() {
      grpcRequest = Empty.getDefaultInstance();
      grpcResponse = ServerStatusReply.newBuilder()
          .setStatus(ServerServiceStatus.UP)
          .setService("GRPC-SERVER")
          .setNewField("A new field!")
          .build();

      assertDoesNotThrow(() -> underTest.getServerStatus(grpcRequest, streamObserver));
      verify(streamObserver).onNext(grpcResponse);
      verify(streamObserver).onCompleted();
    }
  }

  @Nested
  class Greeting {

    GreetingRequest grpcRequest;
    GreetingResponse grpcResponse;

    @Mock
    StreamObserver<GreetingResponse> streamObserver;

    @Test
    @WriteGrpcRequestAndResponseAsJsonToFile
    void greetMarco() {
      grpcRequest = GreetingRequest.newBuilder().setFirstname("Marco").setLastname("Rossi").build();
      assertDoesNotThrow(() -> underTest.greeting(grpcRequest, streamObserver));

      grpcResponse = GreetingResponse.newBuilder().setMessage("Hello Marco Rossi!").build();
      verify(streamObserver).onNext(grpcResponse);
      verify(streamObserver).onCompleted();
    }

    @Test
    @WriteGrpcRequestAndResponseAsJsonToFile
    void greetLuca() {
      grpcRequest = GreetingRequest.newBuilder().setFirstname("Luca").setLastname("Rossi").build();
      assertDoesNotThrow(() -> underTest.greeting(grpcRequest, streamObserver));

      grpcResponse = GreetingResponse.newBuilder().setMessage("Hello Luca Rossi!").build();
      verify(streamObserver).onNext(grpcResponse);
      verify(streamObserver).onCompleted();
    }
  }

  @Nested
  class Introduce {

    IntroduceRequest grpcRequest;
    IntroduceResponse grpcResponse;

    @Mock
    StreamObserver<IntroduceResponse> streamObserver;

    @Test
    @WriteGrpcRequestAndResponseAsJsonToFile
    void introduceCesare() {
      grpcRequest = IntroduceRequest.newBuilder().setFirstname("Cesare").setCity("Rome").build();
      assertDoesNotThrow(() -> underTest.introduce(grpcRequest, streamObserver));

      grpcResponse = IntroduceResponse.newBuilder().setReply("Hi, my name is Cesare, I am from Rome!").build();
      verify(streamObserver).onNext(grpcResponse);
      verify(streamObserver).onCompleted();
    }

    @Test
    @WriteGrpcRequestAndResponseAsJsonToFile
    void introduceAntonio() {
      grpcRequest = IntroduceRequest.newBuilder().setFirstname("Antonio").setCity("Napoli").build();
      assertDoesNotThrow(() -> underTest.introduce(grpcRequest, streamObserver));

      grpcResponse = IntroduceResponse.newBuilder().setReply("Hi, my name is Antonio, I am from Napoli!").build();
      verify(streamObserver).onNext(grpcResponse);
      verify(streamObserver).onCompleted();
    }
  }

  @Nested
  class EdgeCaseEmptyResponse {

    EdgeCaseEmptyRequest grpcRequest;
    Empty grpcResponse;

    @Mock
    StreamObserver<Empty> streamObserver;

    @Test
    @WriteGrpcRequestAndResponseAsJsonToFile
    void happyPath() {
      grpcRequest = EdgeCaseEmptyRequest.newBuilder().setFirstname("Achille").build();
      grpcResponse = Empty.getDefaultInstance();

      assertDoesNotThrow(() -> underTest.edgeCaseEmptyResponse(grpcRequest, streamObserver));
      verify(streamObserver).onNext(grpcResponse);
      verify(streamObserver).onCompleted();
    }

  }
}


