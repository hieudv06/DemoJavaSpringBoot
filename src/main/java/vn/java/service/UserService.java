package vn.java.service;

import jakarta.mail.MessagingException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import vn.java.dto.request.UserRequestDTO;
import vn.java.dto.response.PageResponse;
import vn.java.dto.response.UserDetailResponse;
import vn.java.util.UserStatus;

import java.io.UnsupportedEncodingException;

public interface UserService {

    UserDetailsService userDetailService();

    long saveUser(UserRequestDTO request) throws MessagingException, UnsupportedEncodingException;

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);


    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);
    PageResponse<?> getAllUsersWithSortByColumnsAndSearch(int pageNo, int pageSize, String search, String sortBy);
    PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy,String address, String ...search);

    PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] user, String[] address);

    void confirmUser(int userId, String secretCode);
}
