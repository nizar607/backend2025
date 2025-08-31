package com.example.stage24.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private Long total;
    private Long banned;
    private Long permitted;
}