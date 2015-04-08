# MVD

user case protocol commu

when first LBB:
MVD: "enum"
ENUMERARE SENZORI (on notification / on new LBB)
--------------------------
LBB: "5"
MVD: "enum 1"
LBB: ID;[R/W]
MVD: insert into table
MVD: "enum 2"
LBB: ID;[R/W]
MVD: insert into table
...

are o tabela cu: 
ID_LBB ID_SENZOR TIP_SENZOR VALUE_FROM VALUE_TO LAST_MESSAGE TIMESTAMP_ENTRY

MAIN LOOP 
----------------------------------

pentru toate entry-urile din tabela sort by ID_LBB
MVD: "get ID_SENZOR"
LBB: "255"
MVD: insert into table

MVD: "set ID_SENZOR"
LBB: "ok/fail"
MVD: insert into table
....

EVENIMENTE
----------------------------------

on notification MVD/ID_LBB/Set/Get/Enum


2ND THREAD
---------------------------------

datele se duc in prima tabela + in a doua tabela
TIMESTAMP ID_LBB ID_SENZOR TIP_SENZOR VALUE_FROM VALUE_TO LAST_MESSAGE TIMESTAMP_SENT


SEND2CLOUD
-----------------------
webapi calls

foeach now()-timestamp_sent - put *

push notification pentru set




# LBB

scrie dani



#CLOUD

- TIMESTAMP ID_LBB ID_SENZOR TIP_SENZOR VALUE_FROM VALUE_TO LAST_MESSAGE TIMESTAMP_SENT

- for notification ID_LBB ID_SENZOR VALUE_TO TIMESTAMP_NOTIFY
