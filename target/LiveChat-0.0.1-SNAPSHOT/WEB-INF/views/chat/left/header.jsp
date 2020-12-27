<%@ page pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
</head>
<body>
	<div class="card-header ">
		<div class="info-user-group d-flex ">
			<div class="img_cont ">
				<img src="${currentUser.avatar == null ?'assets/images/Login.png': currentUser.avatar}" class="rounded-circle user_img ">
			</div>
			<div class="user_info">
				<span>Chào!</span> <span class="name_user">${currentUser.firstName}</span>
			</div>
			<div class="action_menu_btn action_menu_btn_chat">
				<i class="fas fa-ellipsis-v "></i>
			</div>
			<div class="action_menu action_menu_chat">
				<ul>
					<li data-id="view_profile"><i class=" fas fa-user-circle "></i>
						View profile</li>
					<li data-id="${item.id}"><i class="fas fa-users "></i> Add to
						close friends</li>
					<li data-id="${item.id}"><i class="fas fa-plus "></i> Add to
						group</li>
					<li data-id="logout"><i class="fas fa-sign-out-alt"></i> Log
						out</li>
				</ul>
			</div>
		</div>
		<div class="input-group ">
			<div class="input-group-prepend ">
				<span class="input-group-text search_btn "><i
					class="fas fa-search "></i></span>
			</div>
			<input type="text" placeholder="Search... " name=" "
				class="form-control search" id="input-search ">
		</div>
	</div>
</body>
</html>