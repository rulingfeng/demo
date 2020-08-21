package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class UserVo  {


    private Integer id;

    private String name;

    private String age1;

    private UserCar type;

    private Date brithday;



    @Override
    public String toString() {
        return id +"-"+ name + "-" + age1;
    }

    public static void main(String[] args)  {

    }
}
