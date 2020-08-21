package com.example.demo.mapstruct;

import com.example.demo.model.User;
import com.example.demo.model.UserCar;
import com.example.demo.model.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author RU
 * @date 2020/8/21
 * @Desc
 */
@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    //constant默认写死的值 (自定义常量)
    //expression类型不一致的映射
    @Mappings({@Mapping(constant = "改名",target = "name")
            ,@Mapping(source = "age",target = "age1")
            ,@Mapping(target = "brithday",dateFormat = "yyyy-MM-dd HH:mm:ss")
            ,@Mapping(target = "type",expression = "java(typeToUserCar(user.getType(),user.getId()))")})
    UserVo userToUserVo(User user);

    default UserCar typeToUserCar(String type,Integer id){
        UserCar userCar = new UserCar();
        userCar.setId(Integer.valueOf(type));
        return userCar;
    }
}
