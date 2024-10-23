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

<%-- 아래의 자바스크립트 함수에서 사용하는 url 요청 처리를 별도의 변수화 처리함 --%>
<c:url var="replyf" value="breplyform.do">
	<c:param name="bnum" value="${ board.boardNum }" />
	<c:param name="page" value="${ currentPage }" />
</c:url>

<c:url var="bdel" value="bdelete.do">
	<c:param name="boardNum" value="${ board.boardNum }" />
	<c:param name="boardLev" value="${ board.boardLev }" />
	<c:param name="boardRenameFilename" value="${ board.boardRenameFilename }"/>
</c:url>

<c:url var="bup" value="bupview.do">
	<c:param name="bnum" value="${ board.boardNum }" />
	<c:param name="page" value="${ currentPage }" />
</c:url>

<script type="text/javascript">
//댓글달기 버튼 클릭시 실행함수
function requestReply(){
	location.href = "${ replyf }";
}
//삭제하기 버튼 클릭시 실행함수
function requestDelete(){
	location.href = "${ bdel }";
}
//수정페이지로 이동 버튼 클릭시 실행함수
function requestUpdatePage(){
	location.href = "${ bup }";
}
</script>
</head>
<body>
<%-- <%@ include file="../common/menubar.jsp" %> --%>
<c:import url="/WEB-INF/views/common/menubar.jsp" />
<hr>

<h2 align="center">${ board.boardNum } 번 게시글 상세보기</h2>
<br>
<table align="center" width="500" border="1" cellspacing="0" cellpadding="5">
	<tr><th>제 목</th><td>${ board.boardTitle }</td></tr>
	<tr><th>작성자</th><td>${ board.boardWriter }</td></tr>
	<tr><th>등록날짜</th>
		<td><fmt:formatDate value="${ board.boardDate }" pattern="yyyy-MM-dd" /></td></tr>
	<tr><th>첨부파일</th>
		<td>
			<c:url var="bfdown" value="bfdown.do">
				<c:param name="ofile" value="${ board.boardOriginalFilename }" />
				<c:param name="rfile" value="${ board.boardRenameFilename }" />
			</c:url>
			<c:if test="${ !empty board.boardOriginalFilename }">
				<a href="${ bfdown }">${ board.boardOriginalFilename }</a>				
			</c:if>
			<c:if test="${ board.boardOriginalFilename }">
				첨부 파일 없음
			</c:if>
		</td>
	</tr>
	<tr><th>내 용</th><td>${ board.boardContent }</td></tr>
	<tr><th colspan="2">
		<%-- 로그인한 경우 표시되게 함 --%>
		<c:if test="${ !empty sessionScope.loginUser }">
			<%-- 본인이 작성한 글 또는 관리자이면 수정, 삭제 버튼 제공함 --%>
			<c:if test="${ loginUser.userId eq board.boardWriter or loginUser.adminYN eq 'Y' }">
				<button onclick="requestUpdatePage(); return false;">수정페이지로 이동</button> &nbsp;
				<button onclick="requestDelete(); return false;">글삭제</button>
			</c:if>
					
			
			<%-- 본인이 작성한 글이 아니면 또는 관리자이면 댓글달기 버튼 제공함 --%>
			<c:if test="${ loginUser.userId ne board.boardWriter or loginUser.adminYN eq 'Y' }">
				<%-- 글레벨이 3보다 작은 경우에만 표시함 : 대댓글까지만 등록할 것임 --%>
				<c:if test="${ board.boardLev lt 3 }">
					<button onclick="requestReply(); return false;">댓글달기</button>
				</c:if>
			</c:if>
		</c:if> &nbsp;
		<button onclick="javascript:location.href='${ pageContext.servletContext.contextPath }/blist.do?page=${ currentPage }';">목록</button> &nbsp; 
		<button onclick="javascript:history.go(-1);">이전 페이지로 이동</button>
	</th></tr>
</table>


<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>










