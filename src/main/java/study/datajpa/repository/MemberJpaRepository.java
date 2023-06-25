package study.datajpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {
    @PersistenceContext
    private EntityManager em;

    /** 회원 단건 저장 */
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    /** 회원 단건 조회 */
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    /** 회원 전체 조회 */
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    /** 회원 단건 조회(Optional) */
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    /** 회원 count 조회 */
    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    /** 회원 삭제 */
    public void delete(Member member) {
        em.remove(member);
    }

    /** 회원 이름, 나이 조건으로 조회 */
    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age >= :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    /** NamedQuery */
    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    /** 페이징 조회 단순 값*/
    public List<Member> findByPageOfSimple(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /** 페이징 조회 - Pageable활용 */
    public List<Member> findByPageOfPageable(int age, Pageable pageable) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public long tatalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    /**
     * 벌크연산 : 다량의 update 혹은 delete 처리<br/>
     * executeUpdateQuery()<br/>
     * 주의 : 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리를 날린다.<br/>
     * update 혹은 delete는 flush를 먼저 호출하기 때문에 벌크 연산을 먼저 실행한다.<br/>
     * 만약 캐시에 데이터가 존재하는 경우는 벌크연산 수행 후 영속성 컨텍스트를 초기화 해야한다.(초기화 후 다시 조회-캐시에적재)<br/>
     */
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}
