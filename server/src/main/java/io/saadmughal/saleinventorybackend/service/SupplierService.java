package io.saadmughal.saleinventorybackend.service;

import io.saadmughal.saleinventorybackend.dto.request.SupplierRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.SupplierResponseDTO;
import io.saadmughal.saleinventorybackend.entity.Supplier;
import io.saadmughal.saleinventorybackend.exception.DuplicateResourceException;
import io.saadmughal.saleinventorybackend.exception.SupplierNotFoundException;
import io.saadmughal.saleinventorybackend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    /**
     * Get all suppliers
     */
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get supplier by ID
     */
    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));
        return convertToResponseDTO(supplier);
    }

    /**
     * Get supplier by email
     */
    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierByEmail(String email) {
        Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new SupplierNotFoundException(email));
        return convertToResponseDTO(supplier);
    }

    /**
     * Create new supplier
     */
    @Transactional
    public SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO) {
        // Check for duplicate email
        if (supplierRepository.existsByEmail(requestDTO.getEmail())) {
            throw DuplicateResourceException.supplierEmail(requestDTO.getEmail());
        }

        Supplier supplier = Supplier.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .phone(requestDTO.getPhone())
                .companyName(requestDTO.getCompanyName())
                .address(requestDTO.getAddress())
                .active(requestDTO.getActive())
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);
        return convertToResponseDTO(savedSupplier);
    }

    /**
     * Update supplier
     */
    @Transactional
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO requestDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));

        // Check for duplicate email (if email is being changed)
        if (supplierRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw DuplicateResourceException.supplierEmail(requestDTO.getEmail());
        }

        supplier.setName(requestDTO.getName());
        supplier.setEmail(requestDTO.getEmail());
        supplier.setPhone(requestDTO.getPhone());
        supplier.setCompanyName(requestDTO.getCompanyName());
        supplier.setAddress(requestDTO.getAddress());
        supplier.setActive(requestDTO.getActive());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return convertToResponseDTO(updatedSupplier);
    }

    /**
     * Delete supplier
     */
    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new SupplierNotFoundException(id);
        }
        supplierRepository.deleteById(id);
    }

    /**
     * Get active suppliers only
     */
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search suppliers by name
     */
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Activate/deactivate supplier
     */
    @Transactional
    public SupplierResponseDTO toggleActiveStatus(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));

        supplier.setActive(!supplier.getActive());
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return convertToResponseDTO(updatedSupplier);
    }

    // ========== DTO Conversion Methods ==========

    private SupplierResponseDTO convertToResponseDTO(Supplier supplier) {
        return SupplierResponseDTO.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .companyName(supplier.getCompanyName())
                .address(supplier.getAddress())
                .active(supplier.getActive())
                .build();
    }
}