package re.edu.studentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import re.edu.studentservice.entity.Student;
import re.edu.studentservice.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public Student createStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student can not be null");
        }
        if (student.getName() == null || student.getName().isEmpty()) {
            throw new IllegalArgumentException("Student name can not empty");
        }
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            throw new IllegalArgumentException("Student email can not empty");
        }
        String email = student.getEmail().trim().toLowerCase();
        if (studentRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        student.setId(null);
        student.setName(student.getName().trim());
        student.setEmail(email);

        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStuents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStuentById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Student id must be greater than 0");
        }

        return studentRepository.findById(id).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + id));
    }
}
