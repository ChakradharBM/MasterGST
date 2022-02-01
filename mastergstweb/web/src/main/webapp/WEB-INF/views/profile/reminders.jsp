<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
	<head>
		<title>MasterGST | Client Reminders</title>
		<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
		<link rel="stylesheet" href="${contextPath}/static/mastergst/css/userprofile_edit/reminder.css" media="all" />
		<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
		<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
		<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
		<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
		<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
		<script src="${contextPath}/static/mastergst/js/profile/reminder.js" type="text/javascript"></script>
	</head>
	<body class="body-cls">
		<!-- header page begin -->
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
						<ol class="breadcrumb">
							<li class="breadcrumb-item">
								<c:choose>
									<c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value=" ${id} "/>/<c:out value="${fullname} "/>/<c:out value="${usertype} "/>"/>Admin</a>
									</c:when>
									<c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value=" ${id} "/>/<c:out value="${fullname} "/>/<c:out value="${usertype} "/>"/>Admin</a>
									</c:otherwise>
								</c:choose>
							</li>
							<li class="breadcrumb-item active">
								<c:choose>
									<c:when test="${usertype eq userEnterprise}">Reminders</c:when>
									<c:otherwise>Reminders</c:otherwise>
								</c:choose>
							</li>
						</ol>
						<div class="retresp"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="db-ca-wrap">
			<div class="container">
				<div class="row gstr-info-tabs">
					<ul class="nav nav-tabs col-md-12 pl-4" role="tablist" id="tabsactive">
						<li class="nav-item"><a class="nav-link active tabName" id="clientsTab" data-toggle="tab" href="#clientstab" role="tab">All Clients</a></li>
						<li class="nav-item"><a class="nav-link tabName" id="customersTab" data-toggle="tab" href="#customerstab" role="tab">All Customers</a></li>
						<li class="nav-item"><a class="nav-link tabName" id="suppliersTab" data-toggle="tab" href="#supplierstab" role="tab">All Suppliers</a></li>
					</ul>
					<div class="tab-content col-md-12 mb-3 mt-3">
						<div class="tab-pane active col-md-12" id="clientstab" role="tabpane1">
							<jsp:include page="/WEB-INF/views/profile/reminders_tab.jsp">
								<jsp:param name="id" value="${id}" />
								<jsp:param name="fullname" value="${fullname}" />
								<jsp:param name="usertype" value="${usertype}" />
								<jsp:param name="contextPath" value="${contextPath}" />
								<jsp:param name="month" value="${month}" />
								<jsp:param name="year" value="${year}" />
								<jsp:param name="user" value="${user}" />
								<jsp:param name="companyUser" value="${companyUser}" />
								<jsp:param name="usersignature" value="${usersignature}" />
								<jsp:param name="remindersTabName" value="clientstab" /> 
							</jsp:include>
						</div>
						<div class="tab-pane col-md-12 mt-0" id="customerstab" role="tabpane2">
							<jsp:include page="/WEB-INF/views/profile/reminders_tab.jsp">
								<jsp:param name="id" value="${id}" />
								<jsp:param name="fullname" value="${fullname}" />
								<jsp:param name="usertype" value="${usertype}" />
								<jsp:param name="contextPath" value="${contextPath}" />
								<jsp:param name="month" value="${month}" />
								<jsp:param name="year" value="${year}" />
								<jsp:param name="user" value="${user}" />
								<jsp:param name="companyUser" value="${companyUser}" />
								<jsp:param name="usersignature" value="${usersignature}" />
								<jsp:param name="remindersTabName" value="customerstab" />
							 </jsp:include>
						</div>
						<div class="tab-pane col-md-12 mt-0" id="supplierstab" role="tabpane2">
							<jsp:include page="/WEB-INF/views/profile/reminders_tab.jsp">
								<jsp:param name="id" value="${id}" />
								<jsp:param name="fullname" value="${fullname}" />
								<jsp:param name="usertype" value="${usertype}" />
								<jsp:param name="contextPath" value="${contextPath}" />
								<jsp:param name="month" value="${month}" />
								<jsp:param name="year" value="${year}" />
								<jsp:param name="user" value="${user}" />
								<jsp:param name="companyUser" value="${companyUser}" />
								<jsp:param name="usersignature" value="${usersignature}" />
								<jsp:param name="remindersTabName" value="supplierstab" />
							</jsp:include>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%@include file="/WEB-INF/views/includes/footer.jsp" %>
	</body>
	<!-- Delete Modal -->
	<div class="modal fade" id="AllRemindersModal" role="dialog" aria-labelledby="AllRemindersModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-header p-0">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
					<div class="bluehdr" style="width:100%">
						<h3>All Reminders</h3> </div>
				</div>
				<div class="modal-body popupright ">
					<div class="row pl-4 pr-2 pt-4">
						<h6 id="noReminders"></h6>
						<div class="form-group col-md-12">
							<div class="remindersTab"> </div>
						</div>
					</div>
				</div>
				<div class="modal-footer" style="display:block;text-align:center;">
					<button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="sendReminderModal" role="dialog" aria-labelledby="sendReminderModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-header p-0">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
					<div class="bluehdr" style="width:100%">
						<h3>Send Reminder</h3> </div>
				</div>
				<div class="modal-body popupright bs-fancy-checks">
					<div class="p-3 row gstr-info-tabs">
						<ul class="nav nav-tabs col-md-12 pl-3" role="tablist" id="tabs_active">
							<li class="nav-item"><a class="nav-link active" id="remind" data-toggle="tab" href="#drafts" role="tab">Reminder</a></li>
							<li class="nav-item"><a class="nav-link " id="remind_preview" data-toggle="tab" href="#remind_preview_mode" role="tab" onclick="reminder_Preview('Rem')">Preview</a></li>
						</ul>
						<div class="tab-content col-md-12 mb-3 mt-2 pl-0 pr-0">
							<div class="tab-pane active col-md-12" id="drafts" role="tabpane1">
								<div class="form-group successRemmsg d-none">
									<h6><i class="fa fa-check" style="font-size:32px;color:green"></i><span id="successRemmsg" class="text-success" style="font-weight:bold;color:green;"></span></h6></div>
								<div class="col-md-11 mb-1 f-12" style="color:blue;text-align: right;">You can Enter List of mail id's and also mobile no's by Comma Separated Values</div>
								<div class="form-group col-md-12 mb-1">
									<label for="client_name" class="col-md-4">Client Name<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-7" id="client_name" style="display: inline-block;"> </div>
								<div class="form-group col-md-12 mb-1">
									<label for="mobile_number" class="col-md-4">Mobile No <span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-7" id="mobile_number" style="display: inline-block;"> </div>
								<div class="form-group col-md-12 mb-1">
									<label for="clientemailid" class="col-md-4">Email id<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-7" id="client_emailid" style="display: inline-block;"> </div>
								<div class="form-group col-md-12 mb-1">
									<label for="clientemailids" class="col-md-4">CC <span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-7" id="client_emailids" style="display: inline-block;"> </div>
								<div class="form-group col-md-12 mb-1">
									<label for="Subject" class="col-md-4">Subject<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="reminder_subject form-control col-md-7" style="display: inline-block;" id="reminder_subject"> </div>
								<div class="form-group mt-3 col-md-12 mb-1 pr-0">
									<label for="Meassage" class="col-md-4">Message :</label>
									<textarea class="form-control reminder_meassage col-md-11" id="reminder_meassage" style="width:490px;height:110px;margin-left: 15px;" onkeyup=""> </textarea>
								</div>
								<div class="form-group mt-3 ml-3 col-md-12 mb-1 pr-0 signcheck">
									<div class="meterialform" style="float:left">
										<div class="checkbox pull-right" id="SignatureCheck" style="margin-top:-2px;">
											<label>
												<input class="addSignatureDetails" type="checkbox" name="isSignatureDetails" checked> <i class="helper"></i><strong>Include Signature In Mail</strong></label>
										</div>
									</div>
									<div class="col-md-11" id="reminder_usrdetails"> </div>
								</div>
							</div>
							<div class="tab-pane col-md-12 mt-0" id="remind_preview_mode" role="tabpane2">
								<div style="border:1px solid lightgray;border-radius:5px;">
									<div class="row  p-2">
										<div class="form-group col-md-12 col-sm-12">
											<h6>Dear <span id="reminder_client_name"></span>,</h6> </div>
										<div class="form-group col-md-12 col-sm-12">
											<div id="preview_message"></div>
										</div>
									</div>
									<div class="p-2">
										<p class="mb-0">Thanks ,</p>
										<div id="preview_usrname"> </div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<input type="hidden" id="rem_clientId">
				<div class="modal-footer">
					<button type="button" class="btn btn-blue-dark" id="senRemBtn" onclick="sendReminders()">Send</button>
					<button type="button" class="btn btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="AllsendReminderModal" role="dialog" aria-labelledby="AllsendReminderModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-header p-0">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
					<div class="bluehdr" style="width:100%">
						<h3>Send Reminder To All Clients</h3> </div>
				</div>
				<div class="modal-body popupright bs-fancy-checks">
					<div class="p-3 row gstr-info-tabs">
						<ul class="nav nav-tabs col-md-12 pl-3" role="tablist" id="tabs_all_active">
							<li class="nav-item"><a class="nav-link active" id="remind_all" data-toggle="tab" href="#drafts_all" role="tab">Reminder</a></li>
							<li class="nav-item"><a class="nav-link " id="remind_preview_all" data-toggle="tab" href="#remind_preview_mode_all" role="tab" onclick="reminder_Preview('AllRem')">Preview</a></li>
						</ul>
						<div class="tab-content col-md-12 mb-3 pl-0 pr-0">
							<div class="tab-pane active col-md-12" id="drafts_all" role="tabpane1">
								<div class="form-group successRemmsg1 d-none">
									<h6><i class="fa fa-check" style="font-size:32px;color:green"></i><span id="successRemmsg1" class="text-success" style="font-weight:bold;color:green;"></span></h6></div>
								<div class="col-md-11 mb-1 f-12" style="color:blue;text-align: right;">You can Enter List of mail id's and also mobile no's by Comma Separated Values</div>
								<div class="form-inline col-md-12">
									<label for="cc">CC<span class="coln-txt" style="margin-left:70px;">:</span></label>
									<div class="" id="all_reminderCC" style="width:79%;">
										<input type="text" class="all_reminder_CC form-control" id="all_reminder_CC" style="margin-left: 15px;width:98%;"> </div>
								</div>
								<div class="form-inline col-md-12" style="line-height: 50px;">
									<label for="Subject">Subject<span class="coln-txt" style="margin-left:36px;">:</span></label>
									<div class="" id="all_remindersubject" style="width:79%;">
										<input type="text" class="all_reminder_subject form-control" id="all_reminder_subject" style="margin-left: 15px;width:98%;"> </div>
								</div>
								<div class="form-group col-md-12">
									<label for="Meassage">Message :</label>
									<textarea class="form-control" id="all_reminder_meassage" style="width:96%; height:110px"> </textarea>
								</div>
								<div class="form-group mt-3 col-md-12 mb-1 pr-0 signcheck">
									<div class="meterialform" style="float:left">
										<div class="checkbox pull-right" id="SignatureCheck_all" style="margin-top:-2px;">
											<label>
												<input class="addSignatureDetails_all" type="checkbox" name="isAllSignatureDetails" checked> <i class="helper"></i><strong>Include Signature In Mail</strong></label>
										</div>
									</div>
									<div class="col-md-11" id="reminder_usrdetails_all"> </div>
								</div>
							</div>
							<div class="tab-pane col-md-12 mt-0" id="remind_preview_mode_all" role="tabpane2">
								<div style="border:1px solid lightgray;border-radius:5px;">
									<div class="row  p-2">
										<div class="form-group col-md-12 col-sm-12">
											<h6>Dear <span id="reminder_client_name_all"></span>,</h6> </div>
										<div class="form-group col-md-12 col-sm-12">
											<div id="preview_message_all"></div>
										</div>
									</div>
									<div class="p-2">
										<p class="mb-0">Thanks ,</p>
										<div id="preview_usrname_all"> </div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<input type="hidden" id="rem_all_clientId">
				<div class="modal-footer">
					<button type="button" class="btn btn-blue-dark" id="all_senRemBtn" onclick="AllsendReminders()">Send</button>
					<button type="button" class="btn btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="ConfigSignatureModal" role="dialog" aria-labelledby="ConfigSignatureModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-header p-0">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
					<div class="bluehdr" style="width:100%">
						<h3>Signature</h3> </div>
				</div>
				<div class="modal-body popupright bs-fancy-checks">
					<div class="p-3 row">
						<div class="form-group col-md-12">
							<label for="Signature">Signature :</label>
							<textarea class="form-control" id="configure_usrdetails" style="width:96%; height:110px"> </textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-blue-dark" id="config_sigbtn" onclick="saveUserSignature()">Save</button>
					<button type="button" class="btn btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<script>
	var accessReminders = '${user.accessReminders}';
	$(document).ready(function(){
		$('.reminderhead').addClass('active');
	});
	function reminderModal(id, client, email, mobileno, usrname, usrmail, usrno) {
		if(accessReminders == "false") {
			errorNotification('Your Access to the Reminder Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		} else {
			$('#sendReminderModal').modal("show");
			
			$('#reminder_meassage').val("");
			$('.successRemmsg').addClass("d-none");
			$('#client_emailids').val("");
			$('#client_name').val(client);
			if(email !="null"){
				$('#client_emailid').val(email);				
			}
			if(mobileno !="null"){
				$('#mobile_number').val(mobileno);
			}
			$('#reminder_subject').val("Reminder");
			$('#rem_clientId').val(id);
			var userSignatureDetails = '${usersignature}';
			var usrDetails = userSignatureDetails;
			if(usrDetails != "") {
				$('#reminder_usrdetails').html(usrDetails.replaceAll(/#mgst#/g, "\r\n<br />"));
			}
			$('#reminder_client_name').text(client);
		}
	}

	function AllSendReminderModal(name, mail, mobno, tabName) {
		if(accessReminders == "false") {
			errorNotification('Your Access to the Reminder Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		} else {
			$('.successRemmsg1').addClass("d-none");
			$('#all_reminder_CC,#all_reminder_subject,#all_reminder_meassage').val("");
			$('#AllsendReminderModal').modal("show");
			$('#all_senRemBtn').attr('onclick',"AllsendReminders('"+tabName+"')")
			var userSignatureDetails = '${usersignature}';
			var usrDetails = userSignatureDetails;
			if(usrDetails != "") {
				$('#reminder_usrdetails_all').html(usrDetails.replaceAll(/#mgst#/g, "\r\n<br />"));
			}
		}
	}

	function validateReminder() {
		var rclient = $('#client_name').val();
		var rmobile = $('#mobile_number').val();
		var remail = $('#client_emailid').val();
		var rsubject = $('#reminder_subject').val();
		var rid = $('#rem_clientId').val();
		var rmessage = $('.reminder_meassage').val();
		var c = 0;
		if(rclient == "") {
			c++;
		}
		if(rmobile == "") {
			c++;
		}
		if(remail == "") {
			c++;
		}
		if(rsubject == "") {
			c++;
		}
		if(rid == "") {
			c++;
		}
		if(rmessage.trim().length > 0) {} else {
			c++;
		}
		return c == 0;
	}

	function sendReminders() {
		//$('#senRemBtn').addClass("btn-loader");
		emailArray = new Array();
		mobileArray = new Array();
		ccMailArray = new Array();
		var rclient = $('#client_name').val();
		var rmobile = $('#mobile_number').val();
		var remail = $('#client_emailid').val();
		var rsubject = $('#reminder_subject').val();
		var rid = $('#rem_clientId').val();
		var rmessage = $('#reminder_meassage').val();
		var userdetails = $('#reminder_usrdetails').html();
		var signcheck = $('.addSignatureDetails').is(':checked');
		$('.successRemmsg').addClass("d-none");
		var ccmails = $('#client_emailids').val();
		if(rmobile.indexOf(',') != -1) {
			rmobile = rmobile.split(',');
			for(var i = 0; i < rmobile.length; i++) {
				mobileArray.push(rmobile[i]);
			}
		} else {
			mobileArray.push(rmobile);
		}
		if(remail.indexOf(',') != -1) {
			remail = remail.split(',');
			for(var i = 0; i < remail.length; i++) {
				emailArray.push(remail[i]);
			}
		} else {
			emailArray.push(remail);
		}
		if(ccmails.indexOf(',') != -1) {
			ccmails = ccmails.split(',');
			for(var i = 0; i < ccmails.length; i++) {
				ccMailArray.push(ccmails[i]);
			}
		} else {
			if(ccmails != '') {
				ccMailArray.push(ccmails);
			}
		}
		var remindersdata = new Object();
		remindersdata.clientid = rid;
		remindersdata.clientName = rclient;
		remindersdata.mobileNumber = mobileArray;
		remindersdata.email = emailArray;
		remindersdata.cc = ccMailArray;
		remindersdata.subject = rsubject;
		remindersdata.message = rmessage;
		remindersdata.userDetails = userdetails;
		if(validateReminder()) {
			$.ajax({
				url: "${contextPath}/sendreminders/${id}/" + rid + "?signcheck=" + signcheck,
				data: JSON.stringify(remindersdata),
				type: "POST",
				contentType: 'application/json',
				success: function(data) {
					$('.successRemmsg').removeClass("d-none");
					$('#successRemmsg').text("Message Send Succesfully");
				},
				error: function(dat) {}
			});
		} else {
			//$('#senRemBtn').removeClass("btn-loader");
		}
	}

	function sendReminderSelection(id, tabName, chkbox) {
		var tableLength = $('#dbTableReminders_'+tabName).dataTable().fnGetData().length;
		if(chkbox.checked) {
			reminderArray[tabName].push(id);
		} else {
			var rArray = new Array();
			reminderArray[tabName].forEach(function(inv) {
				if(inv != id) {
					rArray.push(inv);
				}
			});
			reminderArray[tabName] = rArray;
		}
		if(tableLength == reminderArray[tabName].length) {
			$('#remCheck_'+tabName).prop("checked", true);
		} else {
			$('#remCheck_'+tabName).prop("checked", false);
		}
		if(reminderArray[tabName].length > 0) {
			$('.Rem_Btn_'+tabName).removeClass('disable');
		} else {
			$('.Rem_Btn_'+tabName).addClass('disable');
		}
	}

	function AllsendReminders(tabName) {
		allCCmailArray = new Array();
		var subject = $('#all_reminder_subject').val();
		var message = $('#all_reminder_meassage').val();
		var alluserdtls = $('#reminder_usrdetails_all').val();
		var ccmaillist = $('#all_reminder_CC').val();
		var signcheck = $('.addSignatureDetails_all').is(':checked');
		$('.successRemmsg1').addClass("d-none");
		if(ccmaillist.indexOf(',') != -1) {
			ccmaillist = ccmaillist.split(',');
			for(var i = 0; i < ccmaillist.length; i++) {
				allCCmailArray.push(ccmaillist[i]);
			}
		} else {
			if(ccmaillist != '') {
				allCCmailArray.push(ccmaillist);
			}
		}
		var Allremindersdata = new Object();
		Allremindersdata.cc = allCCmailArray;
		Allremindersdata.subject = subject;
		Allremindersdata.message = message;
		$.ajax({
			url: "${contextPath}/sendreminderstoAllClients/${id}?clientids=" + reminderArray[tabName] + "&signcheck=" + signcheck,
			async: false,
			cache: false,
			data: JSON.stringify(Allremindersdata),
			type: "POST",
			contentType: 'application/json',
			success: function(response) {
				$('.successRemmsg1').removeClass("d-none");
				$('#successRemmsg1').text("Message Send Succesfully");
			},
			error: function(data) {
				//$('#all_senRemBtn').removeClass("btn-loader");
			}
		});
	}
	
	function sendAllRemindersSelection(chkBox,tabName) {
		var check = $('#remCheck_'+tabName).prop("checked");
		reminderArray[tabName] = new Array();
		var rows = remindersTable[tabName].rows().nodes();
		if(check) {
			remindersTable[tabName].rows().every(function() {
				var row = this.data(row);
				reminderArray[tabName].push(row.docId);
			});
		}
		$('.chckBox_'+tabName, rows).prop('checked', check);
		if(reminderArray[tabName].length > 0) {
			$('.Rem_Btn_'+tabName).removeClass('disable');
		} else {
			$('.Rem_Btn_'+tabName).addClass('disable');
		}
	}

	function reminder_Preview(type) {
		var userSignatureDetails = '${usersignature}';
		var usrDetails = userSignatureDetails;
		if(type == 'Rem') {
			var rclient = $('#client_name').val();
			var rmessage = $('#reminder_meassage').val();
			var userdtls = $('#reminder_usrdetails').val();
			if(usrDetails != "") {
				$('#preview_usrname').html(usrDetails.replaceAll(/#mgst#/g, "\r\n<br/>"));			
			}
			$('#preview_message').html(rmessage);
		} else {
			var rclient = $('#client_name').val();
			var prmessage = $('#all_reminder_meassage').val();
			var alluserdtls = $('#reminder_usrdetails_all').val();
			if(usrDetails !=""){
				$('#preview_usrname_all').html(usrDetails.replaceAll(/#mgst#/g, "\r\n<br/>"));			
			}
			$('#reminder_client_name').text(rclient);
			$('#preview_message_all').html(prmessage);
		}
	}
	$('.remind_cmnt').hover(function() {
		$(this).attr("src", "${contextPath}/static/mastergst/images/dashboard-ca/comments-blue.png");
	}, function() {
		$(this).attr("src", "${contextPath}/static/mastergst/images/dashboard-ca/comments-black.png");
	});

	function AllReminders_view(clientid) {
		$('.remindersTab,#noReminders').html("");
		$('#AllRemindersModal').modal("show");
		$.ajax({
			url: "${contextPath}/getAllReminders/" + clientid,
			async: false,
			cache: false,
			dataType: "json",
			contentType: 'application/json',
			success: function(response) {
				if(response.length == 0) {
					$('#noReminders').html("No Reminders");
				}
				for(var i = 0; i < response.length; i++) {
					$('.remindersTab').append('<div class="reminders_Tab mb-2 mr-2"><strong><label class="label_txt">Added By : <c:out value="${user.fullname}"/> </label></strong><strong><label style="float:right;">Date : ' + formatDate(response[i].createdDate) + '</label></strong><br/>' + response[i].message + '</div>');
				}
			},
			error: function(err) {}
		});
	}

	function ConfigureUserDetails(uid, uname, umail, uphno) {
		$('#ConfigSignatureModal').modal("show");
		var userSignatureDetails = '${usersignature}';
		var usrDetails = userSignatureDetails;
		if(usrDetails == "") {
			$('#configure_usrdetails').html(uname + "&#13;" + umail + "&#13;" + uphno);
		} else {
			$('#configure_usrdetails').html(usrDetails.replaceAll(/#mgst#/g, "\r\n"));
		}
	}

	function saveUserSignature() {
		var userdetails = $('#configure_usrdetails').val();
		$.ajax({
			url: "${contextPath}/saveUserSignature/${user.id}",
			data: {
				'userdetails': userdetails
			},
			type: "POST",
			contentType: 'application/x-www-form-urlencoded',
			success: function(response) {
				location.reload(true);
			},
			error: function(err) {}
		});
	}

	function formatDate(date) {
		if(date == null || typeof(date) === 'string' || date instanceof String) {
			return date;
		} else {
			var d = new Date(date),
				month = '' + (d.getMonth() + 1),
				day = '' + d.getDate(),
				year = d.getFullYear();
			if(month.length < 2) month = '0' + month;
			if(day.length < 2) day = '0' + day;
			return [day, month, year].join('-');
		}
	}
	</script>
</html>