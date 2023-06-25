package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * [NamedQuery] <br/>
     * @Param을 사용해서 파라미터 바인딩을 한다. <br/>
     * NamedQuery의 name 값과 리포지토리 엔티티,메소드명이 일치한다면 @Query 생략이 가능 <br/>
     * 생략시 우선순위 1. 네임드쿼리 2. 쿼리메소드 <br/>
     * (Generic에 있는 Entity명.메소드명을통해 네임드쿼리를 먼저 찾아준다.)
     * */
//    @Query(name = "Member.findByUsername")
    public List<Member> findByUsername(@Param("username") String username);

    /**
     * @Query - 리포지토리 메소드에 쿼리 정의 <br/>
     * NamedQuery와 같은 장점으로 Application 로딩 시점에 오류를 잡아준다 <br/>
     * 이름없는 NamedQuery라고 볼 수 있다.
     */
    @Query("select m from Member m where m.username = :username")
    public List<Member> findByUsername2(@Param("username") String username);
    @Query("select m from Member m where m.username = :username and m.age = :age")
    public List<Member> findByUsernameAndAge(@Param("username") String username, @Param("age") int age);

}
