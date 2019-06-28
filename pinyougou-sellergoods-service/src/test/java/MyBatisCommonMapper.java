import com.pinyougou.mapper.TbBrandMapper;

import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MyBatisCommonMapper {

    @Autowired
    private TbBrandMapper brandMapper;


    @Test
    public void insert(){
        TbBrand brand = new TbBrand();

        brand.setName("黑马");
        brand.setFirstChar("H");
        brandMapper.insert(brand);


    }
}
