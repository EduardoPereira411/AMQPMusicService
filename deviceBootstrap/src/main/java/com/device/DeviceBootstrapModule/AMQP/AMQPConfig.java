package com.device.DeviceBootstrapModule.AMQP;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfig {

	@Value("${rabbit.device.topic}")
	String deviceTopic;

	@Value("${device.routingKey.created}")
	String createdDeviceKey;

	@Value("${device.routingKey.updated}")
	String updatedDeviceKey;

	@Value("${device.routingKey.deleted}")
	String deletedDeviceKey;

	@Value("${device.routingKey.allDeleted}")
	String allDeletedDeviceKey;


	@Value("${rabbit.device.bootstrap}")
	String deviceBootstrapEx;

	@Value("${device.routingKey.bootstrap}")
	String bootstrapKey;

	@Value("${device.queue.bootstrap}")
	String bootstrapName;


	@Bean
	public Queue createdDeviceQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue updatedDeviceQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue deletedDeviceQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue allDeletedDeviceQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue bootstrapDevicesQueue() {
		return new Queue(bootstrapName);
	}



	@Bean
	public TopicExchange topic() {
		return new TopicExchange(deviceTopic);
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(deviceBootstrapEx);
	}



	@Bean
	public Binding bindingCreate() {
		return BindingBuilder.bind(createdDeviceQueue()).to(topic()).with(createdDeviceKey);
	}

	@Bean
	public Binding bindingUpdate() {
		return BindingBuilder.bind(updatedDeviceQueue()).to(topic()).with(updatedDeviceKey);
	}

	@Bean
	public Binding bindingDeletedDevice() {
		return BindingBuilder.bind(deletedDeviceQueue()).to(topic()).with(deletedDeviceKey);
	}

	@Bean
	public Binding bindingAllDeletedDevice() {
		return BindingBuilder.bind(allDeletedDeviceQueue()).to(topic()).with(allDeletedDeviceKey);
	}

	@Bean
	public Binding bindingBootstrapDevices() {
		return BindingBuilder.bind(bootstrapDevicesQueue()).to(exchange()).with(bootstrapKey);
	}



	@Bean
	public MessageConverter messageConverter()
	{
		ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

		return new Jackson2JsonMessageConverter(mapper);
	}

	@Bean
	public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter());
		return rabbitTemplate;
	}

}
