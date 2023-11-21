package online.jf204.control_204;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class aicmd_204_controller {

    @Autowired
    private JdbcTemplate jdbc;


    List<Map<String,Object>> list_cmd = new ArrayList<>();//AI指令

    @CrossOrigin
    @RequestMapping("/getData/202/aicmd")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata202_aicmd(){
        String sql="select * from aicmd where CommandType='群控控制' " ;
        String sql2="select * from aicmd where CommandType='预控控制' " ;
        List <Map<String,Object>> list=jdbc.queryForList(sql);
        List <Map<String,Object>> list2=jdbc.queryForList(sql2);
        Map<String,Object> ret= new HashMap<>();
        Integer cnt_beat=0;
        Integer cnt_null=0;
        List<Map<String,Object>> list_temp_cmd = new ArrayList<>();  //AI指令寄存器
        for(Map<String,Object>c:list){
            String name=c.get("CommandContent").toString();
            if (Objects.equals(name, "心跳信号")) {
                cnt_beat+=1;
            }
//            else if(Objects.equals(name,null)){
//                cnt_null+=1;
//            }
            else{
               list_temp_cmd.add(c) ;
            }
        }

        for(Map<String,Object>c:list2){
            String name= c.get("CommandContent").toString();
            if (Objects.equals(name, "心跳信号")) {
                cnt_beat+=1;
            }
//            else if(Objects.equals(name,null)){
//                cnt_null+=1;
//            }
            else{
                list_temp_cmd.add(c);
            }
        }
        if(cnt_beat==2 | list.size()+ list2.size()==0){//两条心跳，或两条空，则发送之前指令
//            cnt_beat=0;
            return list_cmd;

        }else{//如果群控或预控有指令，则传送，并更新全局变量为最新指令
            list_cmd=list_temp_cmd;
            return list_cmd;
        }

    }
}
