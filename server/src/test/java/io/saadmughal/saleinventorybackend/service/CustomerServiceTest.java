package io.saadmughal.saleinventorybackend.service;

import io.saadmughal.saleinventorybackend.dto.request.CustomerRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.CustomerResponseDTO;
import io.saadmughal.saleinventorybackend.entity.Customer;
import io.saadmughal.saleinventorybackend.exception.CustomerNotFoundException;
import io.saadmughal.saleinventorybackend.exception.DuplicateResourceException;
import io.saadmughal.saleinventorybackend.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phone("+92-321-9876543")
                .address("456 Park Avenue, Karachi")
                .blocked(false)
                .build();
    }

    @Test
    @DisplayName("Should return all customers")
    void shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(testCustomer));

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jane Smith");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return customer by ID when exists")
    void shouldReturnCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerResponseDTO result = customerService.getCustomerById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when ID not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with ID: 99");
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        CustomerRequestDTO requestDTO = CustomerRequestDTO.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phone("+92-321-9876543")
                .address("456 Park Avenue, Karachi")
                .blocked(false)
                .build();

        when(customerRepository.existsByEmail("jane.smith@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponseDTO result = customerService.createCustomer(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Jane Smith");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException on existing email")
    void shouldThrowExceptionOnDuplicateEmail() {
        CustomerRequestDTO requestDTO = CustomerRequestDTO.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();

        when(customerRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Customer with email 'jane.smith@example.com' already exists");

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should toggle customer blocked status")
    void shouldToggleCustomerBlockStatus() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponseDTO result = customerService.toggleBlockStatus(1L);

        assertThat(testCustomer.getBlocked()).isTrue();
        verify(customerRepository, times(1)).save(testCustomer);
    }
}
