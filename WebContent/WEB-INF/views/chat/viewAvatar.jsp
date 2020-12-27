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
<!--Support Chat-->
<link href="<c:url value="assets/support/css/register/register.css"/>"
	rel="stylesheet">
<link href="<c:url value="assets/support/css/chat/viewAvatar.css"/>"
	rel="stylesheet">
<link href="<c:url value="assets/support/css/main.css"/>"
	rel="stylesheet">
</head>
<body>
	<div class="container-fluid h-100">
		<div class="row h-100 justify-content-center align-items-center">
			<div class="col-md-8 col-xl-7 login">
				<div class="card mb-sm-3 mb-md-0 login_card">
					<form class="form-register" action="viewavatar" method="post">
						<h2>Your avatar</h2>
						<div class="row">
							<div class="col-1"></div>
							<div class="col">
								<img alt="Avatar" name="avatar-img"
									src="${avatar == null ?'assets/images/Login.png':avatar}"
									class="full-avatar" />
							</div>
							<div class="col-1"></div>
						</div>
						<div class="submit-group">
							<h1></h1>
						</div>
						<div class="submit-group">
							<button class="loginBtn">Change avatar</button>
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