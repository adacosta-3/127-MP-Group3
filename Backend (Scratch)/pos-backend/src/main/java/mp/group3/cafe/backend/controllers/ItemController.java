package mp.group3.cafe.backend.controllers;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.ItemDTO;
import mp.group3.cafe.backend.DTO.ItemSizeDTO;
import mp.group3.cafe.backend.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<ItemDTO> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Integer id) {
        Optional<ItemDTO> itemOpt = itemService.getItemById(id);
        return itemOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        try {
            ItemDTO createdItem = itemService.createItem(itemDTO);
            return ResponseEntity.ok(createdItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadItemsFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getContentType().equals("text/csv")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File must be a CSV.");
            }

            List<ItemDTO> itemDTOs = itemService.parseCSVToItems(file);
            List<ItemDTO> createdItems = itemService.createItems(itemDTOs);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdItems);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid CSV file format.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Integer id, @RequestBody ItemDTO itemDTO) {
        try {
            ItemDTO updatedItem = itemService.updateItem(id, itemDTO);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ItemDTO>> getItemsByCategoryId(@PathVariable Integer categoryId) {
        List<ItemDTO> items = itemService.getItemsByCategoryId(categoryId);
        return ResponseEntity.ok(items);
    }


    @PutMapping("/code/{itemCode}")
    public ResponseEntity<ItemDTO> updateItemByItemCode(@PathVariable String itemCode, @RequestBody ItemDTO itemDTO) {
        try {
            ItemDTO updatedItem = itemService.updateItemByItemCode(itemCode, itemDTO);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/code/{itemCode}")
    public ResponseEntity<Void> deleteItemByItemCode(@PathVariable String itemCode) {
        try {
            itemService.deleteItemByItemCode(itemCode);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/sizes/{itemCode}")
    public ResponseEntity<List<ItemSizeDTO>> addSizesToItem(@PathVariable String itemCode, @RequestBody List<ItemSizeDTO> sizes) {
        System.out.println("Received PUT request for itemCode: " + itemCode);
        System.out.println("Sizes payload: " + sizes);
        try {
            List<ItemSizeDTO> updatedSizes = itemService.addSizesToItem(itemCode, sizes);
            return ResponseEntity.ok(updatedSizes);
        } catch (RuntimeException e) {
            e.printStackTrace(); // Print the stack trace for debugging
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/sizes/{itemCode}")
    public ResponseEntity<List<ItemSizeDTO>> getSizesForItem(@PathVariable String itemCode) {
        try {
            List<ItemSizeDTO> sizes = itemService.getSizesForItem(itemCode);
            return ResponseEntity.ok(sizes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/sizes/{sizeId}")
    public ResponseEntity<Void> deleteSize(@PathVariable Integer sizeId) {
        itemService.deleteSize(sizeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/sizes/modify/{itemCode}")
    public ResponseEntity<List<ItemSizeDTO>> modifySizesForItem(
            @PathVariable String itemCode,
            @RequestBody List<ItemSizeDTO> sizes) {
        try {
            List<ItemSizeDTO> updatedSizes = itemService.modifySizesForItem(itemCode, sizes);
            return ResponseEntity.ok(updatedSizes);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }




}

