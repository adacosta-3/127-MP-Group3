package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.ItemDTO;
import mp.group3.cafe.backend.entities.Categorization;
import mp.group3.cafe.backend.entities.Item;
import mp.group3.cafe.backend.mapper.ItemMapper;
import mp.group3.cafe.backend.repositories.CategorizationRepository;
import mp.group3.cafe.backend.repositories.ItemRepository;
import mp.group3.cafe.backend.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategorizationRepository categorizationRepository;

    @Override
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::mapToItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDTO> getItemById(Integer itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::mapToItemDTO);
    }

    @Override
    public ItemDTO createItem(ItemDTO itemDTO) {
        Optional<Categorization> categoryOpt = categorizationRepository.findById(itemDTO.getCategoryId());
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with ID: " + itemDTO.getCategoryId());
        }

        Categorization category = categoryOpt.get();

        // Generate code for the item dynamically
        String generatedCode = generateItemCode(itemDTO.getName(), category);

        // Map DTO to Item entity and set the generated code
        Item item = ItemMapper.mapToItem(itemDTO, category);
        item.setItemCode(generatedCode); // Ensure the entity has a `code` field

        Item savedItem = itemRepository.save(item);
        return ItemMapper.mapToItemDTO(savedItem);
    }


    @Override
    public ItemDTO updateItem(Integer itemId, ItemDTO itemDTO) {
        Optional<Item> existingItemOpt = itemRepository.findById(itemId);
        if (existingItemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }

        Optional<Categorization> categoryOpt = categorizationRepository.findById(itemDTO.getCategoryId());
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with ID: " + itemDTO.getCategoryId());
        }

        Item existingItem = existingItemOpt.get();
        Categorization category = categoryOpt.get();

        existingItem.setName(itemDTO.getName());
        existingItem.setBasePrice(itemDTO.getBasePrice());
        existingItem.setCategory(category);

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.mapToItemDTO(updatedItem);
    }

    @Override
    public void deleteItem(Integer itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDTO> getItemsByCategoryId(Integer categoryId) {
        return itemRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(ItemMapper::mapToItemDTO)
                .collect(Collectors.toList());
    }

    private String generateItemCode(String name, Categorization category) {
        String prefix = (category.getName().substring(0, 1) +
                category.getName().substring(category.getName().length() - 1))
                .toUpperCase();

        String nameSegment = name.replaceAll(" ", "")
                .toUpperCase()
                .substring(0, Math.min(name.length(), 4));
        if (nameSegment.length() < 4) {
            nameSegment = String.format("%-4s", nameSegment).replace(' ', 'Y');
        }

        long itemCount = itemRepository.countByCategory_CategoryId(category.getCategoryId());
        String incrementingNumber = String.format("%03d", itemCount + 1);

        return prefix + nameSegment + incrementingNumber;
    }
}