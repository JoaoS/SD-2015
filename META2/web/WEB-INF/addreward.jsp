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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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
    <script>
        $(document).ready(function() {
            var max_fields      = 20; //maximum input boxes allowed
            var wrapper         = $(".input_fields_rewards"); //Fields wrapper
            var add_button      = $(".add_field_button"); //Add button ID
            var add_button2 = $(".add_field_button2");
            var wrapper2 = $(".input_fields_alternatives");
            var x = 1; //initlal text box count
            $(add_button).click(function(e){ //on add input button click
                e.preventDefault();
                if(x < max_fields){ //max input box allowed
                    x++; //text box increment
                    $(wrapper).append(
                            '<div class = "row"> ' +
                            '<label class="control-label col-sm-3" for = "reward_description2">Reward</label> ' +
                            '<div class = "col-sm-3">' +
                            '<s:textfield name="reward_description2" class="form-control" />' +
                            '</div>' +
                            '<label class="control-label col-sm-2" for = "min_pledge2">Minimum pledge</label>' +
                            '<div class = "col-sm-2">' +
                            '<s:textfield name = "min_pledge2" class = "form-control"/>' +
                            '</div>' +
                            '<div class = "col-sm-1"><a href="#" class="remove_field">Remove</a></div>' +
                            '</div>'); //add input box
                }
            });

            var y = 1; //initlal text box count
            $(add_button2).click(function(e){ //on add input button click
                e.preventDefault();
                if(y < max_fields){ //max input box allowed
                    y++; //text box increment
                    $(wrapper2).append(
                            '<div class = "row"> ' +
                            '<label class="control-label col-sm-3" for = "alternative_description2">Alternative</label> ' +
                            '<div class = "col-sm-3">' +
                            '<s:textfield name="alternative_description2" class="form-control" />' +
                            '</div>' +
                            '<label class="control-label col-sm-2" for = "divisor2">Divisor</label>' +
                            '<div class = "col-sm-2">' +
                            '<s:textfield name = "divisor2" class = "form-control"/>' +
                            '</div>' +
                            '<div class = "col-sm-1"><a href="#" class="remove_field">Remove</a></div>' +
                            '</div>'); //add input box
                }
            });

            $(wrapper).on("click",".remove_field", function(e){ //user click on remove text
                e.preventDefault();
                //$(this).parent('div').remove();
                $(this).parent('div').parent().remove();
                x--;
            })

            $(wrapper2).on("click",".remove_field", function(e){ //user click on remove text
                e.preventDefault();
                //$(this).parent('div').remove();
                $(this).parent('div').parent().remove();
                x--;
            })


        });
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

            <h3>Create project</h3>
            <s:form action="addRewardToProject"  method="post"  class = "form-horizontal" role = "form">                    <!-- FALTA A ACTION!!!!!!!!!!!!! -->

                <div class="input_fields_rewards">
                    <div class = "row">
                        <label class="control-label col-sm-3" for = "description"> <s:text name="Reward " /></label>
                        <div class = "col-sm-3">
                            <s:textfield name="description" class="form-control" />
                        </div>
                        <label class="control-label col-sm-2" for = "value"> <s:text name="Pledge value" /></label>
                        <div class = "col-sm-2">
                            <s:textfield name = "value" class = "form-control"/>
                        </div>
                    </div>
                </div>
                <s:submit type = "button" class="btn btn-primary btn-lg center-block" id="create-project-btn"/>
            </s:form>

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
