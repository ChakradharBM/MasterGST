<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | Add GSTN | Add Client | Add Business</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<style>
.bodybreadcrumb.main{top: 43px;}
#addclntgstnno,#pannumber,#lastName{text-transform: uppercase;}
.db-ca-wrap{padding-top:0px!important}
.btn.btn-blue:hover{background-color: #1dc2f8;}
.invoice_otp::-webkit-input-placeholder {font-size:50px;}
#otpboxwrap .formbox { padding:50px; }
#otpboxwrap .formbox .btn-blue-dark { margin-left:10px }
.otp_form_input { margin:30px auto 40px; }
.otp_form_input input[type='text'] { border-radius:0; border: none; border-bottom: 3px solid #4664be; width: 20%; display: inline-block; margin-left: 7px; text-align: center; font-size:50px ; margin-bottom:15px	}
#gstnerrMsg{text-align: right; position: absolute; padding-right: 0px;}
.bnkdetail .meterialform .checkbox input:checked~.helper::before,.termsdetails .meterialform .checkbox input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.bnkdetail .meterialform .checkbox input:checked~.helper::after,.termsdetails .meterialform .checkbox input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.bnkdetail .meterialform .checkbox .helper,.termsdetails .meterialform .checkbox .helper{border-radius:4px;color: #337ab7;}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>

  <div class="db-ca-wrap" style="margin-top:86px!important">
    <div class="container">
      <div class="row">
        <!-- meterialform begin -->
        <div class="bg-white">
				<div class="col-11 m-auto bs-fancy-checks">
				
		
		<form:form method="POST" data-toggle="validator" class="meterialform" id="clientform" name="clientform" action="${contextPath}/createclient/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="client">
				  <div class="row">
				  		<div class="col-md-2"></div>
				  		<div class="col-md-6 pl-0">
							<h4 class="text-center" style="margin-left:-22px;"> Let&rsquo;s Add a New <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></h4>
				  		</div>
				  		<div class="form-group mb-2 mb-sm-0 bnkdetail" style="margin-top:0px; margin-bottom:5px;float:right;">
                    <div class="row p-0">
										
										<div class="pl-0">
										<div class="form-group-inline pr-4 pl-2" style="padding-top: 2px;">
		                                <div class="form-radio" style="vertical-align:super;">
		                                    <div class="radio">
		                                        <label>
		                                            <input name="clienttype" id="gstreg" type="radio" value="Registered" checked/>
		                                            <i class="helper"></i>GST Registered </label>
		                                    </div>
		                                </div>
		                                <div class="form-radio" style="vertical-align:super;">
		                                    <div class="radio">
		                                        <label>
		                                            <input name="clienttype" id="gstreg" type="radio" value="UnRegistered"/>
		                                            <i class="helper"></i>GST not Registered</label>
		                                    </div>
		                                </div>
		                            </div>
										</div></div>
				  </div>
				  </div>
		
          <!-- add new row begin -->
          
          <div class="addnew-client pb-1" >
            <div class="row">
				<div class="col-md-3 col-sm-12 text-left">
					<span class="txt-md">1</span>
					<div class="det-txt"> <h5 class="active">Personal Details</h5> </div>
					<div class="det-txt-box"><p>Click here to Login <a href="https://services.gst.gov.in/services/login" target="_blank">GSTN Portal</a> and <a href="https://services.gst.gov.in/services/login" target="_blank">enable API Access</a>,<br /> Follow our <a href="${contextPath}/static/mastergst/Enable-API-Access-On-GST-Portal.pdf" target="_blank">Help guide</a>.</p></div>
				</div>
            <div class="col-md-9 col-sm-12">              
              <div class="row">
              			<!--  <div class="form-group col-md-12 col-sm-12 pl-5">
                            <div class="form-group-inline">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="clienttype" id="type" type="radio" value="Registered" checked/>
                                            <i class="helper"></i>Registered</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="clienttype" id="type" type="radio" value="UnRegistered"/>
                                            <i class="helper"></i>UnRegistered</label>
                                    </div>
                                </div>
                            </div>
                        </div> -->
              
				<div class="form-group col-md-6 col-sm-12 pl-5" id="gstno_div">
                  <p class="lable-txt astrich gstinStateLable_txt">GSTIN <a href="#" onClick="invokePublicAPI(this)" id="invokePublicAPIc" class="btn btn-green btn-sm pull-right disable">Get GSTIN Details</a></p>
                  <span class="errormsg" id="cgstnnumber_Msg"></span>
                  <input type="text" id="addclntgstnno" name="gstnnumber" required="required" placeholder="Business GST Number" onChange="updatePan(this.value)"  pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" />
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
				<div class="form-group col-md-6 col-sm-12 pl-5 panno">
                  <p class="lable-txt astrich gstinStateLable_txt">PAN</p>
                  <span class="errormsg" id="lastName_Msg"></span>
                  <input type="text" id="pannumber" name="pannumber" required="required" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" data-error="Please enter Valid PAN.(Sample AAAAA9999A)" placeholder="PAN Number"  value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <=90) || (event.charCode >= 48 && event.charCode <=57) || event.charCode == 0))"/>
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
				<div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt astrich">Authorized Signatory Name</p>
                  <span class="errormsg" id="firstName_Msg"></span>
                  <input type="text" id="firstName" name="signatoryName" required="required" data-error="Please enter the authorized signatory name" placeholder="Jane Smith" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
                <div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt astrich gstinStateLable_txt">Authorized Person PAN</p>
                  <span class="errormsg" id="lastName_Msg"></span>
                  <input type="text" id="lastName" name="signatoryPAN" required="required" data-error="Please enter Valid PAN.(Sample AAAAA9999A)"pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" placeholder="PAN Number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
                <div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt">Client Email Id</p>
                  <span class="errormsg" id="emailId_Msg"></span>
                  <input type="email" id="emailId" name="email" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email address"  placeholder="janeSmith@gmail.com" value="" />
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
                <div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt">Client Mobile Number</p>
                  <span class="errormsg" id="mobileNumber_Msg"></span>
                  <input type="text" id="mobileId" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" placeholder="222-444-5555" value="" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" />
                  <script>$("#mobileId").intlTelInput({"initialCountry":"in"});</script>
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
              </div>
            </div>
						</div>
          </div>
          <!-- add new row  end -->
          <!-- add new row begin -->
          <div class="addnew-client pt-1 pb-0">
            <div class="row">
				<div class="col-md-3 col-sm-12 text-center">
					<span class="txt-md">2</span>
                    <div class="det-txt"> <h5>Business Details</h5> </div>
					<div class="det-txt-box"><p>Verify your GSTN User Name, This User Name must be same you login to <a href="https://services.gst.gov.in/services/login" target="_blank">gst.gov.in</a></p>    </div>
				</div>
            <div class="col-md-9 col-sm-12">
            
              <div class="row">
                <div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt astrich" id="businesIdname">Business Name</p>
                  <span class="errormsg" id="businessname_Msg"></span>
                  <input type="text" id="businessname" name="businessname" required="required" data-error="Please enter the name of the Business"  placeholder="Business Name" value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" />
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
                <div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt astrich">State</p>
                  <span class="errormsg" id="StateName_Msg"></span>
				  <input type="text" id="statename" required="required" name="statename" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]*" data-error="Please enter the state name" placeholder="State" readonly/>
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
				  <div id="statenameempty" style="display:none">
						<div class="ddbox">
						  <p>Search didn't return any results.</p>
						</div>
					</div>
                  <i class="bar"></i> </div>
                <div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt astrich gstinStateLable_txt">GST Login/Username <a id="btnValidateClient" href="#" onClick="invokeOTP(this)" class="btn btn-green btn-sm pull-right disable">Verify GSTIN Login</a></p>
                  <span class="errormsg" id="gstname_Msg"></span>
                  <input type="text" id="gstname" name="gstname" required="required" placeholder="Business GST Name" value="" data-toggle="tooltip" data-placement="bottom" title="Please enter the Login Name you have received from GSTIN to login to GSTIN Portal."/>
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
				<div class="form-group col-md-6 col-sm-12 pl-5">
										<p class="lable-txt astrich gstinStateLable_txt">Dealer Type</p>
										<div class="form-group1">
											<span class="errormsg" id="subnumber_Msg"></span>
											<select id="dealerType" name="dealertype" data-error="Please Select Dealer Type" required="required">
												<option value="">-- Select Dealer Type --</option>
												<option value="NonCompound">Regular / Non Compound Dealer</option>
												<option value="Compound">Composition / Compound Dealer</option>
												<option value="Casual">Casual Taxable Person</option>
												<option value="InputServiceDistributor">Input Service Distributor (ISD)</option>
												<option value="SezUnit">SEZ Unit</option>
												<option value="SezDeveloper">SEZ Developer</option>
											</select>
											<label for="dealerType" class="control-label"></label>
											<div class="help-block with-errors"></div>
											<i class="bar"></i> </div>
									</div>
				
                <div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt aggregateTurnOver_lable_txt astrich">Aggregate Turnover of the Taxable Person in the previous FY</p>
                  <span class="errormsg" id="mobileNumber_Msg"></span>
                  <input type="text" id="aggregateTurnOver" name="turnover" required="required" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter numeric value with max 2 decimal places"  placeholder="1234" value="" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" />
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
                <div class="form-group col-md-6 col-sm-12 pl-5 Compound" style="display:none">
                  <p class="lable-txt">Are you filied Returns for the Previous Quarters</p>
                  <div class="form-group-inline">
                    <div class="form-radio">
                      <div class="radio">
                        <label>
                        <input name="previousquarters" id="branches" type="radio" value="Yes" checked />
                        <i class="helper"></i>Yes</label>
                      </div>
                    </div>
                    <div class="form-radio">
                      <div class="radio">
                        <label>
                        <input name="previousquarters" id="branches" type="radio" value="No" />
                        <i class="helper"></i>No</label>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="form-group col-md-6 col-sm-12 pl-5 Compound prev_amt" style="display:none">
                  <p class="lable-txt">Please enter the Turn Over you have disclosed</p>
                  <span class="errormsg" id="mobileNumber_Msg"></span>
                  <input type="text" id="mobileNumber" name="disclousedturnover" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter numeric value with a max precision of 2 decimal places"  placeholder="12345" value="" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" />
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> 
				 </div>
				 <div class="form-group col-md-6 col-sm-12 pl-5">
                  <p class="lable-txt">Address</p>
                  <span class="errormsg" id="lastName_Msg"></span>
                  <input type="text" id="addr" name="address" placeholder="Address" value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))" />
                  <label for="input" class="control-label"></label>
                  <i class="bar"></i> </div>
				  				<div class="form-group col-md-6 col-sm-12 pl-5">
										<p class="lable-txt astrich gstinStateLable_txt">Filing Option</p>
										<div class="form-group1">
											<span class="errormsg" id="subnumber_Msg"></span>
											<select id="filingOption" name="filingoption" data-error="Please Select Filing Option" required="required">
												<option value="">-- Select Filing Option --</option>
												<option value="Monthly">Monthly</option>
												<option value="Quarterly">Quarterly</option>
											</select>
											<label for="filingOption" class="control-label"></label>
											<div class="help-block with-errors"></div>
											<i class="bar"></i> </div>
									</div>
				  
				 <div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks">             
					<div class="row">
						<div class="form-group col-md-8"><p class="lable-txt">Is it part of group of Companies </p></div>
						<div class="form-group col-md-4">
							<div class="form-check" style="margin:0px!important">
								<input name="group_companies" type="checkbox" id="group_companies" onClick="companyCheck()">
								<label for="group_companies"><span class="ui"></span></label>
							</div>
						</div>
					</div>
                 </div>
				 <div class="form-group col-md-6 col-sm-12 pl-5 companyName" style="display:none">
                  <p class="lable-txt">Enter Group Name : </p>
                  <input type="text" id="group_Name" name="groupName"  placeholder="Group Name" value="" />
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
				  <div id="groupnameempty" style="display:none">
						<div class="ddbox">
						  <p>Search didn't return any results.</p>
						</div>
					</div>
                  <i class="bar"></i> 
				 </div>
                <!--- end -->
              </div>
            </div>
						</div>
          </div>
          <!-- add new row  end -->
          <div class="row">
          <div class="col-md-4"></div>
          <div class="col-md-6 text-center mt-0" style="display:block">
			<input id="btnAddClient" type="submit" class="btn btn-blue-dark urllink ml-2 mr-2" value="Add"/>
			<a type="button" class="btn btn-blue urllink" href="#" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span aria-hidden="true">Cancel</span></a></div>
          </div>
		  </div>
        </form:form></div>
				
				</div>
        <!-- meterialform end -->
      </div>
    </div>
  </div>
</div>
<!-- footer begin here -->

 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
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

                    <div class="gstnuser-wrap">

                        <div class="formboxwrap p-0" style="min-height: unset;">
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
										<span class="errormsg col-md-12" id="gstnerrMsg"></span>
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
													 <h6 class="col-md-9 col-sm-12 mt-3">Didn&acute;t  Received OTP? <a href="#" onClick="otpTryAgain('apiNotEnabled')">try again</a></h6>
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
												<div class="">
													<div class=""></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="10" placeholder="0" />
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="11" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="12" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="13" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="14" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="15" placeholder="0"/>
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
	var uploadResponse=null;

   var table = $('table.display').DataTable({
   "dom": '<"toolbar">frtip',
    
     "pageLength": 10,

     "language": {

        "paginate": {

           "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",

           "next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"

        }

     }

   });
 $("div.toolbar").html('<h4>All Clients</h4>');
 
   var headertext = [],

     headers = document.querySelectorAll("table.display th"),

     tablerows = document.querySelectorAll("table.display th"),

     tablebody = document.querySelector("table.display tbody");

   for (var i = 0; i < headers.length; i++) {

     var current = headers[i];

     headertext.push(current.textContent.replace(/\r?\n|\r/, ""));

   }

   for (var i = 0, row; row = tablebody.rows[i]; i++) {

     for (var j = 0, col; col = row.cells[j]; j++) {

        col.setAttribute("data-th", headertext[j]);

     }

   }

     

</script>
<script type="text/javascript">
	var ipAddress = '';
	$(document).ready(function() {
		$('#nav-client').addClass('active');
		//$('.xdsoft_calendar').hide();
		$('input[type=radio][name=clienttype]').change(function() {
			updateGSTINState(this.value);
		});
		 $('#type').click(function(){
			if($("#type").is(':checked')){
				$('#type').val('Registered');
				updateGSTINState('Registered');
		    }else{
		    	$('#type').val('UnRegistered');
		    	updateGSTINState('UnRegistered');
		   }
		});
		$(".otp_form_input .form-control").keyup(function () {
			if (this.value.length == this.maxLength) {
				$(this).next().next('.form-control').focus();
			}
		});
		$('#dealerType').on('change',function(){
			var inputValue = $(this).val();
			if(inputValue == 'Compound') {
				$('.Compound').show();
				var field = $('input[name="previousquarters"]:checked').val();
				if(field == 'No') {
					$('.prev_amt').hide();
				}
			} else {
				$('.Compound').hide();
			}
		});
		$('input[name="previousquarters"]').on('click',function(){
			var inputValue = $(this).attr("value");
			if(inputValue == 'Yes') {
				$('.prev_amt').show();
			} else {
				$('.prev_amt').hide();
			}
		});
		var groupname = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/srchgroupName?query="+ phrase + "&id=${id}&format=json";
			},
			getValue: "groupName",
			list: {
				onChooseEvent: function() {
					var custData = $("#group_Name").getSelectedItemData();
					$('#group_Name').val(custData.groupName);
				}
			}

		};
		$("#group_Name").easyAutocomplete(groupname);
	});
	
	function updateGSTINState(value) {
		if (value == 'Registered') {
			$('.gstinStateLable_txt,.aggregateTurnOver_lable_txt').addClass("astrich");
			$('#addclntgstnno,#pannumber,#lastName,#gstname,#filingOption,#dealerType,#aggregateTurnOver').attr("required",true);
			$('#statename').prop("readonly",true);
			//$('.category_business').show();
			//$('input').val("");
			$('#filingOption').val("");
			$('#btnAddClient').val("Add");
		} else {
			$('.gstinStateLable_txt,.aggregateTurnOver_lable_txt').removeClass("astrich");
			$('#addclntgstnno,#pannumber,#lastName,#gstname,#filingOption,#dealerType,#aggregateTurnOver').removeAttr("required");
			$('#statename').prop("readonly",false);
			//$('input').val("");
			$('#filingOption').val("Monthly");
			$('#btnAddClient').val("Add");
			var stateoptions = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					list: {
						onLoadEvent: function() {
							if($("#eac-container-statename ul").children().length == 0) {
								//$("#state").val('');
								$("#statenameempty").show();
							} else {
								$("#statenameempty").hide();
							}
						},
						maxNumberOfElements: 37
					}
				};
				$("#statename").easyAutocomplete(stateoptions);
				//google.maps.event.addDomListener(window, 'load', initialize);
			//$('.category_business').hide();
		}
		$('form[name="clientform"]').validator('update');
	}
	function companyCheck(){
		var checkBox = document.getElementById("group_companies");
		if (checkBox.checked == true){
			$('#group_companies').prop("checked",true);
			$('.companyName').show();
		}else if(checkBox.checked == false){
			$('#group_companies').prop("checked",false);
			$('.companyName').hide();
		}
	}
		
	function updatePan(value) {
		if(value.length == 15) {
			$('#pannumber').val(value.substring(2,12));
			$('.panno .with-errors').html('');
			$('.panno').removeClass('has-error has-danger');
			$.ajax({
				url: "${contextPath}/srchstatecd?code="+value.substring(0,2),
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response) {
						$('#statename').val(response.name);
					}
				}
			});
		}
	}
	function invokePublicAPI(btn) {
		var gstnno = $("#addclntgstnno").val();
		updatePan(gstnno);
		$('#businessname').val('');
		$('#dealerType').val('');
		$('#addr').val('');
		var userid ='${id}'; 
		if(gstnno != '') {
			var gstnumber = gstnno.toUpperCase();
			$(btn).addClass('btn-loader');
			$.ajax({
				url: "${contextPath}/publicsearch?gstin="+gstnumber+"&userid="+userid,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					
					if(response.error && response.error.message) {
						if(response.error.message == 'SWEB_9035'){
							$('#cgstnnumber_Msg').text("No Records Found");	
					  	} else{
							$('#cgstnnumber_Msg').text(response.error.message);
					  	}	
					}
					$(btn).removeClass('btn-loader');
					if(response.status_cd == '1') {
						if(response.data) {
							var address = "";
							if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
								$('#businessname').val(response.data['lgnm']);
							}else{
								$('#businessname').val(response.data['tradeNam']);
							}
							Object.keys(response.data).forEach(function(key) {
								if(key == 'dty'){
									if(response.data[key] == 'Regular'){
										$('#dealerType').val('NonCompound');
									}else if(response.data[key] == 'Composition'){
										$('#dealerType').val('Compound');
									}else if(response.data[key] == 'Input Service Distributor (ISD)'){
										$('#dealerType').val('InputServiceDistributor');
									}else if(response.data[key] == 'Casual Taxable Person'){
										$('#dealerType').val('Casual');
									}else if(response.data[key] == 'SEZ Unit'){
										$('#dealerType').val('SezUnit');
									}else if(response.data[key] == 'SEZ Developer'){
										$('#dealerType').val('SezDeveloper');
									}else{
										$('#dealerType').val('');
									}
								}
								if(key == 'sts'){
									if(response.data[key] != 'Active') {
										$('#enableAccessModal').modal('show');
										$('#idClientError').html("We noticed that your GSTIN <strong>( "+gstnno+" )</strong> doesn't have API Access with the GSTN Portal Login/User Name <strong></strong> Please enable the API access and update GSTN User Name correctly, Please follow below steps.");
									}
								}
								if(key == 'pradr'){
									Object.keys(response.data['pradr']['addr']).forEach(function(key){
										if(response.data['pradr']['addr'][key] != ''){
												address = address.concat(response.data['pradr']['addr'][key]+",");
										}
									});
								}
						});
						$('#addr').val(address.slice(0,-1));
						}
					} else if(response.error && response.error.message) {
						errorNotification(response.error.message);
					}
					$(btn).removeClass('btn-loader');
				},
				error : function(e, status, error) {
					$(btn).removeClass('btn-loader');
				}
			});
		}
	}
	function invokeOTP(btn) {
		var state = $('#statename').val();
		var gstname = $('#gstname').val();
		var gstnnumber = $('#addclntgstnno').val();
		$('#cotp_Msg').text('').css("display","none");
			$('#otp_Msg').text('').css("display","none");
			$("#accessotpEntryForm")[0].reset();
			$("#otpEntryForm")[0].reset();
		if(state != '' && gstname != '') {
			$(btn).addClass('btn-loader');
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
		var state = $('#statename').val();
		var gstname = $('#gstnUserId').val();
		var gstnnumber = $('#addclntgstnno').val();
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				uploadResponse = response;
				if(uploadResponse.status_cd == '1'){
					$('.gstn-otp-wrap').show();
				}else if(uploadResponse.error 
						&& uploadResponse.error.error_cd == 'AUTH4037') {
						$('#gstnerrMsg').text("Your Api Access is not enabled,please enable");
						$('#gstnUserId').val(gstname);
				} else if(uploadResponse.error && uploadResponse.error.message) {
					errorNotification(uploadResponse.error.message);
				}
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
			var gstnnumber = $('#gstnnumber').val();
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
							$('#otpModalClose').click();
							$('#btnValidateClient').html('Verified').addClass('disable');
							$('#gstname,#statename').addClass('disable');
						}else{
							$('#cotp_Msg').text('Please Enter Valid otp').css("display","block");
						}
					}
				},
				error : function(e, status, error) {
					$('#otpModalClose').click();
					$('#btnValidateClient').hide();
					if(e.responseText) {
						errorNotification(e.responseText);
					}
				}
			});
		}
	}
</script>
<script type="text/javascript">
$(function(){	
		$("#addclntgstnno").keyup(function() {
			var regex = /^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$/;			
			if($("#addclntgstnno").val().trim().length>14) {
				if(!regex.test($("#addclntgstnno").val())){
					$("#invokePublicAPIc").addClass("disable");
				}else{
					$("#invokePublicAPIc").removeClass("disable");
				}
			}else {
					$("#invokePublicAPIc").addClass("disable");
			}
		});
		
		$("#gstname").keyup(function() {
			if($("#gstname").val().trim().length<=0) {
				$("#btnValidateClient").addClass("disable");
			}else {
				$("#btnValidateClient").removeClass("disable");
			}
		});
	});
	
	//disallow backspace and delete key for state field
	var clientype = $('#type').val();
	if(clientype == "Registered"){
		statenameKeyUp();
	} 
	function statenameKeyUp(){
		  if( e.which == 8 && ( document.activeElement.id == 'statename') ){   
		    e.preventDefault();  
		    return false;   
		  } 
	}
	
	
	
</script>
</body>
</html>
