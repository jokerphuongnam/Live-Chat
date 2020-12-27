<!--core jsp-->
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page pageEncoding="utf-8"%>
<!--bootstrap-->
<link href="<c:url value="assets/bootstrap/4.1.1/bootstrap.min.css"/>"
	rel="stylesheet" id="bootstrap-css" />
<script src="<c:url value="assets/bootstrap/4.1.1/bootstrap.min.js"/>">
	
</script>
<!--jquery-->
<script src="<c:url value="assets/jquery/3.2.1/jquery.min.js"/>"></script>
<script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"
	integrity="sha256-0YPKAwZP7Mp3ALMRVB2i8GXeEndvCq3eSl/WsAl1Ryk="
	crossorigin="anonymous"></script>
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
<link href="<c:url value="assets/support/css/register/register.css"/>"
	rel="stylesheet">
<link href="<c:url value="assets/support/css/main.css"/>"
	rel="stylesheet">
</head>
<body>
	<script type="text/javascript"
		src="<c:url value="assets/support/js/register/register.js"/>"></script>
	<div class="container-fluid h-100">
		<div class="row h-100 justify-content-center align-items-center">
			<div class="col-md-8 col-xl-7 login">
				<div class="card mb-sm-3 mb-md-0 login_card">
					<form:form action="register" class="form-register"
						modelAttribute="registerUser">
						<h2>Register</h2>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="far fa-envelope"></i></span>
								</div>
								<form:input path="email" type="text"
									placeholder="Email(add '@gmail.com' if you haven't '@')"
									class="form-control" />
							</div>
							<div class="error-area error">
								<form:errors path="email"></form:errors>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="fas fa-phone-square"></i></span>
								</div>
								<form:input path="phoneNumber" type="number"
									pattern="/^-?\d+\.?\d*$/"
									onKeyPress="if(this.value.length==10) return false;"
									placeholder="Phone Number" class="form-control" />
							</div>
							<div class="error-area error">
								<form:errors path="phoneNumber"></form:errors>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="fas fa-unlock-alt"></i></span>
								</div>
								<form:input path="password" type="password"
									placeholder="Password" class="form-control" />
							</div>
							<div class="error-area error">
								<form:errors path="password"></form:errors>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="fas fa-unlock-alt"></i></span>
								</div>
								<input type="password" name="repeatPassword"
									placeholder="Repeat Password" class="form-control" />
							</div>
							<label class="error-area error">${repeatPasswordError}</label>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="fas fa-user"></i></span>
								</div>
								<form:input path="firstName" type="text"
									placeholder="First Name" class="form-control" />
							</div>
							<div class="error-area error">
								<form:errors path="firstName"></form:errors>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="fas fa-signature"></i></span>
								</div>
								<form:input path="lastName" type="text" placeholder="Last Name"
									class="form-control" />
							</div>
							<div class="error-area error">
								<form:errors path="lastName"></form:errors>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="fas fa-venus-mars"></i></span>
								</div>
								<form:select path="sex" class="form-control">
									<form:option value="0">Nam</form:option>
									<form:option value="1">Nữ</form:option>
								</form:select>
							</div>
							<div class="error-area error">
								<form:errors path="sex"></form:errors>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="fas fa-birthday-cake"></i></span>
								</div>
								<form:input path="birthDay" type="text"
									placeholder="Birth day(separated by '/')" class="form-control" />
							</div>
							<label class="error-area error">${birthDayError}</label>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="input-group-prepend">
									<span class="input-group-text tb-label"><i
										class="fas fa-map-marked-alt"></i></span>
								</div>
								<form:input path="address" type="text" placeholder="Address"
									class="form-control" />
							</div>
							<div class="error-area error">
								<form:errors path="address"></form:errors>
							</div>
						</div>
						<label class="error">${error}</label>
						<div class="submit-group">
							<button class="loginBtn" name="registerBtn">Register</button>
						</div>
						<div class="submit-group">
							<button class="loginBtn" name="cancelBtn">Cancel</button>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
</body>

</html>