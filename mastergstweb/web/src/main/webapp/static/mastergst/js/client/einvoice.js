var einvTableUrl = new Object();
var einvTable = new Object();
$(document).ready(function(){
	$('#eitemasOndate').datetimepicker({
		timepicker: false,
		format: 'd/m/Y',
        scrollMonth: true
	});
	$('.easOndatewrap').click(function(){
		$('#eitemasOndate').focus();
	});
	
	$('input[type=radio][name=itemType]').change(function() {
		updateeItemProduct(this.value);
	});
	function updateeItemProduct(value){
		if(value == "Product"){
			$('.eitemCode_txt').text("Item Code");
			$('.eitemName_txt').text("Item Name");
			$('.eitem_grpCode_txt').text("Item Group Code");
			$('.eitem_grpName_txt').text("Item Group Name");
			$('.eitem_description_txt').text("Item Description");
			$('.ehsnSacTxt').text("HSN Code");
			$('.eitemDescription').attr("placeholder","Item Description");
			$('.eitemSalesPriceTxt').text('Item Sale Price');
			//$('.eitem_description_txt').css("margin-left","2px");
			$('.edescInput').css({"padding-left":"52px"});
			$('.edescInput').addClass("pr-0");
			$('.eopeningStockDiv,.easOnDateStockDiv,.esafteyStockLevelDiv,.ereOrderDiv,.eItemPurchasePriceDiv,.ewholeSalePriceDiv,.emrpPriceDiv,.eserviceDiv,.eimagesDiv').removeClass("d-none");
			 var uqcoptions = {
						url: function(phrase) {
							phrase = phrase.replace('(',"\\(");
							phrase = phrase.replace(')',"\\)");
							return _getContextPath()+"/uqcconfig?query="+ phrase + "&format=json";
						},
						getValue: "name",
						list: {
							onLoadEvent: function() {
							if($("#eac-container-eunit ul").children().length == 0) {
									$("#eunitempty").show();
								} else {
									$("#eunitempty").hide();
								}
							},
							maxNumberOfElements: 43
						}
					};
					$("#eunit").easyAutocomplete(uqcoptions);
		}else{
			$('.eitemCode_txt').text("Service Code");
			$('.eitemName_txt').text("Service Name");
			$('.eitem_grpCode_txt').text("Service Group Code");
			$('.eitem_grpName_txt').text("Service Group Name");
			$('.eitem_description_txt').text("Service Description");
			$('.eitemDescription').attr("placeholder","Service Description");
			$('.ehsnSacTxt').text("SAC Code");
			$('.eitemSalesPriceTxt').text('Service Sale Price');
			$('.edescInput').css({"padding-left":"43px"});
			$('.edescInput').removeClass("pr-0");
			$('#eunit').val("NA");
			$('.eopeningStockDiv,.easOnDateStockDiv,.esafteyStockLevelDiv,.ereOrderDiv,.eItemPurchasePriceDiv,.ewholeSalePriceDiv,.emrpPriceDiv,.eserviceDiv,.eimagesDiv').addClass("d-none");
			var uqcoptions = {
					data: ["NA"]
				};
				$("#eunit").easyAutocomplete(uqcoptions);
		}
	}
	var clientSignUrl = _getContextPath()+'/getClientDetails/'+clientId;
	$.ajax({
		url: clientSignUrl,
		async: false,
		cache: false,
		success : function(response) {clientDetails = response;},
		error : function(e) {}
	});
});

function eloadInvTable(id, clientId, varRetType,varRetTypeCode, month, year, userType, fullname,booksOrReturns){
	if(varRetTypeCode == 'EINVOICE'){
		var pUrl = _getContextPath()+'/getEInvs/'+id+'/'+clientId+'/EINVOICE/'+month+'/'+year;
		var urlSuffix = id+'/'+fullname+'/'+userType+'/'+clientId+'/'+varRetType
		einvTableUrl[varRetTypeCode] = pUrl;
		if(einvTable[varRetTypeCode]){
			einvTable[varRetTypeCode].destroy();
		}
		einvTable[varRetTypeCode] = $('#dbTable'+varRetTypeCode).DataTable({
			"dom": '<"toolbar"f>lrtip<"clear">',
			 "processing": true,
			 "serverSide": true,
			 "lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
			 "ajax": {
				 url: pUrl,
				 type: 'GET',
				 contentType: 'application/json; charset=utf-8',
				 dataType: "json",
				 'dataSrc': function(resp){
					 $('#updtEinvfilter_summary').show();
					 resp.recordsTotal = resp.invoices.totalElements;
					 resp.recordsFiltered = resp.invoices.totalElements;
					 loadETotals(varRetTypeCode, resp.invoicesAmount);
					 return resp.invoices.content ;
				 }
			 },
			"paging": true,
			'pageLength':10,
			"responsive": true,
			"searching": true,
			
			'columns': geteInvColumns(id, clientId, varRetType, userType, month, year,urlSuffix, booksOrReturns),
			'columnDefs' : geteInvColumnDefs(varRetType),
			"order": [[5,'desc']],
			"createdRow": function( row, data, dataIndex ) {

			  }
		});	
		$('#dbEinvoiceTable_body').on('click','tr', function(e){
			if (!$(e.target).closest('.nottoedit').length) {
				var dat = einvTable[varRetTypeCode].row($(this)).data();
				editEInvPopup(dat.invtype, 'GSTR1', dat.invoiceId,'NotInJournals');
			}
		});
	}
}
function eloadInvoiceSupport(clientId, varRetType, varRetTypeCode, month, year, callback){
	if(varRetTypeCode == 'EINVOICE'){
		var urlStr = _getContextPath()+'/getAddtionalInvsSupport/'+clientId+'/'+varRetType+'/'+month+'/'+year;
		$.ajax({
			url: urlStr,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				callback(response, varRetType, varRetTypeCode);
			}
		});
	}
}
function loadeCustomersInDropdown(response, varRetType, varRetTypeCode){
	var custMultiSelObj = $('#emultiselect'+varRetTypeCode+'3');
	var customFieldsMultiSelObj = $('#emultiselect'+varRetTypeCode+'7');
	var customFieldsMultiSelObj1 = $('#emultiselect'+varRetTypeCode+'8');
	var customFieldsMultiSelObj2 = $('#emultiselect'+varRetTypeCode+'9');
  	var customFieldsMultiSelObj3 = $('#emultiselect'+varRetTypeCode+'10');
  	custMultiSelObj.find('option').remove().end();
  	customFieldsMultiSelObj.find('option').remove().end();
  	customFieldsMultiSelObj1.find('option').remove().end();
  	customFieldsMultiSelObj2.find('option').remove().end();
  	customFieldsMultiSelObj3.find('option').remove().end();
	if (response.billToNames.length > 0) {				
		response.billToNames.forEach(function(customer) {
			if(customer != ""){
				custMultiSelObj.append($("<option></option>").attr("value",customer).text(customer)); 
			}
		});
	}
	if(response.customField1 != null && response.customField1.length > 0) {				
		response.customField1.forEach(function(field) {
			if(field != ''){
				customFieldsMultiSelObj.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	if(response.customField2 != null && response.customField2.length > 0) {				
		response.customField2.forEach(function(field) {
			if(field != ''){
				customFieldsMultiSelObj1.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	if(response.customField3 != null && response.customField3.length > 0) {				
		response.customField3.forEach(function(field) {
			if(field != ''){
				customFieldsMultiSelObj2.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	if(response.customField4 != null && response.customField4.length > 0) {				
		response.customField4.forEach(function(field) {
			if(field != ''){
				customFieldsMultiSelObj3.append($("<option></option>").attr("value",field).text(field));
			} 
		});
	}
	emultiselrefresh('#emultiselect'+varRetTypeCode+'3', '- Customers -', varRetType);
	if(response.customField1 != null){
		var cF1 = '- Custom Field1 -';
		if(response.customFieldName1 != null){
			cF1 = response.customFieldName1;
		}
		$('#emultiselect'+varRetTypeCode+'7').css("display","block");
		emultiselrefresh('#emultiselect'+varRetTypeCode+'7', cF1, varRetType);
    	$('#emultiselect'+varRetTypeCode+'7').multiselect('rebuild');
	}else{
		$('#emultiselect'+varRetTypeCode+'7').css("display","none");
	}
	if(response.customField2 != null){
		var cF2 = '- Custom Field2 -';
		if(response.customFieldName2 != null){
			cF2 = response.customFieldName2;
		}
		$('#emultiselect'+varRetTypeCode+'8').css("display","block");
		emultiselrefresh('#emultiselect'+varRetTypeCode+'8', cF2, varRetType);
    	$('#emultiselect'+varRetTypeCode+'8').multiselect('rebuild');
	}else{
		$('#emultiselect'+varRetTypeCode+'8').css("display","none");
	}
	if(response.customField3 != null){
		var cF3 = '- Custom Field3 -';
		if(response.customFieldName3 != null){
			cF3 = response.customFieldName3;
		}
		$('#emultiselect'+varRetTypeCode+'9').css("display","block");
		emultiselrefresh('#emultiselect'+varRetTypeCode+'9', cF3, varRetType);
    	$('#emultiselect'+varRetTypeCode+'9').multiselect('rebuild');
	}else{
		$('#emultiselect'+varRetTypeCode+'9').css("display","none");
	}
	if(response.customField4 != null){
		var cF4 = '- Custom Field4 -';
		if(response.customFieldName4 != null){
			cF4 = response.customFieldName4;
		}
		$('#emultiselect'+varRetTypeCode+'10').css("display","block");
		emultiselrefresh('#emultiselect'+varRetTypeCode+'10', cF4, varRetType);
    	$('#emultiselect'+varRetTypeCode+'10').multiselect('rebuild');
	}else{
		$('#emultiselect'+varRetTypeCode+'10').css("display","none");
	}
}
function getEInvData(invId, returnType, popudateData){
	var urlStr = _getContextPath()+'/getinv/'+invId+'/'+returnType;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			popudateData(response);
		}
	});
}
function editEInvPopup(invType,varRetType,id,journalType){
	getEInvData(id, varRetType, function(invoice) {
        if (invoice.invoiceId == id) {
        	invoice= updateInvoiceDetails(invoice);
			ebankAndVertical();
			$('#idEInvType').val(invType);
			if(invoice.customerEmail != null){
				$('#customer_email').val(invoice.customerEmail);
			}
        	if(invoice.irnNo == null || invoice.irnNo == ""){
        		$('#irnNumber_txt,#cancelIRN_btn').hide();
        		if(gnIRN){
        			$('#generateIRNBtn').show();
        		}
        	}else{
				$('#einvDraft_Btn').val("Update");
        		$('#irnNumber_txt').show();
        		if(invoice.irnStatus != "Cancelled"){
        			if(canIRN){
        				$('#cancelIRN_btn').show();
        			}
            	}
        		$('#irnNumber').html(invoice.irnNo);
				$('#irnNo').val(invoice.irnNo);
				$('#irnStatus').val(invoice.irnStatus);
				$('#signedQrCode').val(invoice.signedQrCode);
				$('#signedInvoice').val(invoice.signedInvoice);
				$('#ackNo').val(invoice.ackNo);
				$('#einvStatus').val(invoice.einvStatus);
				$('#ackDt').val(invoice.ackDt);
        		$('#generateIRNBtn').hide();
        	}
        	if(invoice.srctype != null){
				$('#esrctype').val(invoice.srctype);
			}else{
				$('#esrctype').val("");
			}
        	if(invoice.termDays != null && invoice.termDays != ""){
        		$('#eterms_drop_div').val(invoice.termDays);
        		$('#etermDays').val(invoice.termDays);
        		$('.eadddropdown').find('span').text(invoice.termDays);
        	}
        	if(invoice.dueDate != null){
        		$('#eduedate_div').val(invoice.dueDate);
        	}
        	  if (invoice.invoiceLevelCess == 'No') {
	                $('#einvoiceLevelCess').prop("checked", false).val('No');
	                $('.ecessFlag').hide();
	                $('#einvLevelCess').val('No');
	            } else {
	                $('#einvoiceLevelCess').prop("checked", true);
	                $('#einvoiceLevelCess').val('Yes');
	                $('.ecessFlag').show();
	                $('#einvLevelCess').val('Yes');
	            }
        	var form = $('#einvoceform');
        	//etcstdsoptions(varRetType);
        	//$('#einvoiceId').val(id);
        	var name1 = $(this).attr('name');
        	$.each(invoice,function(key,value) {
            	if(key == 'id'){
            		$('#einvoiceId').val(value).attr('name','id');
            		irnCancelArray.push(value);
            	}
				 if (key == 'section') {
		                    $('#etdstcssection').val(value);
		                }
		                if (key == 'tcstdspercentage') {
		                    $('#etcs_percent').val(value);
		                }
				if (key == 'dateofinvoice') {
		                    form.find("input[id='einvoicedate']").val(value);
		                }
				if(key == 'notes'){
					$('#ebankNotes').val(value);
				}
				if(key == 'terms'){
					$('#ebankTerms').val(value);
				}
				if(key == 'branch'){
					$('#ebranch').val(value);
				}
				if(key == 'entertaimentprintto'){
					$('#entertaimentprintto').val(value);
				}
				if(key == 'countryCode'){
					$('#einvCountryCode').val(value);
				}
				if(key == 'vertical'){
					$('#evertical').val(value);
				}
				if(key == 'cessType'){
                	if(value == 'Taxable Value' || value == null){
                		$('#ecess_taxable').attr("checked","true");	
                	}else{
                		$('#ecess_qty').attr("checked","true");	
                	}
                }
				$('#epaymentStatusinv').val(invoice.paymentStatus);
		            if(invoice.pendingAmount != null){
		            	$('#eInvPendingAmount').val(parseFloat(invoice.pendingAmount).toFixed(2));	
		            }
		            if(invoice.receivedAmount != null){
		            	$('#eInvReceivedAmount').val(parseFloat(invoice.receivedAmount).toFixed(2));
		            }
				if(key == 'voucherNumber'){
					$('#evoucherNumber').val(value);
				}
				if(key == 'voucherDate'){
					$('#evoucherDate').val(value);
				}
				if(key == 'ecommercegstin'){
					$('#einvEcommGSTIN').val(value);
				}
				if(key == 'portcode'){
					$('#eportcode').val(value);
				}
				if(key == 'shippingBillNumber'){
					$('#eshippingBillNumber').val(value);
				}
				if(key == 'shippingBillDate'){
					$('#eshippingBillDate').val(value);
				}
				if (key == 'samebilladdress') {
					if(value == "1"){
						$('#samedispatchaddress').prop("checked",true);
						$('#shimentAddrEdit').addClass('d-none');
					}else{
						$('#samedispatchaddress').prop("checked",false);
						$('#shimentAddrEdit').removeClass('d-none');
					}
				}
				if (key == 'bankdetails') {
		                    if (value == 'true') {
		                            $('.eaddBankDetails ').attr("checked", "true");
		                            if ($('.eaddBankDetails').is(":checked")) {
		                                $("#eselectBankDiv").show();
		                                $("#eselectBank").removeAttr("disabled");
		                                $("#ebank_details").show();
		                            } else {
		                                $("#ebank_details").hide();
		                                $("#eselectBankDiv").hide();
		                            }
		                    } else if (value == 'false') {
		                        $('.eaddBankDetails').removeAttr("checked");
		                        if ($('.eaddBankDetails').is(":checked")) {
		                            $("#eselectBankDiv").show();
		                            $("#ebank_details").show();
		                        } else {
		                            $("#eselectBankDiv").hide();
		                            $("#ebank_details").hide();
		                        }
		                    }
		                }
				if(key == 'bankname'){
					$('#ebankName').val(value);
				}
				if(key == 'bankAccountName'){
					$('#ebankAccountName').val(value);
				}
				if(key == 'branchname'){
					$('#ebankBranch').val(value);
				}
				if(key == 'accountnumber'){
					$('#ebankAcctNo').val(value);
				}
				if(key == 'ifsccode'){
					$('#ebankIFSC').val(value);
				}
				if(key == 'accountname'){
					$('#ebankAccountName').val(value);
				}
				  if (invoice.roundOffAmount == "" || invoice.roundOffAmount == null) {
		                $('#eroundOffTotalValue').val('');
		            } else {
		                $('#eroundOffTotalChckbox').attr("checked", "true");
		                $('#eroundOffTotalValue').val((invoice.roundOffAmount).toFixed(2));
		            }
		            if (invoice.notroundoftotalamount == "" || invoice.notroundoftotalamount == null) {
		                $('#ehiddenroundOffTotalValue').val(invoice.totalamount);
		            } else {
		                $('#ehiddenroundOffTotalValue').val(parseFloat(invoice.notroundoftotalamount).toFixed(2));
		            }
					if (key == 'tdstcsenable') {
		                if (value == true) {
		                    $('#etcsval').prop("checked", true);
		                    etcscheckval();
		                }
		            }
    		});
        	
        		$('#igstOnIntra').val(invoice.igstOnIntra ? invoice.igstOnIntra : "N");
				$('#exchangeRate').val(invoice.exchangeRate);
				$('#addcurrencyCode').val(invoice.addcurrencyCode);
				if(invoice.addcurrencyCode != ""){
					$('.usd-lable').html("<span style='padding-left:2px'>1 "+invoice.addcurrencyCode+" = </span>");
				}else{
					$('.usd-lable').html("<span style='padding-left:2px'>1 INR = </span>");
				}
        	   $('#einvoiceserialno').val(invoice.invoiceno);
        	   $('#einvDocType').val(invoice.typ);
        	   $('#invoiceStatecode').val(invoice.statename);
        	   $('#invoiceTradeName').val(invoice.billedtoname);
        	  // $('#einvbilledtostatecode').val(invoice.statename);
			  if(invoice.b2b[0]){
				  if(invoice.b2b[0].ctin != undefined){
					$('#ebilledtogstin').val(invoice.b2b[0].ctin);
				  }
			  }
        	   if(invoice.einvCategory != "" && invoice.einvCategory != null){
        		   $('#invCategory').val(invoice.einvCategory);
        	   }else{
        		   var expType = "";
				   var einvType = "";
				   if(invoice.b2b[0] && invoice.b2b[0].inv[0]){
					   if(invoice.b2b[0].inv[0].invTyp != undefined){
							einvType = invoice.b2b[0].inv[0].invTyp;
					   }
				   }
        		   if(invoice.exp[0]){
        			   if(invoice.exp[0].expTyp != undefined){
        				   expType = invoice.exp[0].expTyp;
        			   }
        		   }
        		   if(einvType == "R"){
        			   $('#invCategory').val("B2B");  
        		   }else if(einvType == "DE"){
        			   $('#invCategory').val("DEXP");  
        		   }else if(einvType == "SEZWP"){
        			   $('#invCategory').val("SEZWP");  
        		   }else if(einvType == "SEZWOP"){
        			   $('#invCategory').val("SEZWOP");  
        		   }
        		   if(expType == "WPAY"){
        			   $('#invCategory').val("EXPWP");  
        		   }else if(expType == "WOPAY"){
        			   $('#invCategory').val("EXPWOP");  
        		   }
        	   }
        	  
        	   if(invoice.revchargetype == "Regular"){
        		   $('#einv_revchargetype').val("N");
        	   }else if(invoice.revchargetype == "Reverse"){
        		   $('#einv_revchargetype').val("Y");
        	   }else{
        		   $('#einv_revchargetype').val(invoice.revchargetype ? invoice.revchargetype : "N");
        	   }
        	   $('#e_type').val(invoice.addressType);
        	   $('#eCommSupplyType').val(invoice.eCommSupplyType);
			   if(invoice.b2b[0]){
				  if(invoice.b2b[0].etin != undefined){
					$('#einvEcommGSTIN').val(invoice.b2b[0].etin);
				  }
			   }
        	   $('#invoicePincode').val(invoice.buyerPincode);
        	  // $('#einv_revchargetype').val(invoice.revchargetype);
				$('#ereferenceNumber').val(invoice.referenceNumber);
				$('.customField1').val(invoice.customField1);
				$('.customField2').val(invoice.customField2);
				$('.customField3').val(invoice.customField3);
				$('.customField4').val(invoice.customField4);
        	 
        	   //$('#totAsseAmt').html(invoice.totalAssAmount ? formatNumber(parseFloat(invoice.totalAssAmount).toFixed(2)) : '0.00');
        	   $('#etotTaxable').html(invoice.totaltaxableamount ? formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2)) : '0.00');
        	   $('#etotIGST').html(invoice.totaltax ? formatNumber(parseFloat(invoice.totaltax).toFixed(2)) : '0.00');
        	   $('#etotStateCess').html(invoice.totalStateCessAmount ? formatNumber(parseFloat(invoice.totalStateCessAmount).toFixed(2)) : '0.00');
        	   $('#etotCESS').html(invoice.totalCessAmount ? formatNumber(parseFloat(invoice.totalCessAmount).toFixed(2)) : '0.00');
        	   $('#totCessAdvol').html(invoice.totalCessNonAdVal ? formatNumber(parseFloat(invoice.totalCessNonAdVal).toFixed(2)) : '0.00');
        	   $('#etotTotal').html(invoice.totalamount ? formatNumber(parseFloat(invoice.totalamount).toFixed(2)) : '0.00');
        	   $('#eidTotal').html(invoice.totalamount ? formatNumber(parseFloat(invoice.totalamount).toFixed(2)) : '0.00');
			   if(invType == "Exports"){
					$('#totcurAmt').html(invoice.totalCurrencyAmount ? formatNumber(parseFloat(invoice.totalCurrencyAmount).toFixed(2)) : '0.00');
			   }
			  
			   $('#etcsamount').html(invoice.tcstdsAmount ? formatNumber(parseFloat(invoice.tcstdsAmount).toFixed(2)) : '0.00');
			   $('#etcsfield').html(invoice.tcstdsAmount ? formatNumber(parseFloat(invoice.tcstdsAmount).toFixed(2)) : '0.00');
			   $('#etcsamt').val(invoice.tcstdsAmount ? invoice.tcstdsAmount.toFixed(2) : '0.00');
			   $('#einvvalwithtcs').html(invoice.netAmount ? formatNumber(parseFloat(invoice.netAmount).toFixed(2)) : '0.00');
				var bgstin = "";
				if(invoice.buyerDtls.gstin != null){
					bgstin = invoice.buyerDtls.gstin+",";
				}
				var blgNm = "";
				if(invoice.buyerDtls.lglNm != null){
					blgNm = invoice.buyerDtls.lglNm+",";
				}
				var bloc = "";
				if(invoice.buyerDtls.loc != null){
					bloc = invoice.buyerDtls.loc+",";
				}
				var bstate = "";
				if(invoice.buyerDtls.state != null){
					bstate = invoice.buyerDtls.state+",";
				}
				var bpin = "";
				if(invoice.buyerDtls.pin != null){
					bpin = invoice.buyerDtls.pin;
				}
				$('#buyerDtls_gstin').val(invoice.buyerDtls.gstin);
				$('#buyerDtls_lglNm').val(invoice.buyerDtls.lglNm);
				$('#buyerDtls_addr1').val(invoice.buyerDtls.addr1);
				$('#buyerDtls_addr2').val(invoice.buyerDtls.addr2);
				$('#buyerDtls_loc').val(invoice.buyerDtls.loc);
				$('#buyerDtls_pin').val(invoice.buyerDtls.pin);
				$('#buyerDtls_state').val(invoice.buyerDtls.state);
				$('#buyerDtls_pos').val(invoice.buyerDtls.pos);
				$('.buyerAddrText').html('Edit');
				var addr = "";
				if(invoice.buyerDtls.addr1 != null){					
				addr = invoice.buyerDtls.addr1.substring(1,15)+"...";
				}
				var gstlgnm = "";
				if(bgstin != ""){
					gstlgnm = gstlgnm+""+bgstin;
				}
				if(blgNm != ""){
					gstlgnm = gstlgnm+""+blgNm;
				}
				if(gstlgnm != ""){
					addr = gstlgnm+""+addr;
				}
				if(bloc != ""){
					addr = addr+""+bloc;
				}
				if(bstate != ""){
					addr = addr+""+bstate;
				}
				if(bpin != ""){
					addr = addr+""+bpin;
				}
				$('#einvbuyerAddr').html(addr);
				if(invoice.dispatcherDtls != null){	
					$('#dispatcherDtls_nm').val(invoice.dispatcherDtls.nm);
					$('#dispatcherDtls_addr1').val(invoice.dispatcherDtls.addr1);
					$('#dispatcherDtls_addr2').val(invoice.dispatcherDtls.addr2);
					$('#dispatcherDtls_loc').val(invoice.dispatcherDtls.loc);
					$('#dispatcherDtls_pin').val(invoice.dispatcherDtls.pin);	
					$('#dispatcherDtls_stcd').val(invoice.dispatcherDtls.stcd);
					var disaddr = "";
				if(invoice.dispatcherDtls.addr1 != null){	
					disaddr = invoice.dispatcherDtls.addr1.substring(1,15)+"...";
				}
				var dlgNm = "";
				if(invoice.dispatcherDtls.nm != null){
					dlgNm = invoice.dispatcherDtls.nm+",";
				}
				var dloc = "";
				if(invoice.dispatcherDtls.loc != null){
					dloc = invoice.dispatcherDtls.loc+",";
				}
				var dstate = "";
				if(invoice.dispatcherDtls.stcd != null){
					dstate = invoice.dispatcherDtls.stcd+",";
				}
				var dpin = "";
				if(invoice.dispatcherDtls.pin != null){
					dpin = invoice.dispatcherDtls.pin;
				}
				var gstlgnm = "";
				if(dlgNm != ""){
					gstlgnm = gstlgnm+""+dlgNm;
				}
				if(gstlgnm != ""){
					disaddr = gstlgnm+""+disaddr;
				}
				if(dloc != ""){
					disaddr = disaddr+""+dloc;
				}
				if(dstate != ""){
					disaddr = disaddr+""+dstate;
				}
				if(dpin != ""){
					disaddr = disaddr+""+dpin;
				}
					$('#einvdispatchAddr').html(disaddr);
				}
				if(invoice.shipmentDtls != null){
					$('#shipmentDtls_gstin').val(invoice.shipmentDtls.gstin);
					$('#shipmentDtls_lglNm').val(invoice.shipmentDtls.lglNm);
					$('#shipmentDtls_trdNm').val(invoice.shipmentDtls.trdNm);
					$('#shipmentDtls_addr1').val(invoice.shipmentDtls.addr1);
					$('#shipmentDtls_addr2').val(invoice.shipmentDtls.addr2);
					$('#shipmentDtls_loc').val(invoice.shipmentDtls.loc);
					$('#shipmentDtls_pin').val(invoice.shipmentDtls.pin);	
					$('#shipmentDtls_stcd').val(invoice.shipmentDtls.stcd);
						var shipaddr = "";
				if(invoice.shipmentDtls.addr1 != null){	
					shipaddr = invoice.shipmentDtls.addr1.substring(1,15)+"...";
				}
				var sgstin = "";
				if(invoice.shipmentDtls.gstin != null){
					sgstin = invoice.shipmentDtls.gstin+",";
				}
				var slgNm = "";
				if(invoice.shipmentDtls.trdNm != null){
					slgNm = invoice.shipmentDtls.trdNm+",";
				}
				var sloc = "";
				if(invoice.shipmentDtls.loc != null){
					sloc = invoice.shipmentDtls.loc+",";
				}
				var sstate = "";
				if(invoice.shipmentDtls.stcd != null){
					sstate = invoice.shipmentDtls.stcd+",";
				}
				var spin = "";
				if(invoice.shipmentDtls.pin != null){
					spin = invoice.shipmentDtls.pin;
				}
				var gstlgnm = "";
				if(sgstin != ""){
					gstlgnm = gstlgnm+""+sgstin;
				}
				if(slgNm != ""){
					gstlgnm = gstlgnm+""+slgNm;
				}
				if(gstlgnm != ""){
					shipaddr = gstlgnm+""+shipaddr;
				}
				if(sloc != ""){
					shipaddr = shipaddr+""+sloc;
				}
				if(sstate != ""){
					shipaddr = shipaddr+""+sstate;
				}
				if(spin != ""){
					shipaddr = shipaddr+""+spin;
				}	
					$('#einvshipmentAddr').html(shipaddr);
				}
			   einvRowCount = 1;
        	   
               if (einvRowCount < invoice.items.length) {
                   for (var i = 1; i < invoice.items.length; i++) {
                       if (invoice.items[i]) {
                    	   addEinvoiceRow('EINVOICE');
                       }
                   }
               }
               for (var i = 1; i <= invoice.items.length; i++) {
            	   var totTax = invoice.items[i - 1].igstamount + invoice.items[i - 1].cgstamount + invoice.items[i - 1].sgstamount;
	                if (true) {
	                	if (invoice.items[i - 1].cessrate == null) {
	                        invoice.items[i - 1].cessrate = 0;
	                    }
	                	if(invoice.cessType == null || invoice.cessType == 'Taxable Value'){
	                    	invoice.items[i - 1].cessrate = invoice.items[i - 1].cessrate+"%";
			            }
	                	$("#itemnotes_text" + i).val(invoice.items[i - 1].itemNotescomments);
	                	$("#eproduct_text" + i).val(invoice.items[i - 1].itemno);
	                	$("#ehsn_text" + i).val(invoice.items[i - 1].hsn);
	                	$("#euqc_text" + i).val(invoice.items[i - 1].uqc);
	                	$("#ebarcode_text" + i).val(invoice.items[i - 1].barCode);
						$("#efreeqty_text" + i).val(invoice.items[i - 1].freeQty);
	                	$("#eqty_text" + i).val(invoice.items[i - 1].quantity ? invoice.items[i - 1].quantity.toFixed(3) : '0.000');
	                	//$("#efreeqty_text" + i).val(invoice.items[i - 1].freeQty ? invoice.items[i - 1].freeQty.toFixed(2) : '0.00');
	                	$("#erate_text" + i).val(invoice.items[i - 1].rateperitem ? invoice.items[i - 1].rateperitem.toFixed(3) : '0.000');
	                	$("#ediscount_text" + i).val(invoice.items[i - 1].discount ? invoice.items[i - 1].discount.toFixed(2) : '0.00');
	                	$("#othrcharge_text" + i).val(invoice.items[i - 1].othrCharges ? invoice.items[i - 1].othrCharges.toFixed(2) : '0.00');
	                	//$("#eassamt_text" + i).val(invoice.items[i - 1].assAmt ? invoice.items[i - 1].assAmt : '0.00');
	                	$("#eabb"+i).val(totTax ? totTax.toFixed(2) : '0.00');
	                	$("#etaxableamount_text" + i).val(invoice.items[i - 1].taxablevalue ? invoice.items[i - 1].taxablevalue : '0.00');
	                	$("#etaxrate_text" + i).val(invoice.items[i - 1].rate);
	                	$("#eigsttax_text" + i).val(invoice.items[i - 1].igstamount ? invoice.items[i - 1].igstamount.toFixed(2) : '0.00');
	                	$("#ecgsttax_text" + i).val(invoice.items[i - 1].cgstamount ? invoice.items[i - 1].cgstamount.toFixed(2) : '0.00');
	                	$("#esgsttax_text" + i).val(invoice.items[i - 1].sgstamount ? invoice.items[i - 1].sgstamount.toFixed(2) : '0.00');
	                	$("#ecessnonadv_text" + i).val(invoice.items[i - 1].cessNonAdvol ? invoice.items[i - 1].cessNonAdvol.toFixed(2) : '0.00');
	                	$("#estatecess_text" + i).val(invoice.items[i - 1].stateCess ? invoice.items[i - 1].stateCess.toFixed(2) : '0.00');
	                	$("#ecessrate_text" + i).val(invoice.items[i - 1].cessrate);
	                	$("#ecessamount_text" + i).val(invoice.items[i - 1].cessamount ? invoice.items[i - 1].cessamount.toFixed(2) : '0.00');
	                	$("#etotal_text" + i).val(invoice.items[i - 1].total ? invoice.items[i - 1].total : '0.00' );
						if(invType == "Exports"){
							$("#ecurtotal_text" + i).val(invoice.items[i - 1].currencytotalAmount ? invoice.items[i - 1].currencytotalAmount : '0.00');
						}
						$("#eitemCustomField_text1" + i).val(invoice.items[i - 1].itemCustomField1 ? invoice.items[i - 1].itemCustomField1 : ' ');
                        $("#eitemCustomField_text2" + i).val(invoice.items[i - 1].itemCustomField2 ? invoice.items[i - 1].itemCustomField2 : ' ');
                        $("#eitemCustomField_text3" + i).val(invoice.items[i - 1].itemCustomField3 ? invoice.items[i - 1].itemCustomField3 : ' ');
                        $("#eitemCustomField_text4" + i).val(invoice.items[i - 1].itemCustomField4 ? invoice.items[i - 1].itemCustomField4 : ' ');
                        $('#eitemId_text'+i).val(invoice.items[i - 1].itemId ? invoice.items[i - 1].itemId : ' ');
	                }
               }
        }
        if(invoice.irnNo == null || invoice.irnNo == ""){
        	}else{
        		editFlag = true;
				$('.form-control,.che_form-control').addClass('disable');
				$('.einvedit,.custmail').removeClass('disable');
				$('#adderow').attr('disabled', true).css("background-color", "white");
				$('.gstr2adeletefield').css("display", "none");
        	}
		showEinvoicePopup(invType,varRetType,false,invoice);
	});
}
function loadETotals(varRetType, totalsData){
	$('#idCount'+varRetType).html(totalsData ? totalsData.totalTransactions : '0');
	$('#idTotAmtVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTaxableVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idExemptedVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalExemptedAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTaxVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idIGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalIGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idSGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalSGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idCESS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCESSAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTabTotal'+varRetType).html(varRetType == 'Purchase_Register' ? totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00' : totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
}

function geteInvColumns(id, clientId, varRetType, userType, month, year, printurlSuffix, booksOrReturns){
	var billedtoname = '';
	var varRetTypeCode = varRetType.replace(" ", "_");
				var chkBx = {data: function ( data, type, row ) {
      	 			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invFilter'+varRetTypeCode+data.userid+'" onClick="updateEinvSelection(\''+data.userid+'\', \''+varRetType+'\', \''+data.irnNo+'\',\''+billedtoname+'\',this)"/><i class="helper"></i></label></div>';
    			}};
	
				var irnNo = {data: function ( data, type, row ) {
					if(data.irnNo){
						return '<span class="text-left invoiceclk" data-toggle="tooltip" title="'+data.irnNo+'">'+data.irnNo.substring(0,20)+' <span>...</span></span>';
					}else{
						return '<span class="text-left invoiceclk"></span>';
					}
				}};
				
				var status = {data: function ( data, type, row ) {

					var statuss = '';
						var statusTooltip = '';	
							statuss = data.irnStatus;
							 if(statuss == null || statuss == ''){
								 statuss = 'Not Generated';
							}else if(statuss == 'Generated'){
								statuss = 'Generated';
							}else if(statuss == 'Cancelled'){
								statuss = 'Cancelled';
							}else{
								statusTooltip = statuss;
								statuss = 'Failed';	
							}
						
	var paymentStCls = (statuss == 'Generated') ? 'color-green' : (statuss == 'Cancelled' || statuss == 'Failed') ? 'color-red' : (statuss == 'Not Generated') ? 'color-yellow' : 'color-red';
				   	 
					 if(statuss == 'Generated' || statuss == 'Cancelled' || statuss == 'Not Generated'){
						 return '<span class="text-left invoiceclk '+paymentStCls+'">'+statuss+'</span>';
					 }else{
						return '<span class="'+paymentStCls+' text-left invoiceclk"  data-toggle="tooltip" title="'+statusTooltip+'">Failed</span><i class="fa fa-info-circle" style="font-size:17px;color:red" rel="tooltip" title="'+statusTooltip+'"></i>';
					 }
				}};
	
				var itype = {data: function ( data, type, row ) {
					     	if(data.revchargetype == 'Reverse'){
					     		return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+data.invtype+'</span><span><img data-toggle="tooltip" title="Reverse Charge Applied on this Invoice" src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/reversecharge.png" alt="reversecharge" style="height: 18px;margin-left: 10px;margin-bottom:3px"></span>';
					     	}else{
					     		return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+data.invtype+'</span>';
					     	}			              	 
					    }};
				var invsNo = {data:  function ( data, type, row ) {
					var invoiceno = '';
					if(data.invoiceno != undefined){
						invoiceno = data.invoiceno;
					}
			      	 return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+invoiceno+'</span>';
			    }};
				var billtoname = {data: function ( data, type, row ) {
						
						if(data.billedtoname != undefined){
							billedtoname = data.billedtoname;
						}
				      	 return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+billedtoname+'</span>';
				    }};
				var billtogtnn = {data: function ( data, type, row ) {
					var gstnnum= '';
					if(data.b2b && data.b2b.length > 0){
						if(data.b2b[0].ctin != undefined){
							gstnnum = data.b2b[0].ctin;
						}
					}
			      	 return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+gstnnum+'</span>';
			    }};
	
					var invDate = {data: function ( data, type, row ) {
				      	 return '<span class="text-left invoiceclk permissionInvoices-Sales-Edits">'+(new Date(data.dateofinvoice)).toLocaleDateString('en-GB')+'</span>';
				    }};
					var taxblamt = {data: function ( data, type, row ) {
					var totalTaxableAmt = 0.0;
					if(data.totaltaxableamount){
						totalTaxableAmt = data.totaltaxableamount;
					}
				   	 return '<span class="ind_formats text-right invoiceclk permissionInvoices-Sales-Edits">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
				    }};
					var totlTax = {data: function ( data, type, row ) {
					var totalTax = 0.0;
					if(data.totaltax){
						totalTax = data.totaltax;
					}
				   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk permissionInvoices-Sales-Edits">'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CESS :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCessAmount ? data.totalCessAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
				    }};
				var totalamt = {data: function ( data, type, row ) {
				var totalAmount = 0.0;
				if(data.totalamount){
					totalAmount = data.totalamount;
				}
			   	 return '<span class="ind_formats text-right invoiceclk permissionInvoices-Sales-Edits">'+formatNumber(totalAmount.toFixed(2))+'</span>';
			    }};
	
	
			return [chkBx ,irnNo, status, itype, invsNo,invDate,  billtogtnn,billtoname,taxblamt,  totlTax, totalamt,
			        {data: function ( data, type, row ) {
			     		 return '<a href="#" onclick="sendEmailToBuyers(\''+data.userid+'\',\''+data.invoiceno+'\',\''+data.billedtoname+'\',\''+varRetType+'\')" class="nottoedit" style="float: right;"> <i class="fa fa-envelope" style="font-size:15px;"></i></a><a href="'+_getContextPath()+'/einvprint/'+printurlSuffix+'/'+data.userid+'" target="_blank" style="float: right;" class="nottoedit"> <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;margin-right:5px;"></a><a href="#" class="permissionInvoices-Purchase-Edit" style="margin-right:0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>';
			            }} 
			        ];
		
	return [];
}


function geteInvColumnDefs(varRetType){
		return  [
					{
						"targets":  [8, 9, 10],
						className: 'dt-body-right'
					},
					{
						"targets": 0,
						"orderable": false
					}
				];
		
	return [];
}

function einitiateCallBacksForMultiSelect(varRetType, varRetTypeCode){
	var multiSelDefaultVals = ['', '- IRN Status -','- Invoice Type -', '', '- Reverse Charge -','- Branches -', '- Verticals -',''];
	for(i=1;i<11;i++){
		if(i == 3 || i == 7 || i == 8 || i == 9 || i == 10){
			continue;
		}
		emultiselrefresh('#emultiselect'+varRetTypeCode+i, multiSelDefaultVals[i], varRetType);
	}
}

function emultiselrefresh(idval, descVal, varRetType){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyEInvFilters(varRetType);
		},
		onSelectAll: function(){
			applyEInvFilters(varRetType);
		},
		onDeselectAll: function(){
			applyEInvFilters(varRetType);
		}
	});
}

function updateBankDefaults(){
	$.ajax({
		url: contextPath+'/bnkdtls'+urlSuffixs,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(bankDetails) {
			clientBankDetails = new Array();
			$('#eselectBank').html('').hide();
			$("#eselectBank").append($("<option></option>").attr("value","").text("-- Select Bank --")); 
			for (var i=0; i<bankDetails.length; i++) {
				$("#eselectBank").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname));
				clientBankDetails.push(bankDetails[i]);
			}
			if(clientBankDetails && clientBankDetails.length == 1) {
					$("#ebank_details").show();
				$(".eaddBankDetails").attr("checked","checked");
				$('#eselectBank').val(clientBankDetails[0].accountnumber);
				eselectBankName(clientBankDetails[0].accountnumber);
			}else if(clientBankDetails && clientBankDetails.length > 1){
				$('#eselectBank').attr("disabled","disabled");
			}
			$("#eselectBank").show();
		}
	});
}
function eupdateBankDetails(type,bacctno,bname) {
	$.ajax({
		url: contextPath+'/bnkdtls'+urlSuffixs,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(bankDetails) {
			$('#eselectBank').children('option').remove();
			$("#eselectBank").append($("<option></option>").attr("value","").text("-- Select Bank --")); 
			clientBankDetails = new Array();
			for (var i=0; i<bankDetails.length; i++) {
				$("#eselectBank").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname)); 
				clientBankDetails.push(bankDetails[i]);
			}
			$("#eselectBank").show();
			$('.eselectBank').val(bacctno);
			if(clientBankDetails && clientBankDetails.length == 1){
				$('#eselectBank').attr("disabled","disabled");
			}
		}
	});
}
function eselectBankName(bnknumber) {
	//var bankaccountNumber = $('#selectBank').val();
	$('#eebankName').val(' ');
	$('#ebankAcctNo').val(' ');
	$('#ebankBranch').val(' ');
	$('#ebankIFSC').val(' ');
	$('#ebankAccountName,#ebankMode,#ebankCreditDays,#ebankCreditTrans,#ebankDirectDebit,#ebankBal,#ebankDueDate').val(' ');
	if(bnknumber) {
		clientBankDetails.forEach(function(bankdetail) {
			if(bankdetail.accountnumber == bnknumber) {
				$('#ebankName').val(bankdetail.bankname);
				$('#ebankAcctNo').val(bankdetail.accountnumber);
				$('#ebankBranch').val(bankdetail.branchname);
				$('#ebankIFSC').val(bankdetail.ifsccode);
				$('#ebankAccountName').val(bankdetail.accountName);
				$('#ebankMode').val(bankdetail.modeOfPayment);
				$('#ebankCreditDays').val(bankdetail.creditDays);
				$('#ebankCreditTrans').val(bankdetail.creditTransfer);
				$('#ebankDirectDebit').val(bankdetail.directDebit);
				$('#ebankBal').val(bankdetail.balAmtToBePaid);
				$('#ebankDueDate').val(bankdetail.dueDate);
			}
		});
	}else if($('#eselectBank').val() !=''){
		var bankaccountNumber = $('#eselectBank').val();
		var bankaccountNumber1 = $('#selectBank1').val();
		if(bankaccountNumber){
		clientBankDetails.forEach(function(bankdetail) {
			if(bankdetail.accountnumber == bankaccountNumber) {
				$('#ebankName').val(bankdetail.bankname);
				$('#ebankAcctNo').val(bankdetail.accountnumber);
				$('#ebankBranch').val(bankdetail.branchname);
				$('#ebankIFSC').val(bankdetail.ifsccode);
				$('#ebankAccountName').val(bankdetail.accountName);
				$('#ebankMode').val(bankdetail.modeOfPayment);
				$('#ebankCreditDays').val(bankdetail.creditDays);
				$('#ebankCreditTrans').val(bankdetail.creditTransfer);
				$('#ebankDirectDebit').val(bankdetail.directDebit);
				$('#ebankBal').val(bankdetail.balAmtToBePaid);
				$('#ebankDueDate').val(bankdetail.dueDate);
			}
		});
	}
	
	}
}
function changeBankDetails(){
	if($('.eaddBankDetails').is(":checked")){
		$("#ebank_details").show();
		if(clientBankDetails && clientBankDetails.length == 1) {
			$('#eselectBank').val(clientBankDetails[0].accountnumber);
			selectBankName(clientBankDetails[0].accountnumber);
		}else if(clientBankDetails && clientBankDetails.length > 1){
			$('#eselectBank').removeAttr("disabled");
		}
	}else{
		$("#ebank_details").hide();
		$('#eselectBank').val("");
		$('#ebankName').val("");
		$('#ebankAcctNo').val("");
		$('#ebankAccountName').val("");
		$('#ebankBranch').val("");
		$('#ebankIFSC,#ebankMode,#ebankCreditDays,#ebankCreditTrans,#ebankDirectDebit,#ebankBal,#ebankDueDate').val("");
		$('#eselectBank').attr("disabled","disabled");
	}
}
function samedispatchaddresscheck() {
	if($('#samedispatchaddress').is(":checked")){
		$('#samedispatchaddress').val("1");
		var txt = $('#einvbuyerAddr').html();
		$('#einvshipmentAddr').html(txt);
		$('#shimentAddrEdit').addClass('d-none');
		$('#shipmentDtls_gstin').val($('#buyerDtls_gstin').val());
	   $('#shipmentDtls_lglNm').val($('#buyerDtls_lglNm').val());
	   $('#shipmentDtls_trdNm').val($('#buyerDtls_lglNm').val());
		$('#shipmentDtls_addr1').val($('#buyerDtls_addr1').val());
		$('#shipmentDtls_addr2').val($('#buyerDtls_addr2').val());
		$('#shipmentDtls_loc').val($('#buyerDtls_loc').val());
		$('#shipmentDtls_pin').val($('#buyerDtls_pin').val());	
		$('#shipmentDtls_stcd').val($('#buyerDtls_state').val());
		}else{
			$('#samedispatchaddress').val("0");
			$('#shipmentGstno').val('');
			   $('#shipmentName').val('');
			   $('#shipmentTradeName').val('');
				$('#shipmentAddress1').val('');
				$('#shipmentAddress2').val('');
				$('#shipmentloc').val('');
				$('#shipmentPincode').val('');	
				$('#shipmentStatecode').val('');
				$('#shipmentDtls_gstin').val('');
				$('#shipmentDtls_trdNm').val('');
				$('#shipmentDtls_lglNm').val('');
				$('#shipmentDtls_addr1').val('');
				$('#shipmentDtls_addr2').val('');
				$('#shipmentDtls_loc').val('');
				$('#shipmentDtls_pin').val('');
				$('#shipmentDtls_stcd').val('');
			$('#einvshipmentAddr').html('');
			$('#shippingAddress').val('');
			$('#shimentAddrEdit').removeClass('d-none');
		}
} 

function saveEinvoiceAsDraft(retType){
	$('#einvDraft_Btn').addClass("btn-loader-blue");
	var err = 0;
	$('#einvoceform').find('input').each(function(){
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
	$('#einvoceform').find('select').each(function(){
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
	var ele=$('.form-group').is('.has-error');
	if(!ele){
		$('#eInvitemdetails').html("");
		if($('#buyerDtls_pin').val() == ""){
			$('#buyerDtls_pin').val(parseInt("0"));
		}
		$.each($("#all_einvoice tr td input.cessval"),function() {
			if ($(this).val()) {
				var dStr = ($(this).val()).toString();
				if(dStr.indexOf("%") > -1){
					dStr = dStr.replace("%","");
				}
				$(this).val(dStr);
			}
		});
	$.ajax({
		url : contextPath+'/saveeinvoiceasdraft/'+retType+"/"+varUserType+"/"+month+"/"+year,
		type: "POST",
		data: $("#einvoceform").serialize(),
		success : function(response) {
			
			window.location.href = contextPath+'/einvoice'+commonSuffix+'/EINVOICE/'+clientId+'/'+month+'/'+year;
			$('#einvDraft_Btn').removeClass("btn-loader-blue");
		}, 	error:function(data){
			$('#einvDraft_Btn').removeClass("btn-loader-blue");
    	}
		
	});
}else{
	$('#eInvitemdetails').html("Please fill All Details/You are missing mandatory fields, please enter the red-colour high lighted fields");
	$('#einvDraft_Btn').removeClass("btn-loader-blue");	
}

if (err != 0) {
  $('#einvDraft_Btn').removeClass("btn-loader-blue");	
  return false;
}
}


function findEinvTaxableValue(no) {
	var q = document.getElementById('eqty_text'+no);
	var r = document.getElementById('erate_text'+no);
	var d = document.getElementById('ediscount_text'+no);
	var tx = document.getElementById('etaxableamount_text'+no);
	var at = document.getElementById('eassamt_text'+no);
	var oc = document.getElementById('othrcharge_text'+no);
	if(q && r){
	if(q.value && r.value && tx) {
		var val = parseFloat(q.value)*parseFloat(r.value);
		if(d != null){
			if(d.value <= val){
				if(d.value) {
					val -= d.value;
				}
			}else{
				d.value = Number(0).toFixed(2);
			}
		}
		
		if(val <= 0){
			tx.value = Number(0).toFixed(2);
		}else{
			tx.value = val.toFixed(2);
		}
		
	}
}
	if(r && q){
	if(r.value == '' || q.value == ''){
		tx.value = Number(0).toFixed(2);
	}
	}

	findEinvTaxAmount(no);
	findEinvCessAmount(no);
	//findEinvAssAmount(no);
}
/*function findEinvAssAmount(no){
	var tx = document.getElementById('etaxableamount_text'+no);
	var at = document.getElementById('eassamt_text'+no);
	var oc = document.getElementById('othrcharge_text'+no);
	var d = document.getElementById('ediscount_text'+no);
	var assval=0;
	
	
		if(tx && tx.value){
			assval = tx.value;
		}
	
	document.getElementById('eassamt_text'+no).value = assval;
	findEinvTotal(no);
}*/

function findEinvTaxAmount(no,editType) {
	var revtype = $("#einv_revchargetype>option:selected").val();
	var rtype=$('#retType').val();
	var billname = $('#billedtoname').val();
	var gstno = $('#billedtogstin').val();
	var itype = $("#idInvType").val();
	var invType = $('#invCategory').val();
	var exportType = $("#exportType option:selected").val();
	var t = document.getElementById('etaxableamount_text'+no);
	var oc = document.getElementById('othrcharge_text'+no);
	//einterStateFlag = false;
//updateEinvRateTypes(clientStatename, 'GSTR1');
var invType = $('#invCategory').val();
		var eInvType = $('#idEInvType').val();
		var igstOnIntra = $('#igstOnIntra').val();
		if(invType == 'SEZWP' || invType == 'EXPWP') {
			einterStateFlag=false;
		}else if(invType == 'R'){
			einterStateFlag=true;
		}
		if(eInvType != 'Exports' && igstOnIntra == 'Y'){
			einterStateFlag=false;
		}
	var r = document.getElementById('etaxrate_text'+no);
		if(r){
			if(invType == 'SEZWOP' || invType == 'EXPWOP'){
				$('#etaxrate_text'+no).val(0);
				$('#etaxrate_text'+no).attr("readonly","readonly");	
			}else{
				$('#etaxrate_text'+no).removeAttr("readonly","readonly");
			}
		}
	
	if(t && t.value && r && r.value) {
		var taxableValue = parseFloat(t.value);
		var val = 0;
		if($('#einvincludetax').is(":checked")) {
			$('#einvincludetax').val('Yes');
			var q = document.getElementById('eqty_text'+no);
			var rt = document.getElementById('erate_text'+no);
			var d = document.getElementById('ediscount_text'+no);
			if(q && rt){
			if(q.value && rt.value) {
				taxableValue = parseFloat(q.value)*parseFloat(rt.value);
				if(d && d.value){
					if(d.value == ''){
						d.value = 0;
					}
					if(d.value <= taxableValue) {
						taxableValue -= d.value;
					}
					}
			}
			}
			var cs = document.getElementById('ecessrate_text'+no);
			if(cs.value == '') {
				cs.value = 0;
			}
			//val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(cs.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
			val = ( (parseFloat(taxableValue)/( 100+parseFloat(r.value)+parseFloat(cs.value)) * (parseFloat(r.value)+parseFloat(cs.value))));
			
		} else {
			$('#einvincludetax').val('No');
			val = ((taxableValue*parseFloat(r.value))/100);
		}
		if($('#einvincludetax').is(":checked")) {
			$('#einvincludetax').val('Yes');
			var txval = val;
			var cs = document.getElementById('ecessamount_text'+no);
			if(cs.value) {
				txval += parseFloat(cs.value);
			}
			document.getElementById('etaxableamount_text'+no).value = ((taxableValue-txval).toFixed(2))/1;
			//document.getElementById('eassamt_text'+no).value = (taxableValue-d.value) + oc.value;
		}

		if(val > 0) {
			if(einterStateFlag) {
				if($('#ediffPercent').is(":checked")) {
					
						document.getElementById('eigsttax_text'+no).value = Number(0).toFixed(2);
						document.getElementById('ecgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
						document.getElementById('esgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
						var igst21 = document.getElementById('eigsttax_text'+no).value;
						var cgst21 = document.getElementById('ecgsttax_text'+no).value;
						var sgst21 = document.getElementById('esgsttax_text'+no).value;
						var tot21;
						tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
						document.getElementById('eabb'+no).value = tot21.toFixed(2);
					
				}else{
					
						document.getElementById('eigsttax_text'+no).value = Number(0).toFixed(2);
						document.getElementById('ecgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
						document.getElementById('esgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
						var igst21 = document.getElementById('eigsttax_text'+no).value;
						var cgst21 = document.getElementById('ecgsttax_text'+no).value;
						var sgst21 = document.getElementById('esgsttax_text'+no).value;
						var tot21;
						tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
						document.getElementById('eabb'+no).value = tot21.toFixed(2);
				
				}
			} else {
				if($('#ediffPercent').is(":checked")) {
					
						document.getElementById('eigsttax_text'+no).value = ((val)*0.65).toFixed(2);
						document.getElementById('ecgsttax_text'+no).value =Number(0).toFixed(2);
						document.getElementById('esgsttax_text'+no).value = Number(0).toFixed(2);
						var igst21 = document.getElementById('eigsttax_text'+no).value;
						var cgst21 = document.getElementById('ecgsttax_text'+no).value;
						var sgst21 = document.getElementById('esgsttax_text'+no).value;
						var tot21;
						tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
						document.getElementById('eabb'+no).value = tot21.toFixed(2);
					
					}else{
						
							document.getElementById('eigsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
							document.getElementById('ecgsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('esgsttax_text'+no).value = Number(0).toFixed(2);
							var igst21 = document.getElementById('eigsttax_text'+no).value;
							var cgst21 = document.getElementById('ecgsttax_text'+no).value;
							var sgst21 = document.getElementById('esgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('eabb'+no).value = tot21.toFixed(2);
					}
			}
		} else {
			document.getElementById('eigsttax_text'+no).value = Number(0).toFixed(2);
			document.getElementById('ecgsttax_text'+no).value = Number(0).toFixed(2);
			document.getElementById('esgsttax_text'+no).value = Number(0).toFixed(2);
			var igst21 = document.getElementById('eigsttax_text'+no).value;
			var cgst21 = document.getElementById('ecgsttax_text'+no).value;
			var sgst21 = document.getElementById('esgsttax_text'+no).value;
			var tot21;
			tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
			document.getElementById('eabb'+no).value = tot21.toFixed(2);
		}
		
		findEinvTotal(no,editType);
	}

}

function findEinvTotal(no,editType) {
	var revtype = $("#einv_revchargetype>option:selected").val();
	var itype = $("#idEInvType").val();
	var t;
	
		t = document.getElementById('etaxableamount_text'+no);
	
	var totalValue = 0;
	if(t && t.value) {
		totalValue = parseFloat(t.value);
	}
	var cessamt = document.getElementById('ecessamount_text'+no);
	if(cessamt && cessamt.value) {
		totalValue += parseFloat(cessamt.value);
	}
	var samt = document.getElementById('esgsttax_text'+no);
	if(samt && samt.value) {
		totalValue += parseFloat(samt.value);
	}
	var iamt = document.getElementById('eigsttax_text'+no);
	if(iamt && iamt.value) {
		totalValue += parseFloat(iamt.value);
	}
	var camt = document.getElementById('ecgsttax_text'+no);
	if(camt && camt.value) {
		totalValue += parseFloat(camt.value);
	}
	var stateCess = document.getElementById('estatecess_text'+no);
	if(stateCess && stateCess.value) {
		totalValue += parseFloat(stateCess.value);
	}
	var cessNonAdvol = document.getElementById('ecessnonadv_text'+no);
	if(cessNonAdvol && cessNonAdvol.value) {
		totalValue += parseFloat(cessNonAdvol.value);
	}
	var exempted = document.getElementById('eexempted_text'+no);
	var qty = document.getElementById('eqty_text'+no);
	if(exempted && exempted.value && qty && qty.value) {
		totalValue += (parseFloat(exempted.value))*(parseFloat(qty.value));
	}
	var oc = document.getElementById('othrcharge_text'+no);
	if(oc && oc.value) {
		totalValue += parseFloat(oc.value);
	}
	if(revtype == "Y"){
		if(t && t.value) {
			var taxbleval = 0;
			taxbleval = parseFloat(t.value);
			if(oc && oc.value) {
				taxbleval += parseFloat(oc.value);
			}
		  document.getElementById('etotal_text'+no).value = taxbleval.toFixed(2);
		  if(itype == 'Exports'){
				var excrate = document.getElementById('exchangeRate');
				if(excrate){
					var extotalvalue = 0;
					if(excrate.value && excrate.value > 0){
						extotalvalue = parseFloat(t.value)/parseFloat(excrate.value);
						document.getElementById('ecurtotal_text'+no).value = extotalvalue.toFixed(2);
					}else{
						extotalvalue = 0;
						document.getElementById('ecurtotal_text'+no).value = extotalvalue.toFixed(2);
					}
				}
			}
		}
	}else{
		if(totalValue) {
			document.getElementById('etotal_text'+no).value = totalValue.toFixed(2);
			if(itype == 'Exports'){
				var excrate = document.getElementById('exchangeRate');
				if(excrate){
					var extotalvalue = 0;
					if(excrate.value && excrate.value > 0){
						extotalvalue = parseFloat(totalValue)/parseFloat(excrate.value);
						document.getElementById('ecurtotal_text'+no).value = extotalvalue.toFixed(2);
					}else{
						extotalvalue = 0;
						document.getElementById('ecurtotal_text'+no).value = extotalvalue.toFixed(2);
					}
				}
			}
		}
	}
	updateEinvTotals(no,editType);
}

function updateEinvTotals(no,editType) {
	var revtype = $("#einv_revchargetype>option:selected").val();
	var table=document.getElementById("einvoice_table");
	var itype = $("#idEInvType").val();
	var iRows=einvRowCount+1;
	var totTaxable = 0, totIGST = 0, totCGST = 0, totSGST = 0, totCESS = 0, totTotal = 0,totAss = 0,totStateCess = 0,totCessNonAdv = 0,totCurTotal = 0;
	for(var i=1;i<iRows;i++) {
		if(i == no) {
			var totalTxbl , total , totalIGST , totalCGST , totalSGST, totalAss, totalStateCess, totalCessNonAdv,totalCurencyAmount;
				totalTxbl = document.getElementById('etaxableamount_text'+i);
				totalAss = document.getElementById('eassamt_text'+i);
				totalStateCess = document.getElementById('estatecess_text'+i);
				totalCessNonAdv = document.getElementById('ecessnonadv_text'+i);
				totalCurencyAmount= document.getElementById('ecurtotal_text'+i);
				
			if(totalTxbl && totalTxbl.value > 0) {
				totTaxable+=parseFloat(totalTxbl.value);
			}
			if(totalAss && totalAss.value > 0) {
				totAss+=parseFloat(totalAss.value);
			}else{
				if(totalTxbl && totalTxbl.value > 0) {
					totAss+=parseFloat(totalTxbl.value);
				}
			}
			if(totalStateCess && totalStateCess.value){
				totStateCess += parseFloat(totalStateCess.value);
			}
			if(totalCessNonAdv && totalCessNonAdv.value){
				totCessNonAdv+=parseFloat(totalCessNonAdv.value);
			}
				var totalIGST = document.getElementById('eigsttax_text'+i);
				var totalCGST = document.getElementById('ecgsttax_text'+i);
				var totalSGST = document.getElementById('esgsttax_text'+i);
				if(totalIGST && totalIGST.value) {
					totIGST+=parseFloat(totalIGST.value);
					totIGST+=parseFloat(totalCGST.value);
					totIGST+=parseFloat(totalSGST.value);
				}
			var totalCESS = document.getElementById('ecessamount_text'+i);
			if(totalCESS && totalCESS.value){
			if(totalCESS.value) {
				totCESS+=parseFloat(totalCESS.value);
			}
			}
			total = document.getElementById('etotal_text'+i);
			if(total && total.value) {
				totTotal+=parseFloat(total.value);
			}
			if(totalCurencyAmount && totalCurencyAmount.value) {
				totCurTotal+=parseFloat(totalCurencyAmount.value);
			}
		} else {
			var totalTxbl , total , totalIGST , totalCGST , totalSGST, totalAss, totalStateCess, totalCessNonAdv,totalCurencyAmount;

				totalTxbl = document.getElementById('etaxableamount_text'+i);
				totalAss = document.getElementById('eassamt_text'+i);
				totalStateCess = document.getElementById('estatecess_text'+i);
				totalCessNonAdv = document.getElementById('ecessnonadv_text'+i);
				totalCurencyAmount= document.getElementById('ecurtotal_text'+i);
			if(totalTxbl && totalTxbl.value) {
				totTaxable+=parseFloat(totalTxbl.value);
			}
			if(totalAss && totalAss.value) {
				totAss+=parseFloat(totalAss.value);
			}
			if(totalStateCess && totalStateCess.value){
				totStateCess+=parseFloat(totalStateCess.value);
			}
			if(totalCessNonAdv && totalCessNonAdv.value){
				totCessNonAdv+=parseFloat(totalCessNonAdv.value);
			}
			totalIGST = document.getElementById('eigsttax_text'+i);
			if(totalIGST && totalIGST.value) {
				totIGST+=parseFloat(totalIGST.value);
			}
			totalCGST = document.getElementById('ecgsttax_text'+i);
			if(totalCGST && totalCGST.value) {
				totIGST+=parseFloat(totalCGST.value);
			}
			totalSGST = document.getElementById('esgsttax_text'+i);
			if(totalSGST && totalSGST.value) {
				totIGST+=parseFloat(totalSGST.value);
			}
			var totalCESS = document.getElementById('ecessamount_text'+i);
			if(totalCESS && totalCESS.value){
			if(totalCESS.value) {
				totCESS+=parseFloat(totalCESS.value);
			}
			}
			total = document.getElementById('etotal_text'+i);
			if(total && total.value) {
				totTotal+=parseFloat(total.value);
			}
			if(totalCurencyAmount && totalCurencyAmount.value) {
				totCurTotal+=parseFloat(totalCurencyAmount.value);
			}
		}
	}
	$('#etotTaxable').html(parseFloat(totTaxable));
	//$('#totAsseAmt').html(parseFloat(totAss));
	$('#etotStateCess').html(parseFloat(totStateCess));
	$('#totCessAdvol').html(parseFloat(totCessNonAdv));
	$('#etotIGST').html(parseFloat(totIGST));
	$('#etotCGST').html(parseFloat(totCGST));
	$('#etotSGST').html(parseFloat(totSGST));
	$('#etotCESS').html(parseFloat(totCESS));
	$('#etotTotal').html(parseFloat(totTotal));
	$('#totcurAmt').html(parseFloat(totCurTotal));
	if($('#eroundOffTotalChckbox').is(':checked')) {
		$('#eidTotal').html(Math.round(parseFloat(totTotal)).toFixed(2));
		$('#eroundOffTotalValue').val((Math.round(totTotal)-totTotal).toFixed(2));
	}else if(!$('#eroundOffTotalChckbox').is(':checked')){
		$('#eidTotal').html(parseFloat(totTotal));
		$('#eroundOffTotalValue').val('');
	}
	$('#ehiddenroundOffTotalValue').val(parseFloat(totTotal));
	$('#cdn_taxableamount').val(parseFloat(totTaxable));
	$(".indformat").each(function(){
		    $(this).html($(this).html().replace(/,/g , ''));
		});
	OSREC.CurrencyFormatter.formatAll({
		selector: '.indformat'
	});
	if(editType != "edit"){
		etdstcscal();
	}
}

function findEinvCessAmount(no) {
	var t = document.getElementById('etaxableamount_text'+no);
	var r = document.getElementById('ecessrate_text'+no);
	var invType = $('#idInvType').val();
	var quantity = document.getElementById('eqty_text'+no);
	var cesstype = $('input[class="cessType"]:checked').val();
	if(t && t.value && r.value) {
		if(quantity && quantity.value){
			var qty = parseFloat(quantity.value);
		}
		var taxableValue = parseFloat(t.value);
		var val = 0;
		if($('#eincludetax').is(":checked")) {
			$('#eincludetax').val('Yes');
			var q = document.getElementById('eqty_text'+no);
			var rt = document.getElementById('erate_text'+no);
			var d = document.getElementById('ediscount_text'+no);
			if(q && rt){
				if(q.value && rt.value) {
					taxableValue = parseFloat(q.value)*parseFloat(rt.value);
			
					if(d.value == ''){
						d.value = 0;
					}
					if(d.value <= taxableValue) {
						taxableValue -= d.value;
					}
				}
			}
			var tx = document.getElementById('etaxrate_text'+no);
			if(tx.value == '') {
				tx.value = 0;
			}
			val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(tx.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
		} else {
			$('#eincludetax').val('No');
			if(cesstype == "Taxable Value"){
				val = ((taxableValue*parseFloat(r.value))/100);
			}else{
				if(qty){
					val = (qty*parseFloat(r.value));
				}
			}
		}
		if(val > 0) {
			val = ((val.toFixed(2))/1).toFixed(2);
		} else {
			val = Number(0).toFixed(2);
		}
		document.getElementById('ecessamount_text'+no).value = val;
		if($('#eincludetax').is(":checked")) {
			$('#eincludetax').val('Yes');
			findEinvTaxAmount(no);
		} else {
			$('#eincludetax').val('No');
			findEinvTotal(no);
		}
	}
	updateEinvTotals(no);
}
function einvokegstnPublicAPI(btn,type) {
	dealertype = "";
	var invType = $('#invTyp').val();
	var gstnno = "";
	if(type == 'invoice'){
		gstnno = $("#ebilledtogstin").val();
	}else if(type == 'dispatch'){
		gstnno = $("#dispatchGstno").val();
	}else if(type == 'shipment'){
		gstnno = $("#shipmentGstno").val();
	}else if(type == 'buyer'){
		gstnno = $("#buyerGstno").val();
	}
	$('#eigstnnumber_Msg').text('');
	var userid = $('#userid').val();
	eupdatePan(gstnno,type);
		if(gstnno != '') {
			$(btn).addClass('btn-loader');
			var gstnumber = gstnno.toUpperCase();
			$.ajax({
				url: contextPath+"/publicsearch?gstin="+gstnumber+"&userid="+userid,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response.error && response.error.message) {
						if(response.error.message == 'SWEB_9035'){$('#eigstnnumber_Msg').text("No Records Found");	
					  	} else{
					  		$('#eigstnnumber_Msg').text(response.error.message);
					  	}
					} else if(response.status_cd == '0') {
						$('#eigstnnumber_Msg').text(response.status_desc);
					}
					if(response.status_cd == '1') {
						if(response.data) {
							var address = "";
							if(type == 'buyer' || type == 'invoice'){
								$('#buyerDtls_gstin').val(gstnno);
							}
							Object.keys(response.data).forEach(function(key) {
								if(key == 'dty'){
									dealertype = response.data['dty'];
									$('#dealerType').val(response.data['dty']);
								}
								if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
									if(type == 'buyer' || type == 'invoice'){
										$('#buyerDtls_lglNm').val(response.data['lgnm']);
									}
									$('#'+type+'TradeName').val(response.data['lgnm']);
								}else{
									$('#'+type+'TradeName').val(response.data['tradeNam']);
									if(type == 'buyer' || type == 'invoice'){
										$('#buyerDtls_lglNm').val(response.data['tradeNam']);
									}
								}
							if(key == 'pradr'){
							Object.keys(response.data['pradr']['addr']).forEach(function(key){
								if(response.data['pradr']['addr'][key] != ''){
									
									if(key != 'pncd' && key != 'stcd'){
										address = address.concat(response.data['pradr']['addr'][key]+",");
										if(type == 'buyer' || type == 'invoice'){
											$('#buyerDtls_addr1').val(address);
										}
									}
									if(key == 'pncd'){
										$('#'+type+'Pincode').val(response.data['pradr']['addr'][key]);
										if(type == 'buyer' || type == 'invoice'){
											$('#buyerDtls_pin').val(parseInt(response.data['pradr']['addr'][key]));
										}
									}
									/*if(key == 'stcd'){
										$('#'+type+'Statecode').val(response.data['pradr']['addr'][key]);
										if(type == 'buyer' || type == 'invoice'){
											$('#buyerDtls_state').val(response.data['pradr']['addr'][key]);
										}
									}*/
									if(key == 'bno'){
										$('#'+type+'Bno').val(response.data['pradr']['addr'][key]);
									}
									if(key == 'bnm'){
										$('#'+type+'Bname').val(response.data['pradr']['addr'][key]);
									}
									if(key == 'dst'){
										$('#'+type+'District').val(response.data['pradr']['addr'][key]);
									}
									if(key == 'flno'){
										$('#'+type+'FloorNo').val(response.data['pradr']['addr'][key]);
									}
									if(key == 'loc'){
										$('#'+type+'loc').val(response.data['pradr']['addr'][key]);
										if(type == 'buyer' || type == 'invoice'){
											$('#buyerDtls_loc').val(response.data['pradr']['addr'][key]);
										}
									}
									}
							});
						}
						});
						//$('#billingAddress').val(address.slice(0,-1));
							if(type == 'buyer' || type == 'invoice'){
								if($('#samedispatchaddress').is(":checked")){
										$('#shipmentDtls_gstin').val($('#buyerDtls_gstin').val());
									   	$('#shipmentDtls_lglNm').val($('#buyerDtls_lglNm').val());
									   	$('#shipmentDtls_trdNm').val($('#buyerDtls_lglNm').val());
										$('#shipmentDtls_addr1').val($('#buyerDtls_addr1').val());
										$('#shipmentDtls_addr2').val($('#buyerDtls_addr2').val());
										$('#shipmentDtls_loc').val($('#buyerDtls_loc').val());
										$('#shipmentDtls_pin').val($('#buyerDtls_pin').val());	
										$('#shipmentDtls_stcd').val($('#buyerDtls_state').val());
									var addr = $('#buyerDtls_addr1').val().substring(1,15)+"...";
									$('#einvbuyerAddr,#einvshipmentAddr').html($('#buyerDtls_gstin').val()+","+$('#buyerDtls_lglNm').val()+","+addr+","+$('#buyerDtls_loc').val()+","+$('#buyerDtls_state').val()+","+$('#buyerDtls_pin').val());
								}else{
									$('#shipmentDtls_gstin').val('');
								   	$('#shipmentDtls_lglNm').val('');
								   	$('#shipmentDtls_trdNm').val('');
									$('#shipmentDtls_addr1').val('');
									$('#shipmentDtls_addr2').val('');
									$('#shipmentDtls_loc').val('');
									$('#shipmentDtls_pin').val('');	
									$('#shipmentDtls_stcd').val('');
									var addr = $('#buyerDtls_addr1').val().substring(1,15)+"...";
									$('#einvbuyerAddr').html($('#buyerDtls_gstin').val()+","+$('#buyerDtls_lglNm').val()+","+addr+","+$('#buyerDtls_loc').val()+","+$('#buyerDtls_state').val()+","+$('#buyerDtls_pin').val());
								}
							}else{
								var addr = $('#buyerDtls_addr1').val().substring(1,15)+"...";
								$('#einvbuyerAddr').html($('#buyerDtls_gstin').val()+","+$('#buyerDtls_lglNm').val()+","+addr+","+$('#buyerDtls_loc').val()+","+$('#buyerDtls_state').val()+","+$('#buyerDtls_pin').val());
							}
							
						}
					}
					$(btn).removeClass('btn-loader');
				},
				error : function(e, status, error) {
					$(btn).removeClass('btn-loader');
				}
			});
		}
	}

function updateEinvSelection(id, retType, irn,billname, chkBox){
	if(chkBox.checked) {
		irnCancelArray.push(id);
	}else {
		var iArray=new Array();
		irnCancelArray.forEach(function(inv) {
			if(inv != id) {
				iArray.push(inv);
			}
		});
		irnCancelArray = iArray;
	}
	//$('#cancelIRN').attr('onclick','showEinvoiceCancelPopup("'+retType+'")');
	  if(irnCancelArray.length > 0 && billname != ""){
	    	$('#genIRN').removeClass('disabled');
	    }else{
	    	$('#genIRN').addClass('disabled');
	    }
	  if(irnCancelArray.length > 0){
	    	$('#genIRN,#deleteEInvoices,#cancelIRN').removeClass('disabled');
			$('#deleteEInvoices').attr('onclick','showEinvDeletePopup("'+retType+'","","selectedInvs")');
	    }else{
	    	$('#genIRN,#deleteEInvoices,#cancelIRN').addClass('disabled');
			$('#deleteEInvoices').removeAttr("onclick");
	    }
}
function updateAllEinvSelection(retType,filingstatus,chkBox){
	irnCancelArray=new Array();
	var check = $('#check'+retType.replace(" ", "_")).prop("checked");
    var rows = einvTable[retType.replace(" ", "_")].rows().nodes();
    if(check){
	    einvTable[retType.replace(" ", "_")].rows().every(function () {
		  	var row = this.data();
		  	irnCancelArray.push(row.userid);
		  	
	  });
    }
  
    if(irnCancelArray.length > 0){
    	$('#genIRN,#deleteEInvoices,#cancelIRN').removeClass('disabled');
		$('#deleteEInvoices').attr('onclick','showEinvDeletePopup("'+retType+'","","selectedInvs")');
    }else{
    	$('#genIRN,#deleteEInvoices,#cancelIRN').addClass('disabled');
		$('#deleteEInvoices').removeAttr("onclick");
    }
    
    $('input[type="checkbox"]', rows).prop('checked', check);
}
function eupdateCustomerName(value){
	$('#buyerDtls_lglNm').val(value);
	if($('#samedispatchaddress').is(":checked")){
		var addr = $('#buyerDtls_addr1').val().substring(1,15)+"...";
		$('#einvbuyerAddr,#einvshipmentAddr').html($('#buyerDtls_gstin').val()+","+$('#buyerDtls_lglNm').val()+","+addr+","+$('#buyerDtls_loc').val()+","+$('#buyerDtls_state').val()+","+$('#buyerDtls_pin').val());
	}else{
		var addr = $('#buyerDtls_addr1').val().substring(1,15)+"...";
		$('#einvbuyerAddr').html($('#buyerDtls_gstin').val()+","+$('#buyerDtls_lglNm').val()+","+addr+","+$('#buyerDtls_loc').val()+","+$('#buyerDtls_state').val()+","+$('#buyerDtls_pin').val());
	}
	
}
function eupdatePan(value,type) {
	$('#einvoiceserialno').trigger('change');
	dealertype = "";
	var rtype = $('#retType').val();var gstno = $('#ebilledtogstin').val();
		if(value.length == 15) {
			if(type == 'buyer' || type == 'invoice'){
				$('#buyerDtls_gstin').val(gstno.toUpperCase());
			}
			$.ajax({
				url: contextPath+"/srchstatecd?code="+value.substring(0,2),
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response) {
						if(response.name != ''){$('.eplaceofsupply').removeClass("has-error has-danger");$('.eplaceofsupply .with-errors').html('');$("#invoiceStatecodeempty").hide(); }
						$('#'+type+'Statecode').val(response.name);
						var pos = response.name.split("-");
						if(type == 'buyer' || type == 'invoice'){
							$('#buyerDtls_state').val(response.name);
							$('#'+type+'Statecode').val(response.name);
							$('#buyerDtls_pos').val(pos[0]);
							
						}
					}
				}
			});
		}
	
	updateEinvRateType(clientStatename, 'GSTR1','notedit');
}
function updateEinvRateType(clientState,returntype,editType){
	
	var invType = $('#idInvType').val();
	var billedState = document.getElementById('invoiceStatecode');
	
		if(billedState && clientState) {
			var stateCode = billedState.value;
			if(stateCode) {
				if(stateCode.length > 2) {
					$.ajax({
						url: contextPath+"/stateconfig?query="+stateCode,
						async: false,
						cache: false,
						dataType:"json",
						contentType: 'application/json',
						success : function(states) {
							if(states.length == 1) {
								stateCode = states[0].code;
							}
						}
					});
				}
				if(clientState.length > 2) {
					$.ajax({
						url: contextPath+"/stateconfig?query="+clientState,
						async: false,
						cache: false,
						dataType:"json",
						contentType: 'application/json',
						success : function(states) {
							if(states.length == 1) {
								clientState = states[0].code;
							}
						}
					});
				}
				if(stateCode == clientState) {
					einterStateFlag=true;
				} else {
					einterStateFlag=false;
				}
			}
			var i=1;
		$("#all_einvoice tr").each(function(index){
			findEinvTaxAmount(index+1,editType);
			index++;
		});
		}
}


function pupdateEinvRateType(clientState,returntype,billedState){
	
	var invType = $('#idInvType').val();
	//var billedState = document.getElementById('invoiceStatecode');
	
		if(billedState != "" && clientState) {
			var stateCode = billedState;
			if(stateCode) {
				if(stateCode.length > 2) {
					$.ajax({
						url: contextPath+"/stateconfig?query="+stateCode,
						async: false,
						cache: false,
						dataType:"json",
						contentType: 'application/json',
						success : function(states) {
							if(states.length == 1) {
								stateCode = states[0].code;
							}
						}
					});
				}
				if(clientState.length > 2) {
					$.ajax({
						url: contextPath+"/stateconfig?query="+clientState,
						async: false,
						cache: false,
						dataType:"json",
						contentType: 'application/json',
						success : function(states) {
							if(states.length == 1) {
								clientState = states[0].code;
							}
						}
					});
				}
				if(stateCode == clientState) {
					einterStateFlag=true;
				} else {
					einterStateFlag=false;
				}
			}
			var i=1;
		$("#all_einvoice tr").each(function(index){
			findEinvTaxAmount(index+1);
			index++;
		});
		}
}


function updateEinvRateTypes(clientState,returntype){
	var invType = $('#idInvType').val();
	var billedState = document.getElementById('invoiceStatecode');
	
		if(billedState && clientState) {
			var stateCode = billedState.value;
			if(stateCode) {
				if(stateCode.length > 2) {
					$.ajax({
						url: contextPath+"/stateconfig?query="+stateCode,
						async: false,
						cache: false,
						dataType:"json",
						contentType: 'application/json',
						success : function(states) {
							if(states.length == 1) {
								stateCode = states[0].code;
							}
						}
					});
				}
				if(clientState.length > 2) {
					$.ajax({
						url: contextPath+"/stateconfig?query="+clientState,
						async: false,
						cache: false,
						dataType:"json",
						contentType: 'application/json',
						success : function(states) {
							if(states.length == 1) {
								clientState = states[0].code;
							}
						}
					});
				}
				if(stateCode == clientState) {
					einterStateFlag=true;
				} else {
					einterStateFlag=false;
				}
			}
		}
}

	function generateIRN(retType){
		var custgstno = $('#buyerDtls_gstin').val();
		var custname = $('#buyerDtls_lglNm').val();
		var custpos = $('#buyerDtls_pos').val();
		var custaddr1 = $('#buyerDtls_addr1').val();
		var custlocation = $('#buyerDtls_loc').val();
		var custstate = $('#buyerDtls_state').val();
		var custpin = $('#buyerDtls_pin').val();
		$('#generateIRNBtn').addClass("btn-loader");
		var err = 0;
	$('#einvoceform').find('input').each(function(){
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
	$('#einvoceform').find('select').each(function(){
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
	var ele=$('.form-group').is('.has-error');
	if(!ele){
		var invtype = $('#idEInvType').val();
		var invCategory = $('#invCategory').val();
		if(invCategory == "EXPWP" || invCategory == "EXPWOP"){
			$('#buyerGstno_msg').text(""); 
		}
    if(custname == "" || custpos == "" || custaddr1 == "" || custlocation == "" || custstate == "" || custpin == ""){
				$('#eInvitemdetails').html("please Add customer Details to generate IRN");
				$('#generateIRNBtn').removeClass("btn-loader");
				err = 1;
	}else if(invtype == "B2B" && custgstno == ""){
				$('#eInvitemdetails').html("please Add customer GSTN No");
				$('#generateIRNBtn').removeClass("btn-loader");
				err = 1;
	}else if(invtype == "Credit/Debit Notes" && (invCategory == "DEXP" || invCategory == "B2B" || invCategory == "SEZWP" || invCategory == "SEZWOP") && custgstno == ""){
				$('#eInvitemdetails').html("please Add customer GSTN No");
				$('#generateIRNBtn').removeClass("btn-loader");
				err = 1;
	}else{
		$.each($("#all_einvoice tr td input.cessval"),function() {
			if ($(this).val()) {
				var dStr = ($(this).val()).toString();
				if(dStr.indexOf("%") > -1){
					dStr = dStr.replace("%","");
				}
				$(this).val(dStr);
			}
		});
		$('#eInvitemdetails').html("");
		$.ajax({
			url : contextPath+'/generateIRN/'+retType+"/"+varUserType+"/"+month+"/"+year,
			type: "POST",
			data: $("#einvoceform").serialize(),
			success : function(response) {
				if(response != ""){
					$('#eInvitemdetails').html(response);
				}else{
					window.location.href = contextPath+'/einvoice'+commonSuffix+'/EINVOICE/'+clientId+'/'+month+'/'+year;
				}
			$('#generateIRNBtn').removeClass("btn-loader");
			}, 	error:function(data){
				location.reload(true);
				$('#generateIRNBtn').removeClass("btn-loader");	
	    	}
			
		});
}
	}else{
		err = 1;
	$('#eInvitemdetails').html("Please fill All Details/You are missing mandatory fields, please enter the red-colour high lighted fields");
	$('#generateIRNBtn').removeClass("btn-loader");
}

if (err != 0) {
  $('#generateIRNBtn').removeClass("btn-loader");	
  return false;
}
		
	}

	function showEinvoiceCancelPopup(retType) {
		esubscriptioncheck();
		if(esubscriptionchecks == ""){
			if(einvAuthSttauss == "Active"){
				var inyType = $('#idInvType').val();$('#invoiceModal').css("z-index","1040");$('#cancelEinvoiceModal').modal('show');$('#btn_eCancel').attr('onclick', "cancelEInvoice('"+retType+"')");
			}else{
				einvErrorNotification(einvAuthSttauss);
			}
		}else{
			if(esubscriptionchecks == 'Your subscription has expired. Kindly subscribe to proceed further!') {
				if(varUserType == 'suvidha' || varUserType == 'enterprise'){
					errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
				}else{
					errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+_getContextPath()+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
				}
			}else {
				errorNotification(esubscriptionchecks);
			}
		}
	}

	function cancelEInvoice(retType) {
		var clientid = $('#clientid').val();
		var cancelcode = $('#ecancelcode').val();
		var cancelremark = $('#ecancelremark').val();

    	$('#btn_eCancel').addClass("btn-loader");
		var cancelIRNdata=new Object();
		//cancelIRNdata.Irn = irnno;
		cancelIRNdata.CnlRsn=cancelcode;
		cancelIRNdata.CnlRem=cancelremark;
		if(cancelcode != '' && cancelremark != ''){
			$.ajax({
				url: contextPath+"/cancelIRN/"+urlSuffixs+"/"+retType+"/"+month+"/"+year+"?ids="+irnCancelArray,
				type: "POST",
				data: JSON.stringify(cancelIRNdata),
				contentType: 'application/json',
				success : function(response) {
					if(response != ""){
						$('#ecancel_error').text(response);
					}else{
						location.reload(true);
					}
					$('#btn_eCancel').removeClass("btn-loader");
		
				}
			});
		}else{$('#btn_eCancel').removeClass("btn-loader");}

}
	
	function ecancelCodeSelection(no){
		var cancelremark = $('#ecancelremark').val();
		if(cancelremark == "Wrong Entry"){
			$('#ecancelcode').val("1");
		}else if(cancelremark == "Order Cancelled"){
			$('#ecancelcode').val("2");
		}else if(cancelremark == "Data Entry mistake"){
			$('#ecancelcode').val("3");
		}else if(cancelremark == "Others"){
			$('#ecancelcode').val("4");
		}
	}
	
	function addEinvoiceRow(retType){
		einvRowCount++;
		var table = null;
		var rowPrefix = null;var row = null;
		var table_len=einvRowCount;
		var einvoiceLevel_Cess = $('#einvLevelCess').val();
		var einvtype = $('#idEInvType').val();
		if(table_len == '1'){
		$('.eitem_delete').hide();
		}else{
		$('.eitem_delete').show();	
		}
		table = document.getElementById("einvoice_table");
		if(einvtype == "Exports"){
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='esno_row1'>"+table_len+"</span></td><td align='left' id='eproduct_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='eproduct_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='eremainproduct_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add remainddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateItemNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0' data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service' style='margin-right:8px;'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showEinvAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></td><td align='left' data ='' id='ehsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='ehsn_text"+table_len+"' name='items["+(table_len-1)+"].hsn' placeholder='HSN/SAC' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='eitemcodeempty' style='display:none' class='"+retType+"ddbox3"+table_len+"'><div class='"+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='left' id='euqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' id='euqc_text"+table_len+"' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' placeholder='UQC' value='' required><div id='uqc_textempty' style='display:none' class='remain"+retType+"ddbox4"+table_len+"'><div class='remain"+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='left' id='ebarcode_row"+table_len+"' class='form-group ebarcode_row"+table_len+"'><input type='text' class='form-control' id='ebarcode_text"+table_len+"' name='items["+(table_len-1)+"].barCode' placeholder='Bar Code' value=''></td><td align='right' id='eqty_row"+table_len+"' class='form-group'><input type='hidden' id='eopening_stock"+table_len+"'/><input type='hidden' id='esaftey_stock"+table_len+"'/><input type='text' onchange='changeeStock("+table_len+")' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' id='eqty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'  value='' onKeyUp='findEinvTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Qty' required/></td><td align='left' id='efreeqty_row"+table_len+"' class='form-group efreeqty_row"+table_len+"'><input type='text' class='form-control text-right' id='efreeqty_text"+table_len+"' name='items["+(table_len-1)+"].freeQty' placeholder='FreeQty' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' value=''></td><td align='right' id='erate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='erate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' value='' required onKeyUp='findEinvTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate'/></td><td align='right' id='ediscount_row"+table_len+"'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control disval text-right' id='ediscount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findEinvTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='Discount' /></td><td align='left' id='othrcharge_row"+table_len+"' class='form-group othrcharge_row"+table_len+"'><input type='text' class='form-control othercharge text-right' id='othrcharge_text"+table_len+"' name='items["+(table_len-1)+"].othrCharges' placeholder='Other Charge' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' onKeyUp='findEinvTaxableValue("+table_len+")' value=''></td><td align='right' id='etaxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='etaxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td><td align='right' id='etaxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='etaxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findEinvTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ettax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='eabb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='etax_rate_drop"+table_len+" pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3'><p class='mr-3'>IGST <span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='eigsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='ecgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='esgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='ecessrate_row"+table_len+"' class='form-group ecessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='ecessrate_text"+table_len+"' class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findEinvCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places' value='' /></td><td align='right' id='ecessamount_row"+table_len+"'  class='tablegreybg ecessFlag'><input type='text'  class='form-control text-right cessamtval' id='ecessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' value='' readonly></td><td align='right' id='etotal_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control text-right' id='etotal_text"+table_len+"' name='items["+(table_len-1)+"].total' value='' readonly></td><td align='right' id='eassamt_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control' id='ecurtotal_text"+table_len+"' name='items["+(table_len-1)+"].currencytotalAmount' value='' readonly></td><td align='center' width='2%'><a href='javascript:void(0)' class='eitem_delete' onclick='delete_erow("+table_len+")'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td><td class='d-none'><input type='hidden' id='eitemCustomField_text1"+table_len+"' name='items["+(table_len-1)+"].itemCustomField1'/><input type='hidden' id='eitemCustomField_text2"+table_len+"' name='items["+(table_len-1)+"].itemCustomField2'/><input type='hidden' id='eitemCustomField_text3"+table_len+"' name='items["+(table_len-1)+"].itemCustomField3'/><input type='hidden' id='eitemCustomField_text4"+table_len+"' name='items["+(table_len-1)+"].itemCustomField4'/><input type='hidden' id='eitemnotes_text"+table_len+"' name='items["+(table_len-1)+"].itemNotescomments'/><input type='hidden' id='eitemId_text"+table_len+"' name='items["+(table_len-1)+"].itemId'/></td></tr>";
		}else{
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='esno_row1'>"+table_len+"</span></td><td align='left' id='eproduct_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='eproduct_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='eremainproduct_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add remainddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateItemNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0' data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service' style='margin-right:8px;'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showEinvAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></td><td align='left' data ='' id='ehsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='ehsn_text"+table_len+"' name='items["+(table_len-1)+"].hsn' placeholder='HSN/SAC' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='eitemcodeempty' style='display:none' class='"+retType+"ddbox3"+table_len+"'><div class='"+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='left' id='euqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' id='euqc_text"+table_len+"' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' placeholder='UQC' value='' required><div id='uqc_textempty' style='display:none' class='remain"+retType+"ddbox4"+table_len+"'><div class='remain"+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='left' id='ebarcode_row"+table_len+"' class='form-group ebarcode_row"+table_len+"'><input type='text' class='form-control' id='ebarcode_text"+table_len+"' name='items["+(table_len-1)+"].barCode' placeholder='Bar Code' value=''></td><td align='right' id='eqty_row"+table_len+"' class='form-group'><input type='hidden' id='eopening_stock"+table_len+"'/><input type='hidden' id='esaftey_stock"+table_len+"'/><input type='text' onchange='changeeStock("+table_len+")' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' id='eqty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'  value='' onKeyUp='findEinvTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Qty' required/></td><td align='left' id='efreeqty_row"+table_len+"' class='form-group efreeqty_row"+table_len+"'><input type='text' class='form-control text-right' id='efreeqty_text"+table_len+"' name='items["+(table_len-1)+"].freeQty' placeholder='FreeQty' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' value=''></td><td align='right' id='erate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='erate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' value='' required onKeyUp='findEinvTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate'/></td><td align='right' id='ediscount_row"+table_len+"'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control disval text-right' id='ediscount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findEinvTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='Discount' /></td><td align='left' id='othrcharge_row"+table_len+"' class='form-group othrcharge_row"+table_len+"'><input type='text' class='form-control othercharge text-right' id='othrcharge_text"+table_len+"' name='items["+(table_len-1)+"].othrCharges' placeholder='Other Charge' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' onKeyUp='findEinvTaxableValue("+table_len+")' value=''></td><td align='right' id='etaxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='etaxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td><td align='right' id='etaxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='etaxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findEinvTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ettax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='eabb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='etax_rate_drop"+table_len+" pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3'><p class='mr-3'>IGST <span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='eigsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='ecgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='esgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='ecessrate_row"+table_len+"' class='form-group ecessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='ecessrate_text"+table_len+"' class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findEinvCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places' value='' /></td><td align='right' id='ecessamount_row"+table_len+"'  class='tablegreybg ecessFlag'><input type='text'  class='form-control text-right cessamtval' id='ecessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' value='' readonly></td><td align='right' id='etotal_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control text-right' id='etotal_text"+table_len+"' name='items["+(table_len-1)+"].total' value='' readonly></td><td align='center' width='2%'><a href='javascript:void(0)' class='eitem_delete' onclick='delete_erow("+table_len+")'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td><td class='d-none'><input type='hidden' id='eitemCustomField_text1"+table_len+"' name='items["+(table_len-1)+"].itemCustomField1'/><input type='hidden' id='eitemCustomField_text2"+table_len+"' name='items["+(table_len-1)+"].itemCustomField2'/><input type='hidden' id='eitemCustomField_text3"+table_len+"' name='items["+(table_len-1)+"].itemCustomField3'/><input type='hidden' id='eitemCustomField_text4"+table_len+"' name='items["+(table_len-1)+"].itemCustomField4'/><input type='hidden' id='eitemnotes_text"+table_len+"' name='items["+(table_len-1)+"].itemNotescomments'/><input type='hidden' id='eitemId_text"+table_len+"' name='items["+(table_len-1)+"].itemId'/></td></tr>";
		}
		row = table.insertRow(table_len+1).outerHTML=rowPrefix;
		$('#einvoice_table').tableDnDUpdate();
		$('#eabb'+einvRowCount).hover(
				  function() {
					  $(this).next().css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"10%","margin-top":"5px"});
				  }, function() {
					  $(this).next().css("display","none");
				  }
		);
		if($("#ecessrate_text"+einvRowCount).val() == ""){
			var cesstype = $('input[name="cessType"]:checked').val();
			if(cesstype == "Taxable Value"){
				document.getElementById("ecessrate_text"+einvRowCount).value = "0%"; 
			}else{
				document.getElementById("ecessrate_text"+einvRowCount).value = "0"; 
			}
			document.getElementById("ecessamount_text"+einvRowCount).value = "0.0";
		}
		if(enableEinvDiscount == true || enableEinvDiscount == "true"){
			$('#ediscount_row'+table_len).show();
		}else{
			$('#ediscount_row'+table_len).hide();
		}
		if(enableEinvCess == true || enableEinvCess == "true"){
			if($('#einvoiceLevelCess').is(":checked")) {
	    		   $('.ecessFlag').show();
	    	}else{
	    		if(einvoiceLevel_Cess == 'No'){
	    			$('.ecessFlag').hide();
	    			$('#ecessrate_text'+table_len).removeAttr("required");
	    		}
	    	}
		}else{
			 if($('#einvoiceLevelCess').is(":checked")) {
				 $('.ecessFlag').show();
			 }else{
				 if(einvoiceLevel_Cess == 'No'){
		    		   $('.ecessFlag').hide();
		    		   $('#ecessrate_text'+table_len).removeAttr("required");
	    		   }
			 }
		}
		$('#all_einvoice tr').click(function() {
			$(this).addClass("addrowshadow");
			$(this).siblings().removeClass("addrowshadow" );
			});
		eINVintialIntialization();
		itemoptions(einvRowCount,'remain','EINVOICE');
		$('form[name="einvoceform"]').validator('update');
	}
	
	function eupdateDetailsAfterDelete_row(no){
		var totalTaxable=document.getElementById("etotTaxable").innerHTML;
		var taxableamount = '';
		var taxableamount1 = '';
			taxableamount=document.getElementById("etaxableamount_text"+no).value;
			taxableamount1=$("#etaxableamount_text"+no).val();
		if(taxableamount != '' && taxableamount>0){
			totalTaxable = totalTaxable.replace(/,/g , '');
			totalTaxable-=parseFloat(taxableamount).toFixed(2);
		}else if(taxableamount1>0){
			taxableamount1 = taxableamount1.replace(/,/g , '');
			totalTaxable = totalTaxable.replace(/,/g , '');
			totalTaxable-=parseFloat(taxableamount1);
		}
		if(/[,\-]/.test(totalTaxable)){
		totalTaxable = totalTaxable.replace(/,/g , '');
		}
		$('#etotTaxable').html(parseFloat(totalTaxable));
		
		var totalIGST=document.getElementById("etotIGST").innerHTML;
		var igstTax=document.getElementById("eigsttax_text"+no).value;
		var igstTax1=$("#igsttax_text"+no).val();
		
		if(igstTax != '' && igstTax>0){
			totalIGST = totalIGST.replace(/,/g , '');
			igstTax = igstTax.replace(/,/g , '');
			totalIGST-=parseFloat(igstTax);
		}else if(igstTax1>0){
			totalIGST = totalIGST.replace(/,/g , '');
			igstTax1 = igstTax1.replace(/,/g , '');
			totalIGST-=parseFloat(igstTax1);
		}
		if(/[,\-]/.test(totalIGST)){
			totalIGST = totalIGST.replace(/,/g , '');
		}
		$('#etotIGST').html(parseFloat(totalIGST));
		
		var totalCGST=document.getElementById("etotIGST").innerHTML;
		var cgstTax=document.getElementById("ecgsttax_text"+no).value;
		var cgstTax1=$("#ecgsttax_text"+no).val();
		
		if(cgstTax != '' && cgstTax>0){
			totalCGST = totalCGST.replace(/,/g , '');
			cgstTax = cgstTax.replace(/,/g , '');
			totalCGST-=parseFloat(cgstTax);
		}else if(cgstTax1>0){
			totalCGST = totalCGST.replace(/,/g , '');
			cgstTax1 = cgstTax1.replace(/,/g , '');
			totalCGST-=parseFloat(cgstTax1);
		}
		if(/[,\-]/.test(totalCGST)){
			totIGST = totalCGST.replace(/,/g , '');
		}
		$('#etotIGST').html(parseFloat(totalCGST));
		
		var totalSGST=document.getElementById("etotIGST").innerHTML;
		var sgstTax=document.getElementById("esgsttax_text"+no).innerText;
		var sgstTax1=$("#esgsttax_text"+no).val();
		
		if(sgstTax != '' && sgstTax>0){
			totalSGST = totalSGST.replace(/,/g , '');
			sgstTax = sgstTax.replace(/,/g , '');
			totalSGST-=parseFloat(sgstTax);
		}else if(sgstTax1>0){
			totalSGST = totalSGST.replace(/,/g , '');
			sgstTax1 = sgstTax1.replace(/,/g , '');
			totalSGST-=parseFloat(sgstTax1);
		}
		if(/[,\-]/.test(totalSGST)){
			totIGST = totalSGST.replace(/,/g , '');
		}
		$('#etotIGST').html(parseFloat(totalSGST));
		
		var totalCESS=document.getElementById("etotCESS").innerHTML;
		var cessamount=document.getElementById("ecessamount_text"+no).value;
		var cessamount1=$("#cessamount_text"+no).val();
		
		if(cessamount != '' && cessamount>0){
			totalCESS = totalCESS.replace(/,/g , '');
			cessamount = cessamount.replace(/,/g , '');
			totalCESS-=parseFloat(cessamount);
		}else if(cessamount1>0){
			totalCESS = totalCESS.replace(/,/g , '');
			cessamount1 = cessamount1.replace(/,/g , '');
			totalCESS-=parseFloat(cessamount1);
		}
		if(/[,\-]/.test(totalCESS)){
			totalCESS = totalCESS.replace(/,/g , '');
		}
		$('#etotCESS').html(parseFloat(totalCESS));
		var totTotal=document.getElementById("etotTotal").innerHTML;
		var total=document.getElementById("etotal_text"+no).value;
		var total1=$("#etotal_text"+no).val();
		
		if(total != '' && total>0){
			totTotal = totTotal.replace(/,/g , '');
			total = total.replace(/,/g , '');
			totTotal-=parseFloat(total);
		}else if(total1>0){
			totTotal = totTotal.replace(/,/g , '');
			total1 = total1.replace(/,/g , '');
			totTotal-=parseFloat(total1);
		}
		if(/[,\-]/.test(totTotal)){
		totTotal = totTotal.replace(/,/g , '');
		}
		//$('#totTotal, #idTotal').html(parseFloat(totTotal));
		$('#etotTotal').html(parseFloat(totTotal));
		var invtype = $('#idEInvType').val();
		if(invtype == 'Exports'){
			var curtotTotal=document.getElementById("totcurAmt").innerHTML;
			var curtotal=document.getElementById("ecurtotal_text"+no).value;
			var curtotal1=$("#ecurtotal_text"+no).val();
			
			if(curtotal != '' && curtotal>0){
				curtotTotal = curtotTotal.replace(/,/g , '');
				curtotal = curtotal.replace(/,/g , '');
				curtotTotal-=parseFloat(curtotal);
			}else if(curtotal1>0){
				curtotTotal = curtotTotal.replace(/,/g , '');
				curtotal1 = curtotal1.replace(/,/g , '');
				curtotTotal-=parseFloat(curtotal1);
			}
			if(/[,\-]/.test(curtotTotal)){
			curtotTotal = curtotTotal.replace(/,/g , '');
		}
		//$('#totTotal, #idTotal').html(parseFloat(totTotal));
		$('#totcurAmt').html(parseFloat(curtotTotal));
		}
		
		if($('#eroundOffTotalChckbox').is(':checked')) {
			$('#eidTotal').html(Math.round(parseFloat(totTotal)).toFixed(2));
			$('#eroundOffTotalValue').val((Math.round(totTotal)-totTotal).toFixed(2));
		}else if(!$('#eroundOffTotalChckbox').is(':checked')){
			$('#eidTotal').html(parseFloat(totTotal));
			$('#eroundOffTotalValue').val('');
		}
		$('#ehiddenroundOffTotalValue').val(totTotal);
		$(".indformat").each(function(){
		    $(this).html($(this).html().replace(/,/g , ''));
		});
		OSREC.CurrencyFormatter.formatAll({
			selector: '.indformat'
		});
		etdstcscal();
	}
	
	function delete_erow(no) {
		eupdateDetailsAfterDelete_row(no);
			var table=document.getElementById("einvoice_table");
			if(no > einvRowCount){
				no=no-1;
			}
			table.deleteRow(no+1);
			//$('#sortable_table').tableDnDUpdate();
			einvRowCount--;
			var retType = $('#retType').val();
			var invType = $('#idInvType').val();
			
			currRowIndex=null;
			var tablerows = $('#all_einvoice tr').length;
			$("#all_einvoice tr").each(function(index) {
				 $(this).attr('id',index+1);
				 $(this).find("#esno_row1").html(index+1);
				
				 var rowno = (index+1).toString();
				 var rownoo = (index).toString();
				 $(this).find('td').each (function() {
		   	    	 	var name = $(this).attr('id');
			   	    	if(name != undefined){
			   	    		if(rowno<10){
			   	    			name = name.slice(0, -1);
				   	    		}else{
				   	    			name = name.slice(0, -2);
				   	    		}
							name = name+rowno;
							$(this).attr('id',name);
							$(this).find('.input_itemDetails_txt').each (function() {
								var itemClass = $('.input_itemDetails_txt').attr('class');
								if(itemClass != undefined){
									if(rowno<10){
										itemClass = itemClass.slice(0, -1);
						   	    		}else{
						   	    			itemClass = itemClass.slice(0, -2);
						   	    		}
									itemClass = itemClass+rowno;
									$(this).attr('class',itemClass);
								}
							});
							$(this).find('.additem_box').each (function() {
								var additemid = $('.additem_box').attr('id');
								if(additemid != undefined){
									if(rowno<10){
										additemid = additemid.slice(0, -1);
						   	    		}else{
						   	    			additemid = additemid.slice(0, -2);
						   	    		}
									additemid = additemid+rowno;
									$(this).attr('id',additemid);
								}
							});
							$(this).find('.additem_box div').each (function() {
								var additemclass = $('.additem_box div').attr('class');
								if(additemclass != undefined){
									if(rowno<10){
										additemclass = additemclass.slice(0, -1);
						   	    		}else{
						   	    			additemclass = additemclass.slice(0, -2);
						   	    		}
									additemclass = additemclass+rowno;
									$(this).attr('class',additemclass);
								}
							});
							$(this).find('.additem_box input').each (function() {
								var additemclick = $('.additem_box input').attr('onclick');
								if(additemclick != 'item_delete'){
									var abcd = $(this).attr('onclick');
						   	    	abcd = replaceAt(abcd,12,rowno);
						   	    	$(this).attr('onclick',abcd);
								}else{
									var abcd = $(this).attr('onclick');
							   	    abcd = replaceAt(abcd,14,rowno);
							   	    $(this).attr('onclick',abcd);
								}
							});
							$(this).find('input').each (function() {
								var inputid = $(this).attr('id');
								var inputname = $(this).attr('name');
								if(inputname != undefined){
									if(inputname.indexOf("items[") >= 0) {
										if(rownoo == '9'){
											inputname = inputname.replace('10',' ');
										}
										inputname = replaceAt(inputname,6,rownoo);
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
								
								var qty = $(this).attr('class');
								var rate = $(this).attr('class');
								var othercharge = $(this).attr('class');
								var stateCess = $(this).attr('class');
								var cessnonAdvol = $(this).attr('class');

									if(qty != undefined && qty.indexOf("qtyval") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,21,rowno);
							   	    	$(this).attr('onkeyup',abcd);
										
										var change = $(this).attr('onchange');
							   	    	change = replaceAt(change,13,rowno);
							   	    	$(this).attr('onchange',change);
									}
									if(rate != undefined && rate.indexOf("rateval") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,21,rowno);
							   	    	$(this).attr('onkeyup',abcd);
									}
									if(othercharge != undefined && othercharge.indexOf("othercharge") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,21,rowno);
							   	    	$(this).attr('onkeyup',abcd);
									}
									if(stateCess != undefined && stateCess.indexOf("statecessval") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,21,rowno);
							   	    	$(this).attr('onkeyup',abcd);
									}
									if(cessnonAdvol != undefined && cessnonAdvol.indexOf("cessnonadvolval") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,21,rowno);
							   	    	$(this).attr('onkeyup',abcd);
									}
								var discount = $(this).attr('class');
								if(discount != undefined && discount.indexOf("disval") >= 0) {
									var abcd = $(this).attr('onkeyup');
						   	    	abcd = replaceAt(abcd,21,rowno);
						   	    	$(this).attr('onkeyup',abcd);
								}
								var cessval = $(this).attr('class');
								if(cessval != undefined && cessval.indexOf("cessval") >= 0) {
									var abcd = $(this).attr('onkeyup');
						   	    	abcd = replaceAt(abcd,19,rowno);
						   	    	$(this).attr('onkeyup',abcd);
								}
			
							});
							$(this).find('select').each (function() {
								var inputid = $(this).attr('id');
								var selectname = $(this).attr('name');
								if(selectname != undefined){
									if(selectname.indexOf("items[") >= 0) {
										if(rownoo == '9'){
											selectname = selectname.replace('10',' ');
										}
										selectname = replaceAt(selectname,6,rownoo);
										$(this).attr('name',selectname);
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
								
								var taxrateval = $(this).attr('class');
								if(taxrateval.indexOf("taxrateval") >= 0) {
									var abcd = $(this).attr('onchange');
						   	    	abcd = replaceAt(abcd,18,rowno);
						   	    	$(this).attr('onchange',abcd);
								}
								
						
							});
							$(this).find('div.easy-autocomplete-container').each (function() {
								var inputid = $(this).attr('id');
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
						}else{
							$(this).find('input').each (function() {
								var inputname = $(this).attr('id');
								var inputname1 = $(this).attr('class');
								if(inputname != undefined){
									if(rowno<10){
										inputname = inputname.slice(0, -1);
						   	    		}else{
						   	    			inputname = inputname.slice(0, -2);
						   	    		}
									inputname = inputname+rowno;
									$(this).attr('id',inputname);
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
								if(det != 'eitem_delete'){
									var abcd = $(this).attr('onclick');
						   	    	abcd = replaceAt(abcd,9,rowno);
						   	    	$(this).attr('onclick',abcd);
								}else{
									var abcd = $(this).attr('onclick');
							   	    abcd = replaceAt(abcd,12,rowno);
							   	    $(this).attr('onclick',abcd);
								}
							});
						}
			   	      });
				
			});
				$('form[name="einvoceform"]').validator('update');
				var tableng = $('#all_einvoice tr').length;
				
				if(tableng == '2'){
				$('.eitem_delete').hide();
				}else{
				$('.eitem_delete').show();	
				}
	}
	function closeIRNmodal(modalId){
		$('#'+modalId).modal('hide');
		irnCancelArray=new Array();
	}
	
	function differentialSelection(){
		$("#all_einvoice tr").each(function(index){
		findEinvTaxAmount(index+1);
		findEinvTaxableValue(index+1);
		//findEinvAssAmount(index+1);
		findEinvCessAmount(index+1);
		findEinvTotal(index+1);
		index++;
		});
		if($('#ediffPercent').is(":checked")){
			$('.diffPercent').val("Yes");
		}else{
			$('.diffPercent').val("No");
		}
	}
	function inclusiveTaxSelection(){
		$("#all_einvoice tr").each(function(index){
			var rtype = $("#einv_revchargetype>option:selected").val();
			if(rtype != 'Y'){
				findEinvTaxAmount(index+1);
				findEinvTaxableValue(index+1);
				//findEinvAssAmount(index+1);
				findEinvCessAmount(index+1);
				$('.rateinctax').text("");
			}else{
				$("#einvincludetax").prop("checked",false);
				$('.rateinctax').text("If we select RCM rate inclusive tax not applicable");
			}		
			index++;
		});
	}
	
function roundOffSelection(){
		$('input[name="roundOffTotal"]').click(function(){
            if($(this).prop("checked") == true){
            	var notroundOffTotalValue=$('#ehiddenroundOffTotalValue').val();
				if(notroundOffTotalValue != ''){
				$('#eroundOffTotalValue').val((Math.round(parseFloat(notroundOffTotalValue))-parseFloat(notroundOffTotalValue)).toFixed(2));$('#eidTotal').html(Math.round(notroundOffTotalValue).toFixed(2));
				}
            	OSREC.CurrencyFormatter.formatAll({selector: '.indformat_roundoff'});
            }else if($(this).prop("checked") == false){
            	$('#eroundOffTotalValue').val('');var notroundOffTotalValue=$('#ehiddenroundOffTotalValue').val();$('#eidTotal').html(notroundOffTotalValue);
            	OSREC.CurrencyFormatter.formatAll({selector: '.indformat_roundoff'});	
            }
        });
    }
	$('#all_einvoice tr').click(function() {
		$(this).addClass("addrowshadow");
		$(this).siblings().removeClass("addrowshadow" );
		});
	
	
	function eINVintialIntialization(){

		var uqcoptions1 = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/uqcconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				list: {
					onLoadEvent: function() {
						if($("#eac-container-uqc_text1 ul").children().length == 0) {
							$(".remainEINVOICEddbox41").show();
							$(".remainEINVOICEddbox41").css({'background-color':'#fff','border':'1px solid #f5f5f5','padding':'3px','position':'absolute','width':'8%','z-index':'1','box-shadow':'0px 0px 5px 0px #e5e5e5'})
							$(".remainEINVOICEddbox41 p").css({'color':'#CC0000','margin':'0'});
							$('.uqc_row1').addClass('has-error has-danger');
						} else {
							$(".remainEINVOICEddbox41").hide();
							$('.uqc_row1').removeClass('has-error has-danger');
						}
					},
					maxNumberOfElements: 43
				}
			};
		
			$("#euqc_text1").easyAutocomplete(uqcoptions1);
			$("#euqc_text1").parent().parent().mouseleave(function() {
				$(".remainEINVOICEddbox41").hide();
				
			});
			var codeoptions1 = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/hsnsacconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					categories: [{
						listLocation: "hsnConfig",
						header: "--- HSN ---"
					}, {
						listLocation: "sacConfig",
						header: "--- SAC ---"
					}],
					list: {
						match: {
							enabled: false
						},

						onLoadEvent: function() {
							if($("#eac-container-ehsn_text1 ul").css('width','300px').children().length == 0) {
								$("#eitemcodeempty").show();
								$(".remainEINVOICEddbox31").css({'background-color':'#fff','border':'1px solid #f5f5f5','padding':'3px','position':'absolute','width':'8%','z-index':'1','box-shadow':'0px 0px 5px 0px #e5e5e5'})
								$(".remainEINVOICEddbox31 .eremainhsnddbox1 p").css({'font-size':'12px','color':'#cc0000','margin':'0'});
							} else {
								$("#eitemcodeempty").hide();
							}
						}
					}
				};
			$("#ehsn_text1").easyAutocomplete(codeoptions1);
			$("#ehsn_text1").parent().parent().mouseleave(function() {
				$("#eitemcodeempty").hide();
			});
			

			var itemoptions1 = {
					url: function(phrase) {
						var clientid = $('#clientid').val();
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/srchcommon?query="+ phrase + "&clientid="+clientid+"&format=json";
					},
					categories: [{
						listLocation: "items"
					}],
					getValue: "itemnoAndDescription",
					list: {
						onChooseEvent: function() {
							var itemData = $("#eproduct_text1").getSelectedItemData();
							var invtype = $('#idInvType').val();
							var rettype = $('#retType').val();
							$("#eproduct_text1").val('');
							$("#eproduct_text1").val(itemData.description);
							if(itemData.code) {
								$("#ehsn_text1").val(itemData.code);
							} else if(itemData.hsn) {
								$("#ehsn_text1").val(itemData.hsn);
							} else if(itemData.sac) {
								$("#ehsn_text1").val(itemData.sac);
							}
							$("#euqc_text1").val(itemData.unit);
							
							if(itemData.taxrate){
								$("#etaxrate_text1").val(itemData.taxrate);
							}
							if(itemData.discount){
								if(invtype != 'Credit/Debit Notes'){
								$("#ediscount_text1").val(itemData.discount);
								}else{
								$("#ediscount_text1").val('');
								}
							}
							
							if(itemData.salePrice){
								$("#erate_text1").val(itemData.salePrice);
							}else if(itemData.cost) {
								$("#erate_text1").val(itemData.cost);
							} else{
								$("#erate_text1").val(itemData.sellingpriceb2b);
							}
							$("#eqty_text1").val(1);
							$("#eqty_text1").focus();
							//findTaxableValue();
							
							$('#eitemCustomField_text11').val(itemData.itemCustomField1);
							$('#eitemCustomField_text21').val(itemData.itemCustomField2);
							$('#eitemCustomField_text31').val(itemData.itemCustomField3);
							$('#eitemCustomField_text41').val(itemData.itemCustomField4);
							$('#eitemId_text1').val(itemData.userid);
							var invtype = $('#idInvType').val();
							
							var hsnsacdata = $("#ehsn_text1").val();

						
								findEinvTaxableValue(1);
							
						},
						onLoadEvent: function() {
							if($("#eac-container-eproduct_text1 ul").children().length == 0) {
								$("#eremainproduct_textempty1").show();
								$("#eremainproduct_textempty1 p").css({'color':'#CC0000','margin':'0'});
							} else {
								$("#eremainproduct_textempty1").hide();
							}
						}
					}
				};
				$("#eproduct_text1").easyAutocomplete(itemoptions1);
				$("#eproduct_text1").parent().parent().mouseleave(function() {
					 setTimeout(function () {$("#eremainproduct_textempty1").hide();}, 1000);
				});
		}
	
	function itemoptions(rowCoun,type,retType){
		var rc = einvRowCount;
		var codeoptions = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/hsnsacconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				categories: [{
					listLocation: "hsnConfig",
					header: "--- HSN ---"
				}, {
					listLocation: "sacConfig",
					header: "--- SAC ---"
				}],
				list: {
					match: {
						enabled: false
					},
	
					onLoadEvent: function() {
						if($("#eac-container-ehsn_text"+rc+" ul").css('width','300px').children().length == 0) {
							$('.'+type+''+retType+'ddbox3'+rc).show();
							$('.'+type+''+retType+'ddbox3'+rc).css({'background-color':'#fff','border':'1px solid #f5f5f5','padding':'3px','position':'absolute','width':'8%','z-index':'1','box-shadow':'0px 0px 5px 0px #e5e5e5'})
							$('#ehsn_row'+rc).addClass('has-error has-danger');
						} else {
							$('.'+type+''+retType+'ddbox3'+rc).hide();
							$('#ehsn_row'+rc).removeClass('has-error has-danger');
							
						}
					}
				}
			};
		$("#ehsn_text"+rc).easyAutocomplete(codeoptions);
		$("#ehsn_text"+rc).parent().parent().mouseleave(function() {
			$('.'+type+''+retType+'ddbox3'+rc).hide();
		});
		var uqcoptions = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/uqcconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				list: {
					onLoadEvent: function() {
						if($("#eac-container-euqc_text"+rc+" ul").children().length == 0) {
							$('.'+type+''+retType+'ddbox4'+rc).show();
							$('.'+type+''+retType+'ddbox4'+rc).css({'background-color':'#fff','border':'1px solid #f5f5f5','padding':'3px','position':'absolute','width':'8%','z-index':'1','box-shadow':'0px 0px 5px 0px #e5e5e5'});
							$('#euqc_row'+rc).addClass('has-error has-danger');
						} else {
							$('.'+type+''+retType+'ddbox4'+rc).hide();
							$('#euqc_row'+rc).removeClass('has-error has-danger');
						}
					},
					maxNumberOfElements: 43
				}
			};
			$("#euqc_text"+rc).easyAutocomplete(uqcoptions);
			$("#euqc_text"+rc).parent().parent().mouseleave(function() {
				$('.'+type+''+retType+'ddbox4'+rc).hide();
				
			});

		var itemoptions = {
				url: function(phrase) {
					var clientid = $('#clientid').val();
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/srchcommon?query="+ phrase + "&clientid="+clientid+"&format=json";
				},
				categories: [{
					listLocation: "items"
				}],
				getValue: "itemnoAndDescription",
				list: {
					onChooseEvent: function() {
						var itemData = $("#eproduct_text"+rc).getSelectedItemData();
						var rettype = $('#retType').val();
						$("#eproduct_text"+rc).val('');
						$("#eproduct_text"+rc).val(itemData.description);
						if(itemData.code) {
							$("#ehsn_text"+rc).val(itemData.code);
						} else if(itemData.hsn) {
							$("#ehsn_text"+rc).val(itemData.hsn);
						} else if(itemData.sac) {
							$("#ehsn_text"+rc).val(itemData.sac);
						}
						$("#euqc_text"+rc).val(itemData.unit);
						
						if(itemData.taxrate){
							$("#etaxrate_text"+rc).val(itemData.taxrate);
						}
						if(itemData.discount){
							$("#ediscount_text"+rc).val(itemData.discount);
						}
					
						if(itemData.salePrice){
							$("#erate_text"+rc).val(itemData.salePrice);
						}else if(itemData.cost) {
							$("#erate_text"+rc).val(itemData.cost);
						} else{
							$("#erate_text"+rc).val(itemData.sellingpriceb2b);
						}
						$("#eqty_text"+rc).val(1);
						$("#eqty_text"+rc).focus();
						$('#eitemCustomField_text1'+rc).val(itemData.itemCustomField1);
						$('#eitemCustomField_text2'+rc).val(itemData.itemCustomField2);
						$('#eitemCustomField_text3'+rc).val(itemData.itemCustomField3);
						$('#eitemCustomField_text4'+rc).val(itemData.itemCustomField4);
						$('#eitemId_text'+rc).val(itemData.userid);
						//findTaxableValue();
						var hsnsacdata = $("#ehsn_text"+rc).val();
							findTaxableValue(rc);
						
					},
					onLoadEvent: function() {
						if($("#eac-container-eproduct_text"+rc+" ul").children().length == 0) {
							$("#e"+type+"product_textempty"+rc).show();
							$("."+type+"ddbox"+rc).css({'left':'53px','position':'absolute','background-color': '#fff','border': '1px solid #f5f5f5','padding': '10px',' z-index': '99','box-shadow': '0px 0px 5px 0px #e5e5e5','width':'25.4%','z-index':'2'});
							$("#e"+type+"product_textempty"+rc+" p").css({'color':'#CC0000','margin':'0'});
						} else {
							$("#e"+type+"eproduct_textempty"+rc).hide();
						}
					}
				}
			};
			$("#eproduct_text"+rc).easyAutocomplete(itemoptions);
			$("#eproduct_text"+rc).parent().parent().mouseleave(function() {
				setTimeout(function () {$("#e"+type+"product_textempty"+rc).hide();}, 1000);
			});
	}
	function closeEinvmodal(modalId){
		einvRowCount=1;
		 //$('#einvoiceModal').reset();
		$('#'+modalId).modal('toggle');
		//$('input,textarea,select').val("");
		$('.form-control,.che_form-control').removeClass('disable');
		$('#einvDraft_Btn,#generateIRNBtn,#cancelIRN_btn').removeClass("disabled");
		$('#einvoiceLevelCess').val("Yes").prop("checked",false);
		$('#einvLevelCess').val("Yes");
		$('#eInvitemdetails,.errormsg').text("");
		var ele=$('.form-group').is('.has-error');
		if(ele){
			$('div,td').removeClass("has-error has-danger");
		}
		$('#einvoceform').find('input').each(function(){
			$(this).val("");
		});
		$('#einvoceform').find('textarea').each(function(){
			$(this).val("");
		});
		$('#einvoceform').find('select').each(function(){
			$(this).val("");
		});
		$('#esrctype,#irnNo,#irnStatus,#signedQrCode,#signedInvoice,#ackNo,#ehiddenroundOffTotalValue,#ackDt,#buyerDtls_gstin,#buyerDtls_lglNm,#buyerDtls_addr1,#buyerDtls_addr2,#buyerDtls_loc,#buyerDtls_pin,#buyerDtls_state,#buyerDtls_pos,#dispatcherDtls_nm,#dispatcherDtls_addr1,#dispatcherDtls_addr2,#dispatcherDtls_loc,#dispatcherDtls_pin,#dispatcherDtls_stcd,#shipmentDtls_gstin,#shipmentDtls_trdNm,#shipmentDtls_lglNm,#shipmentDtls_addr1,#shipmentDtls_addr2,#shipmentDtls_loc,#shipmentDtls_pin,#shipmentDtls_stcd').val("");
		$('.eapply_tcsOtds').remove();
		$('#einvoice_table tbody').find("tr:gt(0)").remove();$('#einvoice_table tfoot tr').not(':last').remove();
		$('#invCategory').children('option').remove();
		$('#invCategory').append($("<option></option>").attr("value","").text("-- Select --"));
		$('#invCategory').append($("<option></option>").attr("value", "EXPWP").text("Export with Payment"));
		$('#invCategory').append($("<option></option>").attr("value", "EXPWOP").text("Export without payment"));
		$('#invCategory').append($("<option></option>").attr("value", "DEXP").text("Deemed Export"));
		$('#invCategory').append($("<option></option>").attr("value", "B2B").text("Business to Business"));
		$('#invCategory').append($("<option></option>").attr("value", "SEZWP").text("SEZ with payment"));
		$('#invCategory').append($("<option></option>").attr("value", "SEZWOP").text("SEZ without payment"));
		$('#etdstcssection ,#etcs_percent').attr("readonly","true");$('#etdstcssection').attr("disabled",true);
		//$('.etcsval').removeAttr('checked').val('false');
		$('.etcsval').prop('checked',false).val("false");
		$('#irnNumber_txt').css("display","none");
		$('#irnNumber').html("");
		$('#ecess_qty').removeAttr("checked").val("Quantity");
		$('#ecess_taxable').prop("checked",true).val("Taxable Value");
		//$('#generateIRNBtn').css("display","inline-block");	
		$('#cancelIRN_btn').css("display","none");
		$('#adderow').removeAttr('disabled').css("background-color", "");
		$('.gstr2adeletefield').css("display", "block");
		$('#etotTotal,#etotIGST,#eidTotal,#etotTaxable,#totAsseAmt').html("0.00");
		$('#igstOnIntra,#einv_revchargetype').val("N");
		$('#generateIRNBtn').val("Generate IRN & Save");
		$('#einvokegstnPublicAPI1').val("Get GSTIN Details");
		$('#einvDraft_Btn').val("Save As Draft");
		$('.buyerAddrText').html('Add');
		$('#einvbuyerAddr,#einvdispatchAddr,#einvshipmentAddr').html("");
		$('form[name="einvoceform"]').validator('update');
	}
 function updateItemNames(no) {
	 	getEinvItemCustomFields(einvCustomFieldsData,"aItem",no);
		$('#einvoiceModal').css("z-index","1033");
		$('.modal-backdrop.show').css("display","block");
		$('#eitemDescription').val($('.itemname'+no).val());
		$('.errormsg').css('display','block').html('');
		$(".form-group").removeClass('has-error has-danger');
		$('.help-block.with-errors').html('');
		$('#eunitempty').css('display','none');
		$('.rowno').val(no);
		$('#addEItemModal').css('display','block');
		$('#addEItemModal').addClass('show');
		$('#addEItemModal').modal('show');
	}
	function closeEAddCustomer(modalId){
			$('#einvoiceModal').css("z-index","");
		$('#'+modalId).modal('hide');
	}
	
	function validateEItem(){  
	var priceValidation = /^[0-9,]+(\.[0-9]{1,2})?$/;
	var itemNumber = $('#eitemNumber').val();
	var itemDescription = $('#eitemDescription').val();
	var unit = $('#eunit').val();
	var recommendedSellingPriceForB2B = $('#erecommendedSellingPriceForB2B').val();
	var recommendedSellingPriceForB2C = $('#erecommendedSellingPriceForB2C').val();
	var ItemHSNCode = $('#eItemHSNCode').val();
	var saftlyStockLevel = $('#esaftlyStockLevel').val();
	itemDescription = itemDescription.replace(/ +/g, "");
	ItemHSNCode = ItemHSNCode.replace(/ +/g, "");
	var err = 0;
	var c = 0;  
	if(itemNumber==""){
		$('#eitemNumber_Msg').text("Please enter Item Number/Code"); 
		c++;
	}else{  
		$('#eitemNumber_Msg').text(""); 
	}
		if(itemDescription==""){
			$('#eproductDescription_Msg').text("Please enter Item Description");
			c++;
		}else{
			$('#eproductDescription_Msg').text("");
		}
		
		if(unit==""){
			$('#eunitofMeasurement_Msg').text("Please enter the Unit of Measurement");
			c++;
		}else{
			$.ajax({
				url: contextPath+"/validuqcconfig?query="+ unit + "&format=json",
				async: false,
				contentType: 'application/json',
				success : function(response) {
					if(response.length == 0){
						err=1;
					}
				},error : function(err){
				}
			});
		}
		
		if(err != 0){
			$('#eunitofMeasurement_Msg').text("Please enter valid UQC");
			c++;
		}
	
	if(ItemHSNCode==""){
		$('#eHSNSACCode_Msg').text("Please enter HSN/SAC Code");
		c++;
	}else{
		$('#eHSNSACCode_Msg').text("");
	}
	return c==0; 
}
	
	
	function saveEItem() {
		$('#einvadditm').addClass("btn-loader-blue");
		if(validateEItem()){
			$('.form-group').removeClass('has-error has-danger');
			var rowno = $('.rowno').val();
			var sb2b = $('#erecommendedSellingPriceForB2B').val();
			var sb2c = $('#erecommendedSellingPriceForB2C').val();
			/*if(sb2b != ''){
				sb2b = sb2b.replace(/,/g , '');
				$('#erecommendedSellingPriceForB2B').val(sb2b);
			}*/
			
			$.ajax({
				type: "POST",
				url: $("#itemEForm").attr('action'),
				data: $("#itemEForm").serialize(),
				success: function(data) {
					$("#additemno").hide();
					$("#addEItemModal").hide();
					$('#ehsn_text'+rowno).val($('#eItemHSNCode').val());
					$('#euqc_text'+rowno).val($('#eunit').val());
					$('#erate_text'+rowno).val($('#eitemsalePrice').val());
					$('#etaxrate_text'+rowno).val($('#etaxrate_text').val());
					$('#ediscount_text'+rowno).val($('#eDiscount').val());
					$('#eexempted_text'+rowno).val($('#eexmeptedvalue').val());
					$('#eqty_text'+rowno).val(1).focus();
					$('#etaxableamount_text'+rowno).val('');
					$('#eigsttax_text'+rowno).val('');
					$('#esgsttax_text'+rowno).val('');
					$('#ecgsttax_text'+rowno).val('');
					$('#etotal_text'+rowno).val('');
					$('#ecessamount_text'+rowno).val('');
					$('#ecessrate_text'+rowno).val(0);
					$('#eopening_stock'+rowno).val($('#eitemopeningStockLevel').val());
					$('#esaftey_stock'+rowno).val($('#eitemsaftlyStockLevel').val());
					$('#eitemCustomField_text1'+rowno).val($('#eitem_CustomField1aItem').val());
					$('#eitemCustomField_text2'+rowno).val($('#eitem_CustomField2aItem').val());
					$('#eitemCustomField_text3'+rowno).val($('#eitem_CustomField3aItem').val());
					$('#eitemCustomField_text4'+rowno).val($('#eitem_CustomField4aItem').val());
					$('#eitemId_text'+rowno).val(data);
					$('#eitemnotes_text'+rowno).val($('#eitemCommentss').val());
					findEinvTaxableValue(rowno);
					$('#einvadditm').removeClass("btn-loader-blue");
						$('#einvoiceModal').css("z-index","");
						$('.modal-backdrop.show').css("display","none");
					$("#itemEForm")[0].reset();
				},
				error: function(data) {
					$('#einvadditm').removeClass("btn-loader-blue");
					$('#einvoiceModal').css("z-index","");
					$('.modal-backdrop.show').css("display","none");
					$("#itemEForm")[0].reset();
					$("#addEItemModal").hide();
				}
			});
		}else{
			$('#einvadditm').removeClass("btn-loader-blue");
		}
	}
	function validateeCustomer(){
		var c = 0;
		var customerorsuppliertype=$('#ecustomerorsuppliertype').val();
	
			var customerid=$('#ecustomerid').val();
			var clientid=$('#clientid').val();
			$.ajax({
				type : "GET",
				async: false,
				contentType : "application/json",
				url: contextPath+"/customeridexits/"+clientid+"/"+customerid,
				success : function(response) {
					if(response == 'success'){
						c++;
						$('#ecustomerid_Msg').text('customerid already exists');
						$('#ecustomer_type').addClass('has-error has-danger');
					}else{
						$('#ecustomerid_Msg').text('');
						$('#ecustomer_type').removeClass('has-error has-danger');
					}
				}
			});
	
	var custname = $('#ecustname').val();
	var custgstin= $('#egstnnumber').val();
	var contactperson = $('#econtactperson').val();
	custname = custname.replace(/ +/g, "");
	contactperson = contactperson.replace(/ +/g, "");
	var checks = $('.ebusiness').attr('checked');
	var state = $('#estate').val();
	if($('.ebusiness').is(":checked")){
		if(custgstin==""){
			$('#egstnnumber_Msg').text("Please enter Valid GSTIN"); 
			c++;
		}else{  
			$('#egstnnumber_Msg').text(""); 
		}
	}else{
		$('#egstnnumber_Msg').text("");
	}
	var state = $('#estate').val();
	if(custname==""){
		$('#ebusinessName_Msg').text("Please enter Name"); 
		c++;
	}else{  
		$('#ebusinessName_Msg').text(""); 
	}
	if(state==""){
		$('#estate_Msg').text("Please enter state");
		c++;
	}else{
		$('#estate_Msg').text("");
	}	
	return c==0; 
}
	function saveECustomer() {
		$('#einvaddcst').addClass("btn-loader-blue");
		if(validateeCustomer()){
			$('.errormsg').css('display','none');
			$('.with-errors').html('');
			$('.form-group').removeClass('has-error has-danger');
					
			$.ajax({
				type: "POST",
				url: $("#customerEForm").attr('action'),
				data: $("#customerEForm").serialize(),
				success: function(data) {
					$('#invoiceStatecode').val($('#estate').val());
					$('#ebilledtogstin').val($('#egstnnumber').val());
					//$('#billingAddress').val($('#custaddress').val());
					$('#ebankTerms').val($('#ecustomertermsinv').val());
					$("#eaddbilledtoname").hide();
					$('#einvoiceModal').css("z-index","");
					$('#einvaddcst').removeClass("btn-loader-blue");
					$('#addEinvCustomerModal').modal('hide');
					$('.modal-backdrop.show').css("display","none");
					$("#customerEForm")[0].reset();
				},
				error: function(data) {
					$('#einvaddcst').removeClass("btn-loader-blue");
					$("#eaddbilledtoname").hide();
					$("#customerEForm")[0].reset();
				}
			});
			$('.ebusiness').attr('checked','checked');
		}else{
			$('#einvaddcst').removeClass("btn-loader-blue");
		}	
	}
	function updateCustomerName(sourceId, destId, modalId,type) {
		$('.errormsg').css('display','block').html('');	
		$('#invoiceModal').css("z-index","1033");
		$('#'+modalId).modal('show');
		$('#egroupstabinv').addClass('active');
		$('#egroups1tabinv').removeClass('active');
		$('#esgroupstabinv').removeClass('active');
		var today = new Date();
		 var year = today.getFullYear();
		 $('.openTxt').text(year);
			$("#esupplierBankDetails,#esupplier_additional_details,#esupplier_faxno").css("display","none");
			$('#ecustomerorsuppliertype').val('GSTR1');
			$('#eccustomerLedgerName').val($('#'+sourceId).val());
			$('#ecustomer_type').removeClass('d-none');
			$('#esupplier_type').addClass('d-none');
			$('#eccustomer_type').removeClass('d-none');
			$('#essupplier_type').addClass('d-none');
			$('.essupplier_type').removeClass('d-none');
			$('#ecustomerEForm').attr('action',contextPath+"/cp_createcustomer/"+commonSuffix+"/"+month+"/"+year);
		
		$('.eaddCustomerBankDetailss').prop('checked',false);$("#eselectCBankDiv2").css("display","none");$("#eselectCBankDiv3").css("display","none");$('#eselectCustomerBanks').val('');$('#ecustomerBankNames').val('');$('#ecustomerBankAcctNos').val('');$('#ecustomerBankBranchs').val('');$('#ecustomerBankIFSCs').val('');$('#ecustomerBankAccountNames').val('');$('#'+destId).val($('#'+sourceId).val());$("#enotrequireds").css('display','block');
		$('#egstnnumber').val($('#ebilledtogstin').val()).trigger("change");
		$.ajax({
			url: contextPath+"/countrieslist",
			contentType: 'application/json',
			success : function(response) {for(var i = 0; i < response.length; i++) {$('#ecountry').append("<option value="+response[i].name+">"+response[i].name+"</option>");}}
		});
		$.ajax({
			url: contextPath+'/bnkdtls'+urlSuffixs,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(bankDetails) {
				customerClientBankDetailss = new Array();
					$("#eselectCustomerBanks").append($("<option></option>").attr("value","").text("-- Select Bank --"));
					for (var i=0; i<bankDetails.length; i++) {
						$("#eselectCustomerBanks").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname));
						customerClientBankDetailss.push(bankDetails[i]);
					}
			}
		});
	}
	function applyEInvFilters(retType) {
		var retTypeCode = retType.replace(" ", "_");
		var irnStatusOptions = $('#emultiselect'+retTypeCode+'1 option:selected');
		var invTypeOptions = $('#emultiselect'+retTypeCode+'2 option:selected');
		var customerOptions = $('#emultiselect'+retTypeCode+'3 option:selected');
		var reverseChargeOptions = $('#emultiselect'+retTypeCode+'4 option:selected');
		var branchOptions = $('#emultiselect'+retTypeCode+'5 option:selected');
		var verticalOptions = $('#emultiselect'+retTypeCode+'6 option:selected');
		var customFieldOptions1 = $('#emultiselect'+retTypeCode+'7 option:selected');
		var customFieldOptions2 = $('#emultiselect'+retTypeCode+'8 option:selected');
		var customFieldOptions3 = $('#emultiselect'+retTypeCode+'9 option:selected');
		var customFieldOptions4 = $('#emultiselect'+retTypeCode+'10 option:selected');
		if(irnStatusOptions.length > 0 || invTypeOptions.length > 0 || customerOptions.length > 0 || reverseChargeOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0 || customFieldOptions1.length > 0 || customFieldOptions2.length > 0 || customFieldOptions3.length > 0 || customFieldOptions4.length > 0){
			$('.normaltable .filter').css("display","block");
		}else{
			$('.normaltable .filter').css("display","none");
		}
		var statusArr=new Array();
		var invtypeArr=new Array();
		var customerArr=new Array();
		var reverseChargeArr=new Array();
		var branchArr=new Array();
		var verticalArr=new Array();
		var customFieldArr1=new Array();
		var customFieldArr2=new Array();
		var customFieldArr3=new Array();
		var customFieldArr4=new Array();
		var filterContent='';
		
		if(irnStatusOptions.length > 0) {
			for(var i=0;i<irnStatusOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+irnStatusOptions[i].value+'<span data-val="'+irnStatusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				statusArr.push(irnStatusOptions[i].value);
			}
		}
		if(invTypeOptions.length > 0) {
			for(var i=0;i<invTypeOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+invTypeOptions[i].text+'<span data-val="'+invTypeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				invtypeArr.push(invTypeOptions[i].value);
			}
		}
		if(customerOptions.length > 0) {
			for(var i=0;i<customerOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+customerOptions[i].value+'<span data-val="'+customerOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				var vendorValue = customerOptions[i].value;
				if(vendorValue.includes("&")){
					vendorValue = vendorValue.replace("&","-mgst-");
				}
				customerArr.push(vendorValue);
			}
		}
		if(reverseChargeOptions.length > 0) {
			for(var i=0;i<reverseChargeOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+reverseChargeOptions[i].text+'<span data-val="'+reverseChargeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				reverseChargeArr.push(reverseChargeOptions[i].value);
			}
		}
		if(branchOptions.length > 0) {
			for(var i=0;i<branchOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+branchOptions[i].text+'<span data-val="'+branchOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				branchArr.push(branchOptions[i].value);
			}
		}
		if(verticalOptions.length > 0) {
			for(var i=0;i<verticalOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+verticalOptions[i].text+'<span data-val="'+verticalOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				verticalArr.push(verticalOptions[i].value);
			}
		}
		if(customFieldOptions1.length > 0){
			for(var i=0;i<customFieldOptions1.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+customFieldOptions1[i].value+'<span data-val="'+customFieldOptions1[i].value+'" class="deltag" data-role="remove"></span></span>';
				var customValue = customFieldOptions1[i].value;
				if(customValue.includes("&")){
					customValue = customValue.replace("&","-mgst-");
				}
				customFieldArr1.push(customValue);
			}
		}
		if(customFieldOptions2.length > 0){
			for(var i=0;i<customFieldOptions2.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+customFieldOptions2[i].value+'<span data-val="'+customFieldOptions2[i].value+'" class="deltag" data-role="remove"></span></span>';
				var customValue = customFieldOptions2[i].value;
				if(customValue.includes("&")){
					customValue = customValue.replace("&","-mgst-");
				}
				customFieldArr2.push(customValue);
			}
		}
		if(customFieldOptions3.length > 0){
			for(var i=0;i<customFieldOptions3.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+customFieldOptions3[i].value+'<span data-val="'+customFieldOptions3[i].value+'" class="deltag" data-role="remove"></span></span>';
				var customValue = customFieldOptions3[i].value;
				if(customValue.includes("&")){
					customValue = customValue.replace("&","-mgst-");
				}
				customFieldArr3.push(customValue);
			}
		}
		if(customFieldOptions4.length > 0){
			for(var i=0;i<customFieldOptions4.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+customFieldOptions4[i].value+'<span data-val="'+customFieldOptions4[i].value+'" class="deltag" data-role="remove"></span></span>';
				var customValue = customFieldOptions4[i].value;
				if(customValue.includes("&")){
					customValue = customValue.replace("&","-mgst-");
				}
				customFieldArr4.push(customValue);
			}
		}
		$('#divFilters'+retTypeCode).html(filterContent);
		
		ereloadInvTable(retTypeCode, statusArr, invtypeArr, customerArr,reverseChargeArr,branchArr,verticalArr,customFieldArr1,customFieldArr2,customFieldArr3,customFieldArr4);
	}
	function ereloadInvTable(retTypeCode, statusArr, invtypeArr, customerArr,reverseChargeArr,branchArr,verticalArr,customFieldArr1,customFieldArr2,customFieldArr3,customFieldArr4){
		var pUrl = einvTableUrl[retTypeCode];
		var appd = '';
		if(statusArr.length > 0){
			appd+='&irnStatus='+statusArr.join(',');
		}
		if(invtypeArr.length > 0){
			appd+='&invoiceType='+invtypeArr.join(',');
		}
		if(customerArr.length > 0){
			appd+='&vendor='+customerArr.join(',');
		}
		if(reverseChargeArr.length > 0){
			appd+='&reverseCharge='+reverseChargeArr.join(',');
		}
		if(branchArr.length > 0){
			appd+='&branch='+branchArr.join(',');
		}
		if(verticalArr.length > 0){
			appd+='&vertical='+verticalArr.join(',');
		}
		if(customFieldArr1.length > 0){
			appd+='&customFieldText1='+customFieldArr1.join(',');
		}
		if(customFieldArr2.length > 0){
			appd+='&customFieldText2='+customFieldArr2.join(',');
		}
		if(customFieldArr3.length > 0){
			appd+='&customFieldText3='+customFieldArr3.join(',');
		}
		if(customFieldArr4.length > 0){
			appd+='&customFieldText4='+customFieldArr4.join(',');
		}
		pUrl += '?'+appd;
		einvTable[retTypeCode].ajax.url(pUrl).load();
	}

	function clearEInvFilters(retType) {
		for(i=1;i<11;i++){
			if(i == 7 || i == 8 || i == 9 || i == 10){
				if(($('#emultiselect'+retType+i).css('display') != 'none')){
					$('#emultiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
				}
			}else{
				$('#emultiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
			}
		}
			$('#divFilters'+retType.replace(" ", "_")).html('');
			$('.normaltable .filter').css("display","none");
			var retTypeCode = retType.replace(" ", "_");
			ereloadInvTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array());
		}
	function einitializeRemoveAppliedFilters(varRetType, varRetTypeCode){
		$('#divFilters'+varRetTypeCode).on('click', '.deltag', function(e) {
			var val = $(this).data('val');
			for(i=1;i<11;i++){
				if(i == 7 || i == 8 || i == 9 || i == 10){
					if(($('#emultiselect'+varRetTypeCode+i).css('display') != 'none')){
						$('#emultiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
					}
				}else{
					$('#emultiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
				}
			}
			applyEInvFilters(varRetType);
		});
	}
	
	function etcscheckval(){
		if($('#etcsval').is(":checked")){
			$('#etcsval').val('true');
			$('#etdstcssection ,#etcs_percent').removeAttr("readonly");
			$('#etdstcssection').removeAttr("disabled");
			var invtype = $('#idEInvType').val();
			$('#eallinvoicettfoot').append('<tr class="eapply_tcsOtds"><th colspan="12" class="tfootwitebg text-right tot_net_type"> TCS Amount </th><th class="tfootbg text-right" id="etcsfield">0.00</th><th class="tfootwitebg"><input type="hidden" name="tcstdsAmount" id="etcsamt" value="0.00"/><a href="#" style="margin-right: 2px;" onclick="einvedittcs()"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a></th></tr><tr class="eapply_tcsOtds"><th colspan="12" class="tfootwitebg text-right tot_net_type">Net Receivable(Total Invoice Value + TCS Amount)</th><th class="tfootbg etcsindformat" id="einvvalwithtcs">0.00</th><th class="tfootwitebg"></th></tr>');
			etdstcschange();
			if($('#einvoiceLevelCess').is(":checked")) {
				supportEinvoiceTCS(invtype,enableEinvDiscount,true);
			}else{
				supportEinvoiceTCS(invtype,enableEinvDiscount,false);
			}
		}else{
			$('#etcsval').val('false');
			$('#etdstcssection').val("");
			$('#etcs_percent').val("");
			$('#etdstcssection ,#etcs_percent').attr("readonly","true");$('#etdstcssection').attr("disabled",true);
			$('.eapply_tcsOtds').remove();
			}
		//$('#sortable_table').tableDnDUpdate();
	} 
	function einvedittcs(){
		var tcsamts = $('#etcsfield').text().replace(/,/g , '');
		$('#etcsamount').val(formatNumber(parseFloat(tcsamts).toFixed(2)));
		$('#etcsfield').html('<input type="text" onkeyup="echangeTcsAmount()" class="form-control text-right" id="etcsamount" value="'+tcsamts+'" style="font-size:14px;font-weight:600;"/>');
		
	}
	function etdstcscal(){
		var rettype = $('#retType').val();
		var tcspercent = $('#etcs_percent').val();
		var tcscalval = 0;
		if (otherconfigdetails.enableTCS == true) {
            tcscalval = $('#etotTaxable').text().replace(/,/g , '')*tcspercent/100;
		}else if (otherconfigdetails.enableTCS == false || otherconfigdetails.enableTCS == undefined){
			tcscalval = $('#etotTotal').text().replace(/,/g , '')*tcspercent/100;
		}
		var totinvval = $('#etotTotal').text().replace(/,/g , '');
		$('#etcsamount').val(parseFloat(tcscalval).toFixed(2));
		$('#einvvalwithtcs').html(parseFloat(totinvval)+parseFloat(tcscalval));
		$('#etcsfield').html(parseFloat(tcscalval).toFixed(2));
		$('#etcsamt').val(parseFloat(tcscalval).toFixed(2));
		OSREC.CurrencyFormatter.formatAll({
			selector: '.etcsindformat'
		});
	}
	function etdstcschange(){
		var tcspercent = $('#etdstcssection').val();
		var abcd = tcspercent.substring(tcspercent.lastIndexOf("(")+1,tcspercent.lastIndexOf(")"));
		$('#etcs_percent').val(abcd);	
		etdstcscal();
	}
	function echangeTcsAmount(){
		var tcsTot = 0;
		var tcsVal = 0;
		if($('#etcsamount').val() != ""){
			tcsVal = $('#etcsamount').val().replace(/,/g , '');
		}
		
		$('#etcsamt').val(parseFloat(tcsVal).toFixed(2));
		var tot = $('#etotTotal').text().replace(/,/g , '');
		tcsTot = formatNumber((parseFloat(tcsVal)+parseFloat(tot)).toFixed(2));
		$('#einvvalwithtcs').html(tcsTot);
	}
	
	function showEinvDeletePopup(retType,booksOrReturns,allOrSelected) {
		$('#deleteEinvModal').modal('show');
		$('#deleinvDetails').html("invoices of this month ?");
		if(allOrSelected == 'selectedInvs'){
			$('#btnDelete_einv').attr('onclick', "deleteSelectedEInvoices('GSTR1','"+booksOrReturns+"')");
		}else{
			$('#btnDelete_einv').attr('onclick', "deleteAllEInvoices('GSTR1','"+booksOrReturns+"')");
		}
	}
	
	function deleteSelectedEInvoices(retType,booksOrReturns){
			if(irnCancelArray.length > 0) {
					deleteSelectEInvoices(irnCancelArray,retType,booksOrReturns);
			} else {
				deleteSelectEInvoices(new Array(),retType,booksOrReturns);
			}
	}
	function deleteSelectEInvoices(invArray,retType,booksOrReturns){
		$('#deleteEinvModal').modal('hide');
	if(invArray.length <= 10){
		$('.invview_Process').removeClass('d-none').css("top","15%");
	}else if(invArray.length <= 25){
		$('.invview_Process').removeClass('d-none').css("top","10%");
	}else if(invArray.length <= 50){
		$('.invview_Process').removeClass('d-none').css("top","5%");
	}else if(invArray.length <= 100){
		$('.invview_Process').removeClass('d-none').css("top","3%");
	}else{
		$('.invview_Process').removeClass('d-none').css("top","1%");
	}
	
	var invdat = new Object;
	invdat.invnos = invArray;
	
	setTimeout(function(){
		$.ajax({
			url:contextPath+"/delSelectedeInvss"+urlSuffixs+"/"+retType+"/"+month+"/"+year+'?booksOrReturns='+booksOrReturns,
			type:'POST',
			async: false,
			cache: false,
			contentType: 'application/json',
			data: JSON.stringify(invdat),
			success : function(response) {
				location.reload(true);
			}, error: function(error){
				$('.invview_Process').addClass('d-none').css("width","37%");
			}
		});
	},1000);
	}
function deleteAllEInvoices(retType,booksOrReturns){
	$('#deleteEinvModal').modal('hide');
	$('.invview_Process').removeClass('d-none').css("top","1%");
	setTimeout(function(){
		$.ajax({
			url:contextPath+"/deleteAllEInvoices"+urlSuffixs+"/"+retType+"/"+month+"/"+year+'?booksOrReturns='+booksOrReturns,
			type:'POST',
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				location.reload(true);
				$('.invview_Process').addClass('d-none').css("width","37%");
			}, error: function(error){
				$('.invview_Process').addClass('d-none').css("width","37%");
			}
		});
	},1000);
}

function deleteSelectInvoices(invArray,retType,booksOrReturns){
		$.ajax({
			url: contextPath+"/delSelectedeInvss"+urlSuffixs+"/"+retType+"/"+month+"/"+year+"/"+invArray+'?booksOrReturns='+booksOrReturns,
				success : function(response) {
					location.reload(true);
				}
		});
}
	function eloadEinvoiceAuthStatus(varRetType,varRetTypeCode,connSttaus){
		if(filingOption == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>'){
			if(month == 3 || month == 2 || month == 1) {monthNames[month-1]='Jan - Mar';
			} else if(month == 6 || month == 5 || month == 4) {monthNames[month-1]='Apr - Jun';
			} else if(month == 9 || month == 8 || month == 7) {monthNames[month-1]='Jul - Sep';
			} else if(month == 12 || month == 11 || month == 10) {monthNames[month-1]='Oct - Dec';}
		}
		$('.esummary_retperiod').append(' Of <span style="color:#ff9900">'+monthNames[month-1]+' '+year+'</span> , Connection Status <span id="einv_authStatus" class="einv_authStatus" style="margin-left: 4px;"></span><span id="einv_msg"></span>');
		if(varRetType == 'EINVOICE'){
			if(connSttaus == "Active"){
				$('.einv_authStatus').html('Active').css("color","green");
				$('#synceinv').removeClass("disabled");
			}else if(connSttaus == ""){
				$('#synceinv').addClass("disabled");
				$('.einv_authStatus').html('InActive, <a href="'+contextPath+'/cp_upload/'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=einvoiceconfig">configure now</a>').css("color","orange");
			}else{
				$('#synceinv').addClass("disabled");
				$('.einv_authStatus').html('<span style="color:red;">Expired</span><a href="#" onclick="configEinvauthentication()" style="color:green;"> <span id="inactive_btn">Authenticate Now</span></a>');
				
			}
			
		}
	}
	function configEinvauthentication(){
		closeNotifications();
		$('#einv_authStatus').html('<span style="color:red;">Expired</span><a href="#" style="color:green;"> <span id="inactivebtn">Authenticating...</span></a>');
		$.ajax({
			type: "GET",
			url: contextPath+"/einvAuthconfigdetails"+commonturnOverSuffix+"/"+varRetType+"/"+month+"/"+year,
			async: true,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				if(response.connStaus == "Active"){
					$('#einv_authStatus').html('Active').css("color","green");
					$('#synceinv').removeClass("disabled");
					einvAuthSttauss = "Active";
				}else{
					$('#einv_msg').html('<span style="color:red;">Error</span> : Please Check Your <a href="'+contextPath+'/cp_upload/'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=einvoiceconfig">Configuration</a>');
					$('#einv_authStatus').html('');
				}	
			},
			error : function(e, status, error) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	function showBuyerModal(){
		$('#ebuyeraddrModal').modal("show");
		var invCategory = $('#invCategory').val();
		$('#buyerAddress1_msg,#buyerloc_msg,#buyerPincode_msg,#buyerStatecode_msg').text("");
		var invtype = $('#idEInvType').val();
		/*if(invtype == "B2B" || invtype == "Credit/Debit Notes"){
			$('#buyerdetgstin').css("display","block");
			$('#buyerGstno').attr("required",true);
			if(invCategory == "EXPWP" || invCategory == "EXPWOP"){
				$('#buyerGstno').removeAttr("required");
				$('#buyerdetgstinlabel').removeClass("astrich");
			}else{
				$('#buyerdetgstinlabel').addClass("astrich");
			}
		}else{
			$('#buyerGstno').removeAttr("required");
			$('#buyerdetgstin').css("display","none");
			$('#buyerdetgstinlabel').addClass("astrich");
		}*/
		$('#buyerAddress1,#buyerloc,#buyerStatecode').attr("required",true);
		   var bstateoptions = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/stateconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					list: {
						onClickEvent: function() {
							var rettype = $('#retType').val();
						},
						onLoadEvent: function() {
							if($("#eac-container-buyerStatecode ul").children().length == 0) {
								$("#buyerStatecodeempty").show();
							}else {
								$("#buyerStatecodeempty").hide();
							}
						},
						maxNumberOfElements: 37
					}
				};
		$("#buyerStatecode").easyAutocomplete(bstateoptions);
		//$('#buyerGstno').val($('#buyerDtls_gstin').val());
		//$('#buyerTradeName').val($('#buyerDtls_lglNm').val());
		$('#buyerAddress1').val($('#buyerDtls_addr1').val());
		$('#buyerAddress2').val($('#buyerDtls_addr2').val());
		if(invtype != "Exports"){
			$('#buyerloc').val($('#buyerDtls_loc').val());
			if($('#buyerDtls_pin').val() != ""){
				$('#buyerPincode').val(parseInt($('#buyerDtls_pin').val()));	
			}else{
				$('#buyerPincode').val(parseInt("0"));	
			}
			$('#buyerStatecode').val($('#buyerDtls_state').val());
		}else{
			$('#buyerloc').val("Other Teritory");
			$('#buyerPincode').val(parseInt("999999"));	
			$('#buyerStatecode').val("96");
		}
	}
	function showDispatchModal(){
		$('#edispatchaddrModal').modal("show");
		$('#dispatchTradeName,#dispatchAddress1,#dispatchloc,#dispatchPincode,#dispatchStatecode').attr("required",true);
		   var bstateoptions = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/stateconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					list: {
						onClickEvent: function() {
							var rettype = $('#retType').val();
						},
						onLoadEvent: function() {
							if($("#eac-container-dispatchStatecode ul").children().length == 0) {
								$("#dispatchStatecodeempty").show();
							}else {
								$("#dispatchStatecodeempty").hide();
							}
						},
						maxNumberOfElements: 37
					}
				};
		$("#dispatchStatecode").easyAutocomplete(bstateoptions);
		$('#dispatchTradeName').val($('#dispatcherDtls_nm').val());
		$('#dispatchAddress1').val($('#dispatcherDtls_addr1').val());
		$('#dispatchAddress2').val($('#dispatcherDtls_addr2').val());
		$('#dispatchloc').val($('#dispatcherDtls_loc').val());
		$('#dispatchPincode').val(parseInt($('#dispatcherDtls_pin').val()));	
		$('#dispatchStatecode').val($('#dispatcherDtls_stcd').val());
	}
function showShipmentModal(){
	$('#eshipaddrModal').modal("show");
	$('#shipmentTradeName,#shipmentAddress1,#shipmentloc,#shipmentPincode,#shipmentStatecode').attr("required",true);
	var invCategory = $('#invCategory').val();
	if(invCategory == "EXPWP" || invCategory == "EXPWOP"){
		$('#shipmentGstno').removeAttr("required");
	}
	 var bstateoptions = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/stateconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				list: {
					onClickEvent: function() {
						var rettype = $('#retType').val();
					},
					onLoadEvent: function() {
						if($("#eac-container-shipmentStatecode ul").children().length == 0) {
							$("#shipmentStatecodeempty").show();
						}else {
							$("#shipmentStatecodeempty").hide();
						}
					},
					maxNumberOfElements: 37
				}
			};
	   $("#shipmentStatecode").easyAutocomplete(bstateoptions);
	   $('#shipmentGstno').val($('#shipmentDtls_gstin').val());
	   $('#shipmentName').val($('#shipmentDtls_lglNm').val());
	   $('#shipmentTradeName').val($('#shipmentDtls_trdNm').val());
		$('#shipmentAddress1').val($('#shipmentDtls_addr1').val());
		$('#shipmentAddress2').val($('#shipmentDtls_addr2').val());
		$('#shipmentloc').val($('#shipmentDtls_loc').val());
		$('#shipmentPincode').val(parseInt($('#shipmentDtls_pin').val()));	
		$('#shipmentStatecode').val($('#shipmentDtls_stcd').val());
	}
function buyerAddrDetails(){
	if(validateBuyerDetails()){
		$('#buyerDtls_gstin').val($('#ebilledtogstin').val());
		$('#buyerDtls_lglNm').val($('#invoiceTradeName').val());
		$('#buyerDtls_addr1').val($('#buyerAddress1').val());
		$('#buyerDtls_addr2').val($('#buyerAddress2').val());
		$('#buyerDtls_loc').val($('#buyerloc').val());
		$('#buyerDtls_pin').val(parseInt($('#buyerPincode').val()));
		$('#buyerDtls_state').val($('#buyerStatecode').val());
		var pos = $('#invoiceStatecode').val().split("-");
		$('#buyerDtls_pos').val(pos[0]);
		if($('#samedispatchaddress').is(":checked")){
			$('#shimentAddrEdit').addClass('d-none');
			 $('#shipmentGstno').val($('#ebilledtogstin').val());
			   $('#shipmentName').val($('#invoiceTradeName').val());
			   $('#shipmentTradeName').val($('#invoiceTradeName').val());
				$('#shipmentAddress1').val($('#buyerAddress1').val());
				$('#shipmentAddress2').val($('#buyerAddress1').val());
				$('#shipmentloc').val($('#buyerloc').val());
				$('#shipmentPincode').val(parseInt($('#buyerPincode').val()));	
				$('#shipmentStatecode').val($('#buyerStatecode').val());
				$('#shipmentDtls_gstin').val($('#shipmentGstno').val());
				$('#shipmentDtls_trdNm').val($('#shipmentTradeName').val());
				$('#shipmentDtls_lglNm').val($('#shipmentName').val());
				$('#shipmentDtls_addr1').val($('#shipmentAddress1').val());
				$('#shipmentDtls_addr2').val($('#shipmentAddress2').val());
				$('#shipmentDtls_loc').val($('#shipmentloc').val());
				$('#shipmentDtls_pin').val($('#shipmentPincode').val());
				$('#shipmentDtls_stcd').val($('#shipmentStatecode').val());
		}
		$('#buyerform').find('input').each(function(){
			if(!$(this).prop('required')){
			}else{
				var bca = $(this).val();
				   if( bca == ''){
					  $(this).parent().addClass('has-error has-danger');
				   }else{
					  $(this).parent().removeClass('has-error has-danger');
					 
				   }
			}
		});
		//$('#invoiceTradeName').val($('#buyerTradeName').val());
		$('#egstin_lab').val($('#buyerGstno').val());
		var einvtype = $('#idEInvType').val();
		if(einvtype == "B2B" || einvtype == "Credit/Debit Notes"){
			//$('#ebilledtogstin').val($('#buyerGstno').val()).trigger("change");
			var addr = $('#buyerAddress1').val().substring(1,15)+"...";
			if($('#samedispatchaddress').is(":checked")){
				$('#einvbuyerAddr,#einvshipmentAddr').html($('#ebilledtogstin').val()+","+$('#invoiceTradeName').val()+","+addr+","+$('#buyerloc').val()+","+$('#buyerStatecode').val()+","+$('#buyerPincode').val());
			}else{
				$('#einvbuyerAddr').html($('#ebilledtogstin').val()+","+$('#invoiceTradeName').val()+","+addr+","+$('#buyerloc').val()+","+$('#buyerStatecode').val()+","+$('#buyerPincode').val());
			}
		}else{
			var addr = $('#buyerAddress1').val().substring(1,15)+"...";
			if($('#samedispatchaddress').is(":checked")){
				$('#einvbuyerAddr,#einvshipmentAddr').html($('#invoiceTradeName').val()+","+addr+","+$('#buyerloc').val()+","+$('#buyerStatecode').val()+","+$('#buyerPincode').val());
			}else{
				$('#einvbuyerAddr').html($('#invoiceTradeName').val()+","+addr+","+$('#buyerloc').val()+","+$('#buyerStatecode').val()+","+$('#buyerPincode').val());
			}
		}
		$('.buyerAddrText').html('Edit');
		$('#buyer_save').attr("data-dismiss","modal");
	}
}


function validateBuyerDetails(){
		var c = 0;
	//var buyerGstno = $('#buyerGstno').val();
	//var buyerTradeName = $('#buyerTradeName').val();
	var buyerAddress1 = $('#buyerAddress1').val();
	var buyerloc = $('#buyerloc').val();
	var buyerPincode = $('#buyerPincode').val();
	var buyerStatecode = $('#buyerStatecode').val();
	
	//buyerGstno = buyerGstno.replace(/ +/g, "");
	//buyerTradeName = buyerTradeName.replace(/ +/g, "");
	var invtype = $('#idEInvType').val();
	var invCategory = $('#invCategory').val();
	/*if(invtype == "B2B" || invtype == "Credit/Debit Notes"){
		if(invCategory == "EXPWP" || invCategory == "EXPWOP"){
			$('#buyerGstno_msg').text(""); 
		}else{
			if(buyerGstno==""){
				$('#buyerGstno_msg').text("Please enter GSTIN Number"); 
				c++;
			}else{  
				$('#buyerGstno_msg').text(""); 
			}
		}
	}
	if(buyerTradeName==""){
		$('#buyerTradeName_msg').text("Please enter Name");
		c++;
	}else{
		$('#buyerTradeName_msg').text("");
	}*/
if(buyerAddress1==""){
		$('#buyerAddress1_msg').text("Please enter Address");
		c++;
	}else{
		$('#buyerAddress1_msg').text("");
	}
if(buyerloc==""){
		$('#buyerloc_msg').text("Please enter Location");
		c++;
	}else{
		$('#buyerloc_msg').text("");
	}
if(buyerPincode==""){
		$('#buyerPincode_msg').text("Please enter Pincode");
		c++;
	}else{
		$('#buyerPincode_msg').text("");
	}
if(buyerStatecode==""){
		$('#buyerStatecode_msg').text("Please enter state");
		c++;
	}else{
		$('#buyerStatecode_msg').text("");
	}	
	return c==0; 
}
function dispatchAddrDetails(){
		$('#dispatcherDtls_nm').val($('#dispatchTradeName').val());
		$('#dispatcherDtls_addr1').val($('#dispatchAddress1').val());
		$('#dispatcherDtls_addr2').val($('#dispatchAddress2').val());
		$('#dispatcherDtls_loc').val($('#dispatchloc').val());
		$('#dispatcherDtls_pin').val(parseInt($('#dispatchPincode').val()));
		$('#dispatcherDtls_stcd').val($('#dispatchStatecode').val());
		$('#dispatchform').find('input').each(function(){
		    if(!$(this).prop('required')){
		    }else{
		    	var bca = $(this).val();
			 	   if( bca == ''){
			 		  $(this).parent().addClass('has-error has-danger');
			 		 $('#dispatch_save').removeAttr("data-dismiss");
			 	   }else{
			 		  $(this).parent().removeClass('has-error has-danger');
			 		 $('#dispatch_save').attr("data-dismiss","modal");
			 	   }
		    }
		});
		var addr = $('#dispatchAddress1').val().substring(1,15)+"...";
		$('#einvdispatchAddr').html($('#dispatchTradeName').val()+","+addr+","+$('#dispatchloc').val()+","+$('#dispatchStatecode').val()+","+$('#dispatchPincode').val());
	}
function shipmentAddrDetails(){
	$('#shipmentDtls_gstin').val($('#shipmentGstno').val());
	$('#shipmentDtls_trdNm').val($('#shipmentTradeName').val());
	$('#shipmentDtls_lglNm').val($('#shipmentName').val());
	$('#shipmentDtls_addr1').val($('#shipmentAddress1').val());
	$('#shipmentDtls_addr2').val($('#shipmentAddress2').val());
	$('#shipmentDtls_loc').val($('#shipmentloc').val());
	$('#shipmentDtls_pin').val($('#shipmentPincode').val());
	$('#shipmentDtls_stcd').val($('#shipmentStatecode').val());
	$('#shipmentform').find('input').each(function(){
	    if(!$(this).prop('required')){
	    }else{
	    	var bca = $(this).val();
		 	   if( bca == ''){
		 		  $(this).parent().addClass('has-error has-danger');
		 		$('#shipment_save').removeAttr("data-dismiss");
		 	   }else{
		 		  $(this).parent().removeClass('has-error has-danger');
			 	 $('#shipment_save').attr("data-dismiss","modal");
		 	   }
	    }
	});
	var addr = $('#shipmentAddress1').val().substring(1,15)+"...";
	$('#einvshipmentAddr').html($('#shipmentGstno').val()+","+$('#shipmentName').val()+","+addr+","+$('#shipmentloc').val()+","+$('#shipmentStatecode').val()+","+$('#shipmentPincode').val());
}

function allInvGenerateIRN(){
	esubscriptioncheck();
	if(esubscriptionchecks == ""){
		if(einvAuthSttauss == "Active"){
			$('#genIRN').addClass('btn-loader');
			if(irnCancelArray != ""){
				$.ajax({
					url: contextPath+"/allInvgenerateIRN/"+urlSuffixs+"/EINVOICE/"+varUserType+"/"+month+"/"+year,
					type: "POST",
					async: false,
					cache: false,
					dataType:"json",
					data:JSON.stringify(irnCancelArray),
					contentType: 'application/json',
					success : function(response){
						$('#genIRN').removeClass('btn-loader');
						location.reload(true);
					},error:function(err){
						$('#genIRN').removeClass('btn-loader');
						location.reload(true);
					}
				});
			}
		}else{
			einvErrorNotification(einvAuthSttauss);
		}
	}else{
		if(esubscriptionchecks == 'Your subscription has expired. Kindly subscribe to proceed further!') {
			if(varUserType == 'suvidha' || varUserType == 'enterprise'){
				errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
			}else{
				errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+_getContextPath()+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
			}
		}else {
			errorNotification(esubscriptionchecks);
		}
	}
}

function performEinvImport(btn) {
	var templateType = $('#etemplateType').val();
	var filename =  $('#emessages').html();
	if(templateType != ''){
	$('#etemplateType_Msg').html('');
	if(filename != ''){
		$(btn).addClass('btn-loader');		
		$('#einvimportModalForm').ajaxSubmit( {
			url: $("#einvimportModalForm").attr("action"),
			dataType: 'json',
			type: 'POST',
			cache: false,
			success: function(response) {
				$('#emsgImportProgress').show();$(btn).removeClass('btn-loader');
				if(response && response.summaryList && response.summaryList.length > 0) {
					response.summaryList.forEach(function(inv) {$('#eimportSummaryBody').append("<tr><td>"+inv.name+"</td><td class='blocktxt'>"+inv.total+"</td><td class='blocktxt'>"+inv.totalinvs+"</td><td class='greentxt'>"+inv.success+"</td><td class='greentxt'>"+inv.invsuccess+"</td><td class='redtxt'>"+inv.failed+"</td><td class='redtxt'>"+inv.invfailed+"</td></tr>");});
					$('#eidImportSummary').show();$('#einvidImportBody').hide();
					if(response.month) {month = response.month;	year = response.year;}
					if(response.showLink) {$('#einverrorXls').show();}
				} else if(response && response.error) {$('#eimportSummaryError').parent().show().html(response.error);}
			},
			error: function(e) {$('#emsgImportProgress').hide();	$(btn).removeClass('btn-loader');$('#eimportSummaryError').parent().show().html("Something went wrong, Please try again");}
		});
	}else{$('#eimportSummaryError').parent().show().html("Please Select File");}
	}else{$('#etemplateType_Msg').html('Please Select Template Type');}
}
function chooseefileSheets(){
	$('#efileselect1')[0].click();
}
$('#efileselect1').change(function(e){
	var fileName = e.target.files[0].name;
	$('#emessages').html(fileName);
	$('#eimporterr').css('display','none');
});
function updateESheets() {
	$('#eidSheet').show();$('.esheets').hide();var invType = $('#etemplateType').val();if(invType != 'tally'){invType = 'mastergst';}$('#'+invType+'Sheet').show();
}

$(document).on('mouseover','#eabb1', function (event) {
	$(this).find('#etax_rate_drop').css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"10%","margin-top":"5px"});
}).on('mouseleave','#eabb1',  function(){
    	$(this).next().css("display","none");
    	$(this).find("#etax_rate_drop").stop(true, true).delay(100).fadeOut(300);
   }); 

function updateInvTypeOfSupply(){
		var invType = $('#invCategory').val();
		var eInvType = $('#idEInvType').val();
		var igstOnIntra = $('#igstOnIntra').val();
		if(invType == 'SEZWP' || invType == 'EXPWP') {
			einterStateFlag=false;
		}else if(invType == 'R'){
			einterStateFlag=true;
		}
		if(eInvType != 'Exports' && igstOnIntra == 'Y'){
			einterStateFlag=false;
		}
		if(invType == 'SEZWOP' || invType == 'EXPWOP'){
			$("#includetax").prop("disabled",true);
			$(".taxrate_textDisable").prop('disabled',true);
		}else{
			$("#includetax").prop("disabled",false);
			$(".taxrate_textDisable").prop('disabled',false);
		}
		if(invType == 'DEXP' || invType == 'B2B'){
			$(".taxrate_textDisable").prop('disabled',false);
			$(".taxrate_textDisable").prop('readonly',false);
		}
		if(eInvType != 'Exports' && (invType == 'EXPWP' || invType == 'EXPWOP')){
			$('#ebilledtogstin').removeAttr("required");
			$('#egstin_lab').removeClass("astrich");
		}else if(eInvType != 'Exports'){
			if(eInvType != 'B2C'){
				$('#ebilledtogstin').attr("required","required");
				$('#egstin_lab').addClass("astrich");
			}
		}
		var i=1;
		$.each($("#all_einvoice tr"),function() { 
			findEinvTaxAmount(i);
			i++;
		})
	}


	function updateCurrencyCode(){
		var rtype = $('.addcurrencyCode').val();
		$('#currencytext').html(rtype);
		$('.usd-lable').html("<span style='padding-left:2px'>1 "+rtype+" = </span>");
		$.ajax({
			url: contextPath+'/getCountrycode?code='+rtype,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(codes) {
			$('#einvCountryCode').val(codes.countrycode);
			}
		});
	}
function etcstdsoptions(returntype){
		$('#etdstcssection').children('option').remove();
		$('#etdstcssection').append($("<option></option>").attr("value","").text("-- Select Section --"));
		$('#etdstcssection').append($("<option></option>").attr("value","206C(1)").text("206C"));
		
	}
	
	function einvErrorNotification(connSttaus) {
		closeNotifications();
		var errorMsg = "Connection Status ";
		if(connSttaus == "Active"){
			}else if(connSttaus == ""){
				errorMsg += 'InActive, <a href="'+contextPath+'/cp_upload/'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=einvoiceconfig">configure now</a>';
			}else{
				errorMsg += '<span style="color:red;">Expired </span><a href="#" onclick="configEinvauthentication()" style="color:green;"> <span id="inactive_btn">Authenticate Now</span></a>';
				
			}
		$('#errorMessage').html(errorMsg);$('#errorMessage').parent().show();
	}

function updateEinvInvoiceStatus(){
	esubscriptioncheck();
	if(esubscriptionchecks == ""){
		var pUrl = _getContextPath()+'/syncirnstatus/'+commonturnOverSuffix+'/EINVOICE/'+Paymenturlprefix;
		$('#refreshEinv').addClass('fa-spin');
				window.setTimeout(function(){
					$('#refreshEinv').removeClass("fa-spin");
					}, 4000);
		$.ajax({
			url : pUrl,
			async: true,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				window.location.href = _getContextPath()+'/einvoice'+commonSuffix+'/EINVOICE/'+clientId+'/'+Paymenturlprefix;
			}
		});
	}else{
		if(esubscriptionchecks == 'Your subscription has expired. Kindly subscribe to proceed further!') {
			if(varUserType == 'suvidha' || varUserType == 'enterprise'){
				errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
			}else{
				errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+_getContextPath()+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
			}
		}else {
			errorNotification(esubscriptionchecks);
		}
	}
}

function einvsubscriptioncheck(){
	$.ajax({
		url : _getContextPath()+'/subscriptionCheck/'+clientId+'/'+userId,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response.status_cd == '0') {
				if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
					if(varUserType == 'suvidha' || varUserType == 'enterprise'){
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
					}else{
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+_getContextPath()+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
					}
				}else {
					errorNotification(response.status_desc);
				}
			}
		}
	});
}

function esubscriptioncheck(){
	esubscriptionchecks = "";
	$.ajax({
		url : _getContextPath()+'/einvsubscriptionCheck/'+clientId+'/'+userId,
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response) {
			esubscriptionchecks = response;
		}
	});
}
function getEinvCustomFieldsData(data,returnType,addFlag,invoiceCustomData){
	if(returnType == 'EINVOICE' || returnType == 'GSTR1'){
		var content ="";
		var indexarry = new Array();
		if(data.einvoice){
			for(var i=0;i<data.einvoice.length;i++){
				var index = i + 1;
				var einvoiceData = data.einvoice[i];
				if(einvoiceData != undefined){
					if(einvoiceData.customFieldType == 'input'){
						var require = einvoiceData.isMandatory ? 'required' : ''; 
						var astrich = einvoiceData.isMandatory ? 'astrich' : '';
						content += '<div class="col-md-6 col-sm-12 form-group" id="einv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+einvoiceData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+einvoiceData.customFieldName+'"/><input class="form-control" name="customField'+index+'" id="customField'+index+'" data-error="Please enter this value" placeholder="'+einvoiceData.customFieldName+'" '+require+'></div>';
					}else if(einvoiceData.customFieldType == 'list'){
						var require = einvoiceData.isMandatory ? 'required' : ''; 
						var astrich = einvoiceData.isMandatory ? 'astrich' : '';
						content += '<div class="col-md-6 col-sm-12 form-group" id="einv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+einvoiceData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+einvoiceData.customFieldName+'"/><select class="form-control" name="customField'+index+'" data-error="Please enter this value" id="customField'+index+'" '+require+'><option value="">-Select-</option>';
						for(var j = 0; j < einvoiceData.typeData.length; j++){
							content += "<option value=\'"+einvoiceData.typeData[j]+"\'>"+einvoiceData.typeData[j]+"</option>";
						}
						content +='</select></div>';
					}else if(einvoiceData.customFieldType == 'checkB'){
						content += '<div class="col-md-6 col-sm-12" id="einv_CustField1"><h6 id="invcustFieldText1">'+einvoiceData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+einvoiceData.customFieldName+'"/>';
						if(invoiceCustomData){
								if(einvoiceData.customFieldName == invoiceCustomData.customField1 || einvoiceData.customFieldName ==  invoiceCustomData.customField2 || einvoiceData.customFieldName == invoiceCustomData.customField3 || einvoiceData.customFieldName ==  invoiceCustomData.customField4){
									content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.customFieldName+'" checked>&nbsp;<label>'+einvoiceData.customFieldName+'</label>&nbsp;';
								}else{
									content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.customFieldName+'">&nbsp;<label>'+einvoiceData.customFieldName+'</label>&nbsp;';
								}
							}else{
								content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.customFieldName+'">&nbsp;<label>'+einvoiceData.customFieldName+'</label>&nbsp;';
							}
						content += '</div>';
						indexarry.push(index);
						index++;
					}else if(einvoiceData.customFieldType == 'radio'){
						content += '<div class="col-md-6 col-sm-12" id="einv_CustField1"><h6 id="invcustFieldText1">'+einvoiceData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+einvoiceData.customFieldName+'"/>';
						for(var j = 0; j < einvoiceData.typeData.length; j++){
							if(invoiceCustomData){
								if(einvoiceData.typeData[j] == invoiceCustomData.customField1 || einvoiceData.typeData[j] ==  invoiceCustomData.customField2 || einvoiceData.typeData[j] == invoiceCustomData.customField3 || einvoiceData.typeData[j] ==  invoiceCustomData.customField4){
									content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.typeData[j]+'" checked = "true">&nbsp;<label>'+einvoiceData.typeData[j]+'</label>&nbsp;';
								}else{
									content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.typeData[j]+'">&nbsp;<label>'+einvoiceData.typeData[j]+'</label>&nbsp;';
								}
							}else{
								content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.typeData[j]+'">&nbsp;<label>'+einvoiceData.typeData[j]+'</label>&nbsp;';
							}	
						} 
						content += '</div>';
						indexarry.push(index);
						index++;
					}
				}
				$('#einvCustomFields_row').html(content);
			}
			
			if(addFlag){
			}else{
				for(var j=1;j<5;j++){
					if(!indexarry.includes(j)){
						if(j==1){
							if(invoiceCustomData.customField1 != "" && invoiceCustomData.customField1 != null){
								$('#customField1').val(invoiceCustomData.customField1);
							}
						}else if(j==2){
							if(invoiceCustomData.customField2 != "" && invoiceCustomData.customField2 != null){
								$('#customField2').val(invoiceCustomData.customField2);
							}
						}else if(j==3){
							if(invoiceCustomData.customField3 != "" && invoiceCustomData.customField3 != null){
								$('#customField3').val(invoiceCustomData.customField3);
							}
						}else if(j==4){
							if(invoiceCustomData.customField4 != "" && invoiceCustomData.customField4 != null){
								$('#customField4').val(invoiceCustomData.customField4);	
							}
						}
					}
				}
				
			}
		}
	}
}

function updateECustomerPan(value) {
	if(value.length == 15) {
		$('#ecustomerPanNumber').val(value.substring(2,12));
		$('.epan_number .with-errors').html('');
		$('.epan_number').removeClass('has-error has-danger');
		$.ajax({
			url: _getContextPath()+"/srchstatecd?code="+value.substring(0,2),
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response) {
					$('#estate').val(response.name);
				}
			}
		});
	}
}
function closeEAddCustomer(modalId){
	$('#'+modalId).modal('hide');
}

function termdrp(btn){
	$(btn).attr('tabindex', 1).focus();
	$(btn).toggleClass('active');
	$(btn).find('.dropdown-menu').slideToggle(300);
}

$('.eadddropdown').focusout(function () {
	$(this).removeClass('active');
	$(this).find('.dropdown-menu').slideUp(300);
});
function showTermsEList(type){
	if(epaymentTerms.length > 0){
		if(type == "invpopup"){
			$('.etermMenu').html('');
			$('.etermMenu').append('<li id="eselect" class="term_li" onclick = "termSel(this)">- Select -</li>');
		}
		for(var i =0;i<epaymentTerms.length;i++){
			var days;
			if(type == "invpopup"){
				days = epaymentTerms[i].termName == "Immediate" ? "Immediate" : epaymentTerms[i].noOfDays+ " Days";
				$('.etermMenu').append('<li id="eterm'+days+'" class="eterm_li" onclick = "etermSel(this)">'+days+'</li>');
			}else{
				var no = i +1;
				$('#eterm_name'+no).val(epaymentTerms[i].termName);
				$('#eterm_days'+no).val(epaymentTerms[i].noOfDays);
			}
		}
		if(type == "invpopup"){
			$('.etermMenu').append('<li id="eaddbtn"><a onclick="configeTerms()" data-toggle="modal" data-target="#addeTermModal"><i class="fa fa-plus-circle"></i>Configure Term</a></li>');
		}
	}
}
function etermSel(li){
		if($(li).text() != "Configure Term"){
			$(li).parents('.eadddropdown').find('span').text($(li).text());
			$(li).parents('.eadddropdown').find('input').attr('value', $(li).text());
			var input = '<strong>' + $(li).parents('.eadddropdown').find('input').val() + '</strong>';
			if($(li).text() != "- Select -"){
				var val = $(li).text();
				var invdt = $('#einvoicedate').val();
				var mnyr = invdt.split("/");var dt = parseInt(mnyr[0]);var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
				var d = new Date(yr, mn-parseInt(1), dt);
				if(val == 'Immediate'){
					d.setDate(d.getDate());
				}else{
					d.setDate(d.getDate() + parseInt(val));
				}
				var day = d.getDate();
				var month = d.getMonth()+parseInt(1);
				var year=d.getFullYear();
				var mndate = ('0' + day).slice(-2) + '/' + ('0' + (month)).slice(-2) + '/' + year;
				$('#eduedate_div').datetimepicker({
					value: mndate,
					timepicker: false,
					format: 'd/m/Y',
					maxDate: false
				});
			}else{
				$('#eduedate_div').val("");
				$(li).parents('.eadddropdown').find('input').attr('value', '');
			}
		}
	
	
}

function configeTerms(){
	etermsRowCount = 0;
	$('#addeTermModal').modal('show');
	var rowCount = epaymentTerms.length == 0 ? 6 : epaymentTerms.length;
	$('#eConfigTemsTable_body').html('');
	for(var i =1;i<=rowCount;i++){
		addeTermsrow();
	}
	showTermsEList("Termpopup");
	if(epaymentTerms.length == 0){
		$('#eterm_name1').val("Immediate");$('#eterm_days1').val("0");
		$('#eterm_name2').val("Net 7");$('#eterm_days2').val("7");
		$('#eterm_name3').val("Net 15");$('#eterm_days3').val("15");
		$('#eterm_name4').val("Net 30");$('#eterm_days4').val("30");
		$('#eterm_name5').val("Net 45");$('#eterm_days5').val("45");
		$('#eterm_name6').val("Net 60");$('#eterm_days6').val("60");
	}
}
function addeTermsrow(){
	etermsRowCount++;
	var table = null;
	var rowPrefix = null;
	var row = null;
	var table_len=etermsRowCount;
	table = document.getElementById("econfigTermsTable");
	//rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='no_row1'>"+table_len+"</span></td><td align='center'><input type='text' class='form-control' id='term_name"+table_len+"' name='termName'  placeholder='Terms Name'/></td><td align='center'><input type='text' class='form-control' id='term_days"+table_len+"' name='noOfDays'  placeholder='Terms Days'/></td><td align='center' width='2%'><a href='javascript:void(0)' class='term_delete' onclick='deleteeTerm("+table_len+")'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td></tr>";
	$('#eConfigTemsTable_body').append("<tr id='"+table_len+"' draggable='true' class='rowshadow' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='no_row1'>"+table_len+"</span></td><td align='center'><input type='text' class='form-control' id='eterm_name"+table_len+"' name='termName'  placeholder='Terms Name'/></td><td align='center'><input type='text' class='form-control' id='eterm_days"+table_len+"' name='noOfDays'  placeholder='Terms Days'/></td><td align='center' width='2%'><a href='javascript:void(0)' class='term_delete' onclick='deleteeTerm("+table_len+")'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td></tr>");
	$('form[name="einvoceform"]').validator('update');
}
function deleteeTerm(no){
	var tab=document.getElementById("econfigTermsTable");
	tab.deleteRow(no);
	etermsRowCount--;
	$("#eConfigTemsTable_body tr").each(function(index) {
		 $(this).attr('id',index+1);
		 $(this).find("#no_row1").html(index+1);
		 var rowno = (index+1).toString();
		 var rownoo = (index).toString();
		 $(this).find('input').each (function() {
				var inputname1 = $(this).attr('class');
				var inputid1 = $(this).attr('id');
				var inputname = $(this).attr('name');
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
				if(det != 'deleteTerm'){
				}else{
					var abcd = $(this).attr('onclick');
			   	    abcd = replaceAt(abcd,18,rowno);
			   	    $(this).attr('onclick',abcd);
				}
			});
	});
}
function saveePaymentTerms(){
	var termsArray = new Array();
	var trowCount = $('#eConfigTemsTable_body tr').length;
	var name,days;
	if(etermsRowCount <= trowCount) {
		for(var i=1;i<=trowCount;i++) {
			name = $('#eterm_name'+i).val();
			days = $('#eterm_days'+i).val();
			if(days == "Immediate"){
				days=0;
			}
			var termsData=new Object();
			termsData.termName=name;
			termsData.noOfDays=days;
			termsArray.push(termsData);
		}
	}
	$.ajax({
		url : contextPath+'/configureTerms/'+clientId,
		type: "POST",
		data: JSON.stringify(termsArray),
		contentType: 'application/json',
		success : function(response) {
			epaymentTerms = response;
			$('.etermMenu li').remove();
			$('.etermMenu').append('<li id="eselect" class="term_li" onclick = "termSel(this)">- Select -</li>');
			for(var i=0;i<response.length;i++){
				var days = response[i].termName == "Immediate" ? "Immediate" : response[i].noOfDays+ " Days";
				$('.etermMenu').append('<li id="eterm'+days+'" class="eterm_li" onclick = "etermSel(this)">'+days+'</li>');
			}
			closeTermModal('addeTermModal');
			$('.etermMenu').append('<li id="eaddbtn"><a onclick="configeTerms()" data-toggle="modal" data-target="#addeTermModal"><i class="fa fa-plus-circle"></i>Configure Term</a></li>');
		}
	});
}
function closeeTermModal(id){
	$('#'+id).modal('hide');
	$('.modal-d').css("display","none");
	etermsRowCount =1;
}

function closeSendMailmodal(modalId){
	$('#einvcustomer_emailid').parent().removeClass('has-error has-danger');
	$('#send_emailModal').modal('hide');
	$('.einvsuccessEmailmsg').addClass("d-none");
}

function sendEmailToBuyers(invoiceid,invoiceno,billedtoname,retType){
	$('#einvemail_returnType,#einvcustomer_name').val("");
	$('#send_emailModal').modal('show');
	$('#einvemail_invoiceId').val(invoiceid);
	$('#einvemail_returnType').val("GSTR1");
	$('.einvsignLink').attr('href',''+contextPath+'/cp_upload'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=');
	if(clientDetails.clientSignature == ""){
		$('.einvsignLink').html("Edit Email Signature");
		$('#einvemail_custdetails').html(clientDetails.businessname+"<br>"+clientDetails.email+"<br>"+clientDetails.mobilenumber);
	}else{
		$('.einvsignLink').html("Edit Email Signature");
		var clientdetails = clientDetails.clientSignature;
		$('#einvemail_custdetails').html(clientdetails.replaceAll("#mgst#", "</br>"));
	}
	if(billedtoname != ""){
		$('#einvcustomer_name').val(billedtoname);
		$.ajax({
			url : _getContextPath()+'/getCustomerDetails/'+clientId+'/'+billedtoname,
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				if(response.customerEmail != ""){
					$('#einvcustomer_emailid').val(response.customerEmail);
				}
			}
		});
	}
	var urlStr = _getContextPath()+'/getinv/'+invoiceid+'/GSTR1';
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(invoice) {
			if(invoice.mailSubject != null){
				 $('#einvemail_subject').val(invoice.mailSubject);
			}else{
				$('#einvemail_subject').val("Invoice "+invoiceno+" from "+clientDetails.businessname+"");
			}
			if(invoice.mailMessage != null){
				 $('#einvemail_meassage').val(invoice.mailMessage);
			}else{
				$('#einvemail_meassage').html("Here's your invoice! We appreciate your prompt payment.");
			}
			if(invoice.customerMailIds != null && invoice.customerMailIds != ''){
				$('#einvcustomer_emailid').val(invoice.customerMailIds);
			}
			if(invoice.customerCCMailIds != null){
				$('#einvcustomer_emailids').val(invoice.customerCCMailIds);
			}
			if(invoice.isIncludeSignature == false){
				$('.einvaddEmailSignatureDetails').attr("checked",false);
				$('#einvemail_custdetails,.einvsignLink,#einvpreview_custname').css("display","none");
			}else{
				$('.einvsignLink,#einvpreview_custname').css("display","inline-block");
				$('#einvemail_custdetails').css("display","table-caption");
				$('.einvaddEmailSignatureDetails').attr("checked",true);
			 }
		}
	});
}
$('#send_emailModal').on('shown.bs.modal', function () {
	var cname = $('#einvcustomer_name').val();
	var cemail = $('#einvcustomer_emailid').val();
	if( cname == ''){
		$('#einvcustomer_name').focus();
	}else if (cemail == '' ) {
		$('#einvcustomer_emailid').focus();
	}
    
});
function sendeinvMails(){
	$('#seneinvEmailBtn').addClass("btn-loader-blue");
	$('#sendEmailEinvErrorMsg').html('');
	$('#einvcustomer_emailid').parent().removeClass('has-error has-danger');
	ecustEmailids = new Array();ecustCCEmailids = new Array();
	var customername = $('#einvcustomer_name').val();
	var customeremail = $('#einvcustomer_emailid').val();
	var customerccemail = $('#einvcustomer_emailids').val();
	var emailsubject = $('#einvemail_subject').val();
	var emailMsg = $('#einvemail_meassage').val();
	var clientSign = $('#einvemail_custdetails').html();
	var signcheck = $('.einvaddEmailSignatureDetails').is(':checked');
	var invoiceid=$('#einvemail_invoiceId').val();
	var retType = $('#einvemail_returnType').val();
	var flag = true;
	if(customeremail.trim() == ""){
		//$('#sendEmailEinvErrorMsg').html('Please Enter Email id');
		$('#einvcustomer_emailid').parent().addClass('has-error has-danger');
		$('#seneinvEmailBtn').removeClass("btn-loader-blue");
		flag = false;
	}
	if(flag){
		if(customeremail.indexOf(',') != -1) {
			customeremail = customeremail.split(',');
			for(var i = 0; i < customeremail.length; i++) {
				ecustEmailids.push(customeremail[i]);
			}
		} else {
			ecustEmailids.push(customeremail);
		}
		if(customerccemail.indexOf(',') != -1) {
			customerccemail = customerccemail.split(',');
			for(var i = 0; i < customerccemail.length; i++) {
				ecustCCEmailids.push(customerccemail[i]);
			}
		} else {
			ecustCCEmailids.push(customerccemail);
		}
		var mailObject = new Object();
		mailObject.clientid = clientId;
		mailObject.clientName = customername;
		mailObject.email = ecustEmailids;
		mailObject.cc = ecustCCEmailids;
		mailObject.subject = emailsubject;
		mailObject.message = emailMsg;
		mailObject.userDetails = clientSign;
		var url = _getContextPath()+"/sendMails/"+ userId + "/"+ invoiceid + "/EINVOICE/"+clientId+"?signcheck=" + signcheck;
		$.ajax({
			url: url,
			data: JSON.stringify(mailObject),
			type: "POST",
			contentType: 'application/json',
			success: function(data) {
				$('.einvsuccessEmailmsg').removeClass("d-none");
				$('#einvsuccessEmailmsg').text("Mail Send Succesfully");
				$('#seneinvEmailBtn').removeClass("btn-loader-blue");
			},
			error: function(dat) {
				$('#seneinvEmailBtn').removeClass("btn-loader-blue");
			}
		});
	}
}
function email_einvPreview(){
	var customername = $('#einvcustomer_name').val();
	var customeremail = $('#einvcustomer_emailid').val();
	var emailsubject = $('#einvemail_subject').val();
	var emailMsg = $('#einvemail_meassage').val();
	$('#einvemail_cust_name').text(customername);
	$('#einvpreview_email').html(emailMsg);
	if(clientDetails.clientSignature == ""){
		$('#einvpreview_custname').html(clientDetails.businessname+"<br>"+clientDetails.email+"<br>"+clientDetails.mobilenumber);
	}else{
		var clientdetails = clientDetails.clientSignature;
		$('#einvpreview_custname').html(clientdetails.replaceAll("#mgst#", "</br>"));
	}
}
function signatureChange(){
	var signcheck = $('.einvaddEmailSignatureDetails').is(':checked');
	if(signcheck == false){
		 $('#einvemail_custdetails,.einvsignLink,#einvpreview_custname').css("display","none");
	}else{
		 $('.einvsignLink,#einvpreview_custname').css("display","inline-block");
		 $('#einvemail_custdetails').css("display","table-caption");
	}
}
function getEinvItemCustomFields(data,type,no){
	var content ="";
	var indexarry = new Array();
	if(data.items){
	$('.addEitemCustLink').hide();
	for(var i=0;i<data.items.length;i++){
		var index = i + 1;
		var itemsData = data.items[i];
		if(itemsData != undefined){
			if(itemsData.customFieldType == 'input'){
				var require = itemsData.isMandatory ? 'required' : ''; 
				var astrich = itemsData.isMandatory ? 'astrich' : '';
				content += '<div class="col-md-6 col-sm-12 form-group mb-0" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-5 pl-3">'+itemsData.customFieldName+'</p><div class="col-md-7 pl-0"><input class="form-control eitem_CustomField'+index+''+type+'" name="itemCustomField'+index+'" id="eitem_CustomField'+index+''+type+'" placeholder="'+itemsData.customFieldName+'" '+require+'><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div></div>';
				index++;
			}else if(itemsData.customFieldType == 'list'){
				var require = itemsData.isMandatory ? 'required' : ''; 
				var astrich = itemsData.isMandatory ? 'astrich' : '';
				content += '<div class="col-md-6 col-sm-12 form-group mb-0" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-5 pl-3">'+itemsData.customFieldName+'</p><div class="col-md-7 pl-0"><select class="form-control eitem_CustomField'+index+''+type+'" name="itemCustomField'+index+'" data-error="Please enter this value" id="eitem_CustomField'+index+''+type+'" '+require+'><option value="">-Select-</option>';
				for(var j = 0; j < itemsData.typeData.length; j++){
					content += "<option value=\'"+itemsData.typeData[j]+"\'>"+itemsData.typeData[j]+"</option>";
				}
				content +='</select><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div></div>';
				index++;
			}else if(itemsData.customFieldType == 'checkB'){
				content += '<div class="col-md-6 col-sm-12 form-group mb-0 customCheck" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="lable-txt pl-3 pt-3 col-md-5">'+itemsData.customFieldName+'</p><div class="col-md-7 pl-0 mb-3 pt-3 echeckBoxCustomField"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/><input type="hidden" name="itemCustomField'+index+'" class="eitem_CustomField'+index+''+type+'" id="eitem_CustomField'+index+''+type+'"/>';
				
				for(var j = 0; j < itemsData.typeData.length; j++){
				    if(itemsData.typeData[j] !=null && $('#eitemCustomField_text1'+no).val().includes(itemsData.typeData[j])){
						content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && $('#eitemCustomField_text2'+no).val().includes(itemsData.typeData[j])){
						content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && $('#eitemCustomField_text3'+no).val().includes(itemsData.typeData[j])){
						content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && $('#eitemCustomField_text4'+no).val().includes(itemsData.typeData[j])){
						content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else {
						content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'"><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}
				}
				content += '</div></div></div>';
				indexarry.push(index);
				index++;
			}else if(itemsData.customFieldType == 'radio'){
				content += '<div class="col-md-6 col-sm-12 mb-0" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="lable-txt pl-3 pt-3 col-md-5">'+itemsData.customFieldName+'</p><div class="col-md-7 pl-0 mb-3 pt-3">';
				for(var j = 0; j < itemsData.typeData.length; j++){
					if(itemsData.typeData[j] == $('#eitemCustomField_text1'+no).val() || itemsData.typeData[j] ==  $('#eitemCustomField_text2'+no).val()|| itemsData.typeData[j] == $('#eitemCustomField_text3'+no).val() || itemsData.typeData[j] ==  $('#eitemCustomField_text4'+no).val()){
						content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" class="eitem_CustomField'+index+''+type+'" id="eitem_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'" checked = "true">&nbsp;<label>'+itemsData.typeData[j]+'</label>&nbsp;</div>';
					}else{
						content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" class="eitem_CustomField'+index+''+type+'" id="eitem_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'">&nbsp;<label>'+itemsData.typeData[j]+'</label>&nbsp;</div>';
					}
				} 
				content += '</div></div></div>';
				indexarry.push(index);
				index++;
			}
		}
		$('.eitem_custom_Fields'+type+'').html(content);
		if($('#eitemCustomField_text1'+no).val() != null && $('#eitemCustomField_text1'+no).val() != ""){
			$('#eitem_CustomField1'+type).val($('#eitemCustomField_text1'+no).val());
		}
		if($('#eitemCustomField_text2'+no).val() != null && $('#eitemCustomField_text2'+no).val() != ""){
			$('#eitem_CustomField2'+type).val($('#eitemCustomField_text2'+no).val());
		}
		if($('#eitemCustomField_text3'+no).val() != null && $('#eitemCustomField_text3'+no).val() != ""){
			$('#eitem_CustomField3'+type).val($('#eitemCustomField_text3'+no).val());
		}
		if($('#eitemCustomField_text4'+no).val() != null && $('#eitemCustomField_text4'+no).val() != ""){
			$('#eitem_CustomField4'+type).val($('#eitemCustomField_text4'+no).val());
		}
		if($('#eitemId_text'+no).val() != null && $('#eitemId_text'+no).val() != ""){
			$('#eitemId_text').val($('#eitemId_text'+no).val());
		}
		$('#eitemId_text').val($('#eitemId_text'+no).val());
	}

	}else{
		$('.addEitemCustLink').show();
	}
}
function showEinvAdditionalItemFieldsPopup(no){
	$('#itemEinvCustomFieldsModal').modal("show");
	$('#eitemcustomsave').attr("onclick","saveEinvItemCustomFields("+no+")");
	$('#eitem_notes_text1').val("")
	$('.modal-backdrop.show').css('opacity','0');
	$('.modal-arrow'+no).css('display','block');
	getEinvAddItemCustomFields(customFieldsData,"Item",no);
}

function saveEinvItemCustomFields(i){
	$('#eitemCustomField_text1'+i).val($('#eitem_CustomField1Item').val());
	$('#eitemCustomField_text2'+i).val($('#eitem_CustomField2Item').val());
	$('#eitemCustomField_text3'+i).val($('#eitem_CustomField3Item').val());
	$('#eitemCustomField_text4'+i).val($('#eitem_CustomField4Item').val());
	$('#eitemId_text'+i).val($('#eitemId_text').val());
	closeeTermModal('itemEinvCustomFieldsModal');
}
function getEinvAddItemCustomFields(data,type,no){
	var content ="";
	var indexarry = new Array();
	if(data.items){
	$('.addecustFieldsLink').hide();
	for(var i=0;i<data.items.length;i++){
		var index = i + 1;
		var itemsData = data.items[i];
		if(itemsData != undefined){
			if(itemsData.customFieldType == 'input'){
				var require = itemsData.isMandatory ? 'required' : ''; 
				var astrich = itemsData.isMandatory ? 'astrich' : '';
				content += '<div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-4 pl-3">'+itemsData.customFieldName+'</p><div class="col-md-8 pl-0"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/><input class="form-control eitem_CustomField'+index+''+type+'" name="itemCustomField'+index+'" id="eitem_CustomField'+index+''+type+'" placeholder="'+itemsData.customFieldName+'" '+require+'><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div>';
				index++;
			}else if(itemsData.customFieldType == 'list'){
				var require = itemsData.isMandatory ? 'required' : ''; 
				var astrich = itemsData.isMandatory ? 'astrich' : '';
				content += '<div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-4 pl-3">'+itemsData.customFieldName+'</p><div class="col-md-8 pl-0"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/><select class="form-control eitem_CustomField'+index+''+type+'" name="itemCustomField'+index+'" data-error="Please enter this value" id="eitem_CustomField'+index+''+type+'" '+require+'><option value="">-Select-</option>';
				for(var j = 0; j < itemsData.typeData.length; j++){
					content += "<option value=\'"+itemsData.typeData[j]+"\'>"+itemsData.typeData[j]+"</option>";
				}
				content +='</select><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div>';
				index++;
			}else if(itemsData.customFieldType == 'checkB'){
				content += '<div class="row meterialform addItemForm p-0"><p id="invcustFieldText1" class="lable-txt pl-3 col-md-4">'+itemsData.customFieldName+'</p><div class="col-md-8 pl-0 mb-1 echeckBoxCustomField"><input type="hidden" name="itemCustomField'+index+'" class="eitem_CustomField'+index+''+type+'" id="eitem_CustomField'+index+''+type+'"/><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/>';
				for(var j = 0; j < itemsData.typeData.length; j++){
					if(itemsData.typeData[j] !=null && itemsData.typeData[j].indexOf($('#eitemCustomField_text1'+no).val()) != 0){
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && itemsData.typeData[j].indexOf($('#eitemCustomField_text2'+no).val()) != 0){
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && itemsData.typeData[j].indexOf($('#eitemCustomField_text3'+no).val()) != 0){
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && itemsData.typeData[j].indexOf($('#eitemCustomField_text4'+no).val()) != 0){
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else {
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'"><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}
			}
				content += '</div></div>';
				indexarry.push(index);
				index++;
			}else if(itemsData.customFieldType == 'radio'){
				content += '<div class="row p-0"><p id="invcustFieldText1" class="lable-txt pl-3 col-md-4">'+itemsData.customFieldName+'</p><div class="col-md-8 pl-0 mb-1"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/>';
				for(var j = 0; j < itemsData.typeData.length; j++){
					if(itemsData.typeData[j] == $('#eitemCustomField_text1'+no).val() || itemsData.typeData[j] ==  $('#eitemCustomField_text2'+no).val()|| itemsData.typeData[j] == $('#eitemCustomField_text3'+no).val() || itemsData.typeData[j] ==  $('#eitemCustomField_text4'+no).val()){
						content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" class="eitem_CustomField'+index+''+type+'" id="eitem_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'" checked = "true">&nbsp;<label>'+itemsData.typeData[j]+'</label>&nbsp;</div>';
					}else{
						content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" class="eitem_CustomField'+index+''+type+'" id="eitem_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'">&nbsp;<label>'+itemsData.typeData[j]+'</label>&nbsp;</div>';
					}
				} 
				content += '</div></div>';
				indexarry.push(index);
				index++;
			}
		}
		$('.eitem_custom_Fields'+type+'').html(content);
		if($('#eitemCustomField_text1'+no).val() != null && $('#eitemCustomField_text1'+no).val() != ""){
			$('#eitem_CustomField1'+type).val($('#eitemCustomField_text1'+no).val());
		}
		if($('#eitemCustomField_text2'+no).val() != null && $('#eitemCustomField_text2'+no).val() != ""){
			$('#eitem_CustomField2'+type).val($('#eitemCustomField_text2'+no).val());
		}
		if($('#eitemCustomField_text3'+no).val() != null && $('#eitemCustomField_text3'+no).val() != ""){
			$('#eitem_CustomField3'+type).val($('#eitemCustomField_text3'+no).val());
		}
		if($('#eitemCustomField_text4'+no).val() != null && $('#eitemCustomField_text4'+no).val() != ""){
			$('#eitem_CustomField4'+type).val($('#eitemCustomField_text4'+no).val());
		}
		if($('#eitemId_text'+no).val() != null && $('#eitemId_text'+no).val() != ""){
			$('#eitemId_text').val($('#eitemId_text'+no).val());
		}
		$('#eitemId_text').val($('#eitemId_text'+no).val());
	}

	}else{
		$('.addecustFieldsLink').hide();
	}
}
function changeeItemCustCheckBox(no){
	  var checkval="";
	  $('.echeckBoxCustomField input:checked').each(function() {
		  var val = $(this).val();
		  if(checkval == ""){
			  checkval = val;
		  }else{
			  checkval = checkval+","+val; 
		  }
	  });
	$('input[name=itemCustomField'+no+']').val(checkval);
}
function changeStock(no){
	var quantity = $('#eqty_text'+no).val();
	var openingStock = $('#eopening_stock'+no).val();
	var itemno = $('#eproduct_text'+no).val();
	var saftey_stock = $('#esaftey_stock'+no).val();
	var stock=0;
	if(itemno != ""){
		$.ajax({
			url: contextPath+"/checkStock/"+clientId+"/"+itemno,
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response){
				openingStock -= response;
				stock = openingStock;
				//if(response){
				if(quantity > stock){
					$('#eqty_text'+no).attr("data-toggle","tooltip");
					$('#eqty_text'+no).attr("title","Your stock is exceeded");
				}else{
					$('#stockerr_name').text("");
					$('#eqty_text'+no).removeAttr("data-toggle");
			    }
				
				
					/*if(quantity > saftey_stock){
						$('#einvitemdetails').text("Only few Items left");
					}
					if(quantity > stock){
						$('#einvitemdetails').text("You Don't have this much stock");
					}else{
						$('#einvitemdetails').text("");
				    }*/
				//}
			}
		});
	}
}
function closeAddEItem(modalId){
	$('.modal-backdrop.show').css("display","none");
	$('#'+modalId).hide();
}
function eitemCustomFieldsLink(){
	window.location.href = _getContextPath()+'/cp_upload'+paymenturlSuffix+'/'+month+'/'+year+'?type=customfields';
}

function changeeInvoiceLevelCess(){
	var invtype = $('#idEInvType').val();
	var cessenable = true;
	if($('#einvoiceLevelCess').is(":checked")) {
		$('.ecessFlag').show();
		$('#einvoiceLevelCess,#einvLevelCess').val('Yes');
		cessenable = true;
	}else{
		var cesstype = $('input[class="cessType"]:checked').val();
		if(cesstype == "Taxable Value"){
			$('.cessval').val('0%');
		}else{
			$('.cessval').val(0);
		}
		$('.cessamtval').val(0.00);
		$('.ecessFlag').hide();
		$('#einvoiceLevelCess,#einvLevelCess').val('No');
		cessenable = false;
	}
	
	$("#all_einvoice tr").each(function(index){
		findEinvCessAmount(index+1);
		index++;
	});
	
	if($('#etcsval').is(":checked")){
		supportEinvoiceTCS(invtype,enableEinvDiscount,cessenable);
	}else{
		supportEinvoiceTCS(invtype,enableEinvDiscount,cessenable);
	}
	if($('#einvoiceLevelCess').is(":checked") && $('#etcsval').is(":checked")) {
		supportEinvoiceTCS(invtype,enableEinvDiscount,cessenable);
	}
}
function supportEinvoiceTCS(invtype,enableEinvDiscount,enableEinvCess){
	if(enableEinvDiscount == true || enableEinvDiscount == "true"){
		if(invtype == 'Exports'){
			$('.tot_net_type').attr("colspan","14");
		}else{
			$('.tot_net_type').attr("colspan","13");
		}
	}
	if(enableEinvCess == true || enableEinvCess == "true"){
		if(invtype == 'Exports'){
			$('.tot_net_type').attr("colspan","15");
		}else{
			$('.tot_net_type').attr("colspan","14");
		}
	}
	if(((enableEinvDiscount == true || enableEinvDiscount == "true") && (enableEinvCess == true || enableEinvCess == "true"))){
		if(invtype == 'Exports'){
			$('.tot_net_type').attr("colspan","16");
		}else{
			$('.tot_net_type').attr("colspan","15");
		}
	}else if(((enableEinvDiscount == false || enableEinvDiscount == "false") && (enableEinvCess == false || enableEinvCess == "false"))){
		if(invtype == 'Exports'){
			$('.tot_net_type').attr("colspan","13");
		}else{
			$('.tot_net_type').attr("colspan","12");
		}
	}
}

function reversechargeeinvoice(){
		var rtype  = $('#einv_revchargetype').val();
		if(rtype == 'Y'){
			$("#einvincludetax").prop("checked",false);
			$("#all_einvoice tr").each(function(index){
				findEinvTaxAmount(index+1);
				findEinvTaxableValue(index+1);
				findEinvCessAmount(index+1);
				//findEinvAssAmount(index+1);
				index++;
			});
		}else{
			$('.rateinctax').text("");
			$("#all_einvoice tr").each(function(index){
				findEinvTaxAmount(index+1);
				findEinvTaxableValue(index+1);
				findEinvCessAmount(index+1);
				//findEinvAssAmount(index+1);
				index++;
			});
		}
	}