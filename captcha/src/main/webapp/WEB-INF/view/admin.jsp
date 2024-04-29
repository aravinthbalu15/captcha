<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Captcha Management</title>
</head>
<body>
<h1>Captcha Management</h1>

<form action="addCaptcha" method="post">
    <input type="submit" value="Add New Captcha">
</form>

<h2>Captcha List</h2>

<form action="listCaptchas" method="get">
    Page Size: <input type="number" name="pageSize" value="${pageSize}" min="1">
    <input type="submit" value="Update">
</form>

<table>
    <tr>
        <th>ID</th>
        <th>Code</th>
        <th>Image</th>
        <th>Created At</th>
        <th>Action</th>
    </tr>
    <c:forEach items="${captchas}" var="captcha">
        <tr>
            <td>${captcha.id}</td>
            <td>${captcha.code}</td>
            <td><img src="data:image/png;base64,${captcha.imageBase64}" alt="Captcha Image"></td>
            <td>${captcha.createdAt}</td>
            <td>
                <form action="deleteCaptcha" method="post" style="display: inline">
                    <input type="hidden" name="captchaId" value="${captcha.id}">
                    <input type="submit" value="Delete">
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<div>
    <c:if test="${currentPage > 1}">
        <a href="listCaptchas?page=${currentPage - 1}&pageSize=${pageSize}">Previous</a>
    </c:if>
    Page ${currentPage} of ${totalPages}
    <c:if test="${currentPage < totalPages}">
        <a href="listCaptchas?page=${currentPage + 1}&pageSize=${pageSize}">Next</a>
    </c:if>
</div>
</body>
</html>
