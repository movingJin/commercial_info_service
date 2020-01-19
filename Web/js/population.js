$(document).ready(function() {


    $('#population_info').on('click', function(event) {
            var pop_time=[];
            var sum_pop=[];
            var pop_address=[];
	var lat=[];
	var lng=[];
            var pop_num=0;

            for (var i = 0; i < circles.length; i++) {
                circles[i].setMap(null);
            }
            $.ajax({
                type : "GET",
                url : "http://52.78.123.217/query_population.php",
                dataType : "json",
                data : "rq_dong=" + extraAddr,
                async: false,
                success : function(json) {

                    for (var i=0;i<json.result.length;i++) 
                    {   
                        pop_time[i]=[];
                        pop_time[i].push(parseInt(json.result[i].t7));
                        pop_time[i].push(parseInt(json.result[i].t8));
                        pop_time[i].push(parseInt(json.result[i].t9));
                        pop_time[i].push(parseInt(json.result[i].t10));
                        pop_time[i].push(parseInt(json.result[i].t11));
                        pop_time[i].push(parseInt(json.result[i].t12));
                        pop_time[i].push(parseInt(json.result[i].t13));
                        pop_time[i].push(parseInt(json.result[i].t14));
                        pop_time[i].push(parseInt(json.result[i].t15));
                        pop_time[i].push(parseInt(json.result[i].t16));
                        pop_time[i].push(parseInt(json.result[i].t17));
                        pop_time[i].push(parseInt(json.result[i].t18));
                        pop_time[i].push(parseInt(json.result[i].t19));
                        pop_time[i].push(parseInt(json.result[i].t20));
                        sum_pop.push(parseInt(json.result[i].t7)+parseInt(json.result[i].t8)+parseInt(json.result[i].t9)+parseInt(json.result[i].t10)+parseInt(json.result[i].t11)+parseInt(json.result[i].t12)+parseInt(json.result[i].t13)+parseInt(json.result[i].t14)+parseInt(json.result[i].t15)+parseInt(json.result[i].t16)+parseInt(json.result[i].t17)+parseInt(json.result[i].t18)+parseInt(json.result[i].t19)+parseInt(json.result[i].t20));
                        lat[i]=[];
			lng[i]=[];
			lat[i].push(json.result[i].lat);
			lng[i].push(json.result[i].lng);
			pop_address.push(json.result[i].addr);
                    }
                    pop_num=json.result.length;
                    
                
                    
                },
                error : function(e) {
                    alert("처리중 장애가 발생하였습니다.");
                }
            });
            for(var j=0;j<pop_num;j++)(function (j){
                // 주소-좌표 변환 객체를 생성합니다
                //var geocoder = new daum.maps.services.Geocoder();

                // 주소로 좌표를 검색합니다
                //geocoder.addr2coord(pop_address[j], function(status, result) {
                            
                            
                // 정상적으로 검색이 완료됐으면 
                //if (status === daum.maps.services.Status.OK) {
                    //document.write(lat[j]+", "+lng[j]+"\n"); 
                    var circle_color='#B2EBF4';
                    var circle_radius=50;
                    if(parseInt(sum_pop[j])>1300&&parseInt(sum_pop[j])<=2500){circle_color='#D1B2FF'; circle_radius=60;}        //전체 유동인구 수에 따라 원의 색과 지름 다르게 설정
                    else if(parseInt(sum_pop[j])>2500&&parseInt(sum_pop[j])<=3700){circle_color='#B7F0B1'; circle_radius=100;}
                    else if(parseInt(sum_pop[j])>3700&&parseInt(sum_pop[j])<=6400){circle_color='#FFE08C'; circle_radius=150;}
                    else if(parseInt(sum_pop[j])>6400){circle_color='#FFA7A7'; circle_radius=200;}
                    var circle = new daum.maps.Circle({
                        center : new daum.maps.LatLng(lat[j], lng[j]),  // 원의 중심좌표 입니다 
                        radius: circle_radius,  // 미터 단위의 원의 반지름입니다 
                        strokeColor: circle_color, // 선의 색깔입니다
                        strokeOpacity: 0, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
                        strokeStyle: 'soild', // 선의 스타일 입니다
                        fillColor: circle_color, // 채우기 색깔입니다
                        fillOpacity: 0.7  // 채우기 불투명도 입니다  

                    }); 
                    circles.push(circle);

                    // 지도에 원을 표시합니다 
                    circle.setMap(map);
                    // 다각형에 마우스오버 이벤트를 등록합니다
                    daum.maps.event.addListener(circle, 'mouseover', function() { 
                    // 다각형에 마우스오버 이벤트가 발생했을 때 변경할 채우기 옵션입니다
                        var mouseoverOption = { 
                            fillColor: '#EFFFED', // 채우기 색깔입니다
                            fillOpacity: 0.8 // 채우기 불투명도 입니다        
                        };

                        // 다각형의 채우기 옵션을 변경합니다
                        circle.setOptions(mouseoverOption);

                    });   

                    daum.maps.event.addListener(circle, 'mouseout', function() { 


                        // 다각형에 마우스아웃 이벤트가 발생했을 때 변경할 채우기 옵션입니다
                        var mouseoutOption = {
                            fillColor: circle_color, // 채우기 색깔입니다 
                            fillOpacity: 0.7 // 채우기 불투명도 입니다        
                        };
                        // 다각형의 채우기 옵션을 변경합니다
                        circle.setOptions(mouseoutOption);
    
                    }); 

                    // 다각형에 마우스다운 이벤트를 등록합니다
                    //var downCount = 0;
                    var r=0;

                    daum.maps.event.addListener(circle, 'mousedown', function() { 
                        google.charts.load('current', {packages: ['corechart', 'bar']});
                        google.charts.setOnLoadCallback(drawBasic);
                        function drawBasic() {
                            var data = google.visualization.arrayToDataTable([
                                ['시간', '유동인구',],
                                ['7시', pop_time[j][r++]],
                                ['8시', pop_time[j][r++]],
                                ['9시', pop_time[j][r++]],
                                ['10시', pop_time[j][r++]],
                                ['11시', pop_time[j][r++]],
                                ['12시', pop_time[j][r++]],
                                ['13시', pop_time[j][r++]],
                                ['14시', pop_time[j][r++]],
                                ['15시', pop_time[j][r++]],
                                ['16시', pop_time[j][r++]],
                                ['17시', pop_time[j][r++]],
                                ['18시', pop_time[j][r++]],
                                ['19시', pop_time[j][r++]],
                                ['20시', pop_time[j][r++]]
                            ]);
                                var view = new google.visualization.DataView(data);
                                view.setColumns([0, {
                                    type: 'number',
                                    label: data.getColumnLabel(1),
                                    calc: function () {return 0;}
                                }]);
                            var options = {
                                width: 375,
                                height: 300,
                                title: '시간대 별 유동인구',
                                chartArea: {width: '60%'},
                                animation:{
                                    duration: 1000
                                },
                                hAxis: {
                                    title: 'Total Population',
                                    minValue: 0
                                },
                                vAxis: {
                                    title: '시간'
                                }
                            };

                            var chart = new google.visualization.BarChart(document.getElementById('chart_div'));

                            //chart.draw(data, options);
                            var runOnce = google.visualization.events.addListener(chart, 'ready', function () {
                                google.visualization.events.removeListener(runOnce);
                                chart.draw(data, options);
                            });
                            chart.draw(view, options);

                        }
                        r=0;

                    });
               // }
               // });
                        
            })(j);          
            
    });

});
