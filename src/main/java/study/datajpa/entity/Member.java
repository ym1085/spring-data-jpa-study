package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "userName", "age"}) // 연관관계 필드는 @ToString 지양
@NamedQuery(
        name = "Member.findByUserName",
        query = "select m from Member m where m.userName = :userName"
)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String userName;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id") // FK
    private Team team;

//    protected Member() {
//    }

    public Member(String userName) {
        this.userName = userName;
    }

    public Member(Long id, String userName, int age) {
        this.id = id;
        this.userName = userName;
        this.age = age;
    }

    public Member(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    // 연관관계 편의 메서드 생성
    // N(Member) :  1(Team)
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
