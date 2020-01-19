$(document).ready(function() {
	extraAddr='';
	rq_type_s='';

    		mapContainer = document.getElementById('map'), // 지도를 표시할 div
        		mapOption = {
            		center: new daum.maps.LatLng(37.537187, 127.005476), // 지도의 중심좌표
            		level: 5 // 지도의 확대 레벨
        		};

    		//지도를 미리 생성
    		map = new daum.maps.Map(mapContainer, mapOption);
    		//주소-좌표 변환 객체를 생성
    		geocoder = new daum.maps.services.Geocoder();
    		//마커를 미리 생성
    		marker = new daum.maps.Marker({
        		position: new daum.maps.LatLng(37.537187, 127.005476),
        		map: map 
    		});
    		//marker 배열
    		markers = [];
    		//circle 배열 : 삭제를 위함
    		circles=[];

    google.charts.load('current', {packages: ['corechart', 'bar']});
    google.charts.setOnLoadCallback(drawBasic);
    function drawBasic() {
      var data = google.visualization.arrayToDataTable([
        ['시간', '유동인구',],
        ['7시', 0],
        ['8시', 0],
        ['9시', 0],
        ['10시', 0],
        ['11시', 0],
        ['12시', 0],
        ['13시', 0],
        ['14시', 0],
        ['15시', 0],
        ['16시', 0],
        ['17시', 0],
        ['18시', 0],
        ['19시', 0],
        ['20시', 0]
      ]);

      var options = {
         width: 375,
        height: 300,
        title: '시간대 별 유동인구',
        chartArea: {width: '60%'},
        hAxis: {
          minValue: 0
        },
        vAxis: {
          title: '시간'
        }
      };

      var chart = new google.visualization.BarChart(document.getElementById('chart_div'));

      chart.draw(data, options);
    }

		// 우편번호 찾기 화면을 넣을 element
    	var element_layer = document.getElementById('layer');
    	
    	$('#btnCloseLayer').on('click', function(event) {
    	//function closeDaumPostcode() {
        	// iframe을 넣은 element를 안보이게 한다.
        	element_layer.style.display = 'none';
    	});

    	$('#address_search').on('click', function(event) {
        	new daum.Postcode({
            	oncomplete: function(data) {
                	// 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                	// 각 주소의 노출 규칙에 따라 주소를 조합한다.
                	// 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                	var fullAddr = data.address; // 최종 주소 변수
                	extraAddr = ''; // 조합형 주소 변수

                	// 기본 주소가 도로명 타입일때 조합한다.
                	if(data.addressType === 'R'){
                    	//법정동명이 있을 경우 추가한다.
                    	if(data.bname !== ''){
                        	extraAddr += data.bname;
				extraAddr=extraAddr.substring(0,2);	//extraAddr="화양";
                    	}
                    	// 건물명이 있을 경우 추가한다.
                    	if(data.buildingName !== ''){
                        	//extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    	}
                    	// 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
                    	fullAddr += (extraAddr !== '' ? ' ('+ extraAddr +')' : '');
                	}
						// 주소 정보를 해당 필드에 넣는다.
                		document.getElementById("sample5_address").value = fullAddr;
				
				document.getElementById('t_1').innerHTML = 0;		//상권정보 초기화
				document.getElementById('t_2').innerHTML = 0.00; 
				document.getElementById('t_3').innerHTML = 0; 
				
				google.charts.load('current', {packages: ['corechart', 'bar']});
				google.charts.setOnLoadCallback(drawBasic);
                        	function drawBasic() {
                            	var data = google.visualization.arrayToDataTable([
                                	['시간', '유동인구',],
                                	['7시', 0],
                                	['8시', 0],
                                	['9시', 0],
                                	['10시', 0],
                                	['11시', 0],
                                	['12시', 0],
                                	['13시', 0],
                                	['14시', 0],
                                	['15시', 0],
                                	['16시', 0],
                                	['17시', 0],
                                	['18시', 0],
                                	['19시', 0],
                                	['20시', 0]
                            	]);
                                var view = new google.visualization.DataView(data);
                                view.setColumns([0, {
                                    type: 'number',
                                    label: data.getColumnLabel(1),
                                    calc: function () {return 0;}
                                }]);
                            	
                            	var chart = new google.visualization.BarChart(document.getElementById('chart_div'));
				chart.draw(view);
				
				}

                		// 주소로 좌표를 검색
                		geocoder.addr2coord(data.address, function(status, result) {
                    		// 정상적으로 검색이 완료됐으면
                    		if (status === daum.maps.services.Status.OK) {
                        		// 해당 주소에 대한 좌표를 받아서
                        		var coords = new daum.maps.LatLng(result.addr[0].lat, result.addr[0].lng);
                        		// 지도를 보여준다.
                        		mapContainer.style.display = "block";
                        		map.relayout();
                        		// 지도 중심을 변경한다.
                        		map.setCenter(coords);
                        		// 마커를 결과값으로 받은 위치로 옮긴다.
                        		marker.setPosition(coords)
                    		}
                		});

                	// iframe을 넣은 element를 안보이게 한다.
                	// (autoClose:false 기능을 이용한다면, 아래 코드를 제거해야 화면에서 사라지지 않는다.)
                	element_layer.style.display = 'none';
            	},
            	width : '100%',
            	height : '100%'
        	}).embed(element_layer);

        	// iframe을 넣은 element를 보이게 한다.
        	element_layer.style.display = 'block';

        	// iframe을 넣은 element의 위치를 화면의 가운데로 이동시킨다.
        	var width = 300; //우편번호서비스가 들어갈 element의 width
        	var height = 460; //우편번호서비스가 들어갈 element의 height
        	var borderWidth = 5; //샘플에서 사용하는 border의 두께

        	// 위에서 선언한 값들을 실제 element에 넣는다.
        	element_layer.style.width = width + 'px';
        	element_layer.style.height = height + 'px';
        	element_layer.style.border = borderWidth + 'px solid';
        	// 실행되는 순간의 화면 너비와 높이 값을 가져와서 중앙에 뜰 수 있도록 위치를 계산한다.
        	element_layer.style.left = (((window.innerWidth || document.documentElement.clientWidth) - width)/2 - borderWidth) + 'px';
        	element_layer.style.top = (((window.innerHeight || document.documentElement.clientHeight) - height)/2 - borderWidth) + 'px';




    	});

    	$('#rq_type_select').on('change',function(event){
    		rq_type_s=$('#rq_type_select option:selected').val();
    	});
    	//검색한 주소에 디비 읽어서 지도에 유사업종 마커랑 인포윈도우!
    	//폐업률, 평당임대가도 알수 있음
    	$('#restaurant_info').on('click', function(event) {
		//document.write("testbug");	
    		for (var i = 0; i < markers.length; i++) {
        		markers[i].setMap(null);
    		} 
    
		 $.ajax({
                                type : "GET",
                                url : "http://52.78.123.217/query_restaurant_count_info.php",
                                dataType : "json",
                                data : "rq_dong=" + extraAddr + "&rq_type=" + rq_type_s,

                                success : function(json) {

                                        var num=json.result[0].count;
                                        document.getElementById('t_1').innerHTML = num;

                                },
                        error : function(e) {
                        alert("처리중 장애가 발생하였습니다.");
                        }
                        });
	

    		$.ajax({
				type : "GET",
				url : "http://52.78.123.217/query_restaurant_info.php",
				dataType : "json",
				data : "rq_dong=" + extraAddr + "&rq_type=" + rq_type_s,

				success : function(json) {

					//alert(json.result.length);
					//여기서 유사업종 수를 바꾸기
					//document.getElementById('t_1').innerHTML = json.result.length; 

					for (var i=0;i<json.result.length;i++) 
            		{
            			// 주소로 좌표를 검색합니다
						var coords = new daum.maps.LatLng(json.result[i].lat, json.result[i].lng);
        				// 결과값으로 받은 위치를 마커로 표시합니다
						var marker= new daum.maps.Marker({
							map: map,
    						position: coords
   						});
   						markers.push(marker);
   						var year=parseInt(json.result[i].launch_date/10000);
   						var month=parseInt(json.result[i].launch_date%10000/100);
   						var day=parseInt(json.result[i].launch_date%100);
        				// 인포윈도우로 장소에 대한 설명을 표시합니다
						var iwContent = '<div style=\"padding:5px;\">업소명: '+json.result[i].name+
        								'</br>주소: '+json.result[i].addr+
        								'</br>개시일: '+year+'.'+month+'.'+day+
        								'</br>업종: '+json.result[i].type+'</div>',iwRemoveable = true;
     					// 인포윈도우를 생성합니다
						var infowindow= new daum.maps.InfoWindow({
							content : iwContent,
                            removable : iwRemoveable
						});
						
							
		
        				//daum.maps.event.addListener(marker, 'mouseover', makeOverListener(map, marker, infowindow));
    					//daum.maps.event.addListener(marker, 'mouseout', makeOutListener(infowindow));
                        daum.maps.event.addListener(marker, 'click', makeClickListener(map,marker,infowindow) );

               			//alert(json.result[i].name);
                		//alert(json.result[i].addr);
                		//alert(json.result[i].launch_date);
            		}
                	//alert(extraAddr);
                	//alert(json.result[0].name);
                	//alert(json.result[0].addr);
                	//alert(json.result[0].launch_date);
				},
        		error : function(e) {
               		alert("처리중 장애가 발생하였습니다.");
        		}
			});


			$.ajax({
				type : "GET",
				url : "http://52.78.123.217/query_fail_ratio.php",
				dataType : "json",
				data : "rq_dong=" + extraAddr + "&rq_type=" + rq_type_s,

				success : function(json) {

					var num=json.result[0].fail_ratio;
					num=(num*100).toFixed(2);
					document.getElementById('t_2').innerHTML = num; 

				},
        		error : function(e) {
               		alert("처리중 장애가 발생하였습니다.");
        		}
			});

			$.ajax({
				type : "GET",
				url : "http://52.78.123.217/query_rent_price.php",
				dataType : "json",
				data : "rq_dong=" + extraAddr,

				success : function(json) {
					var num=json.result[0].avg_unitPrice;
					num=Math.round(num*100)/100;

					document.getElementById('t_3').innerHTML = num;

				},
        		error : function(e) {
               		alert("처리중 장애가 발생하였습니다.");
        		}
			});
			

    	});

    	//마커 클릭 리스너
    	function makeOverListener(map, marker, infowindow) {
    		return function() {
        		infowindow.open(map, marker);
    		};
		}

		// 인포윈도우를 닫는 클로저를 만드는 함수입니다 
		function makeOutListener(infowindow) {
    		return function() {
        		infowindow.close();
    		};
		}
        
        function makeClickListener(map, marker,infowindow){
            return function() {
                infowindow.open(map, marker);
            };
        }
        //circle 클릭, 마우스 리스너
        


        

        $('#subway_info').on('click',function(event){
			//subway가까운 곳 좌표저
        	var xp=0,yp=0;
        	var sub_name;
        	
        	//지하철 마커는 다른 이미지를 사용 
        	var imageSrc = "http://i1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png"; 
        	var imageSize = new daum.maps.Size(24, 35); 
    		// 마커 이미지를 생성합니다    
    		var markerImage = new daum.maps.MarkerImage(imageSrc, imageSize); 

        	// 장소 검색 객체를 생성합니다
			var ps = new daum.maps.services.Places(map); 

			// 카테고리로 은행을 검색합니다
			ps.categorySearch('SW8', placesSearchCB, {useMapBounds:true}); 

			// 키워드 검색 완료 시 호출되는 콜백함수 입니다
			function placesSearchCB (status, data, pagination) {
    			if (status === daum.maps.services.Status.OK) {
        			//for (var i=0; i<data.places.length; i++) {
            			//displayMarker(data.places[i]); 
            			// 마커를 생성하고 지도에 표시합니다
    					var marker = new daum.maps.Marker({
        					map: map,
        					position: new daum.maps.LatLng(data.places[0].latitude, data.places[0].longitude),
        					image : markerImage // 마커 이미지 
   	 					});
   	 					xp=Math.floor(data.places[0].latitude*100)/100;
   	 					yp=Math.floor(data.places[0].longitude*100)/100;
   	 					sub_name=data.places[0].title;
        			//}  
        	

        	//php 읽기
			$.ajax({
                type : "GET",
                url : "http://52.78.123.217/query_subwaypop.php",
                dataType : "json",
                data : "rq_dong=" + extraAddr,
                success : function(json) {

                    
                    for (var i=0;i<json.result.length;i++) 
                    {
                    	var j_xp=Math.floor(json.result[i].XPOINT*100)/100;
                    	var j_yp=Math.floor(json.result[i].YPOINT*100)/100;
                    	if(j_xp==xp&&j_yp==yp){
                    		var sub_pop=parseInt(json.result[i].ride)+parseInt(json.result[i].alight);
                    		var iwContent = '<div style="padding:5px;font-size:12px;">' + sub_name + 
                    						'</br>승차인원: '+json.result[i].ride+
								'</br>하차인원: '+json.result[i].alight+'</div>',iwRemoveable = true;
    						var infowindow= new daum.maps.InfoWindow({
								content : iwContent,
                            	removable : iwRemoveable
							});
    						daum.maps.event.addListener(marker, 'click', makeClickListener(map,marker,infowindow) );
    						break;
                    	}
                        
                    }
                },
                error : function(e) {
                    alert("처리중 장애가 발생하였습니다.");
                }
            });



    			}		
			}

        });

		


        
});
