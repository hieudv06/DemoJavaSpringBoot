package vn.java.repository;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.java.dto.response.PageResponse;

import java.util.List;

//@Component
@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;
   public PageResponse<?> getAllUsersWithSortByColumnsAndSearch(int pageNo, int pageSize, String search, String sortsBy){
       //1. query ra list user

       StringBuilder sqlQuery = new StringBuilder("select new vn.java.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone, u.dateOfBirth, u.gender, u.username, u.type, u.status) from User u where 1=1 ");
       // them ký tự vào câu sql
       if(StringUtils.hasLength(search)){
           sqlQuery.append(" and lower(u.firstName) like lower(:firstName)");
           sqlQuery.append(" or lower(u.lastName) like lower(:lastName)");
           sqlQuery.append(" or lower(u.email) like lower(:email)");
       }
       Query selectQuery = entityManager.createQuery(sqlQuery.toString());
       //phaan trang
       selectQuery.setFirstResult(pageNo);
       selectQuery.setMaxResults(pageSize);
       // tim kiem theo lastname, firtname, email
       if(StringUtils.hasLength(search)){
           selectQuery.setParameter("firstName",String.format("%%%s%%",search));
           selectQuery.setParameter("lastName",String.format("%%%s%%",search));
           selectQuery.setParameter("email",String.format("%%%s%%",search));
       }
       List user = selectQuery.getResultList();
       // 2 . query so record
       StringBuilder sqlCoutQuery = new StringBuilder("select count(*) from User u");
       // them ký tự vào câu sql
       if(StringUtils.hasLength(search)){
           sqlCoutQuery.append(" where lower(u.firstName) like lower(?1)");
           sqlCoutQuery.append(" or lower(u.lastName) like lower(?2)");
           sqlCoutQuery.append(" or lower(u.email) like lower(?3)");
       }
       Query selectCoutQuery = entityManager.createQuery(sqlCoutQuery.toString());
       if(StringUtils.hasLength(search)){
           selectCoutQuery.setParameter(1,String.format("%%%s%%",search));
           selectCoutQuery.setParameter(2,String.format("%%%s%%",search));
           selectCoutQuery.setParameter(3,String.format("%%%s%%",search));
       }
       Long totalElements = (Long) selectCoutQuery.getSingleResult();
       return PageResponse.builder()
               .pageNo(pageNo)
               .pageSize(pageSize)
               .totalPage(totalElements.intValue()/pageSize)
               .items(user)
               .build();
   };
}
