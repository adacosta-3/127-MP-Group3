package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.MemberDTO;
import mp.group3.cafe.backend.entities.Customer;
import mp.group3.cafe.backend.entities.Member;
import mp.group3.cafe.backend.mapper.MemberMapper;
import mp.group3.cafe.backend.repositories.CustomerRepository;
import mp.group3.cafe.backend.repositories.MemberRepository;
import mp.group3.cafe.backend.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(MemberMapper::mapToMemberDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MemberDTO> getMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .map(MemberMapper::mapToMemberDTO);
    }

    @Override
    public MemberDTO createMember(MemberDTO memberDTO) {
        // Check if the customer exists
        Optional<Customer> customerOpt = customerRepository.findById(memberDTO.getCustomerId());
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found with ID: " + memberDTO.getCustomerId());
        }

        Customer customer = customerOpt.get();

        // Generate a random 5-character alphanumeric Member ID
        String generatedMemberId = generateUniqueMemberId();
        memberDTO.setMemberId(generatedMemberId);

        // Map DTO to Entity
        Member member = MemberMapper.mapToMember(memberDTO, customer);

        // Save the Member entity
        Member savedMember = memberRepository.save(member);

        // Map back to DTO and return
        return MemberMapper.mapToMemberDTO(savedMember);
    }

    private String generateUniqueMemberId() {
        int length = 5;
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        String memberId;
        do {
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                builder.append(alphanumeric.charAt(random.nextInt(alphanumeric.length())));
            }
            memberId = builder.toString();
        } while (memberRepository.existsById(memberId)); // Ensure uniqueness

        return memberId;
    }



    @Override
    public MemberDTO updateMember(String memberId, MemberDTO memberDTO) {
        Optional<Member> existingMemberOpt = memberRepository.findById(memberId);
        if (existingMemberOpt.isEmpty()) {
            throw new RuntimeException("Member not found with ID: " + memberId);
        }

        Member existingMember = existingMemberOpt.get();
        existingMember.setFullName(memberDTO.getFullName());
        existingMember.setEmail(memberDTO.getEmail());
        existingMember.setPhoneNumber(memberDTO.getPhoneNumber());
        existingMember.setDateOfBirth(
                memberDTO.getDateOfBirth() != null ? java.sql.Date.valueOf(memberDTO.getDateOfBirth()) : null
        );

        Member updatedMember = memberRepository.save(existingMember);
        return MemberMapper.mapToMemberDTO(updatedMember);
    }

    @Override
    public void deleteMember(String memberId) {
        memberRepository.deleteById(memberId);
    }
}

