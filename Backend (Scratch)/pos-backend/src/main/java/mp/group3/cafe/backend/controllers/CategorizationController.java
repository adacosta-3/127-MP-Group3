package mp.group3.cafe.backend.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CategorizationDTO;
import mp.group3.cafe.backend.service.CategorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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

    // Uploading a JSON file - Initial file-handling
    @PostMapping("/upload-json")
    public ResponseEntity<?> uploadCategories(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Uploaded file: " + file.getOriginalFilename());

            String fileContent = new String(file.getBytes());
            System.out.println("File content: " + fileContent);

            ObjectMapper objectMapper = new ObjectMapper();
            List<CategorizationDTO> categorizationDTOs = objectMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<List<CategorizationDTO>>() {}
            );

            List<CategorizationDTO> createdCategories = categorizationService.createCategories(categorizationDTOs);
            return new ResponseEntity<>(createdCategories, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON file format.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An unexpected error occurred.");
        }
    }

    // Uploading a CSV file
    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCategoriesFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getContentType().equals("text/csv")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File must be a CSV.");
            }

            List<CategorizationDTO> categorizationDTOs = categorizationService.parseCSVToCategories(file);
            List<CategorizationDTO> createdCategories = categorizationService.createCategories(categorizationDTOs);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategories);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid CSV file format.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
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

    @GetMapping("/item-type/{itemType}")
    public ResponseEntity<List<CategorizationDTO>> getCategoriesByItemType(@PathVariable String itemType) {
        List<CategorizationDTO> categories = categorizationService.getCategoriesByItemType(itemType);
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }
}


