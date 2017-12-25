<#include "const.ftl" >
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>选桌</title>
    <style>
        img{
            width: 150px;
            cursor: pointer;
        }
        .info{
            font-size: 50px;
            margin: 50px;
            color: grey;
        }
        .tableDiv{
            margin: 50px;
            text-align: center;
        }
    </style>
</head>
<body>
<p class="info">${Session.user.username}，请选择游戏房间。</p>
<div class="tablesDiv">
<#list tables as t>
<div class="tableDiv col-sm-4">
<#if t.status ==0>
<img src="/img/table.png" onclick="chooseTable(${t.tableId})">
<p>${t.tableId}号桌</p>
<#else >
    <img src="/img/tableDisabled.png">
    <p>${t.tableId}号桌</p>
</#if>
</div>
</#list>
    <div>
<#--<c:forEach items="${tables}" var="t">
    <c:choose>
        <c:when test="${t.status==0}">
            <button onclick="chooseTable(${t.tableId})">加入${t.tableId}号桌</button>
        </c:when>
        <c:otherwise>
            <button onclick="chooseTable(${t.tableId})" disabled>加入${t.tableId}号桌</button>
        </c:otherwise>
    </c:choose>

</c:forEach>-->

</body>
</html>
<script type="text/javascript">
    function chooseTable(id) {
        alert(id);
        var initialPlace = 0;
        $.ajax({
            method: 'POST',
            url: '/game/chooseTable',
            data: {
                tableId: id,
                initialPlace: initialPlace
            },
            dataType: "json",
            success: function (data) {
                if (data.success==true){
                    window.location.href='/game/gamePage?tableId='+id;
                } else {
                    alert(data.error);
                }
            },
            error:function () {
                alert("请求出错！");
            }
        });
    }
</script>
