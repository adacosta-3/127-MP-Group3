package mp.group3.cafe.backend.serviceImpl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.ItemDTO;
import mp.group3.cafe.backend.DTO.ItemSizeDTO;
import mp.group3.cafe.backend.entities.Categorization;
import mp.group3.cafe.backend.entities.Customization;
import mp.group3.cafe.backend.entities.Item;
import mp.group3.cafe.backend.entities.ItemSize;
import mp.group3.cafe.backend.mapper.ItemMapper;
import mp.group3.cafe.backend.mapper.ItemSizeMapper;
import mp.group3.cafe.backend.repositories.CategorizationRepository;
import mp.group3.cafe.backend.repositories.CustomizationRepository;
import mp.group3.cafe.backend.repositories.ItemRepository;
import mp.group3.cafe.backend.repositories.ItemSizeRepository;
import mp.group3.cafe.backend.service.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemSizeRepository itemSizeRepository;
    private final CategorizationRepository categorizationRepository;
    private final CustomizationRepository customizationRepository;

    @Override
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::mapToItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDTO updateItemByItemCode(String itemCode, ItemDTO itemDTO) {
        Optional<Item> existingItemOpt = itemRepository.findById(itemCode);
        if (existingItemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + itemCode);
        }

        Optional<Categorization> categoryOpt = categorizationRepository.findById(itemDTO.getCategoryId());
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with ID: " + itemDTO.getCategoryId());
        }

        Item existingItem = existingItemOpt.get();
        Categorization category = categoryOpt.get();

        // Update item details
        existingItem.setName(itemDTO.getName());
        existingItem.setBasePrice(itemDTO.getBasePrice());
        existingItem.setCategory(category);

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.mapToItemDTO(updatedItem);
    }

    @Override
    public Optional<ItemDTO> getItemByCode(String itemCode) {
        return itemRepository.findById(itemCode)
                .map(ItemMapper::mapToItemDTO);
    }

    @Override
    public ItemDTO createItem(ItemDTO itemDTO) {
        Optional<Categorization> categoryOpt = categorizationRepository.findById(itemDTO.getCategoryId());
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with ID: " + itemDTO.getCategoryId());
        }

        Categorization category = categoryOpt.get();

        if (itemDTO.getItemCode() == null || itemDTO.getItemCode().isEmpty()) {
            String generatedCode = generateItemCode(itemDTO.getName(), category);
            itemDTO.setItemCode(generatedCode);
            System.out.println("Generated Item Code: " + generatedCode);
        }

        Item item = ItemMapper.mapToItem(itemDTO, category);
        System.out.println("Saving item: " + item);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.mapToItemDTO(savedItem);
    }

    @Override
    public ItemDTO updateItem(String itemCode, ItemDTO itemDTO) {
        Optional<Item> existingItemOpt = itemRepository.findById(itemCode);
        if (existingItemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + itemCode);
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
    public void deleteItem(String itemCode) {
        itemRepository.deleteById(itemCode);
    }

    @Override
    public List<ItemDTO> getItemsByCategoryId(Integer categoryId) {
        return itemRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(ItemMapper::mapToItemDTO)
                .collect(Collectors.toList());
    }

    private String generateItemCode(String name, Categorization category) {
        // Extract prefix from category
        String prefix = (category.getName().substring(0, 1) +
                category.getName().substring(category.getName().length() - 1)).toUpperCase();

        // Create the name segment, ensuring it is exactly 4 characters
        String nameSegment = name.replaceAll(" ", "")
                .toUpperCase()
                .substring(0, Math.min(name.replaceAll(" ", "").length(), 4));
        if (nameSegment.length() < 4) {
            nameSegment = String.format("%-4s", nameSegment).replace(' ', 'Y');
        }

        // Generate incrementing number with leading zeros
        long itemCount = itemRepository.countByCategory_CategoryId(category.getCategoryId());
        String incrementingNumber = String.format("%03d", itemCount + 1);

        // Return the formatted code with dashes
        return String.format("%s-%s-%s", prefix, nameSegment, incrementingNumber);
    }

    @Override
    public void deleteItemByItemCode(String itemCode) {
        // Find the item by itemCode
        Optional<Item> itemOpt = itemRepository.findById(itemCode);
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + itemCode);
        }

        Item item = itemOpt.get();

        // Delete all customizations associated with the item
        List<Customization> customizations = customizationRepository.findByItem_ItemCode(item.getItemCode());
        customizationRepository.deleteAll(customizations);

        // Delete the item
        itemRepository.delete(item);
    }

    @Override
    public List<ItemSizeDTO> addSizesToItem(String itemCode, List<ItemSizeDTO> sizes) {
        Optional<Item> itemOpt = itemRepository.findById(itemCode);
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + itemCode);
        }

        Item item = itemOpt.get();

        // Fetch existing sizes for the item
        List<ItemSize> existingSizes = itemSizeRepository.findByItem_ItemCode(item.getItemCode());

        // Remove duplicates from the incoming sizes list
        List<ItemSizeDTO> uniqueSizes = sizes.stream()
                .filter(sizeDTO -> existingSizes.stream()
                        .noneMatch(existingSize -> existingSize.getSizeName().equalsIgnoreCase(sizeDTO.getSizeName())
                                && existingSize.getPriceAdjustment().equals(sizeDTO.getPriceAdjustment())))
                .collect(Collectors.toList());

        // Map the unique sizes to entities
        List<ItemSize> itemSizes = uniqueSizes.stream()
                .map(sizeDTO -> ItemSizeMapper.mapToItemSize(sizeDTO, item))
                .collect(Collectors.toList());

        // Save the new unique sizes
        itemSizeRepository.saveAll(itemSizes);

        // Return all sizes (existing + new unique sizes) as DTOs
        List<ItemSize> updatedSizes = itemSizeRepository.findByItem_ItemCode(item.getItemCode());
        return updatedSizes.stream()
                .map(ItemSizeMapper::mapToItemSizeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSizeDTO> getSizesForItem(String itemCode) {
        Optional<Item> itemOpt = itemRepository.findById(itemCode);
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + itemCode);
        }

        return itemSizeRepository.findByItem_ItemCode(itemOpt.get().getItemCode())
                .stream()
                .map(ItemSizeMapper::mapToItemSizeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSize(Integer sizeId) {
        itemSizeRepository.deleteById(sizeId);
    }

    @Override
    public List<ItemDTO> parseCSVToItems(MultipartFile file) throws IOException, CsvException {
        List<ItemDTO> items = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                ItemDTO dto = new ItemDTO();
                dto.setName(values[0]);
                dto.setBasePrice(Double.parseDouble(values[1]));
                dto.setCategoryId(Integer.parseInt(values[2]));
                items.add(dto);
            }
        }

        return items;
    }

    @Override
    public List<ItemSizeDTO> uploadSizesToItemFromCSV(String itemCode, MultipartFile file)
            throws IOException, CsvException {
        Optional<Item> itemOpt = itemRepository.findById(itemCode);
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + itemCode);
        }

        Item item = itemOpt.get();

        List<ItemSizeDTO> sizes = parseSizesCSV(file);

        List<ItemSize> itemSizes = sizes.stream()
                .map(sizeDTO -> ItemSizeMapper.mapToItemSize(sizeDTO, item))
                .collect(Collectors.toList());

        itemSizeRepository.saveAll(itemSizes);

        List<ItemSize> updatedSizes = itemSizeRepository.findByItem_ItemCode(item.getItemCode());

        return updatedSizes.stream()
                .map(ItemSizeMapper::mapToItemSizeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSizeDTO> parseSizesCSV(MultipartFile file) throws IOException, CsvException {
        List<ItemSizeDTO> sizes = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                ItemSizeDTO sizeDTO = new ItemSizeDTO();
                sizeDTO.setSizeName(values[0]);
                sizeDTO.setPriceAdjustment(Double.parseDouble(values[1]));
                sizes.add(sizeDTO);
            }
        }

        return sizes;
    }

    @Override
    public List<ItemDTO> createItems(List<ItemDTO> itemDTOs) {
        List<ItemDTO> createdItems = new ArrayList<>();

        for (ItemDTO itemDTO : itemDTOs) {
            createdItems.add(createItem(itemDTO));
        }

        return createdItems;
    }

    @Override
    public List<ItemSizeDTO> modifySizesForItem(String itemCode, List<ItemSizeDTO> sizes) {
        // Find the item by its code
        Optional<Item> itemOpt = itemRepository.findById(itemCode);
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + itemCode);
        }

        Item item = itemOpt.get();

        // Retrieve existing sizes for the item
        List<ItemSize> existingSizes = itemSizeRepository.findByItem_ItemCode(item.getItemCode());

        // Map existing sizes to their IDs for quick lookup
        Map<Integer, ItemSize> existingSizeMap = existingSizes.stream()
                .collect(Collectors.toMap(ItemSize::getSizeId, size -> size));

        // Prepare the updated list of sizes
        List<ItemSize> updatedSizes = new ArrayList<>();
        for (ItemSizeDTO sizeDTO : sizes) {
            if (sizeDTO.getSizeId() != null && existingSizeMap.containsKey(sizeDTO.getSizeId())) {
                // Modify existing size
                ItemSize existingSize = existingSizeMap.get(sizeDTO.getSizeId());
                existingSize.setSizeName(sizeDTO.getSizeName());
                existingSize.setPriceAdjustment(sizeDTO.getPriceAdjustment());
                updatedSizes.add(existingSize);
            } else {
                // Add new size
                ItemSize newSize = ItemSizeMapper.mapToItemSize(sizeDTO, item);
                updatedSizes.add(newSize);
            }
        }

        // Save all updated sizes
        itemSizeRepository.saveAll(updatedSizes);

        // Return updated sizes as DTOs
        return updatedSizes.stream()
                .map(ItemSizeMapper::mapToItemSizeDTO)
                .collect(Collectors.toList());
    }

}