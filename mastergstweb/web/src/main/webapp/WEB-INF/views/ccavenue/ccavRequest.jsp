<html>
<head>
	<title>Checkout page</title>
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
</head>
<body>
	<form id="nonseamless" method="post" name="redirect" action="${ccavenueUrl}/transaction/transaction.do?command=initiateTransaction"/> 
		<input type="hidden" id="encRequest" name="encRequest" value="${encRequest}">
		<input type="hidden" name="access_code" id="access_code" value="${access_code}">
		<script language='javascript'>document.redirect.submit();</script>
	</form>
</body> 
</html>
