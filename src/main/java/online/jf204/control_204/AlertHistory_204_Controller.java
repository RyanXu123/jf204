package online.jf204.control_204;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import online.jf204.entity.alert;
import online.jf204.service.alertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AlertHistory_204_Controller {
    @Autowired
    private alertService alertservice;
    @CrossOrigin
    @RequestMapping("/getData/204/alert_history")
    @ResponseBody
    public List<alert> alert_history(){
        LambdaQueryWrapper<alert> andWrapper = new LambdaQueryWrapper<>();
        andWrapper.last("limit 1000");
        List <alert> list =alertservice.list(andWrapper);
//        return new HashMap<>();
        return list;
    }

    @CrossOrigin
    @PostMapping("/getData/204/alert_history")
    @ResponseBody
    public List<alert> alert_history(@RequestBody List<String> data){
        String start_time=data.get(0);
        String end_time=data.get(1);
        LambdaQueryWrapper<alert> andWrapper = new LambdaQueryWrapper<>();
        andWrapper.ge(alert::getSampleTime,start_time).lt(alert::getSampleTime,end_time);
        List <alert> list =alertservice.list(andWrapper);
//        return new HashMap<>();
        return list;
    }

}
