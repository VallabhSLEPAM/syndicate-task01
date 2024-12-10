package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;


import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	runtime = DeploymentRuntime.JAVA11,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	@Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        // Extract HTTP method and path
        String path = event.getRawPath(); // Path of the HTTP request
        String method = event.getRequestContext().getHttp().getMethod(); // HTTP method

        // Handle /hello GET request
        if ("/hello".equals(path) && "GET".equalsIgnoreCase(method)) {
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody("{\"statusCode\": 200, \"message\": \"Hello from Lambda\"}")
                    .build();
        }

        // Handle other requests with a 400 Bad Request response
        String errorMessage = String.format(
                "{\"statusCode\": 400, \"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}",
                path, method
        );
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(400)
                .withBody(errorMessage)
                .build();
		
	}
}
