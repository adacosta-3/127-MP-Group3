package mp.group3.cafe.backend.controllers;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CategorizationDTO;
import mp.group3.cafe.backend.service.CategorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorizationController {

    private final CategorizationService categorizationService;

    @GetMapping
    public ResponseEntity<List<CategorizationDTO>> getAllCategories() {
        List<CategorizationDTO> categories = categorizationService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorizationDTO> getCategoryById(@PathVariable Integer id) {
        Optional<CategorizationDTO> categoryOpt = categorizationService.getCategoryById(id);
        return categoryOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategorizationDTO> createCategory(@RequestBody CategorizationDTO categorizationDTO) {
        try {
            CategorizationDTO createdCategory = categorizationService.createCategory(categorizationDTO);
            return ResponseEntity.ok(createdCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorizationDTO> updateCategory(@PathVariable Integer id, @RequestBody CategorizationDTO categorizationDTO) {
        try {
            CategorizationDTO updatedCategory = categorizationService.updateCategory(id, categorizationDTO);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categorizationService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
