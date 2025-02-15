package ua.edu.chdtu.deanoffice.service.document.report.journal;


import org.springframework.data.jpa.domain.Specification;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class StudentGroupSpecification {
    static Specification<StudentGroup> getStudentGroupsWithImportFilters(
            int degreeId, int currentYear, int year, TuitionForm tuitionForm, int facultyId, int groupId) {
        return (Root<StudentGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (groupId != 0) {
                predicates.add(cb.equal(root.get("id"), groupId));
                return cb.and(predicates.toArray(new Predicate[0]));
            }
            predicates.add(cb.equal(root.get("specialization").get("degree").get("id"), degreeId));
            predicates.add(cb.equal(cb.sum(cb.diff(currentYear, root.get("creationYear")), root.get("beginYears")), year));
            predicates.add(cb.equal(root.get("specialization").get("faculty").get("id"), facultyId));
            if (tuitionForm != null)
                predicates.add(cb.equal(root.get("tuitionForm"), tuitionForm));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}