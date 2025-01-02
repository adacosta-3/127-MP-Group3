package mp.group3.cafe.backend.service;

import com.opencsv.exceptions.CsvException;
import mp.group3.cafe.backend.DTO.ItemDTO;
import mp.group3.cafe.backend.DTO.ItemSizeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<ItemDTO> getAllItems();

    ItemDTO updateItemByItemCode(String itemCode, ItemDTO itemDTO);

    Optional<ItemDTO> getItemById(Integer itemId);

    ItemDTO createItem(ItemDTO itemDTO);

    ItemDTO updateItem(Integer itemId, ItemDTO itemDTO);

    void deleteItem(Integer itemId);

    List<ItemDTO> getItemsByCategoryId(Integer categoryId);

    void deleteItemByItemCode(String itemCode);

    List<ItemSizeDTO> addSizesToItem(String itemCode, List<ItemSizeDTO> sizes);
    List<ItemSizeDTO> getSizesForItem(String itemCode);
    void deleteSize(Integer sizeId);


    List<ItemDTO> parseCSVToItems(MultipartFile file) throws IOException, CsvException;

    List<ItemDTO> createItems(List<ItemDTO> itemDTOs);

    List<ItemSizeDTO> modifySizesForItem(String itemCode, List<ItemSizeDTO> sizes);

}

