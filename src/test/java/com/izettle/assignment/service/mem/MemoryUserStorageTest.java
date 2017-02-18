package com.izettle.assignment.service.mem;

import com.izettle.assignment.exception.AuthenticationException;
import com.izettle.assignment.model.Token;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class MemoryUserStorageTest {

    private MemoryUserStorage memoryUserStorage;

    @Before
    public void setUp() throws Exception {
        this.memoryUserStorage = new MemoryUserStorage();
    }

    @Test
    public void testCreate() throws Exception {
        String userName = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomAlphanumeric(10);
        Assert.assertTrue(memoryUserStorage.createUser(userName, password));
        Token token = memoryUserStorage.userLogin(userName, password);
        Assert.assertTrue(token.isValid());
        Assert.assertFalse(memoryUserStorage.createUser(userName, password));

    }

    @Test
    public void testLogin() throws Exception {
        String userName = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomAlphanumeric(10);
        Assert.assertTrue(memoryUserStorage.createUser(userName, password));
        Token token = memoryUserStorage.userLogin(userName, password);
        Assert.assertTrue(token.isValid());
        Token token2 = memoryUserStorage.userLogin(userName, password);
        Assert.assertEquals(token.getToken(), token2.getToken());
        Assert.assertEquals(token.getExpireTime(), token2.getExpireTime());
        Assert.assertEquals(token.getUserName(), token2.getUserName());
    }

    @Test
    public void testRenewToken() throws Exception {
        String userName = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomAlphanumeric(10);
        Assert.assertTrue(memoryUserStorage.createUser(userName, password));
        Token token = memoryUserStorage.userLogin(userName, password);
        Assert.assertTrue(token.isValid());
        Thread.sleep(10);
        Token token2 = memoryUserStorage.renewToken(userName, token.getToken());
        Assert.assertNotEquals(token.getToken(), token2.getToken());
        Assert.assertNotEquals(token.getExpireTime(), token2.getExpireTime());
        Assert.assertEquals(token.getUserName(), token2.getUserName());
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginFail() throws Exception {
        String userName = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomAlphanumeric(10);
        Assert.assertTrue(memoryUserStorage.createUser(userName, password));
        Token token = memoryUserStorage.userLogin(userName, password);
        Assert.assertTrue(token.isValid());
        memoryUserStorage.userLogin(userName, RandomStringUtils.randomAlphanumeric(10));
    }

    @Test
    public void testLogout() throws Exception {
        String userName = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomAlphanumeric(10);
        Assert.assertTrue(memoryUserStorage.createUser(userName, password));
        Token token = memoryUserStorage.userLogin(userName, password);
        //Logout with userName
        memoryUserStorage.userLogout(null, userName);
        Assert.assertNull(memoryUserStorage.getToken(token.getToken()));
        token = memoryUserStorage.userLogin(userName, password);
        //Logout with token
        memoryUserStorage.userLogout(token.getToken(), null);
        Assert.assertNull(memoryUserStorage.getToken(token.getToken()));
    }
}