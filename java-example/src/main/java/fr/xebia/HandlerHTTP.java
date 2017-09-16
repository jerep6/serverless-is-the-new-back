package fr.xebia;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import fr.xebia.code.AwsProxyResponseBuilder;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class HandlerHTTP implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

	@Override
	public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
    System.out.println("received: " + request.getPath());

    String name = Optional
      .ofNullable(request.getQueryStringParameters())
      .orElseGet(Collections::emptyMap)
      .getOrDefault("name", "inconnu");

    return AwsProxyResponseBuilder.builder()
				.setStatusCode(200)
				.setRawBody("Hello " + name)
				.addHeader("X-Powered-By", "AWS Lambda & serverless")
				.build();
	}
}
