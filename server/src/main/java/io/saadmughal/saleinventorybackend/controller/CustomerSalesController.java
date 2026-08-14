package io.saadmughal.saleinventorybackend.controller;

import io.saadmughal.saleinventorybackend.dto.response.SaleResponseDTO;
import io.saadmughal.saleinventorybackend.entity.SaleStatus;
import io.saadmughal.saleinventorybackend.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Separate controller for customer-specific sales endpoints
 * Supports the Customer Sales Dashboard feature
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerSalesController {

    private final SaleService saleService;

    /**
     * GET /api/customers/{id}/sales - Get all sales for a customer
     * Used by Customer Sales Dashboard
     */
    @GetMapping("/{id}/sales")
    public ResponseEntity<List<SaleResponseDTO>> getCustomerSales(@PathVariable Long id) {
        List<SaleResponseDTO> sales = saleService.getSalesByCustomerId(id);
        return ResponseEntity.ok(sales);
    }

    /**
     * GET /api/customers/{id}/sales?status=CONFIRMED - Filter customer sales by status
     * Supports dashboard filtering feature
     */
    @GetMapping("/{id}/sales/filter")
    public ResponseEntity<List<SaleResponseDTO>> getCustomerSalesByStatus(
            @PathVariable Long id,
            @RequestParam(required = false) SaleStatus status) {

        if (status != null) {
            List<SaleResponseDTO> sales = saleService.getSalesByCustomerIdAndStatus(id, status);
            return ResponseEntity.ok(sales);
        } else {
            List<SaleResponseDTO> sales = saleService.getSalesByCustomerId(id);
            return ResponseEntity.ok(sales);
        }
    }
}