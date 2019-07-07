package com.itheima;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws Exception {
        //模板+数据集 =html
         //1.创建一个模板文件  官方推荐.ftl

        // 2.创建一个配置类(configuration)  直接 new 一个对象。构造方法的参数就是 freemarker的版本号
        Configuration configuration = new Configuration(Configuration.getVersion());

        //3.设置模板文件所在的位置
        configuration.setDirectoryForTemplateLoading(new File("E:\\IdeaProjects\\JavaEE60\\Test\\pinyougou\\itheima-freemarker\\src\\main\\resources\\template"));
        //4.设置模板文件的字符编码
         configuration.setDefaultEncoding("utf-8");
         //加载模板文件 参数:模板文件的名称(相对路径) 相当于已经把这个文件放到java类中
        Template template = configuration.getTemplate("demo.ftl");
        //5.准备数据集(map,pojo) 这个和Controller里面返回给页面的model是一样的
        Map model = new HashMap();
        //这个键和页面取值的key保持一致
        model.put("name", "张三");
        model.put("message", "这是一个神奇的时间");
        //6.创建一个写流 输出文件 复制指定生产的文件夹路径在后面加上指定文件  根据数据集根据模板 生产了index.html
        FileWriter writer = new FileWriter(new File("E:\\IdeaProjects\\JavaEE60\\Test\\pinyougou\\itheima-freemarker\\src\\main\\resources\\html\\index.html"));
        template.process(model, writer);
        //7.关闭流
        writer.close();




    }
}
