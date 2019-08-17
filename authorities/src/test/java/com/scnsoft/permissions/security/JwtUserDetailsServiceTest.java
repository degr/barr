package com.scnsoft.permissions.security;

import com.scnsoft.permissions.dto.UserDTO;
import com.scnsoft.permissions.security.jwt.JwtUserDetailsService;
import com.scnsoft.permissions.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtUserDetailsServiceTest {

    @Autowired
    private JwtUserDetailsService userDetailsService;


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadUserByUsername() {


        UserDetails oleg = userDetailsService.loadUserByUsername("Oleg");
        Assert.assertFalse(oleg.getAuthorities().isEmpty());
    }
}