<?php

$output["status"] = TRUE;
$output["msg"] = "";

if (empty($_POST['area_coordinate'])) {
    $output["status"] = FALSE;
    $output["errors"]['area_coordinate'] = "Area coordinate field is required..";
}
if (empty($_POST['area_name'])) {
    $output["status"] = FALSE;
    $output["errors"]['area_name'] = "Area name field is required..";
}
if (empty($_POST['address'])) {
    $output["status"] = FALSE;
    $output["errors"]['address'] = "Address field is required..";
}
if (empty($_POST['postalcode'])) {
    $output["status"] = FALSE;
    $output["errors"]['postalcode'] = "Postal Code field is required..";
}

if ($output["status"] == FALSE) {
    $output["msg"] = "Invalid field arguments";
    echo json_encode($output);
    exit;
}

$area_coordinate = $_POST['area_coordinate'];
$area_name = $_POST['area_name'];
$address = $_POST['address'];
$postalcode = $_POST['postalcode'];


#mysql server and database variable define
$host = "localhost";
$user = "root";
$password = "";
$database = "test";

#conect mysql server
mysql_connect($host, $user, $password);
#select database from mysql server
mysql_select_db($database);

#inset query of area_details table.
$insert_query = "insert into area_details (id,area_coordinate,area_name,address,postalcode) values(null,'$area_coordinate','$area_name','$address','$postalcode')";

#execute query on mysql 
$query = mysql_query($insert_query);

#affected row 
$row = mysql_affected_rows();

#check record is inserted or not 
# if $row is greter then 0 than record is successfully inserted otherwise any problem while inserting a record.
if ($row > 0) {
    #send response
    $output['msg'] = "Record is successfully inserted..";
    echo json_encode($output);
    exit;
} else {
    #send response
    $output['status'] = FALSE;
    $output['msg'] = "Some Problem while insert record";
    $output['error'] = mysql_error();
    echo json_encode($output);
    exit;
}