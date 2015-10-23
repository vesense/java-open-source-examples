package com.vesense.demobank.kafka;

import java.util.Properties;

public class OldSenderClient {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("metadata.broker.list",
                "pc-host01:9092,pc-host02:9092,pc-host03:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("producer.type", "async");
        props.put("request.required.acks", "1");
        props.put("message.send.max.retries", "3");
        props.put("retry.backoff.ms", "500");
        OldSender sender = new OldSender(props);
        sender.setTopic("topic");

        sender.send("msg01");
        sender.send("msg02");

    }

}
