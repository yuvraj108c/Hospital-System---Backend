create table patient(
    id int auto_increment,
    fname varchar(55),
    lname varchar(55),
    phone int,
    address varchar(55),
    dob date,
    gender varchar(55),
    primary key(id)
);

create table department(
    id int auto_increment,
    name varchar(55),
    primary key(id)
);

create table users(
    id int auto_increment,
    fname varchar(55),
    lname varchar(55),
    phone int,
    address varchar(55),
    dob date,
    gender varchar(55),
    departmentid int,
    email varchar(55),
    password varchar(55),
    primary key(id),
    foreign key (departmentid) references department(id)
);

create table checkup(
    id int auto_increment,
    patientid int,
    doctorid int,
    reason varchar(255),
    diagnosis varchar(255),
    status varchar(55),
    date date,
    primary key(id),
    foreign key (patientid) references patient(id),
    foreign key (doctorid) references users(id)
);

create table specialtreatment(
    id int auto_increment,
    checkupid int,
    specialistid int,
    giventreatment varchar(255),
    date date,
    status varchar(55),
    departmentid int,
    primary key(id),
    foreign key(checkupid) references checkup(id),
    foreign key(specialistid) references users(id),
    foreign key(departmentid) references department(id)
);

insert into patient(fname,lname,phone,address,dob,gender) values ('john','doe','5123123','Curepipe','2020-12-24','male');
insert into patient(fname,lname,phone,address,dob,gender) values ('lara','croft','5342123','Savanne','2020-08-12','female');

insert into department(name) values ("ENT");
insert into department(name) values ("GENERAL");
insert into department(name) values ("RECEPTIONIST");

insert into users(fname,lname,phone,address,dob,gender,departmentid,email,password) values ('Adel','Rose','5123123','Curepipe','2020-12-24','male',1,"adel@gmail.com","827ccb0eea8a706c4c34a16891f84e7b");
insert into users(fname,lname,phone,address,dob,gender,departmentid,email,password) values ('Kaviraj','Gosaye','5342123','Savanne','2020-08-12','male',2,"kavirajl@gmail.com","827ccb0eea8a706c4c34a16891f84e7b");
insert into users(fname,lname,phone,address,dob,gender,departmentid,email,password) values ('Henri','Jason','5342123','Savanne','2020-08-12','male',2,"henri@gmail.com","827ccb0eea8a706c4c34a16891f84e7b");

insert into checkup(patientid,doctorid,reason,diagnosis,status,date) values(1,2,'Headache','Small disease diagnosed','incomplete','2020-12-06');
insert into checkup(patientid,doctorid,reason,diagnosis,status,date) values(2,1,'Vomit','Covid19','complete','2020-12-05');

insert into specialtreatment(checkupid,specialistid,giventreatment,status,departmentid,date) values(1,1,"special treatment for ear given",'complete',1,"2020-10-24");  
insert into specialtreatment(checkupid,specialistid,giventreatment,status,departmentid,date) values(2,2,"special treatment for nose given",'inccomplete',1,"2020-10-24");  