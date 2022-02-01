<!-- Add Customer Modal Start -->
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<style>
#gstnnumber{text-transform: uppercase;}
.bnkdetails .meterialform .checkbox input:checked~.helper::before,.termsdetails_inv .meterialform .checkbox input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.bnkdetails .meterialform .checkbox input:checked~.helper::after,.termsdetails_inv .meterialform .checkbox input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.bnkdetails .meterialform .checkbox .helper,.termsdetails_inv .meterialform .checkbox .helper{border-radius:4px;color: #337ab7;}
</style>
    <div class="modal fade" id="addCustomerModal" role="dialog" aria-labelledby="addCustomerModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
        <div class="modal-dialog modal-md  modal-right" role="document">
            <div class="modal-content">
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('addCustomerModal')">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3 id="addcust">Add Customer</h3>
                    </div>
					<form method="POST" data-toggle="validator" id="customerForm" class="meterialform" name="userform" modelAttribute="customer">
                    <div class="row pl-5 pr-5 pb-3 pt-1 gstr-info-tabs" style="height:100%;overflow-x:hidden;overflow-y:auto;">
						<div class="form-group col-md-12 col-sm-12 mt-1 mb-1">
                            <div class="lable-txt"></div>
                            <div class="form-group-inline">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="type" id="type" type="radio" class="business" value="Business" checked="checked" />
                                            <i class="helper"></i>Registered</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="type" id="type" type="radio" class="individual" value="Individual" />
                                            <i class="helper"></i>UnRegistered</label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 category_business gstin_number">
                            <p class="lable-txt astrich">GSTIN/Unique ID <a href="#" onclick="invokeInvoicePublicAPI(this)" class="btn btn-green btn-sm pull-right">Get GSTIN Details</a></p>
                            <span class="errormsg" id="gstnnumber_Msg" style="margin-top:-31px"></span>
                            <input type="text" id="gstnnumber" name="gstnnumber" aria-describedby="gstnnumber" onChange="updateCustomerPan(this.value)" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" maxlength="15" placeholder="07CQZCD1111I4Z7" required/>
                            <label for="gstnnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich" id="idName">Business Name</p>
                            <span class="errormsg" id="businessName_Msg" style="margin-top:-33px"></span>
                            <input type="text" id="custname" name="name" required="required" data-error="PLease enter the name of the Business" placeholder="Name" value="" />
                            <label for="name" class="control-label"></label>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 category_business contact_person_name">
                            <p class="lable-txt">Contact Person Name</p>
                            <span class="errormsg" id="contactPersonName_Msg" style="margin-top:-33px"></span>
                            <input type="text" id="contactperson" name="contactperson" data-error="Please enter the contact person name" placeholder="Jane Smith" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>
                     
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="customer_type">
                            <p class="lable-txt">Customer ID
                            <span class="errormsg" id="customerid_Msg"></span></p>
                            <input type="text" id="customerid" class="customerid" name="customerId" data-error="Enter the Customer id" placeholder="Customer ID" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
                           <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none" id="supplier_type">
                            <p class="lable-txt">Supplier ID
                            <span class="errormsg" id="suppliercustomerid_Msg"></span></p>
                            <input type="text" id="suppliercustomerid" class="suppliercustomerid" name="supplierCustomerId" data-error="Enter the Supplier id" placeholder="Supplier ID" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt" style="margin-bottom:6px">Mobile Number</p>
                            <span class="errormsg" id="mobilenumber_Msg" style="margin-top:-33px"></span>
                            <input type="text" id="mobilenumber" name="mobilenumber" pattern="[0-9]+" maxlength="10" data-error="Please enter valid mobile number" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <script>$("#mobileNumber").intlTelInput({"initialCountry":"in"});</script>
                            <label for="mobilenumber" class="control-label"></label>
                            <i class="bar"></i> </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Email Id</p>
                            <span class="errormsg" id="emailId_Msg"></span>
                            <input type="text" id="email" name="email" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email address" placeholder="janeSmith@gmail.com" value="" />
                            <label for="email" class="control-label"></label>
                            <i class="bar"></i> </div> 
                            
                            <div class="form-group col-md-6 col-sm-12" id="supplier_faxno" style="display:none;">
                            <p class="lable-txt">FAX Number</p>
                            <span class="errormsg" id="faxno_Msg"></span>
                            <input type="text" id="faxno" name="faxNo" data-error="Please enter valid FAX No"  placeholder="12345" value="" />
                            <label for="faxno" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                          <br/>
                           <ul class="nav nav-tabs col-md-12 mt-3 pl-3 invtabs" role="tablist" id="tabsactive">
					<li class="nav-item"><a class="nav-link active" id="groupstabinv" data-toggle="tab" href="#addresstabinv" role="tab">Address</a></li>
					<li class="nav-item"><a class="nav-link " id="groups1tabinv" data-toggle="tab" href="#detailstabinv" role="tab">Details</a></li>
					<li class="nav-item ssupplier_type"><a class="nav-link" id="sgroupstabinv" data-toggle="tab" href="#banksdetailsinv" role="tab">Bank Details</a></li>
			  		<li class="nav-item"><a class="nav-link" id="termstabinv" data-toggle="tab" href="#termsdetailsinv" role="tab">Customer Notes</a></li>
			  		<li class="nav-item" id="supplier_additional_details" style="display:none;"><a class="nav-link " id="supplieraddionaltab" data-toggle="tab" href="#supplieradditionaltab" role="tab">Additional Details</a></li>
			  </ul>
                               
                         <div class="tab-content col-md-12 mb-3 mt-1 p-0">
					<div class="tab-pane active col-md-12" id="addresstabinv" role="tabpane1" style="height:240px">
					<div class="row">
					         
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="countries_list">
                            <p class="lable-txt astrich">Country</p>
                            <span class="errormsg" id="country_Msg" style="margin-top:-33px"></span>
                            <select class="form-controll countriesList" required="required" id="country" name="country" data-error="Please select country">
                            	<option value="India">India</option>
                            </select>
                            <label for="country" class="control-label"></label>
                            <i class="bar"></i>
                          </div>  
                        
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Landline No</p>
                            <span class="errormsg" id="landline_Msg"></span>
                            <input type="text" id="landline" name="landline" data-minlength="10" maxlength="11" pattern="[0-9]+" data-error="Please enter valid landline number" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <label for="landline" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
					
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
							<div class="lable-txt astrich" id="div_state">State</div>
							<div class="form-group">
								<span class="errormsg" id="state_Msg" style="margin-top:-33px"></span>
								<input id="state" required="required" name="state" readonly="readonly" data-error="Please enter the state" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]*\s*[a-zA-z]*" placeholder="State" />
								<label for="state" class="control-label"></label>
								<div id="stateempty" style="display:none">
									<div class="ddbox">
									  <p>Search didn't return any results.</p>
									</div>
								</div>
								<i class="bar"></i> </div>
						</div>
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
							<div class="lable-txt">Address</div>
							<div class="form-group">
								<span class="errormsg" id="address_Msg"></span>
								<input type="text" id="custaddress" name="address" class="mapicon" aria-describedby="address" placeholder="Address" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))" />
								<label for="address" class="control-label"></label>
								<i class="bar"></i> </div>
						</div>
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Pin Code</p>
                            <span class="errormsg" id="mobilenumber_Msg"></span>
                            <input type="text" id="pincode" name="pincode" data-minlength="5" maxlength="10" pattern="[0-9]+" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""/>
                            <label for="pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">City</p>
                            <span class="errormsg" id="mobilenumber_Msg"></span>
                            <input type="text" id="city" name="city" value="" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" />
                            <label for="city" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
					</div>
					</div>
					<div class="tab-pane col-md-12 mt-0" id="detailstabinv" role="tabpane2" style="height:240px">
						<div class="row">
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="ccustomer_type">
                            <p class="lable-txt">Ledger Name</p>
                            <span class="errormsg" id="ledger_Msg"></span>
                            <input type="text" class="customerLedgerName" id="ccustomerLedgerName" name="customerLedgerName" data-error="Please enter Ledger Name" value="" />
                            <label for="customerLedgerName" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none" id="ssupplier_type">
                            <p class="lable-txt">Ledger Name</p>
                            <span class="errormsg" id="ledger_Msg"></span>
                            <input type="text" class="supplierLedgerName" id="ssupplierLedgerName" name="supplierLedgerName" data-error="Please enter Ledger Name" value="" />
                            <label for="supplierLedgerName" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                         <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none ssupplier_type">
                            <p class="lable-txt">Credit Period</p>
                            <span class="errormsg" id="credp_Msg"></span>
                            <input type="text" class="credit_p" id="credit_period" name="creditPeriod" placeholder="Number of Days"  pattern="[0-9]+" data-error="Please enter valid number of days" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <label for="creditp" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                         <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none ssupplier_type">
                            <p class="lable-txt">Credit Amount</p>
                            <span class="errormsg" id="creditamt_Msg"></span>
                            <input type="text" class="creditAmt" id="creditAmount" name="creditAmount" data-error="Please enter credit Amount" value="" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))"/>
                            <label for="creditAmt" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
						 <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none ssupplier_type">
                            <p class="lable-txt">Opening Balance as on 01-04-<span class="openText">2019</span></p>
                            <span class="errormsg" id="openingb_Msg"></span>
                            <input type="text" class="openingbalance" id="openingbalance" name="openingbalance" placeholder="balance"  data-error="Please enter valid amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""  />
                            <label for="openingb" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 pan_num">
                            <p class="lable-txt">PAN Number</p>
                            <span class="errormsg" id="customerpannumber_Msg"></span>
                            <input type="text" id="customerpannumber" name="customerPanNumber" data-error="Enter the valid pan number" placeholder="PAN Number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">TAN Number (PAN Based)</p>
                            <span class="errormsg" id="customertanpannumber_Msg"></span>
                            <input type="text" id="customertanpannumber" class="customertanpannumber" name="customerTanPanNumber" maxlength="10" pattern="^[A-Za-z]{4}\d{5}[A-Za-z]{1}$" data-error="Enter the pan tan number" placeholder="PAN TAN Number" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">TAN Number (GSTIN Based)</p>
                            <span class="errormsg" id="customertannumber_Msg"></span>
                            <input type="text" id="customertannumber" name="customerTanNumber" data-error="Enter the tan number" placeholder="TAN Number" maxlength="10" pattern="^[A-Za-z]{4}\d{5}[A-Za-z]{1}$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>    
						</div>
					</div>
					<div class="tab-pane col-md-12 ssupplier_type" id="banksdetailsinv" role="tabpane3" style="height:240px">
					<div class="row" id="customerBankDetails">
						<div class="col-md-6 col-sm-12 bnkdetails mt-1">
                  <div class="form-check mb-2 mb-sm-0" style="margin-top:0px; margin-bottom:5px;margin-left:2px;">
                  <div class="meterialform mt-2" style="float:left">
                      <div class="checkbox pull-right" id="customerBankChecks" style="margin-top:-2px;">
                        <label>
                       <input class="addCustomerBankDetailss" type="checkbox" name="addCustomerBankDetailss">
                        <i class="helper"></i> Link Your Bank</label>
                      </div>
                    </div>
				  </div>
				  <br>
				  <p style="font-size:12px;display: inline-flex;">By Linking of Bank to this customer, bank details will be Automatically populate in Invoice Screen on selection of this customer. </p>
                </div>
				  <div class="col-md-6 col-sm-12 form-group" id="selectCBankDiv3" style="display:none">
                  <p for="inlineFormInput" class="lable-txt mb-0">Select Bank</p>
                  <select class="form-control mb-2 mr-sm-2 mb-sm-0 selectCustomerBanks" id="selectCustomerBanks" onchange="selectCustomerBankNames()">
                  </select>
				  <i class="bar"></i>
                </div>
                <div class="col-md-6 col-sm-12">
				</div>
				<div id="selectCBankDiv2" class="col-md-6 col-sm-12 pl-3 pr-3 pull-right" style="display:none"> 
				<label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Account Number <span style="float: right;">:</span></label>
			  <span id="customerBankAcctNos_txt" class="bold-txt" style="font-size:12px"></span><br/>
              <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Account Name <span style="float: right;">:</span></label>
			  <span id="customerBankAccountNames_txt" class="bold-txt" style="font-size:12px"></span><br/>
			   <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Branch Name <span style="float: right;">:</span></label>
			  <span id="customerBankBranchs_txt" class="bold-txt" style="font-size:12px"></span><br/>
              <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">IFSC Code <span style="float: right;">:</span></label>
			  <span id="customerBankIFSCs_txt" class="bold-txt" style="font-size:12px"></span><br>
			  </div>
					</div>
					
					
								<div class="row" id="supplierBankDetails" style="display:none">
									<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Beneficiary Name</p>
		                            <span class="errormsg" id="benefi_Msg"></span>
		                            <input type="text" class="sbeneficiaryName" id="sbeneficiaryName" name="beneficiaryName" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" placeholder="Beneficiary Name"  value="" />
		                            <label for="sbeneficiaryName" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Bank Name</p>
		                            <span class="errormsg" id="bankname_Msg"></span>
		                            <input type="text" class="sbankName" id="sbankName" name="bankName" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" placeholder="Branch Name" value="" />
		                            <label for="sbankName" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Branch Address</p>
		                            <span class="errormsg" id="branchaddr_Msg"></span>
		                            <input type="text" class="sbranchAddress" id="sbranchAddress" name="branchAddress"  placeholder="Branch Address" value="" />
		                            <label for="sbranchAddress" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Account Number</p>
		                            <span class="errormsg" id="accNo_Msg"></span>
		                            <input type="text" class="saccountNumber" id="saccountNumber" name="accountNumber"  maxlength="18" minlength="9"  onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" pattern="^[0-9]*$" placeholder="Account Number" value="" />
		                            <label for="saccountNumber" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                             <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">IFSC Code</p>
		                            <span class="errormsg" id="ifsc_Msg"></span>
		                            <input type="text" class="sifscCode" id="sifscCode" name="ifscCode" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="IFSC Code" pattern="^[a-zA-Z0-9]*$"  value="" />
		                            <label for="sifscCode" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Account Type</p>
		                            <span class="errormsg" id="acctype_Msg"></span>
		                            <input type="text" class="saccountType" id="saccountType" name="accountType"  onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="Account Type" pattern="^[a-zA-Z0-9]*$"  value="" />
		                            <label for="saccountType" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">MICR Code</p>
		                            <span class="errormsg" id="micr_Msg"></span>
		                            <input type="text" class="smicrCode" id="smicrCode" name="micrCode"  onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="MICR Code" pattern="^[a-zA-Z0-9]*$" value="" />
		                            <label for="smicrCode" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                 </div>
					
					
					</div>
					
										<div class="tab-pane col-md-12" id="supplieradditionaltab" role="tabpane5" style="height:210px">
					<div class="row">
					<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Company Registration Number</p>
		                            <span class="errormsg" id="regno_Msg"></span>
		                            <input type="text" class="sregNo" id="sregNo" name="companyRegNo"  pattern="[0-9]+" data-error="Please enter valid landline number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
		                            <label for="sregNo" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Date Of Inception(dd/mm/yyyy)</p>
		                            <span class="errormsg" id="incepitiondate_Msg"></span>
		                            <input type="text" class="sdateofinception" id="sdateofinception" name="dateofInception"  value="" />
		                            <label for="sdateofinception" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                             <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Nature Of Expertise</p>
		                            <span class="errormsg" id="expertize_Msg"></span>
		                            <input type="text" class="snatuteOfExpertise" id="snatuteOfExpertise" name="natuteOfExpertise"  value="" />
		                            <label for="snatuteOfExpertise" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Revenue TurnOver</p>
		                            <span class="errormsg" id="turnover_Msg"></span>
		                            <input type="text" class="sturnover" id="sturnover" name="turnover"  value="" />
		                            <label for="sturnover" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Quality Programs And Certifications</p>
		                            <span class="errormsg" id="progandcert_Msg"></span>
		                            <input type="text" class="sprogramsAndCert" id="sprogramsAndCert" name="programsAndCert"  value="" />
		                            <label for="sprogramsAndCert" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Full Time Employees</p>
		                            <span class="errormsg" id="fulltimeemployees_Msg"></span>
		                            <input type="text" class="sfulltimeEmployees" id="sfulltimeEmployees" name="fulltimeEmployees"  value="" />
		                            <label for="sfulltimeEmployees" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Applicability Of Micro,Small,Medium Act 2006</p>
		                            <span class="errormsg" id="applicability_Msg"></span>
		                            <input type="text" class="sapplicability" id="sapplicability" name="applicability"  value="" />
		                            <label for="sapplicability" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
					
					
					</div>
					</div>
					
					<div class="tab-pane col-md-12" id="termsdetailsinv" role="tabpane4" style="height:240px">
					<div class="row">
						<div class="col-md-12 col-sm-12 termsdetails_inv mt-1">
		                  <div class="form-check mb-2 mb-sm-0" style="margin-top:0px; margin-bottom:5px;margin-left:2px;">
			                  <div class="meterialform mt-2" style="float:left">
			                      <div class="checkbox pull-right" id="customerTermsChecks" style="margin-top:-2px;">
			                        <label>
			                       <input class="addCustomerTermsDetailss" type="checkbox" name="isCustomerTermsDetails">
			                        <i class="helper"></i>Enable Notes</label>
			                      </div>
			                    </div>
						  </div>
	                </div>
	                 <div class="col-md-12 col-sm-12" id="selectTermsDivinv" style="display:none">
	                 	<textarea type="text" class="custTermsinv" id="customertermsinv" name="customerterms" placeholder="Customer Notes" value="" style="border: 1px solid lightgray;width:100%;height:150px;"></textarea>
	                 </div>
                </div>
			</div>
					
					
				</div>
                       
						<input type="hidden" id="customerBankNames" name="customerBankName">
						<input type="hidden" id="customerBankAcctNos" name="customerAccountNumber">
						<input type="hidden" id="customerBankAccountNames" name="customerAccountName">
						<input type="hidden" id="customerBankBranchs" name="customerBranchName">
						<input type="hidden" id="customerBankIFSCs" name="customerBankIfscCode">
						<input type="hidden" id="customerorsuppliertype" name="customertype"/>
							<input type="hidden" name="userid" value="${id}">
						    <input type="hidden" name="fullname" value="${fullname}">
							<input type="hidden" name="clientid" value="${client.id}">	
							<input type="hidden" name="serialno" value="">
							<input type="hidden" name="createdDate" value=''>
							<input type="hidden" name="createdBy" value="">
                    </div>
                    
                   <div class="row">
                    <div class="col-12 mt-4 text-center" style="display:block">
                    <input type="button" class="btn btn-blue-dark itmcustsave" id="invaddcst" value="Save" aria-label="Close" onclick="saveCustomer()"/>
					<a href="#" class="btn btn-blue-dark ml-2" onclick="closeAddCustomer('addCustomerModal')" aria-label="Close">Cancel</a>
                   </div>
                   </div>
                    
					</form>
                </div>
            </div>
        </div>
    </div>
	<script type="text/javascript">
	$('input[type=radio][name=type]').change(function() {
			updateState(this.value);
	});
		function updateState(value) {	
		$('#state').val('');
		
		if (value == 'Business') {
			$('#state').val('');
			$('#idName').html('Business Name');
			$('.individual').removeAttr('checked');
			$('.business').attr('checked','checked');
			$('.category_business').show();
			$('#businessName_Msg').text(""); 
			$('#mobilenumber_Msg').text("");
			$('#country_Msg').text("");
			$('#contactPersonName_Msg').text("");
			$('#state_Msg').text("");
			$('#country_Msg').text("");
			 $("#selectTermsDivinv").css('display','none');
	    	 $('#customertermsinv').val('');
			
			$('#customerid_Msg').text("");
			$('#customerpannumber_Msg').text("");
			$('#customertanpannumber_Msg').text("");
			$('#customertannumber_Msg').text("");
			$('#customerid').val("");
			$('#customerpannumber').val("");
			$('#customertanpannumber').val("");
			$('#customertannumber').val("");
			
		}else {
			$('#customerid').val("");
			$('#customerpannumber').val("");
			$('#customertanpannumber').val("");
			$('#customertannumber').val("");
			$('#customerid_Msg').text("");
			$('#customerpannumber_Msg').text("");
			$('#customertanpannumber_Msg').text("");
			$('#customertannumber_Msg').text("");		
			 $("#selectTermsDivinv").css('display','none');
	    	 $('#customertermsinv').val('');
	    	 
			$('#state').val('');
			$('#idName').html('Individual Name');
			$('.category_business').hide();
			$('#gstnnumber').val("");
			$('#gstnnumber').prop('readonly', false);
			$('.individual').attr('checked','checked');
			$('.business').removeAttr('checked');
			$('#businessName_Msg').text(""); 
			$('#mobilenumber_Msg').text("");
			$('#country_Msg').text("");
			$('#contactPersonName_Msg').text("");
			$('#state_Msg').text("");
			$('#country_Msg').text("");
			$('#gstnnumber').attr("required",false);
		}
		$(".form-group").removeClass("has-error has-danger");
		$('form[name="userform"]').validator('update');
	}
		$('.addCustomerTermsDetailss').click(function(){
			if($(".addCustomerTermsDetailss").is(':checked')){
		      $("#selectTermsDivinv").css('display','block');
		    }else{
		    	 $("#selectTermsDivinv").css('display','none');
		    	 $('#customertermsinv').val('');
		    }
		});
	$('.addCustomerBankDetailss').click(function(){
	if($(".addCustomerBankDetailss").is(':checked')){
      $("#selectCBankDiv2").css('display','none');
	  $("#selectCBankDiv3").css('display','block');
	  $("#notrequireds").css('display','none');
	  $('#selectCustomerBanks').val('');
		$('#customerBankNames').val('');
		$('#customerBankAcctNos').val('');
		$('#customerBankBranchs').val('');
		$('#customerBankIFSCs').val('');
		$('#customerBankAccountNames').val('');
		$('#customerBankNames_txt').html(' ');
		$('#customerBankAcctNos_txt').html(' ');
		$('#customerBankBranchs_txt').html(' ');
		$('#customerBankIFSCs_txt').html(' ');
		$('#customerBankAccountNames_txt').html(' ');
    }else{
		$('#selectCustomerBanks').val('');
		$('#customerBankNames').val('');
		$('#customerBankAcctNos').val('');
		$('#customerBankBranchs').val('');
		$('#customerBankIFSCs').val('');
		$('#customerBankAccountNames').val('');
		$('#customerBankNames_txt').html(' ');
		$('#customerBankAcctNos_txt').html(' ');
		$('#customerBankBranchs_txt').html(' ');
		$('#customerBankIFSCs_txt').html(' ');
		$('#customerBankAccountNames_txt').html(' ');
	  $("#selectCBankDiv2").css('display','none');
	  $("#selectCBankDiv3").css('display','none');
	  $("#notrequireds").css('display','block');
   }
});


	function selectCustomerBankNames() {
		//var bankaccountNumber = $('#selectBank').val();
		$('#customerBankNames').val('');
		$('#customerBankAcctNos').val('');
		$('#customerBankBranchs').val('');
		$('#customerBankIFSCs').val('');
		$('#customerBankAccountNames').val('');
		$('#customerBankNames_txt').html(' ');
		$('#customerBankAcctNos_txt').html(' ');
		$('#customerBankBranchs_txt').html(' ');
		$('#customerBankIFSCs_txt').html(' ');
		$('#customerBankAccountNames_txt').html(' ');
		if($('#selectCustomerBanks').val() !=''){
			$("#selectCBankDiv2").css('display','block');
			var bankaccountNumber = $('#selectCustomerBanks').val();
			customerClientBankDetailss.forEach(function(bankdetail) {
				if(bankdetail.accountnumber == bankaccountNumber) {
					$('#customerBankNames').val(bankdetail.bankname);
					$('#customerBankAcctNos').val(bankdetail.accountnumber);
					$('#customerBankBranchs').val(bankdetail.branchname);
					$('#customerBankIFSCs').val(bankdetail.ifsccode);
					$('#customerBankAccountNames').val(bankdetail.accountName);
					$('#customerBankNames_txt').html(bankdetail.bankname);
					$('#customerBankAcctNos_txt').html(bankdetail.accountnumber);
					$('#customerBankBranchs_txt').html(bankdetail.branchname);
					$('#customerBankIFSCs_txt').html(bankdetail.ifsccode);
					$('#customerBankAccountNames_txt').html(bankdetail.accountName);
				}
			});
		}
	}

$(document).ready(function(){
	
	$('#state').prop('readonly',false);
	$('.countriesList').change(function(){
		var country=$('.countriesList').val();
		if(country == 'India'){
			$('#state').val('');
			$('#state').prop('readonly',false);
			$('#gstnnumber').prop('readonly',false);
			$('#gstnnumber').val('');
			$('.with-errors').text("");
		}else if(country == ''){
			$('#state').val('');
			$('#state').prop('readonly',true);
			$('#gstnnumber').prop('readonly',false);
			$('#state').prop('readonly',true);
			$('#gstnnumber').val('');
		}else{
			$('#state').val('');
			$('#state').prop('readonly',true);
			$('#state').val('97-Other Territory');
			$('#gstnnumber').prop('readonly',true);		
			$('#gstnnumber').val('');
		}
	});
});

$('#customerid').change(function() {

	var customerid=$('#customerid').val();
	var clientid='<c:out value="${client.id}"/>';	
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url: "${contextPath}/customeridexits/"+clientid+"/"+customerid,
		success : function(response) {
			if(response == 'success'){
				$('#customerid_Msg').text('customerid already exists');
				$('#customer_type').addClass('has-error has-danger');
			}else{
				$('#customerid_Msg').text('');
				$('#customer_type').removeClass('has-error has-danger');
			}
		}
	});
});

$('#suppliercustomerid').change(function() {

	var supplierid=$('#suppliercustomerid').val();
	var clientid='<c:out value="${client.id}"/>';
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url: "${contextPath}/suppliercustomeridexits/"+clientid+"/"+supplierid,
		success : function(response) {
			if(response == 'success'){
				$('#suppliercustomerid_Msg').text('supplier id already exists');
				$('#supplier_type').addClass('has-error has-danger');
			}else{
				$('#suppliercustomerid_Msg').text('');
				$('#supplier_type').removeClass('has-error has-danger');
			}
		}
	});
});

$('#custname').keyup(function() {
	var spath = $("#custname").val();
	if(spath != ''){
		$('#ccustomerLedgerName').val(spath);
		$('#ssupplierLedgerName').val(spath);
	}else{
		$('#ccustomerLedgerName').val('');
		$('#ssupplierLedgerName').val('');
	}
});
	</script>
    <!-- Edit Modal End -->