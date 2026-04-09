package edu.eci.arsw.exam.events;

import java.io.Serializable;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.AmqpException;



public class OffertMessageProducer {

	protected AmqpTemplate amqpTemplate; 

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void sendMessages(Object message) throws AmqpException {
	    amqpTemplate.convertAndSend("my.routingkey.1",message);
	}

}
