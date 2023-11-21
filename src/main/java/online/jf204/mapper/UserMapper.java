package online.jf204.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.jf204.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}