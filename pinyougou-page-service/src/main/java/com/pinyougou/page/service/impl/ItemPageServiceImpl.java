package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pageDir}")
    private String pageDir;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public void genItemHtml(Long goodsId) {
        //1.根据SPU的id 获取到spu的信息 (包括描述的信息) goods goodsDesc
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
        //2.调用freeamrker的方法 输出静态页面
        genHTML("item.ftl", tbGoods, tbGoodsDesc);
    }

    @Override
    public void deleteById(Long[] goodsId) {
        try {
            for (Long aLong : goodsId) {
                FileUtils.forceDelete(new File(pageDir + aLong + ".html"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void genHTML(String templateName, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        FileWriter writer = null;

        try {
            //1.创建一个configuration对象
            //2.设置字符编码和模板加载的目录
            Configuration configuration = configurer.getConfiguration();
            //3.加载模板对象
            Template template = configuration.getTemplate(templateName);
            //4.获取数据集
            Map model = new HashMap();
            model.put("tbGoods", tbGoods);
            model.put("tbGoodsDesc", tbGoodsDesc);
            //根据分类的id 查询分类的对象
            TbItemCat tbItemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat tbItemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat tbItemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            model.put("tbItemCat1", tbItemCat1.getName());
            model.put("tbItemCat2", tbItemCat2.getName());
            model.put("tbItemCat3", tbItemCat3.getName());
            //查询商品spu的对应的所有sku的列表数据
            ////select * from tb_item where goods_id=1 and status=1 order by is_default desc
            //构建查询条件
            Example example = new Example(TbItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId", tbGoods.getId());
            criteria.andEqualTo("status", "1");
            example.setOrderByClause("is_default desc"); //order by  is_default desc
            List<TbItem> tbItems = tbItemMapper.selectByExample(example);
            model.put("skuList", tbItems);


            //5.创建一个输出流 指定输出的目录文件
            writer = new FileWriter(new File(pageDir + tbGoods.getId() + ".html"));
            //6.执行输出的动作生产静态页面
            template.process(model, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //7.关闭流
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }
    }
}
