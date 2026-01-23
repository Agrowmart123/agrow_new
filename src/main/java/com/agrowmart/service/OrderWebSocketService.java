package com.agrowmart.service;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.agrowmart.dto.auth.order.OrderStatusUpdateDTO;

@Service
public class OrderWebSocketService {

    private final SimpMessagingTemplate template;

    public OrderWebSocketService(SimpMessagingTemplate template) {
        this.template = template;
    }

    // Send private message to specific user
    public void sendToUser(String userId, OrderStatusUpdateDTO update) {
        template.convertAndSendToUser(userId, "/queue/orders", update);
    }

    // Broadcast to all delivery partners
    public void broadcastToDeliveryPartners(OrderStatusUpdateDTO update) {
        template.convertAndSend("/topic/delivery/pickups", update);
    }

	public void broadcastNewPickup(OrderStatusUpdateDTO update) {
		// TODO Auto-generated method stub
		
	}
}