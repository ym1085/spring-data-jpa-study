package study.datajpa.repository;

import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    // 저장
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    // 삭제
    public void delete(Member member) {
        em.remove(member);
    }

    // 전체 조회
    public List<Member> findAll() {
        // em.findAll(); -> 순수 JPA 만으로는 불가능하다, JPQL을 사용해서 값을 출력해야 함
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    // 단건 조회
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    // 카운팅
    public long count() {
        // 단건인 경우에는 getSingleResult()를 사용하면 된다
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
