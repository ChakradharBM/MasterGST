<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<%@include file="/WEB-INF/views/includes/script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<c:set var="varClientId" value="<%=UUID.randomUUID().toString()%>"/>
<c:set var="varClientSecret" value="<%=UUID.randomUUID().toString()%>"/>
<style>
#headerkeysTable thead tr th{font-weight:500;font-size: 14px;}
</style>
<script type="text/javascript">
	$(function () {
		$('.radio input[type="radio"]').click(function(){
			if($(this).attr("value") == 'Sandbox') {
				$('#sectionProduction').hide();
				$('#sectionSandbox').show();
				$('#clientid').val('${varClientId}').prop('readonly', true);
				$('#clientsecret').val('${varClientSecret}').prop('readonly', true);
			} else if($(this).attr("value") == 'Production') {
				$('#sectionProduction').show();
				$('#sectionSandbox').hide();
			}
		});
	});
</script>
<style>
.txtbx-production{width:230px}
#idproductionBody{margin-left: -272px;}
#idproductionBody .modal-body{width:900px}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/app_header.jsp" %>
    <!-- header page begin -->
   <div class="bodywrap">
<div class="bodybreadcrumb main">
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
				<div class="navbar-left">
					<ul>
						<li class="nav-item">
							<a class="nav-link urllink active" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=gst" >GST Credentials</a>
						</li>
						<li class="nav-item">
							<a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=eway" >e-Way Bill Credentials</a>
 						</li>
 						<li class="nav-item">
							<a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=einv" >e-Invoice Credentials</a>
 						</li>
 						<li class="nav-item">
							<a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=authorizationKeys" >Authorization Keys</a>
 						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>
    <div>
            <div class="db-inner">
                <!-- begin content  -->
                <div class="container db-inner-txt" style="margin: 0px 75px;margin-top: 80px;">
                    <div class="row cred-wrap" >
                        <div class="credentialwrap">
                            <div class="row">
                           
                                <div class="col-lg-3 col-md-2 col-sm-12 bdr-dashed-r">
                                    <!-- left profile list begin -->
                                  <h4 class="presize">Prerequisites to Create Sandbox Credentials: </h4>
                                  <div class="row stpsblock">
                                  <p class="col-md-2 col-sm-12"><strong>Step1:</strong></p>
                                  <p class="col-md-10 col-sm-12"> First you need create an account with GSTN Network,<a href="https://docs.google.com/forms/d/e/1FAIpQLSfyJIrHuycgF1jiD9P47qjdiFH2Nmvba_1a9WnBLfgw_VSaYw/viewform">
                                     Click here</a> to create</p></div>
                                  <div class="row stpsblock">
                                    <p class="col-md-2 col-sm-12 "><strong>Step2:</strong></p>
                                    <p class="col-md-10 col-sm-12">Please <b>select GSP</b> as "<span class="selcol"><b>Tera Software Limited</b></span>" in the Google Form </p></div>
                                    <div class="row stpsblock">
                                        <p class="col-md-2 col-sm-12 "><strong>Step3:</strong></p>
                                        <p class="col-md-10 col-sm-12"> GSTN will take usually 10 days time to process the request </p></div>
                                    <div class="row stpsblock">
                                            <p class="col-md-2 col-sm-12 "><strong>Step4:</strong></p>
                                            <p class="col-md-10 col-sm-12"> Upon receive the GST User Name & GSTIN from GSTN we will share with you.</p></div>
                                    <!-- left profile list end -->
                                </div>
                               
                                <div class="col-lg-9 col-md-9 col-sm-12">
                                    <!--   profile table begin -->
                                    <div class="welhdr">
                                        <h5 class="welcomehr">Welcome to MasterGST GST API Portal, please follow below steps to create sandbox access</h5>
                                        <p class="parafnt"><span class="welspn"><strong>Step1:</strong></span>Create MasterGST GST API Sandbox Credentials </p>
                                        <p class="parafnt"><span class="welspn"><strong>Step2:</strong></span>Use the MasterGST Registered Email, Client ID & Client Secret ID, GSTN User Name & GSTIN</p>
                                    </div>
                                    <!-- Nav tabs -->
                                    <div class="customtable">
                              <div class="gstr-info-tabs"><div>&nbsp;</div>
    							<ul class="nav nav-tabs" role="tablist">
						            <li class="nav-item">
						              <a class="nav-link active" data-toggle="tab" href="#credential" role="tab">Sandbox Keys</a>
						            </li>
						            <li class="nav-item">
						              <a class="nav-link" data-toggle="tab" href="#production" role="tab">Production Keys</a>
						            </li>
						        </ul>
						      </div> 
          <!-- Tab panes -->
          <div class="tab-content">       	
            <div class="tab-pane active" id="credential" role="tabpanel">
            		<c:if test="${Sandbox == null }">
                    	<h3 class="mt-1"><a href="" class="btn btn-darkblue pull-right btn-sm" data-toggle="modal" data-target="#create_credentials">Create Credentials</a></h3>
                    </c:if>
                    <table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
									<th>Type of Taxpayer</th>
                                    <th>Client ID</th>
                                    <th>Client Secret ID</th>
                                    <th>GST User ID</th>
                                    <th>GSTIN</th>
                                    <th> Status</th>
                                </tr>
                            </thead>
                            <tbody>
								<c:if test="${Sandbox != null }">
									<c:set var="isEnabled" value="Disabled"/>
									<c:if test="${Sandbox.isenabled}">
										<c:set var="isEnabled" value="Enabled"/>
									</c:if>		
				                    <tr>
									  <td>Normal Taxpayer</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstusername1}</td>
				                      <td>${Sandbox.gstinno1}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
				                    <tr>
				                      <td>Normal Taxpayer</td>
									  <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstusername2}</td>
				                      <td>${Sandbox.gstinno2}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
				                    <tr>
				                      <td>Normal Taxpayer</td>
									  <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstusername3}</td>
				                      <td>${Sandbox.gstinno3}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
				                    <tr>
				                      <td>Normal Taxpayer</td>
									  <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstusername4}</td>
				                      <td>${Sandbox.gstinno4}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									<c:if test="${Sandbox.gstcompusername1 != null && Sandbox.gstcompusername1 ne ''}">
									<tr>
									  <td>Composite User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstcompusername1}</td>
				                      <td>${Sandbox.compgstinno1}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gstcompusername2 != null && Sandbox.gstcompusername2 ne ''}">
									<tr>
									  <td>Composite User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstcompusername2}</td>
				                      <td>${Sandbox.compgstinno2}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gstcompusername3 != null && Sandbox.gstcompusername3 ne ''}">
									<tr>
									  <td>Composite User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstcompusername3}</td>
				                      <td>${Sandbox.compgstinno3}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gstcompusername4 != null && Sandbox.gstcompusername4 ne ''}">
									<tr>
									  <td>Composite User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstcompusername4}</td>
				                      <td>${Sandbox.compgstinno4}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gsttcsusername1 != null && Sandbox.gsttcsusername1 ne ''}">
									<tr>
									  <td>TCS User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gsttcsusername1}</td>
				                      <td>${Sandbox.tcsgstinno1}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gsttcsusername2 != null && Sandbox.gsttcsusername2 ne ''}">
									<tr>
									  <td>TCS User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gsttcsusername2}</td>
				                      <td>${Sandbox.tcsgstinno2}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gsttcsusername3 != null && Sandbox.gsttcsusername3 ne ''}">
									<tr>
									  <td>TCS User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gsttcsusername3}</td>
				                      <td>${Sandbox.tcsgstinno3}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gsttcsusername4 != null && Sandbox.gsttcsusername4 ne ''}">
									<tr>
									  <td>TCS User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gsttcsusername4}</td>
				                      <td>${Sandbox.tcsgstinno4}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gsttdsusername1 != null && Sandbox.gsttdsusername1 ne ''}">
									<tr>
				                      <td>TDS User</td>
									  <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gsttdsusername1}</td>
				                      <td>${Sandbox.tdsgstinno1}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gsttdsusername2 != null && Sandbox.gsttdsusername2 ne ''}">
									<tr>
				                      <td>TDS User</td>
									  <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gsttdsusername2}</td>
				                      <td>${Sandbox.tdsgstinno2}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gsttdsusername3 != null && Sandbox.gsttdsusername3 ne ''}">
									<tr>
				                      <td>TDS User</td>
									  <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gsttdsusername3}</td>
				                      <td>${Sandbox.tdsgstinno3}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gsttdsusername4 != null && Sandbox.gsttdsusername4 ne ''}">
									<tr>
									  <td>TDS User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gsttdsusername4}</td>
				                      <td>${Sandbox.tdsgstinno4}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gstisdusername1 != null && Sandbox.gstisdusername1 ne ''}">
									<tr>
									  <td>ISD User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstisdusername1}</td>
				                      <td>${Sandbox.isdgstinno1}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gstisdusername2 != null && Sandbox.gstisdusername2 ne ''}">
									<tr>
									  <td>ISD User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstisdusername2}</td>
				                      <td>${Sandbox.isdgstinno2}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gstisdusername3 != null && Sandbox.gstisdusername3 ne ''}">
									<tr>
									  <td>ISD User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstisdusername3}</td>
				                      <td>${Sandbox.isdgstinno3}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
									<c:if test="${Sandbox.gstisdusername4 != null && Sandbox.gstisdusername4 ne ''}">
									<tr>
									  <td>ISD User</td>
				                      <td>${Sandbox.clientid}</td>
				                      <td>${Sandbox.clientsecret}</td>
				                      <td>${Sandbox.gstisdusername4}</td>
				                      <td>${Sandbox.isdgstinno4}</td>
				                      <td>${isEnabled}</td>
				                    </tr>
									</c:if>
				                    </c:if>
                            </tbody>
                        </table>
            </div>
            <div class="tab-pane" id="production" role="tabpanel"> 
            <c:if test="${Production == null }">
            <p class="prolisthrd">Your Production keys are not yet generated, please contact <a href="mailto:sales@mastergst.com" data-rel="external">sales@mastergst.com</a> to get production keys access</p>
<div class="production-btn d-none">
														<p>
														<button class="permissionImport_Invoice button btn btn-blue" data-toggle="modal" data-target="#productionModal" onclick="getaspuserdetails('${id}')" style="margin-left: 190px;">Click Here To Apply Production Keys</button>
														</p>
													</div>
													<div class="alert alert-success d-none divalert">
															Thank you for submit the details, usually GTSN will take 1 to 10 working days to approve the Production keys. We will enable upon we get the details. Further details Please contact our support team info@mastergst.com or +91 7901022478 | 040-48531992	
													</div>           
		   <p><b>Check List for Production Access:</b> </p>
            
            <ol class="prokeylist">
                <li> First Name:</li>
                <li> Last Name:</li>
                <li>Email:</li>
                <li>Mobile Number:</li>
                <li>Company Name as Per Registered:</li>
                <li>Company Address: </li>
                <li>Certificate of incorporation:</li>
                <li>Company PAN card:</li>
                <li> Address Proof (like power bill, Bank statement, First page of bank pass book):</li>
                <li>Authorization letter on company letter head:</li>
                <li>Cancelled Bank check</li>
            </ol>
            </c:if>
             <c:if test="${Production != null }">
             <c:set var="isProdEnabled" value="Disabled"/>
				<c:if test="${Production.isenabled}">
				<c:set var="isProdEnabled" value="Enabled"/>
				</c:if>	
            <table id="dbTable-prod" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                    <th>Client ID</th>
                                    <th>Client Secret ID</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
			                    <tr>
			                      <td>${Production.clientid}</td>
			                      <td>${Production.clientsecret}</td>
			                      <td>${isProdEnabled}</td>
			                    </tr>
                            </tbody>
                        </table>
            </c:if>
            </div>
          </div>
              </div>                   
              </div>            
                                      <!--   profile table end -->
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- end content  -->
            </div>
            </div>
<!--ggg-->
<c:if test="${otheruser=='no'}">
<div class="modal fade" id="create_credentials" tabindex="-1" role="dialog" aria-labelledby="create_credentials" aria-hidden="true">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
                    <div class="bluehdr">
                    <h5 class="modal-title" id="create_credentials">Sandbox Credentials</h5></div>
                    <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"><img src="${contextPath}/static/mastergst/images/credentials/closebtn.png" alt="Close" class="cross" /></span> </button>
                </div>
                <form:form method="POST" name="meterialform" class="meterialform" action="createcredentials" modelAttribute="userkeys">
                 <input type="hidden" name="creType" value="${creType}"/>
                <div class="modal-body">
<!--                     <form class="meterialform"> -->
                        <div class="labletxt">Name</div>
                        <div class="form-group">
                            <input type="text" required="required" name="keyname">
                            <label for="input" class="control-label"></label> <i class="bar"></i> </div>
                        <div class="labletxt">Create Date</div>
                        <div class="form-group">
                             <input type="text" required="required" name="createdate" id="datetimepicker" value=<%=strDate%>  readonly>
                            <label for="input" class="control-label"></label> <i class="bar"></i> </div>
                        <div class="labletxt">Client ID</div>
                        <div class="form-group">
                             <input type="text" required="required" id="clientid" name="clientid" value="${varClientId}"  readonly>
                            <label for="input" class="control-label"></label> <i class="bar"></i> </div>
                        <div class="labletxt">Client Secret</div>
                        <div class="form-group">
                             <input type="text" required="required" id="clientsecret" name="clientsecret" value="${varClientSecret}"  readonly>
                            <label for="input" class="control-label"></label> <i class="bar"></i> </div>
                       <div class="form-group">
                            <div class="checkbox">
                                    <label>
                                        <input type="checkbox" id="checkme"/> <i class="helper"></i>Please confirm, you have created Sandbox Account with GSTN using <a href="https://docs.google.com/forms/d/e/1FAIpQLSfyJIrHuycgF1jiD9P47qjdiFH2Nmvba_1a9WnBLfgw_VSaYw/viewform">google form</a></label>
                                </div></div>
								<input type="hidden" name="stage" value="Sandbox"/>
                         <input type="hidden" name="hiddenid" value=<c:out value="${id}"/>>
<!--                     </form> -->
                 </div>
                <div class="modal-footer text-center crde-keys">
                    <button type="button" class="btn btn-md btn-modeldarkblue m-auto" data-dismiss="modal" disabled="disabled" id="sendNewSms" value=" Send " onclick="document.meterialform.submit();" >CREATE</button>
<!--                      <button type="button" class="btn btn-md btn-modeldarkblue m-auto" data-dismiss="modal" disabled="disabled" id="sendNewSms" value=" Send " onclick="document.meterialform.submit();" >CREATE</button>  -->
               		
                </div>
                </form:form>
            </div>
        </div>
    </div>
    </c:if>
	<div class="modal fade" id="productionModal" role="dialog" aria-labelledby="productionModal" aria-hidden="true">
	<div class="modal-dialog modal-md modal-right" role="document">
    	<div class="modal-content" id="idproductionBody">
		<!-- <form id="frmdata" data-toggle="validator" action="" name=""> -->
        	<div class="modal-body">
            	<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                	<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                </button>
                <div class="production-hdr bluehdr">
                	<h3 id="productionModalTitle">GST API Production Keys Request</h3>
                </div><br/>
				<div class="alert alert-success d-none divalert" style="width: 96%;margin-left: 14px;padding: 5px;">
					Thank you for submit the details, usually GTSN will take 1 to 10 working days to approve the Production keys. We will enable upon we get the details. Further details Please contact our support team info@mastergst.com or +91 7901022478 | 040-48531992	
				</div>
				<button type="button" class="btn btn-lg btn-blue d-none" id="close-success" style="position:absolute;top:50%;left:44%" data-dismiss="modal">Close</button>
				<div class="container prosuccess">
				<div class="alert alert-success"><p class="mb-0">1. The below details you enter her will be submitted to GSTN and please provide the Authorised person details from your organisation.</p>
					<p>2. We have Auto-populated based on available information and please cross check once and submit the details.</p>
				</div>
					<input type="hidden" id="currentuserid" name="userid" value="${id}"/>	
					<div class="row">		
						<div class="form-group col-md-6 col-sm-12">
							<div class="row">
								<label class="col-md-4 astrich">First Name</label>
								<div class="col-md-8" id="prodIsEnabled">          
									<input type="text" name="firstName" id="firstName" data-error="Please Enter First Name" class="form-control txtbx-production" placeholder="First Name" required="required" value="${fname}">
								</div>
							</div>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<div class="row">
								<label class="col-md-4 astrich">Last Name</label>
								<div class="col-md-8" id="prodIsEnabled">          
									<input type="text" name="lastName" id="lastName" data-error="Please Enter Second Name" class="form-control txtbx-production" placeholder="Last Name" required="required" value="${lname}">
								</div>
							</div>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<div class="row">
								<label class="col-md-4 astrich">Email</label>
								<div class="col-md-8" id="prodIsEnabled">          
									<input type="text" name="email" id="email" data-error="Please Enter Email" class="form-control txtbx-production" placeholder="abcd@gmail.com" required="required" value="${mail}">
									</div>
							</div>
						</div>					
						<div class="form-group col-md-6 col-sm-12">
							<div class="row">
								<label class="col-md-4 astrich">Mobile Number</label>
									<div class="col-md-8" id="prodIsEnabled">          
										<input type="text" name="mobileNum" id="mobileNum" data-error="Please Enter Mobile Number" class="form-control txtbx-production" placeholder="Mobile Number" required="required" value="${mobilenumber}">
									</div>
							</div>
						</div>						
						<div class="form-group col-md-6 col-sm-12">
							<div class="row">
								<label class="col-md-4 astrich">Company Name as Per Registered</label>
								<div class="col-md-8" id="prodIsEnabled">          
									<input type="text" name="companyRegisterName" id="companyRegisterName" class="form-control txtbx-production" placeholder="Company Name" required="required">
								</div>
							</div>
						</div>
						<div class="form-group col-md-12 col-sm-12">
							<div class="row">
								<label class="col-md-2 astrich">Company Address</label>
									<div class="col-md-8" id="prodIsEnabled">          
										<textarea id="companyAddress" name="companyAddress" class="form-control" style="margin-top: 0px;margin-bottom: 0px;height: 91px;width: 120%;" placeholder="Yours Company Address" required="required">${address}</textarea> 
									</div>
							</div>
						</div>
					</div>
                  </div>
        </div>
		<div class="modal-footer">			  
			<button type="submit" class="btn  btn-blue-dark save-btn" id="cmmnt_save_btn" onclick="saveProductionData('GST_API')">submit</button>
			<button type="button" class="btn btn-lg btn-blue close-bttn" data-dismiss="modal">Close</button>
		</div>
							
		</div>
  </div>
   </div>
  

    <!-- Create credentials Modal end -->
    <!-- footer begin here -->
 <%-- <%@include file="/WEB-INF/views/includes/footer.jsp" %> --%>
<!-- footer end here -->
    <script type="text/javascript">
        //button disable function
        var checker = document.getElementById('checkme');
 var sendbtn = document.getElementById('sendNewSms');
 // when unchecked or checked, run the function
 checker.onchange = function(){
if(this.checked){
    sendbtn.disabled = false;
} else {
    sendbtn.disabled = true;
}

}

/*--- datatable script-----*/
        var table = $('table.display').DataTable({
            dom: 'Bfrtip',
            "pageLength": 5,
            "oSearch": false,
            "language": {
                "paginate": {
                    "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
                    "next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
                }
            }
        });
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
        /*---date picker
        $('#datetimepicker').datetimepicker({
            timepicker: false,
            format: 'd-m-Y'
        }); ---*/
    </script>
    
<script type="text/javascript">
  
$(document).ready(function(){
	
	var userid='<c:out value="${id}"/>';	
	$.ajax({
		type : "GET",
		url : '${contextPath}/getAspUserDetails/'+userid,
		contentType : "application/json",
    	success : function(data) {
    		if(data == ""){
    			$('.production-btn').removeClass('d-none');
    		}else{
    			$('.divalert').removeClass('d-none');
    			$('.production-btn').addClass('d-none');
       		}
    	},
    	error:function(errorres){}	
	 });
	
});
   function getaspuserdetails(userid){
    	$.ajax({
			type : "GET",
			url : '${contextPath}/getAspUserDetails/'+userid,
			contentType : "application/json",
	    	success : function(data) {
	    		
	    		if(data == ""){
	    			$('.save-btn').removeClass('d-none');
	    		}else{
	    			$('.save-btn').addClass('d-none');
    	        	$('.divalert').removeClass('d-none');	    			
	    		}
	    	},
	    	error:function(errorres){}	
		 });   	
   }
   function saveProductionData(apitype){
	   	var fname=$('#firstName').val();
    	var lname=$('#lastName').val();
    	var email=$('#email').val();
    	var mobileNum=$('#mobileNum').val();
    	var cmpnyRegName=$('#companyRegisterName').val(); 	
    	var companyAddress=$('#companyAddress').val();    	
    	var userid=$('#currentuserid').val();
    	
    	var apiuserdata=new Object();
    	apiuserdata.firstName=fname;
    	apiuserdata.lastName=lname;
    	apiuserdata.email=email;
    	apiuserdata.mobileNumber=mobileNum;
    	apiuserdata.companyRegisterName=cmpnyRegName;
    	apiuserdata.companyAddress=companyAddress; 
    	apiuserdata.userid = userid;
    	apiuserdata.apitype = apitype;
    	if(fname!="" && lname!="" && email!="" && mobileNum!="" && cmpnyRegName!="" && companyAddress!=""){
    		 $.ajax({
    			type : "POST",
    			url : '${contextPath}/saveapiuserdetails/'+userid,
    			data : JSON.stringify(apiuserdata),
    			contentType: "application/json; charset=utf-8",
    	    	success : function(data) {
    	    		if(data=='success'){
	    	    		$('#firstName').val('');
	    	        	$('#lastName').val('');
	    	        	$('#email').val('');
	    	        	$('#mobileNum').val('');
	    	        	$('#companyRegisterName').val(''); 	
	    	        	$('#companyAddress').val('');
	    	        	$('.save-btn').addClass('d-none');
	    	        	$('.divalert').removeClass('d-none');
	    	        	$('.production-btn').addClass('d-none');
	    	        	$('.container.prosuccess').addClass('d-none');
	    	        	$('#close-success').removeClass('d-none');
	    	        	$('.close-bttn').addClass('d-none');
	    	        	
	    	        	
	    	          /*   $('button.btn.btn-lg.btn-blue.close').css('position':'absolute');
	    	        	$('button.btn.btn-lg.btn-blue.close').css('top':'50%');
	    	        	$('button.btn.btn-lg.btn-blue.close').css('left':'44%');  */
    	    		}
    	    	},
    	    	error:function(dat){}	
    		 });
    	}else{
    		
    	}
   }
    </script> 
</body>
</html>