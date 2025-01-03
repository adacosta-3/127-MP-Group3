package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.MemberDTO;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    List<MemberDTO> getAllMembers();

    Optional<MemberDTO> getMemberById(String memberId);

    MemberDTO createMember(MemberDTO memberDTO);

    MemberDTO updateMember(String memberId, MemberDTO memberDTO);

    void deleteMember(String memberId);
}

