

# Pré-requis
Dans le terminal
```bash
export ROLE_LAMBDA=arn:aws:iam::010154155802:role/lambda_basic_execution
export AWS_PROFILE=xebia-dev
```

# DEMO Lambda Java

## Lambda simple

1 - Créer le fichier fr.xebia.Handler
```java
public class Handler {

	public String process(Map<String, Object> request, Context context) {
    System.out.println("received: " + request);

    String name = (String) request.getOrDefault("name", "inconnu");
    return "Hello " + name;
	}
}
```
2 - Builder le fat jar
```bash
./gradlew build
```

3 - Déployer sur AWS
```bash
aws lambda create-function \
  --function-name java-hello \
  --region eu-west-1 \
  --zip-file fileb://build/libs/java-example-uber.jar \
  --role $ROLE_LAMBDA \
  --handler fr.xebia.Handler::process \
  --runtime java8
```

4 - Appeler la lambda
```bash
aws lambda invoke 
  --function-name java-hello 
  --payload '{"name": "JUG"}' 
  --region eu-west-1 result.txt && cat result.txt
```

5 - Montrer cloudwatch log
 

## Utilisation des types AWS avec event HTTP
1. Créer le fichier fr.xebia.HandlerHTTP
```java
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
```

2. Builder le fat jar
```bash
./gradlew build
```

3. Créer le fichier serverless.yml

```yaml
service: java-lambda-example

provider:
  name: aws
  runtime: java8
  region: eu-west-1

package:
  artifact: build/libs/java-example-uber.jar

functions:
  hello:
    handler: fr.xebia.HandlerHTTP
    events:
      - http:
          path: hello
          method: get
```

4.Appeler la lambda
```bash
curl https://API_GATEWAY.eu-west-1.amazonaws.com/dev/hello?name=JUG
```

5. Tester la lambda dans l'interface graphique avec un event ApiGateway
```json
{
  "queryStringParameters": {
      "name": "You"
    }
}
```


# DEMO X-RAY
L'idée est de montrer que X-Ray aide à monitorer le cold start.

1 - Activer X-Ray sur la lambda Java hello
Modifier le fichier serverless.yml

```yaml
provider:
  ...
  iamRoleStatements:
    - Effect: Allow
      Action:
        - xray:PutTraceSegments
        - xray:PutTelemetryRecords
      Resource: "*"
```

2 - Installation du plugin serverless
```
npm i serverless-plugin-tracing
```

```yaml
provider:
  tracing: true

plugins:
  - serverless-plugin-tracing
```

3 - Déploiement
```
serverless deploy
```

4 - Exécution de la lambda
```bash
curl https://API_GATEWAY.eu-west-1.amazonaws.com/dev/hello?name=JUG
```

4 - Interface X-Ray
* Aller dans l'onglet monitoring de la lambda pour avoir le lien X-Ray

5 - Exécuter une lambda node.js pour voir la différence de cold start
```bash
curl https://m4tac334ok.execute-api.eu-west-1.amazonaws.com/dev/hello?name=jug
```
6 - Interface X-Ray
* Aller dans l'onglet monitoring de la lambda pour avoir le lien X-Ray
