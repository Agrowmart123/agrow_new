package com.agrowmart.service.customer;

import com.agrowmart.dto.auth.customer.*;
import com.agrowmart.entity.*;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct;
import com.agrowmart.entity.customer.Cart;
import com.agrowmart.entity.customer.CartItem;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.exception.ResourceNotFoundException;

import com.agrowmart.repository.*;
import com.agrowmart.repository.customer.CartRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final WomenProductRepository womenProductRepository;
    private final AgriProductRepository agriProductRepository;

    private final VegetableDetailRepository vegRepo;
    private final DairyDetailRepository dairyRepo;
    private final MeatDetailRepository meatRepo;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            WomenProductRepository womenProductRepository,
            AgriProductRepository agriProductRepository,
            VegetableDetailRepository vegRepo,
            DairyDetailRepository dairyRepo,
            MeatDetailRepository meatRepo
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.womenProductRepository = womenProductRepository;
        this.agriProductRepository = agriProductRepository;
        this.vegRepo = vegRepo;
        this.dairyRepo = dairyRepo;
        this.meatRepo = meatRepo;
    }

    // ===================== ADD TO CART =====================
    public CartSummaryDTO addToCart(Customer customer, AddToCartRequest request) {

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> createNewCart(customer));

        CartItem existingItem = cart.getItems().stream()
                .filter(i ->
                        i.getProductId().equals(request.productId()) &&
                        i.getProductType().equalsIgnoreCase(request.productType())
                )
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.quantity());
        } else {
            CartItem newItem = createCartItem(request);
            cart.addItem(newItem);
        }

        cartRepository.save(cart);
        return toSummaryDTO(cart);
    }

    // ===================== UPDATE QUANTITY =====================
    public CartSummaryDTO updateQuantity(Customer customer, UpdateCartItemRequest request) {

        Cart cart = getCustomerCart(customer);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(request.itemId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (request.quantity() <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(request.quantity());
        }

        cartRepository.save(cart);
        return toSummaryDTO(cart);
    }

    // ===================== REMOVE ITEM =====================
    public CartSummaryDTO removeItem(Customer customer, Long itemId) {

        Cart cart = getCustomerCart(customer);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.removeItem(item);
        cartRepository.save(cart);

        return toSummaryDTO(cart);
    }

    // ===================== GET CART =====================
    public CartSummaryDTO getCart(Customer customer) {
        return toSummaryDTO(getCustomerCart(customer));
    }

    // ===================== CLEAR CART =====================
    public void clearCart(Customer customer) {
        Cart cart = getCustomerCart(customer);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // ===================== HELPERS =====================

    private Cart createNewCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cart;
    }

    private Cart getCustomerCart(Customer customer) {
        return cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer"));
    }

    private CartItem createCartItem(AddToCartRequest request) {

        CartItem item = new CartItem();
        item.setProductId(request.productId());
        item.setProductType(request.productType().toUpperCase());
        item.setQuantity(request.quantity());

        item.setPriceAtAdd(fetchPrice(request.productId(), request.productType()));
        item.setNameSnapshot(fetchName(request.productId(), request.productType()));
        item.setImageSnapshot(fetchImage(request.productId(), request.productType()));

        return item;
    }

    // ===================== PRICE SNAPSHOT =====================
    private BigDecimal fetchPrice(Long productId, String productType) {

        return switch (productType.toUpperCase()) {

            case "REGULAR" -> {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                String type = determineProductType(product.getCategory());

                yield switch (type) {
                    case "VEGETABLE" -> vegRepo.findByProductId(productId)
                            .map(VegetableDetail::getMinPrice)
                            .orElseThrow(() -> new ResourceNotFoundException("Vegetable price not found"));

                    case "DAIRY" -> dairyRepo.findByProductId(productId)
                            .map(DairyDetail::getMinPrice)
                            .orElseThrow(() -> new ResourceNotFoundException("Dairy price not found"));

                    case "MEAT" -> meatRepo.findByProductId(productId)
                            .map(MeatDetail::getMinPrice)
                            .orElseThrow(() -> new ResourceNotFoundException("Meat price not found"));

                    default -> throw new IllegalArgumentException("Unsupported product category");
                };
            }

            case "WOMEN" -> womenProductRepository.findById(productId)
                    .map(WomenProduct::getMinPrice)
                    .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

            case "AGRI" -> agriProductRepository.findById(productId)
                    .map(BaseAgriProduct::getAgriprice)
                    .orElseThrow(() -> new ResourceNotFoundException("Agri product not found"));

            default -> throw new IllegalArgumentException("Invalid product type: " + productType);
        };
    }

    private String fetchName(Long id, String type) {
        return switch (type.toUpperCase()) {
            case "REGULAR" -> productRepository.findById(id)
                    .map(Product::getProductName)
                    .orElse("Unknown");
            case "WOMEN" -> womenProductRepository.findById(id)
                    .map(WomenProduct::getName)
                    .orElse("Unknown");
            case "AGRI" -> agriProductRepository.findById(id)
                    .map(BaseAgriProduct::getAgriproductName)
                    .orElse("Unknown");
            default -> "Unknown";
        };
    }

    private String fetchImage(Long id, String type) {
        return switch (type.toUpperCase()) {
            case "REGULAR" -> productRepository.findById(id)
                    .map(p -> p.getImagePaths() != null ? p.getImagePaths().split(",")[0] : null)
                    .orElse(null);
            case "WOMEN" -> womenProductRepository.findById(id)
                    .map(w -> w.getImageUrls() != null ? w.getImageUrls().split(",")[0] : null)
                    .orElse(null);
            case "AGRI" -> agriProductRepository.findById(id)
                    .map(a -> a.getAgriImageUrls() != null && !a.getAgriImageUrls().isEmpty()
                            ? a.getAgriImageUrls().get(0)
                            : null)
                    .orElse(null);
            default -> null;
        };
    }

    // ===================== CATEGORY → TYPE =====================
    private String determineProductType(Category category) {
        if (category == null) return "GENERAL";

        Category current = category;
        while (current != null) {
            String slug = current.getSlug();
            if ("vegetable-root".equals(slug)) return "VEGETABLE";
            if ("dairy-root".equals(slug)) return "DAIRY";
            if ("seafoodmeat-root".equals(slug)) return "MEAT";
            current = current.getParent();
        }
        return "GENERAL";
    }

    // ===================== DTO MAPPER =====================
    private CartSummaryDTO toSummaryDTO(Cart cart) {

        List<CartItemDTO> items = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        item.getId(),
                        item.getProductId(),
                        item.getProductType(),
                        item.getNameSnapshot(),
                        item.getImageSnapshot(),
                        item.getPriceAtAdd(),
                        item.getQuantity(),
                        item.getPriceAtAdd().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        BigDecimal subtotal = cart.calculateSubtotal();

        return new CartSummaryDTO(
                cart.getId(),
                items,
                cart.getTotalQuantity(),
                subtotal,
                subtotal,
                "INR"
        );
    }
}
