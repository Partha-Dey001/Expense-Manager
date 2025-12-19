package com.partha.expensemanager.repository;

import com.partha.expensemanager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    // SELECT * FROM tbl_categories WHERE profile_id = ?
    List<CategoryEntity> findByProfileId(Long profileId);

    // SELECT * FROM tbl_categories WHERE profile_id = ?1 and id = ?2
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    // SELECT * FROM tbl_categories WHERE type = ?1 and profile_id = ?2
    List<CategoryEntity>findByTypeAndProfileId(String type, Long profileId);

    Boolean existsByNameAndProfileId(String name, Long profileId);
}
