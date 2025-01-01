package mp.group3.backend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class UserDTO {
    private  Integer id;
    private String name;
    private String  email;
    private String contactNumber;
    private String status;

    public UserDTO(Integer id, String name, String email, String contactNumber, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.status = status;
    }
}
