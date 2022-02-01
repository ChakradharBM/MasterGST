<%@include file="/WEB-INF/views/includes/main_header.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
   <div class="bodywrap">
        <!--- company info bodybreadcrumb start -->
        <div class="bodybreadcrumb main">
			<div class="container">
				<div class="row">
					<div class="col-sm-12">
						<div class="navbar-left">
							<ul>
								<li class="nav-item nonAspAdmin" style="margin-left:8px">
									<c:choose><c:when test="${usertype eq userCenter}"><a id="nav-team" class="nav-link urllink" href="#" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> Admin</c:when><c:otherwise><a id="nav-team" class="nav-link urllink permissionTeam" href="#" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> Admin</c:otherwise></c:choose> </a> 
								</li>
								<li class="nav-item nonAspReports">
									<a id="nav-team" class="nav-link urllink" href="#" link="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Global Reports</a>
								</li>
								<li class="nav-item multi-gst permissionMulti_GSTIN-Multi_GSTIN_SEARCH">
									<a id="nav-team" class="nav-link urllink" href="#" link="${contextPath}/cp_multiGtstin/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>?type=multigstin">Multi GSTIN Report</a>
								</li>
								<li class="nav-item reminderhead">
									<a id="nav-team" class="nav-link urllink" href="#" link="${contextPath}/cp_reminders/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Reminders</a>
								</li>
								<li class="nav-item acknowledgementhead">
									<a id="nav-team" class="idAcknowledgement nav-link urllink" href="#" link="${contextPath}/cp_acknowledgementUser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Acknowledgements</a>
								</li>
								<li class="nav-item audithead">
									<a id="nav-team" class="nav-link urllink" href="#" link="${contextPath}/cp_auditlog/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Audit Log</a>
								</li>
							</ul>
						</div>
						</div>
						</div>
				<div class="alert alert-success gst-notifications" role="alert"> <img src="${contextPath}/static/mastergst/images/errors/success-alert.png" alt="Alert" class="mr-2" />
				  <span id="successMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="pull-right" onclick="closeNotifications()" />
				</div>
				<div class="alert alert-warning gst-notifications" role="alert"> <img src="${contextPath}/static/mastergst/images/errors/success-alert.png" alt="Alert" class="mr-2" />
				  <span id="successMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="pull-right" onclick="closeNotifications()" />
				</div>
				<div class="alert alert-danger gst-notifications" role="alert"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="mr-2" />
				  <span id="errorMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="pull-right" onclick="closeNotifications()" />
				</div>
			</div>
        </div>
<script type="text/javascript">
	var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
	var filingOption = '<c:out value="${client.filingOption}"/>';var globaluser='<c:out value="${fullname}"/>';var varRetType='<c:out value="${returntype}"/>';var contextPath='<c:out value="${contextPath}"/>';var urlSuffix='/${id}/${client.id}/${returntype}';var commonSuffix='/${id}/${fullname}/${usertype}';var urlSuffixs='/${id}/${client.id}';var clientId='<c:out value="${client.id}"/>';var subuseremailSuffix='/${usertype}/${id}';
	$(function() {
		$('body').tooltip({
            container: 'body',
            trigger: 'hover',
            html: true,
            animation: false,
            selector: '[data-toggle="tooltip"]'        
		});
		var date = new Date();
		month = '<c:out value="${month}"/>';
		year = '<c:out value="${year}"/>';
		if(month == null || month == '') {
			month = date.getMonth()+1;
			year = date.getFullYear();
		}
		var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
		var date = $('#datetimepicker').datepicker({
			autoclose: true,
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy',
			orientation: 'right', 
			beforeShowMonth: function (date){
				if(((filingOption == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')
				&& ('<c:out value="${returntype}"/>' == '<%=MasterGSTConstants.GSTR1%>')) || ((filingOption == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')
				&& ('<c:out value="${returntype}"/>' == '<%=MasterGSTConstants.GSTR4%>')) || ((filingOption == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')
				&& ('<c:out value="${returntype}"/>' == '<%=MasterGSTConstants.GSTR2%>'))) {
					if(date.getMonth() == 0 || date.getMonth() == 1
						|| date.getMonth() == 3	|| date.getMonth() == 4
						|| date.getMonth() == 6	|| date.getMonth() == 7
						|| date.getMonth() == 9	|| date.getMonth() == 10) {
						return false;
					}
				}
			}
		}).on('changeDate', function(ev) {
			//$('.datepicker').toggle();
			updateReturnPeriod(ev.date);
			month = ev.date.getMonth()+1;
			year = ev.date.getFullYear();
		});
		//if(filingOption != '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>') {
		//	$('#datetimepicker').datepicker('setEndDate', '0d');
		//}
		<c:if test='${not empty error}'>
			var errorMsg = '<c:out value="${error}"/>';
			if(errorMsg == 'OTP verification is not yet completed!' 
				|| errorMsg == 'Invalid Session' || errorMsg == 'Unauthorized User' 
				|| errorMsg == 'Unauthorized User!' || errorMsg == 'Missing Mandatory Params' || errorMsg == 'API Authorization Failed') {
				errorMsg += '. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.';
			}
			errorNotification(errorMsg);
		</c:if>
		$('#datetimepicker').datepicker('update', dateValue);
		if($('#datetimepicker').val() == ""){
			$('#datetimepicker').val(dateValue);
		}
	});
	function closeNotifications() {$('.gst-notifications').hide();}
	function errorNotification(errorMsg) {
		closeNotifications();
		if(errorMsg == 'OTP verification is not yet completed!'	|| errorMsg == 'Invalid Session' || errorMsg == 'Unauthorized User'	|| errorMsg == 'Unauthorized User!' || errorMsg == 'Missing Mandatory Params' || errorMsg == 'API Authorization Failed') {errorMsg += '. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.';}
		if(errorMsg == 'Your subscription has expired. Kindly subscribe to proceed further!'){
			if(varUserType == 'suvidha' || varUserType == 'enterprise'){errorMsg += ' Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.';
			}else{errorMsg += ' Click <a href="'+contextPath+'/dbllng'+commonSuffix+'/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.';}
		}$('#errorMessage').html(errorMsg);$('#errorMessage').parent().show();
	}
</script>