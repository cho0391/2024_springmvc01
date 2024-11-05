<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>결과 : result4.jsp</h1>
	<h2>자동차 이름들</h2>
	<c:forEach items="${carName}" var="k" varStatus="c">
		<h3>${k}</h3>
	</c:forEach>
</body>
</html>