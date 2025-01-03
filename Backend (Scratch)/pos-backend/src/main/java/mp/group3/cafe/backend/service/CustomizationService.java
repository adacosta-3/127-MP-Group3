package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.CustomizationDTO;
import mp.group3.cafe.backend.DTO.CustomizationOptionsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CustomizationService {
    List<CustomizationDTO> getAllCustomizations();

    Optional<CustomizationDTO> getCustomizationById(Integer customizationId);

    List<CustomizationDTO> getCustomizationsByItemCode(String itemCode);

    CustomizationDTO createCustomization(CustomizationDTO customizationDTO);

    CustomizationDTO updateCustomization(Integer customizationId, CustomizationDTO customizationDTO);

    void deleteCustomization(Integer customizationId);

    List<CustomizationDTO> updateCustomizationsByItemCode(String itemCode, List<CustomizationDTO> customizations);
    List<CustomizationOptionsDTO> addOptionsToCustomization(Integer customizationId, List<CustomizationOptionsDTO> options);

    List<CustomizationDTO> uploadCustomizationsFromCSV(String itemCode, MultipartFile file) throws IOException;
}

