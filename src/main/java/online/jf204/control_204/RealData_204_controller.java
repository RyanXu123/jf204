package online.jf203.control_203;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
@Controller
public class RealData_203_controller {
    @Autowired
    private JdbcTemplate jdbc;

    @CrossOrigin
    @RequestMapping("/203")
    @ResponseBody
    public String test(){
        return "test pass";
    }

    @CrossOrigin
    @RequestMapping("/getData/203/realdata/ktnew")
    @ResponseBody
    public List<Map<String,Object>> ktnew() {

        //        kt2
        List<Map<String, Object>> list_all = new ArrayList<>();
        String sql20_sf = "  select * from realdata_once where Location='JF203' and Equipment='空调0' and time = ( SELECT MAX(time) FROM realdata_once )";//从表中筛选某空调的所有参数
        TreeMap<Integer, Object> kt_all = new TreeMap<>();
        for (Integer i = 1; i <= 13; i++) {
            String sql_temp = sql20_sf.replace("空调0", "空调" + i);   //遍历所有空调 1，2....20

            List<Map<String, Object>> list_kt = jdbc.queryForList(sql_temp); //一台空调所有参数，list里面为一台空调的所有参数：回风温度、送风温度等等
            //select * from realdata_once where Location='JF203' and Equipment='空调1'  在数据库里面运行该sql，查看返回的字段
            LinkedHashMap<String, Object> temp = new LinkedHashMap<>();
            LinkedHashMap<String, Object> temp2 = new LinkedHashMap<>();

            for (Map<String, Object> kt : list_kt) {//遍历返回的list，截取PointName和value0列，即参数名称和参数值
                Object PointName = kt.get("PointName");
                String PointName2 = PointName.toString();
                Object Value0 = kt.get("Value0");
                temp.put(PointName2, Value0);
            }
            // 存入键值对，如空调1 为（1，空调1所有参数的PointName和value0对）
            temp2.put("压缩机1容量", temp.get("压缩机1容量"));
            temp2.put("压缩机2容量", temp.get("压缩机2容量"));
            temp2.put("风机1转速", temp.get("风机1转速"));
            temp2.put("风机2转速", temp.get("风机2转速"));
            temp2.put("冷凝风机1转速", temp.get("冷凝风机1转速"));
            temp2.put("冷凝风机2转速", temp.get("冷凝风机2转速"));
            temp2.put("回风温度设定", temp.get("回风温度设定"));
            temp2.put("回风温度1", temp.get("回风温度1"));
            temp2.put("回风温度2", temp.get("回风温度2"));
            temp2.put("回风温度3", temp.get("回风温度3"));
            temp2.put("回风温度4", temp.get("回风温度4"));
            temp2.put("送风温度设定", temp.get("送风温度设定"));
            temp2.put("送风温度1", temp.get("送风温度1"));
            temp2.put("送风温度4", temp.get("送风温度4"));
            temp2.put("空调功率", temp.get("空调功率"));
            temp2.put("空调异常状态",temp.get("空调异常状态"));
            kt_all.put(i, temp2);
        }
        Map<String,Object> kt_name = new HashMap<>();
        kt_name.put("机房空调",kt_all);   // 存入键值对 ,样式为（机房空调，{[1，空调1参数键值对],[2,空调2参数键值对]...})）
        list_all.add(kt_name);
        return list_all;
    }

    @CrossOrigin
    @RequestMapping("/getData/203/realdata/servernew")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata203_servernew2(){

        List <Map<String,Object>> list_data= new ArrayList<>();  //储存返回的json
        Map<String, Object> data = new HashMap<String, Object>();

        List<String> server = Arrays.asList("A","B","C","D","E","F","G","H","J","K");
        String sql="select Value0 from realdata_once where Location='JF203' and Equipment='服务器A' and SiteName='A01-上' and time = ( SELECT MAX(time) FROM realdata_once ) limit 0,1";

        String sql1="select * from realdata_once where Location='JF203' and Equipment='服务器' and time = ( SELECT MAX(time) FROM realdata_once ) limit 0,58";//一个时刻数据

        Map<String, Object> servers_cold= new TreeMap<>();  //所有列列服务器冷通道
        Map<String, Object> servers_hot= new TreeMap<>();  //某列服务器冷通道
        Map<String, Object> servers_power= new TreeMap<>();  //某列服务器冷通道
        Integer siteNum=19;



        Map<String, Object> server_temp_power = new TreeMap<>();  //某列服务器功率 放在服务器外面，包裹所有服务器
        for (String c:server){  //遍历服务器 c为（"A","B","C","D" ...）

//            List <Map<String,Object>> list1= new jdbc.queryForList(sql1);
            Map<String, Object> server_temp_cold= new TreeMap<>();  //某列服务器冷通道
            Map<String, Object> server_temp_hot = new TreeMap<>();  //某列服务器热通道

            Integer cold_up_cnt_null=0;
            Integer cold_down_cnt_null=0;
            Integer hot_cnt_null=0;

            double cold_up_sum=0.0;
            double cold_down_sum=0.0;
            double hot_sum=0.0;//某列服务器热通道测点温度求和

            String sql_temp1=sql1.replace("'服务器'","'服务器"+c+"'"); //某服务器所有测点
            List <Map<String,Object>> list1 =jdbc.queryForList(sql_temp1);
            List <Double>server_site_cold_up =new ArrayList<>();  //某列服务器冷通道上测点
            List <Double>server_site_cold_down =new ArrayList<>();  //某列服务器冷通道下测点
            List <Double>server_site_hot_all =new ArrayList<>();  //某列服务器热通道测点
            Integer cnt=0;
            for(Map<String,Object>l:list1){
                double value0=(double) l.get("Value0");
                if(cnt<siteNum*2){
                    if(cnt%2!=0){//奇数下测点
                        server_site_cold_down.add(value0);//冷上 0 2 4 6 8
                        if(value0<=0.0){
                            cold_up_cnt_null++;
                        }else{
                            cold_up_sum+=value0;
                        }
                    }else{
                        server_site_cold_up.add(value0);//冷下 1 3 5 7
                        if(value0<=0.0){
                            cold_down_cnt_null++;
                        }else{
                            cold_down_sum+=value0;
                        }
                    }
                }else if(cnt<siteNum*3){
                    server_site_hot_all.add(value0);//热 46 47 48 49
                    if(value0<=0.0){
                        hot_cnt_null++;
                    }else{
                        hot_sum+=value0;
                    }
                }else{ //69
                    server_temp_power.put(c,value0);
                }
                cnt++;

            }

            Map<String, Object> server_site =new HashMap<String, Object>();  //服务器测点

            cold_up_sum=cold_up_sum/(siteNum-cold_up_cnt_null);//某列服务冷通道上测点的总和除非0测点的个数，为某列服务器冷通道上测点平均
            cold_down_sum=cold_down_sum/(siteNum-cold_down_cnt_null);//某列服务冷通道下测点的总和除非0测点的个数，为某列服务器冷通道下测点平均
            hot_sum=hot_sum/(siteNum-hot_cnt_null);//某列服务热通道测点的总和除非0测点的个数，为某列服务器热通道测点平均

            String t1 = String.format("%.2f", cold_up_sum);
            String t2 = String.format("%.2f", cold_down_sum);
            String t3 = String.format("%.2f", hot_sum); // 保留两位小数


            Map<String, Object> site_avg_cold = new TreeMap<>();//某服务器冷通道平均
            Map<String, Object> site_avg_hot = new TreeMap<>();//热通道平均
            Map<String, Object> site_cold = new TreeMap<>(); //冷通道
            Map<String, Object> site_hot = new TreeMap<>();//热通道
            Map<String, Object> site_power = new TreeMap<>();//功率
//            TreeMap<String, Object> server_site22_avg = new TreeMap<>(server_site2_avg);  //测点排序

            site_avg_cold.put("upall",t1);//冷通道上，（upall，某列冷上平均）
            site_avg_cold.put("downall",t2);//冷通道下 （downall，某列冷下平均）
            site_avg_hot.put("all",t3); ////热通道  （all，某列热平均）

            site_cold.put("up",server_site_cold_up); //某列服务器所有上测点  （up，{服务器所有测点（1，22）（2，22）..}）
            site_cold.put("down",server_site_cold_down);//某列服务器所有下测点  （down，{服务器所有测点（1，22）（2，22）..}）
            //返回索引最高和次高温度
            TreeSet<Double> cold_up_temp=new TreeSet<Double>(server_site_cold_up);
            TreeSet<Double> cold_down_temp=new TreeSet<Double>(server_site_cold_down);
            TreeSet<Double> hot_temp=new TreeSet<Double>(server_site_hot_all);
//
//            Collections.sort(cold_up_temp);
//            Collections.sort(cold_down_temp);
//            Collections.sort(hot_temp);

            double cold_max_up1=0.0;
            double cold_max_up2=0.0;
            double cold_max_down1=0.0;
            double cold_max_down2=0.0;


            if(!cold_up_temp.isEmpty()){
                cold_max_up1=cold_up_temp.last();
                cold_up_temp.remove(cold_up_temp.last());
                cold_max_up2=cold_up_temp.last();
            }

//            for( Double i:cold_down_temp){
//                if(cnt_num==cold_down_temp.size()){
//                    cold_max_down1=i;//最大
//                }else if(cnt_num==cold_down_temp.size()-1){
//                    cold_max_down2=i;//次大
//                }
//                cnt_num++;
//            }//找出最大值和次大值
            if(!cold_down_temp.isEmpty()){
                cold_max_down1=cold_down_temp.last();
                cold_down_temp.remove(cold_down_temp.last());
                cold_max_down2=cold_down_temp.last();
            }


            List <Integer>up_index=new ArrayList<>();
            List <Integer>down_index=new ArrayList<>();
            List <Integer>up2_index=new ArrayList<>();
            List <Integer>down2_index=new ArrayList<>();
            int index_cnt=0;
            for(double i :server_site_cold_up){//遍历为最大值的index
                if(i==cold_max_up1 & i>0){ //为最大或次大 但不是废弃测点
                    up_index.add(index_cnt);
                }else if(i ==cold_max_up2 & i>0){//遍历为次大值的index
                    up2_index.add(index_cnt);
                }
                index_cnt++;
            }

            index_cnt=0;
            for(double i :server_site_cold_down){
                if(i==cold_max_down1  & i>0){
                    down_index.add(index_cnt);
                }else if(i ==cold_max_down2  & i>0){
                    down2_index.add(index_cnt);
                }
                index_cnt++;
            }


//            double hot_max1= (double)cold_up_temp.get(cold_up_temp.size()-1);
//            double hot_max2= (double)cold_up_temp.get(cold_up_temp.size()-2);
            site_cold.put("cold_up_max_index",up_index);  //在数组查找索引
            site_cold.put("cold_up_submax_index",up2_index);
            site_cold.put("cold_down_max_index",down_index);
            site_cold.put("cold_down_submax_index",down2_index);

            server_temp_cold.put("avg",site_avg_cold);  // (avg, {（upall，某列冷上平均）,（downall，某列冷下平均）})
            server_temp_cold.put("sitedetail",site_cold);// .put("sitedetail",serv);//(sitedetail,{（up，{服务器所有测点（1，22）（2，22）..}）,（down，{服务器所有测点（1，22）（2，22）..}）})

            //热通道
            server_temp_hot.put("sitedetail",server_site_hot_all); //(sitedetail,{服务器所有测点（1，22）（2，22）..})
            server_temp_hot.put("avg",site_avg_hot); //(avg,（all，某列热平均）)


            servers_cold.put(c,server_temp_cold); //冷通道（A，{(avg,xx),(sitedetail,xx)}）
            servers_hot.put(c,server_temp_hot); //热通道（A，{}）
//            servers_power.put(c,server_temp_power); //热通道（A，{}）
//            break;
//            server_num+=1;
//            data.put(sql_temp1,c);
        }

//        server_temp2=temp_p;
        data.put("servercold",servers_cold);
        data.put("serverhot",servers_hot);
        data.put("serverpower",server_temp_power);

        list_data.add(data);
        return list_data;
    }
}
