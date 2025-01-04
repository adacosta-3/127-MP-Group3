package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberDTO {
    private String memberId;
    private Integer customerId;
    private String fullName;
    private String email;
    private Long phoneNumber;
    private String dateOfBirth;
}

