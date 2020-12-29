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
			<div class="d-flex user-info room-info">
				<c:choose>
					<c:when
						test="${currentRoom.getClass().name == 'chat.roomchat.GroupChat'}">
						<div class="img_cont ">
							<img
								src="${currentRoom.imageGroup == null ?'assets/images/Login.png': currentRoom.imageGroup}"
								class="rounded-circle user_img ">
						</div>
						<div class="room_info">
							<span class="name_user">${currentRoom.nameGroup == null ? 'Insert Group Name': currentRoom.nameGroup}</span>
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
							<span class="name_user">${partnerUser.firstName}
								${partnerUser.lastName == null ? '': partnerUser.lastName}</span>
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
			</div>
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
				<ul class="right_action_menu">
					<li data-id="edit_nick_name"><i class="fas fa-pen-nib"></i>Edit
						nickname</li>
					<c:choose>
						<c:when
							test="${currentRoom.getClass().name == 'chat.roomchat.InboxChat'}">
							<li data-id="add_group"><i class="fas fa-plus"></i> Add to
								group</li>
						</c:when>
						<c:when
							test="${currentRoom.getClass().name == 'chat.roomchat.GroupChat'}">
							<li data-id="edit_group_name"><i class="fas fa-pen-nib"></i>Change
								group name</li>
							<li data-id="members"><i class="fas fa-users"></i>Members</li>
							<li data-id="edit_image_group"><i
								class=" fas fa-user-circle "></i>Change image group</li>
							<li data-id="leave_group"><i class="fas fa-sign-out-alt"></i>Leave
								group</li>
						</c:when>
					</c:choose>
				</ul>
			</div>
			<div class="right_helper_action_menu inputmenu">
				<div class="row input-helper">
					<input type="text" placeholder="Input..." class="input_helper"><i
						class="fas fa-check-circle submit_action_menu"></i><i
						class="fas fa-times-circle cancel_action_menu"></i>
				</div>
			</div>
			<div class="right_helper_action_menu roomsjoined">
				<span></span>
				<ul class="rooms"></ul>
			</div>
			<div class="right_helper_action_menu upload_image">
				<div class="row input-helper">
					<div class="container">
						<div class="row">
							<h7>If don't choose image, image group will default</h7>
							<i class="fas fa-times-circle cancel_action_menu"></i>
						</div>
						<div class="row">
							<div class="col"></div>
							<div class="col">
								<form method="post" enctype="multipart/form-data"
									class="upload_image_group">
									<div class="col">
										<div class="row">
											<div class="col"></div>
											<div class="col">
												<input type="file" class="avata_input review_avatar"
													name="newImageRoom" onchange="onChange(event)"
													style="margin-left: -18px;">
											</div>
											<div class="col"></div>
										</div>
										<span> <img alt="Avatar" name="avatar-img"
											class="rounded-circle user_img makup-avatar review_avatar" />
										</span>
									</div>
									<div class="row">
										<div class="col"></div>
										<div class="col">
											<div class="row">
												<div class="col"></div>
												<div class="col">
													<button class="submit_image_room">
														<i class="fas fa-check-circle submit_action_menu"></i>
													</button>
												</div>
												<div class="col">
													<button class="submit_image_room" name="removeImageGroup">
														<i class="fas fa-minus-circle"></i>
													</button>
												</div>
												<div class="col"></div>
											</div>
										</div>
										<div class="col"></div>
									</div>
								</form>
							</div>
							<div class="col"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>