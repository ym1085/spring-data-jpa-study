package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);

    List<Member> findFirstBy();

    List<Member> findFirst3By();

    List<Member> findTopBy();

    List<Member> findTop3HelloBy();

//    @Query(name = "Member.findByUserName")
    List<Member> findByUserName(@Param("userName") String userName);

    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);

    // 리포지토리 쿼리에서 단순한 값 조회
    @Query("select m.userName from Member m")
    List<String> findUserNameList();

    // 리포지토리 쿼리에서 DTO 바로 조회, 불편하다.. QueryDSL 써야한다..
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUserName(String userName); // 컬렉션
    Member findMemberByUserName(String userName); // 단건
    Optional<Member> findOptionalByUserName(String userName); // 단건 Optional
}
