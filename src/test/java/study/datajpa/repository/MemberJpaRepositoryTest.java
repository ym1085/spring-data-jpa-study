package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("순수 JPA 기반 테스트 진행")
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("맴버 저장 후 조회 테스트")
    public void testMember() throws Exception {
        //given
        Member member = new Member("memberA");

        //when
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member); // 동일한 하나의 Transaction 단위에서 JPA는 동일한 객체 반환을 보장 한다, 1차 캐시
    }

    @Test
    @Rollback(false)
    @DisplayName("기본 CRUD 기능 테스트")
    public void basicCRUD() throws Exception {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        //when
        Member savedMember1 = memberJpaRepository.save(member1);
        Member savedMember2 = memberJpaRepository.save(member2);
        Member findMember1 = memberJpaRepository.findById(savedMember1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(savedMember2.getId()).get();

        //then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //HINT : 하나의 Transaction 단위 내에서 엔티티 수정 시 dirty checking(변경 감지)가 발생한다
        findMember1.setUserName("member!!!!!!!!");

        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2); // 위에서 등록한 Member는 2명, size = 2 (success)

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThan() {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        //when
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

//        List<Member> result = memberJpaRepository.findByUserNameAndAgeGreaterThan("AAA", 15);

        //then
//        assertThat(result.get(0).getUserName()).isEqualTo("AAA");
//        assertThat(result.get(0).getAge()).isEqualTo(20);
////        assertThat(result.get(0).getAge()).isEqualTo(10); // fail
//        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberJpaRepository.findByUserName(member1.getUserName());
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void paging() {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 1;
        int limit = 3;

        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        // 스프링 데이터 JPA에 페이징 관련 객체가 존재함
        // 페이지 계산 공식 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 ..

        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    @Rollback(false)
    public void bulkUpdate() {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member1", 19));
        memberJpaRepository.save(new Member("member1", 20));
        memberJpaRepository.save(new Member("member1", 21));
        memberJpaRepository.save(new Member("member1", 40));

        //when
        int resultCnt = memberJpaRepository.bulkAgePlus(20);

        //then
        assertThat(resultCnt).isEqualTo(3);
    }
}