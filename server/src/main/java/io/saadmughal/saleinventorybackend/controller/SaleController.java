package io.saadmughal.saleinventorybackend.controller;

import io.saadmughal.saleinventorybackend.dto.request.SaleRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.SaleResponseDTO;
import io.saadmughal.saleinventorybackend.entity.SaleStatus;
import io.saadmughal.saleinventorybackend.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    /**
     * GET /api/sales - Get all sales
     */
    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        List<SaleResponseDTO> sales = saleService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    /**
     * GET /api/sales/{id} - Get sale by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        SaleResponseDTO sale = saleService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }

    /**
     * POST /api/sales - Create new sale
     *
     * Business rules enforced:
     * - Product must exist and be active
     * - Product must have sufficient stock
     * - Customer must exist and not be blocked
     * - Stock is automatically decreased
     */
    @PostMapping
    public ResponseEntity<SaleResponseDTO> createSale(
            @Valid @RequestBody SaleRequestDTO requestDTO) {
        SaleResponseDTO sale = saleService.createSale(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(sale);
    }

    /**
     * GET /api/sales/status/{status} - Get sales by status
     * Status can be: CONFIRMED or CANCELLED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByStatus(
            @PathVariable SaleStatus status) {
        List<SaleResponseDTO> sales = saleService.getSalesByStatus(status);
        return ResponseEntity.ok(sales);
    }

    /**
     * GET /api/sales/product/{productId} - Get sales for a specific product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByProductId(
            @PathVariable Long productId) {
        List<SaleResponseDTO> sales = saleService.getSalesByProductId(productId);
        return ResponseEntity.ok(sales);
    }
}