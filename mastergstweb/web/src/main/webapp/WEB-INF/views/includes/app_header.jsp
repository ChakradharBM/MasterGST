<!-- header page begin -->
<nav class="navbar navbar-toggleable-md navbar-inverse fixed-top bg-inverse navbar-db">
  <div class="container"><!-- toggle button for device begin -->
  <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation"> <span class="navbar-toggler-icon"></span> </button>
  
  <!-- toggle button for device end -->
  <a class="navbar-brand"href="https://www.mastergst.com" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" alt="Master Gst" /> </a>
  <div class="collapse navbar-collapse" id="navbarCollapse">
    <div class="mainnav-wrap">
      <!-- main menu begin -->
      <ul class="navbar-nav mainnav-left">
  <c:if test="${otheruser=='no'}">
  <c:choose>
    <c:when test="${headerflag=='apis'}">
       <li class="nav-item"> <a class="nav-link" href="${contextPath}/dashboard?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&hour=Hour&hdvalue=1&usertype=<c:out value="${usertype}"/>&dashboardType=gst"> dashboard </a> </li>
        <li class="nav-item"> <a class="nav-link active" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=gst"> api </a> </li>
        <li class="nav-item"> <a class="nav-link" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=gst"> credentials </a> </li>
    </c:when>    
	<c:when test="${headerflag=='credentials'}">
       <li class="nav-item"> <a class="nav-link" href="${contextPath}/dashboard?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&hour=Hour&hdvalue=1&usertype=<c:out value="${usertype}"/>&dashboardType=gst"> dashboard </a> </li>
        <li class="nav-item"> <a class="nav-link" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=gst"> api </a> </li>
        <li class="nav-item"> <a class="nav-link active" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=gst"> credentials </a> </li>
    </c:when>
    <c:when test="${headerflag=='dashboard'}">
      <li class="nav-item"> <a class="nav-link active" href="${contextPath}/dashboard?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&hour=Hour&hdvalue=1&usertype=<c:out value="${usertype}"/>&dashboardType=gst"> dashboard </a> </li>
        <li class="nav-item"> <a class="nav-link" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=gst"> api </a> </li>
        <li class="nav-item"> <a class="nav-link" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=gst"> credentials </a> </li>
    </c:when>
    <c:otherwise>
        <li class="nav-item"> <a class="nav-link" href="${contextPath}/dashboard?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&hour=Hour&hdvalue=1&usertype=<c:out value="${usertype}"/>&dashboardType=gst"> dashboard </a> </li>
        <li class="nav-item"> <a class="nav-link" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=gst"> api </a> </li>
        <li class="nav-item"> <a class="nav-link" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=gst"> credentials </a> </li>
    </c:otherwise>
</c:choose>
	<jsp:useBean id="now" class="java.util.Date" />
	<li class="nav-item"> <a id="nav-billing" class="nav-link" href="${contextPath}/dbllng/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<%=MasterGSTConstants.ASPDEVELOPER%>/<fmt:formatDate value="${now}" pattern="MM" />/<fmt:formatDate value="${now}" pattern="yyyy" />"> Billing </a> </li>
 </c:if>	
 <c:if test="${otheruser=='yes'}">
 <li class="nav-item"> <a class="nav-link active" href="#"> dashboard </a> </li>
  </c:if>
	   </ul>
	  
        
     
      <!-- main menu end -->
      <!-- right side menu begin -->
      <c:set var = "varname" value = "${fn:split(fullname, ' ')}"/>
      <ul class="navbar-nav mainnav-right" style="z-index: 999">
        <li class="nav-item dropdown">
             <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false"> <span class="dbspriticons notifybell"></span><span class="badge blue badge-circle">${fn:length(messages)}</span> </a>
              <c:if test="${not empty messages}"> 
	             <div class="dropdown-menu  dropdown-menu-right">
	             <c:forEach items="${messages}" var="message" varStatus="loop">
	             	<a class="dropdown-item" id="message${loop.index}" href="${contextPath}/messages?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&messageId=<c:out value="${message.msgId}"/>&usertype=<c:out value="${usertype}"/>">${message.subject} <span class="pull-right" style="font-size:12px!important"><fmt:formatDate value="${message.createdDate}" pattern="MMM dd yyyy" /></span></a> 
					<div class="bdr" id="border${loop.index}" style="border-bottom: 1px solid lightgray;"></div>
<%-- 	             	<a class="dropdown-item" href="#">${message.subject}</a>  --%>
	             </c:forEach>
				 <a class="dropdown-item text-right" id="seemore" <c:if test="${messageCount le 5}">style="display:none"</c:if> href="${contextPath}/messages?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&messageId=<c:out value="${message.msgId}"/>&usertype=<c:out value="${usertype}"/>">see more...</a> 
	            </div>
            </c:if>
         </li>
        <li class="nav-item dropdown" style="min-width: 220px;"> <a class="nav-link dropdown-toggle p-0 pl-1 pt-1" style="height:100%;" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false"> <span class="rounded-circle img-sm-rounded">${fn:substring(varname[0], 0, 1)}<c:if test="${fn:length(varname) gt 1}">${fn:substring(varname[1], 0, 1)}</c:if></span> <span class="user-name"><c:choose><c:when test='${fn:length(fullname) > 15}'>${fn:substring(fullname, 0, 25)} ...</c:when><c:otherwise>${fullname}</c:otherwise></c:choose><span class="dbspriticons ddarw"></span></span>
        <span class="lastloggedin">Last Logged In : <b><span id="usrloggedin"></span></b></span>
        </a>
          <div class="dropdown-menu dropdown-menu-right">
				<ul class="profilelist">
					<li class="rowbdr row">
						<div class="col-md-3" style="display: inline-block;">
						<div class="icon-l"><span class="userimg"><img src="${contextPath}/static/mastergst/images/credentials/user1.jpg" alt="User" class="circle" /></span> </div></div>
						<div class="col-md-9" style="display: inline-block;vertical-align: top;">
						<div class="icon-r"><span class="usertxt">${fullname}</span> </div>
						</div>
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
					<li><a class="btn btn-blue-dark btn-sm pull-left" data-toggle="modal" data-target="#profileViewModal">View Profile</a><a class="btn btn-blue-dark btn-sm pull-right" href="${contextPath}/logout">Logout</a>   </li>
				</ul>
			</div>
        </li>
      </ul>
      <!-- right side menu end -->
    </div>
  </div>
</div>
</nav>
<!-- notification msg in app -->
<div class="alert alert-danger gst-notifications" id="asp-error-box" role="alert"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2" onclick="notifi_disp()"/><span id="errorMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2" onclick="notifi_disp()" />
</div>
<div class="modal fade" id="subnowmodal" role="dialog" aria-labelledby="subnowmodal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content" id="subnowmodal">
                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3 id="importModalTitle">Payment Process ( Offline)</h3>
                    </div>
                    <!-- row begin -->
					<div class="p-3">
					
					<h5 style="color: #344371;">Payment Methods</h5>
					<ul>
					<li>Payment to be done by cheque drawn in favor of <b>TERA SOFTWARE LTD</b>.</li>
					<li>RTGS / NEFT / IMPS ( Online Transfer)</li>
					<li>Cash Deposit</li>
					</ul>
					<h5 style="color: #344371;" class="mt-4">Bank Details</h5>
					<div class="row">
					<div class="col-md-4">
					<ul>
					<li>Bank Name </li>
					<li>Bank Branch Name </li>
					<li>Account Name </li>
					<li>Account Number</li>
					<li>IFSC </li>
					</ul>
					</div>
					<div class="col-md-8">
					<ul style="list-style:none">
					<li>: BANK OF MAHARASTRA </li>
					<li>: SULTAN BAZAR, HYDERABAD</li>
					<li>: TERA SOFTWARE LTD</li>
					<li>: 20003953280</li>
					<li>: MAHB0000019</li>
					</ul>
					</div>
					</div>
					
					<p style="color: #344371; background-color:#f5c6c2!important;border-color:#f5c6c2!important" class="mt-4 alert alert-info" ><b>Note : </b>As soon as you make a payment using one of the offline methods, Please E-mail us <a href="mailto:Sales@MasterGST.com">Sales@mastergst.com</a>giving details of payment made, screenshot and the purpose of the payment.</p>
					</div>
					
        </div>
		<div class="modal-footer">
					<a href="#" class="btn btn-blue-dark"  data-dismiss="modal" aria-label="Close">Close</a>
					</div>
    </div>
	
    </div>
	</div>

<!-- notification msg in app end -->
	<!-- profileEdit Modal Start -->
<%@include file="/WEB-INF/views/includes/profileeditmodal.jsp" %>
<!-- profileEdit Modal End -->
<!-- header page end -->
<script type="text/javascript"> 
$(document).ready(function(){
	for(i = 5; i < 1000; i++){
		$('#message'+i).css("display","none");
		$('#border'+i).css("display","none");
	}
	
	var subscriptionStatus = '<c:out value="${subscriptionStatus}"/>';
	var dashboardType = '<c:out value="${dashboardType}"/>';
	var apiType = '<c:out value="${apiType}"/>';
	var creType = '<c:out value="${creType}"/>';
	var dayss = '<c:out value="${days}"/>';
	if(subscriptionStatus == 'Expired'){
		if(dashboardType == 'gst'){
			$('#errorMessage').html('Your <span style="color:#3678d8">GST API Subscription is Expired</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.<a type="button" class="btn btn-blue-dark pull-right" data-toggle="modal" data-target="#subnowmodal" style="padding:5px 9px; margin-right:8%">Pay Now</a>');
		}else if(dashboardType == 'eway'){
			$('#errorMessage').html('Your <span style="color:#3678d8">EwayBill API Subscription is Expired</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.<a type="button" class="btn btn-blue-dark pull-right" data-toggle="modal" data-target="#subnowmodal" style="padding:5px 9px; margin-right:8%">Pay Now</a>');
		}else if(dashboardType == 'einv'){
			$('#errorMessage').html('Your <span style="color:#3678d8">E-Invoice API Subscription is Expired</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.<a type="button" class="btn btn-blue-dark pull-right" data-toggle="modal" data-target="#subnowmodal" style="padding:5px 9px; margin-right:8%">Pay Now</a>');
		}
		$('#errorMessage').parent().show();
	}else if(subscriptionStatus == 'About to Expire'){
		if(dashboardType == 'gst'){
			$('#errorMessage').html('Your <span style="color:#3678d8">GST API Subscription is about to <b>expire in '+dayss+' days</b></span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
		}else if(dashboardType == 'eway'){
			$('#errorMessage').html('Your <span style="color:#3678d8">EwayBill API Subscription is about to <b>expire in '+dayss+' days</b></span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
		}else if(dashboardType == 'einv'){
			$('#errorMessage').html('Your <span style="color:#3678d8">E-Invoice API Subscription is about to <b>expire in '+dayss+' days</b></span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
		}
		$('#errorMessage').parent().show();
	}
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
				$('#EmailId').html(user.email);
				$('#MobileNumber').html(user.mobilenumber);
				$('#Address').html(user.address);
				$('#usrloggedin').html(user.usrLastLoggedIn);
				if(user.userkeys) {
					var te = 0;
					user.userkeys.forEach(function(userkey) {
						if(!userkey.isenabled) {
							if(te>0){
								if((userkey.stage == 'Production' || userkey.stage == 'Sandbox') && (dashboardType == 'gst' || apiType == 'gst' || creType == 'gst')){
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">GST Sandbox and Production keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}else if((userkey.stage == 'EInvoiceProduction' || userkey.stage == 'EInvoiceSandBox')&& (dashboardType == 'einv' || apiType == 'einv' || creType == 'einv')){
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">e-Invoice Sandbox and E-Invoice Production keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}else if((userkey.stage == 'EwayBillProduction' || userkey.stage == 'EwayBillSandBox')&& (dashboardType == 'eway' || apiType == 'eway' || creType == 'eway')){
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">Eway Bill Sandbox and Eway Bill Production keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}
							}else{
								if(userkey.stage == 'Production' && (dashboardType == 'gst' || apiType == 'gst' || creType == 'gst')) {
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">GST Production keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}else if(userkey.stage == 'EwayBillSandBox' && (dashboardType == 'eway' || apiType == 'eway' || creType == 'eway')){
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">Eway Bill Sandbox keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}else if(userkey.stage == 'EwayBillProduction' && (dashboardType == 'eway' || apiType == 'eway' || creType == 'eway')){
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">Eway Bill Production keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}else if(userkey.stage == 'Sandbox' && (dashboardType == 'gst' || apiType == 'gst' || creType == 'gst')){
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">GST Sandbox keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}else if(userkey.stage == 'EInvoiceSandBox' && (dashboardType == 'einv' || apiType == 'einv' || creType == 'einv')){
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">e-Invoice Sandbox keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}else if(userkey.stage == 'EInvoiceProduction'&& (dashboardType == 'einv' || apiType == 'einv' || creType == 'einv')){
									$('#errorMessage').html('Your Access to the <span style="color:#3678d8">e-Invoice Production keys are disabled</span>, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
								}
							}
							te++;
						}
					});
					if(te>0){
					
						if(creType != 'authorizationKeys'){
							
							$('#errorMessage').parent().show();
						}
					}
				}
			}
		}
	});
});
   
   function profileSaveDetails(){
	   if(profileEditValidate()){
		var userDetails = new Object;
		var address = $('#useraddress').val();
		var mobilenumber = $('#usermobilenumber').val();
		$('.user_details_field').each(function() {
			var field = $(this).attr('data');
			if(field != 'createdate') {
				userDetails[field]=$('#user'+field).val();
			}
			$(this).html(userDetails[field]);
		});
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : '${contextPath}/updteprofileuserdtls?id=${id}',
				data : JSON.stringify(userDetails),
				dataType : 'json',
				success : function(dat) {
					$('#acctUserCell').text(mobilenumber);
					$('#acctUserAddr').text(address);					
					$('#profile_save_btn').css('display','none');
					$('#profile_edit_btn').css('display','block');
				}
			});
	   }
	}
	/* notification display none function*/
	function notifi_disp(){
		$(".gst-notifications").css("display","none")
	}
	/* notification display none function end*/
</script>
<style>.navbar-left{margin-left:0px!important}</style>