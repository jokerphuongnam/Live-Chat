<!--core jsp-->
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page pageEncoding="utf-8"%>
<!--bootstrap-->
<link href="<c:url value="/assets/bootstrap/4.1.1/bootstrap.min.css"/>"
	rel="stylesheet" id="bootstrap-css"></link>
<!--include library-->

<!DOCTYPE html>
<html>

<head>
<meta charset=" UTF-8 ">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<base href="${pageContext.servletContext.contextPath}/">
<title>Chat</title>
<link rel="shortcut icon" type="image/x-icon "
	href="assets/icons/chat.svg ">
<link rel="stylesheet"
	href="<c:url value="https://use.fontawesome.com/releases/v5.5.0/css/all.css"/>"
	integrity="sha384-B4dIYHKNBt8Bc12p+WXckhzcICo0wtJAoU8YZTY5qE0Id1GSseTk6S+L3BlXeVIU "
	crossorigin="anonymous" />
<!--js-->
<script type="text/javascript"
	src="<c:url value="assets/jquery/3.2.1/jquery.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="assets/jquery/1.4.1/jquery.cookie.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="assets/sockjs/1.1.4/sockjs.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="assets/stomp/2.3.3/stomp.min.js"/>"></script>
<!--support css-->
<link rel="stylesheet"
	href="<c:url value="assets/support/css/main.css"/>">
<link rel="stylesheet"
	href="<c:url value="assets/support/css/chat/chat.css"/>">
<body>
	<script type="text/javascript"
		src="<c:url value="assets/support/js/chat/SocketHelper.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="assets/support/js/chat/AjaxHelper.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="assets/support/js/chat/InitVar.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="assets/support/js/chat/chat.js"/>"></script>
	<div class="container-fluid h-100 ">
		<div class="row justify-content-center h-100 ">
			<div class="col-sm-2 col-md-4 col-xl-3 chat ">
				<div class="card mb-sm-3 mb-md-0 contacts_card ">
					<jsp:include page="left/header.jsp"></jsp:include>
					<div class="card-body contacts_body scrollBox ">
						<jsp:include page="left/rooms.jsp"></jsp:include>
						<jsp:include page="left/search.jsp"></jsp:include>
					</div>
					<div class="card-footer "></div>
				</div>
			</div>
			<div class="col-sm-10 col-md-8 col-xl-9 chat ">
				<div class="card ">
					<jsp:include page="right/header.jsp"></jsp:include>
					<jsp:include page="right/messages.jsp"></jsp:include>
					<jsp:include page="right/input.jsp"></jsp:include>
				</div>
			</div>
		</div>
	</div>
</body>
</html>