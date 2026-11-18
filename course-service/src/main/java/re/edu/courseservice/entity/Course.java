package re.edu.courseservice.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("courseName")
    @JsonAlias("name")
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "enrolled_courses", nullable = false)
    private Integer enrolledCount = 0;

    @Column(nullable = false)
    private Integer credits;
}
