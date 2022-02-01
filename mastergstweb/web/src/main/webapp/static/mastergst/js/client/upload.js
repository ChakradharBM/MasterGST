function editcustom(){
		for(var i=1; i<5; i++){
			var disInSales = $('.sacustom_lacon_'+i).text();
			if(disInSales == 'Yes'){$('#sacustomfiled'+i).attr("checked","checked");$('#clabelinput'+i).removeAttr('disabled');
			}else{$('#sacustomfiled'+i).removeAttr("checked");$('#clabelinput'+i).attr('disabled',true);}		
			var disInPurchase = $('.pucustom_lacon_'+i).text();
			if(disInPurchase == 'Yes'){$('#pucustomfiled'+i).attr("checked","checked");$('#customlabel'+i).removeAttr('disabled');
			}else{$('#pucustomfiled'+i).removeAttr("checked");$('#customlabel'+i).attr('disabled',true);}		
			var disInPrint = $('.prscustom_lacon_'+i).text();
			if(disInPrint == 'Yes'){$('#pcustomfiled'+i).attr("checked","checked");	
			}else{$('#pcustomfiled'+i).removeAttr("checked");}
			$('#clabelinput'+i).val($('.sacustom_lable_'+i).text());$('#customlabel'+i).val($('.pucustom_lable_'+i).text());
		}
		$('#custom_savebutton,#custom_cancelbutton').css('display','block');
		$('#custom_editbutton').css('display','none');
		$('.cFiled').show();$('.la_filed').hide();
	}
function editcustomewayOreinv(type){
		for(var i=1; i<5; i++){
			var disInSales = $('.'+type+'custom_lacon_'+i).text();
			if(disInSales == 'Yes'){$('#'+type+'customfiled'+i).attr("checked","checked");$('#'+type+'labelinput'+i).removeAttr('disabled');
			}else{$('#'+type+'customfiled'+i).removeAttr("checked");$('#clabelinput'+i).attr('disabled',true);}				
			var disInPrint = $('.'+type+'pcustom_lacon_'+i).text();
			if(disInPrint == 'Yes'){$('#'+type+'pcustomfiled'+i).attr("checked","checked");	
			}else{$('#'+type+'pcustomfiled'+i).removeAttr("checked");}
			$('#'+type+'labelinput'+i).val($('.'+type+'custom_lable_'+i).text());
		}
		$('#'+type+'custom_savebutton,#'+type+'custom_cancelbutton').css('display','block');
		$('#'+type+'custom_editbutton').css('display','none');
		$('.cFiled').show();$('.la_filed').hide();
	}
function editinvdate(){
	
	var invdateSales = $('#inv_cutoffdate_sales').text();
	var invdatePurchases = $('#inv_cutoffdate_purchases').text();
	 
		document.getElementById("inv_cutoffdate_sales").innerHTML='<input type="text" class="form-control reconcile" id="inv_cutoffdateSales" placeholder="dd/mm/yyyy" style="border: 1px solid lightgrey;" value='+invdateSales+'>';
		document.getElementById("inv_cutoffdate_purchases").innerHTML='<input type="text" class="form-control reconcile" id="inv_cutoffdatePurchases" placeholder="dd/mm/yyyy" style="border: 1px solid lightgrey;" value='+invdatePurchases+'>';
		$('#inv_cutoffdateSales,#inv_cutoffdatePurchases').datetimepicker({
	  	timepicker: false,
	  	format: 'd/m/Y',
	  	scrollMonth: true
	});
		$('#invdate_savebutton,#invdate_cancelbutton').css('display','block');
		$('#invdate_editbutton').css('display','none');
}
function cancelCustomValues(){
	if(oldsacustomfiled1 == 'true' || oldsacustomfiled1 == true){$('.sacustom_lacon_1').text('Yes').css("color","green");	
	}else{$('.sacustom_lacon_1').text('No').css("color","red");}
	if(oldpucustomfiled1 == 'true' || oldpucustomfiled1 == true){$('.pucustom_lacon_1').text('Yes').css("color","green");	
	}else{$('.pucustom_lacon_1').text('No').css("color","red");}
	if(oldpcustomfiled1 == 'true' || oldpcustomfiled1 == true){$('.prscustom_lacon_1').text('Yes').css("color","green");	
	}else{$('.prscustom_lacon_1').text('No').css("color","red");}
	$('.sacustom_lable_1').text(oldclabelinput1);
	$('.pucustom_lable_1').text(oldcustomlabel1);		
	if(oldsacustomfiled2 == 'true' || oldsacustomfiled2 == true){$('.sacustom_lacon_2').text('Yes').css("color","green");	
	}else{$('.sacustom_lacon_2').text('No').css("color","red");}
	if(oldpucustomfiled2 == 'true' || oldpucustomfiled2 == true){$('.pucustom_lacon_2').text('Yes').css("color","green");	
	}else{$('.pucustom_lacon_2').text('No').css("color","red");}
	if(oldpcustomfiled2 == 'true' || oldpcustomfiled2 == true){$('.prscustom_lacon_2').text('Yes').css("color","green");	
	}else{$('.prscustom_lacon_2').text('No').css("color","red");}
	$('.sacustom_lable_2').text(oldclabelinput2);
	$('.pucustom_lable_2').text(oldcustomlabel2);		
	if(oldsacustomfiled3 == 'true' || oldsacustomfiled3 == true){$('.sacustom_lacon_3').text('Yes').css("color","green");	
	}else{$('.sacustom_lacon_3').text('No').css("color","red");}
	if(oldpucustomfiled3 == 'true' || oldpucustomfiled3 == true){$('.pucustom_lacon_3').text('Yes').css("color","green");	
	}else{$('.pucustom_lacon_3').text('No').css("color","red");}
	if(oldpcustomfiled3 == 'true' || oldpcustomfiled3 == true){$('.prscustom_lacon_3').text('Yes').css("color","green");	
	}else{$('.prscustom_lacon_3').text('No').css("color","red");}
	$('.sacustom_lable_3').text(oldclabelinput3);
	$('.pucustom_lable_3').text(oldcustomlabel3);		
	if(oldsacustomfiled4 == 'true' || oldsacustomfiled4 == true){$('.sacustom_lacon_4').text('Yes').css("color","green");	
	}else{$('.sacustom_lacon_4').text('No').css("color","red");}
	if(oldpucustomfiled4 == 'true' || oldpucustomfiled4 == true){$('.pucustom_lacon_4').text('Yes').css("color","green");	
	}else{$('.pucustom_lacon_4').text('No').css("color","red");}
	if(oldpcustomfiled4 == 'true' || oldpcustomfiled4 == true){$('.prscustom_lacon_4').text('Yes').css("color","green");	
	}else{$('.prscustom_lacon_4').text('No').css("color","red");}
	$('.sacustom_lable_4').text(oldclabelinput4);
	$('.pucustom_lable_4').text(oldcustomlabel4);
}
function cancelcustom(){
	cancelCustomValues();
	$('#custom_savebutton,#custom_cancelbutton').css('display','none');
	$('#custom_editbutton').css('display','block');
	$('.cFiled').hide();$('.la_filed').show();
}
function cancelCustomValuesEwayOrEinv(type){
	if(type == 'eway'){
		if(oldewaycustomfiled1 == 'true' || oldewaycustomfiled1 == true){$('.ewaycustom_lacon_1').text('Yes').css("color","green");	
		}else{$('.ewaycustom_lacon_1').text('No').css("color","red");}
		if(oldewaypcustomfiled1 == 'true' || oldewaypcustomfiled1 == true){$('.ewaypcustom_lacon_1').text('Yes').css("color","green");	
		}else{$('.ewaypcustom_lacon_1').text('No').css("color","red");}
		$('.ewaycustom_lable_1').text(oldewayclabelinput1);
		if(oldewaycustomfiled2 == 'true' || oldewaycustomfiled2 == true){$('.ewaycustom_lacon_2').text('Yes').css("color","green");	
		}else{$('.ewaycustom_lacon_2').text('No').css("color","red");}
		if(oldewaypcustomfiled2 == 'true' || oldewaypcustomfiled2 == true){$('.ewaypcustom_lacon_2').text('Yes').css("color","green");	
		}else{$('.ewaypcustom_lacon_2').text('No').css("color","red");}
		$('.ewaycustom_lable_2').text(oldewayclabelinput2);
		if(oldewaycustomfiled3 == 'true' || oldewaycustomfiled3 == true){$('.ewaycustom_lacon_3').text('Yes').css("color","green");	
		}else{$('.ewaycustom_lacon_3').text('No').css("color","red");}
		if(oldewaypcustomfiled3 == 'true' || oldewaypcustomfiled3 == true){$('.ewaypcustom_lacon_3').text('Yes').css("color","green");	
		}else{$('.ewaypcustom_lacon_3').text('No').css("color","red");}
		$('.ewaycustom_lable_3').text(oldewayclabelinput3);
		if(oldewaycustomfiled4 == 'true' || oldewaycustomfiled4 == true){$('.ewaycustom_lacon_4').text('Yes').css("color","green");	
		}else{$('.ewaycustom_lacon_4').text('No').css("color","red");}
		if(oldewaypcustomfiled4 == 'true' || oldewaypcustomfiled4 == true){$('.ewaypcustom_lacon_4').text('Yes').css("color","green");	
		}else{$('.ewaypcustom_lacon_4').text('No').css("color","red");}
		$('.ewaycustom_lable_4').text(oldewayclabelinput4);
	}else{
		if(oldeinvcustomfiled1 == 'true' || oldeinvcustomfiled1 == true){$('.einvcustom_lacon_1').text('Yes').css("color","green");	
		}else{$('.einvcustom_lacon_1').text('No').css("color","red");}
		if(oldeinvpcustomfiled1 == 'true' || oldeinvpcustomfiled1 == true){$('.einvpcustom_lacon_1').text('Yes').css("color","green");	
		}else{$('.einvpcustom_lacon_1').text('No').css("color","red");}
		$('.einvcustom_lable_1').text(oldeinvclabelinput1);
		if(oldeinvcustomfiled2 == 'true' || oldeinvcustomfiled2 == true){$('.einvcustom_lacon_2').text('Yes').css("color","green");	
		}else{$('.einvcustom_lacon_2').text('No').css("color","red");}
		if(oldeinvpcustomfiled2 == 'true' || oldeinvpcustomfiled2 == true){$('.einvpcustom_lacon_2').text('Yes').css("color","green");	
		}else{$('.einvpcustom_lacon_2').text('No').css("color","red");}
		$('.einvcustom_lable_2').text(oldeinvclabelinput2);
		if(oldeinvcustomfiled3 == 'true' || oldeinvcustomfiled3 == true){$('.einvcustom_lacon_3').text('Yes').css("color","green");	
		}else{$('.einvcustom_lacon_3').text('No').css("color","red");}
		if(oldeinvpcustomfiled3 == 'true' || oldeinvpcustomfiled3 == true){$('.einvpcustom_lacon_3').text('Yes').css("color","green");	
		}else{$('.einvpcustom_lacon_3').text('No').css("color","red");}
		$('.einvcustom_lable_3').text(oldeinvclabelinput3);
		if(oldeinvcustomfiled4 == 'true' || oldeinvcustomfiled4 == true){$('.einvcustom_lacon_4').text('Yes').css("color","green");	
		}else{$('.einvcustom_lacon_4').text('No').css("color","red");}
		if(oldeinvpcustomfiled4 == 'true' || oldeinvpcustomfiled4 == true){$('.einvpcustom_lacon_4').text('Yes').css("color","green");	
		}else{$('.einvpcustom_lacon_4').text('No').css("color","red");}
		$('.einvcustom_lable_4').text(oldeinvclabelinput4);
	}
}

function cancelcustomEwayOrEinv(type){
	cancelCustomValuesEwayOrEinv(type);
	$('#'+type+'custom_savebutton,#'+type+'custom_cancelbutton').css('display','none');
	$('#'+type+'custom_editbutton').css('display','block');
	$('.cFiled').hide();$('.la_filed').show();
}

function cancelebillconfig(){
	$('#ewaybill_cancelbutton,#ewaybill_savebutton,#ewaybill_authbutton').css("display","none");
	$('#ewaybill_editbutton').css("display","block");
	$('#uname').html(econfig);
	$('#pwd').html(epwd);
	
}
function editreconcile(){
	$('#reconcile_savebutton,#reconcile_cancelbutton').css('display','block');
	$('#reconcile_editbutton').css('display','none');
	var recon=$('#reconsileAmt').text().replace(/,/g, "");
	var reconDate = $('#alloweddays').text();
	var eim = $('#invmatch').text();
	/*var eimfc = $('#invfirstfivecharmatch').text();
	var eimlc = $('#invlastfivecharmatch').text();*/
	var eis = $('#enable_ignore_slash').text();
	var eih = $('#enable_ignore_hyphen').text();
	var eiz = $('#enable_ignore_zero').text();
	var eii = $('#enable_ignore_i').text();
	var eil = $('#enable_ignore_l').text();
	document.getElementById("reconsileAmt").innerHTML='<input type="text" class="form-control reconcile" id="reconcile" placeholder="Reconcile Amount" style="border: 1px solid lightgrey;" value='+recon+'>';
	document.getElementById("alloweddays").innerHTML='<input type="text" class="form-control reconcileDate" id="reconcileDate" placeholder="Reconcile Date" style="border: 1px solid lightgrey;" value='+reconDate+'>';
	if(eim == "Yes"){
		document.getElementById("invmatch").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableinvmatch" type="checkbox" id="enableinvmatch" checked><label for="enableinvmatch"><span class="ui"></span></label></div></div>';
	}else if(eim == '' || eim == 'No'){
		document.getElementById("invmatch").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableinvmatch" type="checkbox" id="enableinvmatch"><label for="enableinvmatch"><span class="ui"></span></label></div></div>';
	}
	/*if(eimfc == "Yes"){
		document.getElementById("invfirstfivecharmatch").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableInvoiceFirstFiveCharMatch" type="checkbox" id="enableInvoiceFirstFiveCharMatch" checked><label for="enableInvoiceFirstFiveCharMatch"><span class="ui"></span></label></div></div>';
	}else if(eimfc == '' || eimfc == 'No'){
		document.getElementById("invfirstfivecharmatch").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableInvoiceFirstFiveCharMatch" type="checkbox" id="enableInvoiceFirstFiveCharMatch"><label for="enableInvoiceFirstFiveCharMatch"><span class="ui"></span></label></div></div>';
	}
	if(eimlc == "Yes"){
		document.getElementById("invlastfivecharmatch").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableInvoiceLastFiveCharMatch" type="checkbox" id="enableInvoiceLastFiveCharMatch" checked><label for="enableInvoiceLastFiveCharMatch"><span class="ui"></span></label></div></div>';
	}else if(eimlc == '' || eimlc == 'No'){
		document.getElementById("invlastfivecharmatch").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableInvoiceLastFiveCharMatch" type="checkbox" id="enableInvoiceLastFiveCharMatch"><label for="enableInvoiceLastFiveCharMatch"><span class="ui"></span></label></div></div>';
	}*/
	if(eis == "Yes"){
		document.getElementById("enable_ignore_slash").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignoreslash" type="checkbox" id="enableignoreslash" checked><label for="enableignoreslash"><span class="ui"></span></label></div></div>';
	}else if(eis == '' || eis == 'No'){
		document.getElementById("enable_ignore_slash").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignoreslash" type="checkbox" id="enableignoreslash"><label for="enableignoreslash"><span class="ui"></span></label></div></div>';
	}
	if(eih == "Yes"){
		document.getElementById("enable_ignore_hyphen").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignorehyphen" type="checkbox" id="enableignorehyphen" checked><label for="enableignorehyphen"><span class="ui"></span></label></div></div>';
	}else if(eih == '' || eih == 'No'){
		document.getElementById("enable_ignore_hyphen").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignorehyphen" type="checkbox" id="enableignorehyphen"><label for="enableignorehyphen"><span class="ui"></span></label></div></div>';
	}
	if(eiz == "Yes"){
		document.getElementById("enable_ignore_zero").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignorezero" type="checkbox" id="enableignorezero" checked><label for="enableignorezero"><span class="ui"></span></label></div></div>';
	}else if(eiz == '' || eiz == 'No'){
		document.getElementById("enable_ignore_zero").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignorezero" type="checkbox" id="enableignorezero"><label for="enableignorezero"><span class="ui"></span></label></div></div>';
	}
	if(eii == "Yes"){
		document.getElementById("enable_ignore_i").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignorei" type="checkbox" id="enableignorei" checked><label for="enableignorei"><span class="ui"></span></label></div></div>';
	}else if(eii == '' || eii == 'No'){
		document.getElementById("enable_ignore_i").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignorei" type="checkbox" id="enableignorei"><label for="enableignorei"><span class="ui"></span></label></div></div>';
	}
	if(eil == "Yes"){
		document.getElementById("enable_ignore_l").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignorel" type="checkbox" id="enableignorel" checked><label for="enableignorel"><span class="ui"></span></label></div></div>';
	}else if(eil == '' || eil == 'No'){
		document.getElementById("enable_ignore_l").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableignorel" type="checkbox" id="enableignorel"><label for="enableignorel"><span class="ui"></span></label></div></div>';
	}
}
function cancelreconcile(){
	$("#reconcile_savebutton,#reconcile_cancelbutton").css('display','none');
	$("#reconcile_editbutton").css('display','block');
	$('#reconsileAmt').html(reconcile_amt_old);
	$('#alloweddays').html(reconcile_allowed_days);
	$('#invmatch').html(invoicematch);
	/*$('#invfirstfivecharmatch').html(invfirstfivechar);
	$('#invlastfivecharmatch').html(invlastfivechar);*/
	$('#enable_ignore_slash').html(ignoreslash);
	$('#enable_ignore_hyphen').html(ignorehyphen);
	$('#enable_ignore_zero').html(ignorezero);
	$('#enable_ignore_i').html(ignorei);
	$('#enable_ignore_l').html(ignorel);
	$(".indformat").each(function(){
	    $(this).html($(this).html().replace(/,/g , ''));
	});
	OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});	
}
function saveReconsileConfig(clientid) {
	$("#reconcile_savebutton").css('display','none');$("#reconcile_cancelbutton").css('display','none');$("#reconcile_editbutton").css('display','block');
	var amt = $('#reconcile').val();var alloweddays = $('#reconcileDate').val();
	/*var enable_invoicefirstfivematch_field=$('#enableInvoiceFirstFiveCharMatch').is(':checked');
	var enable_invoicelastfivematch_field=$('#enableInvoiceLastFiveCharMatch').is(':checked');*/
	var enable_invoicematch_field=$('#enableinvmatch').is(':checked');
	var enable_slash_field=$('#enableignoreslash').is(':checked');
	var enable_hyphen_field=$('#enableignorehyphen').is(':checked');
	var enable_zero_field=$('#enableignorezero').is(':checked');
	var enable_i_field=$('#enableignorei').is(':checked');
	var enable_l_field=$('#enableignorel').is(':checked');
	$.ajax({
		url: contextPath+"/updtclntconfig/"+clientid,
		data: {
			'diffamt': amt,
			'alloweddays': alloweddays,
			'enableInvoiceMatchField' : enable_invoicematch_field,
			'enableIgnoreSlashField':enable_slash_field,
			'enableIgnoreHyphenField':enable_hyphen_field,
			'enableIgnoreZeroField' : enable_zero_field,
			'enableIgnoreIField':enable_i_field,
			'enableIgnoreLField':enable_l_field
		},
		type:"POST",
		contentType: 'application/x-www-form-urlencoded',
		success : function(response) {
			successNotification('Save successful!');
			$('#reconsileAmt').html(amt);$('#alloweddays').html(alloweddays);
			reconcile_amt_old=amt;reconcile_allowed_days = alloweddays;
			$(".indformat").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
			OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});	
			if(enable_invoicematch_field == true || enable_invoicematch_field == "true"){
				invoicematch = 'Yes';$('#invmatch').html('Yes');$('#invmatch').css("display","inline-block");$('#invmatch').css("color","green");
			}else{
				invoicematch = 'No';$('#invmatch').css("display","inline-block");$('#invmatch').html('No');$('#invmatch').css("color","red");
			}
			/*if(enable_invoicefirstfivematch_field == true || enable_invoicefirstfivematch_field == "true"){
				invfirstfivechar = 'Yes';$('#invfirstfivecharmatch').html('Yes');$('#invfirstfivecharmatch').css("display","inline-block");$('#invfirstfivecharmatch').css("color","green");
			}else{
				invfirstfivechar = 'No';$('#invfirstfivecharmatch').css("display","inline-block");$('#invfirstfivecharmatch').html('No');$('#invfirstfivecharmatch').css("color","red");
			}
			if(enable_invoicelastfivematch_field == true || enable_invoicelastfivematch_field == "true"){
				invlastfivechar = 'Yes';$('#invlastfivecharmatch').html('Yes');$('#invlastfivecharmatch').css("display","inline-block");$('#invlastfivecharmatch').css("color","green");
			}else{
				invlastfivechar = 'No';$('#invlastfivecharmatch').css("display","inline-block");$('#invlastfivecharmatch').html('No');$('#invlastfivecharmatch').css("color","red");
			}*/
			if(enable_slash_field == true || enable_slash_field == "true"){
				ignoreslash = 'Yes';$('#enable_ignore_slash').html('Yes');$('#enable_ignore_slash').css("display","inline-block");$('#enable_ignore_slash').css("color","green");
			}else{
				ignoreslash = 'No';$('#enable_ignore_slash').css("display","inline-block");$('#enable_ignore_slash').html('No');$('#enable_ignore_slash').css("color","red");
			}
			if(enable_hyphen_field == true || enable_hyphen_field == "true"){
				ignorehyphen = 'Yes';$('#enable_ignore_hyphen').html('Yes');$('#enable_ignore_hyphen').css("display","inline-block");$('#enable_ignore_hyphen').css("color","green");
			}else{
				ignorehyphen = 'No';$('#enable_ignore_hyphen').css("display","inline-block");$('#enable_ignore_hyphen').html('No');$('#enable_ignore_hyphen').css("color","red");
			}
			if(enable_zero_field == true || enable_zero_field == "true"){
				ignorezero = 'Yes';$('#enable_ignore_zero').html('Yes');$('#enable_ignore_zero').css("display","inline-block");$('#enable_ignore_zero').css("color","green");
			}else{
				ignorezero = 'No';$('#enable_ignore_zero').css("display","inline-block");$('#enable_ignore_zero').html('No');$('#enable_ignore_zero').css("color","red");
			}
			if(enable_i_field == true || enable_i_field == "true"){
				ignorei = 'Yes';$('#enable_ignore_i').html('Yes');$('#enable_ignore_i').css("display","inline-block");$('#enable_ignore_i').css("color","green");
			}else{
				ignorei = 'No';$('#enable_ignore_i').css("display","inline-block");$('#enable_ignore_i').html('No');$('#enable_ignore_i').css("color","red");
			}
			if(enable_l_field == true || enable_l_field == "true"){
				ignorel = 'Yes';$('#enable_ignore_l').html('Yes');$('#enable_ignore_l').css("display","inline-block");$('#enable_ignore_l').css("color","green");
			}else{
				ignorel = 'No';$('#enable_ignore_l').css("display","inline-block");$('#enable_ignore_l').html('No');$('#enable_ignore_l').css("color","red");
			}
		},
		error : function(e) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
	});
}

function cancelother(){
	$("#other_savebutton").css('display','none');
	$("#other_cancelbutton").css('display','none');
	$("#itcinput").css('display','none');
	$("#itcinputservices").css('display','none');
	$("#itccapitalgood").css('display','none');
	 $(".radiobtn").css('display','none');
	 $(".trradiobtn,.cessradiobtn").css('display','none');
	 var drcr1 = $("input[id='enabledrcr']:checked").val();
	$("#other_editbutton").css('display','block');
	$("#itc_input").html(itcinput);
	$('#itc_inputservices').html(itcinputservices);
	$('#itc_capitalgood').html(itccapitalgood);
	 if(drStatus == "true"){
		   $('#enable_drcr_field').text('Dr/Cr');
		}else{
			  $('#enable_drcr_field').text('By/To');	
		}
	   if(trStatus == "true"){
		   $('.tdate_invdate').text('Transaction Date');		
		}else{
			  $('.tdate_invdate').text('Invoice Date');
		} 
	   if(cessStatus == "true"){
			$('.cqty_ctaxAmt').text('Quantity');
		}else{
			$('.cqty_ctaxAmt').text('Taxable Value');
		}	
	   if(sStatus == "false" || sStatus == false){
			$('#enable_invsales_field').text("Yes");
			$('#enable_invsales_field').css("color","green");
		}else{
			$('#enable_invsales_field').text("No");
			$('#enable_invsales_field').css("color","red");
		}
		if(pStatus == "false"  || pStatus == false){
			$('#enable_invpurchase_field').text("Yes");
			$('#enable_invpurchase_field').css("color","green");
		}else{
			$('#enable_invpurchase_field').text("No");
			$('#enable_invpurchase_field').css("color","red");
		} 
		
		 if(lsStatus == "false" || lsStatus == false){
				$('#enable_invledgersales_field').text("Yes");
				$('#enable_invledgersales_field').css("color","green");
		}else{
				$('#enable_invledgersales_field').text("No");
				$('#enable_invledgersales_field').css("color","red");
		}
		if(lpStatus == "false"  || lpStatus == false){
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
		if(rpStatus == "false"  || rpStatus == false){
				$('#enable_invroundoffpurchase_field').text("Yes");
				$('#enable_invroundoffpurchase_field').css("color","green");
		}else{
				$('#enable_invroundoffpurchase_field').text("No");
				$('#enable_invroundoffpurchase_field').css("color","red");
		}
		if(tcsStatus == "false"  || tcsStatus == false){
			$('#enable_tcs_field').text("Yes");
			$('#enable_tcs_field').css("color","green");
	    }else{
			$('#enable_tcs_field').text("No");
			$('#enable_tcs_field').css("color","red");
	    }
		if(journalStatus == "false"  || journalStatus == false){
			$('#enable_journals_field').text("Yes");
			$('#enable_journals_field').css("color","green");
	    }else{
			$('#enable_journals_field').text("No");
			$('#enable_journals_field').css("color","red");
	    }
}
function cancelnotes(){
	$("#enablesign").hide();
	$("#sign").show();
	$("#dsg").show();
	$('.note_terms_divs').addClass('mb-2');
	$(".ccnotes").html(notes_old);
	$(".tms_cond").html(terms_old);
	$(".auth_signatory").html(signatory_old);
	$(".auth_designation").html(designation_old);
	$("#savebutton,#cancelbutton").css("display","none");
	document.getElementById("editbutton").style.display = "inline";
	document.getElementById("editbutton").disabled = false;
}
function cancelinvdate(){
	
	$(".inv_cutoffdate_sales").html(invdatesales_old);
	$(".inv_cutoffdate_purchases").html(invdatepurchase_old);
	$("#invdate_savebutton,#invdate_cancelbutton").css("display","none");
	document.getElementById("invdate_editbutton").style.display = "inline";
}
function cancelconfig(){
	document.getElementById("invoicetext").value = "";
	document.getElementById("invoiceqtytext").value = "";
	document.getElementById("invoiceratetext").value = "";
	document.getElementById("invoiceauthsigntext").value = "";
	document.getElementById("einvoiceheadertext").value = "";
	$('#labels').css("display","block");
	//$('.imgsize-wrap-thumb').css('left','70%');
	$("#savebutton1,#cancelbutton1").css("display","none");$('.checkDiv').css("display","block");$('#footernotes').css("display","none");
	$("#inv_text").html(invoice_label_text);$('#footnotecheckval').html(footer_check);
	$("#einv_header_text").html(einvoice_header_label_text);
	$("#inv_qty_text").html(qty_label_text);
	$("#inv_rate_text").html(rate_label_text);
	$("#inv_authsign_text").html(authsign_label_text);
	if(footer_check == 'Yes'){$('#footnotecheckval').css("color","green");$('#footnoteDiv').css("display","block");}else{$('#footnotecheckval').css("color","red");$('#footnoteDiv').css("display","none");}
	$('#footnoteval').html(footer_text);
	$('#enable_roundoff_field').html(roundoff);
	$('#enable_discount_field').html(discount);
	$('#enable_rate_field').html(rate);
	$('#enable_qty_field').html(quantity);
	$('#enable_state_field').html(statecode);
	$('#enable_pan_field').html(pannumber);
	$('#enable_placeofsupply_field').html(placeofsupply);
	document.getElementById("editbutton1").style.display = "inline";
	document.getElementById("editbutton1").disabled = false; 
}
function cancelnotes2(){
	$("#enablesign1").hide();
	$("#sign1").show();
	$("#sign2").show();
	$("#enablesign").hide();
	$("#sign").show();
	$("#dsg").show();
	$('.note_terms_divs').addClass('mb-2');
	$(".ccnotes").html($('.ccnotes').text());
	$(".tms_cond").html($('.tms_cond').text());
	$(".auth_signatory").html(signatory_old);
	$(".auth_designation").html(designation_old);
	var en = $('.enableSignatory').text();
	$(".enableSignatory,.designation1").css("display","inline-block");
	if(en == 'Yes'){
		$(".enable_auth_signatory").html('true');
	}else if(en == 'No'){
		$(".enable_auth_signatory").html('false');
	}
	$("#savebutton,#cancelbutton").css("display","none");
	document.getElementById("editbutton").style.display = "inline";
	document.getElementById("editbutton").disabled = false;
}
function cancelnotes1(){
	$("#enablesign").hide();
	$("#sign").show();
	$("#dsg").show();
	$('.note_terms_divs').addClass('mb-2');
	$("#customernotes").val('');
	$("#termsandconditions").val('');
	$("#authorisedsignatoryname").val('');
	$('#auth_designation').val('');
	document.getElementById("savebutton").disabled = true;
	document.getElementById("cancelbutton").disabled = true;
	$("#savebutton,#cancelbutton").css("display","block");
}
function editEinvoiceconfig(){
	$('#einvoice_cancelbutton,#einvoice_savebutton,#einvoice_authbutton').css("display","block");
	$('#einvoice_editbutton').css("display","none");
	var un = $("#euname").text();var pwd = $("#epwd").text();var status = $("#EinvConnStatus").text();
	document.getElementById("euname").innerHTML='<input type="text" class="form-control einvusername" id="einvusername" style="width:200px;height: 31px;" name="userName" placeholder="User name" value="'+eInvconfig+'" >';
	document.getElementById("epwd").innerHTML='<input type="password" class="form-control einvpassword" id="einvpassword"  style="width:200px;height: 31px;" name="password" placeholder="............" value="'+eInvpwd+'" >';
	$('#EinvConnStatus').html(status);
}
function saveEinvoiceconfig(){
	$("#einvoice_savebutton,#einvoice_cancelbutton,#einvoice_authbutton").css('display','none');
	$("#einvoice_editbutton").css('display','block');
	var uname = $("#einvusername").val();
	var pwd = $("#einvpassword").val();
	$.ajax({
		url: contextPath+"/saveEinvoiceconfig/"+clientId,
		data: {
			'username': uname,
			'password': pwd
		},
		type:"POST",
		contentType: 'application/x-www-form-urlencoded',
		success : function(response) {
			successNotification('Save successful!');
			$("#euname").html(uname);
			$("#epwd").html(pwd);
			 if(eInvstatus == "Active"){
				$('#EinvConnStatus').html("Active").css("color","green");;
			}else{
				$('#EinvConnStatus').html("InActive").css("color","red");;
			} 
			 einvusername = uname;einvpassword = pwd;
},
     error : function(e) {
		if(e.responseText) {
			errorNotification(e.responseText);
		}
	}
	});
}
function cancelEinvoiceconfig(){
	$('#einvoice_cancelbutton,#einvoice_savebutton,#einvoice_authbutton').css("display","none");
	$('#einvoice_editbutton').css("display","block");
	$('#euname').html(eInvconfig);
	$('#epwd').html(eInvpwd);
}
function authEinvconfig(){
	$('#EinvAuthError').html("");
	var uname = $("#einvusername").val();var pwd = $("#einvpassword").val();var status = $("#EinvConnStatus").text();
	$('#einvoice_authbutton').addClass("btn-loader");
	$.ajax({
		url: contextPath+"/authEInvoiceConfig/"+clientId,
		data: {
			'username': uname,
			'password': pwd
		},
		type:"POST",
		contentType: 'application/x-www-form-urlencoded',
		success : function(response) {
			if(response != ""){
				$('#EinvAuthError').html(response);
				$('#einvoice_authbutton,#einvoice_savebutton,#einvoice_cancelbutton').hide();
				$('#einvoice_editbutton').show();
			}else{
				$('#EinvAuthError').html("");
			}
			 if(eInvstatus == "Active"){
					$('#EinvConnStatus').html("Active").css("color","green");
				}else{
					$('#EinvConnStatus').html("InActive").css("color","red");
				} 
			 $("#euname").html(uname);$("#epwd").html(pwd);
			 $('#einvoice_authbutton').removeClass("btn-loader");
			 einvusername = uname;einvpassword = pwd;
},
     error : function(e) {
		if(e.responseText) {
			errorNotification(e.responseText);
			$('#einvoice_authbutton').removeClass("btn-loader");
		}
	}
	});
}
function capitalizeFirstLetter(string){
	 return string.charAt(0).toUpperCase() + string.slice(1);
}
function loadCustomFileds(id,clientid,month,year){
	var content='';
	$('#customFieldsBody').html('');
	var urlStr = _getContextPath()+'/getCustomFields/'+clientid;
	$.ajax({
		url: urlStr,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response && response.sales){
				for(var i=0;i<response.sales.length;i++){
					var dprint="";
					if(response.sales[i].displayInPrint == false){
						dprint = "False";
					}else if(response.sales[i].displayInPrint == true){
						dprint = "True";
					}
					content += '<tr id="row'+response.sales[i].typeId+'"><td>'+response.sales[i].customFieldName+'</td><td>'+capitalizeFirstLetter(response.sales[i].customFieldType)+'</td><td>Sales</td><td>'+dprint+'</td><td class="text-center"><a class="btn-edt" href="#" data-toggle="modal" data-target="#editCustomModal" onClick="showEditPopup(\''+response.userid+'\',\''+response.sales[i].customFieldName+'\',\''+response.sales[i].customFieldType+'\',\'Sales\',\''+response.sales[i].displayInPrint+'\',\''+response.sales[i].displayInFilters+'\',\''+response.sales[i].isMandatory+'\',\''+response.sales[i].typeData+'\')"><i class="fa fa-edit"></i> </a><a href="#" class="nottoedit" onClick="showCustomDeletePopup(\'Sales\',\''+response.userid+'\',\''+response.sales[i].typeId+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>';
				}
			}
			if(response && response.purchase){
				for(var i=0;i<response.purchase.length;i++){
					var dprint="";
					if(response.purchase[i].displayInPrint == false){
						dprint = "False";
					}else if(response.purchase[i].displayInPrint == true){
						dprint = "True";
					}
					content += '<tr><td>'+response.purchase[i].customFieldName+'</td><td>'+capitalizeFirstLetter(response.purchase[i].customFieldType)+'</td><td>Purchase</td><td>'+dprint+'</td><td class="text-center"><a class="btn-edt" href="#" data-toggle="modal" data-target="#editCustomModal" onClick="showEditPopup(\''+response.userid+'\',\''+response.purchase[i].customFieldName+'\',\''+response.purchase[i].customFieldType+'\',\'Purchase\',\''+response.purchase[i].displayInPrint+'\',\''+response.purchase[i].displayInFilters+'\',\''+response.purchase[i].isMandatory+'\',\''+response.purchase[i].typeData+'\')"><i class="fa fa-edit"></i> </a><a href="#" class="nottoedit" onClick="showCustomDeletePopup(\'Purchase\',\''+response.userid+'\',\''+response.purchase[i].typeId+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>';
				}
			}
			if(response && response.ewaybill){
				for(var i=0;i<response.ewaybill.length;i++){
					if(response.ewaybill[i].displayInPrint == false){
						dprint = "False";
					}else if(response.ewaybill[i].displayInPrint == true){
						dprint = "True";
					}
					content += '<tr><td>'+response.ewaybill[i].customFieldName+'</td><td>'+capitalizeFirstLetter(response.ewaybill[i].customFieldType)+'</td><td>Ewaybill</td><td>'+dprint+'</td><td class="text-center"><a class="btn-edt" href="#" data-toggle="modal" data-target="#editCustomModal" onClick="showEditPopup(\''+response.userid+'\',\''+response.ewaybill[i].customFieldName+'\',\''+response.ewaybill[i].customFieldType+'\',\'Ewaybill\',\''+response.ewaybill[i].displayInPrint+'\',\''+response.ewaybill[i].displayInFilters+'\',\''+response.ewaybill[i].isMandatory+'\',\''+response.ewaybill[i].typeData+'\')"><i class="fa fa-edit"></i> </a><a href="#" class="nottoedit" onClick="showCustomDeletePopup(\'Ewaybill\',\''+response.userid+'\',\''+response.ewaybill[i].typeId+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>';
				}
			}
			if(response && response.einvoice){
				for(var i=0;i<response.einvoice.length;i++){
					if(response.einvoice[i].displayInPrint == false){
						dprint = "False";
					}else if(response.einvoice[i].displayInPrint == true){
						dprint = "True";
					}
					content += '<tr><td>'+response.einvoice[i].customFieldName+'</td><td>'+capitalizeFirstLetter(response.einvoice[i].customFieldType)+'</td><td>Einvoice</td><td>'+dprint+'</td><td class="text-center"><a class="btn-edt" href="#" data-toggle="modal" data-target="#editCustomModal" onClick="showEditPopup(\''+response.userid+'\',\''+response.einvoice[i].customFieldName+'\',\''+response.einvoice[i].customFieldType+'\',\'E-invoice\',\''+response.einvoice[i].displayInPrint+'\',\''+response.einvoice[i].displayInFilters+'\',\''+response.einvoice[i].isMandatory+'\',\''+response.einvoice[i].typeData+'\')"><i class="fa fa-edit"></i> </a><a href="#" class="nottoedit" onClick="showCustomDeletePopup(\'Einvoice\',\''+response.userid+'\',\''+response.einvoice[i].typeId+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>';
				}
			}
			if(response && response.items){
				for(var i=0;i<response.items.length;i++){
					if(response.items[i].displayInPrint == false){
						dprint = "False";
					}else if(response.items[i].displayInPrint == true){
						dprint = "True";
					}
					content += '<tr><td>'+response.items[i].customFieldName+'</td><td>'+capitalizeFirstLetter(response.items[i].customFieldType)+'</td><td>Items</td><td>'+dprint+'</td><td class="text-center"><a class="btn-edt" href="#" data-toggle="modal" data-target="#editCustomModal" onClick="showEditPopup(\''+response.userid+'\',\''+response.items[i].customFieldName+'\',\''+response.items[i].customFieldType+'\',\'Items\',\''+response.items[i].displayInPrint+'\',\''+response.items[i].displayInFilters+'\',\''+response.items[i].isMandatory+'\',\''+response.items[i].typeData+'\')"><i class="fa fa-edit"></i> </a><a href="#" class="nottoedit" onClick="showCustomDeletePopup(\'Einvoice\',\''+response.userid+'\',\''+response.items[i].typeId+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>';
				}
			}
		}
	});
	if(customTable) {
			customTable.destroy();
		}
		$('#customFieldsBody').html(content);
		customTable=$('.cust_table').DataTable({
			dom: '<"toolbar"f>Blfrtip<"clear">', 	
			"pageLength": 10,
			"paging": true,
			"searching": true,
			"lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
			"responsive": true,
			"ordering": true,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search..."
			}
		});
		$(".customFieldTable div.toolbar").html('<a href="#" class="btn btn-blue-dark" data-toggle="modal" data-target="#addCustomFieldModal" onclick="populateElement()">Add</a> ');
}

function showEditPopup(userid,fieldname,fieldType,type,displayinprint,displayinFilters,ismandatory,typedata){
	$('#addCustomFieldModal').modal('show');
	$('.customFieldTxt').html('EDIT CUSTOM FIELD');
	var dat=JSON.stringify(typedata);
	var input = dat.split(',');
		$('#cFieldName').val(fieldname);
		$('#inputType').val(fieldType);
		$('#customField_Name').val(fieldname);
		$('#customField_Type').val(fieldType);
		$('#fieldIn').val(type);
		if(displayinprint == false || displayinprint == 'false'){
			$('#print_check').prop("checked",false);
		}else{
			$('#print_check').prop("checked",true);
		}
		if(ismandatory == false || ismandatory == 'false'){
			$('#mandatory_check').prop("checked",false);
		}else{
			$('#mandatory_check').prop("checked",true);
    }
		if(displayinFilters == false || displayinFilters == 'false'){
			$('#filters_check').prop("checked",false);
		}else{
			$('#filters_check').prop("checked",true);
		}
		if(fieldType == 'list'){
			$('.field_label').html('List Name')
			$('#add_fieldtext').html('Add List Item');
		}else if(fieldType == 'radio'){
			$('.field_label').html('Radio Button Name')
			$('#add_fieldtext').html('Add Radio Button');
		}else if(fieldType == 'checkB'){
			$('.field_label').html('CheckBox Name')
			$('#add_fieldtext').html('Add Checkbox');
		}
		no=1;
		if(no <= input.length) {
			for(var i=0;i<input.length;i++) {
				if(input[i]){
					$('#customrow'+i).remove();
					addCustom();
				}
			}
		}
	for(var i=1;i<=input.length;i++){
		if(fieldType == 'list' || fieldType == 'radio' || fieldType == 'checkB'){
			$('.Ftable,.newField').removeClass('d-none');	
		}
		$('#val'+i).val(input[i-1].replace(/^"|"$/g, ''));
	}
	$('#customId').val(userid);
}
function checkCustomFields(){
	var fname = $('#cFieldName').val();
	var ftype = $('#inputType').val();
	var type = $('#fieldIn').val();
	var urlStr = _getContextPath()+'/checkCustomFields/'+clientId+"?fieldname="+fname+"&fieldtype="+ftype+"&type="+type;
	$.ajax({
		url: urlStr,
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(status) {
			if(status){
				isExist = true;
				$('#dup_message').html("Duplicate Custom Fields, Please Add Different One");
			}else{
				isExist = false;
				$('#dup_message').html("");
			}
		},error : function(status) {
		}
	});
}
function closeCustomModal(){
	valuesArray = new Array();
	$('.customFieldTxt').html('ADD CUSTOM FIELD');
	$('#custErrorMessage').html("");
	$('#customId,#customField_Name,#customField_Type,#cFieldName').val("");
	$('.cfieldType').val("input");
	$('.fieldIn').val("Sales");
	$('#mandatory_check,#print_check').prop("checked",false);
	$('.Ftable,.newField').addClass('d-none');	
	//$(".Ftable tbody tr").remove();
	$('.Ftable tbody').find("tr").remove();
}
function customFieldssubmit(){
	var name = $('#cFieldName').val();
	var type = $('#inputType').val();
	var displayType = $('#fieldIn').val();
	var rtype = $('#fieldIn').val();
	var print = $('#print_check').is(':checked');
	var mandatory = $('#mandatory_check').is(':checked');
	//var duplicates = $('#duplicates_check').is(':checked');
	var filters = $('#filters_check').is(':checked');
	var oldCustomid = $('#customId').val();
	var oldName = $('#customField_Name').val();
	if(oldName.includes("&")){
		oldName = oldName.replace("&","-mgst-");
	}
	var oldtype = $('#customField_Type').val();
	var customdetails = new Object();
	var Filter_legth = $('.add-filed-table tbody tr').length;
	for(var i=1;i<=Filter_legth;i++){
		var values = $('#val'+i).val();
		if(values != null && values != ""){
			valuesArray.push(values);
		}
	}
		$('#custErrorMessage').html("");
		customdetails.customFieldName=name;
		customdetails.customFieldType=type;
		customdetails.displayInPrint=print;
		customdetails.isMandatory=mandatory;
		//customdetails.isDuplicatesAllow=duplicates;
		customdetails.displayInFilters=filters;
		customdetails.typeData=valuesArray;
		if(isExist){
		}else{
			$('#custom_Save').addClass('btn-loader');
			$.ajax({
				type: "POST",
				url: contextPath+"/savecustomFields"+urlSuffixs+"?displayType="+displayType+"&oldid="+oldCustomid+"&oldname="+oldName+"&oldtype="+oldtype,
				async: false,
				cache: false,
				data: JSON.stringify(customdetails),
				contentType: 'application/json',
				success : function(response){
					if(response != ""){
						$('#custErrorMessage').html(response);
					}else{
						window.location.href = contextPath+'/cp_upload'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=customfields'; 
					}
					$('#custom_Save').removeClass('btn-loader');
				},error : function(response) {
					$('#custom_Save').removeClass('btn-loader');
				}
			});
			
		}
	
}
function showCustomDeletePopup(type,id,typeId){
	$('#deleteModal').modal('show');
	$('#btnDelete_custom').attr('onclick', "deleteCustom('"+id+"','"+typeId+"', '"+clientId+"', '"+type+"')");
}
function deleteCustom(id,typeId,clientid,type){
	$.ajax({
		url: contextPath+"/deleteCustomField/"+id+"/"+typeId+"/"+clientid+"/"+type,
		success : function(response) {
			//table.row( $('#row'+typeId)).remove().draw();
			window.location.href = contextPath+'/cp_upload'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=customfields'; 
		}
	});
}
function valuesChange(rowno){
	var val = $('#val'+rowno).val();
}
	function editinvConfig(){
		$('#discExempt_savebutton,#discExempt_cancelbutton').css('display','block');
		$('#discExempt_editbutton').css('display','none');
		var invDisc = $('#inv_disc_config').text();
		var invPurDisc = $('#inv_purdisc_config').text();
		var invSalesCess= $('#inv_salesCess_config').text();
		var invPurCess = $('#inv_purCess_config').text();
		var einvCess= $('#inv_einvCess_config').text();
		var einvDisc = $('#inv_einvdisc_config').text();
		var invExempted = $('#inv_exempted_config').text();
		var invLedgerName = $('#inv_ledger_config').text();
		var invPurLedgerName = $('#inv_purledger_config').text();
		 if(invDisc == "Yes" || invDisc == ""){
			 document.getElementById("inv_disc_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableDiscount" type="checkbox" id="enableDiscount" checked><label for="enableDiscount"><span class="ui"></span></label></div></div>';
		 }else if(invDisc == "No"){
			 document.getElementById("inv_disc_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableDiscount" type="checkbox" id="enableDiscount"><label for="enableDiscount"><span class="ui"></span></label></div></div>'; 
		 }
		 if(invPurDisc == "Yes" || invPurDisc == ""){
			 document.getElementById("inv_purdisc_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablePurDiscount" type="checkbox" id="enablePurDiscount" checked><label for="enablePurDiscount"><span class="ui"></span></label></div></div>';
		 }else if(invPurDisc == "No"){
			 document.getElementById("inv_purdisc_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablePurDiscount" type="checkbox" id="enablePurDiscount"><label for="enablePurDiscount"><span class="ui"></span></label></div></div>'; 
		 }
		 if(einvDisc == "Yes" || einvDisc == ""){
			 document.getElementById("inv_einvdisc_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableEinvDiscount" type="checkbox" id="enableEinvDiscount" checked><label for="enableEinvDiscount"><span class="ui"></span></label></div></div>';
		 }else if(einvDisc == "No"){
			 document.getElementById("inv_einvdisc_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableEinvDiscount" type="checkbox" id="enableEinvDiscount"><label for="enableEinvDiscount"><span class="ui"></span></label></div></div>'; 
		 }
		 if(invSalesCess == "Yes" || invSalesCess == ""){
			 document.getElementById("inv_salesCess_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableSalesCess" type="checkbox" id="enableSalesCess" checked><label for="enableSalesCess"><span class="ui"></span></label></div></div>';
		 }else if(invSalesCess == "No"){
			 document.getElementById("inv_salesCess_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableSalesCess" type="checkbox" id="enableSalesCess"><label for="enableSalesCess"><span class="ui"></span></label></div></div>'; 
		 }
		 if(invPurCess == "Yes" || invPurCess == ""){
			 document.getElementById("inv_purCess_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablePurCess" type="checkbox" id="enablePurCess" checked><label for="enablePurCess"><span class="ui"></span></label></div></div>';
		 }else if(invPurCess == "No"){
			 document.getElementById("inv_purCess_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablePurCess" type="checkbox" id="enablePurCess"><label for="enablePurCess"><span class="ui"></span></label></div></div>'; 
		 }
		 if(einvCess == "Yes" || einvCess == ""){
			 document.getElementById("inv_einvCess_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableEinvCess" type="checkbox" id="enableEinvCess" checked><label for="enableEinvCess"><span class="ui"></span></label></div></div>';
		 }else if(einvCess == "No"){
			 document.getElementById("inv_einvCess_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableEinvCess" type="checkbox" id="enableEinvCess"><label for="enableEinvCess"><span class="ui"></span></label></div></div>'; 
		 }
		if(invExempted == "Yes"){
			document.getElementById("inv_exempted_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableExempted" type="checkbox" id="enableExempted" checked><label for="enableExempted"><span class="ui"></span></label></div></div>';
		}else if(invExempted == "" || invExempted == "No"){
			document.getElementById("inv_exempted_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableExempted" type="checkbox" id="enableExempted"><label for="enableExempted"><span class="ui"></span></label></div></div>';	
		}
		if(invLedgerName == "Yes" || invLedgerName == ""){
			document.getElementById("inv_ledger_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableLedgerName" type="checkbox" id="enableLedgerName" checked><label for="enableLedgerName"><span class="ui"></span></label></div></div>';
		}else if(invLedgerName == "No"){
			document.getElementById("inv_ledger_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enableLedgerName" type="checkbox" id="enableLedgerName"><label for="enableLedgerName"><span class="ui"></span></label></div></div>';	
		}
		if(invPurLedgerName == "Yes" || invPurLedgerName == ""){
			document.getElementById("inv_purledger_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablePurLedgerName" type="checkbox" id="enablePurLedgerName" checked><label for="enablePurLedgerName"><span class="ui"></span></label></div></div>';
		}else if(invPurLedgerName == "No"){
			document.getElementById("inv_purledger_config").innerHTML='<div class="form-group col-md-6 col-sm-12 meterialform bs-fancy-checks labels"><div class="form-check" style="margin:0px!important"><input name="enablePurLedgerName" type="checkbox" id="enablePurLedgerName"><label for="enablePurLedgerName"><span class="ui"></span></label></div></div>';	
		}
	}
	function saveinvConfig() {
		var allowDiscount = $('#enableDiscount').is(':checked');
		var allowPurDiscount = $('#enablePurDiscount').is(':checked');
		var allowSalesCess = $('#enableSalesCess').is(':checked');
		var allowEinvCess = $('#enableEinvCess').is(':checked');
		var allowPurCess = $('#enablePurCess').is(':checked');
		var allowEinvDiscount = $('#enableEinvDiscount').is(':checked');
		var allowExempted =	$('#enableExempted').is(':checked');
		var allowLedgerName =	$('#enableLedgerName').is(':checked');
		var allowPurLedgerName =	$('#enablePurLedgerName').is(':checked');
		$.ajax({
			url: contextPath+"/saveInvConfiurations/"+clientId,
			data: {
				'allowDiscount': allowDiscount,
				'allowPurDiscount': allowPurDiscount,
				'allowEinvDiscount': allowEinvDiscount,
				'allowSalesCess': allowSalesCess,
				'allowPurCess': allowPurCess,
				'allowEinvoiceCess': allowEinvCess,
				'allowExempted': allowExempted,
				'allowLedgerName':allowLedgerName,
				'allowPurLedgerName':allowPurLedgerName
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				if(allowDiscount == true || allowDiscount == "true"){
					$('#inv_disc_config').text('Yes');
					$('#inv_disc_config').css("color","green");
				}else{
					$('#inv_disc_config').text('No');
					$('#inv_disc_config').css("color","red");
				}
				if(allowPurDiscount == true || allowPurDiscount == "true"){
					$('#inv_purdisc_config').text('Yes');
					$('#inv_purdisc_config').css("color","green");
				}else{
					$('#inv_purdisc_config').text('No');
					$('#inv_purdisc_config').css("color","red");
				}
				if(allowEinvDiscount == true || allowEinvDiscount == "true"){
					$('#inv_einvdisc_config').text('Yes');
					$('#inv_einvdisc_config').css("color","green");
				}else{
					$('#inv_einvdisc_config').text('No');
					$('#inv_einvdisc_config').css("color","red");
				}
				if(allowSalesCess == true || allowSalesCess == "true"){
					$('#inv_salesCess_config').text('Yes');
					$('#inv_salesCess_config').css("color","green");
				}else{
					$('#inv_salesCess_config').text('No');
					$('#inv_salesCess_config').css("color","red");
				}
				if(allowPurCess == true || allowPurCess == "true"){
					$('#inv_purCess_config').text('Yes');
					$('#inv_purCess_config').css("color","green");
				}else{
					$('#inv_purCess_config').text('No');
					$('#inv_purCess_config').css("color","red");
				}
				if(allowEinvCess == true || allowEinvCess == "true"){
					$('#inv_einvCess_config').text('Yes');
					$('#inv_einvCess_config').css("color","green");
				}else{
					$('#inv_einvCess_config').text('No');
					$('#inv_einvCess_config').css("color","red");
				}
				if(allowExempted == true || allowExempted == "true"){
					$('#inv_exempted_config').text('Yes');
					$('#inv_exempted_config').css("color","green");
				}else{
					$('#inv_exempted_config').text('No');
					$('#inv_exempted_config').css("color","red");
				}
				if(allowLedgerName == true || allowLedgerName == "true"){
					$('#inv_ledger_config').text('Yes');
					$('#inv_ledger_config').css("color","green");
				}else{
					$('#inv_ledger_config').text('No');
					$('#inv_ledger_config').css("color","red");
				}
				if(allowPurLedgerName == true || allowPurLedgerName == "true"){
					$('#inv_purledger_config').text('Yes');
					$('#inv_purledger_config').css("color","green");
				}else{
					$('#inv_purledger_config').text('No');
					$('#inv_purledger_config').css("color","red");
				}
				window.location.href = _getContextPath()+'/cp_upload'+paymenturlSuffix+'/'+month+'/'+year+'?type=';
			},
			error : function(e) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	function cancelinvConfig(){
		$(".inv_disc_config").html(invdiscount_old);
		$(".inv_einvdisc_config").html(einvdiscount_old);
		$(".inv_purdisc_config").html(invpurdiscount_old);
		$(".inv_exempted_config").html(invexempted_old);
		$(".inv_salesCess_config").html(invcess_old);
		$(".inv_purCess_config").html(invpurcess_old);
		$(".inv_einvCess_config").html(einvcess_old);
		$(".inv_ledger_config").html(invledger_old);
		$(".inv_purledger_config").html(invpurledger_old);
		$("#discExempt_savebutton,#discExempt_cancelbutton").css("display","none");
		document.getElementById("discExempt_editbutton").style.display = "inline";
	}
	
	function editEmailConfig(){
		$('#emailConfig_savebutton,#emailConfig_cancelbutton,#email_signdetails').css('display','block');
		$('#emailConfig_editbutton').css('display','none');
		var cn = $("#configure_clientdetails").html();
		//document.getElementById("email_config_text").innerHTML='<textarea class="form-control email_config" id="email_config" name="emailconfig" value="" style="width:96%; height:110px;border:1px solid lightgray;">'+cn+'</textarea>';
		$("#configure_clientdetails").css("display","none");
		$('#email_signdetails').html(cn.replaceAll("<br>", '\r\n'));
		emailSignature = cn;
	}
	function cancelEmailConfig(){
		$("#emailConfig_savebutton,#emailConfig_cancelbutton").css("display","none");
		document.getElementById("emailConfig_editbutton").style.display = "inline";
		$("#configure_clientdetails").css("display","table-caption");
		$("#email_signdetails").css("display","none");
		$('#configure_clientdetails').html(emailSignature);
	}
	function saveEmailConfig(){
		var cs = document.getElementById('email_signdetails').value;
		$.ajax({
			url: contextPath+"/saveClientSignature/"+clientId,
			data: {
				'clientSignature': cs
			},
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				if(clientSignature != ""){
					$('#configure_clientdetails').html(clientSignature);
				}
				window.location.href = _getContextPath()+'/cp_upload'+paymenturlSuffix+'/'+month+'/'+year+'?type=';
			}
		});
	}