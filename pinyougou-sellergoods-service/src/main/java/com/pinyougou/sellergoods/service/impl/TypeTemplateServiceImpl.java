package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;

import com.pinyougou.sellergoods.service.TypeTemplateService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl extends CoreServiceImpl<TbTypeTemplate> implements TypeTemplateService {


    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper optionMapper;


    @Autowired
    public TypeTemplateServiceImpl(TbTypeTemplateMapper typeTemplateMapper) {
        super(typeTemplateMapper, TbTypeTemplate.class);
        this.typeTemplateMapper = typeTemplateMapper;
    }


    @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbTypeTemplate> all = typeTemplateMapper.selectAll();
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (StringUtils.isNotBlank(typeTemplate.getName())) {
                criteria.andLike("name", "%" + typeTemplate.getName() + "%");
                //criteria.andNameLike("%"+typeTemplate.getName()+"%");
            }
            if (StringUtils.isNotBlank(typeTemplate.getSpecIds())) {
                criteria.andLike("specIds", "%" + typeTemplate.getSpecIds() + "%");
                //criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
            }
            if (StringUtils.isNotBlank(typeTemplate.getBrandIds())) {
                criteria.andLike("brandIds", "%" + typeTemplate.getBrandIds() + "%");
                //criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
            }
            if (StringUtils.isNotBlank(typeTemplate.getCustomAttributeItems())) {
                criteria.andLike("customAttributeItems", "%" + typeTemplate.getCustomAttributeItems() + "%");
                //criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
            }

        }
        List<TbTypeTemplate> all = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    //根据模板的id 获取模板的对象中的规格的数据 拼接成 : [{id:27,'text':'网络',options:[{},{}] },{id:28,'text':'11',options:[{},{}]} ]
    @Override
    public List<Map> findSpecList(Long typeTmplateId) {
        //1.获取模板对象
        TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(typeTmplateId);
        //2.获取模板对象中规格列表数据字符
        String specIds = template.getSpecIds();
        //查询到的字符串不好遍历 可以转为json对象 然后遍历取出id
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        //3.根据规格id 获取规格的选项列表
        for (Map map : maps) {
            //{id:27,'text':'网络'
            Integer id = (Integer) map.get("id");//获取到规格的id
            //根据id查询所有规格选项
            TbSpecificationOption option = new TbSpecificationOption();
            option.setSpecId(Long.valueOf(id));
            //传入一个对象根据对象查询 对应的规格选项
            List<TbSpecificationOption> options = optionMapper.select(option);
            //4.拼接数据
            //options:[{},{}]
            map.put("options",options);
            //拼接完成之后的数据 {id:27,'text':'网络',options:[{},{}] }

        }
        //5.返回拼接后的数据
        return maps;
    }

}
