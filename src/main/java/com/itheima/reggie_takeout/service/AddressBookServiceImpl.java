package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.entity.AddressBook;
import com.itheima.reggie_takeout.mapper.AddressBookMapper;
import com.itheima.reggie_takeout.service.impl.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
