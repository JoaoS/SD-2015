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

<jsp:include page="header.jsp"/>
<div class = "row" id = "remove-rewards-main">
    <div class = "col-md-3"></div>
    <div class = "col-md-6">
        <!--List a all rewards from a project-->
        <c:set var="temp" value="one
        two"/>
        <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
        <c:forEach items = "${fn:split(fundStarterBean.listRewards(),newline)}" var = "value">
            <ul class = "list-group">
                <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                    <li class = "list-group-item"><c:out value="${value2}"/></li>
                </c:forEach>
            </ul>
        </c:forEach>


        <s:form action="removeRewardFromProject" method="post" role = "form" >
            <c:set var="temp" value="one
            two"/>
            <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
            <div class="form-group">
                <label for="sel7">Remove reward with id</label>
                <select name= "idtoRemove" class="form-control" id="sel7">
                    <c:forEach items="${fn:split(fundStarterBean.getRewardsProjectIds(),newline)}" var="i">
                        <option value="${i}">
                            <c:out value="${i}"/>
                        </option>
                    </c:forEach>
                </select>
                <br>
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
