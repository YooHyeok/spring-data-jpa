package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Map;

public interface MemberRepository extends JpaRepository<Member, Long> {
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
     * @Query Map<'String,Object> 조회 <br/>
     * 값 1개 이상 조회 <br/>
     * 주의사항 : as 키워드를 사용하여 컬럼명을 명시적으로 지정해줘야 Map에서 key를 기준으로 할때 불러올 수 있다. <br/>
     * (jpa는 쿼리가 돌면서 자동으로 as에 임의의 값이 부여되기 때문에 DB컬럼명 그대로 불러와지지 않는다.)
     * @Element : Key(컬럼명)
     * @Type : Value(데이터)
     */

    @Query("select m.username as username, m.age as age from Member m")
    List<Map<String,Object>> findUserNameListOfMap();

    /**
     * @Query DTO 조회 <br/>
     * 순수 JPA JPQL과 동일하다. <br/>
     * 조회할 컬럼을 다음과 같이 new 예약어를 사용하여 DTO 생성자에 주입해준다. <br/>
     * new인스턴스 DTO생성자 사용시 주의할 점은 풀패키지명을 기입해줘야한다. <br/>
     * 예) new jpabook.jpql.UserDTO(m.username, m.age)
     */
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findUserNameListOfDto();
}
