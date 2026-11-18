package re.edu.courseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import re.edu.courseservice.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {
    @Modifying
            (clearAutomatically = true,
            flushAutomatically = true)
    @Query("""
        UPDATE Course c
        SET c.enrolledCount = c.enrolledCount + 1
        WHERE c.id = :courseId AND c.enrolledCount < c.capacity
""")
    int enrollIfAvaiable(@Param("coureId") Long courseId);
}
