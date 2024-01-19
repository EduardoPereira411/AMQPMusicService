package com.device.DeviceCommandModule.AMQP;


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

	@Value("${rabbit.sub.topic}")
	String subTopic;

	@Value("${sub.routingKey.created}")
	String createdSubKey;

	@Value("${sub.routingKey.updated}")
	String updatedSubKey;

	@Value("${rabbit.plan.topic}")
	String planTopic;

	@Value("${plan.routingKey.created}")
	String createdPlanKey;

	@Value("${plan.routingKey.updated}")
	String updatedPlanKey;

	
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
	public Queue createdSubQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue updatedSubQueue() {
		return new AnonymousQueue();
	}


	@Bean
	public TopicExchange topic() {
		return new TopicExchange(deviceTopic);
	}

	@Bean
	public TopicExchange subtopic() {
		return new TopicExchange(subTopic);
	}


	@Bean
	public Binding bindingCreateDevice() {
		return BindingBuilder.bind(createdDeviceQueue()).to(topic()).with(createdDeviceKey);
	}

	@Bean
	public Binding bindingUpdateDevice() {
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
	public Binding bindingCreateSub() {
		return BindingBuilder.bind(createdSubQueue()).to(subtopic()).with(createdSubKey);
	}

	@Bean
	public Binding bindingUpdateSub() {
		return BindingBuilder.bind(updatedSubQueue()).to(subtopic()).with(updatedSubKey);
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

	@Bean
	public Queue createdPlanQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue updatedPlanQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public TopicExchange plantopic() {
		return new TopicExchange(planTopic);
	}

	@Bean
	public Binding bindingCreatePlan() {
		return BindingBuilder.bind(createdPlanQueue()).to(plantopic()).with(createdPlanKey);
	}

	@Bean
	public Binding bindingUpdatePlan() {
		return BindingBuilder.bind(updatedPlanQueue()).to(plantopic()).with(updatedPlanKey);
	}

}
