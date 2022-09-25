package gtcloud.plugin.repository.service;

import gtcloud.plugin.repository.domain.entity.User;

public interface UserService {

    /**
     * 获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
}
