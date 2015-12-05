<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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
    <div class = "supporting">
          <div class = "row">
           <div class = "col-md-3">
                <div class="navbar-inner">
                    <ul class="nav nav-pills nav-stacked">
                        <li class="active"><a data-toggle="tab" href="#home">List current projects</a></li>
                        <li><a data-toggle="tab" href="#menu1">List old projects</a></li>
                        <li><a data-toggle="tab" href="#menu2">View details of a project</a></li>
                        <li><a data-toggle="tab" href="#menu3">Check Account Balance</a></li>
                        <li><a data-toggle="tab" href="#menu4">Check my rewards</a></li>
                        <li><a data-toggle="tab" href="#menu5">Create project</a></li>
                        <li><a data-toggle="tab" href="#menu6">Administrator menu</a></li>
                    </ul>
                </div>
           </div>
              <div class = "col-md-1">
                  </div>
        <div class = "col-md-6">
            <div class="tab-content">
                <div id="home" class="tab-pane fade in active">
                    <h3>List current projects</h3>
                    <c:set var="temp" value="one
                    two"/>
                    <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                    <c:forEach items = "${fn:split(fundStarterBean.listProjects(1),newline)}" var = "value">
                       <ul class = "list-group">
                        <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                            <li class = "list-group-item"><c:out value="${value2}"/></li>
                        </c:forEach>
                       </ul>
                    </c:forEach>
                </div>
                <div id="menu1" class="tab-pane fade">
                    <h3>List old projects</h3>
                    <c:set var="temp" value="one
                    two"/>
                    <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                    <c:forEach items = "${fn:split(fundStarterBean.listProjects(0),newline)}" var = "value">
                        <ul class = "list-group">
                            <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                                <li class = "list-group-item"><c:out value="${value2}"/></li>
                            </c:forEach>
                        </ul>
                    </c:forEach>
                </div>
                <div id="menu2" class="tab-pane fade">
                    <h3>View details of a project</h3>
                    <c:set var="temp" value="one
                    two"/>
                    <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                    <c:forEach items = "${fn:split(fundStarterBean.listProjects(2),newline)}" var = "value">
                        <ul class = "list-group">
                            <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                                <li class = "list-group-item"><c:out value="${value2}"/></li>
                            </c:forEach>
                        </ul>
                    </c:forEach>
                    <form role="form">
                        <div class="form-group">
                            <label for="sel1">Which project do you want to view the details ?</label>
                            <select class="form-control" id="sel1">
                                <c:forEach var= "i" begin="1" end="${fundStarterBean.getNumberProjects()}">
                                    <option><c:out value="${i}"/></option>
                                </c:forEach>
                            </select>
                            <br>
                        </div>
                    </form>
                </div>
                <div id="menu3" class="tab-pane fade">
                    <h3>Check Account Balance</h3>
                    <p>You have <strong> <c:out value="${fundStarterBean.checkAccountBalance()}"/> </strong>dollars in your  account.</p>
                </div>
                <div id="menu4" class="tab-pane fade">
                    <h3>Check my rewards</h3>
                    <c:set var="temp" value="one
                    two"/>
                    <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                    <c:forEach items = "${fn:split(fundStarterBean.checkRewards(),newline)}" var = "value">
                        <ul class = "list-group">
                            <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                                <li class = "list-group-item"><c:out value="${value2}"/></li>
                            </c:forEach>
                        </ul>
                    </c:forEach>
                </div>
                <div id="menu5" class="tab-pane fade">
                    <h3>Create project</h3>
                    <s:form  method="post"  class = "form-horizontal" role = "form">                    <!-- FALTA A ACTION!!!!!!!!!!!!! -->
                        <div class="form-group" id = "register-form">
                            <label  class="control-label col-sm-4" for = "username"> <s:text name="Name" /></label>
                            <div class="col-sm-4">
                                <s:textfield name="name" class="form-control"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4" for = "description"> <s:text name="Description (max. 400 characters)" /></label>
                            <div class="col-sm-4">
                                <s:textarea name="description" class="form-control" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4" for = "date"> <s:text name="Date (dd/MM/yyyy HH)" /></label>
                            <div class="col-sm-4">
                                <s:textfield name="date" class="form-control" format="dd/MM/yyyy HH"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4" for = "target_value"> <s:text name="Target" /></label>
                            <div class="col-sm-4">
                                <s:textfield name="target_value" class="form-control" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4" for = "entreprise"> <s:text name="Enterprise" /></label>
                            <div class="col-sm-4">
                                <s:textfield name="enterprise" class="form-control" />
                            </div>
                        </div>
                        <s:submit type = "button" class="btn btn-primary btn-lg center-block" id="create-project-btn"/>     <!--Falta rewards e alternatives !!!!-->
                    </s:form>
                </div>
                <div id="menu6" class="tab-pane fade">
                    <h3>Administrator menu</h3>
                    <p>Eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.</p>
                </div>
            </div>
        </div>
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
