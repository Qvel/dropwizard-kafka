package com.kafka;

import java.util.Properties;

import io.dropwizard.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;


public class KafkaSimpleConfiguration extends Configuration {
    // TODO: implement service configuration
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

    @NotEmpty
    private Properties producer;

    @NotEmpty
    private Properties consumer;

    public Properties getProducer() {
        return producer;
    }

    public void setProducer(Properties producer) {
        this.producer = producer;
    }

    public Properties getConsumer() {
        return consumer;
    }

    public void setConsumer(Properties consumer) {
        this.consumer = consumer;
    }
}
