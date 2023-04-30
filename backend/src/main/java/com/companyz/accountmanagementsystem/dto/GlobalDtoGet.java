package com.companyz.accountmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalDtoGet {
    private String message;
    private String extra;
    public GlobalDtoGet(String message){
        this.message = message;
    }
}
