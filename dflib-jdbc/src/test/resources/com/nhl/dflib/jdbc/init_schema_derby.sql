CREATE TABLE "t1" ("id" bigint primary key, "name" varchar(100), "salary" double);

CREATE TABLE "t2" (
    "bigint" bigint,
    "int" int,
    "double" double,
    "boolean" boolean,
    "string" varchar(100),
    "timestamp" timestamp,
    "date" date,
    "time" time,
    "bytes" VARCHAR(100) FOR BIT DATA );

/* Mandatory primitive */
CREATE TABLE "t3" (
    "int" int not null,
    "long" bigint not null,
    "double" double not null,
    "boolean" boolean not null
);


CREATE TABLE "t1_audit" (
    "id" bigint primary key GENERATED ALWAYS AS IDENTITY,
    "op" VARCHAR(10) not null,
    "op_id" bigint not null
);

CREATE TRIGGER "t1_insert_trigger" AFTER
INSERT ON "t1"
referencing new as inserted
for each row
INSERT INTO "t1_audit" ("op_id", "op") values (inserted."id", 'INSERT');

CREATE TRIGGER "t1_update_trigger" AFTER
UPDATE ON "t1"
referencing new as updated
for each row
INSERT INTO "t1_audit" ("op_id", "op") values (updated."id", 'UPDATE');