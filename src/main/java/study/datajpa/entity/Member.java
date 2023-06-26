package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery( // namedQuery 생성
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
@NamedEntityGraph( //NamedEntityGraph : 미리 록하여 관리
        name = "Member.all", //EntityGrraph를 호출할 이름을 미리 지정한다.
        attributeNodes = @NamedAttributeNode("team") // attributePaths값을 적용한다.
)
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    /**
     * 1번 생성자 <br/>
     * 2번 생성자 호출 : username 초기화 및 age 0
     * @param username
     */
    public Member(String username) {
        this(username, 0, null); // 2번 생성자 호출
    }

    /**
     * 2번 생성자 <br/>
     * 3번 생성자 호출 : username, age 초기화 및 team을 null로 초기화
     * @param username
     * @param age
     */
    public Member(String username, int age) {
        this(username, age, null); // 3번 생성자 호출
    }

    /**
     * 3번 생성자 <br/>
     * username, age, Team 초기화
     * @param username
     * @param age
     */
    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    /**
     * 양방향 연관관계 편의 메소드 <br/>
     * Member의 team을 초기화함과 동시에 Team의 members에 현재 Member추가
     * @param team
     */
    private void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
