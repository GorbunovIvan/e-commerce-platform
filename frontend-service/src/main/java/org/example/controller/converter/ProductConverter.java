package org.example.controller.converter;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Product;
import org.example.repository.products.ProductRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductConverter extends BasicConverter implements Converter<String, Product> {

    private final ProductRepository productRepository;

    @Override
    public Product convert(@Nullable String source) {

        if (source == null || source.isEmpty()) {
            return null;
        }

        log.info("Converting product '{}' to object by id", source);

        var pattern = Product.patternToReadIdFromUniqueView();
        var id = readIdFromStringByPattern(pattern, source);
        if (id == null) {
            id = stringToLong(source);
        }
        if (id != null) {
            var productFound = productRepository.getById(id);
            if (productFound != null) {
                log.info("Product with id '{}' exits - {}", id, productFound);
                return productFound;
            } else {
                log.error("Product with id '{}' not found", id);
                return null;
            }
        }

        log.info("Converting product '{}' to object by name", source);
        var productsFound = productRepository.getAll(source, null, null);
        if (productsFound.isEmpty()) {
            var product = new Product();
            product.setName(source);
            return product;
        }

        if (productsFound.size() == 1) {
            var productFound = productsFound.getFirst();
            log.info("Product with name '{}' exits - {}", source, productFound);
            return productFound;
        } else {
            log.error("There are more than one products with name '{}': {}", source, productsFound);
            return null;
        }
    }
}
