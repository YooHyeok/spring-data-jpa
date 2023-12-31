package study.datajpa;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberJpaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2); //영속성 1차캐시 프록시 객체로 동일성 보장

        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        // 이름이 AAA고 나이가 15이상인 회원 : m2
        List<Member> aaa = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(aaa.get(0).getUsername()).isEqualTo("AAA");
        assertThat(aaa.get(0).getAge()).isEqualTo(20);
        assertThat(aaa.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }
    
    @Test
    public void paging() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));
        
        //순수 값으로 처리
        int age = 10, offset = 0, limit = 3;

        List<Member> members = memberJpaRepository.findByPageOfSimple(age, offset, limit);
        long totalCount = memberJpaRepository.tatalCount(10);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);

        // PageRequest에 담아 처리
        PageRequest pageable = PageRequest.of(offset, limit);
        List<Member> members2 = memberJpaRepository.findByPageOfPageable(age, pageable);
        long totalCount2 = memberJpaRepository.tatalCount(10);

        assertThat(members2.size()).isEqualTo(3);
        assertThat(totalCount2).isEqualTo(5);

    }

    @Test
    public void bulkUpdate() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        //나이가 20살 이상인 회원들의 나이를 1씩 증가시킨다.
        int resultCount = memberJpaRepository.bulkAgePlus(20);
        assertThat(resultCount).isEqualTo(3); // 20, 21, 40 3명
    }
    
    
}