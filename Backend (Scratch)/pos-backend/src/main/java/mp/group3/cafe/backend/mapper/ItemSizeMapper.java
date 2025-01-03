package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.ItemSizeDTO;
import mp.group3.cafe.backend.entities.Item;
import mp.group3.cafe.backend.entities.ItemSize;
public class ItemSizeMapper {
    public static ItemSizeDTO mapToItemSizeDTO(ItemSize itemSize) {
        return new ItemSizeDTO(
                itemSize.getSizeId(),
                itemSize.getSizeName(),
                itemSize.getPriceAdjustment()
        );
    }

    public static ItemSize mapToItemSize(ItemSizeDTO itemSizeDTO, Item item) {
        ItemSize itemSize = new ItemSize();
        itemSize.setSizeId(itemSizeDTO.getSizeId());
        itemSize.setSizeName(itemSizeDTO.getSizeName());
        itemSize.setPriceAdjustment(itemSizeDTO.getPriceAdjustment());
        itemSize.setItem(item);
        return itemSize;
    }
}

