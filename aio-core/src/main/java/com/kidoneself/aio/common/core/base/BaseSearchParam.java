package com.kidoneself.aio.common.core.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YiiDii Wang
 * @create 2021-03-13 23:21
 */
@Data
public class BaseSearchParam implements Serializable {

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    private String keyword;

    /**
     * 开始日期
     */
    @ApiModelProperty(hidden = true)
    private String startTime;

    /**
     * 结束日期
     */
    @ApiModelProperty(hidden = true)
    private String endTime;

    /**
     * 排序属性
     */
    @ApiModelProperty(hidden = true)
    private String orderBy;

    /**
     * 排序方式：asc,desc
     */
    @ApiModelProperty(hidden = true)
    private String order;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页")
    private Integer current = 1;

    /**
     * 每页的数量
     */
    @ApiModelProperty(value = "每页的数量")
    private Integer size = 20;

}
