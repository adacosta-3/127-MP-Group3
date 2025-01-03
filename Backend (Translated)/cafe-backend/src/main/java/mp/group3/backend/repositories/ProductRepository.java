package mp.group3.backend.repositories;

import mp.group3.backend.dtos.ProductDTO;
import mp.group3.backend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<ProductDTO> getAllProduct();

    List<ProductDTO> getByCategory(@Param("id") Integer id);

    ProductDTO getProductById(@Param("id") Integer id);

    @Modifying
    @Transactional
    void updateProductStatus(@Param("status") String status, @Param("id") Integer id);

}
