<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<%-- <c:set var="varGSTR1" value="<%=MasterGSTConstants.GSTR1%>"/> --%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Dashboard</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/ca-dashboard.css" media="all" />
<script type="text/javascript" src="${contextPath}/static/mastergst/js/client/dashboard.js"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/acknowledgement/acknowledgement.js" type="text/javascript"></script>
<%-- <script src="${contextPath}/static/mastergst/js/acknowledgement/acknowledgement.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/acknowledgement/acknowledgement_tableview.js" type="text/javascript"></script> --%>

<style>
.gstsuccess-notifications{position: absolute;width: 60%;text-align: center;top: 0%;left: 17%;display: none;z-index: 5;}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/acknowledgement_header.jsp"%>
<div class="bodywrap">
	<div class="bodybreadcrumb main">
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div class="navbar-left">
						<ul>
							<li class="nav-item">
								<a id="nav-team" class="nav-link urllink ml-3" href="#" link="${contextPath}/cp_acknowlegment/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Acknowledgements</a>
							</li>	
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<input type="hidden" name="isStorageCredentialsAvailable" id="isStorageCredentialsAvailable" value="${isStorageCredentialsAvailable}"/>
 <div class="db-ca-wrap" style="padding-top:50px!important">
    <div class="container">
 		<div class="row gstr-info-tabs pt-1 mt-3">
			<ul class="nav nav-tabs col-md-12 mt-3 pl-4" role="tablist" id="tabsactive">
				<li class="nav-item"><a class="nav-link active tabName" id="pendingTab" data-toggle="tab" href="#packtab" role="tab">Pending Acknowledgements</a></li>
				<li class="nav-item"><a class="nav-link tabName" id="uploadTab" data-toggle="tab" href="#upacktab" role="tab">Uploaded Acknowledgements</a></li>
			 </ul><input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000" />
			<div class="tab-content col-md-12 mb-3 mt-1">
				<div class="tab-pane active col-md-12" id="packtab" role="tabpane1">
					<jsp:include page="/WEB-INF/views/dashboard/acknowledgement_tab.jsp">
						<jsp:param name="id" value="${id}" />
						<jsp:param name="fullname" value="${fullname}" />
						<jsp:param name="usertype" value="${usertype}" />
						<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR1%>" />
						<jsp:param name="contextPath" value="${contextPath}" />
						<jsp:param name="month" value="${month}" />
						<jsp:param name="year" value="${year}" />
						<jsp:param name="client" value="${client}"/>
						<jsp:param name="ackTabName" value="pendingTab"/>
					</jsp:include>
				</div>
				<div class="tab-pane col-md-12 mt-0" id="upacktab" role="tabpane2">
					<jsp:include page="/WEB-INF/views/dashboard/acknowledgement_tab.jsp">
						<jsp:param name="id" value="${id}" />
						<jsp:param name="fullname" value="${fullname}" />
						<jsp:param name="usertype" value="${usertype}" />
						<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR1%>" />
						<jsp:param name="contextPath" value="${contextPath}" />
						<jsp:param name="month" value="${month}" />
						<jsp:param name="year" value="${year}" />
						<jsp:param name="client" value="${client}"/>
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
        <button type="button" class="btn btn-secondary mr-2" id="btnDelete" onclick="deleteInvoiceAttachment()"><span id="scanner" style="margin-top: -8px;margin-right: 3px;float:left;"></span>Delete Attachment</button>
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
<%-- <script src="${contextPath}/static/mastergst/js/common/filedrag-multiple.js" type="text/javascript"></script> --%>
</body>
</html>