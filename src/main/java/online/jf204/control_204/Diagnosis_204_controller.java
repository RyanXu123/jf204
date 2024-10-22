package online.jf204.control_204;

import com.alibaba.fastjson2.JSONObject;
import online.jf204.entity.alert;
import online.jf204.entity.log;
import online.jf204.entity.sitecold;
import online.jf204.mapper.sitecoldMapper;
import online.jf204.mapper.logMapper;
import online.jf204.service.alertService;
import online.jf204.service.sitecoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class Diagnosis_204_controller {

    @Autowired
    private JdbcTemplate jdbc;
    List <Double> sf_range =Arrays.asList(22.0,26.0);
    List  <Double>hf_range =Arrays.asList(32.0,38.0);
    List <Double>ysj_range =Arrays.asList(33.0,100.0);
    List <Double> fj_range =Arrays.asList(33.0,100.0);
    List  <Double>lnfj_range =Arrays.asList(33.0,100.0);

    List  cold_range =Arrays.asList(20.0,26.8);
    List  hot_range =Arrays.asList(28.0,38.0);

    @Autowired
    private logMapper logMapper;


    @CrossOrigin
    @RequestMapping("/getData/204/realdata/server_display")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> server_display(){

        List <Map<String,Object>> list_data= new ArrayList<>();  //储存返回的json
        Map<String, Object> data = new HashMap<String, Object>();

        List<String> server =  Arrays.asList("A","B","C","D","E","F","G","H","J","K","L","M","N","P");
        String sql="select Value0 from realdata_once where Location='JF204' and Equipment='服务器A' and SiteName='A1-上' limit 0,1";

        String sql1="select * from realdata_once where Location='JF204' and Equipment='服务器' limit 0,58"; //19*3+1

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
            List<Double> server_site_cold_up =new ArrayList<>(); //某列服务器冷通道上测点
            List<Double> server_site_cold_down =new ArrayList<>();  //某列服务器冷通道下测点
            List<Double> server_site_hot_all =new ArrayList<>();  //某列服务器热通道测点
            Integer cnt=0;
            for(Map<String,Object>l:list1){
                Double value0=(double) l.get("Value0");
                if(cnt<siteNum*2){
                    if(cnt%2!=0){//奇数下测点
                        if( value0>=26.8){
                            server_site_cold_down.add(1.0);//冷上 0 2 4 6 8  过热1
                        }else if( value0<=0.0){
                            server_site_cold_down.add(-1.0);//+1      异常-1
                        }else{
                            server_site_cold_down.add(0.0);         //正常0
                        }
                    }else{

                        if(value0>=26.8){
                            server_site_cold_up.add(1.0);//冷下 1 3 5 7
                        }else if( value0<=0.0){
                            server_site_cold_up.add(-1.0);
                        }else{
                            server_site_cold_up.add(0.0);
                        }
                    }
                }else if(cnt<69){
                    if((value0<=28.0 & value0>0.0)| value0>=38.0){
                        server_site_hot_all.add(1.0);//热 46 47 48 49
                    }else if( value0<=0.0){
                        server_site_hot_all.add(-1.0);//热 46 47 48 49
                    }else{
                        server_site_hot_all.add(0.0);
                    }
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
//        data.put("serverpower",server_temp_power);

        list_data.add(data);
        return list_data;
    }

    ////新代码


    @CrossOrigin
    @RequestMapping("/getData/204/realdata/kt_diagnosis")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_judge(){

        //        kt2
        List <Map<String,Object>> list_all= new ArrayList<>();
        String sql20_sf=" select PointName,Equipment,Value0 from realdata_once where Location='JF204' and Equipment='空调0' limit 0,16";  //从表中筛选某空调的所有参数
        List <Map<String,Object>> list_temp= new ArrayList<>();
        Map<String,Object> a = new HashMap<>();
        Map<Integer,Object> kt_all = new HashMap<>();
        for(Integer i=1;i<=13;i++){
            String sql_temp=sql20_sf.replace("空调0","空调"+i);   //遍历所有空调 1，2....20
            List <Map<String,Object>> list_kt=jdbc.queryForList(sql_temp); //一台空调所有参数，list里面为一台空调的所有参数：回风温度、送风温度等等

            Map<String,Object> temp = new HashMap<>();
            for (Map<String,Object> c:list_kt){
                String params=c.get("PointName").toString();
                Double value0=(double) c.get("Value0");
                if(params.equals("送风温度1") | params.equals("送风温度4")){
                    if(value0<sf_range.get(0) | value0>sf_range.get(1)){
                        list_temp.add(c);
                    }
                }else if(params.equals("回风温度4")|params.equals("回风温度3")|params.equals("回风温度2")|params.equals("回风温度1")){
                    if(value0<hf_range.get(0) | value0>hf_range.get(1)){
                        list_temp.add(c);
                    }
                }else if(params.equals("压缩机1容量")|params.equals("压缩机2容量")){
                    if(value0<ysj_range.get(0)| value0>ysj_range.get(1)){
                        list_temp.add(c);
                    }
                }else if(params.equals("风机1转速")|params.equals("风机2转速")){
                    if(value0<fj_range.get(0) | value0>fj_range.get(1)){
                        list_temp.add(c);
                    }
                }
            }
        }

        return list_temp;
    }


    @CrossOrigin
    @RequestMapping("/getData/204/realdata/server_diagnosis")
    @ResponseBody
    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_judge2(){

        //        kt2
        List <Map<String,Object>> list_all= new ArrayList<>();
        List<String> server =  Arrays.asList("A","B","C","D","E","F","G","H","J","K","L","M","N","P");
        String sql20_sf=" select PointName,Equipment,Value0,SiteName from realdata_once where Location='JF204' and Equipment='服务器' limit 0,58";  //从表中筛选某空调的所有参数
        List <Map<String,Object>> list_temp= new ArrayList<>();
        Map<String,Object> a = new HashMap<>();
        Map<Integer,Object> kt_all = new HashMap<>();
        for(String c:server){
            String sql_temp=sql20_sf.replace("服务器","服务器"+c);   //遍历所有空调 1，2....20
            List <Map<String,Object>> list_kt=jdbc.queryForList(sql_temp); //一台空调所有参数，list里面为一台空调的所有参数：回风温度、送风温度等等
            Map<String,Object> temp = new HashMap<>();

            for (Map<String,Object> site:list_kt){
                String PointName=site.get("PointName").toString();
                String Equipment=site.get("Equipment").toString();
                String SiteName=site.get("SiteName").toString();
//                String params=site.get("PointName").toString();
                Double value0=(double) site.get("Value0");
                if(PointName.equals("冷通道温度")){
                    if(value0>26.8){
                        list_temp.add(site);
                    }
                }else if(PointName.equals("热通道温度")){
                    if((value0>0&value0<28) | value0>38){
                        list_temp.add(site);
                    }
                }
            }
        }

        return list_temp;
    }


    @CrossOrigin
    @RequestMapping("/getData/204/realdata/diagnosis_kt_design")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List <List<Double>> diagnosis_design(){
        List <List<Double>> ret =new ArrayList<>();
        List temp= Arrays.asList(22,26);
        ret.add(sf_range);
        ret.add(hf_range);
        ret.add(ysj_range);
        ret.add(fj_range);
        ret.add(lnfj_range);
        return ret;
    }

    @CrossOrigin
    @PostMapping("/getData/204/realdata/diagnosis_kt_design")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)

    public JSONObject diagnosis_design2(@RequestBody JSONObject data){
        //输入数据库的数据
        String sfrangeLog = "";
        String hfrangeLog = "";
        String ysjrangeLog = "";
        String fjrangeLog = "";
        String lnfjrangeLog = "";

        List<List<Double>> params_diagnosis_design=(List<List<Double>>) data.get("params");

        List<String> user_diagnosis_design= (List<String>)data.get("user");

        List<Double> sfRange = params_diagnosis_design.get(0);
        List<Double> hfRange =  params_diagnosis_design.get(1);
        List<Double> ysjRange = params_diagnosis_design.get(2);
        List<Double> fjRange =  params_diagnosis_design.get(3);
        List<Double> lnfjRange =  params_diagnosis_design.get(4);

        String userName = user_diagnosis_design.get(0).toString();
        String userRole = user_diagnosis_design.get(1).toString();
        String time_operate = user_diagnosis_design.get(2).toString();
//送风范围
        if (!sfRange.equals(sf_range)) {
            sfrangeLog ="送风温度范围改变为";

                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(sfrangeLog + sfRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
        }

        sf_range = sfRange;

        //回风温度范围
         if (!hfRange.equals(hot_range)) {
            hfrangeLog ="回风温度范围改变为";

                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(hfrangeLog + hfRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
         }

        hf_range =hfRange;

        //压缩机温度范围
        if (!ysjRange.equals(ysj_range)) {
            ysjrangeLog ="压缩机温度范围改变为";

                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(ysjrangeLog + ysjRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);

        }
        ysj_range =ysjRange;

        //风机温度范围
        if (!fjRange.equals(fj_range)) {
            fjrangeLog ="风机温度范围改变为";

                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(fjrangeLog + fjRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
        }

        fj_range =fjRange;

        //冷凝风机温度范围
        if (!lnfjRange.equals(lnfj_range)) {
            lnfjrangeLog ="冷凝风机温度范围改变为";
            if (!lnfjRange.equals(lnfj_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(lnfjrangeLog + lnfjRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        lnfj_range =lnfjRange;
        return data;
    }



    @CrossOrigin
    @RequestMapping("/getData/204/realdata/diagnosis_server_design")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List <List<Double>> diagnosis_server_design(){
        List <List<Double>> ret =new ArrayList<>();
        ret.add(cold_range);
        ret.add(hot_range);
        return ret;
    }
    @CrossOrigin
    @PostMapping("/getData/204/realdata/diagnosis_server_design")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)

    public JSONObject diagnosis_server_design2(@RequestBody JSONObject data){
        String coldrangeLog = "";
        String hotrangeLog = "";

        List<List<Double>> params_diagnosis_server_design=(List<List<Double>>) data.get("params");

        List<String> user_diagnosis_server_design= (List<String>)data.get("user");


        List<Double> coldRange=params_diagnosis_server_design.get(0);
        List<Double> hotRange =params_diagnosis_server_design.get(1);

        String userName = user_diagnosis_server_design.get(0).toString();
        String userRole = user_diagnosis_server_design.get(1).toString();
        String time_operate = user_diagnosis_server_design.get(2).toString();

        if (!coldRange.equals(cold_range)) {
            coldrangeLog ="冷通道正常阈值范围改变为";
            if (!coldRange.equals(cold_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(coldrangeLog + coldRange );
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        cold_range =coldRange;

        if (!hotRange.equals(hot_range)) {
            hotrangeLog ="热通道正常阈值范围改变为";
            if (!coldRange.equals(hot_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(hotrangeLog + hotRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        hot_range =hotRange;
        return data;

    }


    Integer time_limit=6;
    @Autowired
    private alertService alertservice;

    @Autowired(required=false)
    private sitecoldService scService;
    @Autowired
    private sitecoldMapper sitecoldmapper;
    @CrossOrigin
    @RequestMapping("/getData/204/dataStatus_test")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Integer dataStatus_test() {

        String sql="select * from data_reasonable order by Value0 desc limit 6" ;
        sql.replace("6",time_limit.toString());
//        String sql2="select * from aicmd where CommandType='保底控制' " ;
        List <Map<String,Object>> list=jdbc.queryForList(sql);
        Integer cnt=0;
        for(Map<String,Object> c : list){
            cnt+=Integer.parseInt(c.get("Value0").toString());
        }
        if(cnt==time_limit){
            return 1;
        }else{
            return 0;
        }
    }
    //    Integer time_limit=6;
    @CrossOrigin
    @RequestMapping("/getData/204/dataStatus")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Integer dataStatus() {

        String sql="select * from data_reasonable order by Value0 desc limit 6" ;
        sql.replace("6",time_limit.toString());
//        String sql2="select * from aicmd where CommandType='保底控制' " ;
        List <Map<String,Object>> list=jdbc.queryForList(sql);
        Integer cnt=0;
        for(Map<String,Object> c : list){
            cnt+=Integer.parseInt(c.get("Value0").toString());
        }
        if(cnt.equals(time_limit)){
            return 1;
        }else{
            return 0;
        }
    }


    //    public Map<String,Object> cold_all_show= new HashMap<>();



}