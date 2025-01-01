package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.OrderLineCustomizationDTO;
import mp.group3.cafe.backend.entities.CustomizationOptions;
import mp.group3.cafe.backend.entities.OrderLine;
import mp.group3.cafe.backend.entities.OrderLineCustomization;
import mp.group3.cafe.backend.mapper.OrderLineCustomizationMapper;
import mp.group3.cafe.backend.repositories.CustomizationOptionsRepository;
import mp.group3.cafe.backend.repositories.OrderLineCustomizationRepository;
import mp.group3.cafe.backend.repositories.OrderLineRepository;
import mp.group3.cafe.backend.service.OrderLineCustomizationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderLineCustomizationServiceImpl implements OrderLineCustomizationService {

    private final OrderLineCustomizationRepository customizationRepository;
    private final OrderLineRepository orderLineRepository;
    private final CustomizationOptionsRepository optionsRepository;

    @Override
    public List<OrderLineCustomizationDTO> getCustomizationsByOrderLineId(Integer orderLineId) {
        return customizationRepository.findByOrderLine_OrderLineId(orderLineId)
                .stream()
                .map(OrderLineCustomizationMapper::mapToOrderLineCustomizationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderLineCustomizationDTO addCustomizationToOrderLine(OrderLineCustomizationDTO customizationDTO) {
        Optional<OrderLine> orderLineOpt = orderLineRepository.findById(customizationDTO.getOrderLineId());
        if (orderLineOpt.isEmpty()) {
            throw new RuntimeException("Order line not found with ID: " + customizationDTO.getOrderLineId());
        }

        Optional<CustomizationOptions> optionOpt = optionsRepository.findById(customizationDTO.getOptionId());
        if (optionOpt.isEmpty()) {
            throw new RuntimeException("Customization option not found with ID: " + customizationDTO.getOptionId());
        }

        OrderLineCustomization customization = OrderLineCustomizationMapper.mapToOrderLineCustomization(
                customizationDTO, orderLineOpt.get(), optionOpt.get());
        OrderLineCustomization savedCustomization = customizationRepository.save(customization);
        return OrderLineCustomizationMapper.mapToOrderLineCustomizationDTO(savedCustomization);
    }

    @Override
    public void removeCustomizationFromOrderLine(Integer customizationId) {
        customizationRepository.deleteById(customizationId);
    }
}

