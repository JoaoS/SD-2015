<%--
  Created by IntelliJ IDEA.
  User: joaosubtil
  Date: 08/12/15
  Time: 15:33
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
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

</head>
<body>

<jsp:include page="header.jsp"/>

<div class = "add-reward-main">
            <h3>Add reward to Project</h3>
            <s:form action="addRewardToProject"  method="post"  class = "form-horizontal" role = "form" >
                <div class="input_fields_rewards">
                    <div class = "row" required>
                        <div class = "col-sm-2"></div>
                        <label class="control-label col-sm-1" for = "description"> <s:text name="Reward " /></label>
                        <div class = "col-sm-3">
                            <s:textfield name="description" class="form-control" required = "true" />
                        </div>
                        <label class="control-label col-sm-1" for = "value"> <s:text name="Pledge value" /></label>
                        <div class = "col-sm-2">
                            <s:textfield name = "value" class = "form-control" required = "true" pattern="[0-9]*" title="Only positive numbers"/>
                        </div>
                    </div>
                </div>
                <s:submit type = "button" class="btn btn-primary btn-lg center-block" id="add-reward-btn"/>
            </s:form>
        </div>
        <div class = "col-md-2">
            <jsp:include page="websocketbox.jsp"/>
        </div>
    </div>
    <!--websocket notifications box-->
</div>

<div class="footer-menuIni">
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
