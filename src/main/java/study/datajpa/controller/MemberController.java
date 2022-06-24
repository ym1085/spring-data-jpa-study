package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
//        memberRepository.save(new Member("userA"));
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i)); // userName, age
        }
    }

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUserName();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUserName();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
//        log.info("pageable = {}", pageable.toString());
//        return memberRepository.findAll(pageable);

        // Member Entity -> Member DTO
        return memberRepository.findAll(pageable)
                               .map(m -> new MemberDto(m.getId(), m.getUserName(), null));
    }
}
