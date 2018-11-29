#! /bin/bash

SCRIPT=`realpath $0`
SCRIPTDIR=`dirname $SCRIPT`
DATABASE=test_postgis
TABLE=polygons

createdb $DATABASE
psql $DATABASE -c "CREATE EXTENSION postgis"
psql $DATABASE -c "CREATE TABLE $TABLE(geom geometry)"
psql $DATABASE -c "COPY $TABLE FROM '$SCRIPTDIR/modules/core/target/test-classes/testdata/intersection.wkt'"
psql $DATABASE -c "SELECT ST_UnaryUnion(ST_Collect(geom)) FROM $TABLE"
