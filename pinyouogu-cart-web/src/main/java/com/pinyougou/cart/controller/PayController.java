package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付控制层
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeiXinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    /**
     * 生成二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
        //redis中有日志才生成二维码
        if (payLog!=null){
            ////生成一个支付订单号,每一次支付的订单号 和商品订单是不一样的
            //String out_trade_no=new IdWorker( ).nextId()+"";
            ////获取商品订单金额 单位是分
            //String total_fee="1";
            ////调用服务 统一下单的api
            return weixinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        }
        return null;

    }






    /**
     * 查询订单号的支付状态,
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        //定义一个计数器 规定超时时间
        int count = 0;
        //轮询查询 要给一个出口
        while (true) {
            //调用查询接口 根据返回的xml 判断支付状态
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);

            count++;

            if (count>=20) {
                result = new Result(false,"支付超时");
                break;
            }


            if (map == null) {//出错
                result = new Result(false, "支付失败");
                break;
            }

            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                result = new Result(true, "支付成功");
                //根据微信支付订单id 修改数据库状态
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
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
