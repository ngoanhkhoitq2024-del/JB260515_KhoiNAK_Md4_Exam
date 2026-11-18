
package re.edu.studentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import re.edu.studentservice.client.CourseClient;
import re.edu.studentservice.client.CourseResponse;
import re.edu.studentservice.dto.EnrollRequest;
import re.edu.studentservice.entity.Student;
import re.edu.studentservice.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseClient courseClient;

    // Create student
    public Student createStudent(Student student) {

        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        if (student.getName() == null || student.getName().isBlank()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }

        if (student.getEmail() == null || student.getEmail().isBlank()) {
            throw new IllegalArgumentException("Student email cannot be empty");
        }

        String email = student.getEmail().trim().toLowerCase();

        if (studentRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        student.setId(null);
        student.setName(student.getName().trim());
        student.setEmail(email);

        return studentRepository.save(student);
    }

    // Get all students
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by ID
    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid student ID");
        }

        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found with id: " + id
                ));
    }

    // Enroll student in a course
    public Student enrollStudent(EnrollRequest request) {

        if (request == null ||
                request.getStudentId() == null ||
                request.getCourseId() == null) {
            throw new IllegalArgumentException(
                    "Student ID and Course ID are required"
            );
        }

        // 1. Find student
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found"
                ));

        // 2. Check duplicate enrollment
        if (student.getEnrolledCourses().contains(request.getCourseId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student has already enrolled in this course"
            );
        }

        // 3. Get course information through OpenFeign
        CourseResponse course = courseClient.getCourseById(
                request.getCourseId()
        );

        // 4. Check course capacity
        if (course.getCapacity() == null || course.getCapacity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Course is full"
            );
        }

        // 5. Update course enrollment count
        courseClient.enrollCourse(request.getCourseId());

        // 6. Update student's enrolled courses
        student.getEnrolledCourses().add(request.getCourseId());

        return studentRepository.save(student);
    }
}
