<!--core jsp-->
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ page pageEncoding="utf-8"%>
<!--bootstrap-->
<link href="<c:url value="assets/bootstrap/4.1.1/bootstrap.min.css"/>"
	rel="stylesheet" id="bootstrap-css"></link>
<!--include library-->

<!DOCTYPE html>
<html>

<head>
<meta charset=" UTF-8 ">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${fullName}</title>
<link rel="shortcut icon" type="image/x-icon "
	href="assets/icons/chat.svg ">
<link rel="stylesheet"
	href="<c:url value="https://use.fontawesome.com/releases/v5.5.0/css/all.css"/>"
	integrity="sha384-B4dIYHKNBt8Bc12p+WXckhzcICo0wtJAoU8YZTY5qE0Id1GSseTk6S+L3BlXeVIU "
	crossorigin="anonymous" />
<!--js-->
<script type="text/javascript"
	src="<c:url value="assets/jquery/3.2.1/jquery.min.js"/>"></script>
<!--Support Chat-->
<link href="<c:url value="assets/support/css/register/register.css"/>"
	rel="stylesheet">
<link href="<c:url value="assets/support/css/main.css"/>"
	rel="stylesheet">
</head>
<body>
	<script type="text/javascript"
		src="<c:url value="assets/support/js/editProfile/editProfile.js"/>"></script>
	<div class="container-fluid h-100">
		<div class="row h-100 justify-content-center align-items-center">
			<div class="col-md-8 col-xl-7 login">
				<div class="card mb-sm-3 mb-md-0 login_card">
					<form class="form-register" action="uploadavatar" method="post"
						enctype="multipart/form-data">
						<h2>Change your avatar</h2>
						<div class="input-group image-input">
							<span> <img alt="Avatar" name="avatar-img"
								src="${oldAvatar == null ?'assets/images/Login.png':oldAvatar}"
								class="rounded-circle user_img makup-avatar review_avatar" />
							</span><input type="file" name="oldAvatar" accept="image/*"
								onchange="changeAvatar(event)" class="avata_input review_avatar">
						</div>
						<div class="submit-group">
							<button class="loginBtn">Change avatar</button>
						</div>
						<div class="submit-group">
							<button class="loginBtn" name="removeBtn">Remove avatar</button>
						</div>
						<div class="submit-group">
							<button class="loginBtn" name="cancelBtn">Cancel</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>

</html>