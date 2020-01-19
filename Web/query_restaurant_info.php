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

$res = mysql_query("select * from restaurant_info_inseoul_refine where addr like '%$rq_dong%' and type like '%$rq_type%' limit 100");

if($res === FALSE) {
    die(mysql_error()); // TODO: better error handling
}
$result = array();

while($row = mysql_fetch_array($res)){
  array_push($result,
    array('name'=>$row[0],'addr'=>$row[1],'launch_date'=>$row[2],'type'=>$row[3], 'lat'=>$row[4], 'lng'=>$row[5]
    ));
}

echo json_encode(array("result"=>$result));

mysql_close($con);
?>
