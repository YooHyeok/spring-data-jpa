package study.datajpa.entity;


import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @EntityListeners : 엔티티를 데이터베이스에 적용하기 전후로 콜백을 요청할 수 있게 하는 어노테이션
 * AuditingEntityListener : 엔터티의 Auditing 정보를 주입하는 JPA 엔터티 리스너 클래스이다.
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class SpringJpaBaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

}
