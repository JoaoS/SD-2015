<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
                            '<label class="control-label col-sm-3" for = "rewardDescriptionList">Reward</label> ' +
                            '<div class = "col-sm-3">' +
                            '<s:textfield name="rewardDescriptionList" class="form-control" />' +
                            '</div>' +
                            '<label class="control-label col-sm-2" for = "minPledgeList">Minimum pledge</label>' +
                            '<div class = "col-sm-2">' +
                            '<s:textfield name = "minPledgeList" class = "form-control"/>' +
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
                            '<label class="control-label col-sm-3" for = "alternativeDescriptionList">Alternative</label> ' +
                            '<div class = "col-sm-3">' +
                            '<s:textfield name="alternativeDescriptionList" class="form-control" />' +
                            '</div>' +
                            '<label class="control-label col-sm-2" for = "divisorList">Divisor</label>' +
                            '<div class = "col-sm-2">' +
                            '<s:textfield name = "divisorList" class = "form-control"/>' +
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

        //funÃ§Ã£o de teste !!!!!
       /* window.onload = function()
        {
            document.getElementById("ID : 4").innerHTML = "Current value : 400000000";
        };*/
    </script>

</head>
<body>

<!-- notificaÃ§oes erros e sucess-->
<jsp:include page="notificationsHeader.jsp"/>
<!--fim dos alerts-->

<!--header with logout button-->
<jsp:include page="header.jsp"/>


<div class="supporting">
    <div class="row">
        <div class="col-md-3">
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
        <div class="col-md-1">
        </div>
        <div class="col-md-6">
            <div class="tab-content">
                <div id="home" class="tab-pane fade in active">
                    <h3>List current projects</h3>              <!-- if split == ID: , id = ID:-->
                    <c:set var="temp" value="one
                    two"/>
                    <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                    <c:forEach items = "${fn:split(fundStarterBean.listProjects(1),newline)}" var = "value">
                        <ul class = "list-group">
                            <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                                <c:choose>
                                    <c:when test="${fn:contains(value2, 'ID')}">
                                        <c:set var = "currentValueId" value = "${value2}"/>
                                        <li class = "list-group-item"><c:out value="${value2}"/></li>
                                    </c:when>
                                    <c:when test="${fn:contains(value2, 'Current value')}">
                                        <li class = "list-group-item" id = "${currentValueId}"><c:out value="${value2}"/></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class = "list-group-item"><c:out value="${value2}"/></li>
                                    </c:otherwise>
                                </c:choose>
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
                                <c:choose>
                                    <c:when test="${fn:contains(value2, 'ID')}">
                                        <c:set var = "currentValueId" value = "${value2}"/>
                                        <li class = "list-group-item"><c:out value="${value2}"/></li>
                                    </c:when>
                                    <c:when test="${fn:contains(value2, 'Current value')}">
                                        <li class = "list-group-item" id = "${currentValueId}"><c:out value="${value2}"/></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class = "list-group-item"><c:out value="${value2}"/></li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </ul>
                    </c:forEach>
                </div>
                <div id="menu2" class="tab-pane fade">
                    <h3>View details of a project</h3>
                    <form role="form" action = "viewDetails">
                        <div class="form-group">
                            <label for="sel1">Which project do you want to view the details ?</label>
                            <select name = "viewDetailsId" class="form-control" id="sel1">
                                <c:set var="temp" value="one
                                two"/>
                                <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                                <c:forEach items = "${fn:split(fundStarterBean.getNumberProjects(),newline)}" var = "value">>
                                    <option value="${value}"><c:out value="${value}"/></option>
                                </c:forEach>
                            </select>
                            <br>
                        </div>
                        <s:submit type="button" class="btn btn-primary btn-md center-block" id="viewDetails-btn"/>
                    </form>
                    <c:set var="temp" value="one
                    two"/>
                    <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                    <c:forEach items = "${fn:split(fundStarterBean.listProjects(2),newline)}" var = "value">
                        <ul class = "list-group">
                            <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                                <c:choose>
                                    <c:when test="${fn:contains(value2, 'ID')}">
                                        <c:set var = "currentValueId" value = "${value2}"/>
                                        <li class = "list-group-item"><c:out value="${value2}"/></li>
                                    </c:when>
                                    <c:when test="${fn:contains(value2, 'Current value')}">
                                        <li class = "list-group-item" id = "${currentValueId}"><c:out value="${value2}"/></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class = "list-group-item"><c:out value="${value2}"/></li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </ul>
                    </c:forEach>
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
                    <s:form  method="post"  class = "form-horizontal" role = "form" action = "createProject">
                        <div class="form-group" id = "register-form">
                            <label  class="control-label col-sm-4" for = "projectName"> <s:text name="Name" /></label>
                            <div class="col-sm-4">
                                <s:textfield name="projectName" class="form-control" required = "true"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4" for = "description"> <s:text name="Description (max. 400 characters)" /></label>
                            <div class="col-sm-4">
                                <s:textarea name="description" class="form-control" required = "true"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4" for = "projectDate"> <s:text name="Date (dd/MM/yyyy HH)" /></label>
                            <div class="col-sm-4">
                                <s:textfield name="projectDate" class="form-control" format="dd/MM/yyyy HH" required = "true"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4" for = "targetValue"> <s:text name="Target value" /></label>
                            <div class="col-sm-4">
                                <s:textfield name="targetValue" class="form-control" required = "true" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4" for = "entreprise"> <s:text name="Enterprise" /></label>
                            <div class="col-sm-4">
                                <s:textfield name="enterprise" class="form-control" />
                            </div>
                        </div>
                        <div class="input_fields_rewards">
                            <div class = "row">
                                <label class="control-label col-sm-3" for = "rewardDescriptionList"> <s:text name="Reward" /></label>
                                <div class = "col-sm-3">
                                    <s:textfield name="rewardDescriptionList" class="form-control" required = "true"/>
                                </div>
                                <label class="control-label col-sm-2" for = "minPledgeList"> <s:text name="Minimum pledge" /></label>
                                <div class = "col-sm-2">
                                    <s:textfield name = "minPledgeList" class = "form-control" required = "true"/>
                                </div>
                                <div class = "col-sm-1">
                                    <button class="add_field_button">+</button>
                                </div>
                            </div>
                        </div>
                        <div class="input_fields_alternatives">
                            <div class = "row">
                                <label class="control-label col-sm-3" for = "alternativeDescriptionList"> <s:text name="Alternative" /></label>
                                <div class = "col-sm-3">
                                    <s:textfield name="alternativeDescriptionList" class="form-control"/>
                                </div>
                                <label class="control-label col-sm-2" for = "divisorList"> <s:text name="Divisor*" /></label>
                                <div class = "col-sm-2">
                                    <s:textfield name = "divisorList" class = "form-control"/>
                                </div>
                                <div class = "col-sm-1">
                                    <button class="add_field_button2">+</button>
                                </div>
                            </div>
                        </div>
                        <s:submit type = "button" class="btn btn-primary btn-lg center-block" id="create-project-btn"/>
                        <p> <strong>* Divisor(Nr votes of pledge = ceil(pledge value/(divisor * minimum value of pledge).</strong></p>
                    </s:form>
                </div>
                <div id="menu6" class="tab-pane fade">
                    <h3>Administrator menu</h3>
                    <h5>Projects that you administrate</h5>
                    <c:set var="temp" value="one
                    two"/>
                    <c:set var="newline" value="${fn:substring(temp,3,4)}"/>
                    <c:forEach items = "${fn:split(fundStarterBean.showAdminProjects(),newline)}" var = "value">
                        <ul class = "list-group">
                            <c:forEach items = "${fn:split(value,'|')}" var = "value2">
                                <li class = "list-group-item">
                                    <c:out value="${value2}"/></li>
                            </c:forEach>
                        </ul>
                    </c:forEach>
                    <div class = "row">
                        <s:form action="gotoRewardpageAction" method="post"  >
                            <div class="form-group">
                                <label for="sel2">Add rewards to project</label>
                                <select name="idSelected" class="form-control" id="sel2">
                                    <c:forEach items="${fn:split(fundStarterBean.getAdminProjectIds(),'.')}" var="i">
                                       <option value="${i}">
                                            <c:out value="${i}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                                <br>
                            </div>
                            <s:submit type="button" class="btn btn-primary btn-md center-block" id="idReward" value="Add Reward"/>
                        </s:form>
                    </div>
                    <div class = "row">
                        <s:form action="gotoRemovePage" role="form">
                            <div class="form-group">
                                <label for="sel3">Remove rewards from project</label>
                                <select name="idSelected" class="form-control" id="sel3">
                                    <c:forEach items="${fn:split(fundStarterBean.getAdminProjectIds(),'.')}" var="i">
                                        <option value="${i}">
                                            <c:out value="${i}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                                <br>
                            </div>
                            <s:submit type="button" id="idReward" value="Remove Reward" class="btn btn-primary btn-md center-block"/>

                        </s:form>
                    </div>
                    <div class = "row">
                            <s:form action="cancelProjectAction" role="form" method="post">
                                <div class="form-group">
                                    <label for="sel4">Cancel project</label>
                                    <select name="idSelected" class="form-control" id="sel4">
                                        <c:forEach items="${fn:split(fundStarterBean.getAdminProjectIds(),'.')}" var="i">
                                            <option value="${i}">
                                                <c:out value="${i}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <br>
                                </div>
                                <s:submit type="button" id="idReward" value="Cancel Project" class="btn btn-primary btn-md center-block"/>

                            </s:form>
                    </div>
                    <div class = "row">
                        <s:form action="replyMessageAction" role="form" method="post">
                            <div class="form-group">
                                <label for="sel7">Reply to project with id</label>
                                <select name="idSelected" class="form-control" id="sel7">
                                    <c:forEach items="${fn:split(fundStarterBean.getAdminProjectIds(),'.')}" var="i">
                                        <option value="${i}">
                                            <c:out value="${i}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                                <br>
                            </div>
                            <s:submit type="button" id = "reply-btn" value="Reply to messages" class="btn btn-primary btn-md center-block"/>
                        </s:form>
                    </div>
                </div>
            </div>
        </div>
        <!--websocket notifications box-->
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
<!-- todo
ver warnings que têm de ser gerados !!!!!!!!!!!
testar inserir campos vazios e caso seja necessario fazer interceptors
-->