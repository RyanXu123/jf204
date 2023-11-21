package online.jf204.service;

import online.jf204.dto.LoginDto;
import online.jf204.entity.ResultMassage;

public interface LoginService {
    public ResultMassage login(LoginDto loginDto);

}
