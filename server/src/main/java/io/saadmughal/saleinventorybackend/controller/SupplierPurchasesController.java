package io.saadmughal.saleinventorybackend.controller;

import io.saadmughal.saleinventorybackend.dto.response.PurchaseResponseDTO;
import io.saadmughal.saleinventorybackend.entity.PurchaseStatus;
import io.saadmughal.saleinventorybackend.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Separate controller for supplier-specific purchase endpoints
 */
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierPurchasesController {

    private final PurchaseService purchaseService;

    /**
     * GET /api/suppliers/{id}/purchases - Get all purchases from a supplier
     */
    @GetMapping("/{id}/purchases")
    public ResponseEntity<List<PurchaseResponseDTO>> getSupplierPurchases(@PathVariable Long id) {
        List<PurchaseResponseDTO> purchases = purchaseService.getPurchasesBySupplierId(id);
        return ResponseEntity.ok(purchases);
    }

    /**
     * GET /api/suppliers/{id}/purchases/filter?status=RECEIVED - Filter supplier purchases
     */
    @GetMapping("/{id}/purchases/filter")
    public ResponseEntity<List<PurchaseResponseDTO>> getSupplierPurchasesByStatus(
            @PathVariable Long id,
            @RequestParam(required = false) PurchaseStatus status) {

        if (status != null) {
            List<PurchaseResponseDTO> purchases =
                    purchaseService.getPurchasesBySupplierIdAndStatus(id, status);
            return ResponseEntity.ok(purchases);
        } else {
            List<PurchaseResponseDTO> purchases = purchaseService.getPurchasesBySupplierId(id);
            return ResponseEntity.ok(purchases);
        }
    }
}
