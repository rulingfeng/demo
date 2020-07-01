package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/5/26 19:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTest {

    private Integer id;
    private Integer age;
    private String name;
}
