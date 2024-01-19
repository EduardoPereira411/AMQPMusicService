package com.subscription.SubscriptionQueryModule.AMQP;


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

	@Value("${rabbit.plan.topic}")
	String planTopic;

	@Value("${plan.routingKey.created}")
	String createdPlanKey;

	@Value("${plan.routingKey.updated}")
	String updatedPlanKey;


	@Value("${rabbit.sub.topic}")
	String subTopic;

	@Value("${sub.routingKey.created}")
	String createdSubKey;

	@Value("${sub.routingKey.updated}")
	String updatedSubKey;

	@Bean
	public Queue createdPlanQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue updatedPlanQueue() {
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
	public TopicExchange planTopic() {
		return new TopicExchange(planTopic);
	}

	@Bean
	public TopicExchange subscriptionTopic() {
		return new TopicExchange(subTopic);
	}

	@Bean
	public Binding bindingCreatePlan() {
		return BindingBuilder.bind(createdPlanQueue()).to(planTopic()).with(createdPlanKey);
	}

	@Bean
	public Binding bindingUpdatePlan() {
		return BindingBuilder.bind(updatedPlanQueue()).to(planTopic()).with(updatedPlanKey);
	}

	@Bean
	public Binding bindingCreateSub() {
		return BindingBuilder.bind(createdSubQueue()).to(subscriptionTopic()).with(createdSubKey);
	}

	@Bean
	public Binding bindingUpdateSub() {
		return BindingBuilder.bind(updatedSubQueue()).to(subscriptionTopic()).with(updatedSubKey);
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
