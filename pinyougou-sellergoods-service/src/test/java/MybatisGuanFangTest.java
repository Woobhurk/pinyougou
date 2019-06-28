import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MybatisGuanFangTest {


    @Autowired
    private TbBrandMapper tbBrandMapper;

    //增
    @Test
    public void insert() {
        TbBrand brand = new TbBrand();
        brand.setName("黑马");
        brand.setFirstChar("H");
        tbBrandMapper.insert(brand);

    }

    //删
    @Test
    public void delete() {
        //ID要加上L 类型
        tbBrandMapper.deleteByPrimaryKey(32L);
    }

    //改
    @Test
    public void update() {
        TbBrand brand = new TbBrand();
        brand.setId(31L);
        brand.setName("海马");
        //有值就更新 没有设置值就变空
        tbBrandMapper.updateByPrimaryKey(brand);
        //有值就更新 没有设置值就不变
        // tbBrandMapper.updateByPrimaryKeySelective(brand);

    }


    //查询
    @Test
    public void select() {
        //参数就是搜索的where条件
       // 查询所有的数据参数就为null
        Example exampl = new Example(TbBrand.class);
        Example.Criteria criteria = exampl.createCriteria();
        //criteria and 查询的条件
        //criteria.andNameEqualTo("海马");
        criteria.andEqualTo("海马");
        // 去重复  exampl.setDistinct();  排序   exampl.setOrderByClause();

        List<TbBrand> brands = tbBrandMapper.selectByExample(null);
        System.out.println(brands.toString());

    }


    @Test
    public void findAll(){
        List<TbBrand> all = tbBrandMapper.findAll();
        System.out.println(all);


    }



}
