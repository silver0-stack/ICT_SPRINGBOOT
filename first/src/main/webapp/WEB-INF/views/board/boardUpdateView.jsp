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

<h2 align="center">${ board.boardNum } 번 게시글 수정 페이지</h2>
<br>
<%-- 원글 수정 폼 : 첨부파일 수정 기능 포함 --%>
<c:if test="${ board.boardLev eq 1 }">
<form action="borginupdate.do" method="post" enctype="multipart/form-data">
	<input type="hidden" name="boardNum" value="${ board.boardNum }">
	<input type="hidden" name="boardOriginalFilename" value="${ board.boardOriginalFilename }">
	<input type="hidden" name="boardRenameFilename" value="${ board.boardRenameFilename }">
	<input type="hidden" name="page" value="${ currentPage }">
	
<table align="center" width="500" border="1" cellspacing="0" cellpadding="5">
	<tr><th>제 목</th>
		<td><input type="text" name="boardTitle" size="50" value="${ board.boardTitle }"></td></tr>
	<tr><th>작성자</th>
		<td><input type="text" name="boardWriter" readonly value="${ board.boardWriter }"></td></tr>	
	
	<tr><th>첨부파일</th>
		<td>			
			<c:if test="${ !empty board.boardOriginalFilename }">
				${ board.boardOriginalFilename } &nbsp;
				<input type="checkbox" name="deleteFlag" value="yes"> 파일삭제 <br>
				변경 : <input type="file" name="upfile">
			</c:if>
			<c:if test="${ empty board.boardOriginalFilename }">
				첨부 파일 없음 <br>
				추가 : <input type="file" name="upfile">
			</c:if>
		</td>
	</tr>
	<tr><th>내 용</th>
		<td><textarea rows="5" cols="50" name="boardContent">${ board.boardContent }</textarea></td></tr>
	<tr><th colspan="2">
		<input type="submit" value="수정하기"> &nbsp;
		<input type="reset" value="수정취소"> &nbsp;
		<button onclick="javascript:location.href='${ pageContext.servletContext.contextPath }/blist.do?page=${ currentPage }'; return false;">목록</button> &nbsp;  
		<button onclick="javascript:history.go(-1); return false;">이전 페이지로 이동</button>
	</th></tr>
</table>
</form>
</c:if>

<%-- 댓글, 대댓글 수정 폼 : 첨부파일 기능 없음 --%>
<c:if test="${ board.boardLev gt 1 }">
<form action="breplyupdate.do" method="post">
	<input type="hidden" name="boardNum" value="${ board.boardNum }">	
	<input type="hidden" name="page" value="${ currentPage }">
	
<table align="center" width="500" border="1" cellspacing="0" cellpadding="5">
	<tr><th>제 목</th>
		<td><input type="text" name="boardTitle" size="50" value="${ board.boardTitle }"></td></tr>
	<tr><th>작성자</th>
		<td><input type="text" name="boardWriter" readonly value="${ board.boardWriter }"></td></tr>		
	<tr><th>내 용</th>
		<td><textarea rows="5" cols="50" name="boardContent">${ board.boardContent }</textarea></td></tr>
	<tr><th colspan="2">
		<input type="submit" value="수정하기"> &nbsp;
		<input type="reset" value="수정취소"> &nbsp;
		<button onclick="javascript:location.href='${ pageContext.servletContext.contextPath }/blist.do?page=${ currentPage }'; return false;">목록</button> &nbsp;  
		<button onclick="javascript:history.go(-1); return false;">이전 페이지로 이동</button>
	</th></tr>
</table>
</form>
</c:if>
<br>

<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>