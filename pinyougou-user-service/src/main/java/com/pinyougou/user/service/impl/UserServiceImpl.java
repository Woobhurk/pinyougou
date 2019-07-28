package com.pinyougou.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {

    @Value("${templateCode}")
    private String templateCode;
    @Value("${sign_name}")
    private String signName;

    @Autowired
    private DefaultMQProducer producer;

    @Autowired
    private RedisTemplate redisTemplate;


    private TbUserMapper userMapper;

    @Autowired
    public UserServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }

    @Override
    public void createSmsCode(String phone) {
        try {
            //生产6位随机数字
            // math.random()*9+1一定是小于10的，(Math.random()*9+1)*100000一定是<10*100000=1000000的一个数
            String code = (long) ((Math.random() * 9 + 1) * 100000) + "";
            //储存到redis中
            //为了让key不重复 在前面加上功能前缀+手机号 存入生成的随机数字码
            this.redisTemplate.boundValueOps("ZHUCE_" + phone).set(code);
            //设置key的有效时间
            this.redisTemplate.boundValueOps("ZHUCE_" + phone).expire(1L, TimeUnit.MINUTES);
            //组装消息对象 手机号 签名 模板  验证码
            HashMap<String, String> map = new HashMap<>();
            map.put("mobile", phone);
            map.put("sign_name", this.signName);
            map.put("templateCode", this.templateCode);
            map.put("param", "{\"code\":\"" + code + "\"}");
            //发消息  注入producer
            Message message = new Message(
                "SMS_TOPIC",
                "SEND_MESSAGE_TAG",
                "createCode",
                JSON.toJSONString(map).getBytes());
            //发送消息
            this.producer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbUser> all = this.userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }




    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if (user != null) {
            if (StringUtils.isNotBlank(user.getUsername())) {
                criteria.andLike("username", "%" + user.getUsername() + "%");
                //criteria.andUsernameLike("%"+user.getUsername()+"%");
            }
            if (StringUtils.isNotBlank(user.getPassword())) {
                criteria.andLike("password", "%" + user.getPassword() + "%");
                //criteria.andPasswordLike("%"+user.getPassword()+"%");
            }
            if (StringUtils.isNotBlank(user.getPhone())) {
                criteria.andLike("phone", "%" + user.getPhone() + "%");
                //criteria.andPhoneLike("%"+user.getPhone()+"%");
            }
            if (StringUtils.isNotBlank(user.getEmail())) {
                criteria.andLike("email", "%" + user.getEmail() + "%");
                //criteria.andEmailLike("%"+user.getEmail()+"%");
            }
            if (StringUtils.isNotBlank(user.getSourceType())) {
                criteria.andLike("sourceType", "%" + user.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(user.getNickName())) {
                criteria.andLike("nickName", "%" + user.getNickName() + "%");
                //criteria.andNickNameLike("%"+user.getNickName()+"%");
            }
            if (StringUtils.isNotBlank(user.getName())) {
                criteria.andLike("name", "%" + user.getName() + "%");
                //criteria.andNameLike("%"+user.getName()+"%");
            }
            if (StringUtils.isNotBlank(user.getStatus())) {
                criteria.andLike("status", "%" + user.getStatus() + "%");
                //criteria.andStatusLike("%"+user.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(user.getHeadPic())) {
                criteria.andLike("headPic", "%" + user.getHeadPic() + "%");
                //criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }
            if (StringUtils.isNotBlank(user.getQq())) {
                criteria.andLike("qq", "%" + user.getQq() + "%");
                //criteria.andQqLike("%"+user.getQq()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsMobileCheck())) {
                criteria.andLike("isMobileCheck", "%" + user.getIsMobileCheck() + "%");
                //criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsEmailCheck())) {
                criteria.andLike("isEmailCheck", "%" + user.getIsEmailCheck() + "%");
                //criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getSex())) {
                criteria.andLike("sex", "%" + user.getSex() + "%");
                //criteria.andSexLike("%"+user.getSex()+"%");
            }

        }
        List<TbUser> all = this.userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        //非空判断
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(smsCode)) {
            return false;
        }
        String code = (String) this.redisTemplate.boundValueOps("ZHUCE_" + phone).get();

        if (!smsCode.equals(code)) {
            return false;
        }
        return true;
    }

}
