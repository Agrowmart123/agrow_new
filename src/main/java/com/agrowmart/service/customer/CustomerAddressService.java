// src/main/java/com/agrowmart/service/customer/CustomerAddressService.java

package com.agrowmart.service.customer;

import com.agrowmart.dto.auth.customer.AddressRequest;
import com.agrowmart.dto.auth.customer.AddressResponse;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.customer.CustomerAddress;
import com.agrowmart.entity.customer.CustomerAddress.AddressType;
import com.agrowmart.repository.customer.CustomerAddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerAddressService {

    private final CustomerAddressRepository addressRepository;

    public CustomerAddressService(CustomerAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public CustomerAddress addAddress(Customer customer, AddressRequest req) {
        // Unset other defaults if this one is default
        if (Boolean.TRUE.equals(req.isDefault())) {
            addressRepository.findByCustomer(customer)
                    .forEach(addr -> {
                        addr.setDefaultAddress(false);
                        addressRepository.save(addr);
                    });
        }

        // Manual object creation (NO Builder)
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
        address.setAddressType(req.addressType() != null ?
                AddressType.valueOf(req.addressType().toUpperCase()) : AddressType.HOME);
        address.setDefaultAddress(Boolean.TRUE.equals(req.isDefault()));

        return addressRepository.save(address);
    }

    public List<AddressResponse> getAllAddresses(Customer customer) {
        return addressRepository.findByCustomer(customer).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerAddress updateAddress(Customer customer, Long addressId, AddressRequest req) {
        CustomerAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (!address.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Not your address");
        }

        if (Boolean.TRUE.equals(req.isDefault())) {
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
            address.setAddressType(AddressType.valueOf(req.addressType().toUpperCase()));
        }
        address.setDefaultAddress(Boolean.TRUE.equals(req.isDefault()));

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Customer customer, Long addressId) {
        CustomerAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (!address.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Not authorized");
        }

        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(Customer customer, Long addressId) {
        CustomerAddress newDefault = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (!newDefault.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Not your address");
        }

        addressRepository.findByCustomer(customer)
                .forEach(a -> {
                    a.setDefaultAddress(false);
                    addressRepository.save(a);
                });

        newDefault.setDefaultAddress(true);
        addressRepository.save(newDefault);
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