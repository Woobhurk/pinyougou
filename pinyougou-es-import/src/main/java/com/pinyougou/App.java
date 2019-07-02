package com.pinyougou;

import com.pinyougou.es.service.ItemService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void  main(String[] ages){
        //调用service方法 导入数据到es中

        //1,初始化spring容器
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");


        //2,获取容器中的server的实例对象
        ItemService itemService = classPathXmlApplicationContext.getBean(ItemService.class);

        //3.调用实例方法  导入
        itemService.ImportDataToEs();

    }

}
