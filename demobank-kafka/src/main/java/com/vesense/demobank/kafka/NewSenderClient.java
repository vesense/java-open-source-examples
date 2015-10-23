package com.vesense.demobank.kafka;

import java.util.Properties;

public class NewSenderClient {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers",
                "pc-host01:9092,pc-host02:9092,pc-host03:9092");
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "1");
        props.put("retries", "3");
        props.put("retry.backoff.ms", "500");
        props.put("reconnect.backoff.ms", "500");
        NewSender sender = new NewSender(props);
        sender.setTopic("topic");
        sender.send("msg01");
        sender.send("msg02");
    }

}
