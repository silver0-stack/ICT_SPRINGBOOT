<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- <%@ page import="org.myweb.first.notice.model.dto.Notice" %>
<%
	Notice notice = (Notice)request.getAttribute("notice");
%> --%>  <%-- el 로 대체함 --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<h2 align="center">${ notice.noticeNo } 번 공지글 상세보기</h2>
<br>
<table align="center" width="500" border="1" cellspacing="0" cellpadding="5">
	<tr><th>제 목</th><td>${ notice.noticeTitle }</td></tr>
	<tr><th>작성자</th><td>${ notice.noticeWriter }</td></tr>
	<tr><th>등록날짜</th>
		<td><fmt:formatDate value="${ notice.noticeDate }" pattern="yyyy-MM-dd" /></td></tr>
	<tr><th>첨부파일</th>
		<td>
			<c:url var="nfdown" value="nfdown.do">
				<c:param name="ofile" value="${ notice.originalFilePath }" />
				<c:param name="rfile" value="${ notice.renameFilePath }" />
			</c:url>
			<c:if test="${ !empty notice.originalFilePath }">
				<a href="${ nfdown }">${ notice.originalFilePath }</a>
				<%-- <a href="nfdown.do?ofile=${ notice.originalFilePath }&rfile=${ notice.renameFilePath }">${ notice.originalFilePath }</a> --%>
			</c:if>
			<c:if test="${ empty notice.originalFilePath }">
				첨부 파일 없음
			</c:if>
		</td>
	</tr>
	<tr><th>내 용</th><td>${ notice.noticeContent }</td></tr>
	<tr><th colspan="2">
		<button onclick="javascript:location.href='${ pageContext.servletContext.contextPath }/nlist.do';">목록</button> &nbsp; 
		<button onclick="javascript:history.go(-1);">이전 페이지로 이동</button>
	</th></tr>
</table>


<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>










