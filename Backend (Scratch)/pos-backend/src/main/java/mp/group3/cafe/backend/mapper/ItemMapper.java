package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.ItemDTO;
import mp.group3.cafe.backend.entities.Categorization;
import mp.group3.cafe.backend.entities.Item;

public class ItemMapper {
    public static ItemDTO mapToItemDTO(Item item) {
        return new ItemDTO(
                item.getItemId(),
                item.getItemCode(),
                item.getName(),
                item.getBasePrice(),
                item.getCategory().getCategoryId()
        );
    }

    public static Item mapToItem(ItemDTO itemDTO, Categorization category) {
        Item item = new Item();
        item.setItemId(itemDTO.getItemId());
        item.setItemCode(itemDTO.getItemCode());
        item.setName(itemDTO.getName());
        item.setBasePrice(itemDTO.getBasePrice());
        item.setCategory(category);
        return item;
    }
}
