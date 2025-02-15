package ua.edu.chdtu.deanoffice.api.grade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.grade.dto.GradeDTO;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/grades")
public class GradeController {
    private GradeService gradeService;
    private StudentDegreeService studentDegreeService;
    private CourseForGroupService courseForGroupService;

    @Autowired
    public GradeController(
            GradeService gradeService,
            StudentDegreeService studentDegreeService,
            CourseForGroupService courseForGroupService
    ) {
        this.gradeService = gradeService;
        this.studentDegreeService = studentDegreeService;
        this.courseForGroupService = courseForGroupService;
    }

    @PutMapping
    public ResponseEntity putGrades(@RequestBody List<Grade> grades) {
        try {
            this.gradeService.insertGrades(gradeService.setGradeAndEcts(grades));
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PutMapping("/academic-difference")
    public ResponseEntity putAcademicDifference(@RequestBody Map<Boolean, List<Integer>> gradesIds){
        if (gradesIds == null || gradesIds.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Жоден атрибут академічної різниці не було змінено");
        }
        try {
            gradeService.setAcademicDifferenceByGradeIds(gradesIds);
            return ResponseEntity.ok().build();
        } catch (Exception exception){
            return handleException(exception);
        }
    }

    @GetMapping("/{groupId}")
    public ResponseEntity getGrades(
            @PathVariable Integer groupId,
            @RequestParam(value = "semester") Integer semester) {
        try {
            List<Grade> grades = this.gradeService.getGradesForStudents(getStudentsIdsByGroupId(groupId),
                    getCoursesIdsByGroupIdAndSemester(groupId, semester));
            return ResponseEntity.ok(map(grades, GradeDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private List<Integer> getStudentsIdsByGroupId(Integer groupId) {
        List<StudentDegree> students = this.studentDegreeService.getAllByGroupId(groupId);
        return students.stream().map(BaseEntity::getId).collect(Collectors.toList());
    }

    private List<Integer> getCoursesIdsByGroupIdAndSemester(Integer groupId, Integer semester) {
        List<CourseForGroup> courses = this.courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
        return courses.stream().map(course -> course.getCourse().getId()).collect(Collectors.toList());
    }

    @GetMapping("/{groupId}/{courseId}")
    public ResponseEntity getGradesByGroupIdAndCourseId(
            @PathVariable Integer groupId,
            @PathVariable Integer courseId) {
        try {
            List<Grade> grades = this.gradeService.getGradesByCourseAndGroup(courseId, groupId);
            return ResponseEntity.ok(map(grades, GradeDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteGrades(@RequestParam(value = "gradeId") Integer gradeId) {
        try {
            this.gradeService.deleteGradeById(gradeId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GradeController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
