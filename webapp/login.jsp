<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

	<script>
		$(function(){

			if(window.top!=window){
				window.top.location=window.location;
			}


			//在页面加载完毕后将用户账号和密码文本框清空【页面刷新清空】
			$("#loginAct").val("");
			$("#loginPwd").val("");

			//页面加载完毕自动获得焦点
			$("#loginAct").focus()

			//为按钮绑定单击事件，完成登陆操作
			$("#submitBtn").click(function () {

				login();

			})

			//为当前登录窗口绑定敲键盘事件
			//event这个参数可以取得按下键盘所对应的按钮的键值
			$(window).keydown(function (event) {

				//13是回车键的键值
				if (event.keyCode == 13){
					login();
				}

			})
		})


		function login() {

			// alert("登录操作")
			//验证账号密码不能为空
			//取得账号密码
			//将文本框中的空格去掉，常用的方法是: $.trim(文本)
			var loginAct = $.trim($("#loginAct").val());
			var loginPwd = $.trim($("#loginPwd").val());

			if ("" == loginAct || "" == loginPwd){

				$("#msg").html("账号密码不能为空");

				//如果账号密码为空，则需要强制结束方法
				return false;

			}

			//去后台验证相关操作
			$.ajax({
				url:"settings/user/login.do",
				type:"post",
				dataType:"json",
				data:{
					"loginAct":loginAct,
					"loginPwd":loginPwd
				},
				success:function (date) {

					/*
						date
							{"success":true/false,"msg":"xxx"}
					 */
					if (date.success){

						//验证成功，跳转到工作台初始页（欢迎页）
						document.location.href="workbench/index.jsp";

					}else{

						$("#msg").html(date.msg);

					}
				}
			})


		}



	</script>
</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2017&nbsp;动力节点</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="workbench/index.jsp" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" type="text" placeholder="用户名" id="loginAct">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginPwd">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						
							<span id="msg" style="color: red"></span>
						
					</div>
					<!--
						注意：
							在form表单中，按钮的默认行为就是提交表单
							所以要将按钮的类型设置为button，
							button按钮所触发的行为由程序员自己定义，以完成后续的操作
					-->
					<button type="button" id="submitBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>