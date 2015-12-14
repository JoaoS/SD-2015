<%--
  Created by IntelliJ IDEA.
  User: joaosubtil
  Date: 08/12/15
  Time: 15:33
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
    <title>FundStarter add Reward</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link rel='stylesheet' href='../style.css'/>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <!-- Latest compiled JavaScript -->
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script type="text/javascript">

        var websocket = null;

        window.onload = function() {
            connect('ws://' + window.location.host + '/ws');
            /////////////////////////
            var resultArray = ${fundStarterBean.showCommentsProjectAdmin()};
            for(var counter = 0; counter < resultArray.length;counter++)
            {
                var aux = resultArray[counter];
                var commentsBox = document.getElementById('replies-box');
                if(aux.hasOwnProperty("idReply"))
                {
                    var commenter = document.createElement('strong');
                    commenter.className = "pull-right primary-font";
                    commenter.style.wordWrap = 'break-word';
                    commenter.innerHTML = aux.from;
                    commentsBox.appendChild(commenter);
                    var moment =  document.createElement('small');
                    moment.className = "pull-left text-muted";
                    moment.style.wordWrap = 'break-word';
                    var hour  = document.createElement('span');
                    hour.className = "glyphicon glyphicon-time";
                    hour.style.wordWrap = 'break-word';
                    hour.innerHTML = aux.date;
                    moment.appendChild(hour);
                    commentsBox.appendChild(moment);
                    var br = document.createElement('br');
                    br.style.wordWrap = 'break-word';
                    commentsBox.appendChild(br);
                    var comment = document.createElement('li');
                    comment.className = "ui-state-default";
                    comment.id = "replies";
                    comment.style.wordWrap = 'break-word';
                    comment.innerHTML = aux.text;
                    commentsBox.appendChild(comment);
                    commentsBox.scrollTop = commentsBox.scrollHeight;
                }
                else
                {
                    var commenter = document.createElement('strong');
                    commenter.className = "pull-left primary-font";
                    commenter.style.wordWrap = 'break-word';
                    commenter.innerHTML = aux.from + " Message ID:" + aux.idMessage;
                    commentsBox.appendChild(commenter);
                    var moment =  document.createElement('small');
                    moment.className = "pull-right text-muted";
                    moment.style.wordWrap = 'break-word';
                    var hour  = document.createElement('span');
                    hour.className = "glyphicon glyphicon-time";
                    hour.style.wordWrap = 'break-word';
                    hour.innerHTML = aux.date;
                    moment.appendChild(hour);
                    commentsBox.appendChild(moment);
                    var br = document.createElement('br');
                    br.style.wordWrap = 'break-word';
                    commentsBox.appendChild(br);
                    var comment = document.createElement('li');
                    comment.className = "ui-state-default";
                    comment.style.wordWrap = 'break-word';
                    comment.innerHTML = aux.text;
                    commentsBox.appendChild(comment);
                    commentsBox.scrollTop = commentsBox.scrollHeight;
                }
            }
            ////////////////////////
            document.getElementById("replyComment").focus();
        };


        function connect(host) { // connect to the host websocket
            if ('WebSocket' in window)
                websocket = new WebSocket(host);
            else if ('MozWebSocket' in window)
                websocket = new MozWebSocket(host);
            else {
                writeToCommentsBox('Get a real browser which supports WebSocket.');
                return;
            }
            websocket.onopen    = onOpen; // set the event listeners below
            websocket.onclose   = onClose;
            websocket.onmessage = onMessage;
            websocket.onerror   = onError;
        }

        function onOpen(event) {
            document.getElementById('replyComment').onkeydown = function(key) {
                if (key.keyCode == 13)
                    doSend();
            };
        }

        function onClose(event) {
            document.getElementById('replyComment').onkeydown = null;
        }

        function onMessage(message) {
            writeToCommentsBox(message);
        }

        function onError(event) {
            writeToCommentsBox('WebSocket error (' + event.data + ').');
            document.getElementById('replyComment').onkeydown = null;
        }

        function doSend() {
            var message = JSON.stringify({"from" : "${session.fundStarterBean.username}", "text" : document.getElementById('replyComment').value, "date": getFormattedDate()});
            if (document.getElementById('replyComment').value  != '')
                websocket.send(message);
        }

        function getFormattedDate()
        {
            var now = new Date();
            var year = now.getFullYear();
            var month = now.getMonth().toString();
            month = month.length > 1 ? month : '0' + month;
            var day = now.getDay().toString();
            day = day.length > 1 ? day : '0' + day;
            var hour = now.getHours().toString();
            hour = hour.length > 1 ? hour : '0' + hour;
            var min = now.getMinutes().toString();
            min = min.length > 1 ? min : '0' + min;
            return " " + day + "/" + month + "/" + year + " " + hour + ":" + min;
        }


        function writeToCommentsBox(message) {
            var aux = JSON.parse(message.data);
            var commentsBox = document.getElementById('replies-box');
            /*var commenter = document.createElement('strong');
            commenter.className = "pull-left primary-font";
            commenter.style.wordWrap = 'break-word';
            commenter.innerHTML = jsonObj.from + " Message ID:" + aux.idMessage;
            commentsBox.appendChild(commenter);
            var moment =  document.createElement('small');
            moment.className = "pull-right text-muted";
            moment.style.wordWrap = 'break-word';
            var hour  = document.createElement('span');
            hour.className = "glyphicon glyphicon-time";
            hour.style.wordWrap = 'break-word';
            hour.innerHTML = jsonObj.date;
            moment.appendChild(hour);
            commentsBox.appendChild(moment);
            var br = document.createElement('br');
            br.style.wordWrap = 'break-word';
            commentsBox.appendChild(br);
            var comment = document.createElement('li');
            comment.className = "ui-state-default";
            comment.id = "replies";
            comment.style.wordWrap = 'break-word';
            comment.innerHTML = jsonObj.text;
            commentsBox.appendChild(comment);
            commentsBox.scrollTop = commentsBox.scrollHeight;*/
            if(aux.hasOwnProperty("idReply"))
            {
                var commenter = document.createElement('strong');
                commenter.className = "pull-right primary-font";
                commenter.style.wordWrap = 'break-word';
                commenter.innerHTML = aux.from;
                commentsBox.appendChild(commenter);
                var moment =  document.createElement('small');
                moment.className = "pull-left text-muted";
                moment.style.wordWrap = 'break-word';
                var hour  = document.createElement('span');
                hour.className = "glyphicon glyphicon-time";
                hour.style.wordWrap = 'break-word';
                hour.innerHTML = aux.date;
                moment.appendChild(hour);
                commentsBox.appendChild(moment);
                var br = document.createElement('br');
                br.style.wordWrap = 'break-word';
                commentsBox.appendChild(br);
                var comment = document.createElement('li');
                comment.className = "ui-state-default";
                comment.id = "replies";
                comment.style.wordWrap = 'break-word';
                comment.innerHTML = aux.text;
                commentsBox.appendChild(comment);
                commentsBox.scrollTop = commentsBox.scrollHeight;
            }
            else
            {
                var commenter = document.createElement('strong');
                commenter.className = "pull-left primary-font";
                commenter.style.wordWrap = 'break-word';
                commenter.innerHTML = aux.from + " Message ID:" + aux.idMessage;
                commentsBox.appendChild(commenter);
                var moment =  document.createElement('small');
                moment.className = "pull-right text-muted";
                moment.style.wordWrap = 'break-word';
                var hour  = document.createElement('span');
                hour.className = "glyphicon glyphicon-time";
                hour.style.wordWrap = 'break-word';
                hour.innerHTML = aux.date;
                moment.appendChild(hour);
                commentsBox.appendChild(moment);
                var br = document.createElement('br');
                br.style.wordWrap = 'break-word';
                commentsBox.appendChild(br);
                var comment = document.createElement('li');
                comment.className = "ui-state-default";
                comment.style.wordWrap = 'break-word';
                comment.innerHTML = aux.text;
                commentsBox.appendChild(comment);
                commentsBox.scrollTop = commentsBox.scrollHeight;
            }
            document.getElementById('replyComment').value = '';
        }
    </script>
</head>
<body>

<div class = "header">
    <div class = "col-md-11">
        <h1>FundStarter</h1>
    </div>
    <div class = "cold-md-1">
        <a class  ="btn btn-primary" id = "logout-btn" href ="#">Logout</a>
    </div>
</div>

<!--Reply to supporter's messages-->
<div class="container-comments">
    <div class="col-lg-12 text-center">
        <div class="well">
            <h4>Reply to supporter's messages</h4>
            <form role="form" action = "replyProject" method = "post">
                <div class="input-group">
                    <input type="text" name = "reply" id="replyComment" class="form-control input-sm chat-input" placeholder="Write your reply here..." />
                    <span>
                                                <label for="sel6">Reply to message with id</label>
                                                <c:set var="temp" value="one
                                                two"/>
                                                <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                                                <select name="messageSelected" class="form-control" id="sel6">
                                                    <c:forEach items="${fn:split(fundStarterBean.getMessagesProjectIds(),newline)}" var="i">
                                                        <option value="${i}">
                                                            <c:out value="${i}"/>
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </span>
                                             <span class="input-group-btn">
                                                <button class="btn btn-primary btn-sm" id = "add-reply-btn"><span class="glyphicon glyphicon-comment"></span> Add reply</button>
                                            </span>
                </div>
            </form>
            <hr data-brackets-id="12673">
            <ul data-brackets-id="12674" class="list-unstyled ui-sortable" id = "replies-box">
            </ul>
        </div>
    </div>
</div>
<div class="footer-reply-messages">
    <div class="container">
        <div class="col-md-2">
        </div>
        <div class="col-md-4">
            <h3><strong>Authors</strong></h3>
            <ul class = "list-unstyled">
                <li>João Gonçalves 2012143747</li>
                <li>João Subtil 2012151975</li>
            </ul>
        </div>
        <div class="col-md-2">
        </div>
        <div class="col-md-4">
            <h3><strong>FundStarter</strong></h3>
            <ul class = "list-unstyled">
                <li>Sistemas Distribuídos 2015/2016</li>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
