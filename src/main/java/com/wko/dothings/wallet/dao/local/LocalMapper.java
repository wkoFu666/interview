package com.wko.dothings.wallet.dao.local;

import com.wko.dothings.wallet.entities.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LocalMapper {

    @Select("select * from person")
    List<Person> findAllPerson();
}
