package com.pinyougou.common.util;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON工具类
 * 和JsonUtils不同的是如果发生异常不会抛出，而是返回null。
 */
public class SafeJsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 配置objectMapper
        // 美化输出
        //objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        // 允许序列化null对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略未知的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 在JSON中允许C/C++ 样式的注释（非标准，默认禁用）
        //objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 允许没有引号的字段名（非标准）
        //objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号（非标准）
        //objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private SafeJsonUtils() {}

    /**
     * 将对象转化成json
     *
     * @param object 对象
     * @return json字符串
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将普通类型的json转化成对象
     *
     * @param json json字符串
     * @param classOfT 目标对象类
     * @param <T> 目标对象类型
     * @return 转化得到的对象
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return objectMapper.readValue(json, classOfT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
