var invGstr2bTableReportsUrl = new Object();
var invGstr2bTableReports = new Object();
var invSummaryTableReportsUrl = new Object();
var invTableDwnld_invoiceUrl = new Object();
var invTableDwnld_itemUrl = new Object();
var invTableDwnld_fullUrl = new Object();
var invTableSummaryDownloadUrl = new Object();
var cpUsersResponse;
var isCpUsersResponseLoaded = false;
function getval(sel) {
	if (sel == 'Yearly') {
		$('.yearly-sp').css("display", "inline-block");$('.dropdown-menu.ret-type').css("left", "19%");
	}
}
function updateYearlyOption(value){
	$('.yearlyoption').text(value);
}
function loadGstr2bReportsUsersByClient(id, clientId, varRetType, varRetTypeCode, callback){
	var urlStr = _getContextPath()+'/cp_users/'+id+'/'+clientId;
	if(isCpUsersResponseLoaded){
		callback(cpUsersResponse, varRetType, varRetTypeCode);
	}else{
		$.ajax({
			url: urlStr,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				cpUsersResponse = response;
				isCpUsersResponseLoaded = true;
				callback(response, varRetType, varRetTypeCode);
			}
		});
	}
}
function loadGstr2bReportsUsersInDropDown(response, varRetType, varRetTypeCode){
	var usersMultiSelObj = $('#multiselect'+varRetTypeCode+'2')
	usersMultiSelObj.find('option').remove().end();
	usersMultiSelObj.append($("<option></option>").attr("value",globaluser).text(globaluser)); 
	if (response.length > 0) {				
		response.forEach(function(cp_user) {
			usersMultiSelObj.append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
		});
	}
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'2', '- User -', varRetType);
	$('#multiselect'+varRetTypeCode+'2').multiselect('rebuild');
}
function loadGstr2bReportsCustomersInDropdown(response, varRetType, varRetTypeCode){
	var usersMultiSelObj = $('#multiselect'+varRetTypeCode+'3')
	usersMultiSelObj.find('option').remove().end();
	if (response.billToNames.length > 0) {				
		response.billToNames.forEach(function(customer) {
			if(customer != ''){
				usersMultiSelObj.append($("<option></option>").attr("value",customer).text(customer));
			} 
		});
	}
	var customerType ='- Customers -';
	if(varRetType == 'Purchase Register'){
		customerType ='- Suppliers -';
	}
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'3', customerType , varRetType);
	$('#multiselect'+varRetTypeCode+'3').multiselect('rebuild');
}
function loadGstr2bReportsInvoiceSupport(clientId, varRetType, varRetTypeCode, month, year, callback){
	var urlStr = _getContextPath()+'/getmultimonthInvsSupport/'+clientId+'/'+varRetType+'/'+month+'/'+year;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			callback(response, varRetType, varRetTypeCode);
		}
	});
}
function invsGstr2bDownloads(id, clientId, varRetType, varRetTypeCode, month ,year, rtyear){
	var dwnldurl_invoicewise = _getContextPath()+"/gstr2bmultimonthdwnldxlsyearly/"+id+"/"+clientId+"/"+varRetType+"/"+rtyear+"/invoicewise?reporttype=Multimonth-Reports";
	var dwnldurl_itemwise = _getContextPath()+"/gstr2bmultimonthdwnldxlsyearly/"+id+"/"+clientId+"/"+varRetType+"/"+rtyear+"/itemwise?reporttype=Multimonth-Reports";
	var dwnldurl_full = _getContextPath()+"/monthgstr2bfulldwnldyearlyxlsnew/"+id+"/"+clientId+"/"+varRetType+"/"+rtyear+"?reporttype=Multimonth-Reports";
	
	invTableDwnld_invoiceUrl[varRetTypeCode] = dwnldurl_invoicewise;
	invTableDwnld_itemUrl[varRetTypeCode] = dwnldurl_itemwise;
	invTableDwnld_fullUrl[varRetTypeCode] = dwnldurl_full;
	  
	$(".reportsdbTable_"+varRetTypeCode+" div.toolbar").html('').append('<div class="dropdown pull-right mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false">DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="iditemUrl" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="idinvoiceUrl" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="idfullUrl" href="'+dwnldurl_full+'">ALL DETAILS DOWNLOAD</a></div></div></div>');
	//$("#reports_dataTable"+varRetTypeCode+"_wrapper div.toolbar").html('').append('<div class="dropdown pull-right mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false">DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="iditemUrl" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="idinvoiceUrl" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="idfullUrl" href="'+dwnldurl_full+'">ALL DETAILS DOWNLOAD</a></div></div></div>');
}
function loadGstr2bReportsInvTable(id, clientId, varRetType, varRetTypeCode, month ,year, rtyear){
	var pUrl =_getContextPath()+"/getmultimonthinvs/"+id+"/"+clientId+"/"+varRetType+"/"+month+'/'+rtyear;
	invGstr2bTableReportsUrl[varRetTypeCode] = pUrl;
	if(invGstr2bTableReports[varRetTypeCode]){
		invGstr2bTableReports[varRetTypeCode].clear();
		invGstr2bTableReports[varRetTypeCode].destroy();
	}
	invGstr2bTableReports[varRetTypeCode] = $('#reports_dataTable'+varRetTypeCode).DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [[10, 25, 50, 100, 500], [10, 25, 50, 100, 500]],
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 loadGstr2bReportsTotals(varRetTypeCode, resp.invoicesAmount);
	        	 $('#updatefilter_summary_reports').show();
	        	 return resp.invoices.content ;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		'columns': getInvColumns(id, clientId, varRetType, year),
		'columnDefs':getInvColumnDefs(varRetType),
		"createdRow": function( row, data, dataIndex ) {
		    if ( data.gstStatus == "CANCELLED" ) {
		      $(row).addClass( 'cancelled_line' );
		    }
		  }
	});
}

function getInvColumns(id, clientId, varRetType, year){
	var varRetTypeCode = varRetType.replace(" ", "_");
	var itype = {data: function ( data, type, row ) {
		var ntType = "";
		if(data.invtype == "Credit/Debit Notes"){
			if(data.cdn && data.cdn.length > 0){
				if(data.cdn[0].nt[0].ntty != undefined){
					ntType = data.cdn[0].nt[0].ntty;
					if(ntType  == "C"){
						data.invtype = "Credit Note";
					}else if(ntType  == "D"){
						data.invtype = "Debit Note";
					}else{
						data.invtype = "Credit/Debit Notes";
					}
				}
			}
			if(data.cdna && data.cdna.length > 0){
				if(data.cdna[0].nt[0].ntty != undefined){
					ntType = data.cdna[0].nt[0].ntty;
					if(ntType  == "C"){
						data.invtype = "Credit Note";
					}else if(ntType  == "D"){
						data.invtype = "Debit Note";
					}else{
						data.invtype = "Credit/Debit Notes";
					}
				}
			}
		}
		return '<span class="text-left invoiceclk ">'+data.invtype+'</span>';
	}};
	var invsNo = {data:  function ( data, type, row ) {
					var invoiceno = '';
					if(data.invoiceno != undefined){
						invoiceno = data.invoiceno;
					}
			      	 return '<span class="text-left invoiceclk ">'+invoiceno+'</span>';
	}};
	var billtoname = {data: function ( data, type, row ) {
						var billedtoname = '';
						if(data.billedtoname != undefined){
							billedtoname = data.billedtoname;
						}
					    return '<span class="text-left invoiceclk ">'+billedtoname+'</span>';
	}};
	var billtogtnn = {data: function ( data, type, row ) {
						var invtype = data.invtype;
						var gstnnum= '';
						var cfs = '';
						if(invtype == 'Import Goods'){
							if(data.impGoods && data.impGoods.length > 0){
								if(data.impGoods[0].stin != undefined){
									gstnnum = data.impGoods[0].stin;
								}
							}
						}else{
							if(data.b2b && data.b2b.length > 0){
								if(data.b2b[0].ctin != undefined){
									gstnnum = data.b2b[0].ctin;
								}
							}else if(data.cdnr && data.cdnr.length > 0){
								if(data.cdnr[0].ctin != undefined){
									gstnnum = data.cdnr[0].ctin;
								}
							}
						}
				var cfs = '';
				if(invtype == 'B2B'){
				  		if(data.b2b && data.b2b.length > 0){
								if(data.b2b[0].cfs != undefined){
										cfs = data.b2b[0].cfs;
								}
						}
				}else if(invtype == 'Credit/Debit Notes' || invtype == 'Credit Note' || invtype == 'Debit Note'){
				        		if(data.cdn && data.cdn.length > 0){
									if(data.cdn[0].cfs != undefined){
											cfs = data.cdn[0].cfs;
										}
				        		}
		}
		return '<span class="text-left invoiceclk ">'+gstnnum+'</span>';
	}};		
	var invDate = {data: function ( data, type, row ) {
		var dateofinvoice = "";
		if(data.dateofinvoice != null){
			var invDate = new Date(data.dateofinvoice);
			var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			dateofinvoice = day + "/" + month + "/" + year;
		}else{dateofinvoice = "";}
		return '<span class="text-left invoiceclk ">'+(new Date(data.dateofinvoice)).toLocaleDateString("en-GB")+'</span>';
	}};
	var taxblamt = {data: function ( data, type, row ) {
						var totalTaxableAmt = 0.0;
						if(data.totaltaxableamount){
							totalTaxableAmt = data.totaltaxableamount;
						}
						if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)' || data.invtype == "Advance Adjusted Detail"){
					   	 	return '<span class="ind_formats text-right invoiceclk ">-'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
						}else{
							return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
						}
	}};
	var totlTax = {data: function ( data, type, row ) {
						var totalTax = 0.0;
						if(data.totaltax){
							totalTax = data.totaltax;
						}
						if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)' || data.invtype == "Advance Adjusted Detail"){
					   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">-'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">-'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">-'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">-'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
						}else{
							return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
						}
	}};
	var totalamt = {data: function ( data, type, row ) {
					var totalAmount = 0.0;
					if(data.totalamount){
						totalAmount = data.totalamount;
					}
					if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)' || data.invtype == "Advance Adjusted Detail"){
				   	 	return '<span class="ind_formats text-right invoiceclk ">-'+formatNumber(totalAmount.toFixed(2))+'</span>';
					}else{
						return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalAmount.toFixed(2))+'</span>';
					}
	}};
	
	return [itype, invsNo, billtoname, billtogtnn, invDate, taxblamt,  totlTax, totalamt];		
}
function getInvColumnDefs(varRetType){
	return [
		{
			"targets":  [5,6,7],
			className: 'dt-body-right'
		}
	];
}
function initializeRemoveAppliedFiltersGstr2bReports(varRetType, varRetTypeCode){
	$('#divFilters'+varRetTypeCode).on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i=1;i<=5;i++){
			$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
			continue;
		}
		applyGstr2bReportsInvFilters(varRetType);
	});
}
function initiateCallBacksForGstr2bMultiSelectReports(varRetType, varRetTypeCode){
	var multiSelDefaultVals = ['','- Invoice Type -', '', '', '- Branches -', '- Verticals -','- Filing Status -'];	
	for(i=1;i<=5;i++){
		if(i == 2 || i == 3){
			$('#multiselect'+varRetTypeCode+i).hide();
			continue;
		}
		multiselrefresh_reports('#multiselect'+varRetTypeCode+i, multiSelDefaultVals[i], varRetType);
	}
}
function multiselrefresh_reports(idval, descVal, varRetType){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyGstr2bReportsInvFilters(varRetType);
		},
		onSelectAll: function(){
			applyGstr2bReportsInvFilters(varRetType);
		},
		onDeselectAll: function(){
			applyGstr2bReportsInvFilters(varRetType);
		}
	});
}
function defaultClearGstr2bInvFiltersReports(retType, type) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=5;i++){
		$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#divFilters'+retType.replace(" ", "_")).html('');
	$('.normaltable .filter').css("display","none");
	var retTypeCode = retType.replace(" ", "_");
	//reloadReportsInvTable(retTypeCode, new Array(), new Array(), new Array(), new Array(), new Array());
	//reloadInvSummaryTable(retTypeCode, new Array(), new Array(), new Array(), new Array(), new Array(),"");		
}
function clearGstr2bInvFiltersReportss(retType) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=5;i++){
		$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#divFilters'+retType.replace(" ", "_")).html('');
	$('.normaltable .filter').css("display","none");
	var retTypeCode = retType.replace(" ", "_");
	reloadReportsInvTable(retTypeCode, new Array(), new Array(), new Array(), new Array(), new Array());
	reloadInvSummaryTable(retTypeCode, new Array(), new Array(), new Array(), new Array(), new Array(), "");
}
function applyGstr2bReportsInvFilters(retType) {
	var retTypeCode = retType.replace(" ", "_");
	var typeOptions = $('#multiselect'+retTypeCode+'1 option:selected');
	var userOptions = $('#multiselect'+retTypeCode+'2 option:selected');
	var vendorOptions = $('#multiselect'+retTypeCode+'3 option:selected');
	var branchOptions = $('#multiselect'+retTypeCode+'4 option:selected');
	var verticalOptions = $('#multiselect'+retTypeCode+'5 option:selected');
	
	if(typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var branchArr=new Array();
	var verticalArr=new Array();
	var filterContent='';
	
	if(typeOptions.length > 0) {
		for(var i=0;i<typeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+typeOptions[i].text+'<span data-val="'+typeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			typeArr.push(typeOptions[i].value);
		}
	}
	if(userOptions.length > 0) {
		for(var i=0;i<userOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+userOptions[i].value+'<span data-val="'+userOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			var userValue = userOptions[i].value;
			if(userValue.includes("&")){
				userValue = userValue.replace("&","-mgst-");
			}
			userArr.push(userValue);
		}
	}
	if(vendorOptions.length > 0) {
		for(var i=0;i<vendorOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+vendorOptions[i].value+'<span data-val="'+vendorOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			var vendorValue = vendorOptions[i].value;
			if(vendorValue.includes("&")){
				vendorValue = vendorValue.replace("&","-mgst-");
			}
			vendorArr.push(vendorValue);
		}
	}
	if(branchOptions.length > 0) {
		for(var i=0;i<branchOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+branchOptions[i].value+'<span data-val="'+branchOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			branchArr.push(branchOptions[i].value);
		}
	}
	if(verticalOptions.length > 0) {
		for(var i=0;i<verticalOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+verticalOptions[i].value+'<span data-val="'+verticalOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			verticalArr.push(verticalOptions[i].value);
		}
	}
	
	$('#divFilters'+retTypeCode).html(filterContent);
	reloadReportsInvTable(retTypeCode, typeArr, userArr, vendorArr, branchArr, verticalArr);	
	reloadInvSummaryTable(retTypeCode, typeArr, userArr, vendorArr, branchArr, verticalArr, "");	
}
function reloadReportsInvTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr){
	var pUrl = invGstr2bTableReportsUrl[retTypeCode];
	var appd = 'booksOrReturns=';
	
	if(typeArr.length > 0){
		appd+='&invoiceType='+typeArr.join(',');
	}
	if(userArr.length > 0){
		appd+='&user='+userArr.join(',');
	}
	if(vendorArr.length > 0){
		appd+='&vendor='+vendorArr.join(',');
	}
	if(branchArr.length > 0){
		appd+='&branch='+branchArr;
	}
	if(verticalArr.length > 0){
		appd+='&vertical='+verticalArr.join(',');
	}
	
	pUrl += '?'+appd;
	invGstr2bTableReports[retTypeCode].ajax.url(pUrl).load();
	var invoiceUrl = invTableDwnld_invoiceUrl[retTypeCode]+''+appd;
	var itemUrl = invTableDwnld_itemUrl[retTypeCode] + ''+appd;
	var fullUrl = invTableDwnld_fullUrl[retTypeCode] + ''+appd;
	$('#iditemUrl').attr('href',itemUrl);
	$('#idinvoiceUrl').attr('href',invoiceUrl);
	$('#idfullUrl').attr('href',fullUrl);
}
function loadGstr2bReportsTotals(varRetType, totalsData){
	$('#idCount'+varRetType).html(totalsData ? totalsData.totalTransactions : '0');
	$('#idTaxableVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTaxVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotAmtVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idIGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalIGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idSGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalSGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCESS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCESSAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTCSTDS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.tcsTdsAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
}
function reloadInvSummaryTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr, booksOrReturns){
	var pUrl = invSummaryTableReportsUrl[retTypeCode];
	var dUrl = invTableSummaryDownloadUrl[retTypeCode];
	var appd = '';
	if(typeArr.length > 0){
		appd+='&invoiceType='+typeArr.join(',');
	}
	if(userArr.length > 0){
		appd+='&user='+userArr.join(',');
	}
	if(vendorArr.length > 0){
		appd+='&vendor='+vendorArr.join(',');
	}
	if(branchArr.length > 0){
		appd+='&branch='+branchArr;
	}
	if(verticalArr.length > 0){
		appd+='&vertical='+verticalArr.join(',');
	}
	
	pUrl += appd;
	dUrl += ''+appd;
	$('#dwnldxls').attr("href",dUrl);
	$.ajax({
		url: pUrl,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			summaryResponse(response);
		}
	});
}
function loadGstr2bReportsSummary(id, clientid, varRetType, month, year){
	var yr = year-1;
	var urlStr = _getContextPath()+'/mutltimonthReportsTotalSummarynew/'+clientid+'/'+varRetType+'/'+month+"/"+year+"?booksorReturns=";
	var dwnUrl = _getContextPath()+'/dwnldReportsFinancialSummaryxlsnew/'+id+'/'+clientid+'/'+varRetType+'?reporttype=Multimonth-Reports&year='+year+'&fromdate=null&todate=null';
	
	invSummaryTableReportsUrl[varRetType] = urlStr;
	invTableSummaryDownloadUrl[varRetType] = dwnUrl;
	$('#dwnldxls').attr("href",dwnUrl);
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			summaryResponse(response);
		}
	});
}
function summaryResponse(response){
	var totinvs=0, totTaxableVal=0.0, totTaxAmt=0.0, totExemAmt=0.0, totSales=0.0, totIgst=0.0, totCgst=0.0, totSgst=0.0, totCess=0.0, totTcsAmt=0.0, totTdsAmt=0.0, totItcAmt=0.0;
	if($.fn.DataTable.isDataTable('#reportTable4')){$('#reportTable4').DataTable().destroy();}
	$.each(response, function(key, value){
		$('#totalinvoices'+key).html(value.totalTransactions);
		$('#totalTaxableValue'+key).html(formatNumber(value.Sales));
		$('#taxAmt'+key).html(formatNumber(value.Tax));
		$('#sales'+key).html(formatNumber(value.totalamt));
		$('#igstAmount'+key).html(formatNumber(value.igst));
		$('#cgstAmount'+key).html(formatNumber(value.cgst));
		$('#sgstAmount'+key).html(formatNumber(value.sgst));
		$('#cessAmount'+key).html(formatNumber(value.cess));
		$('#tcsAmt'+key).html(formatNumber(value.tcsamount));
		if(varRetType == 'GSTR1'){
			$('#exemAmt'+key).html(formatNumber(value.exempted));
			totExemAmt+=parseFloat(value.exempted);					
			totTcsAmt+=parseFloat(value.tcsamount);
		}else{
			$('#itcAmt'+key).html(formatNumber(value.itc));
			totItcAmt+=parseFloat(value.itc);
			totTdsAmt+=parseFloat(value.tdsamount);
		}
		totinvs+=parseInt(value.totalTransactions);
		totTaxableVal+=parseFloat(value.Sales);
		totTaxAmt+=parseFloat(value.Tax);
		totSales+=parseFloat(value.totalamt);
		totIgst+=parseFloat(value.igst);
		totCgst+=parseFloat(value.cgst);
		totSgst+=parseFloat(value.sgst);
		totCess+=parseFloat(value.cess);
	});
	$('#ytotal_Transactions').html(totinvs);
	$('#ytotal_Taxablevalue').html(formatNumber(totTaxableVal.toFixed(2)));
	$('#ytotal_Taxvalue').html(formatNumber(totTaxAmt.toFixed(2)));
	if(varRetType == 'GSTR1'){
		$('#ytotal_Exemvalue').html(formatNumber(totExemAmt.toFixed(2)));
		$('#ytotal_Tcsvalue').html(formatNumber(totTcsAmt.toFixed(2)));
	}else{
		$('#ytotal_ITCvalue').html(formatNumber(totItcAmt.toFixed(2)));
		$('#ytotal_Tcsvalue').html(formatNumber(totTdsAmt.toFixed(2)));
	}
	$('#ytotal_TotalAmount').html(formatNumber(totSales.toFixed(2)));
	$('#ytotal_IGSTAmount').html(formatNumber(totIgst.toFixed(2)));
	$('#ytotal_CGSTAmount').html(formatNumber(totCgst.toFixed(2)));
	$('#ytotal_SGSTAmount').html(formatNumber(totSgst.toFixed(2)));
	$('#ytotal_CessAmount').html(formatNumber(totCess.toFixed(2)));
	$('#reportTable4').DataTable({
		"paging": false,
		scrollX: true,
		scrollCollapse: true,
		ordering: false,
		info: false,
		filter: false,
		"dom": '<"toolbar">frtip',  
		lengthChange: false,
		fixedColumns:{
			leftColumns:1,
			rightColumns:1
		}
	});
}
function formatNumber(nStr) {
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
