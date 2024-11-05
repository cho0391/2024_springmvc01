<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ajax DB 연습</title>
<style type="text/css">
span{
	width: 80px;
	color: #5F9EA0;
	margin-left: 2px;
}

input {
	border: 1px solid #5F9EA0;
	border-radius: 2px;
}

table {
	width: 80%;
	margin: 0 auto;	
	border: 1px solid #5F9EA0;
}

table, th, td {
	border: 1px solid #008B8B;
	border-radius: 3px;
}

th {
	background-color: #3CB371;
	color: #EEE8AA;
}

h1{
	color: #9ACD32;
}
h2 {
	text-align: center;
	color: #2E8B57;
}
</style>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
</head>
<body>
<h1>Ajax DB 연습</h1>
	<h2>회원 정보 입력하기</h2>
	<form action="/db_join" method="post" id="myForm">
		<table>
			<thead>
				<tr>
					<th>아이디</th><th>패스워드</th><th>이름</th><th>나이</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<input type="text" size="12" name="m_id" id="m_id">
						<span>중복여부</span>
					</td>
					<td>
						<input type="text" size="12" name="m_pw" id="m_pw">
					</td>
					<td>
						<input type="text" size="12" name="m_name" id="m_name">
					</td>
					<td>
						<input type="text" size="12" name="m_age" id="m_age">
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4" align="center"><input type="button" value="가입하기" id="btn_join" disabled="disabled"></td>
				</tr>
			</tfoot>
		</table>
	</form>
	
	<br>
	<br>
	<br>
	<br>
	
	<h2>회원 정보 보기</h2>
	<div>
		<table id="lsit">
			<thead>
				<th>번호</th><th>아이디</th><th>패스워드</th><th>이름</th><th>나이</th><th>가입</th><th>삭제</th>
			</thead>
			<tbody id="tbody">
			</tbody>
		</table>
	</div>
	
	<script type="text/javascript">
		// ajax를 이용하여 DB정보 가져오기 (접속, 삭제, 삽입)
		function getList() {
			$.ajax({
				url : "/ajax_db_list",
				method : "post",
				dataType : "xml",
				success : function (data) {
					//console.log(data);
					let tbody = "";
					$(data).find("member")
				},
				error : function() {
					alert("읽기 실패");
				}
			});
		}
		getList();
	</script>
	
	
	
</body>
</html>