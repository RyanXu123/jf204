package online.jf204.control_204;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class UpsAlert_204_controller {
    @Autowired
    private JdbcTemplate jdbc;

    @CrossOrigin
    @RequestMapping("/204/upsalert")
    @ResponseBody
    public String upsalertcontroller(){
//        String sql="select * from user where UserName=xx";
        String sql="select * from ups_reasonable ORDER BY id DESC limit 0,1";
        List<Map<String,Object>> list_ups=jdbc.queryForList(sql);
        String ret="0";
//        for (Map<String,Object> ups:list_ups) {
//            ret=ups.get("Value0").toString();
////            System.out.println("upsAlert is "+ret);
////            return ret;
//        }
        return ret;//默认ups正常
    }
}
