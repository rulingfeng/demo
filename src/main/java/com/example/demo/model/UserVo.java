package com.example.demo.model;

import lombok.Data;
import org.elasticsearch.client.license.LicensesStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/5/14 11:02
 */
@Data
public class UserVo extends User {
    private String age;

    public void a(){
        System.out.println(this.id);
    }

    @Override
    public String toString() {
        return id +"-"+ userName + "-" + age;
    }

    public static void main(String[] args)  {

    }
}
