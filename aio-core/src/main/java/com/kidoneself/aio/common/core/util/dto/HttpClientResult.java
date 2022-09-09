package com.kidoneself.aio.common.core.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 封装httpClient响应结果
 *
 * @author YiiDii Wang
 * @create 2021-02-15 22:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpClientResult {

    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应数据
     */
    private String content;

    /**
     * Cookie字符串
     */
    private String cookieStr;

    /**
     * Cookie集合
     */
    private Map<String, String> cookieMap;
}
