<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
<title>MasterGST | E-INVOICE </title>
<script src="${contextPath}/static/mastergst/js/client/einvoice.js" type="text/javascript"></script>
</head>
<body>
<jsp:include page="/WEB-INF/views/client/addEItem.jsp"/>
<jsp:include page="/WEB-INF/views/einvoice/addEcustomer.jsp"/>
<jsp:include page="/WEB-INF/views/einvoice/einvoice_bds.jsp"/>
<div class="modal fade fullscreen" id="einvoiceModal" aria-labelledby="einvoiceModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
    <div class="modal-content">
    <div class="modal-header p-0">
    <div class="invoice-hdr bluehdr"><h3 id="einvTypeTxt">ADD B2B INVOICE</h3>
    <h6 id="irnNumber_txt" class="mt-1" style="color:white;display:none;">IRN No : <span id="irnNumber" name="irnNo"></span></h6>
    </div>
    <button type="button" class="close" aria-label="Close" onClick="closeEinvmodal('einvoiceModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button></div>
     <div class="modal-body">
		
	<form:form method="POST" data-toggle="validator" class="sortable-form mb-2" name="einvoceform" id="einvoceform"  modelAttribute="einvoice">
		<div class="customtable db-ca-view col-sm-12" id="wrapper">
			<div class="row mt-2">
              <div class="col-md-8 col-sm-12">
                <div class="row">
               
					   <div class="col-md-3 col-sm-12 form-group edocTypeDiv specific_field <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.CDNUR%>">
		                  <label for="inlineFormInputGroup" class="bold-txt astrich">Document Type</label>
		                 <select class="form-control" id="einvDocType" name="typ" required="required" data-error="Plese select Document Type"><option value="">-Select-</option><option value="CRN">CREDIT NOTE</option><option value="DBN">DEBIT NOTE</option></select>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div> 
					  <div class="col-md-3 col-sm-12 form-group" id="einvoice_number">
							  <label for="inlineFormInput" class="bold-txt astrich einvoice_NumberText">Invoice Number</label>
							  <input type="text" id="einvoiceserialno" name="b2b[0].inv[0].inum" required="required" maxlength="16" pattern="^[0-9a-zA-Z/ -]+$" data-error="Please enter valid invoice number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" aria-describedby="einvoiceserialno" placeholder="12345" class="form-control einvoiceserialno" />
							  <span class="control-label"></span>
							  <div class="help-block with-errors"></div>
					  </div>
	                  <div class="col-md-3 col-sm-12 form-group">
	                  <i class="fa fa-calendar" style="position: absolute;margin-left: 155px;margin-top: 30px;z-index: 0; color: gray;"></i>
		                  <label for="inlineFormInputGroup" class="bold-txt astrich einvoice_DateText">Invoice Date</label>
		                  <input type="text" id="einvoicedate" name="dateofinvoice" style="margin-top:0px" aria-describedby="einvoicedate" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" placeholder="DD/MM/YYYY" pattern="^(0?[1-9]|[12][0-9]|3[01])\/(0?[1-9]|1[012])\/[1-9]\d{3}$" required="required" data-error="Please enter valid invoice date" class="form-control" onChange="echeckInvoiceNumber()"/>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div>
	                  <div class="col-md-3 col-sm-12 form-group eplaceofsupply" id="einvoice_pos">
		                  <label for="inlineFormInputGroup" class="bold-txt astrich">Place Of Supply</label>
		                  <input id="invoiceStatecode" name="statename" placeholder="State" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]*\s*[a-zA-z]*" class="form-control einvstatecode" data-error="Please Enter place Of supply" required/>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
		                  <div id="invoiceStatecodeempty" style="display:none">
		                  		<div class="ddbox"><p>Search didn't return any results.</p></div>
		                  </div>
	                  </div>
                </div>
              </div>
              <div class="col-md-4 col-sm-12">
              <div class="row"><div class="col-md-12 col-sm-12"><div class="pull-right balancedue"><label class="bold-txt">Total Amount</label><h1><i class="fa fa-rupee"></i> <span id="eidTotal" class="indformat indformat_roundoff">0.00</span></h1></div></div></div></div>
              </div>
              <div class="row">
               <div class="col-md-8 col-sm-12">
                <div class="row">
                 <div class="col-md-6 col-sm-12 form-group" id="einvoicebillname">
		                 <label for="billingAddress" class="bold-txt custName custNameLabel astrich">Customer Name</label> 
		                 <span id="ebillingAddress_id" style="font-size: 12px;font-variant: full-width;font-weight: 700;float: right;color:green;"></span>
		                 <input type="text" class="form-control billedtoname" id="invoiceTradeName" name="billedtoname" required aria-describedby="customerName" placeholder="Customer Name" data-error="Please enter Customer Name" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" style="width: 96.8%!important;max-width: 100%;" onChange="eupdateCustomerName(this.value)"/>
		                 <span class="control-label"></span><div class="help-block with-errors"></div>
		                 <div id="eaddbilledtoname" style="display:none"><div class="ddbox2"><p id="newcust">Please add new customer</p><input type="button" class="btn btn-sm btn-blue-dark permissionSettings-Customers-Add" id="newcustval" value="Add New Customer" data-toggle="modal" onclick="updateCustomerName('invoiceTradeName', 'ecustname', 'addEinvCustomerModal','${varRetType}')" ></div></div>
                 </div>
                 <div class="col-md-3 col-sm-12 form-group" id="einvoicegstin">
                 	<label for="terms" class="bold-txt astrich" id="egstin_lab" style="width:100%">GSTIN<input type="button" onclick="einvokegstnPublicAPI(this,'invoice')" id="einvokegstnPublicAPI1" class="btn btn-green btn-sm pull-right disable" style="width: 50%; margin-top: -6px; margin-left: 6px; font-size:11px!important;margin-right:29px" value="Get GSTIN Details"/> </label>
                 	<span class="errormsg" id="eigstnnumber_Msg" style="margin-top: -42px;margin-right: 26px;"></span>
                 	<input type="text" class="form-control" id="ebilledtogstin" name="b2b[0].ctin" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4Z7" onChange="eupdatePan(this.value,'invoice')"/>
                 	<span class="control-label"></span><div class="help-block with-errors"></div>
                 </div>
				 <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.B2C%>">
					  
				  </div>
                 <div class="col-md-3 col-sm-12 form-group">
                 <label for="einvbillingAddress" class="bold-txt einvbuyerLabel">Buyer Details</label><a href="#" class="actionicons buyerAddrText che_form-control" style="font-size: 13px;line-height: 25px;" onclick="showBuyerModal()">Add</a>
	       				<div  id="einvbuyerAddr" style="min-height:60px;font-size:12px;word-break: break-all;font-family: sans-serif;"></div>
                  <!-- <div class="col-md-6 col-sm-12 form-group pl-0" id="einvbillingAdressdiv">
	                 <label for="einvbillingAddress" class="bold-txt einvbillingAddressLabel">Dispatcher Address</label><a href="#" class="actionicons" style="font-size: 13px;line-height: 25px;" onclick="showDispatchModal()">Edit</a>
	       				<div  id="einvdispatchAddr" style="min-height:60px;font-size:12px;word-break: break-all;font-family: sans-serif;"></div>
	               
                 </div> -->
                 </div>
				 
                 <div class="col-md-3 col-sm-12 form-group">
		                  <label for="inlineFormInputGroup" class="bold-txt einvcat astrich">Type Of Supply</label>
		                  <select class="form-control" required="required" id="invCategory" name="einvCategory" onchange="updateInvTypeOfSupply()" data-error="Please Select supply type"><option value="">--Select-</option><option value="DEXP">Deemed Export</option><option value="B2B">Business to Business</option><option value="SEZWP">SEZ with payment</option><option value="SEZWOP">SEZ without payment</option><option value="EXPWP">Export with Payment</option><option value="EXPWOP">Export without payment</option></select>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div>
                 
                  <div class="col-md-3 col-sm-12 form-group eoriginalInvNoDiv specific_field <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.CDNUR%>">
		                  <label for="inlineFormInputGroup" class="bold-txt">Original Invoice No</label>
		                 <input type="text" class="form-control" name="cdn[0].nt[0].ntNum" id="evoucherNumber" maxlength="16"/>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div>
					  <div class="col-md-3 col-sm-12 form-group eoriginalInvNoDiv specific_field <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>">
						<label for="terms" class="bold-txt datepattern">Original Invoice Date(DD-MM-YYYY)</label>
						<input type="text" class="form-control date_field" id="evoucherDate" name="cdn[0].nt[0].ntDt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 45) || (event.charCode == 0))" pattern="^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-[1-9]\d{3}$" placeholder="DD-MM-YYYY" data-error="Please enter Voucher Date"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
	                   <div class="col-md-3 col-sm-12 form-group">
		                  <label for="inlineFormInputGroup" class="bold-txt">E-Commerce GSTIN</label>
		                 <input type="text" class="form-control" name="b2b[0].etin" id="einvEcommGSTIN"/>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div>
	                  <div class="col-md-3 col-sm-12 form-group">
		                  <label for="inlineFormInputGroup" class="bold-txt">IGST on Intra</label>
		                 <select class="form-control" name="igstOnIntra" id="igstOnIntra" onchange="updateInvTypeOfSupply()">
			                 <option value="N">No</option>
			                 <option value="Y">Yes</option>
		                 </select>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div>      
	                   <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%>">
		                   <label for="terms" class="bold-txt">Port Code</label>
		                   <input type="text" class="form-control einvedit" id="eportcode" name="exp[0].inv[0].sbpcode" data-error="Please Enter Port Code" maxlength="6"/>
		                   <span class="control-label"></span>
		                   <div class="help-block with-errors"></div>
	                   </div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%>">
					  <label for="terms" class="bold-txt">Shipping Bill Number</label>
					  <input type="text" class="form-control einvedit" id="eshippingBillNumber" name="exp[0].inv[0].sbnum" data-minlength="3" maxlength="7" data-error="Please Enter valid Shipping Bill Number"/>
					  <span class="control-label"></span>
					  <div class="help-block with-errors"></div>
				  </div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%>">
					  <label for="terms" class="bold-txt">Shipping Bill Date</label>
					  <input type="text" class="form-control date_field einvedit" id="eshippingBillDate" name="exp[0].inv[0].sbdt" placeholder="DD-MM-YYYY" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 45) || (event.charCode == 0))" pattern="^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-[1-9]\d{3}$" data-error="Please Enter Shipping Bill Date"/>
					  <span class="control-label"></span>
					  <div class="help-block with-errors"></div>
				  </div>
	                  
	                    <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%>">
		                  <label for="inlineFormInputGroup" class="bold-txt">Country Code</label>
		                 <input class="form-control einvedit" name="countryCode" id="einvCountryCode" minlength="2" maxlength="2"/>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div>
	                  
	                   <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%>">
		                  <label for="inlineFormInputGroup" class="bold-txt">Additional Currency Code</label>
						 <select class="form-control einvedit addcurrencyCode" name="addcurrencyCode" id="addcurrencyCode" onchange="updateCurrencyCode()">
							
						</select>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div>
                </div>
                </div>
                 <div class="col-md-4 col-sm-12" id="einvAddress_wrap">
                <div class="row mb-4">
               
                
                
                 <div class="col-md-6 col-sm-12 form-group pl-0" id="einvbillingAdressdiv">
	                 <label for="einvbillingAddress" class="bold-txt einvbillingAddressLabel">Dispatcher Address</label><a href="#" class="actionicons che_form-control" style="font-size: 13px;line-height: 25px;" onclick="showDispatchModal()">Edit</a>
	       				<div  id="einvdispatchAddr" style="min-height:60px;font-size:12px;word-break: break-all;font-family: sans-serif;"></div>
	               
                 </div>
                  <div class="col-md-6 col-sm-12" id="einvshippingAdressdiv">
                  <label for="einvshippingAddress" class="bold-txt ShipmentAddressLabel">Shipment Address</label><a href="#" class="actionicons d-none" style="font-size: 13px;line-height: 25px;" onclick="showShipmentModal()" id="shimentAddrEdit">Edit</a>
                   <div  id="einvshipmentAddr" style="min-height:60px;font-size:12px;word-break: break-all;font-family: sans-serif;"></div>
                  </div>	
                  <div class="col-md-3 col-sm-12 einvoiceLevelCessDiv pr-0" style="margin-top: 35px;"><div class="form-check mb-2 mb-sm-0 einvoiceLevelCess"><div class="meterialform"><div class="checkbox che_form-control"><label><input type="checkbox" id="einvoiceLevelCess" onchange="changeeInvoiceLevelCess()"><i class="helper che-box"></i> Show Cess</label></div></div></div></div> 
                    <div class="col-md-4 col-sm-12 pl-0 pr-0" style="margin-top: 35px;">
	                    <div class="form-check mb-2 mb-sm-0 rateinclusive">
		                    <div class="meterialform">
			                    <div class="checkbox che_form-control">
			                    	<label><input type="checkbox" name="includetax"  id="einvincludetax" onchange="inclusiveTaxSelection()"><i class="helper che-box"></i>Rate inclusive of Tax</label><span class="rateinctax errormsg"></span>
			                    </div>
		                    </div>
	                    </div>
                    </div>
                  <div class="col-md-5 col-sm-12 pl-0" style="margin-top: 35px;">
	                  <div class="form-check mb-2 mb-sm-0 pull-right" id="esamebilladdressdiv">
		                  <div class="meterialform">
			                  <div class="checkbox che_form-control">
			                	  <label><input type="checkbox" name="samebilladdress" value="1" id="samedispatchaddress" onChange="samedispatchaddresscheck()"><i class="helper che-box"></i> Same as Buyer Address</label>
			                  </div>
		                  </div>
	                  </div>
                  </div>
<div class="col-md-8 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%> pl-0" style="margin-top: 25px;">
		                  <label for="inlineFormInputGroup" class="bold-txt">Exchange Rate</label>
		                  <div class="row">
		                  	<div class="col-md-3 col-xs-4 p-0 usd-lable bold-txt"><span style="padding-left:2px">1 INR = &nbsp;</span></div>
		                  	<div class="col-md-5 col-xs-6 p-0"><span class="exchange-span bold-txt">INR</span><input class="form-control einvedit" name="exchangeRate" id="exchangeRate" onkeyup="updateInvTypeOfSupply()"/></div>
		                  </div>
		                  <span class="control-label"></span>
		                  <div class="help-block with-errors"></div>
	                  </div>      				  
			   </div>
              </div>
              </div>
              
              <div class="pull-left" style="height:20px"><span class="errormsg" id="eInvitemdetails"></span></div>
			   <table align="center" id="einvoice_table" border="0" class="row-border dataTable">
              <thead>
                <tr>
                <th rowspan="2" width="3%">S.No</th>
                <th rowspan="2" width="20%" class="">Item/Product/Service</th>
                <th rowspan="2" width="8%" class="astrich">HSN/SAC</th>
                <th rowspan="2" width="8%" class="astrich">UQC</th>
                <th rowspan="2" width="7%">Bar Code</th>
                <th rowspan="2" width="5%" class="astrich">Qty</th>
                <th rowspan="2" width="5%">Free Qty</th>
                <th rowspan="2"  width="5%" class="astrich">Rate</th>
                <th rowspan="2" width="" class="discountFlag">Discount</th>
                <th rowspan="2" width="5%">Other Charge</th>
                <!--<th rowspan="2" width="5%">Ass.Amt</th>-->
                <th rowspan="2" width="7%">Taxable <br>Amount</th>
                <th rowspan="2" width="5%" class="astrich">Tax <br>Rate</th>
                <th rowspan="2" width="5%">Tax (GST)</th>
                 <!-- <th rowspan="2" width="2%">State Cess</th> -->
                <th colspan="2" width="8%" class="p-0 ecessFlag">Cess on
                <div class="form-group-inline cesstype">
                 <div class="form-radio mr-0">
                                    <div class="radio">
                                        <label>
                                            <input name="cessType" id="ecess_taxable" class="cessType" type="radio" value="Taxable Value" onchange= "changeEinvCessType()" checked/>
                                            <i class="helper"></i>Taxable</label>
                                    </div>
                                </div>
                                <div class="form-radio mr-0">
                                    <div class="radio">
                                        <label>
                                            <input name="cessType" id="ecess_qty" class="cessType" type="radio" value="Quantity" onchange= "changeEinvCessType()"/>
                                            <i class="helper"></i>Qty</label>
                                    </div>
                                </div>
                               
                            </div>
                </th>
                <!--  <th rowspan="2" width="3%">CESS Non-Advol</th> -->
                <th rowspan="2" width="9%">Total</th>
				<th rowspan="2" width="9%" class="addExportFlag" id="currencytext">Cur Total</th>
                <th rowspan="2" width="3%"></th></tr>
                 <tr><th class="ecessFlag">Rate</th><th class="ecessFlag">Amount</th></tr>
              </thead>
              <tbody id="all_einvoice">
                <tr id="1" class="rowshadow item_edit">
                  <td id="esno_row1" align="center">1</td>
                  <td id="" class="form-group noadvFlag product_notes"><input type="hidden" class="form-control" id='ledger1' name='items[0].ledgerName'  placeholder="ledger"><div class="col-md-12 p-0"><input type='text' class='form-control input_itemDetails_txt itemDetails itemname1' id='eproduct_text1' name='items[0].itemno' placeholder='Item/Product/Service' value=''></div><div id='eremainproduct_textempty1' style='display:none'><div class='remainddbox1 dbbox permissionSettings-Items-Add'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateItemNames(1)'></div></div>
                  <div class="dropdown dropdown-search1 col-md-1 p-0" data-toggle="tooltip" title=" Click on Icon 'C' to enter additional details of your Item/Product/Service" style="margin-right:8px;">
	                 <span class="itemnote_info_icon" onclick="showEinvAdditionalItemFieldsPopup(1)" id="dropdownMenuitemdec"><i><b>C</b></i></span>
	                  	<div class="modal-d modal-arrow1" style="display:none;"></div>
	                  <!-- <div  id="item_notes_filed" class="dropdown-menu search1" aria-labelledby="dropdownMenuitemdec" style="border: none;">
		                  <div class="itemnot_hdr"><span class="itemnot_lable">ITEM NOTES</span>
		                  <span class="itemno_clo_span"><i class="fa item-close-icon">&#xf00d;</i></span></div>
		                  <textarea rows="3" cols="23" class="item_notes" id="eitemnotes_text1" name="items[0].itemNotescomments" Placeholder="Item Description"></textarea>
	                  </div> -->
                  </div>
                  </td>
                  <td id=""  class="form-group"><input type='text' class='form-control' id='ehsn_text1' name='items[0].hsn' placeholder='HSN/SAC' value='' onkeypress='' required><div id='eitemcodeempty' class="remainEINVOICEddbox31" style='display:none'><div class='eremainhsnddbox1'><p>Search didn't return any results.</p></div></div></td>
                  <td id="" class="form-group"><input type='text' class='form-control' id='euqc_text1' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items[0].uqc' value='' required><div id='uqc_textempty' style='display:none' class="remainEINVOICEddbox41"><div class='eremainddboxuqc4'><p>Search didn't return any results.</p></div></div></td>
                  <td class="form-group"><input type='text' class='form-control' id='ebarcode_text1' placeholder='Bar Code' name="items[0].barCode"/></td>
                  <td id="" align="right" class=""><input type="hidden" id="eopening_stock1"/><input type="hidden" id="esaftey_stock1"/><input type="text" onchange="changeeStock(1)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)"  class="form-control text-right" required id="eqty_text1" name="items[0].quantity"  value="" onKeyUp="findEinvTaxableValue(1)" pattern="^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="quantity" placeholder="Qty" /></td>
                  <td id="" align="right" class=""><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)"  class="form-control text-right" id="efreeqty_text1" name="items[0].freeQty"  value="" onKeyUp="findEinvTaxableValue(1)" pattern="^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="quantity" placeholder="FreeQty" /></td>
                  <td id="" align="right" class="form-group"><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" class="form-control text-right" id="erate_text1" name="items[0].rateperitem" required value="" onKeyUp="findEinvTaxableValue(1)" pattern="^[0-9]+(\.[0-9]{1,10})?$" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="rate" placeholder="Rate" /></td>
                  <td id="" align="right" class="form-group discountFlag"><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" class="form-control text-right" id="ediscount_text1" name="items[0].discount" value="" onKeyUp="findEinvTaxableValue(1)" pattern="^[0-9]+(\.[0-9]{1,10})?$" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="discount" placeholder="Discount" /></td>
                  <td id="" align="right" class="form-group"><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" class="form-control text-right" id="othrcharge_text1" name="items[0].othrCharges" value="" onKeyUp="findEinvTaxableValue(1)" pattern="^[0-9]+(\.[0-9]{1,10})?$" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="Other Charge" placeholder="Other Charge" /></td>
				  <!--<td id="" class="" align="right"><input type='text' class='form-control indformat' id='eassamt_text1' name='items[0].assAmt'  readonly></td>-->
				  <td id="" class="tablegreybg" align="right"><input type='text' class='form-control indformat text-right' id='etaxableamount_text1' name='items[0].taxablevalue' readonly></td>
				  <td id="" align="right" class="form-group"><select id="etaxrate_text1" class="form-control" name="items[0].rate" onchange="findEinvTaxAmount(1)" required><option value="">-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td>
				  <td id="" class="tablegreybg form-group" align="right"><input type='text' class='form-control dropdown  text-right' id='eabb1' name='items[0].totaltaxamount'  readonly><div id='etax_rate_drop' style='display:none'><div id='eicon-drop'></div><i style="font-size:12px;display:none" class="fa">&#xf00d;</i><h6 style='text-align: center;' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST <span style='margin-left:5px'>:<span></p><span><input type='text' class='form-control dropdown' id='eigsttax_text1' name='items[0].igstamount' style='border:none;width: 70px;padding-top: 2px;background: none;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='ecgsttax_text1' name='items[0].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='esgsttax_text1' name='items[0].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td>
				   <!-- <td class="form-group"><input type='text' class='form-control statecessval' id='estatecess_text1' name="items[0].stateCess" onKeyUp="findEinvTaxableValue(1)"/></td> -->
				  <td id="" align="right" class="form-group ecessFlag"><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)" id="ecessrate_text1" class="form-control cessval text-right" name="items[0].cessrate" onKeyUp="findEinvCessAmount(1)" pattern="^[0-9]+(\.[0-9]{1,2})?[%]?$" data-error="Please enter numeric value with a max precision of 2 decimal places" /></td>
				  <td id="" class="tablegreybg form-group SnilFlag ecessFlag" align="right"><input type='text'  class='form-control text-right cessamtval' id='ecessamount_text1' name='items[0].cessamount'  readonly></td>
				 <!-- <td class="form-group"><input type='text' class='form-control cessnonadvolval' id='ecessnonadv_text1' name="items[0].cessNonAdvol" onKeyUp="findEinvTaxableValue(1)"/></td> -->
				  <td id="" class="tablegreybg form-group" align="right"><input type='text' class='form-control text-right' id='etotal_text1' name='items[0].total' readonly></td>
				  <td id="" class="tablegreybg form-group addExportFlag" align="right"><input type='text' class='form-control text-right' id='ecurtotal_text1' name='items[0].currencytotalAmount' readonly></td>
				  <td align="center" width="2%"><a href="javascript:void(0)" id="edelete_button1" class="eitem_delete" onclick="delete_erow(1)"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td>
                <td class="d-none"><input type='hidden' class='form-control text-right' id='eitemCustomField_text11' name='items[0].itemCustomField1'>
                   <input type='hidden' class='form-control text-right' id='eitemCustomField_text21' name='items[0].itemCustomField2'>
                   <input type='hidden' class='form-control text-right' id='eitemCustomField_text31' name='items[0].itemCustomField3'>
                   <input type='hidden' class='form-control text-right' id='eitemCustomField_text41' name='items[0].itemCustomField4'>
                   <input type='hidden' class='form-control text-right' id='eitemnotes_text1' name='items[0].itemNotescomments'>
                    <input type='hidden' class='form-control text-right' id='eitemId_text1' name='items[0].itemId'>
                   </td>
                </tr>
				<tr style="display:none" class="rowshadow item_edit">
                  <td align="center"></td><td align="left"><input type="text" id="new_product" class="btn"></td><td align="left"><input type="text" id="new_hsn" class="btn"></td><td align="right"><input type="text" id="new_uqc" class="btn"></td><td align="right"><input type="text" id="new_barcode" class="btn"><input type="text" id="new_qty" class="btn"></td><td align="right"><input type="text" id="new_rate" class="btn"></td><td align="right"><input type="text" id="new_discount" class="btn"></td>
                  <td align="right"><input type="text" id="new_taxableamount" class="btn" disabled></td><td align="right"><input type="text" id="new_taxrate" class="btn SnilFlag"></td><td align="center"><input type="text" id="new_igsttax" class="btn SnilFlag"  disabled></td><td align="center"><input type="text" id="new_cgsttax" class="btn SnilFlag"  disabled></td><td align="center"><input type="text" id="new_sgsttax" class="btn SnilFlag"  disabled></td>
               	<td align="center"><input type="text" id="new_cessrate" class="btn SnilFlag"></td><td align="center"><input type="text" id="new_cessamount" class="btn SnilFlag" disabled></td><td align="center"><input type="text" id="new_itcrule" class="btn addITCRuleFlag"></td><td align="center"><input type="text" id="new_itctype" class="btn addITCFlag"></td><td align="center"><input type="text" id="new_itcpercent" class="btn"></td><td align="center"><input type="text" id="new_igstitc" class="btn addITCFlag" disabled></td>
                  <td align="center"><input type="text" id="new_cgstitc" class="btn addITCFlag" disabled></td><td align="center"><input type="text" id="new_sgstitc" class="btn addITCFlag" disabled></td><td align="center"><input type="text" id="new_cessitc" class="btn addITCFlag"></td><td align="center"><input type="text" id="new_advrcvd" class="btn addAdvFlag" disabled></td><td align="center"><input type="text" id="new_total" class="btn" disabled></td><td width="3%"></td>
                </tr>
              </tbody>
              <tfoot id="eallinvoicettfoot">
                <tr>
                  <th colspan="9" class="tfootwitebg einvoicefooter"><span class="add pull-left" id="adderow1" onclick="addEinvoiceRow('EINVOICE')"><i class="add-btn">+</i> Add another row</span><span class="pull-right SnilFlag">Total Inv. Val</span></th><th class="item_ledger tfootwitebg tcuramt" style="display:none"></th>
                  <th class="tfootbg indformat SnilFlag itcFlag" id="etotTaxable" style="border: none;"></th><th class="tfootwitebg  row_foot"></th><th class="tfootbg indformat SnilFlag" id="etotIGST"></th><!-- <th class="tfootbg indformat SnilFlag" id="totCGST"></th><th class="tfootbg indformat SnilFlag" id="totSGST"></th> --><!-- <th class="tfootbg indformat row_foot" id="etotStateCess"></th> --><th class="tfootbg ecessFlag"></th><th class="tfootbg indformat SnilFlag ecessFlag" id="etotCESS"></th><th class="tfootwitebg addITCRuleFlag itcrule_text1"></th>
                  <th class="tfootwitebg addITCFlag"></th><th class="tfootwitebg addITCFlag"></th><th class="tfootwitebg addITCFlag indformat" id="totITCIGST"></th><!-- <th class="tfootwitebg addITCFlag indformat" id="totITCCGST"></th><th class="tfootwitebg addITCFlag indformat" id="totITCSGST"></th><th class="tfootwitebg addITCFlag indformat" id="totITCCESS"></th> -->
                 	<!-- <th class="tfootbg indformat" id="totCessAdvol"></th> --> <th class="tfootbg indformat" id="etotTotal"></th><th class="tfootbg indformat addExportFlag" id="totcurAmt"><th class="tfootwitebg addbutton"> <span class="add add-btn" id="adderow" onclick="addEinvoiceRow('EINVOICE')">+</span></th>
                </tr>
              </tfoot>
            </table>
                      <div class="col-12 mt-2  form-group-inline p-0" style="display:block">
            <div class="sortable-form">
              <div class="row no_vehicle">
       		<div class="col-md-2 col-sm-12 form-group" id="einvreversecharge">
	                   <label for="inlineFormInputGroup" class="bold-txt">Reverse Charge :</label>
	                  <select class="form-control einv_revchargetype" name="revchargetype" id="einv_revchargetype" onchange="reversechargeeinvoice()"><option value="N">Regular</option><option value="Y">Reverse</option></select>
	             	  <span class="control-label"></span>
		               <div class="help-block with-errors"></div>
	                </div>
                <div class="col-md-2 col-sm-12"><h6>Reference : </h6><input id="ereferenceNumber" name="referenceNumber" placeholder="P.O Number or Any other text" class="form-control einvedit"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				<div class="col-md-2 col-sm-12"><h6>Branch :</h6><select class="form-control einvedit" name="branch" id="ebranch" readonly></select></div>
				<div class="col-md-2 col-sm-12"><h6>Vertical :</h6><select class="form-control einvedit" name="vertical" id="evertical" readonly></select></div>
               <!--  <div class="col-md-2 col-sm-12 rounoff" id="eroundoffdiv" style="height:63px!important"></div> -->
                <div class="col-md-2 pr-0 mt-2" id="">
	            <div class="col-md-12 col-sm-12 pl-0 pr-0">
		            <h6>&nbsp;</h6>
		            <div class="form-check mb-sm-0">
			            <div class="meterialform">
				            <div class="checkbox che_form-control" id="ediffpercent" onchange="differentialSelection()">
				            	<label style="font-size: 12px;padding-left: 18px;"><input class="diffPercent" id="ediffPercent" type="checkbox" name="diffPercent" value="No"><i class="helper"></i><strong> Differential Percentage(0.65)</strong></label>
				            </div>
			            </div>
		            </div>
	            </div>
	                </div>
                <div class="col-md-2 col-sm-12 form-group rounoff pl-0" id="eroundOffTotalAmount"><h6>&nbsp;</h6>
                  <input class="form-control col-xs-2" name="roundOffAmount" id="eroundOffTotalValue" style="width: 60px;text-align: right; float:right;margin-right:16px" readonly/>                   
				  <div class="form-check mb-2 mb-sm-0 ml-2" style="float:left"><div class="meterialform"><div class="checkbox pull-right che_form-control" id="eroundOffTotal" onclick="roundOffSelection()" style="margin-top:-2px; margin-right:15px">
                        <label style="font-size: 12px;padding-left: 27px;"><input class="roundOffTotal" type="checkbox" id="eroundOffTotalChckbox" name="roundOffTotal"><i class="helper"></i><strong>Roundoff Total</strong></label>
                  </div></div></div>
                </div>
              </div>
            </div>
            <div class="bdr-b"></div>
            <div class="row no_vehicle mt-2">
			
			<div class="col-md-2 col-sm-12 form-group mb-0 eTerms_Conditions_wrap" id="eTerms_Conditions_wrap">
						<label for="inlineFormInput" class="bold-txt">Terms</label>
						<span class="term-span bold-txt">Days</span>
						<input type="text" class="form-control mb-2 mb-sm-0 invedit" id="etermDays" name="termDays" pattern="^[0-9]*$" placeholder="Payment Term Days" onkeyup = "epaymentterm()"  onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" data-error="Please Enter Number"><span class="control-label"></span><div class="help-block with-errors"></div>
					</div>
		
		              <div class="col-md-2 col-sm-12 eTerms_Conditions_wrap mb-0"><i class="fa fa-calendar" style="position: absolute;margin-left: 155px;margin-top: 30px;z-index: 0; color: gray;"></i><label for="inlineFormInput" class="bold-txt">Due Date(DD/MM/YYYY)</label><input class="form-control" id="eduedate_div" name="dueDate" /></div>
		         
                <div class="col-md-2 col-sm-12 ebnkdetail mb-0">
		                <div class="form-check mb-2 mb-sm-0">
			                <div class="meterialform">
				                <div class="checkbox" id="ebankCheck" style="margin-top:-2px;">
				                <h6>&nbsp;</h6>
				                	<label><input class="eaddBankDetails einvedit" type="checkbox" name="addBankDetails" onchange="changeBankDetails()"><i class="helper"></i> Add Bank Details</label>
				                </div>
			                </div>
		                </div>
                </div>
                <div class="col-md-2 col-sm-12 form-group mb-0" id="eselectBankDiv">
	                <label for="inlineFormInput" class="bold-txt">Select Bank</label>
	                <select class="form-control mb-2 mr-sm-2 mb-sm-0 eselectBank einvedit" id="eselectBank" onchange="eselectBankName()"></select>
                </div>
          		<div class="col-md-4 col-sm-12 mt-2 pr-0 row">
          		<div class="form-check col-md-3 mb-sm-0">
		                <div class="meterialform">
			                <div class="checkbox ml-0 mt-3" id="etcsvaldiv">
			               		 <label><input class="etcsval einvedit" id="etcsval" type="checkbox" name="tdstcsenable"  onchange="etcscheckval()" value="false"><i class="helper"></i><strong> <span class="eapply_type">Add TCS</span></strong></label>
			                </div>
		                </div>
	                </div>
          			<div class="col-md-3 col-sm-12 form-group mb-0" id="esection" style="margin-top:-10px">
	             <label for="sectionlable" class="bold-txt ">Section</label>
	              <span class="errormsg esection"></span>
	              <select class="form-control esection einvedit" id="etdstcssection" name="section" onchange="etdstcschange()" aria-describedby="section" placeholder="Section" data-error="Please enter section Name" style="max-width: unset;width:70px;min-width: unset;" readonly disabled>
				  <option value="">-- Select Section --</option>
				  <option value="206C(1)">206C</option>
				  </select>
	              <span class="control-label"></span>
	              <div class="help-block with-errors"></div>
              </div>
			 <div class="col-md-4 col-sm-12 form-group mb-0 ml-2" id="eTcs_percent" style="margin-top:-10px">
				 <label for="inlineFormInputGroup" class="bold-txt eperc_type">TCS Percentage</label>
				 <input id="etcs_percent" name="tcstdspercentage" class="form-control einvedit" pattern="^[0-9]+(\.[0-9]{1,10})?$" onkeyup="etdstcscal()" data-error="Please enter valid value" readonly/>
				 <span class="control-label"></span>
				 <div class="help-block with-errors"></div>
			 </div>
                </div>
				</div>
				<div class="row mt-2">
		              <div class="col-md-4 col-sm-12 form-group" id="ecustomer_notes_wrap">
			              <label for="inlineFormInput" class="bold-txt">Customer Notes</label>
			              <textarea class="form-control mb-2 mr-sm-2 mb-sm-0 einvedit" id="ebankNotes" style="min-height:80px!important" name="notes">Customer Notes</textarea>
		              </div>
					  <div class="col-md-4 col-sm-12 form-group"> 
						   <div class="form-inline bank-details-box sortable-form" id="ebank_details" style="display:none;">
			              		<div class="col-12 p-0" style="display:block">
						              <div class="row">
								             <div class="col-md-6 col-sm-12 form-group"> <label for="inlineFormInput" class="bold-txt">Bank Name</label><input type="text" class="form-control mb-2 mb-sm-0 einvedit" id="ebankName" name="bankDetails.bankname" style="margin-top:5px;margin-bottom:5px!important" placeholder="Bank Name" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" data-error="Please enter Bank Name"><span class="control-label"></span><div class="help-block with-errors"></div></div>             
								              <div class="col-md-6 col-sm-12 form-group bacno"><label for="inlineFormInput" class="bold-txt">Account Number</label><input type="text" class="form-control mb-2 mb-sm-0 einvedit" id="ebankAcctNo" name="bankDetails.accountnumber" style="margin-top:5px;margin-bottom:5px!important"  pattern="^[0-9]*$" placeholder="Account Number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" data-error="Please Enter Account Number"><span class="control-label"></span><div class="help-block with-errors"></div></div> 
								              <div class="col-md-6 col-sm-12 form-group"><label for="inlineFormInput" class="bold-txt">Account Name</label><input type="text" class="form-control mb-2 mb-sm-0 einvedit" id="ebankAccountName" name="bankDetails.accountName" placeholder="Account Name" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$"><span class="control-label"></span><div class="help-block with-errors"></div></div>               
											  <div class="col-md-6 col-sm-12 form-group"><label for="inlineFormInput" class="bold-txt">Branch Name</label><input type="text" class="form-control mb-2 mb-sm-0 einvedit" id="ebankBranch" name="bankDetails.branchname" placeholder="Branch Name" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" data-error="Please enter Branch Name"><span class="control-label"></span><div class="help-block with-errors"></div></div>             
											  <div class="col-md-6 col-sm-12 form-group bifscno"><label for="inlineFormInput" class="bold-txt">IFSC Code</label><input type="text" class="form-control mb-2 mb-sm-0 einvedit" id="ebankIFSC" name="bankDetails.ifsccode" placeholder="IFSC Code" onkeypress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" pattern="^[a-zA-Z0-9]*$" data-error="Please enter IFSC Code"><span class="control-label"></span><div class="help-block with-errors"></div></div>
						              
						              </div>
			             		 </div>
			              </div>
		              </div>
		            <div class="col-md-3 col-sm-12 form-group mb-0 entertaiment_einvoice_import_template">
	             <label for="sectionlable" class="bold-txt">Invoice Issued To</label>
	              <span class="errormsg"></span>
	              <select class="form-control einvedit" id="entertaimentprintto" name="entertaimentprintto" aria-describedby="section" data-error="Please enter section Name">
					  <option value="">-- Select --</option>
					  <option value="Exhibitor">Exhibitor</option>
					  <option value="Producer">Producer</option>
				  </select>
	              <span class="control-label"></span>
	              <div class="help-block with-errors"></div>
              </div>  
					  <div class="col-md-4 sortable-form">
              <div class="row" id="einvCustomFields_row">
              </div>
            </div>
              </div>
              
          </div>
		</div>
</div>
		  <div class="modal-footer" style="display:block;text-align:center;">
				<input type="button" class="btn btn-primary mr-0" id="einvDraft_Btn" value="Save As Draft" onclick="saveEinvoiceAsDraft('GSTR1')"/>
				<input type="button" class="btn btn-greendark permissionEinvoice_Actions-Generate_IRN mr-0" id="generateIRNBtn" value="Generate IRN & Save" onclick="generateIRN('EINVOICE')" style="display:none;"/>
				<button type="button" class="btn btn-greendark permissionEinvoice_Actions-Cancel_IRN mr-0" id="cancelIRN_btn" style="display:none;" onclick="showEinvoiceCancelPopup('EINVOICE')">Cancel IRN</button>
				<!-- <input type="button" class="btn permissionEinvoice_Actions-Generate_IRN mr-2 save-mail" id="email_Send_btn" value="Generate IRN & Email"> -->
				 <button type="button" class="btn btn-primary" onClick="closeEinvmodal('einvoiceModal')">Close</button>
				<span style="display:none" id="einvnotes">${client.notes}</span><span style="display:none" id="einvnotess">${client.notes}</span>
				 
				 <input type="hidden" name="userid" id="euserid" value="<c:out value="${id}"/>">
				 <input type="hidden" name="clientid" id="eclientid" value="<c:out value="${client.id}"/>">
				 <input type="hidden" name="fullname" id="fullname" value="<c:out value="${fullname}"/>">
				  <input type="hidden" name="id" id="einvoiceId" value="">
				  <input type="hidden" id="einvLevelCess" name="invoiceLevelCess" value="">
				   <input type="hidden" name="customerEmail" id="customer_email" value="">
				   <input type="hidden" id="ecustomer_MailId" name="customerMailIds" value=""/>
				   <input type="hidden" id="ecustomer_CCMailId" class="ecustomer_CCMailId" name="customerCCMailIds" value=""/>
				  <input type="hidden" name="irnNo" id="irnNo" value="">
				  <input type="hidden" name="irnStatus" id="irnStatus" value="">
				  <input type="hidden" name="signedQrCode" id="signedQrCode" value="">
				  <input type="hidden" name="signedInvoice" id="signedInvoice" value="">
				  <input type="hidden" name="ackNo" id="ackNo" value="">
				  <input type="hidden" name="einvStatus" id="einvStatus" value="">
				  <input type="hidden" name="srctype" id="esrctype" value="">
				  <input type="hidden" name="ackDt" id="ackDt" value="">
				 <input type="hidden" name="invtype" id="idEInvType" class="form-control" value="">
				 <input type="hidden" name="notroundoftotalamount" id="ehiddenroundOffTotalValue"/>
				 <input type="hidden" id="epaymentStatusinv" name="paymentStatus" value=""/>
				 <input type="hidden" id="eInvPendingAmount" name="pendingAmount" value=""/>
				 <input type="hidden" id="eInvReceivedAmount" name="receivedAmount" value=""/>
				 	<input type="hidden" name="buyerDtls.gstin" id="buyerDtls_gstin"/>
					<input type="hidden" name="buyerDtls.lglNm" id="buyerDtls_lglNm"/>
				   <input type="hidden" name="buyerDtls.addr1" id="buyerDtls_addr1"/>
				   <input type="hidden" name="buyerDtls.addr2" id="buyerDtls_addr2"/>
				   <input type="hidden" name="buyerDtls.loc" id="buyerDtls_loc"/>
				    <input type="hidden" name="buyerDtls.pin" id="buyerDtls_pin"/>
				   <input type="hidden" name="buyerDtls.state" id="buyerDtls_state"/>
				   <input type="hidden" name="buyerDtls.pos" id="buyerDtls_pos"/>
				 
				 <input type="hidden" name="dispatcherDtls.nm" id="dispatcherDtls_nm"/>
				   <input type="hidden" name="dispatcherDtls.addr1" id="dispatcherDtls_addr1"/>
				    <input type="hidden" name="dispatcherDtls.addr2" id="dispatcherDtls_addr2"/>
				   <input type="hidden" name="dispatcherDtls.loc" id="dispatcherDtls_loc"/>
				    <input type="hidden" name="dispatcherDtls.pin" id="dispatcherDtls_pin"/>
				   <input type="hidden" name="dispatcherDtls.stcd" id="dispatcherDtls_stcd"/>
				   
				   
				    <input type="hidden" name="shipmentDtls.gstin" id="shipmentDtls_gstin"/>
				   <input type="hidden" name="shipmentDtls.trdNm" id="shipmentDtls_trdNm"/>
				    <input type="hidden" name="shipmentDtls.lglNm" id="shipmentDtls_lglNm"/>
				   <input type="hidden" name="shipmentDtls.addr1" id="shipmentDtls_addr1"/>
				    <input type="hidden" name="shipmentDtls.addr2" id="shipmentDtls_addr2"/>
				   <input type="hidden" name="shipmentDtls.loc" id="shipmentDtls_loc"/>
				   <input type="hidden" name="shipmentDtls.pin" id="shipmentDtls_pin"/>
				    <input type="hidden" name="shipmentDtls.stcd" id="shipmentDtls_stcd"/>
					
				 </form:form>
			
			</div>
		</div>
		</div>
</div>   
		    
		   <script>
		   var einvCustomFieldsData=null;var epaymentTerms=null;var clientDetails=null;var eotherconfigdetails= null;
		   var einvRowCount=1,etermsRowCount=1;
		   var einterStateFlag=true; var editFlag=false;
		   var clientgstno = '<c:out value="${client.gstnnumber}"/>';var clientStatename = '<c:out value="${client.statename}"/>';var enableEinvDiscount = '${client.allowEinvDiscount}';var enableEinvCess = '${client.allowEinvoiceCess}';
		   $.ajax({
				url: "${contextPath}/getCustomFields/${client.id}",
				async: false,
				cache: false,
				success : function(response) {einvCustomFieldsData = response;},
				error : function(e) {}
			});
		   $.ajax({
				url: "${contextPath}/otherconfiglist/${client.id}",
				async: false,
				cache: false,
				success : function(response) {eotherconfigdetails = response;},
				error : function(e) {}
			});
		   $(function(){
			   $('#eduedate_div').datetimepicker({
			        timepicker: false,
			        format: 'd/m/Y',
			        maxDate: false,
					scrollMonth: true
			    });
			   /*$.ajax({
					url: "${contextPath}/getTerms/${client.id}",
					async: false,
					cache: false,
					success : function(response) {epaymentTerms = response;},
					error : function(e) {}
				});
				showTermsEList("invpopup");*/
				$.ajax({
			url: '${contextPath}/currencycodes',
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(bankDetails) {
				$("#addcurrencyCode").append($("<option></option>").attr("value","").text("-- Select Currency --")); 
				for (var i=0; i<bankDetails.length; i++) {
					$("#addcurrencyCode").append($("<option></option>").attr("value",bankDetails[i].code).text(bankDetails[i].code));
				}
				
			}
		});
				$("#ebilledtogstin").keyup(function() {
					var regex = /^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Zz]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$/;			
					if($("#ebilledtogstin").val().trim().length>14) {
						if(!regex.test($("#ebilledtogstin").val())){$("#einvokegstnPublicAPI1").addClass("disable");
						}else{$("#einvokegstnPublicAPI1").removeClass("disable");}
					}else {$("#einvokegstnPublicAPI1").addClass("disable");}
					if(clientgstno == $("#ebilledtogstin").val()){$('.clientgstno').text("clientgstno should not allowed");}
					else{$('.clientgstno').text('')}
				});
				$("#invoiceStatecode").on('paste', function(e) {
					var pastedData = e.originalEvent.clipboardData.getData('text');
					pupdateEinvRateType('<c:out value="${client.statename}"/>','GSTR1',pastedData);
				});
			});
			$('#eabb1').hover(function() {$("#etax_rate_drop").css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"10%","margin-top":"5px"});
			  }, function() {$("#etax_rate_drop").css("display","none");}
		);
		   var bstateoptions = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					list: {
						onClickEvent: function() {
							var rettype = $('#retType').val();updateEinvRateType('<c:out value="${client.statename}"/>','GSTR1','notedit');
						},
						onLoadEvent: function() {
							if($("#eac-container-invoiceStatecode ul").children().length == 0) {
								$("#invoiceStatecodeempty").show();
							}else {
								$("#invoiceStatecodeempty").hide();
							}
						},
						maxNumberOfElements: 37
					}
				};
		   var bstateoptions1 = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					list: {
						onClickEvent: function() {
							var rettype = $('#retType').val();updateEinvRateType('<c:out value="${client.statename}"/>','GSTR1','notedit');
						},
						onLoadEvent: function() {
							if($("#eac-container-estate ul").children().length == 0) {
								$("#estateeempty").show();
							}else {
								$("#estateempty").hide();
							}
						},
						maxNumberOfElements: 37
					}
				};
		   $("#estate").easyAutocomplete(bstateoptions1);
		   $("#invoiceStatecode").easyAutocomplete(bstateoptions);
			var custoptions = {
					url: function(phrase) {
						var rettype = $('#retType').val();
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
							return "${contextPath}/srchcustomer?query="+ phrase + "&clientid=${client.id}&format=json";
					},
					getValue: "customerIdAndName",
					list: {
						onChooseEvent: function() {
							$('#ebillingAddress_id').text('');var custData = $("#invoiceTradeName").getSelectedItemData();
							var invtype = $('#idEInvType').val();
							var rettype = $('#retType').val();
							if(invtype != "Exports"){
								$('#ebilledtogstin').val(custData.gstnnumber).trigger("change");
							}
							$('#invoiceTradeName').val(custData.name);
							if(invtype != "Exports"){
								$('#buyerDtls_gstin').val(custData.gstnnumber);
								$('#buyerDtls_loc').val(custData.city);
								if(custData.pincode != "" && custData.pincode != null){
									$('#buyerDtls_pin').val(parseInt(custData.pincode));	
								}else{
									$('#buyerDtls_pin').val(parseInt("0"));	
								}
								$('#buyerDtls_state').val(custData.state);
								var pos = $('#invoiceStatecode').val().split("-");
								$('#buyerDtls_pos').val(pos[0]);
								var gstinlgnm = "";
								if(custData.gstnnumber != null && custData.gstnnumber != ""){
									gstinlgnm = gstinlgnm+""+custData.gstnnumber+",";
								}
								if(custData.name != null && custData.name != ""){
									gstinlgnm = gstinlgnm+""+custData.name+",";
								}
								var addr ="";
								if(custData.address != null && custData.address != ""){
									addr = gstinlgnm+""+custData.address.substring(1,15)+"...";
								}
								if(custData.city != null && custData.city != ""){
									addr = addr+""+custData.city+",";
								}
								if(custData.state != null && custData.state != ""){
									addr = addr+""+custData.state+",";
								}
								if(custData.pincode != null && custData.pincode != ""){
									addr = addr+""+custData.pincode+",";
								}
								
								if($('#samedispatchaddress').is(":checked")){
									$('#einvbuyerAddr,#einvshipmentAddr').html(addr);
								}else{
									$('#einvbuyerAddr').html(addr);
								}
							}else{
								$('#buyerDtls_gstin').val("URP");
								$('#buyerDtls_loc').val("Other Teritory");
								$('#buyerDtls_pin').val(parseInt("999999"));
								$('#buyerDtls_state').val("96");
								$('#buyerDtls_pos').val("96");
								
								var gstinlgnm = "";
								if(custData.name != null && custData.name != ""){
									gstinlgnm = gstinlgnm+""+custData.name+",";
								}
								var addr ="";
								if(custData.address != null && custData.address != ""){
									addr = gstinlgnm+""+custData.address.substring(1,15)+"...";
								}
								if(custData.city != null && custData.city != ""){
									addr = addr+""+custData.city+",";
								}
								if($('#samedispatchaddress').is(":checked")){
									$('#einvbuyerAddr,#einvshipmentAddr').html("URP,"+addr+",96,999999");
								}else{
									$('#einvbuyerAddr').html("URP,"+addr+",96,999999");
								}
							}
							$('#buyerDtls_lglNm').val(custData.name);
							$('#buyerDtls_addr1').val(custData.address);
							//$('#buyerDtls_addr2').val(custData.address);
							
							$('.buyerAddrText').html('Edit');
							if($('#samedispatchaddress').is(":checked")){
								$('#shipmentDtls_gstin').val($('#buyerDtls_gstin').val());
							   	$('#shipmentDtls_lglNm').val($('#buyerDtls_lglNm').val());
							   	$('#shipmentDtls_trdNm').val($('#buyerDtls_lglNm').val());
								$('#shipmentDtls_addr1').val($('#buyerDtls_addr1').val());
								$('#shipmentDtls_addr2').val($('#buyerDtls_addr2').val());
								$('#shipmentDtls_loc').val($('#buyerDtls_loc').val());
								$('#shipmentDtls_pin').val($('#buyerDtls_pin').val());	
								$('#shipmentDtls_stcd').val($('#buyerDtls_state').val());
							}else{
								$('#shipmentDtls_gstin').val('');
							   	$('#shipmentDtls_lglNm').val('');
							   	$('#shipmentDtls_trdNm').val('');
								$('#shipmentDtls_addr1').val('');
								$('#shipmentDtls_addr2').val('');
								$('#shipmentDtls_loc').val('');
								$('#shipmentDtls_pin').val('');	
								$('#shipmentDtls_stcd').val('');
							}
							//$('#billingAddress').val(custData.address+","+custData.pincode);
							var invtype = $('#idEInvType').val();
							var rettype = $('#retType').val();
							if(invtype != "Exports"){
							$('#ebilledtogstin').val(custData.gstnnumber).trigger("change");
							}
							if(custData.dealerType != "" && custData.dealerType != null){
								dealertype = custData.dealerType;
								$('#dealerType').val(custData.dealerType);
							}
						
							if(custData.isCustomerTermsDetails == true || custData.isCustomerTermsDetails == "true"){
								$('#ebankNotes').val(custData.customerterms);
								$('#einvnotes').html(custData.customerterms);
								}else{
									var terms_cond = $("#einvnotess").text();
									$('#ebankNotes').val(terms_cond);
									$('#einvnotes').html(terms_cond);
								}
						
								if(custData.state != ''){$('.eplaceofsupply').removeClass("has-error has-danger");$('.eplaceofsupply .with-errors').html('');$("#invoiceStatecodeempty").hide();
					     		}
								if(invtype != "Exports"){
									$('#billedtostatecode').val(custData.state);
								}
								$('#invoiceCustomerId').val(custData.customerId);
								$('#vendorName').val(custData.customerLedgerName);
							
							updateEinvRateType('<c:out value="${client.statename}"/>', rettype,'notedit');
						},
						onLoadEvent: function() {
							if($("#eac-container-billedtoname ul").children().length == 0) {$("#eaddbilledtoname").show();} else {$("#eaddbilledtoname").hide();}
						},
						maxNumberOfElements: 30
					}
				};
				$("#invoiceTradeName").easyAutocomplete(custoptions);	
				$("#invoiceTradeName").parent().parent().mouseleave(function() {$("#eaddbilledtoname").hide();});
			function ebankAndVertical(){
				var notess =  $("#einvnotes").text();var terms_cond = $("#einvterms").text();var branchname;var verticalname;
				$('#ebranch,#evertical,#esnilbranch,#esnilvertical').children('option').remove();
					$("#ebranch,#esnilbranch").append($("<option></option>").attr("value","").text("-- Select Branch --")); 
					$("#evertical,#esnilvertical").append($("<option></option>").attr("value","").text("-- Select Vertical --"));
					<c:choose>
						<c:when test="${not empty companyUser}">
							<c:if test='${not empty companyUser.branch}'>
								<c:forEach items = "${companyUser.branch}" var="branch">
									<c:forEach items="${client.branches}" var="branchs">
										<c:if test='${branch eq branchs.name}'>
											$("#ebranch,#esnilbranch").append($("<option></option>").attr("value","${branch}").text("${branch}"));
											branchname = "${branch}";
										</c:if>
									</c:forEach>		
								</c:forEach>
							</c:if>
							<c:if test='${not empty companyUser.vertical}'>
								<c:forEach items = "${companyUser.vertical}" var="vertical">
									<c:forEach items="${client.verticals}" var="verticals">
										<c:if test='${vertical eq verticals.name}'>
											$("#evertical,#esnilvertical").append($("<option></option>").attr("value","${vertical}").text("${vertical}"));
											verticalname = "${vertical}";
										</c:if>
									</c:forEach>	
								</c:forEach>
							</c:if>
						</c:when>
						<c:otherwise>
							<c:forEach items="${client.branches}" var="branch">
								$("#ebranch,#esnilbranch").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
							</c:forEach>
							<c:forEach items="${client.verticals}" var="vertical">
								$("#evertical,#esnilvertical").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
							</c:forEach>
						</c:otherwise>
					</c:choose>
					var length = $('#ebranch').children('option').length;var user = "<c:out value='${companyUser}'/>";
					var snilbranchlength = $('#esnilbranch').children('option').length;
					if(length == 1){$('#ebranch').attr('disabled','disabled');$('#ebranch').attr('title','Please configure at least one Branch to see here');
					}else if(length == 2){if(user == '' || user == null){$('#ebranch').attr('readonly',false);}else{$('#ebranch').attr('readonly',true);$('#ebranch').val(branchname);$('#ebranch').css("pointer-events","none");}}
					else{$('#ebranch').removeAttr('disabled');$('#ebranch').removeAttr('readonly');$('#ebranch').removeAttr('title');if(user == '' || user == null){$('#ebranch').removeAttr('required');}else{$('#ebranch').attr('required','true');}}
					if(snilbranchlength == 1){$('#esnilbranch').attr('disabled','disabled');$('#esnilbranch').attr('title','Please configure at least one Branch to see here');}else{$('#esnilbranch').removeAttr('disabled');$('#esnilbranch').removeAttr('title');}
					var verticalLength = $('#evertical').children('option').length;var snilverticalLength = $('#esnilvertical').children('option').length;
					if(verticalLength == 1){$('#evertical').attr('disabled','disabled');$('#evertical').attr('title','Please configure at least one Vertical to see here');
					}else if(verticalLength == 2){if(user == '' || user == null){$('#evertical').attr('readonly',false);}else{$('#evertical').val(verticalname);$('#evertical').attr('readonly',true);$('#evertical').css("pointer-events","none");}}
					else{$('#evertical').removeAttr('disabled');$('#evertical').removeAttr('title');if(user == '' || user == null){$('#evertical').removeAttr('required');}else{$('#evertical').attr('required','true');}}
					if(snilverticalLength == 1){$('#esnilvertical').attr('disabled','disabled');$('#esnilvertical').attr('title','Please configure at least one Vertical to see here');}else{$('#esnilvertical').removeAttr('disabled');$('#esnilvertical').removeAttr('title');}
			}
function showEinvoicePopup(invType,retType,addFlag,invoiceCustomData){
	$('#einvoiceModal').modal("show");
	getEinvCustomFieldsData(einvCustomFieldsData,retType,addFlag,invoiceCustomData);
	if(addFlag){
		if(enableEinvCess == true || enableEinvCess == "true"){
			$('#einvoiceLevelCess').prop("checked",true).val("Yes");
			$('#einvLevelCess').val("Yes");
	   	}else{
	    	$('#einvoiceLevelCess').prop("checked",false).val("No");
			$('#einvLevelCess').val("No");
	    }
		$('#cancelIRN_btn').css("display","none");
		 if(eotherconfigdetails.enableCessQty == true){
	        	$('#ecess_qty').attr("checked", true);
	        	$('#ecess_taxable').removeAttr("checked");
	        	if ($('#ecessrate_text1').val() == '') {
		            document.getElementById('ecessrate_text1').value = "0";
		            document.getElementById('ecessamount_text1').value = "0.00";
		        }
	        }else{
	        	$('#ecess_taxable').attr("checked", true);
	        	$('#ecess_qty').removeAttr("checked");
	        	if ($('#ecessrate_text1').val() == '') {
		            document.getElementById('ecessrate_text1').value = "0%";
		            document.getElementById('ecessamount_text1').value = "0.00";
		        }
	        }
	}
	var einvoiceLevel_Cess  = $('#einvLevelCess').val();
	if(invType == 'B2C'){
		$('#generateIRNBtn').css("display","none");
		$('#invCategory,#ebilledtogstin').removeAttr("required");
		$('.einvcat').removeClass("astrich");
		$('#einvDraft_Btn').val('Save');
	}else{
		$('.einvcat').addClass("astrich");
		$('#invCategory').attr("required","required");
		if(addFlag){
			$('#einvDraft_Btn').val("Save As Draft");
		}
		if($('#irnNo').val() == ""){
			if(gnIRN){
				$('#generateIRNBtn').css("display","inline-block");
			}
		}
	}
	if(addFlag){
		var notess = $("#invnotes").text();
		notess = notess.replaceAll("#mgst# ", "\r\n");
		$('#einvoceform').find("textarea[id='ebankNotes']").val(notess);
		
	}
	$('.addITCRuleFlag,.addITCFlag').hide();
	if(invType == "Exports"){
		$('.addExportFlag,.tcuramt').show();
		$('.tcuramt').hide();
		$('.einvoicefooter').attr("colspan","10");
	}else{
		$('.addExportFlag').hide();
		$('.tcuramt').show();
		$('.einvoicefooter').attr("colspan","9");
	}
	$('#generateIRNBtn').val("Generate IRN & Save");
		$('#einvokegstnPublicAPI1').val("Get GSTIN Details");
		
		var invcat = "";var invbranch = "";
		if(!addFlag){
			invcat = $('#invCategory').val();
			invbranch = $('#ebranch').val();
		}else{
			$('#samedispatchaddress').prop("checked",false);
			$('#shimentAddrEdit').removeClass('d-none');
		}
		$('#invCategory').children('option').remove();
		$('#invCategory').append($("<option></option>").attr("value","").text("-- Select --"));
		$('#invCategory').append($("<option></option>").attr("value", "EXPWP").text("Export with Payment"));
		$('#invCategory').append($("<option></option>").attr("value", "EXPWOP").text("Export without payment"));
		$('#invCategory').append($("<option></option>").attr("value", "DEXP").text("Deemed Export"));
		$('#invCategory').append($("<option></option>").attr("value", "B2B").text("Business to Business"));
		$('#invCategory').append($("<option></option>").attr("value", "SEZWP").text("SEZ with payment"));
		$('#invCategory').append($("<option></option>").attr("value", "SEZWOP").text("SEZ without payment"));
		$('#invCategory').val(invcat);
	eINVintialIntialization();
	ebankAndVertical();
	$('#ebranch').val(invbranch);
	
	var tableng = $('#all_einvoice tr').length;
	if(tableng == '2'){
		$('.eitem_delete').hide();
	}else{
		$('.eitem_delete').show();
	}
	$('#dispatchTradeName,#dispatchAddress1,#dispatchloc,#dispatchPincode,#dispatchStatecode').removeAttr("required");
	$('#shipmentName,#shipmentAddress1,#shipmentloc,#shipmentPincode,#shipmentStatecode').removeAttr("required");
	
	$('#einvoicegstin').css("display","block");
	$('#ebilledtogstin').attr("required","required");
	$('.einvstatecode').removeAttr("readonly");
	if(invType == "Credit/Debit Notes" || invType == "Credit/Debit Note for Unregistered Taxpayers"){
		$('#idEInvType').val('Credit/Debit Notes');
		$('#einvoice_table').removeClass('mt-4');
		$('#einvTypeTxt').html('ADD CREDIT/DEBIT INVOICE');
		$('.einvoice_NumberText').html("Document Number");
		$('.einvoice_DateText').html("Document Date");
		$('#invCategory,#exportPmnt,#serialnoofinvoice,#einvCountryCode,#einvExpCategory').removeAttr("required");
		var invcategory = $("#invCategory").val();
		if(invType == "Credit/Debit Note for Unregistered Taxpayers" && (invcategory == 'EXPWP' || invcategory == 'EXPWOP')){
			$('#ebilledtogstin').removeAttr("required");
			$('#egstin_lab').removeClass("astrich");
		}else{
			$('#ebilledtogstin').attr("required","required");
			$('#egstin_lab').addClass("astrich");
		}
	}else if(invType == 'Exports'){
		$('#idEInvType').val('Exports');
		//$('#einvoice_table').addClass('mt-4');
		$('#einvTypeTxt').html('ADD EXPORT INVOICE');
		$('#einvDocType').removeAttr("required");
		$("#invCategory option[value='DEXP']").remove();
		$("#invCategory option[value='B2B']").remove();
		$("#invCategory option[value='SEZWP']").remove();
		$("#invCategory option[value='SEZWOP']").remove();
		$('#einvoicegstin').css("display","none");
		$('#ebilledtogstin').removeAttr("required");
		$('.einvstatecode').val("97-Other Teritory").attr("readonly","readonly");
	}else if(invType == 'B2C'){
		$('#idEInvType').val('B2C');
		$('#einvTypeTxt').html('ADD B2C INVOICE');
		$('#einvoice_table').removeClass('mt-4');
		$('#exportPmnt,#einvCountryCode,#serialnoofinvoice,#einvDocType,#einvExpCategory').removeAttr("required");
		$("#invCategory option[value='EXPWP']").remove();
		$("#invCategory option[value='EXPWOP']").remove();
		$('#einvoicegstin').css("display","none");
		$('#ebilledtogstin').removeAttr("required");
	}else{
		$('#idEInvType').val('B2B');
		$('#einvoice_table').removeClass('mt-4');
		$('#exportPmnt,#einvCountryCode,#serialnoofinvoice,#einvDocType,#einvExpCategory').removeAttr("required");
		$("#invCategory option[value='EXPWP']").remove();
		$("#invCategory option[value='EXPWOP']").remove();
	}
	if(addFlag){
		$('.eaddBankDetails').removeAttr("checked");$('#ebank_details').css("display","none");
		updateBankDefaults(invType,retType);
		$('input[type="hidden"]').each(function() {
			var name = $(this).attr('name');
			if(name == 'id'){
				$('#einvoiceId').attr('name','einv_id').val('');
			}
		});
		var invoicedate=new Date().getDate()+"/"+month+"/"+year;
	$('#einvoicedate').datetimepicker({value: invoicedate,timepicker : false,format: 'd/m/Y', maxDate: '0', minDate: false,onSelectDate: function(date){
        var selectedDate = new Date(date);var msecsInADay = 86400000;var endDate = new Date(selectedDate.getTime());
    }});
	var res1 = invoicedate.split("/");
	var mdte1 = res1[1]+"/"+res1[0]+"/"+res1[2];
	$('#eduedate_div').datetimepicker({
		timepicker: false,
		format: 'd/m/Y',
		maxDate: false,
		minDate: new Date(mdte1)
	});
	}else{
		var bacctno, bname;	bacctno = $('#ebankAcctNo').val();bname = $('#ebankName').val();
		eupdateBankDetails(type,bacctno,bname);
		updateEinvRateType('<c:out value="${client.statename}"/>', 'GSTR1','edit');
		invdate = $('#einvoicedate').val();
			$('#einvoicedate').datetimepicker({
				value: invdate,
				maxDate: '0',
				format: 'd/m/Y',
				timepicker: false
			});
			var res1 = invdate.split("/");
			var mdte1 = res1[1]+"/"+res1[0]+"/"+res1[2];
			$('#eduedate_div').datetimepicker({
				timepicker: false,
				format: 'd/m/Y',
				maxDate: false,
				minDate: new Date(mdte1)
			});
	}
	$('.specific_field').each(function() {
		if($(this).attr('class').indexOf(invType) >= 0) {
			$(this).show();
		} else {
			$(this).hide();
		}
	});
	if(enableEinvDiscount == true || enableEinvDiscount == "true"){
		$('.discountFlag').show();
		if(invType == 'Exports'){
			$('.einvoicefooter').attr("colspan","10");
		}else{
			$('.einvoicefooter').attr("colspan","9");
		}
	}else{
		$('.discountFlag').hide();
		if(invType == 'Exports'){
			$('.einvoicefooter').attr("colspan","9");
		}else{
			$('.einvoicefooter').attr("colspan","8");
		}
	}
	if(enableEinvCess == true || enableEinvCess == "true"){
		if(einvoiceLevel_Cess == "" || einvoiceLevel_Cess == 'No' || einvoiceLevel_Cess == null || einvoiceLevel_Cess == undefined){
 			$('.ecessFlag').hide();
 	 		$('#ecessrate_text1').removeAttr("required");
 	 		$('#einvoiceLevelCess').val('No');
 	  }else{
 		 if(invType == 'Exports'){
 			$('.einvoicefooter').attr("colspan","9");
 		}
 		$('.ecessFlag').show();
 		$('#einvoiceLevelCess').prop("checked",true).val("Yes");
 	  }
		
		
	}else{
		if(einvoiceLevel_Cess == "" || einvoiceLevel_Cess == 'No' || einvoiceLevel_Cess == null || einvoiceLevel_Cess == undefined){
			$('.ecessFlag').hide();
		}
		$('#ecessrate_text1').removeAttr("required");
	}
	if(((enableEinvDiscount == true || enableEinvDiscount == "true") && (enableEinvCess == true || enableEinvCess == "true"))){
		if(invType == 'Exports'){
			$('.einvoicefooter').attr("colspan","10");
		}
	}
}
function echeckInvoiceNumber(edit){
	var val = $('#etermDays').val();
		var invdt = $('#einvoicedate').val();
		var mnyr = invdt.split("/");var dt = parseInt(mnyr[0]);var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
		var d = new Date(yr, mn-parseInt(1), dt);
		if(val == ''){
			$('#eduedate_div').val("");
			$('#eduedate_div').datetimepicker({
				timepicker: false,
				format: 'd/m/Y',
				maxDate: false,
				minDate: new Date(yr, mn-parseInt(1), dt)
			});
		}else{
			d.setDate(d.getDate() + parseInt(val));
			var day = d.getDate();
			var month = d.getMonth()+parseInt(1);
			var year=d.getFullYear();
			var mndate = ('0' + day).slice(-2) + '/' + ('0' + (month)).slice(-2) + '/' + year;
			$('#eduedate_div').datetimepicker({
				value: mndate,
				timepicker: false,
				format: 'd/m/Y',
				maxDate: false,
				minDate: new Date(yr, mn-parseInt(1), dt)
			});
		}
}
function epaymentterm(){
		var val = $('#etermDays').val();
		var invdt = $('#einvoicedate').val();
		var mnyr = invdt.split("/");var dt = parseInt(mnyr[0]);var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
		var d = new Date(yr, mn-parseInt(1), dt);
		if(val == ''){
			$('#eduedate_div').val("");
		}else{
			d.setDate(d.getDate() + parseInt(val));
			var day = d.getDate();
			var month = d.getMonth()+parseInt(1);
			var year=d.getFullYear();
			var mndate = ('0' + day).slice(-2) + '/' + ('0' + (month)).slice(-2) + '/' + year;
			$('#eduedate_div').datetimepicker({
				value: mndate,
				timepicker: false,
				format: 'd/m/Y',
				maxDate: false,
				minDate: new Date(yr, mn-parseInt(1), dt)
			});
		}			
	}
	function eduedatechange(){
		var invdate = $('#einvoicedate').val();var billdate = $('#eduedate_div').val();
		var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
		var dtFromArray = invdate.match(rxDatePattern);
		dtFromDay = dtFromArray[1];
		dtFromMonth= dtFromArray[3];	
		dtFromYear = dtFromArray[5];
		var invdate1 = dtFromMonth+"/"+dtFromDay+"/"+dtFromYear;
		var dtToArray = billdate.match(rxDatePattern);
		dtToDay = dtToArray[1];
		dtToMonth= dtToArray[3];	
		dtToYear = dtToArray[5];
		var billdate1 = dtToMonth+"/"+dtToDay+"/"+dtToYear;
		
		var Difference_In_Time = new Date(billdate1) - new Date(invdate1);
 
		// To calculate the no. of days between two dates
		var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24);
		if(Difference_In_Days >= 0){
			$('#etermDays').val(Difference_In_Days);
		}else{
			$('#etermDays').val('0');
			$('#eduedate_div').val(dtFromDay+"/"+dtFromMonth+"/"+dtFromYear);
		}
		
	}
	//$('input[type=radio][class=cessType]').change(function() {
	function changeEinvCessType(){
		var cesstype = $('input[class="cessType"]:checked').val();
		$("#all_einvoice tr").each(function(index){
			var abc = index+1;
			var dStr = ($('#ecessrate_text'+abc).val()).toString();
			if(dStr.indexOf("%") > -1){
				dStr = dStr.replace("%","");
			}
			if(cesstype == "Taxable Value"){
				$('#ecessrate_text'+abc).val(dStr+"%");
			}else{
				$('#ecessrate_text'+abc).val(dStr);
			}
			findEinvCessAmount(index+1);
			index++;
		});
	}
		   </script> 
</body>
</html>