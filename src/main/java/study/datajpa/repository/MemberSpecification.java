package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.*;

public class MemberSpecification {

    /**
     * inner join
     * team team1_
     * on member0_.team_id=team1_.team_id
     */
    public static Specification<Member> teamName(final String teamName) {
        /*return new Specification<Member>() {
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (StringUtils.isEmpty(teamName)) { //teamName이 Empty면 null반환 (null은 무시된다)
                    return null;
                }
                Join<Member, Team> t = root.join("team", JoinType.INNER);//회원과 조인하는 JPACriteria문법
                return builder.equal(t.get("name"), teamName);
            }
        };*/
        return (Specification<Member>) (root, query, builder) -> {
            if (StringUtils.isEmpty(teamName)) {
                return null;
            }
            Join<Member, Team> t = root.join("team", JoinType.INNER); //회원과 조인
            return builder.equal(t.get("name"), teamName);
        };
    }

    /**
     * where
     * member0_.username='m1'
     * and team1_.name='teamA'
     */
    public static Specification<Member> username(final String username) {
        /*return new Specification<Member>() {
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.equal(root.get("username"), username);
            }
        };*/
        return (Specification<Member>) (root, query, builder) ->
                builder.equal(root.get("username"), username);
    }
}
