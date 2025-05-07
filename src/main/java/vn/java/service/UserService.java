package vn.java.service;

import vn.java.dto.request.UserRequestDTO;
import vn.java.dto.response.PageResponse;
import vn.java.dto.response.UserDetailResponse;
import vn.java.util.UserStatus;

public interface UserService {

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);


    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);
    PageResponse<?> getAllUsersWithSortByColumnsAndSearch(int pageNo, int pageSize, String search, String sortBy);

}
