	<!-- uploadOtpModal Start -->
	<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<c:set var="varsGSTR1" value="<%=com.mastergst.core.common.MasterGSTConstants.GSTR1%>"/>
<c:set var="varsGSTR2" value="<%=com.mastergst.core.common.MasterGSTConstants.GSTR2%>"/>
<c:set var="varsGSTR3B" value="<%=com.mastergst.core.common.MasterGSTConstants.GSTR3B%>"/>
<c:set var="varsGSTR4" value="<%=com.mastergst.core.common.MasterGSTConstants.GSTR4%>"/>
<c:set var="varsGSTR6" value="<%=com.mastergst.core.common.MasterGSTConstants.GSTR6%>"/>
<c:set var="varsGSTR5" value="<%=com.mastergst.core.common.MasterGSTConstants.GSTR5%>"/>
	<div class="modal fade" id="uploadOtpModal" tabindex="-1" role="dialog" aria-labelledby="uploadOtpModal" aria-hidden="true">
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
						  <a href="https://services.gst.gov.in/services/login" target="_blank" class="btn btn-blue-dark btn-sm pull-left">Enable API Access</a>
						  <a href="${contextPath}/static/mastergst/Enable-API-Access-On-GST-Portal.pdf" target="_blank" class="btn btn-sm btn-green pull-right">See Help Guide</a>
						  </span>
                         <div class=" mb-3" style="border-bottom:1px solid #f5f5f5;width:100%;">&nbsp;</div> 
						<h6><span class="steptext"><strong><u>Step 2 </u> : </strong></span> Verify GSTIN User Name</h6> 					  
                            <div class="col-md-12 col-sm-12 m-auto p-0">
                                <div class="formbox otpbox mt-3">
                                    <form class="meterialform row" id="otpEntryForm">
										<span class="steptext">&nbsp;</span>
										<div class="col-md-5 col-sm-12">
                                                <div class="lable-txt">Enter Your GSTIN Login/User Name</div>
                                                <div class="form-group">
                                                    <span class="errormsg" id="gstnUserIdMsg"></span>
                                                    <input type="text" id="gstnUserId" name="gstnUserId" required="required" aria-describedby="gstnUserId" placeholder="GSTIN Login/User Name">
                                                    <label for="input" class="control-label"></label>
                                                    <i class="bar"></i> </div>
                                            </div>
											<div class="col-md-4 col-sm-12">
												<a href="#" onclick="showOtp();" class="btn btn-red btn-sm mt-4">Verify Now</a>
											</div>	
									<div class="whitebg gstn-otp-wrap">                                 
									<h5><span class="steptext"><strong><u>Step 3 </u> : </strong></span>GSTN has sent you an OTP please enter here for verification.</h5>                                          
                                            <!-- serverside error begin -->
                                            <div class="errormsg mt-2"> </div>
                                            <!-- serverside error end -->
                                            <div class="otp_form_input">
											<div class="errormsg" id="otp_Msg"></div>  
											<div class=" col-12" style="display:block"></div>
											<div class="row">
													<span class="steptext col-sm-12">&nbsp;</span>
													<div class="col-md-9 col-sm-12">
													<input type="text" name="otp" class="form-control invoice_otp" id="otp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="11" placeholder="0"/>
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
													<div class="col-md-3 col-sm-12 pull-right">
													 <a href="#" onClick="validOtp()" class="btn btn-blue btn-sm btn-verify">Submit</a> 
													 </div> 
													 <h6 class="col-md-9 col-sm-12 mt-3">Didn&acute;t  Receive OTP? <a href="">try again</a></h6>
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
												<input type="button" value="Close" class="btn btn-red" data-dismiss="modal" aria-label="Close" />
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
	<!-- uploadOtpModal End -->
	<!-- downloadOtpModal Start -->
	<div class="modal fade" id="downloadOtpModal" role="dialog" aria-labelledby="downloadOtpModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" id="downloadOtpModalClose" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
					</button>
					<div class="invoice-hdr bluehdr">
						<h3>Verify OTP</h3>
					</div>
					<div class=" p-4" style="min-height:600px;">
						<div class="formboxwrap">
							<h3> Filing GST Made Simple, & Pay your Tax easily </h3>
							<h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
							<div class="col-md-12 col-sm-12 m-auto" style="text-align:center">
								<div class="formbox otpbox">
									<form class="meterialform" id="dwnldOtpEntryForm" data-toggle="validator">
										<div class="whitebg">
											<h2> Verify Mobile Number</h2>
											<h6>OTP has been sent to your GSTIN registered mobile number & e-mail, Please enter the same below
											</h6>
											<!-- serverside error begin -->                    
											<div class="errormsg"> </div>
											<span class="errormsg" id="dotp_Msg"></span>
											<!-- serverside error end --> 
											<div class="col-sm-12 otp_form_input" style="display:block;margin-top:30px">
												<div class="">
													<div class=""></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="1" placeholder="0" />
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="2" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="3" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="4" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="5" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="6" placeholder="0"/>
													<div class="help-block with-errors"></div>
												</div>
												<h6 style="display:inline-block">Didn&acute;t receive OTP? <a href="#" onClick="otpTryAgain()">try again</a></h6>
											</div>
										</div>
										<div class="p-2 text-center">
											<p><a href="#" onclick="validateDownloadOtp()" class="btn btn-lg btn-blue btn-verify">Verify OTP</a></p>
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
	<!-- downloadOtpModal End -->
	<!-- evcOtpModal Start -->
	<div class="modal fade" id="evcOtpModal" role="dialog" aria-labelledby="evcOtpModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" id="evcOtpModalClose" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
					</button>
					<div class="invoice-hdr bluehdr">
						<h3>Submit EVC OTP</h3>
					</div>
					<div class=" p-4" style="min-height:600px;">
						<div class="formboxwrap">
							<h3> Filing GST Made Simple, & Pay your Tax easily </h3>
							<h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
							<div class="col-md-12 col-sm-12 m-auto" style="text-align:center">
								<div class="formbox otpbox">
									<form class="meterialform" id="evcOtpEntryForm" data-toggle="validator">
										<div class="whitebg">
											<h2> Verify EVC OTP</h2>
											<h6>OTP has been sent to your PAN registered mobile number & e-mail, Please enter the same below
											</h6>
											<!-- serverside error begin -->                    
											<div class="errormsg"> </div>
											<!-- serverside error end --> 
											<div class="col-sm-12">
												<div class="">
													<div class="errormsg"></div>
													<div class=""></div>
													<input type="text" class="form-control" id="evcotp1" required="required"  data-minlength="4" maxlength="6" pattern="[A-Za-z0-9]+" data-error="Please enter valid otp number" tabindex="1" placeholder="0" />
													<div class="help-block with-errors"></div>
												</div>
												<h6 style="display:inline-block">Didn't receive OTP? <a href="">try again</a></h6>
											</div>
										</div>
										<div class="p-2 text-center">
											<p><a href="#" onclick="fileEVC()" class="btn btn-lg btn-blue btn-verify">Submit</a></p>
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
	<!-- evcOtpModal End -->
	<!-- Delete Modal -->

<div class="modal fade bd-example-modal-lg" tabindex="-1" id="reconcilemodal" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" style="top:10%">
    <div class="modal-content p-0" >
    <div class="modal-body p-0">
        <button type="button" class="close reconcileclose" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr reconcilehdr">
          <h3>Reconcile Summary</h3>
        </div>
      		<div class="invoicesumwrap p-3">
      			<ul class="invoicesum row">
					<li class="col-md-4 matched"><h5 class="">Matched</h5><p><span id="totalMatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Matched')">View Details</a></p></li>
					<li class="col-md-4 matched"><h5 class="">Matched In Other Months</h5><p><span id="totalMatchedInvoicesInOtherMonths" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Matched In Other Months')">View Details</a></p></li>
					<li class="col-md-4 matched"><h5>Roundoff Matched</h5><p><span id="totalRoundoffMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Round Off Matched')">View Details</a></p></li>
				</ul>
				<ul class="invoicesum row">
					<li class="col-md-4 matched"><h5>Probable Matched</h5><p><span id="totalProbableMatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Probable Matched')">View Details</a></p></li>
					<li class="col-md-4 mis-matched"> <h5> Not In GSTR 2A</h5><p><span id="totalNotInGstr2AInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Not In GSTR 2A')">View Details</a></p></li>
					<li class="col-md-4 mis-matched"> <h5> Not In Purchases</h5><p><span id="totalNotInPurchasesInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Not In Purchases')">View Details</a></p></li>
				</ul>
				<ul class="invoicesum row">
					<li class="col-md-4 mis-matched"> <h5>Mismatched</h5><p><span id="totalMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Mismatched')">View Details</a></p></li>
					<li class="col-md-4 par-matched"><h5> Invoice No Mismatched</h5><p><span id="totalInvoiceNoMismatchInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Invoice No Mismatched')">View Details</a></p></li>
					<li class="col-md-4 par-matched"><h5> Tax Mismatched</h5><p><span id="totalTaxMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Tax Mismatched')">View Details</a></p></li>
				</ul>
				<ul class="invoicesum row">
					 <li class="col-md-4 par-matched"><h5> Invoice Value Mismatched</h5><p><span id="totalInvoiceValueMismatchedInvoices" style="margin-right: 35px;">0</span>| <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Invoice Value Mismatched')">View Details</a></p></li>
					 <li class="col-md-4 par-matched"><h5> GST No Mismatched</h5><p><span id="totalGSTnoMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('GST No Mismatched')">View Details</a></p></li>
					 <li class="col-md-4 par-matched"><h5>Invoice Date Mismatched</h5><p><span id="totalInvoiceDateMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Invoice Date Mismatched')">View Details</a></p></li>	
				</ul>
				<ul class="invoicesum row">
					 <li class="col-md-4 matched"><h5> Manual Matched</h5><p><span id="totalMannualMatchedInvoices" style="margin-right: 35px;">0</span>| <a href="#" class="sdsd" id="122" onclick="updateReconcileFilterDetails('Manual Matched')">View Details</a></p></li>
					 	
				</ul>
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-blue-dark" data-dismiss="modal">Continue</button>
        <button type="button" class="btn btn-blue-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade bd-example-modal-lg" tabindex="-1" id="reconcile2bmodal" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" style="top:10%">
    <div class="modal-content p-0" >
    <div class="modal-body p-0">
        <button type="button" class="close reconcile2bmodal" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr reconcilehdr">
          <h3>Reconcile Summary</h3>
        </div>
      		<div class="invoicesumwrap p-3">
      			<ul class="invoicesum row">
					<li class="col-md-4 matched"><h5 class="">Matched</h5><p><span id="RecMatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" onclick="updateReconcileDetailsGSTR2B('Matched')">View Details</a></p></li>
					<li class="col-md-4 matched"><h5 class="">Matched In Other Months</h5><p><span id="RecMatchedInvoicesInOtherMonths" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" onclick="updateReconcileDetailsGSTR2B('Matched In Other Months')">View Details</a></p></li>
					<li class="col-md-4 matched"><h5>Roundoff Matched</h5><p><span id="RecRoundoffMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd"  onclick="updateReconcileDetailsGSTR2B('Round Off Matched')">View Details</a></p></li>
				</ul>
				<ul class="invoicesum row">
					<li class="col-md-4 matched"><h5>Probable Matched</h5><p><span id="RecProbableMatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" onclick="updateReconcileDetailsGSTR2B('Probable Matched')">View Details</a></p></li>
					<li class="col-md-4 mis-matched"> <h5> Not In GSTR2B</h5><p><span id="RecNotInGstr2AInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" onclick="updateReconcileDetailsGSTR2B('Not In GSTR2B')">View Details</a></p></li>
					<li class="col-md-4 mis-matched"> <h5> Not In Purchases</h5><p><span id="RecNotInPurchasesInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd"  onclick="updateReconcileDetailsGSTR2B('Not In Purchases')">View Details</a></p></li>
				</ul>
				<ul class="invoicesum row">
					<li class="col-md-4 mis-matched"> <h5>Mismatched</h5><p><span id="RecMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd"  onclick="updateReconcileDetailsGSTR2B('Mismatched')">View Details</a></p></li>
					<li class="col-md-4 par-matched"><h5> Invoice No Mismatched</h5><p><span id="RecInvoiceNoMismatchInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd"  onclick="updateReconcileDetailsGSTR2B('Invoice No Mismatched')">View Details</a></p></li>
					<li class="col-md-4 par-matched"><h5> Tax Mismatched</h5><p><span id="RecTaxMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd"  onclick="updateReconcileDetailsGSTR2B('Tax Mismatched')">View Details</a></p></li>
				</ul>
				<ul class="invoicesum row">
					 <li class="col-md-4 par-matched"><h5> Invoice Value Mismatched</h5><p><span id="RecInvoiceValueMismatchedInvoices" style="margin-right: 35px;">0</span>| <a href="#" class="sdsd"  onclick="updateReconcileDetailsGSTR2B('Invoice Value Mismatched')">View Details</a></p></li>
					 <li class="col-md-4 par-matched"><h5> GST No Mismatched</h5><p><span id="RecGSTnoMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" onclick="updateReconcileDetailsGSTR2B('GST No Mismatched')">View Details</a></p></li>
					 <li class="col-md-4 par-matched"><h5>Invoice Date Mismatched</h5><p><span id="RecInvoiceDateMismatchedInvoices" style="margin-right: 35px;">0</span> | <a href="#" class="sdsd" onclick="updateReconcileDetailsGSTR2B('Invoice Date Mismatched')">View Details</a></p></li>	
				</ul>
				<ul class="invoicesum row">
					 <li class="col-md-4 matched"><h5> Manual Matched</h5><p><span id="RecMannualMatchedInvoices" style="margin-right: 35px;">0</span>| <a href="#" class="sdsd" onclick="updateReconcileDetailsGSTR2B('Manual Matched')">View Details</a></p></li>
				</ul>
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-blue-dark" data-dismiss="modal">Continue</button>
        <button type="button" class="btn btn-blue-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>


<!-- Delete Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete <span class="invOrExpenseTypeHead">invoice</span> </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete <span class="invOrExpenseType">invoice</span><span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once <span class="invOrExpenseTypeNote">invoices</span> are deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete <span class="invOrExpenseTypeBtn">invoice</span></button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<!-- Submit Invoice Modal -->
<div class="modal fade" id="submitInvModal" tabindex="-1" role="dialog" aria-labelledby="submitInvModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Submit</h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>I acknowledge that I have reviewed the details of the preview and the information is correct and would like to submit the details. I am aware that no changes can be made after submit.</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once invoices are submitted, it cannot be modified.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnSubmitInv" data-dismiss="modal">Submit</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
      </div>
    </div>
  </div>
</div>
<!-- Offset Liability Modal -->
<div class="modal fade" id="offLiabModal" tabindex="-1" role="dialog" aria-labelledby="offLiabModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Offset</h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>I acknowledge that I have reviewed the details of the preview and the information is correct and would like to offset liability/latefee details. I am aware that no changes can be made after Offset.</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once invoices are offset, it cannot be modified.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnOffLiabInv" data-dismiss="modal">Offset</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="claimModal" tabindex="-1" role="dialog" aria-labelledby="claimModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Claim ITC on Multiple Invoices </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
         <div class="row">
       
         <label class="label-txt col-md-4 col-sm-12">Input Type</label>
         <select class="form-control col-md-6 col-sm-12" id="inputtype">
        <option value="">- Input Type -</option>
		<option value="cp">Capital Good</option>
		<option value="ip">Inputs</option>
		<option value="is">Input Services</option>
		<option value="no">Ineligible</option>
		<option value="pending">Pending</option> 
         </select>
      
        
         <label class="label-txt col-md-4 col-sm-12 mt-2">ITC Claimed In</label>
         <input  type="text" id="" class="form-control allitcclaimdate col-md-6 col-sm-12 mt-2" aria-describedby="allitcclaimdate" placeholder="dd/mm/yyyy">
    
         
         <label class="label-txt col-md-4 col-sm-12 mt-2">ITC Amount</label>
         <input id="itcamount" type="text"  class="form-control itc_amount col-md-2 col-sm-12 mt-2" maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)"  onkeyup="itcclaim()" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeyup="itcclaim()"  value="100" style="height:30px;">
 		<span class="" style="padding: 2px;margin-top:9px">%</span>
       
         </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-blue-dark" id="btnOK" data-dismiss="modal" style="padding:7px 20px;" onclick="claimallinvoices()">Claim Now</button>
        <button type="button" class="btn btn-blue-dark" data-dismiss="modal"  style="padding:7px 20px;">Cancel</button>
      </div>
    </div>
  </div>
</div>
<!-- Help Modal -->
<div class="modal fade" id="helpGuideModal" tabindex="-1" role="dialog" aria-labelledby="helpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Help To File </h3>
        </div>
        <div class=" p-2 steptext-wrap">
<ul>
                <li><span class="steptext">Step 1:</span> <span class="steptext-desc">Review ${returntype} Summary</span></li><li> 
<span class="steptext"> Step 2:</span> <span class="steptext-desc">Click on "Submit ${returntype}". Please Note, Once you Submit the Invoices, you can't make any changes to the invoices</span></li>
<li><span class="steptext">Step 3:</span> <span class="steptext-desc">Click on "File ${returntype} with Digital Signature (DSC)", Please login to your Digital Signature to file.</span>
</li>                
 </ul> 	
 <p style="text-align:center">For more information, please click <c:choose><c:when test="${returntype eq varsGSTR1}"><a href="https://www.mastergst.com/user-guide/how-to-add-sales-invoice.html" target="_blank">here</a></c:when><c:when test="${returntype eq varsGSTR2}"><a href="https://www.mastergst.com/user-guide/how-to-add-purchase-invoice.html" target="_blank">here</a></c:when><c:when test="${returntype eq varsGSTR3B}"><a href="https://www.mastergst.com/user-guide/how-to-add-gstr3b-invoice.html" target="_blank">here</a></c:when><c:when test="${returntype eq varsGSTR4}"><a href="https://www.mastergst.com/user-guide/how-to-file-gstr4-in-mastergst.html" target="_blank">here</a></c:when> <c:when test="${returntype eq varsGSTR5}"><a href="https://www.mastergst.com/user-guide/how-to-file-gstr5-in-mastergst.html" target="_blank">here</a></c:when>  <c:when test="${returntype eq varsGSTR6}"><a href="https://www.mastergst.com/user-guide/how-to-file-gstr6-in-mastergst.html" target="_blank">here</a></c:when> <c:otherwise><a href="https://www.mastergst.com/user-guide/user_guide.html"></a> </c:otherwise></c:choose> </p>			
         </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="fileReturnModal" tabindex="-1" role="dialog" aria-labelledby="fileReturnModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Pre-requisites for DSC Filing</h3>
        </div>
        <div class=" p-2 steptext-wrap">
<ul>
                <li><span class="steptext-desc">1) Install Digital Signature Software, <a href="https://files.truecopy.in/downloads/lh/latest/DSCSignerLH.msi">Click Here</a> to download & install</span></li>
				<li><span class="steptext-desc">2) Make sure Digital Signature software is running in your system</span></li>
				<li><span class="steptext-desc">3) Make Sure ePass Application is Running in your System</span></li>
				<li><span class="steptext-desc">3) Login to ePass Application</span></li>
 </ul>
        <%--  <ul><li>==================================================</li></ul>
		<ul>
		<li><span class="steptext">Step 1:</span> <span class="steptext-desc"> Certificate verification</span></li><li> 
<span class="steptext"> Step 2:</span> <span class="steptext-desc"> Sign the invoices</span></li>
<li><span class="steptext">Step 3:</span> <span class="steptext-desc"> Filing of invoices</span>
</li>
 </ul> --%>
         </div>
      </div>
      <div class="modal-footer">
		<button type="button" class="btn btn-secondary" onclick="trueCopyFiling()" data-dismiss="modal">File Now</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="reconModal" role="dialog" aria-labelledby="reconModal" aria-hidden="true"><div class="modal-dialog modal-lg" role="document" style="height: 100%;">
			<div class="modal-content p-0" id="reconModalbody" style="height: auto;">
				<div class="modal-body p-0">
            	<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                <div class="production-hdr bluehdr"><h3 id="productionModalTitle">Manual Matching</h3></div>                
                <h6 id="invhdr">Record In Purchase</h6>
                <table class="row-border dataTable meterialform" id="mainMatch_table5" style="width:100%;border: 1px solid #ddd!important">
                <thead><tr><th>Type</th><th>Suppliers</th><th>Invoice Number</th><th>Invoice Date</th><th>GST No</th><th>Taxable Amt</th><th>Total Tax</th></tr></thead>
                <tbody id="mainMatchInvoices"></tbody>
                </table>
                
                <h6 id="dinvhdr">Record In GSTRA</h6>
                <div class="form-check mb-2 mb-sm-0 mannualfilter customtable"><div class="meterialform"><span style="font-size: 15px; font-weight: bold; color:grey">Filter By: </span><div class="checkbox"><label id="mannual_Gstno" style="font-size: 15px; font-weight: bold; color:grey"><input type="checkbox" id="mannualGstno" class="mannualGstno" onclick="mannualMatchInvoiceFilterValues()"><i class="helper che-box" style="top:2px; left:5px"></i>Same GSTN Number</label></div> <div class="checkbox"><label id="mannual_Invoice_Number" style="font-size: 15px; font-weight: bold; color:grey"><input type="checkbox" id="mannualInvoiceNumber" class="mannualInvoiceNumber" onclick="mannualMatchInvoiceFilterValues()"><i class="helper che-box" style="top:2px; left:5px"></i>Same Invoice Number</label></div> <div class="checkbox"><label id="mannual_Invoice_Date" style="font-size: 15px; font-weight: bold; color:grey"><input type="checkbox"  id="mannualInvoiceDate" class="mannualInvoiceDate" onclick="mannualMatchInvoiceFilterValues()"><i class="helper che-box" style="top:2px; left:5px"></i>Same Invoice Date</label></div></div></div>
                
                <div class="pl-5 pr-5 pt-0 row customtable">
                <table class="row-border dataTable meterialform" id="mannualMatch_table5" style="width:100%;border: 1px solid #ddd!important">
                <thead><tr><th class="text-center"><div class="checkbox"><label><input type="checkbox" id="checkMismatch" onClick="updateMainMannualSelection(this)"/><i class="helper"></i></label></div> </th><th style="width: 20%;">Type</th><th>Suppliers</th><th>Invoice Number</th><th>Invoice Date</th><th>GST No</th><th>Taxable Amt</th><th>Total Tax</th></tr></thead>
                <tbody id="MannulaMatchInvoices"></tbody>
                </table>
			</div></div>	
			<input type="hidden" id="mannualHiddenInvoiceid"><input type="hidden" id="mannualHiddenReturnType">	
			<div class="modal-footer pt-0"><button type="button" class="btn  btn-blue-dark save-btn" id="message_send_btn" <c:choose><c:when test="${yearlyreconcilem eq 'yearly'}">onclick="updateMannualMatchData(this,'yearly')"</c:when><c:otherwise>onclick="updateMannualMatchData(this,'monthly')"</c:otherwise></c:choose>>Match Now</button><button type="button" class="btn btn-blue-dark close-bttn" data-dismiss="modal">Close</button>
			</div></div></div></div>
			<div class="modal fade" id="viewMannualMatchModal" role="dialog" aria-labelledby="viewMannualMatchModal" aria-hidden="true"><div class="modal-dialog modal-lg" role="document" style="height: 100%;"><div class="modal-content p-0" id="viewMannualMatchModalbody" style="height: auto;"><div class="modal-body p-0">
            	<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                <div class="production-hdr bluehdr"><h3 id="productionModalTitle">Matched Invoices</h3></div>
                <h6 id="vinvhdr">Record In Purchase</h6>
                <table class="row-border dataTable meterialform" id="mainvMatch_table5" style="width:100%;border: 1px solid #ddd!important">
                <thead><tr><th>Type</th><th>Suppliers</th><th>Invoice Number</th><th>Invoice Date</th><th>GST No</th><th>Taxable Amt</th><th>Total Tax</th></tr></thead>
                <tbody id="mainvMatchInvoices"></tbody>
                </table>
                <h6 id="vdinvhdr">Record In GSTR2A</h6>
                <div class="p-5 row customtable">
                <table class="row-border dataTable meterialform" id="viewmannualMatch_table5" style="width:100%;border: 1px solid #ddd!important">
                <thead><tr><th style="width: 20%;">Type</th><th>Suppliers</th><th>Invoice Number</th><th>Invoice Date</th><th>GST No</th><th>Taxable Amt</th><th>Total Tax</th></tr></thead>
                <tbody id="viewMannulaMatchInvoices"></tbody>
                </table>
			</div></div></div></div></div>
			
			 <div class="modal fade" id="sendMessageModal" role="dialog" aria-labelledby="sendMessageModal" aria-hidden="true"><div class="modal-dialog modal-md modal-right" role="document" style="height: 100%;"><div class="modal-content" id="idSendMessageBody" style="height: 100%;"><div class="modal-body">
            	<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                <div class="production-hdr bluehdr"><h3 id="productionModalTitle">SEND MEASSAGE</h3></div>
             	<div class="p-3 row"><div class="form-group"><h6><span id="errormsg" class="text-danger"></span></h6></div> 
  				<div class="form-group successmsg d-none"><h6><i class="fa fa-check" style="font-size:32px;color:green"></i><span id="successmsg" class="text-success"></span></h6></div>
			<div class="col-md-11 mb-1 f-12" style="color:blue;text-align:right;">You can Add List of mail id's by Comma Separated Values</div>
			<div class="form-group col-md-12"><label for="supplier_fullname">Supplier Name :</label><input type="text" class="form-control" id="supplier_fullname" style="width:75%;display: inline-block;margin-left: 13px;"></div>
				<div class="sendmessage col-md-12">
				<div class="form-inline col-md-12 pl-0 pr-0">
				<label for="emailid">Supplier Email id<span class="coln-txt" style="margin-right:5px;">:</span></label>
				<input type="text" class="subject form-control" id="supplier_emailid" style="width:75%;">
				</div></div>	
				<div class="form-inline col-md-12" style="line-height: 60px;">
				<label for="ccemailids">CC<span class="coln-txt" style="margin-right:5px;margin-left:95px;">:</span></label>
				<input type="text" class="sup_ccemailids form-control" id="supplier_ccemailids" style="width:75%;">
				</div>
					<div class="form-inline col-md-12" style="line-height: 35px;"><label for="emailSubject">Email Subject<span class="coln-txt" style="margin-left:20px;margin-right:5px;">:</span></label><div class="" id="messagesubject" style="width:79%;"><input type="text" class="subject form-control" id="subject" style="width:95%;"></div></div>		
				<div class="form-group mt-3 col-md-12"><label for="emailMeassage">Message :</label><textarea class="form-control" id="meassagedetail" style="width:96%; height:110px"></textarea></div>
			<div style="width:100%;"><span id="msgDetailTable_text" class="ml-3" style="display:inline-block;"></span><div class="form-check pr-3" style="display:inline-block;margin-bottom:-5px;float:right;"><div class="meterialform msg_check mt-2"><div class="checkbox pull-right" id="" style="margin-top:-2px;"><label data-toggle="tooltip" title="On check this select box, the below invoice details wont include in the mail"><input class="uncheck_inv" type="checkbox" name="" onclick="updateMainMsgSection(this)"><i class="helper"></i>Don't Include Invoices in the mail</label></div> </div> </div></div>
			<div class="customtable db-ca-view pl-3 pr-3">
						<table id="msgDetailTable" class="row-border dataTable meterialform" cellspacing="0" width="100%" style="display:inline-table;"><thead><tr><th class="text-center"><div class="checkbox"><label><input type="checkbox" id='' /><i class="helper"></i></label></div> </th><th class="text-center">Source</th><th class="text-center">Invoice Number</th><th class="text-center">Invoice Date</th><th class="text-center">GSTIN No</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Amt</th></tr></thead><tbody id="sendMsgsBody"></tbody></table>
		     </div>		
			</div></div>		
			<div class="modal-footer"><button type="button" class="btn  btn-blue-dark save-btn" id="message_send_btn" onclick="sendMessages('${client.id}','${id}')">Send</button><button type="button" class="btn btn-blue-dark close-bttn" data-dismiss="modal">Close</button>
			</div></div></div></div>
			
<div class="modal fade" id="showMismatchInvModal" tabindex="-1" role="dialog" aria-labelledby="showMismatchInvModal" aria-hidden="true">
<div class="modal-dialog modal-lg" role="document" style="height: 100%;">
			<div class="modal-content p-0" id="showreconModalbody" style="height: auto;">
				<div class="modal-body p-0">
            	<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                <div class="production-hdr bluehdr"><h3 id="" style="font-weight:100;">View Invoices</h3></div> <h6 id="recPurchase">Record In Purchase</h6>
                <div class="pt-2 row customtable">
                <table class="row-border dataTable meterialform" id="showInvDetailTable" style="width:93%;border: 1px solid #ddd!important">
                 <thead><tr><th class="text-center">Invoice Type</th><th class="text-center">Supplier</th><th class="text-center">Invoice No</th><th class="text-center">Invoice Date</th><th class="text-center">GSTIN</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Branch</th><th class="text-center">Status</th></tr></thead>
                <tbody id="showInvDetailTable_Body"></tbody>
                </table>
                </div>
                 <h6 id="recgstr2a">Record In GSTR2A</h6>
                <div class="pt-2 row customtable">
                <table class="row-border dataTable meterialform" id="showInvDetailTable1" style="width:93%;border: 1px solid #ddd!important;margin-top: -10px;">
                <thead><tr><th style="width: 20%;">Type</th><th>Supplier</th><th>Invoice Number</th><th>Invoice Date</th><th>GST No</th><th>Taxable Amt</th><th>Total Tax</th><th>Status</th></tr></thead>
                <tbody id="showInvDetailTable_Body1"></tbody>
                </table>
			</div>
               </div>	
			<input type="hidden" id="mannualHiddenInvoiceid"><input type="hidden" id="mannualHiddenReturnType">	
			<div class="modal-footer">  <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
			</div></div></div>
</div>
<div class="modal fade" id="hsnModal" role="dialog" aria-labelledby="hsnModal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document" style="max-width: unset; width: 80%;">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>HSN Summary</h3>
                    </div>
				</div>
          <div class="modal-body">
                <form:form method="post" data-toggle="validator" class="p-4" name="hsnsummaryform" id="hsnsummaryform" action="${contextPath}/savehsnSummary/${id}/${client.id}/${fullname}/${usertype}/${returntype}/${month}/${year}"  modelAttribute="hsnsummary">
				<table align="center" id="hsnsummarysortable_table4" border="0" class="row-border dataTable hsnsummary_table" style="margin-bottom: 9%;width: 100%;">
              <thead><tr><th style="width:5%">S.No</th><th >HSN</th><th>Desc</th><th>UQC </th><th>Qty</th><th><c:choose><c:when test="${month > 4 && year > 2020}">Tax Rate</c:when><c:when test="${month < 4 && year > 2021}">Tax Rate</c:when><c:otherwise>Total Value</c:otherwise></c:choose></th><th>Taxable Value</th><th>Igst</th><th>Cgst</th><th>Sgst</th><th>Cess</th><th style="width:4%"></th></tr></thead>
              <tbody id="hsnSummarybody">
                <tr id="1" class=""><td id="sno_row2" align="center">1</td><td id="" class="form-group" style="border: none;margin-bottom: 0px;"><input type="text" class="form-control hsn_text1"  required="required"  placeholder="HSN Code" id="hsnc_text1" name="hsnData[0].hsnSc"/></td><td class="form-group"><input type="text" class="form-control hsndesc_text1"  required="required"  placeholder="HSN Desc" id="hsndesc_text1" name="hsnData[0].desc"/></td><td id="dramttd" class="form-group"><input type="text" class="form-control huqc_text1" id="huqc_text1" name="hsnData[0].uqc" required="required" placeholder="UQC" /></td><td class="form-group"><input type="text" class="form-control hqty_text1" id="hqty_text1" name="hsnData[0].qty" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Quantity" /></td><td class="form-group"><c:choose><c:when test="${month > 4 && year > 2020}"><input type="text" class="form-control hrt_text1" id="hrt_text1" name="hsnData[0].rt" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter Tax Rate" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Tax Rate" /></c:when><c:when test="${month < 4 && year > 2021}"><input type="text" class="form-control hrt_text1" id="hrt_text1" name="hsnData[0].rt" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter Tax Rate" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))</c:when><c:otherwise><input type="text" class="form-control hval_text1" id="hval_text1" name="hsnData[0].val" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Total Value" /></c:otherwise></c:choose></td><td class="form-group"><input type="text" class="form-control htxval_text1" id="htxval_text1" name="hsnData[0].txval" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Taxable Value" /></td><td class="form-group"><input type="text" class="form-control higst_text1" id="higst_text1" name="hsnData[0].iamt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Igst Amount" /></td><td class="form-group"><input type="text" class="form-control hcgst_text1" id="hcgst_text1" name="hsnData[0].camt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Cgst Amount" /></td><td class="form-group"><input type="text" class="form-control hsgst_text1" id="hsgst_text1" name="hsnData[0].samt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Sgst Amount" /></td><td class="form-group"><input type="text" class="form-control hcsamt_text1" id="hcsamt_text1" name="hsnData[0].csamt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount"onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Cess Amount" /></td><td align="center" width="2%" style="text-align:center;"><a href="javascript:void(0)" id="delete_button1" class="item_delete" onclick="delete_HsnSummaryrow(1)"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>
              </tbody>
              <tfoot style="color:black"><th colspan="11" class="text-left"><i class="add-btn" onclick="addHSNSummaryrow()" style="cursor: pointer;">+</i><span style="color:black">Add Another Row</span></th><th colspan="1" style="text-align: right;"><i class="add-btn"  onclick="addHSNSummaryrow()" style="cursor: pointer;">+</i></th></tfoot>
            </table>
				 <input type="hidden" id="userid" name="userid" value="<c:out value="${id}"/>"><input type="hidden" id="clientid" name="clientid" value="<c:out value="${client.id}"/>"><div class=" style="position: absolute;bottom: 0px;right:0px"><button class="btn btn-blue-dark addvou_submit" style="display:none">Add HSN Summary</button>
			</div>  
 </form:form>
  
  </div>
 <div class="modal-footer text-center" style="display:block">
	<label for="addvou_submit" class="btn btn-blue-dark m-0" id="addhsnsummarybtn" tabindex="0" onClick="addhsnsummary()">Save HSN Summary</label>
	<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close" onClick="closevcmodal()">Cancel</a> 
  </div>
  </div>
</div>
</div>
<div class="modal fade" id="supCommentsModal" role="dialog" aria-labelledby="supCommentsModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Comments/Narration</h3>
                    </div>
				</div>
				<div class="modal-body popupright ">
					<div class="row pl-4 pr-2 pt-4">
					<span id="nocomments_sup" class="pl-2"></span>
					<div class="form-group col-md-12">
						<div class="suppliercommentsTab" style="max-height:300px;min-height:300px;overflow-y:auto;">
						
						</div>
					</div>
						<div class="form-group col-md-12 mb-0">
						<label for="Comments">Add Comments/Narration :</label>
						<textarea class="form-control" id="sup_comments" style="width:97%;height:110px"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer" style="display:block;text-align:center;">
				<a type="button" class="btn  btn-blue-dark" id="addComment">Save</a>
				 <button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
				
			</div>
		</div>
	</div>
	
		<div class="modal fade" id="ebillimportModal" role="dialog" aria-labelledby="ebillimportModal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document">
            <div class="modal-content" id="ebillidImportBody">
                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                    <div class="invoice-hdr bluehdr"><h3>Import Ewaybill Invoices</h3></div>
					<div class="row alert alert-danger mr-1 ml-2" id="ebillimporterr" role="alert" style="display:none;"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2" /><span id="eimportSummaryError"></span></div>
					<div class="row p-3">
						<div class="errormsg col-md-12" id="ebilltemplateType_Msg" style="position:relative;left:49%;"></div>
							<div class="pl-5 pr-5">
								<form:form method="POST" class="meterialform" id="ebillimportModalForm" enctype="multipart/form-data" action="${contextPath}/uploadebillInvoice/${id}/${fullname}/${usertype}/${client.id}/EWAYBILL/${month}/${year}">
										<div class="row meterialform">
											<div class="col-md-4 p-0 mb-2 text-right"><h6><span class="astrich imp"></span>Select Template Type : </h6></div>
											<div class="col-md-6">
												<select id="etemplateType" name="templatetype" style="width:90%">
													<option value="">-Select Template Type-</option>
													<option value="mastergst">MasterGST Ewaybill Template</option>
												</select>
											</div>
											<div class="col-md-4 p-0 mb-2 text-right"><h6 class="vert_branch">Branch : </h6></div><div class="col-md-6"><select  name="branch" style="width:90%"><option value="">- Branch -</option><c:forEach items="${client.branches}" var="branch"><option value="${branch.name}">${branch.name}</option></c:forEach></select></div>
											<div class="col-md-4 p-0 mb-2 text-right"><h6 class="vert_branch vert_text">Vertical : </h6></div><div class="col-md-6"><select name="vertical" style="width:90%"><option value="">- Vertical -</option><c:forEach items="${client.verticals}" var="vertical"><option value="${vertical.name}">${vertical.name}</option></c:forEach></select></div>
									</div>
									<div class="row">
											<fieldset style="width:  100%;">
					                              <div class="filedragwrap" onclick="chooseebillfileSheets()">
											              <div id="ebillfiledrag1" style="display: block;">
												                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
												                <div class="filedraginput"> <span id="chosefile-cnt">Choose File</span><input type="file" name="file" id="ebillfileselect1" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" onchange="updateEbillSheets()">  </div>
												                <div class="ortxt"> --( OR )--</div> <div id="ebillfiledrag1" >Drop File Here</div>
											              </div> 
					             				 </div>
					           			 </fieldset>	
												<div class="form-group col-md-12 col-sm-12" id="ebillidSheet" style="display:none;">
													<p class="lable-txt">File Name : <span id="ebillmessages"></span> </p><div class="">&nbsp;</div>
													<div class="form-check form-check-inline" style="position:inherit;"><input class="form-check-input" type="checkbox" id="ebillSheet" value="invoiceList" name='list' checked="checked" /><label for="invoiceSheet"><span class="ui"></span></label> <span class="labletxt">Invoices</span> </div><div class=""></div>
													
												</div>
												<div class="form-group col-4"><input type="button" class="btn btn-blue-dark" onClick="performEbillinvImport(this)" value="Import Invoices"/></div>
												<div class="form-group col-12" id="ebillmsgImportProgress" style="display:none;">We are importing your invoices. It may take few seconds based on your network bandwidth. Please wait.. </div>
									</div>
						</form:form>
						</div>
						<div class="mt-4"><div class="form-group col-md-12 col-sm-12"><div class="rightside-wrap"><h5>Download Templates</h5><ul><li class="col-12 ebill_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_ewaybill_excel_Template.xls">MasterGST Ewaybill Template</a></li></ul></div></div></div>
            </div>
        </div>
    </div>
	<div class="modal-content" id="ebillidImportSummary" style="display:none;">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Import Invoice Summary </h3></div> <div class="">&nbsp;</div>
		<div class="col-11 m-auto">
        <p>We have processed all your invoices and find the below Import invoices Summary, for Failed invoices you can download from the error log and correct the invoices and re-import again.</p>
          <table width="100%" border="0" cellspacing="0" cellpadding="5" class="table-imports table table-sm table-bordered table-hover"><thead>
          <tr>
            	<th></th>
            	<th colspan="2">Totals</th>
            	<th colspan="2">Imported</th>
            	<th colspan="2">Failed</th>
            </tr>
              <tr>
                <th>Invoice Name</th>
                <th>Line Items</th>
                <th>Invoices</th>
                <th>Line Items</th>
                <th>Invoices</th>
                <th>Line Items</th>
                <th>Invoices</th>
              </tr></thead> <tbody id="ebillimportSummaryBody"></tbody> </table>
          <table width="100%" border="0" cellspacing="0" id="einverrorXls" cellpadding="5" class="table table-inverse" style="display:none"><tr><td>Download Error Log file <a class="ml-3" href="${contextPath}/geterrorxls"><i class="fa fa-file-excel-o"></i> Error Sheet</a></td><td class="redtxt"></td></tr></table>
        </div>
      </div>
      <div class="modal-footer">
      <a href="${contextPath}/ewaybill/${id}/${fullname}/${usertype}/${returntype}/${client.id}/${month}/${year}" class="btn btn-blue-dark urllink" >Continue..</a>
      <a href="${contextPath}/ewaybill/${id}/${fullname}/${usertype}/${returntype}/${client.id}/${month}/${year}" class="btn btn-secondary urllink">Close</a>
      </div>
    </div>
	</div>
</div>
<div class="modal fade" id="addTermModal" role="dialog" aria-labelledby="addTermModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
			<div class="modal-header p-0">
				<button type="button" class="close" onclick="closeTermModal('addTermModal')" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Configure Terms</h3>
                    </div>
				</div>
				<div class="modal-body popupright ">
					<div class="row customtable pl-5 pr-2 pt-4">
		                <table class="row-border dataTable meterialform" id="configTermsTable" style="width:93%;">
		                <thead><tr><th>S.No</th><th>Term Name</th><th>Term Days</th><th></th></tr></thead>
		                <tbody id="ConfigTemsTable_body">
		                	<tr id="1">
		                		<td id="no_row1" align="center">1</td>
								<td><input type="text" class="form-control" id="term_name1" name="termName" placeholder="Terms Name"/></td>
								<td><input type="text" class="form-control" id="term_days1" name="noOfDays" placeholder="Terms Days"/></td>
								<td align="center"><a href="javascript:void(0)" id="termdelete_button1" class="term_delete" onclick="deleteTerm(1)"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td>
							</tr>
		                </tbody>
		                <tfoot>
		                <tr>
			                <th colspan="3" class="tfootwitebg"><span class="add pull-left" id="addConfigrow1" onclick="addTermsrow()" style="color:black;"><i class="add-btn">+</i> Add another row</span></th>
			                 <th class="tfootwitebg addCtbutton"> <span class="add add-btn" id="addConfigrow" onclick="addTermsrow()">+</span></th>
		                </tr>
		                </tfoot>
		                </table>
					</div>
			</div>
			<div class="modal-footer" style="display:block;text-align:center;">
				<a type="button" class="btn  btn-blue-dark" id="addTerm_btn" onclick="savePaymentTerms()">Save</a>
				<button type="button" class="btn  btn-blue-dark" onclick="closeTermModal('addTermModal')">Cancel</button>
			</div>
				
			</div>
		</div>
	</div>