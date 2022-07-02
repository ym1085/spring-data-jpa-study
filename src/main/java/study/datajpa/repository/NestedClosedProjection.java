package study.datajpa.repository;

public interface NestedClosedProjection {

    String getUserName();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
