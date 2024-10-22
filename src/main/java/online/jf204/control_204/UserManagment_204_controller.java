package online.jf204.control_204;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import online.jf204.entity.User;
import online.jf204.mapper.UserMapper;
import online.jf204.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class UserManagment_204_controller {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userservice;

    @CrossOrigin
    @PostMapping("204/UserManagmentAdd")
    @ResponseBody
    public String addUser(@RequestBody User data) {
        try{

            User user1= new User();
//            Integer id=Integer.parseInt(data.getId());
//            int id= data.getId();
            String name=data.getUserName();
            String role=data.getRole();
            String password=data.getPassword();
//            System.out.println(data);
            if(name.equals(null) || role.equals(null) || password.equals(null) ){
                return "wrong";
            }else{
                user1.setUserName(name);
                user1.setRole(role);
                user1.setPassword(password);
//            System.out.println(user1);
                userservice.save(user1);
                return  "Succeed";
            }

        }catch (Exception e){
            return "Failed";
        }
    }

    @CrossOrigin
    @PostMapping("204/UserManagmentDelete")
    @ResponseBody
    public String DeleteUser(@RequestBody User data) {
        try{
            String name=data.getUserName();
            String role=data.getRole();
            String password=data.getPassword();
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserName,name).eq(User::getRole,role).eq(User::getPassword,password);

            boolean success = userservice.remove(wrapper); // userService是用于操作User的服务类
            if (success) {
                // 删除成功
                return "Succeed";
            } else {
                // 删除失败
                return "Failed";
            }
        }catch (Exception e){
            return "Failed";
        }
    }

    @CrossOrigin
    @PostMapping("204/UserManagmentUpdate")
    @ResponseBody
    public String UpdateUser(@RequestBody List<User> userlist) {
        try{
            String name=userlist.get(0).getUserName();
//            Integer id=userlist.get(0).getId();
            String role=userlist.get(0).getRole();
            String password=userlist.get(0).getPassword();

            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserName,name).eq(User::getRole,role).eq(User::getPassword,password);
            User userToUpdate = userservice.getOne(wrapper);
            userservice.remove(wrapper);
            if (true) {
                String name0=userlist.get(1).getUserName();
//                Integer id=userlist.get(1).getId();
                String role0=userlist.get(1).getRole();
                String password0=userlist.get(1).getPassword();

                userToUpdate.setUserName(name0);
                userToUpdate.setPassword(password0);
                userToUpdate.setRole(role0);
                boolean success = userservice.save(userToUpdate);

                if (success) {
                    // 修改成功
                    return "success";
                } else {
                    // 修改失败
                    return "Failed";
                }
            }else {
                // 修改失败
                return "Failed";
            }
        }catch (Exception e){
            return "Failed";
        }
    }


    @CrossOrigin
    @PostMapping("204/UserManagmentSelect")
    @ResponseBody
    public List<User> SelectUser(@RequestBody List<String> test) {
        try{
            String vertify=test.get(0);
            if(vertify.equals("PKU")){
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                List<User> userlist= userMapper.selectList(wrapper);
                System.out.println(userlist);
                return userlist;


                }else {
                    // 修改失败
                    return null;
                }
            } catch (Exception e){
                return null;
        }
    }
}
