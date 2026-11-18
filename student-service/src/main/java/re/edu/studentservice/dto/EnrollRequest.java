package re.edu.studentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollRequest {

    private Long studentId;

    private Long courseId;
}
