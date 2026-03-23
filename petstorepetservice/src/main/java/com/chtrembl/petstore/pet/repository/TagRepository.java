package com.chtrembl.petstore.pet.repository;

import com.chtrembl.petstore.pet.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long> {
}
