package com.example.testEstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.testEstore.models.ProductEntity;
import com.example.testEstore.services.ProductRepository;

@Controller
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductRepository repository;
	
	@GetMapping({"", "/"})
	public String listProducts(Model model) {
		Iterable<ProductEntity> products = repository.findAll();
		model.addAttribute("products", products);
		return "products/index";
	}
}
