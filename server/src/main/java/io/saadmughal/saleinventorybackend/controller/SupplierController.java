package io.saadmughal.saleinventorybackend.controller;

import io.saadmughal.saleinventorybackend.dto.request.SupplierRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.SupplierResponseDTO;
import io.saadmughal.saleinventorybackend.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * GET /api/suppliers - Get all suppliers
     */
    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAllSuppliers() {
        List<SupplierResponseDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    /**
     * GET /api/suppliers/{id} - Get supplier by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Long id) {
        SupplierResponseDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    /**
     * GET /api/suppliers/email/{email} - Get supplier by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<SupplierResponseDTO> getSupplierByEmail(@PathVariable String email) {
        SupplierResponseDTO supplier = supplierService.getSupplierByEmail(email);
        return ResponseEntity.ok(supplier);
    }

    /**
     * POST /api/suppliers - Create new supplier
     */
    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(
            @Valid @RequestBody SupplierRequestDTO requestDTO) {
        SupplierResponseDTO supplier = supplierService.createSupplier(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplier);
    }

    /**
     * PUT /api/suppliers/{id} - Update supplier
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestDTO requestDTO) {
        SupplierResponseDTO supplier = supplierService.updateSupplier(id, requestDTO);
        return ResponseEntity.ok(supplier);
    }

    /**
     * DELETE /api/suppliers/{id} - Delete supplier
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/suppliers/active - Get active suppliers only
     */
    @GetMapping("/active")
    public ResponseEntity<List<SupplierResponseDTO>> getActiveSuppliers() {
        List<SupplierResponseDTO> suppliers = supplierService.getActiveSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    /**
     * GET /api/suppliers/search?name=xxx - Search suppliers by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<SupplierResponseDTO>> searchSuppliers(
            @RequestParam(required = false) String name) {
        List<SupplierResponseDTO> suppliers = supplierService.searchSuppliersByName(name);
        return ResponseEntity.ok(suppliers);
    }

    /**
     * PATCH /api/suppliers/{id}/toggle-active - Activate/deactivate supplier
     */
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<SupplierResponseDTO> toggleActiveStatus(@PathVariable Long id) {
        SupplierResponseDTO supplier = supplierService.toggleActiveStatus(id);
        return ResponseEntity.ok(supplier);
    }
}