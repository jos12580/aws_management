package com.tk.server.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tk.server.application.dto.UserListReqDTO;
import com.tk.server.domain.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-03-03
 */
public interface IUserService extends IService<User> {

    User get(String account, String password);

    Page<User> paged(UserListReqDTO params);

    void syncAccount();

    User getByAccount(String account);

    void removeBatchByUsername(String username);

    Page<User> paged(UserListReqDTO dto, int showed);

    List<User> getByRole(String role);
}
