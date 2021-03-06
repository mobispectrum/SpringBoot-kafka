package com.james.kafkarproject.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Configuration
public class Consumer {
	
	
	@Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.messageKey}")
    private String messageKey;
    
    @Value("${kafka.broker.address}")
    private String brokerAddress;
	
	
	
	
	@Bean
	public KafkaMessageDrivenChannelAdapter<String, String>
	            adapter(KafkaMessageListenerContainer<String, String> container) {
	    KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter =
	            new KafkaMessageDrivenChannelAdapter(container);
	    kafkaMessageDrivenChannelAdapter.setOutputChannel(fromKafka());
	    return kafkaMessageDrivenChannelAdapter;
	}
	
	
	

	@Bean
	public KafkaMessageListenerContainer<String, String> container() throws Exception {
	    ContainerProperties properties = new ContainerProperties(brokerAddress);
	    // set more properties
	    return new KafkaMessageListenerContainer(consumerFactory(), new ContainerProperties(new TopicPartitionInitialOffset(this.topic, 0)));
	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
	    Map<String, Object> props = new HashMap<String, Object> ();
	    props.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.brokerAddress);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

	    return new DefaultKafkaConsumerFactory(props);
	}
	
	@Bean
    public PollableChannel fromKafka() {
        return new QueueChannel();
    }

	

}
