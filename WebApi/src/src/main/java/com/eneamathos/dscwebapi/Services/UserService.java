package com.eneamathos.dscwebapi.Services;

import com.eneamathos.dscwebapi.Configuration.AsyncConfiguration;
import com.eneamathos.dscwebapi.DAL.Entities.UserEntity;
import com.eneamathos.dscwebapi.DAL.Repositories.IUserRepository;
import com.eneamathos.dscwebapi.Models.User;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final ModelMapper mapper;
    private final Logger logger;

    @Autowired
    public UserService(
            IUserRepository userRepository,
            ModelMapper mapper,
            Logger logger) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.logger = logger;
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<User> createUserAsync(User newUser) throws InterruptedException
    {
        if (newUser == null){
            throw new NullPointerException("User object is null!");
        }

        UserEntity entity = mapper.map(newUser, UserEntity.class);

        userRepository.save(entity);
        logger.info("User is saved to database. [UserId: {}]", entity.getId());

        User user = mapper.map(entity, User.class);

        return CompletableFuture.completedFuture(user);
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<Iterable<User>> findAsync() throws InterruptedException
    {
        Iterable<UserEntity> entities = userRepository.findAll();

        ArrayList<User> users = new ArrayList<User>();
        for (UserEntity entity : entities){
            users.add(mapper.map(entity, User.class));
        }

        return CompletableFuture.completedFuture(users);
    }

}
