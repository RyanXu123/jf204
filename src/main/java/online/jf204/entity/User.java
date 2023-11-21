package online.jf204.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class User {
    @TableId
    private Integer id;
    private String Role;
    private String UserName;
    private String Password;
}
