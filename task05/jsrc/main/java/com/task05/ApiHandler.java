package com.task05;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
        authType = AuthType.NONE
        // invokeMode = InvokeMode.BUFFERED
)
public class ApiHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
    private static final String TABLE_NAME = "Events";
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
			
        String requestBody = event.getBody();
		System.out.println("Request Body:"+requestBody);
        // Insert into DynamoDB
        try {
			// Parse the JSON body
            JsonNode jsonNode = objectMapper.readTree(requestBody);
 			// String content = jsonNode.get("content").asText();
			String principalId = jsonNode.get("principalId").asText();
 			String requestId = UUID.randomUUID().toString(); // Unique ID for each record
	   
			// Prepare item for DynamoDB
			Map<String, AttributeValue> item = new HashMap<>();
			item.put("id", AttributeValue.builder().s(requestId).build());
			// item.put("body", AttributeValue.builder().s(content).build());
			item.put("principalId", AttributeValue.builder().s(principalId).build());
			item.put("createdAt", AttributeValue.builder().s(String.valueOf(System.currentTimeMillis())).build());
			
			Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 201);
            response.put("event", item);


            PutItemRequest request = PutItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);
            context.getLogger().log("Item successfully stored in DynamoDB");
            String responseBody = objectMapper.writeValueAsString(response);

            // Return success response
            return new APIGatewayV2HTTPResponse().builder()
                    .withStatusCode(201)
                    .withBody(responseBody).build();
        } catch (Exception e) {
            context.getLogger().log("Error storing data in DynamoDB: " + e.getMessage());

            // Return error response
            return new APIGatewayV2HTTPResponse().builder()
                    .withStatusCode(500)
                    .withBody("Failed to store data: " + e.getMessage()).build();
        }
	}
}
