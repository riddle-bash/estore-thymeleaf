package com.example.testEstore.services;

import org.springframework.data.repository.CrudRepository;

import com.example.testEstore.models.ProductEntity;

public interface ProductRepository extends CrudRepository<ProductEntity, Integer>{

}
