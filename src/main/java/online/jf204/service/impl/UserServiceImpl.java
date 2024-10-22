package online.jf204.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.jf204.entity.User;
import online.jf204.mapper.UserMapper;
import online.jf204.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
