package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "sqs_handler",
	roleName = "sqs_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(
	targetQueue = "async_queue",
	batchSize = 10
)
@DependsOn(
        name = "async_queue",
        resourceType = ResourceType.SQS_QUEUE
)
public class SqsHandler implements RequestHandler<SQSEvent, Void> {

	public Void handleRequest(SQSEvent event, Context context) {
		for (SQSEvent.SQSMessage message : event.getRecords()) {
            // Print the message body to CloudWatch Logs
            context.getLogger().log(message.getBody());
        }
		return null;
	}
}
