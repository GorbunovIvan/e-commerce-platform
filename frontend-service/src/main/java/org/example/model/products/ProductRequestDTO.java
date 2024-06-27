package org.example.model.products;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class ProductRequestDTO {

    private String name;
    private String description;
    private String category;
    private Long userId;
    private LocalDateTime createdAt;

    public static ProductRequestDTO fromProduct(Product product) {

        var productRequestDTO = new ProductRequestDTO();

        productRequestDTO.setName(product.getName());
        productRequestDTO.setDescription(product.getDescription());
        productRequestDTO.setCategory(product.getCategoryName());
        productRequestDTO.setUserId(product.getUserId());
        productRequestDTO.setCreatedAt(product.getCreatedAt());

        return productRequestDTO;
    }
}
