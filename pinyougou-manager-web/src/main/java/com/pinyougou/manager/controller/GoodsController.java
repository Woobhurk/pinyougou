package com.pinyougou.manager.controller;

import java.util.List;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品controller
 *
 * @author Administrator
 */

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Autowired
    private DefaultMQProducer producer;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return this.goodsService.findAll();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return this.goodsService.findPage(pageNo, pageSize);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //获取用户名 商家id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            this.goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            this.goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public Goods findOne(@PathVariable(value = "id") Long id) {
        return this.goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            this.goodsService.delete(ids);

            MessageInfo messageInfo = new MessageInfo("Goods_Topic", "goods_delete_tag", "delete",
                ids, MessageInfo.METHOD_DELETE);

            this.producer.send(
                new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(),
                    JSON.toJSONString(messageInfo).getBytes()));


            ////调用搜索服务 执行 删除ES数据
            //itemSearchService.deleteByIds(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/search")
    public PageInfo<TbGoods> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
        @RequestBody TbGoods goods) {

        return this.goodsService.findPage(pageNo, pageSize, goods);

    }


    @RequestMapping("/updateStatus/{status}")
    public Result updateStatus(@RequestBody Long[] ids,
        @PathVariable(value = "status") String status) {

        try {
            this.goodsService.updateStatus(ids, status);
            //调用搜索服务的方法 实现同步 更新
            //把更新的数据查出来  然后把更新的数据存入es中
            //重点就是要看审核的到底是哪个表
            if (status.equals("1")) {
                //审核通过
                //1.根据审核的SPU的ID获取SKU的列表数据 因为ES的数据就是sku的数据
                List<TbItem> tbItemList = this.goodsService.findTbItemListByIds(ids);

                ////2.调用搜索服务的方法(传递SKU的列表数据过去) 内部执行更新的动作
                //itemSearchService.updateIndex(tbItemList);
                //2.将消息体发送到mq服务器上
                MessageInfo messageInfo = new MessageInfo(
                    "Goods_topic",
                    "goods_update_tag",
                    "updateStatus",
                    tbItemList,
                    MessageInfo.METHOD_UPDATE);
                SendResult send = this.producer.send(new Message(
                    messageInfo.getTopic(),
                    messageInfo.getTags(),
                    messageInfo.getKeys(),
                    //需要转为字节数组 所以选把对象转为字符串
                    JSON.toJSONString(messageInfo).getBytes()), 10000);

                System.out.println(">>>>" + send.getSendStatus());

                ////3.生成静态页面
                //for (Long id:ids){
                //    itemPageService.genItemHtml(id);
                //}
            }

            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }


}
