package com.kafka.resources;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.kafka.api.Saying;

@Path("/kafka-ep")
@Produces(MediaType.APPLICATION_JSON)
public class KafkaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaResource.class);

    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    private final Producer<String,String> kafkaProducerBean;
    private final Consumer<String,String> kafkaConsumerBean;

    public KafkaResource(String template, String defaultName, Producer<String,String> producer, Consumer<String,String> consumer) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
        this.kafkaProducerBean = producer;
        this.kafkaConsumerBean = consumer;
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }


    @GET
    @Path("/producer")
    @Timed
    public Saying sendMessage(@QueryParam("message") String message) {
        kafkaProducerBean.send(new ProducerRecord<>("my-topic", message, message));
        return new Saying(counter.incrementAndGet(), message);
    }

    @GET
    @Path("/consumer")
    @Timed
    public Saying getMessage() {
        ConsumerRecords<String, String> records = kafkaConsumerBean.poll(Duration.ofMinutes(3));
        for (ConsumerRecord<String, String> record : records) {
            LOGGER.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
            LOGGER.info("topic = {}", record.topic());
            LOGGER.info(record.toString());
        }
        return new Saying(counter.incrementAndGet(), records.toString());
    }

}
