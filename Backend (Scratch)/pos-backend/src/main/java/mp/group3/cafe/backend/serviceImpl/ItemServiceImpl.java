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
        Item item = ItemMapper.mapToItem(itemDTO, category);
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
}
