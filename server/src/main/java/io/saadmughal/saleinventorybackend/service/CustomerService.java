package io.saadmughal.saleinventorybackend.service;

import io.saadmughal.saleinventorybackend.dto.request.CustomerRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.CustomerResponseDTO;
import io.saadmughal.saleinventorybackend.entity.Customer;
import io.saadmughal.saleinventorybackend.exception.CustomerNotFoundException;
import io.saadmughal.saleinventorybackend.exception.DuplicateResourceException;
import io.saadmughal.saleinventorybackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Get all customers
     */
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get customer by ID
     */
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return convertToResponseDTO(customer);
    }

    /**
     * Get customer by email
     */
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException(email));
        return convertToResponseDTO(customer);
    }

    /**
     * Create new customer
     */
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        // Check for duplicate email
        if (customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw DuplicateResourceException.customerEmail(requestDTO.getEmail());
        }

        Customer customer = Customer.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .phone(requestDTO.getPhone())
                .address(requestDTO.getAddress())
                .blocked(requestDTO.getBlocked())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponseDTO(savedCustomer);
    }

    /**
     * Update customer
     */
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        // Check for duplicate email (if email is being changed)
        if (customerRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw DuplicateResourceException.customerEmail(requestDTO.getEmail());
        }

        customer.setName(requestDTO.getName());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhone(requestDTO.getPhone());
        customer.setAddress(requestDTO.getAddress());
        customer.setBlocked(requestDTO.getBlocked());

        Customer updatedCustomer = customerRepository.save(customer);
        return convertToResponseDTO(updatedCustomer);
    }

    /**
     * Delete customer
     */
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }

    /**
     * Get active (non-blocked) customers
     */
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getActiveCustomers() {
        return customerRepository.findByBlockedFalse().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search customers by name
     */
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> searchCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Block/unblock customer
     */
    @Transactional
    public CustomerResponseDTO toggleBlockStatus(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setBlocked(!customer.getBlocked());
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToResponseDTO(updatedCustomer);
    }

    // ========== DTO Conversion Methods ==========

    private CustomerResponseDTO convertToResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .blocked(customer.getBlocked())
                .build();
    }
}