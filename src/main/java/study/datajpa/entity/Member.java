package study.datajpa.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String userName;

    protected Member() {
    }

    public Member(String userName) {
        this.userName = userName;
    }
}
