package online.jf204.control_204;

import online.jf204.dto.LoginDto;
import online.jf204.entity.ResultMassage;
import online.jf204.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class login_204_controller {
    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    @CrossOrigin
    @ResponseBody
    public ResultMassage login(@RequestBody LoginDto loginDto){
        return loginService.login(loginDto);
    }

}
