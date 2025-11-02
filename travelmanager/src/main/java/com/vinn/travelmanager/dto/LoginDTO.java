package com.vinn.travelmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录DTO
 */
@Data
public class LoginDTO {
    @NotBlank(message = "用户名或邮箱不能为空")
    @Size(min = 3, max = 50, message = "用户名或邮箱长度应在3-50个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度应在6-20个字符之间")
    private String password;

    private Boolean remember;
}

