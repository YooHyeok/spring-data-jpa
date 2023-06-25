package study.datajpa.repository;

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
    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age >= :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }
}
