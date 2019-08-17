package com.scnsoft.permissions.persistence.repository;

import com.scnsoft.permissions.persistence.entity.AdditionalPermission;
import com.scnsoft.permissions.persistence.entity.CompositePermissionId;
import com.scnsoft.permissions.persistence.entity.Permission;
import com.scnsoft.permissions.persistence.entity.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class AdditionalPermissionRepositoryTest {

    @Autowired
    private AdditionalPermissionRepository additionalPermissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Transactional
    @Test
    public void test() {
        User user = userRepository.findById(3l).get();
        Permission permission = permissionRepository.findById(1l).get();
        CompositePermissionId compositePermissionId = new CompositePermissionId(user.getId(), permission.getId());
        AdditionalPermission additionalPermission = new AdditionalPermission();
        additionalPermission.setId(compositePermissionId);
        additionalPermission.setUser(user);
        additionalPermission.setPermission(permission);
        additionalPermission.setEnabled(true);
        AdditionalPermission save = additionalPermissionRepository.save(additionalPermission);
        Assert.assertNotNull(save);
    }

    @After
    public void tearDown() throws Exception {
    }
}