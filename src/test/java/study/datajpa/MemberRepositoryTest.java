package study.datajpa;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    /**
     * JpaRepository를 상속받은 인터페이스는 SpringDataJPA가 구현체 생성해서 Proxy객체로 injection 해준다. <br/>
     * 따라서, 구현체의 기능 (findById, findAll, save, 쿼리메소드 등)을 사용할 수 있게 된다.
     */
    @Test
    public void proxyPrint() {
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
    }
    
    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findById = memberRepository.findById(savedMember.getId()).get();

        assertThat(findById.getId()).isEqualTo(savedMember.getId());
        assertThat(findById.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findById).isEqualTo(savedMember); //JPA 엔터티 동일성 보장 (영속성 1차캐시 Proxy객체)
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);
        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);
        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    /** 회원 이름, 나이 조건으로 조회 */
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // 이름이 AAA고 나이가 15이상인 회원 : m2
        List<Member> aaa = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(aaa.get(0).getUsername()).isEqualTo("AAA");
        assertThat(aaa.get(0).getAge()).isEqualTo(20);
        assertThat(aaa.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQueryAnnotation() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAge("AAA", 10);
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQueryAnnotationDTOTest() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA); // persist 후 flush, clear
        Member m1 = new Member("AAA", 10, teamA);
        memberRepository.save(m1);

        Team teamB= new Team("teamB");
        teamRepository.save(teamB);
        Member m2 = new Member("BBB", 20, teamB);
        memberRepository.save(m2);

        // 타입 테스트
        List<String> resultByType = memberRepository.findUserNameListOne();
        assertThat(resultByType.get(0)).isEqualTo(m1.getUsername());

        // Map<String, Object> 테스트
        List<Map<String,Object>> resultByMap = memberRepository.findUserNameListOfMap();
        assertThat(resultByMap.get(0).get("username")).isEqualTo(m1.getUsername());
        assertThat(resultByMap.get(0).get("age")).isEqualTo(m1.getAge());

        // DTO 테스트
        List<MemberDto> result = memberRepository.findUserNameListOfDto();
        MemberDto findMember = result.get(0);
        assertThat(findMember.getId()).isSameAs(m1.getId());
        assertThat(findMember.getUsername()).isSameAs(m1.getUsername());
        assertThat(findMember.getTeamName()).isSameAs(m1.getTeam().getName());
    }
}