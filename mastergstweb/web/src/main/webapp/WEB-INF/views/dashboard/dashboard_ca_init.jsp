<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Add Client</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<style>
.db-ca-wrap{margin-top: 43px!important;padding:0px!important}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/main_header.jsp" %>

<div class="bodywrap">
  <div class="db-ca-wrap">
    <div class="container">
      <div class="row">         
        <div class="col-sm-12"><h3>You haven't Added any <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose> and Team Yet!</h3></div>
         <div class="col-md-3 col-sm-12"></div>
        <!-- dashboard ca begin -->
        <div class="db-ca-box col-md-3 col-sm-12">
          <div class="db-ca-box-img"><img src="${contextPath}/static/mastergst/images/dashboard-ca/add-loan.png" alt="Add Loan" /></div>
          <div class=""> <a href="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark"><img src="${contextPath}/static/mastergst/images/dashboard-ca/add-loan-icon.png" alt="Loan" /> Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a> </div>
          <h6>Add your first client to get started</h6>
        </div>
        <!-- dashboard ca end -->
        <!-- dashboard ca begin -->
		<c:choose><c:when test="${usertype eq userCenter}">
        <div class="db-ca-box col-md-3 col-sm-12" id="idCDInit" style="display:none;">
          <div class="db-ca-box-img"><img src="${contextPath}/static/mastergst/images/dashboard-ca/add-team.png" alt="Add Center" /></div>
          <div class=""> <a href="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark"><img src="${contextPath}/static/mastergst/images/dashboard-ca/add-team-icon.png" alt="Loan" /> Add Centers</a> </div>
          <h6>Add your first client to get started</h6>
        </div>
		</c:when><c:otherwise>
		<div class="db-ca-box col-md-3 col-sm-12">
          <div class="db-ca-box-img"><img src="${contextPath}/static/mastergst/images/dashboard-ca/add-team.png" alt="Add Team" /></div>
          <div class=""> <a href="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark"><img src="${contextPath}/static/mastergst/images/dashboard-ca/add-team-icon.png" alt="Loan" /> Add Team</a> </div>
          <h6>Add your first client to get started</h6>
        </div>
		</c:otherwise></c:choose>
        <!-- dashboard ca end -->
	
      </div>
    </div>
  </div>
</div>
<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
	var date = new Date();
	month = '<c:out value="${month}"/>';
	year = '<c:out value="${year}"/>';
	if(month == null || month == '') {
		month = date.getMonth()+1;
		year = date.getFullYear();
	}
	$(document).ready(function() {
		//$('.xdsoft_calendar').hide();
		$('#nav-client').addClass('active');
	});
 </script>
</body>
</html>
