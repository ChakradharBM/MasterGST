<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Users</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<!-- datepicker start -->
<script src="${contextPath}/static/mastergst/js/common/datetimepicker-inv.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<!-- datepicker end -->
<script>
$(document).ready(function(){
	
$('.admin_lnk').addClass('active');
});
</script>
<style type="text/css">
div#adminUserDataTable_filter{margin-right: 11%!important;}

</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>

<c:set value="${id}" var="id"/>
<c:set value="${fullname}" var="fullname"/>
<c:set value="${usertype}" var="usertype"/>

<div class="bodywrap" style="min-height: 480px; padding-top: 10px">
	<!--- company info bodybreadcrumb start -->
	<div class="bodybreadcrumb" style="margin-top: -8px!important;">
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div class="bdcrumb-tabs">
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">Users</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!--- company info bodybreadcrumb end -->
	<div class="container" style="padding-top: 40px;">
		<div class="col-sm-12">&nbsp;</div>
		<div class="col-sm-12">&nbsp;</div>
		<div class="customtable tabtable">
			<a id="adminuserpage_lnk" class="btn btn-blue-dark report mr-2 pull-right adminUser-link-btn" style="padding: 10px 21px;position: absolute;right: -7px; z-index: 2;" onClick="populateElementsData()" role="button" data-toggle="modal" data-target="#adminUserModal">ADD USER</a>
			<table id="adminUserDataTable" class="display dataTable meterialform p-0" cellspacing="0" width="100%">
				<thead>
					<tr>
						<th>User Name</th>
						<th>Email</th>
						<th>Phone</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${adminUsersList}" var="adminUsersList">
					<!-- <script type="text/javascript">
									var adminuser = new Object();
									adminuser.id = '<c:out value="${adminUsersList.id}"/>';
									adminuser.name = '<c:out value="${adminUsersList.adminUserName}"/>';
									adminuser.type = '<c:out value="${adminUsersList.adminUserEmail}"/>';
									admin_users.push(adminuser);
						</script> -->
					<tr id="row${adminUsersList.id}">
						<td><c:out value="${adminUsersList.adminUserName}"/></td>
						<td><c:out value="${adminUsersList.adminUserEmail}"/></td><!-- onClick="deleteAdminUser('${adminUsersList.adminUserId}')"  -->
						<td></td>
						<td class="actionicons"><a class="btn-edt" href="#" onClick="editAdminUser('${adminUsersList.id}')"><i class="fa fa-edit"></i> </a>
						
						<a href="#" onClick="showDeletePopup('${adminUsersList.id}','${adminUsersList.adminUserEmail}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
						
						
						<%-- <a href="${contextPath}/deleteadminuserdetails?adminuserid=<c:out value="${adminUsersList.id}"/>&id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td> --%>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>		
	</div>
</div>
<!-- Delete Modal Start -->
			<div class="modal fade" id="deleteAdminUserModal" role="dialog" aria-labelledby="deleteAdminUserModal" aria-hidden="true">
				<div class="modal-dialog col-6 modal-center" role="document">
					<div class="modal-content">
						<div class="modal-body">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
							<div class="invoice-hdr bluehdr">
							<h3>Delete User </h3>
						</div>
						<div class=" pl-4 pt-4 pr-4">
							<h6>Are you sure you want to delete User <span id="delPopupDetails"></span> ?</h6>
							<p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
						</div>
					</div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete User</button>
			        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
			      </div>
			    </div>
			</div>
		</div>
		<!-- Delete Modal End -->
<!-- Add Modal Form start -->
<div class="modal fade" id="adminUserModal" role="dialog" aria-labelledby="adminUserModal" aria-hidden="true">
	<div class="modal-dialog modal-md modal-right" role="document" style="height:100%;min-width:800px">
    	<div class="modal-content" id="adminUserModalBody" style="height:100%;">
    		<div class="modal-header p-0">
    		<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                	<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                </button>
                <div class="adminUserModal-hdr bluehdr" style="width:100%;">
                	<h3 id="adminUserModalTitle">ADD USERS</h3>
                </div>
    		</div>
    	
        	<div class="modal-body meterialform bs-fancy-checks" style="height:100%;overflow:auto;">
            	
               	<form:form method="POST" data-toggle="validator" class="meterialform" id="adminuserform" action="saveAdminUserDetails" modelAttribute="adminUsers">
					<input type="hidden" value="${id}" name="id" id="id"/>
					<input type="hidden" value="${fullname}" name="fullname" id="fullname"/>
					<input type="hidden" value="${usertype}" name="usertype" id="usertype"/>
					<div class="email-errormsg form-group">
    					<h6><span id="errormsg" style="margin-left: 13px;" class="text-danger"></span></h6>
  					</div> 
					<div class="container">
					<div class="">
						
						<div class="row">
							<div class="form-group col-md-12 col-sm-12">
								<div class="row">
									<label class="col-md-4" for="adminUserName" >User Name<span class="coln-txt">:</span></label>
									<div class="col-md-6" id="divadminUserName">          
										<input type="text" class="form-control" autocomplete="off" name="adminUserName" data-error="please enter your name" id="adminUserName" placeholder="Enter Name" required="required" style="border: 1px solid rgba(0,0,0,.15);">
									</div>
								</div>
							</div>
							<div class="form-group col-md-12 col-sm-12" >
								<div class="row">
									<label class="col-md-4" for="adminUserEmail">Email Id<span class="coln-txt">:</span></label>
									<div class="col-md-6" id="divadminUserEmail">          
										<input type="text" class="form-control" autocomplete="off" name="adminUserEmail" id="adminUserEmail" data-error="please enter valid email" placeholder="sample@abc.com" required="required" style="border: 1px solid rgba(0,0,0,.15);">
									</div>
								</div>
							</div>
							<div class="form-group col-md-12 col-sm-12">
								<div class="row">
									<label class="col-md-4" for="adminUserPassword">Password<span class="coln-txt">:</span></label>
									<div class="col-md-6" id="divadminUserPassword">          
										<input type="password" autocomplete="off" class="form-control" name="adminUserPassword" id="adminUserPassword" placeholder="******" data-error="please enter password" required="required" style="border: 1px solid rgba(0,0,0,.15);">
									</div>
								</div>
							</div>
							<div class="form-group col-md-12 col-sm-12">
								<p class="lable-txt mb-4">Menus</p>
								<c:forEach items="${menupermissionsList}" var="admin_permissions" varStatus="loop">
								<div class="form-check form-check-inline col-md-5 col-sm-5">
										<input class="form-check-input ${admin_permissions}" type="checkbox" id="permissions${loop.index}" name='permissions[${admin_permissions}][${loop.index}].name' value="${admin_permissions}"/>
										<label for="permission${loop.index}"><span class="ui"></span>
										</label> <span class="labletxt" id="${admin_permissions}" style="font-size:13px;">${admin_permissions}</span>
									</div>
								</c:forEach>
							</div>	
								<div class="form-group col-md-12 col-sm-12">
								<p class="lable-txt mb-4">Actions</p>
								<c:forEach items="${actpermissionsList}" var="admin_permissions" varStatus="loop">
								<div class="form-check form-check-inline col-md-5 col-sm-5">
										<input class="form-check-input ${admin_permissions}" type="checkbox" id="permissions${loop.index}" name='permissions[${admin_permissions}][${loop.index}].name' value="${admin_permissions}"/>
										<label for="permission${loop.index}"><span class="ui"></span>
										</label> <span class="labletxt" id="${admin_permissions}" style="font-size:13px;">${admin_permissions}</span>
									</div>
								</c:forEach>
							</div>	
							<div class="form-group col-md-12 col-sm-12">
								<p class="lable-txt">Tabs</p>
								<div class="pl-3">
								<p class="lable-txt mb-4">Users</p>
								<c:forEach items="${tabpermissionsList}" var="admin_permissions" varStatus="loop">
								<div class="form-check form-check-inline col-md-5 col-sm-5">
										<input class="form-check-input ${admin_permissions}" type="checkbox" id="permissions${loop.index}" name='permissions[${admin_permissions}][${loop.index}].name' value="${admin_permissions}"/>
										<label for="permission${loop.index}"><span class="ui"></span>
										</label> <span class="labletxt" id="${admin_permissions}" style="font-size:13px;">${admin_permissions}</span>
									</div>
								</c:forEach></div>
								<div class="pl-3">
								<p class="lable-txt mb-4">Messages</p>
								<c:forEach items="${msgpermissionsList}" var="admin_permissions" varStatus="loop">
								<div class="form-check form-check-inline col-md-5 col-sm-5">
										<input class="form-check-input ${admin_permissions}" type="checkbox" id="permissions${loop.index}" name='permissions[${admin_permissions}][${loop.index}].name' value="${admin_permissions}"/>
										<label for="permission${loop.index}"><span class="ui"></span>
										</label> <span class="labletxt" id="${admin_permissions}" style="font-size:13px;">${admin_permissions}</span>
									</div>
								</c:forEach></div>
								<div class="pl-3">
							<p class="lable-txt mb-4">Details</p>
								<c:forEach items="${genpermissionsList}" var="admin_permissions" varStatus="loop">
								<div class="form-check form-check-inline col-md-5 col-sm-5">
										<input class="form-check-input ${admin_permissions}" type="checkbox" id="permissions${loop.index}" name='permissions[${admin_permissions}][${loop.index}].name' value="${admin_permissions}"/>
										<label for="permission${loop.index}"><span class="ui"></span>
										</label> <span class="labletxt" id="${admin_permissions}" style="font-size:13px;">${admin_permissions}</span>
									</div>
								</c:forEach>
							</div>	
							</div>
							
							<div class="form-group col-md-12 col-sm-12">
								<p class="lable-txt mb-4">Reports</p>
								<c:forEach items="${arrReports}" var="admin_permissions" varStatus="loop">
								<div class="form-check form-check-inline col-md-5 col-sm-5">
										<input class="form-check-input ${admin_permissions}" type="checkbox" id="permissions${loop.index}" name='permissions[${admin_permissions}][${loop.index}].name' value="${admin_permissions}"/>
										<label for="permission${loop.index}"><span class="ui"></span>
										</label> <span class="labletxt" id="${admin_permissions}" style="font-size:13px;">${admin_permissions}</span>
									</div>
								</c:forEach>
							</div>		
						
							<%-- <div class="form-group col-md-12 col-sm-12">
								<p class="lable-txt mb-4">Select the Options</p>
								<c:forEach items="${permissionsList}" var="admin_permissions" varStatus="loop">
								<div class="form-check form-check-inline col-md-5 col-sm-5">
										<input class="form-check-input ${admin_permissions}" type="checkbox" id="permissions${loop.index}" name='permissions[${admin_permissions}][${loop.index}].name' value="${admin_permissions}"/>
										<label for="permission${loop.index}"><span class="ui"></span>
										</label> <span class="labletxt">${admin_permissions}</span>
									</div>
								</c:forEach>
							</div>		 --%>				 
						</div>
					</div>
					
				</div>
			</form:form>
		</div>		
			<div class="modal-footer">
				<input  type="submit" class="btn btn-blue-dark save-btn pull-right  ml-10p mr-2" id="adminusersavebtn" onclick="user_save()" value="save"/>
				<button type="button" class="btn btn-blue-dark close-btn pull-right" data-dismiss="modal">Close</button>
			</div>
				
</div>
</div>
</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp"%>
<!-- footer end here -->

<script type="text/javascript">

/* var admin_users=new Array(); */

var table = $('#adminUserDataTable').DataTable();
 function user_save(){
	$('#adminuserform').submit();
} 
$('#adminuserform').submit(function(e) {
});
function showDeletePopup(id,email){
	$('#deleteAdminUserModal').modal('show');
	$('#delPopupDetails').html(email);
	$('#btnDelete').attr('onclick', "deleteProduct('"+id+"')"); 
}

function deleteProduct(id) {
	$.ajax({
		url: "${contextPath}/deleteadminuserdetails?adminuserid="+id,
		success : function(response) {
			table.row( $('#row'+id) ).remove().draw();
		},error:function(data){
		}
	});
}
$('#adminUserEmail').change(function() {

	var email=$('#adminUserEmail').val();
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url: "${contextPath}/emailidexits?email="+email,
		success : function(response) {
			if(response == 'success'){
			
				$('#errormsg').text('Email already exists');
			}else{
				$('#errormsg').text('');
			}
		}
	});
});

/* $(".modal").on("hidden.bs.modal", function(){
	$('#adminUserName').val('');
	$('#adminUserEmail').val('');
	$('#adminUserPassword').val('');
}); */

$('body').on('hidden.bs.modal', '.modal', function () {
    $(this).removeData('bs.modal');
  });

function populateElementsData(){
	$('#adminUserName').val('');
	$('#adminUserEmail').val('');
	$('#adminUserPassword').val('');
	$('#PAID_EXPIRED_USERS').html("Paid Expired Users (Sales Status)");
	$('#ALL_EXPIRED_USERS').html("All Expired Users");
	$('#RENEWAL').html("Renewal In Next 45 Days");
	$('#MONTHLY_INV_USAGE').html("Monthly Invoices Usage");
	$('#CUSTOM_MONTHLY_API').html("Customer wise Monthly API Usage");
	$('#PARTNER_PAYMENTS').html("Partner Payments");
	$('#INVOICES_LIMIT_EXCEEDS_USERS').html("Invoices Limit Exceeds Users");
	$('#API_VERSION_DOCUMENT').html("API Version Document");
	$('#ACTIVE_USERS').html("Active Users");
	$('#DEMO_USERS').html("Demo Users");
	$('#SUBSCRIPTION_SUMMARY').html("Subscription Summary");
	$('#USER_SUMMARY').html("User Summary");
	$('#PAYMENTS').html("Payments");
	$('#DETAILS').html("Details");
	$('#GST_PRODUCTION').html("GST Production");
	$('#EWAY_BILL_PRODUCTION').html("EwayBill Production");
	$('#USER_TYPE_CHANGE').html("User Type Change");
	$('#PARTNER_LINK').html("Partner Link");
	$('#CREATE_MESSAGES').html("Create Messages");
	$('#LATESTUPDATES').html("Latest Updates");
	$('#LATESTNEWS').html("Latest News");
	$('#ALL').html("All");
	$('#ASP').html("ASP");
	$('#CAANDCMA').html("CA/CMA");
	$('#SMALLANDMEDIUM').html("Small/Medium");
	$('#ENTERPRISE').html("Enterprise");
	$('#PARTNERS').html("Partners");
	$('#SUVIDHAKENDRA').html("Suvidha Kendra");
	$('#SUBUSERS').html("Sub Users");
	$('#SUBCENTERS').html("Sub Centers");
	$('#TESTACCOUNTS').html("Test Accounts");
	$('#OTPNOTVERIFIED').html("OTP Not Verified");
	$('#USERS').html("Users");
	$('#REPORTS').html("Reports");
	$('#ERROR_LOG').html("Error Log");
	$('#MESSAGES').html("Messages");
	$('#LEADS').html("Leads");
	$('#EDIT').html("Edit");
	$('#DELETE').html("Delete");
}

function deleteAdminUser(id){
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url:"${contextPath}/deleteadminuserdetails?adminuserid="?
		
		url: "${contextPath}/deleteadminuserdetails?id="+id,
		success : function(data) {
		}
	});
}

function editAdminUser(id){
	$('#adminUserName').val('');
	$('#adminUserEmail').val('');
	$('#adminUserPassword').val('');
	$('#adminUserModal').on('hidden.bs.modal', function (e) {
		  $(this).find("input[type=checkbox]")
		       .prop("checked", "")
		       .end();
	});
	$('#PAID_EXPIRED_USERS').html("Paid Expired Users (Sales Status)");
	$('#ALL_EXPIRED_USERS').html("All Expired Users");
	$('#RENEWAL').html("Renewal In Next 45 Days");
	$('#MONTHLY_INV_USAGE').html("Monthly Invoices Usage");
	$('#CUSTOM_MONTHLY_API').html("Customer wise Monthly API Usage");
	$('#PARTNER_PAYMENTS').html("Partner Payments");
	$('#INVOICES_LIMIT_EXCEEDS_USERS').html("Invoices Limit Exceeds Users");
	$('#API_VERSION_DOCUMENT').html("API Version Document");
	$('#ACTIVE_USERS').html("Active Users");
	$('#DEMO_USERS').html("Demo Users");
	$('#SUBSCRIPTION_SUMMARY').html("Subscription Summary");
	$('#USER_SUMMARY').html("User Summary");
	$('#PAYMENTS').html("Payments");
	$('#DETAILS').html("Details");
	$('#GST_PRODUCTION').html("GST Production");
	$('#EWAY_BILL_PRODUCTION').html("EwayBill Production");
	$('#USER_TYPE_CHANGE').html("User Type Change");
	$('#PARTNER_LINK').html("Partner Link");
	$('#CREATE_MESSAGES').html("Create Messages");
	$('#LATESTUPDATES').html("Latest Updates");
	$('#LATESTNEWS').html("Latest News");
	$('#ALL').html("All");
	$('#ASP').html("ASP");
	$('#CAANDCMA').html("CA/CMA");
	$('#SMALLANDMEDIUM').html("Small/Medium");
	$('#ENTERPRISE').html("Enterprise");
	$('#PARTNERS').html("Partners");
	$('#SUVIDHAKENDRA').html("Suvidha Kendra");
	$('#SUBUSERS').html("Sub Users");
	$('#SUBCENTERS').html("Sub Centers");
	$('#TESTACCOUNTS').html("Test Accounts");
	$('#OTPNOTVERIFIED').html("OTP Not Verified");
	$('#USERS').html("Users");
	$('#REPORTS').html("Reports");
	$('#ERROR_LOG').html("Error Log");
	$('#MESSAGES').html("Messages");
	$('#LEADS').html("Leads");
	$('#EDIT').html("Edit");
	$('#DELETE').html("Delete");
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url: "${contextPath}/getadminuserdetails?id="+id,
		success : function(data) {
			$('#adminUserName').val(data.adminUserName);
			$('#adminUserEmail').val(data.adminUserEmail);
			$('#adminUserPassword').val(data.adminUserPassword);		
			$.each( data.permissions, function( i, val ) {
				
				if(val[0].name == "USERS" && val[0].status == "Yes"){
					$('.USERS').prop('checked', true);
				}else if(val[0].name == "USERS" && val[0].status == "No"){
					$('.USERS').prop('checked', false);
				}
				if(val[0].name == "ERROR_LOG" && val[0].status == "Yes"){
					$('.ERROR_LOG').prop('checked', true);
				}else if(val[0].name == "ERROR_LOG" && val[0].status == "No"){
					$('.ERROR_LOG').prop('checked', false);
				}
				
				if(val[0].name == "PAYMENTS" && val[0].status == "Yes"){
					
					$('.PAYMENTS').prop('checked', true);
				}else if(val[0].name == "PAYMENTS" && val[0].status == "No"){
					
					$('.PAYMENTS').prop('checked', false);
				}
				
				if(val[0].name == "EWAY_BILL_PRODUCTION" && val[0].status == "Yes"){
					
					$('.EWAY_BILL_PRODUCTION').prop('checked', true);
				}else if(val[0].name == "EWAY_BILL_PRODUCTION" && val[0].status == "No"){
					
					$('.EWAY_BILL_PRODUCTION').prop('checked', false);
				}
				
				if(val[0].name == "DETAILS" && val[0].status == "Yes"){
					
					$('.DETAILS').prop('checked', true);
				}else if(val[0].name == "DETAILS" && val[0].status == "No"){
					
					$('.DETAILS').prop('checked', false);
				}
				
				if(val[0].name == "MESSAGES" && val[0].status == "Yes"){
					
					$('.MESSAGES').prop('checked', true);
				} else if(val[0].name == "MESSAGES" && val[0].status == "No"){
					
					$('.MESSAGES').prop('checked', false);
				} 

				if(val[0].name == "LEADS" && val[0].status == "Yes"){
					$('.LEADS').prop('checked', true);
				} else if(val[0].name == "LEADS" && val[0].status == "No"){
					$('.LEADS').prop('checked', false);
				} 
								
				if(val[0].name == "REPORTS" && val[0].status == "Yes"){
					
					$('.REPORTS').prop('checked', true);
				}else if(val[0].name == "REPORTS" && val[0].status == "No"){
					
					$('.REPORTS').prop('checked', false);
				}
				if(val[0].name == "GST_PRODUCTION" && val[0].status == "Yes"){
					
					$('.GST_PRODUCTION').prop('checked', true);
				}else if(val[0].name == "GST_PRODUCTION" && val[0].status == "No"){
					$('.GST_PRODUCTION').prop('checked', false);
				}
				if(val[0].name == "USER_TYPE_CHANGE" && val[0].status == "Yes"){
					$('.USER_TYPE_CHANGE').prop('checked', true);
				}else if(val[0].name == "USER_TYPE_CHANGE" && val[0].status == "No"){
					$('.USER_TYPE_CHANGE').prop('checked', false);
				}
				if(val[0].name == "PARTNER_LINK" && val[0].status == "Yes"){
					$('.PARTNER_LINK').prop('checked', true);
				}else if(val[0].name == "PARTNER_LINK" && val[0].status == "No"){
					$('.PARTNER_LINK').prop('checked', false);
				}
				if(val[0].name == "OTP_NOT_VERIFIED_USERS" && val[0].status == "Yes"){
					$('.OTP_NOT_VERIFIED_USERS').prop('checked', true);
				}else if(val[0].name == "OTP_NOT_VERIFIED_USERS" && val[0].status == "No"){
					$('.OTP_NOT_VERIFIED_USERS').prop('checked', false);
				}
				if(val[0].name == "PAID_EXPIRED_USERS" && val[0].status == "Yes"){
					$('.PAID_EXPIRED_USERS').prop('checked', true);
				}else if(val[0].name == "PAID_EXPIRED_USERS" && val[0].status == "No"){
					$('.PAID_EXPIRED_USERS').prop('checked', false);
				}
				if(val[0].name == "ALL_EXPIRED_USERS" && val[0].status == "Yes"){
					$('.ALL_EXPIRED_USERS').prop('checked', true);
				}else if(val[0].name == "ALL_EXPIRED_USERS" && val[0].status == "No"){
					$('.ALL_EXPIRED_USERS').prop('checked', false);
				}
				if(val[0].name == "RENEWAL" && val[0].status == "Yes"){
					$('.RENEWAL').prop('checked', true);
				}else if(val[0].name == "RENEWAL" && val[0].status == "No"){
					$('.RENEWAL').prop('checked', false);
				}
				if(val[0].name == "MONTHLY_INV_USAGE" && val[0].status == "Yes"){
					$('.MONTHLY_INV_USAGE').prop('checked', true);
				}else if(val[0].name == "MONTHLY_INV_USAGE" && val[0].status == "No"){
					$('.MONTHLY_INV_USAGE').prop('checked', false);
				}
				if(val[0].name == "CUSTOM_MONTHLY_API" && val[0].status == "Yes"){
					$('.CUSTOM_MONTHLY_API').prop('checked', true);
				}else if(val[0].name == "CUSTOM_MONTHLY_API" && val[0].status == "No"){
					$('.CUSTOM_MONTHLY_API').prop('checked', false);
				}
				if(val[0].name == "ACTIVE_USERS" && val[0].status == "Yes"){
					$('.ACTIVE_USERS').prop('checked', true);
				}else if(val[0].name == "ACTIVE_USERS" && val[0].status == "No"){
					$('.ACTIVE_USERS').prop('checked', false);
				}
				if(val[0].name == "DEMO_USERS" && val[0].status == "Yes"){
					$('.DEMO_USERS').prop('checked', true);
				}else if(val[0].name == "DEMO_USERS" && val[0].status == "No"){
					$('.DEMO_USERS').prop('checked', false);
				}
				if(val[0].name == "SUBSCRIPTION_SUMMARY" && val[0].status == "Yes"){
					$('.SUBSCRIPTION_SUMMARY').prop('checked', true);
				}else if(val[0].name == "SUBSCRIPTION_SUMMARY" && val[0].status == "No"){
					$('.SUBSCRIPTION_SUMMARY').prop('checked', false);
				}
				/* if(val[0].name == "MONTHLY_TAX_ITCSUM" && val[0].status == "Yes"){
					$('.MONTHLY_TAX_ITCSUM').prop('checked', true);
				}else if(val[0].name == "MONTHLY_TAX_ITCSUM" && val[0].status == "No"){
					$('.MONTHLY_TAX_ITCSUM').prop('checked', false);
				}
				if(val[0].name == "GST_FILING_STATUS" && val[0].status == "Yes"){
					$('.GST_FILING_STATUS').prop('checked', true);
				}else if(val[0].name == "GST_FILING_STATUS" && val[0].status == "No"){
					$('.GST_FILING_STATUS').prop('checked', false);
				}
				if(val[0].name == "GST_FILING_STATUS_SUM" && val[0].status == "Yes"){
					$('.GST_FILING_STATUS_SUM').prop('checked', true);
				}else if(val[0].name == "GST_FILING_STATUS_SUM" && val[0].status == "No"){
					$('.GST_FILING_STATUS_SUM').prop('checked', false);
				} */
				if(val[0].name == "USER_SUMMARY" && val[0].status == "Yes"){
					$('.USER_SUMMARY').prop('checked', true);
				}else if(val[0].name == "USER_SUMMARY" && val[0].status == "No"){
					$('.USER_SUMMARY').prop('checked', false);
				}
				if(val[0].name == "ALL" && val[0].status == "Yes"){
					$('.ALL').prop('checked', true);
				}else if(val[0].name == "ALL" && val[0].status == "No"){
					$('.ALL').prop('checked', false);
				}
				if(val[0].name == "ASP" && val[0].status == "Yes"){
					$('.ASP').prop('checked', true);
				}else if(val[0].name == "ASP" && val[0].status == "No"){
					$('.ASP').prop('checked', false);
				}
				if(val[0].name == "CAANDCMA" && val[0].status == "Yes"){
					$('.CAANDCMA').prop('checked', true);
				}else if(val[0].name == "CAANDCMA" && val[0].status == "No"){
					$('.CAANDCMA').prop('checked', false);
				}
				if(val[0].name == "SMALLANDMEDIUM" && val[0].status == "Yes"){
					$('.SMALLANDMEDIUM').prop('checked', true);
				}else if(val[0].name == "SMALLANDMEDIUM" && val[0].status == "No"){
					$('.SMALLANDMEDIUM').prop('checked', false);
				}
				if(val[0].name == "ENTERPRISE" && val[0].status == "Yes"){
					$('.ENTERPRISE').prop('checked', true);
				}else if(val[0].name == "ENTERPRISE" && val[0].status == "No"){
					$('.ENTERPRISE').prop('checked', false);
				}
				if(val[0].name == "PARTNERS" && val[0].status == "Yes"){
					$('.PARTNERS').prop('checked', true);
				}else if(val[0].name == "PARTNERS" && val[0].status == "No"){
					$('.PARTNERS').prop('checked', false);
				}
				if(val[0].name == "SUVIDHAKENDRA" && val[0].status == "Yes"){
					$('.SUVIDHAKENDRA').prop('checked', true);
				}else if(val[0].name == "SUVIDHAKENDRA" && val[0].status == "No"){
					$('.SUVIDHAKENDRA').prop('checked', false);
				}
				if(val[0].name == "SUBUSERS" && val[0].status == "Yes"){
					$('.SUBUSERS').prop('checked', true);
				}else if(val[0].name == "SUBUSERS" && val[0].status == "No"){
					$('.SUBUSERS').prop('checked', false);
				}
				if(val[0].name == "SUBCENTERS" && val[0].status == "Yes"){
					$('.SUBCENTERS').prop('checked', true);
				}else if(val[0].name == "SUBCENTERS" && val[0].status == "No"){
					$('.SUBCENTERS').prop('checked', false);
				}
				if(val[0].name == "TESTACCOUNTS" && val[0].status == "Yes"){
					$('.TESTACCOUNTS').prop('checked', true);
				}else if(val[0].name == "TESTACCOUNTS" && val[0].status == "No"){
					$('.TESTACCOUNTS').prop('checked', false);
				}
				if(val[0].name == "OTPNOTVERIFIED" && val[0].status == "Yes"){
					$('.OTPNOTVERIFIED').prop('checked', true);
				}else if(val[0].name == "OTPNOTVERIFIED" && val[0].status == "No"){
					$('.OTPNOTVERIFIED').prop('checked', false);
				}
				if(val[0].name == "EDIT" && val[0].status == "Yes"){
					$('.EDIT').prop('checked', true);
				}else if(val[0].name == "EDIT" && val[0].status == "No"){
					$('.EDIT').prop('checked', false);
				}
				if(val[0].name == "DELETE" && val[0].status == "Yes"){
					$('.DELETE').prop('checked', true);
				}else if(val[0].name == "DELETE" && val[0].status == "No"){
					$('.DELETE').prop('checked', false);
				}
				if(val[0].name == "CREATE_MESSAGES" && val[0].status == "Yes"){
					$('.CREATE_MESSAGES').prop('checked', true);
				}else if(val[0].name == "CREATE_MESSAGES" && val[0].status == "No"){
					$('.CREATE_MESSAGES').prop('checked', false);
				}
				if(val[0].name == "LATESTUPDATES" && val[0].status == "Yes"){
					$('.LATESTUPDATES').prop('checked', true);
				}else if(val[0].name == "LATESTUPDATES" && val[0].status == "No"){
					$('.LATESTUPDATES').prop('checked', false);
				}
				if(val[0].name == "LATESTNEWS" && val[0].status == "Yes"){
					$('.LATESTNEWS').prop('checked', true);
				}else if(val[0].name == "LATESTNEWS" && val[0].status == "No"){
					$('.LATESTNEWS').prop('checked', false);
				}
			});
		},
		error:function(dat){}
	});
	$('#adminUserModal').modal('show');
}
</script>
</body>
</html>