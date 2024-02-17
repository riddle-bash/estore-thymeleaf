package com.riddlebash.estore.services;

import org.springframework.data.repository.CrudRepository;

import com.riddlebash.estore.models.ProductEntity;

public interface ProductRepository extends CrudRepository<ProductEntity, Integer>{

}
