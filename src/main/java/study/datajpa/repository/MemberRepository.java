package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);

    List<Member> findFirstBy();

    List<Member> findFirst3By();

    List<Member> findTopBy();

    List<Member> findTop3HelloBy();

}
