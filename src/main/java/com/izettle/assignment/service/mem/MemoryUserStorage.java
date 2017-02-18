package com.izettle.assignment.service.mem;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.izettle.assignment.exception.AuthenticationException;
import com.izettle.assignment.exception.InternalException;
import com.izettle.assignment.model.Token;
import com.izettle.assignment.model.User;
import com.izettle.assignment.service.UserStorage;
import com.izettle.assignment.util.HashingUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class MemoryUserStorage implements UserStorage {
    private static final long TOKEN_TTL = 60000L;
    private static final int SALT_LENGTH = 16;
    private static final Logger LOGGER = Logger.getLogger(MemoryUserStorage.class.getCanonicalName());
    private final Map<String, User> userStorage = new ConcurrentHashMap<>();
    private final Cache<String, Token> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(TOKEN_TTL * 5, TimeUnit.MILLISECONDS).build();
    private final Cache<String, Token> userTokenCache = CacheBuilder.newBuilder().expireAfterWrite(TOKEN_TTL * 5, TimeUnit.MILLISECONDS).build();

    @Override
    public boolean createUser(String userName, String password) {
        if (!userStorage.containsKey(userName)) {
            String salt = RandomStringUtils.random(SALT_LENGTH);
            try {
                User toStore = new User(userName, salt, HashingUtil.hashPassword(salt, password));
                userStorage.put(toStore.getUserName(), toStore);
                return true;
            } catch (NoSuchAlgorithmException e) {
                LOGGER.log(Level.WARNING, "Failed to create user", e);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Token userLogin(String userName, String password) throws AuthenticationException, InternalException {

        if (userStorage.containsKey(userName)) {
            User user = userStorage.get(userName);
            try {
                if (user.getHash().equals(HashingUtil.hashPassword(user.getSalt(), password))) {
                    Token currentToken = userTokenCache.getIfPresent(userName);
                    if (currentToken != null && currentToken.isValid()) {
                        return currentToken;
                    } else {
                        return issueToken(userName);
                    }
                } else {
                    throw new AuthenticationException("Login failed!");
                }
            } catch (NoSuchAlgorithmException e) {
                throw new InternalException(e);
            }
        } else {
            throw new AuthenticationException("User not found");
        }
    }


    @Override
    public Token renewToken(String userName, String token) throws AuthenticationException, InternalException {
        Token currentToken = getToken(token);
        if (currentToken != null && currentToken.getUserName().equals(userName) && currentToken.isValid()) {
            return issueToken(userName);
        } else {
            throw new AuthenticationException("Invalid token");
        }
    }

    @Override
    public Pair<Boolean, String> userLogout(String token, String user) {
        //If non supplied
        if (StringUtils.isEmpty(token) && StringUtils.isEmpty(user)) {
            return new ImmutablePair<>(false, null);
        }
        //Fill the blanks with best effort
        if (StringUtils.isEmpty(token)) {
            Token uToken = userTokenCache.getIfPresent(user);
            if (uToken != null) {
                token = uToken.getToken();
            }
        }
        if (StringUtils.isEmpty(user)) {
            Token tToken = tokenCache.getIfPresent(token);
            if (tToken != null) {
                user = tToken.getUserName();
            }
        }
        if (!StringUtils.isEmpty(token)) tokenCache.invalidate(token);
        if (!StringUtils.isEmpty(user)) userTokenCache.invalidate(user);
        return new ImmutablePair<>(StringUtils.isEmpty(user) || userStorage.containsKey(user), user);
    }

    private Token issueToken(String userName) {
        Token token = new Token(UUID.randomUUID().toString(), System.currentTimeMillis() + TOKEN_TTL, userName);
        tokenCache.put(token.getToken(), token);
        userTokenCache.put(userName, token);
        return token;
    }

    @Override
    public Token getToken(String token) {
        return tokenCache.getIfPresent(token);
    }

    @Override
    public User getUser(String userName) {
        return userStorage.get(userName);
    }
}
