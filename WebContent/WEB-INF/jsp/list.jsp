<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath }/1.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap.css">
<script type="text/javascript">
$(function(){
	$("#close").click(function(){
		$("#s_details").hide();
	})
})
</script>
<title>学生列表</title>
</head>
<body>
<center>
<h3>学生列表</h3><br><br><br>
<hr width="70%"><br>
<form class="form-inline"  action="${pageContext.request.contextPath }/getAll.action" method="search">
  <div class="form-group">
    <label class="sr-only" for="exampleInputAmount">Amount (in dollars)</label>
    <div class="input-group">
      <div class="input-group-addon">姓名</div>
      <input type="text" name="search" placeholder="请输入搜索条件" value="${search }" class="form-control" id="exampleInputAmount" >
      <div class="input-group-addon"></div>
    </div>
  </div>
  <input type="submit" value="搜索" class="btn btn-info">
</form><br>
<a href="${pageContext.request.contextPath }/add.action" class="btn btn-primary" style="position:fixed;right:0px;top:170px;">添加学生信息</a>
<table width="100%" border=1 class="table table-hover">
<tbody>
<tr>
	<th>学生ID</th><th>学生姓名</th><th>学生住址</th><th>学生电话</th><th>学生年龄</th><th>学生学校</th><th>学生简介</th><th>添加时间</th><th>操作</th>
</tr>
</tbody>
<c:forEach items="${list }" var="item">
<tr>
	<td>${item.id }</td><td>${item.name }</td><td>${item.address }</td><td>${item.tel }</td><td>${item.age }</td><td>${item.school }</td><td>${item.test }</td><td>${item.addtime }</td>
	<td><a href="javascript:void(0)" onclick="del(${item.id })">删除</a> | <a href="${pageContext.request.contextPath }/doEdit.action?id=${item.id}">修改</a> | <a href="javascript:void(0)" onclick="getAjax(${item.id})">查看详情</a></td>
</tr>
</c:forEach> 

</table>
</center>
<div style="display:none;border:2px solid #ccc;border-radius:10px;width:400px;height:400px;z-index:1000;position:absolute;top:200px;left:36%;background-color:white;" id="s_details">
<button type="button" class="close" aria-label="Close" style="position:relative;right:5px;" id="close"><span aria-hidden="true">&times;</span></button>
<table class="table" style="position:relative;top:10px;">
	<tr><td>姓名：</td><td id="t_name"></td></tr><tr><td>年龄：</td><td id="t_age"></td></tr><tr><td>电话：</td><td id="t_tel"></td></tr><tr><td>学校：</td><td id="t_school"></td></tr><tr><td>地址：</td><td id="t_address"></td></tr><tr><td>介绍：</td><td id="t_test"></td></tr><tr><td>添加时间：</td><td id="t_addtime"></td></tr>
</table>
</div>
</body>
<script type="text/javascript">
	function del(stu_id){
		$.ajax({
			url:"/zjsun_zspring_mvc/del.action",data:{stu_id:stu_id},type:"post",
			success:function(){location.reload();},
			error:function(){alert("请稍后再试");}
		});
	}
	function getAjax(stu_id){
		$.ajax({
			url:"/zjsun_zspring_mvc/getJson.action",type:"post",data:{stu_id:stu_id},dataType:"json",
			success:function(data){
					$("#t_name").text(data.name);$("#t_age").text(data.age);$("#t_tel").text(data.tel);
					$("#t_school").text(data.school);$("#t_address").text(data.address);$("#t_test").text(data.test);
					$("#t_addtime").text(data.addtime);
					$("#s_details").show();
			},
			error:function(){alert("请稍后再试！～");}
		});
	}
</script>
</html>