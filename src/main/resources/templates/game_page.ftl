<#include "const.ftl" >
<html>
<head>
    <title>Title</title>
</head>
<body>
<h5>欢迎，${Session.user.username}</h5>
<div>
    当前游戏状态：<span id="statusDiv" style="color: red"></span>
</div>
<div>
    接下来的操作：<span id="currnetStepDiv" style="color: red"></span>
</div>
<br>

<div>
    <div>桌上的玩家们：</div>
    <div id="playerDiv">
    <#list players as p>
        <div>
        ${p.playerName} - <#if p.isReady >已准备<#else >未准备</#if>
        </div>
    </#list>
    </div>
</div>
<br>

<div>
    <div>游戏进程：</div>
    <div>当前玩家ID：<span id="currentPlayerDiv" style="color: #074c8a"></span></div>
    <div>骰子点数：<span id="diceDiv" style="color: #074c8a"></span></div>
</div>
<br>

<div>
    <button id="btnReady" onclick="ready()" disabled="disabled">准备</button>
    <button id="btnStopDice" onclick="stopDice()" disabled="disabled">掷骰子</button>
    <div id="questionDiv" style="color: darkcyan"></div>
    <input type="number" id="answerInput" disabled="disabled">
    <button id="btnSubmitAnswer" onclick="submitAnswer()" disabled="disabled">提交问题</button>
    <button onclick="exit()">退出</button>
</div>
<br>

<div>
    gameStatus：<span id="jsonDiv" style="color: black"></span>
</div>
</body>
</html>
<script type="text/javascript">
    var gameStatus = null;
    var websocket = null;
    $(function () {
        if ('WebSocket' in window) {
            console.log("此浏览器支持websocket");
            websocket = new WebSocket("ws://localhost:8080/websocket/${tableId}/${Session.user.id}");
        } else if ('MozWebSocket' in window) {
            alert("此浏览器只支持MozWebSocket");
        } else {
            alert("此浏览器只支持SockJS");
        }
        websocket.onopen = function (evnt) {
            alert("链接服务器成功!");
            $("#btnReady").removeAttr("disabled");
        };
        websocket.onmessage = function (evnt) {
            var msg = evnt.data;

            if (msg == "start") {
                alert("游戏开始");
                $("#statusDiv").html("游戏开始");
            } else {
                //收到的是gameStatus的json字符串
                $("#jsonDiv").html(msg);
                gameStatus = eval("(" + msg + ")");//把字符串解析成json

                if (gameStatus.status == 0) {
                    //在游戏开始前，有新玩家加入，或者有玩家离开这个桌子
                    $("#statusDiv").html("桌上玩家发生变化");
                    $("#currnetStepDiv").html("游戏待开始。若您准备好，请按下“准备”按钮，等全部【2】名玩家都按下准备按钮后，开始游戏。");
                    var players = gameStatus.players;
                    var str = "";
                    for (var i = 0; i < players.length; i++) {
                        str += "<div>" + players[i].playerName + "-" + players[i].isReady + "</div>";
                    }
                    $("#playerDiv").html(str);

                } else {
                    //游戏已经开始

                    //显示桌上玩家信息
                    var players = gameStatus.players;
                    var str = "";
                    for (var i = 0; i < players.length; i++) {
                        str += "<div>" + players[i].playerName + ": 位置-" + players[i].place +
                                "金币数-" + players[i].sumOfGoldCoins +
                                "是否在禁闭室内-" + players[i].inPenaltyBox + "</div>";
                    }
                    $("#playerDiv").html(str);
                    $("#currentPlayerDiv").html(gameStatus.currentPlayerId);

                    //游戏进程判断
                    if (gameStatus.status == 1) {
                        //case1：这一轮是第一轮
                        rollIfMyTurn();

                    } else if (gameStatus.status == 2) {
                        //case2：玩家点了掷骰子以后，要提问了
                        $("#statusDiv").html("显示骰子点数，玩家移动到最新位置，提问");
                        $("#currnetStepDiv").html("玩家" + gameStatus.currentPlayerId + "回答问题");
                        $("#currentPlayerDiv").html(gameStatus.currentPlayerId);
                        $("#diceDiv").html(gameStatus.dice);

                        //重绘一下界面，来更新玩家在地图上的位置…………

                        if (gameStatus.currentQuestion != null) {
                            var q = '<div>请输入答案选项的下标，从0开始</div>' +
                                    '<div>问题类型：' + gameStatus.currentQuestion.domain + '</div>' +
                                    '<div>题目：' + gameStatus.currentQuestion.title + '</div>' +
                                    '<div>选项：' + gameStatus.currentQuestion.answers.replace(/#/g, ' ') + '</div>';
                            $("#questionDiv").html(q);
                            if (gameStatus.currentPlayerId == ${Session.user.id}) {
                                $("#answerInput").removeAttr("disabled");
                                $("#btnSubmitAnswer").removeAttr("disabled");
                            }
                        } else {
                            $("#currnetStepDiv").html("当前玩家在禁闭室中");
                            rollIfMyTurn();
                        }

                    } else if (gameStatus.status == 3) {
                        $("#statusDiv").html("上一位玩家回答正确，正确答案是：" + gameStatus.currentQuestion.rightAnswer);
                        //重绘一下界面，来更新玩家在地图上的位置…………
                        rollIfMyTurn();
                    } else if (gameStatus.status == 4) {
                        $("#statusDiv").html("上一位玩家回答错误，正确答案是：" + gameStatus.currentQuestion.rightAnswer);
                        //重绘一下界面，来更新玩家在地图上的位置…………
                        rollIfMyTurn();
                    } else if (gameStatus.status == -1) {
                        //重绘一下界面，来更新玩家在地图上的位置…………
                        if (gameStatus.winner.playerName == null) {
                            $("#statusDiv").html("游戏结束,没有人获胜");
                        } else {
                            $("#statusDiv").html("游戏结束,胜者是：" + gameStatus.winner.playerName);
                            var isWinner = (gameStatus.winner.user.id == ${Session.user.id});
                            $.ajax({
                                method: 'POST',
                                url: '/user/recordGameResult',
                                data: {
                                    isWinner: isWinner
                                },
                                dataType: "json",
                                success: function (data) {
                                    if (data.success != true) {
                                        alert(data.error);
                                    }
                                    websocket.close();
                                    websocket = null;
                                },
                                error: function () {
                                    alert("请求出错！");
                                    websocket.close();
                                    websocket = null;
                                }
                            });

                        }
                    }
                }


            }
        };

        websocket.onerror = function (evnt) {
        };
        websocket.onclose = function (evnt) {
            alert("与服务器断开了链接!");
            //window.location.href = '/game/tables';
        }

        $('#send').bind('click', function () {
            send();
        });

        function send() {
            if (websocket != null) {
                var message = document.getElementById('message').value;
                websocket.send(message);
            } else {
                alert('未与服务器链接.');
            }
        }
    });

    function ready() {
        $.ajax({
            method: 'POST',
            url: '/game/setReady',
            data: {
                tableId: ${tableId}
            },
            success: function () {
                $('#btnReady').attr("disabled", "disabled");
                $("#statusDiv").html("已准备");
                $("#currnetStepDiv").html("等待游戏开始");
            },
            error: function () {
                alert("请求出错！");
            }
        });
    }

    function stopDice() {
        $("#statusDiv").html("您掷了骰子");
        $.ajax({
            method: 'POST',
            url: '/game/stopDice',
            data: {
                tableId: ${tableId}
            },
            success: function () {

            },
            error: function () {
                alert("请求出错！");
            }
        });
    }

    function submitAnswer() {
        var isCorrect = (gameStatus.currentQuestion.rightAnswer == $("#answerInput").val());
        $("#statusDiv").html("提交回答");
        $("#answerInput").attr("disabled", "disabled");
        $("#btnSubmitAnswer").attr("disabled", "disabled");
        $.ajax({
            method: 'POST',
            url: '/game/answerQuestion',
            data: {
                tableId: ${tableId},
                isCorrect: isCorrect
            },
            success: function () {

            },
            error: function () {
                alert("请求出错！");
            }
        });
    }

    function rollIfMyTurn() {
        if (gameStatus.currentPlayerId ==${Session.user.id}) {
            $("#currnetStepDiv").html("你可以掷骰子了");
            $("#btnStopDice").removeAttr("disabled");
        } else {
            $("#currnetStepDiv").html("玩家" + gameStatus.currentPlayerId + "掷骰子");
            $("#btnStopDice").attr("disabled", "disabled");
        }
    }

    function exit() {
//        if (websocket != null) {
//            websocket.close();
//            websocket = null;
//        }
        window.location.href = '/game/tables';
    }

    $(window).bind('beforeunload',exit);
</script>
