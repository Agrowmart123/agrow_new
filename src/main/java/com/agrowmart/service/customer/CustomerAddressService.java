//// src/main/java/com/agrowmart/service/customer/CustomerAddressService.java
//
//package com.agrowmart.service.customer;
//
//import com.agrowmart.dto.auth.customer.AddressRequest;
//import com.agrowmart.dto.auth.customer.AddressResponse;
//import com.agrowmart.entity.customer.Customer;
//import com.agrowmart.entity.customer.CustomerAddress;
//import com.agrowmart.entity.customer.CustomerAddress.AddressType;
//import com.agrowmart.repository.customer.CustomerAddressRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class CustomerAddressService {
//
//    private final CustomerAddressRepository addressRepository;
//
//    public CustomerAddressService(CustomerAddressRepository addressRepository) {
//        this.addressRepository = addressRepository;
//    }
//
//    @Transactional
//    public CustomerAddress addAddress(Customer customer, AddressRequest req) {
//        // Unset other defaults if this one is default
//        if (Boolean.TRUE.equals(req.isDefault())) {
//            addressRepository.findByCustomer(customer)
//                    .forEach(addr -> {
//                        addr.setDefaultAddress(false);
//                        addressRepository.save(addr);
//                    });
//        }
//
//        // Manual object creation (NO Builder)
//        CustomerAddress address = new CustomerAddress();
//        address.setCustomer(customer);
//        address.setSocietyName(req.societyName());
//        address.setHouseNo(req.houseNo());
//        address.setBuildingName(req.buildingName());
//        address.setLandmark(req.landmark());
//        address.setArea(req.area());
//        address.setPincode(req.pincode());
//        address.setState(req.state());
//        address.setLatitude(req.latitude());
//        address.setLongitude(req.longitude());
//        address.setAddressType(req.addressType() != null ?
//                AddressType.valueOf(req.addressType().toUpperCase()) : AddressType.HOME);
//        address.setDefaultAddress(Boolean.TRUE.equals(req.isDefault()));
//
//        return addressRepository.save(address);
//    }
//
//    public List<AddressResponse> getAllAddresses(Customer customer) {
//        return addressRepository.findByCustomer(customer).stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public CustomerAddress updateAddress(Customer customer, Long addressId, AddressRequest req) {
//        CustomerAddress address = addressRepository.findById(addressId)
//                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
//
//        if (!address.getCustomer().getId().equals(customer.getId())) {
//            throw new IllegalArgumentException("Not your address");
//        }
//
//        if (Boolean.TRUE.equals(req.isDefault())) {
//            addressRepository.findByCustomer(customer)
//                    .forEach(a -> {
//                        a.setDefaultAddress(false);
//                        addressRepository.save(a);
//                    });
//        }
//
//        Optional.ofNullable(req.societyName()).ifPresent(address::setSocietyName);
//        Optional.ofNullable(req.houseNo()).ifPresent(address::setHouseNo);
//        Optional.ofNullable(req.buildingName()).ifPresent(address::setBuildingName);
//        Optional.ofNullable(req.landmark()).ifPresent(address::setLandmark);
//        Optional.ofNullable(req.area()).ifPresent(address::setArea);
//        Optional.ofNullable(req.pincode()).ifPresent(address::setPincode);
//        Optional.ofNullable(req.latitude()).ifPresent(address::setLatitude);
//        Optional.ofNullable(req.longitude()).ifPresent(address::setLongitude);
//        if (req.addressType() != null) {
//            address.setAddressType(AddressType.valueOf(req.addressType().toUpperCase()));
//        }
//        address.setDefaultAddress(Boolean.TRUE.equals(req.isDefault()));
//
//        return addressRepository.save(address);
//    }
//
//    @Transactional
//    public void deleteAddress(Customer customer, Long addressId) {
//        CustomerAddress address = addressRepository.findById(addressId)
//                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
//
//        if (!address.getCustomer().getId().equals(customer.getId())) {
//            throw new IllegalArgumentException("Not authorized");
//        }
//
//        addressRepository.delete(address);
//    }
//
//    @Transactional
//    public void setDefaultAddress(Customer customer, Long addressId) {
//        CustomerAddress newDefault = addressRepository.findById(addressId)
//                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
//
//        if (!newDefault.getCustomer().getId().equals(customer.getId())) {
//            throw new IllegalArgumentException("Not your address");
//        }
//
//        addressRepository.findByCustomer(customer)
//                .forEach(a -> {
//                    a.setDefaultAddress(false);
//                    addressRepository.save(a);
//                });
//
//        newDefault.setDefaultAddress(true);
//        addressRepository.save(newDefault);
//    }
//
//    private AddressResponse mapToResponse(CustomerAddress addr) {
//        return new AddressResponse(
//                addr.getId(),
//                addr.getSocietyName(),
//                addr.getHouseNo(),
//                addr.getBuildingName(),
//                addr.getLandmark(),
//                addr.getArea(),
//                addr.getPincode(),
//                addr.getState(),
//                addr.getLatitude(),
//                addr.getLongitude(),
//                addr.getAddressType().name(),
//                addr.isDefaultAddress()
//        );
//    }
//}
package com.agrowmart.service.customer;

import com.agrowmart.dto.auth.customer.AddressRequest;
import com.agrowmart.dto.auth.customer.AddressResponse;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.customer.CustomerAddress;
import com.agrowmart.entity.customer.CustomerAddress.AddressType;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.customer.CustomerAddressRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerAddressService {

    private static final Logger log = LoggerFactory.getLogger(CustomerAddressService.class);

    private final CustomerAddressRepository addressRepository;

    public CustomerAddressService(CustomerAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public CustomerAddress addAddress(Customer customer, AddressRequest req) {
        if (req == null) {
            log.warn("Add address attempt with null request for customer ID: {}", customer.getId());
            throw new BusinessValidationException("Address request cannot be null");
        }

        log.info("Adding new address for customer ID: {}", customer.getId());

        // Unset other defaults if this one is default
        if (Boolean.TRUE.equals(req.isDefault())) {
            log.debug("Unsetting default flags for other addresses of customer ID: {}", customer.getId());
            addressRepository.findByCustomer(customer)
                    .forEach(addr -> {
                        addr.setDefaultAddress(false);
                        addressRepository.save(addr);
                    });
        }

        CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        address.setSocietyName(req.societyName());
        address.setHouseNo(req.houseNo());
        address.setBuildingName(req.buildingName());
        address.setLandmark(req.landmark());
        address.setArea(req.area());
        address.setPincode(req.pincode());
        address.setState(req.state());
        address.setLatitude(req.latitude());
        address.setLongitude(req.longitude());

        // Safe enum parsing with validation
        address.setAddressType(parseAddressType(req.addressType()));

        address.setDefaultAddress(Boolean.TRUE.equals(req.isDefault()));

        CustomerAddress saved = addressRepository.save(address);
        log.info("Address added successfully - ID: {}, Customer ID: {}", saved.getId(), customer.getId());

        return saved;
    }

    public List<AddressResponse> getAllAddresses(Customer customer) {
        log.debug("Fetching all addresses for customer ID: {}", customer.getId());
        return addressRepository.findByCustomer(customer).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerAddress updateAddress(Customer customer, Long addressId, AddressRequest req) {
        if (addressId == null) {
            throw new BusinessValidationException("Address ID is required for update");
        }

        CustomerAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.warn("Address update failed - not found: ID {}", addressId);
                    return new ResourceNotFoundException("Address not found with ID: " + addressId);
                });

        if (!address.getCustomer().getId().equals(customer.getId())) {
            log.warn("Unauthorized address update attempt - Address ID: {}, Customer ID: {}", addressId, customer.getId());
            throw new BusinessValidationException("You are not authorized to update this address");
        }

        log.info("Updating address ID: {} for customer ID: {}", addressId, customer.getId());

        if (Boolean.TRUE.equals(req.isDefault())) {
            log.debug("Unsetting default flags for other addresses before setting new default");
            addressRepository.findByCustomer(customer)
                    .forEach(a -> {
                        a.setDefaultAddress(false);
                        addressRepository.save(a);
                    });
        }

        Optional.ofNullable(req.societyName()).ifPresent(address::setSocietyName);
        Optional.ofNullable(req.houseNo()).ifPresent(address::setHouseNo);
        Optional.ofNullable(req.buildingName()).ifPresent(address::setBuildingName);
        Optional.ofNullable(req.landmark()).ifPresent(address::setLandmark);
        Optional.ofNullable(req.area()).ifPresent(address::setArea);
        Optional.ofNullable(req.pincode()).ifPresent(address::setPincode);
        Optional.ofNullable(req.latitude()).ifPresent(address::setLatitude);
        Optional.ofNullable(req.longitude()).ifPresent(address::setLongitude);

        if (req.addressType() != null) {
            address.setAddressType(parseAddressType(req.addressType()));
        }

        address.setDefaultAddress(Boolean.TRUE.equals(req.isDefault()));

        CustomerAddress updated = addressRepository.save(address);
        log.info("Address updated successfully - ID: {}", updated.getId());

        return updated;
    }

    @Transactional
    public void deleteAddress(Customer customer, Long addressId) {
        if (addressId == null) {
            throw new BusinessValidationException("Address ID is required for deletion");
        }

        CustomerAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.warn("Delete address failed - not found: ID {}", addressId);
                    return new ResourceNotFoundException("Address not found with ID: " + addressId);
                });

        if (!address.getCustomer().getId().equals(customer.getId())) {
            log.warn("Unauthorized delete attempt - Address ID: {}, Customer ID: {}", addressId, customer.getId());
            throw new BusinessValidationException("You are not authorized to delete this address");
        }

        log.info("Deleting address ID: {} for customer ID: {}", addressId, customer.getId());
        addressRepository.delete(address);
        log.info("Address deleted successfully - ID: {}", addressId);
    }

    @Transactional
    public void setDefaultAddress(Customer customer, Long addressId) {
        if (addressId == null) {
            throw new BusinessValidationException("Address ID is required to set as default");
        }

        CustomerAddress newDefault = addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.warn("Set default failed - address not found: ID {}", addressId);
                    return new ResourceNotFoundException("Address not found with ID: " + addressId);
                });

        if (!newDefault.getCustomer().getId().equals(customer.getId())) {
            log.warn("Unauthorized set-default attempt - Address ID: {}, Customer ID: {}", addressId, customer.getId());
            throw new BusinessValidationException("You are not authorized to set this address as default");
        }

        log.info("Setting address as default - ID: {}, Customer ID: {}", addressId, customer.getId());

        addressRepository.findByCustomer(customer)
                .forEach(a -> {
                    a.setDefaultAddress(false);
                    addressRepository.save(a);
                });

        newDefault.setDefaultAddress(true);
        addressRepository.save(newDefault);

        log.info("Default address updated successfully - New default ID: {}", addressId);
    }

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────

    private AddressType parseAddressType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return AddressType.HOME;
        }

        try {
            return AddressType.valueOf(typeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid address type provided: '{}'", typeStr);
            throw new BusinessValidationException(
                    "Invalid address type: " + typeStr + ". Allowed values: HOME, WORK, OTHER");
        }
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
                addr.getAddressType().name(),
                addr.isDefaultAddress()
        );
    }
}