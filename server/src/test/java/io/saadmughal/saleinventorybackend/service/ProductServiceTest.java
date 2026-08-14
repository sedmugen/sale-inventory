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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDetailRepository productDetailRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDetail testProductDetail;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .code("P-1001")
                .name("Wireless Mouse")
                .unitPrice(new BigDecimal("25.99"))
                .currentStock(50)
                .active(true)
                .build();

        testProductDetail = ProductDetail.builder()
                .id(1L)
                .product(testProduct)
                .brand("Logitech")
                .category("Electronics")
                .description("Ergonomic wireless mouse")
                .minStockLevel(10)
                .taxRate(17.0)
                .build();
    }

    @Test
    @DisplayName("Should return all products in lightweight list DTO format")
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(testProduct));
        when(productDetailRepository.findByProductId(1L)).thenReturn(Optional.of(testProductDetail));

        List<ProductListDTO> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("P-1001");
        assertThat(result.get(0).getBrand()).isEqualTo("Logitech");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product by ID when product exists")
    void shouldReturnProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productDetailRepository.findByProductId(1L)).thenReturn(Optional.of(testProductDetail));

        ProductResponseDTO result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Wireless Mouse");
        assertThat(result.getProductDetail()).isNotNull();
        assertThat(result.getProductDetail().getBrand()).isEqualTo("Logitech");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product ID does not exist")
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with ID: 99");
    }

    @Test
    @DisplayName("Should create product with detail successfully")
    void shouldCreateProductSuccessfully() {
        ProductRequestDTO.ProductDetailDTO detailDTO = ProductRequestDTO.ProductDetailDTO.builder()
                .brand("Logitech")
                .category("Electronics")
                .description("Ergonomic wireless mouse")
                .minStockLevel(10)
                .taxRate(17.0)
                .build();

        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .code("P-1001")
                .name("Wireless Mouse")
                .unitPrice(new BigDecimal("25.99"))
                .currentStock(50)
                .active(true)
                .productDetail(detailDTO)
                .build();

        when(productRepository.existsByCode("P-1001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productDetailRepository.save(any(ProductDetail.class))).thenReturn(testProductDetail);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productDetailRepository.findByProductId(1L)).thenReturn(Optional.of(testProductDetail));

        ProductResponseDTO result = productService.createProduct(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("P-1001");
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productDetailRepository, times(1)).save(any(ProductDetail.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when product code already exists")
    void shouldThrowExceptionOnDuplicateProductCode() {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .code("P-1001")
                .name("Wireless Mouse")
                .unitPrice(new BigDecimal("25.99"))
                .currentStock(50)
                .active(true)
                .build();

        when(productRepository.existsByCode("P-1001")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Product with code 'P-1001' already exists");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should soft-delete product by setting active to false")
    void shouldSoftDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.deleteProduct(1L);

        assertThat(testProduct.getActive()).isFalse();
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("Should return low stock products with deficit calculations")
    void shouldReturnLowStockProducts() {
        testProduct.setCurrentStock(3);
        when(productRepository.findLowStockProducts()).thenReturn(List.of(testProduct));
        when(productDetailRepository.findByProductId(1L)).thenReturn(Optional.of(testProductDetail));

        List<LowStockProductDTO> lowStock = productService.getLowStockProducts(5);

        assertThat(lowStock).hasSize(1);
        assertThat(lowStock.get(0).getCurrentStock()).isEqualTo(3);
        assertThat(lowStock.get(0).getStockDeficit()).isEqualTo(7); // 10 - 3 = 7
    }
}
