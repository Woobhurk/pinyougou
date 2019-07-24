package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.apache.ibatis.mapping.ResultMap;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付控制层
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeiXinPayService weixinPayService;


    @Reference
    private SeckillOrderService orderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前用户的id
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //从redis中获取预订单的金额 和支付订单号
        TbSeckillOrder order = orderService.getUserOrderStatus(userId);

        if (order != null) {
            double v = order.getMoney().doubleValue() * 100;
            long x = (long) v;
            return weixinPayService.createNative(order.getId() + "", x + "");
        }
        return null;

    }


    /**
     * 查询订单号的支付状态,
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        //拿到UserId
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = new Result(false, "支付失败");
        //定义一个计数器 规定超时时间
        int count = 0;
        //轮询查询 要给一个出口
        while (true) {
            //调用查询接口 根据返回的xml 判断支付状态
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);

            count++;

            if (count >= 5) {
                result = new Result(false, "支付超时");

                //3.关闭微信支付订单
                Map<String, String> resultMap = weixinPayService.closePay(out_trade_no);
                if ("SUCCESS".equals(resultMap.get("result_code"))) {
                    //关闭微信订单成功 删除订单 恢复库存
                    //删除订单
                    orderService.deleteOrder(userId);

                } else if ("ORDERPAID".equals(resultMap.get("err_code"))) {
                    //如果已经支付成功
                    orderService.updateOrderStatus(map.get("transaction_id"), userId);

                } else {

                    System.out.println("由于微信端错误");
                }

                break;
            }


            if (map == null) {//出错
                result = new Result(false, "支付失败");
                break;
            }

            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                result = new Result(true, "支付成功");
                //根据微信支付订单id 修改数据库状态
                // orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
                /**
                 * 更新订单的状态 (status 支付时间)支付成功的时候执行
                 * @param transaction_id  交易订单号
                 * @param userId 支付的用户的ID
                 */
                orderService.updateOrderStatus(map.get("transaction_id"), userId);
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒查询一次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
