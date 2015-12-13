<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<html>
<head>
    <title>FundStarter</title>
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

        window.onload = function () {
            connect('ws://' + window.location.host + '/ws');
            document.getElementById("userComment").focus();
        }

        function connect(host) { // connect to the host websocket
            if ('WebSocket' in window)
                websocket = new WebSocket(host);
            else if ('MozWebSocket' in window)
                websocket = new MozWebSocket(host);
            else {
                writeToCommentsBox('Get a real browser which supports WebSocket.');
                return;
            }
            websocket.onopen = onOpen; // set the event listeners below
            websocket.onclose = onClose;
            websocket.onmessage = onMessage;
            websocket.onerror = onError;
        }

        function onOpen(event) {
            document.getElementById('userComment').onkeydown = function (key) {
                if (key.keyCode == 13)
                    doSend(); // call doSend() on enter key
            };
        }

        function onClose(event) {
            document.getElementById('userComment').onkeydown = null;
        }

        function onMessage(message) {
            writeToCommentsBox(message.data);
        }

        function onError(event) {
            writeToCommentsBox('WebSocket error (' + event.data + ').');
            document.getElementById('userComment').onkeydown = null;
        }

        function doSend() {
            var message = document.getElementById('userComment').value;
            if (message != '')
                websocket.send(message); // send the message
            document.getElementById('userComment').value = '';
        }

        function writeToCommentsBox(text) {
            var commentsBox = document.getElementById('comments-box');
            var line = document.createElement('div');
            line.style.wordWrap = 'break-word';
            //line.innerHTML = '<strong class="pull-left primary-font">Taylor</strong><small class="pull-right text-muted"><span class="glyphicon glyphicon-time"></span>14 mins ago</small></br><li class="ui-state-default">Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</li>';
            line.innerHTML = '<strong class="pull-left primary-font">' + "<c:out value="${session.username}"/>" + "</strong>" + '<small class= "pull-right text-muted"><span class="glyphicon glyphicon-time"></span>14 mins ago</small></br>';
            commentsBox.appendChild(line);
            var comment = document.createElement('li');
            comment.className = "ui-state-default";
            comment.style.wordWrap = 'break-word';
            comment.innerHTML = text;
            commentsBox.appendChild(comment);
            commentsBox.scrollTop = commentsBox.scrollHeight;
        }

    </script>
</head>
<body>
<!--Header-->
<jsp:include page="header.jsp"/>

<div class="supporting-details">
    <h3>View Details of project</h3>

    <div class="row" id="menu7">
        <div class="col-md-3"></div>
        <div class="col-md-6">
            <c:set var="temp" value="one
            two"/>
            <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
            <c:forEach items="${fn:split(fundStarterBean.viewProject(),newline)}" var="value">
                <ul class="list-group">
                    <c:forEach items="${fn:split(value,'|')}" var="value2">
                        <li class="list-group-item"><c:out value="${value2}"/></li>
                    </c:forEach>
                </ul>
            </c:forEach>
        </div>
    </div>
    <div class="row">
        <h4>Contribute to this project</h4>
        <s:form role="form" method="post" action="contributeProject">
            <div class="col-md-3"></div>
            <label class="control-label col-sm-1" for="pledge_value"> <s:text name="Pledge value"/></label>

            <div class="col-md-2">
                <s:textfield name="pledgeValue" class="form-control"/>
            </div>
            <label class="control-label col-sm-1" for="sel5"> <s:text name="Vote for alternative"/></label>

            <div class="col-md-2">
                <c:set var="temp" value="one
                two"/>
                <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                <select class="form-control" id="sel5" name="alternativeVotedId">
                    <c:forEach var="value" items="${fn:split(fundStarterBean.getAlternativeIdsProject(),newline)}">
                        <option value="${value}"><c:out value="${value}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary" id="contribute-btn">Contribute</button>
            </div>
        </s:form>
    </div>
</div>
<!--comments-->
<div class="container-comments">
    <div class="col-lg-3"></div>
    <div class="col-lg-6 col-sm-6 text-center">
        <div class="well" id="comments-box">
            <h4>Comment Project</h4>

            <div class="input-group">
                <input type="text" id="userComment" class="form-control input-sm chat-input"
                       placeholder="Write your comment here..."/>
	                <span class="input-group-btn">
                        <a href="#" class="btn btn-primary btn-sm" id="add-comment-btn"><span
                                class="glyphicon glyphicon-comment"></span> Add Comment</a>
                    </span>
            </div>
            <hr data-brackets-id="12673">
            <ul data-brackets-id="12674" id="sortable" class="list-unstyled ui-sortable">
                <strong class="pull-left primary-font">James</strong>
                <small class="pull-right text-muted">
                    <span class="glyphicon glyphicon-time"></span>7 mins ago
                </small>
                </br>
                <li class="ui-state-default">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod
                    tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                    exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                </li>
                </br>
                <strong class="pull-left primary-font">Taylor</strong>
                <small class="pull-right text-muted">
                    <span class="glyphicon glyphicon-time"></span>14 mins ago
                </small>
                </br>
                <li class="ui-state-default">Duis aute irure dolor in reprehenderit in voluptate velit esse cillum
                    dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui
                    officia deserunt mollit anim id est laborum.
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="footer-viewDetails">
    <div class="container">
        <div class="col-md-2">
        </div>
        <div class="col-md-4">
            <h3><strong>Authors</strong></h3>
            <ul class="list-unstyled">
                <li>João Gonçalves 2012143747</li>
                <li>João Subtil 2012151975</li>
            </ul>
        </div>
        <div class="col-md-2">
        </div>
        <div class="col-md-4">
            <h3><strong>FundStarter</strong></h3>
            <ul class="list-unstyled">
                <li>Sistemas Distribuídos 2015/2016</li>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
