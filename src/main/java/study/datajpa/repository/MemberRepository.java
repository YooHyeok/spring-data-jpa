package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.Entity;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * [NamedQuery] <br/>
     *
     * @Param을 사용해서 파라미터 바인딩을 한다. <br/>
     * NamedQuery의 name 값과 리포지토리 엔티티,메소드명이 일치한다면 @Query 생략이 가능 <br/>
     * 생략시 우선순위 1. 네임드쿼리 2. 쿼리메소드 <br/>
     * (Generic에 있는 Entity명.메소드명을통해 네임드쿼리를 먼저 찾아준다.)
     */
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * @Query - 리포지토리 메소드에 쿼리 정의 <br/>
     * NamedQuery와 같은 장점으로 Application 로딩 시점에 오류를 잡아준다 <br/>
     * 이름없는 NamedQuery라고 볼 수 있다.
     */
    @Query("select m from Member m where m.username = :username")
    List<Member> findByUsername2(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findByUsernameAndAge(@Param("username") String username, @Param("age") int age);

    /**
     * @Query 조회 <br/>
     * 단순 값 1개 조회 <br/>
     * 해당 컬럼의 타입을 반환타입의 제너릭으로 지정 <br/>
     * 이 경우도 Map<'String,Object> 타입을 사용할 수 있다.
     */
    @Query("select m.username from Member m")
    List<String> findUserNameListOne();

    /**
     * @Query Map<' String, Object> 조회 <br/>
     * 값 1개 이상 조회 <br/>
     * 주의사항 : as 키워드를 사용하여 컬럼명을 명시적으로 지정해줘야 Map에서 key를 기준으로 할때 불러올 수 있다. <br/>
     * (jpa는 쿼리가 돌면서 자동으로 as에 임의의 값이 부여되기 때문에 DB컬럼명 그대로 불러와지지 않는다.)
     * @Element : Key(컬럼명)
     * @Type : Value(데이터)
     */

    @Query("select m.username as username, m.age as age from Member m")
    List<Map<String, Object>> findUserNameListOfMap();

    /**
     * @Query DTO 조회 <br/>
     * 순수 JPA JPQL과 동일하다. <br/>
     * 조회할 컬럼을 다음과 같이 new 예약어를 사용하여 DTO 생성자에 주입해준다. <br/>
     * new인스턴스 DTO생성자 사용시 주의할 점은 풀패키지명을 기입해줘야한다. <br/>
     * 예) new jpabook.jpql.UserDTO(m.username, m.age)
     */
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findUserNameListOfDto();

    /**
     * @Query 컬렉션 파라미터 바인딩 <br/>
     * Query의 조건절중 In절에서 사용한다.
     */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByUsernameIn(@Param("names") List<String> names);


    //===반환타입===//
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    Page<Member> findByAge(int age, PageRequest pageable); //반환 타입을 Page로 받으면 TotalCount도 함께 쿼리가 조회된다.

    Slice<Member> findSliceByAge(int age, PageRequest pageable);

    /**
     * [페이징] - countQuery 분리 <br/>
     * JPQL사용할때 LeftJOin 문이라면 CountQuery를 따로 지정할 수 있다.
     */
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAgeOfJPQL(int age, PageRequest pageable);

    /**
     * [벌크 연산] <br/>
     * @Modifying 어노테이션을 사용한다.<br/>
     * (생략시 getResultList나 getSingleResult를 호출한다. - 에러 발생) <br/>
     * 벌크 연산은 바로 DB에 Update쿼리가 날라가기 때문에 영속성 컨텍스트에 영향을 주지 않는다. <br/>
     * 즉, 트랜잭션 커밋 시점에 save()가 persist되므로 flush로 인해 쿼리는 날라가지만 update이후 1차캐시는 여전히 그대로가 될것이다. <br/>
     * 이에 따른 대안으로는 clearAutomatically = true 통해 1차캐시를 비워줄 수 있다.(기본값 false)
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //=== Fetch Join ===//

    /**
     * Member와 Team을 조인하지만 사실 이 쿼리는 Team을 전혀 사용하지 않는다.
     * SELECT절이나, WHERE절에서 사용하지 않는다는 뜻이다.
     * 사실상 select m from Member m과 같다.
     * left join이기 때문에 왼쪽에 있는 member 자체를 다 조회한다는 뜻이 된다.
     * 만약 select나, where에 team의 조건이 들어간다면 정상적인 join문이 보인다.
     * JPA는 이 경우 최적화를 해서 해당 join없이 해당 내용만으로 SQL을 만든다.
     * 만약 Member와 Team을 하나의 SQL로 한번에 조회하고 싶다면 JPA가 제공하는 fetch Jon을 사용해야 한다.
     * left join fetch 혹은 @EntityGraph(attributePath="team")
     *
     * @return
     */
    //    @EntityGraph(attributePaths = "team")
    @Query("select m from Member m join fetch m.team t")
    List<Member> findmemberByLeftJoin();

    /**
     * 기존 메소드를 오버라이드 하여 Fetch Join 적용
     * Team객체 변경사항 : proxy -> 진짜객체
     * 쿼리 : N+1현상 삭제됨
     * @return
     */
    @EntityGraph(attributePaths = "team")
    @Override
    List<Member> findAll();

    /**
     * fetch Join
     * Member에 대해서만 조회하는 쿼리에 @EntityGraph를 적용해도 fetch Join이 걸린다.
     */
//    @EntityGraph(attributePaths = "team")
    @EntityGraph("Member.all")
    List<Member> findEgByUsername(@Param("username") String username);


    /**
     * @QueryHints 적용 <br/>
     * simpleJpaRepository 오버라이드 메소드 <br/>
     * 오류 발생함
     */
//    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Override
    Optional<Member> findById(Long aLong);

    /**
     * @QueryHints 적용 <br/>
     * 조회용으로만 사용한다. <br/>
     * update Query가 발생하지 않는다. <br/>
     * 값을 변경하지 않더라도 불필요한 변경감지용 스냅샷을 생성하지 않는다. <br/>
     * 불필요한 메모리를 낭비하지 않게된다. (성능 최적화)
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * [ @Lock ]
     * 조회하는 동안 다른 사용자가 쓰기를 할 수 없게 하기 위해 Lock을 건다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);
}
