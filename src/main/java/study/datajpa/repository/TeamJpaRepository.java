package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {

    @PersistenceContext
    private EntityManager em; // JPA EntityManger

    // 저장
    public Team saveTeam(Team team) {
        em.persist(team);
        return team;
    }

    // 삭제
    public void delete(Team team) {
        em.remove(team);
    }

    // 전체 조회
    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class).getResultList();
    }

    // 단건 조회
    public Optional<Team> findById(int id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    // 카운팅
    public long count() {
        return em.createQuery("select t from Team t", Long.class).getSingleResult();
    }
}
