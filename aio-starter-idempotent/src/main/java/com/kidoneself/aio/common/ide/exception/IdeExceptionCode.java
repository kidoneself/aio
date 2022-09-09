package com.kidoneself.aio.common.ide.exception;

import com.kidoneself.aio.common.core.exception.code.BaseExceptionCode;
import lombok.AllArgsConstructor;

/**
 * @author YiiDii Wang
 * @create 2021-04-28 22:25
 */
@AllArgsConstructor
public enum IdeExceptionCode implements BaseExceptionCode {
    
    IDE_TARGET(90001, "请勿重复提交");

    private final int code;
    private String msg;

    /**
     * 异常编码
     *
     * @return 异常编码
     */
    @Override
    public int getCode() {
        return this.code;
    }

    /**
     * 异常消息
     *
     * @return 异常消息
     */
    @Override
    public String getMsg() {
        return this.msg;
    }
}
