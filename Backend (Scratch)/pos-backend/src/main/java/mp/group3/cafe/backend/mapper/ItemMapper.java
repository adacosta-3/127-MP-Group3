package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.ItemDTO;
import mp.group3.cafe.backend.entities.Categorization;
import mp.group3.cafe.backend.entities.Item;

import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDTO mapToItemDTO(Item item) {
        return new ItemDTO(
                item.getItemCode(),
                item.getName(),
                item.getBasePrice(),
                item.getCategory().getCategoryId(),
                item.getSizes().stream().map(ItemSizeMapper::mapToItemSizeDTO).collect(Collectors.toList()),
                item.getCustomizations().stream().map(CustomizationMapper::mapToCustomizationDTO).collect(Collectors.toList())
        );
    }


    public static Item mapToItem(ItemDTO itemDTO, Categorization category) {
        Item item = new Item();
        item.setItemCode(itemDTO.getItemCode());
        item.setName(itemDTO.getName());
        item.setBasePrice(itemDTO.getBasePrice());
        item.setCategory(category);
        return item;
    }
}

