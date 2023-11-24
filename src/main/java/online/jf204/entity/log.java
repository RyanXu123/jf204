package online.jf204.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class log {
    @TableId
//    private Integer id;
    private String datacenter_room;
    private String content;
    private String userName;
    private String userRole;
    private String time;



}
