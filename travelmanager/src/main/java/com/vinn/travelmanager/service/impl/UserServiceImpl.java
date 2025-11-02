package com.vinn.travelmanager.service.impl;

import com.vinn.travelmanager.common.ResultCode;
import com.vinn.travelmanager.dto.LoginDTO;
import com.vinn.travelmanager.dto.RegisterDTO;
import com.vinn.travelmanager.entity.User;
import com.vinn.travelmanager.mapper.UserMapper;
import com.vinn.travelmanager.service.UserService;
import com.vinn.travelmanager.util.JwtUtil;
import com.vinn.travelmanager.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public User register(RegisterDTO registerDTO) {
        // 验证密码一致性
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException(ResultCode.PARAM_ERROR.getMessage() + ": 两次输入的密码不一致");
        }

        // 验证用户协议
        if (registerDTO.getAgreement() == null || !registerDTO.getAgreement()) {
            throw new RuntimeException(ResultCode.PARAM_ERROR.getMessage() + ": 请先同意用户协议和隐私政策");
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException(ResultCode.USER_ALREADY_EXISTS.getMessage());
        }

        // 检查邮箱是否已存在
        existingUser = userMapper.selectByEmail(registerDTO.getEmail());
        if (existingUser != null) {
            throw new RuntimeException(ResultCode.EMAIL_ALREADY_EXISTS.getMessage());
        }

        // 如果提供了手机号，检查手机号是否已存在
        if (StringUtils.hasText(registerDTO.getPhone())) {
            existingUser = userMapper.selectByPhone(registerDTO.getPhone());
            if (existingUser != null) {
                throw new RuntimeException(ResultCode.PHONE_ALREADY_EXISTS.getMessage());
            }
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(StringUtils.hasText(registerDTO.getPhone()) ? registerDTO.getPhone() : null);
        user.setPassword(PasswordUtil.encrypt(registerDTO.getPassword()));

        // 插入用户
        int result = userMapper.insert(user);
        if (result > 0) {
            // 返回用户信息（不包含密码）
            user.setPassword(null);
            return user;
        } else {
            throw new RuntimeException(ResultCode.FAILED.getMessage() + ": 注册失败");
        }
    }

    @Override
    public User login(LoginDTO loginDTO) {
        // 根据用户名或邮箱查询用户
        User user = userMapper.selectByUsernameOrEmail(loginDTO.getUsername(), loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException(ResultCode.USERNAME_OR_PASSWORD_ERROR.getMessage());
        }

        // 验证密码
        if (!PasswordUtil.verify(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException(ResultCode.USERNAME_OR_PASSWORD_ERROR.getMessage());
        }

        // 返回用户信息（不包含密码）
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}

