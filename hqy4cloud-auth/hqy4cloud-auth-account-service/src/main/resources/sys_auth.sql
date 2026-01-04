/******************************************/
/*   DatabaseName = sys_auth   */
/*   TableName = account   */
/******************************************/
create table if not exists sys_auth.account
(
    id        bigint               not null comment 'id' primary key,
    client_id varchar(64)          not null comment '用于唯一标识每一个客户端(client)',
    username  varchar(16)          not null comment '用户名',
    phone     varchar(64)          null comment '手机',
    password  varchar(64)          null comment '密码,密文保存',
    email     varchar(64)          null comment '用户邮箱',
    id_card   varchar(64)          null comment '身份证',
    real_name varchar(64)          null comment '真实姓名',
    role      varchar(16)          null comment '用户角色',
    status    varchar(16)          not null,
    version   int        default 0 null comment '锁版本',
    deleted   tinyint(1) default 0 not null comment '是否删除',
    created   datetime             null comment '创建时间',
    updated   datetime             not null comment '更新时间',
    constraint udx_email
    unique (email, deleted),
    constraint udx_phonez
    unique (phone, deleted),
    constraint udx_username
    unique (username, deleted)
    ) comment '基础账户表';


/******************************************/
/*   DatabaseName = sys_auth   */
/*   TableName = oauth_client   */
/******************************************/
create table if not exists sys_auth.oauth_client
(
    client_id               varchar(64)          not null comment '用于唯一标识每一个客户端(client)' primary key,
    resource_ids            varchar(256)         null comment '客户端所能访问的资源id集合,多个资源时用逗号(,)分隔.',
    client_secret           varchar(256)         null comment '用于指定客户端(client)的访问密匙; 在注册时必须填写(也可由服务端自动生成).',
    scope                   varchar(256)         null comment '指定客户端申请的权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔,如: "read,write".',
    authorized_grant_types  varchar(256)         null comment '指定客户端支持的grant_type,可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔,如: "authorization_code,password".
在实际应用中,当注册时,该字段是一般由服务器端指定的,而不是由申请者去选择的,最常用的grant_type组合有: "authorization_code,refresh_token"(针对通过浏览器访问的客户端); "password,refresh_token"(针对移动设备的客户端).
implicit与client_credentials在实际中很少使用，可以根据自己的需要，在OAuth2.0 提供的地方进行扩展自定义的授权',
    web_server_redirect_uri varchar(256)         null comment '	客户端的重定向URI,可为空, 当grant_type为authorization_code或implicit时, 在Oauth的流程中会使用并检查与注册时填写的redirect_uri是否一致',
    authorities             varchar(256)         null comment '指定客户端所拥有的Spring Security的权限值',
    access_token_validity   int                  null comment '设定客户端的access_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时).',
    refresh_token_validity  int                  null comment '设定客户端的refresh_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天).',
    additional_information  varchar(4096)        null comment '这是一个预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据',
    autoapprove             varchar(256)         null comment '设置用户是否自动Approval操作, 默认值为 ''false'', 可选值包括 ''true'',''false'', ''read'',''write''.该字段只适用于grant_type="authorization_code"的情况,当用户登录成功后,若该值为''true''或支持的scope值,则会跳过用户Approve的页面, 直接授权.',
    status                  tinyint(1) default 1 not null comment '状态 是否可用',
    deleted                 tinyint(1) default 0 not null comment '状态 是否删除',
    version                 int        default 0 not null comment '乐观锁版本号',
    created                 datetime             null comment '创建时间',
    updated                 datetime             null comment '更新时间'
    ) comment 'oauth2商户客户端表';


/******************************************/
/*   DatabaseName = sys_auth   */
/*   TableName = tcc_fence_log   */
/******************************************/

create table if not exists sys_auth.tcc_fence_log
(
    xid          varchar(128) not null comment 'global id',
    branch_id    bigint       not null comment 'branch id',
    action_name  varchar(64)  not null comment 'action name',
    status       tinyint      not null comment 'status(tried:1;committed:2;rollbacked:3;suspended:4)',
    gmt_create   datetime(3)  not null comment 'create time',
    gmt_modified datetime(3)  not null comment 'update time',
    primary key (xid, branch_id)
    )
    charset = utf8mb4;

create index idx_gmt_modified
    on sys_auth.tcc_fence_log (gmt_modified);

create index idx_status
    on sys_auth.tcc_fence_log (status);


/******************************************/
/*   DatabaseName = sys_auth   */
/*   TableName = undo_log   */
/******************************************/

create table sys_auth.undo_log
(
    branch_id     bigint       not null comment 'branch transaction id',
    xid           varchar(128) not null comment 'global transaction id',
    context       varchar(128) not null comment 'undo_log context,such as serialization',
    rollback_info longblob     not null comment 'rollback info',
    log_status    int          not null comment '0:normal status,1:defense status',
    log_created   datetime(6)  not null comment 'create datetime',
    log_modified  datetime(6)  not null comment 'modify datetime',
    constraint ux_undo_log
        unique (xid, branch_id)
)
    comment 'AT transaction mode undo table' charset = utf8mb4;