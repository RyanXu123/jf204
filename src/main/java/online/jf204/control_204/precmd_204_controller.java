package online.jf204.control_204;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


@Controller
public class precmd_204_controller {

    @Autowired
    private JdbcTemplate jdbc;
    @CrossOrigin
    @RequestMapping("/getData/202/precmd")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata202_pre(){ //按服务器和空调变化状态给数据

        String sql0="select * from precmd where Location='JF202' and ChangedType='sfwd'";
        String sql1="select * from precmd where Location='JF202' and ChangedType='hfwd'";
        String sql2="select * from predata where Location='JF202'and PointName='冷通道最大温度' ORDER BY id DESC limit 0,7";
        List <Map<String,Object>> list0= new ArrayList<>();
        List <Map<String,Object>> list=jdbc.queryForList(sql0);
        List <Map<String,Object>> list1=jdbc.queryForList(sql1);
        List <Map<String,Object>> list2=jdbc.queryForList(sql2);
//        list.addAll(list2);
        HashMap<String,Object> kt= new HashMap<>(); //字符串为null
        HashMap<String,Double> sf= new HashMap<>(); //字符串为null
        HashMap<String,Double> hf= new HashMap<>(); //字符串为null
        TreeMap<String,Object> server= new TreeMap<>();
        TreeMap<String,Object> server_all= new TreeMap<>();
        TreeMap<String,Object> kt_all= new TreeMap<>();

        for(Map<String,Object>c :list){
            String name=c.get("HelperKt").toString().substring(2);
            Double value=Double.parseDouble(c.get("ChangedValue").toString());
            sf.put(name,value);
        }

        for(Map<String,Object>c :list1){
            String name=c.get("HelperKt").toString().substring(2);
            Double value=Double.parseDouble(c.get("ChangedValue").toString());
            hf.put(name,value);
        }
        kt.put("sf",sf);
        kt.put("hf",hf);

        for(Map<String,Object>c :list2){
            String name=c.get("Equipment").toString().substring(3);
            Object value=c.get("Value0");
            server.put(name,value);
        }

        kt_all.put("kt",kt);
        server_all.put("server",server);
        list0.add(kt_all);
        list0.add(server_all);
        return list0;
    }

}
