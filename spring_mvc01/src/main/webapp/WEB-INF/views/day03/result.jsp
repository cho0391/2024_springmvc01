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
	<h1>결과페이지</h1>
	<h3>
		<p> 올린사람 : ${name}</p>
		<p> 파일이름 : ${f_name}</p>
		<p> 파일타입 : ${file_type}</p>
		<p> 파일크기 : ${size}KB</p>
		<p> 파일수정날짜 : ${lastday}</p>
		<p> 
			<!-- 
			
				다운로드 방법1)
				저장위치와 파일이름이 서버로 넘어가야한다.
				하지만 위치가 고정되어 있으면 파일이름만 넘어간다..
				
			 -->
			<a href="/down?f_name=${f_name}">
				<img alt="" src='<c:url value="/resources/upload/${f_name}" />' style="width: 100px;"> 
			</a>
		</p>
		<p> 
			<!-- 
			
				다운로드 방법2)
				저장위치와 파일이름이 서버로 넘어가야한다.
				하지만 위치가 고정되어 있으면 파일이름만 넘어간다..
				
			 -->
			<a href="/down2?f_name=${f_name}">
				<img alt="" src='<c:url value="/resources/upload/${f_name}" />' style="width: 100px;"> 
			</a>
		</p>
	</h3>
</body>
</html>