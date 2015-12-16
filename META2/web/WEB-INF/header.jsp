<%--
  Created by IntelliJ IDEA.
  User: joaosubtil
  Date: 11/12/15
  Time: 17:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<script type="text/javascript">

    var websocket = null;

    window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
        connect('ws://' + window.location.host + '/wsGeneric');
        document.getElementById("chat").focus();
    }

    function connect(host) { // connect to the host websocket
        if ('WebSocket' in window)
            websocket = new WebSocket(host);
        //isto é para o firefox
        else if ('MozWebSocket' in window)
            websocket = new MozWebSocket(host);
        else {
            writeToHistory('Get a real browser which supports WebSocket.');
            return;
        }

        websocket.onopen    = onOpen; // set the event listeners below
        websocket.onclose   = onClose;
        websocket.onmessage = onMessage;
        websocket.onerror   = onError;
    }

    function onOpen(event) {
        writeToHistory('Connected to ' + window.location.host + '.');
        document.getElementById('chat').onkeydown = function(key) {
            if (key.keyCode == 13)
                doSend(); // call doSend() on enter key
        };
    }

    function onClose(event) {
        writeToHistory('WebSocket closed.');
        document.getElementById('chat').onkeydown = null;
    }

    function onMessage(message) { // print the received message
        writeToHistory(message.data);
    }

    function onError(event) {
        writeToHistory('WebSocket error (' + event.data + ').');
        document.getElementById('chat').onkeydown = null;
    }

    function doSend() {
        var message = document.getElementById('chat').value;
        if (message != '')
            websocket.send(message); // send the message
        document.getElementById('chat').value = '';

    }

    function writeToHistory(text) {
        var history = document.getElementById('history');
        var line = document.createElement('p');
        line.style.wordWrap = 'break-word';
        line.innerHTML = text;
        history.appendChild(line);
        history.scrollTop = history.scrollHeight;
    }

</script>


<div class="header">
    <div class = "row">
        <div class="col-md-10">
            <h1>FundStarter</h1>
        </div>
        <div class="col-md-1">
            <form role = "form" method = "post" action = "menuIni">
                <button class="btn btn-primary" id="menuIni-btn">Initial Menu</button>
            </form>
        </div>
        <div class="col-md-1">
            <form role = "form" method = "post" action = "logout">
                <button class="btn btn-primary" id="logout-btn">Logout</button>
            </form>
        </div>
    </div>
    <!--
    todo será necessario adicionar aqui um espaço para ver as notificações(websockets)???

    -->
</div>