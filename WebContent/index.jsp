<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<c:redirect url="${cookie['currentUser'] == null ? 'login': 'index'}"/>