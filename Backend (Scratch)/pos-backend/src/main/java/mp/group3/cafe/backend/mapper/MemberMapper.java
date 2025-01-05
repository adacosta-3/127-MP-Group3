package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.MemberDTO;
import mp.group3.cafe.backend.entities.Customer;
import mp.group3.cafe.backend.entities.Member;

public class MemberMapper {
    public static MemberDTO mapToMemberDTO(Member member) {
        return new MemberDTO(
                member.getMemberId(),
                member.getCustomer().getCustomerId(),
                member.getFullName(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getDateOfBirth() != null ? member.getDateOfBirth().toString() : null
        );
    }

    public static Member mapToMember(MemberDTO memberDTO, Customer customer) {
        Member member = new Member();
        member.setMemberId(memberDTO.getMemberId());
        member.setCustomer(customer);
        member.setFullName(memberDTO.getFullName());
        member.setEmail(memberDTO.getEmail());
        member.setPhoneNumber(memberDTO.getPhoneNumber());
        member.setDateOfBirth(
                memberDTO.getDateOfBirth() != null ? java.sql.Date.valueOf(memberDTO.getDateOfBirth()) : null
        );
        return member;
    }
}
