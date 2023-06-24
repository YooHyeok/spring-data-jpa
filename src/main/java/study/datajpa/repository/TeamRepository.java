package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

//@Repository // 컴포넌트 스캔 뿐만 아니라 JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리한다. - Spring Jpa Repository는 어노테이션 생략 가능
public interface TeamRepository extends JpaRepository<Team, Long> {
}
