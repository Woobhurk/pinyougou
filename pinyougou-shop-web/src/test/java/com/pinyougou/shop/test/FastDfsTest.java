package com.pinyougou.shop.test;

import com.pinyougou.common.util.FastDFSClient;
import org.csource.fastdfs.*;
import org.junit.Test;

public class FastDfsTest {
    //上传图片
    @Test
    public void uploadFile() throws Exception{
        //1.创建一个配置文件,配置服务器ip地址和端口 fastdsf_client.conf

        //2.加载配置文件(ID 和端口)
        ClientGlobal.init("E:\\IdeaProjects\\JavaEE60\\Test\\pinyougou\\pinyougou-shop-web\\src\\main\\resources\\fastdfs_client.conf");
        //3.创建 tracker client 对象
        TrackerClient trackerClient = new  TrackerClient();
        //4.获取 tracker Server 对象 这个对象来管理id地址 就可以使用代理取出
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建 storage Server 的端口都给到Tracker 然后Tracker在给到客户端 设置为null的意义是因为要用到它的api 获取StorageClient
        StorageServer storageServer =null;
        //6.创建 storage Client 使用该client 的 API 上传文件
        //这个客户端不是由 storageServer 里面来的 是由 trackerServer 给的
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        /**
         * 上传图片
         * 参数一,文件的路径
         * 参数二,文件的扩展名,不能加.(jpg..)
         * 参数三,文件的元数据(像素,时间戳,路径,像素等..)
         */
        String[] jpgs = storageClient.upload_file("E:\\user2-160x160.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);
        }

    }

    @Test
    public void test1() throws Exception{
        //传入 ip和端口号
        FastDFSClient fastDFSClient = new FastDFSClient("E:\\IdeaProjects\\JavaEE60\\Test\\pinyougou\\pinyougou-shop-web\\src\\main\\resources\\fastdfs_client.conf");
        //上传文件 传入文件路径 扩展名 元数据
        String jpg = fastDFSClient.uploadFile("E:\\user2-160x160.jpg", "jpg", null);

        System.out.println(jpg);

    }
}

