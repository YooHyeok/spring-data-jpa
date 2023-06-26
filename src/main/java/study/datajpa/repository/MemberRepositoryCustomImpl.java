package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 사용자 정의 인터페이스 구현체 클래스 <br/>
 * MemberRepositoryCustom 인터페이스 기능을 구현하는 구현체 클래스이다. <br/>
 * suffix(Postfix) 이름을 Impl로 설정해줘야 스프링 빈으로 등록된다. <br/>
 * 이 인터페이스를 상속받는 MemberRepository에서 구현한 기능을 사용할 수 있게 된다.
 * 이에대한 이유는 의존성 주입을 받기 위해서는 빈으로 등록이 되어야 하는데
 * 현재 빈으로 등록되어 있는 레포지토리는 MemberRepository이기 때문에
 * (JpaRepository를 상속받으면 자동으로 스프링빈으로 등록된다.)
 * 상속을 받는 레포지토리 또한 빈으로 등록해줘야 자동으로 의존성이 주입되어 연결된다.
 */
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
