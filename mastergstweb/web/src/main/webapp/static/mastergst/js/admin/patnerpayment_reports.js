var partnerPaymentsTable;
var partnerPaymentsTableUrl = new Object();
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
function partnerdatepick(){
	$('#pinvoicedate').datetimepicker({
		  timepicker: false,
		  format: 'd-m-Y',
		  minDate: 0,
		  scrollMonth: true
	});
}
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
function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;
}
function findRemainingAmts(){
	var partneramt = $('#pamount').val();
	var paidamount = $('#paidamt').val();
	var tdsAmt = $('#ptdsamt').val();
	if(partneramt.trim() == ""){partneramt = 0;}
	if(paidamount.trim() == ""){paidamount = 0;}
	if(tdsAmt.trim()== ""){tdsAmt = 0;}
	var ramt = parseFloat(partneramt) - (parseFloat(paidamount)+parseFloat(tdsAmt));
	$('#ramount').val(parseFloat(ramt).toFixed(2));
}
function savePartnerPayments(userid){
	$('#savePartnerPayments').addClass('btn-loader');
	$('#savePartnerPayments').addClass('disable');
	var partnerPayments =new Object();
	var fullname = $('#pname').val();
	var subscriptionamount = $('#subamt').val();
	var percentage = $('#pshare').val();
	var partneramt = $('#pamount').val();
	var paidamount = $('#paidamt').val();
	
	var paidtdsamount = $('#ptdsamt').val();
	var paidinvoiceno = $('#pinvoiceno').val();
	var paidinvoicedate = $('#pinvoicedate').val();
	
	partnerPayments.userid = userid;
	partnerPayments.fullname = fullname;
	partnerPayments.percentage = percentage;
	partnerPayments.subscriptionamount = subscriptionamount;
	partnerPayments.paidamount = paidamount;
	partnerPayments.partneramt = partneramt;
	partnerPayments.invoiceno = paidinvoiceno;
	partnerPayments.invoicedate = paidinvoicedate;
	partnerPayments.tdsamt = paidtdsamount
	
	var mthCd = $('#mthCd').val();
	var yrCd = $('#yrCd').val();
	$.ajax({
		url:_getContextPath()+"/savepartnerPayments/"+userid+"?month="+mthCd+"&year="+yrCd+"&type=",
		type : "POST",
		contentType : "application/json",
		data : JSON.stringify(partnerPayments),
		dataType : 'json',
		success : function(response) {
			if(response != null && response !=""){
				subamt = response.subscriptionamount;
				if(response.paidamount !=null && response.paidamount !=""){
					prtnrpaidamount = response.paidamount;
				}
				if(response.partneramt !=null && response.partneramt !=""){
					partneramt = response.partneramt					
				}
			}
			$('#savePartnerPayments').removeClass('btn-loader');
			$('#savePartnerPayments').removeClass('disable');
			$("#patnerPaymentModal").modal('hide');
		}
	});
}
$(document).on('show.bs.modal', '.modal', function (event) {
    var zIndex = 1040 + (10 * $('.modal:visible').length);
    $(this).css('z-index', zIndex);
    setTimeout(function() {
        $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
    }, 0);
});
function clearFormData(){	
	$('#pid').val('');
	$('#pname').val('');
	$('#pshare').val('');
	$('#subamt').val('');
	$('#pamount').val('');
	$('#paidamt').val('');
	$('#ramount').val('');
	
	$('#ptdsamt').val('');
	$('#pinvoiceno').val('');
	$('#pinvoicedate').val('');
}
function showPartnerPayments(userid, fullname, subscriptionamt, percentage){
	clearFormData();
	$('#pid').val(userid);
	$('#pname').val(fullname);
	$('#pshare').val(percentage); 
	
	var mthCd = $('#mthCd').val();
	var yrCd = $('#yrCd').val();
	var prtnrpaidamount = 0;
	var partneramt = 0;
	var ramount = 0;
	var subamt = subscriptionamt;
	var tdsAmt = 0;
	$.ajax({
		url:_getContextPath()+"/getpartnerPayments/"+userid+"?month="+mthCd+"&year="+yrCd+"&type=",
		type:'GET',
		success : function(response) {
			if(response != null && response !=""){
				//subamt = response.subscriptionamount;
				if(response.paidamount !=null && response.paidamount !=""){
					prtnrpaidamount += response.paidamount;
				}
				if(response.partneramt !=null && response.partneramt !=""){
					partneramt += response.partneramt;				
				}
				$('#pinvoiceno').val(response.invoiceno);
				$('#pinvoicedate').val(response.invoicedate);
				if(response.tdsamt !=null && response.tdsamt !=""){
					tdsAmt += response.tdsamt;
				}
				$('#pinvoiceno').val(response.invoiceno);
				$('#pinvoicedate').val(response.invoicedate);
				$('#ptdsamt').val(response.tdsamt);
			}
			partneramt = (subamt * percentage) / 100;
			ramount = parseFloat(partneramt) - (parseFloat(prtnrpaidamount)+parseFloat(tdsAmt));
			$('#ptdsamt').val(tdsAmt.toFixed(2));$('#subamt').val(subamt);
			$('#pamount').val(partneramt.toFixed(2));
			$('#paidamt').val(prtnrpaidamount.toFixed(2));
			$('#ramount').val(ramount.toFixed(2));
			
		}
	});
	$('#savePartnerPayments').attr("onClick","savePartnerPayments(\'"+userid+"\')");
}

function showComments(userid){
	$('#addCommentBtn').attr("onClick","saveComments(\'"+userid+"\')");
	$.ajax({
		url:_getContextPath()+"/getallcomments/"+userid+"?stage=partnerpayment",
		type:'GET',
		success : function(data) {
			$(".partnercommentsInfo").html("");
			$.each(data, function(key, commnt) {
				$(".partnercommentsInfo").append('<div class="leadscommentsTab mb-2" style="margin-right: 10px;"><strong><label class="label_txt">Added By : '+commnt.addedby+'</label></strong><strong><label style="float:right;">Date : '+commnt.commentDate+'</label></strong><br/>'+commnt.comments+'</div>');
			});
		}
	});
}

function saveComments(userid){
	var commentsData=new Object();
	
	var comments=$("#partnercomments").val();
	var createdate=new Date();
	var addedby= loginUser;
	
	commentsData.comments=comments;
	commentsData.commentDate=createdate;
	commentsData.addedby=addedby; 
		
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : _getContextPath()+'/savecomments/'+userid+'?stage=partnerpayment',
		data : JSON.stringify(commentsData),
		dataType : 'json',
		success : function(data) {
			
			$("#partnercomments").val('');
			$(".partnercommentsInfo").html("");
			$.each(data, function(key, commnt) {
				$(".partnercommentsInfo").append('<div class="leadscommentsTab mb-2" style="margin-right: 10px;"><strong><label class="label_txt">Added By : '+commnt.addedby+'</label></strong><strong><label style="float:right;">Date : '+commnt.commentDate+'</label></strong><br/>'+commnt.comments+'</div>');
			});		
		},
		error:function(dat){
		}
	});
}

function loadPartnersTable(month, year, fromtime, totime, fetchType){
	if(partnerPaymentsTable){
		partnerPaymentsTable.clear();
		partnerPaymentsTable.destroy();
	}
	$('#mthCd').val(month);
	$('#yrCd').val(year);
	var pUrl = '';
	if(fetchType == "custom"){
		pUrl =_getContextPath()+"/getPartnetPaymentInvoices/0/0/"+fromtime+"/"+totime+"?type="+fetchType;
	}else{
		pUrl =_getContextPath()+"/getPartnetPaymentInvoices/"+month+"/"+year+"/"+null+"/"+null+"?type="+fetchType;
	}
	partnerPaymentsTableUrl=pUrl;
	partnerPaymentsTable = $('#partnerpaymentTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		"processing": true,
		"serverSide": true,
	    "ajax": {
			url: pUrl,
	        type: 'GET',
	        contentType: 'application/json; charset=utf-8',
	        dataType: "json",
	        'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.totalElements;
	        	 resp.recordsFiltered = resp.totalElements;
	        	 return resp.content;
	         }
	    },
	    "paging": true,
		"pageLength":10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		//"order": [[1,'desc']],
	    "columns": getPartnerPaymentColumns()
	});
}

function getPartnerPaymentColumns(){
	var ptnrid = {data:  function ( data, type, row ) {
		var partnerid = data.userId ? data.userId : "";
			return '<span class="text-left invoiceclk">'+partnerid+'</span>';
	}};
	var ptnrname = {data:  function ( data, type, row ) {
		var partnername = data.fullname ? data.fullname : "";
			return '<span class="text-left invoiceclk">'+partnername+'</span>';
	}};
	var ptnrmobile = {data:  function ( data, type, row ) {
		var partnermobileno = data.mobilenumber ? data.mobilenumber : "";
			return '<span class="text-left invoiceclk">'+partnermobileno+'</span>';
	}};
	var paymntStatus = {data:  function ( data, type, row ) {
		var paymentStatus = data.paymentStatus ? data.paymentStatus : "Not Paid";
			return '<span class="text-left invoiceclk">'+paymentStatus+'</span>';
	}};
	
	var invoiceFrmPartner = {data:  function ( data, type, row ) {
		var invoiceFromPartner = data.invoiceFromPartner ? data.invoiceFromPartner : "Not Received";
			return '<span class="text-left invoiceclk">'+invoiceFromPartner+'</span>';
	}};
	
	
	var partnrPercentage = {data:  function ( data, type, row ) {
		var partnerPercentage = data.partnerPercentage ? data.partnerPercentage : 0.0;
		return '<span class="text-left invoiceclk">'+partnerPercentage+'</span>';
	}};
	
	var paidAmnt = {data:  function ( data, type, row ) {
		var paidAmount = data.paidAmount ? data.paidAmount : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(paidAmount)+'</span>';
	}};
	
	var subscriptioncost = {data:  function ( data, type, row ) {
		var subscriptionCost = data.subscriptionamount ? data.subscriptionamount : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(subscriptionCost)+'</span>';
	}};
	var commentsEdit = {data:  function ( data, type, row ) {
		return '<img style="width:25px;" data-toggle="modal" data-target="#ppaymentCommentsModal" onclick="showComments(\''+data.userId+'\')" class="cmntsimg mr-2" src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/comments-black.png" /><a class="btn-edt" href="#" data-toggle="modal" data-target="#patnerPaymentModal" onclick="showPartnerPayments(\''+data.userId+'\',\''+data.fullname+'\',\''+data.subscriptionamount+'\',\''+data.partnerPercentage+'\')"><i class="fa fa-edit" style="font-size:22px;vertical-align:sub;"></i></a>';
    }};
	
	return [ptnrid, ptnrname, ptnrmobile, subscriptioncost, partnrPercentage, paidAmnt, invoiceFrmPartner, paymntStatus, commentsEdit];
}