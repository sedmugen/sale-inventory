package io.saadmughal.saleinventorybackend.service;

import io.saadmughal.saleinventorybackend.dto.request.SupplierRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.SupplierResponseDTO;
import io.saadmughal.saleinventorybackend.entity.Supplier;
import io.saadmughal.saleinventorybackend.exception.DuplicateResourceException;
import io.saadmughal.saleinventorybackend.exception.SupplierNotFoundException;
import io.saadmughal.saleinventorybackend.repository.SupplierRepository;
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
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        testSupplier = Supplier.builder()
                .id(1L)
                .name("Alice Corp")
                .email("contact@alicecorp.com")
                .phone("+92-42-1234567")
                .companyName("Alice Electronics")
                .address("Industrial Estate, Lahore")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should return all suppliers")
    void shouldReturnAllSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(testSupplier));

        List<SupplierResponseDTO> result = supplierService.getAllSuppliers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Alice Corp");
        verify(supplierRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return supplier by ID when found")
    void shouldReturnSupplierById() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

        SupplierResponseDTO result = supplierService.getSupplierById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("contact@alicecorp.com");
    }

    @Test
    @DisplayName("Should throw SupplierNotFoundException when ID does not exist")
    void shouldThrowExceptionWhenSupplierNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.getSupplierById(99L))
                .isInstanceOf(SupplierNotFoundException.class)
                .hasMessageContaining("Supplier not found with ID: 99");
    }

    @Test
    @DisplayName("Should create new supplier when email is unique")
    void shouldCreateSupplierSuccessfully() {
        SupplierRequestDTO requestDTO = SupplierRequestDTO.builder()
                .name("Alice Corp")
                .email("contact@alicecorp.com")
                .phone("+92-42-1234567")
                .companyName("Alice Electronics")
                .address("Industrial Estate, Lahore")
                .active(true)
                .build();

        when(supplierRepository.existsByEmail("contact@alicecorp.com")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

        SupplierResponseDTO result = supplierService.createSupplier(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Alice Corp");
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException on duplicate supplier email")
    void shouldThrowExceptionOnDuplicateEmail() {
        SupplierRequestDTO requestDTO = SupplierRequestDTO.builder()
                .name("Alice Corp")
                .email("contact@alicecorp.com")
                .build();

        when(supplierRepository.existsByEmail("contact@alicecorp.com")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.createSupplier(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Supplier with email 'contact@alicecorp.com' already exists");

        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Should toggle supplier active status")
    void shouldToggleSupplierActiveStatus() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

        SupplierResponseDTO result = supplierService.toggleActiveStatus(1L);

        assertThat(testSupplier.getActive()).isFalse();
        verify(supplierRepository, times(1)).save(testSupplier);
    }
}
