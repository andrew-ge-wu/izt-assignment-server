package com.izettle.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest({ResourceController.class, ACLController.class})
public class ResourceControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;
    private ObjectMapper objMapper;

    @Before
    public void setUp() throws Exception {
        this.objMapper = new ObjectMapper();
    }

    @Test
    public void allEvents() throws Exception {
        String userName = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomAlphanumeric(10);
        mvc.perform(get("/register?user=" + userName + "&password=" + password)).andExpect(status().isCreated());
        MvcResult result = mvc.perform(get("/login?user=" + userName + "&password=" + password)).andExpect(status().isOk()).andReturn();
        LinkedHashMap token = objMapper.readValue(result.getResponse().getContentAsString(), LinkedHashMap.class);

        result = mvc.perform(get("/user/" + userName + "/event?token=" + token.get("token"))).andExpect(status().isOk()).andReturn();

        List events = objMapper.readValue(result.getResponse().getContentAsString(), ArrayList.class);
        Assert.assertEquals(3, events.size());
        mvc.perform(get("/logout?token=" + token.get("token"))).andExpect(status().isOk());
        mvc.perform(get("/user/" + userName + "/event?token=" + token.get("token"))).andExpect(status().isForbidden());

    }



    @Test
    public void successfulLogin() throws Exception {
        String userName = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomAlphanumeric(10);
        mvc.perform(get("/register?user=" + userName + "&password=" + password)).andExpect(status().isCreated());
        MvcResult result = mvc.perform(get("/login?user=" + userName + "&password=" + password)).andExpect(status().isOk()).andReturn();
        LinkedHashMap token = objMapper.readValue(result.getResponse().getContentAsString(), LinkedHashMap.class);

        result = mvc.perform(get("/user/" + userName + "/event/LoginSuccess?token=" + token.get("token"))).andExpect(status().isOk()).andReturn();

        List events = objMapper.readValue(result.getResponse().getContentAsString(), ArrayList.class);
        Assert.assertEquals(1, events.size());
        mvc.perform(get("/logout?token=" + token.get("token"))).andExpect(status().isOk());
    }
}