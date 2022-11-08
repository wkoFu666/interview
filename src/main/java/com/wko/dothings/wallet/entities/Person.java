package com.wko.dothings.wallet.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {

    private String name;

    private String sex;

    private Integer age;

    private Timestamp birthDay;
}
