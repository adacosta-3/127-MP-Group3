package com.inn.cafe.dao;

import com.inn.cafe.POJO.CustomizationOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomizationOptionDao extends JpaRepository<CustomizationOption, Integer> {

    // Find customization options by Customization ID
    @Query("SELECT co FROM CustomizationOption co WHERE co.customizationId = :customizationId")
    List<CustomizationOption> findByCustomizationId(@Param("customizationId") Integer customizationId);
}
