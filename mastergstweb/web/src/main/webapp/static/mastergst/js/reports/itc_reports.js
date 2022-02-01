var invTableReportsUrl = new Object();
var invTableReports = new Object();
function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;
}
$(function(){
	$('input.btaginput').tagsinput({tagClass : 'big',});
	$(document).on('click', '.btn-remove-tag', function() {$('.bootstrap-tagsinput').html('');});
	$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
		$('.bootstrap-tagsinput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
	});
	$( ".helpicon" ).hover(function() {$('.reportunclaim').show();
	}, function() {$('.reportunclaim').hide();
	});
	var date = new Date();
	var month = date.getMonth()+1;
	var	year = date.getFullYear();
	var day = date.getDate();
	var mnt = date.getMonth()+1;
	var yr = date.getFullYear();
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
}
function loadItcInvoices(id, clientid, varRetType, varRetTypeCode, month, year, itcType, type){
	var pUrl = _getContextPath()+'/getitcinvs/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType;
	var cUrl = _getContextPath()+'/getitcinvsdwnld/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType+'&dwnldxlstype=invoicewise';
	var dwnldurl_invoicewise = _getContextPath()+'/getitcinvsdwnld/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType+'&dwnldxlstype=invoicewise';
	var dwnldurl_itemwise = _getContextPath()+'/getitcinvsdwnld/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType+'&dwnldxlstype=itemwise';
	if(itcType == "itc_claimed"){
		dwnldurl_invoicewise = _getContextPath()+'/getitcinvsdwnld/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType+'&dwnldxlstype=invoicewise';
		dwnldurl_itemwise = _getContextPath()+'/getitcinvsdwnld/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType+'&dwnldxlstype=itemwise';
	}
	
	if(type == 'custom'){
		//month eq fromtime and  year eq totime
		pUrl = _getContextPath()+'/getitcCustomInvs/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType;
		cUrl = _getContextPath()+'/getitcCustominvsdwnld/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType+'&dwnldxlstype=invoicewise';
		if(itcType == "itc_claimed"){
			dwnldurl_invoicewise = _getContextPath()+'/getitcCustominvsdwnld/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType+'&dwnldxlstype=invoicewise';
			dwnldurl_itemwise = _getContextPath()+'/getitcCustominvsdwnld/'+id+'/'+clientid+'/'+varRetType+'/'+month+'/'+year+'?itcinvtype='+itcType+'&dwnldxlstype=itemwise';
		}
	}
	invTableReportsUrl[varRetTypeCode] = pUrl;
	if(invTableReports[varRetTypeCode]){
		invTableReports[varRetTypeCode].clear();
		invTableReports[varRetTypeCode].destroy();
	}
	invTableReports[varRetTypeCode] = $('#dbItcInvoiceTable').DataTable({
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
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 loadItcSummary(varRetTypeCode, resp.invoicesAmount);
	        	 //$(".reportsdbTable div.toolbar").html('').append('<div class="dropdown pull-right permissionExcel_Download_In_Reports-Sales mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="monthlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="monthlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="" >DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="iditemUrl" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="idinvoiceUrl" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="idfullUrl" href="'+dwnldurl_full+'">ALL DETAILS DOWNLOAD</a></div></div></div></div>');
	        	 if(itcType == "itc_claimed"){
	        		 $(".reportsdbTable div.toolbar").html('').append('<div class="dropdown pull-right permissionExcel_Download_In_Reports-Sales mr-2"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="monthlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="monthlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="" >DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="iditemUrl" href="'+dwnldurl_itemwise+'">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="idinvoiceUrl" href="'+dwnldurl_invoicewise+'">INVOICE WISE DOWNLOAD</a></div></div></div></div>');
	        	 }else{
	        		 $(".reportsdbTable div.toolbar").html('').append('<a href="'+cUrl+'" id="monthlydwnldxls" class="btn btn-blue mr-2">Download to Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
	        	 }
	        	 //rolesPermissions();
	        	 return resp.invoices.content ;
	         }
	     },
		"paging": true,
		"pageLength":10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[6,'desc']],
		"columns": getItcInvColumns(id, clientId, varRetType, month, year, itcType),
		"columnDefs":getInvColumnDefs(varRetType)
	});
}
function getItcInvColumns(id, clientid, varRetType, month, year, itcType){
	var varRetTypeCode = varRetType.replace(" ", "_");
	var itype = {data: function ( data, type, row ) {
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
						}
				var cfs = '';
				if(invtype == 'B2B'){
				  		if(data.b2b && data.b2b.length > 0){
								if(data.b2b[0].cfs != undefined){
										cfs = data.b2b[0].cfs;
								}
						}
				}else if(invtype == 'Credit/Debit Notes'){
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
						if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)'){
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
						if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)'){
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
					if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)'){
				   	 	return '<span class="ind_formats text-right invoiceclk ">-'+formatNumber(totalAmount.toFixed(2))+'</span>';
					}else{
						return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalAmount.toFixed(2))+'</span>';
					}
				    }};
	
	var totalitc = {data: function ( data, type, row ) {
			var totlitc = 0.0;
			if(data.totalitc){
				totlitc = data.totalitc;
			}
			if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)'){
		   	 	return '<span class="ind_formats text-right invoiceclk ">-'+formatNumber(totlitc.toFixed(2))+'</span>';
			}else{
				return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totlitc.toFixed(2))+'</span>';
			}
		    }};	
	if(itcType == "itc_claimed"){
		var itcClaimedDate = {data: function ( data, type, row ) {
			var dateofitcClaimed = "";
			if(data.dateofitcClaimed != null){
						var itcDate = new Date(data.dateofitcClaimed);
						var day = itcDate.getDate() + "";var month = (itcDate.getMonth() + 1) + "";	var year = itcDate.getFullYear() + "";
						day = checkZero(day);month = checkZero(month);year = checkZero(year);
						dateofitcClaimed = day + "/" + month + "/" + year;
					}else{dateofitcClaimed = "";}
			      	 return '<span class="text-left invoiceclk ">'+(new Date(data.dateofitcClaimed)).toLocaleDateString("en-GB")+'</span>';
			    }};
		return [itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt, totalitc, itcClaimedDate];
	}else{
		return [itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt, totalitc];
	}
}
function getInvColumnDefs(varRetType, itcType){
	if(itcType == "itc_claimed"){
		return [
			{
				"targets": [5,6,7,8],
				className: 'dt-body-right'
			},
			{
				"targets": [9],
				className: 'dt-body-center'
			},
			{
				"targets": 0,
				"orderable": false
			}
		];
	}else{
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
}
function loadItcSummary(varRetType ,totalsData){
	$('#idTotalTransactions').html(totalsData ? totalsData.totalTransactions : '0');
	$('#idTotalTaxableValue').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotalTaxValue').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotalAmount').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotalItcClaimed').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
}
function formatNumber(nStr) {
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	var z = 0;
	var len = String(x1).length;
	var num = parseInt((len/2)-1);
	while (rgx.test(x1)){
		if(z > 0){
			x1 = x1.replace(rgx, '$1' + ',' + '$2');
		}else{
			x1 = x1.replace(rgx, '$1' + ',' + '$2');
			rgx = /(\d+)(\d{2})/;
		}
		z++;
		num--;
		if(num == 0){
			break;
		}
	}
	if(nStr == '0'){
		return '0.00'
	}
	return x1 + x2.substring(x2.indexOf('.'),3);
}