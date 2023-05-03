package com.companyz.accountmanagementsystem.dto.userdto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedUserResponse {
    private List<GetUserDto> users;
    private Long numberOfItems;
    private int numberOfPages;
}
