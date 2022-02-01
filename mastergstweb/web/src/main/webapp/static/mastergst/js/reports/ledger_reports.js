$(document).ready(function() {
	$('.ledrop_search').select2({placeholder: "Please select ledger",allowClear: true});
	var table = $('#ledgerReportTable').DataTable({
    	   "dom": '<"toolbar">lrftip',    
    	   "lengthChange": true,
    	    pageLength: '10',
    	   "responsive":true,
    	   "searching": true,
    	   "paging":true,
    	   "bInfo" : false,
    	   "language": {
    		  "search": "_INPUT_",
    		  "searchPlaceholder": "Search...",
    		  "paginate": {
    		       "previous": "<img src='../../../../../../static/mastergst/images/master/td-arw-l.png' />",
    		       "next": "<img src='../../../../../../static/mastergst/images/master/td-arw-r.png' />"
    		  }
    	    }
    	}); 
    	$("#collapsed_wrapper div.toolbar").html('<a href="#" id="" class="btn btn-blue pull-right" style="z-index:1;position: absolute;right: 17%;">Download TO Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
    	var day =new Date().getDate();
    	var mnt =new Date().getMonth()+1;
    	var yr =new Date().getFullYear();
    	var customValue = day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
    	var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
    	$('.fromtime').val(customValue);
    	$('.totime').val(customValue);
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
    		//updateReturnPeriod(ev.date);
    		day = ev.date.getDate();
    		mnt = ev.date.getMonth()+1;
    		yr = ev.date.getFullYear();
    		$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
    	});
    	var date = $('.cdpMonths').datepicker({
    		autoclose: true,
    		viewMode: 1,
    		minViewMode: 1,
    		format: 'mm-yyyy'
    	}).on('changeDate', function(ev) {
    		//updateReturnPeriod(ev.date);
    		month = ev.date.getMonth()+1;
    		year = ev.date.getFullYear();
    	});
    	$('.cdpMonths').datepicker('update', dateValue);	
});
function ledgertabledata(data,ledgername){
	var content = '<tr id="opnening"><td class="text-right"></td><td class="text-left" style="color:black!important;font-weight:bold;"><span class="mr-3"></span>Opening Balance</td><td class="text-right"></td><td class="text-right"></td><td class="text-right" style="color:black!important;font-weight:bold;"><span id="openingDebit"></span></td><td class="text-right" style="color:black!important;font-weight:bold;"><span id="openingCredit"></span></td></tr>', EMPTY_SPACE = '';
	var currentDebitTotal = 0, currentCreditTotal = 0;
	if ( $.fn.DataTable.isDataTable('#ledgerReportTable') ) {$('#ledgerReportTable').DataTable().destroy();}
	$('#ledgerReportTable tbody').empty();
	//$('#ledgerReportTable').append('');
	$.each(data, function(key, invoice){
		var isExist = false, isPositive = 'Dr', balance = 0;
		$.each(invoice.drEntrie, function(key, jrnl){
			if(jrnl.name == ledgername){
				isExist = true;
				isPositive = 'Dr';
				balance = jrnl.value;
				//break;
			}
		});
		if(!isExist){
			$.each(invoice.crEntrie, function(key, jrnl){
				if(jrnl.name == ledgername){
					isExist = true;
					isPositive = 'Cr';
					balance = jrnl.value;
					//break;
				}
			});
		}
		var jrnlData = isPositive == 'Dr' ? invoice.crEntrie : invoice.drEntrie;
		content+='<tr data-toggle="modal" data-target="#detailedModal" onclick="populateElement(\''+invoice.accountingJournalId+'\')"><td class="text-left">'+formatDate(invoice.dateofinvoice)+'</td>';		
		var isAdded = false;
		$.each(jrnlData, function(key, jrnl){
			if(jrnl.name != ledgername){
				if(!isAdded){
					content+='<td class="text-left"><span class="mr-3">To</span> '+jrnl.name+'</td>';
					isAdded = true;
				}
			}
		});
		var returntype = '';
		if(invoice.returnType == 'GSTR1'){
			returntype = 'Sales-'+invoice.invoiceType;
		}else if(invoice.returnType == 'GSTR2'){
			returntype = 'Purchases-'+invoice.invoiceType;			
		}else{
			returntype = invoice.returnType;
		}
		var invoiceNumber = '';
		if(invoice.returnType != 'EXPENSES'){
			invoiceNumber = invoice.invoiceNumber;
		}
		content+='<td class="text-left">'+returntype+'</td><td class="text-right">'+invoiceNumber+'</td>';
		if(isPositive == 'Dr'){
			currentDebitTotal += balance;		
			content+='<td class="text-right"><span class="indformatLedger">'+ balance+'</span></td>';
			content+='<td class="text-right"></td></tr>';			
		}else{
			currentCreditTotal += balance;			
			content+='<td class="text-right"></td>';			
			content+='<td class="text-right"><span class="indformatLedger">'+ balance +'</span></td></tr>';
		}		
	});
	$('#ledgerReportTable').append(content);
	$('#processing').text('');
	$('#currentDebitTotal').html(currentDebitTotal).addClass('indformatLedger');
	$('#currentCreditTotal').html(currentCreditTotal).addClass('indformatLedger');
	OSREC.CurrencyFormatter.formatAll({selector : '.indformatLedger'});
	$('#ledgerReportTable').DataTable({
  	   "dom": '<"toolbar">lrftip',    
  	   "lengthChange": true,
  	    pageLength: '10',
  	   "responsive":true,
  	   "searching": true,
  	   "paging":true,
  	   "bInfo" : false,
  	   "language": {
  		  "search": "_INPUT_",
  		  "searchPlaceholder": "Search...",
  		  "paginate": {
  		       "previous": "<img src='../../../../../../static/mastergst/images/master/td-arw-l.png' />",
  		       "next": "<img src='../../../../../../static/mastergst/images/master/td-arw-r.png' />"
  		  }
  	    }
  	}); 
  	$("#collapsed_wrapper div.toolbar").html('<a href="#" id="" class="btn btn-blue pull-right" style="z-index:1;position: absolute;right: 17%;">Download TO Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
}
/*function closeJournalVModal(){
	if ($.fn.DataTable.isDataTable('#ledgerJournalsViewTable') ) {$('#ledgerJournalsViewTable').DataTable().destroy();}
}*/
function ledgerClosingAndOpeningBalance(data){
	$('#openingDebit').html('').removeClass('indformat');
	$('#openingCredit').html('').removeClass('indformat');
	$('#closingDebit').html('').removeClass('indformat');
	$('#closingCredit').html('').removeClass('indformat');
	//$('#openingCredit').html(1).addClass('indformat');
	//$('#closingCredit').html(2).addClass('indformat');
	if(data.openingamt != 0 && data.openingamt > 0){
		$('#openingDebit').html(data.openingamt).addClass('indformat');		
	}else if(data.openingamt !=0){
		$('#openingCredit').html(Math.abs(data.openingamt)).addClass('indformat');		
	}
	if(data.closingamt != 0 && data.closingamt > 0){
		$('#closingDebit').html(data.closingamt).addClass('indformat');		
	}else if(data.closingamt !=0){
		$('#closingCredit').html(Math.abs(data.closingamt)).addClass('indformat');		
	}
	OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
}

function formatDate(data){
	var createdDt = new Date(data) ;
    var month = createdDt.getUTCMonth() + 1; 
	var day = createdDt.getUTCDate();
	var year = createdDt.getUTCFullYear();
	return day+'-'+month+'-'+year;
}
