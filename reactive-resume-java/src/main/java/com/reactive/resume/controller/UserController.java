package com.reactive.resume.controller;

import com.reactive.resume.common.Result;
import com.reactive.resume.dto.user.UpdateUserDto;
import com.reactive.resume.dto.user.UserDto;
import com.reactive.resume.service.UserService;
import com.reactive.resume.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * 用户控制器
 * 
 * @author Reactive Resume Team
 */
@Tag(name = "用户管理", description = "用户信息相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserDto> getCurrentUser(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        return Result.success(userService.getCurrentUser(userId));
    }

    @Operation(summary = "更新用户信息")
    @PatchMapping("/me")
    public Result<UserDto> updateUser(@Valid @RequestBody UpdateUserDto updateUserDto,
                                    HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        return Result.success(userService.updateUser(userId, updateUserDto));
    }

    @Operation(summary = "删除用户账户")
    @DeleteMapping("/me")
    public Result<Void> deleteUser(HttpServletRequest request, HttpServletResponse response) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        userService.deleteUser(userId);
        return Result.success();
    }
}
