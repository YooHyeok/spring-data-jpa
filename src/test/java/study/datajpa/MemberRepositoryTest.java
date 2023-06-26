package study.datajpa;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.MemberSpecification;
import study.datajpa.repository.TeamRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @PersistenceContext
    EntityManager em;

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

    @Test
    public void collectionParameterBindingTest() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameIn(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnTypeTest() {
        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);

        List<Member> listMember = memberRepository.findListByUsername("asdadasd"); //빈 컬렉션을 반환한다.
        assertThat(listMember.size()).isEqualTo(0); // 0열 반환 - (데이터가 없다고 하더라도 null일 수가 없음.)
        assertThat(listMember).isNotNull(); // 빈 컬렉션 이므로 if(listMember == null) 위험한 코드이다.
        assertThat(listMember).isEmpty(); // 비어있음.

        Member member = memberRepository.findMemberByUsername("asdadasd"); //null을 반환한다.
        assertThat(member).isNull(); // 순수 JPA에서 NoResultException이 터지는것을 Spring에서는 TryCatch로 에러를 감싸 null로 반환한다.

        Optional<Member> optionalMember =
                memberRepository.findOptionalByUsername("asdadasd"); //데이터가 있을 지 없을 지 모르면 Optional로 반환받는다. return Optional.offNullable(member);
        assertThat(optionalMember.isPresent()).isEqualTo(false); // isPresent()를 사용하기보다 return optionalMember.orElse(null); 를 추천한다.
        System.out.println("orElse(null) : " + optionalMember.orElse(null)); // orElse는 값이 존재하면 값 반환 없으면 매개변수 값 반환

        /**
         * Optional은 한건의 결과를 조회하면서 null유무를 확인할때 사용하는데 <br/>
         * 데이터가 2건 이상일 경우 IncorrectResultSizeDataAccessException을 터트린다. <br/>
         * 원래는 NonUniqueResultException이 터지는데 Spring Data JPA가 IncorrectResultSizeDataAccessException 로 바꿔서 반환한다. <br/>
         * 이유는 Repository의 기술은 예를들어 몽고DB가 될 수도 있고 Redis가 될 수 도 있는데, <br/>
         * 그것을 사용하는 서비스계층의 클라이언트 코드들은 JPA 예외에 의존하는게 아니라 스프링이 추상화한 예외에 의존하면 <br/>
         * 하부의 리포지토리 기술을 JPA에서 몽고디비나 다른 JDBC기술로 바꾸어도 <br/>
         * 스프링은 동일하게 데이터가 맞지 않맞는 것들은 IncorrectResultSizeDataAccessException 예외를 터트린다.
         * 이것을 사용하는 클라이언트 코드를 바꿀 필요가 없어진다.
         */
        Member m2 = new Member("AAA", 10);
        memberRepository.save(m2);

        Optional<Member> optionalMemberAAA =
                memberRepository.findOptionalByUsername("AAA"); // Optional은 결과가 한건을 조회할때 사용하는데 2건이상이면
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10, offset = 0, limit = 3;
        Sort usernameDesc = Sort.by(Sort.Direction.DESC, "username");

        // PageRequest에 담아 처리
        PageRequest pageable = PageRequest.of(offset, limit, usernameDesc);

        Page<Member> page = memberRepository.findByAge(age, pageable);
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); //stream반복자 문법 Dto변환

        List<Member> content = page.getContent(); //page객체로부터 조회된 데이터를 가져온다.

        System.out.println("조회된 데이터 수 : " + content.size());
        System.out.println("전체 데이터 수 : " + page.getTotalElements());
        System.out.println("페이지 번호 : " + page.getNumber());
        System.out.println("전체 페이지 번호 : " + page.getTotalPages());
        System.out.println("첫번째 항목 여부 : " + page.isFirst());
        System.out.println("다음 페이지 여부 : " + page.hasNext());

        assertThat(content.size()).isEqualTo(3); // limit이 3 이므로 3명이 조회된다.
        assertThat(page.getTotalElements()).isEqualTo(5); // 전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); // 첫번째 항목인지 여부 (boolean)
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는지 여부(boolean)

        Slice<Member> slice = memberRepository.findSliceByAge(age, pageable);
        List<Member> content2 = slice.getContent();
        assertThat(content2.size()).isEqualTo(3); // limit이 3 이므로 3명이 조회된다.
        assertThat(slice.getNumber()).isEqualTo(0); // 현재 페이지 번호
        assertThat(slice.isFirst()).isTrue(); // 첫번째 항목인지 여부 (boolean)
        assertThat(slice.hasNext()).isTrue(); // 다음 페이지가 있는지 여부(boolean)

        content2.stream().map(member -> new MemberDto(member.getId(), member.getUsername(), null)); //stream반복자 문법 Dto변환


    }

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //나이가 20살 이상인 회원들의 나이를 1씩 증가시킨다.
        int resultCount = memberRepository.bulkAgePlus(20);
        assertThat(resultCount).isEqualTo(3); // 20, 21, 40 3명

        List<Member> member5 = memberRepository.findByUsername("member5");
        System.out.println("member5 = " + member5); //트랜잭션 커밋 시점에 save()가 persist되므로 현재 flush로 인해 쿼리는 날라갔지만 update이후 1차캐시는 여전히 그대로 이다.

//        em.clear(); //clearAutomatically = true 옵션 대체
//        List<Member> remember5 = memberRepository.findByUsername("member5");
//        System.out.println("member5 = " + remember5);
    }

    @Test
    public void fetchJoinTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 19, teamB));

        /**
         * left조인은 걸리고, select절에 team은 없지만
         * n+1이 발생하지 않고도 team이 조회되는 이유
         * 프록시 객체가 아닌 진짜 team객체를 반환해주는 이유
         */
        List<Member> members1 = memberRepository.findmemberByLeftJoin();
        for (Member member : members1) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //=== fetch를 적용한 join ===//
        /**
         * 1. select절에 member와 team을 모두 불러오는 left join 쿼리가 호출된다.
         * 2. N+1 현상이 발생할때 Team에 대한 proxy객체를 주입했던 현상이 발생하지 않는다.
         * (진짜 객체를 주입한다.)
         */
        List<Member> members2 = memberRepository.findAll();
        for (Member member : members2) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        List<Member> members3 = memberRepository.findEgByUsername("member1");
        for (Member member : members2) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1); // 영속화
        em.flush(); // insert 쿼리
        em.clear(); // 영속 컨텍스트 초기화

//        Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");
        em.flush(); // 변경 감지 - update 발생
    }

    @Test
    public void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();//호출이 된다.
    }

    @Test
    public void jpaEventBaseEntity() throws Exception {
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist발생 (등록/수정 현재시간 세팅)
        Thread.sleep(100); //잠시 쉬고 수정하기 위해서
        member.setUsername("member2");
        em.flush(); //@PreUpdate
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();

        System.out.println("findMember.createdDate = " + findMember.getCreatedDate());
        System.out.println("findMember.lastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("findMember.createdBy = " + findMember.getCreatedBy());
        System.out.println("findMember.lastModifiedBy = " + findMember.getLastModifiedBy());
    }

    @Test
    public void secipcationBasic() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        // spec : username이 m1이고 teamName이 teamA인 spec 생성
        Specification<Member> spec = MemberSpecification.username("m1").and(MemberSpecification.teamName("teamA"));
        //spec을 JPA 기본 쿼리메소드에 담아주면 repository에 상속받았던 JpaSpecification에 의해 실행이 되고 spec에 맞게끔 join조건이 걸린다.
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        Member member = new Member("m1"); //Member 엔티티 자체가 검색 Condition이 된다.
        Team team = new Team("teamA");
        member.setTeam(team); //Member와 Team의 연관관계를 건다. (inner join구문)

        // ExampleMatcher : age는 primitive이므로 null 처리가 되지 않기 때문에 아래와 같이 조건을 무시할 수 있도록 설정
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age"); //age라는 속성이 있으면 condition에서 제외
        Example<Member> example = Example.of(member, matcher); //username이 들어있는 m1이므로 username이 m1인 조건을 추가
        List<Member> result = memberRepository.findAll(example);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo("m1");

        /**
         * Example of({prove}, {ExampleMatcher})
         *
         * Probe: 필드에 데이터가 있는 실제 도메인 객체
         * ExampleMatcher: 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
         * Example: Probe와 ExampleMatcher로 구성, 쿼리를 생성하는데 사용
         *
         * [장점]
         * 1. 동적쿼리를 편하게 처리
         * 2. 도메인 객체를 그대로 사용
         * 3. 데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경이 없게 추상화 되어 있다.
         * 4. 스프링 데이터 JPA - JpaRepository 인터페이스에 포함되어있다.(JpaRepository를 상속받으면 사용 가능)
         *
         * [단점]
         * 1. inner조인만 가능하고 left조인은 되지 않는다.
         * 2. 중첩 제약조건이 안된다.
         *  -> firstname = ?0 or (firstname = ?1 and lastname =?2)
         * 3. 매칭 조건이 매우 단순하다.
         *  -> 문자는 starts/contains/ends/regex
         *  -> 다른 속성은 정확한 매칭( = )만 지원
         *  
         * 실무에서 사용하기 매칭조건이 너무 단순하고 LEFT조인이 안되므로 QueryDsl을 사용하는것을 추천
         */


    }
}