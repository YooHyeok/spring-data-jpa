package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {
    @PersistenceContext
    private EntityManager em;

    /** team 저장 */
    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    /**
     * team 전체조회
     */
    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class)
                .getResultList();
    }

    /** team 단건조회 (Optional) */
    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    /** team 개수 조회 (count) */
    public long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }

    /** team 삭제 */
    public void delete(Team team) {
        em.remove(team);
    }

}
