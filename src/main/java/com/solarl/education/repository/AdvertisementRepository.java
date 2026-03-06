package com.solarl.education.repository;

import com.solarl.education.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query("select a from Advertisement a join fetch a.client")
    List<Advertisement> findAll();
}
