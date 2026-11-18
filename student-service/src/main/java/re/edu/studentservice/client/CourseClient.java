package re.edu.studentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "COURSE-SERVICE")
public interface CourseClient {

    @GetMapping("/api/courses/{id}")
    CourseResponse getCourseById(@PathVariable("id") Long id);

    @PutMapping("/api/courses/{id}/enroll")
    void enrollCourse(@PathVariable("id") Long id);
}
