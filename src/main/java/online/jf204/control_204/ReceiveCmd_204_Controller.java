package online.jf204.control_204;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReceiveCmd_204_Controller {
    @PostMapping("/ai/trainning")
    @ResponseBody
    public String ai_instruction(){
        return "success";
    }
}
