var invTableReportsUrl = new Object();
var invTableReports = new Object();
var invSummaryTableReportsUrl = new Object();
var invTableDwnld_invoiceUrl = new Object();
var invTableDwnld_itemUrl = new Object();
var invTableDwnld_fullUrl = new Object();
var invTableSummaryDownloadUrl = new Object();
var cpUsersResponse;
var isCpUsersResponseLoaded = false;
$(function() {
	var year=new Date().getFullYear();
	$( ".helpicon" ).hover(function() {
		$('.reportSales ').show();
	}, function() {
			$('.reportSales ').hide();
	});
	var date = new Date();
	var month = date.getMonth()+1;
	var	year = date.getFullYear();
	var day = date.getDate();
	var mnt = date.getMonth()+1;
	var yr = date.getFullYear();
	salesFileName = 'MGST_Sales_Monthly_'+gstnnumber+'_'+month+year;
	var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
	var customValue = day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
	var date = $('.dpMonths').datepicker({
		autoclose: true,
		viewMode: 1,
		minViewMode: 1,
		format: 'mm-yyyy'
	}).on('changeDate', function(ev) {
		month = ev.date.getMonth()+1;
		year = ev.date.getFullYear();
	});
	$('.dpCustom').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();
		mnt = ev.date.getMonth()+1;
		yr = ev.date.getFullYear();
		$('.fromtime').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
	});
	$('.dpCustom1').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();
		mnt = ev.date.getMonth()+1;
		yr = ev.date.getFullYear();
		$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
	});
	$('.dpMonths').datepicker('update', dateValue);
	$('.dpCustom').datepicker('update', customValue);
	$('.dpCustom1').datepicker('update', customValue);

	
	
});

function loadGlobalReportsUsersByClient(id, clientId, varRetType, varRetTypeCode, callback){
	var urlStr = _getContextPath()+'/adminsales_cp_users/'+id+'/'+clientId
	if(isCpUsersResponseLoaded){
		callback(cpUsersResponse, varRetType, varRetTypeCode);;
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
function loadGlobalReportsInvoiceSupport(clientId, varRetType, varRetTypeCode, month, year, type,booksorReturns, callback){
	var urlStr = _getContextPath()+'/getAddtionalGlobalReportsInvsSupport/'+varRetType+'/'+month+'/'+year+'?booksorReturns='+booksorReturns+'&clientids='+clientId;
	if(type == 'Custom'){
		//month eq fromtime and  year eq totime
		urlStr = _getContextPath()+'/getGlobalReportsCustomAddtionalInvsSupport/'+varRetType+'/'+month+'/'+year+'?booksorReturns='+booksorReturns+'&clientids='+clientId;
	}
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
function loadGlobalReportsUsersInDropDown(response, varRetType, varRetTypeCode){
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
function loadGlobalReportsCustomersInDropdown(response, varRetType, varRetTypeCode){
	var usersMultiSelObj = $('#multiselect'+varRetTypeCode+'3');
	usersMultiSelObj.find('option').remove().end();
	if (response.billToNames && response.billToNames.length > 0) {				
		response.billToNames.forEach(function(customer) {
			if(customer != ''){
				usersMultiSelObj.append($("<option></option>").attr("value",customer).text(customer));
			} 
		});
	}
	var customerType ='- Customers -';
	if(varRetType == 'Purchase Register' || varRetType == 'GSTR2'){
		customerType ='- Suppliers -';
	}
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'3', customerType , varRetType);
	$('#multiselect'+varRetTypeCode+'3').multiselect('rebuild');
	
	/*var custMultiSelObj = $('#multiselect'+varRetTypeCode+'7');
	custMultiSelObj.find('option').remove().end();
	if (response.customFields && response.customFields.length > 0) {				
		response.customFields.forEach(function(field) {
			if(field != ''){
				custMultiSelObj.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'7', '- Custom Fields -' , varRetType);
	$('#multiselect'+varRetTypeCode+'7').multiselect('rebuild');*/
}
function initializeRemoveAppliedFiltersGlobalReports(varRetType, varRetTypeCode){
	$('#divFilters'+varRetTypeCode).on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i=1;i<=7;i++){
			$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
			continue;
		}
		applyAdminInvFilters(varRetType);
	});
}
function initiateCallBacksForMultiSelectGlobalReports(varRetType, varRetTypeCode){
	var multiSelDefaultVals = ['','- Invoice Type -', '', '', '- Branches -', '- Verticals -', '- Filing Status -',''];	
	for(i=1;i<=7;i++){
		//if(varRetTypeCode == 'GSTR1' || ){
			if(i == 2 || i == 3 || i == 4 || i == 7){
				$('#multiselect'+varRetTypeCode+i).hide();
				continue;
			}
		//}
		multiselrefresh_reports('#multiselect'+varRetTypeCode+i, multiSelDefaultVals[i], varRetType);
	}
}
function multiselrefresh_reports(idval, descVal, varRetType){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyAdminInvFilters(varRetType);
		},
		onSelectAll: function(){
			applyAdminInvFilters(varRetType);
		},
		onDeselectAll: function(){
			applyAdminInvFilters(varRetType);
		}
	});
	//$(idval).multiselect('refresh');
}
function loadGlobalReportsCustomTotals(varRetType, totalsData){
	
	var totalTransactions = 0, totalTaxableAmount = 0, totalTaxAmount = 0, totalAmount = 0, totalIGSTAmount = 0;
	var totalCGSTAmount = 0, totalSGSTAmount = 0, totalCESSAmount = 0, totalExemptedAmount  = 0, totalITCAvailable = 0,tcsTdsAmount =0;
	
	for(var i=0; i < totalsData.length; i++){
		 
		totalTransactions = totalTransactions+totalsData[i].totalTransactions;
		totalTaxableAmount =totalTaxableAmount+totalsData[i].totalTaxableAmount; 
		totalTaxAmount =totalTaxAmount+totalsData[i].totalTaxAmount;
		totalAmount =totalAmount+totalsData[i].totalAmount;
		totalIGSTAmount =totalIGSTAmount+totalsData[i].totalIGSTAmount;
		totalCGSTAmount =totalCGSTAmount+totalsData[i].totalCGSTAmount; 
		totalSGSTAmount =totalSGSTAmount+totalsData[i].totalSGSTAmount; 
		totalCESSAmount =totalCESSAmount+totalsData[i].totalCESSAmount; 
		
		if('GSTR1' == varRetType){
			totalExemptedAmount  =totalExemptedAmount+totalsData[i].totalExemptedAmount;
			tcsTdsAmount = tcsTdsAmount+totalsData[i].tcsTdsAmount;
		}else{
			totalITCAvailable =totalITCAvailable+totalsData[i].totalITCAvailable;
			tcsTdsAmount = tcsTdsAmount+totalsData[i].tdsAmount;
		}
		
	}
	
	$('#idCount'+varRetType).html(totalTransactions);
	$('#idTaxableVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTaxVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotAmtVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idIGST'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalIGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCGST'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalCGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idSGST'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalSGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCESS'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalCESSAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTCSTDS'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(tcsTdsAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	if('GSTR1' == varRetType){
		$('#idExemptedVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalExemptedAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');		
	}else{
		$('#idITCVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	}
}
function loadGlobalReportsTotals(varRetType, totalsData){
	$('#idCount'+varRetType).html(totalsData ? totalsData.totalTransactions : '0');
	$('#idTaxableVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTaxVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotAmtVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idIGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalIGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idSGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalSGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCESS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCESSAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	
	if('GSTR1' == varRetType){
		$('#idExemptedVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalExemptedAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idTCSTDS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.tcsTdsAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	}else{
		$('#idITCVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idTCSTDS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.tdsAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	}
}
function invsDownloads(id, clientIds, varRetType,varRetTypeCode, month, year, userType, fullname,booksOrReturns, type){
	if(booksOrReturns == 'books'){
		if(type == 'Yearly'){
			var yr=parseInt(year);//-1;
			dwnldurl_invoicewise = _getContextPath()+"/dwnldxlsyearlyAdminGroupn/"+varRetType+"/"+yr+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_itemwise = _getContextPath()+"/dwnldxlsyearlyAdminGroupn/"+varRetType+"/"+yr+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_full = _getContextPath()+"/dwnldxlsyearlyAdminGroupn/"+varRetType+"/"+yr+"/fullDetails?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		}else if(type == 'Custom'){
			dwnldurl_invoicewise =_getContextPath()+"/dwnldxlscustomAdminGroupn/"+varRetType+"/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_itemwise =_getContextPath()+"/dwnldxlscustomAdminGroupn/"+varRetType+"/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_full =_getContextPath()+"/dwnldxlscustomAdminGroupn/"+varRetType+"/"+month+"/"+year+"/fullDetails?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		}else{
			dwnldurl_invoicewise = _getContextPath()+"/dwnldxlsmonthlyAdminGroupn/"+varRetType+"/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_itemwise = _getContextPath()+"/dwnldxlsmonthlyAdminGroupn/"+varRetType+"/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_full = _getContextPath()+"/dwnldxlsmonthlyAdminGroupn/"+varRetType+"/"+month+"/"+year+"/fullDetails?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		}
	}else{
		if(type == 'Yearly'){
			var yr=parseInt(year);//-1;
			dwnldurl_invoicewise = _getContextPath()+"/dwnldxlsGSTR1yearlyAdminGroupn/"+varRetType+"/"+yr+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_itemwise = _getContextPath()+"/dwnldxlsGSTR1yearlyAdminGroupn/"+varRetType+"/"+yr+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_full = _getContextPath()+"/dwnldxlsGSTR1yearlyAdminGroupn/"+varRetType+"/"+yr+"/fullDetails?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		}else if(type == 'Custom'){
			dwnldurl_invoicewise =_getContextPath()+"/dwnldxlsgstr1customAdminGroupn/"+varRetType+"/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_itemwise =_getContextPath()+"/dwnldxlsgstr1customAdminGroupn/"+varRetType+"/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_full =_getContextPath()+"/dwnldxlsgstr1customAdminGroupn/"+varRetType+"/"+month+"/"+year+"/fullDetails?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		}else{
			dwnldurl_invoicewise = _getContextPath()+"/dwnldxlsgstr1monthlyAdminGroupn/"+varRetType+"/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_itemwise = _getContextPath()+"/dwnldxlsgstr1monthlyAdminGroupn/"+varRetType+"/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
			dwnldurl_full = _getContextPath()+"/dwnldxlsgstr1monthlyAdminGroupn/"+varRetType+"/"+month+"/"+year+"/fullDetails?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		}
	}
	
	invTableDwnld_invoiceUrl[varRetTypeCode] = dwnldurl_invoicewise;
	invTableDwnld_itemUrl[varRetTypeCode] = dwnldurl_itemwise;
	invTableDwnld_fullUrl[varRetTypeCode] = dwnldurl_full;
	if(varRetTypeCode == 'GSTR1'){
		 $(".reportsdbTable"+varRetTypeCode+" div.toolbar").html('').append('<div class="dropdown pull-right permissionExcel_Download_In_Reports-Sales mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="monthlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="monthlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="" >DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="iditemUrl" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="idinvoiceUrl" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="idfullUrl" href="'+dwnldurl_full+'">ALL DETAILS DOWNLOAD</a></div></div></div></div>');	        		 
	 }else{
		 $(".reportsdbTable"+varRetTypeCode+" div.toolbar").html('').append('<div class="dropdown pull-right permissionExcel_Download_In_Reports-Purchases mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="monthlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="monthlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="" >DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="iditemUrl" href="#" onclick="exceldownload(\''+invTableDwnld_itemUrl[varRetTypeCode]+'\')">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="idinvoiceUrl" href="#" onclick="exceldownload(\''+invTableDwnld_invoiceUrl[varRetTypeCode]+'\')">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="idfullUrl" href="#" onclick="exceldownload(\''+invTableDwnld_fullUrl[varRetTypeCode]+'\')">ALL DETAILS DOWNLOAD</a></div></div></div></div>');
	 }
	 rolesPermissions();
}

function exceldownload(url) {
    var a = document.createElement("a");
        a.href = url;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
}

function loadGlobalReportsInvTable(id, clientidsArray, varRetType,varRetTypeCode, month, year, userType, fullname,booksorReturns, type){
	var pUrl =_getContextPath()+'/getGlobalReportsAddtionalInvs/'+id+'/'+varRetType+'/'+month+'/'+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;	
	if(type == 'Custom'){
		//month eq fromtime and  year eq totime
		pUrl=_getContextPath()+'/getGlobalReportsAddtionalCustomInvs/'+id+'/'+varRetType+'/'+month+'/'+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;
	}
	
	invTableReportsUrl[varRetTypeCode] = pUrl;
	//if(invTableReports[varRetTypeCode]){
	if ($.fn.DataTable.isDataTable('#gloablReports_dataTable'+varRetTypeCode) ) {
		//invTableReports[varRetTypeCode].clear();
		invTableReports[varRetTypeCode].destroy();
	}

	
	invTableReports[varRetTypeCode] = $('#gloablReports_dataTable'+varRetTypeCode).DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 if(type == "Custom"){
	        		 loadGlobalReportsCustomTotals(varRetTypeCode, resp.invoicesAmount);
	        	 }else{
	        		 loadGlobalReportsTotals(varRetTypeCode, resp.invoicesAmount);
	        	 }
	        	 $('#updatefilter_summary_reports').show();
	        	 rolesPermissions();
	        	 return resp.invoices.content ;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[4,'desc']],
		'columns': getInvColumns(id, clientId, varRetType),
		'columnDefs':getInvColumnDefs(varRetType),
		"createdRow": function( row, data, dataIndex ) {
		    if ( data.gstStatus == "CANCELLED" ) {
		      $(row).addClass( 'cancelled_line' );
		    }
		  }
	});
}

function getInvColumns(id, clientId, varRetType, userType, month, year, booksOrReturns){
	var varRetTypeCode = varRetType.replace(" ", "_");
	var itype = {data: function ( data, type, row ) {
		var ntType = "";
		if(data.invtype == "Credit/Debit Notes"){
			if(varRetType == 'GSTR1')	{
				if(data.cdnr && data.cdnr.length > 0){
					if(data.cdnr[0].nt[0].ntty != undefined){
						ntType = data.cdnr[0].nt[0].ntty;
						if(ntType  == "C"){
							data.invtype = "Credit Note";
						}else if(ntType  == "D"){
							data.invtype = "Debit Note";
						}else{
							data.invtype = "Credit/Debit Notes";
						}
					}
				}
			}else{
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
			}
		}else if(data.invtype == "Credit/Debit Note for Unregistered Taxpayers"){
			if(data.cdnur && data.cdnur.length > 0){
				if(data.cdnur[0].ntty != undefined){
					ntType = data.cdnur[0].ntty;
					if(ntType  == "C"){
						data.invtype = "Credit Note(UR)";
					}else if(ntType  == "D"){
						data.invtype = "Debit Note(UR)";
					}else{
						data.invtype = "Credit/Debit Note for Unregistered Taxpayers";
					}
				}
			}
		}	
					if(data.revchargetype == 'Reverse'){
						return '<span class="text-left invoiceclk ">'+data.invtype+'</span><span><img data-toggle="tooltip" title="Reverse Charge Applied on this Invoice" src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/reversecharge.png" alt="reversecharge" style="height: 18px;margin-left: 10px;margin-bottom:3px"></span>';
				     }else{
				     	return '<span class="text-left invoiceclk ">'+data.invtype+'</span>';
				     }			              	 
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
					   	 	return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">-'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
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
	if(varRetType == 'Purchase Register' || varRetType == 'GSTR2'){
		var totalitc = {data: function ( data, type, row ) {
			var totlitc = 0.0;
			if(data.totalitc){
				totlitc = data.totalitc;
			}
		   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totlitc.toFixed(2))+'</span>';
		    }};
		return [itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt, totalitc];
	}else{
		return [itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt];		
	}
}
function getInvColumnDefs(varRetType){
	if(varRetType == 'GSTR1' || varRetType == 'GSTR2A'){
		return  [
					{
						"targets": [5,6,7],
						className: 'dt-body-right'
					},
				];	
	}else if(varRetType == 'Purchase Register' || varRetType == 'GSTR2'){
		return [
				{
				"targets": [5,6,7,8],
				className: 'dt-body-right'
				},
				{
				"targets": 0,
				"orderable": false
				}
		];
	}
	return [];
}


function clearFilters(retType, booksOrReturns) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=7;i++){
		$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#divFilters'+retType.replace(" ", "_")).html('');
	$('.normaltable .filter').css("display","none");
	var retTypeCode = retType.replace(" ", "_");
	reloadInvTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array(),new Array(),new Array());
	var type = $('#fillingoption span').html();
	if(type == 'Yearly' || type == 'Custom' || type == 'custom'){
		reloadInvSummaryTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array(),'',new Array(),new Array());		
	}else{}
}
function reloadInvTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr,gstr2aFilingArr,custArr){
	var pUrl = invTableReportsUrl[retTypeCode];
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
	if(gstr2aFilingArr.length > 0){
		appd+='&gstr2aFilingStatus='+gstr2aFilingArr.join(',');
	}
	if(custArr.length > 0){
		appd+='&customFieldText1='+custArr.join(',');
	}
	pUrl += ''+appd;
	invTableReports[retTypeCode].ajax.url(pUrl).load();
	var invoiceUrl = invTableDwnld_invoiceUrl[retTypeCode]+''+appd;
	var itemUrl = invTableDwnld_itemUrl[retTypeCode] + ''+appd;
	var fullUrl = invTableDwnld_fullUrl[retTypeCode] + ''+appd;
	$('#iditemUrl').attr('href',itemUrl);
	$('#idinvoiceUrl').attr('href',invoiceUrl);
	$('#idfullUrl').attr('href',fullUrl);
}
function reloadInvSummaryTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr, booksOrReturns,gstr2aFilingArr,custArr){
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
	if(gstr2aFilingArr.length > 0){
		appd+='&gstr2aFilingStatus='+gstr2aFilingArr.join(',');
	}
	if(custArr.length > 0){
		appd+='&customFieldText1='+custArr.join(',');
	}
	pUrl += appd;
	dUrl += ''+appd;
	$('#groupwise_dwnldxls').attr("href",dUrl);
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
function loadGlobalReportsSummary(id,varRetType, clientidsArray, month, year,booksorReturns, type){
	var varRetTypeCode = varRetType.replace(" ", "_");
	var yr = year -1;
	var urlStr =_getContextPath()+'/globalReportsTotalSummary/'+varRetType+'/'+month+"/"+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;
			var dwnUrl =_getContextPath()+'/dwnldGlobalReportsFinancialSummaryxls/'+id+'/'+varRetType+'?booksorReturns='+booksorReturns+'&year='+yr+'&clientids='+clientidsArray+'&fromdate=null&todate=null';
			if(type == 'Custom'){
				urlStr=_getContextPath()+'/globalReportsCustomTotalSummary/'+varRetType+'/'+month+"/"+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;
				dwnUrl =_getContextPath()+'/dwnldGlobalReportsFinancialSummaryxls/'+id+'/'+varRetType+'?booksorReturns='+booksorReturns+'&year=0&clientids='+clientidsArray+'&fromdate='+month+'&todate='+year;
			}
			
			invSummaryTableReportsUrl[varRetTypeCode] = urlStr;
			invTableSummaryDownloadUrl[varRetTypeCode] = dwnUrl;
			$('#groupwise_dwnldxls').attr("href",dwnUrl);
			$.ajax({
				url: urlStr,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					summaryResponse(response,varRetType);
				}
			});
}
function summaryResponse(response,varRetType){
	var totinvs=0, totTaxableVal=0.0, totTaxAmt=0.0, totExemAmt=0.0, totSales=0.0, totIgst=0.0, totCgst=0.0, totSgst=0.0, totCess=0.0, totTcsAmt=0.0, ptotTcsAmt=0.0, totTdsAmt=0.0, totItcAmt=0.0;
	if($.fn.DataTable.isDataTable('#reportTable4')){$('#reportTable4').DataTable().destroy();}
	$.each(response, function(key, value){
		$('#totalinvoices'+key).html(value.totalTransactions);
		$('#totalTaxableValue'+key).html(formatNumber(value.Sales));
		$('#taxAmt'+key).html(formatNumber(value.Tax));
		$('#sales'+key).html(formatNumber(value.totalamt));
		$('#salesigstAmount'+key).html(formatNumber(value.igst));
		$('#salescgstAmount'+key).html(formatNumber(value.cgst));
		$('#salessgstAmount'+key).html(formatNumber(value.sgst));
		$('#salescessAmount'+key).html(formatNumber(value.cess));
		
		
		if(varRetType == 'GSTR1'){
			$('#tcsAmt'+key).html(formatNumber(value.tcsamount));
			$('#exemAmt'+key).html(formatNumber(value.exempted));
			totExemAmt+=parseFloat(value.exempted);					
			totTcsAmt+=parseFloat(value.tcsamount);
		}else{
			$('#salesItcAmount'+key).html(formatNumber(value.itc));
			$('#tcsAmt'+key).html(formatNumber(value.ptcsamount));
			$('#tdsAmt'+key).html(formatNumber(value.tdsamount));
			totItcAmt+=parseFloat(value.itc);
			totTdsAmt+=parseFloat(value.tdsamount);
			ptotTcsAmt+=parseFloat(value.ptcsamount);
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
		$('#ytotal_Tcsvalue').html(formatNumber(ptotTcsAmt.toFixed(2)));
		$('#ytotal_Tdsvalue').html(formatNumber(totTdsAmt.toFixed(2)));
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
function getval(sel) {
	if(sel){
		document.getElementById('filingoption').innerHTML = sel;
		if (sel == 'Custom') {
			$('#group_and_client').css("right","53%");
			$('.monthely-sp').css("display", "none");
			$('.yearly-sp').css("display", "none");
			$('.custom-sp').css("display", "inline-block");
			$('.dropdown-menu.ret-type').css("left", "16%");
		} else if (sel == 'Yearly') {
			$('.monthely-sp').css("display", "none");
			$('.yearly-sp').css("display", "inline-block");
			$('.custom-sp').css("display", "none");
			$('.dropdown-menu.ret-type').css("left", "19%");
		} else {
			$('#group_and_client').css("right","47%");
			$('.monthely-sp').css("display", "inline-block");
			$('.yearly-sp').css("display", "none");
			$('.custom-sp').css("display", "none");
			$('.dropdown-menu.ret-type').css("left", "19%"); 
		}
	}
}
function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;document.getElementById('yearlyoption1').innerHTML=value;document.getElementById('yearlyoption2').innerHTML=value;
}
function loadDefaultDataTable(varRetType,varRetTypeCode){
	invTableReports[varRetTypeCode] = $('#gloablReports_dataTable'+varRetTypeCode).DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
	});
}


function applyAdminInvFilters(retType) {
	var retTypeCode = retType.replace(" ", "_");
	var typeOptions = $('#multiselect'+retTypeCode+'1 option:selected');
	var userOptions = $('#multiselect'+retTypeCode+'2 option:selected');
	var vendorOptions = $('#multiselect'+retTypeCode+'3 option:selected');
	var branchOptions = $('#multiselect'+retTypeCode+'4 option:selected');
	var verticalOptions = $('#multiselect'+retTypeCode+'5 option:selected');
	var gstr2aFilingOptions = $('#multiselect'+retTypeCode+'6 option:selected');
	var custOptions = $('#multiselect'+retTypeCode+'7 option:selected');
	if(typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0 || gstr2aFilingOptions.length > 0 || custOptions.length > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var branchArr=new Array();
	var verticalArr=new Array();
	var gstr2aFilingArr=new Array();
	var custArr=new Array();
	var filterContent='';
	
	if(typeOptions.length > 0) {
		for(var i=0;i<typeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+typeOptions[i].text+'<span data-val="'+typeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			typeArr.push(typeOptions[i].value);
		}
	} else {
		//typeArr.push('All');
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
	} else {
		//userArr.push('All');
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
	} else {
		//vendorArr.push('All');
	}
	if(branchOptions.length > 0) {
		
		for(var i=0;i<branchOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+branchOptions[i].value+'<span data-val="'+branchOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			branchArr.push(branchOptions[i].value);
		}
	} else {
		//branchArr.push('All');
	}
	if(verticalOptions.length > 0) {
		for(var i=0;i<verticalOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+verticalOptions[i].value+'<span data-val="'+verticalOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			verticalArr.push(verticalOptions[i].value);
		}
	} else {
		//verticalArr.push('All');
	}
	if(gstr2aFilingOptions.length > 0) {
		for(var i=0;i<gstr2aFilingOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+gstr2aFilingOptions[i].text+'<span data-val="'+gstr2aFilingOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			gstr2aFilingArr.push(gstr2aFilingOptions[i].value);
		}
	} else {
		//verticalArr.push('All');
	}
  if(custOptions.length > 0){
		for(var i=0;i<custOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+custOptions[i].value+'<span data-val="'+custOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			var customValue = custOptions[i].value;
			if(customValue.includes("&")){
				customValue = customValue.replace("&","-mgst-");
			}
			custArr.push(customValue);
		}
	}
	$('#divFilters'+retTypeCode).html(filterContent);
	reloadInvTable(retTypeCode, typeArr,userArr,vendorArr,branchArr,verticalArr,gstr2aFilingArr,custArr);
	var type = $('#fillingoption span').html();
	if(type == 'Yearly' || type == 'Custom' || type == 'custom'){
		reloadInvSummaryTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr, "",gstr2aFilingArr,custArr);		
	}else{}
	//invoiceTable[retTypeCode].draw();
}