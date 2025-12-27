package io.saadmughal.saleinventorybackend.service;

import io.saadmughal.saleinventorybackend.dto.request.ProductRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.LowStockProductDTO;
import io.saadmughal.saleinventorybackend.dto.response.ProductListDTO;
import io.saadmughal.saleinventorybackend.dto.response.ProductResponseDTO;
import io.saadmughal.saleinventorybackend.entity.Product;
import io.saadmughal.saleinventorybackend.entity.ProductDetail;
import io.saadmughal.saleinventorybackend.exception.DuplicateResourceException;
import io.saadmughal.saleinventorybackend.exception.ProductNotFoundException;
import io.saadmughal.saleinventorybackend.repository.ProductDetailRepository;
import io.saadmughal.saleinventorybackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    /**
     * Get all products (lightweight list)
     */
    @Transactional(readOnly = true)
    public List<ProductListDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return convertToResponseDTO(product);
    }

    /**
     * Get product by code
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(code));
        return convertToResponseDTO(product);
    }

    /**
     * Create new product with details
     */
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        // Check for duplicate code
        if (productRepository.existsByCode(requestDTO.getCode())) {
            throw DuplicateResourceException.productCode(requestDTO.getCode());
        }

        // Create product entity
        Product product = Product.builder()
                .code(requestDTO.getCode())
                .name(requestDTO.getName())
                .unitPrice(requestDTO.getUnitPrice())
                .currentStock(requestDTO.getCurrentStock())
                .active(requestDTO.getActive())
                .build();

        // Save product first
        Product savedProduct = productRepository.save(product);

        // Create and save product detail if provided
        if (requestDTO.getProductDetail() != null) {
            ProductRequestDTO.ProductDetailDTO detailDTO = requestDTO.getProductDetail();

            ProductDetail productDetail = ProductDetail.builder()
                    .product(savedProduct)
                    .brand(detailDTO.getBrand())
                    .category(detailDTO.getCategory())
                    .description(detailDTO.getDescription())
                    .minStockLevel(detailDTO.getMinStockLevel())
                    .taxRate(detailDTO.getTaxRate())
                    .build();

            productDetailRepository.save(productDetail);
        }

        return getProductById(savedProduct.getId());
    }

    /**
     * Update product
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Check for duplicate code (if code is being changed)
        if (!product.getCode().equals(requestDTO.getCode()) &&
                productRepository.existsByCode(requestDTO.getCode())) {
            throw DuplicateResourceException.productCode(requestDTO.getCode());
        }

        // Update product fields
        product.setCode(requestDTO.getCode());
        product.setName(requestDTO.getName());
        product.setUnitPrice(requestDTO.getUnitPrice());
        product.setCurrentStock(requestDTO.getCurrentStock());
        product.setActive(requestDTO.getActive());

        productRepository.save(product);

        // Update or create product detail
        if (requestDTO.getProductDetail() != null) {
            ProductRequestDTO.ProductDetailDTO detailDTO = requestDTO.getProductDetail();

            ProductDetail productDetail = productDetailRepository.findByProductId(id)
                    .orElse(ProductDetail.builder().product(product).build());

            productDetail.setBrand(detailDTO.getBrand());
            productDetail.setCategory(detailDTO.getCategory());
            productDetail.setDescription(detailDTO.getDescription());
            productDetail.setMinStockLevel(detailDTO.getMinStockLevel());
            productDetail.setTaxRate(detailDTO.getTaxRate());

            productDetailRepository.save(productDetail);
        }

        return getProductById(id);
    }

    /**
     * Delete product (soft delete by setting active = false)
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Soft delete - just mark as inactive
        product.setActive(false);
        productRepository.save(product);
    }

    /**
     * Get low stock products (bonus feature)
     */
    @Transactional(readOnly = true)
    public List<LowStockProductDTO> getLowStockProducts(Integer limit) {
        List<Product> lowStockProducts = productRepository.findLowStockProducts();

        return lowStockProducts.stream()
                .limit(limit != null ? limit : 5)
                .map(this::convertToLowStockDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search products by name
     */
    @Transactional(readOnly = true)
    public List<ProductListDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active products only
     */
    @Transactional(readOnly = true)
    public List<ProductListDTO> getActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }

    // ========== DTO Conversion Methods ==========

    private ProductResponseDTO convertToResponseDTO(Product product) {
        ProductResponseDTO.ProductResponseDTOBuilder builder = ProductResponseDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .unitPrice(product.getUnitPrice())
                .currentStock(product.getCurrentStock())
                .active(product.getActive());

        // Add product detail if exists
        productDetailRepository.findByProductId(product.getId()).ifPresent(detail -> {
            ProductResponseDTO.ProductDetailResponseDTO detailDTO =
                    ProductResponseDTO.ProductDetailResponseDTO.builder()
                            .id(detail.getId())
                            .brand(detail.getBrand())
                            .category(detail.getCategory())
                            .description(detail.getDescription())
                            .minStockLevel(detail.getMinStockLevel())
                            .taxRate(detail.getTaxRate())
                            .build();
            builder.productDetail(detailDTO);
        });

        return builder.build();
    }

    private ProductListDTO convertToListDTO(Product product) {
        ProductListDTO.ProductListDTOBuilder builder = ProductListDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .unitPrice(product.getUnitPrice())
                .currentStock(product.getCurrentStock())
                .active(product.getActive());

        // Add category and brand if detail exists
        productDetailRepository.findByProductId(product.getId()).ifPresent(detail -> {
            builder.category(detail.getCategory());
            builder.brand(detail.getBrand());
        });

        return builder.build();
    }

    private LowStockProductDTO convertToLowStockDTO(Product product) {
        LowStockProductDTO.LowStockProductDTOBuilder builder = LowStockProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .unitPrice(product.getUnitPrice())
                .currentStock(product.getCurrentStock())
                .active(product.getActive());

        // Add detail info and calculate deficit
        productDetailRepository.findByProductId(product.getId()).ifPresent(detail -> {
            builder.category(detail.getCategory());
            builder.minStockLevel(detail.getMinStockLevel());
            builder.stockDeficit(detail.getMinStockLevel() - product.getCurrentStock());
        });

        return builder.build();
    }
}