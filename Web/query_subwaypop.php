<?php
$con=mysql_connect("localhost","root","qwerasdf13","commercial_info");
  
#if (mysql_connect_errno($con))  
#{  
#   echo "Failed to connect to MySQL: " . mysqli_connect_error();
#}  

mysql_query("set names utf8");
mysql_query("use commercial_info"); 

$res = mysql_query("select pop.*,XPOINT,YPOINT from (select SUB_STA_NM,truncate(avg(ride),0) as ride,truncate(avg(alight),0) as alight from
(select USE_DT,SUB_STA_NM,sum(RIDE_PASGR_NUM)as ride, sum(ALIGHT_PASGR_NUM) as alight
from subway_population group by USE_DT,SUB_STA_NM)as temp group by SUB_STA_NM) as pop,
(SELECT STATION_NM, XPOINT,YPOINT
FROM subway_refer_addr
GROUP BY STATION_NM) as refer
where pop.SUB_STA_NM=STATION_NM  order by pop.SUB_STA_NM");

if($res === FALSE) {
    die(mysql_error()); // TODO: better error handling
}
$result = array();

while($row = mysql_fetch_array($res)){
  array_push($result,
    array('SUB_STA_NM'=>$row[0],'ride'=>$row[1],'alight'=>$row[2],'XPOINT'=>$row[3],'YPOINT'=>$row[4]
    ));
}

echo json_encode(array("result"=>$result));

mysql_close($con);
?>
