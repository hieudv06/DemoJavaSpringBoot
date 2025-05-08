package vn.java.repository;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.java.dto.response.PageResponse;
import vn.java.repository.criteria.SearchCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Component
@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;
   public PageResponse<?> getAllUsersWithSortByColumnsAndSearch(int pageNo, int pageSize, String search, String sortBy){
       //1. query ra list user

       StringBuilder sqlQuery = new StringBuilder("select new vn.java.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone,u.dateOfBirth ,u.gender, u.type, u.status) from User u where 1=1 ");
       // them ký tự vào câu sql
       if(StringUtils.hasLength(search)){
           sqlQuery.append(" and lower(u.firstName) like lower(:firstName)");
           sqlQuery.append(" or lower(u.lastName) like lower(:lastName)");
           sqlQuery.append(" or lower(u.email) like lower(:email)");
       }

       //. SortBy thì viết thêm vào câu lệnh sql
       if(StringUtils.hasLength(sortBy)){
           Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
           Matcher matcher = pattern.matcher(sortBy);
           if (matcher.find()) {
               sqlQuery.append(String.format(" order by u.%s %s" ,matcher.group(1),matcher.group(3)));
           }

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

       Pageable pageable = PageRequest.of(pageNo,pageSize);

       Page<?> page = new PageImpl<>(user,pageable,totalElements);

       return PageResponse.builder()
               .pageNo(pageNo)
               .pageSize(pageSize)
               .totalPage(page.getTotalPages())
               .items(user)
               .build();
   };

   public PageResponse advanceSearchByCriteria (int pageNo, int pageSize, String sortBy, String... search){
       //loc   phần tử truyền vao ,viết biểu thức tách các cột
       // firstName:T, lastName:T,

       List<SearchCriteria> criteriaList = new ArrayList<>();
       if(search !=null){
           for (String sear : search) {
               // firstName:value
               Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(.*)");
               Matcher matcher = pattern.matcher(sortBy);
               if (matcher.find()) {
                   criteriaList.add(new SearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3)));
                   //todo
               }
           }

       }



       //1 .lấy ra danh sách user


            // 2. Lấy ra số lượng bản ghi và phân trang

       return PageResponse.builder()
               .pageNo(pageNo)
               .pageSize(pageSize)
               .totalPage(0)
               .items(null)
               .build();
   }
}
