<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST | Tax Slab Wise Sales Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<style>
.salestable{position:unset}button.dt-button.buttons-excel.buttons-html5{width: auto;}.dt-buttons{left: 59%;}
.dropdown:hover .dropdown-content.reporttaxSales{display: block;}.arrow-up {width: 0; height: 0; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reporttaxSales{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 600px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
</style>
<script type="text/javascript">
var invoiceArray=new Object();
	$('input.btaginput').tagsinput({tagClass : 'big',});
	$(document).on('click', '.btn-remove-tag', function() {$('.bootstrap-tagsinput').html('');});
	$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
		$('.bootstrap-tagsinput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
	});
	function getval(sel) {
		document.getElementById('filing_option').innerHTML = sel;document.getElementById('filing_option1').innerHTML = sel;document.getElementById('filing_option2').innerHTML = sel;
		if (sel == 'Custom') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type').css("left", "16%");
		} else if (sel == 'Yearly') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type').css("left", "19%");
		} else {
			$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type').css("left", "19%");
		}
	};
	function updateYearlyOption(value){
		document.getElementById('yearlyoption').innerHTML=value;document.getElementById('yearlyoption1').innerHTML=value;document.getElementById('yearlyoption2').innerHTML=value;
	}
	function getdiv() {
    	var clientname = '<c:out value="${client.businessname}"/>';var abc = $('#fillingoption span').html();
		$('#multiselectGSTR14,#multiselectcustomGSTR14,#multiselectyearGSTR14').empty();
		$('#divFiltersGSTR1').html('');
	if(abc == 'Monthly'){
		$('.monthely1').css("display", "block");$('.yearly1').css("display", "none");$('.custom1').css("display", "none");$('span#fillingoption').css("vertical-align", "middle");
		var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
		salesFileName = 'MGST_Sales_Monthly_'+gstnnumber+'_'+mn+yr;
		var dwnldurl = "${contextPath}/dwnldxls/${id}/${client.id}/GSTR1/"+mn+"/"+yr;
		$.ajax({
			url: "${contextPath}/getmonthlyinvs/${id}/${client.id}/GSTR1/"+mn+"/"+yr,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			beforeSend: function () {$('#monthProcess').text('Processing...');},
			success : function(invoiceList) {
				var content='';
				if(invoiceList instanceof Array) {
					if(invoiceList.length > 0) {
						
						if ( $.fn.DataTable.isDataTable('#dbHSNFilingTable') ) {$('#dbHSNFilingTable').DataTable().destroy();}
						$('#dbHSNFilingTable tbody').empty();
						var counts =0;var hsnitems = [];var i=1;
						invoiceList.forEach(function(invoice){
						if(invoice.items){
								invoice.items.forEach(function(item) {
									if(item.hsn != '' && item.hsn != null){
										if(item.hsn.indexOf(':') > 0) {
												item.hsns=item.hsn.substring(0,item.hsn.indexOf(':'));
												item.desc=item.hsn.substring(item.hsn.indexOf(':')+1);
										}else{
											item.hsns = item.hsn;
											item.desc = '';
										}
									}else{
										item.hsns = '';item.desc = '';
										}
								if(item.rateperitem == null){item.rateperitem = 0.00;}
								if(item.rate == null){
									if(item.igstrate != null){
										item.rate = item.igstrate;
									}else if(item.cgstrate != null && item.sgstrate != null){
										item.rate = item.cgstrate + item.sgstrate;
									}else{
										item.rate = 0.00;
									}
									}
								if(item.igstamount == null){item.igstamount = 0.00;}
								if(item.cgstamount == null){item.cgstamount = 0.00;}
								if(item.sgstamount == null){item.sgstamount = 0.00;}
								if(item.cessamount == null){item.cessamount = 0.00;}
								if(item.taxablevalue == null){item.taxablevalue = 0.00;}
								if(item.quantity == null){item.quantity = 0.00;}
								if(item.total == null){item.total = 0.00;}
										if(counts == 0){
									if(item.rate != ""){
										hsnitems[item.rate] = item;
									}
								}else{
								if(item.rate in hsnitems){
									var hhssnn = item.rate;
									var abc = hsnitems[hhssnn];
									item.quantity += abc.quantity;
									if((invoice.invtype == 'Credit/Debit Notes') || (invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers')){
										
										if(invoice.invtype == 'Credit/Debit Notes'){
											if(invoice.cdnr[0].nt[0].ntty == 'C'){
												item.total = abc.total-item.total;item.igstamount = abc.igstamount-item.igstamount;item.cgstamount = abc.cgstamount-item.cgstamount;item.sgstamount = abc.sgstamount-item.sgstamount;item.cessamount = abc.cessamount-item.cessamount;item.taxablevalue = abc.taxablevalue-item.taxablevalue;	
											}else{
												item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
											}
										}else{
												if(invoice.cdnur[0].ntty == 'C'){
												item.total = abc.total-item.total;item.igstamount = abc.igstamount-item.igstamount;item.cgstamount = abc.cgstamount-item.cgstamount;item.sgstamount = abc.sgstamount-item.sgstamount;item.cessamount = abc.cessamount-item.cessamount;item.taxablevalue = abc.taxablevalue-item.taxablevalue;
												
											}else{
												item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
											}
										}
									}else{
										item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
									}
									delete hsnitems[hhssnn];
									hsnitems[item.rate] = item;
								}else{
									if(item.rate != ""){
										hsnitems[item.rate] = item;
									}
								}
								}
								
								//content += '<tr>';content += '<td class="text-center"><span>'+i+'</span></td><td class="text-center"><span>'+item.rate+'</span></td><td class="text-center"><span>'+item.quantity+'</span></td><td class="text-center"><span>'+formatNumber(item.rateperitem.toFixed(2))+'</span></td><td class="text-center"><span>'+formatNumber(item.taxablevalue.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.igstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.cgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.sgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.cessamount.toFixed(2))+'</span></td>';content += '</tr>';
								i++;
								counts++;
							});
							}
					
					});
					var j =1;
					var objsize = Object.objsize(hsnitems);				
						for (var key in hsnitems) {
							content += '<tr>';content += '<td class="text-center"><span>'+j+'</span></td><td class="text-center"><span>'+hsnitems[key].rate+'</span></td><td class="text-center"><span>'+hsnitems[key].quantity+'</span></td><td class="text-center"><span>'+formatNumber(hsnitems[key].total.toFixed(2))+'</span></td><td class="text-center"><span>'+formatNumber(hsnitems[key].taxablevalue.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].igstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].cgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].sgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].cessamount.toFixed(2))+'</span></td>';content += '</tr>';
							j++;
						}
						$('#monthlyhsn').html(content);
						salestable = $("#dbHSNFilingTable").DataTable({
							dom: 'lBfrtip', 		
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							buttons : [ {
								extend : 'excel',
								filename : 'HSNReport',
								title : '',
								text: 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
							}],
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
								   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
							   }
							 }
						});
						$(".salestable div.toolbar").append('<a href="'+dwnldurl+'" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
						invoiceArray['GSTR1'] = invoiceList;
						$('#monthProcess').text('');
					}else{if ( $.fn.DataTable.isDataTable('#dbHSNFilingTable') ) {$('#dbHSNFilingTable').DataTable().destroy();}
						$('#dbHSNFilingTable tbody').empty();
							salestable = $("#dbHSNFilingTable").DataTable({
							dom: 'lBfrtip',
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							buttons : [ {
								extend : 'excel',
								filename : 'HSNReport',
								title : '',
								text: 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
							}],
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
								   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
							   }
							 }
						});
						$(".salestable div.toolbar").append('<a href="'+dwnldurl+'" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
						$('#monthProcess').text('');
					}
				} else {errorNotification(invoiceList);}
			}
		});
	}else if (abc == 'Yearly') {
			$('.monthely1').css("display", "none");	$('.yearly1').css("display", "block");$('.custom1').css("display", "none");$('span#fillingoption').css("vertical-align", "bottom");		
			var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;var mn = parseInt('0');
			salesFileName = 'MGST_Sales_Yearly_'+gstnnumber+'_'+yrs+'-'+yrs1;
			$(".reportTable4  div.toolbar").html('<h4>Yearly Summary</h4>');
			var yearlydwnldurl = "${contextPath}/dwnldxlsyearly/${id}/${client.id}/GSTR1/"+yrs;
		$.ajax({
			url: "${contextPath}/getmonthlyinvs/${id}/${client.id}/GSTR1/"+mn+"/"+yrs,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			beforeSend: function () {
		        $('#yearProcess').text('Processing...');
		    },
			success : function(invoiceList) {
				var content='';
				if(invoiceList instanceof Array) {
					if(invoiceList.length > 0) {
						
						if ( $.fn.DataTable.isDataTable('#dbyearlyHSNFilingTable') ) {
							$('#dbyearlyHSNFilingTable').DataTable().destroy();
						}
						$('#dbyearlyHSNFilingTable tbody').empty();
						var counts = 0;
						var hsnitems = [];
						var i=1;
						invoiceList.forEach(function(invoice){
							if(invoice.items){
								invoice.items.forEach(function(item) {
									if(item.hsn != '' && item.hsn != null){
										if(item.hsn.indexOf(':') > 0) {
												item.hsns=item.hsn.substring(0,item.hsn.indexOf(':'));
												item.desc=item.hsn.substring(item.hsn.indexOf(':')+1);
										}else{
											item.hsns = item.hsn;
											item.desc = '';
										}
									}else{
										item.hsns = '';item.desc = '';
										}
								if(item.rateperitem == null){item.rateperitem = 0.00;}
								if(item.rate == null){
									if(item.igstrate != null){
										item.rate = item.igstrate;
									}else if(item.cgstrate != null && item.sgstrate != null){
										item.rate = item.cgstrate + item.sgstrate;
									}else{
										item.rate = 0.00;
									}
									}
								if(item.igstamount == null){item.igstamount = 0.00;}
								if(item.cgstamount == null){item.cgstamount = 0.00;}
								if(item.sgstamount == null){item.sgstamount = 0.00;}
								if(item.cessamount == null){item.cessamount = 0.00;}
								if(item.taxablevalue == null){item.taxablevalue = 0.00;}
								if(item.quantity == null){item.quantity = 0.00;}
								if(item.total == null){item.total = 0.00;}
										if(counts == 0){
									if(item.rate != ""){
										hsnitems[item.rate] = item;
									}
								}else{
								if(item.rate in hsnitems){
									var hhssnn = item.rate;
									var abc = hsnitems[hhssnn];
									item.quantity += abc.quantity;
									if((invoice.invtype == 'Credit/Debit Notes') || (invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers')){
										
										if(invoice.invtype == 'Credit/Debit Notes'){
											if(invoice.cdnr[0].nt[0].ntty == 'C'){
												item.total = abc.total-item.total;item.igstamount = abc.igstamount-item.igstamount;item.cgstamount = abc.cgstamount-item.cgstamount;item.sgstamount = abc.sgstamount-item.sgstamount;item.cessamount = abc.cessamount-item.cessamount;item.taxablevalue = abc.taxablevalue-item.taxablevalue;	
											}else{
												item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
											}
										}else{
												if(invoice.cdnur[0].ntty == 'C'){
												item.total = abc.total-item.total;item.igstamount = abc.igstamount-item.igstamount;item.cgstamount = abc.cgstamount-item.cgstamount;item.sgstamount = abc.sgstamount-item.sgstamount;item.cessamount = abc.cessamount-item.cessamount;item.taxablevalue = abc.taxablevalue-item.taxablevalue;
												
											}else{
												item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
											}
										}
									}else{
										item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
									}
									delete hsnitems[hhssnn];
									hsnitems[item.rate] = item;
								}else{
									if(item.rate != ""){
										hsnitems[item.rate] = item;
									}
								}
								}
								
								//content += '<tr>';content += '<td class="text-center"><span>'+i+'</span></td><td class="text-center"><span>'+item.rate+'</span></td><td class="text-center"><span>'+item.quantity+'</span></td><td class="text-center"><span>'+formatNumber(item.rateperitem.toFixed(2))+'</span></td><td class="text-center"><span>'+formatNumber(item.taxablevalue.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.igstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.cgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.sgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.cessamount.toFixed(2))+'</span></td>';content += '</tr>';
								i++;
								counts++;
							});
							}
					
					});
					var j =1;
					var objsize = Object.objsize(hsnitems);				
						for (var key in hsnitems) {
							content += '<tr>';content += '<td class="text-center"><span>'+j+'</span></td><td class="text-center"><span>'+hsnitems[key].rate+'</span></td><td class="text-center"><span>'+hsnitems[key].quantity+'</span></td><td class="text-center"><span>'+formatNumber(hsnitems[key].total.toFixed(2))+'</span></td><td class="text-center"><span>'+formatNumber(hsnitems[key].taxablevalue.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].igstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].cgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].sgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].cessamount.toFixed(2))+'</span></td>';content += '</tr>';
							j++;
						}
						$('#yearlyhsn').html(content);
						salestable = $("#dbyearlyHSNFilingTable").DataTable({
							dom: 'lBfrtip',
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							buttons : [ {
								extend : 'excel',
								filename : 'HSNReport',
								title : '',
								text: 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
							}],
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
								   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
							   }
							 }
						});
						$(".salestable div.toolbar").append('<a href="'+yearlydwnldurl+'" id="yearlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
						invoiceArray['GSTR1'] = invoiceList;
						$('#yearProcess').text('');
					}else{
						if ( $.fn.DataTable.isDataTable('#dbyearlyHSNFilingTable') ) {$('#dbyearlyHSNFilingTable').DataTable().destroy();}
						$('#dbyearlyHSNFilingTable tbody').empty();
							salestable = $("#dbyearlyHSNFilingTable").DataTable({
							dom: 'lBfrtip',
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							buttons : [ {
								extend : 'excel',
								filename : 'HSNReport',
								title : '',
								text: 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
							}],
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
								   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
							   }
							 }
						});
						$(".salestable div.toolbar").append('<a href="'+yearlydwnldurl+'" id="yearlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
						$('#yearProcess').text('');
					}
				} else {errorNotification(invoiceList);}
			}
		});
		}else{
			$('.monthely1').css("display","none");$('.yearly1').css("display","none");$('.custom1').css("display","block");$('span#fillingoption').css("vertical-align","bottom");
	var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
	salesFileName = 'MGST_Sales_Custom_'+gstnnumber+'_'+fromtime+'_'+totime;
	$(".reportTable5  div.toolbar").html('<h4>Custom Summary</h4>');
	var customdwnldurl ="${contextPath}/dwnldxlscustom/${id}/${client.id}/GSTR1/"+fromtime+"/"+totime;
	$.ajax({
			url: "${contextPath}/getcustominvs/${id}/${client.id}/GSTR1/"+fromtime+"/"+totime,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			beforeSend: function () {$('#customProcess').text('Processing...');},
			success : function(invoiceList) {
			var content='';
				if(invoiceList instanceof Array) {
					if(invoiceList.length > 0) {
					
						if ( $.fn.DataTable.isDataTable('#dbcustomHSNFilingTable') ) {$('#dbcustomHSNFilingTable').DataTable().destroy();}
						$('#dbcustomHSNFilingTable tbody').empty();var counts =0;	var hsnitems = [];var i=1;
						invoiceList.forEach(function(invoice){
							if(invoice.items){
								invoice.items.forEach(function(item) {
									if(item.hsn != '' && item.hsn != null){
										if(item.hsn.indexOf(':') > 0) {
												item.hsns=item.hsn.substring(0,item.hsn.indexOf(':'));
												item.desc=item.hsn.substring(item.hsn.indexOf(':')+1);
										}else{
											item.hsns = item.hsn;
											item.desc = '';
										}
									}else{
										item.hsns = '';item.desc = '';
										}
								if(item.rateperitem == null){item.rateperitem = 0.00;}
								if(item.rate == null){
									if(item.igstrate != null){
										item.rate = item.igstrate;
									}else if(item.cgstrate != null && item.sgstrate != null){
										item.rate = item.cgstrate + item.sgstrate;
									}else{
										item.rate = 0.00;
									}
									}
								if(item.igstamount == null){item.igstamount = 0.00;}
								if(item.cgstamount == null){item.cgstamount = 0.00;}
								if(item.sgstamount == null){item.sgstamount = 0.00;}
								if(item.cessamount == null){item.cessamount = 0.00;}
								if(item.taxablevalue == null){item.taxablevalue = 0.00;}
								if(item.quantity == null){item.quantity = 0.00;}
								if(item.total == null){item.total = 0.00;}
										if(counts == 0){
									if(item.rate != ""){
										hsnitems[item.rate] = item;
									}
								}else{
								if(item.rate in hsnitems){
									var hhssnn = item.rate;
									var abc = hsnitems[hhssnn];
									item.quantity += abc.quantity;
									if((invoice.invtype == 'Credit/Debit Notes') || (invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers')){
										
										if(invoice.invtype == 'Credit/Debit Notes'){
											if(invoice.cdnr[0].nt[0].ntty == 'C'){
												item.total = abc.total-item.total;item.igstamount = abc.igstamount-item.igstamount;item.cgstamount = abc.cgstamount-item.cgstamount;item.sgstamount = abc.sgstamount-item.sgstamount;item.cessamount = abc.cessamount-item.cessamount;item.taxablevalue = abc.taxablevalue-item.taxablevalue;	
											}else{
												item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
											}
										}else{
												if(invoice.cdnur[0].ntty == 'C'){
												item.total = abc.total-item.total;item.igstamount = abc.igstamount-item.igstamount;item.cgstamount = abc.cgstamount-item.cgstamount;item.sgstamount = abc.sgstamount-item.sgstamount;item.cessamount = abc.cessamount-item.cessamount;item.taxablevalue = abc.taxablevalue-item.taxablevalue;
												
											}else{
												item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
											}
										}
									}else{
										item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;	
									}
									delete hsnitems[hhssnn];
									hsnitems[item.rate] = item;
								}else{
									if(item.rate != ""){
										hsnitems[item.rate] = item;
									}
								}
								}
								
								//content += '<tr>';content += '<td class="text-center"><span>'+i+'</span></td><td class="text-center"><span>'+item.rate+'</span></td><td class="text-center"><span>'+item.quantity+'</span></td><td class="text-center"><span>'+formatNumber(item.rateperitem.toFixed(2))+'</span></td><td class="text-center"><span>'+formatNumber(item.taxablevalue.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.igstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.cgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.sgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.cessamount.toFixed(2))+'</span></td>';content += '</tr>';
								i++;
								counts++;
							});
							}
					
					});
					var j =1;
					var objsize = Object.objsize(hsnitems);				
						for (var key in hsnitems) {
							content += '<tr>';content += '<td class="text-center"><span>'+j+'</span></td><td class="text-center"><span>'+hsnitems[key].rate+'</span></td><td class="text-center"><span>'+hsnitems[key].quantity+'</span></td><td class="text-center"><span>'+formatNumber(hsnitems[key].total.toFixed(2))+'</span></td><td class="text-center"><span>'+formatNumber(hsnitems[key].taxablevalue.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].igstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].cgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].sgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].cessamount.toFixed(2))+'</span></td>';content += '</tr>';
							j++;
						}
						$('#customhsn').html(content);
						salestable = $("#dbcustomHSNFilingTable").DataTable({
							dom: 'lBfrtip',
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							buttons : [ {
								extend : 'excel',
								filename : 'HSNReport',
								title : '',
								text: 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
							}],
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
								   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
							   }
							 }
						});
						$(".salestable div.toolbar").append('<a href="'+customdwnldurl+'" id="customdwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
						invoiceArray['GSTR1'] = invoiceList;
						$('#customProcess').text('');
					}else{
						if ( $.fn.DataTable.isDataTable('#dbcustomHSNFilingTable') ) {$('#dbcustomHSNFilingTable').DataTable().destroy();}
						$('#dbcustomHSNFilingTable tbody').empty();
						salestable = $("#dbcustomHSNFilingTable").DataTable({
							dom: 'lBfrtip',
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							buttons : [ {
								extend : 'excel',
								filename : 'HSNReport',
								title : '',
								text: 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
							}],
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
								   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
							   }
							 }
						});
						$(".salestable div.toolbar").append('<a href="'+customdwnldurl+'" id="customdwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
						$('#customProcess').text('');
					}
				} else {errorNotification(invoiceList);}
			}
		});
		} 
	}
</script>
</head>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
<div class="breadcrumbwrap" >
	<div class="container bread">
	<div class="row">
        <div class="col-md-12 col-sm-12">
		<ol class="breadcrumb">
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
			<li class="breadcrumb-item active">Tax Slab Wise Sales Report</li>
		</ol>
		<div class="retresp"></div></div></div></div>
	</div>
	<div class="db-ca-wrap monthely1">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h5>Monthly Tax Slab Wise Sales Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h5><p>Monthly Tax Slab Wise Sales Report gives you a summary of your monthly sales.</p>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
			<div class="dropdown-content reporttaxSales"> <span class="arrow-up"></span><span class="pl-2"> All the Sale Invoices from your SaleRegister/Books Monthly, Yearly and Custom Wise Based On Tax Rate</span></div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class="dropdown chooseteam mr-0"><span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
					</div>
				</Span>
				<div class="dropdown-menu ret-type"	style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a><a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Return Period:</label></span>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div><a href="#" class="btn btn-greendark pull-right" role="button"	style="padding: 4px 10px;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: none" class="yearly-sp"> 
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px;">Return Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase" onClick="getdiv()">Generate</a>
				</span>
				<span class="datetimetxt custom-sp" style="display: none" id="custom-sp">
					<a href="#" class="btn btn-greendark pull-right" role="button"	style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</a>
					<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 8px; height: 28px; margin-right: 10px;">
							<input type="text" class="form-control totime" value="02-2012"	readonly="">
							<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
					</div>
					<div class="datetimetxt datetime-wrap dpfromtime">
					<label	style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="02-2012"	readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>	
					</div>
				</span>
			</div>
			<div id="monthProcess" class="text-center"></div>
			<div class="customtable db-ca-view salestable">
			
						 <table id="dbHSNFilingTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead>
								<tr>
									<th class="text-center">S.NO</th><th class="text-center">Tax Slab Wise</th><th class="text-center dt-body-right">QUANTITY</th><th class="text-center dt-body-right">VALUE</th><th class="text-center dt-body-right">TAXABLE</th><th class="text-center dt-body-right">IGST</th><th class="text-center dt-body-right">CGST</th><th class="text-center dt-body-right">SGST</th><th class="text-center dt-body-right">CESS</th>
								</tr>
							</thead>
							<tbody id="monthlyhsn">
							</tbody>
						</table>
				</div>
		</div>
	</div>
	<div class="db-ca-wrap yearly1" style="display: none">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h5>Yearly Tax Slab Wise Sales Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h5><p>Yearly Tax Slab Wise Sales Report gives you a summary of your annual sales.</p>
			<div class="dropdown chooseteam mr-0" style="height: 32px">
			<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<span>Report Type:</span>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option1" class="filing_option"	style="vertical-align: bottom">Yearly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i> 
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type" style="WIDTH: 108px !important; min-width: 36px; left: 16%; top: 26px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> 
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					<a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: none"	id="monthely-sp">
				<label id="ret-period">Return Period:</label>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months"	style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" value="02-2012"	readonly=""> 
							<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: inline-block" class="yearly-sp">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Return Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption1" class="yearlyoption">2021 - 2022</span><span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc" id="date-drop" style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div></Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
				</span>
				<span class="datetimetxt custom-sp" style="display: none" id="custom-sp">
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="02-2012"	readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>	
					</div>
					<div class="datetimetxt datetime-wrap">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="02-2012" readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
					</div> 	
				</span>
			</div>
			<div id="yearProcess" class="text-center"></div>
			<div class="customtable db-ca-view salestable">
			
						 <table id="dbyearlyHSNFilingTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead>
								<tr>
									<th class="text-center">S.NO</th><th class="text-center">Tax Slab Wise</th><th class="text-center dt-body-right">QUANTITY</th><th class="text-center dt-body-right">VALUE</th><th class="text-center dt-body-right">TAXABLE</th><th class="text-center dt-body-right">IGST</th><th class="text-center dt-body-right">CGST</th><th class="text-center dt-body-right">SGST</th><th class="text-center dt-body-right">CESS</th>
								</tr>
							</thead>
							<tbody id="yearlyhsn">
							</tbody>
						</table>
				</div>
			
		</div>
	</div>
	<div class="db-ca-wrap custom1" style="display: none">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h5>Custom Tax Slab Wise Sales Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h5><p>Custom Tax Slab Wise Sales Report gives you a summary of your monthly, quarterly and annual sales.</p>
			<div class="dropdown chooseteam  mr-0">
				<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<span style="height: 32px">Report Type:</span>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option2" class="filing_option"	style="vertical-align: top">Custom</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i>
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type"	style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> 
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a> 
					<a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: none"	id="monthely-sp">
					<label id="ret-period">Return Period:</label>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" value="02-2012" readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: none margin-bottom:4px" class="yearly-sp"> 
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px;">Return Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; background-color: white; height: 27px; align-items: top; padding-right:14px; min-width: 104px;max-width: 104px;">
							<span style="vertical-align: top;" id="yearlyoption2" class="yearlyoption">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc" id="date-drop" style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div> 
					<a href="#" class="btn btn-greendark  pull-right" role="button" style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
				</span> 
				<span class="datetimetxt custom-sp" style="display: block" id="custom-sp"> 
				<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase;"	onClick="getdiv()">Generate</a>
				<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="10-11-2012" data-date-format="dd-mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="11-02-2012" readonly=""> 
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>	
					</div>
					<div class="datetimetxt datetime-wrap">
					<label	style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="10-2-2012" data-date-format="dd-mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="11-02-2014" readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
					</div> 
				</span>
			</div>
			<div class=" "></div>
			<div id="customProcess" class="text-center"></div>
			<div class="customtable db-ca-view salestable">
			
						 <table id="dbcustomHSNFilingTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead>
								<tr>
									<th class="text-center">S.NO</th><th class="text-center">Tax Slab Wise</th><th class="text-center dt-body-right">QUANTITY</th><th class="text-center dt-body-right">VALUE</th><th class="text-center dt-body-right">TAXABLE</th><th class="text-center dt-body-right">IGST</th><th class="text-center dt-body-right">CGST</th><th class="text-center dt-body-right">SGST</th><th class="text-center dt-body-right">CESS</th>
								</tr>
							</thead>
							<tbody id="customhsn">
							</tbody>
						</table>
				</div>
		</div>
	</div>
	</div>
	<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Help To Read This Report </h3></div>
        <div class=" p-2 steptext-wrap"><span class="pl-2"> All the Sale Invoices from your SaleRegister/Books Monthly, Yearly and Custom Wise Based On Tax Rate</span> </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<script type="text/javascript">
	var salesFileName;
	var gstnnumber='<c:out value="${client.gstnnumber}"/>';
	var table = $('table.display').DataTable({
		   "dom": '<"toolbar">frtip',    
		     "pageLength": 10,
			 "responsive":true,
			 "ordering": false,
			 "searching": false,
			 "paging":false,
			 "bInfo" : false,
		     "language": {
		  	    "search": "_INPUT_",
		        "searchPlaceholder": "Search...",
		        "paginate": {
		           "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
		       }
		     }
		   }); 
		$(".reportTable2  div.toolbar").html('<h4>Monthly Summary of BVM</h4><a href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm" style="position: absolute; top: 3%; z-index: 1;">Download Excel</a>');  
		$(".reportTable4  div.toolbar").html('<h4>Yearly Summary of BVM</h4><a href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm" style="position: absolute; top: 3%; z-index: 1;">Download Excel</a>');
		var headertext = [],
		headers = document.querySelectorAll("table.display th"),
		tablerows = document.querySelectorAll("table.display th"),
		tablebody = document.querySelector("table.display tbody");
		for (var i = 0; i < headers.length; i++) {
			var current = headers[i];
			headertext.push(current.textContent.replace(/\r?\n|\r/, ""));
		}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {
			for (var j = 0, col; col = row.cells[j]; j++) {
				col.setAttribute("data-th", headertext[j]);
			}
		}
		
		
	</script>
	<script>

$(document).ready(function() {
	$( ".helpicon" ).hover(function() {$('.reporttaxSales ').show();
	}, function() {$('.reporttaxSales ').hide();
	});
		var clientname = '<c:out value="${client.businessname}"/>';
		var date = new Date();
		month = '<c:out value="${month}"/>';
		year = '<c:out value="${year}"/>';
		if(month == null || month == '') {
		month = date.getMonth()+1;
		year = date.getFullYear();
		}
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
		//updateReturnPeriod(ev.date);
		month = ev.date.getMonth()+1;
		year = ev.date.getFullYear();
		});
		$('.dpCustom').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
		}).on('changeDate', function(ev) {
		//updateReturnPeriod(ev.date);
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
		//updateReturnPeriod(ev.date);
		day = ev.date.getDate();
		mnt = ev.date.getMonth()+1;
		yr = ev.date.getFullYear();
		$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		});
		$('.dpMonths').datepicker('update', dateValue);
		$('.dpCustom').datepicker('update', customValue);
		$('.dpCustom1').datepicker('update', customValue);
		$.ajax({
		url: "${contextPath}/getmonthlyinvs/${id}/${client.id}/GSTR1/${month}/${year}",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		beforeSend: function () {$('#monthProcess').text('Processing...');},
		success : function(invoiceList) {
		var content='';
			if(invoiceList instanceof Array) {
				if(invoiceList.length > 0) {
					var counts =0;
					var hsnitems = [];
					var i=1;
					invoiceList.forEach(function(invoice){
						if(invoice.items){
							invoice.items.forEach(function(item) {
								if(item.hsn != '' && item.hsn != null){
										if(item.hsn.indexOf(':') > 0) {
												item.hsns=item.hsn.substring(0,item.hsn.indexOf(':'));
												item.desc=item.hsn.substring(item.hsn.indexOf(':')+1);
										}else{
											item.hsns = item.hsn;
											item.desc = '';
										}
									}else{
										item.hsns = '';item.desc = '';
										}
										if(item.rateperitem == null){item.rateperitem = 0.00;}
										if(item.rate == null){
											if(item.igstrate != null){
												item.rate = item.igstrate;
											}else if(item.cgstrate != null && item.sgstrate != null){
												item.rate = item.cgstrate + item.sgstrate;
											}else{
												item.rate = 0.00;
											}
											}
								if(item.igstamount == null){item.igstamount = 0.00;}
								if(item.cgstamount == null){item.cgstamount = 0.00;}
								if(item.sgstamount == null){item.sgstamount = 0.00;}
								if(item.cessamount == null){item.cessamount = 0.00;}
								if(item.taxablevalue == null){item.taxablevalue = 0.00;}
								if(item.quantity == null){item.quantity = 0.00;}
								if(item.total == null){item.total = 0.00;}
										if(counts == 0){
									if(item.rate != ""){
										hsnitems[item.rate] = item;
									}
								}else{
								if(item.rate in hsnitems){
									var hhssnn = item.rate;
									var abc = hsnitems[hhssnn];
									item.quantity += abc.quantity;
									item.total += abc.total;item.igstamount += abc.igstamount;item.cgstamount += abc.cgstamount;item.sgstamount += abc.sgstamount;item.cessamount += abc.cessamount;item.taxablevalue += abc.taxablevalue;
									delete hsnitems[hhssnn];
									hsnitems[item.rate] = item;
								}else{
									if(item.rate != ""){
										hsnitems[item.rate] = item;
									}
								}
								}
								
								//content += '<tr>';content += '<td class="text-center"><span>'+i+'</span></td><td class="text-center"><span>'+item.rate+'</span></td><td class="text-center"><span>'+item.quantity+'</span></td><td class="text-center"><span>'+formatNumber(item.rateperitem.toFixed(2))+'</span></td><td class="text-center"><span>'+formatNumber(item.taxablevalue.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.igstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.cgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.sgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(item.cessamount.toFixed(2))+'</span></td>';content += '</tr>';
								i++;
								counts++;
							});
							}
					
					});
					var j =1;
					var objsize = Object.objsize(hsnitems);				
						for (var key in hsnitems) {
							content += '<tr>';content += '<td class="text-center"><span>'+j+'</span></td><td class="text-center"><span>'+hsnitems[key].rate+'</span></td><td class="text-center"><span>'+hsnitems[key].quantity+'</span></td><td class="text-center"><span>'+formatNumber(hsnitems[key].total.toFixed(2))+'</span></td><td class="text-center"><span>'+formatNumber(hsnitems[key].taxablevalue.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].igstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].cgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].sgstamount.toFixed(2))+'</span></td><td class="text-center"><span class="ind_formats">'+formatNumber(hsnitems[key].cessamount.toFixed(2))+'</span></td>';content += '</tr>';
							j++;
						}
					$('#monthlyhsn').html(content);
					
					$('#monthProcess').text('');
					salestable = $("#dbHSNFilingTable").DataTable({
						dom: 'lBfrtip', 		
						"paging": true,
						"searching": true,
						"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
						"responsive": true,
						"ordering": true,
						buttons : [ {
								extend : 'excel',
								filename : 'HSNReport',
								title : '',
								text: 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
							}],
						"language": {
							"search": "_INPUT_",
							"searchPlaceholder": "Search...",
							"paginate": {
							   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
								"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
						   }
						 }
					});
					$(".salestable div.toolbar").append('<a href="${contextPath}/dwnldxls/${id}/${client.id}/GSTR1/${month}/${year}" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
					invoiceArray['GSTR1'] = invoiceList;
					}else{
					salestable = $("#dbHSNFilingTable").DataTable({
						dom: 'lBfrtip', 		
						"paging": true,
						"searching": true,
						"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
						"responsive": true,
						"ordering": true,
						buttons : [ {
								extend : 'excel',
								filename : 'HSNReport',
								title : '',
								text: 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
							}],
						"language": {
							"search": "_INPUT_",
							"searchPlaceholder": "Search...",
							"paginate": {
							   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
								"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
						   }
						 }
					});
					$(".salestable div.toolbar").append('<a href="${contextPath}/dwnldxls/${id}/${client.id}/GSTR1/${month}/${year}" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
					$('#monthProcess').text('');
				}
			} else {
				errorNotification(invoiceList);
			}
		}
		});
});
Object.objsize = function(Myobj) {
    var osize = 0, key;
    for (key in Myobj) {
        if (Myobj.hasOwnProperty(key)) osize++;
    }
    return osize;
};
</script>
	<jsp:include page="/WEB-INF/views/reports/invoicedetails.jsp" />
</body>
</html>
