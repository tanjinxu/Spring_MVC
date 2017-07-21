<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath }/1.js"></script>
<script type="text/javascript">
$(function(){
	var id = $("input[name=id]").val();
	if(id){
		$("#ff").attr("action","/zjsun_zspring_mvc/saveEdit.action");$("#btn").html("修改");$("h3").html("修改页面");
	}else{
		$("#ff").attr("action","/zjsun_zspring_mvc/doAdd.action");$("#btn").html("添加");$("h3").html("添加页面");
	}
})
</script>
<title>action页面</title>
</head>
<body>
	<center>
	<h3></h3>
	<br><br>
		<form action="" method="post" id="ff" >
			<input type="hidden" name="id" value="${info.id}">
			姓名：<input type="text" name="name" value="${info.name}"><br><br>
			地址：<input type="text" name="address" value="${info.address}"><br><br>
			电话：<input type="text" name="tel" value="${info.tel}"><br><br>
			年龄：<input type="text" name="age" value="${info.age}"><br><br>
			学校：<input type="text" name="school" value="${info.school}"><br><br>
			简介：<textarea name="test">${info.test}</textarea><br><br>
				 <button onclick="document:ff.submit()" id="btn"></button>
		</form>
	</center>
</body>
</html>