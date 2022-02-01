var invTableReportsUrl = new Object();
var invSummaryTableReportsUrl = new Object();
var invTableDwnld_invoiceUrl = new Object();
var invTableDwnld_itemUrl = new Object();
var invTableDwnld_fullUrl = new Object();
var invTableSummaryDownloadUrl = new Object();
var invTableReports = new Object();
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

function getval(sel) {
	document.getElementById('filing_option').innerHTML = sel;
	$('#processing').css('top','10%');
	if (sel == 'Custom') {
		$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type-trail').css("left", "16%");
	} else if (sel == 'Yearly') {
		$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-trail').css("left", "19%");
	} else {
		$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-trail').css("left", "19%");
	}
};
function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;
}
function loadReportsUsersByClient(id, clientId, varRetType, varRetTypeCode, callback){
	var urlStr = _getContextPath()+'/cp_users/'+id+'/'+clientId
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
function loadReportsInvoiceSupport(clientId, varRetType, varRetTypeCode, month, year, type, callback){
	var urlStr = _getContextPath()+'/getAddtionalInvsSupport/'+clientId+'/'+varRetType+'/'+month+'/'+year;
	if(type == 'custom'){
		//month eq fromtime and  year eq totime
		urlStr = _getContextPath()+'/getCustomAddtionalInvsSupport/'+clientId+'/'+varRetType+'/'+month+'/'+year;
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
function loadReportsUsersInDropDown(response, varRetType, varRetTypeCode){
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
function loadReportsCustomersInDropdown(response, varRetType, varRetTypeCode){
	var usersMultiSelObj = $('#multiselect'+varRetTypeCode+'3');
	var custMultiSelObj = $('#multiselect'+varRetTypeCode+'6');
	var custMultiSelObj1 = $('#multiselect'+varRetTypeCode+'7');
  	var custMultiSelObj2 = $('#multiselect'+varRetTypeCode+'8');
  	var custMultiSelObj3 = $('#multiselect'+varRetTypeCode+'9');
	usersMultiSelObj.find('option').remove().end();
	custMultiSelObj.find('option').remove().end();
	custMultiSelObj1.find('option').remove().end();
	custMultiSelObj2.find('option').remove().end();
	custMultiSelObj3.find('option').remove().end();
	if (response.billToNames.length > 0) {				
		response.billToNames.forEach(function(customer) {
			if(customer != ''){
				usersMultiSelObj.append($("<option></option>").attr("value",customer).text(customer));
			} 
		});
	}
	
	if(response.customField1 != null && response.customField1.length > 0) {				
		response.customField1.forEach(function(field) {
			if(field != ''){
				custMultiSelObj.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	if(response.customField2 != null && response.customField2.length > 0) {				
		response.customField2.forEach(function(field) {
			if(field != ''){
				custMultiSelObj1.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	if(response.customField3 != null && response.customField3.length > 0) {				
		response.customField3.forEach(function(field) {
			if(field != ''){
				custMultiSelObj2.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	if(response.customField4 != null && response.customField4.length > 0) {				
		response.customField4.forEach(function(field) {
			if(field != ''){
				custMultiSelObj3.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	
	if(response.customField1 != null){
		var cF1 = '- Custom Field1 -';
		if(response.customFieldName1 != null){
			cF1 = response.customFieldName1;
		}
		$('#multiselect'+varRetTypeCode+'6').css("display","block");
		multiselrefresh_reports('#multiselect'+varRetTypeCode+'6', cF1, varRetType);
    	$('#multiselect'+varRetTypeCode+'6').multiselect('rebuild');
	}else{
		$('#multiselect'+varRetTypeCode+'6').css("display","none");
	}
	if(response.customField2 != null){
		var cF2 = '- Custom Field2 -';
		if(response.customFieldName2 != null){
			cF2 = response.customFieldName2;
		}
		$('#multiselect'+varRetTypeCode+'7').css("display","block");
		multiselrefresh_reports('#multiselect'+varRetTypeCode+'7', cF2, varRetType);
    	$('#multiselect'+varRetTypeCode+'7').multiselect('rebuild');
	}else{
		$('#multiselect'+varRetTypeCode+'7').css("display","none");
	}
	if(response.customField3 != null){
		var cF3 = '- Custom Field3 -';
		if(response.customFieldName3 != null){
			cF3 = response.customFieldName3;
		}
		$('#multiselect'+varRetTypeCode+'8').css("display","block");
		multiselrefresh_reports('#multiselect'+varRetTypeCode+'8', cF3, varRetType);
    	$('#multiselect'+varRetTypeCode+'8').multiselect('rebuild');
	}else{
		$('#multiselect'+varRetTypeCode+'8').css("display","none");
	}
	if(response.customField4 != null){
		var cF4 = '- Custom Field4 -';
		if(response.customFieldName4 != null){
			cF4 = response.customFieldName4;
		}
		$('#multiselect'+varRetTypeCode+'9').css("display","block");
		multiselrefresh_reports('#multiselect'+varRetTypeCode+'9', cF4, varRetType);
    	$('#multiselect'+varRetTypeCode+'9').multiselect('rebuild');
	}else{
		$('#multiselect'+varRetTypeCode+'9').css("display","none");
	}
	var customerType ='- Customers -';
	if(varRetType == 'Purchase Register'){
		customerType ='- Suppliers -';
	}
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'3', customerType , varRetType);
	$('#multiselect'+varRetTypeCode+'3').multiselect('rebuild');
	
}
function initiateCallBacksForMultiSelectReports(varRetType, varRetTypeCode){
	var multiSelDefaultVals = ['','- Invoice Type -', '', '', '- Branches -', '- Verticals -',''];	
	for(i=1;i<=9;i++){
			if(i == 2 || i == 3 || i == 6|| i == 7|| i == 8|| i == 9){
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
			applyInvFilterss(varRetType);
		},
		onSelectAll: function(){
			applyInvFilterss(varRetType);
		},
		onDeselectAll: function(){
			applyInvFilterss(varRetType);
		}
	});
	//$(idval).multiselect('refresh');
}
function loadReportsTotals(varRetType, totalsData){
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
function invsDownloads(id, clientId, varRetType,varRetTypeCode, month, year, userType, fullname,booksOrReturns, type){
	var dwnldurl_invoicewise =_getContextPath()+"/dwnldxlscustom/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns;
	var dwnldurl_itemwise =_getContextPath()+"/dwnldxlscustom/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns;
	var dwnldurl_full =_getContextPath()+"/fulldwnldcustomxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"?booksOrReturns="+booksOrReturns;
	if(type == 'Yearly'){
		var yr=parseInt(year);//-1;
		dwnldurl_invoicewise = _getContextPath()+"/dwnldxlsyearly/"+id+"/"+clientId+"/"+varRetType+"/"+yr+"/invoicewise?booksOrReturns="+booksOrReturns;
		dwnldurl_itemwise = _getContextPath()+"/dwnldxlsyearly/"+id+"/"+clientId+"/"+varRetType+"/"+yr+"/itemwise?booksOrReturns="+booksOrReturns;
		dwnldurl_full = _getContextPath()+"/fulldwnldyearlyxls/"+id+"/"+clientId+"/"+varRetType+"/"+yr+"?booksOrReturns="+booksOrReturns;
	}else if(type == 'custom' || type == 'Custom'){
		dwnldurl_invoicewise =_getContextPath()+"/dwnldxlscustom/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns;
		dwnldurl_itemwise =_getContextPath()+"/dwnldxlscustom/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns;
		dwnldurl_full =_getContextPath()+"/fulldwnldcustomxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"?booksOrReturns="+booksOrReturns;
	}else{
		dwnldurl_invoicewise = _getContextPath()+"/dwnldxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/invoicewise?booksorReturns="+booksOrReturns;
		dwnldurl_itemwise = _getContextPath()+"/dwnldxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/itemwise?booksorReturns="+booksOrReturns;
		dwnldurl_full = _getContextPath()+"/fulldwnldmonthlyxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"?booksorReturns="+booksOrReturns;
	}
	
	invTableDwnld_invoiceUrl[varRetTypeCode] = dwnldurl_invoicewise;
	invTableDwnld_itemUrl[varRetTypeCode] = dwnldurl_itemwise;
	invTableDwnld_fullUrl[varRetTypeCode] = dwnldurl_full;
	
	 if(varRetTypeCode == 'GSTR1'){
		 $(".reportsdbTable"+varRetTypeCode+" div.toolbar").html('').append('<div class="dropdown pull-right permissionExcel_Download_In_Reports-Sales mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="monthlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="monthlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="" >DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="iditemUrl" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="idinvoiceUrl" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="idfullUrl" href="'+dwnldurl_full+'">ALL DETAILS DOWNLOAD</a></div></div></div></div>');	        		 
	 }else{
		 $(".reportsdbTable"+varRetTypeCode+" div.toolbar").html('').append('<div class="dropdown pull-right permissionExcel_Download_In_Reports-Purchases mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="monthlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="monthlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="" >DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="iditemUrl" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="idinvoiceUrl" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="idfullUrl" href="'+dwnldurl_full+'">ALL DETAILS DOWNLOAD</a></div></div></div></div>');
	 }
	rolesPermissions();
}
function loadReportsInvTable(id, clientId, varRetType,varRetTypeCode, month, year, userType, fullname,booksOrReturns, type){
	
	var pUrl =_getContextPath()+'/getAddtionalInvs/'+id+'/'+clientId+'/'+varRetType+'/'+month+'/'+year+'?booksOrReturns='+booksOrReturns+'&reportType=reports';
	if(type == 'custom'){
		//month eq fromtime and  year eq totime
		pUrl=_getContextPath()+'/getAddtionalCustomInvs/'+id+'/'+clientId+'/'+varRetType+'/'+month+'/'+year+'?booksOrReturns='+booksOrReturns;
	}
	invTableReportsUrl[varRetTypeCode] = pUrl;
	if(invTableReports[varRetTypeCode]){
		invTableReports[varRetTypeCode].clear();
		invTableReports[varRetTypeCode].destroy();
	}
	invTableReports[varRetTypeCode] = $('#reports_dataTable'+varRetTypeCode).DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 $('#check'+varRetTypeCode.replace(" ", "_")).prop('checked', false);
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 loadReportsTotals(varRetTypeCode, resp.invoicesAmount);
	        	 $('#updatefilter_summary_reports').show();
	        	/* if(varRetTypeCode == 'GSTR1'){
	        		 $(".reportsdbTable"+varRetTypeCode+" div.toolbar").html('').append('<div class="dropdown pull-right permissionExcel_Download_In_Reports-Sales mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="monthlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="monthlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="" >DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="'+dwnldurl_full+'">ALL DETAILS DOWNLOAD</a></div></div></div></div>');	        		 
	        	 }else{
	        		 $(".reportsdbTable"+varRetTypeCode+" div.toolbar").html('').append('<div class="dropdown pull-right permissionExcel_Download_In_Reports-Purchases mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="monthlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="monthlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="" >DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="'+dwnldurl_full+'">ALL DETAILS DOWNLOAD</a></div></div></div></div>');
	        	 }*/
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
		'columns': getInvColumns(id, clientId, varRetType, userType, month, year, booksOrReturns),
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
	if(varRetType == 'Purchase Register'){
		var totalitc = {data: function ( data, type, row ) {
			var totlitc = 0.0;
			if(data.totalitc){
				totlitc = data.totalitc;
			}
			if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)' || data.invtype == "Advance Adjusted Detail"){
		   	 	return '<span class="ind_formats text-right invoiceclk ">-'+formatNumber(totlitc.toFixed(2))+'</span>';
			}else{
				return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totlitc.toFixed(2))+'</span>';
			}
		    }};
		return [itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt, totalitc];
	}else{
		return [itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt];		
	}
}
function getInvColumnDefs(varRetType){
	if(varRetType == 'GSTR1'){
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
	}else if(varRetType == 'GSTR2A'){
		return [
				{
				"targets":  [5,6,7],
				className: 'dt-body-right'
				}
		];
	}
	return [];
}
function initializeRemoveAppliedFiltersReports(varRetType, varRetTypeCode){
	$('#divFilters'+varRetTypeCode).on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i=1;i<=9;i++){
			if(i == 6  || i == 7  || i == 8  || i == 9){
				if(($('#multiselect'+varRetTypeCode+i).css('display') != 'none')){
					$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
				}
			}else{
				$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
			}
			continue;
		}
		applyInvFilterss(varRetType);
	});
}
function clearInvFiltersReports(retType, booksOrReturns) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=9;i++){
		if(i == 6  || i == 7  || i == 8  || i == 9){
			if(($('#multiselect'+retType+i).css('display') != 'none')){
				$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
			}
		}else{
			$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		}
	}
	$('#divFilters'+retType.replace(" ", "_")).html('');
	$('.normaltable .filter').css("display","none");
	var retTypeCode = retType.replace(" ", "_");
	reloadInvTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),undefined);
	var type = $('#fillingoption span').html();
	if(type == 'Yearly' || type == 'Custom' || type == 'custom'){
		reloadInvSummaryTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),undefined);		
	}else{}
}
function clearInvFiltersReportss(retType) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=9;i++){
		if(i == 6  || i == 7  || i == 8  || i == 9){
			if(($('#multiselect'+retType+i).css('display') != 'none')){
				$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
			}
		}else{
			$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		}
	}
	$('#divFilters'+retType.replace(" ", "_")).html('');
	$('.normaltable .filter').css("display","none");
}


function applyInvFilterss(retType) {
	var retTypeCode = retType.replace(" ", "_");
	var typeOptions = $('#multiselect'+retTypeCode+'1 option:selected');
	var userOptions = $('#multiselect'+retTypeCode+'2 option:selected');
	var vendorOptions = $('#multiselect'+retTypeCode+'3 option:selected');
	var branchOptions = $('#multiselect'+retTypeCode+'4 option:selected');
	var verticalOptions = $('#multiselect'+retTypeCode+'5 option:selected');
	var custOptions1 = $('#multiselect'+retTypeCode+'6 option:selected');
	var custOptions2 = $('#multiselect'+retTypeCode+'7 option:selected');
	var custOptions3 = $('#multiselect'+retTypeCode+'8 option:selected');
	var custOptions4 = $('#multiselect'+retTypeCode+'9 option:selected');
	if(typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0 || custOptions1.length > 0 || custOptions2.length > 0 || custOptions3.length > 0 || custOptions4.length > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var branchArr=new Array();
	var verticalArr=new Array();
	var custArr1=new Array();
	var custArr2=new Array();
	var custArr3=new Array();
	var custArr4=new Array();
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
	if(custOptions1.length > 0) {
		for(var i=0;i<custOptions1.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+custOptions1[i].value+'<span data-val="'+custOptions1[i].value+'" class="deltag" data-role="remove"></span></span>';
			var customValue = custOptions1[i].value;
			if(customValue.includes("&")){
				customValue = customValue.replace("&","-mgst-");
			}
			custArr1.push(customValue);
		}
	}
	if(custOptions2.length > 0) {
		for(var i=0;i<custOptions2.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+custOptions2[i].value+'<span data-val="'+custOptions2[i].value+'" class="deltag" data-role="remove"></span></span>';
			var customValue = custOptions2[i].value;
			if(customValue.includes("&")){
				customValue = customValue.replace("&","-mgst-");
			}
			custArr2.push(customValue);
		}
	}
	if(custOptions3.length > 0) {
		for(var i=0;i<custOptions3.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+custOptions3[i].value+'<span data-val="'+custOptions3[i].value+'" class="deltag" data-role="remove"></span></span>';
			var customValue = custOptions3[i].value;
			if(customValue.includes("&")){
				customValue = customValue.replace("&","-mgst-");
			}
			custArr3.push(customValue);
		}
	}
	if(custOptions2.length > 0) {
		for(var i=0;i<custOptions4.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+custOptions4[i].value+'<span data-val="'+custOptions4[i].value+'" class="deltag" data-role="remove"></span></span>';
			var customValue = custOptions4[i].value;
			if(customValue.includes("&")){
				customValue = customValue.replace("&","-mgst-");
			}
			custArr4.push(customValue);
		}
	}
	$('#divFilters'+retTypeCode).html(filterContent);
	
	reloadInvTable(retTypeCode,  typeArr, userArr,vendorArr,branchArr,verticalArr,custArr1,custArr2,custArr3,custArr4,undefined);
	var type = $('#fillingoption span').html();
	if(type == 'Yearly' || type == 'Custom' || type == 'custom'){
		reloadInvSummaryTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr,custArr1,custArr2,custArr3,custArr4, "");		
	}else{}
}


function reloadInvTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr, custArr1,custArr2,custArr3,custArr4,booksOrReturns){
	var pUrl = invTableReportsUrl[retTypeCode];
	var appd = 'booksorReturns='+booksOrReturns;
	
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
	if(custArr1.length > 0){
		appd+='&customFieldText1='+custArr1.join(',');
	}
	if(custArr2.length > 0){
		appd+='&customFieldText2='+custArr2.join(',');
	}
	if(custArr3.length > 0){
		appd+='&customFieldText3='+custArr3.join(',');
	}
	if(custArr4.length > 0){
		appd+='&customFieldText4='+custArr4.join(',');
	}
	pUrl += '&'+appd;
	
	invTableReports[retTypeCode].ajax.url(pUrl).load();
	var invoiceUrl = invTableDwnld_invoiceUrl[retTypeCode]+''+appd;
	var itemUrl = invTableDwnld_itemUrl[retTypeCode] + ''+appd;
	var fullUrl = invTableDwnld_fullUrl[retTypeCode] + ''+appd;
	$('#iditemUrl').attr('href',itemUrl);
	$('#idinvoiceUrl').attr('href',invoiceUrl);
	$('#idfullUrl').attr('href',fullUrl);
  }
function reloadInvSummaryTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr,custArr1,custArr2,custArr3,custArr4, booksOrReturns){
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
	if(custArr1.length > 0){
		appd+='&customFieldText1='+custArr1.join(',');
	}
	if(custArr2.length > 0){
		appd+='&customFieldText2='+custArr2.join(',');
	}
	if(custArr3.length > 0){
		appd+='&customFieldText3='+custArr3.join(',');
	}
	if(custArr4.length > 0){
		appd+='&customFieldText4='+custArr4.join(',');
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
function loadReportsSummary(id,varRetType, clientid, month, year, type){
	var varRetTypeCode = varRetType.replace(" ", "_");
	var urlStr =_getContextPath()+'/reportsTotalSummary/'+varRetType+'/'+clientid+'/'+month+"/"+year+"?booksorReturns=";
	var dwnUrl =_getContextPath()+'/dwnldReportsFinancialSummaryxls/'+id+'/'+clientid+'/'+varRetType+'?reporttype=invoice_report&year='+year+'&fromdate=null&todate=null';
	if(type == 'custom'){
		//month eq fromtime & year eq totime
		urlStr=_getContextPath()+'/reportsCustomTotalSummary/'+varRetType+'/'+clientid+'/'+month+"/"+year+"?booksorReturns=";
		dwnUrl =_getContextPath()+'/dwnldReportsFinancialSummaryxls/'+id+'/'+clientid+'/'+varRetType+'?reporttype=invoice_report&year=0&fromdate='+month+'&todate='+year;
	}
	
	invSummaryTableReportsUrl[varRetTypeCode] = urlStr;
	invTableSummaryDownloadUrl[varRetTypeCode] = dwnUrl;
	$('#dwnldxls').attr("href",dwnUrl);
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
	var totinvs=0, totTaxableVal=0.0, totTaxAmt=0.0, totExemAmt=0.0, totSales=0.0, totIgst=0.0, totCgst=0.0, totSgst=0.0, totCess=0.0, totTcsAmt=0.0,ptotTcsAmt=0.0, totTdsAmt=0.0, totItcAmt=0.0;
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
			
			if(varRetType == 'GSTR1'){
				$('#tcsAmt'+key).html(formatNumber(value.tcsamount));
				$('#exemAmt'+key).html(formatNumber(value.exempted));
				totExemAmt+=parseFloat(value.exempted);					
				totTcsAmt+=parseFloat(value.tcsamount);
			}else{
				$('#itcAmt'+key).html(formatNumber(value.itc));
				totItcAmt+=parseFloat(value.itc);
				totTdsAmt+=parseFloat(value.tdsamount);
				ptotTcsAmt+=parseFloat(value.ptcsamount);
				$('#tcsAmt'+key).html(formatNumber(value.ptcsamount));
				$('#tdsAmt'+key).html(formatNumber(value.tdsamount));
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
