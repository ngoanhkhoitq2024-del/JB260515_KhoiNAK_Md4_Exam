package re.edu.studentservice.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseResponse {

    private Long id;
    private String courseName;
    private Integer capacity;
    private Integer enrolledCount;
    private Integer credits;
}

