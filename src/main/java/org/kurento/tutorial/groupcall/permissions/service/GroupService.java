package org.kurento.tutorial.groupcall.permissions.service;

import org.kurento.tutorial.groupcall.permissions.converter.GroupConverter;
import org.kurento.tutorial.groupcall.permissions.dto.GroupDTO;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.Group;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.GroupRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupService extends BaseCrudService<Group, GroupDTO, Long> {
    public GroupService(GroupRepository groupRepository, GroupConverter groupConverter) {
        super(groupRepository, groupConverter);
    }
}