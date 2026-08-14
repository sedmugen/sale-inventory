package io.saadmughal.saleinventorybackend.controller;

import io.saadmughal.saleinventorybackend.dto.request.ProductRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.LowStockProductDTO;
import io.saadmughal.saleinventorybackend.dto.response.ProductListDTO;
import io.saadmughal.saleinventorybackend.dto.response.ProductResponseDTO;
import io.saadmughal.saleinventorybackend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products - Get all products
     */
    @GetMapping
    public ResponseEntity<List<ProductListDTO>> getAllProducts() {
        List<ProductListDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id} - Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * GET /api/products/code/{code} - Get product by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ProductResponseDTO> getProductByCode(@PathVariable String code) {
        ProductResponseDTO product = productService.getProductByCode(code);
        return ResponseEntity.ok(product);
    }

    /**
     * POST /api/products - Create new product
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO product = productService.createProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * PUT /api/products/{id} - Update product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO product = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(product);
    }

    /**
     * DELETE /api/products/{id} - Delete product (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/products/active - Get active products only
     */
    @GetMapping("/active")
    public ResponseEntity<List<ProductListDTO>> getActiveProducts() {
        List<ProductListDTO> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/search?name=xxx - Search products by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductListDTO>> searchProducts(
            @RequestParam(required = false) String name) {
        List<ProductListDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/low-stock?limit=5 - Get low stock products (BONUS FEATURE)
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<LowStockProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<LowStockProductDTO> products = productService.getLowStockProducts(limit);
        return ResponseEntity.ok(products);
    }
}
