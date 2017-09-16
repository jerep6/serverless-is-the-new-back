package fr.xebia;


import com.amazonaws.services.lambda.runtime.Context;

import java.util.Map;

public class Handler {

	public String process(Map<String, Object> request, Context context) {
    System.out.println("received: " + request);

    String name = (String) request.getOrDefault("name", "inconnu");
    return "Hello " + name;
	}
}
