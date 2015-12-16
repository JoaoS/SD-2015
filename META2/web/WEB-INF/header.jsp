<%--
  Created by IntelliJ IDEA.
  User: joaosubtil
  Date: 11/12/15
  Time: 17:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<div class="header">
    <div class = "row">
        <div class="col-md-10">
            <h1>FundStarter</h1>
        </div>
        <div class="col-md-1">
            <form role = "form" method = "post" action = "menuIni">
                <button class="btn btn-primary" id="menuIni-btn">Initial Menu</button>
            </form>
        </div>
        <div class="col-md-1">
            <form role = "form" method = "post" action = "logout">
                <button class="btn btn-primary" id="logout-btn">Logout</button>
            </form>
        </div>
    </div>
    <!--
    todo serÃ¡ necessario adicionar aqui um espaÃ§o para ver as notificaÃ§Ãµes(websockets)???

    -->
</div>