var invTableReports;

function loadReportsInvTable(reportType, clientid,tcstds, cUrl){
	if(invTableReports){
		invTableReports.clear();
		invTableReports.destroy();
	}
	invTableReports = $('#tds_accounting_datatable').DataTable({
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
	        	 loadTotals(tcstds, resp.totaljournalamount);
	        	 $('#updatefilter_summary_reports').show();
	        	 return resp.invoices.content ;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[4,'desc']],
		'columns': getInvColumns(reportType, clientid,tcstds),
		'columnDefs':getInvColumnDefs(reportType)
	});
}

function loadTotals(varRetType, totalsData){
	$('#idCount').html(totalsData ? totalsData.totalTransactions : '0');
	$('#idTotAmtVal').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.invoiceamount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	if(varRetType == 'tds'){
		$('#idTDS').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.tds_payable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	}else{
		$('#idTDS').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.tcs_payable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	}
}

function loadReportsSummary(summaryurl,tcstds){
	$.ajax({
		url: summaryurl,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			summaryResponse(response,tcstds);
		}
	});
}

function summaryResponse(response,tcstds){
	var totinvs=0, totAmountVal=0.0, totTcsAmt=0.0, totTdsAmt=0.0;
	if($.fn.DataTable.isDataTable('#reportTable4')){$('#reportTable4').DataTable().destroy();}
		$.each(response, function(key, value){
			$('#totalinvoices'+key).html(value.totalTransactions);
			$('#totalAmountValue'+key).html(formatNumber(value.totalAmount));
			if(tcstds == 'tds'){
				$('#tdsAmt'+key).html(formatNumber(value.tdsAmount));
				totTdsAmt+=parseFloat(value.tdsAmount);
			}else{
				$('#tcsAmt'+key).html(formatNumber(value.tcsAmount));
				totTcsAmt+=parseFloat(value.tcsAmount);
			}
			totinvs+=parseInt(value.totalTransactions);
			totAmountVal+=parseFloat(value.totalAmount);
		});
		$('#ytotal_Transactions').html(totinvs);
		$('#ytotal_Amountvalue').html(formatNumber(totAmountVal.toFixed(2)));
		if(tcstds == 'tds'){
			$('#ytotal_TDSAmount').html(formatNumber(totTdsAmt.toFixed(2)));
		}else{
			$('#ytotal_TCSAmount').html(formatNumber(totTcsAmt.toFixed(2)));
		}
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


function getInvColumns(reportType, clientid,tcstds){
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
		var invoiceno = '';
		if(data.invoiceNumber != undefined){
			invoiceno = data.invoiceNumber;
		}
		return '<span class="text-left invoiceclk">'+invoiceno+'</span>';
	}};
	var billtoname = {data: function ( data, type, row ) {
		var billedtoname = '';
		if(data.vendorName != undefined){
			billedtoname = data.vendorName;
		}
		return '<span class="text-left invoiceclk">'+billedtoname+'</span>';
	}};
	var billtogtnn = {data: function ( data, type, row ) {
		var gstnnum = '';
		if(data.ctin != undefined){
			gstnnum = data.ctin;
		}
		return '<span class="text-left invoiceclk">'+gstnnum+'</span>';
	}};		
	var totalamt = {data: function ( data, type, row ) {
		var totalAmount = 0.0;
		if(data.invoiceamount){
			totalAmount = data.invoiceamount;
		}
		return '<span class="ind_formats text-right invoiceclk">'+formatNumber(totalAmount.toFixed(2))+'</span>';
	}};
	var sectioninfo = {data: function ( data, type, row ) {
		var sectiondt = '';
		if(tcstds == "tds"){
			if(data.tdssection != undefined){
				sectiondt = data.tdssection;
			}
		}else if(tcstds == "tcs"){
			if(data.tcssection != undefined){
				sectiondt = data.tcssection;
			}
		}
		return '<span class="text-left invoiceclk">'+sectiondt+'</span>';
	}};
	var sectioninf = {data: function ( data, type, row ) {
		var sectiondtf = '';
		if(tcstds == "tds"){
			if(data.tdspercentage != undefined){
				sectiondtf = data.tdspercentage;
			}
		}else if(tcstds == "tcs"){
			if(data.tcspercentage != undefined){
				sectiondtf = data.tcspercentage;
			}
		}
		return '<span class="text-left invoiceclk">'+sectiondtf+'</span>';
	}};
	var pancategory = {data: function ( data, type, row ) {
		var category = '';
		if(data.pancategory != undefined){
			category = data.pancategory;
		}
		return '<span class="text-left invoiceclk">'+category+'</span>';
	}};
	var tdsdeduct = {data: function ( data, type, row ) {
		var totTcs = 0.0;
		if(data.drEntrie != undefined && data.crEntrie != undefined){
			data.drEntrie.forEach(function(drEntrie){
				if(tcstds == "tds"){
					if("TDS Payable" == drEntrie.name){
						totTcs = drEntrie.value;
					}
				}else if(tcstds == "tcs"){
					if("TCS Payable" == drEntrie.name){
						totTcs = drEntrie.value;
					}
				}
			});
			data.crEntrie.forEach(function(crEntrie){
				if(tcstds == "tds"){
					if("TDS Payable" == crEntrie.name){
						totTcs = crEntrie.value;
					}
				}else if(tcstds == "tcs"){
					if("TCS Payable" == crEntrie.name){
						totTcs = crEntrie.value;
					}
				}
			});
		}
		return '<span class="ind_formats text-left invoiceclk">'+totTcs+'</span>';
	}};
	return [invDate, invsNo, billtoname, billtogtnn, pancategory, totalamt, sectioninfo, sectioninf, tdsdeduct];
}
function getInvColumnDefs(varRetType){
	return  [
		{
			"targets": [5, 7, 8],
			className: 'dt-body-right'
		}
	];	
}