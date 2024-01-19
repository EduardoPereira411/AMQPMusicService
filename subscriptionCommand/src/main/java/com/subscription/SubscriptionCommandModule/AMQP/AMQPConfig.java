package com.subscription.SubscriptionCommandModule.AMQP;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class AMQPConfig {

	@Value("${rabbit.plan.topic}")
	String planTopic;

	@Value("${plan.routingKey.created}")
	String createdPlanKey;

	@Value("${plan.routingKey.updated}")
	String updatedPlanKey;

	@Value("${rabbit.bonusPlan.rpc}")
	String bonusPlanRPC;

	@Value("${rabbit.bonusPlan.queue}")
	String bonusPlanQueue;

	@Value("${bonusPlan.routingKey.created}")
	String bonusPlanCreatedKey;


	@Value("${rabbit.sub.topic}")
	String subTopic;

	@Value("${sub.routingKey.created}")
	String createdSubKey;

	@Value("${sub.routingKey.updated}")
	String updatedSubKey;


	@Value("${rabbit.user.topic}")
	String userTopic;

	@Value("${user.routingKey.created}")
	String createdUserKey;

	@Value("${user.routingKey.flip}")
	String flipUserKey;

	@Bean
	public Queue createdPlanQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue updatedPlanQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue bonusPlanRPCQueue() {
		return new Queue(bonusPlanQueue);
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
	public Queue createdUserQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue flipUserQueue() {
		return new AnonymousQueue();
	}


	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(bonusPlanRPC);
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
	public TopicExchange userTopic() {
		return new TopicExchange(userTopic);
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
	public Binding bindingBonusPlanCreate() {
		return BindingBuilder.bind(bonusPlanRPCQueue()).to(exchange()).with(bonusPlanCreatedKey);
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
	public Binding bindingCreateUser() {
		return BindingBuilder.bind(createdUserQueue()).to(userTopic()).with(createdUserKey);
	}
	@Bean
	public Binding bindingFlipUser() {
		return BindingBuilder.bind(flipUserQueue()).to(userTopic()).with(flipUserKey);
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
