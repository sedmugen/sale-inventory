package io.saadmughal.saleinventorybackend.controller;

import io.saadmughal.saleinventorybackend.dto.request.CustomerRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.CustomerResponseDTO;
import io.saadmughal.saleinventorybackend.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * GET /api/customers - Get all customers
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * GET /api/customers/{id} - Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    /**
     * GET /api/customers/email/{email} - Get customer by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerResponseDTO> getCustomerByEmail(@PathVariable String email) {
        CustomerResponseDTO customer = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(customer);
    }

    /**
     * POST /api/customers - Create new customer
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO customer = customerService.createCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    /**
     * PUT /api/customers/{id} - Update customer
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO customer = customerService.updateCustomer(id, requestDTO);
        return ResponseEntity.ok(customer);
    }

    /**
     * DELETE /api/customers/{id} - Delete customer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/customers/active - Get active (non-blocked) customers
     */
    @GetMapping("/active")
    public ResponseEntity<List<CustomerResponseDTO>> getActiveCustomers() {
        List<CustomerResponseDTO> customers = customerService.getActiveCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * GET /api/customers/search?name=xxx - Search customers by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomers(
            @RequestParam(required = false) String name) {
        List<CustomerResponseDTO> customers = customerService.searchCustomersByName(name);
        return ResponseEntity.ok(customers);
    }

    /**
     * PATCH /api/customers/{id}/toggle-block - Block/unblock customer
     */
    @PatchMapping("/{id}/toggle-block")
    public ResponseEntity<CustomerResponseDTO> toggleBlockStatus(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.toggleBlockStatus(id);
        return ResponseEntity.ok(customer);
    }
}