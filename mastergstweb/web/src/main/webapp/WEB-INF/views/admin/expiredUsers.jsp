<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Users</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" 	href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" 	media="all" />
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/reports/reports.css" 	media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/admin/admin.css" media="all" />
<!-- datepicker start -->
<script src="${contextPath}/static/mastergst/js/common/datetimepicker-inv.js" type="text/javascript"></script> 
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<script src="${contextPath}/static/mastergst/js/admin/usersDetail.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/admin/expiryusersDetail.js" type="text/javascript"></script>
<!-- datepicker end -->
<style>
.renual-message{overflow:auto}
.bodybreadcrumb{margin-top:-8px!important}
</style>
</head>
<jsp:include page="/WEB-INF/views/admin/userDetails.jsp" />
<body>
	<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
	<div class="bodywrap" style="min-height: 480px; padding-top: 10px">
		<!--- company info bodybreadcrumb start -->
		<div class="bodybreadcrumb">
			<div class="container">
				<div class="row">
					<div class="col-sm-12">
						<div class="bdcrumb-tabs">
							<ul class="nav nav-tabs" role="tablist">
								<li class="nav-item">Reports</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!--- company info bodybreadcrumb end -->
		<div class="container" style="padding-top: 30px;">
			<div class="row">
				<div class="col-sm-12">&nbsp;</div>
				<div class="col-sm-12" style="margin-top: 10px;">
					<a id="backpage_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark report mr-0 pull-right" role="button">BACK</a>
					<a id="backpage_lnk" class="btn btn-blue-dark report mr-2 pull-right d-none sendMeaasage-btn" data-toggle="modal" data-target="#sendMessageModal" role="button" onclick="sendMessage()">SEND MESSAGE</a>
					<a id="backpage_lnk" class="btn btn-blue-dark report mr-2 pull-right d-none sendMeaasageAll-btn" role="button" data-toggle="modal" data-target="#sendMessageModal" onclick="sendMessageAllUsers()">SEND MESSAGE ALL USERS</a>
				</div>
				<div class="col-sm-12">&nbsp;</div>
				<div class="customtable tabtable col-sm-12">
					<table id="dbTable_ALL_EXPIRED_USERS" class="display dataTable meterialform col-sm-12 p-0"  cellspacing="0" width="100%">
						<thead>
							<tr>
								<th>Name</th>
								<th>Email</th>
								<th>Phone</th>
								<th style="min-width:78px">User Type</th>
								<th>Amount</th>
								<th style="min-width:107px">Sub. Start Date</th>
								<th style="min-width:118px">Renewal Date</th>
								<th>Sub. Type</th>
								<th>Message</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		</div>
		
<!-- Modal Form start -->
<div class="modal fade" id="sendMessageModal" role="dialog" aria-labelledby="sendMessageModal" aria-hidden="true">
	<div class="modal-dialog modal-md modal-right" role="document" style="height: 100%;">
    	<div class="modal-content" id="idSendMessageBody" style="height: 100%;">
        	<div class="modal-body">
            	<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                	<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                </button>
                <div class="production-hdr bluehdr">
                	<h3 id="productionModalTitle">SENDING MEASSAGE ALL RENEWAL USERS</h3>
                </div>
				<div class="p-3">
				<div class="renual-message">
				<div class="form-group ">
    				<label for="exampleFormControlTextarea1"><b>User Name :</b> </label>
    				<b><span id="usersName"></span></b>
    				<input type="hidden" id="client_userid"/>
    				<input type="hidden" id="client_user_expirydate"/>
  				</div>
  				<div class="form-group ">
    				<label for="emailid"><b>Email id :</b> </label>
    				<b><span id="emailid"></span></b>
    				
  				</div>
  				<div class="form-group">
    				<h6><span id="errormsg" class="text-danger"></span></h6>
  				</div> 
  				<div class="form-group successmsg d-none">
    				<h6><i class="fa fa-check" style="font-size:32px;color:green"></i><span id="successmsg" class="text-success">Message Send successfully...</span></h6>
  				</div>
  				</div>
  				  				
  				<div class="form-group col-md-12 col-sm-12">
					<div class="row">
						<label class="col-md-2" for="emailSubject">Subject<span class="coln-txt">:</span></label>
						<div class="col-md-10" id="divcommentsdate">              				
	    					<select id="emailSubject" class="form-control">
	  							<option value>-select-</option>
	  							<optgroup label="ASP DEVELOPERS">
								    <option value="MasterGST API Subscription Expired">MasterGST API Subscription Expired</option>
								</optgroup>
							  	<optgroup label="NON ASP DEVELOPERS">
								    <option value="MasterGST - Your Subscription Expired">MasterGST - Your Subscription Expired</option>
								</optgroup>
							</select>
						</div>
					</div>
				</div>  				
				<div class="form-group">
    				<label for="emailMeassage">Message</label>
    				<textarea class="form-control" id="emailMeassage" style="width:98%; height:110px"></textarea>
  				</div>
  				<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Date<span class="coln-txt">:</span></label>
					<div class="col-md-8" id="divcommentsdate">          
						<input type="text" id="commentedDate" style="width:95%" class="form-control" name="commentsDate" aria-describedby="commentsDate" placeholder="DD-MM-YYYY" onmousedown="datepick();"><label for="commentsDate" class="control-label"></label>
					</div>
				</div>
			</div>	
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Added By<span class="coln-txt">:</span></label>
					<div class="col-md-8" id="divcommentsaddedby">          
						<input class="form-control addedby" style="width:95%" id="exaddedby" placeholder="Enter Name">
					</div>
				</div>
			</div>
			</div>
			</div>
			</div>
			<div class="modal-footer">			  
				<button type="button" class="btn  btn-blue-dark save-btn" id="mail_send_btn" onclick="sendMessages()">Send</button>
				<button type="button" class="btn btn-blue-dark close-bttn" data-dismiss="modal">Close</button>
			</div>
		</div>					
	</div>
</div>
<!-- Modal Form end -->		
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp"%>
<!-- footer end here -->
	
<script type="text/javascript">
var expiryTable;
	$(document).ready(function() {
		$('#reports_lnk').addClass('active');
				
		loadExpiryUsersDetails('<c:out value="${id}"/>', 'ALL_EXPIRED_USERS');
	});
	
</script>
<script type="text/javascript">
var selUsersArray=new Array();
var client_userExpiryDate;
function sendEmail(id,name,email,expirydate){
	$('#client_userid').val('');
	$('#client_user_expirydate').val('');
	$('#usersName').text('');
	$('#emailid').text('');
	$('#client_userid').val(id);
	$('#client_user_expirydate').val(expirydate);
	$('#usersName').text(name);
	$('#emailid').text(email);
}
</script>
<script type="text/javascript">
$(".modal").on("hidden.bs.modal", function(){
    $("#emailSubject").val("");
    $("#emailMeassage").val("");
    $("#commentedDate").val("");
    $("#addedby").val("");
}); 
function datepick() {
	$('#commentedDate').datetimepicker({
	  	timepicker: false,
	  	format: 'd-m-Y',
	  	maxDate: 0,
	  	scrollMonth: true
	});
}


function sendMessages(){
	
	var emailSubject=$('#emailSubject').val();
	var emailMeassage=$('#emailMeassage').val();
	var commentedDate=$('#commentedDate').val();
	var exaddedby=$('#exaddedby').val();
	var userid=$('#client_userid').val();
	if(exaddedby =="" || commentedDate =="" || emailMeassage =="" || emailSubject ==""){
			$('#errormsg').text('Please Fill Required Fields');
	}else{
		var commentsData=new Object();
		var comments = emailSubject+"#MGST#"+emailMeassage;
		var createdate=commentedDate;
		var addedby= exaddedby;
		$('#remindersErrorMsg').html('');
		commentsData.comments=comments;
		commentsData.commentDate=commentedDate;
		commentsData.addedby=exaddedby;
		$('#errormsg').text('');
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : '${contextPath}/sendEmail/'+userid,
			data : JSON.stringify(commentsData),
			dataType : 'json',
			success : function(data) {
					$('#emailMeassage').val('');
					$('#commentedDate').val('');
					$('#exaddedby').val('');
					$('#emailSubject').val('');
					$('.successmsg').removeClass('d-none');
					$('#mail_send_btn').addClass('d-none');
					$('#successmsg').text('Message Send successfully...');
					/* //For wait 5 seconds
					  setTimeout(function() 
					  {
					    location.reload();  //Refresh page
					  }, 5000); */
				},
				error:function(dat){
					$('#emailMeassage').val('');
					$('#commentedDate').val('');
					$('#exaddedby').val('');
					$('#emailSubject').val('');
					$('.successmsg').removeClass('d-none');
					$('#mail_send_btn').addClass('d-none');
					$('#successmsg').text('Message Send successfully...');
				}
			});
		}
	}
</script>
<script type="text/javascript">
$(document).ready(function(){
	
	$('#emailSubject').on('change', function(){
		$('#emailMeassage').val('');
		var expirydate=$('#client_user_expirydate').val();
		$('#emailMeassage').val('Thank you being part of MasterGST, we have been noticed your subscription is Expired on '+expirydate+'. This is to inform you that your services are barred. please do renewal to enable the services');	
	});
});
</script>
</body>
</html>
