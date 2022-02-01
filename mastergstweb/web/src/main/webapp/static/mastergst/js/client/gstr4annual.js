var gstr4AnnualInvoiceArray = new Array();
var indexObj = new Object();

function supplierWiseAmts(type){
	var yearCode = $('#gstr4FinancialYear').val();
	$('#gstwiseData').html('');
	var tableContent='';
	$.ajax({
		url : contextPath+'/populateSupplierData'+urlSuffixs+'/'+yearCode+'?type='+type,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			gstr4AnnualInvoiceArray = new Array();
			var tableContent = "";
			var index = 1;
			if(response !="" && response !=null){
				if(type == 'b2bor'){
					if(response.b2bor !="" && response.b2bor !=null){
						for(var i = 0; i< response.b2bor.length; i++){
							gstr4AnnualInvoiceArray.push(response.b2bor[i]);
							var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
							
							for(var j = 0; j< response.b2bor[i].itms.length; j++){
								txval += response.b2bor[i].itms[j].txval;
								iamt += response.b2bor[i].itms[j].iamt;
								camt += response.b2bor[i].itms[j].camt;
								samt += response.b2bor[i].itms[j].samt;
								csamt += response.b2bor[i].itms[j].csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td>'+response.b2bor[i].ctin+'</td><td></td><td>'+response.b2bor[i].pos+'</td><td>'+txval+'</td><td>'+iamt+'</td><td>'+camt+'</td><td>'+samt+'</td><td>'+csamt+'</td><td><a data-toggle="tab" href="#amountstab" role="tab" aria-expanded="false" onclick=showAmountDetails(\''+response.b2bor[i].ctin+'\',\''+type+'\')>click here</a></td></tr>';
							index++;
						}
					}
				}else if(type == 'b2br'){
					if(response.b2br !="" && response.b2br !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.b2br.length; i++){
							gstr4AnnualInvoiceArray.push(response.b2br[i]);
							for(var j = 0; j< response.b2br[i].itms.length; j++){
								txval += response.b2br[i].itms[j].txval;
								iamt += response.b2br[i].itms[j].iamt;
								camt += response.b2br[i].itms[j].camt;
								samt += response.b2br[i].itms[j].samt;
								csamt += response.b2br[i].itms[j].csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td>'+response.b2br[i].ctin+'</td><td></td><td>'+response.b2br[i].pos+'</td><td>'+txval+'</td><td>'+iamt+'</td><td>'+camt+'</td><td>'+samt+'</td><td>'+csamt+'</td><td><a data-toggle="tab" href="#amountstab" role="tab" aria-expanded="false" onclick=showAmountDetails(\''+response.b2br[i].ctin+'\',\''+type+'\')>click here</a></td></tr>';
							index++;
						}
					}
				}else if(type == 'b2c'){
					if(response.b2bur !="" && response.b2bur !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.b2bur.length; i++){
							gstr4AnnualInvoiceArray.push(response.b2bur[i]);
							for(var j = 0; j< response.b2bur[i].itms.length; j++){
								txval += response.b2bur[i].itms[j].txval;
								iamt += response.b2bur[i].itms[j].iamt;
								camt += response.b2bur[i].itms[j].camt;
								samt += response.b2bur[i].itms[j].samt;
								csamt += response.b2bur[i].itms[j].csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td></td><td></td><td>'+response.b2bur[i].pos+'</td><td>'+txval+'</td><td>'+iamt+'</td><td>'+camt+'</td><td>'+samt+'</td><td>'+csamt+'</td><td><a a data-toggle="tab" href="#amountstab" role="tab" aria-expanded="false" onclick=showAmountDetails(\''+response.b2bur[i].pos+'\',\''+type+'\')>click here</a></td></tr>';
							index++;
						}
					}
				}else if(type == 'imps'){
					if(response.imps !="" && response.imps !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.imps.length; i++){
							gstr4AnnualInvoiceArray.push(response.imps[i]);
							for(var j = 0; j< response.imps[i].itms.length; j++){
								txval += response.imps[i].itms[j].txval;
								iamt += response.imps[i].itms[j].iamt;
								csamt += response.imps[i].itms[j].csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td></td><td></td><td>'+response.imps[i].pos+'</td><td>'+txval+'</td><td>'+iamt+'</td><td>'+camt+'</td><td>'+samt+'</td><td>'+csamt+'</td><td><a a data-toggle="tab" href="#amountstab" role="tab" aria-expanded="false" onclick=showAmountDetails(\''+response.imps[i].pos+'\',\''+type+'\')>click here</a></td></tr>';
							index++;
						}
					}
				}
			}
			$('#gstwiseData').append(tableContent);
		},error:function(err){console.log(err);}
	});
}

function showAmountDetails(ctin, type){
	$('#amountswisetableData').html('');
	$('#amountswise').addClass('active');
	$('#gstinwise').removeClass('active');
	var amountsContent = '';
	if(gstr4AnnualInvoiceArray.length >= 1){
		$.each(gstr4AnnualInvoiceArray, function(key, keyVal ) {
			if(type == 'b2c' || type == 'imps'){
				if(keyVal.pos == ctin){
					var slno = 1;
					$.each(keyVal.itms, function(key, item) {
						amountsContent += '<tr><td>'+slno+'</td><td>'+item.rt+'</td><td>'+item.txval+'</td><td>'+item.iamt+'</td>';
						if(type == 'imps'){
							amountsContent += '<td>0</td><td>0</td><td>'+item.csamt+'</td></tr>';							
						}else{
							amountsContent += '<td>'+item.camt+'</td><td>'+item.samt+'</td><td>'+item.csamt+'</td></tr>';
						}
						slno++;
					});
				}
			}else {
				if(keyVal.ctin == ctin){
					var slno = 1;
					$.each(keyVal.itms, function(key, item) {
						amountsContent += '<tr><td>'+slno+'</td><td>'+item.rt+'</td><td>'+item.txval+'</td><td>'+item.iamt+'</td><td>'+item.camt+'</td><td>'+item.samt+'</td><td>'+item.csamt+'</td></tr>';
						slno++;
					});
				}
			}
		});
	}
	$('#amountswisetableData').append(amountsContent);
}

function typeOfSupply(no){
	$('#tab6amtval'+no).val(0);
	$('#typeOfSupplyTaxRate'+no).val(0);
	
	var type = $('#typeOfSupply'+no).val();
	if(type == 'Inward'){
		$('#typeOfSupplyTaxRate'+no).find('option').remove().end().append("<option value='0'>0</option><option value='0.1'>0.1</option><option value='0.25'>0.25</option><option value='1'>1</option><option value='1.5'>1.5</option><option value='3'>3</option><option value='5'>5</option><option value='7.5'>7.5</option><option value='12'>12</option><option value='18'>18</option><option value='28'>28</option>");
	}else if(type == 'Outward'){
		$('#typeOfSupplyTaxRate'+no).find('option').remove().end().append("<option value='0'>0</option><option value='1'>1</option><option value='2'>2</option><option value='5'>5</option><option value='6'>6</option>");
	}
	$('#tab6amtval'+no).val('');
	findTax(no);
}
function typeOfSupplyTaxRate(no){
	//$('#tab6amtval'+no).val(0);
	//$('#typeOfSupplyTaxRate'+no).val(0);
	findTax(no);
}

function findTaxableamt(no){
	var amount = $('#tab6amtval'+no).val();
	var rate = $('#typeOfSupplyTaxRate'+no).val();
	findTax(no);
}
function findTax(no){
	
	var type = $('#typeOfSupply'+no).val();
	var amount = $('#tab6amtval'+no).val();
	var rate = $('#typeOfSupplyTaxRate'+no).val();
	if(amount == ''){
		amount = 0;
	}
	if(rate == ''){
		rate = 0;
	}

	if(type == 'Inward'){
		$('#tab6Iamt'+no).val(0.0);
		$('#tab6Camt'+no).val(0.0);
		$('#tab6Samt'+no).val(0.0);
		$('#tab6Cess'+no).val(0.0);
	}else if(type == 'Outward'){
		var tax = amount * rate / 100;
		$('#tab6Iamt'+no).val(0.0);
		$('#tab6Camt'+no).val(tax/2);
		$('#tab6Samt'+no).val(tax/2);
		$('#tab6Cess'+no).val(0.0);
	}
	findTotalTaxs();
}

function clickinwordOrOutwardtab6Edit(a,b,c,d,e){
	$(a).show();
	$(b).show();
	$(c).hide();
	$(d).attr('readonly', false);
	$(d).addClass('tpone-input-edit');
	$(e).show();
	var index = indexObj['tab6'];
	$('#addmore'+index).removeClass('d-none');
}

function addInwardOrOutwardRecord() {
	var index = indexObj['tab6']+1;
	var indx = indexObj['tab6'];
	$('#inwordOrOutwardtab6Body').append('<tr id="inwordOrOutwardtab6Bdytr'+index+'"><td id="sno_row'+index+'"class="text-left">'+index+'</td><td class="text-right form-group gst6-error"><select class="form-control" id="typeOfSupply'+index+'" onchange="typeOfSupply('+index+')"><option value=""> select </option><option value="Inward">Inward</option><option value="Outward">Outward</option></select></td><td class="text-right form-group gst6-error"><input id="tab6amtval'+index+'" type="text" class="form-control" name="gstr4annualinv.outsupply['+indx+'].txval" onkeyup="findTaxableamt('+index+')"/></td><td class="text-left form-group gst6-error"><select name="gstr4annualinv.outsupply['+indx+'].rate" class="form-control" id="typeOfSupplyTaxRate'+index+'" onchange="typeOfSupplyTaxRate('+index+')"><option value=""> select </option></select></td><td class="text-right form-group gst6-error"><input id="tab6Iamt'+index+'" type="text" class="form-control tpthree-input" placeholder="0.00"/></td><td class="text-right form-group gst6-error"><input id="tab6Camt'+index+'" type="text" class="form-control tpthree-input" placeholder="0.00"/></td><td class="text-right form-group gst6-error"><input id="tab6Samt'+index+'" type="text" class="form-control tpthree-input" placeholder="0.00"/></td><td class="text-right form-group gst6-error"><input id="tab6Cess'+index+'" type="text" class="form-control tpthree-input" placeholder="0.00"/></td><td class="text-right form-group gst6-error"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteItem('+index+')" class="delrow"></td></tr>');
	$('form[name="gstr4annualinvoceform"]').validator('update');
	// name="gstr4annualinv.outsupply['+indx'].txval"
	// name="gstr4annualinv.outsupply['+indx'].rate" 
	// name="gstr4annualinv.outsupply['+indx'].type"
	
	indexObj['tab6']++;
}

function deleteItem(index) {
	//$(item).parent().parent().remove();
	indexObj['tab6']--;
	findTotalTaxs();
	$("#inwordOrOutwardtab6Body tr").each(function(index) {
		
		$(this).find('input , select').each (function() {
			var inputclass = $(this).attr('class');
			var inputname = $(this).attr('name');
			var inputid = $(this).attr('id');
			var onkyup = $(this).attr('onkeyup');
			var onchnge = $(this).attr('onchange');
			if(onchnge != undefined){
				onchnge = replaceAt(onchnge,9,rowno);
			    $(this).attr('onchange',onchnge);
			}
			if(onkyup != undefined){
				onkyup = replaceAt(onkyup,10,rowno);
			    $(this).attr('onkeyup',onkyup);
			}
			if(inputclass != undefined){
				if(rowno<10){
					inputclass = inputclass.slice(0, -1);
		   	   	}else{
		   	   		inputclass = inputclass.slice(0, -2);
		   	   	}
				inputclass = inputclass+rowno;
				$(this).attr('class',inputclass);
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
			if(inputid != undefined){
				if(rowno<10){
					inputid = inputid.slice(0, -1);
		   	   	}else{
		   	   		inputid = inputid.slice(0, -2);
		   	   	}
				inputid = inputid+rowno;
				$(this).attr('id',inputid);
			}
		});
	});
}
function findTotalTaxs(){
	var iamt = 0, camt = 0, samt = 0;
	$("#inwordOrOutwardTable6 tbody tr").each(function () {
        iamt += parseFloat($(this).find("td:nth-child(5) input[type='text']").val() == '' ? '0' : $(this).find("td:nth-child(5) input[type='text']").val());
        camt += parseFloat($(this).find("td:nth-child(6) input[type='text']").val() == '' ? '0' : $(this).find("td:nth-child(6) input[type='text']").val());
        samt += parseFloat($(this).find("td:nth-child(7) input[type='text']").val() == '' ? '0' : $(this).find("td:nth-child(7) input[type='text']").val());
    });	
	$('#totIamt').val(iamt);
	$('#totCamt').val(camt);
	$('#totSamt').val(samt);
	$('#totCess').val('0.0');
}
/*function deleteItem(item) {
				
	$(item).parent().parent().remove();
	indexObj['tab6']--;
	findTotalTaxs();
}*/