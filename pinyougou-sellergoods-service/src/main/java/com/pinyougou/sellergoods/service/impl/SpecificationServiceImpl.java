package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import entity.Specification;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;  

import com.pinyougou.sellergoods.service.SpecificationService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl extends CoreServiceImpl<TbSpecification>  implements SpecificationService {

	
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper optionMapper;

	//通过构造函数给 成员变量赋值
	@Autowired
	public SpecificationServiceImpl(TbSpecificationMapper specificationMapper) {
		super(specificationMapper, TbSpecification.class);
		this.specificationMapper=specificationMapper;
	}

	
	

	
	@Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSpecification> all = specificationMapper.selectAll();
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();

        if(specification!=null){			
						if(StringUtils.isNotBlank(specification.getSpecName())){
				criteria.andLike("specName","%"+specification.getSpecName()+"%");
				//criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
        List<TbSpecification> all = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    @Override
    public void delete(Long[] ids) {
        //删除规格
        Example example = new Example(TbSpecification.class);

        example.createCriteria().andIn("id", Arrays.asList(ids));
        //按条件删除 传入删除条件
        specificationMapper.deleteByExample(example);
        //删除规格关联的规格选项
        Example exampleOption = new Example(TbSpecificationOption.class);
        //设置删除条件
        exampleOption.createCriteria().andIn("specId",Arrays.asList(ids) );

        optionMapper.deleteByExample(exampleOption);

    }

    @Override
    public void update(Specification specification) {

            //对注入的对象全部更新 如果为空设置为null updateByPrimaryKeySelective 如果为null就忽略更新
         specificationMapper.updateByPrimaryKey(specification.getSpecification());
         //创建规格选项对象
        TbSpecificationOption option = new TbSpecificationOption();
        option.setSpecId(specification.getSpecification().getId());
        int delete = optionMapper.delete(option);
        //拿出规格选项集合遍历设置关联id
        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption tbSpecificationOption : optionList) {
            tbSpecificationOption.setSpecId(specification.getSpecification().getId());
            //更新数据库
            optionMapper.insert(tbSpecificationOption);
        }

    }

    /**
     * 要一对多查询 所以要重写查询方法
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //创建自己定义的一对多实体类
        Specification specification = new Specification();
        //通过主键查单个产品规格信息
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //创建系统生成的产品规格选项实体类对象
        TbSpecificationOption option = new TbSpecificationOption();
        //需要手动设置关联id
        option.setSpecId(tbSpecification.getId());
        //通过id查询所有 规格选线的数据
        List<TbSpecificationOption> options = optionMapper.select(option);
        //把数据封装到自定义的 一对多实体类中
        specification.setSpecification(tbSpecification);
        specification.setOptionList(options);

        return specification;
    }

    /**
     *  因为基础的添加满足不了一对多的业务需求 所以重写添加方法
     * @param record
     */
    @Override
    public void add(Specification record) {
        //获取规格数据
        TbSpecification specification = record.getSpecification();
        //获取规格的选项中的列表数据
        List<TbSpecificationOption> optionList = record.getOptionList();
        //插入到两个表中
        //往规格表插入数据 这个一定要先插入 因为下面的选项表中需要一个规格的id 要从这里传入
        //如果使用通用mapper 添加了主键注解 会自动获取之后把id注入pojo属性的id中
        specificationMapper.insert(specification);

        for (TbSpecificationOption tbSpecificationOption : optionList) {
            //遍历选项集合 添加 因为页面没有传递规格id 需要手动设置
            tbSpecificationOption.setSpecId(specification.getId());
            optionMapper.insert(tbSpecificationOption);
        }


    }
}
