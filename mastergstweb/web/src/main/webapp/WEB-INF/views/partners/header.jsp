<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/select2.min.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/socials/jssocials.css">
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/socials/jssocials-theme-minima.css">    
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<script src="${contextPath}/static/mastergst/js/socials/jssocials.min.js" type="text/javascript"></script> 
<script src="${contextPath}/static/mastergst/js/partners/invitation.js" type="text/javascript"></script>
<style>
.navbar-db.navbar-inverse .navbar-nav .nav-link {padding: 11px 2px;}
</style>
</head>
<body>
	<!-- header page begin -->
    <nav class="navbar navbar-toggleable-md navbar-inverse fixed-top bg-inverse navbar-db nav-ca">
       <div class="container"> <!-- toggle button for device begin -->
        <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation"> <span class="navbar-toggler-icon"></span> </button>
        <!-- toggle button for device end -->
        <a class="navbar-brand" href="#"> <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" alt="Master Gst" /> </a>
        <div class="collapse navbar-collapse" id="navbarCollapse">
            <div class="mainnav-wrap">
                <!-- main menu begin -->
                <ul class="navbar-nav mainnav-left db-ca">
					<li class="nav-item"> <a id="nav_invitation" class="nav-link" href="${contextPath}/pinvit/<c:out value="${id}"/>/<c:out value="${fullname}"/>"> Invitations </a> </li>
                    <li class="nav-item"> <a id="nav_client" class="nav-link" href="${contextPath}/pcnt/<c:out value="${id}"/>/<c:out value="${fullname}"/>?type=My Clients">My Clients </a> </li>
                    <li class="nav-item"> <a id="nav_client_notrequired" class="nav-link" href="${contextPath}/pcnt/<c:out value="${id}"/>/<c:out value="${fullname}"/>?type=Not Required">Not Required</a> </li>
                    <li class="nav-item"> <a id="nav_billing" class="nav-link" href="${contextPath}/pbil/<c:out value="${id}"/>/<c:out value="${fullname}"/>">Revenue </a> </li>
					<li class="nav-item"> <a id="nav_bankDetails" class="nav-link" href="${contextPath}/pbankDetails/<c:out value="${id}"/>/<c:out value="${fullname}"/>">Bank Details </a> </li>
                </ul>
                <!-- main menu end -->
                <!-- right side menu begin -->
                <ul class="navbar-nav mainnav-right">
                	  <li class="nav-item dropdown">
			             <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false"> <span class="dbspriticons notifybell"></span><span class="badge blue badge-circle">${fn:length(messages)}</span> </a>
			              <c:if test="${not empty messages}"> 
				             <div class="dropdown-menu  dropdown-menu-right">
				             <c:forEach items="${messages}" var="message">
				             	<a class="dropdown-item" href="#">${message.subject}</a> 
				             </c:forEach>
				            </div>
			            </c:if>
			         </li>
                    <c:set var = "varname" value = "${fn:split(fullname, ' ')}"/>
                    <li class="nav-item dropdown" style="min-width: 220px;">
                        <a class="nav-link dropdown-toggle p-0 pl-1 pt-1" style="height:100%;" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false"> <span class="rounded-circle img-sm-rounded">${fn:substring(varname[0], 0, 1)}<c:if test="${fn:length(varname) gt 1}">${fn:substring(varname[1], 0, 1)}</c:if>
						</span> <span class="user-name"><c:choose><c:when test='${fn:length(fullname) > 25}'><c:out value="${fn:substring(fullname, 0, 25)}.." /></c:when><c:otherwise><c:out value="${fullname}" /></c:otherwise></c:choose> <span class="dbspriticons ddarw"></span></span>
						<span class="lastloggedin">Last Logged In : <b><span id="usrloggedin"></span></b></span></a>
                        <div class="dropdown-menu dropdown-menu-right">
						<ul class="profilelist">
							<li class="rowbdr">
								<div class="icon-l"><span class="userimg"><img src="${contextPath}/static/mastergst/images/credentials/user1.jpg" alt="User" class="circle" /></span> </div>
								<div class="icon-r"><span class="usertxt">${fullname}</span> </div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon1"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserMail"></span> </div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon2"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserCell"></span> </div>
							</li>
							<!-- <li>
								<div class="icon-l"><span class="cred-sprite credicon3"></span> </div>
								<div class="icon-r"><span class="icontxt">Hyderabad</span> </div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon4"></span> </div>
								<div class="icon-r"><span class="icontxt">Telangana</span> </div>
							</li> -->
							<li>
								<div class="icon-l"><span class="cred-sprite credicon5"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserAddr"></span> </div>
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
    <div class="bodywrap">
        <!--- company info bodybreadcrumb start -->
        <div class="bodybreadcrumb">
            <div class="container">
                 <div class="row">
		               <div class="col-sm-12">
									 <div class="partner-id">Partner ID :&nbsp; <span><c:out value="${user.userSequenceid}"/></span></div>
									 <button class="btn btn-danger btn-sm pull-right ml-2" type="button" onclick="leadorBusineess('lead')">Add Lead </button>
									  <button class="btn btn-greendark btn-sm pull-right" type="button"  onclick="leadorBusineess('business')">Invite  a Business </button>
						<div class="navbar-left float-r">
					     <ul>
								<li class="nav-item dropdown">
					        		<a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Share with social media </a>
					        		<div class="dropdown-menu" style="background-color: white;" aria-labelledby="navbarDropdownMenuLink" id="idSocials"></div>
					      		</li>
						      <li class="nav-item">
						        <a class="nav-link" href="#" onclick="copyToClipboard('${id}')">Copy Link</a>
						      </li>
			    		</ul>
		   			 </div>
				</div>
               </div>
            </div>
        </div>
        <!--- company info bodybreadcrumb end -->
        <!--- breadcrumb start -->
		<!--		<div class="container">
    <div class="row">
        <div class="col-md-12 col-sm-12">
            <div class="breadcrumbwrap">
                <ol class="breadcrumb">
                <li class="breadcrumb-item active">Dashboard</li>
            </ol>
						<span class="datetimetxt"> 
              <input type="text" class="form-control" id="datetimepicker" placeholder="05/2017" /><i class="fa fa-sort-desc"></i>  
         </span>
            </div>
        </div>
        
    </div>
</div> -->
         

        <!--- breadcrumb end -->

 <!-- inviteBusinessModal Start -->
<div class="modal fade" id="inviteBusinessModal" role="dialog" aria-labelledby="inviteBusinessModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content" style="height:100vh;">
       <div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3 id="leadorBusinessTxt">Invite a Business</h3>
                    </div>
				</div>
      <div class="modal-body meterialform popupright bs-fancy-checks">
			 <div class="row pl-4 pr-4 pt-4 pb-0">
                        <form:form method="POST" data-toggle="validator" id="businessinvite" class="meterialform row pr-4 pl-4" name="userform" action="${contextPath}/paddclient/${id}/${fullname}" modelAttribute="client"> 
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Customer Name</p>
                            <span class="errormsg" id="customerName_Msg"></span>
                            <input type="text" id="customerName" name="name" required="required" placeholder="Rajesh" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Customer Email </p>
                            <span class="errormsg" id="customerEmail_Msg"></span>
                            <input type="text" id="customerEmail" name="email" required="required" placeholder="rajesh@gmail.com" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Phone</p>
                            <span class="errormsg" id="phone_Msg"></span>
                            <p class="indiamobilecode">+91</p><input type="text" id="mobileId" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" required="required" placeholder="9848012345" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">User Type</p>
                            <select class="form-control" name="clienttype" id="addcurrencyCode">
							<option value="">--Select--</option>
							<option value="cacmas">CA/CMA/CS/TAX PROFESSIONAL</option>
							<option value="suvidha">SUVIDHA CENTERS</option>
							<option value="business">SMALL/MEDIUM BUSINESS</option>
							<option value="enterprise">ENTERPRISE</option>
							<option value="partner">PARTNER</option>
							<option value="aspdeveloper">ASP/DEVELOPER</option>
							</select>
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>
							 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Estimated Cost </p>
                            <input type="text" id="estimatedCost" name="estimatedCost"  placeholder="123456" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>
							
							<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">State </p>
                            <input type="text" id="state" name="state" value="" />
                            <label for="state" class="control-label"></label>
								<div class="help-block with-errors"></div>
								<div id="stateempty" style="display:none">
									<div class="ddbox">
									  <p>Search didn't return any results.</p>
									</div>
								</div>
								<i class="bar"></i></div>
								<div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt">City </p>
		                            <input type="text" id="city" name="city"  value="" />
		                            <label for="input" class="control-label"></label>
		                            <i class="bar"></i>
		                       </div>	
		                       <div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt">Industry/Vertical </p>
		                            <input type="text" id="industryType" name="industryType"  value="" />
		                            <label for="input" class="control-label"></label>
		                            <div class="help-block with-errors"></div>
								<div id="industryTypeempty" style="display:none">
									<div class="ddbox">
									  <p>Search didn't return any results.</p>
									</div>
		                       </div><i class="bar"></i></div>
		                      	 <div class="form-group col-md-6 col-sm-12">
									<p class="lable-txt">Sales Status</p>
									<select class="form-control" id="salesStatus" name="salesstatus">
										<option value="" selected>-select-</option>
										<option value="Call Not Lift">Call Not Lift</option>
										<option value="Duplicate">Duplicate</option>
										<option value="Not Required">Not Required</option>
										<option value="Ready to Go">Ready to Go</option>
										<option value="Ready to Pay">Ready to Pay</option>
										<option value="Yet to Take Decision">Yet to Take Decision</option>
										<option value="Test Account">Test Account</option>
										<option value="Pricing Issue">Pricing Issue</option>
										<option value="Sandbox Testing">Sandbox Testing</option>
										<option value="Closed">Closed</option>
									</select>
									<label for="input" class="control-label"></label><i class="bar"></i>
								</div>
		                      
									 <div class="form-group col-md-6 col-sm-12">
									 <p class="lable-txt">Product Type</p>
									  <select id="productType" class="mt-1" name="productType" multiple value="" >
									  <c:forEach items="${productTypes}" var="type">
									  <option value="${type}">${type}</option>
									  </c:forEach> 
									  </select>
									  <label for="input" class="control-label"></label><i class="bar"></i> </div>
									  
									  <div class="form-group col-md-6 col-sm-12">
							  		<div class="form-group col-md-12 col-sm-12 ">
			                            <p class="lable-txt col-md-7 pl-0 pr-0" style="display:inline-block;">Demo Status </p>
			                            <div class="form-check form-check-inline ml-3">
		                                    <input class="form-check-input" type="checkbox" id="demostatus" name="demostatus" value=""/>
		                                    <label for="demostatus"><span class="ui"></span></label>
	                                	</div>
	                                </div>
	                                <div class="form-group col-md-12 col-sm-12">
		                            <p class="lable-txt col-md-7 pl-0 pr-0" style="display:inline-block;">Need To FollowUp </p>
		                            <div class="form-check form-check-inline ml-3">
	                                    <input class="form-check-input" type="checkbox" id="followup" name="needFollowup" value=""/>
	                                    <label for="followup"><span class="ui"></span>
	                                    </label> <!-- <span class="labletxt" style="margin-top:0px!important">Active</span> -->
                                	</div>
		                       </div>	
		                       </div>
								<div class="form-group col-md-6 col-sm-12" id="followupdateDiv" style="display:none;">
		                            <p class="lable-txt">FollowUp Date</p>
		                            <input type="text" id="needFollowupdate" name="needFollowupdate" placeholder="dd/MM/YYYY" value="" />
		                            <label for="input" class="control-label"></label>
		                            <i class="bar"></i>
                             </div>	
						<div class="form-group col-md-12 col-sm-12 mb-2">
                            <p class="lable-txt astrich">Description</p>
                            <span class="errormsg" id="paidAmount_Msg"></span>
                            <textarea  id="description" name="content" required="required" placeholder="I have noticed MasterGST is so simple and easy to use and file GST returns, I highly recommend this GST Software. Please click on the link to signup." ></textarea>
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div> 
								
					  </div>
<input type="hidden" name="userid" value="<c:out value="${id}"/>">
				<input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
				<input type="hidden" name="isLead"  id="isLead" value=""/>
				<input type="submit" class="btn btn-blue-dark mb-3 hidden customer_submit" id="customersave" style="display:none" value="Invite"/>
					</form:form>
      </div>
      	<div class="modal-footer text-center mt-0 pt-0" style="display:block">
				<label for="customer_submit" class="btn btn-blue-dark m-0" id="inviteBtnTxt" tabindex="0" onclick="labelcustsubmit()">Invite</label>
				<!--<input type="submit" class="btn btn-blue-dark" value="Invite"/>-->
				<a type="button" class="btn btn-blue-dark" data-toggle="modal" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">Close</span></a>
		</div>
       
    </div>
  </div>
</div>
<!-- inviteBusinessModal End -->
 <!-- inviteBusinessSucsesModal Start -->
<div class="modal fade modal-right" id="inviteBusinessSucsesModal" role="dialog" aria-labelledby="inviteBusinessModal" aria-hidden="true">
  <div class="modal-dialog modal-sm" role="document">
    <div class="modal-content">
      <div class="modal-body meterialform popupright"><button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
        </button>
      <div class="invoice-hdr bluehdr"><h3>Invite a Business<br /> </h3> </div>
			  <div class="row  p-5">
					<div class="alert alert-info m-auto text-center">
						<h5>Invited in a Business Successfully.</h5>
					</div>
					<div class="col-12">&nbsp;</div>
						<div class="clearfix col-12 mt-4 text-center">
							<a type="button" class="btn btn-blue-dark" data-toggle="modal" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">Close</span></a>
						</div>
			</div>
      </div>
       
    </div>
  </div>
</div>
<!-- inviteBusinessSucsesModal End -->
<script type="text/javascript"> 
var contextPath='<c:out value="${contextPath}"/>';var userId='<c:out value="${id}"/>';
$('#productType').select2();
$(document).ready(function(){
	loadInvitationsTable('${id}','${fullname}');
	
	var date = new Date();
	var month = date.getMonth()+1;
	var dateValue = ((''+month).length<2 ? '0' : '') + month + '/' + date.getFullYear();
	var date = $('#datetimepicker').datepicker({
		viewMode: 1,
		minViewMode: 1,
		format: 'mm-yyyy'
	}).on('changeDate', function(ev) {
		$('.datepicker').hide();
	});
	$('#needFollowupdate').datepicker({
		format: 'dd/mm/yyyy',
		
	});
	$.ajax({
		url: "${contextPath}/getuser?userId=${id}",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(user) {
			if(user) {
				$('#acctUserMail').html(user.email);
				$('#acctUserCell').html(user.mobilenumber);
				$('#acctUserAddr').html(user.address);
				$('#usrloggedin').html(user.usrLastLoggedIn);
			}
		}
	});

	$("#idSocials").jsSocials({
		url: 'http://app.mastergst.com/invsignup?subscrid=&inviteId=${id}',
		text: 'I have noticed MasterGST is so simple and easy to use and file GST returns, I highly recommend this GST Software. Please click on the link to signup.',
		logo: '<img src="${contextPath}/static/mastergst/images/master/logo-mastergst.jpg" />',
		shareIn: "popup",
		showCount: "inside",
		shares: ["twitter", "facebook", "googleplus", "linkedin", "pinterest", "stumbleupon"]
	});
});
	$('#admin1').change(function() {
				var status = $(this).is(':checked');
				$(this).val(status);
			});
function labelcustsubmit(){
	$('#inviteBtnTxt').addClass('disable');
	$('#businessinvite').submit();
}
function copyToClipboard(id) {
  var $temp = $("<input>");
  $("body").append($temp);
  $temp.val("http://app.mastergst.com/signupall?subscrid=&inviteId="+id).select();
  document.execCommand("copy");
  $temp.remove();
}
function leadorBusineess(type){
	$('#inviteBusinessModal').modal("show");
	$('#partner_id').remove();
	$('#customerName').val('');
	$('#customerEmail').val('');
	$('#mobileId').val('');
	$('#addcurrencyCode').val('');
	$('#estimatedCost').val('');
	$('#description').val('');
	$('#isLead').val('');
	$('#demostatus,#needFollowupdate').val('');
	$('#followupdateDiv').css("display","none");
	$('#followup,#demostatus').prop("checked",false);
	if(type == "lead"){
		$('#isLead').val("true");
		$('#leadorBusinessTxt').html("ADD LEAD");
		$('#inviteBtnTxt').html("ADD");
	}else{
		$('#isLead').val("false");
		$('#leadorBusinessTxt').html("Invite a Business");
		$('#inviteBtnTxt').html("INVITE");
	}
}
$('#followup').change(function() {
	if($('#followup').is(':checked')){
		$('#followup').val("true");
		$('#followupdateDiv').css("display","block");
	}else{
		$('#followup').val("false");
		$('#followupdateDiv').css("display","none");
	}
	$('#needFollowupdate').val("");
});
$('#demostatus').change(function() {
	if($('#demostatus').is(':checked')){
		$('#demostatus').val("true");
	}else{
		$('#demostatus').val("false");
	}
});

</script>
