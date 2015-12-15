<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

        window.onload = function() {
            connect('ws://' + window.location.host + '/ws');
            /////////////////////////
            var resultArray = ${fundStarterBean.showCommentsProject()};
            for(var counter = 0; counter < resultArray.length;counter++)
            {
                var aux = resultArray[counter];
                var commentsBox = document.getElementById('comments-box');
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
                    commenter.innerHTML = aux.from;
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
                /*var commenter = document.createElement('strong');
                commenter.className = "pull-left primary-font";
                commenter.style.wordWrap = 'break-word';
                commenter.innerHTML = aux.from;
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
                commentsBox.scrollTop = commentsBox.scrollHeight;*/
            }
            ////////////////////////
            document.getElementById("userComment").focus();
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
            document.getElementById('userComment').onkeydown = function(key) {
                if (key.keyCode == 13)
                    doSend();
            };
        }

        function onClose(event) {
            document.getElementById('userComment').onkeydown = null;
        }

        function onMessage(message) {
            writeToCommentsBox(message);
        }

        function onError(event) {
            writeToCommentsBox('WebSocket error (' + event.data + ').');
            document.getElementById('userComment').onkeydown = null;
        }

        function doSend() {
            var message = JSON.stringify({"from" : "${session.fundStarterBean.username}", "text" : document.getElementById('userComment').value, "date": getFormattedDate()});
            if (document.getElementById('userComment').value  != '')
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
            var jsonObj = JSON.parse(message.data);
            var commentsBox = document.getElementById('comments-box');
            var commenter = document.createElement('strong');
            commenter.className = "pull-left primary-font";
            commenter.style.wordWrap = 'break-word';
            commenter.innerHTML = jsonObj.from;
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
            comment.style.wordWrap = 'break-word';
            comment.innerHTML = jsonObj.text;
            commentsBox.appendChild(comment);
            commentsBox.scrollTop = commentsBox.scrollHeight;
            document.getElementById('userComment').value = '';
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


<div class = "supporting-details">
    <h3>View Details of project</h3>
    <div class = "row" id = "menu7">
        <div class = "col-md-3"></div>
        <div class = "col-md-6">
            <c:set var="temp" value="one
            two"/>
            <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
            <c:forEach items = "${fn:split(fundStarterBean.viewProject(),newline)}" var = "value">
                <ul class = "list-group">
                    <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                        <li class = "list-group-item"><c:out value="${value2}"/></li>
                    </c:forEach>
                </ul>
            </c:forEach>
        </div>
    </div>
    <div class = "row">
        <h4>Contribute to this project</h4>
        <s:form role = "form" method = "post" action="contributeProject">
            <div class = "col-md-3"></div>
            <label class="control-label col-sm-1" for = "pledge_value"> <s:text name="Pledge value" /></label>
            <div class = "col-md-2">
                <s:textfield name="pledgeValue" class="form-control" />
            </div>
            <label class="control-label col-sm-1" for = "sel5"> <s:text name="Vote for alternative" /></label>
            <div class = "col-md-2">
                <c:set var="temp" value="one
                two"/>
                <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                <select class="form-control" id="sel5" name="alternativeVotedId">
                    <c:forEach var= "value" items = "${fn:split(fundStarterBean.getAlternativeIdsProject(),newline)}">
                        <option value="${value}"><c:out value="${value}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class = "col-md-2">
                <button class="btn btn-primary" id = "contribute-btn">Contribute</button>
            </div>
        </s:form>
    </div>
</div>
<!--comments-->
<div class="container-comments">
    <div class = "col-lg-3"></div>
    <div class="col-lg-6 col-sm-6 text-center">
        <div class="well">
            <h4>Comment Project</h4>
            <form role="form" action = "commentProject" method = "post">
                <div class="input-group">
                    <input type="text" name = "comment" id="userComment" class="form-control input-sm chat-input" placeholder="Write your comment here..." />
                            <span class="input-group-btn">
                                <button class="btn btn-primary btn-sm" id = "add-comment-btn"><span class="glyphicon glyphicon-comment"></span> Add Comment</button>
                            </span>
                </div>
            </form>
            <hr data-brackets-id="12673">
            <ul data-brackets-id="12674" class="list-unstyled ui-sortable" id = "comments-box">
            </ul>
        </div>
    </div>
</div>
<!--websocket notifications box-->
<jsp:include page="websocketbox.jsp"/>
<div class="footer-viewDetails">
    <div class="container">
        <div class="col-md-2">
        </div>
        <div class="col-md-4">
            <h3><strong>Authors</strong></h3>
            <ul class = "list-unstyled">
                <li>JoÃ£o GonÃ§alves 2012143747</li>
                <li>JoÃ£o Subtil 2012151975</li>
            </ul>
        </div>
        <div class="col-md-2">
        </div>
        <div class="col-md-4">
            <h3><strong>FundStarter</strong></h3>
            <ul class = "list-unstyled">
                <li>Sistemas DistribuÃ­dos 2015/2016</li>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
