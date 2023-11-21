package online.jf204.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import online.jf204.dto.LoginDto;
import online.jf204.dto.LoginVo;
import online.jf204.entity.User;
import online.jf204.entity.ResultMassage;
import online.jf204.mapper.UserMapper;
import online.jf204.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ResultMassage login(LoginDto loginDto) {
        if (loginDto.getLoginName().equals(null)){
            return new ResultMassage(400,"账号不能为空","");
        }
        if (loginDto.getPassword().equals(null)){
            return new ResultMassage(400,"密码不能为空","");
        }
        //通过登录名查询用户
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("UserName", loginDto.getLoginName());
        User uer=userMapper.selectOne(wrapper);
        //比较密码
        if (uer!=null&&uer.getPassword().equals(loginDto.getPassword())){
            LoginVo loginVO=new LoginVo();
            loginVO.setId(uer.getId());
            //这里token直接用一个uuid
            //使用jwt的情况下，会生成一个jwt token,jwt token里会包含用户的信息
            loginVO.setToken(UUID.randomUUID().toString());
            loginVO.setUser(uer);
            return new ResultMassage(200,"",loginVO);
        }
        return new ResultMassage(401,"登录失败","");
    }
}
