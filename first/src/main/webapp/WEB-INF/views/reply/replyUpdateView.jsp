<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="currentPage" value="${ requestScope.currentPage }" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>first</title>
</head>
<body>
<%-- <%@ include file="../common/menubar.jsp" %> --%>
<c:import url="/WEB-INF/views/common/menubar.jsp" />
<hr>

<h2 align="center">${ reply.replyNum } 번 게시댓글 | 대댓글 수정 페이지</h2>
<br>
<form action="rreplyupdate.do" method="post">
	<input type="hidden" name="replyNum" value="${ reply.replyNum }">
	<input type="hidden" name="boardRef" value="${ reply.boardRef }">
	<input type="hidden" name="replyReplyRef" value="${ reply.replyReplyRef }">
	<input type="hidden" name="replyLev" value="${ reply.replyLev }">
	<input type="hidden" name="replySeq" value="${ reply.replySeq }">
	<input type="hidden" name="replyReadCount" value="${ reply.replyReadCount }">
	<input type="hidden" name="page" value="${ page }">
	
<table align="center" width="500" border="1" cellspacing="0" cellpadding="5">
	<tr><th>제목</th>
		<td><input type="text" name="replyTitle" size="50" value="${ reply.replyTitle }"></td></tr>
	<tr><th>작성자</th>
		<td><input type="text" name="replyWriter" readonly value="${ reply.replyWriter }"></td></tr>
	<tr><th>내용</th>
		<td><textarea rows="5" cols="50" name="replyContent">${ reply.replyContent }</textarea></td></tr>
	<tr><th colspan="2">
		<input type="submit" value="수정하기"> &nbsp;
		<input type="reset" value="수정취소"> &nbsp;
		<button onclick="javascript:location.href='${ pageContext.servletContext.contextPath }/blist.do?page=${ currentPage }'; return false;">목록</button> &nbsp;  
		<button onclick="javascript:history.go(-1); return false;">이전 페이지로 이동</button>
	</th></tr>
</table>
</form>
<br>

<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>