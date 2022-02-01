<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | API Version Document</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" media="all" />
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/reports/reports.css" 	media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>

<!-- datepicker start -->
<script src="${contextPath}/static/mastergst/js/common/datetimepicker-inv.js" type="text/javascript"></script> 
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<script src="${contextPath}/static/mastergst/js/admin/usersDetail.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/admin/apisusageexceeds_reports.js" type="text/javascript"></script>	

<!-- datepicker end -->
<style type="text/css">
button.btn.btn-xs{padding: 3px 13px;background-color: #314999;border:none;}
.bodybreadcrumb{margin-top:-8px!important}
tr td:last-child {display: inline-flex;margin: 0!important;width: 89%;}
td input{border: 1px solid lightgray!important;border-radius: 6px!important;}
.gstr-info-tabs .nav-tabs { margin-bottom:10px;margin-left: -10px; }
.gstr-info-tabs .nav-tabs .nav-item.show .nav-link, .gstr-info-tabs .nav-tabs .nav-link.active, .gstr-info-tabs .nav-tabs .nav-link:hover, .gstr-info-tabs .nav-tabs .nav-link.active:hover { background:transparent; border-bottom:3px solid #8ee3fe; border-left:0; border-right:0; border-top:0; color:#374583; font-weight:bold;font-size:13px }
.gstr-info-tabs .nav-tabs .nav-link { border-bottom:3px solid transparent; border-left:0; border-right:0; border-top:0; color:#707172; font-size:13px; text-transform:uppercase }
.gstr-info-tabs .nav-tabs .nav-link:hover { border-bottom:3px solid #8ee3fe; border-left:0; border-right:0; border-top:0; font-size:13px}
.gstr-info-tabs .nav-tabs { border:0 }
.gstr-info-tabs .nav-tabs .nav-item { position:relative; }
.gstr-info-tabs .nav-tabs .nav-link.active::after, .gstr-info-tabs .nav-tabs .nav-link:hover::after {
content:'';
border-left: 10px solid transparent;
border-right: 10px solid transparent;
border-top: 10px solid #8ee3fe;
position:absolute;
left:40%;
bottom:-10px;
}
.gstr-info-tabs .nav-tabs .nav-link.active::before, .gstr-info-tabs .nav-tabs .nav-link:hover::before {
content: '';
border-left: 10px solid transparent;
border-right: 10px solid transparent;
border-top: 10px solid #f6f9fb;
position: absolute;
left: 40%;
bottom: -6px;
z-index: 1;
}
</style>
</head>
<jsp:include page="/WEB-INF/views/admin/userDetails.jsp" />
<body>
<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
<div class="bodywrap" style="min-height: 480px; padding-top: 10px">
	<!--- company info bodybreadcrumb start -->
	<div class="bodybreadcrumb">
		<div class="container" style="padding-top: 6px;">
			API Usage Exceeds Users
		</div>
	</div>
	<!--- company info bodybreadcrumb end -->
	<div class="tab-content">
   		 <div id="gstapiv" class="tab-pane fade in active show">
   		 	<div class="container" style="padding-top: 30px;">
   		 		<div class="row">
					<div class="col-sm-12">&nbsp;</div>
					<div class="col-sm-12" style="margin-top: 10px;">
						<a id="backpage_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark report mr-0 pull-right" role="button">BACK</a>
					</div>
					<div class="row gstr-info-tabs">
   		 				<ul class="nav nav-tabs pl-5" role="tablist" id="tabsactive">
							<li class="nav-item"><a class="nav-link active tabName" id="APIUSERS" data-toggle="tab" href="#apiUserstab" role="tab">API Users</a></li>
							<li class="nav-item"><a class="nav-link tabName" id="WEBUSERS" data-toggle="tab" href="#webUserstab" role="tab">Web/ Gst Users</a></li>
				 		</ul>
				 		<div class="tab-content col-md-12 mb-3 mt-1">
							<div class="tab-pane active col-md-12" id="apiUserstab" role="tabpane1">
								<jsp:include page="/WEB-INF/views/admin/apisexceeds_report_tab.jsp">
									<jsp:param name="contextPath" value="${contextPath}" />
									<jsp:param name="fullname" value="${fullname}"/>
									<jsp:param name="apisUsageType" value="APIUSERS"/>
								</jsp:include>
							</div>
							<div class="tab-pane col-md-12 mt-0" id="webUserstab" role="tabpane2">
								<jsp:include page="/WEB-INF/views/admin/apisexceeds_report_tab.jsp">
									<jsp:param name="contextPath" value="${contextPath}" />
									<jsp:param name="fullname" value="${fullname}"/>
									<jsp:param name="apisUsageType" value="WEBUSERS"/>
								</jsp:include>
							</div>
						</div>
	   		 		</div>
					<div class="col-sm-12">&nbsp;</div>
				</div>
			</div>
   		</div>
    </div>	
</div>
	<div class="modal fade" id="exReminderModal" role="dialog" aria-labelledby="exReminderModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document" style="height: 100%;">
			<div class="modal-content" style="height: 100%;">
				<div class="modal-header p-0">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
					<div class="bluehdr" style="width:100%"><h3>Send Reminder</h3></div>
				</div>
				<div class="modal-body popupright bs-fancy-checks"  style="height:auto;">
					<div class="p-5 row gstr-info-tabs">
						<div class="form-inline col-md-12" style="line-height: 50px;">
							<label for="Subject">Subject<span class="coln-txt" style="margin-left:36px;">:</span></label>
							<div class="" id="all_remindersubject" style="width:79%;">
								<input type="text" class="all_reminder_subject form-control" id="remindersubject" style="margin-left: 15px;width:98%;">
							</div>
						</div>
						<div class="form-group col-md-12">
							<label for="Meassage">Message :</label>
							<textarea class="form-control" id="remindermeassage" style="width:96%; height:110px"></textarea>
						</div>
						<div class="form-group col-md-12">
							<span id="remindersErrorMsg" style="color:red;"></span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-blue-dark" id="sendRemBtn">Send</button>
					<button type="button" class="btn btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="ExpiryCommentsModal" role="dialog" aria-labelledby="ExpiryCommentsModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content" style="height:100vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                	<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                </button>
                <div class="bluehdr" style="width:100%"><h3>Add Comments/Narration</h3></div>
			</div>
			<div class="modal-body meterialform popupright">
				<div class="row pl-4 pr-2 pt-4" style="width: 100%;">
					<span id="nocomments_leads" class="pl-2"></span>
					<div class="form-group col-md-12">
						<div class="apiisexceedcommentsInfo" style="max-height:300px;min-height:300px;overflow-y:auto;"></div>
					</div>
				</div>
			</div>
			<div class="modal-footer" style="display:block;text-align:center;">
			 	<button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>	

</body>
</html>