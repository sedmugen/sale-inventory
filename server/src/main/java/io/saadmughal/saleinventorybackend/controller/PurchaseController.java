package io.saadmughal.saleinventorybackend.controller;

import io.saadmughal.saleinventorybackend.dto.request.PurchaseRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.PurchaseResponseDTO;
import io.saadmughal.saleinventorybackend.entity.PurchaseStatus;
import io.saadmughal.saleinventorybackend.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * GET /api/purchases - Get all purchases
     */
    @GetMapping
    public ResponseEntity<List<PurchaseResponseDTO>> getAllPurchases() {
        List<PurchaseResponseDTO> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    /**
     * GET /api/purchases/{id} - Get purchase by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponseDTO> getPurchaseById(@PathVariable Long id) {
        PurchaseResponseDTO purchase = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchase);
    }

    /**
     * POST /api/purchases - Create new purchase
     *
     * Business rules enforced:
     * - Product must exist
     * - Supplier must exist and be active
     * - Stock is automatically increased
     */
    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> createPurchase(
            @Valid @RequestBody PurchaseRequestDTO requestDTO) {
        PurchaseResponseDTO purchase = purchaseService.createPurchase(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchase);
    }

    /**
     * GET /api/purchases/status/{status} - Get purchases by status
     * Status can be: RECEIVED or CANCELLED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseResponseDTO>> getPurchasesByStatus(
            @PathVariable PurchaseStatus status) {
        List<PurchaseResponseDTO> purchases = purchaseService.getPurchasesByStatus(status);
        return ResponseEntity.ok(purchases);
    }

    /**
     * GET /api/purchases/product/{productId} - Get purchases for a specific product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PurchaseResponseDTO>> getPurchasesByProductId(
            @PathVariable Long productId) {
        List<PurchaseResponseDTO> purchases = purchaseService.getPurchasesByProductId(productId);
        return ResponseEntity.ok(purchases);
    }
}