<#include "const.ftl" >
<html>
<head>
    <title>Title</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <script src="/js/bootstrap.min.js"></script>
    <script src="/js/jquery.min.js"></script>
    <style>
        #myForm{
            margin: 100px;
        }
        .btn{
            background-color:indianred ;
            border-color:indianred ;
            width: 200px;
        }
    </style>
</head>
<body>
<div id="myForm" style="text-align: center">
    <div id="myForm" class="form-horizontal">
        <p style="font-size: 60px;color: indianred">登录</p>
        <div class="form-group">
            <input type="text" class="form-control" id="username" placeholder="请输入用户名">
        </div>
        <div class="form-group">
            <input type="password" class="form-control" id="password" placeholder="请输入密码">
        </div>
        <button class="btn btn-primary btn-lg" onclick="login()">登录</button>
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
