package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    /**
     * [Open Projection] : 엔티티 데이터를 모두 조회한 뒤 연산을 처리한 결과 반환 <br/>
     * SpEl문법도 지원한다. <br/>
     * @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}") <br/>
     * 에 의해서 값을 더해서 반환할 수 있다.
     */
    @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
    String getUsername();
}
