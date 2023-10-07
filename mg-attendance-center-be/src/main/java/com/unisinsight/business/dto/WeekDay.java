package com.unisinsight.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @ClassName : mg-attendance-center
 * @Description :
 * @Author : xiehb
 * @Date: 2022/11/04 11:33
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekDay {
    private LocalDate monday;
    private LocalDate sunday;
}
