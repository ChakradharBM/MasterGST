<!-- Add Customer Modal Start -->
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<style>
#gstnnumber{text-transform: uppercase;}
.bnkdetails .meterialform .checkbox input:checked~.helper::before,.termsdetails_inv .meterialform .checkbox input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.bnkdetails .meterialform .checkbox input:checked~.helper::after,.termsdetails_inv .meterialform .checkbox input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.bnkdetails .meterialform .checkbox .helper,.termsdetails_inv .meterialform .checkbox .helper{border-radius:4px;color: #337ab7;}
</style>
    <div class="modal fade" id="addEinvCustomerModal" role="dialog" aria-labelledby="addEinvCustomerModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
        <div class="modal-dialog modal-md  modal-right" role="document">
            <div class="modal-content">
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('addEinvCustomerModal')">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3 id="addcust">Add Customer</h3>
                    </div>
					<form method="POST" data-toggle="validator" id="customerEForm" class="meterialform" name="userform" modelAttribute="customer">
                    <div class="row pl-5 pr-5 pb-3 pt-1 gstr-info-tabs" style="height:100%;overflow-x:hidden;overflow-y:auto;">
						<div class="form-group col-md-12 col-sm-12 mt-1 mb-1">
                            <div class="lable-txt"></div>
                            <div class="form-group-inline">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="type" id="etype" type="radio" class="ebusiness" value="Business" checked="checked" />
                                            <i class="helper"></i>Registered</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="type" id="etype" type="radio" class="eindividual" value="Individual" />
                                            <i class="helper"></i>UnRegistered</label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 ecategory_business gstin_number">
                            <p class="lable-txt astrich">GSTIN/Unique ID <a href="#" onclick="invokeInvoicePublicAPI(this)" class="btn btn-green btn-sm pull-right">Get GSTIN Details</a></p>
                            <span class="errormsg" id="egstnnumber_Msg" style="margin-top:-31px"></span>
                            <input type="text" id="egstnnumber" name="gstnnumber" aria-describedby="gstnnumber" onChange="updateECustomerPan(this.value)" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4Z7" required/>
                            <label for="gstnnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich" id="eidName">Business Name</p>
                            <span class="errormsg" id="ebusinessName_Msg" style="margin-top:-33px"></span>
                            <input type="text" id="ecustname" name="name" required="required" data-error="PLease enter the name of the Business" placeholder="Name" value="" />
                            <label for="name" class="control-label"></label>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 ecategory_business contact_person_name">
                            <p class="lable-txt">Contact Person Name</p>
                            <span class="errormsg" id="econtactPersonName_Msg" style="margin-top:-33px"></span>
                            <input type="text" id="econtactperson" name="contactperson" data-error="Please enter the contact person name" placeholder="Jane Smith" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>
                     
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="ecustomer_type">
                            <p class="lable-txt">Customer ID
                            <span class="errormsg" id="ecustomerid_Msg"></span></p>
                            <input type="text" id="ecustomerid" class="ecustomerid" name="customerId" data-error="Enter the Customer id" placeholder="Customer ID" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
                           <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none" id="esupplier_type">
                            <p class="lable-txt">Supplier ID
                            <span class="errormsg" id="esuppliercustomerid_Msg"></span></p>
                            <input type="text" id="suppliercustomerid" class="esuppliercustomerid" name="supplierCustomerId" data-error="Enter the Supplier id" placeholder="Supplier ID" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt" style="margin-bottom:6px">Mobile Number</p>
                            <span class="errormsg" id="emobilenumber_Msg" style="margin-top:-33px"></span>
                            <input type="text" id="emobilenumber" name="mobilenumber" pattern="[0-9]+" maxlength="10" data-error="Please enter valid mobile number" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <script>$("#emobileNumber").intlTelInput({"initialCountry":"in"});</script>
                            <label for="mobilenumber" class="control-label"></label>
                            <i class="bar"></i> </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Email Id</p>
                            <span class="errormsg" id="eemailId_Msg"></span>
                            <input type="text" id="eemail" name="email" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email address" placeholder="janeSmith@gmail.com" value="" />
                            <label for="email" class="control-label"></label>
                            <i class="bar"></i> </div> 
                            
                            <div class="form-group col-md-6 col-sm-12" id="esupplier_faxno" style="display:none;">
                            <p class="lable-txt">FAX Number</p>
                            <span class="errormsg" id="efaxno_Msg"></span>
                            <input type="text" id="efaxno" name="faxNo" data-error="Please enter valid FAX No"  placeholder="12345" value="" />
                            <label for="faxno" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                          <br/>
                           <ul class="nav nav-tabs col-md-12 mt-3 pl-3 invtabs" role="tablist" id="tabsactive">
					<li class="nav-item"><a class="nav-link active" id="egroupstabinv" data-toggle="tab" href="#eaddresstabinv" role="tab">Address</a></li>
					<li class="nav-item"><a class="nav-link " id="egroups1tabinv" data-toggle="tab" href="#edetailstabinv" role="tab">Details</a></li>
					<li class="nav-item ssupplier_type"><a class="nav-link" id="esgroupstabinv" data-toggle="tab" href="#ebanksdetailsinv" role="tab">Bank Details</a></li>
			  		<li class="nav-item"><a class="nav-link" id="etermstabinv" data-toggle="tab" href="#etermsdetailsinv" role="tab">Customer Notes</a></li>
			  		<li class="nav-item" id="esupplier_additional_details" style="display:none;"><a class="nav-link " id="esupplieraddionaltab" data-toggle="tab" href="#supplieradditionaltab" role="tab">Additional Details</a></li>
			  </ul>
                               
                         <div class="tab-content col-md-12 mb-3 mt-1 p-0">
					<div class="tab-pane active col-md-12" id="eaddresstabinv" role="tabpane1" style="height:240px">
					<div class="row">
					         
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="countries_list">
                            <p class="lable-txt astrich">Country</p>
                            <span class="errormsg" id="ecountry_Msg" style="margin-top:-33px"></span>
                            <select class="form-controll ecountriesList" required="required" id="ecountry" name="country" data-error="Please select country">
                            	<option value="India">India</option>
                            </select>
                            <label for="country" class="control-label"></label>
                            <i class="bar"></i>
                          </div>  
                        
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Landline No</p>
                            <span class="errormsg" id="elandline_Msg"></span>
                            <input type="text" id="elandline" name="landline" data-minlength="10" maxlength="11" pattern="[0-9]+" data-error="Please enter valid landline number" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <label for="landline" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
					
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
							<div class="lable-txt astrich" id="ediv_state">State</div>
							<div class="form-group">
								<span class="errormsg" id="estate_Msg" style="margin-top:-33px"></span>
								<input id="estate" required="required" name="state" readonly="readonly" data-error="Please enter the state" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]*\s*[a-zA-z]*" placeholder="State" />
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
								<span class="errormsg" id="eaddress_Msg"></span>
								<input type="text" id="ecustaddress" name="address" class="mapicon" aria-describedby="address" placeholder="Address" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))" />
								<label for="address" class="control-label"></label>
								<i class="bar"></i> </div>
						</div>
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Pin Code</p>
                            <span class="errormsg" id="emobilenumber_Msg"></span>
                            <input type="text" id="epincode" name="pincode" data-minlength="5" maxlength="10" pattern="[0-9]+" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""/>
                            <label for="pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">City</p>
                            <span class="errormsg" id="emobilenumber_Msg"></span>
                            <input type="text" id="ecity" name="city" value="" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" />
                            <label for="city" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
					</div>
					</div>
					<div class="tab-pane col-md-12 mt-0" id="edetailstabinv" role="tabpane2" style="height:240px">
						<div class="row">
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="eccustomer_type">
                            <p class="lable-txt">Ledger Name</p>
                            <span class="errormsg" id="eledger_Msg"></span>
                            <input type="text" class="ecustomerLedgerName" id="eccustomerLedgerName" name="customerLedgerName" data-error="Please enter Ledger Name" value="" />
                            <label for="customerLedgerName" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none" id="essupplier_type">
                            <p class="lable-txt">Ledger Name</p>
                            <span class="errormsg" id="eledger_Msg"></span>
                            <input type="text" class="esupplierLedgerName" id="essupplierLedgerName" name="supplierLedgerName" data-error="Please enter Ledger Name" value="" />
                            <label for="supplierLedgerName" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                         <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none essupplier_type">
                            <p class="lable-txt">Credit Period</p>
                            <span class="errormsg" id="ecredp_Msg"></span>
                            <input type="text" class="ecredit_p" id="ecredit_period" name="creditPeriod" placeholder="Number of Days"  pattern="[0-9]+" data-error="Please enter valid number of days" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <label for="creditp" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                         <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none essupplier_type">
                            <p class="lable-txt">Credit Amount</p>
                            <span class="errormsg" id="ecreditamt_Msg"></span>
                            <input type="text" class="ecreditAmt" id="ecreditAmount" name="creditAmount" data-error="Please enter credit Amount" value="" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))"/>
                            <label for="creditAmt" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
						 <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 d-none essupplier_type">
                            <p class="lable-txt">Opening Balance as on 01-04-<span class="openTxt">2019</span></p>
                            <span class="errormsg" id="eopeningb_Msg"></span>
                            <input type="text" class="eopeningbalance" id="eopeningbalance" name="openingbalance" placeholder="balance"  data-error="Please enter valid amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""  />
                            <label for="openingb" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 epan_num">
                            <p class="lable-txt">PAN Number</p>
                            <span class="errormsg" id="ecustomerpannumber_Msg"></span>
                            <input type="text" id="ecustomerpannumber" name="customerPanNumber" data-error="Enter the valid pan number" placeholder="PAN Number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">TAN Number (PAN Based)</p>
                            <span class="errormsg" id="ecustomertanpannumber_Msg"></span>
                            <input type="text" id="ecustomertanpannumber" class="ecustomertanpannumber" name="customerTanPanNumber" maxlength="10" pattern="^[A-Za-z]{4}\d{5}[A-Za-z]{1}$" data-error="Enter the pan tan number" placeholder="PAN TAN Number" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">TAN Number (GSTIN Based)</p>
                            <span class="errormsg" id="ecustomertannumber_Msg"></span>
                            <input type="text" id="ecustomertannumber" name="customerTanNumber" data-error="Enter the tan number" placeholder="TAN Number" maxlength="10" pattern="^[A-Za-z]{4}\d{5}[A-Za-z]{1}$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>    
						</div>
					</div>
					<div class="tab-pane col-md-12 essupplier_type" id="ebanksdetailsinv" role="tabpane3" style="height:240px">
					<div class="row" id="ecustomerBankDetails">
						<div class="col-md-6 col-sm-12 bnkdetails mt-1">
                  <div class="form-check mb-2 mb-sm-0" style="margin-top:0px; margin-bottom:5px;margin-left:2px;">
                  <div class="meterialform mt-2" style="float:left">
                      <div class="checkbox pull-right" id="ecustomerBankChecks" style="margin-top:-2px;">
                        <label>
                       <input class="eaddCustomerBankDetailss" type="checkbox" name="addCustomerBankDetailss">
                        <i class="helper"></i> Link Your Bank</label>
                      </div>
                    </div>
				  </div>
				  <br>
				  <p style="font-size:12px;display: inline-flex;">By Linking of Bank to this customer, bank details will be Automatically populate in Invoice Screen on selection of this customer. </p>
                </div>
				  <div class="col-md-6 col-sm-12 form-group" id="eselectCBankDiv3" style="display:none">
                  <p for="inlineFormInput" class="lable-txt mb-0">Select Bank</p>
                  <select class="form-control mb-2 mr-sm-2 mb-sm-0 eselectCustomerBanks" id="eselectCustomerBanks" onchange="eselectCustomerBankNames()">
                  </select>
				  <i class="bar"></i>
                </div>
                <div class="col-md-6 col-sm-12">
				</div>
				<div id="eselectCBankDiv2" class="col-md-6 col-sm-12 pl-3 pr-3 pull-right" style="display:none"> 
				<label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Account Number <span style="float: right;">:</span></label>
			  <span id="ecustomerBankAcctNos_txt" class="bold-txt" style="font-size:12px"></span><br/>
              <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Account Name <span style="float: right;">:</span></label>
			  <span id="ecustomerBankAccountNames_txt" class="bold-txt" style="font-size:12px"></span><br/>
			   <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Branch Name <span style="float: right;">:</span></label>
			  <span id="ecustomerBankBranchs_txt" class="bold-txt" style="font-size:12px"></span><br/>
              <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">IFSC Code <span style="float: right;">:</span></label>
			  <span id="ecustomerBankIFSCs_txt" class="bold-txt" style="font-size:12px"></span><br>
			  </div>
					</div>
					
					
								<div class="row" id="esupplierBankDetails" style="display:none">
									<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Beneficiary Name</p>
		                            <span class="errormsg" id="ebenefi_Msg"></span>
		                            <input type="text" class="esbeneficiaryName" id="esbeneficiaryName" name="beneficiaryName" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" placeholder="Beneficiary Name"  value="" />
		                            <label for="sbeneficiaryName" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Bank Name</p>
		                            <span class="errormsg" id="ebankname_Msg"></span>
		                            <input type="text" class="esbankName" id="esbankName" name="bankName" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" placeholder="Branch Name" value="" />
		                            <label for="sbankName" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Branch Address</p>
		                            <span class="errormsg" id="ebranchaddr_Msg"></span>
		                            <input type="text" class="esbranchAddress" id="esbranchAddress" name="branchAddress"  placeholder="Branch Address" value="" />
		                            <label for="sbranchAddress" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Account Number</p>
		                            <span class="errormsg" id="eaccNo_Msg"></span>
		                            <input type="text" class="esaccountNumber" id="esaccountNumber" name="accountNumber"  maxlength="18" minlength="9"  onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" pattern="^[0-9]*$" placeholder="Account Number" value="" />
		                            <label for="saccountNumber" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                             <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">IFSC Code</p>
		                            <span class="errormsg" id="eifsc_Msg"></span>
		                            <input type="text" class="esifscCode" id="esifscCode" name="ifscCode" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="IFSC Code" pattern="^[a-zA-Z0-9]*$"  value="" />
		                            <label for="sifscCode" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Account Type</p>
		                            <span class="errormsg" id="eacctype_Msg"></span>
		                            <input type="text" class="esaccountType" id="esaccountType" name="accountType"  onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="Account Type" pattern="^[a-zA-Z0-9]*$"  value="" />
		                            <label for="saccountType" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">MICR Code</p>
		                            <span class="errormsg" id="emicr_Msg"></span>
		                            <input type="text" class="esmicrCode" id="esmicrCode" name="micrCode"  onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="MICR Code" pattern="^[a-zA-Z0-9]*$" value="" />
		                            <label for="smicrCode" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                 </div>
					
					
					</div>
					
										<div class="tab-pane col-md-12" id="esupplieradditionaltab" role="tabpane5" style="height:210px">
					<div class="row">
					<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Company Registration Number</p>
		                            <span class="errormsg" id="eregno_Msg"></span>
		                            <input type="text" class="esregNo" id="esregNo" name="companyRegNo"  pattern="[0-9]+" data-error="Please enter valid landline number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
		                            <label for="sregNo" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Date Of Inception(dd/mm/yyyy)</p>
		                            <span class="errormsg" id="eincepitiondate_Msg"></span>
		                            <input type="text" class="esdateofinception" id="esdateofinception" name="dateofInception"  value="" />
		                            <label for="sdateofinception" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                             <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Nature Of Expertise</p>
		                            <span class="errormsg" id="eexpertize_Msg"></span>
		                            <input type="text" class="esnatuteOfExpertise" id="esnatuteOfExpertise" name="natuteOfExpertise"  value="" />
		                            <label for="snatuteOfExpertise" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Revenue TurnOver</p>
		                            <span class="errormsg" id="eturnover_Msg"></span>
		                            <input type="text" class="esturnover" id="esturnover" name="turnover"  value="" />
		                            <label for="sturnover" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Quality Programs And Certifications</p>
		                            <span class="errormsg" id="eprogandcert_Msg"></span>
		                            <input type="text" class="esprogramsAndCert" id="esprogramsAndCert" name="programsAndCert"  value="" />
		                            <label for="sprogramsAndCert" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Full Time Employees</p>
		                            <span class="errormsg" id="efulltimeemployees_Msg"></span>
		                            <input type="text" class="esfulltimeEmployees" id="esfulltimeEmployees" name="fulltimeEmployees"  value="" />
		                            <label for="sfulltimeEmployees" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Applicability Of Micro,Small,Medium Act 2006</p>
		                            <span class="errormsg" id="eapplicability_Msg"></span>
		                            <input type="text" class="esapplicability" id="esapplicability" name="applicability"  value="" />
		                            <label for="sapplicability" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
					
					
					</div>
					</div>
					
					<div class="tab-pane col-md-12" id="etermsdetailsinv" role="tabpane4" style="height:240px">
					<div class="row">
						<div class="col-md-12 col-sm-12 etermsdetails_inv mt-1">
		                  <div class="form-check mb-2 mb-sm-0" style="margin-top:0px; margin-bottom:5px;margin-left:2px;">
			                  <div class="meterialform mt-2" style="float:left">
			                      <div class="checkbox pull-right" id="ecustomerTermsChecks" style="margin-top:-2px;">
			                        <label>
			                       <input class="eaddCustomerTermsDetailss" type="checkbox" name="isCustomerTermsDetails">
			                        <i class="helper"></i>Enable Notes</label>
			                      </div>
			                    </div>
						  </div>
	                </div>
	                 <div class="col-md-12 col-sm-12" id="eselectTermsDivinv" style="display:none">
	                 	<textarea type="text" class="ecustTermsinv" id="ecustomertermsinv" name="customerterms" placeholder="Customer Notes" value="" style="border: 1px solid lightgray;width:100%;height:150px;"></textarea>
	                 </div>
                </div>
			</div>
					
					
				</div>
                       
						<input type="hidden" id="ecustomerBankNames" name="customerBankName">
						<input type="hidden" id="ecustomerBankAcctNos" name="customerAccountNumber">
						<input type="hidden" id="ecustomerBankAccountNames" name="customerAccountName">
						<input type="hidden" id="ecustomerBankBranchs" name="customerBranchName">
						<input type="hidden" id="ecustomerBankIFSCs" name="customerBankIfscCode">
						<input type="hidden" id="ecustomerorsuppliertype" name="customertype"/>
							<input type="hidden" name="userid" value="${id}">
						    <input type="hidden" name="fullname" value="${fullname}">
							<input type="hidden" name="clientid" value="${client.id}">	
							<input type="hidden" name="serialno" value="">
							<input type="hidden" name="createdDate" value=''>
							<input type="hidden" name="createdBy" value="">
                    </div>
                    
                   <div class="row">
                    <div class="col-12 mt-4 text-center" style="display:block">
                    <input type="button" class="btn btn-blue-dark itmcustsave" value="Save" id="einvaddcst" aria-label="Close" onclick="saveECustomer()"/>
					<a href="#" class="btn btn-blue-dark ml-2" onclick="closeAddCustomer('addEinvCustomerModal')" aria-label="Close">Cancel</a>
                   </div>
                   </div>
                    
					</form>
                </div>
            </div>
        </div>
    </div>
	<script type="text/javascript">
	$('input[type=radio][name=type]').change(function() {
			updateEState(this.value);
	});
		function updateEState(value) {	
		$('#estate').val('');
		
		if (value == 'Business') {
			$('#estate').val('');
			$('#eidName').html('Business Name');
			$('.eindividual').removeAttr('checked');
			$('.ebusiness').attr('checked','checked');
			$('.ecategory_business').show();
			$('#ebusinessName_Msg').text(""); 
			$('#emobilenumber_Msg').text("");
			$('#ecountry_Msg').text("");
			$('#econtactPersonName_Msg').text("");
			$('#estate_Msg').text("");
			$('#ecountry_Msg').text("");
			 $("#eselectTermsDivinv").css('display','none');
	    	 $('#ecustomertermsinv').val('');
			
			$('#ecustomerid_Msg').text("");
			$('#ecustomerpannumber_Msg').text("");
			$('#ecustomertanpannumber_Msg').text("");
			$('#ecustomertannumber_Msg').text("");
			$('#ecustomerid').val("");
			$('#ecustomerpannumber').val("");
			$('#ecustomertanpannumber').val("");
			$('#ecustomertannumber').val("");
			
		}else {
			$('#ecustomerid').val("");
			$('#ecustomerpannumber').val("");
			$('#ecustomertanpannumber').val("");
			$('#ecustomertannumber').val("");
			$('#ecustomerid_Msg').text("");
			$('#ecustomerpannumber_Msg').text("");
			$('#ecustomertanpannumber_Msg').text("");
			$('#ecustomertannumber_Msg').text("");		
			 $("#eselectTermsDivinv").css('display','none');
	    	 $('#ecustomertermsinv').val('');
	    	 
			$('#estate').val('');
			$('#eidName').html('Individual Name');
			$('.ecategory_business').hide();
			$('#egstnnumber').val("");
			$('#egstnnumber').prop('readonly', false);
			$('.eindividual').attr('checked','checked');
			$('.ebusiness').removeAttr('checked');
			$('#ebusinessName_Msg').text(""); 
			$('#emobilenumber_Msg').text("");
			$('#ecountry_Msg').text("");
			$('#econtactPersonName_Msg').text("");
			$('#estate_Msg').text("");
			$('#ecountry_Msg').text("");
			$('#egstnnumber').attr("required",false);
		}
		$(".form-group").removeClass("has-error has-danger");
		$('form[name="userform"]').validator('update');
	}
		$('.eaddCustomerTermsDetailss').click(function(){
			if($(".eaddCustomerTermsDetailss").is(':checked')){
		      $("#eselectTermsDivinv").css('display','block');
		    }else{
		    	 $("#eselectTermsDivinv").css('display','none');
		    	 $('#ecustomertermsinv').val('');
		    }
		});
	$('.eaddCustomerBankDetailss').click(function(){
	if($(".eaddCustomerBankDetailss").is(':checked')){
      $("#eselectCBankDiv2").css('display','none');
	  $("#eselectCBankDiv3").css('display','block');
	  $("#enotrequireds").css('display','none');
	  $('#eselectCustomerBanks').val('');
		$('#ecustomerBankNames').val('');
		$('#ecustomerBankAcctNos').val('');
		$('#ecustomerBankBranchs').val('');
		$('#ecustomerBankIFSCs').val('');
		$('#ecustomerBankAccountNames').val('');
		$('#ecustomerBankNames_txt').html(' ');
		$('#ecustomerBankAcctNos_txt').html(' ');
		$('#ecustomerBankBranchs_txt').html(' ');
		$('#ecustomerBankIFSCs_txt').html(' ');
		$('#ecustomerBankAccountNames_txt').html(' ');
    }else{
		$('#eselectCustomerBanks').val('');
		$('#ecustomerBankNames').val('');
		$('#ecustomerBankAcctNos').val('');
		$('#ecustomerBankBranchs').val('');
		$('#ecustomerBankIFSCs').val('');
		$('#ecustomerBankAccountNames').val('');
		$('#ecustomerBankNames_txt').html(' ');
		$('#ecustomerBankAcctNos_txt').html(' ');
		$('#ecustomerBankBranchs_txt').html(' ');
		$('#ecustomerBankIFSCs_txt').html(' ');
		$('#ecustomerBankAccountNames_txt').html(' ');
	  $("#eselectCBankDiv2").css('display','none');
	  $("#eselectCBankDiv3").css('display','none');
	  $("#enotrequireds").css('display','block');
   }
});


	function eselectCustomerBankNames() {
		//var bankaccountNumber = $('#selectBank').val();
		$('#ecustomerBankNames').val('');
		$('#ecustomerBankAcctNos').val('');
		$('#ecustomerBankBranchs').val('');
		$('#ecustomerBankIFSCs').val('');
		$('#ecustomerBankAccountNames').val('');
		$('#ecustomerBankNames_txt').html(' ');
		$('#ecustomerBankAcctNos_txt').html(' ');
		$('#ecustomerBankBranchs_txt').html(' ');
		$('#ecustomerBankIFSCs_txt').html(' ');
		$('#ecustomerBankAccountNames_txt').html(' ');
		if($('#eselectCustomerBanks').val() !=''){
			$("#eselectCBankDiv2").css('display','block');
			var bankaccountNumber = $('#eselectCustomerBanks').val();
			customerClientBankDetailss.forEach(function(bankdetail) {
				if(bankdetail.accountnumber == bankaccountNumber) {
					$('#ecustomerBankNames').val(bankdetail.bankname);
					$('#ecustomerBankAcctNos').val(bankdetail.accountnumber);
					$('#ecustomerBankBranchs').val(bankdetail.branchname);
					$('#ecustomerBankIFSCs').val(bankdetail.ifsccode);
					$('#ecustomerBankAccountNames').val(bankdetail.accountName);
					$('#ecustomerBankNames_txt').html(bankdetail.bankname);
					$('#ecustomerBankAcctNos_txt').html(bankdetail.accountnumber);
					$('#ecustomerBankBranchs_txt').html(bankdetail.branchname);
					$('#ecustomerBankIFSCs_txt').html(bankdetail.ifsccode);
					$('#ecustomerBankAccountNames_txt').html(bankdetail.accountName);
				}
			});
		}
	}

$(document).ready(function(){
	
	$('#estate').prop('readonly',false);
	$('.ecountriesList').change(function(){
		var country=$('.ecountriesList').val();
		if(country == 'India'){
			$('#estate').val('');
			$('#estate').prop('readonly',false);
			$('#egstnnumber').prop('readonly',false);
			$('#egstnnumber').val('');
			$('.with-errors').text("");
		}else if(country == ''){
			$('#estate').val('');
			$('#estate').prop('readonly',true);
			$('#egstnnumber').prop('readonly',false);
			$('#estate').prop('readonly',true);
			$('#egstnnumber').val('');
		}else{
			$('#estate').val('');
			$('#estate').prop('readonly',true);
			$('#estate').val('97-Other Territory');
			$('#egstnnumber').prop('readonly',true);		
			$('#egstnnumber').val('');
		}
	});
});

$('#ecustomerid').change(function() {

	var customerid=$('#ecustomerid').val();
	var clientid='<c:out value="${client.id}"/>';	
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url: "${contextPath}/customeridexits/"+clientid+"/"+customerid,
		success : function(response) {
			if(response == 'success'){
				$('#ecustomerid_Msg').text('customerid already exists');
				$('#ecustomer_type').addClass('has-error has-danger');
			}else{
				$('#ecustomerid_Msg').text('');
				$('#ecustomer_type').removeClass('has-error has-danger');
			}
		}
	});
});

$('#esuppliercustomerid').change(function() {

	var supplierid=$('#esuppliercustomerid').val();
	var clientid='<c:out value="${client.id}"/>';
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url: "${contextPath}/suppliercustomeridexits/"+clientid+"/"+supplierid,
		success : function(response) {
			if(response == 'success'){
				$('#esuppliercustomerid_Msg').text('supplier id already exists');
				$('#esupplier_type').addClass('has-error has-danger');
			}else{
				$('#esuppliercustomerid_Msg').text('');
				$('#esupplier_type').removeClass('has-error has-danger');
			}
		}
	});
});

$('#ecustname').keyup(function() {
	var spath = $("#ecustname").val();
	if(spath != ''){
		$('#eccustomerLedgerName').val(spath);
		$('#essupplierLedgerName').val(spath);
	}else{
		$('#eccustomerLedgerName').val('');
		$('#essupplierLedgerName').val('');
	}
});
	</script>
    <!-- Edit Modal End -->