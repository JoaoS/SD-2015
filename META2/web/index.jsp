<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>FundStarter Home Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link rel='stylesheet' href='style.css'/>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <!-- Latest compiled JavaScript -->
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <body>
    <s:form action="login" method="post"  class = "form-horizontal" role = "form">
        <div class="form-group" id = "login-form">
              <label  class="control-label col-sm-4" for = "username"> <s:text name="Username:" /></label>
               <div class="col-sm-4">
                    <s:textfield name="username" class="form-control"/>
               </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-4" for = "password"> <s:text name="Password:" /></label>
              <div class="col-sm-4">
                  <s:textfield name="password" class="form-control" />
              </div>
          </div>
          <s:submit type = "button" class="btn btn-primary btn-lg center-block" id="login-btn"/>
    </s:form>
    </form>
  </body>
</html>

