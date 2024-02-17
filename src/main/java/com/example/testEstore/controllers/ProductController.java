package com.example.testEstore.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.testEstore.models.ProductDto;
import com.example.testEstore.models.ProductEntity;
import com.example.testEstore.services.ProductRepository;

import jakarta.validation.Valid;

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
	
	@GetMapping("/create")
	public String createProduct(Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute(productDto);
		return "products/createProduct";
	}
	
	@PostMapping("/create")
	public String saveProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
		
		if (productDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
		}
		
		if (result.hasErrors()) {
			return "products/createProduct";
		}
		
		MultipartFile imageFile = productDto.getImageFile();
		Date date = new Date();
		String storageFileName = date.getTime() + "_" + imageFile.getOriginalFilename();
		
		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);
			
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			try (InputStream inputStream = imageFile.getInputStream()) {
				Files.copy(inputStream,  Paths.get(uploadDir + storageFileName), 
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		ProductEntity product = new ProductEntity();
		product.setName(productDto.getName());
		product.setBrand(productDto.getBrand());
		product.setCategory(productDto.getCategory());
		product.setPrice(productDto.getPrice());
		product.setDescription(productDto.getDescription());
		product.setTimeCreated(date);
		product.setImageFileName(storageFileName);
		
		repository.save(product);
		
		return "redirect:/products";
	}
	
	@GetMapping("/edit")
	public String editProduct(Model model, @Valid @RequestParam int id) {
		
		try {
			ProductEntity product = repository.findById(id).get();
			model.addAttribute("product", product);
			
			ProductDto productDto = new ProductDto();
			productDto.setName(product.getName());
			productDto.setBrand(product.getBrand());
			productDto.setCategory(product.getCategory());
			productDto.setPrice(product.getPrice());
			productDto.setDescription(product.getDescription());
			
			model.addAttribute("productDto", productDto);
		} catch (Exception e) {
			return "redirect:/products";
		}
		
		return "products/editProduct";
	}
	
	@PostMapping("/edit")
	public String saveEditProduct(
			Model model,
			@Valid @RequestParam int id, 
			@Valid @ModelAttribute ProductDto productDto,
			BindingResult result) {
		
		try {
			
			ProductEntity product = repository.findById(id).get();
			model.addAttribute("product", product);
			
			if (result.hasErrors()) {
				return "products/editProduct";
			}
			
			if (!productDto.getImageFile().isEmpty()) {
				String uploadDir = "public/images/";
				Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
				
				try {
					Files.delete(oldImagePath);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
				MultipartFile imageFile = productDto.getImageFile();
				Date date = new Date();
				String storageFileName = date.getTime() + "_" + imageFile.getOriginalFilename();
				
				try (InputStream inputStream = imageFile.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}
				
				product.setImageFileName(storageFileName);
			}
			
			product.setName(productDto.getName());
			product.setBrand(productDto.getBrand());
			product.setCategory(productDto.getCategory());
			product.setPrice(productDto.getPrice());
			product.setDescription(productDto.getDescription());
			
			repository.save(product);
			
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		return "redirect:/products";
	}
}
