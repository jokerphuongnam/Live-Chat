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
				<a href="viewavatar"> <img
					src="${currentUser.avatar == null ?'assets/images/Login.png': currentUser.avatar}"
					class="rounded-circle user_img avatar_image">
				</a>
			</div>
			<div class="user_info">
				<span>Chào!</span> <span class="name_user">${currentUser.firstName}</span>
			</div>
			<div class="action_menu_btn action_menu_btn_chat">
				<i class="fas fa-ellipsis-v "></i>
			</div>
			<div class="action_menu action_menu_chat">
				<ul>
					<li data-id="create_group"><i class="fas fa-plus "></i>Create
						group</li>
					<li data-id="edit_profile"><a href="editprofile"><i
							class="fas fa-user-edit"></i>Edit profile </a></li>
					<li data-id="edit_password"><a href="editpassword"><i
							class="fas fa-pen-nib"> </i>Change password</a></li>
					<li data-id="logout"><a href=""><i
							class="fas fa-sign-out-alt"></i> Log out</a></li>
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