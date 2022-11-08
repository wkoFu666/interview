package com.wko.dothings.wallet.service.impl;

import com.wko.dothings.wallet.dao.local.LocalMapper;
import com.wko.dothings.wallet.dao.remote.RemoteMapper;
import com.wko.dothings.wallet.entities.Person;
import com.wko.dothings.wallet.service.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalServiceImpl implements LocalService {

    @Autowired
    private LocalMapper localMapper;

    @Autowired
    private RemoteMapper remoteMapper;

    @Override
    public List<Person> getAllPerson() {
        return localMapper.findAllPerson();
    }
}
