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
CREATE TABLE "t3" ("int" int not null);