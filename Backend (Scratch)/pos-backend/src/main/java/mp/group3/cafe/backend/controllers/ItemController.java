package mp.group3.cafe.backend.controllers;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.ItemDTO;
import mp.group3.cafe.backend.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}

