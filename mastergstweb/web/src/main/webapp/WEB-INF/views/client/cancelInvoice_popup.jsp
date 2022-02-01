<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<div class="modal fade" id="cancelModal" tabindex="-1" role="dialog" aria-labelledby="cancelModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('cancelModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Cancel Invoice </h3>
        </div>
        <div class="clearfix pl-4 pt-3 pr-4">
        <span id="irncancel_error"></span>
          <h6>Are you sure you want to Cancel invoice #<span id="cancelPopupDetails"></span> ?</h6>
          <p class="mb-0 f-12" id="irncancel_message"></p>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once invoices are Cancelled, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnCancel">Cancel Invoice</button>
        <button type="button" class="btn btn-primary" onclick="closeAddCustomer('cancelModal')">Don't Cancel</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="cancelEwayBillModal" tabindex="-1" role="dialog" aria-labelledby="cancelModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('cancelEwayBillModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Cancel EwayBill </h3>
        </div>
        <form:form data-toggle="validator" id="cancelewaybill_form" class="meterialform">
        <div class="clearfix pl-4 pt-4 pr-4">
        <div class="form-group row">
         <label class="label-txt col-md-6 col-sm-12 mt-2 astrich">Cancel Remark</label><span class="colon mt-2" style="margin-left:-78px;">:</span>
		 <select class="form-control col-md-6 col-sm-12 ml-2" id="cancelremark" name="cancelRmrk" onchange="cancelCodeSelection(1)" data-error="Please select reason" required style="border:1px solid lightgray;">
         <option value="">-Select-</option> <option value="Duplicate">Duplicate</option> <option value="Order Cancelled">Order Cancelled</option> <option value="Data Entry mistake">Data Entry mistake</option> <option value="Others">Others</option>
         </select>
         
		 <span class="control-label"></span>
		 <div class="help-block with-errors"></div>
		</div> 
		
		<div class="form-group row">
        <label class="label-txt col-md-6 col-sm-12 mt-2">User Comments</label><span class="colon mt-2" style="margin-left:-78px;">:</span>
         <textarea type="text" class="form-control col-md-6 col-sm-12 ml-2 usercmnts" id="usercmnts" name="cancelebillcmnts" placeholder="User Comments" style="border:1px solid lightgray;resize:vertical;"></textarea>
         </div>
         <span id="cancel_error" class="cancel_error" style="font-size:12px;color:red;"></span>
        </div>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-blue-dark" id="btn_Cancel" style="padding:7px 12px;">Cancel EwayBill</button>
        <button type="button" class="btn btn-blue-dark" onclick="closeAddCustomer('cancelEwayBillModal')"  style="padding:7px 12px;">Don't Cancel</button>
      </div>
	  <input type="hidden" class="cancelcode" id="cancelcode" name="cancelcode">
      </form:form>
      </div>
    </div>
  </div>
</div>



<div class="modal fade" id="vehicleUpdateModal" tabindex="-1" role="dialog" aria-labelledby="vehicleUpdateModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('vehicleUpdateModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Update Vehicle </h3>
        </div>
        <div class="clearfix pl-4 pt-4 pr-4">
        <table align="center" id="vehicleupdate_table" border="0" class="vehicleTable row-border dataTable">
        <thead><tr><th rowspan="2">S.No</th><th>Vehicle No</th><th>Transport Mode</th><th>Remarks</th><th>Vehicle Type</th><th>Transport DocNo</th><th>Transport DocDate</th><th></th></tr></thead> 
        <tbody id="vehicleUpdateDetailsBody">
       
        <tr id="1">
        <td id="sNo_row1" align="center">1</td>
        <td><input type="text" class="form-control vehicle_No1" id="vehicle_No1" maxlength="15" minlength="7"></td>
        <!-- <td><input type="text" class="form-control from_Place1" id="from_Place1"></td>
        <td><input type="text" class="form-control from_State1" id="from_State1"></td> -->
        <td><select  class="form-control trans_Mode1" id="trans_Mode1"><option value="">-Select-</option><option value="1">Road</option><option value="2">Rail</option><option value="3">Air</option><option value="4">Ship</option></select></td>
         <td><select class="form-control reasonrem reason_Rem1" id="reason_Rem1" onchange="reasonCodeSelect(1)"><option value="">-Select-</option><option value="Due to Break Down">Due to Break Down</option><option value="Due to Transhipment">Due to Transhipment</option><option value="Others (Pls. Specify)">Others (Pls. Specify)</option><option value="First Time">First Time</option></select></td>
          
            <td><select class="form-control vehicle_Type1" id="vehicle_Type1"><option value="">-Select type-</option><option value="R">Regular</option><option value="O">Over Dimensional Cargo</option></select></td>
       <!--  <td><input type="text"  class="form-control tripsht_No1" id="tripsht_No1"></td>
        <td><input type="text"  class="form-control userGSTIN_Transin1" id="userGSTIN_Transin1"></td>
        <td><input type="text" class="form-control entered_Date1" id="entered_Date1"></td> -->
        
        <td><input type="text"  class="form-control trans_DocNo1" id="trans_DocNo1" maxlength="15"  pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 &amp;&amp; event.charCode <= 90) || (event.charCode >= 97 &amp;&amp; event.charCode <= 122) || (event.charCode >= 48 &amp;&amp; event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))"></td>
        <td><input type="text" class="form-control trans_DocDate1" id="trans_DocDate1"></td>
        <!-- <td><input type="text" class="form-control group_No1" id="group_No1"></td> -->
        </tr>
      
        
        </tbody>
			<!-- <tfoot><th colspan="9" class="text-left"><i class="add-btn ebilladdrow disabled"  onclick="addEbillrow('vehicleupdate_table')" style="cursor: pointer;">+</i><span style="color:black">Add Another Row</span></th><th colspan="9" style="text-align: right;"><i class="add-btn ebilladdrow disabled"  onclick="addEbillrow('vehicleupdate_table')" style="cursor: pointer;">+</i></th></tfoot> -->	
		</table>
		 <span id="vehupdt_error" class="vehupdt_error" style="font-size:12px;color:red;"></span>
		<input type="hidden" class="reason_Code1 reasoncode" id="reason_Code1" name="">
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-blue-dark" id="btnVehicleUpdate" onclick="Update_Vehicle()">Update</button>
        <button type="button" class="btn btn-blue-dark" onclick="closeAddCustomer('vehicleUpdateModal')" data-dismiss="modal">Cancel</button>
      </div>
    
    </div>
  </div>
</div>

<div class="modal fade" id="errorNotificationModal" tabindex="-1" role="dialog" aria-labelledby="errorNotificationModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('errorNotificationModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Error Notification </h3>
        </div>
        <div class="clearfix pl-4 pt-4 pr-4">
          <h6 style="line-height:1.5;">Your Access to the Eway Bill Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.</h6>
          
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" onclick="closeAddCustomer('errorNotificationModal')">Close</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="addtogstr1Modal" tabindex="-1" role="dialog" aria-labelledby="addtogstr1Modal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('addtogstr1Modal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>View GSTR1</h3>
        </div>
        <form:form data-toggle="validator" id="addtogstr1_form" class="meterialform">
        <div class="clearfix pl-4 pt-4 pr-4">
		  <h6 id="maininv_head">Eway Bill Invoice(s) is added to Sales Registers successfully </h6>
        </div>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-blue-dark" id="view_GSTR1" style="padding:7px 12px;" onclick="viewGSTR1Inv()">View SaleRegister</button>
        <button type="button" class="btn btn-blue-dark" onclick="closeAddCustomer('addtogstr1Modal')"  style="padding:7px 12px;">Close</button>
      </div>
      </form:form>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="sendMailModal" tabindex="-1" role="dialog" aria-labelledby="sendMailModal" data-backdrop="static" data-keyboard="false" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" aria-label="Close" onclick="closeMailModal('sendMailModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Send Mail</h3>
        </div>
        <form:form data-toggle="validator" id="mail_form" class="meterialform">
        <div class="row pl-4 pt-0 pr-4">
		  <p style="font-size: 13px;color:red;">Customer Mail Id is not found, Please Enter Mail Address Below To Send a mail</p>
		  <div class="email-errormsg form-group">
    		<h6><span id="email_error" style="margin-left: 298px;font-size:12px" class="text-danger"></span></h6>
  		  </div>
		  <div class="form-inline col-md-12">
		  	<label for="emailid">Email id<span class="coln-txt" style="margin-right:5px;">:</span></label>
		 	 <input type="text" class="form-control buyerEmail" id="cmail" style="width:75%;"/>
		  </div>
		  <div class="form-inline col-md-12" style="line-height: 60px;">
		  	<label for="emailid">CC <span class="coln-txt" style="margin-right:5px;margin-left: 36px;">:</span></label>
		 	 <input type="text" class="form-control buyerEmail" id="ccmail" style="width:75%;"/>
		  </div>
        </div>
      <input type="hidden" class="email_return_type">
      <div class="modal-footer">
        <input type="button" class="btn btn-blue-dark" id="send_invmail" style="padding:7px 12px;" onclick="mailSendToCustomer()" value="Send"/>
        <button type="button" class="btn btn-blue-dark" onclick="closeMailModal('sendMailModal')"  style="padding:7px 12px;">Close</button>
      </div>
      </form:form>
      </div>
    </div>
  </div>
</div>
<!-- Send Email to Customers Modal Start -->
	<div class="modal fade" id="send_EmailModal" role="dialog" aria-labelledby="send_EmailModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document" style="min-width:700px">
			<div class="modal-content">
				<div class="modal-header p-0">
					<button type="button" class="close" onclick="closeMailModal('send_EmailModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
					<div class="bluehdr" style="width:100%">
						<h3>Send Email</h3> </div>
				</div>
				<div class="modal-body popupright bs-fancy-checks">
					<div class="p-3 row gstr-info-tabs">
						<ul class="nav nav-tabs col-md-12 pl-3" role="tablist" id="email_tabs">
							<li class="nav-item"><a class="nav-link active" id="email" data-toggle="tab" href="#email_snd" role="tab">Email</a></li>
							<li class="nav-item"><a class="nav-link " id="email_preview" data-toggle="tab" href="#email_preview_mode" role="tab" onclick="email_Preview('email')">Preview</a></li>
						</ul>
						<div class="tab-content col-md-12 mb-3 mt-2 pl-0 pr-0">
							<div class="tab-pane active col-md-12" id="email_snd" role="tabpane1">
								<div class="form-group successEmailmsg d-none">
									<h6><i class="fa fa-check" style="font-size:32px;color:green"></i><span id="successEmailmsg" class="text-success" style="font-weight:bold;color:green;"></span></h6></div>
								<div class="form-group col-md-11 mt-1 f-12">
									<span id="sendEmailErrorMsg" style="color:red;float: right;margin-bottom: -1px;"></span>
								</div>
								<div class="col-md-11 mb-1 f-12" style="color:blue;text-align: right;">You can Enter List of mail id's by Comma Separated Values</div>
								<div class="form-group col-md-12 mb-1 pr-0">
									<label for="client_name" class="col-md-3">Customer Name<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-8" id="customer_name" style="display: inline-block;"> </div>
								
								<div class="form-group col-md-12 mb-1 pr-0">
									<label for="customeremailid" class="col-md-3">Email id<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-8" id="customer_emailid" style="display: inline-block;"> </div>
								<div class="form-group col-md-12 mb-1 pr-0">
									<label for="clientemailids" class="col-md-3">CC <span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-8" id="customer_emailids" style="display: inline-block;"> </div>
								<div class="form-group col-md-12 mb-1 pr-0">
									<label for="Subject" class="col-md-3">Subject<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="email_subject form-control col-md-8" style="display: inline-block;" id="email_subject"> </div>
								<div class="form-group mt-3 col-md-12 mb-1 pr-0">
									<label for="Meassage" class="col-md-4">Message :</label>
									<textarea class="form-control email_meassage col-md-11" id="email_meassage" style="width:90%;height:110px;margin-left: 15px;" onkeyup=""> </textarea>
								</div>
								
								<div class="form-group mt-3 ml-3 col-md-12 mb-1 pr-0 emailSigncheck">
									<a href="#" class="signLink" style="font-size:14px;"><span id="signtext">Add Email Signature</span></a>
									<div class="meterialform mr-2 mt-1" style="float:left">
										<div class="checkbox pull-right" id="EmailSignatureCheck" style="margin-top:-2px;">
											<label>
												<input class="addEmailSignatureDetails" type="checkbox" name="isEmailSignatureDetails" checked> <i class="helper"></i><strong>Include Signature In Mail</strong></label>
										</div>
									</div>
									<div id="email_custdetails" style="width:700px"> </div>
								</div>
								
							</div>
							<div class="tab-pane col-md-12 mt-0" id="email_preview_mode" role="tabpane2">
								<div style="border:1px solid lightgray;border-radius:5px;">
									<div class="row  p-2">
										<div class="form-group col-md-12 col-sm-12">
											<h6>Dear <span id="email_cust_name"></span>,</h6> </div>
										<div class="form-group col-md-12 col-sm-12">
											<div id="preview_email"></div>
										</div>
									</div>
									<div class="p-2">
										<p class="mb-0">Thanks ,</p>
										<div id="preview_custname"> </div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<input type="hidden" id="email_invoiceId">
				<input type="hidden" id="email_returnType">
				<div class="modal-footer">
					<button type="button" class="btn btn-blue-dark" id="senEmailBtn" onclick="sendEmails()">Send</button>
					<button type="button" class="btn btn-blue-dark" onclick="closeMailModal('send_EmailModal')">Close</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Send Email to Customers Modal End -->
	<div class="modal" id="itemCustomFieldsModal" tabindex="-1" role="dialog" aria-labelledby="itemCustomFieldsModal" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog modal-md modal-right" role="document" style="left: 26%; width: 40%; top: 10%; -webkit-box-shadow: -1px 4px 20px 0px rgba(0, 0, 0, 0.75); -moz-box-shadow: -1px 4px 20px 0px rgba(0, 0, 0, 0.75); box-shadow: -1px 4px 20px 0px rgba(0, 0, 0, 0.75);">
		<div class="modal-content" style="height: 70vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="closeAddCustomer('itemCustomFieldsModal')" style="top: 4px;">
					<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
				</button>
				<div class="bluehdr" style="width: 100%; padding: 10px;">
					<h3 style="font-size: 18px;">Item Custom fields</h3>
				</div>
			</div>
			<div class="modal-body" style="overflow-y: auto;overflow-x: hidden;">
				<div class="pl-3 pr-3 pt-2 item_custom_FieldsItem">
				
				</div>
				
				<div class="row pl-3 pr-3">
					<div class="col-md-12 col-sm-12">
						<p class="lable-txt">Item Notes</p>
						<textarea rows="3" cols="23" class="item_notes" id="item_notes_text" Placeholder="Item Description" style="width: 100%;"></textarea>
					</div>
				</div>
			</div>
			<div class="modal-footer text-right" style="display: block;border-top:1px solid lightgray;">
				<span style="float:left;cursor:pointer;" class="addcustFieldsLink" onclick="itemCustomFieldsLink()"><i class="add-btn">+</i><a href="#" class="ml-2">Custom Field</a></span>
				<button type="button" class="btn btn-primary" id="itemcustomsave" onclick="saveItemCustomFields(1)" style="padding: 3px 10px;">Save</button>
				<button type="button" class="btn btn-primary" onclick="closeAddCustomer('itemCustomFieldsModal')" data-dismiss="modal" style="padding: 3px 10px;">Close</button>
			</div>
		</div>
	</div>
</div>
	<div class="modal" id="itemEinvCustomFieldsModal" tabindex="-1" role="dialog" aria-labelledby="itemEinvCustomFieldsModal" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog modal-md modal-right" role="document" style="left: 21.5%; width: 40%; top: 10%; -webkit-box-shadow: -1px 4px 20px 0px rgba(0, 0, 0, 0.75); -moz-box-shadow: -1px 4px 20px 0px rgba(0, 0, 0, 0.75); box-shadow: -1px 4px 20px 0px rgba(0, 0, 0, 0.75);">
		<div class="modal-content" style="height: 70vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="closeeTermModal('itemEinvCustomFieldsModal')" style="top: 4px;">
					<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png"alt="Close" /></span>
				</button>
				<div class="bluehdr" style="width: 100%; padding: 10px;">
					<h3 style="font-size: 18px;">Item Custom fields</h3>
				</div>
			</div>
			<div class="modal-body" style="overflow-y: auto;overflow-x: hidden;">
				<div class="pl-3 pr-3 pt-2 eitem_custom_FieldsItem"></div>
				 <div class="row pl-3 pr-3">
					<div class="col-md-12 col-sm-12">
						<p class="lable-txt">Item Notes</p>
						<textarea rows="3" cols="23" class="item_notes"id="eitem_notes_text" Placeholder="Item Description" style="width: 100%;"></textarea>
					</div>
				</div> 
			</div>
			<div class="modal-footer text-right" style="display: block;border-top:1px solid lightgray;">
				<span style="float:left;cursor:pointer;" class="addecustFieldsLink" onclick="eitemCustomFieldsLink()"><i class="add-btn">+</i><a href="#" class="ml-2">Custom Field</a></span>
				<button type="button" class="btn btn-primary" id="eitemcustomsave" onclick="saveEinvItemCustomFields(1)" style="padding: 3px 10px;">Save</button>
				<button type="button" class="btn btn-primary" onclick="closeeTermModal('itemCustomFieldsModal')" data-dismiss="modal" style="padding: 3px 10px;">Close</button>
			</div>
		</div>
	</div>
</div>