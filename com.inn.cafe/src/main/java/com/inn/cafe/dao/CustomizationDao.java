package com.inn.cafe.dao;

import com.inn.cafe.POJO.Customization;
import com.inn.cafe.wrapper.CustomizationWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomizationDao extends JpaRepository<Customization, Integer> {

    // Find customizations by Item ID
    @Query("SELECT new com.inn.cafe.wrapper.CustomizationWrapper(c.customizationId, c.name, c.itemId) " +
           "FROM Customization c WHERE c.itemId = :itemId")
    List<CustomizationWrapper> findByItemId(@Param("itemId") Integer itemId);

    // Get all customizations
    @Query("SELECT new com.inn.cafe.wrapper.CustomizationWrapper(c.customizationId, c.name, c.itemId) " +
           "FROM Customization c")
    List<CustomizationWrapper> findAllCustomizations();
}
