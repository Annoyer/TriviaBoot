<#include "const.ftl" >
<html>
<head>
    <title>Title</title>
    <style>
        body {
            background:#fff;
            color: lightpink;
            padding:0;
            margin:0;
            overflow:hidden;
            font-family:georgia;
            text-align:center;
        }
        a {	color: #ff0080;	text-decoration: none; }
        a:hover { color: #0080ff; }

        canvas { pointer-events:none; z-index:10;}

        #d { text-align:center; margin:1em 0 -7.5em 0; z-index:1000; position:absolute; display:block }
        .button { background:orange; color:#fff; padding:0.2em 0.5em; cursor:pointer }
        .inactive { background:#999; color:#eee }

        #oldie { margin-top:11em !important }

        .demo{width:760px; height:120px; margin:10px auto;}
        .wrap{width:90px; height:90px; margin:120px auto 30px auto; position:relative}
        .dice{width:90px; height:90px; background:url(http://www.codefans.net/jscss/demoimg/201404/dice.png) no-repeat; cursor:pointer;}
        .dice_1{background-position:-5px -4px}
        .dice_2{background-position:-5px -107px}
        .dice_3{background-position:-5px -212px}
        .dice_4{background-position:-5px -317px}
        .dice_5{background-position:-5px -427px}
        .dice_6{background-position:-5px -535px}
        .dice_t{background-position:-5px -651px}
        .dice_s{background-position:-5px -763px}
        .dice_e{background-position:-5px -876px}
        p#result{text-align:center; font-size:16px}
        p#result span{font-weight:bold; color:#122b40; margin:6px}
        #dice_mask{width:90px; height:90px; background:#fff; opacity:0; position:absolute; top:0; left:0; z-index:999}

        button{
            background-color: hotpink;
            border-color: hotpink;
            color: white;
        }
    </style>

</head>
<body>


    <div class="row">

    </div>
    <div style="position: fixed; left: 85%;top:1%">
     <button class="btn btn-primary" onclick="exit()" style="background-color: hotpink;border-color: hotpink;margin: 30px;">退出</button>
    </div>
    <div style="position: absolute">

        <div style="position: fixed; left: 80%;bottom:10%">
        <div id="dice" class=" dice dice_1" style="display:none;"></div>
            <div id="diceTextDiv"></div>
        </div>
        <div id="player-div" class="row" style="margin-top: 5px;width: 250px">
        </div>
    </div>
    <div id="d">
    </div>

<script src="/js/three/three.js"></script>

<script src="/js/three/Detector.js"></script>
<script src="/js/three/stats.min.js"></script>
<script src="/js/three/BinaryLoader.js"></script>
<script src="/js/sockjs.min.js"></script>

<script type="text/javascript">
    var gameStatus = null;
    var websocket = null;
    $(function () {
        if ('WebSocket' in window) {
            console.log("此浏览器支持websocket");
            //本地
            websocket = new WebSocket("ws://localhost:8080/websocket?tableId=${tableId}");
            //服务器
            //websocket = new WebSocket("ws://122.152.197.158:10000/websocket?tableId=${tableId}");
        } else if ('MozWebSocket' in window) {
            alert("此浏览器只支持MozWebSocket");
        } else {
            console.log("此浏览器只支持SockJS");
            //本地
            websocket = new SocketJs("http://localhost:8080/sockjs/websocket?tableId=${tableId}");
            //服务器
            //websocket = new SocketJs("http://122.152.197.158:10000/sockjs/websocket?tableId=${tableId}");
        }
        websocket.onopen = function (evnt) {
           // alert("链接服务器成功!");
            $("#btnReady").removeAttr("disabled");
            $.ajax({
                method: 'POST',
                url: '/game/loadGameInfo',
                data: {
                    tableId: ${tableId}
                },
                dataType: "json",
                success: function (data) {
                    if (data.success == true) {
                        $("#playerDiv").html("");
//                        var str = "";
                        var playerStr="";
                        var players = data.data;
                        for (var i = 0; i < players.length; i++) {
//                            str += "<div>" + players[i].playerName + " - ";
                            playerStr+=" <div class='row'  style='margin: 20px'><div class='col-xs-4'><img src='/img/player"+i+".png' style='width:50px'></div><div class='col-xs-8 container' id='player-div"+i+"'><div class='row'  style='font-size: 20px'><div class='col-xs-8'>"
                                    +players[i].playerName+"</div><div class='col-xs-4'>lv"+players[i].user.level+"</div></div><div class='row' id='readyDiv"+i+"'>";
                            if (players[i].isReady) {
//                                str += "已准备";
                                playerStr+="<p>已准备</p>";
                            } else {
//                                str += "未准备";
                                if(players[i].user.id==${Session.user.id}){
                                    playerStr+="<button class='btn' onclick='ready()'>准备</button>"
                                }
                                else{
                                    playerStr+="<p>未准备</p>";
                                }
                            }
//                            str += "</div>";
                            playerStr+="</div></div></div>";
                        }
//                        $("#playerDiv").html(str);
                        $("#player-div").html(playerStr);
                    } else {
                        alert(data.error);
                        window.location.href = '/game/tables';
                    }
                },
                error: function () {
                    alert("请求出错！");
                }
            });

        };
        websocket.onmessage = function (evnt) {
            var msg = evnt.data;

            if (msg == "start") {
               //alert("游戏开始");
                //$("#statusDiv").html("游戏开始");

            } else {
                //收到的是gameStatus的json字符串
                //$("#jsonDiv").html(msg);
                gameStatus = eval("(" + msg + ")");//把字符串解析成json

                if (gameStatus.status == 0) {
                    //在游戏开始前，有新玩家加入，或者有玩家离开这个桌子
//                    $("#statusDiv").html("桌上玩家发生变化");
//                    $("#currnetStepDiv").html("游戏待开始。若您准备好，请按下“准备”按钮，等全部【2】名玩家都按下准备按钮后，开始游戏。");
                    var players = gameStatus.players;

//                    var str = "";
                    var playerStr="";
                    for (var i = 0; i < players.length; i++) {
//                            str += "<div>" + players[i].playerName + " - ";
                        playerStr+=" <div class='row'  style='margin: 20px'><div class='col-xs-4'><img src='/img/player"+i+".png' style='width:50px'></div><div class='col-xs-8 container' id='player-div"+i+"'><div class='row'  style='font-size: 20px'><div class='col-xs-8'>"
                                +players[i].playerName+"</div><div class='col-xs-4'>lv"+players[i].user.level+"</div></div><div class='row' id='readyDiv"+i+"'>";
                        if (players[i].isReady) {
//                                str += "已准备";
                            playerStr+="<p>已准备</p>";
                        } else {
//                                str += "未准备";
                            if(players[i].user.id==${Session.user.id}){
                                playerStr+="<button class='btn' onclick='ready()'>准备</button>"
                            }
                            else{
                                playerStr+="<p>未准备</p>";
                            }
                        }
//                            str += "</div>";
                        playerStr+="</div></div></div>";
                    }
//                    $("#playerDiv").html(str);
                    $("#player-div").html(playerStr);
                } else {
                    //游戏已经开始

                    //显示桌上玩家信息
                    var players = gameStatus.players;
//                    var str = "";

                    for (var i = 0; i < players.length; i++) {
                        var playerStr="<div class='col-xs-2'><img src='/img/coin.png' style='width:20px;'></div><div class='col-xs-1' style='font-size:10px;'>" + players[i].sumOfGoldCoins+"</div>";

                        if(i==gameStatus.currentPlayerIndex){
                            playerStr+="<div class='col-xs-3'><img src='/img/diceIcon.png' width='20px'></div>";
                        }
                        if(players[i].inPenaltyBox){
                            playerStr+="<div class='col-xs-2'><img src='/img/inBox.png' width='20px'></div>";
                        }
                        else {
                            playerStr += "</div>";
                        }
//                        str += "<div>" + players[i].playerName + ": 位置-" + players[i].place +
//                                "金币数-" + players[i].sumOfGoldCoins +
//                                "是否在禁闭室内-" + players[i].inPenaltyBox + "</div>";
                        $("#readyDiv"+i).html(playerStr);
                    }
//                    $("#playerDiv").html(str);

//                    $("#currentPlayerDiv").html(gameStatus.currentPlayerId);

                    //游戏进程判断
                    if (gameStatus.status == 1) {
                        //case1：这一轮是第一轮
                        //初始化player models
                        init();
                        animate();
                        rollIfMyTurn();

                    } else if (gameStatus.status == 2) {
                        //case2：玩家点了掷骰子以后，要提问了
//                        $("#statusDiv").html("显示骰子点数，玩家移动到最新位置，提问");
//                        $("#currnetStepDiv").html("玩家" + gameStatus.currentPlayerId + "回答问题");
//                        $("#currentPlayerDiv").html(gameStatus.currentPlayerId);
//                        $("#diceDiv").html(gameStatus.dice);
                        //设置当前移动玩家和目标格
                        currentPlayer = playerModels[gameStatus.currentPlayerIndex];
                        targetCube=gameCubes[players[gameStatus.currentPlayerIndex].place];

                        <#--if (gameStatus.currentQuestion != null) {-->
                            <#--var q = '<div>请输入答案选项的下标，从0开始</div>' +-->
                                    <#--'<div>问题类型：' + gameStatus.currentQuestion.domain + '</div>' +-->
                                    <#--'<div>题目：' + gameStatus.currentQuestion.title + '</div>' +-->
                                    <#--'<div>选项：' + gameStatus.currentQuestion.answers.replace(/#/g, ' ') + '</div>';-->
                            <#--$("#questionDiv").html(q);-->
                            <#--if (gameStatus.currentPlayerId == ${Session.user.id}) {-->
                                <#--$("#answerInput").removeAttr("disabled");-->
                                <#--$("#btnSubmitAnswer").removeAttr("disabled");-->
                            <#--}-->
                        <#--} else {-->
<#--//                            $("#currnetStepDiv").html("当前玩家在禁闭室中");-->
                            <#--rollIfMyTurn();-->
                        <#--}-->

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
                        if (gameStatus.winner == null) {
                            //alert("游戏结束,没有人获胜");
                            endAlert("游戏结束,没有人获胜");
                        } else {
                            endAlert("游戏结束,胜者是：" + gameStatus.winner.playerName);
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
           // alert("与服务器断开了链接!");
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

    <#--function stopDice() {-->
        <#--$("#statusDiv").html("您掷了骰子");-->
        <#--$.ajax({-->
            <#--method: 'POST',-->
            <#--url: '/game/stopDice',-->
            <#--data: {-->
                <#--tableId: ${tableId}-->
            <#--},-->
            <#--success: function () {-->

            <#--},-->
            <#--error: function () {-->
                <#--alert("请求出错！");-->
            <#--}-->
        <#--});-->
    <#--}-->

    function stopDice2(num){
        $("#statusDiv").html("您掷了骰子");
        $.ajax({
            method: 'POST',
            url: '/game/stopDice',
            data: {
                tableId: ${tableId},
                num:num
            },
            success: function () {
                if(gameStatus.currentQuestion==null){
                    stillInBoxAlert();

                }
            },
            error: function () {
                alert("请求出错！");
            }
        });
    }

    function submitAnswer(answer) {
        var isCorrect = (gameStatus.currentQuestion.rightAnswer == answer);
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
                if(gameStatus.status==3){
                    rightAlert();
                }
                else if(gameStatus.status==4){
                    wrongAlert();
                }
            },
            error: function () {
                alert("请求出错！");
            }
        });
    }

    function rollIfMyTurn() {
        if (gameStatus.currentPlayerId ==${Session.user.id}) {
            document.getElementById("dice").setAttribute("style","display:block");
            $("#dice").bind("click",clickDice);
            $("#diceTextDiv").html("你可以掷骰子了");
            $("#currentStepDiv").html("你可以掷骰子了");
            $("#btnStopDice").removeAttr("disabled");

        } else {
            $("#currnetStepDiv").html("玩家" + gameStatus.currentPlayerId + "掷骰子");
            $("#btnStopDice").attr("disabled", "disabled");
        }


    }

    function exit() {
//        if (websocket != null) {
//            websocket.close();
//        }
        window.location.href = '/game/tables';
    }

    $(window).bind('beforeunload',exit);


    if ( ! Detector.webgl ) Detector.addGetWebGLMessage();

    var container, stats;

    var camera, scene, renderer;

    var mesh, geometry;

    var player;

    var gameCubes=[];

    var loader;

    var pointLight;

    var mouseX = 0;
    var mouseY = 0;

    var playerModels=[];
    var targetCube; //当前移动目标格
    var currentPlayer; //当前轮到的玩家

    var windowHalfX = window.innerWidth / 2;
    var windowHalfY = window.innerHeight / 2;

    //document.addEventListener('mousemove', onDocumentMouseMove, false);



    function init() {

        container = document.createElement( 'div' );
        document.body.appendChild( container );

        camera = new THREE.PerspectiveCamera( 50, window.innerWidth / window.innerHeight, 1, 5000 );
        camera.position.x=-2000;
        camera.position.y=2000;
        camera.position.z = 2000;
        scene = new THREE.Scene();
        //skybox

        var path = "/skybox/";
        var format = '.jpg';
        var urls = [
            path + 'px' + format, path + 'nx' + format,
            path + 'py' + format, path + 'ny' + format,
            path + 'pz' + format, path + 'nz' + format
        ];

        var reflectionCube = new THREE.CubeTextureLoader().load( urls );
        reflectionCube.format = THREE.RGBFormat;

//set sky box
        scene.background = reflectionCube;


        var refractionCube = new THREE.CubeTextureLoader().load( urls );
        refractionCube.mapping = THREE.CubeRefractionMapping;
        refractionCube.format = THREE.RGBFormat;

        // LIGHTS

        var ambient = new THREE.AmbientLight( 0xffffff );
        scene.add( ambient );

        pointLight = new THREE.PointLight( 0xffffff, 2 );
        scene.add( pointLight );

        // light representation

        var sphere = new THREE.SphereGeometry( 100, 16, 8 );

        var mesh = new THREE.Mesh( sphere, new THREE.MeshBasicMaterial( { color: 0xffaa00 } ) );
        mesh.scale.set( 0.05, 0.05, 0.05 );
        pointLight.add( mesh );



        //var cubeMaterial3 = new THREE.MeshPhongMaterial( { color: 0x000000, specular:0xaa0000, envMap: reflectionCube, combine: THREE.MixOperation, reflectivity: 0.25 } );
        var cubeMaterial3 = new THREE.MeshLambertMaterial( { color: 0xffee00, envMap: refractionCube, refractionRatio: 0.95 } );
        var cubeMaterial2 = new THREE.MeshLambertMaterial( { color: 0xffee00, envMap: refractionCube, refractionRatio: 0.95 } );
        var cubeMaterial1 = new THREE.MeshLambertMaterial( { color: 0xffffff, envMap: reflectionCube ,refractionRatio: 0.95 } );



        //

        renderer = new THREE.WebGLRenderer();
        renderer.setPixelRatio( window.devicePixelRatio );
        renderer.setSize( window.innerWidth, window.innerHeight );
        container.appendChild( renderer.domElement );

        //

     //   stats = new Stats();
     //   container.appendChild( stats.dom );

        //
        geometry=new THREE.CubeGeometry(50,50,50);
        createScene( geometry, cubeMaterial1, cubeMaterial2, cubeMaterial3 );
        // loader = new THREE.BinaryLoader();
        // loader.load( "obj/walt/WaltHead_bin.js", function( geometry ) { createScene( geometry, cubeMaterial1, cubeMaterial2, cubeMaterial3 ) } );

        //


        window.addEventListener( 'resize', onWindowResize, false );

    }

    //适应屏幕大小
    function onWindowResize() {

        windowHalfX = window.innerWidth / 2;
        windowHalfY = window.innerHeight / 2;

        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();

        renderer.setSize( window.innerWidth, window.innerHeight );

    }

    function createScene( geometry, m1, m2, m ) {

        var s = 10;

        var mesh1 = new THREE.Mesh( geometry, m1 );
        mesh1.position.set(-600,0,-600);
        mesh1.scale.x = mesh1.scale.y = mesh1.scale.z = s;
        scene.add( mesh1 );
        gameCubes.push(mesh1);

        var mesh2 = new THREE.Mesh( geometry, m2 );
        mesh2.position.set(0,0,-600);
        mesh2.scale.x = mesh2.scale.y = mesh2.scale.z = s;
        scene.add( mesh2 );
        gameCubes.push(mesh2);

        var mesh3 = new THREE.Mesh( geometry, m );
        mesh3.position.set(600,0,-600);
        mesh3.scale.x = mesh3.scale.y = mesh3.scale.z = s;
        scene.add( mesh3 );
        gameCubes.push(mesh3);

        var mesh4 = new THREE.Mesh( geometry, m );
        mesh4.position.set(1200,0,-600);
        mesh4.scale.x = mesh4.scale.y = mesh4.scale.z = s;
        scene.add( mesh4 );
        gameCubes.push(mesh4);


        var mesh5 = new THREE.Mesh( geometry, m );
        mesh5.position.set(1200,0,0);
        mesh5.scale.x = mesh5.scale.y = mesh5.scale.z = s;
        scene.add( mesh5 );
        gameCubes.push(mesh5);

        var mesh6 = new THREE.Mesh( geometry, m );
        mesh6.position.set(1200,0,600);
        mesh6.scale.x = mesh6.scale.y = mesh6.scale.z = s;
        scene.add( mesh6 );
        gameCubes.push(mesh6);

        var mesh7 = new THREE.Mesh( geometry, m );
        mesh7.position.set(1200,0,1200);
        mesh7.scale.x = mesh7.scale.y = mesh7.scale.z = s;
        scene.add( mesh7 );
        gameCubes.push(mesh7);

        var mesh8 = new THREE.Mesh( geometry, m );
        mesh8.position.set(600,0,1200);
        mesh8.scale.x = mesh8.scale.y = mesh8.scale.z = s;
        scene.add( mesh8 );
        gameCubes.push(mesh8);

        var mesh9 = new THREE.Mesh( geometry, m );
        mesh9.position.set(0,0,1200);
        mesh9.scale.x = mesh9.scale.y = mesh9.scale.z = s;
        scene.add( mesh9 );
        gameCubes.push(mesh9);

        var mesh10 = new THREE.Mesh( geometry, m );
        mesh10.position.set(-600,0,1200);
        mesh10.scale.x = mesh10.scale.y = mesh10.scale.z = s;
        scene.add( mesh10 );
        gameCubes.push(mesh10);

        var mesh11 = new THREE.Mesh( geometry, m );
        mesh11.position.set(-600,0,600);
        mesh11.scale.x = mesh11.scale.y = mesh11.scale.z = s;
        scene.add( mesh11 );
        gameCubes.push(mesh11);
        targetCube=mesh11;

        var mesh12 = new THREE.Mesh( geometry, m );
        mesh12.position.set(-600,0,0);
        mesh12.scale.x = mesh12.scale.y = mesh12.scale.z = s;
        scene.add( mesh12 );
        gameCubes.push(mesh12);

//
        //绘制player
        for(var p=0;p<gameStatus.players.length;p++){
            var meterial;
            if(p==0){
                meterial=new THREE.MeshBasicMaterial({color: 0xa6d4ae});}
            else if(p==1){
                meterial=new THREE.MeshBasicMaterial({color: 0xf7b970});}
            else if(p==2){
                meterial=new THREE.MeshBasicMaterial({color: 0xC3E6F8});}
            else if(p==3){
                meterial=new THREE.MeshBasicMaterial({color: 0xF08E83});}
            var playerModel=new THREE.Mesh(new THREE.SphereBufferGeometry(10,50,50),meterial);
            playerModel.position.set(-600,600,- 600);
            playerModel.scale.x=playerModel.scale.y=playerModel.scale.z=15;
            playerModels.push(playerModel);
            scene.add(playerModel);
        }
    }

    // function onDocumentMouseMove(event) {

    // 	mouseX = ( event.clientX - windowHalfX ) * 4;
    // 	mouseY = ( event.clientY - windowHalfY ) * 4;

    // }

    //

    function animate() {

        requestAnimationFrame( animate );

        render();
       // stats.update();

    }

    function render() {
        if(gameStatus.status!=2){}
        else if(currentPlayer==null || targetCube==null){}
        else if(gameStatus.status == 2 && currentPlayer.position.x != targetCube.position.x || currentPlayer.position.z != targetCube.position.z) {
                if (currentPlayer.position.z == -600 && currentPlayer.position.x >= -600 && currentPlayer.position.x < 1200) {
                    currentPlayer.position.x += 10;
                }
                else if (currentPlayer.position.x == 1200 && currentPlayer.position.z < 1200 && currentPlayer.position.z >= -600) {
                    currentPlayer.position.z += 10;
                }
                else if (currentPlayer.position.z == 1200 && currentPlayer.position.x <= 1200 && currentPlayer.position.x > -600) {
                    currentPlayer.position.x -= 10;
                }
                else if (currentPlayer.position.x == -600 && currentPlayer.position.z <= 1200 && currentPlayer.position.z > -600) {
                    currentPlayer.position.z -= 10;
                }
        }
        //当前玩家移动到方格之后提问
        else if(gameStatus.status == 2 && currentPlayer.position.x == targetCube.position.x && currentPlayer.position.z == targetCube.position.z){
            gameStatus.status=-2;
            //回答问题玩家窗口弹出问题
            if(gameStatus.currentPlayerId == ${Session.user.id}){
                questionTest();
            }
            else{
                otherPlayerQuestionAlert(gameStatus.currentPlayerIndex);
            }

        }



        // pointLight.position.x = 1500 * Math.cos( timer );
        // pointLight.position.z = 1500 * Math.sin( timer );

        // camera.position.x += ( mouseX - camera.position.x ) * .05;
        // camera.position.y += ( - mouseY - camera.position.y ) * .05;

        camera.lookAt( scene.position );

        renderer.render( scene, camera );

    }



     function playerMove(num,now){
     	targetCube=gameCubes[(num+now)%12];
     	currentPlayer=player;
     }


     function endAlert(alertStr) {
         $.jAlert({
             'title': '游戏结束',
             'content': '<p>' + alertStr + '</p>',
             'theme': 'blue',
             'btnBackground': false,
             'btns': {
                 'text':'确定', 'theme': 'blue', 'onClick': function(e, btn){
                     e.preventDefault();
                     location.href="/game/tables";

                 }
             }
         });
     }

    function rightAlert() {
        $.jAlert({
            'title': 'yeah!yeah!yeah!',
            'content': '<p>回答正确!加一个金币23333</p>',
            'theme': 'green',
            'showAnimation': 'flipInX',
            'hideAnimation': 'flipOutX',
            'closeOnClick': true,
            'onOpen': function(alert)
            {
                setTimeout(function(){
                    alert.closeAlert();
                }, 1000);
            }
        });
    }

    function wrongAlert() {
        $.jAlert({
            'title': 'emmmmm...',
            'content': '<p>回答错误或超时！你被禁了23333</p>',
            'theme': 'red',
            'showAnimation': 'flipInX',
            'hideAnimation': 'flipOutX',
            'closeOnClick': true,
            'onOpen': function(alert)
            {
                setTimeout(function(){
                    alert.closeAlert();
                }, 1000);
            }

        });
    }

    function stillInBoxAlert() {
        setTimeout(function(){
            document.getElementById("dice").setAttribute("style","display:none");
        }, 1500);
        $.jAlert({
            'title': 'emmmmm...',
            'content': '<p>点数为偶数！小可怜！你还在禁闭室23333</p>',
            'theme': 'red',
            'showAnimation': 'flipInX',
            'hideAnimation': 'flipOutX',
            'closeOnClick': true,
            'onOpen': function(alert)
            {
                setTimeout(function(){
                    alert.closeAlert();
                }, 1000);
            }

        });
    }
    function otherPlayerQuestionAlert(i){
         if(title=gameStatus.currentQuestion!=null) {
             var title = gameStatus.currentQuestion.title;
             var answers = gameStatus.currentQuestion.answers;
             $.jAlert({
                 'title': gameStatus.players[i].playerName + '正在回答：',
                 'content': '<p>(问题领域：'+gameStatus.currentQuestion.domain+')</p><p>' + title +'</p><p>A.' + answers.split("#")[0] +
                 '</p><p>B.' + answers.split("#")[1] +
                 '</p><p>C.' + answers.split("#")[2] +
                 '</p><p>D.' + answers.split("#")[3] + '</p>',
                 'theme': 'yellow',
                 'closeBtn':false, 'closeOnClick': false, 'closeOnEsc': false,
                 'showAnimation': 'flipInX',
                 'hideAnimation': 'flipOutX',
                 'closeOnClick': true,
                 'onOpen': function (alert) {
                     setTimeout(function () {
                         alert.closeAlert();
                     }, 2000);
                 }

             });
         }
    }

    function questionTest(){
         if(title=gameStatus.currentQuestion!=null) {
             var title = gameStatus.currentQuestion.title;
             var answers = gameStatus.currentQuestion.answers;
             theme = 'yellow';
             $.jAlert({
                 'title': '问题',
                 'content': '<div style="height: 30px"><p  style="display: inline">倒计时</p><p  style="display: inline" id="timecount">10</p><p  style="display: inline">秒</p></div><p>(问题领域：'+gameStatus.currentQuestion.domain+')</p><p>' + title +'</p><p>A.' + answers.split("#")[0] +
                 '</p><p>B.' + answers.split("#")[1] +
                 '</p><p>C.' + answers.split("#")[2] +
                 '</p><p>D.' + answers.split("#")[3] + '</p>',
                 'theme': theme,
                 'closeBtn':false, 'closeOnClick': false, 'closeOnEsc': false,
                 'btnBackground': false,
                 'btns': [
                     {'text': 'A', 'theme': theme, 'id': 0, 'class': 'answerBtn'},
                     {'text': 'B', 'theme': theme, 'id': 1, 'class': 'answerBtn'},
                     {'text': 'C', 'theme': theme, 'id': 2, 'class': 'answerBtn'},
                     {'text': 'D', 'theme': theme, 'id': 3, 'class': 'answerBtn'}
                 ],
                 'onOpen':function (alert){
                     timeCount();
                     setTimeout(function () {
                         alert.closeAlert();
                     }, 10000);
                 }

             });

             $(".answerBtn").click(function () {
                 submitAnswer(this.getAttribute("id"));
                 document.getElementById("dice").setAttribute("style","display:none");
             });
             return false;
         }else{
             rollIfMyTurn();
         }
    }

    function timeCount(){
        document.getElementById('timecount').innerHTML=parseInt(document.getElementById('timecount').innerHTML)-1;
        if( document.getElementById('timecount').innerHTML=='0'){
            submitAnswer(-1);
            document.getElementById("dice").setAttribute("style","display:none");
            return;
        }
        setTimeout(timeCount, 1000);
    }


 //   $(".dice").click(function(){
    function clickDice() {
        $("#dice").unbind("click");
        if(gameStatus.currentPlayerId == ${Session.user.id}) {
            $("#diceTextDiv").html("");
            var dice = $(".dice");
            $(".wrap").append("<div id='dice_mask'></div>");//加遮罩
            dice.attr("class", "dice");//清除上次动画后的点数
            dice.css('cursor', 'default');
            var num = Math.floor(Math.random() * 6 + 1);//产生随机数1-6
            dice.animate({left: '+2px'}, 100, function () {
                dice.addClass("dice_t");
            }).delay(200).animate({top: '-2px'}, 100, function () {
                dice.removeClass("dice_t").addClass("dice_s");
            }).delay(200).animate({opacity: 'show'}, 600, function () {
                dice.removeClass("dice_s").addClass("dice_e");
            }).delay(100).animate({left: '-2px', top: '2px'}, 100, function () {
                dice.removeClass("dice_e").addClass("dice_" + num);
                $("#result").html("您掷得点数是<span>" + num + "</span>");
                dice.css('cursor', 'pointer');
                $("#dice_mask").remove();//移除遮罩
            });
            stopDice2(num);
        }
    }

</script>
</body>
</html>