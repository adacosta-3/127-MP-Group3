package mp.group3.cafe.backend.controllers;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CustomizationOptionsDTO;
import mp.group3.cafe.backend.service.CustomizationOptionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/customization-options")
@RequiredArgsConstructor
public class CustomizationOptionsController {

    private final CustomizationOptionsService optionsService;

    @GetMapping
    public ResponseEntity<List<CustomizationOptionsDTO>> getAllOptions() {
        List<CustomizationOptionsDTO> options = optionsService.getAllOptions();
        return ResponseEntity.ok(options);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomizationOptionsDTO> getOptionById(@PathVariable Integer id) {
        Optional<CustomizationOptionsDTO> optionOpt = optionsService.getOptionById(id);
        return optionOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customization/{customizationId}")
    public ResponseEntity<List<CustomizationOptionsDTO>> getOptionsByCustomizationId(@PathVariable Integer customizationId) {
        List<CustomizationOptionsDTO> options = optionsService.getOptionsByCustomizationId(customizationId);
        return ResponseEntity.ok(options);
    }

    @PostMapping
    public ResponseEntity<CustomizationOptionsDTO> createOption(@RequestBody CustomizationOptionsDTO optionsDTO) {
        try {
            CustomizationOptionsDTO createdOption = optionsService.createOption(optionsDTO);
            return ResponseEntity.ok(createdOption);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomizationOptionsDTO> updateOption(@PathVariable Integer id, @RequestBody CustomizationOptionsDTO optionsDTO) {
        try {
            CustomizationOptionsDTO updatedOption = optionsService.updateOption(id, optionsDTO);
            return ResponseEntity.ok(updatedOption);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable Integer id) {
        optionsService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }
}
