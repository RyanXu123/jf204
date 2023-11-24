package online.jf204.control_204;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class pswd_204_controller {
    @Autowired
    private JdbcTemplate jdbc;

    @CrossOrigin
    @PostMapping("/204/password")
    @ResponseBody
    public Boolean pswdcontroller(@RequestBody String data){
//        String sql="select * from user where UserName=xx";
        String name=data;
        System.out.println(data);
        String pswd="pinganai2023";
//        String name = data.substring(1,13);
        System.out.println(name);
        System.out.println(pswd);

        System.out.println(name.toString().length());
        System.out.println(pswd.length());

        if(pswd.equals(name.toString())){
            System.out.println("pswd equals data ");
            return true;
        }else{
            System.out.println("pswd not equals data ");
            return false;
        }
    }
}
