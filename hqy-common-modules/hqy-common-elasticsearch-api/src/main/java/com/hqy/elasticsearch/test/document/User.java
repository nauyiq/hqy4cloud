package com.hqy.elasticsearch.test.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qy
 * @create 2021/9/7 23:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;

    private int age;

}
