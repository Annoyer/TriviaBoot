<#include "const.ftl" >
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>选桌</title>
</head>
<body>
<h5>当前userId=${Session.user.id}</h5>
<#list tables as t>
<#if t.tableId ==0>
<button onclick="chooseTable(${t.tableId})">加入${t.tableId}号桌</button>
<#else >
<button onclick="chooseTable(${t.tableId})" disabled>加入${t.tableId}号桌</button>
</#if>
</#list>
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
