package gtcloud.plugin.repository.service.impl;

import gtcloud.plugin.repository.dao.UserDao;
import gtcloud.plugin.repository.domain.entity.User;
import gtcloud.plugin.repository.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
