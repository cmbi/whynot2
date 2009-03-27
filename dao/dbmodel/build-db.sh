#!/bin/bash
dropdb whynot;
dropuser whynot_owner;
createuser -A -D whynot_owner;
createdb -O whynot_owner whynot;
psql -f rdb.sql whynot;
