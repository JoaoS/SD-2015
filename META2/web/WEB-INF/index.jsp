<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>FundStarter Home Page</title>
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
    <jsp:include page="notificationsHeader.jsp"/>
    <div class = "main">
        <div class = "container">
            <h1>FundStarter</h1>
        </div>
    </div>
    <div class = "supporting-login">
        <div class = "container">
            <h2>Login</h2>
            <s:form action="login" method="post"  class = "form-horizontal" role = "form">
                <div class="form-group" id = "login-form">
                      <label  class="control-label col-sm-4" for = "username"> <s:text name="Username" /></label>
                       <div class="col-sm-4">
                            <s:textfield name="username" class="form-control" required = "true"/>
                       </div>
                </div>
                <div class="form-group">
                  <label class="control-label col-sm-4" for = "password"> <s:text name="Password" /></label>
                      <div class="col-sm-4">
                          <s:password name="password" class="form-control" required = "true" />
                      </div>
                  </div>
                  <s:submit type = "button" class="btn btn-primary btn-lg center-block" id="login-btn"/>
            </s:form>
            <a class="btn btn-lg btn-social btn-tumblr" id = "tumblr-btn" href = "loginTumblr?login=1">
                <span class="fa fa-tumblr"></span> Login with tumblr
            </a>
        </div>
    </div>
    <div class = "supporting-register">
        <div class = "container">
            <h2>Sign Up</h2>
        </div>
        <s:form action="signUp" method="post"  class = "form-horizontal" role = "form">
            <div class="form-group" id = "register-form">
                <label  class="control-label col-sm-4" for = "username"> <s:text name="Username" /></label>
                <div class="col-sm-4">
                    <s:textfield name="username" class="form-control" required = "true"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-4" for = "password"> <s:text name="Password" /></label>
                <div class="col-sm-4">
                    <s:textfield name="password" class="form-control" required = "true"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-4" for = "password"> <s:text name="BI" /></label>
                <div class="col-sm-4">
                    <s:textfield name="bi" class="form-control" required = "true"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-4" for = "password"> <s:text name="Age" /></label>
                <div class="col-sm-4">
                    <s:textfield name="age" class="form-control" required = "true"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-4" for = "password"> <s:text name="email" /></label>
                <div class="col-sm-4">
                    <s:textfield name="email" class="form-control" required = "true" />
                </div>
            </div>
            <s:submit type = "button" class="btn btn-primary btn-lg center-block" id="register-btn"/>
        </s:form>
        </form>
    </div>
    <div class="footer">
        <div class="container">
            <div class="col-md-2">
            </div>
            <div class="col-md-4">
                <h3><strong>Authors</strong></h3>
                <ul>
                    <li>João Gonçalves 2012143747</li>
                    <li>João Subtil 2012151975</li>
                </ul>
            </div>
            <div class="col-md-2">
            </div>
            <div class="col-md-4">
                <h3><strong>FundStarter</strong></h3>
                <ul>
                    <li>Sistemas Distribuídos 2015/2016</li>
                </ul>
            </div>
        </div>
    </div>
  </body>
</html>

