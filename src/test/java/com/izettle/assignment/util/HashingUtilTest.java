package com.izettle.assignment.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class HashingUtilTest {

    @Test
    public void testHashing() throws Exception {
        String salt = RandomStringUtils.randomAlphanumeric(32);
        String password = RandomStringUtils.randomAlphanumeric(32);
        String hash = HashingUtil.hashPassword(salt, password);
        Assert.assertEquals(hash, HashingUtil.hashPassword(salt, password));
    }
}