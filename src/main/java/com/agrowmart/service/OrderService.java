//package com.agrowmart.service;
//
////
////import java.util.List;
////
////import com.agrowmart.dto.auth.order.OrderRequestDTO;
////import com.agrowmart.dto.auth.order.OrderResponseDTO;
////import com.agrowmart.entity.User;
////
////public interface OrderService {
////    OrderResponseDTO createOrder(User customer, OrderRequestDTO request);
////    List<OrderResponseDTO> getCustomerOrders(User customer);
////    List<OrderResponseDTO> getVendorPendingOrders(User vendor);
////    OrderResponseDTO acceptOrder(Long orderId, User vendor);
////    OrderResponseDTO rejectOrder(Long orderId, User vendor);
////	List<OrderResponseDTO> getAllVendorOrders(User vendor);
////    
////}
//
//
////=============
//
//import com.agrowmart.dto.auth.order.OrderRequestDTO;
//import com.agrowmart.dto.auth.order.OrderResponseDTO;
//import com.agrowmart.entity.User;
//import java.util.List;
//
//public interface OrderService {
//    OrderResponseDTO createOrder(User customer, OrderRequestDTO request);
//    List<OrderResponseDTO> getCustomerOrders(User customer);
//    List<OrderResponseDTO> getVendorPendingOrders(User vendor);
//    OrderResponseDTO acceptOrder(String orderId, User vendor);
//    OrderResponseDTO rejectOrder(String orderId, User vendor);
//    OrderResponseDTO markAsDelivered(String orderId, User vendor);
//   
//    List<OrderResponseDTO> getAllVendorOrders(User vendor);
//    
//    
//    
//    OrderResponseDTO cancelOrderByCustomer(String orderId, User customer, String reason);
//    OrderResponseDTO cancelOrderByCustomer(String orderId, String reason, User customer);
//    OrderResponseDTO cancelOrderByVendor(
//    	    String orderId,
//    	    User vendor,
//    	    String reason
//    	);
//	OrderResponseDTO confirmCodCollected(String orderId, User vendor);
//
//}





// src/main/java/com/agrowmart/service/OrderService.java

package com.agrowmart.service;

import com.agrowmart.dto.auth.order.OrderRequestDTO;
import com.agrowmart.dto.auth.order.OrderResponseDTO;
import com.agrowmart.dto.auth.order.OrderStatusResponseDTO;
import com.agrowmart.dto.auth.order.ScanRequestDTO;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.order.Order;
import com.agrowmart.entity.User;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(Customer customer, OrderRequestDTO request);
    List<OrderResponseDTO> getCustomerOrders(Customer customer);
    List<OrderResponseDTO> getVendorPendingOrders(User vendor);
    OrderResponseDTO acceptOrder(String orderId, User vendor);
    OrderResponseDTO rejectOrder(String orderId, User vendor);
    OrderResponseDTO markAsDelivered(String orderId, User vendor);
    List<OrderResponseDTO> getAllVendorOrders(User vendor);

    OrderResponseDTO cancelOrderByCustomer(String orderId, Customer customer, String reason);
    OrderResponseDTO cancelOrderByVendor(String orderId, User vendor, String reason);
    OrderResponseDTO confirmCodCollected(String orderId, User vendor);

    
 // New - when vendor marks order as ready (key decision point)
    OrderResponseDTO markOrderReady(String orderId, User vendor);

    // Existing QR methods (keep or add)
    OrderResponseDTO generateVendorPickupQR(String orderId, User vendor);

    OrderResponseDTO scanToken(String orderId, ScanRequestDTO scanRequest, User scanner);
	Object getVendorScheduledOrders(User vendor);

    
	Order getOrderById(String orderId);
	OrderStatusResponseDTO getOrderStatus(String orderId);
	
	
	
}