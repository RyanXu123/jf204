package online.jf204.dto;

import online.jf204.entity.User;
import lombok.Data;

@Data
public class LoginVo {
    private Integer id;
    private String token;
    private User user;

}
