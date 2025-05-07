package vn.java.dto.response;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PageResponse<T> {

    private int pageNo;
    private int pageSize;
    private int totalPage;
    private T items;
}
