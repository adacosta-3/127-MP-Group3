package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByMemberId(String memberId); // find by member ID
    Optional<Member> findByCustomer_CustomerId(Integer customerId); // find by customer ID
    List<Member> findByFullNameContainingIgnoreCase(String fullName); // search by full name


}

