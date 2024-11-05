<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>   
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  

<c:set var="nowpage" value="1" />
<c:if test="${ !empty requestScope.currentPage }">
	<c:set var="nowpage" value="${ requestScope.currentPage }" />
</c:if>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>first</title>
<style type="text/css">
fieldset#ss {
	width: 650px;
	position: relative;
	left: 450px;
}
form fieldset {
	width: 600px;
}
form.sform {
	background: lightgray;
	width: 650px;
	position: relative;
	left: 450px;
	display: none;  /* 안 보이게 함 */
}
</style>
<script type="text/javascript" src="/first/resources/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
$(function(){
	//input 태그의 name이 item인 radio 값이 바뀌면(change) 작동되는 이벤트 핸들러 작성
	//jQuery('태그선택자').실행할메소드명(.....); => jQuery 는 줄여서  $로 표시함
	$('input[name=item]').on('change', function(){
		//3개의 item 중에 체크표시가 된 radio 를 선택 => 반복 처리 : each() 메소드 사용
		$('input[name=item]').each(function(index){
			//선택된 radio 순번대로 하나씩 checked 인지 확인함 : is() 메소드 사용
			if($(this).is(':checked')){
				//체크 표시된 아이템에 대한 폼이 보여지게 처리함
				$('form.sform').eq(index).css('display', 'block');
			}else{
				//체크 표시 안된 아이템에 대한 폼이 안 보여지게 처리함
				$('form.sform').eq(index).css('display', 'none');
			}
		});  //each
		
	});  //onchange
}); //document.ready
</script>
</head>
<body>
<c:import url="/WEB-INF/views/common/menubar.jsp" />
<hr>
<h1 align="center">게시글 목록</h1>

<center>
	<%-- 게시글 쓰기는 로그인한 회원만 가능하도록 처리 --%>
	<c:if test="${ !empty sessionScope.loginUser }">
		<button onclick="javascript:location.href='${ pageContext.servletContext.contextPath}/bwform.do';">글쓰기</button>		
	</c:if>
</center>


<%-- 항목별 검색 기능 추라 --%>
<fieldset id="ss">
	<legend>검색할 항목을 선택하세요.</legend>
	<input type="radio" name="item" id="title"> 제목 &nbsp;
	<input type="radio" name="item" id="writer"> 작성자 &nbsp;
	<input type="radio" name="item" id="date"> 등록날짜 &nbsp;
</fieldset>

<%-- 검색 항목별 값 입력 전송용 폼 만들기 --%>
<%-- 제목 검색 폼 --%>
<form action="bsearchTitle.do" id="titleform" class="sform" method="get">
	<input type="hidden" name="action" value="title">
	<fieldset>
	<legend>검색할 제목을 입력하세요.</legend>
		<input type="search" name="keyword" size="50"> &nbsp;
		<input type="submit" value="검색">
	</fieldset>
</form>

<%-- 작성자 검색 폼 --%>
<form action="bsearchWriter.do" id="writerform" class="sform" method="get">
	<input type="hidden" name="action" value="writer">
	<fieldset>
	<legend>검색할 작성자를 입력하세요.</legend>
		<input type="search" name="keyword" size="50"> &nbsp;
		<input type="submit" value="검색">
	</fieldset>
</form>

<%-- 등록날짜 검색 폼 --%>
<form action="bsearchDate.do" id="dateform" class="sform" method="get">
	<input type="hidden" name="action" value="date">
	<fieldset>
	<legend>검색할 등록날짜를 선택하세요.</legend>
		<input type="date" name="begin"> ~ <input type="date" name="end"> &nbsp;
		<input type="submit" value="검색">
	</fieldset>
</form>
<br>
<center>
<button onclick="javascript:location.href='${ pageContext.servletContext.contextPath }/blist.do?page=1';">목록</button> &nbsp; &nbsp;
</center>
<br>
<%-- 조회된 공지사항 목록 출력 --%>
<table align="center" width="650" border="1" cellspacing="0" cellpadding="0">
	<tr>
		<th>번호</th>
		<th>제목</th>
		<th>작성자</th>
		<th>첨부파일</th>
		<th>날짜</th>
		<th>조회수</th>
	</tr>
	<c:forEach items="${ requestScope.list }" var="b">
		<tr>
			<td align="center">${ b.boardNum }</td>
			<td align="left">		
					
				<c:url var="bd" value="bdetail.do">
					<c:param name="bnum" value="${ b.boardNum }" />
					<c:param name="page" value="${ currentPage }" />
				</c:url>
				<a href="${ bd }">${ b.boardTitle }</a></td>
			<td align="center">${ b.boardWriter }</td>
			<td align="center">
				<c:if test="${ !empty b.boardOriginalFilename }">◎</c:if>
				<c:if test="${ empty n.boardOriginalFilename }">&nbsp;</c:if>
			</td>
			<td align="center">
				<fmt:formatDate value="${ b.boardDate }" pattern="yyyy-MM-dd" />
			</td>
			<td align="center">${ b.boardReadCount }</td>
		</tr>
	</c:forEach>
</table>
<br>

<%-- 페이징 출력 뷰 포함 처리 --%>
<c:import url="/WEB-INF/views/common/pagingView.jsp" />

<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>