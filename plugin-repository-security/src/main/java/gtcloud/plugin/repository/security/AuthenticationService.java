package gtcloud.plugin.repository.security;

import gtcloud.plugin.repository.domain.entity.User;
import gtcloud.plugin.repository.domain.vo.LoginUser;
import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.service.ExternalLoginRestService;
import gtcloud.plugin.repository.service.UserService;
import gtcloud.plugin.repository.service.impl.PluginServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static gtcloud.plugin.repository.utils.HashUtil.isSha256Match;

@Service
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(PluginServiceImpl.class);

    private final UserService userService;
    private final ExternalLoginRestService loginRestService;

    @Autowired
    public AuthenticationService(UserService userService, ExternalLoginRestService loginRestService) {
        this.userService = userService;
        this.loginRestService = loginRestService;
    }

    public boolean isValid(LoginUser loginUser) throws ResultException {
        if (loginUser == null) {
            return false;
        }
        String submittedUsername = loginUser.getUsername();
        String submittedPassword = loginUser.getPassword();
        if (submittedUsername == null || submittedPassword == null) {
            return false;
        }


        User authUser = userService.findByUsername(submittedUsername);
        if (authUser == null) {
            return false;
        }

        String passwordHash = authUser.getPasswordHash();
        String salt = authUser.getSalt();
        String role = authUser.getRole();

        boolean authenticated = false;
        try {
            authenticated = "ADMIN".equalsIgnoreCase(role) && isSha256Match(submittedPassword, salt, passwordHash);
        } catch (Exception e) {
            // Algorithm Exception
            LOG.error(e.getMessage());
        }

        if (authenticated) {
            return true;
        }
        return false;


//        String token = loginRestService.getToken(submittedUsername, submittedPassword);
//        List<String> roles = loginRestService.getRoles(token);
//        Optional<String> opt = roles.stream().filter(name -> name.equals("admin")).findAny();
//        if (opt.isPresent()) {
//            return true;
//        }
//        return false;
    }
}
