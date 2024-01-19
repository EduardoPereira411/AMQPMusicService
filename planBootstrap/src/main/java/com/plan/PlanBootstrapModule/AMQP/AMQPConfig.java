package com.plan.PlanBootstrapModule.AMQP;


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
	String createdKey;

	@Value("${plan.routingKey.updated}")
	String updatedKey;


	@Value("${rabbit.plan.bootstrap}")
	String planBootstrapEx;

	@Value("${plan.routingKey.bootstrap}")
	String bootstrapKey;

	@Value("${plan.queue.bootstrap}")
	String bootstrapName;

	@Bean
	public Queue createdPlanQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue updatedPlanQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue bootstrapPlanQueue() {
		return new Queue(bootstrapName);
	}

	@Bean
	public TopicExchange topic() {
		return new TopicExchange(planTopic);
	}

	@Bean
	public DirectExchange direct() {
		return new DirectExchange(planBootstrapEx);
	}

	@Bean
	public Binding bindingCreate() {
		return BindingBuilder.bind(createdPlanQueue()).to(topic()).with(createdKey);
	}

	@Bean
	public Binding bindingUpdate() {
		return BindingBuilder.bind(updatedPlanQueue()).to(topic()).with(updatedKey);
	}

	@Bean
	public Binding bindingBootstrap() {
		return BindingBuilder.bind(bootstrapPlanQueue()).to(direct()).with(bootstrapKey);
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
