<?php

#mysql server and database variable define
$host = "localhost";
$user = "root";
$password = "";
$database = "test";

#conect mysql server
mysql_connect($host, $user, $password);
#select database from mysql server
mysql_select_db($database);

#select query from area_details table
$select_query = "select id,area_coordinate from area_details";

#fire select query on mysql server
$query = mysql_query($select_query);

$data= array();
while($row = mysql_fetch_array($query) ){
	$data[$row['id']] = json_decode($row['area_coordinate']);
}

$output['status'] = true;
$output['data'] = $data;
echo json_encode($output);
