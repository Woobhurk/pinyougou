package com.pinyougou.seckill.task;


import com.pinyougou.common.constant.Constant;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;


@Component
public class GoodsTask {
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //每隔30秒执行一次 把数据库的数据推送到redis中
    //使用计划注解利用cron表达式
    // 每隔5秒执行一次
    @Scheduled(cron = "0/5 * * * * ?")
    public void pushGoods(){
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");//审核状态通过的才有意义
        criteria.andGreaterThan("stockCount",0);//剩余库存大于0
        //当前的时间大于开始时间 小于结束时间 才属于在活动时间内
        Date date = new Date();
        criteria.andLessThan("startTime",date);
        criteria.andGreaterThan("endTime",date);

        //排除 已经在redis中的商品
        //拿出redis中所有key的集合
        Set<Long> keys = redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).keys();

        if(keys!=null && keys.size()>0){
            System.out.println("我已经排除了");
            //加入排除条件 排除redis中已有的key
            //select * from where id not in (1,3,12..)
            criteria.andNotIn("id",keys);
        }
        List<TbSeckillGoods> goods = seckillGoodsMapper.selectByExample(example);
        //全部存储到redis中
        for (TbSeckillGoods good : goods) {
            pushGoodsList(good);
            redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).put(good.getId(),good);
        }
        System.out.println(new Date());
    }

    /**
     * 一个队列就是一种商品
     * 队列长度就是商品库存数
     * @param goods
     */
    public void pushGoodsList(TbSeckillGoods goods){
        //向同一个队列中压入商品数据
        for (Integer i = 0; i < goods.getStockCount(); i++) {
            //库存为多少就是多少个SIZE 值就是id即可
            //从左边推进队列
            redisTemplate.boundListOps(Constant.SEC_KILL_GOODS_PREFIX+goods.getId()).leftPush(goods.getId());
        }
    }
}
