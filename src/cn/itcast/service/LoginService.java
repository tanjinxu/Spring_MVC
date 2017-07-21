package cn.itcast.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bean.User;
import cn.itcast.dao.LoginDao;

@Service("loginService")
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, readOnly = false)
public class LoginService {
    @Resource(name = "loginDao")
    private LoginDao loginDao;

    public User doLogin(User user) {
        return loginDao.doLogin(user);
    }

}
