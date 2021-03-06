package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

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

    @Query(value = "select m from Member m left join m.team where m.age = :age",
            countQuery = "select count(m.userName) from Member m")
    Page<Member> findPagingByAge(@Param("age") int age, Pageable pageable);

    @Query(value = "select m from Member m left join m.team where m.age = :age",
            countQuery = "select count(m.userName) from Member m")
    Slice<Member> findSliceByAge(@Param("age") int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // JPA의 JPQL + fetch 조인 사용
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // JPQL은 사용하고 싶지 않을 때
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL도 사용, EntityGraph도 사용
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 메서드 이름으로 조회, EntityGraph도 사용
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUserName(@Param("userName") String userName);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String userName);

    //select for update
//    @Lock(LockModeType.PESSIMISTIC_READ)
//    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUserName(String userName);

//    List<UserNameOnly> findProjectionsByUserName(@Param("userName") String userName); // 인터페이스 기반 Projection

//    List<UserNameOnlyDto> findProjectionsByUserName(@Param("userName") String userName); // 클래스 기반 Projection

    <T> List<T> findProjectionsByUserName(@Param("userName") String userName, Class<T> type); // 클래스 기반 Projection + 동적 Projection [제네릭 타입]

    @Query(value = "select * from member where user_name = ?", nativeQuery = true)
    Member findByNativeQuery(String userName);

    @Query(value = "select m.member_id as id, m.user_name as userName, t.name as teamName " +
            "from member m left join team t ", // ANSI SQL 표준
            countQuery = "select count(*) from member " , // Native Query Count Query가 들어가야함
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
