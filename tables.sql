create table user
(id_user int8 primary key,
name varchar(50) not null,
password varchar(50) not null,
bi varchar(25) not null,
age int1,
email varchar(30) not null,
account_balance int4 default 100,
constraint uni_bi unique(bi),
constraint check_person check (account_balance >= 0)
);

create table project
(id_project int8 primary key,
name varchar(50) not null,
description varchar(400) not null,
limit_date varchar(30) not null,
target_value int8 not null,
current_value int8 default 0,
enterprise varchar(30),
id_user int8,
constraint fk_admin foreign key(id_user) references user(id_user) on delete cascade on update cascade,
constraint check_tg_value check (target_value >=1)
);

create table project_has_user
(id_project int8 primary key,
id_user int8 primary key,
constraint fk_project_user foreign key(id_project) references project(id_project) on delete cascade on update cascade,
constraint fk_user_project foreign key(id_user) references user(id_user) on delete cascade on update cascade
);

create table reward 
(id_reward int8 primary key,
description varchar(200) not null,
min_value int3 not null,
id_project int8,
constraint fk_project foreign key(id_project) references project(id_project) on delete cascade on update cascade,
constraint check_min_value check(min_value >=1)
);

create table donation
(id_donation int8 primary key,
pledge_value int4 not null,
id_user int8,
id_reward int8,
id_alternative int8,
constraint fk_don_user foreign key(id_user) references user(id_user) on delete cascade on update cascade,
constraint fk_reward foreign key(id_reward) references reward(id_reward) on delete cascade on update cascade,
constraint fk_alternative foreign key(id_alternative) references alternative(id_alternative) on delete cascade on update cascade,
constraint check_pledge check(pledge_value >=1)
);

create table alternative
(id_alternative int8 primary key,
description varchar(200) not null,
n_votes int4 default 0,
multiplier int1 not null, 
id_project int8,
constraint fk_al_proj foreign key(id_project) references project(id_project) on delete cascade on update cascade,
constraint check_multiplier check(multiplier >=1)
);

create table message
(id_message int8 primary key,
text varchar(400),
id_user int8,
id_project int8,
constraint fk_msg_user foreign key(id_user) references user(id_user) on delete cascade on update cascade,
constraint fk_msg_project foreign key(id_project) references project(id_project) on delete cascade on update cascade
);