<?php
$con=mysql_connect("localhost","root","qwerasdf13","commercial_info");
  
#if (mysql_connect_errno($con))  
#{  
#   echo "Failed to connect to MySQL: " . mysqli_connect_error();
#}  

mysql_query("set names utf8");
mysql_query("use commercial_info"); 

$rq_dong=$_GET["rq_dong"];
$rq_type=$_GET["rq_type"];

$res = mysql_query("SELECT count(*)as count FROM commercial_info.restaurant_info_inseoul_refine where addr like '%$rq_dong%' and type like '%$rq_type%'");

if($res === FALSE) {
    die(mysql_error()); // TODO: better error handling
}
$result = array();

while($row = mysql_fetch_array($res)){
  array_push($result,
    array('count'=>$row[0]
    ));
}

echo json_encode(array("result"=>$result));

mysql_close($con);
?>
