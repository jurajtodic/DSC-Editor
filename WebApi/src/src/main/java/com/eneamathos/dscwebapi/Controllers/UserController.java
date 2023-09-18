package com.eneamathos.dscwebapi.Controllers;

import com.eneamathos.dscwebapi.Common.ApiConstants;
import com.eneamathos.dscwebapi.Configuration.AsyncConfiguration;
import com.eneamathos.dscwebapi.Controllers.DTOs.UserResponseDto;
import com.eneamathos.dscwebapi.Models.User;
import com.eneamathos.dscwebapi.Services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping(path = ApiConstants.ApiPathV1 + "/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper mapper;

    @Autowired
    public UserController(
            UserService userService,
            ModelMapper mapper
    ){
        this.userService = userService;
        this.mapper = mapper;
    }


    @PostMapping(path = "/create")
    @Async(AsyncConfiguration.AsyncExecutorName)
    public @ResponseBody CompletableFuture<UserResponseDto> createAsync(@RequestBody User newUser) throws ExecutionException, InterruptedException  {

        // TODO: Add model validation
        User result = userService.createUserAsync(newUser).get();

        return CompletableFuture.completedFuture(mapper.map(result, UserResponseDto.class));
    }

    @GetMapping(path = "")
    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<ArrayList<UserResponseDto>> findAsync() throws ExecutionException, InterruptedException {

        CompletableFuture<Iterable<User>> users = userService.findAsync();

        // TODO: Check if there is any solution to map models from array
        ArrayList<UserResponseDto> response = new ArrayList<UserResponseDto>();
        for(User user : users.get()){
            UserResponseDto userDto = mapper.map(user, UserResponseDto.class);
            response.add(userDto);
        }

        return CompletableFuture.completedFuture(response);

    }


}
