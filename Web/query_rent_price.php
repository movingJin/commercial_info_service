<?php
$con=mysql_connect("localhost","root","qwerasdf13","commercial_info");
  
#if (mysql_connect_errno($con))  
#{  
#   echo "Failed to connect to MySQL: " . mysqli_connect_error();
#}  

mysql_query("set names utf8");
mysql_query("use commercial_info"); 

$rq_dong=$_GET["rq_dong"];

$res = mysql_query("select avg(avg_unitPrice)as avg_unitPrice from 
(select addr ,count(left(addr,12)),avg(unit_price)as avg_unitPrice from rent_price_seoul group by left(addr,12)) as rent_price
where addr like '%$rq_dong%'");

if($res === FALSE) {
    die(mysql_error()); // TODO: better error handling
}
$result = array();

while($row = mysql_fetch_array($res)){
  array_push($result,
    array('avg_unitPrice'=>$row[0]
    ));
}

echo json_encode(array("result"=>$result));

mysql_close($con);
?>
