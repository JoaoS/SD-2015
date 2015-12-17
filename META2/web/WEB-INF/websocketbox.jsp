<%--
  Created by IntelliJ IDEA.
  User: joaosubtil
  Date: 14/12/15
  Time: 22:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript">

    var websocket = null;

    window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
        connect('ws://' + window.location.host + '/wsGeneric');
        document.getElementById("notifications-label").focus();


    }

    function connect(host) { // connect to the host websocket
        if ('WebSocket' in window)
            websocket = new WebSocket(host);
        //isto Ã© para o firefox
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
    }

    function onClose(event) {
        writeToHistory('WebSocket closed.');
       // document.getElementById('notifications-label').onkeydown = null;
    }

    function onMessage(message) { // print the received message
        writeToHistory(message.data);
    }

    function onError(event) {
        writeToHistory('WebSocket error (' + event.data + ').');
       // document.getElementById('notifications-label').onkeydown = null;
    }


    function writeToHistory(text) {
        var aux=text.substr(0,4);
        if (aux=='UPDT'){
            var aux2=text.split("|");
            var projectValue=aux2[2];
            var updtID=aux2[1];
            window.alert(updtID);
            document.getElementById(updtID).innerHTML = "Current value : "+projectValue;

        }
        else{
            var history = document.getElementById('notifications-history');
            var line = document.createElement('p');
            line.style.wordWrap = 'break-word';
            line.innerHTML = text;
            history.appendChild(line);
            history.scrollTop = history.scrollHeight;

        }

    }

</script>
<div class="col-md-2">
    <h1 id="notifications-h1">
        <span class="label label-default" id="notifications-label">Notifications</span>
    </h1>

    <div id="notifications-container">
        <div id="notifications-history"></div>
    </div>
</div>
