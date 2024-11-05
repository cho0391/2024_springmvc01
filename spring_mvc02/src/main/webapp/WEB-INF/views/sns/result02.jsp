<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js" crossorigin="anonymous"></script>
</head>
<body>
	<h2>Naver 로그인 성공</h2>
	<div id="result"></div>
	
	<!-- 계정과 함께 로그아웃 -->
	<a href="/naverLogout">로그아웃</a>

	<script type="text/javascript">
	// $(document).ready(function(){ });
		$(function() {
			$("#result").empty();
			$.ajax({
				url : "/naverUserInfo",
				method : "post",
				dataType : "json",
				success : function(data) {
					let name = data.response.name;
					let nickname = data.response.nickname;
					let email = data.response.email
					let profile_image = data.response.profile_image;

					let str = "<li>이름 : " + name + "</li>";
					str += "<li>닉네임 : " + nickname + "</li>";
					str += "<li>이메일 : " + email + "</li>";
					str += "<li>이미지 : <img src=" + profile_image + " style='width:150px'></li>";
					$("#result").append(str);
				},
				error : function() {
					alert("읽기실패");
				}
			});
		});
	</script>
</body>
</html>