package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * [NamedQuery]
     * @Param을 사용해서 파라미터 바인딩을 한다.
     * */
//    @Query(name = "Member.findByUsername") //생략 가능... 생략시 우선순위 1. 네임드쿼리 2. 쿼리메소드 (Generic에 있는 Entity명.메소드명을통해 네임드쿼리를 먼저 찾아준다.)
    public List<Member> findByUsername(@Param("username") String username);
}
