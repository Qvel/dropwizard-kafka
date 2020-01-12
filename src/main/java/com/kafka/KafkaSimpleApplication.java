package com.kafka;

import com.kafka.health.TemplateHealthCheck;
import com.kafka.resources.KafkaResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

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
            configuration.getDefaultName(),
            buildProducer(configuration),
            buildConsumer(configuration)
        );
        final TemplateHealthCheck healthCheck =
            new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

    private Producer<String,String> buildProducer(KafkaSimpleConfiguration configuration){
        return new KafkaProducer<>(configuration.getProducer());
    }

    private Consumer<String,String> buildConsumer(KafkaSimpleConfiguration configuration){
        Consumer<String,String> consumer = new KafkaConsumer<>(configuration.getConsumer());
        consumer.subscribe(Collections.singletonList("my-topic"));
        return consumer;
    }

}
