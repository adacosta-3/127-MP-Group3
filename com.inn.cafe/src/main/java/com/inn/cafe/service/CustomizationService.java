package com.inn.cafe.service;

import com.inn.cafe.dao.CustomizationDao;
import com.inn.cafe.dao.CustomizationOptionDao;
import com.inn.cafe.wrapper.CustomizationWrapper;
import com.inn.cafe.POJO.CustomizationOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomizationService {

    @Autowired
    private CustomizationDao customizationDao;

    @Autowired
    private CustomizationOptionDao customizationOptionDao;

    // Get customizations by item ID
    public List<CustomizationWrapper> getCustomizationsByItemId(Integer itemId) {
        return customizationDao.findByItemId(itemId);
    }

    // Get all customizations
    public List<CustomizationWrapper> getAllCustomizations() {
        return customizationDao.findAllCustomizations();
    }

    // Get customization options by customization ID
    public List<CustomizationOption> getOptionsByCustomizationId(Integer customizationId) {
        return customizationOptionDao.findByCustomizationId(customizationId);
    }

    // Create a new customization option
    public CustomizationOption createCustomizationOption(CustomizationOption option) {
        return customizationOptionDao.save(option);
    }
}
