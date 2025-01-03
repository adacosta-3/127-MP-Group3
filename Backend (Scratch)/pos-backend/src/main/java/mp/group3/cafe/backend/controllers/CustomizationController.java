package mp.group3.cafe.backend.controllers;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CustomizationDTO;
import mp.group3.cafe.backend.service.CustomizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customizations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomizationController {

    private final CustomizationService customizationService;

    @GetMapping
    public ResponseEntity<List<CustomizationDTO>> getAllCustomizations() {
        List<CustomizationDTO> customizations = customizationService.getAllCustomizations();
        return ResponseEntity.ok(customizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomizationDTO> getCustomizationById(@PathVariable Integer id) {
        Optional<CustomizationDTO> customizationOpt = customizationService.getCustomizationById(id);
        return customizationOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/item/{itemCode}")
    public ResponseEntity<List<CustomizationDTO>> getCustomizationsByItemCode(@PathVariable String itemCode) {
        List<CustomizationDTO> customizations = customizationService.getCustomizationsByItemCode(itemCode);
        return ResponseEntity.ok(customizations);
    }

    @PostMapping
    public ResponseEntity<CustomizationDTO> createCustomization(@RequestBody CustomizationDTO customizationDTO) {
        try {
            CustomizationDTO createdCustomization = customizationService.createCustomization(customizationDTO);
            return ResponseEntity.ok(createdCustomization);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomizationDTO> updateCustomization(@PathVariable Integer id,
            @RequestBody CustomizationDTO customizationDTO) {
        try {
            CustomizationDTO updatedCustomization = customizationService.updateCustomization(id, customizationDTO);
            return ResponseEntity.ok(updatedCustomization);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomization(@PathVariable Integer id) {
        customizationService.deleteCustomization(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/item/{itemCode}")
    public ResponseEntity<List<CustomizationDTO>> updateCustomizationsByItemCode(
            @PathVariable String itemCode, @RequestBody List<CustomizationDTO> customizations) {
        try {
            List<CustomizationDTO> updatedCustomizations = customizationService.updateCustomizationsByItemCode(itemCode,
                    customizations);
            return ResponseEntity.ok(updatedCustomizations);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
