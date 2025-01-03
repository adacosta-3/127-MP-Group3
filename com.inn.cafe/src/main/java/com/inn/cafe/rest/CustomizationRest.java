package com.inn.cafe.rest;

import com.inn.cafe.wrapper.CustomizationWrapper;
import com.inn.cafe.POJO.CustomizationOption;
import com.inn.cafe.service.CustomizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/customization")
public interface CustomizationRest {

    @GetMapping("/getByItemId/{itemId}")
    public ResponseEntity<List<CustomizationWrapper>> getCustomizationsByItemId(@PathVariable Integer itemId);

    @GetMapping("/getAll")
    public ResponseEntity<List<CustomizationWrapper>> getAllCustomizations();

    @GetMapping("/getOptionsByCustomizationId/{customizationId}")
    public ResponseEntity<List<CustomizationOption>> getOptionsByCustomizationId(@PathVariable Integer customizationId);

    @PostMapping("/addCustomizationOption")
    public ResponseEntity<CustomizationOption> addCustomizationOption(@RequestBody CustomizationOption option);
}
