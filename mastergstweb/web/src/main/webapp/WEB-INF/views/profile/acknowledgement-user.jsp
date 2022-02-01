<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<%-- <c:set var="varGSTR1" value="<%=MasterGSTConstants.GSTR1%>"/> --%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<title>MasterGST | Acknowledgement Users</title>
	<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/ca-dashboard.css" media="all" />
	<script type="text/javascript" src="${contextPath}/static/mastergst/js/client/dashboard.js"></script>
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
	<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
	<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
	<script src="${contextPath}/static/mastergst/js/profile/acknowledgement-user.js" type="text/javascript"></script>
	<%-- <script src="${contextPath}/static/mastergst/js/profile/acknowledgement-user.js" type="text/javascript"></script> --%>
	 <style>
		button.multiselect.dropdown-toggle.btn-block.text-left.btn.btn-default{width: 168px;text-align: center!important;}
		#clients_group .form-group .multiselect-container.dropdown-menu{width:300px!important}
	</style>
</head>
<body>
<c:choose>
	<c:when test='${not empty client && not empty client.id}'>
		<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
	</c:when>
	<c:otherwise>
		<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
	</c:otherwise>
</c:choose>
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
				<ol class="breadcrumb"><li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li><li class="breadcrumb-item active"><c:choose><c:when test="${usertype eq userEnterprise}">Acknowledgement Users</c:when><c:otherwise>Acknowledgement Users</c:otherwise></c:choose></li></ol>
				<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>
 <div class="db-ca-wrap" style="padding-top:100px!important">
    <div class="container">
		<div class="row gstr-info-tabs pt-1">
		<div style="display:block;width:100%;padding-right: 1%;">
		<a href="${contextPath}/cp_acknowledgementSavedCredentials/${id}/${fullname}/${usertype}/${month}/${year}" style="float:right">Cloud Configurations</a>
		</div>
			<ul class="nav nav-tabs col-md-12 mt-3 pl-4" role="tablist" id="tabsactive">
				<li class="nav-item"><a class="nav-link active tabName" id="pendingTab" data-toggle="tab" href="#packtab" role="tab">Pending Acknowledgements</a></li>
				<li class="nav-item"><a class="nav-link tabName" id="uploadTab" data-toggle="tab" href="#upacktab" role="tab">Uploaded Acknowledgements</a></li>
			   
			 </ul>
			 
			<div class="tab-content col-md-12 mb-3 mt-1 p-0">
				<div class="tab-pane active col-md-12" id="packtab" role="tabpane1">
				<input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000" />
					<jsp:include page="/WEB-INF/views/profile/acknowledgement-user_tab.jsp">
						<jsp:param name="id" value="${id}" />
						<jsp:param name="fullname" value="${fullname}" />
						<jsp:param name="usertype" value="${usertype}" />
						<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR1%>" />
						<jsp:param name="contextPath" value="${contextPath}" />
						<jsp:param name="month" value="${month}" />
						<jsp:param name="year" value="${year}" />
						<jsp:param name="clients" value="${clients}"/>
						<jsp:param name="ackTabName" value="pendingTab"/>
					</jsp:include>
				</div>
				<div class="tab-pane col-md-12 mt-0" id="upacktab" role="tabpane2">
					<jsp:include page="/WEB-INF/views/profile/acknowledgement-user_tab.jsp">
						<jsp:param name="id" value="${id}" />
						<jsp:param name="fullname" value="${fullname}" />
						<jsp:param name="usertype" value="${usertype}" />
						<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR1%>" />
						<jsp:param name="contextPath" value="${contextPath}" />
						<jsp:param name="month" value="${month}" />
						<jsp:param name="year" value="${year}" />
						<jsp:param name="clients" value="${clients}"/>
						<jsp:param name="ackTabName" value="uploadTab"/>
					</jsp:include>
				</div>
				
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="attachModal" tabindex="-1" role="dialog" aria-labelledby="attachModal" aria-hidden="true">
  <div class="modal-dialog" role="document" style=" position:fixed;bottom:0;right:0;margin:0;">
    <div class="modal-content p-0">
    	<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr mb-0" style="padding:10px">
          <h3>Attachments </h3>
        </div>
      <div class="modal-body p-0" style="min-width:500px;min-height:260px;max-height:calc(100vh - 100px);overflow-y:auto">
        <div class="">
         <div id="filemessages"><!-- <p>Status Messages</p> --></div>
        </div>
      </div>
      <div class="modal-footer">        
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Attachment </h3>
        </div>
        <div class="pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Attachment <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
      <input type="hidden" id="invoiceNo" name="invoiceNo"/>
      <input type="hidden" id="tabName" name="tabName"/>
       <input type="hidden" id="invoiceId" name="invoiceId"/>
        <button type="button" class="btn btn-secondary mr-2" id="btnDelete"  onclick="deleteInvoiceAttachment()"><span id="scanner" style="margin-top: -8px;margin-right: 3px;float:left;"></span>Delete Attachment</button>
        <button type="button" class="btn btn-primary closemdl" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<div style="display: none;">
<form id="AttachmentForm" name="AttachmentForm" >
<input type="hidden" name="userId" value="${id}"/>
<input type="hidden" name="returnType" value="Sales"/>
<input type="hidden" name="month" value="${month}"/>
<input type="hidden" name="year" value="${year}"/>
</form>
</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
	$(document).ready(function(){
		$('.acknowledgementhead').addClass('active');
	});
	</script>
</body>
</html>