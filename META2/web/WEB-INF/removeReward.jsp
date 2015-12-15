<%--
  Created by IntelliJ IDEA.
  User: joaosubtil
  Date: 09/12/15
  Time: 16:02
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
    <title>FundStarter remove Reward</title>
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

<div class="header">
    <div class="col-md-11">
        <h1>FundStarter</h1>
    </div>
    <div class="cold-md-1">
        <a class="btn btn-primary" id="logout-btn" href="#">Logout</a>
    </div>
</div>

<!--List a all rewards from a project-->
<c:set var="temp" value="one
two"/>
<c:set var="newline" value="${fn:substring(temp,3,4)}"/>
<c:forEach items="${fn:split(fundStarterBean.listRewards(),newline)}" var="value">
    <ul class="list-group">
        <c:forEach items="${fn:split(value,'|')}" var="value2">
            <li class="list-group-item"><c:out value="${value2}"/></li>
        </c:forEach>
    </ul>
</c:forEach>
<div class="row">
    <div class="col-md-9">
        <s:form action="removeRewardFromProject" method="post" class="form-horizontal" role="form">
            <div class="input_fields_rewards">
                <div class="row">
                    <label class="control-label col-sm-3" for="idtoRemove"> <s:text name="Reward to remove "/></label>

                    <div class="col-sm-3">
                        <s:textfield name="idtoRemove" class="form-control"/>
                    </div>

                </div>
            </div>
            <s:submit type="button" class="btn btn-primary btn-lg center-block" id="create-project-btn"/>
        </s:form>
    </div>
    <div class="col-md-2">
        <jsp:include page="websocketbox.jsp"/>
    </div>
</div>
<div class="footer-menuIni">
    <div class="container">
        <div class="col-md-2">
        </div>
        <div class="col-md-4">
            <h3><strong>Authors</strong></h3>
            <ul class="list-unstyled">
                <li>JoÃ£o GonÃ§alves 2012143747</li>
                <li>JoÃ£o Subtil 2012151975</li>
            </ul>
        </div>
        <div class="col-md-2">
        </div>
        <div class="col-md-4">
            <h3><strong>FundStarter</strong></h3>
            <ul class="list-unstyled">
                <li>Sistemas DistribuÃ­dos 2015/2016</li>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
