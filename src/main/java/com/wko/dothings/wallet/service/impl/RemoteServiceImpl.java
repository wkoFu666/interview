package com.wko.dothings.wallet.service.impl;

import com.wko.dothings.wallet.dao.remote.RemoteMapper;
import com.wko.dothings.wallet.entities.Person;
import com.wko.dothings.wallet.service.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemoteServiceImpl implements RemoteService {

    @Autowired
    private RemoteMapper remoteMapper;

    @Override
    public List<Person> getAllPerson() {
        return remoteMapper.findAllPerson();
    }
}
