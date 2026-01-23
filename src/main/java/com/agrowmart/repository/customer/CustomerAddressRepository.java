package com.agrowmart.repository.customer;

//CustomerAddressRepository.java


import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.customer.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
 List<CustomerAddress> findByCustomer(Customer customer);
 List<CustomerAddress> findByCustomerId(Long customerId);
}