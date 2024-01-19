package com.subscription.SubscriptionBootstrapModule.AMQP;

import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.model.Subscription;
import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.repositories.SubscriptionRepository;
import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AMQPReceiver {

	private final SubscriptionRepository subscriptionRepository;

	private final SubscriptionService subscriptionService;


	@RabbitListener(queues = "#{createdSubQueue.name}")
	public void receiveCreatedSub(Subscription sub) {
		final var isSub = subscriptionRepository.findBySubID(sub.getSubID());
		if(isSub.isEmpty()){
			System.out.println("Sub " +sub.getUserId() + " created");
			subscriptionRepository.save(sub);
		}
	}

	@RabbitListener(queues = "#{updatedSubQueue.name}")
	public void receiveUpdatedSub(Subscription sub) {
		subscriptionService.updateSubAMQP(sub);
		System.out.println("Sub " + sub.getUserId() + " updated");

	}

	@RabbitListener(queues = "#{bootstrapSubQueue.name}")
	public Iterable<Subscription> giveAllSubs() {
		System.out.println("Subs sent");
		return subscriptionService.findAll();
	}

}
