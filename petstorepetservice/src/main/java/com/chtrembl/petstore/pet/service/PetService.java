package com.chtrembl.petstore.pet.service;

import com.chtrembl.petstore.pet.model.Category;
import com.chtrembl.petstore.pet.model.Pet;
import com.chtrembl.petstore.pet.model.Pet.Status;
import com.chtrembl.petstore.pet.model.Tag;
import com.chtrembl.petstore.pet.repository.CategoryRepository;
import com.chtrembl.petstore.pet.repository.PetRepository;
import com.chtrembl.petstore.pet.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public Pet savePet(Pet pet) {

        if(pet.getCategory() != null) {
            Category category = pet.getCategory();
            if(category.getId() == null){
                Category savedCategory = categoryRepository.save(category);
                pet.setCategory(savedCategory);
            } else{
                Category existingCategory = categoryRepository.findById(category.getId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                pet.setCategory(existingCategory);
            }
        }

        if(pet.getTags() != null) {
            List<Tag> tags = new ArrayList<>();
            for(Tag tag : pet.getTags()){
                if(tag.getId() == null){
                    Tag savedTag =  tagRepository.save(tag);
                    tags.add(savedTag);
                } else {
                    Tag existingTag =  tagRepository.findById(tag.getId())
                            .orElseThrow(() -> new RuntimeException("Tag not found"));
                    tags.add(existingTag);
                }
            }

            pet.setTags(tags);
        }

        return petRepository.save(pet);
    }

    public List<Pet> findPetsByStatus(List<Status> status) {
        log.info("Finding pets with status: {}", status);

        return  petRepository.findByStatusIn(status);
    }

    public Optional<Pet> findPetById(Long petId) {
        log.info("Finding pet with id: {}", petId);

        return  petRepository.findById(petId);
    }

    public List<Pet> getAllPets() {
        log.info("Getting all pets");
        return petRepository.findAll();
    }

    public int getPetCount() {
        return petRepository.findAll().size();
    }
}