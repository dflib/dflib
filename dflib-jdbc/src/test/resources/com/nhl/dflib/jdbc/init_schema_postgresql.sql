CREATE TABLE "t1" (
    "id" bigint primary key,
     "name" varchar(100),
      "salary" double precision )
--
CREATE TABLE "t2" (
    "bigint" bigint,
    "int" int,
    "double" double precision,
    "boolean" boolean,
    "string" varchar(100),
    "timestamp" timestamp,
    "date" date,
    "time" time,
    "bytes" BYTEA )
--
/* Mandatory primitive */
CREATE TABLE "t3" (
    "int" int not null,
    "long" bigint not null,
    "double" double precision not null,
    "boolean" boolean not null
)
--
CREATE SEQUENCE "t1_audit_seq"
--
CREATE TABLE "t1_audit" (
    "id" bigint primary key DEFAULT NEXTVAL ('"t1_audit_seq"'),
    "op" VARCHAR(10) not null,
    "op_id" bigint not null
)
--
CREATE OR REPLACE FUNCTION insert_procedure() RETURNS trigger
AS $$
begin
  INSERT INTO t1_audit (op_id, op) values (NEW.id, 'INSERT');
  RETURN NEW;
  end;
$$
LANGUAGE PLPGSQL
--
CREATE TRIGGER t1_insert_trigger
 AFTER INSERT ON t1
 for each row
 execute procedure insert_procedure()
--
CREATE OR REPLACE FUNCTION update_procedure() RETURNS trigger
AS $$
begin
    INSERT INTO t1_audit (op_id, op) values (NEW.id, 'UPDATE');
    return NEW;
    end;
$$
LANGUAGE PLPGSQL
--
CREATE TRIGGER t1_update_trigger
 AFTER UPDATE ON t1
 for each row
  execute procedure update_procedure();
--
CREATE OR REPLACE FUNCTION delete_procedure() RETURNS trigger
AS $$
begin
    INSERT INTO t1_audit (op_id, op) values (OLD.id, 'DELETE');
    return OLD;
    end;
$$
LANGUAGE PLPGSQL
--
CREATE TRIGGER t1_delete_trigger
 AFTER DELETE ON t1
 for each row
  execute procedure delete_procedure();
