<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<c:set var="varRetType" value='<%= (String)request.getParameter("returntype") %>'/>
<c:set var="varGSTR1" value="<%=MasterGSTConstants.GSTR1%>"/>
<%-- <script src="${contextPath}/static/mastergst/js/client/paymentss.js" type="text/javascript"></script> --%>
<style>
#checkadvance .helper{border-radius:4px}
.recbalance.fa-rupee:before{font-size:20px!important}
#checkadvance .helper::before{top: 11px!important;left: 5px!important;box-shadow: none;height: 12px; width:2px}
#checkadvance .helper::after{top: 6px!important;left: 0px!important;height:6px}
#bankselect{height: 28px!important; border-radius: 0px; min-width: 138px;font-size: 14px;padding: 0px;}
#paymentsModal thead th{border: 1px solid #8793c7;}
</style>
<div class="modal fade" id="paymentsModal" role="dialog" aria-labelledby="paymentsModal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document">
            <div class="modal-content" style="width:800px;">
      <div class="modal-body" >
         <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="closePaymentModal()">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3 id="paymenthead">Payments</h3>
                    </div>
                    <ul class="nav nav-tabs" role="paymenttablist">
                        <li class="nav-item"><a class="nav-link addPaymentDetails active" id="addpayment" data-toggle="tab" href="#tab1" role="tab" onclick="paymentDashboard('addpayment','this','${client_id}')">Add Payment</a></li>
                        <li class="nav-item"><a class="nav-link paymenthistory" id="paymenthistory" data-toggle="tab" href="#tab2" role="tab"onclick="paymentDashboard('paymentHistory','this','${client_id}')">Payment History</a></li>
                     </ul> 
                     <div class="tab-content p-2">
                     <div class="tab-pane active" id="tab1">
                      <div class="form-group col-md-6 col-sm-12" style="float: right;">
                     		<input type="hidden" name="beforereceivedAmount" id="beforereceivedAmount" value=""/>
							<div>Invoice Number<span class="colon" style="margin-left: 7px;"> : </span><span class="pdata text-right" id="invno" style="font-weight:bold;font-family: 'latolight', sans-serif!important;float: right;"></span></div>
							<div><c:choose><c:when test="${returntype eq varGSTR1}">Net Receivable<span class="colon" style="margin-left: 14px;"> : </span></c:when><c:otherwise>Net Payable<span class="colon" style="margin-left: 32px;"> : </span></c:otherwise></c:choose><span class="pdata text-right" id="netreceive" style="font-weight:bold;font-family: 'latolight', sans-serif!important;float: right;"></span><i class="fa fa-rupee" style="float: right;margin-top: 2px;"></i></div>
							<div style="border-bottom: 1px solid black;"><c:choose><c:when test="${returntype eq varGSTR1}">Received <span class="colon" style="margin-left: 52px;"> : </span></c:when><c:otherwise>Paid<span class="colon" style="margin-left: 82px;"> : </span></c:otherwise></c:choose><span class="pdata text-right" id="received" style="font-weight:bold;font-family: 'latolight', sans-serif!important;float: right;color:darkgreen;"></span><i class="fa fa-rupee" style="float: right;margin-top: 2px;"></i></div>
							<div>Pending Amount <span class="colon"> : </span><span class="pdata text-right" id="pendingamt" style="font-weight:bold;font-family: 'latolight', sans-serif!important;font-size: 24px;float: right;color:red;"></span><i class="fa fa-rupee recbalance" style="float: right;margin-top: 9px;"></i></div>
							</div>
                  <form:form method="post" data-toggle="validator" class="" name="paymentform" id="paymentform" action="${contextPath}/record_payments/${id}/${client_id}/${paymentreturnType}/${fullname}/${usertype}/${month}/${year}?type="  modelAttribute="payments">
			                <div class="meterialform">
			                 <div class="form-group col-md-4 col-sm-12 ">
										<p class="lable-txt astrich">Voucher Number</p>
										<span class="errormsg" id="voucherNumber_Msg"></span>
										<input type="text" class="voucherNumbers" id="voucherNumbers" name="voucherNumber" required="required" data-error="Please enter voucher number"  placeholder="voucher number" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> </div>
							<div class="form-group col-md-4 col-sm-12 ">
										<p class="lable-txt astrich">Payment Date(DD-MM-YYYY)</p>
										<span class="errormsg" id="paymentDate_Msg"></span>
										<input type="text" id="dateofpayment" name="paymentDate" aria-describedby="dateofpayment" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" placeholder="DD-MM-YYYY" required="required" data-error="Please enter valid date" class="form-control">
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> </div>
							</div>
							<span id="sortable_table3Info"></span>
							<table align="center" id="sortable_table3" border="0" class="row-border dataTable payment_table" style="margin-bottom: 3%;">
			              <thead><tr>
			                  <th>S.No</th><th style="width:20%">Mode Of Payment</th><th>Amount</th><th>Ref. Number</th><th>Ledger</th><th style="width:8%"></th>
			               </tr></thead>
			              <tbody id="paymentbody">
			                <tr id="1" class="">
			                  <td id="sno_row2" align="center">1</td><td id="" class="form-group" style="border: none;margin-bottom: 0px;"><select class="form-control mop_text1" onchange="modeOfPaymentChange(1)" name="paymentitems[0].modeOfPayment" id="mop" required="required" data-error="Please select payment type"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS - GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select></td><td id="" class="form-group"><input type="text" class="form-control amount_text1" id="amount1" name="paymentitems[0].amount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyUp="updateAmount(1)" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td id="" class="form-group"><input id="referenceNumber" name="paymentitems[0].referenceNumber" placeholder="Reference No." class="form-control ref_text1"/></td><td class="form-group"><input type="text" class="form-control ledger_text1" id="ledger_text1" name="paymentitems[0].ledger" required placeholder="Ledger" /></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_button1" class="item_delete" onclick="delete_payrow(1)"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td>
			                </tr>
			              </tbody>
			              <tfoot><th colspan="4" class="text-left"><i class="add-btn pymntaddrow"  onclick="addrow()" style="cursor: pointer;">+</i><span style="color:black">Add Another Row</span></th><th colspan="3" style="text-align: right;"><i class="add-btn pymntaddrow"  onclick="addrow()" style="cursor: pointer;">+</i></th></tfoot>
			            </table>
			            <!-- <div class="row" id="pmnt_bank">
			            	  <div class="col-md-3 col-sm-12 bank_details"><h6>&nbsp;</h6>
			            	   <label class="pl-1 pt-2"> Amount Deposited to :</label>
			                </div>
			               <div class="col-md-3 col-sm-12 form-group pl-0" id="bankdiv">
			                  <label for="inlineFormInput" class="bold-txt" style="font-size: 13px;">Select Bank</label>
			                  <select class="form-control mb-2 mr-sm-2 mb-sm-0 bankselect" name = "bankname" id="bankselect"></select>
			                </div> 
			            <div class="col-md-6 col-sm-12 pr-3 pull-right" id="details_bnk"> 
			           	<label for="inlineFormInput" class="mb-0 col-md-4 pl-0" style="font-size:11px">Account Number <span style="margin-left: 15px;">:</span></label>
						  <input id="bank_AcctNo" name="accountnumber" class="bold-txt col-md-4 p-0" style="font-size:11px;border:none;">
			              
						  
			              <label for="inlineFormInput" class="mb-0 col-md-4 pl-0" style="font-size:11px">Account Name <span style="margin-left: 25px;">:</span></label>
						  <input id="bank_AccountName" name="accountName" class="bold-txt col-md-4 p-0" style="font-size:11px;border:none;">		  
			              <label for="inlineFormInput" class="mb-0 col-md-4 pl-0" style="font-size:11px">Branch Name <span style="margin-left: 30px;">:</span></label>
						  <input id="bank_Branch" name="branchname" class="bold-txt col-md-4 p-0" style="font-size:11px;border:none;">
			              
						  
			              <label for="inlineFormInput" class="mb-0 col-md-4 pl-0" style="font-size:11px">IFSC Code <span style="margin-left: 46px;">:</span></label>
						  <input id="bank_IFSC" name="ifsccode" class="bold-txt p-0 col-md-4"  style="font-size:11px;border:none;">
			             	</div>
			            
			            </div> -->
			           
			            
			            <div class="row" id="pmnt_adv">
			            	  <div class="col-md-3 col-sm-12"><div class="form-check"><div class="meterialform"><div class="checkbox pl-1" id="checkadvance" style="margin-top:-2px;">
			                        <label><input class="adv_adjusted_pmnt" type="checkbox" name="isadvadjust" value="false"><i class="helper"></i> Advance Adjusted</label>
			                </div></div></div></div>
			               
			            </div>
			           <table align="center" id="sortable_table4" border="0" class="row-border dataTable payment_table" style="display:none;">
			               <thead>
			                <tr>
			                  <th rowspan="2" style="">Adv. Rept. No</th><th rowspan="2">Adv. to be Adjusted</th><th rowspan="2" style="width:8%;">Tax Rate</th><th rowspan="2" width="8%">Tax (GST)</th><th colspan="2" width="8%">Cess</th><th rowspan="2">Adv. Remaining</th></tr>
			                 <tr><th width="7%">Rate</th><th width="5%">Amount</th></tr>
			              </thead>
			              <tbody id="payment_adv_adjusted_body">
			              <tr>
			              <td><input type="text" class="form-control" id="advpmntno" name="advpmntrecno"  onkeypress="return ((event.charCode >= 65 &amp;&amp; event.charCode <= 90) || (event.charCode >= 97 &amp;&amp; event.charCode <= 122) || (event.charCode >= 48 &amp;&amp; event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" maxlength="16" pattern="^[0-9a-zA-Z/ -]+$"></td>  <td><input type="text" class="form-control" id="advpmnt_adjusted" name="advpmntadjust" onKeyUp="checkadvpmnt()" onblur="checkadvpmnt()" onkeypress="return (event.charCode >= 48 &amp;&amp; event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$"><div id='advpmnt_amt1' class='advadjamnt' style='display:none; color:red'>This Amount is Greater than available Amount</div></td> <td><select class="form-control" id="advpmnttaxrate" onchange="findpmnttax()" name="advpmnttaxrate"><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td> <td><input type="text" class="form-control" id="advpmnttax" readonly><div id='pmnttax_rate_drop' style='display:none'><div id='pmnt_icon-drop'></div><i style="font-size:12px;display:none" class="fa">&#xf00d;</i><h6 style='text-align: center;' class='mb-2'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST <span style='margin-left:5px'>:<span></p><span><input type='text' class='form-control dropdown' id='pmntigsttax' name='advpmntigstamt' style='border:none;width: 30px;padding-top: 2px;background: none;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='pmntcgsttax' name='advpmntcgstamt' style='border:none;width:50px;padding-top: 2px;background: none;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='pmntsgsttax' name='advpmntsgstamt' style='border:none;width:50px;padding-top: 2px;background: none;'></span></div></div></td> <td><input type="text" class="form-control" id="advpmntcessrate" onKeyUp="findadvpmntcess()" name="advpmncessrate" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return (event.charCode >= 48 &amp;&amp; event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)"></td><td><input type="text" class="form-control" id="advpmntcessamnt" name="advpmntcessamt" readonly></td> <td><input type="text" class="form-control" id="advpmntremaining" name="advpmntremainamt" readonly></td>
			               </tr>
			              </tbody>
			            </table>
			             <div class="col-md-12 col-sm-12 mb-3 pr-0" id="adv_pmnt_div" style="display:none;"><div class="row">
			              <div class="col-md-6"><label for="PmntAdjusamount" id="adv_adj_date" class="" style="font-size:13px;">Adv. Rec. Date<span style="margin-left: 7px;"> :</span> </label><label for="PmntAdjusamountVal" class="bold-txt pr-3 ml-2" id="advpmntdate" style="font-size:13px;color:green;"></label><input type="hidden" id="advpmntdate_val" name="advpmntrecdate"/></div>
			              <div class="col-md-6"><label for="PmntAdjusamount" id="adv_adj_pos" class="AdjusamtLabel" style="font-size:13px;">POS<span style="margin-left: 17px;"> :</span></label><label for="PmntAdjusamountVal" class="bold-txt AdjusamtVal pr-3 ml-2" id="advpmntpos" style="font-size:13px;color:green;"> </label><input type="hidden" id="advpmntpos_val" name="advpmntpos"/></div>
			             <div class="col-md-6"> <label for="PmntAdjusamount" id="adv_adj_amt" class=" AdjusamtLabel" style="font-size:13px;"> Adv. Rec. Amt <span style="margin-left: 10px;"> :</span></label><label for="PmntAdjusamountVal" class="bold-txt AdjusamtVal pr-3 ml-2" id="advpmntamnt" style="font-size:13px;color:green;">  </label><input type="hidden" id="advpmntamnt_val" name="advpmntrecamt"/></div>
			             <div class="col-md-6"> <label for="PmntAdjusamount" id="adv_adj_available" class=" AdjusamtLabel" style="font-size:13px;"> Adv. Avail for Adjustment <span style="margin-left: 10px;"> :</span></label><label for="PmntAdjusamountVal" class="bold-txt AdjusamtVal pr-3 ml-2" id="advpmntadjust" style="font-size:13px;color:green;">  </label><input type="hidden" id="advpmntadjust_val" name="advpmntavailadjust"/></div>
			              </div>
			              </div>
			   			  <input type="hidden" id="userid" name="userid" value='<c:out value="${id}"/>'/>
					      <input type="hidden" id="recordinvno" name="invoiceNumber"/>
					      <input type="hidden" id="custname_record" name="customerName"/>
					      <input type="hidden" id="gstin" name="gstNumber"/>
					      <input type="hidden" id="invoice_returntype" name="returntype"/>
					      <input type="hidden" id="pendingBalance" name="pendingBalance"/>
					      <input type="hidden" id="previousPendingBalance" name="previousPendingBalance"/>
					      <input type="hidden" id="received_amount" name="receivedAmount"/>
					      <input type="hidden" id="paymentStatus" name="paymentStatus"/>
					      <input type="hidden" id="invoice_id" name="invoiceid"/>
					      <input type="hidden" id="invtype" name="invtype"/>
						  <div class="" style="">
					      	<button type="submit" class="btn btn-blue-dark payla_submit" style="display:none">Add Payment</button>
					      </div>  
					     </div>	       
			 	  </form:form>
                  <div class="tab-pane" id="tab2" role="tabpane2">
                  <table id="historyTable" align="center" border="1" class="row-border historyTable" style="width: 750px;margin-top: 69px;">
							<thead>							
							<tr class="table-bg">
								<th colspan="3" class="text-left" style="background-color: #eceeef;border-bottom:1px solid black;"><span style="margin-right:15px;font-weight:100">Original Invoice Amount :</span><span class="" style="font-family: 'latolight', sans-serif!important;font-size: 24px;"><i class="fa fa-rupee recbalance" aria-hidden="true"></i><span class="invtotalamt"></span></span></th>								
								<th colspan="3" class="text-right" style="background-color: #eceeef;border-bottom:1px solid black;"><span style="margin-right:15px;font-weight:100">Opening Balance :</span><span class="" style="font-family: 'latolight', sans-serif!important;font-size: 24px;"><i class="fa fa-rupee recbalance" aria-hidden="true"></i><span class="openingbal"></span></span></th>
							</tr>						
							<tr style="font-weight:none;">
							<th class="text-center">Voucher.no</th>
							<th class="text-center">Date</th>
							<th class="text-center">Mode</th>
							<th class="text-center">Reference</th>
							<th class="text-center">Amount</th>
							<th class="text-center">Balance</th>
							</tr>
							</thead>
							<tbody></tbody>
							</table>
							<div class="modal-footer footer-fixed-bottom">
				
                  </div>
                  </div>
      </div>
    </div>
	
	<div class="modal-footer" style="">
	<label for="payla_submit" class="btn btn-blue-dark m-0 pay_sub" tabindex="0" id="paymentbtn">Save</label>
	<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close" onclick="closePaymentModal()">Cancel</a>
	</div>
	
  </div>
</div>
</div>
<script>
var clientBankDetails = new Array();var totalPaymentAmount = 0;var cashbankledgername = new Array();
$(document).ready(function(){
	$('.pay_sub').click(function(){
	$('#paymentform').submit();
});
$('#paymentform').submit(function(e){
	var err = 0;
	var flag=false;
	$('#paymentform').find('input').each(function(){
	    if(!$(this).prop('required')){
	    }else{
	    	
	    	var bca = $(this).val();
		 	   if( bca == ''){
		 		  err = 1;
		 		   $(this).parent().addClass('has-error has-danger');
		 	   }else{
		 		   $(this).parent().removeClass('has-error has-danger');
		 	   }
	    }
	});
	$('#paymentform').find('select').each(function(){
	    if(!$(this).prop('required')){
	    }else{
	    	if (this.value == '-Select-' || this.value == '') {
				err = 1;
			      $(this).parent().addClass('has-error has-danger');
			}else{
				flag = true;
				/* if(this.value == 'Cash' || this.value == 'Bank'){
					flag= true;
				} */
				$(this).parent().removeClass('has-error has-danger');
			}
	    }
	});
	if(!flag){
		err = 1;
		$('#sortable_table3Info').html('Mode of Payment Bank (or) Cash is mandatory').css('color','red');
	}
	if(parseFloat($('#pendingamt').text().replace(/,/g, "")) < 0){
		err = 1;
		flag = false;
		var invtyp = $('#invoice_returntype').val();
		if(invtyp == 'GSTR2'){
			$('#sortable_table3Info').html('Paid Amount is greater than Payable Amount').css('color','red');
		}else{
			$('#sortable_table3Info').html('Received Amount is greater than Receivable Amount').css('color','red');
		}
		
	}
	if (err != 0) {
		return false;
	  }
});
	var atinvoiceNumberoptions1 = {
			url: function(phrase) {
				var clientid = $('#clientid').val();
				var date = $('#dateofinvoice').val();var mnyr = date.split("/");var rettype = $('#retType').val();var invtype = $('#invtype').val(); invtype = invtype.replace('/', '_');var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/advReceiptInvoiceNumbers/${month}/${year}/"+rettype+"/"+invtype+"/?query="+ phrase + "&clientid="+clientid+"&format=json";
			},
			getValue: function(element) {
				var invnoAndDate = element.invoiceno;
				return invnoAndDate;
			},
			list: {
				onChooseEvent: function() {
					$("#advpmntdate").text('');
					$("#advpmntpos").text('');
					$("#advpmntamnt").text(0.00);
					$("#advpmntadjust").text(0.00);
					var rettype = $('#retType').val();
					var invoice = $("#advpmntno").getSelectedItemData();
					var invDate = new Date(invoice.dateofinvoice);var day = (invDate.getDate()) + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";day = checkZero(day);	month = checkZero(month);year = checkZero(year);var invoiceDate = day + "-" + month + "-" + year;
					$("#advpmntdate").text(invoiceDate);$("#advpmntdate_val").val(invoiceDate);
					$("#advpmntpos").text(invoice.statename);$("#advpmntpos_val").val(invoice.statename);
					$("#advpmntamnt").text(invoice.totalamount);$("#advpmntamnt_val").val(invoice.totalamount);
					var remainingAmount= 0.00;
					if(invoice.advRemainingAmount){
						remainingAmount = invoice.advRemainingAmount
					}else{
						remainingAmount = invoice.totalamount;
					}
					$("#advpmntadjust").text(remainingAmount);$("#advpmntadjust_val").val(remainingAmount);
					$("#advpmnt_adjusted").attr('max',remainingAmount);
					updateAdvRateType(clntStatename,invoice.statename, rettype);
				},
				onLoadEvent: function() {
					if($("#eac-container-advpmntno"+" ul").css({'width':'150px'}).children().length == 0) {
						$("#advpmntno_textempty").show();
						$(".advpaymentddbox1").css({'left':'50px','position':'absolute','background-color': '#fff','border': '1px solid #f5f5f5','padding': '10px',' z-index': '99','box-shadow': '0px 0px 5px 0px #e5e5e5','z-index':'1'});
						$(".advpaymentddbox1 p").css({'font-size':'12px','color':'#cc0000','margin':'0'});
						$('#advpmntno').addClass('has-error has-danger');
						$("#advpmntdate").text('');
						$("#advpmntpos").text('');
						$("#advpmntamnt").text(0.00);
						$("#advpmntadjust").text(0.00);
						$('#advpmntdate').removeAttr('readonly');$("#advpmntpos").removeAttr('readonly');
						$("#advpmntamnt").removeAttr('readonly');$("#advpmntadjust").removeAttr('readonly');
					} else {
						$("#advpmntno_textempty").hide();
						$('#advpmntno_textempty').removeClass('has-error has-danger');
						$('#advpmntdate').attr('readonly','readonly');$("#advpmntpos").attr('readonly','readonly');
						$("#advpmntamnt").attr('readonly','readonly');$("#advpmntadjust").attr('readonly','readonly');
					}
				},
				maxNumberOfElements: 43
			}
		};
	$("#advpmntno").easyAutocomplete(atinvoiceNumberoptions1);
	$("#advpmntno").parent().parent().mouseleave(function() {
		$("#advpmntno_textempty").hide();
		
	});
var ledgeroptions = {
		url: function(phrase) {
			phrase = phrase.replace('(',"\\(");
			phrase = phrase.replace(')',"\\)");
			return "${contextPath}/ledgerlist/${client.id}?query="+ phrase + "&format=json";
		},
		getValue: "ledgerName",
		list: {
			match: {
				enabled: true
			},
		onChooseEvent: function() {
			var groupdetails = $("#ledger_text1").getSelectedItemData();
		}, 
			onLoadEvent: function() {
				if($("#eac-container-ledger_text1 ul").children().length == 0) {
					$("#addlegername").show();
				} else {
					$("#addlegername").hide();
				}
			},
			maxNumberOfElements: 10
		}
	};
$('#ledger_text1').easyAutocomplete(ledgeroptions);
ledgerNames();
});
function checkZero(data){
	  if(data.length == 1){
		data = "0" + data;
	  }
	  return data;
	}

function delete_payrow(no){
	var amount = $('#amount'+no).val();
	if(amount != ""){
	var received_amount =  $('#received_amount').val();
	var pendingBalance =  $('#pendingBalance').val();
	var amnt = amount;
	var rcmnt = received_amount.replace(/,/g, "");
	var pendamnt = pendingBalance.replace(/,/g, "");
	var rcm = rcmnt - parseFloat(amnt).toFixed(2);
	var pend = pendamnt + amnt;
	
	 $('#pendingBalance').val(formatNumber(parseFloat(pend).toFixed(2)));
	 $('#pendingamt').text(formatNumber(parseFloat(pend).toFixed(2)));
	 $('#received').html(formatNumber(parseFloat(rcm).toFixed(2)));
	 $('.openingbal').html(formatNumber(parseFloat(pend).toFixed(2)));
	 $('#received_amount').val(formatNumber(parseFloat(rcm).toFixed(2)));

		var received_amount = $('#received').text().replace(/,/g, "");
		var pendingamt = $('#pendingamt').text().replace(/,/g, "");
		
		var ramt=parseFloat(received_amount);
		var pamt=parseFloat(pendingamt);
		
		 if(pamt == 0){
			 $('#paymentStatus').val("Paid");
		 }else if(pamt != 0){
			 $('#paymentStatus').val("Partially Paid");
		 }else {
			 $('#paymentStatus').val("UnPaid");
		 }
	}
	
	
	var table3=document.getElementById("sortable_table3");
	if(no >= rowCount){
		no=no-1;
	}
	table3.deleteRow(no+1);
	$("#paymentbody tr").each(function(index) {
		 $(this).attr('id',index+1);
		 $(this).find("#sno_row2").html(index+1);
		 var rowno = (index+1).toString();
		 var rownoo = (index).toString();
		 $(this).find('input , select').each (function() {
				var inputname1 = $(this).attr('class');
				var inputid1 = $(this).attr('id');
				var inputname = $(this).attr('name');
				var abcd = $(this).attr('onkeyup');
				var change = $(this).attr('onchange');
				if(change != undefined){
					change = replaceAt(change,20,rowno);
			   	    $(this).attr('onchange',change);
				}
				if(abcd != undefined){
					abcd = replaceAt(abcd,13,rowno);
			   	    $(this).attr('onkeyup',abcd);
				}
				if(inputname1 != undefined){
					if(rowno<10){
						inputname1 = inputname1.slice(0, -1);
		   	    		}else{
		   	    			inputname1 = inputname1.slice(0, -2);
		   	    		}
					inputname1 = inputname1+rowno;
					$(this).attr('class',inputname1);
				}
				if(inputid1 != undefined){
					if(rowno<10){
						inputid1 = inputid1.slice(0, -1);
		   	    		}else{
		   	    			inputid1 = inputid1.slice(0, -2);
		   	    		}
					inputid1 = inputid1+rowno;
					$(this).attr('id',inputid1);
				}
				if(inputname != undefined){
					if(inputname.indexOf("paymentitems[") >= 0) {
						if(rownoo == '9'){
							inputname = inputname.replace('10',' ');
						}
						inputname = replaceAt(inputname,13,rownoo);
						$(this).attr('name',inputname);
						
					}
				}
			});
		 $(this).find('a').each (function() {
				var aname = $(this).attr('id');
				if(aname != undefined){
					if(rowno<10){
						aname = aname.slice(0, -1);
		   	    		}else{
		   	    			aname = aname.slice(0, -2);
		   	    		}
				aname = aname+rowno;
				$(this).attr('id',aname);
				}
				var det = $(this).attr('class');
				if(det != 'item_delete'){
				}else{
					var abcd = $(this).attr('onclick');
			   	    abcd = replaceAt(abcd,14,rowno);
			   	    $(this).attr('onclick',abcd);
				}
			});
	});
	$('form[name="paymentform"]').validator('update');
}
function modeOfPaymentChange(no){
	var modeofpayment = $('.mop_text'+no).val();
	$('.ledger_text'+no).val(modeofpayment);
}
function updatePayment(returntype,invoicetype){
	$('#sortable_table3Info').html('');
	$('#addpayment').click();
	$(".bank_details_add").removeAttr("checked").prop("checked",false);
	$("#payment_bank_details").hide();
	$('#paymenthistory').css("display","block");
	$('.adv_adjusted_pmnt').prop("checked",false).val("false");
	$('#adv_pmnt_div,#sortable_table4').css("display","none");
	$('.mop_text1').children('option').remove();
	$(".mop_text1").append($("<option></option>").attr("value","").text("-- Select --"));
	for (var i=0; i<cashbankledgername.length; i++) {
		$(".mop_text1").append($("<option></option>").attr("value",cashbankledgername[i]).text(cashbankledgername[i]));
	}
	//$("#bankdiv").css("display","none");
	var invDefUrl ='';
	if(returntype == 'GSTR1' || returntype == 'SalesRegister'){
		invDefUrl = '${contextPath}/invdef/${id}/${client.id}/'+returntype+'/Receipts/${month}/${year}';
	}else{
		invDefUrl = '${contextPath}/invdef/${id}/${client.id}/GSTR2/Payments/${month}/${year}';
	}
		$.ajax({
			url: invDefUrl,
			async: true,
			cache: false,
			success : function(invNo) {	
				$('#voucherNumbers').val(invNo.InvoiceNumber);	
			}
		});
	if(returntype == 'GSTR1' || returntype == 'SalesRegister'){
		/* $.ajax({
			url: "${contextPath}/bnkdtls/${id}/${client.id}",
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(bankDetails) {
				clientBankDetails = new Array();
				$('#bankselect').html('').hide();
				$("#bankselect").append($("<option></option>").attr("value","").text("-- Select Bank --")); 
				for (var i=0; i<bankDetails.length; i++) {
					$("#bankselect").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname));
					clientBankDetails.push(bankDetails[i]);
				}
				if(clientBankDetails && clientBankDetails.length == 1) {
					if(returntype != 'Purchase Register' && returntype != 'PurchaseOrder'){
						$("#payment_bank_details").show();
					}
					$(".bank_details_add").attr("checked","checked");
					$('#bankselect').val(clientBankDetails[0].accountnumber);
					selectBankName(clientBankDetails[0].accountnumber);
				}else if(clientBankDetails && clientBankDetails.length > 1){
				}
				$("#bankselect").show();
				
			}
		}); */
	}else{
		/* $('#pmnt_bank').css("display","none");
		$('#pmnt_details').css("display","none"); */
		$('#adv_adj_date').html('Adv. Payment. Date');
		$('#adv_adj_amt').html('Adv. Payment. Amt');
	}
	/* if($('#bankselect').val() != ''){
		$('#payment_bank_details').css("display","block");
		$('.bank_details_add').attr("checked","true");
	}else{
		$('#payment_bank_details').css("display","none");
		$('.bank_details_add').removeAttr("checked");
	} */
	if(returntype == 'GSTR1' || returntype == 'SalesRegister'){
		$('#paymenthead').html('Receipts');
		$('#addpayment').html('Add Receipt');
		$('#paymenthistory').html('Receipt History');
		$('#paymentbtn').html('Add Receipt');
	}else{
		$('#paymenthead').html('Payments');
		$('#addpayment').html('Add Payment');
		$('#paymenthistory').html('Payment History');
		$('#paymentbtn').html('Add Payment');
	}
	$("#invoice_returntype").val(returntype);
	$('.with-errors').html('');
	$('.form-group').removeClass('has-error has-danger');
	$("#advpmntdate").text('');$("#advpmntpos").text('');$("#advpmntamnt").text('');$("#advpmntadjust").text('');$('#advpmntno').val('');
	$('#voucherNumbers').val('');
	$('#mop').val('');
	$('#dateofpayment').val('');
	$('#amount').val('');
	$('#referenceNumber').val('');
	$('#invoiceModal').css("z-index","1033");
	$('#paymentsModal').modal('show');
	 $.ajax({
		  url: "${contextPath}/populate_recordvalues/"+recArray+"/"+returntype+"/"+invoicetype,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response){
				var amount=$('#amount').val();
				var rec=response.amount;
				var netrec=response.totalamount;
				var invdate=response.invdate.split('/');
				var paymentDate = new Date().getDate()+'/' + month + '/' + year;
				$('#dateofpayment').datetimepicker({
					value: paymentDate,
				  	timepicker: false,
				  	format: 'd-m-Y',
				  	minDate: new Date(invdate[2], (invdate[1]-1), invdate[0]),
				  		//new Date(response.invdate),
				  	scrollMonth: true
				});
				if(response.accountno == ''){
					$(".bank_details_add").removeAttr("checked").prop("checked",false);
					$("#payment_bank_details").hide();
					$("#details_bnk").css("display","none");
				}else{
					$(".bank_details_add").attr("checked","checked").prop("checked",true);
					$("#payment_bank_details").show();
					$("#details_bnk").css("display","block");
				}
				$('#bankselect').val(response.accountno);
				$('#bank_Name').val(response.bankname);
				$('#bank_AcctNo').val(response.accountno);
				$('#bank_AccountName').val(response.accountname);
				$('#bank_Branch').val(response.branchname);
				$('#bank_IFSC').val(response.ifsccode);
				$('#invoice_id').val(response.invoiceid);
				$('#beforereceivedAmount').val(response.amount);
				$('#invtype').val(invoicetype);
				$('#invno').html(response.invoicenumber);
				$('#netreceive').html(formatNumber(parseFloat(response.totalamount).toFixed(2)));
    			$('#recordinvno').val(response.invoicenumber);
    			$('#custname_record').val(response.billedtoname);
    			$('#gstin').val(response.gstin);
    			$('#received').html(formatNumber(parseFloat(response.amount).toFixed(2)));
    			pendamt=netrec-rec;
    			$('#previousPendingBalance').val(parseFloat(pendamt).toFixed(2));
    			$('#pendingamt').html(formatNumber(parseFloat(pendamt).toFixed(2)));
    			$('.openingbal').html(formatNumber(parseFloat(pendamt).toFixed(2)));
				$('.invtotalamt').html(formatNumber(parseFloat(response.totalamount).toFixed(2)));
    			if(pendamt == netrec){
    				$('#paymentStatus').val("UnPaid");
    			}else if(pendamt == 0){
    				$('#paymentStatus').val("Paid");
    			}else if(rec > 0){
    				$('#paymentStatus').val("Partially Paid");
    			}
		},error:function(err){
		}
		
	}); 
}
$('#bankselect').change(function(){
	var bnknumber = $(this).val();
	//var bankaccountNumber = $('#selectBank').val();
	$('#bank_Name').val(' ');
	$('#bank_AcctNo').val(' ');
	$('#bank_Branch').val(' ');
	$('#bank_IFSC').val(' ');
	$('#bank_AccountName').val(' ');
	$("#details_bnk").css("display","block");
	if(bnknumber == ''){
		$("#details_bnk").css("display","none");
	}
	if(bnknumber) {
		clientBankDetails.forEach(function(bankdetail) {
			if(bankdetail.accountnumber == bnknumber) {
				$('#bank_Name').val(bankdetail.bankname);
				$('#bank_AcctNo').val(bankdetail.accountnumber);
				$('#bank_Branch').val(bankdetail.branchname);
				$('#bank_IFSC').val(bankdetail.ifsccode);
				$('#bank_AccountName').val(bankdetail.accountName);
			}
		});
	}else if($('#bankselect').val() !=''){
		var bankaccountNumber = $('#bankselect').val();
		if(bankaccountNumber){
		clientBankDetails.forEach(function(bankdetail) {
			if(bankdetail.accountnumber == bankaccountNumber) {
				$('#bank_Name').val(bankdetail.bankname);
				$('#bank_AcctNo').val(bankdetail.accountnumber);
				$('#bank_Branch').val(bankdetail.branchname);
				$('#bank_IFSC').val(bankdetail.ifsccode);
				$('#bank_AccountName').val(bankdetail.accountName);
			}
		});
	}
	
	}
});
function paymentDashboard(tabId,nav,clientid){
	$('#addpayment').removeClass('active');
	if(tabId == 'addpayment'){
		$('#paymentbtn').css('display','inline-block');
		$('#tab1').show();
		$('#tab2').hide();
	}else if(tabId == 'paymentHistory'){
		$('#paymentbtn').css('display','none');
	}
	if(tabId == 'paymentHistory'){
		var invoice_returntype=$('#invoice_returntype').val();
		$.ajax({
			url: "${contextPath}/invoivepaymenthistory/"+recArray+"/"+clientid,
			async: true,
			cache: false,
			success : function(response){
				$('#historyTable tbody').html('');
				var i=1;
				$.each(response, function(index, itemData) {
					if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
						$.each(itemData.paymentitems, function(index, payemntItemData) {
							$('#historyTable tbody').append('<tr><td class="text-center">'+itemData.voucherNumber+'</td><td class="text-center">'+itemData.paymentDate+'</td><td class="text-center">'+payemntItemData.modeOfPayment+'</td><td class="text-center" class="text-center">'+payemntItemData.referenceNumber+'</td><td class="text-center">'+formatNumber(parseFloat(payemntItemData.amount).toFixed(2))+'</td><td class="text-center">'+formatNumber(parseFloat(payemntItemData.pendingBalance).toFixed(2))+'</td></tr>');
						});
					}else{
						$('#historyTable tbody').append('<tr><td class="text-center">'+itemData.voucherNumber+'</td><td class="text-center">'+itemData.paymentDate+'</td><td class="text-center">'+itemData.modeOfPayment+'</td><td class="text-center" class="text-center">'+itemData.referenceNumber+'</td><td class="text-center">'+formatNumber(parseFloat(itemData.amount).toFixed(2))+'</td><td class="text-center">'+formatNumber(parseFloat(itemData.pendingBalance).toFixed(2))+'</td></tr>');
					}
					i++;
				});
			},error:function(err){
			
			}
		});
		$('.addpayment').css('display','none');
		$('#tab2').show();
		$('#tab1').hide();
		$('#paymentHistory').addClass('active');
		$('.openingbal').html(formatNumber(parseFloat(pendamt).toFixed(2)));
	}
}
/* $('#dateofpayment').datetimepicker({
  	timepicker: false,
  	format: 'd-m-Y',
  	scrollMonth: true
}); */
function updateAmount(no){
	var beforereceivedAmount=$('#beforereceivedAmount').val();
	var receive=$('#received').text().replace(/,/g, "");
	var netreceive=parseFloat($('#netreceive').text().replace(/,/g, ""));
	var amt=$('#amount'+no).val();
	if(amt == ''){
    	amt = 0;	
    }	
	//$('#received').html(0);
  var totalamt = parseFloat(netreceive);
  updatePaymentTotals(no);
  var rec = parseFloat($('#received').text().replace(/,/g, ""));
  var tamt =  (parseFloat(beforereceivedAmount) + rec).toFixed(2);
  var pendamt=netreceive - tamt;
  
  $('#pendingBalance').val(pendamt);
 $('#pendingamt').text(formatNumber(parseFloat(pendamt).toFixed(2)));
 $('#received').html(formatNumber(parseFloat(tamt).toFixed(2)));
 $('.openingbal').html(formatNumber(parseFloat(pendamt).toFixed(2)));
 $('#received_amount').val(formatNumber(parseFloat(tamt).toFixed(2)));

	var received_amount = $('#received').text().replace(/,/g, "");
	var pendingamt = $('#pendingamt').text().replace(/,/g, "");
	
	var ramt=parseFloat(received_amount);
	var pamt=parseFloat(pendingamt);
	
	 if(pamt == 0){
		 $('#paymentStatus').val("Paid");
	 }else if(pamt != 0){
		 $('#paymentStatus').val("Partially Paid");
	 }else {
		 $('#paymentStatus').val("UnPaid");
	 }
}

function updatePaymentTotals(no){
	var payRowCount = $('#paymentbody tr').length;
	var netReceivable = 0;
	for(var i=1;i<=payRowCount;i++) {
			var amt=$('#amount'+i).val();
			if(amt != ""){
				netReceivable += parseFloat(amt);
			}
	}
	$('#received').html(formatNumber(parseFloat(netReceivable).toFixed(2)));
}
   $('#checkadvance').change(function(){	
    	if($('.adv_adjusted_pmnt').is(":checked")){
    		$('.adv_adjusted_pmnt').val("true");
    		$('#sortable_table4').css("display","inline-block");$('#adv_pmnt_div').css("display","inline-block");
    		}else{
    			$('.adv_adjusted_pmnt').val("false");
    			$('#sortable_table4').css("display","none");$('#adv_pmnt_div').css("display","none");$('#advpmnttax').val('0.0');$('#advpmnt_adjusted').parent().removeClass('has-error has-danger');$('#advpmnt_amt1').css("display","none");
    			$('#advpmntno').val('');$('#advpmntdate').text('');$('#advpmntpos').text('');$('#advpmntamnt').text('');$('#advpmntadjust').text('');$('#advpmnt_adjusted').val('');
    			$('#advpmnttaxrate').val('0');$('#pmntigsttax').val('0.0');$('#pmntcgsttax').val('0.0');$('#pmntsgsttax').val('0.0');$('#advpmntcessrate').val('');$('#advpmntcessamnt').val('');$('#advpmntremaining').val('');
    		}
    	});
   $('#advpmnttax').hover(function() {$("#pmnttax_rate_drop").css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"16%","margin-top":"5px"});
	  }, function() {$("#pmnttax_rate_drop").css("display","none");}
);
   function findpmnttax(){
	   var rt = document.getElementById('advpmnt_adjusted');
		if(rt){
			var taxableValue = parseFloat(rt.value);
		}
		var r = document.getElementById('advpmnttaxrate');
		
		if(rt && r){
			var val = 0;
			val = ((taxableValue*parseFloat(r.value))/100);
		}
		if(val > 0) {
			if(interStateFlag) {
			document.getElementById('pmntigsttax').value = Number(0).toFixed(2);
			document.getElementById('pmntcgsttax').value = (((val/2).toFixed(2))/1).toFixed(2);
			document.getElementById('pmntsgsttax').value = (((val/2).toFixed(2))/1).toFixed(2);
			}else{
				document.getElementById('pmntigsttax').value = ((val.toFixed(2))/1).toFixed(2);
				document.getElementById('pmntcgsttax').value = Number(0).toFixed(2);
				document.getElementById('pmntsgsttax').value = Number(0).toFixed(2);
			}
		}else {
			document.getElementById('pmntigsttax').value = Number(0).toFixed(2);
			document.getElementById('pmntcgsttax').value = Number(0).toFixed(2);
			document.getElementById('pmntsgsttax').value = Number(0).toFixed(2);
		}
		
		var igst21 = document.getElementById('pmntigsttax').value;
		var cgst21 = document.getElementById('pmntcgsttax').value;
		var sgst21 = document.getElementById('pmntsgsttax').value;
		var tot21;
		tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
		document.getElementById('advpmnttax').value = tot21.toFixed(2);
		findPmntTotal();
		
   }
   function findadvpmntcess(){
	   var rt = document.getElementById('advpmnt_adjusted');
		var r = document.getElementById('advpmntcessrate');
		var taxableValue = parseFloat(rt.value);
		var val = 0;
		val = ((taxableValue*parseFloat(r.value))/100);
		if(val > 0) {
			val = ((val.toFixed(2))/1).toFixed(2);
		} else {
			val = Number(0).toFixed(2);
		}
		document.getElementById('advpmntcessamnt').value = val;
		findPmntTotal();
   }
   function findPmntTotal(){
	   var t = document.getElementById('advpmnt_adjusted');
	   var totalValue = 0;
		if(t && t.value) {
			totalValue = parseFloat(t.value);
		}
		var cessamt = document.getElementById('advpmntcessamnt');
		if(cessamt && cessamt.value) {
			totalValue += parseFloat(cessamt.value);
		}
		var samt = document.getElementById('pmntsgsttax');
		if(samt && samt.value) {
			totalValue += parseFloat(samt.value);
		}
		var iamt = document.getElementById('pmntigsttax');
		if(iamt && iamt.value) {
			totalValue += parseFloat(iamt.value);
		}
		var camt = document.getElementById('pmntcgsttax');
		if(camt && camt.value) {
			totalValue += parseFloat(camt.value);
		}
		
			if(totalValue) {
				document.getElementById('advpmntremaining').value = totalValue.toFixed(2);
			}
			
   }
   function checkadvpmnt(){
	   findpmnttax();
		var adavail = $('#advpmntadjust').text().replace(/,/g, "");
		var advavailadj = $('#advpmntremaining').val();
		var intadavail = 0.00;var intadvavailadj = 0.00;
		if(adavail != ''){
			intadavail = parseInt(adavail);
		}
		if(advavailadj != ''){
			intadvavailadj = parseInt(advavailadj);
		}
		if(intadvavailadj > intadavail){
			$('#advpmnt_adjusted').parent().addClass('has-error has-danger');
			$('#advpmnt_amt1').css("display","block");
			//var adavail = $('#advrcavailadj_text'+no).val(0.00);
			findpmnttax();
		}else{
			findpmnttax();
			$('#advpmnt_adjusted').parent().removeClass('has-error has-danger');
			$('#advpmnt_amt1').css("display","none");
		}
		
	}
   function addrow(){
		var rowCount = $('#paymentbody tr').length;
		var tablen = rowCount;
		rowCount = rowCount+1;
		var table3=document.getElementById("sortable_table3");
		$('#paymentbody').append('<tr><td id="sno_row2" align="center">'+rowCount+'</td><td class="form-group" style="border: none;margin-bottom: 0px;"><select id="pament_mode'+rowCount+'" class="form-control mop_text'+rowCount+'" onchange="modeOfPaymentChange('+rowCount+')" name="paymentitems['+(rowCount-1)+'].modeOfPayment" required="required" data-error="Please select payment type"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS - GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select></td><td class="form-group"><input type="text" class="form-control amount_text'+rowCount+'" id="amount'+rowCount+'" name="paymentitems['+(rowCount-1)+'].amount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyUp="updateAmount('+rowCount+')" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td class="form-group"><input type="text" class="form-control ref_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].referenceNumber" placeholder="Reference No."></td><td class="form-group"><input type="text" class="form-control ledger_text'+rowCount+'" id="ledger_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].ledger" required placeholder="Ledger"/></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_button'+rowCount+'" class="item_delete" onclick="delete_payrow('+rowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
		$('.mop_text'+rowCount).children('option').remove();
		$(".mop_text"+rowCount).append($("<option></option>").attr("value","").text("-- Select --"));
		for (var i=0; i<cashbankledgername.length; i++) {
			$(".mop_text"+rowCount).append($("<option></option>").attr("value",cashbankledgername[i]).text(cashbankledgername[i]));
		}
		var ledgeroptions = {
			url: function(phrase) {
				return contextPath+"/ledgerlist/"+clientId+"?query="+ phrase + "&format=json";
			},
			getValue: "ledgerName",
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $("#ledger_text"+rowCount).getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($("#eac-container-ledger_text"+rowCount+" ul").children().length == 0) {
						$("#addlegername").show();
					} else {
						$("#addlegername").hide();
					}
				},
				maxNumberOfElements: 10
			}
		};
	$('#ledger_text'+rowCount).easyAutocomplete(ledgeroptions);
	$('.date_text'+rowCount).datetimepicker({
	  	timepicker: false,
	  	format: 'd-m-Y',
	  	scrollMonth: true
	});
		$('form[name="paymentform"]').validator('update');
	}
   function formatNumbers(nStr) {
		var negativenumber = false;
		if(nStr && nStr.includes("-")){
			negativenumber = true;
			nStr = nStr.replace("-","");
		}
		nStr=nStr.toString();var afterPoint = '';
		if(nStr.indexOf('.') > 0)
		   afterPoint = nStr.substring(nStr.indexOf('.'),nStr.length);
		nStr = Math.floor(nStr);
		nStr=nStr.toString();
		var lastThree = nStr.substring(nStr.length-3);
		var otherNumbers = nStr.substring(0,nStr.length-3);
		if(otherNumbers != '')
		    lastThree = ',' + lastThree;
		var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + afterPoint;
		if(negativenumber){
			res = "-"+res;
		}
		return res;
	}
   
   function ledgerNames(){
	   $.ajax({
			url: contextPath+'/ledgerNames'+urlSuffix,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(ledgerNames) {
				cashbankledgername = new Array();
				cashbankledgername = ledgerNames;
			}
		});
   }
</script>