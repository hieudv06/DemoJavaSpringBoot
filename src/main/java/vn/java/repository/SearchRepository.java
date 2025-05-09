package vn.java.repository;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.java.dto.response.PageResponse;
import vn.java.model.Address;
import vn.java.model.User;
import vn.java.repository.criteria.SearchCriteria;
import vn.java.repository.criteria.UserSearchCriteriaQueryConsumer;
import vn.java.repository.specification.SpecSearchCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static vn.java.repository.specification.SearchOperation.*;

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

   public PageResponse advanceSearchByCriteria (int pageNo, int pageSize, String sortBy, String address, String... search){
       //loc   phần tử truyền vao ,viết biểu thức tách các cột
       // firstName:T, lastName:T,

       List<SearchCriteria> criteriaList = new ArrayList<>();
       //1 .lấy ra danh sách user
       if(search !=null){
           for (String s : search) {
               // firstName:value
               Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(.*)");
               Matcher matcher = pattern.matcher(s);
               if (matcher.find()) {
                   criteriaList.add(new SearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3)));
               }
           }

       }

            // 2. Lấy ra số lượng bản ghi và phân trang
       List<User> user =  getUser(pageNo,pageSize,criteriaList,sortBy,address);
       Long totalElements = getTotalElements(criteriaList,address);

       return PageResponse.builder()
               .pageNo(pageNo) //offset = vi tri cua ban ghi trong danh sach
               .pageSize(pageSize) //
               .totalPage(totalElements.intValue()) //total elements
               .items(user)
               .build();
   }


    private List<User> getUser(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy,String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query =criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        // Xu ly cac dieu kien tim kiem
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer  queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,predicate,root);
        // them tim kiem - tong hop them addresses
        if(StringUtils.hasLength(address)){
            Join<Address,User> addressUserJoin =root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            //tim kiem tren tat ca cac field cua address?
            query.where(predicate,addressPredicate);

        }else {
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            query.where(predicate);
        }
        //sort
        if(StringUtils.hasLength(sortBy)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("desc")){
                    query.orderBy(criteriaBuilder.desc(root.get(columnName)));
                }else{
                    query.orderBy(criteriaBuilder.asc(root.get(columnName)));
                }

            }

        }


      return  entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();

    }
    private Long getTotalElements(List<SearchCriteria> criteriaList, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query =criteriaBuilder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        // Xu ly cac dieu kien tim kiem
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer  queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,predicate,root);
        // them tim kiem - tong hop them addresses
        if(StringUtils.hasLength(address)){
            Join<Address,User> addressUserJoin =root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            //tim kiem tren tat ca cac field cua address?
            query.select(criteriaBuilder.count(root));
            query.where(predicate,addressPredicate);
        }else {
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            query.select(criteriaBuilder.count(root));
            query.where(predicate);
        }

        return  entityManager.createQuery(query).getSingleResult();

    }


    public PageResponse getUserJoinedAddress(Pageable pageable, String [] user,String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query =criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);
        Join<Address,User> addressRoot =userRoot.join("addresses");

        // Build query

        List<Predicate> userPre =new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        for(String s : user){
            Pattern pattern =Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            Matcher matcher =pattern.matcher(s);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate =toUserPredicate(userRoot,criteriaBuilder,criteria);
                userPre.add(predicate);

            }
        }

        for(String addr : address){
            Pattern pattern =Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            Matcher matcher =pattern.matcher(addr);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate =toAddressPredicate(addressRoot,criteriaBuilder,criteria);
                addressPre.add(predicate);
            }
        }

        Predicate usePredicateArr = criteriaBuilder.or(userPre.toArray(new Predicate[0]));
        Predicate addressPredicateArr = criteriaBuilder.or(addressPre.toArray(new Predicate[0]));
        Predicate finalPre =criteriaBuilder.and(usePredicateArr,addressPredicateArr);

        query.where(finalPre);

      List<User> users =  entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

      Long count =countUser(user,address);

        return  PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(1000)
                .items(users)
                .build();

    }


    private Long countUser (String[] user, String[] address){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query =criteriaBuilder.createQuery(Long.class);
        Root<User> userRoot = query.from(User.class);
        Join<Address,User> addressRoot =userRoot.join("addresses");

        // Build query

        List<Predicate> userPre =new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        for(String s : user){
            Pattern pattern =Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            Matcher matcher =pattern.matcher(s);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate =toUserPredicate(userRoot,criteriaBuilder,criteria);
                userPre.add(predicate);

            }
        }

        for(String addr : address){
            Pattern pattern =Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            Matcher matcher =pattern.matcher(addr);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate =toAddressPredicate(addressRoot,criteriaBuilder,criteria);
                addressPre.add(predicate);
            }
        }

        Predicate usePredicateArr = criteriaBuilder.or(userPre.toArray(new Predicate[0]));
        Predicate addressPredicateArr = criteriaBuilder.or(addressPre.toArray(new Predicate[0]));
        Predicate finalPre =criteriaBuilder.and(usePredicateArr,addressPredicateArr);


        query.select(criteriaBuilder.count(userRoot));
        query.where(finalPre);

        return entityManager.createQuery(query).getSingleResult();
    }

    private Predicate toUserPredicate(Root<User> root,CriteriaBuilder builder,SpecSearchCriteria criteria) {

        return switch (criteria.getOperation()){
            case EQUALITY -> builder.equal(root.get(criteria.getKey()),criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()),criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()),criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()),criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()),criteria.getValue().toString() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()),"%" + criteria.getValue().toString());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        };
    }

    private Predicate toAddressPredicate(Join<Address,User> root,CriteriaBuilder builder,SpecSearchCriteria criteria) {

        return switch (criteria.getOperation()){
            case EQUALITY -> builder.equal(root.get(criteria.getKey()),criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()),criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()),criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()),criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()),criteria.getValue().toString() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()),"%" + criteria.getValue().toString());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        };
    }

}
