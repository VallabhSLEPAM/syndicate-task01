package com.task04;

import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "sns_handler",
	roleName = "sns_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SnsEventSource(targetTopic = "lambda_topic")
@DependsOn(
        name = "lambda_topic",
        resourceType = ResourceType.SNS_TOPIC//"sns_topic"
)
public class SnsHandler implements RequestHandler<SNSEvent, String> {

	 public String handleRequest(SNSEvent event, Context context) {
        // Iterate through SNS records in the event
        // for (SNSEvent.SNSRecord record : event.getRecords()) {
        //     // Extract the SNS message
        //     SNSEvent.SNS sns = record.getSNS();
        //     String message = sns.getMessage();

        //     // Log the message to CloudWatch
        //     context.getLogger().log(message);
        // }
        List<SNSEvent.SNSRecord> records = event.getRecords();
        SNSEvent.SNS firstSNSRecord = records.get(0).getSNS();

        context.getLogger().log(firstSNSRecord.getMessage());
        return "Message logged successfully";
    }
}
