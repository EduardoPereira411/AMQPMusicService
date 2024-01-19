package com.user.UserCommandModule.AMQP;


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

	@Value("${rabbit.user.topic}")
	String userTopic;

	@Value("${user.routingKey.created}")
	String createdKey;

	@Value("${user.routingKey.updated}")
	String updatedKey;

	@Value("${user.routingKey.flip}")
	String flipUserKey;

	@Bean
	public Queue createdUserQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue updatedUserQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue flipUserQueue() {
		return new AnonymousQueue();
	}
	@Bean
	public TopicExchange topic() {
		return new TopicExchange(userTopic);
	}

	@Bean
	public Binding bindingCreate() {
		return BindingBuilder.bind(createdUserQueue()).to(topic()).with(createdKey);
	}

	@Bean
	public Binding bindingUpdate() {
		return BindingBuilder.bind(updatedUserQueue()).to(topic()).with(updatedKey);
	}

	@Bean
	public Binding bindingFlipUser() {
		return BindingBuilder.bind(flipUserQueue()).to(topic()).with(flipUserKey);
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
