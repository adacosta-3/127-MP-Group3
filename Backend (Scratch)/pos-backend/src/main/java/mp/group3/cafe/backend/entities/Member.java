package mp.group3.cafe.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "MEMBER")
public class Member {
    @Id
    @Column(name = "member_id", length = 5)
    private String memberId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private java.sql.Date dateOfBirth;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number")
    private Long phoneNumber;
}


