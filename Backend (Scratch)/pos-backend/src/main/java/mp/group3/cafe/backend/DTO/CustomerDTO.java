package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerDTO {
    private Integer customerId;
    private String type; // "Member" or "Guest"
    private String firstName;
    private String lastName;
}

