<#include "const.ftl" >
<html>
<head>
    <title>Title</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <script src="/js/bootstrap.min.js"></script>
    <script src="/js/jquery.min.js"></script>
</head>
<body>
<div id="myForm">
    <div>
        用户名:
        <input type="text" id="username" placeholder="用户名"></br>
        密 码:
        <input type="password" id="password" placeholder="密码"><br>
        </br>
        <button onclick="login()">登录</button>
    </div>
</div>
</body>
<script>
    function login() {
        var username = $("#username").val();
        var password = $("#password").val();
        $.ajax({
            type: "post",
            url: '/user/login',
            dataType: "json",
            data: {
                "username": username,
                "password": password
            },
            success: function (data) {
                if (data.success == true) {
                    location.href = "/game/tables";
                }
                else {
                    alert("用户名或密码错误！");
                }
            },
            error: function () {
                alert("请求出错！");
            }
        });
    }
</script>
</html>
