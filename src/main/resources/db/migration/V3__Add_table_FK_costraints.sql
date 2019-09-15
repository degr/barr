alter table group_permissions
    add constraint FK_group_permissions_group
        foreign key (group_id) references `groups` (id);

alter table group_permissions
    add constraint FK_group_permissions_permission
        foreign key (permission_id) references permissions (id);

alter table users
    add constraint FK_user_group
        foreign key (group_id) references `groups` (id);

alter table user_permissions
    add constraint FK_user_permissions_permission
        foreign key (permission_id) references permissions (id);

alter table user_permissions
    add constraint FK_user_permissions_user
        foreign key (user_id) references users (id);