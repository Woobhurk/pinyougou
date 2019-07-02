package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
        super(goodsMapper, TbGoods.class);
        this.goodsMapper = goodsMapper;
    }


    /**
     *  更新
     * @param goods
     * @return
     */
    @Override
    public void update(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
        //商家修改就要进行审核才能发布所以每次修改都要进行状态重置
        tbGoods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(tbGoods);
        TbGoodsDesc tbGoodsDesc = goods.getGoodsDesc();
        goodsDescMapper.updateByPrimaryKey(tbGoodsDesc);
        //sku表需要先删除 在添加  如果直接更新会造成脏数据的问题
        //Example example = new Example(TbItem.class);
        //Example.Criteria criteria = example.createCriteria();
        //criteria.andEqualTo("goodId",tbGoods.getId());
        //itemMapper.deleteByExample(criteria);
        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(tbGoods.getId());
        itemMapper.delete(tbItem);
        //新增
        List<TbItem> itemList = goods.getItemList();
        saveItems(goods, tbGoods, tbGoodsDesc);


    }

    @Override
    public Goods findOne(Long id) {
        //根据商品id 查出所有数据返回给页面展示
        //创建组合对象往里面封装查出来的数据
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(id);
        List<TbItem> tbItemList = itemMapper.select(tbItem);
        goods.setGoods(tbGoods);
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setItemList(tbItemList);

        return goods;
    }

    @Override
    public void add(Goods goods) {
        //1获取goods
        TbGoods tbGoods = goods.getGoods();
        //初始状态状态为未审核
        tbGoods.setAuditStatus("0");
        //是否删除 初始状态为否
        tbGoods.setIsDelete(false);
        goodsMapper.insert(tbGoods);

        //2获取goodsdesc
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        //设置描述表的主键
        goodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(goodsDesc);

        saveItems(goods, tbGoods, goodsDesc);


    }

    private void saveItems(Goods goods, TbGoods tbGoods, TbGoodsDesc goodsDesc) {
        //如果启用规则就正常添加
        if ("1".equals(tbGoods.getIsEnableSpec())){
            //3获取 skuList
            //先获取SKU的列表
            List<TbItem> itemList = goods.getItemList();
//"itemList": [ { "spec": { "网络": "移动3G", "机身内存": "16G" }, "price": 0, "num": 0, "status": "0", "isDefault": "0" } ]
            for (TbItem tbItem : itemList) {
                //设置title 标题  SPU名 + 空格 + 规格名称 +
                String spec = tbItem.getSpec(); // 获取的 { "spec": { "网络": "移动3G", "机身内存": "16G" },{}
                //取出商品名
                String title = tbGoods.getGoodsName();
                //把spec转为 Map遍历
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                for (String key : map.keySet()) {
                    //遍历取出里面的 网络,机身内存 等拼接到商品名后.
                    //取出这个键 的值 使用get 传入key
                    title += " " + map.get(key);
                }
                //拼接好后设置 标题
                tbItem.setTitle(title);

                //设置图片从goodsDesc中获取
                String itemImages = goods.getGoodsDesc().getItemImages();
                //[{"color":"灰色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhV0UIr6AEGQjAAAbnsHIuy8707.jpg"}]
                //转为对象 取出其中的url
                List<Map> maps = JSON.parseArray(itemImages, Map.class);
                String url = maps.get(0).get("url").toString();//图片的地址
                tbItem.setImage(url);

                //设置分类
                //获取子类目的分类
                TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
                //设置分类的id
                tbItem.setCategoryid(tbItemCat.getId());
                //设置分类的名称
                tbItem.setCategory(tbItemCat.getName());


                //时间
                tbItem.setCreateTime(new Date());
                tbItem.setUpdateTime(new Date());

                //设置SPU的ID
                tbItem.setGoodsId(tbGoods.getId());


                //设置商家  //通过商家id查询商家店铺名
                TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
                tbItem.setSellerId(tbSeller.getSellerId());
                tbItem.setSeller(tbSeller.getNickName()); //店铺名

                //设置品牌名称
                TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
                tbItem.setBrand(tbBrand.getName());

                itemMapper.insert(tbItem);
            }
        }else {
            //如果不启用规则
            TbItem tbItem = new TbItem();
            //设置标题
            tbItem.setTitle(tbGoods.getGoodsName());
            //设置价格
            tbItem.setPrice(tbGoods.getPrice());
            tbItem.setNum(999);//默认的库存
            tbItem.setStatus("1");//正常启用
            tbItem.setIsDefault("1");//默认
            tbItem.setSpec("{}");//没有选用设置为空

//设置图片从goodsDesc中获取
            //[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
            String itemImages = goodsDesc.getItemImages();//

            List<Map> maps = JSON.parseArray(itemImages, Map.class);

            String url = maps.get(0).get("url").toString();//图片的地址
            tbItem.setImage(url);

            //设置分类
            TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            tbItem.setCategoryid(tbItemCat.getId());
            tbItem.setCategory(tbItemCat.getName());

            //时间
            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(new Date());

            //设置SPU的ID
            tbItem.setGoodsId(tbGoods.getId());

            //设置商家
            TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
            tbItem.setSellerId(tbSeller.getSellerId());
            tbItem.setSeller(tbSeller.getNickName());//店铺名

            //设置品牌
            TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
            tbItem.setBrand(tbBrand.getName());
            itemMapper.insert(tbItem);



            itemMapper.insert(tbItem);
        }
    }

    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("isDelete",false);//只查询没有被删除的

        if (goods != null) {
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                criteria.andEqualTo("sellerId",  "%" +goods.getSellerId()+ "%" );
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(goods.getGoodsName())) {
                criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
                //criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (StringUtils.isNotBlank(goods.getAuditStatus())) {
                criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
                //criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsMarketable())) {
                criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
                //criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
            }
            if (StringUtils.isNotBlank(goods.getCaption())) {
                criteria.andLike("caption", "%" + goods.getCaption() + "%");
                //criteria.andCaptionLike("%"+goods.getCaption()+"%");
            }
            if (StringUtils.isNotBlank(goods.getSmallPic())) {
                criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
                //criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsEnableSpec())) {
                criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
                //criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
            }

        }
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return info;
    }


    /**
     * 修改审核状态
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        //创建一个goods对象把状态传入当做需要更新的值
        TbGoods tbGoods = new TbGoods();
        tbGoods.setAuditStatus(status);
        //创建查询条件
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        //根据id更改 有可能多选 所以把数组转为集合
        criteria.andIn("id", Arrays.asList(ids));
        //传入更新的值 和 更改的id
        // update set status=1 where id in (12,3,...)
        goodsMapper.updateByExampleSelective(tbGoods, example);


    }


    @Override
    public void delete(Object[] ids) {
        //update tb_goods set is_delete=1 where id in (1,2,3)
        Example example = new Example(TbGoods.class);
        //创建一个容器 克隆ids成一个集合
        Long[] issss = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {
            issss[i] = (Long) ids[i];
        }
        example.createCriteria().andIn("id", Arrays.asList(issss));

        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete(true);
        goodsMapper.updateByExampleSelective(tbGoods, example);



    }
}
