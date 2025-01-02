package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.ItemDTO;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<ItemDTO> getAllItems();

    Optional<ItemDTO> getItemById(Integer itemId);

    ItemDTO createItem(ItemDTO itemDTO);

    ItemDTO updateItem(Integer itemId, ItemDTO itemDTO);

    void deleteItem(Integer itemId);

    List<ItemDTO> getItemsByCategoryId(Integer categoryId);
}

