<%--
  Created by IntelliJ IDEA.
  User: DELL
  Date: 2020/9/16
  Time: 19:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>

<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>
</head>
<body>

</body>
</html>


        <!--temp-->
        $.ajax({
        url:"",
        type:"",
        dataType:"",
        date:{},
        success:function (date) {

        }
        })