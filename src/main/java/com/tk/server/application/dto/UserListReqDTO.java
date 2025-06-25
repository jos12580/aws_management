package com.tk.server.application.dto;

import com.tk.common.vo.AbstractPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserListReqDTO extends AbstractPageDTO implements Serializable {


    @Schema(hidden = true)
    private String username;

}
