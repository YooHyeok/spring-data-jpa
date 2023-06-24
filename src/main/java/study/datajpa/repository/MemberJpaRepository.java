package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberJpaRepository {
    @PersistenceContext
    private EntityManager em;

    /** 엔티티 저장 */
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    /** 엔티티 조회 */
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
