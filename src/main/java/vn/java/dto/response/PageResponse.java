package vn.java.dto.response;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PageResponse<T> {

    private int page;
    private int size;
    private int total;
    private T items;
}
