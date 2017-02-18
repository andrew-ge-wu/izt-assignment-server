package com.izettle.assignment.service.mem;

import com.izettle.assignment.model.Event;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.izettle.assignment.model.Event.EventType.AccessResource;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class MemoryDataStorageTest {
    private MemoryDataStorage memoryDataStorage;

    @Before
    public void setUp() throws Exception {
        this.memoryDataStorage = new MemoryDataStorage();
    }

    @Test
    public void testIO() throws Exception {
        String userName = RandomStringUtils.randomAlphanumeric(10);
        memoryDataStorage.addEvent(userName, AccessResource);
        List<Event> result = memoryDataStorage.readEvent(userName,null, 10);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(AccessResource, result.get(0).getType());
        Thread.sleep(10);
        memoryDataStorage.addEvent(userName, AccessResource);
        result = memoryDataStorage.readEvent(userName,null, 10);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(AccessResource, result.get(1).getType());
        Assert.assertTrue(result.get(0).getDate().after(result.get(1).getDate()));

    }
}