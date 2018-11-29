<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=edge" charset="utf-8"> 

<title>下载</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css">  
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/jquery.js"></script>
<script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
<script type="text/javascript">
	var context = "<%=request.getContextPath()%>";
</script>
   
</head>
<body>
 	<div class="input-group">
         <span class="input-group-addon">卖家id:</span>
         <input type="text" style="width:30%" class="form-control" name="seller_id" id="seller_id"/>
    </div>
 	<div class="input-group">
         <span class="input-group-addon">卖家名称:</span>
         <input type="text" style="width:30%" class="form-control"  id="visitor_nick" name="visitor_nick"/>
    </div>
	<input type="button" class="btn btn-default" value="query" onclick="userShow()"/>
<hr>
<table id="show" class="table table-bordered table-striped"></table>
<script type="text/javascript">
	var thObj  = {"visitor_id":"卖家id","visitor_nick":"卖家名称","access_token":"sessionKey"};
	$(document).ready(
			function(){
				userShow();
				
			}
	);
	function userShow(){
		var url = context + "/opt/usershow.do?access_token=1111111";
		var seller_id = $("#seller_id").val();
		var visitor_nick = $("#visitor_nick").val();
		var param = {"visitor_nick":visitor_nick,"seller_id":seller_id};
		$.ajax({
			   type: "POST",
			   url: url,
			   data: param,
			   success: function(msg){
				   fillTable(msg);
			   }
			});
	}
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