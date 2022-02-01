var paymentsTable = new Object();
var paymentsTableUrl = new Object();

	$(function(){
		if(tabType == 'receive'){
			$('#salesTab').click();
		}else if(tabType == 'made'){
			$('#purchasesTab').click();
		}
	});

function loadPaymentInvoices(id, clientid, tabName,financialyear){
	
	var type = 'GSTR1';
	if(tabName == 'purchasesTab'){
		type = 'GSTR2';
	}
	var pUrl = _getContextPath()+'/recordPayments/'+id+'/'+clientid+"?type="+type+'&financialYear='+financialyear;
	paymentsTableUrl[tabName] = pUrl;
	if(paymentsTable['paymentstable_'+tabName]){
		paymentsTable['paymentstable_'+tabName].clear();
		paymentsTable['paymentstable_'+tabName].destroy();
	}
	paymentsTable['paymentstable_'+tabName] = $('#paymentstable_'+tabName).DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, 500, 'ALL'], [10, 25, 50, 100, 500, -1] ],
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 loadPaymentsTotals(tabName, resp.paymentsAmts);
	        	 
	        	 return resp.invoices.content ;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[6,'desc']],
		'columns': getPaymentsInvColumns(clientid, tabName),
		'columnDefs' : getPaymentsInvColumnDefs(tabName)
	});	
}

function loadPaymentSupport(id, clientid, varTabName, callback){
	var type = 'GSTR1';
	if(varTabName == 'purchasesTab'){
		 type = 'GSTR2';
	}
	var urlStr = _getContextPath()+'/getPaymentInvsSupport/'+id+'/'+clientid+"?type="+type;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			callback(response, varTabName);
		}
	});
}
function loadPaymentCustomersInDropdown(response, varTabName){
	var usersMultiSelObj = $('#multiselect'+varTabName+'3')
	if (response.billedtoname.length > 0) {				
		response.billedtoname.forEach(function(customer) {
			if(customer != ''){
				usersMultiSelObj.append($("<option></option>").attr("value",customer).text(customer));
			} 
		});
	}
	var custStr = '- Customers -';
	if(varTabName == 'paymentstab'){
		custStr = '- Suppliers -';
	}
	multiselrefresh('#multiselect'+varTabName+'3', custStr, varTabName);
	var gstnsMultiSelObj = $('#multiselect'+varTabName+'4')
	if (response.billedtogstin.length > 0) {				
		response.billedtogstin.forEach(function(gstno) {
			if(gstno != ''){
				gstnsMultiSelObj.append($("<option></option>").attr("value",gstno).text(gstno));
			} 
		});
	}
	var gstStr = '- GST Numbers -';
	
	multiselrefresh('#multiselect'+varTabName+'4', gstStr, varTabName);
}
function initiateCallBacksForMultiSelect(varTabName){
	var financialYearObj = $('#multiselect'+varTabName+'1');
	var monthObj = $('#multiselect'+varTabName+'2');
	var modeOfPaymentObj = $('#multiselect'+varTabName+'5');
	
	
	var multiSelDefaultVals = ['', '- Financial Year -', '- Months -', '', ''];
	for(i=0; i<multiSelDefaultVals.length ;i++){
		if(multiSelDefaultVals[i] != ''){
			multiselrefresh('#multiselect'+varTabName+i, multiSelDefaultVals[i], varTabName);
		}
	}
}

function multiselrefresh(idval, descVal, varTabName){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyPaymentsInvFilters(varTabName);
		},
		onSelectAll: function(){
			applyPaymentsInvFilters(varTabName);
		},
		onDeselectAll: function(){
			applyPaymentsInvFilters(varTabName);
		}
	});
}
function defaultApplyFinancialYear(varTabName,financialyear) {
	$('.normaltable .'+varTabName).css("display","block");
	$('#multiselect'+varTabName+'1').multiselect().find(':checkbox[value="'+financialyear+'"]').attr('checked','checked');
	$('#multiselect'+varTabName+'1 option[value="'+financialyear+'"]').attr("selected", 1);
	$('#multiselect'+varTabName+'1').multiselect('select',''+financialyear+'').multiselect('updateButtonText');
	$('#multiselect'+varTabName+'1').multiselect('refresh');
	var filterContent = '<span data-role="tagsinput" placeholder="" class="btaginput" >'+financialyear+'<span data-val="'+financialyear+'" class="deltag" data-role="remove"></span></span>';
	$('#divFilters'+varTabName).html(filterContent);
	//applyPaymentsInvFilters(varTabName);
}

function applyPaymentsInvFilters(varTabName) {
	
	var financialYearOptions = $('#multiselect'+varTabName+'1 option:selected');
	var monthOptions = $('#multiselect'+varTabName+'2 option:selected');
	var vendorOptions = $('#multiselect'+varTabName+'3 option:selected');
	var gstnoOptions = $('#multiselect'+varTabName+'4 option:selected');
	
	if(financialYearOptions.length > 0 || monthOptions.length > 0 || vendorOptions.length > 0 || gstnoOptions.length > 0){
		$('.normaltable .'+varTabName).css("display","block");
		//$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .'+varTabName).css("display","none");
		//$('.normaltable .filter').css("display","none");
	}
	var financialYearArr = new Array();
	var monthArr = new Array();
	var vendorArr = new Array();
	var gstnoArr = new Array();
	var paymentModeArr = new Array();
	
	var filterContent='';
	
	if(financialYearOptions.length > 0) {
		for(var i=0;i<financialYearOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+financialYearOptions[i].value+'<span data-val="'+financialYearOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			financialYearArr.push(financialYearOptions[i].value);
		}
	} else {}
	
	if(monthOptions.length > 0) {
		for(var i=0;i<monthOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+monthOptions[i].text+'<span data-val="'+monthOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			monthArr.push(monthOptions[i].value);
		}
	} else {}
	
	if(vendorOptions.length > 0) {
		for(var i=0;i<vendorOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+vendorOptions[i].value+'<span data-val="'+vendorOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			vendorArr.push(vendorOptions[i].value);
		}
	} else {}
	
	if(gstnoOptions.length > 0) {
		for(var i=0;i<gstnoOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+gstnoOptions[i].value+'<span data-val="'+gstnoOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			gstnoArr.push(gstnoOptions[i].value);
		}
	} else {}
	
	$('#divFilters'+varTabName).html(filterContent);
	reloadPaymentsInvTable(financialYearArr, monthArr, vendorArr, gstnoArr, varTabName);
}

function clearPaymentsFilters(varTabName){
	for(i = 1; i <= 4; i++){
		$('#multiselect'+varTabName+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');		
	}
	$('#divFilters'+varTabName).html('');
	//$('.normaltable .filter').css("display","none");
	$('.normaltable .'+varTabName).css("display","none");
	reloadPaymentsInvTable(new Array(), new Array(), new Array(), new Array(), varTabName);
}

function paymentsFilters(varTabName,financialyear){
	for(i = 1; i <= 4; i++){
		$('#multiselect'+varTabName+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');		
	}
	$('#divFilters'+varTabName).html('');
	//$('.normaltable .filter').css("display","none");
	$('.normaltable .'+varTabName).css("display","none");
	var financialYearArr = new Array();
	financialYearArr.push(''+financialyear+'');
	reloadPaymentsInvTable(financialYearArr, new Array(), new Array(), new Array(), varTabName);
}

function reloadPaymentsInvTable(financialYearArr, monthArr, vendorArr, gstnoArr, varTabName){
	var pUrl = paymentsTableUrl[varTabName];
	var sUrl = pUrl.substr(0,pUrl.indexOf('&')).replace("&","");
	var type = 'GSTR1';
	if(varTabName == 'paymentstab'){
		type = 'GSTR2';
	}
	//var appd = 'type='+type;
	var appd = '';
	if(financialYearArr.length > 0){
		appd+='&financialYear='+financialYearArr.join(',');
	}
	if(monthArr.length > 0){
		appd+='&month='+monthArr.join(',');
	}
	if(vendorArr.length > 0){
		appd+='&vendor='+vendorArr.join(',');
	}
	if(gstnoArr.length > 0){
		appd+='&gstno='+gstnoArr.join(',');
	}
	
	sUrl += appd;
	paymentsTable['paymentstable_'+varTabName].ajax.url(sUrl).load();
}

function initializeRemoveAppliedFilters(varTabName){
	$('#divFilters'+varTabName).on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i = 1;i <= 4;i++){
			$('#multiselect'+varTabName+i).multiselect('deselect', [val]);
			continue;
		}
		applyPaymentsInvFilters(varTabName);
	});
}

function updatePaymentDetails(id,rettype,jsptype){
	var referName = 'salesTab';
	if(rettype == 'GSTR2'){
		referName = 'purchasesTab';
	}
	$.ajax({
		url: contextPath+'/ledgerNames'+urlSuffixs+'/'+rettype,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(ledgerNames) {
			cashbankledgername = new Array();
			cashbankledgername = ledgerNames;
		}
	});
	$('#paymentsprogress-bar_'+referName).removeClass('d-none');
	$('#paymentsModal').modal("show");
	$('#paymentid').remove();
	$('#paymenthistory').css("display","none");
	$(".bank_details_add").removeAttr("checked").prop("checked",false);
	$("#payment_bank_details").hide();
	$('.pymntaddrow').attr("onclick","addeditrow()");
	if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
		$('#paymenthead').html('Receipts');
		$('#addpayment').html('Update Receipt');
		$('#paymentbtn').html('Update Receipt');
	}else{
		$('#paymenthead').html('Payments');
		$('#addpayment').html('Update Payment');
		$('#paymentbtn').html('Update Payment');
	}
	if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
	}else{
		$('#pmnt_bank').css("display","none");
		$('#pmnt_details').css("display","none");
	}
	if(id) {
		$.ajax({
			url: _getContextPath()+'/getPaymentsInfo/'+id,
			async: false,
			cache: false,
			//dataType:"json",
			contentType: 'application/json',
			success : function(payments) {
				$('#paymentid').remove();
				$('.payment_table tbody').find("tr:gt(0)").remove();
				var totaladvtax = 0;
				$('#paymentbtn').removeClass('disabled');
				$("form[name='paymentform']").append('<input type="hidden" id="paymentid" name="id" value="'+payments.docId+'">');
				$('#paymentform').removeAttr("action");
				if(jsptype == 'journal'){
					$('#paymentform').attr("action",contextPath+"/record_payments"+urlSuffixs+"/"+rettype+"/"+pmntsSuffix+"?type="+jsptype);
				}else if(jsptype == 'ledgerreport'){
					$('#paymentform').attr("action",contextPath+"/record_payments"+urlSuffixs+"/"+rettype+"/"+pmntsSuffix+"?type="+jsptype);
				}else{
					$('#paymentform').attr("action",contextPath+"/record_payments"+urlSuffixs+"/"+rettype+"/"+pmntsSuffix+"?type="+rettype);
				}
				$('#voucherNumbers').val(payments.voucherNumber);
				$('#dateofpayment').val(payments.paymentDate);
				$('#invno').text(payments.invoiceNumber);
				$('#netreceive').text(formatNumber((payments.previousPendingBalance).toFixed(2)));
				$('#beforereceivedAmount').val(parseFloat((payments.receivedAmount).replace(/,/g, "")));
				$('#received').text(formatNumber((payments.paidAmount).toFixed(2)));
				$('#pendingamt').text(formatNumber((payments.previousPendingBalance - payments.paidAmount).toFixed(2)));
				$('#bankselect').val(payments.accountnumber);
				if(payments.accountnumber == ""){
					$('#details_bnk').css('display','none');
				}else{
					$('#details_bnk').css('display','block');
				}
				$('#bank_AcctNo').val(payments.accountnumber);
				$('#bank_AccountName').val(payments.accountName);
				$('#bank_Branch').val(payments.branchname);
				$('#bank_IFSC').val(payments.ifsccode);
				$('#advpmntdate_val').val(payments.advpmntrecdate);
				$('#advpmntdate').text(payments.advpmntrecdate);
				$('#advpmntpos_val').val(payments.advpmntpos);
				$('#advpmntpos').text(payments.advpmntpos);
				if(payments.advpmntrecamt != "" && payments.advpmntrecamt != undefined){
					
					$('#advpmntamnt_val').val((payments.advpmntrecamt).toFixed(2));
					$('#advpmntamnt').text(formatNumber((payments.advpmntrecamt).toFixed(2)));
				}
				if(payments.advpmntavailadjust != "" && payments.advpmntavailadjust != undefined){
					$('#advpmntadjust_val').val((payments.advpmntavailadjust).toFixed(2));
					$('#advpmntadjust').text(formatNumber((payments.advpmntavailadjust).toFixed(2)));
				}
				$('#advpmntno').val(payments.advpmntrecno);
				if(payments.advpmntadjust != "" && payments.advpmntadjust != undefined){
					$('#advpmnt_adjusted').val((payments.advpmntadjust).toFixed(2));
				}
				$('#advpmnttaxrate').val(payments.advpmnttaxrate);
				if(payments.advpmntigstamt != "" && payments.advpmntigstamt != undefined){
					totaladvtax = totaladvtax+payments.advpmntigstamt;
					$('#pmntigsttax').val((payments.advpmntigstamt).toFixed(2));
				}
				if(payments.advpmntcgstamt != "" && payments.advpmntcgstamt != undefined){
					totaladvtax = totaladvtax+payments.advpmntcgstamt;
					$('#pmntcgsttax').val((payments.advpmntcgstamt).toFixed(2));
				}
				if(payments.advpmntsgstamt != "" && payments.advpmntsgstamt != undefined){
					totaladvtax = totaladvtax+payments.advpmntsgstamt;
					$('#pmntsgsttax').val((payments.advpmntsgstamt).toFixed(2));
				}
				$('#advpmnttax').val((totaladvtax).toFixed(2));
				
				$('#advpmntcessrate').val(payments.advpmntcessrate);
				if(payments.advpmntcessamt != "" && payments.advpmntcessamt != undefined){
					$('#advpmntcessamnt').val((payments.advpmntcessamt).toFixed(2));
				}
				if(payments.advpmntremainamt != "" && payments.advpmntremainamt != undefined){
					$('#advpmntremaining').val((payments.advpmntremainamt).toFixed(2));
				}
				$('#invoice_id').val(payments.invoiceid);
				$('#previousPendingBalance').val(payments.previousPendingBalance);
				$('#invtype').val(payments.invtype);
				$('#recordinvno').val(payments.invoiceNumber);
    			$('#custname_record').val(payments.customerName);
    			$('#gstin').val(payments.gstNumber);
    			$('#invoice_id').val(payments.invoiceid);
    			$('#pendingBalance').val(payments.pendingBalance);
    			$('#invoice_returntype').val(payments.returntype);
    			$('#received_amount').val(payments.receivedAmount);
    			$('#invtype').val(payments.invtype);
    			if(payments.isadvadjust == true){
    				$('.adv_adjusted_pmnt').prop("checked",true).val("true");
    				$('#adv_pmnt_div,#sortable_table4').css("display","inline-block");
    			}else{
    				$('.adv_adjusted_pmnt').prop("checked",false).val("false");
    				$('#adv_pmnt_div,#sortable_table4').css("display","none");
    			}
    			if(parseFloat(payments.previousPendingBalance) == parseFloat(payments.pendingBalance)){
    				$('#paymentStatus').val("UnPaid");
    			}else if(parseFloat(payments.pendingBalance) == 0){
    				$('#paymentStatus').val("Paid");
    			}else if(parseFloat((payments.receivedAmount).replace(/,/g, "")) > 0){
    				$('#paymentStatus').val("Partially Paid");
    			}
    			var totalPending = 0.00;
    			$('#amount1').attr("onkeyup","updateAmount1(1)");
    			$('.mop_text1').children('option').remove();
    			$(".mop_text1").append($("<option></option>").attr("value","").text("-- Select --"));
    			for (var i=0; i<cashbankledgername.length; i++) {
    				$(".mop_text1").append($("<option></option>").attr("value",cashbankledgername[i]).text(cashbankledgername[i]));
    			}
				if(payments.paymentitems != null && payments.paymentitems.length > 0){
					for(var i=1;i<payments.paymentitems.length;i++){
						addeditrow();
					}
					$.each(payments.paymentitems, function(index, payemntItemData) {
						index++;
						$('.mop_text'+index).val(payemntItemData.modeOfPayment);
						if(payemntItemData.amount == '' || payemntItemData.amount == null){
							totalPaymentAmount = 0;
							$('.amount_text'+index).val(0.00);
						}else{
							totalPaymentAmount = totalPaymentAmount+payemntItemData.amount;
							$('.amount_text'+index).val(payemntItemData.amount);
						}
						$('.ref_text'+index).val(payemntItemData.referenceNumber);
						$('.ledger_text'+index).val(payemntItemData.ledger);
					});
				}
    			$('#paymentsprogress-bar_'+referName).addClass('d-none');
			},error : function(err){
				$('#paymentsprogress-bar_'+referName).addClass('d-none');
			}
		});
	}
}

function addrow(){
	var rowCount = $('#paymentbody tr').length;
	var tablen = rowCount;
	rowCount = rowCount+1;
	var table3=document.getElementById("sortable_table3");
	$('#paymentbody').append('<tr><td id="sno_row2" align="center">'+rowCount+'</td><td class="form-group" style="border: none;margin-bottom: 0px;"><select id="pament_mode'+rowCount+'" class="form-control mop_text'+rowCount+'" onchange="modeOfPaymentChange('+rowCount+')" name="paymentitems['+(rowCount-1)+'].modeOfPayment" required="required" data-error="Please select payment type"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS - GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select></td><td class="form-group"><input type="text" class="form-control amount_text'+rowCount+'" id="amount'+rowCount+'" name="paymentitems['+(rowCount-1)+'].amount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyUp="updateAmount('+rowCount+')" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td class="form-group"><input type="text" class="form-control ref_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].referenceNumber" placeholder="Reference No."></td><td class="form-group"><input type="text" class="form-control ledger_text'+rowCount+'" id="ledger_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].ledger" required placeholder="Ledger"/></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_button'+rowCount+'" class="item_delete" onclick="delete_payrow('+rowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
	var ledgeroptions = {
		url: function(phrase) {
			phrase = phrase.replace('(',"\\(");
			phrase = phrase.replace(')',"\\)");
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

function updateAmount1(no){
	var beforereceivedAmount=$('#beforereceivedAmount').val();
	var receive=$('#received').text().replace(/,/g, "");
	var netreceive=parseFloat($('#netreceive').text().replace(/,/g, ""));
	var amt=$('#amount'+no).val();
	if(amt == ''){
	   	amt = 0;	
	}	
	var totalamt = parseFloat(netreceive);
	updatePaymentTotals(no);
	var rec = parseFloat($('#received').text().replace(/,/g, ""));
	var tamt = 0;
	if(rec > parseFloat(beforereceivedAmount)){
		tamt =  ((rec - parseFloat(beforereceivedAmount))+parseFloat(beforereceivedAmount)).toFixed(2);
	}else{
		tamt =  (parseFloat(beforereceivedAmount)-(parseFloat(totalPaymentAmount) - rec)).toFixed(2);  
	}
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

function addeditrow(){
	var rowCount = $('#paymentbody tr').length;
	var tablen = rowCount;
	rowCount = rowCount+1;
	var table3=document.getElementById("sortable_table3");
	$('#paymentbody').append('<tr><td id="sno_row2" align="center">'+rowCount+'</td><td class="form-group" style="border: none;margin-bottom: 0px;"><select id="pament_mode'+rowCount+'" class="form-control mop_text'+rowCount+'" onchange="modeOfPaymentChange('+rowCount+')" name="paymentitems['+(rowCount-1)+'].modeOfPayment" required="required" data-error="Please select payment type"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS - GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select></td><td class="form-group"><input type="text" class="form-control amount_text'+rowCount+'" id="amount'+rowCount+'" name="paymentitems['+(rowCount-1)+'].amount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyUp="updateAmount1('+rowCount+')" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td class="form-group"><input type="text" class="form-control ref_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].referenceNumber" placeholder="Reference No."></td><td class="form-group"><input type="text" class="form-control ledger_text'+rowCount+'" id="ledger_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].ledger" required placeholder="Ledger"/></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_button'+rowCount+'" class="item_delete" onclick="delete_payrow('+rowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
	$('.mop_text'+rowCount).children('option').remove();
	$(".mop_text"+rowCount).append($("<option></option>").attr("value","").text("-- Select --"));
	for (var i=0; i<cashbankledgername.length; i++) {
		$(".mop_text"+rowCount).append($("<option></option>").attr("value",cashbankledgername[i]).text(cashbankledgername[i]));
	}
	var ledgeroptions = {
		url: function(phrase) {
			phrase = phrase.replace('(',"\\(");
			phrase = phrase.replace(')',"\\)");
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

function loadPaymentsTotals(varTabName, totalsData){
	$('#idCount'+varTabName).html(totalsData ? totalsData.totalTransactions : '0');
	$('#idTotalInvoiceVal'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalInvoiceAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotalPaidAmt'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalPaidAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotalPending'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalPendingAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCashAmt'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCashAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idBankAmt'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalBankAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTDSITAmt'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTdsItAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTDSGSTAmt'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTdsGstAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idDiscountAmt'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalDiscountAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idOthersAmt'+varTabName).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalOthersAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
}

function getPaymentsInvColumns(clientid, tabName){
	
	var invDate = {data: function (data, type, row ) {
		return '<span class="text-left">'+data.paymentDate+'</span>';
	}};
	
	var paymentno = {data: function ( data, type, row ) {
		var paymentid = "";
		if(data.voucherNumber){
			paymentid = data.voucherNumber;
		}
		return '<span class="ind_formats text-left">'+paymentid+'</span>';
	}};
	var invoiceno = {data: function (data, type, row ) {
		var invoicenum = "";
		if(data.invoiceNumber){
			invoicenum = data.invoiceNumber;
		}
		return '<span class="ind_formats text-left">'+invoicenum+'</span>';
	}};
	var gstno = {data: function ( data, type, row ) {
		var gstnum = "";
		if(data.gstNumber){
			gstnum = data.gstNumber;
		}
		return '<span class="ind_formats text-left">'+gstnum+'</span>';
	}};
	
	var customerName = {data: function ( data, type, row ) {
		var billedtoname = '';
		if(data.customerName != undefined){
			billedtoname = data.customerName;
		}
      	 return '<span class="text-left">'+billedtoname+'</span>';
    }};
	
	var paidamount = {data: function ( data, type, row ) {
		var receivedAmount = 0;
		if(data.paidAmount != undefined){
			receivedAmount = data.paidAmount;
		}
		var modeofPayments = '<div id="paymentModeOfPayment" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">Mode of Payments</h6>';
		if(data.cashAmount > 0){
			modeofPayments += '<div class="row pl-3" style="height:25px"><p class="mr-3">Cash <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.cashAmount.toFixed(2))+'</label></span></div>';
		}
		if(data.bankAmount > 0){
			modeofPayments += '<div class="row pl-3" style="height:25px"><p class="mr-3">Bank <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.bankAmount.toFixed(2))+'</label></span></div>';
		}
		if(data.tdsItAmount > 0){
			modeofPayments += '<div class="row pl-3" style="height:25px"><p class="mr-3">TDS-IT <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.tdsItAmount.toFixed(2))+'</label></span></div>';
		}
		if(data.tdsGstAmount > 0){
			modeofPayments += '<div class="row pl-3" style="height:25px"><p class="mr-3">TDS-GST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.tdsGstAmount.toFixed(2))+'</label></span></div>';
		}
		if(data.discountAmount > 0){
			modeofPayments += '<div class="row pl-3" style="height:25px"><p class="mr-3">Discount <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.discountAmount.toFixed(2))+'</label></span></div>';
		}
		if(data.othersAmount > 0){
			modeofPayments += '<div class="row pl-3" style="height:25px"><p class="mr-3">Others <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.othersAmount.toFixed(2))+'</label></span></div>';
		}
		modeofPayments += '</div>';
		
		return '<span class="text-right" id="paidThrough">'+formatNumber(receivedAmount.toFixed(2))+''+modeofPayments+'</span>';
    }};
		
	var referenceNo = {data: function ( data, type, row ) {
		var referenceNumber = '';
		if(data.paymentitems[0].referenceNumber != undefined){
			referenceNumber = data.paymentitems[0].referenceNumber;
		}
      	 return '<span class="text-left">'+referenceNumber+'</span>';
    }};
	
	var actions = {data:  function ( data, type, row ) {
		return '<a href="#" data-toggle="modal" style="margin-right:3px;" data-target="#paymentsModal" onClick="updatePaymentDetails(\''+data.docId+'\',\''+data.returntype+'\',\'payments\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+data.docId+'\',\''+data.returntype+'\',\''+data.clientid+'\',\''+data.voucherNumber+'\',\''+data.invoiceNumber+'\')"><img src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a>';
    }};
	return [invDate, paymentno, invoiceno, customerName, gstno, paidamount,  referenceNo, actions];
}
function getPaymentsInvColumnDefs(varTabName){
	return  [
		{
			"targets":  [5],
			className: 'dt-body-right'
		}
	]
}

function showDeletePopup(paymentid,returntype,clientid,vouchernumber,invoicenumber) {
	$('#deleteModal').modal('show');
	if(/^[/]*$/.test(vouchernumber) == false) {
		vouchernumber = vouchernumber.replaceAll("/","invNumCheck");
	}
	if(/^[/]*$/.test(invoicenumber) == false) {
		invoicenumber = invoicenumber.replaceAll("/","invNumCheck");
	}
	$('#btnDelete').attr('onclick', "deletePayment('"+paymentid+"','"+returntype+"','"+clientid+"','"+vouchernumber+"','"+invoicenumber+"')");
}
function deletePayment(paymentid,returntype,clientid,vouchernumber,invoicenumber) {
	$.ajax({
		url: _getContextPath()+"/delpayment/"+paymentid+"/"+returntype+"/"+clientid+"/"+vouchernumber+"/"+invoicenumber,
		success : function(response) {
			if(returntype == 'GSTR1'){
				window.location.href = _getContextPath()+'/payments_history'+paymenturlSuffix+'/GSTR1/'+Paymenturlprefix+'?type=receive'; 
			}else{
				window.location.href = _getContextPath()+'/payments_history'+paymenturlSuffix+'/GSTR2/'+Paymenturlprefix+'?type=made';
			}
		}
	});
}

$(document).on('mouseover','#paidThrough', function (event) {
	$(this).find('#paymentModeOfPayment').css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"12%","margin-top":"5px"});
}).on('mouseleave','#paidThrough',  function(){
    	$(this).next().css("display","none");
    	$(this).find("#paymentModeOfPayment").stop(true, true).delay(100).fadeOut(300);
}); 