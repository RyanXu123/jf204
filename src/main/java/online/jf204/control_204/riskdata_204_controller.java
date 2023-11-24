package online.jf204.control_204;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class riskdata_204_controller {

    @Autowired
    private JdbcTemplate jdbc;
    Map<String,String> risk_list=new HashMap<>();
    Integer cnt=0;//统计数据筛选的次数，满24小时清空一次，即2880



    @CrossOrigin
    @RequestMapping("/getData/204/riskdatanew0216")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_p2(){

        List <Map<String,Object>> list_data= new ArrayList<>();  //储存返回的json
        Map<String, Object> data = new HashMap<String, Object>();

        List<String> server = Arrays.asList("A","B","C","D","E","F","G","H","J","K","L","M","N","P");
        String sql="select Value0 from realdata_once where Location='JF204' and Equipment='服务器A' and SiteName='A1-上' limit 0,1";

        String sql1="select * from realdata_once where Location='JF204' and Equipment='服务器'";

        Map<String, Object> servers_cold= new TreeMap<>();  //所有列列服务器冷通道
        Map<String, Object> servers_hot= new TreeMap<>();  //某列服务器冷通道
        Map<String, Object> servers_power= new TreeMap<>();  //某列服务器冷通道
        Integer siteNum=23;



        Map<String, Object> server_temp_power = new TreeMap<>();  //某列服务器功率 放在服务器外面，包裹所有服务器
        for (String c:server) {  //遍历服务器 c为（"A","B","C","D" ...）

//            List <Map<String,Object>> list1= new jdbc.queryForList(sql1);
            Map<String, Object> server_temp_cold = new TreeMap<>();  //某列服务器冷通道
            Map<String, Object> server_temp_hot = new TreeMap<>();  //某列服务器热通道


            String sql_temp1 = sql1.replace("'服务器'", "'服务器" + c + "'"); //某服务器所有测点
            List<Map<String, Object>> list1 = jdbc.queryForList(sql_temp1);
            List<Double> server_site_cold_up = new ArrayList<>(); //某列服务器冷通道上测点
            List<Double> server_site_cold_down = new ArrayList<>();  //某列服务器冷通道下测点
            List<Double> server_site_hot_all = new ArrayList<>();  //某列服务器热通道测点
            Integer cnt = 0;
            for (Map<String, Object> l : list1) {
                Double value0 = (double) l.get("Value0");
                if (cnt < siteNum * 2) {
                    if (cnt % 2 != 0) {//奇数下测点
                        String s= String.format("%.2f", Math.pow((value0-17)/(26.8-17) ,3)*100);
                        double d =Double.parseDouble(s);
                        if(value0<26.8){
                            d =Double.parseDouble(s);

                        }else if(value0>=26.8){
                            d =0.99;
                        }
                        if (value0==0.0){
                            d=-1.0;
                        }


                        server_site_cold_down.add(d);         //正常0
                    } else {
                        String s= String.format("%.2f", Math.pow((value0-17)/(26.8-17) ,3)*100); //变量区分大，但计算变慢
                        double d =Double.parseDouble(s);

                        if(value0<26.8){
                            d =Double.parseDouble(s);

                        }else if(value0>=26.8){
                            d =0.99;
                        }
                        if (value0==0.0){
                            d=-1.0;
                        }
                        server_site_cold_up.add(d);//冷下 1 3 5 7
                    }
                cnt++;
                }

                Map<String, Object> server_site = new HashMap<String, Object>();  //服务器测点
                Map<String, Object> site_cold = new TreeMap<>(); //冷通道
//            TreeMap<String, Object> server_site22_avg = new TreeMap<>(server_site2_avg);  //测点排序


                site_cold.put("up", server_site_cold_up); //某列服务器所有上测点  （up，{服务器所有测点（1，22）（2，22）..}）
                site_cold.put("down", server_site_cold_down);//某列服务器所有下测点  （down，{服务器所有测点（1，22）（2，22）..}）

                servers_cold.put(c, site_cold); //冷通道（A，{(avg,xx),(sitedetail,xx)}）
            }

        }
        list_data.add(servers_cold);
        return list_data;
    }

}
