<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=edge"> 

<title>下载</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
<script type="text/javascript">
	var context = "<%=request.getContextPath()%>";
</script>
</head>
<body>
<form action="<%=request.getContextPath()%>/ali/aliExportByDt.do">
shop_name：<input type="text" name="shop_tital" id="shop_tital"/><br>
start_date：<input type="text" name="start_date" id="start_date"/><br>
end_date：<input type="text" name="end_date" id="end_date"/><br>
<select  name="platform" id="platform"/>
	<option value ="pc">pc</option>
 	<option value ="wire">wire</option>
</select><br>
<button type="button" onclick="query();" >query</button>
<hr>
<table id="show" class="table table-bordered table-striped"></table>
</form>
<script type="text/javascript">
	var dayTime = 24*60*60*1000;
	$(document).ready(
			function(){
					var dt = getFormatDate(1);
					$("#start_date").val(dt);
					$("#end_date").val(dt);
			}
	);
	
	function query(){
		var shop_tital = $("#shop_tital").val();
		var start_date = $("#start_date").val();
		var end_date = $("#end_date").val();
		var platform = $("#platform").val();
		clickQuery(shop_tital,start_date,end_date,platform);
	}
	function clickQuery(shop_tital,start_date,end_date,platform){
		var url = context + "/dsp/showClick.do";
		var param = {"shop_tital":shop_tital,"start_date":start_date,"end_date":end_date,"platform":platform};
		$.ajax({
			   type: "POST",
			   url: url,
			   data: param,
			   success: function(msg){
				   fillTable(msg);
			   }
			});
	}
	
// 	--------------------------------------------------------------------------------
var thObj  = {"date":"日期","shop_name":"卖家名称","pv":"展现","click":"点击","charge":"消耗","RandomPv":"随机pv"};
	function getTh(obj){
		var ths = "";
		ths += "<tr style=\"color:red;\">";
		for ( var i in obj) {
			ths += "<td>"+obj[i]+"</td>";
		}
		ths += "</tr>";
		return ths;
	}
	function fillTable(data){
		var trs ="";
		var th = getTh(thObj);
		trs += th;
		for ( var obj in data) {
			trs += "<tr>";
			for ( var col in thObj) {
				trs +="<td>"+data[obj][col]+"</td>";
			}
			trs += "</tr>";
		}
		$("#show").html(trs);
	}
</script>
</body>
</html>