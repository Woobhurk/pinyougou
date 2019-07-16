package com.pinyougou.pay.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeiXinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {
    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;



    @Override
    public Map<String,String> createNative(String out_trade_no, String total_fee) {
        //组合参数集  存储到map中 转换成xml
        HashMap<String,String> param = new HashMap<>();
        param.put("appid", appid);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", out_trade_no);//商户订单号
        param.put("total_fee",total_fee);//总金额（分）
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", notifyurl);//异步通知结果的回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型扫码支付

        try {
            //把组合好的参数 和 财富通平台商户秘钥 一起生产一个xml
            //这个方法自动添加签名 并且转为字符串
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("组合好的参数和财富通平台商户秘钥 一起生产一个xml:"+xmlParam);
            //使用工具类调用接口发送请求 传入接口的地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true); //设置为https请求
            client.setXmlParam(xmlParam); //请求体
            client.post();
            //获取结果 是一个xml 转为map
            String resultXml = client.getContent();
            System.out.println("返回的结果"+resultXml);
            //把返回的结果转为map返回  这个map里有一些页面不需要的值
            Map<String, String> map = WXPayUtil.xmlToMap(resultXml);
            //页面只需要一个二维码和金额还有订单号 所以可以新建一个map把页面需要的东西返回即可
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("code_url", map.get("code_url"));//支付地址
            resultMap.put("total_fee", total_fee);//总金额
            resultMap.put("out_trade_no",out_trade_no);//订单号
            //返回map
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map param = new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        //回调地址
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        try {
            //自动添加签名
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            //获取返回的xml 所有数据都在里面
            String result = client.getContent();
            System.out.println("返回的结果"+result);
            //map直接返回
            Map<String, String>map = WXPayUtil.xmlToMap(result);
            System.out.println("xml结果转换成map:"+map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
