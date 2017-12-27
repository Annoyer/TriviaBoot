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
<div id="tablesDiv" class="tablesDiv">
<#--<#list tables as t>-->
<#--<div class="tableDiv col-sm-4">-->
<#--<#if t.status ==0>-->
<#--<img src="/img/table.png" onclick="chooseTable(${t.tableId})">-->
<#--<p>${t.tableId}号桌</p>-->
<#--<#else >-->
    <#--<img src="/img/tableDisabled.png">-->
    <#--<p>${t.tableId}号桌</p>-->
<#--</#if>-->
<#--</div>-->
<#--</#list>-->
</div>
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

    <div id="dataDiv"></div>

</body>
</html>
<script src="/js/sockjs.min.js"></script>
<script type="text/javascript">
    var websocket = null;
    $(function () {
        if ('WebSocket' in window) {
            console.log("此浏览器支持websocket");
            //本地
            websocket = new WebSocket("ws://localhost:8080/websocket?tableId=-1");
            //服务器
            //websocket = new WebSocket("ws://122.152.197.158:10000/websocket?tableId=-1");
        } else if ('MozWebSocket' in window) {
            alert("此浏览器只支持MozWebSocket");
        } else {
            console.log("此浏览器只支持SockJS");
            //本地
            websocket = new SocketJs("http://localhost:8080/sockjs/websocket?tableId=-1");
            //服务器
            //websocket = new SocketJs("http://122.152.197.158:10000/sockjs/websocket?tableId=-1");
        }
        websocket.onopen = function (evnt) {
            alert("链接服务器成功!");
            $.ajax({
                method: 'POST',
                url: '/game/getAllTables',
                dataType: "json",
                success: function (data) {
                    if (data.success==true){
                        var tables = data.data;
                        //show tables
                        showTables(tables)
                    }
                },
                error:function () {
                    alert("请求出错！");
                }
            });
        };
        websocket.onmessage = function (evnt) {
            var tables = $.parseJSON(evnt.data);
            //show tables
            showTables(tables);
        };

        websocket.onerror = function (evnt) {
        };
        websocket.onclose = function (evnt) {
            alert("与服务器断开了链接!");
            //window.location.reload();
        }

    });

    function showTables(tables) {
        $("#dataDiv").html(tables);
        var str = "";
        $.each (tables, function(i,t){
            if (t.status == 0 && t.players.length < 2){
                str += '<button onclick="chooseTable(' + t.tableId + ')">加入' + t.tableId + '号桌</button>';
            } else {
                str += '<button onclick="chooseTable(' + t.tableId + ')" disabled>加入' + t.tableId + '号桌</button>';
            }
        });

        $("#tablesDiv").html(str);

    }
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
