package mp.group3.backend.services;

import mp.group3.backend.dtos.ProductDTO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductDTO>> getAllProduct();

    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<String> delete(Integer id);

    ResponseEntity<List<ProductDTO>> getByCategory(Integer id);

    ResponseEntity<ProductDTO> getProductById(Integer id);
    @Modifying
    @Transactional
    ResponseEntity<String> updateProductStatus(Map<String, String> requestMap);

}
