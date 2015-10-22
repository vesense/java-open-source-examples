package com.vesense.demobank.kafka;

import java.util.Properties;

import org.apache.commons.lang.Validate;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class OldSender implements Sender {

	private Producer<String, String> producer;
	private String topic;

	public OldSender(Properties props) {
		Validate.notNull(props);
		producer = new Producer<String, String>(new ProducerConfig(props));
	}

	@Override
	public void send(String msg) {
		// specify record key
		// producer.send(new KeyedMessage<String, String>(topic, key, value));

		producer.send(new KeyedMessage<String, String>(topic, msg));
	}

	@Override
	public void destroy() {
		producer.close();
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
