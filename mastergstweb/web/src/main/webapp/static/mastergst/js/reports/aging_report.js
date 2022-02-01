var invTableReports;
$(function() {
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
};
function loadReportsInvTable(reportType, clientid, cUrl){
	if(invTableReports){
		invTableReports.clear();
		invTableReports.destroy();
	}
	invTableReports = $('#accounting_aging_'+reportType).DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
	     "ajax": {
	         url: cUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 loadTotals(resp.daytotals30,resp.daytotals60,resp.daytotals90,resp.daytotals120,resp.daytotals180);
	        	 return resp.invoices.content;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[4,'desc']],
		'columns': getInvColumns(reportType, clientid),
		'columnDefs':getInvColumnDefs(reportType)
	});
	OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
}

function loadTotals(pending30days,pending60days,pending90days,pending120days,pending180days){
	
	$('#idless30days').html(pending30days ? '<i class="fa fa-rupee"></i>'+formatNumber(pending30days.pendingAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#id30to60days').html(pending60days ? '<i class="fa fa-rupee"></i>'+formatNumber(pending60days.pendingAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#id60to90days').html(pending90days ? '<i class="fa fa-rupee"></i>'+formatNumber(pending90days.pendingAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#id90to180days').html(pending120days ? '<i class="fa fa-rupee"></i>'+formatNumber(pending120days.pendingAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idgreater180days').html(pending180days ? '<i class="fa fa-rupee"></i>'+formatNumber(pending180days.pendingAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
}
function getInvColumns(reportType, clientid){
	var varRetTypeCode = reportType.replace(" ", "_");
	var invDate = {data: function ( data, type, row ) {
		var dateofinvoice = "";
		if(data.dateofinvoice != null){
			var invDate = new Date(data.dateofinvoice);
			var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			dateofinvoice = day + "/" + month + "/" + year;
		}else{dateofinvoice = "";}
		return '<span class="text-left invoiceclk">'+(new Date(data.dateofinvoice)).toLocaleDateString("en-GB")+'</span>';
	}};
	var invsNo = {data:  function ( data, type, row ) {
		var invnumber = '';
		if(data.invoiceno != undefined){
			invnumber = data.invoiceno;
		}
		return '<span class="text-left invoiceclk">'+invnumber+'</span>';
	}};
	var billtoname = {data: function ( data, type, row ) {
		var billedtoname = '';
		if(data.billedtoname != undefined){
			billedtoname = data.billedtoname;
		}
		return '<span class="text-left invoiceclk">'+billedtoname+'</span>';
	}};
	
	var pendingamt = {data: function ( data, type, row ) {
		var pendingamount = 0.0;
		
		if(data.pendingAmount){
			pendingamount = data.pendingAmount;
		}
		return '<span class="ind_formats text-right invoiceclk">'+formatNumber(pendingamount.toFixed(2))+'</span>';
	}};
	
	var dueDate = {data: function ( data, type, row ) {
		var duedate = "";
		if(data.dueDate != null){
			var invDate = new Date(data.dueDate);
			var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			duedate = day + "/" + month + "/" + year;
		}else{duedate = "";}
		return '<span class="text-left invoiceclk">'+(new Date(data.dueDate)).toLocaleDateString("en-GB")+'</span>';
	}};
	var phaseone = {data: function ( data, type, row ) {
		var one = '', pendingamount = '';
		if(data.pendingAmount){
			pendingamount = data.pendingAmount;
		}
		if(data.sftr != undefined){
			one = data.sftr <= 30 ? pendingamount : ''; 
		}
		if(one != ''){
			return '<span class="text-right ind_formats invoiceclk">'+formatNumber(one.toFixed(2))+'</span>';
		}else{
			return '<span class="text-right invoiceclk">'+one+'</span>';
		}
	}};
	var phasetwo = {data: function ( data, type, row ) {
		var two = '', pendingamount = '';
		if(data.pendingAmount){
			pendingamount = data.pendingAmount;
		};
		if(data.sftr != undefined){
			two = (data.sftr > 30 && data.sftr <= 60) ? pendingamount : ''; 
		}
		if(two != ''){
			return '<span class="text-right ind_formats invoiceclk">'+formatNumber(two.toFixed(2))+'</span>';
		}
		return '<span class="text-right invoiceclk">'+two+'</span>';
	}}
	var phasethree = {data: function ( data, type, row ) {
		var three = '', pendingamount = '';
		if(data.pendingAmount){
			pendingamount = data.pendingAmount;
		}
		if(data.sftr != undefined){
			three = (data.sftr > 60 && data.sftr <= 90) ? pendingamount : '';
		}
		if(three != ''){
			return '<span class="text-right ind_formats invoiceclk">'+formatNumber(three.toFixed(2))+'</span>';
		}else{
			return '<span class="text-right invoiceclk">'+three+'</span>';
		}
	}}
	var phasefour = {data: function ( data, type, row ) {
		var four = '', pendingamount = '';
		if(data.pendingAmount){
			pendingamount = data.pendingAmount;
		}
		if(data.sftr != undefined){
			four = (data.sftr > 90 && data.sftr <= 120) ? pendingamount : '';
		}
		if(four != ''){
			return '<span class="text-right ind_formats invoiceclk">'+formatNumber(four.toFixed(2))+'</span>';
		}else{
			return '<span class="text-right invoiceclk">'+four+'</span>';			
		}
	}}
	var phasefive = {data: function ( data, type, row ) {
		var five = '', pendingamount = '';
		if(data.pendingAmount){
			pendingamount = data.pendingAmount;
		}
		if(data.sftr != undefined){
			five = data.sftr > 120 ? pendingamount : ''; 
		}
		if(five != ''){
			return '<span class="text-right ind_formats invoiceclk">'+formatNumber(five.toFixed(2))+'</span>';
		}else{
			return '<span class="text-right invoiceclk">'+five+'</span>';
		}
	}}
	return [invDate, invsNo, billtoname, pendingamt, dueDate, phaseone, phasetwo, phasethree, phasefour, phasefive];
}
function getInvColumnDefs(varRetType){
	return  [
		{
			"targets": [ 3, 5, 6, 7, 8, 9 ],
			className: 'dt-body-right'
		}
	];	
}