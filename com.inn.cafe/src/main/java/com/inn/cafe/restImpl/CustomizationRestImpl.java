package com.inn.cafe.restImpl;

import com.inn.cafe.wrapper.CustomizationWrapper;
import com.inn.cafe.POJO.CustomizationOption;
import com.inn.cafe.rest.CustomizationRest;
import com.inn.cafe.service.CustomizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomizationRestImpl implements CustomizationRest {

    @Autowired
    private CustomizationService customizationService;

    @Override
    public ResponseEntity<List<CustomizationWrapper>> getCustomizationsByItemId(Integer itemId) {
        List<CustomizationWrapper> customizations = customizationService.getCustomizationsByItemId(itemId);
        return ResponseEntity.ok(customizations);
    }

    @Override
    public ResponseEntity<List<CustomizationWrapper>> getAllCustomizations() {
        List<CustomizationWrapper> customizations = customizationService.getAllCustomizations();
        return ResponseEntity.ok(customizations);
    }

    @Override
    public ResponseEntity<List<CustomizationOption>> getOptionsByCustomizationId(Integer customizationId) {
        List<CustomizationOption> options = customizationService.getOptionsByCustomizationId(customizationId);
        return ResponseEntity.ok(options);
    }

    @Override
    public ResponseEntity<CustomizationOption> addCustomizationOption(CustomizationOption option) {
        CustomizationOption createdOption = customizationService.createCustomizationOption(option);
        return ResponseEntity.ok(createdOption);
    }
}
