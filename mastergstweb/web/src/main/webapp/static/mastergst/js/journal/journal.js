$(function () {
	$('#vocherform').submit(function(e) {
		$('#vocherform input[required="required"]').each(function(){
			 var abc = $(this).val();
			var ele=$('.form-group').is('.has-error');
		    if (ele) { 
		    	err = 1;
		    	
		    }else{
		    	if(abc == ''){
		    		err=1;
		    		
		    	}else{
		    	err=0;
		    	
		    	}
		    }
		 });
		
		var dramt = $('#voucherdramounthidden').val();
		var cramt = $('#vouchercramounthidden').val();
		if(dramt == '' && cramt == ''){
			err=1;
			$('#vouchererrormsg').text('Please Enter Items');
		}else if(dramt == 0 || cramt == 0){
			err=1;
			$('#vouchererrormsg').text('Debit Amount & Credit Amount is Not Equal');
		}else if(dramt != cramt){
			err=1;
			$('#vouchererrormsg').text('Debit Amount & Credit Amount is Not Equal');
		}else{
			err=0;
			$('#vouchererrormsg').text('');
		}
		 if (err != 0) {
			
		    return false;
		  }
	});

	$('#contraform').submit(function(e) {
		$('#contraform input[required="required"]').each(function(){
			 var abc = $(this).val();
			var ele=$('.form-group').is('.has-error');
		    if (ele) { 
		    	err = 1;
		    	
		    }else{
		    	if(abc == ''){
		    		err=1;
		    		
		    	}else{
		    	err=0;
		    	
		    	}
		    }
		 });
		
		var dramt = $('#contradramounthidden').val();
		var cramt = $('#contracramounthidden').val();
		if(dramt == '' && cramt == ''){
			err=1;
			$('#contraerrormsg').text('Please Enter Items');
		}else if(dramt == 0 || cramt == 0){
			err=1;
			$('#contraerrormsg').text('Debit Amount & Credit Amount is Not Equal');
		}else if(dramt != cramt){
			err=1;
			$('#contraerrormsg').text('Debit Amount & Credit Amount is Not Equal');
		}else{
			err=0;
			$('#vouchererrormsg').text('');
		}
		 if (err != 0) {
			
		    return false;
		  }
	});

	$('#dateofvocher,#dateofcontra').datetimepicker({
		timepicker: false,
		format: 'd-m-Y',
		scrollMonth: true
	});
	var ledgeroptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/ledgerlist/"+clientId+"?query="+ phrase + "&format=json";
			},
			getValue: "ledgerName",
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $("#voucherledger_text1").getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($("#eac-container-voucherledger_text1 ul").children().length == 0) {
						$("#addvoucherlegername").show();
					} else {
						$("#addvoucherlegername").hide();
					}
				},
				maxNumberOfElements: 10
			}
		};
	var ledgeroptions1 = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/ledgerlist/"+clientId+"?query="+ phrase + "&format=json";
			},
			getValue: "ledgerName",
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $("#voucherledger_text1").getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($("#eac-container-voucherledger_text2 ul").children().length == 0) {
						$("#addvoucherlegername2").show();
					} else {
						$("#addvoucherlegername2").hide();
					}
				},
				maxNumberOfElements: 10
			}
		};
	var contraledgeroptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/contraledgerlist/"+clientId+"?query="+ phrase + "&format=json";
			},
			getValue: "ledgerName",
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $("#contraledger_text1").getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($("#eac-container-contraledger_text1 ul").children().length == 0) {
						$("#addcontralegername1").show();
					} else {
						$("#addcontralegername1").hide();
					}
				},
				maxNumberOfElements: 10
			}
		};
	var contraledgeroptions1 = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/contraledgerlist/"+clientId+"?query="+ phrase + "&format=json";
			},
			getValue: "ledgerName",
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $("#contraledger_text2").getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($("#eac-container-contraledger_text2 ul").children().length == 0) {
						$("#addcontralegername2").show();
					} else {
						$("#addcontralegername2").hide();
					}
				},
				maxNumberOfElements: 10
			}
		};
	$('#voucherledger_text1').easyAutocomplete(ledgeroptions);
	$('#voucherledger_text2').easyAutocomplete(ledgeroptions1);
	$('#voucherledger_text1').parent().parent().mouseleave(function() {
		$("#addvoucherlegername").hide();
		
	});
	$('#voucherledger_text2').parent().parent().mouseleave(function() {
		$("#addvoucherlegername2").hide();
		
	});
	$('#contraledger_text1').easyAutocomplete(contraledgeroptions);
	$('#contraledger_text1').parent().parent().mouseleave(function() {
		$("#addcontralegername1").hide();
		
	});
	$('#contraledger_text2').easyAutocomplete(contraledgeroptions1);
	$('#contraledger_text2').parent().parent().mouseleave(function() {
		$("#addcontralegername2").hide();
		
	});
});
function addvcInvoices(type){
	if(type == 'Voucher'){
		$('#vocherform').submit();
	}else{
		$('#contraform').submit();
	}
}
function updateVoucherDetails(id, actiontype){
	//actiontype means you submit form journalsdetaions.jps or ledgerreport.jsp
		$('#voucherModal').modal("show");
		if(id) {
			voucherList.forEach(function(voucher) {
				if(voucher.id == id) {
					$('#voucherid').remove();
					$('#vsortable_table4 tbody').find("tr:gt(0)").remove();
					$("form[name='vocherform']").append('<input type="hidden" id="voucherid" name="id" value="'+voucher.id+'">');
					$('#voucherNumbers55').val(voucher.voucherNumber);
					$('#dateofvocher').val(voucher.voucherDate);
					if(voucher.vouchertype != null){
						$('#voucherNumbersType').val(voucher.vouchertype);
					}
					$('#voucher_notes').val(voucher.vouchernotes);
					$('#voucherdramounthidden').val(parseFloat(voucher.totdramount).toFixed(2));
					$('#vouchercramounthidden').val(parseFloat(voucher.totcramount).toFixed(2));
					$('#voucherdrtot').html(parseFloat(voucher.totdramount).toFixed(2));
					$('#vouchercrtot').html(parseFloat(voucher.totcramount).toFixed(2));
					if(voucher.voucheritems != null && voucher.voucheritems.length > 0){
							for(var i=1;i<voucher.voucheritems.length;i++){
								addAccountingrow('voucher');
							}
							$.each(voucher.voucheritems, function(index, voucherItemData) {
								index++;
								$('#voucher_mode'+index).val(voucherItemData.modeOfVoucher);
								$('.ledger_text'+index).val(voucherItemData.ledger);
								if(voucherItemData.dramount == '' || voucherItemData.dramount == null){
									$('#voucherdramt_text'+index).val("").attr("readonly","readonly");
									$('#voucherdramt_text'+index).removeAttr("required");
								}else{
									$('#voucherdramt_text'+index).val(voucherItemData.dramount);
								}
								if(voucherItemData.cramount == '' || voucherItemData.cramount == null){
									$('#vouchercramt_text'+index).val("").attr("readonly","readonly");
									$('#vouchercramt_text'+index).removeAttr("required");
								}else{
									$('#vouchercramt_text'+index).val(voucherItemData.cramount);
								}
							});
							
					}
				}
			});
		}
		$('#voucherActiontype').val(actiontype);
		$('#addVoucherbtn').removeClass('disabled');
	}

function updateContraDetails(id, actiontype){
	//actiontype means you submit form journalsdetaions.jps or ledgerreport.jsp 
	$('#ContraModal').modal("show");
	//$('#vocherform').removeAttr('action');
	//$('#vocherform').attr("action",contextPath+"/record_vouchers"+urlSuffixs+"/"+pmntsSuffix);
	if(id) {
		contraList.forEach(function(contra) {
			if(contra.id == id) {
				$('#contraid').remove();
				$('#vsortable_table5 tbody').find("tr:gt(0)").remove();
				$("form[name='contraform']").append('<input type="hidden" id="contraid" name="id" value="'+contra.id+'">');
				$('#contraNumbers').val(contra.contraNumber);
				$('#dateofcontra').val(contra.contraDate);
				$('#contra_notes').val(contra.contranotes);
				$('#contradramounthidden').val(parseFloat(contra.totdramount).toFixed(2));
				$('#contracramounthidden').val(parseFloat(contra.totcramount).toFixed(2));
				$('#contradrtot').html(parseFloat(contra.totdramount).toFixed(2));
				$('#contracrtot').html(parseFloat(contra.totcramount).toFixed(2));
				if(contra.contraitems != null && contra.contraitems.length > 0){
						for(var i=1;i<contra.contraitems.length;i++){
							addAccountingrow('contra');
						}
						$.each(contra.contraitems, function(index, contraItemData) {
							index++;
							$('#contra_mode'+index).val(contraItemData.modeOfVoucher);
							$('#contraledger_text'+index).val(contraItemData.ledger);
							if(contraItemData.dramount == '' || contraItemData.dramount == null){
								$('#contradramt_text'+index).val("").attr("readonly","readonly");
								$('#contradramt_text'+index).removeAttr("required");
							}else{
								$('#contradramt_text'+index).val(contraItemData.dramount);
							}
							if(contraItemData.cramount == '' || contraItemData.cramount == null){
								$('#contracramt_text'+index).val("").attr("readonly","readonly");
								$('#contracramt_text'+index).removeAttr("required");
							}else{
								$('#contracramt_text'+index).val(contraItemData.cramount);
							}
						});
						
				}
			}
		});
	}
	$('#contraActiontype').val(actiontype);
	$('#addContrabtn').removeClass('disabled');
}

function showJournalPaymentDeletePopup(paymentid,returntype,clientid,vouchernumber,invoicenumber, reporttype) {
	$('#deleteModal').modal('show');
	//$('#delPopupDetails').html(name);
	if(returntype == 'GSTR1' || returntype == 'SalesRegister' ){
		$('#delheader,#btnDelete').html("Delete Receipt");
		$('#delitem').html("Receipt");
	}else{
		$('#delheader,#btnDelete').html("Delete payment");
		$('#delitem').html("payment");
	}
	if(/^[/]*$/.test(vouchernumber) == false) {
		vouchernumber = vouchernumber.replaceAll("/","invNumCheck");
	}
	if(/^[/]*$/.test(invoicenumber) == false) {
		invoicenumber = invoicenumber.replaceAll("/","invNumCheck");
	}
	$('#btnDelete').attr('onclick', "deleteJournalPayment('"+paymentid+"','"+returntype+"','"+clientid+"','"+vouchernumber+"','"+invoicenumber+"','"+reporttype+"')");
}
function deleteJournalPayment(paymentid,returntype,clientid,vouchernumber,invoicenumber, reporttype) {
	
	$.ajax({
		url: contextPath+"/delpayment/"+paymentid+"/"+returntype+"/"+clientid+"/"+vouchernumber+"/"+invoicenumber,
		success : function(response) {
			if(reporttype == 'ledgerreport'){
				$('#successModal').modal('show');
				var journaltype;
				if(returntype == 'GSTR1'){
					journaltype ='Payment Receipt'
				}else{
					journaltype ='Payment Voucher'
				}
				$('#h6data').html(journaltype +"&nbsp; Deleted Successfully...!");
				$('#pdata').html(journaltype+" Number :<strong>"+vouchernumber+"</strong>");
			}else{
				window.location.href = contextPath+'/journaldetails'+paymenturlSuffix+'/'+Paymenturlprefix+'?type=';
			}
		}
	});
}

function addAccountingrow(modal){
	if(modal == 'voucher'){
		vrowCount = $('#voucherbody tr').length;	
	}else{
		vrowCount = $('#contrabody tr').length;
	}
	vrowCount = vrowCount+1;
	nameleng = vrowCount-1;
	var addLedgerModal =  "addLedgerModal";
	$('#'+modal+'body').append('<tr><td id="sno_row2" align="center">'+vrowCount+'</td><td id="" class="form-group" style="border: none;margin-bottom: 0px;"><select id="'+modal+'_mode'+vrowCount+'" onChange="drcrType('+vrowCount+',\''+modal+'\')" name="'+modal+'items['+nameleng+'].modeOfVoucher" class="form-control mov_text'+vrowCount+'" required="required"><option value="">-Select-</option><option value="Dr">Dr</option><option value="Cr">Cr</option></select></td><td class="form-group"><input type="text" class="form-control ledger_text'+vrowCount+'" id="'+modal+'ledger_text'+vrowCount+'" required="required" placeholder="Ledger" name="'+modal+'items['+nameleng+'].ledger"/><div id="add'+modal+'legername'+vrowCount+'" style="display:none"><div class="vledgerddbox"><p id="newledger">Please add new Ledger</p><input type="button" class="btn btn-sm btn-blue-dark" id="newledgerval" value="Add New Ledger" data-toggle="modal" onclick="addLedger(\''+vrowCount+'\',\''+modal+'\',\''+addLedgerModal+'\')" ></div></div></td><td id="'+modal+'dramttd" class="form-group"><input type="text" class="form-control amount_check '+modal+'dramount '+modal+'dramt_text'+vrowCount+'" id="'+modal+'dramt_text'+vrowCount+'" name="'+modal+'items['+nameleng+'].dramount" required="required" onKeyUp="findtotal('+vrowCount+',\''+modal+'\')" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td id="'+modal+'cramttd" class="form-group"><input type="text" class="form-control amount_check '+modal+'cramount '+modal+'cramt_text'+vrowCount+'" id="'+modal+'cramt_text'+vrowCount+'" onKeyUp="findtotal('+vrowCount+',\''+modal+'\')" name="'+modal+'items['+nameleng+'].cramount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td align="center" width="2%" style="text-align:center;"><a href="javascript:void(0)" id="delete_button'+vrowCount+'" class="item_delete" onclick="delete_Accrow('+vrowCount+',\''+modal+'\')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
	
	var ledgeroptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				if(modal == 'voucher'){
					return contextPath+"/ledgerlist/"+clientId+"?query="+ phrase + "&format=json";	
				}else{
					return contextPath+"/contraledgerlist/"+clientId+"?query="+ phrase + "&format=json";
				}
			},
			getValue: "ledgerName",
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $('#'+modal+'ledger_text'+vrowCount).getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($('#eac-container-'+modal+'ledger_text'+vrowCount+' ul').children().length == 0) {
						$('#add'+modal+'legername'+vrowCount).show();
					} else {
						$('#add'+modal+'legername'+vrowCount).hide();
					}
				},
				maxNumberOfElements: 10
			}
		};
	//$('#'+modal+'Ledger1').easyAutocomplete(ledgeroptions);
	$('#'+modal+'ledger_text'+vrowCount).easyAutocomplete(ledgeroptions);
	$('#'+modal+'ledger_text'+vrowCount).parent().parent().mouseleave(function() {
			$('#add'+modal+'legername'+vrowCount).hide();	
	});
	if(modal == 'voucher'){
		$('form[name="vocherform"]').validator('update');
	}else{
		$('form[name="contraform"]').validator('update');
	}
	
}
function updateVoucherDetailsAfterDelete_row(no,modal){
	var	totaldr=document.getElementById(""+modal+"drtot").innerHTML;
	var	tataldramt=document.getElementById(""+modal+"dramt_text"+no).value;
	var	tataldramt1=$("#"+modal+"dramt_text"+no).val();
		
	var	totalcr=document.getElementById(""+modal+"crtot").innerHTML;
	var	tatalcramt=document.getElementById(""+modal+"cramt_text"+no).value;
	var	tatalcramt1=$("#"+modal+"cramt_text"+no).val();
	
	if(tataldramt != '' && tataldramt>0){
		totaldr = totaldr.replace(/,/g , '');
		totaldr-=parseFloat(tataldramt).toFixed(2);
	}else if(tataldramt1>0){
		tataldramt1 = tataldramt1.replace(/,/g , '');
		totaldr = totaldr.replace(/,/g , '');
		totaldr-=parseFloat(tataldramt1);
	}
	if(/[,\-]/.test(totaldr)){
		totaldr = totaldr.replace(/,/g , '');
	}
	if(tatalcramt != '' && tatalcramt>0){
		totalcr = totalcr.replace(/,/g , '');
		totalcr-=parseFloat(tatalcramt).toFixed(2);
	}else if(tatalcramt1>0){
		tatalcramt1 = tatalcramt1.replace(/,/g , '');
		totalcr = totalcr.replace(/,/g , '');
		totalcr-=parseFloat(tatalcramt1);
	}
	if(/[,\-]/.test(totalcr)){
		totalcr = totalcr.replace(/,/g , '');
	}
	
		$('#'+modal+'drtot').html(parseFloat(totaldr).toFixed(2));
		$('#'+modal+'dramounthidden').val(parseFloat(totaldr).toFixed(2));
		
		$('#'+modal+'crtot').html(parseFloat(totalcr).toFixed(2));
		$('#'+modal+'cramounthidden').val(parseFloat(totalcr).toFixed(2));
	
	
}
function delete_Accrow(no,modal){
	var table4= "";
	var tbodyid = ""
	if(modal == 'voucher'){
		table4 = document.getElementById("vsortable_table4"); 
		tbodyid = "voucherbody";
	}else{
		table4 = document.getElementById("vsortable_table5");
		tbodyid = "contrabody";
	}
	
	updateVoucherDetailsAfterDelete_row(no,modal);
	if(no >= vrowCount){
		no=no-1;
	}
	table4.deleteRow(no+1);
	vrowCount--;
	$("#"+tbodyid+" tr").each(function(index) {
		 $(this).attr('id',index+1);
		 $(this).find("#sno_row2").html(index+1);
		 var rowno = (index+1).toString();
		 var rownoo = (index).toString();
		 $(this).find('input , select').each (function() {
				var inputname1 = $(this).attr('class');
				var inputname = $(this).attr('name');
				var inputid1 = $(this).attr('id');
				var abcd = $(this).attr('onkeyup');
				var change = $(this).attr('onchange');
				if(change != undefined){
					change = replaceAt(change,9,rowno);
			   	    $(this).attr('onchange',change);
				}
				if(abcd != undefined){
					abcd = replaceAt(abcd,10,rowno);
			   	    $(this).attr('onkeyup',abcd);
				}
				if(inputname1 != undefined){
					if(rowno<10){
						inputname1 = inputname1.slice(0, -1);
		   	    		}else{
		   	    			inputname1 = inputname1.slice(0, -2);
		   	    		}
					inputname1 = inputname1+rowno;
					$(this).attr('class',inputname1);
				}
				if(inputname != undefined){
					if(inputname.indexOf("voucheritems[") >= 0) {
						if(rownoo == '9'){
							inputname = inputname.replace('10',' ');
						}
						inputname = replaceAt(inputname,13,rownoo);
						$(this).attr('name',inputname);
						
					}
				}
				if(inputid1 != undefined){
					if(rowno<10){
						inputid1 = inputid1.slice(0, -1);
		   	    		}else{
		   	    			inputid1 = inputid1.slice(0, -2);
		   	    		}
					inputid1 = inputid1+rowno;
					$(this).attr('id',inputid1);
				}
			});
		 $(this).find('a').each (function() {
				var aname = $(this).attr('id');
				if(aname != undefined){
					if(rowno<10){
						aname = aname.slice(0, -1);
		   	    		}else{
		   	    			aname = aname.slice(0, -2);
		   	    		}
				aname = aname+rowno;
				$(this).attr('id',aname);
				}
				var det = $(this).attr('class');
				if(det != 'item_delete'){
				}else{
					var abcd = $(this).attr('onclick');
			   	    abcd = replaceAt(abcd,14,rowno);
			   	    $(this).attr('onclick',abcd);
				}
			});
			
	});
}
function findtotal(no,modal){
	var totdt = 0, totct = 0;
	var rowCount = $('#'+modal+'body tr').length;
	rowCount = rowCount +1;
	for(var i=1;i<rowCount;i++) {
		if(i == no) {
			var totaldt , totalct;
			totaldt = document.getElementById(''+modal+'dramt_text'+i);
			
			if(totaldt && totaldt.value) {
				totdt+=parseFloat(totaldt.value);
			}
			totalct = document.getElementById(''+modal+'cramt_text'+i);
			if(totalct && totalct.value) {
				totct+=parseFloat(totalct.value);
			}
		}else{
			totaldt = document.getElementById(''+modal+'dramt_text'+i);
			if(totaldt && totaldt.value) {
				totdt+=parseFloat(totaldt.value);
			}
			totalct = document.getElementById(''+modal+'cramt_text'+i);
			if(totalct && totalct.value) {
				totct+=parseFloat(totalct.value);
			}
		}
	}
		$('#'+modal+'drtot').html(parseFloat(totdt).toFixed(2));
		$('#'+modal+'crtot').html(parseFloat(totct).toFixed(2));
		$('#'+modal+'dramounthidden').val(parseFloat(totdt).toFixed(2));
		$('#'+modal+'cramounthidden').val(parseFloat(totct).toFixed(2));
}

function drcrType(no,modal){
	var	docType = $("#"+modal+"_mode"+no).val();
		if(docType == 'Dr'){
			$('#'+modal+'cramt_text'+no).prop('readonly',true);
			$('#'+modal+'cramt_text'+no).removeAttr('required').val('');
			$('#'+modal+'dramt_text'+no).prop('readonly',false);
		}else{
			$('#'+modal+'cramt_text'+no).prop('readonly',false);
			$('#'+modal+'dramt_text'+no).prop('readonly',true);
			$('#'+modal+'dramt_text'+no).removeAttr('required').val('');
		}
		findtotal(no,modal);
	}
function closevcmodal(){
	$('#voucherid').remove();
	$('#contraid').remove();
	$('#vsortable_table4 tbody').find("tr:gt(1)").remove();
	$('#csortable_table5 tbody').find("tr:gt(1)").remove();
	$('#vouchercramt_text1,#voucherdramt_text1,#contradramt_text1,#contracramt_text1').removeAttr("readonly");
	$('#voucherdramounthidden,#vouchercramounthidden,#contradramounthidden,#contracramounthidden').val("");
	$('#vouchererrormsg').text('');
	$('#voucherdrtot,#vouchercrtot,#contradrtot,#contracrtot').html("0.00");
	$('form[name="vocherform"]').trigger("reset");
	$('form[name="contraform"]').trigger("reset");
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
function formatDate(date) {
	if(date == null || typeof(date) === 'string' || date instanceof String) {
		return date;
	} else {
		var d = new Date(date),
			month = '' + (d.getMonth() + 1),
			day = '' + d.getDate(),
			year = d.getFullYear();

		if (month.length < 2) month = '0' + month;
		if (day.length < 2) day = '0' + day;

		return [day, month, year].join('-');
	}
}
$(document).on('show.bs.modal', '.modal', function (event) {
    var zIndex = 1040 + (10 * $('.modal:visible').length);
    $(this).css('z-index', zIndex);
    setTimeout(function() {
        $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
    }, 0);
});

function loadJournalInvTable(id, clientId, month, year,jtype){
	var pUrl = contextPath+'/getAddtionalJournalInvs/'+id+'/'+clientId+'/'+month+'/'+year+'?type='+jtype;
	if(journaltable){
		journaltable.clear();
		journaltable.destroy();
	}
	journaltable = $('#dbTable1').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
	    "columns": [
	        {
	        	name: 'second',
	            title: 'S.No',
	            width:'2%' 
	        },
	        {
	            title: 'Journal Details / Particulars',
	            width:'45%'
	        },
	        {
	            title: 'Debit'
	        },
	        {
	            title: 'Credit'
	        },
	        {
	        	name: 'second1',
	            title: 'Date',
	            width:'8%'
	        },
			{	
	        	name: 'second2',
	            title: 'Invoice No',
	            width:'10%'
	        },
			{	
	        	name: 'second3',
	            title: 'Action',
	            width:'5%'
	        }
	    ],
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
				 jdetails = resp.invoices.content;
				 loadJournalTotals(resp.invoicesAmount);
				 jd();
	        	 return jdd;
	         }
	     },
		"paging": true,
	    "rowsGroup": [
	        'second:name',
	        'second1:name',
	        'second2:name',
	        'second3:name'
	    ],
	    "order": [[4,'asc']],
	    "columnDefs": [
	    	{
				"targets":  [0, 1, 2, 3, 5, 6 ],
				"orderable": false
				}
           ],
	    "pageLength": '25',
		"responsive": true,
	    "drawCallback": function(){
	          $('.paginate_button', this.api().table().container())          
	             .on('click', function(){
	            	$('.abcd').parent().css('border-bottom','1px dashed lightgrey');
	       		  	$('.leftindent').parent().parent().css('line-height','0.5');
	       		  	$('.rightindent').parent().parent().css('line-height','0.5');
	       		  	$('.rightindents').parent().parent().css('line-height','1');
	       		  	$('.rtType').css('margin-top','10px');
	       		  	$('.srtType').css('margin-top','5px');
	       		  	$(".indformat").each(function(){
	       			    $(this).html($(this).html().replace(/,/g , ''));
	       			});
	       		  	OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
	             });
	          	$('.dataTables_filter input').addClass('searchclass');
	          	$('.abcd').parent().css('border-bottom','1px dashed lightgrey');
	          	$('.leftindent').parent().parent().css('line-height','0.5');
	          	$('.rightindent').parent().parent().css('line-height','0.5');
	          	$('.rightindents').parent().parent().css('line-height','1');
	          	$('.rtType').css('margin-top','10px');
	          	$('.srtType').css('margin-top','5px');
	          	$(".indformat").each(function(){
	          		$(this).html($(this).html().replace(/,/g , ''));
	          	});
	          	$(".indformats").each(function(){ 
				   $(this).html($(this).html().replace(/,/g , ''));
	          	});
	  			OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
	  			OSREC.CurrencyFormatter.formatAll({selector : '.indformats'});
				$('.hrefStatus_Cancelled').removeAttr("onclick");
	      		$('.hrefStatus_Deleted').removeAttr("onclick");
	      		$('.editStatus_Cancelled').hide();
	      		$('.editStatus_Deleted').hide();
	       	}
	    });
}

function jd(){
	
	var jsptype = 'journal';
	var client_id = "";
	var returntype = "";
	jdd=new Array();
	var info = journaltable.page.info();
	if(jdetails instanceof Array) {
		if(jdetails.length > 0) {
			var index = (info.page*info.length)+1;
			jdetails.forEach(function(fyInv){
				client_id = fyInv.clientId;
				var createdDate = formatUpdatedDate(fyInv.dateofinvoice);
				var invStatus = fyInv.status != undefined ? fyInv.status : "";
				journalid = fyInv.invoiceId;
				journalreturntype = fyInv.returnType;
				if(journalreturntype == 'Voucher' || journalreturntype == 'Contra'){
					journalid = fyInv.userId;
				}
				if(fyInv.drEntrie != undefined && fyInv.crEntrie != undefined){
					fyInv.drEntrie.forEach(function(drEntrie){
						var journalss = new Array();
						journalss.push(index);
						journalss.push('<span class="rightindent invStatus_'+invStatus+'">'+debit+' &nbsp; '+drEntrie.name+' A/c</span>');
						journalss.push('<span class="rightindent indformat invStatus_'+invStatus+'">'+drEntrie.value+'</span>');
						journalss.push('<span class=""></span>');
						journalss.push(createdDate);
						if(journalreturntype == 'GSTR1'){
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">('+fyInv.invoiceType+' - Sales)</span></span></div>');
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
						}else if(journalreturntype == 'Voucher'){
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateVoucherDetails(\''+journalid+'\',\'journaldetails\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Voucher)</span></span></div>');
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateVoucherDetails(\''+journalid+'\',\'journaldetails\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showDeletePopup(\''+journalid+'\',\''+journalreturntype+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}else if(journalreturntype == 'Contra'){
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateContraDetails(\''+journalid+'\',\'journaldetails\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Contra)</span></span></div>');
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateContraDetails(\''+journalid+'\',\'journaldetails\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showDeletePopup(\''+journalid+'\',\''+journalreturntype+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}else if(journalreturntype == 'Payment Receipt'){
							var journalvoucherNumber = fyInv.invoiceNumber;
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR1\',\''+jsptype+'\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Receipt)</span></span></div>');
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR1\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\'GSTR1\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}else if(journalreturntype == 'Payment'){
							var journalvoucherNumber = fyInv.invoiceNumber;
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR2\',\''+jsptype+'\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Payment)</span></span></div>');
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR2\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\'GSTR2\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}else if(journalreturntype == 'GSTR2'){
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">('+fyInv.invoiceType+' - Purchase)</span></span></div>');
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
						}else if(journalreturntype == 'EXPENSES'){
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\''+journalreturntype+'\',\''+jsptype+'\')">EXPENSES</a><div class="srtType"></div>');
							journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\''+journalreturntype+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\''+journalreturntype+'\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}
						jdd.push(journalss);
					});
					var lastCol = 0;
					fyInv.crEntrie.forEach(function(crEntrie){
						var pindex = new Array();
						pindex.push(index);
						var cssAdd = ++lastCol == fyInv.crEntrie.length ? "abcd" : "";
						pindex.push('<span class="'+cssAdd+' leftindent invStatus_'+invStatus+'">'+credit+' &nbsp; '+crEntrie.name+' A/c</span>');
						pindex.push('<span class="'+cssAdd+'"></span>');
						pindex.push('<span class="'+cssAdd+' indformat invStatus_'+invStatus+' rightindent">'+crEntrie.value+'</span>');
						pindex.push(createdDate);
						if(journalreturntype == 'GSTR1'){
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">('+fyInv.invoiceType+' - Sales)</span></span></div>');
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
						}else if(journalreturntype == 'Voucher'){
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateVoucherDetails(\''+journalid+'\',\'journaldetails\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Voucher)</span></span></div>');
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateVoucherDetails(\''+journalid+'\',\'journaldetails\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showDeletePopup(\''+journalid+'\',\''+journalreturntype+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}else if(journalreturntype == 'Contra'){
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateContraDetails(\''+journalid+'\',\'journaldetails\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Contra)</span></span></div>');
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateContraDetails(\''+journalid+'\',\'journaldetails\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showDeletePopup(\''+journalid+'\',\''+journalreturntype+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}else if(journalreturntype == 'Payment Receipt'){
							var journalvoucherNumber = fyInv.invoiceNumber;
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR1\',\''+jsptype+'\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Receipt)</span></span></div>');
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR1\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\'GSTR1\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}else if(journalreturntype == 'Payment'){
							var journalvoucherNumber = fyInv.invoiceNumber;
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR2\',\''+jsptype+'\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Payment)</span></span></div>');
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR2\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\'GSTR2\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}else if(journalreturntype == 'GSTR2'){
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')">'+fyInv.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">('+fyInv.invoiceType+' - Purchase)</span></span></div>');
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
						}else if(journalreturntype == 'EXPENSES'){
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\''+journalreturntype+'\',\''+jsptype+'\')">EXPENSES</a><div class="srtType"></div>');
							pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\''+journalreturntype+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\''+journalreturntype+'\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
						}
						jdd.push(pindex);
					});
				}
				index++;
			});
		}
	}
}

function formatUpdatedDate(createdDate){
	var createdDt = new Date(createdDate) ;
    var month = createdDt.getMonth() + 1; 
	var day = createdDt.getDate();
	var year = createdDt.getFullYear();
	return day+'-'+month+'-'+year;
}

function loadJournalTotals(totalsData){
	$('#credit').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCredit.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#debit').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalDebit.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
}