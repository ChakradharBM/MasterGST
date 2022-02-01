<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
</head>
<body>
	<!-- enableAccessModal Start -->
	<div class="modal fade" id="enableAccessModal" tabindex="-1" role="dialog" aria-labelledby="enableAccessModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h4>Important Step before your upload invoices  </h4>
                    </div>
                    <div class=" gstnuser-wrap">
                        <div class="formboxwrap p-0">
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
                                            <div class="errormsg" id="otp_Msg"></div>  
                                            <div class="otp_form_input"  style="display:block;margin-top:30px">
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
<script type="text/javascript">
	function invokeOTP(btn) {
			$(btn).addClass('btn-loader');
			var state = '${client.statename}';
			var gstname = '${client.gstname}';
			var gstnnumber = '${client.gstnnumber}';
			$('#gstnUserId').val(gstname);
			$('#cotp_Msg').text('').css("display","none");
			$('#otp_Msg').text('').css("display","none");
			$("#accessotpEntryForm")[0].reset();
			$("#otpEntryForm")[0].reset();
			if(state != '' && gstname != '') {
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
						} else if(uploadResponse.error 
							&& uploadResponse.error.error_cd == 'AUTH4037') {
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
		}
		function showOtp(){
			var state = '${client.statename}';
			var gstname = '${client.gstname}';
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
				var gstnnumber = '${client.gstnnumber}';
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
							$('#idVerifyClient').parent().show();
							$('#idVerifyClient').html("Verified OTP Number successfully. Your User Name for GSTN Number (<strong>"+gstnnumber+"</strong>) verified.");
							closeNotifications();
						}else{
							$('#otp_Msg').text('Please Enter valid otp').css("display","block");
						}
						}
					},error : function(e, status, error) {
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
							$('#otpModalClose').click();
							closeNotifications();
						}else{
							$('#cotp_Msg').text('Please Enter Valid otp').css("display","block");
						}
						}
					},error : function(e, status, error) {
						$('#otpModalClose').click();
						if(e.responseText) {
							errorNotification(e.responseText);
						}
					}
				});
			}
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
</script>
</body>

</html>