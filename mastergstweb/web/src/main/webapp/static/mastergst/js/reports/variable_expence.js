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
	var date = $('#daily').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	});

	 $('.dpWeekly').datepicker({
		 format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		}).on('changeDate', function(ev) {
			var newdate1 = $(this).datepicker('getDate');
			var n1 = newdate1.getDate();
		    var d1 = newdate1.getMonth() + 1;
		    var y1 = newdate1.getFullYear();
			var newdate = $(this).datepicker('getDate');
			newdate.setDate(newdate.getDate() + 6);
		    var dd = newdate.getDate();
		    var mm = newdate.getMonth() + 1;
		    var y = newdate.getFullYear();
		    var someFormattedDate =  dd+ '-'+((''+mm).length<2 ? '0' : '') + mm + '-' + y;
		    var startDate =  n1+ '-'+((''+d1).length<2 ? '0' : '') + d1 + '-' + y1;
		    $('#week-val').val(startDate+"-To-"+someFormattedDate);
		    updateInput(startDate+"-To-"+someFormattedDate);
 });
	 function updateInput(ish){
		    document.getElementById("week-val").innerHTML = ish;
		}
	 $('.dpMonths').datepicker({
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

function changedval(sel){
	
	var selv = sel.value;
	$('#processing').css('top','10%');
	if (selv == 'Custom') {
		$('.monthely-sp').css("display", "none");$('.daily-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.weekly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");
		$('.dropdown-menu.ret-type-trail').css("left", "17%");
	} else if (selv == 'Yearly') {
		$('.monthely-sp').css("display", "none");$('.daily-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.weekly-sp').css("display", "none");$('.custom-sp').css("display", "none");
		$('.dropdown-menu.ret-type-trail').css("left", "19%");
	} else if (selv == 'Daily') {
		$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.weekly-sp').css("display", "none");$('.daily-sp').css("display", "inline-block");
		$('.dropdown-menu.ret-type-trail').css("left", "19%");
	} else if (selv == 'Weekly') {
		$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.weekly-sp').css("display", "inline-block");$('.daily-sp').css("display", "none");
		$('.dropdown-menu.ret-type-trail').css("left", "16%");
	} else {
		$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.weekly-sp').css("display", "none");$('.daily-sp').css("display", "none");$('.custom-sp').css("display", "none");
		$('.dropdown-menu.ret-type-trail').css("left", "19%");
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
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'2', '- Service Number/ Route ID -', varRetType);
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'3', '- Service Type -', varRetType);
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'4', '- Route -', varRetType);
	multiselrefresh_reports('#multiselect'+varRetTypeCode+'6', '- Bus number list -', varRetType);
	$('#multiselect'+varRetTypeCode+'2').multiselect('rebuild');
}
function initiateCallBacksForMultiSelectReports(varRetType, varRetTypeCode){
	var multiSelDefaultVals = ['','- Route List -', ''];	
	for(i=1;i<=5;i++){
		//if(varRetTypeCode == 'GSTR1' || ){
			if(i == 2 || i == 3){
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
	$('#idTCSTDS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.tcsTdsAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	if('GSTR1' == varRetType){
		$('#idExemptedVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalExemptedAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');		
	}else{
		$('#idITCVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
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
		dwnldurl_invoicewise = _getContextPath()+"/dwnldxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/invoicewise?booksOrReturns="+booksOrReturns;
		dwnldurl_itemwise = _getContextPath()+"/dwnldxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/itemwise?booksOrReturns="+booksOrReturns;
		dwnldurl_full = _getContextPath()+"/fulldwnldmonthlyxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"?booksOrReturns="+booksOrReturns;
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
	invTableReports[varRetTypeCode] = $('#reports_dataTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
	    "paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[6,'desc']]
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
						if(data.b2b && data.b2b.length > 0){
							if(data.b2b[0].ctin != undefined){
								gstnnum = data.b2b[0].ctin;
							}
						}else if(data.cdnr && data.cdnr.length > 0){
							if(data.cdnr[0].ctin != undefined){
								gstnnum = data.cdnr[0].ctin;
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
					   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
					    }};
	var totlTax = {data: function ( data, type, row ) {
						var totalTax = 0.0;
						if(data.totaltax){
							totalTax = data.totaltax;
						}
					   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
					    }};
	var totalamt = {data: function ( data, type, row ) {
					var totalAmount = 0.0;
					if(data.totalamount){
						totalAmount = data.totalamount;
					}
				   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalAmount.toFixed(2))+'</span>';
				    }};
	if(varRetType == 'Purchase Register'){
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
		for(i=1;i<5;i++){
			$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
			continue;
		}
		applyInvFilterss(varRetType);
	});
}
function clearInvFiltersReports(retType, booksOrReturns) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=5;i++){
		$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#divFilters'+retType.replace(" ", "_")).html('');
	$('.normaltable .filter').css("display","none");
	var retTypeCode = retType.replace(" ", "_");
	reloadInvTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array(),undefined);
	var type = $('#fillingoption span').html();
	if(type == 'Yearly' || type == 'Custom' || type == 'custom'){
		reloadInvSummaryTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array(),undefined);		
	}else{}
}
function clearInvFiltersReportss(retType) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=5;i++){
		$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
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
		$('#divFilters'+retTypeCode).html(filterContent);
	
	reloadInvTable(retTypeCode,  typeArr, userArr,vendorArr,branchArr,verticalArr,undefined);
	var type = $('#fillingoption span').html();
	if(type == 'Yearly' || type == 'Custom' || type == 'custom'){
		reloadInvSummaryTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr, "");		
	}else{}
}


function reloadInvTable(retTypeCode, typeArr, userArr,vendorArr,branchArr,verticalArr, booksOrReturns){
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
	pUrl += '&'+appd;
	
	invTableReports[retTypeCode].ajax.url(pUrl).load();
	var invoiceUrl = invTableDwnld_invoiceUrl[retTypeCode]+''+appd;
	var itemUrl = invTableDwnld_itemUrl[retTypeCode] + ''+appd;
	var fullUrl = invTableDwnld_fullUrl[retTypeCode] + ''+appd;
	$('#iditemUrl').attr('href',itemUrl);
	$('#idinvoiceUrl').attr('href',invoiceUrl);
	$('#idfullUrl').attr('href',fullUrl);
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
