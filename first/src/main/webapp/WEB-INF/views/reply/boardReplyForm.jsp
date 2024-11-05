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
<h1 align="center">${ requestScope.bnum } 번 게시글 댓글 등록 페이지</h1>
<br>

<!-- 댓글과 대댓글은 파일첨부 안 함 -->
<form action="breply.do" method="post" >
	<input type="hidden" name="boardRef" value="${ requestScope.bnum }">
	<input type="hidden" name="page" value="${ requestScope.page }">
	
<table id="outer" align="center" width="700" cellspacing="5" cellpadding="5">	
	<tr><th width="120">댓글 제목</th>
		<td>
			<input type="text" name="replyTitle" size="50">
		</td></tr>
	<tr><th width="120">댓글 작성자</th>
		<td>
			<input type="text" name="replyWriter" readonly value="${ sessionScope.loginUser.userId }">
		</td></tr>		
	<tr><th>댓글 내용</th>
		<td><textarea rows="5" cols="50" name="replyContent"></textarea>
		</td></tr>
	
	<tr><th colspan="2">
		<input type="submit" value="댓글 등록하기"> &nbsp;
		<input type="reset" value="댓글 작성취소"> &nbsp;
		<input type="button" value="목록" onclick="javascript:history.go(-1); return false;">
	</th></tr>
</table>
</form>
<br>

<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>












