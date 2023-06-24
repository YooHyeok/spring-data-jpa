package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
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
     * 2번 생성자 호출
     * @param username
     */
    public Member(String username) {
        this(username, 0); // 2번 생성자 호출
    }

    /**
     * 2번 생성자 <br/>
     * username, age 초기화 
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

    private void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
