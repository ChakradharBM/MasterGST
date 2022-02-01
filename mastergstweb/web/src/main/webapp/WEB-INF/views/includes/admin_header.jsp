<script type="text/javascript">
var userPermissions;
var table ,clientPendingPaymentTable, clientPaymentTable, clientTable, paymentTable, userArray, currentUser, selRow, usersTable, headerkeysTable;
var ewaybillClientId,ewaybillClientSecretId,einvClientId,einvClientSecretId, partnerPendingPaymentTable, partnerPaymentTable;
var deleteSpamUsersArray=new Array();
var contextPath='<c:out value="${contextPath}"/>';var fullName='${fullname}';
var loginid = '${id}';
var pArray = ["USERS","REPORTS","ERROR_LOG","MESSAGES","PAYMENTS","DETAILS","GST_PRODUCTION","EWAY_BILL_PRODUCTION","USER_TYPE_CHANGE","PARTNER_LINK","OTP_NOT_VERIFIED_USERS","PAID_EXPIRED_USERS","ALL_EXPIRED_USERS","RENEWAL","MONTHLY_INV_USAGE","CUSTOM_MONTHLY_API","ACTIVE_USERS","DEMO_USERS","SUBSCRIPTION_SUMMARY","USER_SUMMARY","MONTHLY_TAX_ITCSUM","GST_FILING_STATUS","GST_FILING_STATUS_SUM","EDIT","DELETE","ALL","ASP","CAANDCMA","SMALLANDMEDIUM","ENTERPRISE","PARTNERS","SUVIDHAKENDRA","SUBUSERS","SUBCENTERS","TESTACCOUNTS","OTPNOTVERIFIED","CREATE_MESSAGES","LATESTUPDATES","LATESTNEWS","HEADER_KEYS","API_VERSION_DOCUMENT","INVOICES_LIMIT_EXCEEDS_USERS","PARTNER_PAYMENTS"];
	$.ajax({
		url: "${contextPath}/adminuserprms?id=${id}",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(permissions) {
			userPermissions = permissions;
			if(userPermissions) {
				pArray.forEach(function(item) {
					if($.inArray(item, userPermissions) == -1) {
						$('.permission'+item.replace(/\s/g, '_')).hide();
						if(item == 'EDIT'){
							$('#edit_btn,#sand_edit_btn,#prod_edit_btn,#ewaySand_edit_btn,#eway_edit_btn,#save_pmnt_data,#cmmnt_save_btn').removeAttr("onclick").addClass('disabled');
						}
						if(item == 'DELETE'){
							$('#deletespam_user').removeAttr("onclick").addClass('disabled');
						}
						
						if(item == 'DETAILS'){
							$('#userModal #detail').addClass('d-none');
							$('#commentsTab').addClass('d-block');
							$('#details').addClass('d-none');
							$('#commentsTab2').css('color','#464a4c');
							$('#commentsTab2').css('background-color','#fff');
							$('#commentsTab2').css('border-color','#ddd #ddd #fff');
							$('#commentsTab').addClass("active");
						}
					}
				});
			} else {
				pArray.forEach(function(item) {
					$('.permission'+item.replace(/\s/g, '_')).hide();
				});
			}
		},
		error:function(data){}
	});
	function formatNumber(nStr) {
		var negativenumber = false;
		if(nStr && nStr.includes("-")){
			negativenumber = true;
			nStr = nStr.replace("-","");
		}
		nStr=nStr.toString();var afterPoint = '';
		if(nStr.indexOf('.') > 0)
		   afterPoint = nStr.substring(nStr.indexOf('.'),nStr.length);
		nStr = Math.floor(nStr);
		nStr=nStr.toString();
		var lastThree = nStr.substring(nStr.length-3);
		var otherNumbers = nStr.substring(0,nStr.length-3);
		if(otherNumbers != '')
		    lastThree = ',' + lastThree;
		var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + afterPoint;
		if(negativenumber){
			res = "-"+res;
		}
		return res;
	}
</script>

<!-- header page begin -->
    <nav class="navbar navbar-toggleable-md navbar-inverse fixed-top bg-inverse navbar-db nav-ca">
       <div class="container"> <!-- toggle button for device begin -->
        <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation"> <span class="navbar-toggler-icon"></span> </button>
        <!-- toggle button for device end -->
        <a class="navbar-brand" href="#"> <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" alt="Master Gst" /> </a>
        <div class="collapse navbar-collapse" id="navbarCollapse">
            <div class="mainnav-wrap">
            
             <ul class="navbar-nav mainnav-left">
		       <li class="nav-item permissionUSERS"> <a class="nav-link" id="users_lnk" href="${contextPath}/obtainusers?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>"> users </a> </li>
		       <li class="nav-item permissionMESSAGES"> <a class="nav-link"  id="msgs_lnk" href="${contextPath}/obtainmessages?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&msgtype=messagetype"> messages </a> </li>
			   <%-- <li class="nav-item"> <a class="nav-link"  id="monthlyapiusage_lnk" href="${contextPath}/monthlyapiusage?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>"> Monthly API Usage</a> </li> --%>
			   <li class="nav-item"> <a class="nav-link"  id="leads_lnk" href="${contextPath}/obtainleads?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>">Leads</a> </li>
			   <li class="nav-item permissionREPORTS"> <a class="nav-link"  id="reports_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>">Reports</a> </li> 			   
			   <li class="nav-item permissionERROR_LOG"> <a id="nav-errorlog" class="nav-link" href="${contextPath}/errorlog?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>"> Error Log </a> </li>
				<c:if test="${usertype eq 'subadmin'}">	   
			   
			  		 <li class="nav-item"> <a class="nav-link admin_lnk d-none"  id="admin_lnk" href="${contextPath}/superadminpage?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>"> Admin</a> </li>
				</c:if>
				<c:if test="${usertype ne 'subadmin'}">	   
			   
			  		 <li class="nav-item"> <a class="nav-link admin_lnk"  id="admin_lnk" href="${contextPath}/superadminpage?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>"> Admin</a> </li>
				</c:if>
			</ul>
            
			<!-- right side menu begin -->
                <ul class="navbar-nav mainnav-right">
                    <c:set var = "varname" value = "${fn:split(fullname, ' ')}"/>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false"> <span class="rounded-circle img-sm-rounded">${fn:substring(varname[0], 0, 1)}<c:if test="${fn:length(varname) gt 1}">${fn:substring(varname[1], 0, 1)}</c:if>
						</span> <span class="user-name"><c:out value="${fullname}" /> <span class="dbspriticons ddarw"></span></span></a>
                        <div class="dropdown-menu dropdown-menu-right">
						<ul class="profilelist">
							<li class="rowbdr">
								<div class="icon-l"><span class="userimg"><img src="${contextPath}/static/mastergst/images/credentials/user1.jpg" alt="User" class="circle" /></span> </div>
								<div class="icon-r"><span class="usertxt">${fullname}</span> </div>
							</li>
							<li class="divider"></li>
							<li><a class="btn btn-blue btn-sm pull-right" href="${contextPath}/logout">Logout</a>   </li>
						</ul>
					</div>
                    </li>
                </ul>
                <!-- right side menu end -->
            </div> 
        </div></div>
    </nav>
    <!-- header page end -->