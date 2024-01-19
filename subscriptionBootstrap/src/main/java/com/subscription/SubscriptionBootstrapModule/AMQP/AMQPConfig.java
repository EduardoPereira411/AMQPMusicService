package com.subscription.SubscriptionBootstrapModule.AMQP;


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

	@Value("${rabbit.sub.topic}")
	String subTopic;

	@Value("${sub.routingKey.created}")
	String createdSubKey;

	@Value("${sub.routingKey.updated}")
	String updatedSubKey;


	@Value("${rabbit.subscription.bootstrap}")
	String subBootstrapEx;

	@Value("${subscription.routingKey.bootstrap}")
	String bootstrapKey;

	@Value("${subscription.queue.bootstrap}")
	String bootstrapName;


	@Bean
	public Queue createdSubQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue updatedSubQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue bootstrapSubQueue() {
		return new Queue(bootstrapName);
	}


	@Bean
	public TopicExchange subscriptionTopic() {
		return new TopicExchange(subTopic);
	}

	@Bean
	public DirectExchange bootstrapExchange() {
		return new DirectExchange(subBootstrapEx);
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
	public Binding bindingBootstrapSubs() {
		return BindingBuilder.bind(bootstrapSubQueue()).to(bootstrapExchange()).with(bootstrapKey);
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
