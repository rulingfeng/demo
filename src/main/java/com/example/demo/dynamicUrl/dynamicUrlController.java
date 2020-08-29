package com.example.demo.dynamicUrl;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dynamicUrl")
@Slf4j
public class dynamicUrlController {

    /**
     * 获取动态url的md5秘钥
     * @param seckillGoodsId
     * @return
     */
    @GetMapping(value = "/{goodsId}/getUrl")
    public Exposer exposer(@PathVariable("goodsId") Long seckillGoodsId){
        //goodsId表示是什么商品，然后根据该商品的数据库依次获得尚未被秒杀的每个商品的唯一ID，然后根据商品的唯一ID来生成唯一的秒杀URL
        //seckillGoodsId为某个商品的唯一id
        //其中这一步可以省，之间将goodsId表示的传递给exportSeckillUrl也可以完成
        //异常判断省掉，返回上述的model对象。即包含md5的对象
        Exposer result = this.exportSeckillUrl(seckillGoodsId);
        return result;
    }
    private final String salt="12sadasadsafafsafs。/。，";

    public Exposer exportSeckillUrl(long seckillGoodsId) {
        //首页根据该seckillGoodsId判断商品是否还存在。
        //如果不存在则表示已经被秒杀
        String md5 = getMD5(seckillGoodsId);
        return  new Exposer(md5);
    }

    private String getMD5(long seckillGoodsId){
        //结合秒杀的商品id与混淆字符串生成通过md5加密
        String base=seckillGoodsId+"/"+salt;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }


    /**
     * 下单接口
     * @param seckillGoodsId
     * @param md5
     * @return
     */
    @GetMapping(value = "/{seckillGoodsId}/{md5}/execution")
    public Boolean execution(@PathVariable("seckillGoodsId") Long seckillGoodsId,@PathVariable("md5") String md5){
        Boolean result = this.executionSeckillId(seckillGoodsId,md5);
        //executionSeckillId的操作是强事务,操作为减库存+增加购买明细，最终返回是否秒杀成功，秒杀成功的商品消息等。这里省，只返回是否接口合理的信息。
        return  result;
    }

    public Boolean executionSeckillId(long seckillID,String md5){
        if(md5==null||!md5.equals(getMD5(seckillID))){
            //表示接口错误，不会执行秒杀操作
            return false;
        }
        //接口正确，排队执行秒杀操作。减库存+增加购买明细等信息，这里只返回false
        return  true;
    }

}
