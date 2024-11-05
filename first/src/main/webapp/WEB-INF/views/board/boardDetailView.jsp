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
<!-- 원글에 댓글달기 -->
<c:url var="replyf" value="breplyform.do">
	<c:param name="bnum" value="${ board.boardNum }" />
	<c:param name="page" value="${ currentPage }" />
</c:url>

<c:url var="bdel" value="bdelete.do">
	<c:param name="boardNum" value="${ board.boardNum }" />
	<c:param name="boardRenameFilename" value="${ board.boardRenameFilename }"/>
</c:url>

<c:url var="bup" value="bupview.do">
	<c:param name="bnum" value="${ board.boardNum }" />
	<c:param name="page" value="${ currentPage }" />
</c:url>

<script type="text/javascript">
//원글의 댓글달기 버튼 클릭시 실행함수
function requestReply(){
	location.href = "${ replyf }";
}
//댓글의 대댓글달기 버튼 클릭시 실행함수
function requestReplyReply(replyReplyRef){
	location.href = "/first/rreplyform.do?boardRef=" + ${board.boardNum} + "&replyReplyRef=" + replyReplyRef + "&page=" + ${ currentPage };
}
//원글 삭제하기 버튼 클릭시 실행함수
function requestDelete(){
	location.href = "${ bdel }";
}
//댓글, 대댓글 삭제하기 버튼 클릭시 실행함수
function requestReplyDelete(rnum){
	alert('삭제 댓글 번호 : ' + rnum);
	location.href = "/first/breplydelete.do?replyNum=" + rnum + "&boardNum=${board.boardNum}";
}
//게시원글 수정페이지로 이동 버튼 클릭시 실행함수
function requestUpdatePage(){
	location.href = "${ bup }";
}
//게시댓글, 대댓글 수정페이지로 이동 버튼 클릭시 실행함수
function requestReplyUpdatePage(rnum){
	location.href = "/first/replyUpdatePage.do?replyNum=" + rnum + "&page=${ currentPage }";
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
	<tr><th bgcolor="#ff8c00">제 목</th><td>${ board.boardTitle }</td></tr>
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
					<button onclick="requestReply(); return false;">댓글달기</button>
			</c:if>
		</c:if> &nbsp;
		<button onclick="javascript:location.href='${ pageContext.servletContext.contextPath }/blist.do?page=${ currentPage }';">목록</button> &nbsp; 
		<button onclick="javascript:history.go(-1);">이전 페이지로 이동</button>
	</th></tr>
</table>
<br>
<hr style="clear:both;">
<!-- 해당 게시글의 댓글과 대댓글 출력 -->
<div style="width:1000px;">
	<c:forEach items="${ replyList }" var="reply">
		<!-- 댓글 출력 -->
	<c:if test="${ reply.replyLev eq 1 }">
	<table align="right" style="margin-right:200px;" width="500" border="1" cellspacing="0" cellpadding="5">
		<tr><th bgcolor="#7fffd4">댓글 제목</th>
			<td colspan="2">${ reply.replyTitle }</td>
			<th>${ reply.replyReadCount }</th>
		</tr>
		<tr><th>댓글 작성자</th><td>${ reply.replyWriter }</td>
			<th>댓글 등록날짜</th>
			<td><fmt:formatDate value="${ reply.replyDate }" pattern="yyyy-MM-dd" /></td>
		</tr>
		<tr></tr>
		<tr><th>댓글 내용</th><td colspan="3">${ reply.replyContent }</td></tr>
		<tr><th colspan="4">
			<%-- 로그인한 경우 표시되게 함 --%>
			<c:if test="${ !empty sessionScope.loginUser }">
				<%-- 본인이 작성한 글 또는 관리자이면 수정, 삭제 버튼 제공함 --%>
				<c:if test="${ loginUser.userId eq reply.replyWriter or loginUser.adminYN eq 'Y' }">
					<button onclick="requestReplyUpdatePage(${reply.replyNum}); return false;">댓글 수정</button> &nbsp;
					<button onclick="requestReplyDelete(${reply.replyNum}); return false;">댓글 삭제</button>
				</c:if>

				<%-- 본인이 작성한 글이 아니면 또는 관리자이면 대댓글달기 버튼 제공함 --%>
				<c:if test="${ loginUser.userId ne reply.replyWriter or loginUser.adminYN eq 'Y' }">
					<c:if test="${ reply.replyLev eq 1 }">
						<button onclick="requestReplyReply(${reply.replyReplyRef}); return false;">대댓글달기</button>
					</c:if>
				</c:if>
			</c:if> &nbsp;<!-- 로그인한 경우 -->
		</th></tr>
	</table>
		<br>
		</c:if>  <!-- 댓글이면 -->
		<!-- 대댓글 출력 -->
		<c:if test="${ reply.replyLev eq 2 }">
			<table align="right" style="margin-right:100px;" width="500" border="1" cellspacing="0" cellpadding="5">
				<tr><th bgcolor="#ffe4c4">대댓글 제목</th>
					<td colspan="2">${ reply.replyTitle }</td>
					<th>${ reply.replyReadCount }</th>
				</tr>
				<tr><th>대댓글 작성자</th><td>${ reply.replyWriter }</td>
					<th>대댓글 등록날짜</th>
					<td><fmt:formatDate value="${ reply.replyDate }" pattern="yyyy-MM-dd" /></td>
				</tr>
				<tr></tr>
				<tr><th>대댓글 내용</th><td colspan="3">${ reply.replyContent }</td></tr>
				<tr><th colspan="4">
						<%-- 로그인한 경우 표시되게 함 --%>
					<c:if test="${ !empty sessionScope.loginUser }">
						<%-- 본인이 작성한 글 또는 관리자이면 수정, 삭제 버튼 제공함 --%>
						<c:if test="${ loginUser.userId eq reply.replyWriter or loginUser.adminYN eq 'Y' }">
							<button onclick="requestReplyUpdatePage(${reply.replyNum}); return false;">대댓글 수정</button> &nbsp;
							<button onclick="requestReplyDelete(${reply.replyNum}); return false;">대댓글 삭제</button>
						</c:if>
					</c:if> &nbsp;
				</th></tr>
			</table>
			<br>
		</c:if> <!-- 대댓글이면 -->
	</c:forEach>
</div>
<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>










