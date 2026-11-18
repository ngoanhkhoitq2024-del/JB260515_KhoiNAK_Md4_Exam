package re.edu.courseservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import re.edu.courseservice.entity.Course;
import re.edu.courseservice.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public Course createCourse(Course course) {
        if (course.getCourseName() == null || course.getCourseName().isEmpty()) {
            throw new IllegalArgumentException("Course name can not empty");
        }
        if (course.getCapacity() == null || course.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greated than 0");
        }
        if (course.getCredits() == null || course.getCredits() <= 0) {
            throw new IllegalArgumentException("Credits must be greated than 0");
        }
        course.setId(null);
        course.setEnrolledCount(0);
        return courseRepository.save(course);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found with id: " + id));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional
    public void enrollCourse(Long courseId) {
        Course course = getCourseById(courseId);
        if (course.getEnrolledCount() >= course.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course is full");
        }
        int updatedRows = courseRepository.enrollIfAvaiable(courseId);
        if (updatedRows == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course is full or unavailable");

        }
    }
}
