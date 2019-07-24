package com.pinyougou.pay.service;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface WeiXinPayService {
    /**
     * 调用统一下单api 发送请求给支付系统 获取支付的结果(二维码连接)
     * @param out_trade_no 支付订单号
     * @param total_fee 支付金额(分)
     * @return
     */
    Map<String,String> createNative(String out_trade_no, String total_fee);


    /**
     * 查询支付状态
     * @param out_trade_no
     */
      Map queryPayStatus(String out_trade_no);


    Map<String, String> closePay(String out_trade_no);
}
