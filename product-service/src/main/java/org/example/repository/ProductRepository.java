package org.example.repository;

import org.example.model.Product;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = { "category", "user" })
    @NonNull
    List<Product> findAll();

    @Override
    @EntityGraph(attributePaths = { "category", "user" })
    @NonNull
    <S extends Product> List<S> findAll(@NonNull Example<S> example);

    @EntityGraph(attributePaths = { "category", "user" })
    List<Product> findAllByIdIn(Collection<Long> ids);
}
