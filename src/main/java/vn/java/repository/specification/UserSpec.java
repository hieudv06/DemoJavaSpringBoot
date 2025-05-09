package vn.java.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.java.model.User;
import vn.java.util.Gender;

public class UserSpec {
    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%");
    }

    public static Specification<User> notEqualGender(Gender gender) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("gender"), Gender.MALE);
    }
//    public static Specification<User> notEqualGender(Gender gender) {
//        return new Specification<User>() {
//            @Override
//            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                return criteriaBuilder.notEqual(root.get("gender"), Gender.MALE);
//            }
//        };
//    }


}
