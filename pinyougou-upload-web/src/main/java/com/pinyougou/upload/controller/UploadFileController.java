package com.pinyougou.upload.controller;

import com.pinyougou.common.util.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadFileController {
    /**
     * 请求 uploadFile
     * 参数 传输的文件本身
     * 返回值
     *
     * @param file
     */
    @RequestMapping("/uploadFile")
    //系统的跨域要加上这个注解  支持跨域  商家 ip                 运营商ip
    @CrossOrigin(origins = {"http://localhost:9102","http://localhost:9101"},allowCredentials ="true")
    public Result uploadFile(MultipartFile file) {
        try {
            //1.获取文件的字节数组
            byte[] bytes = file.getBytes();
            //2.获取原文件的扩展名 不要.
            String fileName = file.getOriginalFilename(); //xxx.jpg
            String extName = fileName.substring(fileName.lastIndexOf(".") + 1); // . jpg
            //3.创建fastdfs的配置文件
            //4.核心代码, 调用工具类 上传图片
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");

            String file_id = fastDFSClient.uploadFile(bytes, extName);
             //拼接url返回给页面
            String realUrl = "http://192.168.146.130/" + file_id;

            return new Result(true, realUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }


    }
}
