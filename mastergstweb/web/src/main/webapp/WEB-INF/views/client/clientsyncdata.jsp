<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | Add GSTN | Add Client | Add Business</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js"
	type="text/javascript"></script>
<style>
.progress-bar.progress-bar-striped.progress-bar-animated{position: absolute; top: 49%;z-index: 9999;}
#sync_data tr th:first-child {background-color: white !important;color: #5769bb;}
#syncfinancialYear{height: 32px;vertical-align: top;}
#syn_data_btn{padding: 5px 7px;}
#with_out_syn_btn{padding: 6.5px 7px!important;}
.bodybreadcrumb.main {top: 43px;}
#syncdataTable tbody tr td{text-align:center;}
#addclntgstnno, #pannumber, #lastName {text-transform: uppercase;}
.db-ca-wrap {padding-top: 0px !important}
.btn.btn-blue:hover {background-color: #1dc2f8;}
#syncdataTable{width:80%!important;}
.invoice_otp::-webkit-input-placeholder {font-size:50px;}
#otpboxwrap .formbox { padding:50px; }
#otpboxwrap .formbox .btn-blue-dark { margin-left:10px }
.otp_form_input { margin:30px auto 40px; }
.otp_form_input input[type='text'] { border-radius:0; border: none; border-bottom: 3px solid #4664be; width: 20%; display: inline-block; margin-left: 7px; text-align: center; font-size:50px ; margin-bottom:15px	}
</style>
</head>
<body>
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>	
	<div class="db-ca-wrap" style="margin-top: 86px !important">
		<div class="container">
			<div class="row">
				<!-- meterialform begin -->
				<div class="bg-white">
					<div class="col-11 m-auto">
						<h4 class="text-center text-success mb-2">"${clientname}"&nbsp;added successfully.</h4>
						<p class="text-center mb-2">Please Click on "<b>Sync and Continue</b>" button to download all the data from GSTN till the date</p>
					</div>
					<div class="text-center">
						<b style="vertical-align: inherit;">Choose the Financial Year to Sync</b> : <select class="" name="financialYear" id="syncfinancialYear">
							<option value="2021" selected>2021-2022</option>
							<option value="2020">2020-2021</option>
							<option value="2019">2019-2020</option>
							<option value="2018">2018-2019</option>
							<option value="2017">2017-2018</option>
						</select>&nbsp;
						<c:choose>
							<c:when test="${otpcheck eq 'OTP_NOT_VERIFIED' || otpcheck eq 'OTP_EXPIRED'}">
								<a class="btn btn-success disable syn_data_btn" id="syn_data_btn" onclick="syncwithcontinue('${id}','${fullname}','${usertype}','${clientid}')">Sync With GSTN</a>
							</c:when>
							<c:otherwise>
								<a class="btn btn-success" id="syn_data_btn" onclick="syncwithcontinue('${id}','${fullname}','${usertype}','${clientid}')">Sync With GSTN</a>
							</c:otherwise>
						</c:choose>
						<a href="${contextPath}/ccdb/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=initial" id="with_out_syn_btn" class="btn btn-blue-dark" style="padding: 9px;">Client Company Dash board</a>
					</div>
					<div></div>
					<div class="clearfix">&nbsp;</div>
					<!--  table div start -->
					<div class="d-none" id="progress-bar" style="position: absolute;top: 35%;left: 42%;"> 
						<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 100px;height: 100px;"/>
						<!-- <div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="70" aria-valuemin="0" aria-valuemax="100" style="border-radius: 4px;width: 95%"></div> -->
						<p style="opacity: 1; font-weight: bolder;color: darkblue;margin-left: -20px;">Please wait while we download from GSTN, it may take few minutes based on volume of invoices...</p>
					</div>
					<div class="customtable db-ca-view tabtable1" style=" overflow-y: auto">
						<table id="syncdataTable"class="display row-border dataTable meterialform"	cellspacing="0" width="60%">
							<thead>
								<tr>
									<th style="width: 23%;">Year/ Return Type</th>
									<th class="text-center">GSTR1</th>
									<th class="text-center">GSTR2A</th>
									<th class="text-center">GSTR3B</th>
								</tr>
								</thead>
								<tbody id="sync_data">
								<tr>									
									<th>April</th>
									<td id="GSTR14">-</td>
									<td id="GSTR2A4">-</td>
									<td id="GSTR3B4">-</td>
								</tr>
								<tr>
									<th>May</th>
									<td id="GSTR15">-</td>
									<td id="GSTR2A5">-</td>
									<td id="GSTR3B5">-</td>
								</tr>
								<tr>
									<th>June</th>
									<td id="GSTR16">-</td>
									<td id="GSTR2A6">-</td>
									<td id="GSTR3B6">-</td>
								</tr>
								<tr>
									<th>July</th>
									<td id="GSTR17">-</td>
									<td id="GSTR2A7">-</td>
									<td id="GSTR3B7">-</td>
								</tr>
								<tr>
									<th>August</th>
									<td id="GSTR18">-</td>
									<td id="GSTR2A8">-</td>
									<td id="GSTR3B8">-</td>
								</tr>
								<tr>
									<th>September</th>
									<td id="GSTR19">-</td>
									<td id="GSTR2A9">-</td>
									<td id="GSTR3B9">-</td>
								</tr>
								<tr>
									<th>October</th>
									<td id="GSTR110">-</td>
									<td id="GSTR2A10">-</td>
									<td id="GSTR3B10">-</td>
								</tr>
								<tr>
									<th>November</th>
									<td id="GSTR111">-</td>
									<td id="GSTR2A11">-</td>
									<td id="GSTR3B11">-</td>
								</tr>
								<tr>
									<th>December</th>
									<td id="GSTR112">-</td>
									<td id="GSTR2A12">-</td>
									<td id="GSTR3B12">-</td>
								</tr>
								<tr>
									<th>January</th>
									<td id="GSTR11">-</td>
									<td id="GSTR2A1">-</td>
									<td id="GSTR3B1">-</td>
								</tr>
								<tr>
									<th>February</th>
									<td id="GSTR12">-</td>
									<td id="GSTR2A2">-</td>
									<td id="GSTR3B2">-</td>
								</tr>
								<tr>
									<th>March</th>
									<td id="GSTR13">-</td>
									<td id="GSTR2A3">-</td>
									<td id="GSTR3B3">-</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="clearfix"></div>
					<div class="pull-right" style="margin-right:10%">
						<a href="${contextPath}/ccdb/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=initial" class="btn btn-blue-dark" style="padding: 9px;">Client Company Dashboard</a>
						<c:choose>
							<c:when test="${otpcheck eq 'OTP_NOT_VERIFIED' || otpcheck eq 'OTP_EXPIRED'}">
								<a class="btn btn-success disable syn_data_btn" id="syn_data_btn" onclick="syncwithcontinue('${id}','${fullname}','${usertype}','${clientid}')">Sync With GSTN</a>
							</c:when>
							<c:otherwise>
								<a class="btn btn-success" id="syn_data_btn" onclick="syncwithcontinue('${id}','${fullname}','${usertype}','${clientid}')">Sync With GSTN</a>
							</c:otherwise>
						</c:choose>
						<%-- <a class="btn btn-success" onclick="syncwithcontinue('${id}','${fullname}','${usertype}','${clientid}')">Sync With GSTN</a> --%>	
					</div>
					<!--  table div end -->
				</div>
			</div>
			
			<!-- enableAccessModal Start -->
	<div class="modal fade" id="enableAccessModal" tabindex="-1" role="dialog" aria-labelledby="enableAccessModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body">
                    <button type="button" id="enableAccessModalCloase" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h4>Important Step before your upload invoices </h4>
                    </div>
					<div class=" gstnuser-wrap">
						<div class="formboxwrap p-0" style="min-height:unset;">
						<div class="alert alert-info" id="idClientError"></div>	
						  <h6><span class="steptext"><strong><u>Step 1 </u> : </strong></span>Click here to <a href="https://services.gst.gov.in/services/login" target="_blank">Enable API Access</a>, Follow <a href="${contextPath}/static/mastergst/Enable-API-Access-On-GST-Portal.pdf" target="_blank">Help Guide</a> </h6>
						  <p class="txt-sm"><span class="steptext">&nbsp;</span>Login into your <a href="https://services.gst.gov.in/services/login" target="_blank">GSTN portal</a> and enable authorization. For detailed follow this guidence. </p>
						  <span class=""><span class="steptext pull-left">&nbsp;</span>
						  <a href="https://services.gst.gov.in/services/login" target="_blank" class="btn btn-blue-dark btn-sm pull-left mt-3">Enable API Access</a>
						  <a href="${contextPath}/static/mastergst/Enable-API-Access-On-GST-Portal.pdf" target="_blank" class="btn btn-sm btn-green pull-right mt-3">See Help Guide</a>
						  </span>
                         <div class="mb-3 mt-5" style="border-bottom:1px solid #f5f5f5;width:100%;">&nbsp;</div> 
						<h6><span class="steptext"><strong><u>Step 2 </u> : </strong></span> Verify GSTIN User Name</h6>
 					        <div class="col-md-12 col-sm-12 m-auto p-0">
                                <div class="formbox otpbox mt-3">
                                    <form class="meterialform row" id="accessotpEntryForm">
										<span class="steptext">&nbsp;</span>
										<div class="col-md-5 col-sm-12">
                                                <div class="lable-txt">Enter Your GSTIN Login/User Name</div>
                                                <div class="form-group">
                                                    <span class="errormsg" id="gstnUserIdMsg"></span>
                                                    <input type="text" id="gstnUserId" name="gstnUserId" required="required" data-error="Please enter the GSTN Username" aria-describedby="gstnUserId" placeholder="GSTIN Login/User Name">
                                                    <label for="input" class="control-label"></label>
                                                    <i class="bar"></i> </div>
                                            </div>
											<div class="col-md-4 col-sm-12">
												<a href="#" onClick="showOtp();" class="btn btn-red btn-sm mt-4">Verify Now</a>
											</div>
											<div class="whitebg gstn-otp-wrap">                                 
											<h5><span class="steptext"><strong><u>Step 3 </u> : </strong></span>GSTN has sent you an OTP please enter here for verification.</h5>          
                                            <!-- serverside error begin -->
                                            <div class="errormsg mt-2"> </div>
                                            <!-- serverside error end -->
											<span class="errormsg" id="otp_Msg"></span>  
                                            <div class="otp_form_input" style="display:block;margin-top:30px">
											<div class="col-12" style="display:block"></div>
											<div class="row">
												<span class="steptext col-sm-12">&nbsp;</span>
												<div class="col-md-9 col-sm-12">
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="1" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="2" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="3" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="4" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="5" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="6" placeholder="0"/>
													<div class="help-block with-errors"></div>
													</div>
													<div class="col-md-3 col-sm-12 pull-right">
													<a href="#" onClick="validOtp()" class="btn btn-blue btn-sm btn-verify">Submit</a> 
													</div>
													 <h6 class="col-md-9 col-sm-12 mt-3" style="display:inline-block;width:100%;text-align:center;">Didn&acute;t  Receive OTP? <a href="#" onClick="otpTryAgain('apiNotEnabled')">try again</a></h6>
                                                </div>
                                            </div>                                      
                                     	</div>
                                    </form>
                                </div>
                                <div class="formbox otpbox-suces">
                                    <form class="meterialform">
                                        <div class="whitebg row">
                                            <!-- serverside error begin -->
                                            <div class="errormsg"> </div>
                                            <!-- serverside error end -->
											<span class="steptext">&nbsp;</span>
                                            <div class="col-sm-10 pl-0" style="display:none;">
												<div class="mb-5 text-center greenbox" id="idVerifyClient"></div>
                                            </div>
											<div class="form-group col-3 m-auto">
												<input type="button" value="Close" class="btn btn-blue-dark btn-sm" data-dismiss="modal" aria-label="Close" />
											</div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
	<!-- enableAccessModal End -->
	<!-- otpModal Start -->
	<div class="modal fade" id="otpModal" tabindex="-1" role="dialog" aria-labelledby="otpModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" id="otpModalClose" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
					</button>
					<div class="invoice-hdr bluehdr">
						<h3>Add Client - Verify OTP</h3>
					</div>
					<div class=" p-4">
						<div class="formboxwrap p-0" style="min-height: unset;">
							<h3> Filing GST Made Simple, & Pay your Tax easily </h3>
							<h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
							<div class="col-md-12 col-sm-12 m-auto">
								<div class="formbox otpbox">
									<form class="meterialform" id="otpEntryForm" data-toggle="validator">
										<div class="whitebg">
											<h2> Verifying GSTIN User Name for smooth filing</h2>
											<h6>OTP has been sent to your GSTIN registered mobile number & e-mail, Please enter the same below
											</h6>
											<!-- serverside error begin -->                    
											<div class="errormsg"> </div>
											<!-- serverside error end --> 
											<span class="errormsg" id="cotp_Msg"></span>
											<div class="col-sm-12 otp_form_input" style="display:block;margin-top:30px">
												<div class=" ">
													<div class=" "></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="11" placeholder="0" />
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="12" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="13" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="14" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="15" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="16" placeholder="0"/>
													<div class="help-block with-errors"></div>
												</div>
												<h6 style="display: inline-block;text-align: center;width: 100%;">Didn't receive OTP? <a href="#" onClick="otpTryAgain('apiEnabled')">try again</a></h6>
											</div>
										</div>
										<div class="p-2 text-center">
											<p><a href="#" onClick="validateOtp()" class="btn btn-lg btn-blue btn-verify">Verify OTP</a></p>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- otpModal End -->	
		</div>
	</div>
	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->
	<script type="text/javascript">
	//var uploadResponse, ipAddress='';
		function ajaxFunction(userid,fullname,usertype,clientid,financialyear){
			var types=['GSTR1','GSTR2A','GSTR3B'];
			var date = new Date();
		    var currentyear = date.getFullYear();
			var currentmonth = date.getMonth()+1;
			for(var i=0;i<types.length;i++){
				var returntype=types[i];
				if(currentyear == financialyear){
					for (var month = 4; month <=currentmonth; month++) {
						var nextfinancialYear = financialyear;
						if(month <=3){
							nextfinancialYear = parseInt(financialyear)+1;
						}else{
							nextfinancialYear = financialyear;
						}
						$.ajax({
							url : '${contextPath}/downloadsyncdata/'+userid+'/'+fullname+'/'+usertype+'/'+returntype+'/'+clientid+'/'+month+'/'+nextfinancialYear,
							//url : '${contextPath}/downloadsyncdata/'+type+'/'+month,
							//cache: false,
							async: false,
							type: 'GET',
							success : function(response) {
								if(month>3){
									if(response == 'success'){
										$("#"+returntype+month).html("<b>Downloaded</b>").css("color","green");
									}else{
										$("#"+returntype+month).html("<b>Failed</b>").css("color","red");									
									}
								}
							},error : function(response) {}
						});
					}
					successNotification('Successfully Downloaded GSTN Data for the year of '+ $("#syncfinancialYear option:selected").text());
				}else{
					for (var month =1; month <=12; month++) {
						var nextfinancialYear = financialyear;
						if(month <=3){
							nextfinancialYear = parseInt(financialyear)+1;
						}else{
							nextfinancialYear = financialyear;
						}
						$.ajax({
							url : '${contextPath}/downloadsyncdata/'+userid+'/'+fullname+'/'+usertype+'/'+returntype+'/'+clientid+'/'+month+'/'+nextfinancialYear,
							//url : '${contextPath}/downloadsyncdata/'+type+'/'+month,
							//cache: false,
							async: false,
							type: 'GET',
							success : function(response) {
								$("#"+returntype+month).html("downloaded").css("color","green");
	               			},error : function(response) {}
						});
					}
					successNotification('Successfully Downloaded GSTN Data for the year of '+ $("#syncfinancialYear option:selected").text());
				}
			}
			$(".customtable.db-ca-view.tabtable1").css('opacity','1');
			$("#progress-bar").addClass('d-none');
		}
		function syncwithcontinue(userid,fullname,usertype,clientid){
			$.ajax({
				url : '${contextPath}/subscriptiondata/'+userid+'/'+clientid+'/${month}/${year}',
				async: false,
				type: 'GET',
				success : function(response) {
					if(response == "OTP_VERIFIED"){
						$("#progress-bar").removeClass('d-none');
						$(".customtable.db-ca-view.tabtable1").css('opacity','0.4');
						var financialyear=$('#syncfinancialYear').val();
						setTimeout(function(){
							ajaxFunction(userid,fullname,usertype,clientid,financialyear);
						},100);
					}else if(response == "expired"){
						errorNotification('Your subscription has expired. Kindly <a href="${contextPath}/dbllng/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe</a> to proceed further! ');
					}else{
						errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP_sync(this)">Verify Now</a> to proceed further.');
					}
				},error : function(response) {}
			});
		}
		$('#syncfinancialYear').change(function() {
        	$("#syncdataTable tbody tr td").html('-');  
        });
		$(document).ready(function(){
			var uploadResponse, ipAddress='';
			if('${otpcheck}' == 'OTP_NOT_VERIFIED' || '${otpcheck}' == 'OTP_EXPIRED'){
				errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP_sync(this)">Verify Now</a> to proceed further.');
			} /* else if('${response_status}' != 'success') {
				errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP_sync(this)">Verify Now</a> to proceed further.');
			}
			else if('${otperror}' == 'Y') {
				errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP_sync(this)">Verify Now</a> to proceed further.');
			} */
		});
		function invokeOTP_sync(btn) {
			$(btn).addClass('btn-loader');
			var state = '<c:out value="${client.statename}"/>';
			var gstname = '<c:out value="${client.gstname}"/>';
			$('#cotp_Msg').text('').css("display","none");
			$('#otp_Msg').text('').css("display","none");
			$("#accessotpEntryForm")[0].reset();
			$("#otpEntryForm")[0].reset();
			$.ajax({
				url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					uploadResponse = response;
					if(uploadResponse.status_cd == '1') {
						$('#otpModal').modal("show");
						$('.syn_data_btn').removeClass('disable');
					} else if(uploadResponse.error && uploadResponse.error.error_cd == 'AUTH4037') {
						$('#enableAccessModal').modal('show');
						$('#idClientError').html("We noticed that your GSTIN <strong>( "+gstnnumber+" )</strong> doesn't have API Access with the GSTN Portal Login/User Name <strong>"+gstname+"</strong> Please enable the API access and update GSTN User Name correctly, Please follow below steps.");
						$('#gstnUserId').val(gstname);
					} else if(uploadResponse.error && uploadResponse.error.message) {
						errorNotification(uploadResponse.error.message);
					}
				$(btn).removeClass('btn-loader'); 
				},
				error : function(e, status, error) {
					$(btn).removeClass('btn-loader');
					if(e.responseText) {
						errorNotification(e.responseText);
					}
				}
			});
		}
		function showOtp(){
			var state = '<c:out value="${client.statename}"/>';
			var gstname = '<c:out value="${client.gstname}"/>';
			$.ajax({
				url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					uploadResponse = response;
					$('.gstn-otp-wrap').show();
				},
				error : function(e, status, error) {
					if(e.responseText) {
						$('#idClientError').html(e.responseText);
					}
				}
			});
		}
		
		function otpTryAgain(apiAcessStatus){
			$('#cotp_Msg').text('').css("display","none");
			$('#otp_Msg').text('').css("display","none");
			var state = $('#statename').val();
			var gstname = "";
			if(apiAcessStatus == 'apiEnabled'){
				gstname = $('#addfirmgstnno').val();
				$("#otpEntryForm")[0].reset();
			}else{
				gstname = $('#gstnUserId').val();
				$("#accessotpEntryForm")[0].reset();
				$('#gstnUserId').val(gstname);
			}
			$.ajax({
				url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					uploadResponse = response;
				},
				error : function(e, status, error) {
					if(e.responseText) {
						$('#idClientError').html(e.responseText);
					}
				}
			});
		}
		
		function validOtp() {
			var gstnUserId = $('#gstnUserId').val();
			var otp1 = $('#accessotp1').val();
			var otp2 = $('#accessotp2').val();
			var otp3 = $('#accessotp3').val();
			var otp4 = $('#accessotp4').val();
			var otp5 = $('#accessotp5').val();
			var otp6 = $('#accessotp6').val();
			if(otp1=="" || otp2=="" || otp3=="" || otp4=="" || otp5=="" || otp6==""){
				$('#otp_Msg').text('Please Enter otp').css("display","block");
			}else{
				var otp = otp1+otp2+otp3+otp4+otp5+otp6;
				var gstnnumber = '<c:out value="${client.gstnnumber}"/>';
				var pUrl = "${contextPath}/ihubauth/"+otp;
				$("#accessotpEntryForm")[0].reset();
				$('#gstnUserId').val(gstnUserId);
				$.ajax({
					type: "POST",
					url: pUrl,
					async: false,
					cache: false,
					data: JSON.stringify(uploadResponse),
					dataType:"json",
					contentType: 'application/json',
					success : function(authResponse) {
						if(authResponse == null || authResponse == ''){
							$('#otp_Msg').text('We noticed your Internet is slow,Please try again').css("display","block");
						}else{
						if(authResponse.status_cd == '1') {
							$('.gstn-otp-wrap').hide();
							closeNotifications();
							$('.syn_data_btn').removeClass('disable');
							$('#idVerifyClient').parent().show();
							$('#idVerifyClient').html("Verified OTP Number successfully. Your User Name for GSTN Number (<strong>"+gstnnumber+"</strong>) verified.");
						}else{
							$('#otp_Msg').text('Please Enter valid otp').css("display","block");
						}
						}
					},
					error : function(e, status, error) {
						if(e.responseText) {
							$('#idClientError').html(e.responseText);
						}
					}
				});
			}	
		}
		function validateOtp() {
			var otp1 = $('#otp1').val();
			var otp2 = $('#otp2').val();
			var otp3 = $('#otp3').val();
			var otp4 = $('#otp4').val();
			var otp5 = $('#otp5').val();
			var otp6 = $('#otp6').val();
			if(otp1=="" || otp2=="" || otp3=="" || otp4=="" || otp5=="" || otp6==""){
				$('#cotp_Msg').text('Please Enter otp').css("display","block");
			}else{
				var otp = otp1+otp2+otp3+otp4+otp5+otp6;
				var pUrl = "${contextPath}/ihubauth/"+otp;
				$("#otpEntryForm")[0].reset();
				$.ajax({
					type: "POST",
					url: pUrl,
					async: false,
					cache: false,
					data: JSON.stringify(uploadResponse),
					dataType:"json",
					contentType: 'application/json',
					success : function(authResponse) {
						if(authResponse == null || authResponse == ''){
							$('#cotp_Msg').text('We noticed your Internet is slow,Please try again').css("display","block");
						}else{
						if(authResponse.status_cd == '1') {
							$('.syn_data_btn').removeClass('disable');
							$('#otpModalClose').click();
							closeNotifications();
						}else{
							$('#cotp_Msg').text('Please Enter Valid otp').css("display","block");
						}
						}
					},
					error : function(e, status, error) {
						$('#otpModalClose').click();
						if(e.responseText) {
							errorNotification(e.responseText);
						}
					}
				});
			}
		}
		$(".otp_form_input .form-control").keyup(function () {
			if (this.value.length == this.maxLength) {
				$(this).next().next('.form-control').focus();
			}
		});
		$('.otp_form_input .form-control').keydown(function(e) {
		    if ((e.which == 8 || e.which == 46) && $(this).val() =='') {
		        $(this).prev('input').focus();
		        $(this).prev().prev('.form-control').focus();
		    }
		});
	</script>
</body>
</html>
