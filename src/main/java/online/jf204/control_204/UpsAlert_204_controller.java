package online.jf203.control_203;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class UpsAlert_203_controller {
    @Autowired
    private JdbcTemplate jdbc;

    @CrossOrigin
    @RequestMapping("/203/upsalert")
    @ResponseBody
    public String upsalertcontroller(){
//        String sql="select * from user where UserName=xx";
        String sql="select * from ups_reasonable ORDER BY id DESC limit 0,1";
        List<Map<String,Object>> list_ups=jdbc.queryForList(sql);
        String ret="0";
        for (Map<String,Object> ups:list_ups) {
            ret=ups.get("Value0").toString();
//            System.out.println("upsAlert is "+ret);
//            return ret;
        }
        return ret;//默认ups正常
    }
}
