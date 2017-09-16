package fr.xebia.code;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AwsProxyResponseBuilder {
  private static final Logger LOG = Logger.getLogger(AwsProxyResponseBuilder.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();


  private int statusCode = 200;
  private Map<String, String> headers = new HashMap<>(0);
  private String rawBody;
  private Object objectBody;
  private byte[] binaryBody;
  private boolean base64Encoded;

  private AwsProxyResponseBuilder() {
  }
  public static AwsProxyResponseBuilder builder() {
    return new AwsProxyResponseBuilder();
  }

  public AwsProxyResponseBuilder setStatusCode(int statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public AwsProxyResponseBuilder addHeader(String headerName, String headerValue) {
    this.headers.put(headerName, headerValue);
    return this;
  }

  /**
   * Builds the {@link AwsProxyResponseBuilder} using the passed raw body string.
   */
  public AwsProxyResponseBuilder setRawBody(String rawBody) {
    this.rawBody = rawBody;
    return this;
  }

  /**
   * Builds the {@link AwsProxyResponseBuilder} using the passed object body
   * converted to JSON.
   */
  public AwsProxyResponseBuilder setObjectBody(Object objectBody) {
    this.objectBody = objectBody;
    return this;
  }

  /**
   * Builds the {@link AwsProxyResponseBuilder} using the passed binary body
   * encoded as base64. {@link #setBase64Encoded(boolean)
   * setBase64Encoded(true)} will be in invoked automatically.
   */
  public AwsProxyResponseBuilder setBinaryBody(byte[] binaryBody) {
    this.binaryBody = binaryBody;
    setBase64Encoded(true);
    return this;
  }

  /**
   * A binary or rather a base64encoded responses requires
   * <ol>
   * <li>"Binary Media Types" to be configured in API Gateway
   * <li>a request with an "Accept" header set to one of the "Binary Media
   * Types"
   * </ol>
   */
  public AwsProxyResponseBuilder setBase64Encoded(boolean base64Encoded) {
    this.base64Encoded = base64Encoded;
    return this;
  }

  public AwsProxyResponse build() {
    String body = null;
    if (rawBody != null) {
      body = rawBody;
    } else if (objectBody != null) {
      try {
        body = objectMapper.writeValueAsString(objectBody);
      } catch (JsonProcessingException e) {
        LOG.error("failed to serialize object", e);
        throw new RuntimeException(e);
      }
    } else if (binaryBody != null) {
      body = new String(Base64.getEncoder().encode(binaryBody), StandardCharsets.UTF_8);
    }

    AwsProxyResponse response = new AwsProxyResponse(statusCode, headers, body);
    response.setBase64Encoded(base64Encoded);
    return response;
  }
}
