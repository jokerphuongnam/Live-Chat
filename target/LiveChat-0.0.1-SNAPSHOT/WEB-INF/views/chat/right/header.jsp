<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
</head>
<body>
	<div class="card-header msg_head ">
		<div class="d-flex bd-highlight ">
			<c:choose>
				<c:when
					test="${currentRoom.getClass().name == 'chat.roomchat.GroupChat'}">
					<div class="img_cont ">
						<img
							src="${currentRoom.imageGroup == null ?'assets/images/Login.png': currentRoom.imageGroup}"
							class="rounded-circle user_img ">
					</div>
					<div class="room_info">
						<span class="name_user">${currentRoom.nameGroup}</span>
					</div>
				</c:when>
				<c:otherwise>
					<c:set var="partnerUser" value="${currentRoom.members[1].member}" />
					<div class="img_cont ">
						<img
							src="${partnerUser.avatar == null ?'assets/images/Login.png': partnerUser.avatar}"
							class="rounded-circle user_img "> <span
							class="${partnerUser.status == null ?'online_icon':'offline'}"></span>
					</div>
					<div class="room_info">
						<span class="name_user">${partnerUser.firstName} ${partnerUser.lastName == null ? '': partnerUser.lastName}</span>
						<p>
							<c:choose>
								<c:when test="${partnerUser.status == null}">
									Online
								</c:when>
								<c:otherwise>
									Offline by <fmt:formatDate value="${partnerUser.status}"
										type="date" pattern="mm:hh dd/MMM/yyyy" />
								</c:otherwise>
							</c:choose>
						</p>
					</div>
				</c:otherwise>
			</c:choose>
			<div class="header_action_btn">
				<div class="call">
					<span><i class="fas fa-video "></i></span> <span><i
						class="fas fa-phone "></i></span>
				</div>
				<div class="action_menu_btn action_menu_btn_user">
					<i class="fas fa-ellipsis-v "></i>
				</div>
			</div>
			<div class="action_menu action_menu_user">
				<ul>
					<li data-id="viewProfile"><i class=" fas fa-user-circle "></i>
						View profile</li>
					<li data-id="addFriend"><i class="fas fa-users "></i> Add to
						close friends</li>
					<li data-id="addGroup"><i class="fas fa-plus "></i> Add to
						group</li>
					<li data-id="block"><i class="fas fa-ban "></i> Block</li>
				</ul>
			</div>
		</div>
	</div>
</body>
</html>