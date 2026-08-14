package io.saadmughal.saleinventorybackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "product_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String category;

    @Column(length = 1000)
    private String description;

    @Min(value = 0, message = "Minimum stock level must be non-negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer minStockLevel = 0;

    @Min(value = 0, message = "Tax rate must be non-negative")
    @Max(value = 100, message = "Tax rate cannot exceed 100")
    @Builder.Default
    private Double taxRate = 0.0;
}