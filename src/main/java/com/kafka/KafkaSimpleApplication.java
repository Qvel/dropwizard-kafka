package com.kafka;

import com.kafka.health.TemplateHealthCheck;
import com.kafka.resources.KafkaResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

public class KafkaSimpleApplication extends Application<KafkaSimpleConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSimpleApplication.class);

    public static void main(final String[] args) throws Exception {
        new KafkaSimpleApplication().run(args);
    }

    @Override
    public String getName() {
        return "KafkaSimple";
    }

    @Override
    public void initialize(final Bootstrap<KafkaSimpleConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final KafkaSimpleConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
        final KafkaResource resource = new KafkaResource(
            configuration.getTemplate(),
            configuration.getDefaultName()
        );
        final TemplateHealthCheck healthCheck =
            new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);

        runKafkaTest(configuration,environment);

    }


    public void runKafkaTest(KafkaSimpleConfiguration configuration, Environment environment) {
        final ExecutorService executorService = environment
            .lifecycle()
            .executorService("kafka-threads")
            .minThreads(2)
            .maxThreads(10)
            .build();

        executorService.execute(() -> {
            final Producer<String, String> producer = new KafkaProducer<>(configuration.getProducer());
            int i = 0;
            try {
                Thread.sleep(5000);
                String s = "Hello from qvel";
                producer.send(new ProducerRecord<>("my-topic", s, s));

            } catch (InterruptedException e) {
                LOGGER.info("Producer interrupted");
                producer.close();
            }
        });

        executorService.execute(() -> {
            final Consumer<String, String> consumer = new KafkaConsumer<>(configuration.getConsumer());
            consumer.subscribe(Collections.singletonList("my-topic"));
                while(true) {
                    ConsumerRecords<String, String> records = consumer.poll(2000);
                    for (ConsumerRecord<String, String> record : records)
                        LOGGER.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
                }
        });
    }
}
