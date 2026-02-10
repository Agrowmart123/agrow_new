
package com.agrowmart.service;

import com.agrowmart.dto.auth.order.*;
import com.agrowmart.entity.*;
import com.agrowmart.entity.Product.ProductStatus;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.customer.CustomerAddress;
import com.agrowmart.entity.order.*;
import com.agrowmart.enums.DeliveryMode;
import com.agrowmart.enums.VendorAcceptThenCancelReason;
import com.agrowmart.enums.VendorCancelReason;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.*;
import com.agrowmart.entity.customer.Cart;
import com.agrowmart.entity.customer.CartItem;
import com.agrowmart.repository.customer.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	
    private final OrderWebSocketService orderWebSocketService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VegetableDetailRepository vegetableDetailRepository;
    private final DairyDetailRepository dairyDetailRepository;
    private final MeatDetailRepository meatDetailRepository;
    private final WomenProductRepository womenProductRepository;
    private final OfferRepository offerRepository;
    private final OfferUsageRepository offerUsageRepository;
    private final NotificationService notificationService;
    private final CartRepository cartRepository;
    private final AgriProductRepository agriProductRepository;


 
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderStatusHistoryRepository statusHistoryRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            VegetableDetailRepository vegetableDetailRepository,
            DairyDetailRepository dairyDetailRepository,
            MeatDetailRepository meatDetailRepository,
            WomenProductRepository womenProductRepository,
            OfferRepository offerRepository,
            OfferUsageRepository offerUsageRepository,
            NotificationService notificationService,
            OrderWebSocketService  orderWebSocketService,
            CartRepository cartRepository,
            AgriProductRepository agriProductRepository
    		) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.vegetableDetailRepository = vegetableDetailRepository;
        this.dairyDetailRepository = dairyDetailRepository;
        this.meatDetailRepository = meatDetailRepository;
        this.womenProductRepository = womenProductRepository;
        this.offerRepository = offerRepository;
        this.offerUsageRepository = offerUsageRepository;
        this.notificationService = notificationService;
        this.orderWebSocketService=orderWebSocketService;
        this.cartRepository = cartRepository;
        this.agriProductRepository = agriProductRepository;

    }

//------------  Single order code 
  //  @Override
  //  @Transactional
//    public OrderResponseDTO createOrder(Customer customer, OrderRequestDTO request) {
//        User merchant = userRepository.findById(request.merchantId())
//                .orElseThrow(() -> new ResourceNotFoundException("Merchant (vendor) not found"));
//
//        Order order = new Order();
//        order.setCustomer(customer);
//        order.setMerchant(merchant);
//        order.setPaymentMode(request.paymentMode());
//        order.setDeliveryMode(request.deliveryMode() != null ? request.deliveryMode() : DeliveryMode.SELF_DELIVERY);
//        order.setPaymentStatus("PENDING");
//        order.setSettlementStatus("PENDING");
//
//        // Scheduling logic
//        if (request.scheduledDate() != null && request.scheduledDate().isAfter(LocalDate.now())) {
//            order.setScheduled(true);
//            order.setScheduledDate(request.scheduledDate());
//            order.setScheduledSlot(request.scheduledSlot());
//            order.setStatus(Order.OrderStatus.SCHEDULED);
//        } else {
//            order.setStatus(Order.OrderStatus.PENDING);
//        }
//
//        order.setCreatedAt(LocalDateTime.now());
//        order.setUpdatedAt(LocalDateTime.now());
//
//        BigDecimal subtotal = BigDecimal.ZERO;
//
//        for (OrderItemRequestDTO reqItem : request.items()) {
//            Long productId = reqItem.productId();
//            Product normalProduct = productRepository.findById(productId).orElse(null);
//            WomenProduct womenProduct = null;
//            BigDecimal price;
//            double availableStock;
//            String productType = null;
//
//            if (normalProduct != null) {
//                productType = "NORMAL";
//                price = getProductPrice(normalProduct);
//                availableStock = normalProduct.getStockQuantity() != null ? normalProduct.getStockQuantity() : 0.0;
//            } else {
//                womenProduct = womenProductRepository.findById(productId).orElse(null);
//                if (womenProduct == null) {
//                    throw new ResourceNotFoundException("Product not found with ID: " + productId);
//                }
//                productType = "WOMEN";
//                price = womenProduct.getMinPrice();
//                availableStock = womenProduct.getStock() != null ? womenProduct.getStock() : 0;
//            }
//
//            if (availableStock < reqItem.quantity()) {
//                String name = normalProduct != null ? normalProduct.getProductName() :
//                        (womenProduct != null ? womenProduct.getName() : "Unknown");
//                throw new IllegalStateException("Not enough stock for: " + name);
//            }
//
//            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(reqItem.quantity()));
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setQuantity(reqItem.quantity());
//            orderItem.setPricePerUnit(price);
//            orderItem.setTotalPrice(itemTotal);
//
//            if ("NORMAL".equals(productType)) {
//                orderItem.setProduct(normalProduct);
//                normalProduct.updateStock(reqItem.quantity());
//                productRepository.save(normalProduct);
//            } else {
//                orderItem.setWomenProduct(womenProduct);
//                womenProduct.setStock(womenProduct.getStock() - reqItem.quantity());
//                womenProductRepository.save(womenProduct);
//            }
//
//            order.getItems().add(orderItem);
//            subtotal = subtotal.add(itemTotal);
//        }
//
//        order.setSubtotal(subtotal);
//
//        // Promo code logic
//        BigDecimal discount = BigDecimal.ZERO;
//        String appliedOfferCode = null;
//        if (request.promoCode() != null && !request.promoCode().trim().isBlank()) {
//            String code = request.promoCode().trim().toUpperCase();
//            Offer offer = offerRepository.findActiveOfferByCodeAndMerchant(code, merchant.getId(), LocalDate.now())
//                    .orElse(null);
//            if (offer != null && isOfferApplicable(offer, customer, merchant, subtotal)) {
//                if (offerUsageRepository.existsByCustomerAndOffer(customer, offer)) {
//                    throw new IllegalStateException("Coupon already used");
//                }
//                discount = calculateDiscount(offer, subtotal);
//                appliedOfferCode = offer.getCode();
//                OfferUsage usage = new OfferUsage();
//                usage.setCustomer(customer);
//                usage.setOffer(offer);
//                usage.setOrder(order);
//                offerUsageRepository.save(usage);
//            }
//        }
//
//        order.setDiscountAmount(discount);
//        order.setPromoCode(appliedOfferCode);
//
//        BigDecimal afterDiscount = subtotal.subtract(discount);
//        BigDecimal deliveryCharge = afterDiscount.compareTo(BigDecimal.valueOf(100)) < 0
//                ? BigDecimal.valueOf(25) : BigDecimal.ZERO;
//
//        order.setDeliveryCharge(deliveryCharge);
//        order.setTotalPrice(afterDiscount.add(deliveryCharge));
//
//     // ──────────────────────────────────────────────
//        // IMPORTANT: Validate and set delivery address
//        // ──────────────────────────────────────────────
//        if (request.deliveryAddressId() == null) {
//            throw new IllegalArgumentException("Delivery address ID is required");
//        }
//
//        Hibernate.initialize(customer.getAddresses());
//        CustomerAddress deliveryAddress = customer.getAddresses().stream()
//                .filter(addr -> addr.getId().equals(request.deliveryAddressId()))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException(
//                        "Invalid or unauthorized delivery address ID: " + request.deliveryAddressId()
//                ));
//
//        // Set it now - guaranteed not null
//        order.setDeliveryAddress(deliveryAddress);
//        order = orderRepository.save(order);
//        addStatusHistory(order, order.getStatus().name());
//        
//        
//        
//        // Notify vendor (merchant)
//        notificationService.sendNotification(
//                merchant.getId(),
//                "New Order Received",
//                "Order #" + order.getId() + " | ₹" + order.getTotalPrice() + " | " + order.getPaymentMode(),
//                Map.of("type", "new_order", "orderId", order.getId())
//        );
//        notifyOrderUpdate(order, "New order placed!", "NEW_ORDER");
//        
//  
//        return mapToResponse(order);
//    }
//    
//  
    @Override
    @Transactional
    public OrderResponseDTO createOrder(Customer customer, OrderRequestDTO request) {
        // Validate common address
    	if (customer == null) {
            throw new AuthenticationFailedException("Customer must be authenticated");
        }

        if (request.deliveryAddressId() == null) {
            throw new BusinessValidationException("Delivery address ID is required");
        }
        Hibernate.initialize(customer.getAddresses());
        CustomerAddress deliveryAddress = customer.getAddresses().stream()
                .filter(addr -> addr.getId().equals(request.deliveryAddressId()))
                .findFirst()
                .orElseThrow(() -> new BusinessValidationException(
                        "Invalid delivery address ID: " + request.deliveryAddressId()
                ));

        Order createdOrder; // We'll return this one

        // Case 1: Single vendor (old format)
        if (request.merchantId() != null && request.items() != null && !request.items().isEmpty()) {
            createdOrder = createSingleVendorOrder(customer, request.merchantId(), request.items(), request, deliveryAddress);
        } 
        // Case 2: Multi-vendor (new format)
        else if (request.vendorGroups() != null && !request.vendorGroups().isEmpty()) {
            List<Order> allOrders = new ArrayList<>();
            for (var group : request.vendorGroups()) {
                Order multiOrder = createSingleVendorOrder(customer, group.merchantId(), group.items(), request, deliveryAddress);
                allOrders.add(multiOrder);
            }
            // Return the first created order (or you can change to return a summary)
            createdOrder = allOrders.get(0);
        } 
        else {
            throw new BusinessValidationException("Either merchantId+items or vendorGroups must be provided");
        }

        return mapToResponse(createdOrder);
    }

    // Helper method (same as before, but now reusable)
    private Order createSingleVendorOrder(Customer customer, Long merchantId, List<OrderItemRequestDTO> items,
                                          OrderRequestDTO request, CustomerAddress deliveryAddress) {
        User merchant = userRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setMerchant(merchant);
        order.setPaymentMode(request.paymentMode());
        order.setDeliveryMode(request.deliveryMode());
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentStatus("PENDING");
        order.setSettlementStatus("PENDING");

        // Scheduling
        if (request.scheduledDate() != null && request.scheduledDate().isAfter(LocalDate.now())) {
            order.setScheduled(true);
            order.setScheduledDate(request.scheduledDate());
            order.setScheduledSlot(request.scheduledSlot());
            order.setStatus(Order.OrderStatus.SCHEDULED);
        } else {
            order.setStatus(Order.OrderStatus.PENDING);
        }

        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;

        // Process items for this vendor
//        for (OrderItemRequestDTO reqItem : items) {
//            Long productId = reqItem.productId();
//            Product normalProduct = productRepository.findById(productId).orElse(null);
//            WomenProduct womenProduct = null;
//            BigDecimal price;
//            double availableStock;
//            String productType = null;
//
//            if (normalProduct != null) {
//                productType = "NORMAL";
//                price = getProductPrice(normalProduct);
//                availableStock = normalProduct.getStockQuantity() != null ? normalProduct.getStockQuantity() : 0.0;
//            } else {
//                womenProduct = womenProductRepository.findById(productId).orElse(null);
//                if (womenProduct == null) {
//                    throw new ResourceNotFoundException("Product not found with ID: " + productId);
//                }
//                productType = "WOMEN";
//                price = womenProduct.getMinPrice();
//                availableStock = womenProduct.getStock() != null ? womenProduct.getStock() : 0;
//            }
//
//            if (availableStock < reqItem.quantity()) {
//                String name = normalProduct != null ? normalProduct.getProductName() :
//                        (womenProduct != null ? womenProduct.getName() : "Unknown");
//                throw new IllegalStateException("Not enough stock for: " + name);
//            }
//
//            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(reqItem.quantity()));
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setQuantity(reqItem.quantity());
//            orderItem.setPricePerUnit(price);
//            orderItem.setTotalPrice(itemTotal);
//
//            if ("NORMAL".equals(productType)) {
//                orderItem.setProduct(normalProduct);
//                normalProduct.updateStock(reqItem.quantity());
//                productRepository.save(normalProduct);
//            } else {
//                orderItem.setWomenProduct(womenProduct);
//                womenProduct.setStock(womenProduct.getStock() - reqItem.quantity());
//                womenProductRepository.save(womenProduct);
//            }
//
//            order.getItems().add(orderItem);
//            subtotal = subtotal.add(itemTotal);
//        }
        
        //21 Jan 
     // Process items for this vendor
//        for (OrderItemRequestDTO reqItem : items) {
//
//            Long productId = reqItem.productId();
//
//            BigDecimal price;
//            double availableStock;
//            String productType;
//            
//         // Try normal product
//            Product normalProduct =
//            	    productRepository
//            	        .findByIdAndMerchantIdAndStatusAndApprovalStatus(
//            	            productId,
//            	            merchantId,
//            	            ProductStatus.ACTIVE,
//            	            ApprovalStatus.APPROVED
//            	        )
//            	        .orElse(null);
//
//            WomenProduct womenProduct = null;
//            BaseAgriProduct agriProduct = null;
//            if (normalProduct != null) {
//
//                // ✅ NORMAL PRODUCT (SAFE)
//                productType = "NORMAL";
//                price = getProductPrice(normalProduct);
//                availableStock = normalProduct.getStockQuantity() != null
//                        ? normalProduct.getStockQuantity()
//                        : 0.0;
//
//            } else {
//            	
//            	// restore logic 
//            	womenProduct =
//            		    womenProductRepository
//            		        .findByIdAndSeller_IdAndStatusAndApprovalStatus(
//            		            productId,
//            		            merchantId,
//            		            ProductStatus.ACTIVE,
//            		            ApprovalStatus.APPROVED
//            		        )
//            		        .orElseThrow(() ->
//            		            new ForbiddenException(
//            		                "Product is not approved / not active / deleted / does not belong to this merchant"
//            		            )
//            		        );
//
//              
//                productType = "WOMEN";
//                price = womenProduct.getMinPrice();
//                availableStock = womenProduct.getStock() != null
//                        ? womenProduct.getStock()
//                        : 0;
//            }
        for (OrderItemRequestDTO reqItem : items) {
            Long productId = reqItem.productId();
            BigDecimal price;
            double availableStock;
            String productType;

            // Try normal product
            Product normalProduct = productRepository
                    .findByIdAndMerchantIdAndStatusAndApprovalStatus(
                            productId,
                            merchantId,
                            ProductStatus.ACTIVE,
                            ApprovalStatus.APPROVED
                    )
                    .orElse(null);

            WomenProduct womenProduct = null;
            BaseAgriProduct agriProduct = null;

            if (normalProduct != null) {
                productType = "NORMAL";
                price = getProductPrice(normalProduct);
                availableStock = normalProduct.getStockQuantity() != null ? normalProduct.getStockQuantity() : 0.0;
            } else {
                // Try women product
                womenProduct = womenProductRepository
                        .findByIdAndSeller_IdAndStatusAndApprovalStatus(
                                productId,
                                merchantId,
                                ProductStatus.ACTIVE,
                                ApprovalStatus.APPROVED
                        )
                        .orElse(null);

                if (womenProduct != null) {
                    productType = "WOMEN";
                    price = womenProduct.getMinPrice();
                    availableStock = womenProduct.getStock() != null ? womenProduct.getStock() : 0.0;
                } else {
                    // Try agri product
                    agriProduct = agriProductRepository.findById(productId).orElse(null);
                    if (agriProduct != null && agriProduct.getVendor().getId().equals(merchantId)) {
                        productType = "AGRI";
                        price = agriProduct.getAgriprice();
                        availableStock = agriProduct.getAgriquantity() != null ? agriProduct.getAgriquantity() : 0.0;
                    } else {
                        throw new ResourceNotFoundException("Product not found or not approved/active for this merchant");
                    }
                }
            }

            // ✅ STOCK CHECK
            if (availableStock < reqItem.quantity()) {
                String name = normalProduct != null
                        ? normalProduct.getProductName()
                        : womenProduct.getName();
                throw new BusinessValidationException("Not enough stock for: " + name);
            }

            BigDecimal itemTotal =
                    price.multiply(BigDecimal.valueOf(reqItem.quantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(reqItem.quantity());
            orderItem.setPricePerUnit(price);
            orderItem.setTotalPrice(itemTotal);
//
//            if ("NORMAL".equals(productType)) {
//                orderItem.setProduct(normalProduct);
//                normalProduct.updateStock(reqItem.quantity());
//                productRepository.save(normalProduct);
//            } else {
//                orderItem.setWomenProduct(womenProduct);
//                womenProduct.setStock(womenProduct.getStock() - reqItem.quantity());
//                womenProductRepository.save(womenProduct);
//            }

         // Attach correct product & reduce stock
            if ("NORMAL".equals(productType)) {
                orderItem.setProduct(normalProduct);
                normalProduct.updateStock(reqItem.quantity());
                productRepository.save(normalProduct);
            } else if ("WOMEN".equals(productType)) {
                orderItem.setWomenProduct(womenProduct);
                womenProduct.setStock(womenProduct.getStock() - reqItem.quantity());
                womenProductRepository.save(womenProduct);
            } else if ("AGRI".equals(productType)) {
                orderItem.setAgriProduct(agriProduct);
                agriProduct.setAgriquantity(agriProduct.getAgriquantity() - reqItem.quantity());
                agriProductRepository.save(agriProduct);
            }
            
            order.getItems().add(orderItem);
            subtotal = subtotal.add(itemTotal);
        }


//
        order.setSubtotal(subtotal);

        // Promo code & discount (per order)
        BigDecimal discount = BigDecimal.ZERO;
        String appliedOfferCode = null;
        if (request.promoCode() != null && !request.promoCode().trim().isBlank()) {
            String code = request.promoCode().trim().toUpperCase();
            Offer offer = offerRepository.findActiveOfferByCodeAndMerchant(code, merchant.getId(), LocalDate.now())
                    .orElse(null);
            if (offer != null && isOfferApplicable(offer, customer, merchant, subtotal)) {
                if (offerUsageRepository.existsByCustomerAndOffer(customer, offer)) {
                    throw new BusinessValidationException("Coupon already used");
                }
                discount = calculateDiscount(offer, subtotal);
                appliedOfferCode = offer.getCode();
                OfferUsage usage = new OfferUsage();
                usage.setCustomer(customer);
                usage.setOffer(offer);
                usage.setOrder(order);
                offerUsageRepository.save(usage);
            }
        }

        order.setDiscountAmount(discount);
        order.setPromoCode(appliedOfferCode);

        BigDecimal afterDiscount = subtotal.subtract(discount);
        BigDecimal deliveryCharge = afterDiscount.compareTo(BigDecimal.valueOf(100)) < 0
                ? BigDecimal.valueOf(25) : BigDecimal.ZERO;

        order.setDeliveryCharge(deliveryCharge);
        order.setTotalPrice(afterDiscount.add(deliveryCharge));

        // Save this order
        order = orderRepository.save(order);
        addStatusHistory(order, order.getStatus().name());

        // Notify this specific vendor + WebSocket
        notificationService.sendNotification(
                merchant.getId(),
                "New Order Received",
                "Order #" + order.getId() + " | ₹" + order.getTotalPrice() + " | " + order.getPaymentMode(),
                Map.of("type", "new_order", "orderId", order.getId())
        );

        notifyOrderUpdate(order, "New order placed!", "NEW_ORDER");

        return order;
        }

    @Transactional
    public OrderResponseDTO createOrderFromCart(Customer customer, OrderRequestDTO request) {

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new BusinessValidationException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessValidationException("Cart has no items");
        }

        CustomerAddress deliveryAddress = customer.getAddresses().stream()
                .filter(a -> a.getId().equals(request.deliveryAddressId()))
                .findFirst()
                .orElseThrow(() -> new BusinessValidationException("Invalid delivery address"));

        Map<Long, List<CartItem>> itemsByVendor =
                cart.getItems().stream()
                        .collect(Collectors.groupingBy(this::resolveMerchantId));

        Order firstOrder = null;

        for (Map.Entry<Long, List<CartItem>> entry : itemsByVendor.entrySet()) {

            Order order = createOrderForVendor(
                    customer,
                    entry.getKey(),
                    entry.getValue(),
                    request,
                    deliveryAddress
            );

            if (firstOrder == null) {
                firstOrder = order;
            }
        }

        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToResponse(firstOrder);
    }

    private Order createOrderForVendor(
            Customer customer,
            Long merchantId,
            List<CartItem> cartItems,
            OrderRequestDTO request,
            CustomerAddress deliveryAddress
    ) {

        User merchant = userRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setMerchant(merchant);
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentMode(request.paymentMode());
        order.setDeliveryMode(request.deliveryMode());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus("PENDING");
        order.setSettlementStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            BigDecimal price = cartItem.getPriceAtAdd();
            int qty = cartItem.getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(qty);
            orderItem.setPricePerUnit(price);
            orderItem.setTotalPrice(price.multiply(BigDecimal.valueOf(qty)));

            switch (cartItem.getProductType().toUpperCase()) {

                case "REGULAR" -> {
                    Product product = productRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                    if (!product.getMerchantId().equals(merchantId)) {
                        throw new BusinessValidationException("Product does not belong to this merchant");
                    }

                    if (product.getStockQuantity() < qty) {
                        throw new BusinessValidationException("Insufficient stock");
                    }

                    product.setStockQuantity(product.getStockQuantity() - qty);
                    productRepository.save(product);

                    orderItem.setProduct(product);
                }

                case "WOMEN" -> {
                    WomenProduct product = womenProductRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

                    if (!product.getSeller().getId().equals(merchantId)) {
                        throw new BusinessValidationException("Women product does not belong to this merchant");
                    }

                    if (product.getStock() < qty) {
                        throw new BusinessValidationException("Insufficient stock");
                    }

                    product.setStock(product.getStock() - qty);
                    womenProductRepository.save(product);

                    orderItem.setWomenProduct(product);
                }

                case "AGRI" -> {

                    BaseAgriProduct product = agriProductRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Agri product not found"));

                    // ✅ vendor check (NOT merchant)
                    if (!product.getVendor().getId().equals(merchantId)) {
                        throw new BusinessValidationException("Agri product does not belong to this vendor");
                    }

                    Integer availableQty = product.getAgriquantity();

                    if (availableQty == null || availableQty < qty) {
                        throw new BusinessValidationException("Insufficient stock for agri product");
                    }

                    // ✅ reduce stock
                    product.setAgriquantity(availableQty - qty);
                    agriProductRepository.save(product);

                    // ✅ attach product to order item
                    orderItem.setAgriProduct(product);
                }

                default -> throw new BusinessValidationException(
                        "Invalid product type: " + cartItem.getProductType()
                );
            }

            order.getItems().add(orderItem);
            subtotal = subtotal.add(orderItem.getTotalPrice());
        }

        order.setSubtotal(subtotal);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setDeliveryCharge(BigDecimal.ZERO);
        order.setTotalPrice(subtotal);

        orderRepository.save(order);
        addStatusHistory(order, order.getStatus().name());

        notificationService.sendNotification(
                merchant.getId(),
                "New Order Received",
                "Order #" + order.getId(),
                null
        );

        return order;
    }

    private Long resolveMerchantId(CartItem item) {

        return switch (item.getProductType().toUpperCase()) {

            case "REGULAR" -> productRepository.findById(item.getProductId())
                    .map(Product::getMerchantId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product not found"));

            case "WOMEN" -> womenProductRepository.findById(item.getProductId())
                    .map(p -> p.getSeller().getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Women product not found"));

            case "AGRI" -> agriProductRepository.findById(item.getProductId())
                    .map(BaseAgriProduct::getVendor)   // ✅ vendor (not merchant)
                    .map(User::getId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Agri product not found"));

            default -> throw new BusinessValidationException(
                    "Invalid product type: " + item.getProductType()
            );
        };
    }



    
 // ──────────────────────────────────────────────
    // Vendor marks order as ready → key decision point
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public OrderResponseDTO markOrderReady(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);

        if (order.getStatus() != Order.OrderStatus.ACCEPTED) {
            throw new BusinessValidationException("Order must be ACCEPTED before marking as ready");
        }

        LocalDateTime now = LocalDateTime.now();

        if (order.getDeliveryMode() == DeliveryMode.SELF_DELIVERY) {
            // ─── SELF-DELIVERY FLOW ──────────────────────────────────────
            order.setStatus(Order.OrderStatus.OUT_FOR_DELIVERY);
            
            // Optional: Generate delivery QR token for customer (fallback if needed)
            String deliveryToken = UUID.randomUUID().toString();
            order.setUserDeliveryToken(deliveryToken);
            order.setUserDeliveryTokenExpiry(now.plusMinutes(180)); // 3 hours for self-delivery

            // IMPORTANT: Send clear notification to customer
            notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Ready - Self Delivery",
                "Your order #" + order.getId() + " is ready and will be delivered by the vendor soon!",
                null
            );

            // Optional: Notify vendor himself for tracking
            notificationService.sendNotification(
                vendor.getId(),
                "Self-Delivery Order Ready",
                "Order #" + order.getId() + " is ready for self-delivery",
                null
            );

        } else {
            // ─── DELIVERY_PARTNER FLOW ───────────────────────────────────
            String pickupToken = UUID.randomUUID().toString();
            order.setVendorPickupToken(pickupToken);
            order.setVendorPickupTokenExpiry(now.plusMinutes(30));
            order.setStatus(Order.OrderStatus.READY_FOR_PICKUP);

            // Broadcast to all delivery partners
            notificationService.sendNotificationToRole(
                "DELIVERY",
                "New Pickup Ready",
                "Order #" + order.getId() + " is ready for pickup at vendor shop",
                Map.of("orderId", order.getId(), "vendorId", vendor.getId().toString())
            );
        }

        order.setUpdatedAt(now);
        orderRepository.save(order);
        addStatusHistory(order, order.getStatus().name());
     // ← Real-time WebSocket update
        notifyOrderUpdate(order, null, null);
        return mapToResponse(order);
    }
    
    
 // ──────────────────────────────────────────────
    // Delivery Partner scans QR (pickup or delivery)
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public OrderResponseDTO scanToken(String orderId, ScanRequestDTO scanRequest, User scanner) {
        // Only DELIVERY role can scan
        if (scanner.getRole() == null || !"DELIVERY".equals(scanner.getRole().getName())) {
            throw new ForbiddenException("Only delivery partners can scan QR codes");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        String providedToken = scanRequest.token();
        String type = scanRequest.type();

        if ("VENDOR_PICKUP".equals(type)) {
            // ─── Pickup Scan ───────────────────────────────────────────
            if (!providedToken.equals(order.getVendorPickupToken())) {
                throw new BusinessValidationException("Invalid pickup token");
            }
            if (LocalDateTime.now().isAfter(order.getVendorPickupTokenExpiry())) {
                throw new BusinessValidationException("Pickup token expired");
            }
            if (order.getStatus() != Order.OrderStatus.READY_FOR_PICKUP) {
                throw new BusinessValidationException("Order not ready for pickup");
            }

            order.setVendorPickupToken(null); // used
            order.setVendorPickupTokenExpiry(null);
            order.setDeliveryPartner(scanner);
            order.setPickupTime(LocalDateTime.now());
            order.setStatus(Order.OrderStatus.PICKED_UP);

            // Auto-create delivery QR for customer
            String deliveryToken = UUID.randomUUID().toString();
            order.setUserDeliveryToken(deliveryToken);
            order.setUserDeliveryTokenExpiry(LocalDateTime.now().plusMinutes(90));
            order.setStatus(Order.OrderStatus.OUT_FOR_DELIVERY);

            orderRepository.save(order);
            addStatusHistory(order, "PICKED_UP");
            addStatusHistory(order, "OUT_FOR_DELIVERY");

            notificationService.sendNotification(
                    order.getCustomer().getId(),
                    "Order Picked Up",
                    "Your order #" + order.getId() + " is now out for delivery",
                    null
            );

        } else if ("USER_DELIVERY".equals(type)) {
            // ─── Delivery Confirmation Scan ────────────────────────────
            if (!providedToken.equals(order.getUserDeliveryToken())) {
                throw new IllegalArgumentException("Invalid delivery token");
            }
            if (LocalDateTime.now().isAfter(order.getUserDeliveryTokenExpiry())) {
                throw new IllegalArgumentException("Delivery token expired");
            }
            if (order.getStatus() != Order.OrderStatus.OUT_FOR_DELIVERY) {
                throw new IllegalStateException("Order not out for delivery");
            }
            if (!order.getDeliveryPartner().getId().equals(scanner.getId())) {
                throw new ForbiddenException("You are not the assigned delivery partner");
            }

            order.setUserDeliveryToken(null); // used
            order.setUserDeliveryTokenExpiry(null);
            order.setStatus(Order.OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());

            orderRepository.save(order);
            addStatusHistory(order, "DELIVERED");

            notificationService.sendNotification(
                    order.getCustomer().getId(),
                    "Order Delivered",
                    "Your order #" + order.getId() + " has been successfully delivered!",
                    null
            );

        } else {
            throw new IllegalArgumentException("Invalid scan type: " + type);
        }

        return mapToResponse(order);
    }
    
    
    
    
 // ──────────────────────────────────────────────
    // Keep your existing methods with small improvements
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public OrderResponseDTO acceptOrder(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.SCHEDULED) {
            throw new BusinessValidationException("Only PENDING or SCHEDULED orders can be accepted");
        }
        order.setStatus(Order.OrderStatus.ACCEPTED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "ACCEPTED");

        notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Accepted",
                "Your order #" + order.getId() + " has been accepted by the vendor",
                null
        );
        return mapToResponse(order);
    }

    private Order getOrderAndCheckOwnership(String orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (!order.getMerchant().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized for this order");
        }
        return order;
    }

    private void addStatusHistory(Order order, String status) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);
    }

    
    private OrderResponseDTO mapToResponse(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getRealProductId(),
                        item.getQuantity(),
                        item.getPricePerUnit(),
                        item.getTotalPrice(),
                        item.getDisplayName()
                ))
                .toList();

        // Map address (single entity – safe)
        OrderAddressDTO addressDto = null;
        if (order.getDeliveryAddress() != null) {
            CustomerAddress addr = order.getDeliveryAddress();
            addressDto = new OrderAddressDTO(
                    addr.getId(),
                    addr.getSocietyName(),
                    addr.getHouseNo(),
                    addr.getBuildingName(),
                    addr.getLandmark(),
                    addr.getArea(),
                    addr.getPincode(),
                    addr.getState(),
                    addr.getLatitude(),
                    addr.getLongitude(),
                    addr.getAddressType() != null ? addr.getAddressType().name() : null,
                    addr.isDefaultAddress()
            );
        }

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer() != null ? order.getCustomer().getId() : null,      // ← Only ID
                order.getMerchant() != null ? order.getMerchant().getId() : null,      // ← Only ID
                order.getSubtotal(),
                order.getDiscountAmount(),
                order.getDeliveryCharge(),
                order.getTotalPrice(),
                order.getPromoCode(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items,
                order.getCancelReason(),
                order.getCancelledBy(),
                order.getCancelledAt(),
                order.getVendorPickupToken(),
                order.getUserDeliveryToken(),
                order.getDeliveryPartner() != null ? order.getDeliveryPartner().getId() : null,
                order.getPickupTime(),
                addressDto
        );
    }
    
    
  

    @Override
    @Transactional
    public OrderResponseDTO rejectOrder(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        order.setStatus(Order.OrderStatus.REJECTED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "REJECTED");
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Rejected",
                "Sorry, your Order #" + order.getId() + " was rejected",
                Map.of("type", "order_rejected")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO markAsDelivered(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        if (order.getStatus() != Order.OrderStatus.ACCEPTED) {
            throw new BusinessValidationException("Order must be accepted first");
        }
        order.setStatus(Order.OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "DELIVERED");
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Delivered",
                "Your Order #" + order.getId() + " has been delivered!",
                Map.of("type", "order_delivered")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO confirmCodCollected(String orderId, User vendor) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        if (!"COD".equals(order.getPaymentMode())) {
            throw new IllegalStateException("Only COD orders can be confirmed");
        }
        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new IllegalStateException("Order must be delivered first");
        }
        order.setPaymentStatus("SUCCESS");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "COD Collected",
                "Cash collected for Order #" + order.getId(),
                Map.of("type", "cod_collected")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrderByCustomer(String orderId, Customer customer, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new ForbiddenException("You can only cancel your own orders");
        }
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalStateException("Cancellation reason required");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelReason(reason);
        order.setCancelledBy("CUSTOMER");
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "CANCELLED");
        notificationService.sendNotification(
                order.getMerchant().getId(),
                "Order Cancelled",
                "Order #" + order.getId() + " cancelled by customer: " + reason,
                Map.of("type", "order_cancelled")
        );
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrderByVendor(String orderId, User vendor, String reason) {
        Order order = getOrderAndCheckOwnership(orderId, vendor);
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BusinessValidationException("Order already cancelled");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessValidationException("Cancellation reason required");
        }
        if (order.getStatus() == Order.OrderStatus.PENDING) {
            try {
                order.setVendorCancelReason(VendorCancelReason.valueOf(reason));
            } catch (BusinessValidationException e) {
                throw new BusinessValidationException("Invalid reason for pending order");
            }
        } else if (order.getStatus() == Order.OrderStatus.ACCEPTED) {
            try {
                order.setVendorAcceptCancelReason(VendorAcceptThenCancelReason.valueOf(reason));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid reason for accepted order");
            }
        } else {
            throw new IllegalStateException("Order cannot be cancelled in current state");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledBy("VENDOR");
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);
        addStatusHistory(order, "CANCELLED");
        notificationService.sendNotification(
                order.getCustomer().getId(),
                "Order Cancelled by Vendor",
                "Order #" + order.getId() + " was cancelled by vendor: " + reason,
                Map.of("type", "order_cancelled")
        );
        return mapToResponse(order);
    }



//    private OrderResponseDTO mapToResponse(Order order) {
//        List<OrderItemResponseDTO> items = order.getItems().stream()
//                .map(item -> new OrderItemResponseDTO(
//                        item.getId(),
//                        item.getProduct().getId(),
//                        item.getQuantity(),
//                        item.getPricePerUnit(),
//                        item.getTotalPrice()
//                ))
//                .toList();
//        return new OrderResponseDTO(
//                order.getId(),
//                order.getCustomer().getId(),
//                order.getMerchant().getId(),
//                order.getSubtotal(),
//                order.getDiscountAmount(),
//                order.getDeliveryCharge(),
//                order.getTotalPrice(),
//                order.getPromoCode(),
//                order.getStatus().name(),
//                order.getCreatedAt(),
//                order.getUpdatedAt(),
//                items,
//                order.getCancelReason(),
//                order.getCancelledBy(),
//                order.getCancelledAt()
//        );
//    }

    
    
    
    
    private boolean isOfferApplicable(Offer offer, Customer customer, User merchant, BigDecimal subtotal) {
        if (subtotal.compareTo(offer.getMinOrderAmount()) < 0) return false;
        if (!offer.isActive()) return false;
        LocalDate today = LocalDate.now();
        if (today.isBefore(offer.getStartDate()) || today.isAfter(offer.getEndDate())) return false;
        if (offer.getCustomerGroup() == Offer.CustomerGroup.NEW_CUSTOMER) {
            return !orderRepository.existsByCustomerAndMerchant(customer, merchant);
        }
        if (offer.getCustomerGroup() == Offer.CustomerGroup.INACTIVE_30_DAYS) {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            return !orderRepository.existsByCustomerAndMerchantAndCreatedAtAfter(customer, merchant, thirtyDaysAgo);
        }
        return true;
    }

    private BigDecimal calculateDiscount(Offer offer, BigDecimal subtotal) {
        BigDecimal discount = BigDecimal.ZERO;
        if (offer.getDiscountType() == Offer.DiscountType.PERCENTAGE && offer.getDiscountPercent() != null) {
            discount = subtotal.multiply(BigDecimal.valueOf(offer.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (offer.getDiscountType() == Offer.DiscountType.FLAT && offer.getFlatDiscount() != null) {
            discount = offer.getFlatDiscount();
        }
        if (offer.getMaxDiscountAmount() != null && discount.compareTo(offer.getMaxDiscountAmount()) > 0) {
            discount = offer.getMaxDiscountAmount();
        }
        return discount;
    }

    private BigDecimal getProductPrice(Product product) {
        String type = determineProductType(product.getCategory());
        return switch (type) {
            case "VEGETABLE" -> vegetableDetailRepository.findByProductId(product.getId())
                    .map(VegetableDetail::getMinPrice)
                    .orElseThrow(() -> new BusinessValidationException("Price missing"));
            case "DAIRY" -> dairyDetailRepository.findByProductId(product.getId())
                    .map(DairyDetail::getMinPrice)
                    .orElseThrow(() -> new BusinessValidationException("Price missing"));
            case "MEAT" -> meatDetailRepository.findByProductId(product.getId())
                    .map(MeatDetail::getMinPrice)
                    .orElseThrow(() -> new BusinessValidationException("Price missing"));
            case "WOMEN"      -> womenProductRepository.findById(product.getId())   // ← This line
            .map(WomenProduct::getMinPrice)
            
           
                    .orElseThrow(() -> new BusinessValidationException("Price missing"));
            default -> throw new IllegalArgumentException("Unsupported category");
        };
    }

    private String determineProductType(Category category) {
        if (category == null) return "GENERAL";
        Category root = category;
        while (root.getParent() != null) root = root.getParent();
        String name = root.getName().toLowerCase();
        if (name.contains("vegetable") || name.contains("fruit") || name.contains("fresh")) return "VEGETABLE";
        if (name.contains("dairy") || name.contains("milk")) return "DAIRY";
        if (name.contains("meat") || name.contains("chicken") || name.contains("fish") || name.contains("seafood")) return "MEAT";
        if (name.contains("women") || name.contains("handicraft")) return "WOMEN";
        return "GENERAL";
    }

    @Override
    public List<OrderResponseDTO> getCustomerOrders(Customer customer) {
        return orderRepository.findByCustomer(customer)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getVendorPendingOrders(User vendor) {
        // Show BOTH PENDING (immediate) and SCHEDULED (future) orders
        List<Order> pendingOrders = orderRepository.findByMerchantAndStatus(vendor, Order.OrderStatus.PENDING);
        List<Order> scheduledOrders = orderRepository.findByMerchantAndStatus(vendor, Order.OrderStatus.SCHEDULED);

        List<Order> combined = new ArrayList<>();
        combined.addAll(pendingOrders);
        combined.addAll(scheduledOrders);

        // Optional: Sort by createdAt descending (newest first)
        combined.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        return combined.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getAllVendorOrders(User vendor) {
        return orderRepository.findByMerchantOrderByCreatedAtDesc(vendor)
                .stream()
                .map(this::mapToResponse)
                .toList();
    

    }
    
    @Override
    @Transactional
    public OrderResponseDTO generateVendorPickupQR(String orderId, User vendor) {
        // 1. Verify order exists and belongs to this vendor
        Order order = getOrderAndCheckOwnership(orderId, vendor);

        // 2. Must be in READY_FOR_PICKUP status
        if (order.getStatus() != Order.OrderStatus.READY_FOR_PICKUP) {
            throw new BusinessValidationException("Order must be in READY_FOR_PICKUP status to generate pickup QR");
        }

        // 3. Only allowed for DELIVERY_PARTNER mode
        if (order.getDeliveryMode() != DeliveryMode.DELIVERY_PARTNER) {
            throw new BusinessValidationException("Pickup QR can only be generated for DELIVERY_PARTNER delivery mode");
        }

        // 4. Generate new token if:
        //    - No token exists yet, or
        //    - Existing token has expired
        boolean shouldGenerateNewToken = 
            order.getVendorPickupToken() == null ||
            LocalDateTime.now().isAfter(order.getVendorPickupTokenExpiry());

        if (shouldGenerateNewToken) {
            String newToken = UUID.randomUUID().toString();
            order.setVendorPickupToken(newToken);
            order.setVendorPickupTokenExpiry(LocalDateTime.now().plusMinutes(30)); // 30 minutes expiry
            
            // Optional: you can log this regeneration
            System.out.println("Generated new pickup token for order #" + orderId + 
                              " | Vendor ID: " + vendor.getId());
            
            orderRepository.save(order);
            addStatusHistory(order, "PICKUP_QR_GENERATED");
        }

        // 5. Notify vendor (optional - useful for tracking)
        notificationService.sendNotification(
            vendor.getId(),
            "Pickup QR Generated",
            "Pickup QR for Order #" + order.getId() + " is ready. Show to delivery partner.",
            null
        );

        // 6. Return updated order (frontend will use vendorPickupToken to generate QR image)
        return mapToResponse(order);
    }
	
    
 // In OrderServiceImpl.java (already good)
    public List<OrderResponseDTO> getVendorScheduledOrders(User vendor) {
        return orderRepository.findByMerchantAndStatus(vendor, Order.OrderStatus.SCHEDULED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    @Override
    public OrderStatusResponseDTO getOrderStatus(String orderId) {
        Order order = getOrderById(orderId);
        return new OrderStatusResponseDTO(
            order.getId(),
            order.getStatus().name(),
            order.getDeliveredAt(),
            order.getDeliveryPartner() != null ? order.getDeliveryPartner().getName() : null,
            order.getDeliveryMode().name()
        );
    }

    
    
 // ← New helper method for real-time notifications
    private void notifyOrderUpdate(Order order, String customMessage, String type) {
        OrderStatusUpdateDTO update = new OrderStatusUpdateDTO(
            order.getId(),
            order.getStatus().name(),
            customMessage != null ? customMessage : "Order status updated to " + order.getStatus().name(),
            LocalDateTime.now(),
            type != null ? type : "STATUS_UPDATE",
            order.getDeliveryPartner() != null ? order.getDeliveryPartner().getName() : null
        );

        // Send private update to customer
        if (order.getCustomer() != null) {
            orderWebSocketService.sendToUser(order.getCustomer().getId().toString(), update);
        }

        // Send private update to vendor
        if (order.getMerchant() != null) {
            orderWebSocketService.sendToUser(order.getMerchant().getId().toString(), update);
        }

        // Send private update to delivery partner (if assigned)
        if (order.getDeliveryPartner() != null) {
            orderWebSocketService.sendToUser(order.getDeliveryPartner().getId().toString(), update);
        }

        // Broadcast to all delivery partners when new pickup is ready
        if (Order.OrderStatus.READY_FOR_PICKUP.equals(order.getStatus())) {
            orderWebSocketService.broadcastNewPickup(update);
        }
    }
   
    
  
    
}