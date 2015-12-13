<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: joaosubtil
  Date: 11/12/15
  Time: 17:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- notificaçoes accoes admin-->
<c:if test="${session != null && session.success != null}">
    <div class="alert alert-success">
        <strong>Sucessfull:</strong><c:out value="${session.success}"/>
    </div>
    <c:remove var="success"/>
</c:if>
<c:if test="${session != null && session.error != null}">
    <div class="alert alert-danger">
        <strong>Error:</strong><c:out value="${session.error}"/>
    </div>
    <c:remove var="error"/>
</c:if>
<!--fim dos alerts-->