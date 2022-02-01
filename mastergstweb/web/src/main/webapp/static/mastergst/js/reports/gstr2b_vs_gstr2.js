var invCmpTableReportsUrl = new Object();
var invCmpTableReports;

function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;
	document.getElementById('yearlyoption').innerHTML=value;
}
function getval(sel){
	document.getElementById('filing_option').innerHTML=sel;
	document.getElementById('filing_option').innerHTML=sel;
	if(sel == 'Monthly'){
		$('.yearly-sp').css("display","none");
		$('#monthely-sp').css("display","inline-block");
		$('.dropdown-menu.ret-type').css("left","16%");
	}else {
		$('.monthely-sp').css("display","none");
		$('.yearly-sp').css("display","inline-block");
		$('.dropdown-menu.ret-type').css("left","19%");
	}
}
function formatInvoiceDate(date){
	var invDate = new Date(date);
	var day = invDate.getDate() + "";
	var month = (invDate.getMonth() + 1) + "";
	var year = invDate.getFullYear() + "";
	day = checkZero(day);
	month = checkZero(month);
	year = checkZero(year);
	return day + "/" + month + "/" + year;
}
function checkZero(data){
	if(data.length == 1){
		data = "0" + data;
  	}
	return data;
}
function getdiv() {
	var abc = $('#fillingoption span').html();
	if (abc == 'Yearly') {
		$('.yearly1').css("display", "block");
		$('.custom1').css("display", "none");
		$('span#fillingoption').css("vertical-align", "bottom");
		var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');
		var yrs = parseInt(fpsplit[0]);
		var yrs1 = parseInt(fpsplit[0])+1;
		yearlyAjaxFuntion(yrs);
	}else{
	    $('.yearly1').css("display","none");$('.custom1').css("display","block");$('span#fillingoption').css("vertical-align","bottom");
	    var fromtime = $('.fromtime').val();var totime = $('.totime').val();
	    $('.fromtime').val(fromtime);$('.totime').val(totime);
	    customAjaxFuntion(fromtime,totime);
	} 
}



function loadComparisonReportsInvTable(id, clientid, month, year, type){
	var cUrl =_getContextPath()+'/gstr2bvsgstr2xls/'+id+'/'+clientid+'?month='+month+'&year='+year;
	var pUrl =_getContextPath()+'/gstr2bvsgstr2/'+id+'/'+clientid+'?month='+month+'&year='+year;
	invCmpTableReportsUrl = pUrl;
	if(invCmpTableReports){
		invCmpTableReports.clear();
		invCmpTableReports.destroy();
	}
	invCmpTableReports = $('#comaprison_dataTable').DataTable({
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
	        	resp.recordsTotal = resp.totalElements;
	        	resp.recordsFiltered = resp.totalElements;
	        	$(".customtable div.toolbar").html('').append('<a href="'+cUrl+'" id="yearlydwnldxls" class="btn btn-blue mr-1">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
				return resp.invoices.content ;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"scrollX": true,
		"scrollCollapse": true,
		fixedColumns:   {
			leftColumns: 3
		},
		'columns': getInvColumns(id, clientid, month, year, type)
	});	
}

function getInvColumns(id, clientid, month, year, type){

	var invsNo = {data:  function ( data, type, row ) {
					var invoiceno = '';
					if(data.invoiceno != undefined){
						invoiceno = data.invoiceno;
					}
			      	 return '<span class="text-left invoiceclk ">'+invoiceno+'</span>';
				}};
	var billtoname = {data: function ( data, type, row ) {
						var billedtoname = '';
						if(data.fullname != undefined){
							billedtoname = data.fullname;
						}
					    return '<span class="text-left invoiceclk ">'+billedtoname+'</span>';
				}};
	var billtogtnn = {data: function ( data, type, row ) {
					var gstnnum = '';
					if(data.gstin != undefined){
						gstnnum = data.gstin;
					}
					return '<span class="text-left invoiceclk ">'+gstnnum+'</span>';
				}};		
	var invDate = {data: function ( data, type, row ) {
					var dateofinvoice = "";
					if(data.invoicedate != null){
								var invDate = new Date(data.invoicedate);
								var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
								day = checkZero(day);month = checkZero(month);year = checkZero(year);
								dateofinvoice = day + "/" + month + "/" + year;
							}else{dateofinvoice = "";}
					      	 return '<span class="text-left invoiceclk ">'+(new Date(data.invoicedate)).toLocaleDateString("en-GB")+'</span>';
					    }};
	var gstr2btotlTaxable = {data: function ( data, type, row ) {
						var g2btotalTaxable = 0.0;
						if(data.gstr2BTaxValue){
							g2btotalTaxable = data.gstr2BTaxValue;
						}
					   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(g2btotalTaxable.toFixed(2))+'</span>';
					    }};
	var gstr2btotalamt = {data: function ( data, type, row ) {
					var g2btotalAmount = 0.0;
					if(data.gstr2BInvoiceValue){
						g2btotalAmount = data.gstr2BInvoiceValue;
					}
				   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(g2btotalAmount.toFixed(2))+'</span>';
				    }};
	var gstr2btotaligst = {data: function ( data, type, row ) {
		var g2bigst = 0.0;
		if(data.gstr2BIGSTValue){
			g2bigst = data.gstr2BIGSTValue;
		}
	   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(g2bigst.toFixed(2))+'</span>';
	    }};
	var gstr2btotalcgst = {data: function ( data, type, row ) {
		var g2bcgst = 0.0;
		if(data.gstr2BCGSTValue){
			g2bcgst = data.gstr2BCGSTValue;
		}
	   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(g2bcgst.toFixed(2))+'</span>';
	    }};
	var gstr2btotalsgst = {data: function ( data, type, row ) {
		var g2bsgst = 0.0;
		if(data.gstr2BSGSTValue){
			g2bsgst = data.gstr2BSGSTValue;
		}
	   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(g2bsgst.toFixed(2))+'</span>';
	    }};
		var gstr2totlTaxable = {data: function ( data, type, row ) {
			var g2totalTaxable = 0.0;
			if(data.gstr2TaxValue){
				g2totalTaxable = data.gstr2TaxValue;
			}
		   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(g2totalTaxable.toFixed(2))+'</span>';
	    }};
		var gstr2totalamt = {data: function ( data, type, row ) {
			var g2totalAmount = 0.0;
			if(data.gstr2InvoiceValue){
				g2totalAmount = data.gstr2InvoiceValue;
			}
		   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(g2totalAmount.toFixed(2))+'</span>';
	    }};
		var gstr2totaligst = {data: function ( data, type, row ) {
			var g2igst = 0.0;
			if(data.gstr2IGSTValue){
				g2igst = data.gstr2IGSTValue;
			}
			return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(g2igst.toFixed(2))+'</span>';
		}};
		var gstr2totalcgst = {data: function ( data, type, row ) {
			var g2cgst = 0.0;
			if(data.gstr2CGSTValue){
				g2cgst = data.gstr2CGSTValue;
			}
			return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(g2cgst.toFixed(2))+'</span>';
		}};
		var gstr2totalsgst = {data: function ( data, type, row ) {
			var g2sgst = 0.0;
			if(data.gstr2SGSTValue){
				g2sgst = data.gstr2SGSTValue;
			}
			return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(g2sgst.toFixed(2))+'</span>';
		}};
	
		var difftotlTaxable = {data: function ( data, type, row ) {
				var difftotalTaxable = 0.0;
				if(data.diffTaxValue){
					difftotalTaxable = data.diffTaxValue;
				}
			   	return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(difftotalTaxable.toFixed(2))+'</span>';
		    }};
			var difftotalamt = {data: function ( data, type, row ) {
				var difftotalAmount = 0.0;
				if(data.diffInvoiceValue){
					difftotalAmount = data.diffInvoiceValue;
				}
			   	return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(difftotalAmount.toFixed(2))+'</span>';
		    }};
			var difftotaligst = {data: function ( data, type, row ) {
				var diffigst = 0.0;
				if(data.diffIGSTValue){
					diffigst = data.diffIGSTValue;
				}
				return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(diffigst.toFixed(2))+'</span>';
			}};
			var difftotalcgst = {data: function ( data, type, row ) {
				var diffcgst = 0.0;
				if(data.diffCGSTValue){
					diffcgst = data.diffSGSTValue;
				}
				return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(diffcgst.toFixed(2))+'</span>';
			}};
			var difftotalsgst = {data: function ( data, type, row ) {
				var diffsgst = 0.0;
				if(data.diffSGSTValue){
					diffsgst = data.diffSGSTValue;
				}
				return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(diffsgst.toFixed(2))+'</span>';
			}};
	
	return [
			invDate, billtogtnn, billtoname,
			invsNo, gstr2totalamt, gstr2totlTaxable, gstr2totaligst, gstr2totalcgst, gstr2totalsgst, 
			gstr2btotalamt, gstr2btotlTaxable, gstr2btotaligst, gstr2btotalcgst, gstr2btotalsgst,
			difftotalamt, difftotlTaxable, difftotaligst, difftotalcgst, difftotalsgst
	];
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
