// src/main/java/com/agrowmart/controller/CustomerAddressController.java
package com.agrowmart.controller;

import com.agrowmart.dto.auth.customer.AddressRequest;
import com.agrowmart.dto.auth.customer.AddressResponse;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.customer.CustomerAddress;
import com.agrowmart.service.customer.CustomerAddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/addresses")
public class CustomerAddressController {

    private final CustomerAddressService addressService;

    public CustomerAddressController(CustomerAddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> add(  // ← Change return type
            @Valid @RequestBody AddressRequest req,
            @AuthenticationPrincipal Customer customer) {
        CustomerAddress saved = addressService.addAddress(customer, req);
        return ResponseEntity.ok(mapToResponse(saved));  // ← Use DTO
    }
    private AddressResponse mapToResponse(CustomerAddress addr) {
        return new AddressResponse(
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
    @GetMapping
    public ResponseEntity<List<AddressResponse>> list(@AuthenticationPrincipal Customer customer) {
        return ResponseEntity.ok(addressService.getAllAddresses(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerAddress> update(@PathVariable Long id,
                                                  @Valid @RequestBody AddressRequest req,
                                                  @AuthenticationPrincipal Customer customer) {
        return ResponseEntity.ok(addressService.updateAddress(customer, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal Customer customer) {
        addressService.deleteAddress(customer, id);
        return ResponseEntity.ok().build();
    }

    
    @PostMapping("/{id}/default")
    public ResponseEntity<List<AddressResponse>> setDefault(
            @PathVariable Long id,
            @AuthenticationPrincipal Customer customer) {
        addressService.setDefaultAddress(customer, id);
        
        // Return all addresses with updated default status
        List<AddressResponse> updatedAddresses = addressService.getAllAddresses(customer);
        
        return ResponseEntity.ok(updatedAddresses);
    }
}