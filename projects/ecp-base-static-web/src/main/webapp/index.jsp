<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>index</title>
<script type="text/javascript" src="./js/jquery-1.8.2.min.js"></script>
<script type="text/javascript">
	$("document")
			.ready(
					function() {
						
						
						var url2 = "http://localhost:8080/ecp-base-static-web/static/html/55f6ceee8988e5d394d6e1db.html";
						$.ajax({
							url : url2,
							type : "get",
							dataType : "jsonp",//数据类型为jsonp  
							jsonp : "jsonpCallback",//服务端用于接收callback调用的function名的参数  
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								alert("err|" + textStatus + "|" + errorThrown);
								alert(XMLHttpRequest.status);
								alert(XMLHttpRequest.readyState);
								alert(textStatus);
								if (XMLHttpRequest.status == 12029
										&& XMLHttpRequest.readyState == 4) {
									if (flag == 1) {
										alert("加载系统信息异常，请检查网络是否正常！");
										flag = 2;
									}
								}
							},
							success : function(data) {
								$("#static2").empty();
								$("#static2").html(data.result);
							}
						});
						
						
						var url = "http://localhost:8080/ecp-base-static-web/static/html/55f6ceee8988e5d394d6e1db";
						$.ajax({
							url : url,
							type : "get",
							dataType : "jsonp",//数据类型为jsonp  
							jsonp : "jsonpCallback",//服务端用于接收callback调用的function名的参数  
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								alert("err|" + textStatus + "|" + errorThrown);
								alert(XMLHttpRequest.status);
								alert(XMLHttpRequest.readyState);
								alert(textStatus);
								if (XMLHttpRequest.status == 12029
										&& XMLHttpRequest.readyState == 4) {
									if (flag == 1) {
										alert("加载系统信息异常，请检查网络是否正常！");
										flag = 2;
									}
								}
							},
							success : function(data) {
								$("#static").empty();
								$("#static").html(data.result);
							}
						});
					});
</script>
</head>
<body>
	<div id="static"></div>
<div id="static2"></div>
	<img alt=""
		src="http://localhost:8080/ecp-base-static-web/image/53b0dfb0300418069bc0119b.jpg">
	<img alt=""
		src="http://localhost:8080/ecp-base-static-web/image/53b0dfb0300418069bc0119b.jpg">
</body>
</html>