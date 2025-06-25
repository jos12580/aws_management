package com.tk.server.infrastructure.mapper;

import com.tk.server.domain.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2023-03-03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
