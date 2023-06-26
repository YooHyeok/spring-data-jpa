package study.datajpa.repository;

/**
 * Projection 중첩구조처리
 */
public interface NestedClosedProjection {
    String getUsername();

    TeamInfo getTeam(); //내부 인터페이스

    interface TeamInfo {
        String getName();
    }
}
