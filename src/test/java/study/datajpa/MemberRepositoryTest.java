package study.datajpa;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findById = memberRepository.findById(savedMember.getId()).get();

        assertThat(findById.getId()).isEqualTo(savedMember.getId());
        assertThat(findById.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findById).isEqualTo(savedMember); //JPA 엔터티 동일성 보장 (영속성 1차캐시 Proxy객체)
    }
}