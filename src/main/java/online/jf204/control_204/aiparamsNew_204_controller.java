package online.jf204.control_204;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import online.jf204.entity.log;
import online.jf204.service.logService;
import online.jf204.mapper.logMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Controller
public class aiparamsNew_204_controller {
    @Autowired
    private logMapper logMapper;

    String hot_max="26.5";
    String ai_range="1";
    String ai_time="30";

    String sf_up_set="24";
    String sf_down_set="26";


    @CrossOrigin
    @PostMapping("/getData/204/aiparams-1")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String, Object> getdata204_aiparams1(@RequestBody JSONObject data){


            Map<String, Object> ret = new LinkedHashMap<>();
        JSONObject content = data.getJSONObject("content");
        JSONObject userInfo = data.getJSONObject("user");

        // 前端传来的数据
        String newHotMax = content.get("热点检查阈值").toString();
        String newAiRange = content.get("AI群控控制范围").toString();
        String newAiTime = content.get("AI群控控制周期").toString();
        String newSfUpSet = content.get("送风上阈值范围").toString();
        String newSfDownSet = content.get("送风下阈值范围").toString();

        String userName = userInfo.getString("userName");
        String userRole = userInfo.getString("userRole");
        String timeOperate = userInfo.getString("time_operate");


        // 检查并更新全局变量及数据库
        checkAndUpdate("热点检查阈值更改为", hot_max, newHotMax, userName, userRole, timeOperate);
        checkAndUpdate("AI群控控制范围更改为", ai_range, newAiRange, userName, userRole, timeOperate);
        checkAndUpdate("AI群控控制周期更改为", ai_time, newAiTime, userName, userRole, timeOperate);
        checkAndUpdate("送风上阈值范围更改为", sf_up_set, newSfUpSet, userName, userRole, timeOperate);
        checkAndUpdate("送风下阈值范围更改为", sf_down_set, newSfDownSet, userName, userRole, timeOperate);

        // 将所有数据添加到返回的Map中
        ret.put("热点检查阈值", newHotMax);
        ret.put("AI群控控制范围", newAiRange);
        ret.put("AI群控控制周期", newAiTime);
        ret.put("送风上阈值", newSfUpSet);
        ret.put("送风下阈值", newSfDownSet);

        return ret;
    }
    private void checkAndUpdate(String key, String currentValue, String newValue, String userName, String userRole, String timeOperate) {
        if (!newValue.equals(currentValue)) {
            // 更新全局变量
            switch (key) {
                case "热点检查阈值更改为":
                    hot_max = newValue;
                    break;
                case "AI群控控制范围更改为":
                    ai_range = newValue;
                    break;
                case "AI群控控制周期更改为":
                    ai_time = newValue;
                    break;
                case "送风上阈值范围更改为":
                    sf_up_set = newValue;
                    break;
                case "送风下阈值范围更改为":
                    sf_down_set = newValue;
                    break;
            }
            //写入数据库
            log logs = new log();
            logs.setDatacenter_room("JF204");
            logs.setContent(key + newValue);
            logs.setUserName(userName);
            logs.setUserRole(userRole);
            logs.setTime(timeOperate);

            logMapper.insert(logs);

            /*
            使用lambdaquerywrapper构建查询条件
            LambdaQueryWrapper<log> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(log::getDatacenter_room, "JF204")
                    .eq(log::getContent, key + newValue)
                    .eq(log::getUserName, userName)
                    .eq(log::getUserRole, userRole)
                    .eq(log::getTime, timeOperate);
*/
        }
    }

    String openAi="0";
    String content="0";
    String time="0";
    String restart="0";
    @CrossOrigin
    @PostMapping("/getData/204/aicontrol-1")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<String> aicontrol2(@RequestBody List<String> data) {
        String aiopenlog = "";
        String rebootlog = "";

        String openAiTemp = data.get(0).toString();
        String restartTemp = data.get(1).toString();
        //return data;
        String userName = data.get(2).toString();
        String userRole = data.get(3).toString();
        String time_operate = data.get(4).toString();

        if (restartTemp.equals("1")) {
            rebootlog = "一键恢复开启";
            if (!restartTemp.equals(restart)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(rebootlog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (restart.equals("0")) {
            rebootlog = "一键恢复关闭";
            if (!restartTemp.equals(restart)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(rebootlog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        restart=restartTemp;


        if (openAi.equals("1")) {
            aiopenlog = "AI开启";
            if (!openAiTemp.equals(openAi)){
                log log2 = new log();
                log2.setDatacenter_room("JF204");
                log2.setContent(aiopenlog);
                log2.setUserName(userName);
                log2.setUserRole(userRole);
                log2.setTime(time_operate);

                logMapper.insert(log2);
            }
        } else if (openAi.equals("0")) {
            aiopenlog = "AI关闭";
            if (!openAiTemp.equals(openAi)){
                log log2 = new log();
                log2.setDatacenter_room("JF204");
                log2.setContent(aiopenlog);
                log2.setUserName(userName);
                log2.setUserRole(userRole);
                log2.setTime(time_operate);

                logMapper.insert(log2);
            }
        }
        openAi=openAiTemp;
        return data;
    }

}
