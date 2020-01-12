package com.kafka.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaMessageDTO {

    private String value;
    private String key;
    private String topic;
    private String offset;
    private String partition;

    public KafkaMessageDTO() {
    }

    public KafkaMessageDTO(String value, String key, String topic, String offset, String partition) {
        this.value = value;
        this.key = key;
        this.topic = topic;
        this.offset = offset;
        this.partition = partition;
    }

    @JsonProperty
    public String getValue() {
        return value;
    }

    @JsonProperty
    public String getKey() {
        return key;
    }

    @JsonProperty
    public String getTopic() {
        return topic;
    }

    @JsonProperty
    public String getOffset() {
        return offset;
    }

    @JsonProperty
    public String getPartition() {
        return partition;
    }

}
