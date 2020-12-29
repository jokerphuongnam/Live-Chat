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
	<div class="scrollBox-content contacts">
		<c:forEach var="item" items="${rooms}">
			<a href="chat/${item.idRoom}">
				<li
				class="user user-room ${item.idRoom == currentRoom.idRoom?'active' :''}"
				data-id="${item.idRoom}">
					<div class="d-flex bd-highlight ">
						<div class="img_cont ">
							<c:choose>
								<c:when
									test="${item.getClass().name == 'chat.roomchat.GroupChat'}">
									<img
										src="${item.imageGroup == null ?'assets/images/Login.png': item.imageGroup}"
										class="rounded-circle user_img ">
								</c:when>
								<c:otherwise>
									<c:set var="partner" value="${item.members[1]}" />
									<img
										src="${partner.member.avatar == null ?'assets/images/Login.png': partner.member.avatar}"
										class="rounded-circle user_img ">
								</c:otherwise>
							</c:choose>
						</div>
						<div class="room_info_cell ">
							<span> <c:choose>
									<c:when
										test="${item.getClass().name == 'chat.roomchat.GroupChat'}">
								${item.nameGroup == null ? 'Insert Group Name' :item.nameGroup}
							</c:when>
									<c:otherwise>
										<c:set var="partner" value="${item.members[1]}" />
										<c:choose>
											<c:when test="${partner.nickName != null}">
											${partner.nickName}
										</c:when>
											<c:otherwise>
												<c:set var="partnerUser" value="${partner.member}" />
											${partnerUser.firstName} ${partnerUser.lastName == null ? '': partnerUser.lastName }
										</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</span>
							<c:choose>
								<c:when test="${fn:length(item.messages) > 0}">
									<div>
										<c:set var="message"
											value="${item.messages[fn:length(item.messages) - 1]}" />
										<p>
											<c:set var="maxLenght" value="15" />
											<c:if test="${message.sender.idUser == currentUser.idUser}">
									Bạn:
									<c:set var="maxLenght" value="10" />
											</c:if>
											<c:set var="content" value="${message.content.content}" />
											<c:choose>
												<c:when test="${fn:length(content) < maxLenght}">
											${content}
										</c:when>
												<c:otherwise>
											${fn:substring(content, 0, maxLenght)}...
										</c:otherwise>
											</c:choose>
										</p>
										<p class="dot_room_chat ">&sdot;</p>
										<p>
											<c:set var="sendTime" value="${message.sendTime}" />
											<fmt:formatDate value="${sendTime}" type="date"
												var="sendTimeDay" pattern="dd/MMM/yyyy" />
											<c:choose>
												<c:when test="${sendTimeDay lt now}">
											${sendTimeDay lt now}
												<fmt:formatDate value="${sendTime}" type="date"
														pattern="hh:mm" />
												</c:when>
												<c:otherwise>
													<fmt:formatDate value="${sendTime}" type="date"
														pattern="hh:mm dd/MMM/yyyy" />
												</c:otherwise>
											</c:choose>
										</p>
									</div>
								</c:when>
								<c:otherwise>
									<div>
										<p>Room chat empty messages</p>
									</div>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
			</li>
			</a>
		</c:forEach>
	</div>
</body>
</html>
