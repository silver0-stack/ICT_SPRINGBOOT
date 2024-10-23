<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>first</title>
</head>
<body>
<c:import url="/WEB-INF/views/common/menubar.jsp" />
<hr>
<h1 align="center">새 게시 원글 등록 페이지</h1>
<br>

<!-- form 에서 파일이 첨부되어서 전송이 될 경우에는 반드시 enctype="multipart/form-data" 속성을 추가해야 함 -->
<form action="binsert.do" method="post" enctype="multipart/form-data">
<table id="outer" align="center" width="700" cellspacing="5" cellpadding="5">	
	<tr><th width="120">제 목</th>
		<td>
			<input type="text" name="boardTitle" size="50">			
		</td></tr>
	<tr><th width="120">작성자</th>
		<td>
			<input type="text" name="boardWriter" readonly value="${ sessionScope.loginUser.userId }">			
		</td></tr>	
	<tr><th>첨부파일</th>
		<td>
			<input type="file" name="ofile"> 
			<%-- name 속성의 이름은 필드명과 별개로 지정함
				파일업로드 실패시 파일명만 문자열로 command 객체에 저장되지 않게 하기 위함
			 --%>
		</td>
	</tr>
	<tr><th>내 용</th>
		<td><textarea rows="5" cols="50" name="boardContent"></textarea>
		</td></tr>
	
	<tr><th colspan="2">
		<input type="submit" value="등록하기"> &nbsp; 
		<input type="reset" value="작성취소"> &nbsp; 
		<input type="button" value="목록" onclick="javascript:history.go(-1); return false;">
	</th></tr>
</table>
</form>
<br>

<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>












