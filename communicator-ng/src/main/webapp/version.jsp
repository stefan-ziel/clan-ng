<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="ch.claninfo.common.xml.XMLProtocolConsts" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Communicator</title>
</head>
<body>

<c:if test="${param.version ne null}">
<p>Protokollversion <%=XMLProtocolConsts.PROTOCOLL_VERSION%></p>
</c:if>
</body>
</html>