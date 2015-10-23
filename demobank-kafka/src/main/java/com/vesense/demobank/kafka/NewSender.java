package com.vesense.demobank.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.commons.lang.Validate;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class NewSender implements Sender {

    private KafkaProducer<String, String> producer;
    private String topic;
    private boolean async = true;

    public NewSender(Properties props) {
        Validate.notNull(props);
        producer = new KafkaProducer<String, String>(props);
    }

    @Override
    public void send(String msg) {
        // simple way
        // producer.send(new ProducerRecord<String, String>(topic, msg));
        // specify record key
        // producer.send(new ProducerRecord<String, String>(topic, key, value));

        Future<RecordMetadata> result = producer.send(
                new ProducerRecord<String, String>(topic, msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata,
                            Exception exception) {
                        if (exception != null)
                            exception.printStackTrace();
                    }
                });

        if (!async) {
            try {
                result.get();// block until the associated request completes
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        producer.close();
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
