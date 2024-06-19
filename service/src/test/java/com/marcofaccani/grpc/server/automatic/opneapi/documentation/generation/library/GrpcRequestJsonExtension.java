package com.marcofaccani.grpc.server.automatic.opneapi.documentation.generation.library;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Log4j2
public class GrpcRequestJsonExtension implements AfterTestExecutionCallback {

  private final ObjectMapper mapper;

  public GrpcRequestJsonExtension() {
    mapper = new ObjectMapper();
    mapper.registerModule(new ProtobufModule());
    mapper.writerWithDefaultPrettyPrinter();
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    final var testMethod = context.getTestMethod();
    String testMethodName = testMethod.get().getName();
    try {
      if (testMethod.get().isAnnotationPresent(WriteGrpcRequestAndResponseAsJsonToFile.class)) {

        final var testInstance = context.getRequiredTestInstance();

        final var grpcRequest = getReferenceToClassFieldByName(testInstance, "grpcRequest");
        final var grpcResponse = getReferenceToClassFieldByName(testInstance, "grpcResponse");

        final var nestedClassName = getNestedClassName(context);
        final var directory = createDirectoryIfNotExists(nestedClassName, testMethodName);

        writeJsonToFile(grpcRequest, "request", directory);
        writeJsonToFile(grpcResponse, "response", directory);
      }
    } catch (Exception ex) {
      log.warn(
          "GrpcRequestJsonExtension error: could not write to file the gRPC request of the test named {}. Reason: {}",
          testMethodName, ex.getMessage());
    }
  }

  @SneakyThrows
  private static Object getReferenceToClassFieldByName(Object testInstance, String fieldName) {
    Field grpcRequestField = testInstance.getClass().getDeclaredField(fieldName);
    grpcRequestField.setAccessible(true);
    return grpcRequestField.get(testInstance);
  }

  private static String getNestedClassName(ExtensionContext context) {
    return context.getTestClass()
        .map(Class::getSimpleName)
        .orElse("unknown");
  }

  private static File createDirectoryIfNotExists(String nestedClassName, String testMethodName) {
    File directory = new File(String.format("target/test-json/%s/%s", nestedClassName, testMethodName));
    if (!directory.exists()) {
      directory.mkdirs();
    }
    return directory;
  }

  private void writeJsonToFile(Object grpcMessage, String suffix, File directory) {
    if ((grpcMessage instanceof com.google.protobuf.Empty)) {
      return;
    }

    File jsonFile = new File(directory, String.format("%s.json", suffix));
    try {
      mapper.writeValue(jsonFile, grpcMessage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
