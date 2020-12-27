<!--core jsp-->
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<!--bootstrap-->
<link href="<c:url value="assets/bootstrap/4.1.1/bootstrap.min.css"/>"
	rel="stylesheet" id="bootstrap-css"/>
<script src="<c:url value="assets/bootstrap/4.1.1/bootstrap.min.js"/>" >
</script>
<!--jquery-->
<script src="<c:url value="assets/jquery/3.2.1/jquery.min.js"/>"></script>
<!--inclue library-->
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Chat</title>
<link rel="shortcut icon " type="image/x-icon "
	href="<c:url value="assets/icons/chat.svg"/> ">
<!--fontwesome-->
<link rel="stylesheet"
	href="<c:url value="https://use.fontawesome.com/releases/v5.5.0/css/all.css"/>"
	integrity="sha384-B4dIYHKNBt8Bc12p+WXckhzcICo0wtJAoU8YZTY5qE0Id1GSseTk6S+L3BlXeVIU"
	crossorigin="anonymous">
<!--Support Chat-->
<link href="<c:url value="assets/support/css/login/login.css"/>"
	rel="stylesheet">
<link href="<c:url value="assets/support/css/main.css"/>"
	rel="stylesheet">
</head>

<body>
	<div class="container-fluid h-100">
		<div class="row h-100 justify-content-center align-items-center">
			<div class="col-md-6 col-xl-7 rigister_card">
				<div class="card mb-sm-3 mb-md-0 login_card"></div>
			</div>
			<div class="col-md-5 col-xl-4 login">
				<div class="card mb-sm-3 mb-md-0 login_card">
					<form class="form-login">
						<div class="card-header">
							<img id="login_logo" src="assets/images/Login.png">
							<h2>Login</h2>
							<h8>Welcome, you need input eamil or phone number and
							password to continue</h8>
						</div>
						<div class="input-group">
							<div class="input-group-prepend">
								<span class="input-group-text username"><i
									class="fas fa-user"></i></span>
							</div>
							<input type="text" placeholder="Email or phone number" name="usernameTxb"
								class="form-control username">
						</div>
						<div class="input-group">
							<div class="input-group-prepend">
								<span class="input-group-text password"><i
									class="fas fa-unlock-alt"></i></span>
							</div>
							<input type="password" placeholder="password" name="passwordTxb"
								class="form-control password">
						</div>
						<label class= "error">${error}</label>
						<div class="submit-group">
							<button class="loginBtn" name="loginBtn">Login</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>

</html>