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


function loadGlobalReportsInvoiceSupport(clientId, varRetType, varRetTypeCode, month, year, type,booksorReturns, callback){
	var urlStr = _getContextPath()+'/getAddtionalGlobalReportsInvsSupport/'+varRetType+'/'+month+'/'+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;
	if(type == 'Custom'){
		//month eq fromtime and  year eq totime
		urlStr = _getContextPath()+'/getGlobalReportsCustomAddtionalInvsSupport/'+varRetType+'/'+month+'/'+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;
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

function loadGlobalReportsCustomersInDropdown(response, varRetType, varRetTypeCode){
	$('#multiselect'+varRetTypeCode+'3').multiselect('destroy');
	var vendorMultiSelObj = $('#multiselect'+varRetTypeCode+'3');
	vendorMultiSelObj.find('option').remove().end();
	if (response.billToNames && response.billToNames.length > 0) {		
		response.billToNames.forEach(function(customer) {
			if(customer != ''){
				vendorMultiSelObj.append($("<option></option>").attr("value",customer).text(customer));
			} 
		});
	}
	multiselrefresh('#multiselect'+varRetTypeCode+'3', '- Customers -' , varRetType);
	
	/*var custMultiSelObj = $('#multiselect'+varRetTypeCode+'5');
	custMultiSelObj.find('option').remove().end();
	if (response.customFields && response.customFields.length > 0) {				
		response.customFields.forEach(function(field) {
			if(field != ''){
				custMultiSelObj.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	multiselrefresh('#multiselect'+varRetTypeCode+'5', '- Custom Fields -' , varRetType);
	$('#multiselect'+varRetTypeCode+'5').multiselect('rebuild');*/
}
function initializeRemoveAppliedFiltersGlobalReports(varRetType, varRetTypeCode){
	$('#divFilters'+varRetTypeCode).on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i=1;i<6;i++){
			$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
			continue;
		}
		applyAdminInvFilters(varRetType);
	});
}
function initiateCallBacksForMultiSelectGlobalReports(varRetType, varRetTypeCode){
	var multiSelDefaultVals = ['', '- IRN Status -','- Invoice Type -', '', '- Reverse Charge -',''];
	for(i=1;i<6;i++){
		if(i == 3 || i == 5){
			continue;
		}
		multiselrefresh('#multiselect'+varRetTypeCode+i, multiSelDefaultVals[i], varRetType);
	}

}
function multiselrefresh(idval, descVal, varRetType){
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
	$('#idCount'+varRetType).html(totalsData[0] ? totalsData[0].totalTransactions : '0');
	$('#idTaxableVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTaxVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotAmtVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idIGST'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalIGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCGST'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalCGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idSGST'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalSGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCESS'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalCESSAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	if('GSTR1' == varRetType){
		$('#idExemptedVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalExemptedAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');		
	}else{
		$('#idITCVal'+varRetType).html(totalsData[0] ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData[0].totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
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
	}else{
		$('#idITCVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	}
}
function invsDownloads(id, clientIds, varRetType,varRetTypeCode, month, year, userType, fullname,booksOrReturns, type){
	if(type == 'Yearly'){
		var yr=parseInt(year)//-1;
		dwnldurl_invoicewise = _getContextPath()+"/dwnldxlsyearlyAdminGroupn/EINVOICE/"+yr+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		dwnldurl_itemwise = _getContextPath()+"/dwnldxlsyearlyAdminGroupn/EINVOICE/"+yr+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		dwnldurl_full = _getContextPath()+"/dwnldxlsyearlyAdminGroupn/EINVOICE/"+yr+"/fulldownload?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
	}else if(type == 'Custom'){
		dwnldurl_invoicewise =_getContextPath()+"/dwnldxlscustomAdminGroupn/EINVOICE/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		dwnldurl_itemwise =_getContextPath()+"/dwnldxlscustomAdminGroupn/EINVOICE/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		dwnldurl_full =_getContextPath()+"/dwnldxlscustomAdminGroupn/EINVOICE/"+month+"/"+year+"/fulldownload?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
	}else{
		dwnldurl_invoicewise = _getContextPath()+"/dwnldxlsmonthlyAdminGroupn/EINVOICE/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		dwnldurl_itemwise = _getContextPath()+"/dwnldxlsmonthlyAdminGroupn/EINVOICE/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
		dwnldurl_full = _getContextPath()+"/dwnldxlsmonthlyAdminGroupn/EINVOICE/"+month+"/"+year+"/fulldownload?booksOrReturns="+booksOrReturns+'&clientids='+clientIds;
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
function loadGlobalReportsInvTable(id, clientidsArray, varRetType,varRetTypeCode, month, year, userType, fullname,booksorReturns, type){
	var pUrl =_getContextPath()+'/getGlobalReportsAddtionalInvs/'+id+'/EINVOICE/'+month+'/'+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;	
	if(type == 'Custom'){
		//month eq fromtime and  year eq totime
		pUrl=_getContextPath()+'/getGlobalReportsAddtionalCustomInvs/'+id+'/EINVOICE/'+month+'/'+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;
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
		'columnDefs':getInvColumnDefs(varRetType)
	});
}

function getInvColumns(id, clientId, varRetType, userType, month, year, booksOrReturns){
	var billedtoname = '';
	var varRetTypeCode = varRetType.replace(" ", "_");
			
				var irnNo = {data: function ( data, type, row ) {
					if(data.irnNo){
						return '<span class="text-left invoiceclk" data-toggle="tooltip" title="'+data.irnNo+'">'+data.irnNo.substring(0,20)+' <span>...</span></span>';
					}else{
						return '<span class="text-left invoiceclk"></span>';
					}
				}};
			
				var status = {data: function ( data, type, row ) {
					var status = (data.irnStatus == null || data.irnStatus == "") ? "Not Generated" : data.irnStatus;
					var statuscolor  = (status == 'Generated') ? 'color-green' : 'color-red';
						return '<span class="text-left invoiceclk '+statuscolor+'">'+status+'</span>';
				}};
				
				var itype = {data: function ( data, type, row ) {
					     	if(data.revchargetype == 'Reverse'){
					     		return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+data.invtype+'</span><span><img data-toggle="tooltip" title="Reverse Charge Applied on this Invoice" src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/reversecharge.png" alt="reversecharge" style="height: 18px;margin-left: 10px;margin-bottom:3px"></span>';
					     	}else{
					     		return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+data.invtype+'</span>';
					     	}			              	 
					    }};
				var invsNo = {data:  function ( data, type, row ) {
					var invoiceno = '';
					if(data.invoiceno != undefined){
						invoiceno = data.invoiceno;
					}
			      	 return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+invoiceno+'</span>';
			    }};
				var billtoname = {data: function ( data, type, row ) {
						
						if(data.billedtoname != undefined){
							billedtoname = data.billedtoname;
						}
				      	 return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+billedtoname+'</span>';
				    }};
				var billtogtnn = {data: function ( data, type, row ) {
					var gstnnum= '';
					if(data.b2b && data.b2b.length > 0){
						if(data.b2b[0].ctin != undefined){
							gstnnum = data.b2b[0].ctin;
						}
					}
			      	 return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+gstnnum+'</span>';
			    }};
	
					var invDate = {data: function ( data, type, row ) {
				      	 return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+(new Date(data.dateofinvoice)).toLocaleDateString('en-GB')+'</span>';
				    }};
					var taxblamt = {data: function ( data, type, row ) {
					var totalTaxableAmt = 0.0;
					if(data.totaltaxableamount){
						totalTaxableAmt = data.totaltaxableamount;
					}
				   	 return '<span class="ind_formats text-right invoiceclk permissionInvoices-Sales-Edits">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
				    }};
					var totlTax = {data: function ( data, type, row ) {
					var totalTax = 0.0;
					if(data.totaltax){
						totalTax = data.totaltax;
					}
				   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk permissionInvoices-Sales-Edits">'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
				    }};
				var totalamt = {data: function ( data, type, row ) {
				var totalAmount = 0.0;
				if(data.totalamount){
					totalAmount = data.totalamount;
				}
			   	 return '<span class="ind_formats text-right invoiceclk permissionInvoices-Sales-Edits">'+formatNumber(totalAmount.toFixed(2))+'</span>';
			    }};
			return [irnNo, status, itype, invsNo,invDate,  billtogtnn,billtoname,taxblamt,  totlTax, totalamt];
	return [];
}
function getInvColumnDefs(varRetType){
	return  [
		{
			"targets":  [7, 8, 9],
			className: 'dt-body-right'
		}
	];

return [];
}


function clearFilters(retType, booksOrReturns) {
	for(i=1;i<6;i++){
		/*if(i == 3 || i == 5){
			$('#multiselect'+retType+i).hide();
			continue;
		}*/

	$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
}
$('#divFilters'+retType.replace(" ", "_")).html('');
$('.normaltable .filter').css("display","none");
var retTypeCode = retType.replace(" ", "_");
reloadInvTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array());
var type = $('#fillingoption span').html();
if(type == 'Yearly' || type == 'Custom' || type == 'custom'){
	reloadInvSummaryTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),'',new Array());		
}else{}
}
function reloadInvTable(retTypeCode, statusArr, invtypeArr,vendorArr,reverseChargeArr,custArr){
	var pUrl = invTableReportsUrl[retTypeCode];
	var appd = '';
	if(statusArr.length > 0){
		appd+='&irnStatus='+statusArr.join(',');
	}
	if(invtypeArr.length > 0){
		appd+='&invoiceType='+invtypeArr.join(',');
	}
	if(vendorArr.length > 0){
		appd+='&vendor='+vendorArr.join(',');
	}
	if(reverseChargeArr.length > 0){
		appd+='&reverseCharge='+reverseChargeArr.join(',');
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


function applyAdminInvFilters(retType) {
	var retTypeCode = retType.replace(" ", "_");
	var statusOptions = $('#multiselectGSTR11 option:selected');
	var typeOptions = $('#multiselectGSTR12 option:selected');
	var vendorOptions = $('#multiselectGSTR13 option:selected');
	var reverseOptions = $('#multiselectGSTR14 option:selected');
	var custOptions = $('#multiselectGSTR15 option:selected');
	if(statusOptions.length > 0 || typeOptions.length > 0 || vendorOptions.length > 0 || reverseOptions.length > 0 || custOptions.length > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	var statusArr=new Array();
	var typeArr=new Array();
	var vendorArr=new Array();
	var reverseArr=new Array();
	var custArr=new Array();
	var filterContent='';
	
	if(statusOptions.length > 0) {
		for(var i=0;i<statusOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+statusOptions[i].text+'<span data-val="'+statusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			statusArr.push(statusOptions[i].value);
		}
	} else {
		//typeArr.push('All');
	}
	if(typeOptions.length > 0) {
		for(var i=0;i<typeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+typeOptions[i].value+'<span data-val="'+typeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			typeArr.push(typeOptions[i].value);
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
	if(reverseOptions.length > 0) {
		
		for(var i=0;i<reverseOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+reverseOptions[i].value+'<span data-val="'+reverseOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			reverseArr.push(reverseOptions[i].value);
		}
	} else {
		//branchArr.push('All');
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
	reloadInvTable(retTypeCode, statusArr,typeArr,vendorArr,reverseArr,custArr);
	var type = $('#fillingoption span').html();
	if(type == 'Yearly' || type == 'Custom' || type == 'custom'){
		reloadInvSummaryTable(retTypeCode, statusArr, typeArr,vendorArr,reverseArr, "",custArr);		
	}else{}
	//invoiceTable[retTypeCode].draw();
}
function reloadInvSummaryTable(retTypeCode,statusArr, typeArr,vendorArr,reverseArr, booksOrReturns,custArr){
	var pUrl = invSummaryTableReportsUrl[retTypeCode];
	var dUrl = invTableSummaryDownloadUrl[retTypeCode];
	var appd = '';
	if(statusArr.length > 0){
		appd+='&irnStatus='+statusArr.join(',');
	}
	if(typeArr.length > 0){
		appd+='&invoiceType='+typeArr.join(',');
	}
	if(vendorArr.length > 0){
		appd+='&vendor='+vendorArr.join(',');
	}
	if(reverseArr.length > 0){
		appd+='&reverseCharge='+reverseArr.join(',');
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
	var urlStr ="";
			urlStr =_getContextPath()+'/globalReportsTotalSummary/EINVOICE/'+month+"/"+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;
			var dwnUrl =_getContextPath()+'/dwnldGlobalReportsFinancialSummaryxls/'+id+'/'+varRetType+'?booksorReturns='+booksorReturns+'&year='+yr+'&clientids='+clientidsArray+'&fromdate=null&todate=null';
			if(type == 'Custom'){
				urlStr=_getContextPath()+'/globalReportsCustomTotalSummary/EINVOICE/'+month+"/"+year+'?booksorReturns='+booksorReturns+'&clientids='+clientidsArray;
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
		$('#salesigstAmount'+key).html(formatNumber(value.igst));
		$('#salescgstAmount'+key).html(formatNumber(value.cgst));
		$('#salessgstAmount'+key).html(formatNumber(value.sgst));
		$('#salescessAmount'+key).html(formatNumber(value.cess));
		$('#tcsAmt'+key).html(formatNumber(value.tdsamount));
		$('#tcsAmt'+key).html(formatNumber(value.tcsamount));
		if(varRetType == 'GSTR1'){
			$('#exemAmt'+key).html(formatNumber(value.exempted));
			totExemAmt+=parseInt(value.exempted);					
			totTcsAmt+=parseInt(value.tcsamount);
		}else{
			$('#salesItcAmount'+key).html(formatNumber(value.itc));
			totItcAmt+=parseInt(value.itc);
			totTdsAmt+=parseInt(value.tdsamount);
		}
		totinvs+=parseInt(value.totalTransactions);
		totTaxableVal+=parseInt(value.Sales);
		totTaxAmt+=parseInt(value.Tax);
		totSales+=parseInt(value.totalamt);
		totIgst+=parseInt(value.igst);
		totCgst+=parseInt(value.cgst);
		totSgst+=parseInt(value.sgst);
		totCess+=parseInt(value.cess);
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
			//$('#group_and_client').css("right","47%");
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