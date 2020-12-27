<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page pageEncoding="utf-8" import="java.util.Date"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
</head>
<body>
	<div class="card-body messages_body scrollBox">
		<ui class="scrollBox-content msg_card_body messages"> <c:forEach
			var="item" items="${currentRoom.messages}">
			<!-- d-flex justify-content-end justify-content-start-->
			<li
				class="d-flex mb-4 ${item.sender.idUser == currentUser.idUser? 'justify-content-end': 'justify-content-start'}" data-id="${item.idMessage}">
				<c:if test="${item.sender.idUser != currentUser.idUser}">
					<div class="img_cont_mini ">
						<img
							src="${item.sender.avatar == null ?'assets/images/Login.png': item.sender.avatar}"
							class="rounded-circle user_image_mini ">
					</div>
				</c:if>
				<div
					class="${item.sender.idUser == currentUser.idUser? 'msg_container_send': 'msg_container'}">
					<c:if test="${item.sender.idUser != currentUser.idUser && currentRoom.getClass().name == 'chat.roomchat.GroupChat'}">
						<span
							class="msg_name">${item.sender.firstName}
							${item.sender.lastName }</span>
					</c:if>
					<span> ${item.content.content} </span><span
						class="${item.sender.idUser == currentUser.idUser ?'msg_time_send' :'msg_time'}">
						<fmt:formatDate value="${item.sendTime}" type="date"
							pattern="hh:mm dd/MM/yyyy" />
					</span>
				</div>
			</li>
		</c:forEach> </ui>
	</div>
</body>
</html>