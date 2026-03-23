package com.chtrembl.petstore.product.service;

import com.chtrembl.petstore.product.model.Category;
import com.chtrembl.petstore.product.model.Product;
import com.chtrembl.petstore.product.model.Product.Status;
import com.chtrembl.petstore.product.model.Tag;
import com.chtrembl.petstore.product.repository.CategoryRepository;
import com.chtrembl.petstore.product.repository.ProductRepository;
import com.chtrembl.petstore.product.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public Product saveProduct(Product product) {

        if(product.getCategory() != null) {
            Category category = product.getCategory();
            if(category.getId() == null){
                Category savedCategory = categoryRepository.save(category);
                product.setCategory(savedCategory);
            } else{
                Category existingCategory = categoryRepository.findById(category.getId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                product.setCategory(existingCategory);
            }
        }

        if(product.getTags() != null) {
            List<Tag> tags = new ArrayList<>();
            for(Tag tag : product.getTags()){
                if(tag.getId() == null){
                    Tag savedTag =  tagRepository.save(tag);
                    tags.add(savedTag);
                } else {
                    Tag existingTag =  tagRepository.findById(tag.getId())
                            .orElseThrow(() -> new RuntimeException("Tag not found"));
                    tags.add(existingTag);
                }
            }

            product.setTags(tags);
        }

        return productRepository.save(product);
    }

    public List<Product> findProductsByStatus(List<Status> status) {
        log.info("Finding products with status: {}", status);

        return productRepository.findByStatusIn(status);
    }

    public Optional<Product> findProductById(Long productId) {
        log.info("Finding product with id: {}", productId);

        return productRepository.findById(productId);
    }

    public List<Product> getAllProducts() {
        log.info("Getting all products");
        return productRepository.findAll();
    }

    public int getProductCount() {
        return productRepository.findAll().size();
    }
}