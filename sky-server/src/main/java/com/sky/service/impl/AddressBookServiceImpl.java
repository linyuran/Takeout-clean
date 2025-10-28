package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 查询地址信息
     * @param addressBook
     * @return
     */
    public List<AddressBook> list(AddressBook addressBook) {
        //查询用户所有地址数据
        //动态查询
        List<AddressBook> addressBookList = addressBookMapper.list(addressBook);
        return addressBookList;
    }

    /**
     * 新增地址信息
     * @param addressBook
     */
    public void save(AddressBook addressBook) {
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        return addressBook;
    }

    /**
     * 根据id修改地址
     * @param addressBook
     */
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    public void setDefault(AddressBook addressBook) {
        //将所有地址都设置为非默认的
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        //再将当前这个地址设置为默认的
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);

    }

    /**
     * 根据id删除地址
     * @param id
     */
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);

    }
}
