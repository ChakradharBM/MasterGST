
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Upload Invoice</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/client/configuration.css" media="all" />
</head>
<script type="text/javascript">
	var submitStatus = '<c:out value="${client.gstsubmiton}"/>';var othrconfig = '<c:out value="${otherconfig}"/>';var cessStatus = "false";var drStatus = "true";var trStatus = "false";var sStatus = "false";var pStatus = "false";var lsStatus = "true";var lpStatus = "true";var lsStatus = "false";var lpStatus = "false";var tcsStatus = "false"; var rsStatus = "false";var rpStatus = "false";var journalStatus = "false";
	if(othrconfig != ''){cessStatus = '<c:out value="${otherconfig.enableCessQty}"/>';drStatus = '<c:out value="${otherconfig.enableDrcr}"/>'; trStatus = '<c:out value="${otherconfig.enableTransDate}"/>'; sStatus = '<c:out value="${otherconfig.enableSalesFields}"/>'; pStatus = '<c:out value="${otherconfig.enablePurFields}"/>';lsStatus = '<c:out value="${otherconfig.enableLedgerSalesField}"/>'; lpStatus = '<c:out value="${otherconfig.enableLedgerPurField}"/>';rsStatus = '<c:out value="${otherconfig.enableroundoffSalesField}"/>'; rpStatus = '<c:out value="${otherconfig.enableroundoffPurField}"/>';journalStatus = '<c:out value="${otherconfig.enablejournals}"/>';tcsStatus = '<c:out value="${otherconfig.enableTCS}"/>';}
	var rettypeSales = '<c:out value="${rettypeSales}"/>';var rettypePurchase = '<c:out value="${rettypePurchase}"/>';var mnths = '<c:out value="${month}"/>';var type = '<c:out value="${type}"/>';
	var invoices = new Array();var econfig = '<c:out value="${ebillname}"/>';var epwd = '<c:out value="${ebillpwd}"/>';var estatus = '<c:out value="${ebillstatus}"/>';
	var eInvconfig = '<c:out value="${eInvUname}"/>';var eInvpwd = '<c:out value="${eInvPwd}"/>';var eInvstatus = '<c:out value="${eInvstatus}"/>';
	var table;var footnote = "false";var pconfig = '<c:out value="${pconfig}"/>';footnote = '<c:out value="${pconfig.isfooternotescheck}"/>';
	var clientName = '<c:out value="${client.businessname}"/>';var clientEmail = '<c:out value="${client.email}"/>';var clientMobile = '<c:out value="${client.mobilenumber}"/>';
	var clientGstin = '<c:out value="${client.gstnnumber}"/>';var clientSignature = '${clientSignature}';var clntnotes = '<c:out value="${client.notes}"/>';var clntterms = '<c:out value="${client.terms}"/>';
	$(document).ready(function(){
		clntnotes = clntnotes.replaceAll("#mgst# ", "\r\n");
		$("#cust_note").html(clntnotes);
		clntterms = clntterms.replaceAll("#mgst# ", "\r\n");
		$("#terms_condition").html(clntterms);
		table = $('#dbTable').DataTable({
			dom: '<"toolbar"f>Blfrtip<"clear">', 		
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": true,
			"pageLength": 10,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
				   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
			   }
			 }
	});
	$("div.toolbar").html('');	 
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
		if(type == 'reconcileconfig'){$('#reconcileconfig').click();
		}else if(type == 'invoiceconfig'){$('#invoiceconfig').click();
		}else if(type == 'printconfig'){$('#printconfig').click();
		}else if(type == 'ewaybillconfig'){$('#ewaybillconfig').click();
		}else if(type == 'einvoiceconfig'){$('#einvoiceconfig').click();
		}else if(type == 'customfields'){$('#customFieldsconfig').click();}
	   var activeStatus = '<c:out value="${client.digitalSignOn}"/>';
	    	if(activeStatus == 'false' || activeStatus == ' ') {$('#digitalSignature').prop('checked',false);$('.upload_scan_sign').css('display','none');$('.imgsize-wrap-thumb').css('display','none');
			} else {$('#digitalSignature').prop('checked',true);$('.upload_scan_sign').css('display','block');$('.imgsize-wrap-thumb').css('display','block');}
		if(submitStatus == 'On Invoice Creation'){$('#submiton').attr('checked',true);$('#subon').removeAttr('checked');
		}else if(submitStatus == 'On Approve Only'){$('#subon').attr('checked',true);$('#submiton').removeAttr('checked');
		}else{$('#subon').attr('checked',true);} 
	   var currDate = new Date();
	   var mnth = currDate.getMonth()+1; 
		var finyear = "${year-1}-${year}";
		if(mnths > 3){finyear = "${year}-${year+1}";}
		$('#financalYear').val(finyear);
		var notes_old;var terms_old;var invdatesales_old;var invdatepurchase_old;var invdiscount_old;var invcess_old;var invpurcess_old;var einvcess_old;var emailSignature;var invpurdiscount_old;var einvdiscount_old;var invexempted_old;var invledger_old;var invpurledger_old;var reconcile_amt_old;var signatory_old = "";var designation_old = "";var reconcile_allowed_days;
		var invoice_label_text,einvoice_header_label_text,qty_label_text,rate_label_text,authsign_label_text,roundoff,discount,rate,quantity,statecode,pannumber,placeofsupply,designation_old,footer_text,footer_check;
		var itcinput,itcinputservices,itccapitalgood,drcr,trdate,cess,ledgername,sales_fields,purchase_fields,ledgersales_fields,ledgerpurchase_fields,roundoffsales_fields,roundoffpurchase_fields;var username,password,status;
		var ignoreprefix,ignoremonth,ignoreyear,ignorehyphen,ignoreslash,ignorezero,ignorei,ignorel,invoicematch,invfirstfivechar,invlastfivechar;var einvusername,einvpassword,einvstatus;
		$('#cpUploadNav').addClass('active');$('#nav-client').addClass('active');
		var month = currDate.getMonth()+1;
		$('#disp_month').html("  e.g. "+((''+month).length<2 ? '0' : '') + month);$('#disp_year').html("   e.g. "+currDate.getFullYear().toString().substr(-2));
		if(submitStatus != '') {$('input[name=submiton][value="'+submitStatus+'"]').prop('checked', 'checked');}
	});
	$(function(){
			$("#customernotes").keyup(function() {
			if(document.getElementById('customernotes').value==="") {document.getElementById("savebutton").disabled = true;
			}else {document.getElementById("savebutton").disabled = false;}
		});
		$("#termsandconditions").keyup(function() {
			if(document.getElementById('termsandconditions').value==="") {document.getElementById("savebutton").disabled = true;
			}else {document.getElementById("savebutton").disabled = false;}
		});
		$("#authorisedsignatoryname").keyup(function() {
			if(document.getElementById('authorisedsignatoryname').value==="") {document.getElementById('savebutton').disabled = true;
			}else {document.getElementById('savebutton').disabled = false;}
		});
	});
	function editNotes1(){
		$('.designation1,.enableSignatory').css("display","none");$('.auth_designation,.auth_signatory').css("display","block");
		$('#sign').show();$('#dsg').show();$('#sign1').show();$('#sign2').show();
		$('#enablesign1').removeClass('d-none');
		$('.note_terms_divs').removeClass('mb-2');
		$("#enablesign1").css("display","block");
		var cn = $(".ccnotes").text();var tc = $(".tms_cond").text();var sn = $(".auth_signatory").text();var ds = $("#auth_designation").text();var en = $(".enable_auth_signatory").text();
		$('#cancelbutton').css("display","block");$('#savebutton').css("display","block");$('#editbutton').css("display","none");
		document.getElementById("cust_note").innerHTML='<textarea type="text" class="custNotes" id="customernotes" name="customernotes" placeholder="Customer Notes" value="" >'+cn+'</textarea>';
		document.getElementById("terms_condition").innerHTML='<textarea type="text" class="termsCond" id="termsandconditions" name="termsandconditions" placeholder="Terms & Conditions" value="" >'+tc+'</textarea>';
		var ent = $('#enable_auth_signatory_name').text();
		var authsig = $('#clntauthsignatory').text();
		if(ent.trim() == "true"){
			$('#sign1').show();$('#sign2').show();
			document.getElementById("enable_auth_signatory_name").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks" style="margin-top:0px"><div class="form-check" style="margin:0px!important"><input name="enableauthorisedsignatoryname" type="checkbox" id="enableauthorisedsignatoryname" checked><label for="enableauthorisedsignatoryname"><span class="ui"></span></label></div></div>';
			if(sn == ''){document.getElementById("auth_signatory_name").innerHTML='<input type="text" class="autorisedName" id="authorisedsignatoryname" name="authorisedsignatoryname" placeholder="Authorised Signatory Name" style="width: 50%;" value="<c:if test="${not empty client.authorisedSignatory}">'+authsig+'</c:if><c:if test="${empty client.authorisedSignatory}">${client.signatoryName}</c:if>">';		
			}else{document.getElementById("auth_signatory_name").innerHTML='<input type="text" class="autorisedName" id="authorisedsignatoryname" name="authorisedsignatoryname" placeholder="Authorised Signatory Name" value="'+sn+'" style="width: 50%;"/>';}
			if(ds == ''){document.getElementById("auth_designation").innerHTML='<input type="text" class="designation" id="authDesignation" name="designation" placeholder="Designation" value="" style="width: 50%;" >';
			}else{document.getElementById("auth_designation").innerHTML='<input type="text" class="designation" id="authDesignation" name="designation" placeholder="Designation" value="'+ds+'" style="width: 50%;"/>';}
		}else{
		$('#sign1').hide();$('#sign2').hide();
			document.getElementById("enable_auth_signatory_name").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks" style="margin-top:0px"><div class="form-check" style="margin:0px!important"><input name="enableauthorisedsignatoryname" type="checkbox" id="enableauthorisedsignatoryname"><label for="enableauthorisedsignatoryname"><span class="ui"></span></label></div></div>';
		}	
	        $('input[type="checkbox"]').click(function(){
	            if($(this).prop("checked") == true){
	               	$('#sign2').show();$('#sign1').show();
					if(sn == ''){document.getElementById("auth_signatory_name").innerHTML='<input type="text" class="autorisedName" id="authorisedsignatoryname" name="authorisedsignatoryname" placeholder="Authorised Signatory Name" value="<c:if test="${not empty client.authorisedSignatory}">'+authsig+'</c:if><c:if test="${empty client.authorisedSignatory}">${client.signatoryName}</c:if>" style="width: 50%;" >';
			    	}else{document.getElementById("auth_signatory_name").innerHTML='<input type="text" class="autorisedName" id="authorisedsignatoryname" name="authorisedsignatoryname" placeholder="Authorised Signatory Name" value="'+sn+'" style="width: 50%;"/>';}
					if(ds == ''){document.getElementById("auth_designation").innerHTML='<input type="text" class="designation" id="authDesignation" name="designation" placeholder="Designation" value="" style="width: 50%;" >';
					} else{	document.getElementById("auth_designation").innerHTML='<input type="text" class="designation" id="authDesignation" name="designation" placeholder="Designation" value="'+ds+'" style="width: 50%;"/>';}
					document.getElementById('savebutton').disabled = false;
	            }else if($(this).prop("checked") == false){
	               	$('#sign1').hide();$('#sign2').hide();
	            }
	        });
	    $("#customernotes").keyup(function() {
			if(document.getElementById('customernotes').value == "" && document.getElementById('authorisedsignatoryname').value == "") {document.getElementById('savebutton').disabled = true;document.getElementById('cancelbutton').disabled = false;
			}else {document.getElementById('savebutton').disabled = false;}
		});
		$("#termsandconditions").keyup(function() {
			if(document.getElementById('termsandconditions').value== "" && document.getElementById('authorisedsignatoryname').value == "") {document.getElementById('savebutton').disabled = true;
			}else {document.getElementById('savebutton').disabled = false;}
		});
		$("#authorisedsignatoryname").keyup(function() {
			if(document.getElementById('authorisedsignatoryname').value==="") {document.getElementById('savebutton').disabled = true;
			}else {document.getElementById('savebutton').disabled = false;}
		});
	}
	function editconfig(){
		$("#savebutton1,#cancelbutton1,#uplogo").css("display","block");$("#editbutton1,#footnoteDiv").css("display","none");
		var idt = $(".inv_dis_text").text();var qdt = $(".inv_qty_text").text();var rdt = $(".inv_rate_text").text();var ast = $(".inv_authsign_text").text();$('#footernotes').val(footer_text);$('#footernotes').html(footer_text);var einvheadertext = $(".inv_einvheader_text").text();
		document.getElementById("inv_text").innerHTML='<input type="text" class="invo_text_field" id="invoicetext" name="invoicetext" placeholder="invoice text " value="'+idt+'">';
		document.getElementById("einv_header_text").innerHTML='<input type="text" class="einv_header_field" id="einvoiceheadertext" name="einvoiceheadertext" placeholder="einvoice header text " value="'+einvheadertext+'">';
		document.getElementById("inv_qty_text").innerHTML='<input type="text" class="invo_qty_text_field" id="invoiceqtytext" name="qtytext" placeholder="Quantity text " value="'+qdt+'">';
		document.getElementById("inv_rate_text").innerHTML='<input type="text" class="invo_rate_text_field" id="invoiceratetext" name="ratetext" placeholder="Rate text " value="'+rdt+'">';
		document.getElementById("inv_authsign_text").innerHTML='<input type="text" class="invo_authsign_text_field" id="invoiceauthsigntext" name="authsigntext" placeholder="Authorised Signatory text " value="'+ast+'">';
		var edf = $('#enable_discount_field').text();var era = $('#enable_roundoff_field').text();var fcv = $('#footnotecheckval').text();
		if(footer_check == true || footer_check == "true"){$('#footnotecheckval').html('Yes');$('#footnotecheckval').css("color","green");}else{$('#footnotecheckval').html('No');$('#footnotecheckval').css("color","red");}
		if(fcv == "Yes"){
			document.getElementById("footnotecheckval").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-2 meterialform bs-fancy-checks labels" style="margin-left: -10px;"><div class="form-check" style="margin:0px!important"><input name="enablefootcheckfield" type="checkbox" id="enablefootcheckfield" checked><label for="enablefootcheckfield"><span class="ui"></span></label></div></div>';
			$("#footernotes").css("display","block");
		}else if(fcv == '' || fcv == 'No'){
			document.getElementById("footnotecheckval").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-2 meterialform bs-fancy-checks labels" style="margin-left: -10px;"><div class="form-check" style="margin:0px!important"><input name="enablefootcheckfield" type="checkbox" id="enablefootcheckfield"><label for="enablefootcheckfield"><span class="ui"></span></label></div></div>';
			$("#footernotes").css("display","none");	
		}
		if(era == "Yes"){
			document.getElementById("enable_roundoff_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-2 meterialform bs-fancy-checks labels" style="margin-left: -10px;"><div class="form-check" style="margin:0px!important"><input name="enableroundofffield" type="checkbox" id="enableroundofffield" checked><label for="enableroundofffield"><span class="ui"></span></label></div></div>';
			}else if(era == '' || era == 'No'){
			document.getElementById("enable_roundoff_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-2 meterialform bs-fancy-checks labels" style="margin-left: -10px;"><div class="form-check" style="margin:0px!important"><input name="enableroundofffield" type="checkbox" id="enableroundofffield"><label for="enableroundofffield"><span class="ui"></span></label></div></div>';
			}
		if(edf == "Yes"){
		document.getElementById("enable_discount_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablediscountfield" type="checkbox" id="enablediscountfield" checked><label for="enablediscountfield"><span class="ui"></span></label></div></div>';
		}else if(edf == '' || edf == 'No'){
		document.getElementById("enable_discount_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablediscountfield" type="checkbox" id="enablediscountfield"><label for="enablediscountfield"><span class="ui"></span></label></div></div>';
		}
		var erf = $('#enable_rate_field').text();
		if(erf == 'Yes'){
		document.getElementById("enable_rate_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableratefield" type="checkbox" id="enableratefield" checked><label for="enableratefield"><span class="ui"></span></label></div></div>';
		}else if(erf == '' || erf == 'No'){
		document.getElementById("enable_rate_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableratefield" type="checkbox" id="enableratefield"><label for="enableratefield"><span class="ui"></span></label></div></div>';
		}
		var eqf = $('#enable_qty_field').text();
		if(eqf == 'Yes'){
		document.getElementById("enable_qty_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableqtyfield" type="checkbox" id="enableqtyfield" checked><label for="enableqtyfield"><span class="ui"></span></label></div></div>';
		}else if(eqf == '' || eqf == 'No'){
		document.getElementById("enable_qty_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableqtyfield" type="checkbox" id="enableqtyfield"><label for="enableqtyfield"><span class="ui"></span></label></div></div>';
		}
		var esf = $('#enable_state_field').text();
		if(esf == '' || esf == 'Yes'){
		document.getElementById("enable_state_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablestatefield" type="checkbox" id="enablestatefield" checked disabled><label for="enablestatefield"><span class="ui"></span></label></div></div>';
		}else if(esf == 'No'){
		document.getElementById("enable_state_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablestatefield" type="checkbox" id="enablestatefield" disabled><label for="enablestatefield"><span class="ui"></span></label></div></div>';
		}
		var epf = $('#enable_pan_field').text();
		if(epf == '' || epf == 'Yes'){
		document.getElementById("enable_pan_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablepanfield" type="checkbox" id="enablepanfield" checked disabled><label for="enablepanfield"><span class="ui"></span></label></div></div>';
		}else if(epf == 'No'){
		document.getElementById("enable_pan_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablepanfield" type="checkbox" id="enablepanfield" disabled><label for="enablepanfield"><span class="ui"></span></label></div></div>';
		}
		var eposf = $('#enable_placeofsupply_field').text();
		if(eposf == '' || eposf == 'Yes'){
		document.getElementById("enable_placeofsupply_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableplaceofsupplyfield" type="checkbox" id="enableplaceofsupplyfield" checked disabled><label for="enableplaceofsupplyfield"><span class="ui"></span></label></div></div>';
		}else if(eposf == 'No'){
		document.getElementById("enable_placeofsupply_field").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableplaceofsupplyfield" type="checkbox" id="enableplaceofsupplyfield" disabled><label for="enableplaceofsupplyfield"><span class="ui"></span></label></div></div>';
		}
		$('#enablefootcheckfield').click(function(){
		if($("#enablefootcheckfield").is(':checked')){$('#footernotes').css("display","block");}else{$('#footernotes').css("display","none");}
		});
	}
	function editother(){
		$('#other_savebutton,#enable_invdate_field,#enable_trdate_field,#enable_cqty_field,#enable_ctaxAmt_field, #other_cancelbutton,#itcblock,#itcinput,#itcinputservices,#itccapitalgood,#enable_drcr_field,#enable_byto_field').css('display','inline-block');
		$('#other_editbutton').css('display','none');
		$(".tdate_invdate,.cqty_ctaxAmt").text("");
		$(".trradiobtn,.cessradiobtn").css('display','inline-block');
		var itc_input = $(".itc_input").text();var itc_inputservices = $(".itc_inputservices").text();var itc_capitalgood = $(".itc_capitalgood").text();var dr = $("input[id='enabledrcr']:checked").val();var bt = $("input[id='enablebyto']:checked").val();var td = $("input[id='enabletransdate']:checked").val();var cq = $("input[id='enablecessqty']:checked").val();var id = $("input[id='enableinvdate']:checked").val();var cq = $("input[id='enablecessqty']:checked").val();var ct = $("input[id='enablecesstaxable']:checked").val();var eis = $('#enable_invsales_field').text();var eip = $('#enable_invpurchase_field').text();var leis = $('#enable_invledgersales_field').text();var leip = $('#enable_invledgerpurchase_field').text();var rois = $('#enable_invroundoffsales_field').text();var roip = $('#enable_invroundoffpurchase_field').text();var journal = $('#enable_journals_field').text();var tcs = $('#enable_tcs_field').text();$('#itcinput').val('100'); $('#itcinputservices').val('100'); $('#itccapitalgood').val('100');
		document.getElementById("itc_input").innerHTML='<input type="text" maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$" class="form-control" id="itcinput" style="border:1px solid lightgray;height: 22px;width:45px;margin-bottom:10px;margin-left: -6px;margin-top: 0px;" value="'+itc_input+'">';
		document.getElementById("itc_inputservices").innerHTML='<input type="text" maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$" class="form-control" id="itcinputservices" style="border:1px solid lightgray;height: 22px;width:45px;margin-bottom:10px;margin-left: -6px;" value="'+itc_inputservices+'">';
		document.getElementById("itc_capitalgood").innerHTML='<input type="text" maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$" class="form-control" id="itccapitalgood" style="border:1px solid lightgray;height: 22px;width:45px;margin-bottom:10px;margin-left: -6px;" value="'+itc_capitalgood+'">';
		document.getElementById("enable_drcr_field").innerHTML='<label class="radiobtn"><input name="enabledrcr" id="enabledrcr" class="enabledrcr" type="radio" value="true"/><i class="helper"></i>Dr/Cr</label>';
		document.getElementById("enable_byto_field").innerHTML='<label class="radiobtn"><input name="enabledrcr" id="enablebyto" class="enabledrcr" type="radio" value="false"/><i class="helper"></i>By/To</label>';
		document.getElementById("enable_trdate_field").innerHTML='<label class="radiobtn"><input name="enabletransdate" id="enabletransdate" class="enabletransdate" type="radio" value="true" /><i class="helper"></i>Transaction Date</label>';
		document.getElementById("enable_invdate_field").innerHTML='<label class="radiobtn"><input name="enabletransdate" id="enableinvdate" class="enabletransdate" type="radio" value="false"/><i class="helper"></i>Invoice Date</label>';
		document.getElementById("enable_cqty_field").innerHTML='<label class="radiobtn"><input name="enablecessqty" id="enablecessqty" class="enablecessqty" type="radio" value="true" /><i class="helper"></i>Quantity</label>';
		document.getElementById("enable_ctaxAmt_field").innerHTML='<label class="radiobtn"><input name="enablecessqty" id="enablecesstaxable" class="enablecessqty" type="radio" value="false"/><i class="helper"></i>Taxable Value</label>';
		if(leis == "Yes"){
			document.getElementById("enable_invledgersales_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableLedgerSalesField" type="checkbox" id="enableLedgerSalesField" checked><label for="enableLedgerSalesField"><span class="ui"></span></label></div></div>';
			}else if(leis == '' || leis == 'No'){
			document.getElementById("enable_invledgersales_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableLedgerSalesField" type="checkbox" id="enableLedgerSalesField"><label for="enableLedgerSalesField"><span class="ui"></span></label></div></div>';
			}
		if(leip == "Yes"){
			document.getElementById("enable_invledgerpurchase_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableLedgerPurField" type="checkbox" id="enableLedgerPurField" checked><label for="enableLedgerPurField"><span class="ui"></span></label></div></div>';
			}else if(leip == '' || leip == 'No'){
			document.getElementById("enable_invledgerpurchase_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableLedgerPurField" type="checkbox" id="enableLedgerPurField"><label for="enableLedgerPurField"><span class="ui"></span></label></div></div>';
			}
		if(eis == "Yes"){
			document.getElementById("enable_invsales_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableSalesFields" type="checkbox" id="enablesalesfield" checked><label for="enablesalesfield"><span class="ui"></span></label></div></div>';
			}else if(eis == '' || eis == 'No'){
			document.getElementById("enable_invsales_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableSalesFields" type="checkbox" id="enablesalesfield"><label for="enablesalesfield"><span class="ui"></span></label></div></div>';
			}
		if(eip == "Yes"){
			document.getElementById("enable_invpurchase_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablePurFields" type="checkbox" id="enablepurfield" checked><label for="enablepurfield"><span class="ui"></span></label></div></div>';
			}else if(eip == '' || eip == 'No'){
			document.getElementById("enable_invpurchase_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablePurFields" type="checkbox" id="enablepurfield"><label for="enablepurfield"><span class="ui"></span></label></div></div>';
			}
		if(rois == "Yes"){
			document.getElementById("enable_invroundoffsales_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableroundoffSalesField" type="checkbox" id="enableroundoffSalesField" checked><label for="enableroundoffSalesField"><span class="ui"></span></label></div></div>';
			}else if(rois == '' || rois == 'No'){
			document.getElementById("enable_invroundoffsales_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableroundoffSalesField" type="checkbox" id="enableroundoffSalesField"><label for="enableroundoffSalesField"><span class="ui"></span></label></div></div>';
			}
		if(roip == "Yes"){
			document.getElementById("enable_invroundoffpurchase_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableroundoffPurField" type="checkbox" id="enableroundoffPurField" checked><label for="enableroundoffPurField"><span class="ui"></span></label></div></div>';
		}else if(roip == '' || roip == 'No'){
			document.getElementById("enable_invroundoffpurchase_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableroundoffPurField" type="checkbox" id="enableroundoffPurField"><label for="enableroundoffPurField"><span class="ui"></span></label></div></div>';
		}
		if(journal == "Yes"){
			document.getElementById("enable_journals_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablejournals" type="checkbox" id="enablejournals" checked><label for="enablejournals"><span class="ui"></span></label></div></div>';
		}else if(journal == '' || journal == 'No'){
			document.getElementById("enable_journals_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablejournals" type="checkbox" id="enablejournals"><label for="enablejournals"><span class="ui"></span></label></div></div>';
		}
		if(tcs == "Yes"){
			document.getElementById("enable_tcs_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enabletcs" type="checkbox" id="enabletcs" checked><label for="enabletcs"><span class="ui"></span></label></div></div>';
		}else if(tcs == '' || tcs == 'No'){
			document.getElementById("enable_tcs_field").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enabletcs" type="checkbox" id="enabletcs"><label for="enabletcs"><span class="ui"></span></label></div></div>';
		}
		var ip = $('#itcinput').val();
		var is = $('#itcinputservices').val();
		var cg = $('#itccapitalgood').val();
		if(ip == '' && is == '' && cg == ''){
			$('#itcinput').val('100');
			 $('#itcinputservices').val('100');
			 $('#itccapitalgood').val('100');
		}
		$("#itcinput").keyup(function() {
			var p = document.getElementById('itcinput');
			if(p != null && p.value>100){
				$('#itcinput').val('100');
			}		
		});
		$("#itccapitalgood").keyup(function() {
			var p = document.getElementById('itccapitalgood');
			if(p != null && p.value>100){
				$('#itccapitalgood').val('100');
			}		
		});
		$("#itcinputservices").keyup(function() {
			var p = document.getElementById('itcinputservices');
			if(p != null && p.value>100){
				$('#itcinputservices').val('100');
			}	
		});
		if(drStatus == "true"){
			$('#enabledrcr').attr('checked',true);
		}else{
			$('#enablebyto').attr('checked',true);
		}
		if(trStatus == "true"){
			$('#enabletransdate').attr('checked',true);
		}else{
			$('#enableinvdate').attr('checked',true);
		}	
		if(cessStatus == "true"){
			$('#enablecessqty').attr('checked',true);
		}else{
			$('#enablecesstaxable').attr('checked',true);
		}	
	}
	function editebillconfig(){
		$('#ewaybill_cancelbutton,#ewaybill_savebutton,#ewaybill_authbutton').css("display","block");
		$('#ewaybill_editbutton').css("display","none");
		var un = $("#uname").text();var pwd = $("#pwd").text();var status = $("#connStatus").text();
		document.getElementById("uname").innerHTML='<input type="text" class="form-control username" id="username" style="width:200px;height: 31px;" name="userName" placeholder="User name" value="'+econfig+'" >';
		document.getElementById("pwd").innerHTML='<input type="password" class="form-control password" id="password"  style="width:200px;height: 31px;" name="password" placeholder="............" value="'+epwd+'" >';
		$('#connStatus').html(status);
	}
	function editNotes(){
		$("#enablesign").removeClass('d-none').css("display","block");
		$('.enableSignatory').css("display","none");
		$('.note_terms_divs').removeClass('mb-2');
		var cn = $(".ccnotes").text();
		var tc = $(".tms_cond").text();
		var en = $(".enable_auth_signatory").val();
		var sn = $(".auth_signatory").text();
		var ds = $("#auth_designation").text();						
		$('#cancelbutton,#savebutton').css("display","block");
		$('#cancelbutton').attr("onclick","cancelnotes2()");
		$('#editbutton').css("display","none");
		document.getElementById("cust_note").innerHTML='<textarea type="text" class="custNotes" id="customernotes" name="customernotes" placeholder="Customer Notes" value="" >'+cn+'</textarea>';
		document.getElementById("terms_condition").innerHTML='<textarea type="text" class="termsCond" id="termsandconditions" name="termsandconditions" placeholder="Terms & Conditions" value="" >'+tc+'</textarea>';
		var ent = $('#enable_auth_signatory_name').text();
		var authsig = $('#clntauthsignatory').text();
		if(ent.trim() == "true"){
			$('#sign').show();$('#dsg').show();
			$('.auth_signatory,.auth_designation').css("display","block");
			document.getElementById("enable_auth_signatory_name").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks" style="margin-top:0px"><div class="form-check" style="margin:0px!important"><input name="enableauthorisedsignatoryname" type="checkbox" id="enableauthorisedsignatoryname" checked><label for="enableauthorisedsignatoryname"><span class="ui"></span></label></div></div>';
			document.getElementById("auth_signatory_name").innerHTML='<input type="text" class="autorisedName" id="authorisedsignatoryname" name="authorisedsignatoryname" style="width: 50%;!important"  placeholder="Authorised Signatory Name" value="<c:if test="${not empty client.authorisedSignatory}">'+authsig+'</c:if><c:if test="${empty client.authorisedSignatory}">${client.signatoryName}</c:if>" >';		
			document.getElementById("auth_designation").innerHTML='<input type="text" class="designation" id="authDesignation" name="designation"  style="width: 50%!important;" placeholder="Designation" value="<c:if test="${not empty client.designation}">${client.designation}</c:if>" >';
		}else if(ent == ''){
		$('#sign').hide();$('#dsg').hide();
			document.getElementById("enable_auth_signatory_name").innerHTML='<div class="form-group col-md-6 col-sm-12 pl-5 meterialform bs-fancy-checks" style="margin-top:0px"><div class="form-check" style="margin:0px!important"><input name="enableauthorisedsignatoryname" type="checkbox" id="enableauthorisedsignatoryname"><label for="enableauthorisedsignatoryname"><span class="ui"></span></label></div></div>';
		}
	        $('input[type="checkbox"]').click(function(){
	            if($(this).prop("checked") == true){
	            	$('#dsg').show();$('#sign').show();$('#sign2').hide();
	            	$('.auth_designation').css("display","block");
					document.getElementById("auth_signatory_name").innerHTML='<input type="text" class="autorisedName" id="authorisedsignatoryname" name="authorisedsignatoryname" style="width: 50%!important;" placeholder="Authorised Signatory Name" value="<c:if test="${not empty client.authorisedSignatory}">'+authsig+'</c:if><c:if test="${empty client.authorisedSignatory}">${client.signatoryName}</c:if>"  >';		
					document.getElementById("auth_designation").innerHTML='<input type="text" class="designation" id="authDesignation" name="designation" placeholder="Designation" value="" style="width: 50%;" >';
					document.getElementById('savebutton').disabled = false;
	            }else if($(this).prop("checked") == false){
	            	document.getElementById('savebutton').disabled = true;
	            	$('#sign').hide();$('#dsg').hide();
	            }
	        });
    	$("#customernotes").keyup(function() {
			if(document.getElementById('customernotes').value =="" && document.getElementById('authorisedsignatoryname').value == "") {
				document.getElementById('savebutton').disabled = true;document.getElementById('cancelbutton').disabled = false;
			}else {
				document.getElementById('savebutton').disabled = false;
			}
		});
		$("#termsandconditions").keyup(function() {
			if(document.getElementById('termsandconditions').value == "" && document.getElementById('authorisedsignatoryname').value == "") {
				document.getElementById('savebutton').disabled = true;
			}else {
				document.getElementById('savebutton').disabled = false;
			}
		});
		$("#authorisedsignatoryname").keyup(function() {
			if(document.getElementById('authorisedsignatoryname').value == "") {
				document.getElementById('savebutton').disabled = true;
			}else {
				document.getElementById('savebutton').disabled = false;
			}
		});
	}
	function authebillconfig(){
		$('#ewayBillAuthError').html("");
		var uname = $("#username").val();var pwd = $("#password").val();var status = $("#connStatus").text();
		$('#ewaybill_authbutton').addClass("btn-loader");
		$.ajax({
			url: "${contextPath}/authEbillConfig/${client.id}",
			data: {
				'username': uname,
				'password': pwd
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				var s1 = response;
				var s2 = s1.substr(1);
				 if(estatus == "Active"){
						$('#connStatus').html("Active").css("color","green");
					}else{
						$('#connStatus').html("InActive").css("color","red");
					} 
				 if(response.length != 0){
						$('#ewayBillAuthError').html(s2);
						$('#connStatus').html("InActive").css("color","red");
					}else{
						$('#ewayBillAuthError').html("");
						$('#connStatus').html("Active").css("color","green");
						$("#uname").html(uname);$("#pwd").html(pwd);
					}
				 $('#ewaybill_authbutton').removeClass("btn-loader");
				username = uname;password = pwd;
	},
	     error : function(e) {
			if(e.responseText) {
				errorNotification(e.responseText);
				$('#ewaybill_authbutton').removeClass("btn-loader");
			}
		}
		});
	}
	function saveebillconfig(){
		$("#ewaybill_savebutton,#ewaybill_cancelbutton,#ewaybill_authbutton").css('display','none');
		$("#ewaybill_editbutton").css('display','block');
		var uname = $("#username").val();
		var pwd = $("#password").val();
		
		$.ajax({
			url: "${contextPath}/saveewaybillconfig/${client.id}",
			data: {
				'username': uname,
				'password': pwd
				
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				successNotification('Save successful!');
				//otherconfigdetails = response;
				$("#uname").html(uname);
				$("#pwd").html(pwd);
				 if(estatus == "Active"){
					$('#connStatus').html("Active").css("color","green");;
				}else{
					$('#connStatus').html("InActive").css("color","red");;
				} 
				
				username = uname;password = pwd;
	},
	     error : function(e) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
		});
	}
	function saveother(){
		$("#other_savebutton,#other_cancelbutton,#enable_byto_field,#enable_invdate_field").css('display','none');
		$("#other_editbutton").css('display','block');
		$(".trradiobtn,.cessradiobtn").css('display','none');
		var enable_drcr_field = $("input[class='enabledrcr']:checked").val();
		var enable_trdate_field = $("input[class='enabletransdate']:checked").val();
		var enable_cess_field = $("input[class='enablecessqty']:checked").val();
		var enable_sales_field=false;
		if(!$('#enablesalesfield').is(':checked')){
			enable_sales_field=true;
		}
		var enable_pur_field=false;
		if(!$('#enablepurfield').is(':checked')){
			enable_pur_field=true;
		}
		var enable_ledgersales_field=false;
		if(!$('#enableLedgerSalesField').is(':checked')){
			enable_ledgersales_field=true;
		}
		var enable_ledgerpur_field=false;
		if(!$('#enableLedgerPurField').is(':checked')){
			enable_ledgerpur_field=true;
		}
		var enable_roundoffsales_field=false;
		if(!$('#enableroundoffSalesField').is(':checked')){
			enable_roundoffsales_field=true;
		}
		var enable_roundoffpur_field=false;
		if(!$('#enableroundoffPurField').is(':checked')){
			enable_roundoffpur_field=true;
		}
		var enable_journal_field=false;
		if(!$('#enablejournals').is(':checked')){
			enable_journal_field=true;
		}
		var enable_tcs_field=false;
		if(!$('#enabletcs').is(':checked')){
			enable_tcs_field=true;
		}
		var inputtxt = $('#itcinput').val();
		var inputservice = $('#itcinputservices').val();
		var capitalgood = $('#itccapitalgood').val();
		drStatus = enable_drcr_field;
		trStatus = enable_trdate_field;
		cessStatus = enable_cess_field;
		sStatus = enable_sales_field;
		pStatus = enable_pur_field;
		lsStatus = enable_ledgersales_field;
		lpStatus = enable_ledgerpur_field;
		rsStatus = enable_roundoffsales_field;
		rpStatus = enable_roundoffpur_field;
		journalStatus = enable_journal_field;
		tcsStatus = enable_tcs_field;
		$.ajax({
			url: "${contextPath}/saveothers/${client.id}",
			data: {
				'itcinput': inputtxt,
				'itcinputservice': inputservice,
				'itccapgood': capitalgood,
				'enableDrcr':enable_drcr_field,
				'enableTransDate':enable_trdate_field,
				'enableCessQty':enable_cess_field,
				'enableSalesFields':enable_sales_field,
				'enablePurFields':enable_pur_field,
				'enableLedgerSalesField':enable_ledgersales_field,
				'enableLedgerPurField':enable_ledgerpur_field,
				'enableRoundoffSalesField':enable_roundoffsales_field,
				'enableroundoffPurField':enable_roundoffpur_field,
				'enablejournals':enable_journal_field,
				'enabletcs':enable_tcs_field
					
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				successNotification('Save successful!');
				otherconfigdetails = response;
				$("#itc_input").html(inputtxt);
				$("#itc_inputservices").html(inputservice);
				$("#itc_capitalgood").html(capitalgood);
				itcinput = inputtxt;itcinputservices = inputservice;itccapitalgood = capitalgood;
		 if(enable_drcr_field == "true"){
			 $('#enable_drcr_field').text('Dr/Cr');
		}else{
			$('#enable_drcr_field').text('By/To');
		}
		if(enable_trdate_field == "true"){
			$('.tdate_invdate').text('Transaction Date');
		}else{
			$('.tdate_invdate').text('Invoice Date');
		}
		if(enable_cess_field == "true"){
			$('.cqty_ctaxAmt').text('Quantity');
		}else{
			$('.cqty_ctaxAmt').text('Taxable Value');
		}
		if(lsStatus == "false"){
			$('#enable_invledgersales_field').text("Yes");
		}else{
			$('#enable_invledgersales_field').text("No");
		}
		if(lpStatus == "false"){
			$('#enable_invledgerpurchase_field').text("Yes");
		}else{
			$('#enable_invledgerpurchase_field').text("No");
		}
		if(rsStatus == "false"){
			$('#enable_invroundoffsales_field').text("Yes");
		}else{
			$('#enable_invroundoffsales_field').text("No");
		}
		if(rpStatus == "false"){
			$('#enable_invroundoffpurchase_field').text("Yes");
		}else{
			$('#enable_invroundoffpurchase_field').text("No");
		}
		if(journalStatus == "false"){
			$('#enable_journals_field').text("Yes");
		}else{
			$('#enable_journals_field').text("No");
		}
		if(tcsStatus == "false"){
			$('#enable_tcs_field').text("Yes");
		}else{
			$('#enable_tcs_field').text("No");
		}
		if(sStatus == "false"){
			$('#enable_invsales_field').text("Yes");
		}else{
			$('#enable_invsales_field').text("No");
		}
		if(pStatus == "false"){
			$('#enable_invpurchase_field').text("Yes");
		}else{
			$('#enable_invpurchase_field').text("No");
		}
		if(enable_ledgersales_field == false){
			ledgersales_fields = 'Yes';
					$('#enable_invledgersales_field').html('Yes');
					$('#enable_invledgersales_field').css("display","inline-block");
					$('#enable_invledgersales_field').css("color","green");
				}else{
					ledgersales_fields = 'No';
					$('#enable_invledgersales_field').css("display","inline-block");
					$('#enable_invledgersales_field').html('No');
					$('#enable_invledgersales_field').css("color","red");
				}
		if(enable_ledgerpur_field == false){
			ledgerpurchase_fields = 'Yes';
					$('#enable_invledgerpurchase_field').html('Yes');
					$('#enable_invledgerpurchase_field').css("display","inline-block");
					$('#enable_invledgerpurchase_field').css("color","green");
				}else{
					ledgerpurchase_fields = 'No';
					$('#enable_invledgerpurchase_field').css("display","inline-block");
					$('#enable_invledgerpurchase_field').html('No');
					$('#enable_invledgerpurchase_field').css("color","red");
				}
		if(enable_roundoffsales_field == false){
			roundoffsales_fields = 'Yes';
					$('#enable_invroundoffsales_field').html('Yes');
					$('#enable_invroundoffsales_field').css("display","inline-block");
					$('#enable_invroundoffsales_field').css("color","green");
				}else{
					roundoffsales_fields = 'No';
					$('#enable_invroundoffsales_field').css("display","inline-block");
					$('#enable_invroundoffsales_field').html('No');
					$('#enable_invroundoffsales_field').css("color","red");
				}
		if(enable_roundoffpur_field == false){
			roundoffpurchase_fields = 'Yes';
			$('#enable_invroundoffpurchase_field').html('Yes');
			$('#enable_invroundoffpurchase_field').css("display","inline-block");
			$('#enable_invroundoffpurchase_field').css("color","green");
		}else{
			roundoffpurchase_fields = 'No';
			$('#enable_invroundoffpurchase_field').css("display","inline-block");
			$('#enable_invroundoffpurchase_field').html('No');
			$('#enable_invroundoffpurchase_field').css("color","red");
		}
		if(enable_journal_field == false){
			journal_fields = 'Yes';
			$('#enable_journals_field').html('Yes');
			$('#enable_journals_field').css("display","inline-block");
			$('#enable_journals_field').css("color","green");
		}else{
			journal_fields = 'No';
			$('#enable_journals_field').css("display","inline-block");
			$('#enable_journals_field').html('No');
			$('#enable_journals_field').css("color","red");
		}
		if(enable_tcs_field == false){
			tcs_fields = 'Yes';
			$('#enable_tcs_field').html('Yes');
			$('#enable_tcs_field').css("display","inline-block");
			$('#enable_tcs_field').css("color","green");
		}else{
			tcs_fields = 'No';
			$('#enable_tcs_field').css("display","inline-block");
			$('#enable_tcs_field').html('No');
			$('#enable_tcs_field').css("color","red");
		}
		if(enable_sales_field == false){
			sales_fields = 'Yes';
					$('#enable_invsales_field').html('Yes');
					$('#enable_invsales_field').css("display","inline-block");
					$('#enable_invsales_field').css("color","green");
				}else{
					sales_fields = 'No';
					$('#enable_invsales_field').css("display","inline-block");
					$('#enable_invsales_field').html('No');
					$('#enable_invsales_field').css("color","red");
				}
		if(enable_pur_field == false){
			purchase_fields = 'Yes';
					$('#enable_invpurchase_field').html('Yes');
					$('#enable_invpurchase_field').css("display","inline-block");
					$('#enable_invpurchase_field').css("color","green");
				}else{
					purchase_fields = 'No';
					$('#enable_invpurchase_field').css("display","inline-block");
					$('#enable_invpurchase_field').html('No');
					$('#enable_invpurchase_field').css("color","red");
				}
		
	},
	     error : function(e) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
		});
	}
	function saveconfig(){
    	$("#savebutton1,#cancelbutton1").css("display","none");
		document.getElementById("editbutton1").style.display = "inline";
		document.getElementById("editbutton1").disabled = false;
		var enable_roundoff_field=$('#enableroundofffield').is(':checked');
		var enable_discount_field=$('#enablediscountfield').is(':checked');
		var enable_rate_field=$('#enableratefield').is(':checked');
		var enable_qty_field=$('#enableqtyfield').is(':checked');
		var enable_state_field=$('#enablestatefield').is(':checked');
		var enable_pan_field=$('#enablepanfield').is(':checked');
		var enable_placeofsupply_field=$('#enableplaceofsupplyfield').is(':checked');var enable_footernotes=$('#enablefootcheckfield').is(':checked');
		var invtext = $('#invoicetext').val();var qtytext = $('#invoiceqtytext').val();var ratetext = $('#invoiceratetext').val();var footnotes = $('#footernotes').val();var einvoiceheadertext = $('#einvoiceheadertext').val();
		var authsigntext = $('#invoiceauthsigntext').val();
		$.ajax({
			url: "${contextPath}/saveprintconfig/${client.id}",
			data: {
				'invoicetext': invtext,
				'qtytext': qtytext,
				'einvoiceheadertext':einvoiceheadertext,
				'ratetext': ratetext,
				'authsigntext': authsigntext,
				'footernotes':footnotes,
				'enableffooternotes':enable_footernotes,
				'enableRoundOffAmt':enable_roundoff_field,
				'enableDiscountField':enable_discount_field,
					'enableRateField':enable_rate_field,
					'enableQtyField':enable_qty_field,
					'enableStateField':enable_state_field,
					'enablePanField':enable_pan_field,
					'enablePlaceOfSupplyField':enable_placeofsupply_field
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				successNotification('Save successful!');
				$("#inv_text").html(invtext);$('.checkDiv,#footernotes').css("display","none");$('#einv_header_text').html(einvoiceheadertext);
				$("#inv_qty_text").html(qtytext);$("#inv_rate_text").html(ratetext);$('#inv_authsign_text').html(authsigntext);
				if(enable_footernotes == true || enable_footernotes == "true"){
					footer_check = 'Yes';footer_text = footnotes;$('#footnoteval').html(footnotes);$('#footnoteDiv').css("display","block");$('#footnotecheckval').html('Yes');$('#footnotecheckval').css("color","green");
				} else{footer_check = 'No';footer_text = '';$('#footnoteDiv').css("display","none");$('#footnotecheckval').html('No');$('#footnotecheckval').css("color","red");$('#footernotes').val('');}
				invoice_label_text = invtext;
				einvoice_header_label_text = einvoiceheadertext;
				if(enable_roundoff_field == true){roundoff = 'Yes';$('#enable_roundoff_field').html('Yes');$('#enable_roundoff_field').css("display","inline-block");$('#enable_roundoff_field').css("color","green");
				}else{roundoff = 'No';$('#enable_roundoff_field').css("display","inline-block");$('#enable_roundoff_field').html('No');$('#enable_roundoff_field').css("color","red");}
				if(enable_discount_field == true){discount = 'Yes';$('#enable_discount_field').html('Yes');$('#enable_discount_field').css("display","inline-block");$('#enable_discount_field').css("color","green");
				}else{discount = 'No';$('#enable_discount_field').css("display","inline-block");$('#enable_discount_field').html('No');$('#enable_discount_field').css("color","red");}
				if(enable_rate_field == true){rate = 'Yes';$('#enable_rate_field').html('Yes');$('#enable_rate_field').css("display","inline-block");$('#enable_rate_field').css("color","green");
				}else{rate = 'No';$('#enable_rate_field').css("display","inline-block");$('#enable_rate_field').html('No');$('#enable_rate_field').css("color","red");}
				if(enable_qty_field == true){quantity = 'Yes';$('#enable_qty_field').html('Yes');$('#enable_qty_field').css("display","inline-block");$('#enable_qty_field').css("color","green");
				}else{quantity = 'No';$('#enable_qty_field').css("display","inline-block");$('#enable_qty_field').html('No');$('#enable_qty_field').css("color","red");}
				if(enable_state_field == true){statecode = 'Yes';$('#enable_state_field').html('Yes');$('#enable_state_field').css("display","inline-block");$('#enable_state_field').css("color","green");
				}else{statecode = 'No';$('#enable_state_field').css("display","inline-block");$('#enable_state_field').html('No');$('#enable_state_field').css("color","red");}
				if(enable_pan_field == true){pannumber = 'Yes';$('#enable_pan_field').html('Yes');$('#enable_pan_field').css("display","inline-block");$('#enable_pan_field').css("color","green");
				}else{pannumber = 'No';$('#enable_pan_field').css("display","inline-block");$('#enable_pan_field').html('No');$('#enable_pan_field').css("color","red");}
				if(enable_placeofsupply_field == true){placeofsupply = 'Yes';$('#enable_placeofsupply_field').html('Yes');$('#enable_placeofsupply_field').css("display","inline-block");$('#enable_placeofsupply_field').css("color","green");
				}else{placeofsupply = 'No';$('#enable_placeofsupply_field').css("display","inline-block");$('#enable_placeofsupply_field').html('No');$('#enable_placeofsupply_field').css("color","red");}
		},
	     error : function(e) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
		});
	}
	function saveNotes1() {
     	$('#sign1').show();$('#sign2').show();
		$('#enablesign1').hide();
		$('.note_terms_divs').addClass('mb-2');
		var notes = $('#customernotes').val();
		var terms =	$('#termsandconditions').val();
		var enableSignatoryName=$('#enableauthorisedsignatoryname').is(':checked');
		var signatoryName =	$('#authorisedsignatoryname').val();
		var designation = $('#authDesignation').val();
		$.ajax({
			url: "${contextPath}/saveterms/${client.id}",
			data: {
				'terms': terms,
				'notes': notes,
				'enableSignatoryName' : enableSignatoryName,
				'authorisedSignatory':signatoryName,
				'designation':designation
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				successNotification('Save successful!');
				$("#cust_note").html(notes);
				$("#terms_condition").html(terms);
				if(enableSignatoryName == true){
				    $("#auth_signatory_name").html(signatoryName);
					$("#auth_designation").html(designation);
					$('.enableSignatory').html('Yes');
					$('.enableSignatory').css("display","inline-block");
					$('.enableSignatory').css("color","green");
					$('.designation1').html('Yes');
					$('.designation1').css("display","inline-block");
					$('.designation1').css("color","green");
				}else{
					$("#auth_signatory_name").html(signatoryName);
					$("#auth_designation").html(designation);
					$("#auth_signatory_name").css("display","none");
					$('.enableSignatory').css("display","inline-block");
					$('.enableSignatory').html('No');
					$('.enableSignatory').css("color","red");
					$("#auth_designation").css("display","none");
					$('.designation1').css("display","inline-block");
					$('.designation1').html('No');
					$('.designation1').css("color","red");
				}
				$("#enable_auth_signatory_name").html(enableSignatoryName)
				$("#savebutton").css("display","none");
				$("#cancelbutton").css("display","none");
				document.getElementById("editbutton").style.display = "inline";
				document.getElementById("editbutton").disabled = false;
			},
			error : function(e) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	function saveNotes() {
		var notes = $('#customernotes').val();
		var terms =	$('#termsandconditions').val();
		var enableSignatoryName = $('#enableauthorisedsignatoryname').is(':checked');
		var signatoryName =	$('#authorisedsignatoryname').val();
		var designation =	$('#authDesignation').val();
		$('#sign').show();
		$('#dsg').show();
		$('#enablesign').hide();
		$.ajax({
			url: "${contextPath}/saveterms/${client.id}",
			data: {
				'terms': terms,
				'notes': notes,
				'enableSignatoryName' : enableSignatoryName,
				'authorisedSignatory':signatoryName,
				'designation':designation
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				successNotification('Save successful!');
				$("#cust_note").html(notes);
				$("#terms_condition").html(terms);
				if(enableSignatoryName == true){
					$("#auth_signatory_name").html(signatoryName);
					$("#auth_designation").html(designation);
					$('.enableSignatory').html('Yes');
					$('.enableSignatory').css("display","inline-block");
					$('.enableSignatory').css('color','green');
					$('.designation1').html('Yes');
					$('.designation1').css("display","inline-block");
					$('.designation1').css('color','green');
				}else{
					$("#auth_signatory_name").html(signatoryName);
					$("#auth_designation").html(designation);
					$("#auth_signatory_name").css("display","none");
					$('.enableSignatory').css("display","inline-block");
					$('.enableSignatory').html('No');
					$('.enableSignatory').css("color","red");
					$("#auth_designation").css("display","none");
					$('.designation1').css("display","inline-block");
					$('.designation1').html('No');
					$('.designation1').css("color","red");
				}
				$("#enable_auth_signatory_name").html(enableSignatoryName)
				$("#savebutton").css("display","none");
				$("#cancelbutton").css("display","none");
				document.getElementById("editbutton").style.display = "inline";
				document.getElementById("editbutton").disabled = false;
			},
			error : function(e) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	
function populateElement(invoiceId) {
	$('.with-errors').html('');
	$('.form-group').removeClass('has-error has-danger');
	$('#prefix').val('');
	$('#invoiceType').val('');
	$('#returntype').val('');
	$('#startInvoiceNo').val('');
	$('#endInvoiceNo').val('');
	$('#formatMonth').val('');
	$('#formatYear').val('');
	$("input[name='id']").remove();
	$("input[name='createdDate']").val('');
	$("input[name='createdBy']").val('');
	$('#allTypes').removeAttr("checked");
	$('#invoicetype').removeAttr("disabled").css("background-color","");
	$('#idSampleInvNo,#disp_month,#disp_year').html('');
	var invoiceYear = $('#financalYear').val();
	$('input[name=invoicenumbercutoff][value="Yearly"]').prop('checked', 'checked');
	$('#finYear').val(invoiceYear);
	if(invoiceId) {
		invoices.forEach(function(invoice) {
			if(invoice.id == invoiceId) {
				$('#invoicetype').val(invoice.invoiceType);
				if(invoice.allowMonth == ''){
					$('#month_check').removeAttr("checked");
					$('.formatMonth').css("display","none");
				}else{
					$('#month_check').attr("checked","checked");
					$('.formatMonth').css("display","block");
					if(invoice.formatMonth != ''){
						$('#formatMonth').val(invoice.formatMonth);
					}else{
						$('#formatMonth').val('02');
					}
				}
				if(invoice.allowYear == ''){
					$('#year_check').removeAttr("checked");
					$('.formatYear').css("display","none");
				}else{
					$('#year_check').attr("checked","checked");
					$('.formatYear').css("display","block");
					if(invoice.formatYear != ''){
						$('#formatYear').val(invoice.formatYear);
					}else{
						$('#formatYear').val('19');
					}
				}
				$('#prefix').val(invoice.prefix);
				$('#startInvoiceNo').val(invoice.startInvoiceNo);
				$('#endInvoiceNo').val(invoice.endInvoiceNo);
				var sampleInvNo;
				if(invoice.sampleInvNo != ''){
					sampleInvNo = invoice.sampleInvNo;
				}else{				
					if(invoice.allowMonth != '' && invoice.allowYear != ''){
						sampleInvNo = ' '+invoice.prefix+'0219'+invoice.startInvoiceNo;
					}else if(invoice.allowMonth != '' && invoice.allowYear == ''){
						sampleInvNo = ' '+invoice.prefix+'02'+invoice.startInvoiceNo;
					}else if(invoice.allowMonth == '' && invoice.allowYear != ''){
						sampleInvNo = ' '+invoice.prefix+'19'+invoice.startInvoiceNo;
					}else{
						sampleInvNo = ' '+invoice.prefix+''+invoice.startInvoiceNo;
					}
				}
				$('input[name=invoicenumbercutoff][value="'+invoice.invoicenumbercutoff+'"]').prop('checked', 'checked');
				$('#idSampleInvNo').html(sampleInvNo);
				$('#sampleInvNo').val(sampleInvNo);
				$("form[name='invoiceform']").append('<input type="hidden" name="id" value="'+invoice.id+'">');
				$("input[name='year']").val(invoice.year);
				$("input[name='createdDate']").val(invoice.createdDate);
				$("input[name='createdBy']").val(invoice.createdBy);
			}
		});
	}else{
		$('#formatMonth').val('02');
		$('#formatYear').val('19');
	}
}
function showDeletePopup(invoiceId, prefix, invoiceType) {
	$('.deleteModal').modal('show');
	$('.delPopupDetails').html(prefix);
	if(invoiceType == 'ALL'){
		$('#configure').removeAttr("disabled");
	}
	$('#btnDelete').attr('onclick', "deleteProduct('"+invoiceId+"')");
}
function deleteProduct(invoiceId) {
	$.ajax({
		url: "${contextPath}/delinvconfig/"+invoiceId,
		success : function(response) {
			table.row( $('#row'+invoiceId) ).remove().draw();
		}
	});
}
</script>
<script src="${contextPath}/static/mastergst/js/client/upload.js" type="text/javascript"></script>
<body class="body-cls">
  <!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
		<!--- breadcrumb start -->				
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">Company Profile</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
        <!--- breadcrumb end -->
        <div class="db-ca-wrap">
            <div class="container">
                <div class="row">
                    <!-- left side begin -->
                    <%@include file="/WEB-INF/views/profile/leftnav.jsp" %>
                    <!-- left side end -->
                    <!-- right sidebar begin -->
	<div class="col-md-10 col-sm-12 mb-4">
		<div class="whitebgbox meterialform" style="height:auto!important;min-height:89%!important">
			<div class="row p-3 pb-0 gstr-info-tabs" style="padding-top:0px!important;">
				<ul class="nav nav-tabs" role="tablist" id="tabsactive">
					<li class="nav-item"><a class="nav-link active permissionAll_Configurations-Invoice_Config" data-toggle="tab" href="#gtab1" id="invoiceconfig" role="tab">Invoice</a></li>
					<li class="nav-item"><a class="nav-link permissionAll_Configurations-Notes_And_Terms" data-toggle="tab" href="#gtab2" role="tab">Notes & Terms</a></li>
					<li class="nav-item"><a class="nav-link permissionAll_Configurations-Print_Config" data-toggle="tab" href="#gtab3" id="printconfig" role="tab">Print</a></li>
					<li class="nav-item"><a class="nav-link permissionAll_Configurations-Reconcile_Config" data-toggle="tab" id="reconcileconfig" href="#gtab4" role="tab">Reconcile</a></li>
					<li class="nav-item"><a class="nav-link permissionAll_Configurations-Other_Config" data-toggle="tab" id="otherconfig" href="#gtab5" role="tab">Other</a></li>
					<li class="nav-item"><a class="nav-link permissionAll_Configurations-Eway_Bill_Config" data-toggle="tab" id="ewaybillconfig" href="#gtab6" role="tab">EWAY Bill</a></li>
					<li class="nav-item"><a class="nav-link permissionAll_Configurations-Einvoice_Config" data-toggle="tab" id="einvoiceconfig" href="#gtab7" role="tab">e-invoice</a></li>
					<li class="nav-item"><a class="nav-link" data-toggle="tab" id="customFieldsconfig" href="#gtab8" role="tab" onclick="loadCustomFileds('${id}', '${client.id}','${month}','${year}')">Custom Fields</a></li>
				</ul>
				<div class="tab-content" style="width:920px!important">
					<div class="tab-pane active" id="gtab1" role="tabpane1">
						<div class="form-group col-md-12 col-sm-12">
							<div class="group upload-btn f-18-b mb-2 mr-3" style="display: inline-block;">Date Lock Configuration</div>
							<div class="" style="float:right;display: flex;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="invdate_editbutton" style="margin-right:3px;padding: 1.5px 15px;" onClick="editinvdate()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="invdate_savebutton" style="margin-right:3px;display:none;padding: 1.5px 15px;" onClick="saveinvdate()" value="Save"/>
								<input type="button" class="btn btn-blue-dark" id="invdate_cancelbutton" style="margin-right:3px;display:none;padding: 1.5px 12px;" onClick="cancelinvdate()" value="Cancel" />
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Invoice Date CutOff for Sales(dd/mm/yyyy)   </span><span class="colon">: </span>                          
								<span id="inv_cutoffdate_sales" class="inv_cutoffdate_sales" style="margin-left: 15px;display: inline-block;">${client.cutOffDateForSales}</span>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Invoice Date CutOff for Purchases(dd/mm/yyyy)  </span> <span class="colon" >: </span>                    
								<span id="inv_cutoffdate_purchases" class="inv_cutoffdate_purchases" style="margin-left: 15px;display: inline-block;">${client.cutOffDateForPurchases}</span>
							</div>
							<script type="text/javascript">
								invdatesales_old='${client.cutOffDateForSales}';
								invdatepurchase_old='${client.cutOffDateForPurchases}';
							</script>
						</div>
						<div class="group upload-btn bdr-b  p-0 mb-1" style="line-height:4px;display:block">&nbsp;</div>
						<div class="form-group col-md-12 col-sm-12">
							<div class="group upload-btn f-18-b mb-2 mr-3" style="display: inline-block;">Invoice Field Configurations</div>
							<div class="" style="float:right;display: flex;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="discExempt_editbutton" style="margin-right:3px;padding: 1.5px 15px;" onClick="editinvConfig()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="discExempt_savebutton" style="margin-right:3px;display:none;padding: 1.5px 15px;" onClick="saveinvConfig()" value="Save"/>
								<input type="button" class="btn btn-blue-dark" id="discExempt_cancelbutton" style="margin-right:3px;display:none;padding: 1.5px 12px;" onClick="cancelinvConfig()" value="Cancel" />
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Discount In Invoice(Sales) </span> <span class="colon">: </span> 
								<c:choose><c:when test = '${client.allowDiscount eq true}'><span id="inv_disc_config" class="inv_disc_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_disc_config" class="inv_disc_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Discount In Invoice(Purchases)</span><span class="colon">: </span>  
								<c:choose><c:when test = '${client.allowPurDiscount eq true}'><span id="inv_purdisc_config" class="inv_purdisc_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_purdisc_config" class="inv_purdisc_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Discount In Invoice(E-invoice) </span> <span class="colon">: </span> 
								<c:choose><c:when test = '${client.allowEinvDiscount eq true}'><span id="inv_einvdisc_config" class="inv_einvdisc_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_einvdisc_config" class="inv_einvdisc_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Exempted In Invoice</span><span class="colon">: </span> 
								<c:choose><c:when test = '${client.allowExempted eq true}'><span id="inv_exempted_config" class="inv_exempted_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_exempted_config" class="inv_exempted_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Cess In Invoice(Sales) </span> <span class="colon">: </span> 
								<c:choose><c:when test = '${client.allowSalesCess eq true}'><span id="inv_salesCess_config" class="inv_salesCess_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_salesCess_config" class="inv_salesCess_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Cess In Invoice(Purchases)</span><span class="colon">: </span>  
								<c:choose><c:when test = '${client.allowPurCess eq true}'><span id="inv_purCess_config" class="inv_purCess_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_purCess_config" class="inv_purCess_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Cess In Invoice(E-invoice) </span> <span class="colon">: </span> 
								<c:choose><c:when test = '${client.allowEinvoiceCess eq true}'><span id="inv_einvCess_config" class="inv_einvCess_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_einvCess_config" class="inv_einvCess_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Ledger In Invoice(Sales)</span><span class="colon" >: </span>     
								<c:choose><c:when test = '${client.allowLedgerName eq true}'><span id="inv_ledger_config" class="inv_ledger_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_ledger_config" class="inv_ledger_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<div class="form-group inv-text-div col-md-12 col-sm-12 row">
								<span class="lable-txt col-md-5">Show Ledger In Invoice(Purchases)</span><span class="colon" >: </span>     
								<c:choose><c:when test = '${client.allowPurLedgerName eq true}'><span id="inv_purledger_config" class="inv_purledger_config ml-4" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="inv_purledger_config" class="inv_purledger_config ml-4" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
							</div>
							<script type="text/javascript">
								invdiscount_old=$('#inv_disc_config').text();
								invpurdiscount_old=$('#inv_purdisc_config').text();
								invcess_old=$('#inv_salesCess_config').text();
								invpurcess_old=$('#inv_purCess_config').text();
								einvcess_old=$('#inv_einvCess_config').text();
								einvdiscount_old=$('#inv_einvdisc_config').text();
								invexempted_old=$('#inv_exempted_config').text();
								invledger_old=$('#inv_ledger_config').text();
								invpurledger_old=$('#inv_purledger_config').text();
							</script>
						</div>		
						<div class="form-group col-md-12 col-sm-12">
							<div class="group upload-btn f-18-b mb-2 mr-3" style="display: inline-block;">Email Signature Configuration</div>
							<div class="" style="float:right;display: flex;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="emailConfig_editbutton" style="margin-right:3px;padding: 1.5px 15px;" onClick="editEmailConfig()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="emailConfig_savebutton" style="margin-right:3px;display:none;padding: 1.5px 15px;" onClick="saveEmailConfig()" value="Save"/>
								<input type="button" class="btn btn-blue-dark" id="emailConfig_cancelbutton" style="margin-right:3px;display:none;padding: 1.5px 12px;" onClick="cancelEmailConfig()" value="Cancel" />
							</div>
								<div class="form-group col-md-12">
									<span id="configure_clientdetails" class="configure_clientdetails" style="display:table-caption;font-size:15px;width:700px;"></span>
									<!-- <textarea class="form-control" id="email_signdetails" style="display:none;resize:auto;width:96%; text-indent: 14px;height:110px;border:1px solid lightgray;padding-left:6px;"> </textarea> -->
									<textarea id="email_signdetails" rows="4" cols="50" style="display:none;border: 1px solid lightgray;resize:auto;border-radius: 5px;max-width: 800px;margin-left: 10px;">  </textarea> 
								</div>
								<script type="text/javascript">
									emailSignature=document.getElementById('email_signdetails').value;
								</script>
						</div>								
						<form class="meterialform" style="display:block">		
							<div class="group upload-btn bdr-b  p-0 mb-4" style="line-height:4px;display:block">&nbsp;</div>
							<div class="row">
								<div class="form-group col-md-8 col-sm-12 pl-4 m-0">
									<div class="f-18-b" style="display: inline-flex;">Invoice Number Configuration For The Year<div class="form-group fy-upload-invoice" style="z-index:1;margin-top: 0;margin-left: 20px;">
							<span class="errormsg" id="financalYear_Msg"></span>
							<jsp:useBean id="date" class="java.util.Date" />
							<fmt:formatDate value="${date}" pattern="yyyy" var="currentYear" />
							<select id="financalYear" name="year" style="width:98px; border: 1px solid #9E9E9E;">
								<option value="2021-2022">2021-2022</option>
								<option value="2020-2021">2020-2021</option>
								<option value="2019-2020">2019-2020</option>
								<option value="2018-2019">2018-2019</option>
								<option value="2017-2018">2017-2018</option>
								<!--<option value="${currentYear+1}-${currentYear+2}">${currentYear+1}-${currentYear+2}</option>-->
							</select>
							<label for="input" class="control-label"></label>
						</div></div>
								</div>								
								<div class="form-group col-md-4 col-sm-12" style="z-index:2;padding-right: 2rem;">
								<c:if test='${invType == "Yes"}'>
									<input type="button" class="btn btn-blue-dark mb-2 configInvoice permissionSettings-Configurations-Add"  value="Configure Invoice Number" data-toggle="modal" data-target="#editModal"  onClick="populateElement()" disabled/>
								</c:if>
								<c:if test='${invType == "No"}'>
									<input type="button" class="btn btn-blue-dark mb-2 configInvoice permissionSettings-Configurations-Add"  value="Configure Invoice Number" data-toggle="modal" data-target="#editModal" onClick="populateElement()"/>
								</c:if>
								<c:if test='${invType == null}'>
									<input type="button" class="btn btn-blue-dark mb-2 configInvoice permissionSettings-Configurations-Add" value="Configure Invoice Number" data-toggle="modal" data-target="#editModal" onClick="populateElement()"/>
								</c:if>
								</div>
							</div>
						</form>						
						<!-- dashboard cp table begin --> 						
						<div class="customtable" style="width:97%;left:10px;margin-top: 20px;">
							<table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0">
								<thead><tr><th class="text-center">Invoice Type</th><th class="text-center">Prefix</th><th class="text-center">Year</th> <th class="text-center">Starting Invoice No</th><th class="text-center">Ending Invoice No</th><th class="text-center">Submited Date</th> <th class="text-center">Sample Invoice No</th><th class="text-center">Action</th> </tr></thead>
								<tbody id="invconfig">
								<c:forEach items="${invoices}" var="invoice">
									<script type="text/javascript">
									var invoice = new Object();
									invoice.id = '<c:out value="${invoice.id}"/>';
									invoice.invoiceType = '<c:out value="${invoice.invoiceType}"/>';
									invoice.returnType = '<c:out value="${invoice.returnType}"/>';
									invoice.prefix = '<c:out value="${invoice.prefix}"/>';
									invoice.allowMonth = '<c:out value="${invoice.allowMonth}"/>';
									invoice.allowYear = '<c:out value="${invoice.allowYear}"/>';
									invoice.year = '<c:out value="${invoice.year}"/>';
									invoice.startInvoiceNo = '<c:out value="${invoice.startInvoiceNo}"/>';
									invoice.endInvoiceNo = '<c:out value="${invoice.endInvoiceNo}"/>';
									invoice.submittedDate = '<fmt:formatDate value="${invoice.submittedDate}" pattern="dd-MM-yyyy" />';	
									invoice.createdDate = '<fmt:formatDate pattern="yyyy-MM-dd" value="${invoice.createdDate}" />';
									invoice.createdBy = '<c:out value="${invoice.createdBy}"/>';
									invoice.sampleInvNo = '<c:out value="${invoice.sampleInvNo}"/>';
									invoice.formatMonth = '<c:out value="${invoice.formatMonth}"/>';
									invoice.formatYear = '<c:out value="${invoice.formatYear}"/>';
									invoice.invoicenumbercutoff = '<c:out value="${invoice.invoicenumbercutoff}"/>';
									invoices.push(invoice);
									</script>
									<tr id="row${invoice.id}">
										<td align="center">${invoice.invoiceType}</td>
										<td align="center">${invoice.prefix}</td>
										<td align="center">${invoice.year}</td>
										<td align="center">${invoice.startInvoiceNo}</td>
										<td align="center">${invoice.endInvoiceNo}</td>
										<td align="center"><fmt:formatDate value="${invoice.submittedDate}" pattern="dd-MM-yyyy" /></td>
										<td align="center"><c:choose><c:when test="${not empty invoice.sampleInvNo}">${invoice.sampleInvNo}</c:when><c:otherwise>${invoice.prefix}${month}${year}${invoice.startInvoiceNo}</c:otherwise></c:choose></td>
										<td class="actionicons"><a class="btn-edt permissionSettings-Invoice_Print-Edit" href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement('${invoice.id}')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Invoice_Print-Delete" onClick="showDeletePopup('${invoice.id}','${invoice.prefix}','${invoice.invoiceType}')"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
									</tr>
								</c:forEach>
								</tbody>
							</table>
						</div>
						<!-- dashboard cp table end --> 
					</div>
					<!--print page configuration start  -->
					<div class="tab-pane meterialform" id="gtab3" role="tabpane3">
					<div class="modal-body meterialform bs-fancy-checks" style="margin-top: -30px;overflow-y: hidden;height:100vh;">
					<div class="row">
					<fieldset class="sigwrape row">
  					<legend>Scanned Signature</legend>
  					<div class="row">	
					<p class="lable-txt col-md-4" >Display Authorised Scanned Signature </p><span class="col-md-1 pl-2">:</span>
				    <div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels pl-0" style="margin-top: 2px;"><div class="form-check"><input  type="checkbox" id="digitalSignature"><label for="digitalSignature"><span class="ui"></span></label></div></div>
					<div class="col-md-8 col-sm-12"><c:if test="${not empty client.signid}"><span class="imgsize-wrap-thumb" style="position:absolute;top: 83%;left: 95%;border-radius:0px;width: 20%;max-width:200px;height:40px;"><img src="${contextPath}/getlogo/${client.signid}" alt="Signature" class="imgsize-thumb" id="clntsigns" style="border-radius:0px;height: 38px;"></span></c:if>
					<c:if test="${empty client.signid}"><span class="imgsize-wrap-thumb" style="top:83%;left:95%;border-radius:0px;width: 20%;max-width:200px;height:40px;position:absolute;"><img src="${contextPath}/static/mastergst/images/master/signature.png" alt="Signature" class="imgsize-thumb" id="clntsigns" style="border-radius:0px;height: 38px;"></span></c:if>
					</div>
					</div>
					<form method="POST" action="${contextPath}/clntsignature/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" id="clntssign" enctype="multipart/form-data" class="meterialform col-md-12 col-sm-12 pl-0">
					<div class="upload_scan_sign" style="margin-top: 11px;">
					<div class="" id="uplogo" style="position: absolute;left: 40%;">
					<input type="file" name="file" class="clientlogo" accept="image/gif, image/jpeg, image/png" onChange="saveClientSignature(event)"/>
					<span class="uploadclientlogo">Add/Edit Signature</span>
					<div class="helptxt" style="font-size:12px;font-weight:bold;margin-top: 4px;">(Please add White background Signature only)</div>
					</div>
					<div class="row">
					<p class="lable-txt col-md-4" >Upload Authorised Scanned Signature </p><span class="col-md-1" style="padding-left: 1.2%;">:</span></div>
					</div>
					</form>
					</fieldset>
					<form:form method="POST" data-toggle="validator" class="meterialform col-md-12 col-sm-12 pl-0" name="printform" action="${contextPath}/createPrintConfiguration/${id}/${client.id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="role">
					    <div class="row" style="margin-top: 25px;">
					    <div class="form-group col-md-12 col-sm-12"><div class="" style="position: absolute;right: 0px; top: 0px;z-index: 4;display: flex;">
						<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="editbutton1" style="margin-right:3px;padding:1.5px 15px;" onClick="editconfig()" value="Edit" />
						<input type="button" class="btn btn-blue-dark" id="savebutton1" style="margin-right:3px;display:none;padding:1.5px 15px;" onClick="saveconfig()" value="Save"/>
						<input type="button" class="btn btn-blue-dark" id="cancelbutton1" style="margin-right:3px;display:none;padding:1.5px 15px;" onClick="cancelconfig()" value="Cancel" />
						</div></div>
					   <div class="col-md-12 col-sm-12 row mb-2">
								<p class="col-md-4 col-sm-12 lable-txt pr-0">Footer Notes</p> <span class="col-md-1 colon pull-right" style="padding-left: 21px;">: </span>
								<%-- <span id="enable_auth_signatory_name" class="enable_auth_signatory" style="display: inline-block;  margin-left: -38px; margin-top: 3px; margin-bottom: 4px;">${pconfig.footernotes} </span> </p> --%>
							 <c:choose><c:when test = '${pconfig.isfooternotescheck eq true}'><span id="footnotecheckval" class="col-md-7 col-sm-12 footnotecheckval ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="footnotecheckval" class="col-md-7 col-sm-12 footnotecheckval" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
						</div>
				    <textarea type="text" class="footerNotes ml-4" id="footernotes" name="footernotes" placeholder="Footer Notes" value="" style="border: 1px solid lightgray;display:none;width:540px;"></textarea>
				     <div id="footnoteDiv" style="padding-left: 15px;">
				    <p class="lable-txt">Footer Notes Text <span class="colon" style="margin-left: 121px;">: </span><span id="footnoteval" style="margin-left: 15px;">${pconfig.footernotes}</span>
				     </div>
						<div class="form-group inv-text-div col-md-12 col-sm-12 row">
                             <span class="lable-txt col-md-4 col-sm-12 pr-0">Invoice Display Text  </span> <span class="col-md-1 colon pull-right" style="padding-left: 21px;">: </span>
			             <span id="inv_text" class="col-md-7 col-sm-12 inv_dis_text" style="display: inline-block;">${pconfig.invoiceText}</span>
                         </div>
                         <div class="form-group inv-text-div col-md-12 col-sm-12 row">
                            <span class="lable-txt col-md-4 col-sm-12 pr-0">Invoice Quantity Text  </span>  <span class="col-md-1 colon pull-right" style="padding-left: 21px;">: </span>                        
			             <span id="inv_qty_text" class="col-md-7 col-sm-12 inv_qty_text" style="display: inline-block;"><c:choose><c:when test='${empty pconfig.qtyText || pconfig.qtyText eq ""}'>Qty</c:when><c:otherwise>${pconfig.qtyText}</c:otherwise></c:choose></span>
                         </div>
                         <div class="form-group inv-text-div col-md-12 col-sm-12 row">
                            <span class="lable-txt col-md-4 col-sm-12 pr-0">Invoice Rate Text</span> <span class="col-md-1 colon pull-right" style="padding-left: 21px;">: </span>                         
			             <span id="inv_rate_text" class="inv_rate_text col-md-7 col-sm-12" style="display: inline-block;"><c:choose><c:when test='${empty pconfig.rateText || pconfig.rateText eq ""}'>Rate</c:when><c:otherwise>${pconfig.rateText}</c:otherwise></c:choose></span>
                         </div>
                         <div class="form-group inv-text-div col-md-12 col-sm-12 row">
                            <span class="lable-txt col-md-4 col-sm-12 pr-0">Authorised Signatory Display Text</span><span class="col-md-1 colon pull-right" style="padding-left: 21px;">: </span>                          
			             <span id="inv_authsign_text" class="inv_authsign_text col-md-7 col-sm-12" style="display: inline-block;"><c:choose><c:when test='${empty pconfig.authSignText || pconfig.authSignText eq ""}'>Authorised Signature</c:when><c:otherwise>${pconfig.authSignText}</c:otherwise></c:choose></span>
                         </div>
                          <div class="form-group inv-text-div col-md-12 col-sm-12 row">
                            <span class="lable-txt col-md-4 col-sm-12 pr-0">Display E-invoice RoundOff Amount</span><span class="col-md-1 colon pull-right" style="padding-left: 21px;">: </span>                          
			             <c:choose><c:when test = '${pconfig.enableRoundOffAmt eq true}'><span id="enable_roundoff_field" class="enable_roundoff_field col-md-7 col-sm-12" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_roundoff_field" class="enable_roundoff_field col-md-7 col-sm-12" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
                         </div>
                         <div class="form-group inv-text-div col-md-12 col-sm-12 row entertaiment_einvoice_import_template">
                            <span class="lable-txt col-md-4 col-sm-12 pr-0">Display E-invoice Header Text</span><span class="col-md-1 colon pull-right" style="padding-left: 21px;">: </span>                          
			             	<span id="einv_header_text" class="col-md-7 col-sm-12 inv_einvheader_text" style="display: inline-block;">${pconfig.einvoiceHeaderText}</span>
                         </div>
						<div class="form-group col-md-12 col-sm-12" style="margin-top:20px;">
						<p class="lable-txt"><h6>Display Of Fields in the Print Page :  </h6></p>
						</div>
						<div class="col-md-5 col-sm-12">
						<div class="discount_divs mb-2 row" id="enablediscount">
						<p class="lable-txt col-md-4">Discount<span class="colon pull-right" >:</span></p>
						<c:choose><c:when test = '${pconfig.enableDiscount eq true}'><span id="enable_discount_field" class="enable_discount_field" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_discount_field" class="enable_discount_field" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
						</div>
						<div class="rate_divs mb-2 row" id="enablerate">
						<p class="lable-txt col-md-4">Rate<span class="colon pull-right">:</span></p>
						<c:choose><c:when test = '${pconfig.enableRate eq true}'><span id="enable_rate_field" class="enable_rate_field" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_rate_field" class="enable_rate_field" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
						</div>
						<div class="qty_divs mb-2 row" id="enableqty">
						<p class="lable-txt col-md-4">Quantity<span class="colon pull-right">:</span></p>
						<c:choose><c:when test = '${pconfig.enableQuantity eq true}'><span id="enable_qty_field" class="enable_qty_field" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="enable_qty_field" class="enable_qty_field" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose>
						</div>
						</div>
						<div class="col-md-5 col-sm-12 " style="right: 10%;">
						<div class="qty_divs mb-2 row" id="enablestate">
						<p class="lable-txt col-md-5">State Code<span class="colon pull-right" >:</span></p>
						<c:choose><c:when test = '${pconfig.enableState eq true}'><span id="enable_state_field" class="enable_state_field" style="display: inline-block; color:green;">Yes</span></c:when><c:otherwise><span id="enable_state_field" class="enable_state_field" style="display: inline-block; color:green;">Yes</span></c:otherwise></c:choose>
						</div>
						<div class="qty_divs mb-2 row" id="enablepan">
						<p class="lable-txt col-md-5" style="margin-top: 6px;">PAN Number<span class="colon pull-right" >:</span></p>
						<c:choose><c:when test = '${pconfig.enablePan eq true}'><span id="enable_pan_field" class="enable_pan_field" style="display: inline-block;color:green">Yes</span></c:when><c:otherwise><span id="enable_pan_field" class="enable_pan_field" style="display: inline-block; color:green;">Yes</span></c:otherwise></c:choose>
						</div>
						<div class="qty_divs mb-2 row" id="enableplaceofsupply">
						<p class="lable-txt col-md-5" style="margin-top: 6px;">Place Of Supply<span class="colon pull-right">:</span></p>
						<c:choose><c:when test = '${pconfig.enablePlaceOfSupply eq true}'><span id="enable_placeofsupply_field" class="enable_placeofsupply_field" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_placeofsupply_field" class="enable_placeofsupply_field" style="display: inline-block; color:green;">Yes</span></c:otherwise></c:choose>
						</div>
					</div>
						<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">					
						<script type="text/javascript">
								invoice_label_text=$("#inv_text").text();
								qty_label_text=$("#inv_qty_text").text();
								rate_label_text=$("#inv_rate_text").text();
								authsign_label_text=$("#inv_authsign_text").text();
								einvoice_header_label_text = $("#einv_header_text").text();
								discount=$("#enable_discount_field").text();
								rate=$("#enable_rate_field").text();
								quantity=$("#enable_qty_field").text();
								roundoff = $('#enable_roundoff_field').text();
								statecode=$("#enable_state_field").text();
								pannumber=$("#enable_pan_field").text();
								placeofsupply=$("#enable_placeofsupply_field").text();
								footer_text = $("#footnoteval").text();
								footer_check = $("#footnotecheckval").text();
							</script>
                    </div>
					</form:form>
					</div>
					</div>
					</div>
					<!-- print page configuration end -->
					<!--print page configuration start  -->
					<div class="tab-pane meterialform" id="gtab4" role="tabpane4">
					<div class="modal-body meterialform bs-fancy-checks" style="margin-top: -30px;">
					<div class="row">
					<div class="form-group col-md-12 col-sm-12"><div class="" style="display: flex;float:right;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="reconcile_editbutton" style="margin-right:3px;padding:1.5px 15px;" onClick="editreconcile()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="reconcile_savebutton" style="margin-right:3px;display:none;padding:1.5px 15px;" onClick="saveReconsileConfig('${client.id}')" value="Save"/>
								<input type="button" class="btn btn-blue-dark" id="reconcile_cancelbutton" style="margin-right:3px;display:none;padding:1.5px 15px;" onClick="cancelreconcile()" value="Cancel" />
							</div></div>
						<div class="form-group inv-text-div col-md-12 col-sm-12 ">
                            <div class="lable-txt row"><span class="col-md-5">Allowed Tax/Invoice Amount Difference for Reconcile</span><span class="colon">: </span><span style="display: inline-block;"><span id="reconsileAmt" name="reconsileAmt" placeholder="Reconsile Amt" class="indformat"><c:choose><c:when test="${not empty clientConfig.reconcileDiff}">${clientConfig.reconcileDiff}</c:when><c:otherwise>0.00</c:otherwise></c:choose></span></span> <span>Rupees</span></div>
						</div>
						<div class="form-group col-md-12 col-sm-12">
                            <div class="lable-txt row"><span class="col-md-5">Allowed days for Invoice Date Difference for Reconcile</span><span class="colon">: </span><span style="display: inline-block;"><span id="alloweddays" style="display: inline-block;"><c:choose><c:when test="${not empty clientConfig.allowedDays}"><fmt:formatNumber type="number" pattern="###" value="${clientConfig.allowedDays}" />  </c:when><c:otherwise>0</c:otherwise></c:choose></span> Days</span></div>
						</div>
						<div class="form-group col-md-12 col-sm-12 mb-2">
                            <div class="lable-txt">Considers as Matched if Invoice Number matches anywhere in the Invoice Number<span class="colon" style="margin-left: 20px;">: </span>
                            <c:choose><c:when test = '${clientConfig.enableInvoiceMatch eq true}'><span style="display: inline-block;color:green;"><span id="invmatch">Yes</span></span></c:when><c:when test = '${clientConfig.enableInvoiceMatch eq false}'><span style="display: inline-block;color:red;"><span id="invmatch">No</span></span></c:when><c:otherwise><span style="display: inline-block;color:green;"><span id="invmatch">Yes</span></span></c:otherwise></c:choose>
                            </div>
						</div>
						<p class="lable-txt pl-3">Invoice Number Reconcile Settings<span class="colon" style="margin-left: 5px;">: </span></p>
							<div class="col-md-12 col-sm-12" id="">
								<label for="ignore /" class="col-md-3 col-sm-12 mb-0">Ignore "/"<span style="font-size:12px">(Backward Slash)</span><span class="colon" style="float: right;">: </span></label>
								<c:choose><c:when test = '${clientConfig.enableIgnoreSlash eq true}'><span id="enable_ignore_slash" class="enable_ignore_slash col-md-8 col-sm-12 pl-0" style="display: inline-block; margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:when><c:when test = '${clientConfig.enableIgnoreSlash eq false}'><span id="enable_ignore_slash" class="enable_ignore_slash col-md-8 col-sm-12 pl-0" style="display: inline-block; margin-top: 3px; margin-bottom: 4px;color:red;">No</span></c:when><c:otherwise><span id="enable_ignore_slash" class="enable_ignore_slash col-md-8 col-sm-12 pl-0" style="display: inline-block; margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:otherwise></c:choose>
							</div>
							<div class="col-md-12 col-sm-12" id="">
								<label for="ignore -" class="col-md-3 col-sm-12 mb-0">Ignore "-"<span style="font-size:12px">(Hyphen)</span><span class="colon" style="float: right;">: </span></label>
								<c:choose><c:when test = '${clientConfig.enableIgnoreHyphen eq true}'><span id="enable_ignore_hyphen" class="enable_ignore_hyphen col-md-8 col-sm-12 pl-0" style="display: inline-block;margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:when><c:when test = '${clientConfig.enableIgnoreHyphen eq false}'><span id="enable_ignore_hyphen" class="enable_ignore_hyphen col-md-8 col-sm-12 pl-0" style="display: inline-block;margin-top: 3px; margin-bottom: 4px;color:red;">No</span></c:when><c:otherwise><span id="enable_ignore_hyphen" class="enable_ignore_hyphen col-md-8 col-sm-12 pl-0" style="display: inline-block;margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:otherwise></c:choose>
							</div>
							<div class="col-md-12 col-sm-12" id="">
								<label for="ignore 0" class="col-md-3 col-sm-12 mb-0">Ignore "0"<span style="font-size:12px">(Zero/O)</span><span class="colon" style="float: right;">: </span></label>
								<c:choose><c:when test = '${clientConfig.enableIgnoreZero eq true}'><span id="enable_ignore_zero" class="enable_ignore_zero col-md-8 col-sm-12 pl-0" style="display: inline-block;  margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:when><c:when test = '${clientConfig.enableIgnoreZero eq false}'><span id="enable_ignore_zero" class="enable_ignore_zero col-md-8 col-sm-12 pl-0" style="display: inline-block;  margin-top: 3px; margin-bottom: 4px;color:red;">No</span></c:when><c:otherwise><span id="enable_ignore_zero" class="enable_ignore_zero col-md-8 col-sm-12 pl-0" style="display: inline-block;  margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:otherwise></c:choose>
							</div>
							<div class="col-md-12 col-sm-12" id="">
								<label for="ignore ir" class="col-md-3 col-sm-12 mb-0">Ignore "I"<span style="font-size:12px">(Capital i)</span> <span class="colon" style="float: right;">: </span></label>
								<c:choose><c:when test = '${clientConfig.enableIgnoreI eq true}'><span id="enable_ignore_i" class="enable_ignore_i col-md-8 col-sm-12 pl-0" style="display: inline-block;  margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:when><c:when test = '${clientConfig.enableIgnoreI eq false}'><span id="enable_ignore_i" class="enable_ignore_i col-md-8 col-sm-12 pl-0" style="display: inline-block;  margin-top: 3px; margin-bottom: 4px;color:red;">No</span></c:when><c:otherwise><span id="enable_ignore_i" class="enable_ignore_i col-md-8 col-sm-12 pl-0" style="display: inline-block;  margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:otherwise></c:choose>
							</div>
							<div class="col-md-12 col-sm-12" id="">
								<label for="ignore l" class="col-md-3 col-sm-12 mb-0">Ignore "l"<span style="font-size:12px">(Small l)</span><span class="colon" style="float: right;">: </span></label>
								<c:choose><c:when test = '${clientConfig.enableIgnoreL eq true}'><span id="enable_ignore_l" class="enable_ignore_l col-md-8 col-sm-12 pl-0" style="display: inline-block;margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:when><c:when test = '${clientConfig.enableIgnoreL eq false}'><span id="enable_ignore_l" class="enable_ignore_l col-md-8 col-sm-12 pl-0" style="display: inline-block;margin-top: 3px; margin-bottom: 4px;color:red;">No</span></c:when><c:otherwise><span id="enable_ignore_l" class="enable_ignore_l col-md-8 col-sm-12 pl-0" style="display: inline-block;margin-top: 3px; margin-bottom: 4px;color:green;">Yes</span></c:otherwise></c:choose>
							</div>
					</div>
				
							<script type="text/javascript">
							reconcile_amt_old=$('#reconsileAmt').text();
							reconcile_allowed_days = $('#alloweddays').text();invoicematch = $('#invmatch').text();invfirstfivechar = $('#invfirstfivecharmatch').text();invlastfivechar = $('#invlastfivecharmatch').text();
							ignoreprefix = $('#enable_ignore_prefix').text();ignoremonth = $('#enable_ignore_month').text();ignoreyear = $('#enable_ignore_year').text();ignoreslash = $('#enable_ignore_slash').text();
							ignorehyphen = $('#enable_ignore_hyphen').text();ignorezero = $('#enable_ignore_zero').text();ignorei = $('#enable_ignore_i').text();ignorel = $('#enable_ignore_l').text();
                          </script>
					</div>
					</div>
					<div class="tab-pane" id="gtab5" role="tabpane5">
						<div class="form-group col-md-12 col-sm-12" style="height: 27px;"><div class="" style="display: flex;float:right;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="other_editbutton" style="margin-right:3px;padding:1.5px 15px;" onClick="editother()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="other_savebutton" style="margin-right:3px;display:none;padding:1.5px 15px;" onClick="saveother()" value="Save"/>
								<input type="button" class="btn btn-blue-dark" id="other_cancelbutton" style="margin-right:3px;display:none;padding:1.5px 15px;" onClick="cancelother()" value="Cancel" />
							</div></div>						
					<div class="meterialform bs-fancy-checks">	
							
					<div class="form-group col-md-12 col-sm-12">
					<div class="row pl-3">
							<div class="lable-txt col-md-6 f-14-b mb-2 p-0">Display Journal entries pre-fix text as<span class="colon" style="float: right;">:</span></div>
							<div class="form-group-inline col-md-6 pl-3" style="padding:0px;">
								<div class="form-radio" style="margin-top:0px;">
									<div class="radio">
										<span class="enable_drcr_field pl-3" id="enable_drcr_field"></span>
									</div>
								</div>
								<div class="form-radio" style="margin-top:0px;">
									<div class="radio">
										<span class="enable_byto_field" id="enable_byto_field"></span>
									</div>
								</div>
							</div>
							</div>
						</div>
						<div class="form-group col-md-12 col-sm-12">
						<div class="row pl-3">
							<div class="lable-txt col-md-6 f-14-b mb-2 p-0">View Purchase Register / Invoices by<span class="colon" style="float: right;">:</span></div>
							<div class="form-group-inline col-md-6 trinvfield pl-3" style="padding:0px;">
								<div class="form-radio trradiobtn pl-3" style="margin-top:0px;display:none;">
									<div class="radio">
										<span class="enable_invdate_field" id="enable_invdate_field"></span>
									</div>
								</div>
								<div class="form-radio trradiobtn" style="margin-top:0px;display:none;">
									<div class="radio">
										<span class="enable_trdate_field" id="enable_trdate_field"></span>
									</div>
								</div>
								<span class="tdate_invdate"></span>
							</div>
							</div>
						</div>
						<div class="form-group col-md-12 col-sm-12">
						<div class="row pl-3">
							<div class="lable-txt col-md-6 f-14-b mb-2 p-0">Calculate Cess Amount on<span class="colon" style="float: right;">:</span></div>
							<div class="form-group-inline col-md-6 cessfield pl-3" style="padding:0px;">
								<div class="form-radio cessradiobtn pl-3" style="margin-top:0px;display:none;">
									<div class="radio">
										<span class="enable_cqty_field" id="enable_cqty_field"></span>
									</div>
								</div>
								<div class="form-radio cessradiobtn" style="margin-top:0px;display:none;">
									<div class="radio">
										<span class="enable_ctaxAmt_field" id="enable_ctaxAmt_field"></span>
									</div>
								</div>
								<span class="cqty_ctaxAmt"></span>
							</div>
							</div>
						</div>
						
					<div class="form-group">
					<div class="pl-3">
							<div class="lable-txt col-md-6 f-14-b p-0" style="display:inline-block;">Make HSN/SAC, UQC, Quantity as Mandatory Parameters (Sales)<span class="colon" style="float: right;">:</span></div>
								<!-- <span class="enable_invsales_field" id="enable_invsales_field"></span> -->
								<c:choose><c:when test = '${otherconfig.enableSalesFields eq true}'><span id="enable_invsales_field" class="enable_invsales_field ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_invsales_field" class="enable_invsales_field ml-3" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
							</div>					
					</div>					
					<div class="form-group">
					<div class="pl-3">
							<div class="lable-txt col-md-6 f-14-b p-0"  style="display:inline-block;">Make HSN/SAC, UQC, Quantity as Mandatory Parameters (Purchase)<span class="colon" style="float: right;">:</span></div>
								<!-- <span class="enable_invpurchase_field" id="enable_invpurchase_field"></span> -->
								<c:choose><c:when test = '${otherconfig.enablePurFields eq true}'><span id="enable_invpurchase_field" class="enable_invpurchase_field ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_invpurchase_field" class="enable_invpurchase_field ml-3" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
							</div>					
					</div>		
					<div class="form-group">
					<div class="pl-3">
							<div class="lable-txt col-md-6 f-14-b p-0" style="display:inline-block;">Make Ledger as Mandatory Parameter (Sales)<span class="colon" style="float: right;">:</span></div>
									<c:choose><c:when test = '${otherconfig.enableLedgerSalesField eq true}'><span id="enable_invledgersales_field" class="enable_invledgersales_field ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_invledgersales_field" class="enable_invledgersales_field ml-3" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
							</div>					
					</div>		
					<div class="form-group">
					<div class="pl-3">
							<div class="lable-txt col-md-6 f-14-b p-0" style="display:inline-block;">Make Ledger as Mandatory Parameter (Purchase)<span class="colon" style="float: right;">:</span></div>
									<c:choose><c:when test = '${otherconfig.enableLedgerPurField eq true}'><span id="enable_invledgerpurchase_field" class="enable_invledgerpurchase_field ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_invledgerpurchase_field" class="enable_invledgerpurchase_field ml-3" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
							</div>					
					</div>
					<div class="form-group">
						<div class="pl-3">
								<div class="lable-txt col-md-6 f-14-b p-0" style="display:inline-block;">Make Total Invoice Amount Round Off when Import invoices (Sales)<span class="colon" style="float: right;">:</span></div>
										<c:choose><c:when test = '${otherconfig.enableroundoffSalesField eq true}'><span id="enable_invroundoffsales_field" class="enable_invroundoffsales_field ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_invroundoffsales_field" class="enable_invroundoffsales_field ml-3" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
						</div>					
					</div>		
					<div class="form-group">
						<div class="pl-3">
							<div class="lable-txt col-md-6 f-14-b p-0" style="display:inline-block;">Make Total Invoice Amount Round Off when Import invoices (Purchase)<span class="colon" style="float: right;">:</span></div>
									<c:choose><c:when test = '${otherconfig.enableroundoffPurField eq true}'><span id="enable_invroundoffpurchase_field" class="enable_invroundoffpurchase_field ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_invroundoffpurchase_field" class="enable_invroundoffpurchase_field ml-3" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
						</div>					
					</div>
					<div class="form-group">
						<div class="pl-3">
							<div class="lable-txt col-md-6 f-14-b p-0" style="display:inline-block;">Make Account Journal save when Import invoices<span class="colon" style="float: right;">:</span></div>
									<c:choose><c:when test = '${otherconfig.enablejournals eq true}'><span id="enable_journals_field" class="enable_journals_field ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_journals_field" class="enable_journals_field ml-3" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
						</div>					
					</div>
					<div class="form-group">
						<div class="pl-3">
							<div class="lable-txt col-md-6 f-14-b p-0" style="display:inline-block;">Calculate TCS Value on the Grand Total (Base Price + GST)<span class="colon" style="float: right;">:</span></div>
									<c:choose><c:when test = '${otherconfig.enableTCS eq true}'><span id="enable_tcs_field" class="enable_tcs_field ml-3" style="display: inline-block;color:green;">Yes</span></c:when><c:otherwise><span id="enable_tcs_field" class="enable_tcs_field ml-3" style="display: inline-block;color:red;">No</span></c:otherwise></c:choose></p>
						</div>					
					</div>							
						<p class="col-md-6 mb-2" style="margin-left: 17px;font-weight:600;font-size:14px;">ITC Eligibility Configuration<span class="mb-2 f-12 ml-3" style="font-weight:100;">(On configure here, these values will show up on your purchase invoices) </span></p>								
					<div class="form-group row ml-4"> 
					<label for="inputs" class="col-sm-2 col-form-label">Inputs<span class="colon" style="margin-left:71px">:</span></label>
					<div class="col-sm-9">
					<span id="itc_input" class="itc_input" style="display: inline-block;margin-top: 8px;"><c:choose><c:when test="${not empty otherconfig.itcinput}">${otherconfig.itcinput}</c:when><c:otherwise>100</c:otherwise></c:choose></span><span class="itcper" style="margin-left: 7px;">%</span>
					</div>
					</div>
					<div class="form-group row ml-4">
					<label for="inputservices" class="col-sm-2 pr-0 col-form-label" style="margin-top: -17px;">Input Services<span class="colon" style="margin-left:19px">:</span></label>
					<div class="col-sm-9" style="margin-top: -8px;">
					<span id="itc_inputservices" class="itc_inputservices" style="display: inline-block;"><c:choose><c:when test="${not empty otherconfig.itcinputService}">${otherconfig.itcinputService}</c:when><c:otherwise>100</c:otherwise></c:choose></span><span class="itcper" style="margin-left: 7px;">%</span>
					</div>
					</div>
					<div class="form-group row ml-4">
					<label for="capitalgood" class="col-sm-2 pr-0 col-form-label" style="margin-top: -17px;">Capital Good<span class="colon" style="margin-left:27px">:</span></label>
					<div class="col-sm-9" style="margin-top: -7px;">
					<span id="itc_capitalgood" class="itc_capitalgood" style="display: inline-block;"><c:choose><c:when test="${not empty otherconfig.itcCapgood}">${otherconfig.itcCapgood}</c:when><c:otherwise>100</c:otherwise></c:choose></span><span class="itcper" style="margin-left: 7px;">%</span>
					</div>
					</div>
					<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
							<script type="text/javascript">
								itcinput=$("#itc_input").text();
								itcinputservices=$("#itc_inputservices").text();
								itccapitalgood=$("#itc_capitalgood").text();
							 	drcr = $("input[class='enabledrcr']:checked").val();
								trdate = $("input[class='enabletransdate']:checked").val();
								cess = $("input[class='enablecessqty']:checked").val();
							</script>
					</div>
					</div>
					
					<div class="tab-pane" id="gtab6" role="tabpane6">
						<div class="row">
						<div class="form-group col-md-12 col-sm-12"><div class="" style="display: flex;float:right;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="ewaybill_editbutton" style="margin-right:3px;padding:1.5px 15px" onClick="editebillconfig()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="ewaybill_savebutton" style="margin-right:3px; display:none;padding:1.5px 15px;" onClick="saveebillconfig()" value="Save"  />
								<input type="button" class="btn btn-blue-dark" id="ewaybill_cancelbutton" style="margin-right:3px; display:none;padding:1.5px 15px;" onClick="cancelebillconfig()" value="Cancel"/>
							</div></div>							
							<div class="col-md-12 col-sm-12 mb-2">
								<p class="lable-txt">Eway Bill Configuration: </p>
								<p style="font-size:13px;">Please configure your Eway Bill User Name & Password and Authenticate your credentials.</p>
								</div>
								<span id="ewayBillAuthError" class="pl-3" style="font-size:12px;color:red;"></span>
								<div class="col-md-12 col-sm-12 mb-2">
								<p class="lable-txt">Connection Status<span class="colon" style="margin-left: 5px;">:</span>  
								<span id="connStatus" class="ml-3"></span></p>
								</div>
								
							<div class="col-md-12 col-sm-12 mb-2" id="">
								<p class="lable-txt">API User Name<span class="colon" style="margin-left: 27px;">:</span> 
								<!-- <input type="text" class="username" id="username" style="width: 200px;margin-left: 15px;"> -->
								<span id="uname" class="ml-3" style="display:inline-block;"><c:choose><c:when test="${not empty ebillname}">${ebillname}</c:when><c:otherwise></c:otherwise></c:choose></span>
								</p>
							</div>
							<div class="col-md-12 col-sm-12 mb-2" id="">
								<p class="lable-txt">API Password<span class="colon" style="margin-left: 35px;">:</span>  
								<!-- <input type="text" class="password" id="password" style="width: 200px;margin-left: 15px;"> -->
								<span id="pwd"  class="ml-3" style="display:inline-block;"><c:choose><c:when test="${not empty ebillpwd}">${ebillpwd}</c:when><c:otherwise></c:otherwise></c:choose></span>
								</p>
							</div>
							<input type="button" class="btn btn-greendark mt-2" id="ewaybill_authbutton" style="display:none;margin-right:3px;padding:3px 10px;margin-left:22%;" onClick="authebillconfig()" value="Authenticate Now" />
							</div>
							<script type="text/javascript">
								username=$("#uname").text();
								password=$("#pwd").text();
								status=$("#connStatus").text();
							</script>
							</div>
					<div class="tab-pane" id="gtab7" role="tabpane7">
						<div class="row">
						<div class="form-group col-md-12 col-sm-12"><div class="" style="display: flex;float:right;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="einvoice_editbutton" style="margin-right:3px;padding:1.5px 15px" onClick="editEinvoiceconfig()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="einvoice_savebutton" style="margin-right:3px; display:none;padding:1.5px 15px;" onClick="saveEinvoiceconfig()" value="Save"  />
								<input type="button" class="btn btn-blue-dark" id="einvoice_cancelbutton" style="margin-right:3px; display:none;padding:1.5px 15px;" onClick="cancelEinvoiceconfig()" value="Cancel"/>
							</div></div>							
							<div class="col-md-12 col-sm-12">
								<p class="lable-txt" style="font-size:15px">E-Invoice Configuration: </p>
								<p style="font-size:13px;padding-left: 15px;margin-bottom: 5px;">Please configure your E-Invoice User Name & Password and Authenticate your credentials.</p>
								</div>
								<div class="pl-3">
										<span id="EinvAuthError" class="" style="font-size:12px;color:red;"></span>
										<div class="col-md-12 col-sm-12 mb-2">
											<p class="lable-txt">Connection Status<span class="colon" style="margin-left: 5px;">:</span>  
											<span id="EinvConnStatus" class="ml-3"></span></p>
										</div>
										<div class="col-md-12 col-sm-12 mb-2" id="">
											<p class="lable-txt">API User Name<span class="colon" style="margin-left: 27px;">:</span> 
											<!-- <input type="text" class="username" id="username" style="width: 200px;margin-left: 15px;"> -->
											<span id="euname" class="ml-3" style="display:inline-block;"><c:choose><c:when test="${not empty eInvUname}">${eInvUname}</c:when><c:otherwise></c:otherwise></c:choose></span>
											</p>
									  </div>
									<div class="col-md-12 col-sm-12 mb-2" id="">
										<p class="lable-txt">API Password<span class="colon" style="margin-left: 35px;">:</span>  
										<!-- <input type="text" class="password" id="password" style="width: 200px;margin-left: 15px;"> -->
										<span id="epwd"  class="ml-3" style="display:inline-block;"><c:choose><c:when test="${not empty eInvPwd}">${eInvPwd}</c:when><c:otherwise></c:otherwise></c:choose></span>
										</p>
									</div>
									<input type="button" class="btn btn-greendark mt-2" id="einvoice_authbutton" style="display:none;margin-right:3px;padding:3px 10px;margin-left:22%;" onClick="authEinvconfig()" value="Authenticate Now" />
							</div>
							</div>
							<div class="row">
								<div class="col-md-12 col-sm-12 mb-2 mt-4">
									<p class="lable-txt" style="font-size:15px">Configure/Upload QR Code for B2C Invoices : </p>
									<div class="pl-3">
										<p style="font-size:13px;margin-bottom: 5px;">Your QR Code will be provided by your banker, and for each of your bank they will provide you one QR Code, please upload/attach the QR Code Image file in Bank configuration to display in B2C invoices.<a href="${contextPath}/cp_bankDetails/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}"> Click here to Configure</a>.</p>
										<p style="font-size:13px;margin-bottom: 5px;"><strong>Step 1: </strong><span class="pl-2">Bank Configuration</span> </p>
										<p style="font-size:13px;margin-bottom: 5px;"><strong>Step 2: </strong><span class="pl-2">Add / Edit Bank</span></p>
										<p style="font-size:13px;margin-bottom: 5px;"><strong>Step 3: </strong><span class="pl-2">Upload QR Code Image File </span></p>
										<p style="font-size:13px;margin-bottom: 5px;"><strong>Step 4: </strong><span class="pl-2">Save</span></p>
									</div>
								</div>
							
							</div>
							<script type="text/javascript">
								einvusername=$("#euname").text();
								einvpassword=$("#epwd").text();
								einvstatus=$("#EinvConnStatus").text();
							</script>
							</div>
					<div class="tab-pane" id="gtab2" role="tabpane2">
						<div class="row">
							<c:choose>
						<c:when test="${not empty client.notes && not empty client.terms || not empty client.authorisedSignatory}">
						<div class="form-group col-md-12 col-sm-12"><div class="" style="display: flex;float:right;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Invoice_Print-Edit" id="editbutton" style="margin-right:3px;padding:1.5px 15px" onClick="editNotes1()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="savebutton" style="margin-right:3px; display:none;padding:1.5px 15px;" onClick="saveNotes1()" value="Save"  />
								<input type="button" class="btn btn-blue-dark" id="cancelbutton" style="margin-right:3px; display:none;padding:1.5px 15px;" onClick="cancelnotes2()" value="Cancel"/>
							</div></div>
							<span style="display:none" id="clntauthsignatory">${client.authorisedSignatory}</span>
							<div class="col-md-12 col-sm-12 note_terms_divs mb-2 ">
								<p class="lable-txt row"><span class="col-md-2">Customer Notes</span> <span class="col-md-1 pull-right">:</span></p>
								<span id="cust_note" class="ccnotes"></span>
							</div>
							<div class="col-md-12 col-sm-12 note_terms_divs mb-2">
								<p class="lable-txt row"><span class="col-md-2">Terms &amp; Conditions</span><span class="col-md-1"> :</span></p>
								<span id="terms_condition" class="tms_cond"></span>
							</div>
							<div class="col-md-12 col-sm-12 d-none note_terms_divs mb-2 " id="enablesign1">
								<p class="lable-txt row"><span class="col-md-4">Enable Authorised Signatory Name</span><span class="">:</span> 
							<span id="enable_auth_signatory_name" class="enable_auth_signatory" style="display: inline-block;  margin-left: -38px; margin-top: 3px; margin-bottom: 4px;">${client.enableAuthorisedSignatory} </span> ( If you enable this option, this signature will come in all your print invoices )</p>
								</div>	
							<div class="col-md-12 col-sm-12 note_terms_divs mb-2 " id="sign1">
								<p class="lable-txt row"><span class="col-md-2">Signatory Name </span><span class="col-md-1">:</span><span class="enableSignatory" <c:if test="${client.enableAuthorisedSignatory eq 'true'}">style="color:green"</c:if><c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}">style="color:red"</c:if> ><c:if test="${client.enableAuthorisedSignatory eq 'true'}">Yes</c:if><c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}">No</c:if></span></p>
								<span id="auth_signatory_name" class="auth_signatory" style="width:50%;" <c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}"> style="display:none"</c:if>><c:if test="${not empty client.authorisedSignatory}">${client.authorisedSignatory}</c:if><c:if test="${empty client.authorisedSignatory}">${client.signatoryName}</c:if></span>
							</div>
							<div class="col-md-12 col-sm-12 note_terms_divs mb-2 " id="sign2">
								<p class="lable-txt  row"><span class="col-md-2">Designation</span><span class="col-md-1"> :</span><span class="designation1" <c:if test="${client.enableAuthorisedSignatory eq 'true'}">style="color:green"</c:if><c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}">style="color:red"</c:if> ><c:if test="${client.enableAuthorisedSignatory eq 'true'}">Yes</c:if><c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}">No</c:if></span></p>
								<span id="auth_designation" class="auth_designation" <c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}"> style="display:none"</c:if>><c:if test="${not empty client.designation}">${client.designation}</c:if></span>
							</div>
							<script type="text/javascript">
								notes_old=$("#cust_note").text();
								terms_old=$("#terms_condition").text();
								signatory_old=$("#auth_signatory_name").text();
								invtext_old=$("#inv_text").text();
								invqty_old=$("#inv_qty_text").text();
								invrate_old=$("#inv_rate_text").text();
								invauthsign_old=$('#inv_authsign_text').text();
								designation_old=$("#auth_designation").text();
							</script>
						</c:when>						
						<c:otherwise>
							<div class="form-group col-md-12 col-sm-12"><div class="" style="display: flex;float:right;">
								<input type="button" class="btn btn-blue-dark permissionSettings-Configurations-Edit" id="editbutton" style="margin-right:3px;padding:1.5px 15px" onClick="editNotes()" value="Edit" />
								<input type="button" class="btn btn-blue-dark" id="savebutton" style="margin-right:3px;padding:1.5px 15px;display:none;" onClick="saveNotes()" value="Save" disabled />
								<input type="button" class="btn btn-blue-dark" id="cancelbutton" style="margin-right:3px;padding:1.5px 15px;display:none;" onClick="cancelnotes1()" value="Cancel" />
							</div></div>
							<span style="display:none" id="clntauthsignatory">${client.authorisedSignatory}</span>
							<div class="col-md-12 col-sm-12 note_terms_divs mb-2">
								<p class="lable-txt row"><span class="col-md-2">Customer Notes</span> <span class="col-md-1">:</span></p>
								<span id="cust_note" class="ccnotes"></span>
							</div>							
							<div class="col-md-12 col-sm-12 note_terms_divs mb-2">
								<p class="lable-txt row"><span class="col-md-2">Terms &amp; Conditions</span><span class="col-md-1"> :</span></p>
								<span id="terms_condition" class="tms_cond"></span>
							</div>
							<div class="col-md-12 col-sm-12 d-none note_terms_divs mb-2" id="enablesign">
								<p class="lable-txt row"><span class="col-md-4">Enable Authorised Signatory Name</span><span class="col-md-1">:</span> 
								<span id="enable_auth_signatory_name" class="enable_auth_signatory" style="display: inline-block;  margin-left: -38px; margin-top: 3px; margin-bottom: 4px;"></span>( If you enable this option, this signature will come in all your print invoices )</p>
							</div>
							<div class="col-md-12 col-sm-12 note_terms_divs mb-2" id="sign">
								<p class="lable-txt row"><span class="col-md-2">Signatory Name </span><span class="col-md-1">:</span><span class="enableSignatory" style="color:green"></span></p>
								<span id="auth_signatory_name" class="auth_signatory"><%-- <c:if test="${not empty client.authorisedSignatory}">${client.authorisedSignatory}</c:if><c:if test="${empty client.authorisedSignatory}">${client.signatoryName}</c:if> --%>( If you enable this option, this signature will come in all your print invoices )</span>
							</div>
							<div class="col-md-12 col-sm-12 note_terms_divs mb-2" id="dsg">
								<p class="lable-txt row"><span class="col-md-2">Designation </span><span class="col-md-1">:</span><span class="designation1" style="color:green"></span></p>
								<span id="auth_designation" class="auth_designation"><%-- <c:if test="${not empty client.authorisedSignatory}">${client.authorisedSignatory}</c:if><c:if test="${empty client.authorisedSignatory}">${client.signatoryName}</c:if> --%>( If you enable this option, this signature will come in all your print invoices )</span>
							</div>
							<script type="text/javascript">
								notes_old=" ";
								terms_old=" ";
								signatory_old=" ";
								invtext_old=" ";
								invqty_old=" ";
								invrate_old=" ";
								invauthsign_old=" ";
								designation_old=" ";
							</script>
						</c:otherwise>
					</c:choose>
						</div>
					</div>
					<div class="tab-pane" id="gtab8" role="tabpane8">
					<jsp:include page="/WEB-INF/views/profile/customFields.jsp" />
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- right sidebar end -->
      </div>
    </div>
  </div>
</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<div class="modal fade" id="editModal" role="dialog" aria-labelledby="editModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
            <div class="modal-header p-0">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                     <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                     </button>
                 <div class="bluehdr" style="width:100%"><h3>Configure Invoice Number</h3></div>
            </div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                     
                                   <!-- row begin -->
                  <form:form method="POST" data-toggle="validator" class="meterialform col-12" name="invoiceform" action="${contextPath}/saveinvsubmssion/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" modelAttribute="invoice" style="display:block">		
                  <div class="content p-5">
                  <div class="row congic_number">
                  <div class="form-group col-md-6"><p class="lable-txt">Invoice Type :</p></div>
                  <div class="form-group col-md-6"><select id="invoicetype" name="invoiceType">
				  <option value="">- Select -</option>
				  <option value="<%=MasterGSTConstants.B2B%>">B2B/B2CL/B2C</option>
				  <option value="<%=MasterGSTConstants.EXPORTS%>">EXPORTS</option>
				  <%-- <option value="<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>">CREDIT/DEBIT NOTE</option> --%>
				  <option value="<%=MasterGSTConstants.CREDIT_NOTES%>">CREDIT NOTE</option>
				  <option value="<%=MasterGSTConstants.DEBIT_NOTES%>">DEBIT NOTE</option>
				  <option value="<%=MasterGSTConstants.ADVANCES%>">ADVANCE RECEIPTS (RECEIPT VOUCHER)</option>
				  <option value="<%=MasterGSTConstants.NIL%>">BILL OF SUPPLY</option>
				  <option value="<%=MasterGSTConstants.ATPAID%>">ADVANCE ADJUSTED DETAIL</option>
				  <option value="<%=MasterGSTConstants.DELIVERYCHALLANS%>">DELIVERY CHALLANS</option>
				  <option value="<%=MasterGSTConstants.PROFORMAINVOICES%>">PROFORMA INVOICES</option>
				  <option value="<%=MasterGSTConstants.ESTIMATES%>">ESTIMATES</option>
				  <option value="<%=MasterGSTConstants.PURCHASEORDER%>">PURCHASE ORDER</option>
				  <option value="Receipts">Receipts</option>
				 <!--  <option value="Vouchers">Vouchers</option>
				  <option value="Contra">Contra</option> -->
				  <option value="Payments">Payments</option>
				  <option value="PurchaseReverseChargeNo">Purchase Reverse Charge No</option>
				  </select>
                  <label for="input" class="control-label"></label>
                  <i class="bar"></i></div>
                  </div>
                <div class="row congic_number">
                <div class="form-group col-md-6"><p class="lable-txt astrich" style="margin-top: 13px;">Prefix :</p></div>
                <div class="form-group col-md-6">
                <input type="text" id="prefix" onKeyUp="updateSampleInvNo()" name="prefix"  placeholder="ABCDEF" data-minlength="1" maxlength="12"  data-error="please enter prefix" required />
                <i class="bar"></i>
              <div class="help-block with-errors"></div>
                </div>
                </div>
                <div class="row congic_number">
                <div class="form-group col-md-6"><p class="lable-txt">Month ( Current Month) :</p></div>
                <div class="form-group col-md-3">
                <div class="form-check">
                <input class="form-check-input" onChange="updateSampleInvNo()" type="checkbox" id="month_check" name="allowMonth" value="true" checked="">
                <label for="config_invoice"><span class="ui"></span>
                </label>
                </div>
				</div>
				<div class="form-group col-md-3 formatMonth">
				<select id="formatMonth" name="formatMonth" onChange="updateSampleInvNo()">
				<option value="02">02</option>
				<option value="02/">02/</option>
				<option value="02-">02-</option>
				<option value="FEB">FEB</option>
				<option value="FEB/">FEB/</option>
				<option value="FEB-">FEB-</option>
				</select>
				<label for="input" class="control-label"></label>
                  <i class="bar"></i>
				</div>     
                </div>
                <div class="row congic_number">
                <div class="form-group col-md-6"><p class="lable-txt">Year ( Current Year) :</p></div>
                <div class="form-group col-md-3">
                <div class="form-check">
                <input class="form-check-input" onChange="updateSampleInvNo()" type="checkbox" id="year_check" name="allowYear" value="true" checked="">
                <label for="config_invoice"><span class="ui"></span>
                </label>
                </div>
                </div>
				<div class="form-group col-md-3 formatYear">
				<select id="formatYear" name="formatYear" onChange="updateSampleInvNo()"><option value="19">19</option><option value="19/">19/</option><option value="19-">19-</option><option value="2019">2019</option><option value="2019/">2019/</option><option value="2019-">2019-</option><option value="19-20">19-20</option><option value="19-20-">19-20-</option><option value="19-20/">19-20/</option><option value="1920">1920</option><option value="1920/">1920/</option><option value="1920-">1920-</option><option value="2019-20">2019-20</option><option value="2019-20/">2019-20/</option><option value="2019-20-">2019-20-</option><option value="201920">201920</option><option value="201920/">201920/</option><option value="201920-">201920-</option></select>
				<label for="input" class="control-label"></label>
                  <i class="bar"></i>
				</div>
                </div>
                <div class="row congic_number">
                <div class="form-group col-md-6"><p class="lable-txt astrich" style="margin-top: 13px;">Start Invoice Number :</p></div>
                <div class="form-group col-md-6">
                <input type="text" id="startInvoiceNo" onKeyUp="updateSampleInvNo()" name="startInvoiceNo"  data-minlength="1" maxlength="10" pattern="[0-9]+" data-error="Please enter valid number" placeholder="Starting invoice number" required />
                <i class="bar"></i>
				<div class="help-block with-errors"></div>
				</div>
                </div>
                <div class="row congic_number">
                <div class="form-group col-md-6"><p class="lable-txt" style="margin-top: 13px;">End Invoice Number :</p></div>
                <div class="form-group col-md-6">
                <input type="text" id="endInvoiceNo" name="endInvoiceNo" placeholder="Ending invoice number" data-minlength="1" maxlength="10" pattern="[0-9]+" data-error="Please enter valid number" />
                <i class="bar"></i>
				<div class="help-block with-errors"></div>
				</div>
                </div>                
                <div class="row">
                <div class="form-group col-md-6"><p class="lable-txt" style="margin-top: 13px;">Reset Invoice Number :</p></div>
                <div class="form-group-inline col-md-6">
					<div class="form-radio">
						<div class="radio">
							<label><input name="invoicenumbercutoff" id="yearlycutoff" type="radio" value="Yearly" checked/><i class="helper"></i>Yearly</label>
						</div>
					</div>
					<div class="form-radio">
						<div class="radio">
							<label><input name="invoicenumbercutoff" id="monthlycutoff" type="radio" value="Monthly"/><i class="helper"></i>Monthly</label>
						</div>
					</div>
				</div>
				</div>
				<span class="errormsg" id="sampleInv_Msg"></span>
                <div class="">&nbsp;</div>
                	<div class="row congic_number" style="margin-top -12px;">				
		                <div class="form-group col-md-6"><p class="lable-txt">Your Invoice number look like :</p></div>				
		                <div class="form-group col-md-6" id="idSampleInvNo" style="height: 33px;font-size: 22px;margin-top: 0px;font-family: 'latolight', sans-serif;font-weight: bold;"></div>
		                <div class="form-check mb-2" id="config_invoice_check" style="margin-top: 20px!important"><div class="meterialform"><div class="checkbox"><label><input  type="checkbox" ><i class="helper active_client"></i>Renumber Same Pattern for every Year</label></div></div></div>
				     </div>
                </div>
                <div class="modal-footer" style="margin-top:-60px;">
					<input type="hidden" name="sampleInvNo" id="sampleInvNo" value="">
                	<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
							<input type="hidden" name="year" id="finYear" value="<c:out value="${invoice.year}"/>">
							<input type="hidden" name="createdDate" value="">
							<input type="hidden" name="createdBy" value="">
                   <button type="submit" id="submitButton" class="btn btn-blue-dark submit-config-form hidden">Configure Now</button>   
                </div>
                </form:form>
                    </div>
                    <div class="modal-footer">
                    <label for="submit-config-form" class="btn btn-blue-dark m-0 submit-label" tabindex="0" onclick="labelupsubmit()">Configure Now</label>
                    <button type="button" class="btn btn-blue-dark " data-dismiss="modal" aria-label="Close">cancel</button>     
                    </div>
                    <!-- row end -->
                </div>
            </div>
        </div>
        <div class="modal fade deleteModal" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Item </h3>
        </div>
        <div class="pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete invoice configuration with prefix <span id="delPopupDetails" class="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Invoice Configuration</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script>
	if(clientSignature == ""){
		$('#configure_clientdetails').html(clientName + "&#13;" + clientEmail + "&#13;" + clientMobile);
	}else{
		$('#configure_clientdetails').html(clientSignature.replaceAll("#mgst#", "</br>"));
	}
function labelupsubmit(){
	updateSampleInvNo();
	$('#invoice').submit();
}
$('#invoice').submit(function(e){
	var err = 0;
	$('#invoice').find('input').each(function(){
	    if(!$(this).prop('required')){
	    }else{
	    	
	    	var bca = $(this).val();
		 	   if( bca == ''){
		 		  err = 1;
		 		   $(this).parent().addClass('has-error has-danger');
		 	   }else{
		 		   $(this).parent().removeClass('has-error has-danger');
		 	   }
	    }
	});
	$('#invoice').find('select').each(function(){
	    if(!$(this).prop('required')){
	    }else{
				if (this.value == '-Select-' || this.value == '') {
					err = 1;
			       $(this).parent().addClass('has-error has-danger');
			    }else{
			    	$(this).parent().removeClass('has-error has-danger');
			    }
	    }
	});
	 if (err != 0) {
		return false;
	  }
});
$('#digitalSignature').change(function() {
  var status = $(this).is(':checked');
  if(status == true){$('.upload_scan_sign').css('display','block');$('.imgsize-wrap-thumb').css('display','block');}else{$('.upload_scan_sign').css('display','none');  $('.imgsize-wrap-thumb').css('display','none'); }
 $.ajax({
		url: "${contextPath}/digitalSign/${client.id}",
		async: false,
		cache: false,
		data: {
			'digitalSign': status
		},
		success : function(data) {
		}
	}); 
});
	function updateSampleInvNo() {
		var prefix=$('#prefix').val();
		var start = $('#startInvoiceNo').val();
		var sampleInv = prefix.toUpperCase();
		var date = new Date();
		if($('#month_check').prop("checked")) {
			var mnth = $('#formatMonth').val();
			sampleInv += mnth;
			var sample = sampleInv;
			$('.formatMonth').css('display','block');
			var formatMonth =  $('#formatMonth').val();
			if(formatMonth == ''){
				$('#formatMonth').val('02');
			}
		}
		if(!$('#month_check').prop("checked")) {$('.formatMonth').css('display','none');$('#disp_month').html("");}
		if($('#year_check').prop("checked")) {
			var yrs = $('#formatYear').val();
			sampleInv += yrs;
			$('.formatYear').css('display','block');
			var formatYear =  $('#formatYear').val();
			if(formatYear == ''){
				$('#formatYear').val('19');
			}
		}
		if(!$('#year_check').prop("checked")) {
			$('#disp_year').html("");
			$('.formatYear').css('display','none');
			$('#finacialYearFormat').prop("checked",false);
		}
			sampleInv += start;
			$('#idSampleInvNo').html(sampleInv);
			$('#sampleInvNo').val(sampleInv);
			if(sampleInv.length >= 17){
				$('#sampleInv_Msg').html('Invoice Number max allowed characters are 16 only, please adjust your Configuration accordingly.');
				$('#submitButton').attr("disabled", "disabled");
			}else{
				$('#sampleInv_Msg').html('');
				$('#submitButton').removeAttr("disabled");
			}
	}
	function saveClientSignature(event){
		var fi = event.target.files[0];
		var img = URL.createObjectURL(fi);
		$('#clntsigns').attr('src', img);
		$('#clntssign').ajaxSubmit( {
			url: $("#clntssign").attr("action"),
			dataType: 'json',
			type: 'POST',
			cache: false,
			success: function(response) {
			},
			error: function(e) {
			}
		});
	}
	$(function() {
		OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});	
    $("#allTypes").click(function() {
        var el = $("#invoicetype");
        if (el) {
            el.removeAttr("disabled");
            if (this.checked) {
                el.attr("disabled", "disabled").css("background-color","lightgrey");
            }else{
				el.removeAttr("disabled").css("background-color","");
			}
        }
    });
	$("#financalYear").on('change', function(){
		var finyear = $(this).val();
		$('#finYear').val($(this).val());
		var res = finyear.split("-");
		$('.configInvoice').removeAttr('disabled');
		var rowNode;
		table.clear().draw();
		$.ajax({
				url: "${contextPath}/invoicetype/${id}/${fullname}/${usertype}/${client.id}/"+finyear,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					var invtype = '<c:out value="${invType}"/>';
					response.forEach(function(invoice) {
						var invDate = new Date(invoice.submittedDate);
						var day = invDate.getDate() + "";
						var month = (invDate.getMonth() + 1) + "";
						var year = invDate.getFullYear() + "";
						day = checkZero(day);
						month = checkZero(month);
						year = checkZero(year);
						invoice.submittedDate = day + "-" + month + "-" + year;
						var sample;
						if(invoice.sampleInvNo){
							sample = invoice.sampleInvNo;
						}else{
							sample = invoice.prefix+${year}+${month}+invoice.startInvoiceNo;
						}						
						rowNode = table.row.add([invoice.invoiceType,invoice.prefix,invoice.year,invoice.startInvoiceNo,invoice.endInvoiceNo,invoice.submittedDate,sample,'<a class="btn-edt permissionSettings-Invoice_Print-Edit" href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement(\''+invoice.userid+'\')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Invoice_Print-Delete" onClick="showDeletePopup(\''+invoice.userid+'\',\''+invoice.prefix+'\',\''+invoice.invoiceType+'\')"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" style="margin-top: -6px;"></a>']).node().id = "row"+invoice.userid;
						table.row(rowNode).column(8).nodes().to$().addClass('actionIcons');
							var uinvoice = new Object();
									uinvoice.id = invoice.userid;
									uinvoice.invoiceType = invoice.invoiceType;
									uinvoice.returnType = invoice.returnType;
									uinvoice.prefix = invoice.prefix;
									uinvoice.allowMonth = invoice.allowMonth;
									uinvoice.allowYear = invoice.allowYear;
									uinvoice.year = invoice.year;
									uinvoice.startInvoiceNo = invoice.startInvoiceNo;
									uinvoice.endInvoiceNo = invoice.endInvoiceNo;
									uinvoice.submittedDate = invoice.submittedDate;	
									uinvoice.createdBy = invoice.createdBy;
									uinvoice.sampleInvNo = sample;
									uinvoice.formatMonth = invoice.formatMonth;
									uinvoice.formatYear = invoice.formatYear;
									uinvoice.invoicenumbercutoff = invoice.invoicenumbercutoff;
									invoices.push(uinvoice);
							
					});
						if(res[1] < '${year}'){
							$('.configInvoice').attr('disabled','disabled');
						}
						table.draw();
						if(res[1] < '${year}'){
							$('.actionIcons a').css('display','none');
						}
				},error: function(e) {
					
				}
			});			
	});	
	$("input[name='submiton']").click(function(){
			var status = $("input[name='submiton']:checked").val();
			$.ajax({
				url: "${contextPath}/clntgstsubmiton/${client.id}",
				async: false,
				cache: false,
				data: {
					'submiton': status
				},
				success : function(data) {
					successNotification(status + ' Submit to GST is selected for Invoice Configuration');
				}
			});
		});
		function checkZero(data){
	  if(data.length == 1){
		data = "0" + data;
	  }
	  return data;
	}
		
		if(tcsStatus == "false" || tcsStatus == false){
			$('#enable_tcs_field').text("Yes");
			$('#enable_tcs_field').css("color","green");
		}else{
			$('#enable_tcs_field').text("No");
			$('#enable_tcs_field').css("color","red");
		}
		 if(estatus == "Active"){
				$('#connStatus').html("Active").css("color","green");;
			}else{
				$('#connStatus').html("InActive").css("color","red");;
			} 
		 if(eInvstatus == "Active"){
				$('#EinvConnStatus').html("Active").css("color","green");;
			}else{
				$('#EinvConnStatus').html("InActive").css("color","red");;
			} 

		if(drStatus == "true"){
			  $('#enable_drcr_field').text('Dr/Cr');
			   $('#enabledrcr').attr('checked',true);
			}else{
				 $('#enable_drcr_field').text('By/To');
				 $('#enablebyto').attr('checked',true);
			}
		   if(trStatus == "true"){
			   $('.tdate_invdate').text('Transaction Date');
				$('#enabletransdate').attr('checked',true);
				$('#enableinvdate').removeAttr('checked');
			}else{
				  $('.tdate_invdate').text('Invoice Date');
				$('#enableinvdate').attr('checked',true);
				$('#enabletransdate').removeAttr('checked');
			} 
		   if(cessStatus == "true"){
			   $('.cqty_ctaxAmt').text('Quantity');
				$('#enablecessqty').attr('checked',true);
				$('#enablecesstaxable').removeAttr('checked');
			}else{
				  $('.cqty_ctaxAmt').text('Taxable Value');
				$('#enablecesstaxable').attr('checked',true);
				$('#enablecessqty').removeAttr('checked');
			} 
		   if(lsStatus == "false" || lsStatus == false){
				$('#enable_invledgersales_field').text("Yes");
				$('#enable_invledgersales_field').css("color","green");
			}else{
				$('#enable_invledgersales_field').text("No");
				$('#enable_invledgersales_field').css("color","red");
			}
			if(lpStatus == "false" || lpStatus == false){
				$('#enable_invledgerpurchase_field').text("Yes");
				$('#enable_invledgerpurchase_field').css("color","green");
			}else{
				$('#enable_invledgerpurchase_field').text("No");
				$('#enable_invledgerpurchase_field').css("color","red");
			}
			if(rsStatus == "false" || rsStatus == false){
				$('#enable_invroundoffsales_field').text("Yes");
				$('#enable_invroundoffsales_field').css("color","green");
			}else{
				$('#enable_invroundoffsales_field').text("No");
				$('#enable_invroundoffsales_field').css("color","red");
			}
			if(rpStatus == "false" || rpStatus == false){
				$('#enable_invroundoffpurchase_field').text("Yes");
				$('#enable_invroundoffpurchase_field').css("color","green");
			}else{
				$('#enable_invroundoffpurchase_field').text("No");
				$('#enable_invroundoffpurchase_field').css("color","red");
			}
			if(journalStatus == "false" || journalStatus == false){
				$('#enable_journals_field').text("Yes");
				$('#enable_journals_field').css("color","green");
			}else{
				$('#enable_journals_field').text("No");
				$('#enable_journals_field').css("color","red");
			}
		if(sStatus == "false"){
			$('#enable_invsales_field').text("Yes");
			$('#enable_invsales_field').css("color","green");
		}else{
			$('#enable_invsales_field').text("No");
			$('#enable_invsales_field').css("color","red");
		}
		if(pStatus == "false"){
			$('#enable_invpurchase_field').text("Yes");
			$('#enable_invpurchase_field').css("color","green");
		}else{
			$('#enable_invpurchase_field').text("No");
			$('#enable_invpurchase_field').css("color","red");
		}
		if(footnote == "true" || footnote == true){
			$('#footnoteDiv').css("display","block");
		}else{
			$('#footnoteDiv').css("display","none");
		}
});
	function saveinvdate() {
		var cutOffDateForSales = $('#inv_cutoffdateSales').val();
		var cutOffDateForPurchases =	$('#inv_cutoffdatePurchases').val();
		$.ajax({
			url: "${contextPath}/saveCutOffInvdate/${client.id}",
			data: {
				'cutOffDateForSales': cutOffDateForSales,
				'cutOffDateForPurchases': cutOffDateForPurchases
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				window.location.href = "${contextPath}/cp_upload/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=";
			},
			error : function(e) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
</script>
</body>
</html>