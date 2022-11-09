package com.wko.dothings.wallet.entities;


import com.wko.dothings.common.base.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryForm {


    /**
     * 用户id
     */
    private String userId;

    /**
     * 分页参数
     */
    private Page page;

    /**
     * 实际上执行sql查询的limit参数
     */
    private int limitParam;

}
