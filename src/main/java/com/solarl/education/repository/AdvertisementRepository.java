package com.solarl.education.repository;

import com.solarl.education.entity.Advertisement;
import com.solarl.education.enums.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    Optional<Advertisement> findByName(String name);

    @Query("select a from Advertisement a join fetch a.client")
    List<Advertisement> findAll();

    List<Advertisement> findByIdIn(List<Long> ids);

    List<Advertisement> findByCategoryAndCostGreaterThanEqual(CategoryEnum category, Integer cost);
}
