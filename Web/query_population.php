<?php
$con=mysql_connect("localhost","root","qwerasdf13","commercial_info");
  
#if (mysql_connect_errno($con))  
#{  
#   echo "Failed to connect to MySQL: " . mysqli_connect_error();
#}  

mysql_query("set names utf8");
mysql_query("use commercial_info"); 

$rq_dong=$_GET['rq_dong'];

$res = mysql_query("select * from population_seoul_refine where addr like '%$rq_dong%'");

if($res === FALSE) {
    die(mysql_error()); // TODO: better error handling
}
$result = array();

while($row = mysql_fetch_array($res)){
  array_push($result,
    array('SPOT_CD'=>$row[0],'addr'=>$row[1],'t7'=>$row[2],'t8'=>$row[3],'t9'=>$row[4],'t10'=>$row[5],'t11'=>$row[6],'t12'=>$row[7],'t13'=>$row[8],'t14'=>$row[9],'t15'=>$row[10],'t16'=>$row[11],'t17'=>$row[12],'t18'=>$row[13],'t19'=>$row[14],'t20'=>$row[15],'lat'=>$row[16],'lng'=>$row[17]
    ));
}

echo json_encode(array("result"=>$result));

mysql_close($con);
?>
