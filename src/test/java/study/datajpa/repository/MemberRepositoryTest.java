package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("스프링 데이터 JPA 기반 테스트")
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    @Rollback(false)
    public void test() {
        System.out.println("TEST = memberRepository = " + memberRepository.getClass());

        //given
        Member member = new Member("memberA");

        //when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @Rollback(false)
    @DisplayName("기본 CRUD 기능 테스트")
    public void basicCRUD() {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        //when
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member findMember1 = memberRepository.findById(savedMember1.getId()).get();
        Member findMember2 = memberRepository.findById(savedMember2.getId()).get();

        //then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //HINT : 하나의 Transaction 단위 내에서 엔티티 수정 시 dirty checking(변경 감지)가 발생한다
        findMember1.setUserName("member!!!!!!!!");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2); // 위에서 등록한 Member는 2명, size = 2 (success)

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThan() {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        //when
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);

        //then
        assertThat(result.get(0).getUserName()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
//        assertThat(result.get(0).getAge()).isEqualTo(10); // fail
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findTest() {
        System.out.println("================================================");
        List<Member> first = memberRepository.findFirstBy();
        List<Member> first3 = memberRepository.findFirst3By();
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();
        List<Member> top = memberRepository.findTopBy();
        System.out.println("================================================");
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUserName(member1.getUserName());
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUserNameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> userNameList = memberRepository.findUserNameList();
        for (String s : userNameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("AAA", 10, teamA);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("----> dto = " + dto);
        }
    }

    @Test
    public void findByNaems() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("CCC", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB", "CCC")); // in
        System.out.println("[TEST] result.size = " + result.size());
        for (Member member : result) {
            System.out.println("[TEST] member = " + member);
        }

        assertThat(result.get(0).getUserName()).isEqualTo("AAA");
        assertThat(result.get(1).getUserName()).isEqualTo("BBB");
        assertThat(result.get(2).getUserName()).isEqualTo("CCC");
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

//        List<Member> aaa = memberRepository.findListByUserName("AAA");
//        Member findMember = memberRepository.findMemberByUserName("AAA");
//        Optional<Member> findMember = memberRepository.findOptionalByUserName("AAA");
//        System.out.println("findMember = " + findMember);

//        List<Member> result = memberRepository.findListByUserName("asassasas");// Collection에 데이터가 없는 경우 => ? => 빈 컬렉션을 반환 해준다
//        System.out.println("result = "+ result.size());
//
//        Optional<Member> findMember = memberRepository.findOptionalByUserName("aasassas"); // 단건 데이터가 없는 경우 => ? => 없으면 null을 반환 한다
//        System.out.println("findMember = " + findMember);
//
//        Member findMemberTest = memberRepository.findMemberByUserName("asdasdasd");
//        System.out.println("findMemberTest = " + findMemberTest);

        Optional<Member> findMember = memberRepository.findOptionalByUserName("AAA"); // 단건 데이터가 아닌, 여러개의 데이터가 존재하는 경우 -> Exception 발생
        System.out.println("findMember = " + findMember);
    }

    @Test
    @DisplayName("스프링 데이터 JPA 페이징 테스트")
    public void paging() {
        //given
        createMemberForPaging(); // 데이터 생성

        //when : Page<T>
        int age = 10;
        int offset = 0;
        int limit = 9;
        PageRequest page = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "id"));

        Slice<Member> slice = memberRepository.findSliceByAge(age, page);
        System.out.println("slice = " + slice);

//        Page<Member> resultWithPaging = memberRepository.findPagingByAge(age, page);
        /*System.out.println("[TEST] resultWithPaging.getContent() = " + resultWithPaging.getContent()); // 페이징 처리에 의해 출력되는 데이터
        System.out.println("[TEST] resultWithPaging.getSize() = " + resultWithPaging.getSize()); // 페이징 처리에 의해 출력된 데이터의 사이즈
        System.out.println("[TEST] resultWithPaging.getTotalPages() = " + resultWithPaging.getTotalPages()); // 총 페이지 수
        System.out.println("[TEST] resultWithPaging.getTotalElements() = " + resultWithPaging.getTotalElements()); // 총 데이터 수
        System.out.println("[TEST] resultWithPaging.getNumber() = " + resultWithPaging.getNumber()); // 페이지 번호
        System.out.println("[TEST] resultWithPaging.isFirst() = " + resultWithPaging.isFirst()); // 첫번째 페이지 여부
        System.out.println("[TEST] resultWithPaging.hasNext() = " + resultWithPaging.hasNext()); // 다음 페이지 여부*/

        //then
//        assertThat(resultWithPaging.getContent()).isNotEmpty();
//        assertThat(resultWithPaging.getSize()).isEqualTo(9);
//        assertThat(resultWithPaging.getTotalPages()).isEqualTo(2);
//        assertThat(resultWithPaging.getTotalElements()).isEqualTo(13);
//        assertThat(resultWithPaging.getNumber()).isEqualTo(0); // JPA는 0부터 페이지 시작
//        assertThat(resultWithPaging.isFirst()).isTrue();
//        assertThat(resultWithPaging.hasNext()).isTrue();
    }

    private void createMemberForPaging() {
        Member m1 = new Member("ymkim", 10);
        Member m2 = new Member("youngsik", 10);
        Member m3 = new Member("soohyun", 10);
        Member m4 = new Member("mini", 10);
        Member m5 = new Member("jujung", 10);
        Member m6 = new Member("heshoo", 10);
        Member m7 = new Member("namil", 10);
        Member m8 = new Member("jaewoo", 10);
        Member m9 = new Member("ytjtjtj", 10);
        Member m10 = new Member("asdqwe", 10);
        Member m11 = new Member("asdqwe", 99); // fail
        Member m12 = new Member("asdqwe", -1); // fail
        Member m13 = new Member("asdqwe", 66); // fail

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);
        memberRepository.save(m6);
        memberRepository.save(m7);
        memberRepository.save(m8);
        memberRepository.save(m9);
        memberRepository.save(m10);
        memberRepository.save(m11);
        memberRepository.save(m12);
        memberRepository.save(m13);
    }

    @Test
    @Rollback(false)
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        Member member1 = memberRepository.save(new Member("member5", 40));

        //when
        int resultCnt = memberRepository.bulkAgePlus(20);

        //bulk 연산 이후 영속성 컨텍스트 초기화, 스프링 데이터 JPA @Modifying(clearAutomatically = true)
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUserName("member5");
        Member member2 = result.get(0);
        System.out.println("member1 = " + member1);
        System.out.println("member2 = " + member2);

        // 동일 트랜잭션 내에서 동일 객체 반환을 보장
        System.out.println("test = " + (member1 == member2));

        //then
        assertThat(resultCnt).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
//        Member member2 = new Member("member1", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N(2) + 1(한번)
        //select Member 1
//        List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findEntityGraphByUserName("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUserName());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 1차 캐시의 결과를 DB 동기화, 영속성 컨텍스트를 비우는 것이 아니다.
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUserName("member1"); // 100% 조회용으로만 사용할거다...
        findMember.setUserName("member2");

        em.flush(); // Dirty checking
    }

    @Test
    public void lock() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUserName("member1");
    }

    @Test
    public void callCustom() {
        //given
        List<Member> result = memberRepository.findMemberCustom();
        //when

        //then
    }

    @Test
    @DisplayName("persist, merge 테스트")
    @Rollback(false)
    public void saveEntityTest() {
        //given
        //별도의 id 값을 셋팅 하지 않았음
        Member member = new Member("김영민", 30);
        System.out.println("[TEST-01] member = " + member.getClass()); // class study.datajpa.entity.Member

        //when
        Member savedMember = memberRepository.save(member);
        System.out.println("[TEST-01] savedMember = " + savedMember.getClass()); // class study.datajpa.entity.Member

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        System.out.println("[TEST-01] findMember = " + findMember);

        //then
        assertThat(em.contains(member)).isTrue();
        assertThat(em.contains(savedMember)).isTrue();
        assertThat(member == savedMember);
        System.out.println("[TEST-01] member = " + member);
        System.out.println("[TEST-01] savedMember = " + savedMember);
        System.out.println("[TEST-01] member = savedMember = " + (member == savedMember) + "\n");

        //-----------------------------------------------------------

        //given
        Member member2 = new Member(member.getId(), "김정준", 29);
        System.out.println("[TEST-02] member2 = " + member.getClass());

        //when
        Member updatedMember = memberRepository.save(member2);
        System.out.println("[TEST-02] updatedMember = " + updatedMember.getClass());

        //then
        assertThat(em.contains(updatedMember)).isTrue();
        assertThat(em.contains(member2)).isFalse();
        assertThat(updatedMember == member2).isFalse(); // merge를 통해 나온 객체는 동일한 객체가 아니다
        System.out.println("updateMember == member2 = " + (updatedMember == member2));
    }

    @Test
    public void specBasic() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        
        em.flush();
        em.clear();
        
        //when
        Specification<Member> spec = MemberSpec.userName("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1"); // 검색 조건이 Member 엔티티가 된다
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);
        assertThat(result.get(0).getUserName()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // 인터페이스 기반의 Project 적용
        /*List<UserNameOnly> resultList = memberRepository.findProjectionsByUserName("m1");
        for (UserNameOnly result : resultList) {
            System.out.println("result = " + result);
        }*/

        // 클래스 기반의 Projection 생성 + 동적 Projection -> 제네릭
        /*List<UserNameOnlyDto> resultList = memberRepository.findProjectionsByUserName("m1", UserNameOnlyDto.class);
        for (UserNameOnlyDto result : resultList) {
            System.out.println("[>>>>>>>>>>>>>>>>] result = " + result.getUserName());
        }*/

        // 중첩 Projection - NestedClosedProjection
        List<NestedClosedProjection> resultList = memberRepository.findProjectionsByUserName("m1", NestedClosedProjection.class);
        for (NestedClosedProjection result : resultList) {
            System.out.println("[>>>>>>>>>>>>>>>>] result = " + result.getUserName());
            System.out.println("[>>>>>>>>>>>>>>>>] result = " + result.getTeam().getName());
        }
    }

    @Test
    public void nativeQuery() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // 01. 일반 Native Query
        Member member = memberRepository.findByNativeQuery("m1");
        System.out.println("member = " + member);

        // 02. Projection + Native Query + Paging
        /*Page<MemberProjection> member = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = member.getContent();
        for (MemberProjection m : content) {
            System.out.println("m = " + m.getUserName());
            System.out.println("m = " + m.getTeamName());
        }*/
    }
}