package com.example.demo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 微信公众号消息模板
 * @Author: RU
 * @Date: 2020/4/22 17:30
 */
@Data
@Accessors(chain = true)
public class WXTemplateMsgDto implements Serializable {
    private static final long serialVersionUID = -5076111199734779124L;

    //opneid
    private String touser;
    //模板id
    private String template_id;
    //模板跳转链接
    private String url;

    private MiniprogramDto miniprogram;

    private TempDataDto data;

    @Data
    @Accessors(chain = true)
    public static class MiniprogramDto implements Serializable{
        private static final long serialVersionUID = -5076111156479124L;
        //appid
        private String appid;
        //所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），要求该小程序已发布，暂不支持小游戏
        private String pagepath;
    }

    @Data
    @Accessors(chain = true)
    public static class TempDataDto implements Serializable{
        private static final long serialVersionUID = -50761154649124L;
        private TempDataInfoDto first;
        private TempDataInfoDto keyword1;
        private TempDataInfoDto keyword2;
        private TempDataInfoDto keyword3;
        private TempDataInfoDto keyword4;
        private TempDataInfoDto remark;

        @Data
        @Accessors(chain = true)
        public static class TempDataInfoDto implements Serializable{
            private static final long serialVersionUID = -507611123156124L;
            private String value;
            private String color;
        }
    }


}
