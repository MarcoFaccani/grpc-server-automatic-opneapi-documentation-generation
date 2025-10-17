package com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation;

import com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.channel.GrpcServerTwo;
import com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.library.GrpcRequestJsonExtension;
import com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.library.WriteGrpcRequestAndResponseAsJsonToFile;
import com.marcofaccani.grpc.server.v1.TranslateRequest;
import com.marcofaccani.grpc.server.v1.TranslateResponse;
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
public class GrpcServerTwoTest {

  @InjectMocks
  GrpcServerTwo underTest;

  @Nested
  class Translate {

    TranslateRequest grpcRequest;
    TranslateResponse grpcResponse;

    @Mock
    StreamObserver<TranslateResponse> streamObserver;

    @Test
    @WriteGrpcRequestAndResponseAsJsonToFile
    void translate() {
      grpcRequest = TranslateRequest.newBuilder().setMessage("Ciao!").build();
      assertDoesNotThrow(() -> underTest.translate(grpcRequest, streamObserver));

      grpcResponse = TranslateResponse.newBuilder().setMessage("Hi!").build();
      verify(streamObserver).onNext(grpcResponse);
      verify(streamObserver).onCompleted();
    }
  }

}