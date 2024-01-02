CREATE TABLE t1 (
id bigint primary key,
 name varchar(100),
 salary double
 )
--
CREATE TABLE t2 (
	`bigint` bigint,
    `int` int,
    `double` double,
    `boolean` tinyint(1),
    `string` varchar(100),
    `timestamp` timestamp NULL,
    `date` date,
    `time` time,
    `bytes` VARBINARY(100))
--
/* Mandatory primitive */
CREATE TABLE t3 (
    `int` int not null,
    `long` bigint not null,
    `double` double not null,
    `boolean` tinyint(1) not null default 1
)
--
CREATE TABLE t1_audit (
    id bigint primary key AUTO_INCREMENT,
    op VARCHAR(10) not null,
    op_id bigint not null
)
--
CREATE TRIGGER t1_insert_trigger AFTER INSERT ON t1
for each row
INSERT INTO t1_audit (op_id, op) values (NEW.id, 'INSERT')
--
CREATE TRIGGER t1_update_trigger AFTER UPDATE ON t1
for each row
INSERT INTO t1_audit (op_id, op) values (NEW.id, 'UPDATE')
--
CREATE TRIGGER t1_delete_trigger AFTER DELETE ON t1
for each row
INSERT INTO t1_audit (op_id, op) values (OLD.id, 'DELETE')