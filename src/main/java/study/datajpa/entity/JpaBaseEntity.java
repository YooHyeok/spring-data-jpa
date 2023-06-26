package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass // 실제 상속은 아니며 테이블 생성시 속성만 내려받아 DB컬럼으로 사용할 수 있게 해준다.
@Getter
public class JpaBaseEntity {

    //=== Auditing 기능 ===//
    @Column(updatable = false) // 생성일자를 실수라도 변경할수 없게 된다.
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist // JPA제공 => persist 하기 전에 이벤트 발생
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now; //등록일자와 수정일자를 맞춰놓는다
    }

    @PreUpdate // JPA제공 => update 하기 전에 이벤트 발생
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
