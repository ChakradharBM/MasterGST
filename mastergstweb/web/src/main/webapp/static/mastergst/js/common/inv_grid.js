var invTableUrl = new Object();
var invTable = new Object();
var invTableDwnld_invoiceUrl = new Object();
var invTableDwnld_itemUrl = new Object();
var invTableDwnld_fullUrl = new Object();
var cpUsersResponse;
var isCpUsersResponseLoaded = false;
var returnsCategoryOptions = {
								'returns':[
								           	{'txt':'Pending','val':'Pending'}, 
								           	{'txt':'Uploaded','val':'SUCCESS'},
								        	{'txt':'Filed','val':'Filed'},
											{'txt':'In Progress','val':'In Progress'},
								        	{'txt':'Cancelled','val':'CANCELLED'},
								        	{'txt':'Failed','val':'Failed'},
								        	{'txt':'Submitted','val':'Submitted'}
								          ],
						          'books':[
								           	{'txt':'Paid','val':'Paid'}, 
								           	{'txt':'Not Paid','val':'Not Paid'},
								        	{'txt':'Partially Paid','val':'Partially Paid'}
									      ]
								};
var documentTypeOptions = {
		'docType':[
           	{'txt':'Tax Invoice','val':'INV'}, 
           	{'txt':'Delivery Challan','val':'CHL'},
        	{'txt':'Bill of Supply','val':'BIL'},
        	{'txt':'Bill of Entry','val':'BOE'},
        	{'txt':'Credit Note','val':'CNT'},
        	{'txt':'Others','val':'OTH'}
          ]
};
var subSupplyTypeOption = {
		'subSupplyType':[
           	{'txt':'Supply','val':'1'}, 
           	{'txt':'Import','val':'2'},
        	{'txt':'Export','val':'3'},
        	{'txt':'Job Work','val':'4'},
        	{'txt':'For Own Use','val':'5'},
        	{'txt':'Job work Returns','val':'6'},
        	{'txt':'Sales Return','val':'7'}, 
           	{'txt':'Others','val':'8'},
        	{'txt':'SKD/CKD','val':'9'},
        	{'txt':'Line Sales','val':'10'},
        	{'txt':'Recipient Not Known','val':'11'},
        	{'txt':'Exhibition or Fairs','val':'12'}
          ]
};
function loadEwayBillStatus(varRetType,varRetTypeCode,connSttaus){
	if(varRetType == 'EWAYBILL'){
		if(connSttaus == "Active"){
			$('#authStatus').html('Active').css("color","green");
		}else if(connSttaus == ""){
			$('#authStatus').html('InActive, <a href="'+contextPath+'/cp_upload/'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=ewaybillconfig">configure now</a>').css("color","orange");
		}else{
			$('#authStatus').html('<span style="color:red;">Expired</span><a href="#" onclick="configauthentication()" style="color:green;"> <span id="inactivebtn">Authenticate Now</span></a>');
		}
	}
}
function loadUsersByClient(id, clientId, varRetType, varRetTypeCode, callback){
	var urlStr = _getContextPath()+'/cp_users/'+id+'/'+clientId
	if(isCpUsersResponseLoaded){
		callback(cpUsersResponse, varRetType, varRetTypeCode);;
	}else{
		$.ajax({
			url: urlStr,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				cpUsersResponse = response;
				isCpUsersResponseLoaded = true;
				callback(response, varRetType, varRetTypeCode);
			}
		});
	}
}

function loadInvoiceSupport(clientId, varRetType, varRetTypeCode, month, year, callback){
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
function invsDownloads(id, clientId, varRetType, otherreturnType, varRetTypeCode, month, year, userType, fullname,booksOrReturns){
	var vaRtTyp=varRetType;
	varRetTypeCode = varRetTypeCode.replace(" ", "_");
	var appd = '?booksorReturns='+otherreturnType;
	var dwnldurl_invoicewise = _getContextPath()+"/dwnldxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/invoicewise"+appd;
	var dwnldurl_itemwise = _getContextPath()+"/dwnldxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/itemwise"+appd;
	var dwnldurl_full = _getContextPath()+"/fulldwnldmonthlyxls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+appd;
	invTableDwnld_invoiceUrl[varRetTypeCode] = dwnldurl_invoicewise;
	invTableDwnld_itemUrl[varRetTypeCode] = dwnldurl_itemwise;
	invTableDwnld_fullUrl[varRetTypeCode] = dwnldurl_full;
	$('#itemWiseUrl'+varRetTypeCode).attr("href",dwnldurl_itemwise);
	$('#invWiseUrl'+varRetTypeCode).attr("href",dwnldurl_invoicewise);
	$('#fullDwnldUrl'+varRetTypeCode).attr("href",dwnldurl_full);
	rolesPermissions();
}
function loadInvTable(id, clientId, varRetType,varRetTypeCode, month, year, userType, fullname,booksOrReturns,pendinginv,tallyHsnSummary,clientfilingStatus){
	var pUrl = '';
	if(varRetType == "ANX1"){
		pUrl = _getContextPath()+'/getAnx1Invs/'+id+'/'+clientId+'/'+varRetType+'/'+month+'/'+year+'?booksOrReturns='+booksOrReturns;
	}else{
		pUrl = _getContextPath()+'/getAddtionalInvs/'+id+'/'+clientId+'/'+varRetType+'/'+month+'/'+year+'?booksOrReturns='+booksOrReturns+'&reportType=notreports';
	}
	var urlSuffix = id+'/'+fullname+'/'+userType+'/'+clientId+'/'+varRetType;
	invTableUrl[varRetTypeCode] = pUrl;
	if(invTable[varRetTypeCode]){
		invTable[varRetTypeCode].clear();
		invTable[varRetTypeCode].destroy();
	}
	invTable[varRetTypeCode] = $('#dbTable'+varRetTypeCode).DataTable({
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
	        	 $('#check'+varRetTypeCode.replace(" ", "_")).prop('checked', false);
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 loadTotals(varRetTypeCode, resp.invoicesAmount);
	        	 $('.updatefilter_summary').show();
	        	 $('.updateExpensefilter_summary').hide();
	        	 return resp.invoices.content ;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		
		"searching": true,
		"order": [[6,'desc']],
		'columns': getInvColumns(id, clientId, varRetType, userType, month, year, booksOrReturns,urlSuffix),
		'columnDefs' : getInvColumnDefs(varRetType),
		"createdRow": function( row, data, dataIndex ) {
		    if ( data.gstStatus == "CANCELLED" ) {
		      $(row).addClass( 'cancelled_line' );
		    }
		  },
		 "drawCallback": function () {
			 var editInv = true;
			 if(userPermissions && (varRetTypeCode == 'GSTR1' || varRetTypeCode == 'Purchase_Register')){
				 	var roleclass = (varRetTypeCode == 'SalesRegister' ||  varRetTypeCode == 'GSTR1') ? 'permissionInvoices-Sales-Edit':'permissionInvoices-Purchase-Edit';
					var retType = (varRetTypeCode == 'SalesRegister' ||  varRetTypeCode == 'GSTR1') ? 'Sales':'Purchase';
					if(userPermissions != null && userPermissions != '') {
						userPermissions["Invoices"].forEach(function(perm) {
							if((perm.name == retType) && (perm.status.includes('Edit-No'))) {
								editInv = false;
							}
						});
					}
				if(editInv == false){
					 $('.'+roleclass).css("display","none");
				 }
			}
			 
          }
	});	
	
	if(varRetType == 'GSTR1' && booksOrReturns != 'SalesRegister' &&  booksOrReturns != 'PurchaseRegister'){
		var hsnsummary = '<a href="#" class="hsnsummarybtn mt-2" onclick="viewHsnSummary(\''+varRetType+'\',\''+tallyHsnSummary+'\')">HSN Summary</a>';
		if(pendinginv == 'previousMonthsPendingInv'){
			if(tallyHsnSummary ==""){
				hsnsummary = '';
			}
			if(clientfilingStatus == 'Filed' || clientfilingStatus == 'Submitted'){
				
			}else{
				$(".dbTableGSTR1 div.toolbar").append('<span><div class="form-check mb-2 mb-sm-0" style="display: inline-block; float: right;  margin-top: 22px; margin-right: 13px;"><div class="meterialform"><div class="checkbox"><label id="include_old_invoices" style="font-size: 15px; font-weight: bold; color:grey" title="Show Present Month Invoices" data-toggle="tooltip"><input type="checkbox" name="includeoldinv" onclick="includeOldInvoices(this,\''+id+'\',\''+varRetType+'\',\''+varRetTypeCode+'\',\''+booksOrReturns+'\',\''+pendinginv+'\',\''+tallyHsnSummary+'\')" id="includeoldinv" class="previousmonthdata1" checked><i class="helper che-box" style="top:2px; left:5px"></i>Show Previous Months Pending Invoices</label></div></div></div>'+hsnsummary+'</span>');
			}
		}else{
			if(tallyHsnSummary ==""){
				hsnsummary = '';
			}
if(clientfilingStatus == 'Filed' || clientfilingStatus == 'Submitted'){
				
			}else{
				$(".dbTableGSTR1 div.toolbar").append('<span ><div class="form-check mb-2 mb-sm-0" style="display: inline-block; float: right;  margin-top: 22px; margin-right: 13px;"><div class="meterialform"><div class="checkbox"><label id="include_old_invoices" style="font-size: 15px; font-weight: bold; color:grey" title="On selection of this check box, All Pending / Not Filed invoices of previous months till now will show in the below list to file in the current month" data-toggle="tooltip"><input type="checkbox" name="includeoldinv" onclick="includeOldInvoices(this,\''+id+'\',\''+varRetType+'\',\''+varRetTypeCode+'\',\''+booksOrReturns+'\',\''+pendinginv+'\',\''+tallyHsnSummary+'\')" id="includeoldinv" class="previousmonthdata1"><i class="helper che-box" style="top:2px; left:5px"></i>Show Previous Months Pending Invoices</label></div></div></div>'+hsnsummary+'</span>');
			}
		}
	}
	$('#invBody'+varRetTypeCode).on('click','tr', function(e){
		var editInv = true;
		if(varRetTypeCode == 'GSTR1' || varRetTypeCode == 'Purchase_Register'){
		var retType = (varRetTypeCode == 'SalesRegister' ||  varRetTypeCode == 'GSTR1') ? 'Sales':'Purchase';
			if(userPermissions != null && userPermissions != '') {
				userPermissions["Invoices"].forEach(function(perm) {
					if((perm.name == retType) && (perm.status.includes('Edit-No'))) {
						editInv = false;
						
					}
				});
			}
		}
		if(editInv == true){
			if (!$(e.target).closest('.nottoedit').length) {
				var dat = invTable[varRetTypeCode].row($(this)).data();
				editInvPopup(null, varRetType, dat.invoiceId,'NotInJournals');
			}
		}
	});
}

function initializeRemoveAppliedFilters(varRetType, varRetTypeCode,booksOrReturns){
	$('#divFilters'+varRetTypeCode).on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i=1;i<=20;i++){
			if(varRetType == 'ANX2'){
				if(i == 2 || i == 10 || i == 11 || i == 12){
					$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
					continue;
				}
			}else if(varRetType == 'EWAYBILL'){
				if(i == 4 || i == 9 || i == 10 ||  i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
					if(i == 16  || i == 17  || i == 18  || i == 19){
						if(($('#multiselect'+varRetTypeCode+i).css('display') != 'none')){
							$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
						}
					}else{
						$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
					}
					continue;
				}
			}else if(varRetType == 'PROFORMAINVOICES' || varRetType == 'DELIVERYCHALLANS' || varRetType == 'ESTIMATES' || varRetType == 'PurchaseOrder'){
				if(i == 3 || i == 4 || i == 5 || i == 6){
					$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
					continue;
				}
			
			}else if((booksOrReturns == '' && varRetTypeCode == 'Purchase_Register') || varRetType == 'Unclaimed'){
				if(i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i ==7 || i == 8|| i == 16  || i == 17  || i == 18  || i == 19){
					if(i == 16  || i == 17  || i == 18  || i == 19){
						if(($('#multiselect'+varRetTypeCode+i).css('display') != 'none')){
							$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
						}
					}else{
						$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
					}
					continue;
				}
			}else if(varRetType == 'GSTR2A'){
				if(i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i ==7 || i == 8 || i == 14){
					$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
					continue;
				}
			}else{
				if(i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 16  || i == 17  || i == 18  || i == 19){
					if(i == 16  || i == 17  || i == 18  || i == 19){
						if(($('#multiselect'+varRetTypeCode+i).css('display') != 'none')){
							$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
						}
					}else{
						$('#multiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
					}
					continue;
				}
			}
		}
		applyInvFilters(varRetType);
	});
	$('#divFilters'+varRetTypeCode).on('click', '.gstr2aInvDate', function(e) {
		var totalCount=$("#divFiltersGSTR2A > span").length;
		if(totalCount == 1){
			$(".filter").hide();
		}else{
			$('.btaginput_date').hide();
		}
		$('.invDate_dropdown').hide();
		$('#start-date').val("");$('.sdate_msg,.edate_msg').text("");
		$('#end-date').val("");
		
		applyInvFilters("GSTR2A");
	});
}

function clearInvFilters(retType,booksOrReturns) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=20;i++){
		if(retType == 'ANX2'){
			if(i == 1 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 13 || i == 15){
				$('#multiselect'+retType+i).hide();
				continue;
			}
		
		}else if(retType == 'EWAYBILL'){
			if(i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 11 || i == 12 || i == 13 || i == 14){
				$('#multiselect'+retType+i).hide();
				continue;
			}	
		}else if(retType == 'PROFORMAINVOICES' || retType == 'DELIVERYCHALLANS' || retType == 'ESTIMATES' || retType == 'PurchaseOrder'){
			if(i == 1 || i == 2 || i == 4 || i == 7  || i == 8 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13  || i == 14 || i == 15 || i == 20){
				$('#multiselect'+retType+i).hide();
				continue;
			}
		
		}else if((booksOrReturns == '' && retType == 'Purchase_Register') || retType == 'Unclaimed'){
			if(i == 1 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 20){
				continue;
			}
		}else if(retType == 'GSTR2A'){
			
			if(i == 1 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 20){
				continue;
			}
		}else{
			if(retType == 'Purchase_Register'){
				if(i == 9 || i == 10  || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 20){
					$('#multiselect'+retType+i).hide();
					continue;
				}
			}else{
				if(i == 8 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 20){
					$('#multiselect'+retType+i).hide();
					continue;
				}
			}
		}
		if(i == 16  || i == 17  || i == 18  || i == 19){
			if(($('#multiselect'+retType+i).css('display') != 'none')){
				$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
			}
		}else{
			$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		}
	}
	$('#divFilters'+retType.replace(" ", "_")).html('');
	$('.normaltable .filter').css("display","none");
	$('.gstr2aInvDate').css("display","none");$('.invDate_dropdown').hide();
	$('#start-date').val("");$('.sdate_msg,.edate_msg').text("");
	$('#end-date').val("");
	var retTypeCode = retType.replace(" ", "_");
	reloadInvTable(retTypeCode, new Array(), new Array(), new Array(),new Array(),new Array(),new Array(),new Array(),undefined,new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),'','');
	reloadExcelUrls(retTypeCode, new Array(), new Array(), new Array(), new Array(), new Array(), new Array(),new Array(), new Array(), new Array(), new Array(), new Array(), new Array(), new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),new Array(),'','');
}
function clearInvFiltersss(retType,retTypeCode,booksOrReturns) {
	retType = retType.replace(" ", "_");
	for(i=1;i<=20;i++){
		if(retType == 'ANX2'){
			if(i == 1 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 13 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19){
				$('#multiselect'+retType+i).hide();
				continue;
			}
		}else if(retType == 'EWAYBILL'){
			if(i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 11 || i == 12 || i == 13 || i == 14 || i == 16 || i == 17 || i == 18 || i == 19){
				$('#multiselect'+retType+i).hide();
				continue;
			}	
		}else if(retType == 'PROFORMAINVOICES' || retType == 'DELIVERYCHALLANS' || retType == 'ESTIMATES' || retType == 'PurchaseOrder'){
			if(i == 1 || i == 2 || i == 4 || i == 7  || i == 8 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13  || i == 14 || i == 15 || i == 20){
				$('#multiselect'+retType+i).hide();
				continue;
			}else if(i == 16 || i == 17 || i == 18 || i == 19){
				continue;
			}
		}else if((booksOrReturns == '' && retType == 'Purchase_Register') || retType == 'Unclaimed'){
			if(i == 1 || i == 3 || i == 4 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
				continue;
			}
		}else if(retType == 'GSTR2A'){
			$('.gstr2aInvDate').css("display","none");$('.invDate_dropdown').hide();
			$('#start-date').val("");$('.sdate_msg,.edate_msg').text("");
			$('#end-date').val("");
			if(i == 1 || i == 3 || i == 4 || i == 8 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
				continue;
			}
		}else{
			if(retType == 'Purchase_Register'){
				if(i == 3 || i == 4 || i == 9 || i == 10  || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
					$('#multiselect'+retType+i).hide();
					continue;
				}
			}else{
				if(i == 3 || i == 4 || i == 8 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
					$('#multiselect'+retType+i).hide();
					continue;
				}
			}
		}
		$('#multiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#divFilters'+retType.replace(" ", "_")).html('');
	$('.normaltable .filter').css("display","none");
	
}
function loadTotals(varRetType, totalsData){
		$('#idCount'+varRetType).html(totalsData ? totalsData.totalTransactions : '0');
		$('#idTotAmtVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idTaxableVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idExemptedVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalExemptedAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idTaxVal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idIGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalIGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idCGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idSGST'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalSGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idCESS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCESSAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idITC'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		if(varRetType == 'GSTR2' || varRetType == 'Purchase_Register' || varRetType == 'Unclaimed' || varRetType == 'GSTR6'){
			$('#idTCSTDS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.tdsAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		}else{
			$('#idTCSTDS'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.tcsTdsAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		}
		if(varRetType == 'GSTR2A' || varRetType == 'Unclaimed'){
			$('#idTabTotal'+varRetType).html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		}else{
			$('#idTabTotal'+varRetType).html(varRetType == 'Purchase_Register' ? totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalITCAvailable.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00' : totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		}
}

function loadUsersInDropDown(response, varRetType, varRetTypeCode){
	var usersMultiSelObj = $('#multiselect'+varRetTypeCode+'3');
	usersMultiSelObj.html('');
	usersMultiSelObj.append($("<option></option>").attr("value",globaluser).text(globaluser)); 
	if (response.length > 0) {				
		response.forEach(function(cp_user) {
			usersMultiSelObj.append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
		});
	}
	if(varRetTypeCode != 'EWAYBILL'){
		multiselrefresh('#multiselect'+varRetTypeCode+'3', '- User -', varRetType);
		$('#multiselect'+varRetTypeCode+'3').multiselect('rebuild');
	}
}

function loadCustomersInDropdown(response, varRetType, varRetTypeCode){
	var usersMultiSelObj = $('#multiselect'+varRetTypeCode+'4');
  	var custMultiSelObj = $('#multiselect'+varRetTypeCode+'16');
  	var custMultiSelObj1 = $('#multiselect'+varRetTypeCode+'17');
  	var custMultiSelObj2 = $('#multiselect'+varRetTypeCode+'18');
  	var custMultiSelObj3 = $('#multiselect'+varRetTypeCode+'19');
	custMultiSelObj.html('');
	custMultiSelObj1.html('');
	custMultiSelObj2.html('');
	custMultiSelObj3.html('');
  	usersMultiSelObj.html('');
	if (response.billToNames.length > 0) {				
		response.billToNames.forEach(function(customer) {
			if(customer != ''){
				usersMultiSelObj.append($("<option></option>").attr("value",customer).text(customer));
			} 
		});
	}
	if(varRetTypeCode == 'GSTR1' || varRetTypeCode == 'Purchase_Register' || varRetTypeCode == 'SalesRegister'  || varRetTypeCode == 'EWAYBILL'){
		if(response.customField1 != null && response.customField1.length > 0) {				
			response.customField1.forEach(function(field) {
				if(field != ''){
					custMultiSelObj.append($("<option></option>").attr("value",field).text(field));
				} 
			});
		}
		if(response.customField2 != null && response.customField2.length > 0) {				
			response.customField2.forEach(function(field) {
				if(field != ''){
					custMultiSelObj1.append($("<option></option>").attr("value",field).text(field));
				} 
			});
		}
		if(response.customField3 != null && response.customField3.length > 0) {				
			response.customField3.forEach(function(field) {
				if(field != ''){
					custMultiSelObj2.append($("<option></option>").attr("value",field).text(field));
				} 
			});
		}
		if(response.customField4 != null && response.customField4.length > 0) {				
			response.customField4.forEach(function(field) {
				if(field != ''){
					custMultiSelObj3.append($("<option></option>").attr("value",field).text(field));
				} 
			});
		}
		
		if(response.customField1 != null){
			var cF1 = '- Custom Field1 -';
			if(response.customFieldName1 != null){
				cF1 = response.customFieldName1;
			}
			$('#multiselect'+varRetTypeCode+'16').css("display","block");
			multiselrefresh('#multiselect'+varRetTypeCode+'16', cF1, varRetType);
	    	$('#multiselect'+varRetTypeCode+'16').multiselect('rebuild');
		}else{
			$('#multiselect'+varRetTypeCode+'16').css("display","none");
		}
		if(response.customField2 != null){
			var cF2 = '- Custom Field2 -';
			if(response.customFieldName2 != null){
				cF2 = response.customFieldName2;
			}
			$('#multiselect'+varRetTypeCode+'17').css("display","block");
	    	multiselrefresh('#multiselect'+varRetTypeCode+'17', cF2, varRetType);
	    	$('#multiselect'+varRetTypeCode+'17').multiselect('rebuild');
		}else{
			$('#multiselect'+varRetTypeCode+'17').css("display","none");
		}
		if(response.customField3 != null){
			var cF3 = '- Custom Field3 -';
			if(response.customFieldName3 != null){
				cF3 = response.customFieldName3;
			}
			$('#multiselect'+varRetTypeCode+'18').css("display","block");
	    	multiselrefresh('#multiselect'+varRetTypeCode+'18', cF3, varRetType);
	    	$('#multiselect'+varRetTypeCode+'18').multiselect('rebuild');
    	}else{
			$('#multiselect'+varRetTypeCode+'18').css("display","none");
		}
    	if(response.customField4 != null){
    		var cF4 = '- Custom Field4 -';
			if(response.customFieldName4 != null){
				cF4 = response.customFieldName4;
			}
    		$('#multiselect'+varRetTypeCode+'19').css("display","block");
	    	multiselrefresh('#multiselect'+varRetTypeCode+'19', cF4, varRetType);
	    	$('#multiselect'+varRetTypeCode+'19').multiselect('rebuild');
    	}else{
			$('#multiselect'+varRetTypeCode+'19').css("display","none");
		}
	}
	var custStr = '- Customers -';
	if(varRetTypeCode == 'Purchase_Register' || varRetTypeCode == 'PurchaseOrder' || varRetTypeCode == 'Unclaimed' || varRetTypeCode == 'GSTR2A' || varRetTypeCode == 'ANX2'){
		custStr = '- Suppliers -';
	}
	multiselrefresh('#multiselect'+varRetTypeCode+'4', custStr, varRetType);
	$('#multiselect'+varRetTypeCode+'4').multiselect('rebuild');
}

function initiateCallBacksForMultiSelect(varRetType, varRetTypeCode, booksOrReturns){
	var statusObj = $('#multiselect'+varRetTypeCode+'1');
	var docObj = $('#multiselect'+varRetTypeCode+'10');
	var subTypeObj = $('#multiselect'+varRetTypeCode+'15');
	var statusLogo = '- Payment Status -';
	if(booksOrReturns != 'SalesRegister' &&  booksOrReturns != 'PurchaseRegister'){
		statusLogo = 'Invoice Status';
		statusObj.data('booksOrReturns','returns');
	}else{
		statusObj.data('booksOrReturns','books');
	}
	$.each(returnsCategoryOptions[(booksOrReturns != 'SalesRegister' &&  booksOrReturns != 'PurchaseRegister') ? 'returns':'books'], function(key, val){
		statusObj.append($("<option></option>").attr("value",val.val).text(val.txt));
	});
	
	$.each(documentTypeOptions['docType'], function(key, val){
		docObj.append($("<option></option>").attr("value",val.val).text(val.txt));
	});
	$.each(subSupplyTypeOption['subSupplyType'], function(key, val){
		subTypeObj.append($("<option></option>").attr("value",val.val).text(val.txt));
	});
	var multiSelDefaultVals = ['',statusLogo,'- Invoice Type -', '- User -', '', '- Branches -', '- Verticals -', '- Reverse Charge -' , '- Reconcile Status -','- Supply Type -','- Document Type -','- Reconcile Status -','- Portal Status -','- Action -','- Filing Status -','- Sub Supply Type -','','','','','- Status -'];
	for(i=1;i<=20;i++){
		if(varRetTypeCode == 'ANX2'){
			if(i == 1 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 13 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19){
				$('#multiselect'+varRetTypeCode+i).hide();
				continue;
			}
		
		}else if(varRetTypeCode == 'EWAYBILL'){
				if(i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 11 || i == 12 || i == 13 || i == 14 || i == 16 || i == 17 || i == 18 || i == 19){
					$('#multiselect'+varRetTypeCode+i).hide();
					continue;
				}
		}else	if(varRetTypeCode == 'PROFORMAINVOICES' || varRetTypeCode == 'DELIVERYCHALLANS' || varRetTypeCode == 'ESTIMATES' || varRetTypeCode == 'PurchaseOrder'){
			if(i == 1 || i == 2 || i == 3 || i == 4 || i == 7  || i == 8 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
				$('#multiselect'+varRetTypeCode+i).hide();
				continue;
			}	
		}else if(varRetTypeCode == 'GSTR2A'){
			if(i == 1 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
				$('#multiselect'+varRetTypeCode+i).hide();
				continue;
			}else if(i == 3 || i == 4){
				continue;
			}
		}else if(booksOrReturns == '' && varRetTypeCode == 'Purchase_Register'){
			if(i == 1 || i == 3 || i == 4 || i == 9 || i == 10  || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
				$('#multiselect'+varRetTypeCode+i).hide();
				continue;
			}
		}else if(varRetTypeCode == 'Unclaimed'){
			if(i == 1 || i == 3 || i == 4 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
				$('#multiselect'+varRetTypeCode+i).hide();
				continue;
			}
		}else{
			if(varRetTypeCode == 'Purchase_Register'){
				if(i == 3 || i == 4 || i == 9 || i == 10  || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
					$('#multiselect'+varRetTypeCode+i).hide();
					continue;
				}
			}else{
				if(i == 3 || i == 4 || i == 8 || i == 9  || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20){
					$('#multiselect'+varRetTypeCode+i).hide();
					continue;
				}
			}
		}
		
		multiselrefresh('#multiselect'+varRetTypeCode+i, multiSelDefaultVals[i], varRetType);
	}
}

function multiselrefresh(idval, descVal, varRetType){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyInvFilters(varRetType);
		},
		onSelectAll: function(){
			applyInvFilters(varRetType);
		},
		onDeselectAll: function(){
			applyInvFilters(varRetType);
		}
	});
	//$(idval).multiselect('refresh');
}
function loadHSNSummaryTable(clientId, varRetType,varRetTypeCode, month, year){
	var monthName = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
	if($('#filing_option').text() == " Quarterly "){
		if(month == 3) {
			monthName[month-1]='Jan-Mar';
		} else if(month == 6) {
			monthName[month-1]='Apr-Jun';
		} else if(month == 9) {
			monthName[month-1]='Jul-Sep';
		} else if(month == 12) {
			monthName[month-1]='Oct-Dec';
		}
	}
	if(invTable['hsnSummary_'+varRetTypeCode]){
		invTable['hsnSummary_'+varRetTypeCode].clear();
		invTable['hsnSummary_'+varRetTypeCode].destroy();
	}
	var monthdisplay = ""+monthName[month-1]+" "+year;
	var pUrl = _getContextPath()+'/getInvoiceHSNSummary/'+clientId+'/'+varRetType+'/'+month+'/'+year;	
	invTable['hsnSummary_'+varRetTypeCode] = $('#hsnSummary_'+varRetTypeCode).DataTable({
		"dom": '<"toolbar">lrtip',
		"pageLength": 10,
		"paging": true,
		"searching": false,
		"responsive": true,
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(hsn){
	        	 $(".hsnTable  div.toolbar").html('<h4>HSN Summary Of '+monthdisplay+' </h4>');
	        	 return hsn ;
	         }
	     },
		 'columns': getHSNColumns(clientId,month, year),
		'columnDefs' : getHSNColumnDefs()
	});
}
function getHSNColumns(clientId,month, year){
	var hsn = {data:  function ( data, type, row ) {
		var hsnCode = data.hsn_sc ? data.hsn_sc : "";
		return '<span class="text-left invoiceclk">'+hsnCode+'</span>';
	}};
	var description = {data:  function ( data, type, row ) {
		var desc = data.desc ? data.desc : "";
		return '<span class="text-left invoiceclk">'+desc+'</span>';
	}};
	var quantity = {data:  function ( data, type, row ) {
		var qty = data.qty ? data.qty : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(qty.toFixed(2))+'</span>';
	}};
	var totalVal = {data:  function ( data, type, row ) {
		if((month > 4 && year > 2020) || (month < 4 && year > 2021)){
			var total =data.rt ? data.rt : 0.0;
			return '<span class="text-left invoiceclk">'+formatNumber(total.toFixed(2))+'</span>';
		}else{
			var total =data.val ? data.val : 0.0;
			return '<span class="text-left invoiceclk">'+formatNumber(total.toFixed(2))+'</span>';
		}
		
	}};
	var taxableVal = {data:  function ( data, type, row ) {
		var taxval = data.txval ? data.txval : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(taxval.toFixed(2))+'</span>';
	}};
	var iamt = {data:  function ( data, type, row ) {
		var igstAmt = data.iamt ? data.iamt : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(igstAmt.toFixed(2))+'</span>';
	}};
	var camt = {data:  function ( data, type, row ) {
		var cgstAmt = data.camt ? data.camt : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(cgstAmt.toFixed(2))+'</span>';
	}};
	var samt = {data:  function ( data, type, row ) {
		var sgstAmt = data.samt ? data.samt : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(sgstAmt.toFixed(2))+'</span>';
	}};
	var csamt = {data:  function ( data, type, row ) {
		var cessAmt = data.csamt ? data.csamt : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(cessAmt.toFixed(2))+'</span>';
	}};
	
	return [hsn,description,quantity,totalVal,taxableVal,iamt,camt,samt,csamt];
}
function getHSNColumnDefs(){
	return  [
		{
			"targets":  [2,3,4,5,6,7,8],
			className: 'dt-body-right'
		},
		{
			"targets":  [0,1],
			className: 'dt-body-left'
		}
	];
}
function loadInvSummeryTable(clientId, varRetType,varRetTypeCode, month, year,status,booksOrReturns,otperror){
	var retype = '';
	if(varRetType == 'PROFORMAINVOICES' || varRetType == 'DELIVERYCHALLANS' || varRetType == 'ESTIMATES' || varRetTypeCode == 'GSTR1'){
		retype = 'GSTR1';
	}
	if(varRetType == 'PurchaseOrder' || varRetTypeCode == 'Purchase_Register'){
		retype = 'GSTR2';
	}
	if(varRetType == 'ANX1'){
		retype = 'ANX1';
	}
	var returnType = retype;
	var monthName = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
	if($('#filing_option').text() == " Quarterly "){
		if(month == 3) {
			monthName[month-1]='Jan-Mar';
		} else if(month == 6) {
			monthName[month-1]='Apr-Jun';
		} else if(month == 9) {
			monthName[month-1]='Jul-Sep';
		} else if(month == 12) {
			monthName[month-1]='Oct-Dec';
		}
	}
	if(retype == 'GSTR1'){
		retype = 'Sales';
	}else if(retype == 'GSTR2' || retype == 'GSTR6' || retype == 'Purchase Register' || retype == 'Purchase_Register'){
		retype = 'Purchase';
	}
	if(invTable['summery_'+varRetTypeCode]){
		invTable['summery_'+varRetTypeCode].clear();
		invTable['summery_'+varRetTypeCode].destroy();
	}
	
	var monthdisplay = ""+monthName[month-1]+" "+year;
	if(booksOrReturns == 'SalesRegister' || booksOrReturns == 'PurchaseRegister' || booksOrReturns == 'PurchaseOrder' || booksOrReturns == 'DELIVERYCHALLANS' || booksOrReturns == 'ESTIMATES' || booksOrReturns == 'PROFORMAINVOICES'){
		var invviewoption = $('#invoiceview_option').text();
		if(invviewoption.indexOf('Yearly') != -1){
			var finyear = $('#invoiceviewfinancialYear').val();
			var nxtyear = (parseInt(finyear)+1).toString();
			monthdisplay = finyear+" - "+nxtyear;
			year = finyear;
		}
	}
	var pUrl = _getContextPath()+'/invoiceSummeryByTypeForMonth/'+clientId+'/'+varRetType+'/'+month+'/'+year+'?booksOrReturns='+booksOrReturns;
	invTable['summery_'+varRetTypeCode] = $('#summery_'+varRetTypeCode).DataTable({
		 "processing": true,
		 "serverSide": true,
		 "dom": '<"toolbar">frtip',
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 $(".tabtable4  div.toolbar").html('<h4>'+ retype+' Summary Of '+monthdisplay+' </h4>');
	        	 return resp ;
	         }
	     },
	     'paging':false,
		"order": [[1,'asc']],
		'columns' : [
		            {data: function ( data, type, row ) {
		              	 return data.value;
		               }},
	               {data: function ( data, type, row ) {
		              	 return data.order;
		               }},
	               {data: function ( data, type, row ) {
		              	 return data.noOfInvoices;
		               }},
	               {data: function ( data, type, row ) {
	            	   return '<i class="fa fa-rupee"></i>'+formatNumber(data.totalTaxableAmount.toFixed(2));
	               }},
	               {data: function ( data, type, row ) {
	            	   return '<i class="fa fa-rupee"></i>'+formatNumber(data.totalTaxAmount.toFixed(2));
	               }},
	               {data: function ( data, type, row ) {
	            	   return '<i class="fa fa-rupee"></i>'+formatNumber(data.totalAmount.toFixed(2));
	               }},
	               {data: function ( data, type, row ) {
	            	   return '<a href="#" onclick="updateDetails(\''+retype+'\',\''+data._id+'\')" class="f-11 permissionInvoices-Sales-View">Details</a>';
	               }}
		           		            
		            ],
		            
		  'columnDefs': [{
				"targets": [1],
				"visible": false
				},
				{
				"targets":  [3,4, 5],
				className: 'dt-body-right'
				}]
	});	
	$.ajax({
		url : _getContextPath()+'/invoiceSummeryByGstStatusForMonth/'+clientId+'/'+varRetType+'/'+month+'/'+year,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
		$('#totalInvoices'+varRetType).html(response["totalInvoices"]);
		$('#totalPending'+varRetType).html(response["totalPending"]);
		$('#totalUploaded'+varRetType).html(response["totalUploaded"]);
		$('#totalFailed'+varRetType).html(response["totalFailed"]);
		},
		error : function(e, status, error) {}
	});
	if(status == 'Submitted' || status == 'Filed'){
		$('.addInvoice').addClass("disabled");
	}else{
		$('.addInvoice').removeClass("disabled");
	}
	if(otperror == 'Y' || status == 'Submitted' || status == 'Filed'){
		$('.submitinvoices').addClass("disabled");
		$('.uploadinvoice').addClass("disabled");
	}else{
		if(varRetType == 'GSTR1'){
			$('.submitinvoices').removeClass("disabled");
			$('.uploadinvoice').removeClass("disabled");
		}else{
			$('.submitinvoices').addClass("disabled");
			$('.uploadinvoice').addClass("disabled");
		}
		
	}
}
function getInvColumns(id, clientId, varRetType, userType, month, year, booksOrReturns,urlSuffix){
	var varRetTypeCode = varRetType.replace(" ", "_");	
	var chkBx = {data: function ( data, type, row ) {
		var ntType = "";
		var invoicetype = data.invtype;
		if(data.invtype == "Credit/Debit Notes"){
			if(varRetType == 'GSTR1')	{
				if(data.cdnr && data.cdnr.length > 0){
					if(data.cdnr[0].nt[0].ntty != undefined){
						ntType = data.cdnr[0].nt[0].ntty;
						if(ntType  == "C"){
							invoicetype = "Credit Note";
						}else if(ntType  == "D"){
							invoicetype = "Debit Note";
						}else{
							invoicetype = "Credit/Debit Notes";
						}
					}
				}
			}else{
				if(data.cdn && data.cdn.length > 0){
					if(data.cdn[0].nt[0].ntty != undefined){
						ntType = data.cdn[0].nt[0].ntty;
						if(ntType  == "C"){
							invoicetype = "Credit Note";
						}else if(ntType  == "D"){
							invoicetype = "Debit Note";
						}else{
							invoicetype = "Credit/Debit Notes";
						}
					}
				}
			}
		}else if(data.invtype == "Credit/Debit Note for Unregistered Taxpayers"){
			if(data.cdnur && data.cdnur.length > 0){
				if(data.cdnur[0].ntty != undefined){
					ntType = data.cdnur[0].ntty;
					if(ntType  == "C"){
						invoicetype = "Credit Note(UR)";
					}else if(ntType  == "D"){
						invoicetype = "Debit Note(UR)";
					}else{
						invoicetype = "Credit/Debit Note for Unregistered Taxpayers";
					}
				}
			}
		}
		
      	 			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invFilter'+varRetTypeCode+data.userid+'" onClick="updateSelection(\''+data.userid+'\', \''+varRetType+'\', \''+data.gstStatus+'\', \''+invoicetype+'\',this)"/><i class="helper"></i></label></div>';
    			}};
	var status = {data: function ( data, type, row ) {
						var status = '';
						var statusTooltip = '';	
					if(varRetType != 'EWAYBILL'){	
						if(booksOrReturns != 'SalesRegister' &&  booksOrReturns != 'PurchaseRegister'){
							statusTooltip = 'Filing Status';
							status = data.gstStatus;
							 if(status == null || status == ''){
								 status = 'Pending';
							}else if(status == 'SUCCESS'){
								status = 'Uploaded';
							}else if(status == 'CANCELLED'){
								status = 'Cancelled';
							}
						}else{
							statusTooltip = 'Payment Status';
							status = data.paymentStatus;
							if(status == null || status == ''){
								status = 'Not Paid';
							 }
						}
	var paymentStCls = (status == 'Paid' || status == 'Filed' || status == 'Submitted' || status == 'SUCCESS' || status == 'Uploaded') ? 'color-green' : 
							 (status == 'Cancelled') ? 'color-red' : (status == 'Partially Paid' || status == 'Pending') ? 'color-yellow' : (status == 'In Progress') ? 'color-blue' : 'color-red';
				   	 
					 if(status == 'Paid' || status == 'Not Paid' || status == 'Filed' || status == 'Submitted' || status == 'SUCCESS' || status == 'Uploaded' || status == 'Pending' || status == 'In Progress' || status == 'Cancelled' || status == 'Ineligible'){
						 return '<span class="'+paymentStCls+'" id="statuss'+data.userid+'"  data-toggle="tooltip" title="'+statusTooltip+' : '+status+'">'+status+'</span>';
					 }else if(status == 'Partially Paid'){
						 if(data.pendingAmount != null){
							 if(data.receivedAmount != null){
								return '<span class="'+paymentStCls+'" id="statuss'+data.userid+'"  data-toggle="tooltip" title="Paid Amount : '+formatNumber(data.receivedAmount.toFixed(2))+'<br>Pending Amount : '+formatNumber(data.pendingAmount.toFixed(2))+'">'+status+'</span>';
							 }else{
								 return '<span class="'+paymentStCls+'" id="statuss'+data.userid+'"  data-toggle="tooltip" title="Pending Amount : '+formatNumber(data.pendingAmount.toFixed(2))+'">'+status+'</span>';
							 }
						 }else{
							return '<span class="'+paymentStCls+'" id="statuss'+data.userid+'"  data-toggle="tooltip" title="Pending Amount : Not Available">'+status+'</span>';
						 }
					 }else{
						return '<span class="'+paymentStCls+'" id="statuss'+data.userid+'"  data-toggle="tooltip" title="'+statusTooltip+' : '+status+'">Failed</span><i class="fa fa-info-circle" style="font-size:17px;color:red" rel="tooltip" title="'+status+'"></i>';
					 }
					}else{
						var ewaybillno  = data.ewayBillNumber ? data.ewayBillNumber : "";
						return '<span class="text-left invoiceclk">'+ewaybillno+'</span>';
					}
				}};
	var validDate = {data:  function ( data, type, row ) {
		var validUpto  = data.validUpto ? data.validUpto : "";
		var validator = data.ebillValidator ? data.ebillValidator : ""
		if(validator && (validator == true || validator == "true")){
			return '<span class="text-left invoiceclk color-red validateEbill">'+validUpto+'</span>';
		}else{
			return '<span class="text-left invoiceclk">'+validUpto+'</span>';
		}	
		
	}};
	var ebillDate = {data:  function ( data, type, row ) {
		var ewaybilldate  = data.eBillDate ? data.eBillDate : "";
		if(ewaybilldate != ""){
			return '<span class="text-left invoiceclk">'+(new Date(ewaybilldate)).toLocaleDateString('en-GB')+'</span>';
		}else{
			return '<span class="text-left invoiceclk"></span>';
		}
	}};
	var ebillStatus = {data:  function ( data, type, row ) {
		var ewaybillNumber  = data.ewayBillNumber ? data.ewayBillNumber : "";
		var ewaybillStatus = data.status ? data.status : "";
		if(ewaybillNumber != ""){
			if(ewaybillStatus == "CNL"){
				return '<span class = "color-red" data-toggle="tooltip" title="EwayBill Status : Cancelled"><i class="fa fa-circle color-red" style="font-size:17px;"></i></span>';
			}else if(ewaybillStatus == "Rejected"){
				return '<span class = "color-orange" data-toggle="tooltip" title="EwayBill Status : Rejected"><i class="fa fa-circle color-orange" style="font-size:17px;"></i></span>';
			}else{
				return '<span class = "color-green" data-toggle="tooltip" title="EwayBill Status : Generated"><i class="fa fa-circle color-green" style="font-size:17px;"></i></span>';
			}
		}else{
			return '<span class = "color-yellow" data-toggle="tooltip" title="EwayBill Status : Not Generated"><i class="fa fa-circle color-yellow" style="font-size:17px;"></i></span>';
		}
	}};
	
	var subStype = {data:  function ( data, type, row ) {
		var type  = data.subSupplyType ? data.subSupplyType : "";
		var subTypes = ["Supply","Import","Export","Job Work","For Own Use","Job work Returns","Sales Return","Others","SKD/CKD","Line Sales","Recipient Not Known","Exhibition or Fairs"];
		if(type != ""){
			return '<span class="text-left invoiceclk">'+subTypes[type-1]+'</span>';
		}else{
			return '<span class="text-left invoiceclk"></span>';
		}
	}};	
	var itype = {data: function ( data, type, row ) {
		var ntType = "";
					if(data.invtype == "Credit/Debit Notes"){
						if(varRetType == 'GSTR1')	{
							if(data.cdnr && data.cdnr.length > 0){
								if(data.cdnr[0].nt[0].ntty != undefined){
									ntType = data.cdnr[0].nt[0].ntty;
									if(ntType  == "C"){
										data.invtype = "Credit Note";
									}else if(ntType  == "D"){
										data.invtype = "Debit Note";
									}else{
										data.invtype = "Credit/Debit Notes";
									}
								}
							}
						}else{
							if(data.cdn && data.cdn.length > 0){
								if(data.cdn[0].nt[0].ntty != undefined){
									ntType = data.cdn[0].nt[0].ntty;
									if(ntType  == "C"){
										data.invtype = "Credit Note";
									}else if(ntType  == "D"){
										data.invtype = "Debit Note";
									}else{
										data.invtype = "Credit/Debit Notes";
									}
								}
							}
						}
					}else if(data.invtype == "Credit/Debit Note for Unregistered Taxpayers"){
						if(data.cdnur && data.cdnur.length > 0){
							if(data.cdnur[0].ntty != undefined){
								ntType = data.cdnur[0].ntty;
								if(ntType  == "C"){
									data.invtype = "Credit Note(UR)";
								}else if(ntType  == "D"){
									data.invtype = "Debit Note(UR)";
								}else{
									data.invtype = "Credit/Debit Note for Unregistered Taxpayers";
								}
							}
						}
					}else if(data.invtype == "PROFORMAINVOICES"){
						if(data.convertedtoinv != null){
							if(data.convertedtoinv == "converted"){
								data.invtype = "<span><img data-toggle='tooltip' title='Converted Proforma Invoice' src='"+_getContextPath()+"/static/mastergst/images/dashboard-ca/converted-icon-green.png' alt='reversecharge' style='height: 18px;width:18px;margin-right: 5px;margin-bottom:3px'></span>";
							}else{
								data.invtype = "<span><img data-toggle='tooltip' title='Proforma Invoice' src='"+_getContextPath()+"/static/mastergst/images/dashboard-ca/converted-icon-red.png' alt='reversecharge' style='height: 18px;width:18px;margin-right: 5px;margin-bottom:3px'></span>";
							}
						}
					}	
			     	if(data.revchargetype == 'Reverse' || data.revchargetype == 'Y'){
			     		return '<span class="text-left invoiceclk ">'+data.invtype+'</span><span><img data-toggle="tooltip" title="Reverse Charge Applied on this Invoice" src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/reversecharge.png" alt="reversecharge" style="height: 18px;margin-left: 10px;margin-bottom:3px"></span>';
			     	}else{
			     		return '<span class="text-left invoiceclk ">'+data.invtype+'</span>';
			     	}			              	 
			    }};
	var invsNo = {data:  function ( data, type, row ) {
					var invoiceno = '';
					if(data.invoiceno != undefined){
						invoiceno = data.invoiceno;
					}
			      	 return '<span class="text-left invoiceclk ">'+invoiceno+'</span>';
			    }};
	var billtoname = {data: function ( data, type, row ) {
						var billedtoname = '';
						if(data.billedtoname != undefined){
							billedtoname = data.billedtoname;
						}
				      	 return '<span class="text-left invoiceclk ">'+billedtoname+'</span>';
				    }};
	var billtogtnn = {data: function ( data, type, row ) {
					var invtype = data.invtype;
					var gstnnum= '';
					var cfs = '';
					if(varRetType != 'ANX1'){
						if(invtype == 'Import Goods'){
							if(data.impGoods && data.impGoods.length > 0){
								if(data.impGoods[0].stin != undefined){
									gstnnum = data.impGoods[0].stin;
								}
							}
						}else{
							if(data.b2b && data.b2b.length > 0){
								if(data.b2b[0].ctin != undefined){
									gstnnum = data.b2b[0].ctin;
								}
							}else if(data.cdnr && data.cdnr.length > 0){
								if(data.cdnr[0].ctin != undefined){
									gstnnum = data.cdnr[0].ctin;
								}
							}
						}
					}else{
						if(data.anxb2b && data.anxb2b.length > 0){
							if(data.anxb2b[0].ctin != undefined){
								gstnnum = data.anxb2b[0].ctin;
							}
						}else if(data.de && data.de.length > 0){
							if(data.de[0].ctin != undefined){
								gstnnum = data.de[0].ctin;
							}
						}else if(data.sezwp && data.sezwp.length > 0){
							if(data.sezwp[0].ctin != undefined){
								gstnnum = data.sezwp[0].ctin;
							}
						}else if(data.sezwop && data.sezwop.length > 0){
							if(data.sezwop[0].ctin != undefined){
								gstnnum = data.sezwop[0].ctin;
							}
						}
					}
					
					var cfs = '';
		        	if(invtype == 'B2B'){
		        		if(data.b2b && data.b2b.length > 0){
							if(data.b2b[0].cfs != undefined){
								cfs = data.b2b[0].cfs;
							}
						}
		        	}else if(invtype == 'Credit/Debit Notes' || invtype == 'Credit Note' || invtype == 'Debit Note'){
		        		if(data.cdn && data.cdn.length > 0){
							if(data.cdn[0].cfs != undefined){
								cfs = data.cdn[0].cfs;
							}
						}
		        	}else if(invtype == 'B2BA'){
		        		if(data.b2ba && data.b2ba.length > 0){
							if(data.b2ba[0].cfs != undefined){
								cfs = data.b2ba[0].cfs;
							}
						}
		        	}else if(invtype == 'CDNA'){
		        		if(data.cdna && data.cdna.length > 0){
							if(data.cdna[0].cfs != undefined){
								cfs = data.cdna[0].cfs;
							}
						}
		        	}
		        	if(varRetType == 'GSTR2A'){
		        		
		        		 var cfscolor = (cfs == 'Y') ? 'color:#119400!important' :  'color:rgb(235, 188, 0)';
		        		 var cfstooltip = (cfs == 'Y') ? 'Filed' :  'Pending';
		        		 if(cfs == ''){
		        			 return '<span class="text-left invoiceclk ">'+gstnnum+'</span>';
		        		 }else{
		        			 return '<span class="text-left invoiceclk" style="'+cfscolor+'" data-toggle="tooltip" title="Supplier Invoice Status is '+cfstooltip+'">'+gstnnum+'</span>';
		        		 }
		        	}else{
				      	 return '<span class="text-left invoiceclk ">'+gstnnum+'</span>';
		        	}
					
			    }};
	
	var invDate = {data: function ( data, type, row ) {
					var dateofinvoice = "";
						if(data.dateofinvoice != null){
							var invDate = new Date(data.dateofinvoice);
							var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
							day = checkZero(day);month = checkZero(month);year = checkZero(year);
							dateofinvoice = day + "/" + month + "/" + year;
						}else{dateofinvoice = "";}
				      	 return '<span class="text-left invoiceclk ">'+(new Date(data.dateofinvoice)).toLocaleDateString("en-GB")+'</span>';
				    }};
	var taxblamt = {data: function ( data, type, row ) {
					var invtype = data.invtype;
					var totalTaxableAmt = 0.0;
					if(data.totaltaxableamount){
						totalTaxableAmt = data.totaltaxableamount;
					}
					var txbleflag = false;
					if(invtype == 'Credit Note' || invtype == 'Credit Note(UR)' || invtype == 'CDNA' || invtype == 'CDNURA' || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
						if(invtype == 'CDNA'){
							if(data.cdnr && data.cdnr.length > 0){
								if(data.cdnr[0].nt[0].ntty != undefined){
									ntType = data.cdnr[0].nt[0].ntty;
									if(ntType  == "C"){
										txbleflag = true;
									}
								}
							}
						}else if(invtype == 'CDNURA'){
							if(data.cdnur && data.cdnur.length > 0){
								if(data.cdnur[0].ntty != undefined){
									ntType = data.cdnur[0].ntty;
									if(ntType  == "C"){
										txbleflag = true;
									}
								}
							}
						}
						if(invtype == 'CDNA' || invtype == 'CDNURA'){
							if(txbleflag){
								return '<span class="ind_formats text-right invoiceclk ">- '+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';							
							}else{
								return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';															
							}
						}else{
							return '<span class="ind_formats text-right invoiceclk ">- '+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';							
						}
					}else{
						return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
					}
			}};
	var totlTax = {data: function ( data, type, row ) {
					var invtype = data.invtype;
					var totalTax = 0.0;
					if(data.totaltax){
						totalTax = data.totaltax;
					}
					var txflag = false;
					if(invtype == 'Credit Note' || invtype == 'Credit Note(UR)' || invtype == 'CDNA' || invtype == 'CDNURA' || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
						if(invtype == 'CDNA'){
							if(data.cdnr && data.cdnr.length > 0){
								if(data.cdnr[0].nt[0].ntty != undefined){
									ntType = data.cdnr[0].nt[0].ntty;
									if(ntType  == "C"){
										txflag = true;
									}
								}
							}
						}else if(invtype == 'CDNURA'){
							if(data.cdnur && data.cdnur.length > 0){
								if(data.cdnur[0].ntty != undefined){
									ntType = data.cdnur[0].ntty;
									if(ntType  == "C"){
										txflag = true;
									}
								}
							}
						}
						if(invtype == 'CDNA' || invtype == 'CDNURA'){
							if(txflag){
								return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">- '+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CESS :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCessAmount ? data.totalCessAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
							}else{
								return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CESS :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCessAmount ? data.totalCessAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
							}
						}else{
							return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">- '+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CESS :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCessAmount ? data.totalCessAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
						}
					}else{
						return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CESS :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCessAmount ? data.totalCessAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
					}
				    }};
	var totalamt = {data: function ( data, type, row ) {
				var invtype = data.invtype;
				var totalAmount = 0.0;
				if(data.totalamount){
					totalAmount = data.totalamount;
				}
				var tamtflag = false;
				if(invtype == 'Credit Note' || invtype == 'Credit Note(UR)' || invtype == 'CDNA' || invtype == 'CDNURA' || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
					if(invtype == 'CDNA'){
						if(data.cdnr && data.cdnr.length > 0){
							if(data.cdnr[0].nt[0].ntty != undefined){
								ntType = data.cdnr[0].nt[0].ntty;
								if(ntType  == "C"){
									tamtflag = true;
								}
							}
						}
					}else if(invtype == 'CDNURA'){
						if(data.cdnur && data.cdnur.length > 0){
							if(data.cdnur[0].ntty != undefined){
								ntType = data.cdnur[0].ntty;
								if(ntType  == "C"){
									tamtflag = true;
								}
							}
						}
					}
					if(invtype == 'CDNA' || invtype == 'CDNURA'){
						if(tamtflag){
							return '<span class="ind_formats text-right invoiceclk ">- '+formatNumber(totalAmount.toFixed(2))+'</span>';							
						}else{
							return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalAmount.toFixed(2))+'</span>';							
						}
					}else{
						return '<span class="ind_formats text-right invoiceclk ">- '+formatNumber(totalAmount.toFixed(2))+'</span>';
					}
				}else{
					return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalAmount.toFixed(2))+'</span>';
				}
			    }};
	var branch = {data: function ( data, type, row ) {
		var branch = "";
		if(data.branch){
			branch = data.branch;
		}
	   	 return '<span class="ind_formats text-right invoiceclk ">'+branch+'</span>';
	    }};
	var vertical = {data: function ( data, type, row ) {
		var vertical = "";
		if(data.vertical){
			vertical = data.vertical;
		}
	   	 return '<span class="ind_formats text-right invoiceclk ">'+vertical+'</span>';
	    }};
	var section7 = {data: function ( data, type, row ) {
		var section = "";
		if(data.anx2b2b && data.anx2b2b.length > 0 && data.anx2b2b[0].docs){
			if(data.anx2b2b[0].docs[0].sec7act != undefined){
				section = data.anx2b2b[0].docs[0].sec7act;
			}
		}
		section = "Y" ? "Yes" : "No"
	   	 return '<span class="ind_formats text-right invoiceclk ">'+section+'</span>';
	    }};
	var ctin = {data: function ( data, type, row ) {
		var cgstin = "";
		if(data.invtype == "B2B"){
			if(data.anx2b2b && data.anx2b2b.length > 0){
				if(data.anx2b2b[0].ctin != undefined){
					cgstin = data.anx2b2b[0].ctin;
				}
			}
		}else if(data.invtype == "DE"){
			if(data.de && data.de.length > 0){
				if(data.de[0].ctin != undefined){
					cgstin = data.de[0].ctin;
				}
			}
		}
	   	 return '<span class="ind_formats text-left invoiceclk ">'+cgstin+'</span>';
	    }};
	var reconcileStatus = {data: function ( data, type, row ) {
		return '<div class="text-center" data-toggle="tooltip" title="Not Yet Reconciled"><i class="fa fa-circle p-0" style="color:#f44336;font-size:18px"></i></div>';
	 }};
	var actionStatus = {data: function ( data, type, row ) {
		var reconStatus ="";
		if(data.anx2b2b && data.anx2b2b.length > 0 && data.anx2b2b[0].docs){
			if(data.anx2b2b[0].docs[0].action != undefined){
				reconStatus = data.anx2b2b[0].docs[0].action;
			}
		}
	
		 if(reconStatus=="A"){
			  $('#aoption'+data.userid).prop('checked',true);
		  }else if(reconStatus=="R"){
			  $('#roption'+data.userid).prop('checked',true);
		  }else if(reconStatus=="P"){
			  $('#poption'+data.userid).prop('checked',true);
		  }
			return '<span class="text-center invoiceclk"><div class="meterialform bs-fancy-checks text-center"><div class="form-group-inline" style="display: flex;"><div class="form-radio"><div class="radio tab atype" data-toggle="tooltip" title="Change invoice status to Accepted"><input type="radio" id="aoption'+data.userid+'" name="statusoption"><label for="aoption'+data.userid+'"></label><div class="check"></div></div></div><div class="form-radio"><div class="radio tab rtype" data-toggle="tooltip" title="Change invoice status to Reject"><input type="radio" id="roption'+data.userid+'" name="statusoption"><label for="roption'+data.userid+'"></label><div class="check"></div></div></div><div class="form-radio"><div class="radio tab ptype" data-toggle="tooltip" title="Change invoice status to Pending"><input type="radio" id="poption'+data.userid+'" name="statusoption"><label for="poption'+data.userid+'"></label><div class="check"></div></div></div></div></div></span>';
			 
			
	}};
	var portalStatus = {data: function ( data, type, row ) {
		return '<span class="text-center invoiceclk"></span>';
	}};
	
	var itcClimed = {data: function( data, type, row ){
							if(data.invtype == 'Advances' || data.invtype == 'Advance Adjusted Detail' || data.invtype == 'TXPA' || data.invtype == 'ITC Reversal' || data.invtype == 'Nil Supplies' || data.invtype == 'Adv. Adjustments' || data.invtype == 'ISD'){
								return '<span class="text-right ind_formats nottoedit">'+formatNumber(0)+'</span><div class="dropdown nottoedit" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width: 300px;height: 180px;font-size: 15px;"><span aria-hidden="true"> <img src="'+_getContextPath()+'/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span><div class="row"><div class="col-md-12" style="color:red;">This Invoice is not Eligible for ITC</div></div></div></div>';
							}else{
								var today = new Date();
								var tddate = today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2) + '-' + ('0' + today.getDate()).slice(-2);
								var gstr3bmonth = mnths[today.getMonth()];
								var itcLoader = '<div id="itcLoader" class="itcLoader d-none"><img src="'+_getContextPath()+'/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 80px;height: 80px;"></div>'
								$('#'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate').datetimepicker({
									timepicker: false,
									format: 'd-m-Y',
									scrollMonth: true
								});
								if(data.totalitc){
									if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)'){
										return '<span class="text-right ind_formats nottoedit" id="itcVal'+varRetTypeCode+data.userid+'">-'+formatNumber(data.totalitc.toFixed(2))+'</span><div class="dropdown nottoedit" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" onclick="itcClaimedData(\''+varRetType+'\',\''+data.userid+'\')" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width: 300px;height: 180px;font-size: 15px;"><span aria-hidden="true"> <img src="'+_getContextPath()+'/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span>'+itcLoader+'<div class="row"><label class="col-md-4" style="padding-top: 0px;padding-bottom: 13px;padding-left: 15px;padding-right: 1px;">Input Type<span aria-hidden="true"></span></label><div class="col-md-6"><form><select id="'+data.userid+varRetType.replace(" ", "_")+'itc_droptype" onchange="updateITCEligibity(this.value,\''+varRetType+'\',\''+data.userid+'\')" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Services</option><option value="no">Ineligible</option><option value="pending">Pending</option> </select></form></div></div><span style="color:red;font-size:12px;">Your Input will reflect in GSTR 3B for the month of <span class="'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate">'+gstr3bmonth+'</span></span><div class="row"><label class="col-md-4">ITC Claimed in</label><div class="col-md-5 p-0"> <div id="datepicker"><input type="text" min="2017-08-15" value="'+tddate+'" class="form-control input-datepicker" name="date" id="'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate" onchange="gstr3bmonthChange(\''+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate\')" required/><span style="position: absolute;top: 20%;right: 0%;pointer-events: none;"><i class="fa fa-calendar"></i> </span></div></div></div><div class="row" style="margin-top: 10px;"><label class="col-md-4">ITC Amount</label><input id="'+data.userid+varRetType.replace(" ", "_")+'itc_dropamt" type="text"  maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" onkeyup="itcclaim()" pattern="^[0-9]+(\.[0-9]{1,2})?$" class="form-control itc_amount col-md-2" value="100" style="width:  45px;"><span class="col-md-1" style="padding: 0px;margin-top:6px">%</span><button class="btn btn-blue col-md-1" id="claimbtn" style="height:24px" onclick="updateITCType(\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')" value="ok"> ok </button></div><a class="" href="#"  style="" onclick="updateunclaimdetails(\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')">Clear ITC Claimed Values</a></div></div>';
									}else{
										return '<span class="text-right ind_formats nottoedit" id="itcVal'+varRetTypeCode+data.userid+'">'+formatNumber(data.totalitc.toFixed(2))+'</span><div class="dropdown nottoedit" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" onclick="itcClaimedData(\''+varRetType+'\',\''+data.userid+'\')" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width: 300px;height: 180px;font-size: 15px;"><span aria-hidden="true"> <img src="'+_getContextPath()+'/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span>'+itcLoader+'<div class="row"><label class="col-md-4" style="padding-top: 0px;padding-bottom: 13px;padding-left: 15px;padding-right: 1px;">Input Type<span aria-hidden="true"></span></label><div class="col-md-6"><form><select id="'+data.userid+varRetType.replace(" ", "_")+'itc_droptype" onchange="updateITCEligibity(this.value,\''+varRetType+'\',\''+data.userid+'\')" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Services</option><option value="no">Ineligible</option><option value="pending">Pending</option> </select></form></div></div><span style="color:red;font-size:12px;">Your Input will reflect in GSTR 3B for the month of <span class="'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate">'+gstr3bmonth+'</span></span><div class="row"><label class="col-md-4">ITC Claimed in</label><div class="col-md-5 p-0"> <div id="datepicker"><input type="text" min="2017-08-15" value="'+tddate+'" class="form-control input-datepicker" name="date" id="'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate" onchange="gstr3bmonthChange(\''+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate\')" required/><span style="position: absolute;top: 20%;right: 0%;pointer-events: none;"><i class="fa fa-calendar"></i> </span></div></div></div><div class="row" style="margin-top: 10px;"><label class="col-md-4">ITC Amount</label><input id="'+data.userid+varRetType.replace(" ", "_")+'itc_dropamt" type="text"  maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" onkeyup="itcclaim()" pattern="^[0-9]+(\.[0-9]{1,2})?$" class="form-control itc_amount col-md-2" value="100" style="width:  45px;"><span class="col-md-1" style="padding: 0px;margin-top:6px">%</span><button class="btn btn-blue col-md-1" id="claimbtn" style="height:24px" onclick="updateITCType(\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')" value="ok"> ok </button></div><a class="" href="#"  style="" onclick="updateunclaimdetails(\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')">Clear ITC Claimed Values</a></div></div>';
									}
								}else{
									if(data.invtype == 'Credit Note' || data.invtype == 'Credit Note(UR)'){
										return '<span class="text-right ind_formats nottoedit" id="itcVal'+varRetTypeCode+data.userid+'">-0.00</span><div class="dropdown nottoedit" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" onclick="itcClaimedData(\''+varRetType+'\',\''+data.userid+'\')" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width: 300px;height: 180px;font-size: 15px;"><span aria-hidden="true"> <img src="'+_getContextPath()+'/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span>'+itcLoader+'<div class="row"><label class="col-md-4" style="padding-top: 0px;padding-bottom: 13px;padding-left: 15px;padding-right: 1px;">Input Type<span aria-hidden="true"></span></label><div class="col-md-6"><form><select id="'+data.userid+varRetType.replace(" ", "_")+'itc_droptype" onchange="updateITCEligibity(this.value,\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Services</option><option value="no">Ineligible</option><option value="pending">Pending</option> </select></form></div></div><span style="color:red;font-size:12px;">Your Input will reflect in GSTR 3B for the month of <span class="'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate">'+gstr3bmonth+'</span></span><div class="row"><label class="col-md-4">ITC Claimed in</label><div class="col-md-5 p-0"> <div id="datepicker"><input type="text" min="2017-08-15" value="'+tddate+'" class="form-control input-datepicker" name="date" id="'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate" onchange="gstr3bmonthChange(\''+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate\')" required/><span class="itcclaimeddatewrap"z pointer-events: none; style="position: absolute;top: 20%;right: 0%;pointer-events: none;"><i class="fa fa-calendar"></i> </span></div></div></div><div class="row" style="margin-top: 10px;"><label class="col-md-4">ITC Amount</label><input id="'+data.userid+varRetType.replace(" ", "_")+'itc_dropamt" type="text"  maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" onkeyup="itcclaim()" pattern="^[0-9]+(\.[0-9]{1,2})?$" class="form-control itc_amount col-md-2" value="100" style="width:  45px;"><span class="col-md-1" style="padding: 0px;margin-top:6px">%</span><button class="btn btn-blue col-md-1" id="claimbtn" style="height:24px" onclick="updateITCType(\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')" value="ok"> ok </button></div><a class="" href="#"  style="" onclick="updateunclaimdetails(\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')">Clear ITC Claimed Values</a></div></div>';
									}else{
										return '<span class="text-right ind_formats nottoedit" id="itcVal'+varRetTypeCode+data.userid+'">0.00</span><div class="dropdown nottoedit" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" onclick="itcClaimedData(\''+varRetType+'\',\''+data.userid+'\')" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width: 300px;height: 180px;font-size: 15px;"><span aria-hidden="true"> <img src="'+_getContextPath()+'/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span>'+itcLoader+'<div class="row"><label class="col-md-4" style="padding-top: 0px;padding-bottom: 13px;padding-left: 15px;padding-right: 1px;">Input Type<span aria-hidden="true"></span></label><div class="col-md-6"><form><select id="'+data.userid+varRetType.replace(" ", "_")+'itc_droptype" onchange="updateITCEligibity(this.value,\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Services</option><option value="no">Ineligible</option><option value="pending">Pending</option> </select></form></div></div><span style="color:red;font-size:12px;">Your Input will reflect in GSTR 3B for the month of <span class="'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate">'+gstr3bmonth+'</span></span><div class="row"><label class="col-md-4">ITC Claimed in</label><div class="col-md-5 p-0"> <div id="datepicker"><input type="text" min="2017-08-15" value="'+tddate+'" class="form-control input-datepicker" name="date" id="'+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate" onchange="gstr3bmonthChange(\''+data.userid+varRetType.replace(" ", "_")+'itc_claimeddate\')" required/><span class="itcclaimeddatewrap"z pointer-events: none; style="position: absolute;top: 20%;right: 0%;pointer-events: none;"><i class="fa fa-calendar"></i> </span></div></div></div><div class="row" style="margin-top: 10px;"><label class="col-md-4">ITC Amount</label><input id="'+data.userid+varRetType.replace(" ", "_")+'itc_dropamt" type="text"  maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" onkeyup="itcclaim()" pattern="^[0-9]+(\.[0-9]{1,2})?$" class="form-control itc_amount col-md-2" value="100" style="width:  45px;"><span class="col-md-1" style="padding: 0px;margin-top:6px">%</span><button class="btn btn-blue col-md-1" id="claimbtn" style="height:24px" onclick="updateITCType(\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')" value="ok"> ok </button></div><a class="" href="#"  style="" onclick="updateunclaimdetails(\''+varRetType+'\',\''+data.userid+'\',\''+data.invtype+'\')">Clear ITC Claimed Values</a></div></div>';
									}
								}
							}
						}};
	if(varRetType == 'GSTR1' || varRetType == 'ANX1'){
	return [
	        chkBx , status, itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt,
		     
		     {data: function ( data, type, row ) {
		     		var status = data.gstStatus;
		     		var titleCd = '';
		     		var clr = '';
		     		if(status == null || status == ''){
		     			titleCd = 'Pending';
		     			clr= '#FF9900;';
		     		}else if(status == 'SUCCESS' || status == 'Filed'){
		     			if(status == 'SUCCESS'){
							titleCd = "Uploaded";
						}else{
							titleCd = status;
						}
		     			clr= 'green;';
		     		}else if(status == 'CANCELLED'){
		     			titleCd = 'Cancelled';
		     			clr= 'red;';
		     		}else if(status == 'In Progress'){
						titleCd = 'In Progress';
		     			clr= 'blue;';
					}else{
						titleCd = status;
		     			clr= 'red;';
					}
		           	 return '<a href="#" onclick="sendEmailToCustomers(\''+data.userid+'\',\''+data.invoiceno+'\',\''+data.billedtoname+'\',\''+varRetType+'\')" class="nottoedit" style="float: right;"> <i class="fa fa-envelope" style="font-size:15px;"></i></a><a href="'+_getContextPath()+'/invprint/'+urlSuffix+'/'+data.userid+'" class="nottoedit" target="_blank"  style="float: right;margin-right: 5px;"> <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" style="margin-right: 2px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : '+titleCd+'" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:'+clr+';"></i></span>';
		             //return '<a href="'+_getContextPath()+'/invprint/'+urlSuffix+'/'+data.userid+'" class="nottoedit" target="_blank"  style="float: right;padding-top: 4px;"> <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-mail" style="margin-right: 2px;float: right;padding-top: 4px;"> <i class="fa fa-envelope" style="font-size: 17px;vertical-align: middle;float:right"></i></a><a href="#" class="permissionInvoices-Sales-Edit" style="margin-right: 2px;padding-top: 4px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : '+titleCd+'" style="float: right;margin-right: 2px;padding-top: 4px;"><i class="fa fa-circle" style="color:'+clr+';"></i></span>';
                }}			            
		     ];
		}else if(varRetType == 'Purchase Register' || varRetType == 'GSTR2'){
			if(booksOrReturns == 'PurchaseRegister'){
				return [chkBx , status, itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt, itcClimed,
			        {data: function ( data, type, row ) {
			        	var invtype = data.invtype;
			        	var matchingStatus = data.matchingStatus;
			        	var cfs = '';
			        	if(invtype == 'B2B'){
			        		if(data.b2b && data.b2b.length > 0){
								if(data.b2b[0].cfs != undefined){
									cfs = data.b2b[0].cfs;
								}
							}
			        	}else if(invtype == 'Credit/Debit Notes'){
			        		if(data.cdn && data.cdn.length > 0){
								if(data.cdn[0].cfs != undefined){
									cfs = data.cdn[0].cfs;
								}
							}
			        	}
			        	var titleMs = '';
			        	var icolor = 'style="color:#53bc45"';
			        	if(invtype == 'B2B' || invtype == 'Credit Note' || invtype == 'Debit Note'  || invtype == 'Credit/Debit Notes' || invtype == 'Credit/Debit Note for Unregistered Taxpayers' || invtype == 'Import Goods'){
							if(matchingStatus == 'Matched' || matchingStatus == 'Matched In Other Months' || matchingStatus == 'Round Off Matched' || matchingStatus == 'Probable Matched'){
								if(cfs == ''){
									titleMs = matchingStatus+' In GSTR2A';
									icolor = 'style="color:#53bc45"'; 	
								}else if(cfs == 'Y'){
									titleMs = matchingStatus+' In GSTR2A<br> Supplier is Filed';
									icolor = 'style="color:#53bc45"'; 	
								}else{
									titleMs = matchingStatus+' In GSTR2A<br> Supplier is Not Filed';
									icolor = 'style="display: block;background: linear-gradient( 90deg, #53bc45 49.7%, rgb(235, 188, 0) 50% ); -webkit-background-clip: text;-webkit-text-fill-color: transparent;margin-right: -4px;"'; 	
								}
							}else if(matchingStatus == '' || matchingStatus == null){
								titleMs = 'Not In GSTR2A';
								icolor = 'style="color:red"';
							}else if(matchingStatus == 'Mismatched' || matchingStatus == 'Not In GSTR 2A' || matchingStatus == 'Not In Purchases'){
								titleMs = matchingStatus;
								icolor = 'style="color:red"';
							}else{
								titleMs = matchingStatus;
								icolor = 'style="color:red"';
							}
						}else{
							titleMs = 'Not Eligible';
							icolor = 'style="color:yellow"';
						}
			     		 return '<a href="'+_getContextPath()+'/invprint/'+urlSuffix+'/'+data.userid+'" class="nottoedit" target="_blank" style="float: right;"> <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+titleMs+'" style="float: right;margin-right: 2px;"><i class="fa fa-circle" '+icolor+';"></i></span>';
			            }} 
			        ];
			}else{
				return [chkBx , itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt, itcClimed,
						{data: function ( data, type, row ) {
							var invtype = data.invtype;
							var matchingStatus = data.matchingStatus;
							var cfs = '';
							if(invtype == 'B2B'){
								if(data.b2b && data.b2b.length > 0){
									if(data.b2b[0].cfs != undefined){
										cfs = data.b2b[0].cfs;
									}
								}
							}else if(invtype == 'Credit/Debit Notes'){
								if(data.cdn && data.cdn.length > 0){
									if(data.cdn[0].cfs != undefined){
										cfs = data.cdn[0].cfs;
									}
								}
							}
							var titleMs = '';
							var icolor = 'style="color:#53bc45"';
							if(invtype == 'B2B' || invtype == 'Credit Note' || invtype == 'Debit Note'  || invtype == 'Credit/Debit Notes' || invtype == 'Credit/Debit Note for Unregistered Taxpayers' || invtype == 'Import Goods'){
								if(matchingStatus == 'Matched' || matchingStatus == 'Matched In Other Months' || matchingStatus == 'Round Off Matched' || matchingStatus == 'Probable Matched'){
									if(cfs == ''){
										titleMs = matchingStatus+' In GSTR2A';
										icolor = 'style="color:#53bc45"'; 	
									}else if(cfs == 'Y'){
										titleMs = matchingStatus+' In GSTR2A<br> Supplier is Filed';
										icolor = 'style="color:#53bc45"'; 	
									}else{
										titleMs = matchingStatus+' In GSTR2A<br> Supplier is Not Filed';
										icolor = 'style="display: block;background: linear-gradient( 90deg, #53bc45 49.7%, rgb(235, 188, 0) 50% ); -webkit-background-clip: text;-webkit-text-fill-color: transparent;margin-right: -4px;"'; 	
									}
								}else if(matchingStatus == '' || matchingStatus == null){
									titleMs = 'Not In GSTR2A';
									icolor = 'style="color:red"';
								}else if(matchingStatus == 'Mismatched' || matchingStatus == 'Not In GSTR 2A' || matchingStatus == 'Not In Purchases'){
									titleMs = matchingStatus;
									icolor = 'style="color:red"';
								}else{
									titleMs = matchingStatus;
									icolor = 'style="color:red"';
								}
							}else{
								titleMs = 'Not Eligible';
								icolor = 'style="color:yellow"';
							}
							 return '<a href="'+_getContextPath()+'/invprint/'+urlSuffix+'/'+data.userid+'" class="nottoedit" target="_blank" style="float: right;"> <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+titleMs+'" style="float: right;margin-right: 2px;"><i class="fa fa-circle" '+icolor+';"></i></span>';
							}} 
						];
			}
		}else if(varRetType == 'EWAYBILL'){
			return [chkBx, ebillStatus, status,ebillDate, validDate, subStype, billtoname, billtogtnn,taxblamt,  totlTax, totalamt,
		        {data: function ( data, type, row ) {
		     		 return '<a href="'+_getContextPath()+'/ewaybillprint/'+urlSuffix+'/'+data.userid+'" target="_blank" class="nottoedit" style="float: right;">  <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>';
		            }} 
		        ];
		}else if(varRetType == 'ANX2'){
			return [chkBx , itype, invsNo, billtoname, ctin,invDate,taxblamt,  totlTax, totalamt, itcClimed, section7,reconcileStatus,portalStatus,actionStatus
		        
		        ];
		}else if(varRetType == 'ANX1'){
			return [chkBx , itype, invsNo, billtoname, ctin,invDate,taxblamt,  totlTax, totalamt
		        
		        ];
		}else if(varRetType == 'GSTR2A'){
			return [itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt,
		        {data: function ( data, type, row ) {
		        	var invtype = data.invtype;
		        	var matchingStatus = data.matchingStatus;
		        	var cfs = '';
		        	if(invtype == 'B2B'){
		        		if(data.b2b && data.b2b.length > 0){
							if(data.b2b[0].cfs != undefined){
								cfs = data.b2b[0].cfs;
							}
						}
		        	}else if(invtype == 'Credit/Debit Notes'){
		        		if(data.cdn && data.cdn.length > 0){
							if(data.cdn[0].cfs != undefined){
								cfs = data.cdn[0].cfs;
							}
						}
		        	}
		        	var titleMs = '';
		        	var icolor = 'style="color:#53bc45"';
		        	if(invtype == 'B2B' || invtype == 'Credit Note' || invtype == 'Debit Note'  || invtype == 'Credit/Debit Notes' || invtype == 'Credit/Debit Note for Unregistered Taxpayers' || invtype == 'Import Goods'){	
						if(matchingStatus == 'Matched' || matchingStatus == 'Matched In Other Months' || matchingStatus == 'Round Off Matched' || matchingStatus == 'Probable Matched'){
							titleMs = matchingStatus+' In Purchases';
							icolor = 'style="color:#53bc45"';
						}else if(matchingStatus == '' || matchingStatus == null){
							titleMs = 'Not In Purchases';
							icolor = 'style="color:red"';
						}else if(matchingStatus == 'Mismatched' || matchingStatus == 'Not In GSTR 2A' || matchingStatus == 'Not In Purchases'){
							titleMs = matchingStatus;
							icolor = 'style="color:red"';
						}else{
							titleMs = matchingStatus;
							icolor = 'style="color:yellow"';
						}
					}
		     		 return '<span data-toggle="tooltip" title="'+titleMs+'" style="float: right;margin-right: 3px;"><i class="fa fa-circle" '+icolor+'></i></span>';
		            }} 
		        ];
		}else if(varRetType == 'Unclaimed'){
			return [chkBx , itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt, itcClimed,
		        {data: function ( data, type, row ) {
		        	var invtype = data.invtype;
		        	var matchingStatus = data.matchingStatus;
		        	var cfs = '';
		        	if(invtype == 'B2B'){
		        		if(data.b2b && data.b2b.length > 0){
							if(data.b2b[0].cfs != undefined){
								cfs = data.b2b[0].cfs;
							}
						}
		        	}else if(invtype == 'Credit/Debit Notes'){
		        		if(data.cdn && data.cdn.length > 0){
							if(data.cdn[0].cfs != undefined){
								cfs = data.cdn[0].cfs;
							}
						}
		        	}
		        	var titleMs = '';
		        	var icolor = 'style="color:#53bc45"';
		        	if(invtype == 'B2B' || invtype == 'Credit Note' || invtype == 'Debit Note'  || invtype == 'Credit/Debit Notes' || invtype == 'Credit/Debit Note for Unregistered Taxpayers' || invtype == 'Import Goods'){
						if(matchingStatus == 'Matched' || matchingStatus == 'Matched In Other Months' || matchingStatus == 'Round Off Matched' || matchingStatus == 'Probable Matched'){
							if(cfs == ''){
								titleMs = matchingStatus+' In GSTR2A';
								icolor = 'style="color:#53bc45"'; 	
							}else if(cfs == 'Y'){
								titleMs = matchingStatus+' In GSTR2A<br> Supplier is Filed';
								icolor = 'style="color:#53bc45"'; 	
							}else{
								titleMs = matchingStatus+' In GSTR2A<br> Supplier is Not Filed';
								icolor = 'style="display: block;background: linear-gradient( 90deg, #53bc45 49.7%, rgb(235, 188, 0) 50% ); -webkit-background-clip: text;-webkit-text-fill-color: transparent;margin-right: -4px;"'; 	
							}
						}else if(matchingStatus == '' || matchingStatus == null){
							titleMs = 'Not In GSTR2A';
							icolor = 'style="color:red"';
						}else if(matchingStatus == 'Mismatched' || matchingStatus == 'Not In GSTR 2A' || matchingStatus == 'Not In Purchases'){
							titleMs = matchingStatus;
							icolor = 'style="color:red"';
						}else{
							titleMs = matchingStatus;
							icolor = 'style="color:red"';
						}
					}else{
						titleMs = 'Not Eligible';
						icolor = 'style="color:yellow"';
					}
		     		 return '<a href="'+_getContextPath()+'/invprint/'+urlSuffix+'/'+data.userid+'" class="nottoedit" target="_blank" style="float: right;"> <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+titleMs+'" style="float: right;margin-right: 2px;"><i class="fa fa-circle" '+icolor+';"></i></span>';
		            }} 
		        ];
		}else if(varRetType == 'PROFORMAINVOICES' || varRetTypeCode == 'DELIVERYCHALLANS' || varRetTypeCode == 'ESTIMATES' || varRetTypeCode == 'PurchaseOrder'){
			return [
		        chkBx, itype, invsNo, billtoname, billtogtnn,invDate,taxblamt,  totlTax, totalamt,
			     
			     {data: function ( data, type, row ) {
			          if(varRetTypeCode == 'PurchaseOrder'){
			        	  return '<a href="'+_getContextPath()+'/invprint/'+urlSuffix+'/'+data.userid+'" class="nottoedit" target="_blank"  style="float: right;"> <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" style="margin-right: 2px;float:right"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>';
			          }else{
			        	  return '<a href="#" onclick="sendEmailToCustomers(\''+data.userid+'\',\''+data.invoiceno+'\',\''+data.billedtoname+'\',\''+varRetTypeCode+'\')" class="nottoedit" style="float: right;"> <i class="fa fa-envelope" style="font-size:15px;"></i></a><a href="'+_getContextPath()+'/invprint/'+urlSuffix+'/'+data.userid+'" class="nottoedit" target="_blank"  style="float: right;"> <img src="'+_getContextPath()+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;margin-right: 5px;"></a><a href="#" style="margin-right: 2px;float:right"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>'; 
			          } 	 
			            }}			            
			     ];
		}
	return [];
}


function getInvColumnDefs(varRetType){
	if(varRetType == 'ANX2'){
		return  [
					{
						"targets":  [6,7, 8, 9],
						className: 'dt-body-right'
					},
					{
						"targets":  [10],
						className: 'dt-body-center'
					},
					{
						"targets": 0,
						"orderable": false
					}
					
				];
	}else if(varRetType == 'GSTR1' || varRetType == 'ANX1'){
		return  [
					{
						"targets":  [7, 8, 9],
						className: 'dt-body-right'
					},
					{
						"targets": 0,
						"orderable": false
					}
				];
	}else if(varRetType == 'Purchase Register' || varRetType == 'GSTR2' || varRetType == 'Unclaimed'){
		return [
				{
				"targets":  [7, 8, 9,10],
				className: 'dt-body-right'
				},
				{
				"targets": 0,
				"orderable": false
				}
		];
	}else if(varRetType == 'GSTR2A'){
		return [
				{
				"targets":  [5,6,7],
				className: 'dt-body-right'
				}
		];
	}else if(varRetType == 'EWAYBILL'){
		return [
			{
			"targets":  [8, 9, 10, 11],
			className: 'dt-body-right'
			},
			{
			"targets": 0,
			"orderable": false
			}
	];
}else if(varRetType == 'PROFORMAINVOICES'|| varRetType == 'DELIVERYCHALLANS' || varRetType == 'ESTIMATES' || varRetType == 'PurchaseOrder'){
		return [
			{
			"targets":  [6,7,8],
			className: 'dt-body-right'
			},
			{
			"targets": 0,
			"orderable": false
			}
	];
}else if(varRetType == 'EXPENSES'){
	return [
		{
		"targets":  [4],
		className: 'dt-body-right'
		},
		{
			"targets":  [2,3,5],
			className: 'dt-body-center'
		},
		{
			"targets": 0,
			"orderable": false
		}
];
}
	return [];
}

function applyInvFilters(retType) {
	var retTypeCode = retType.replace(" ", "_");
	var statusOptions = new Array();
	var stoptions = "";
	if(retType != 'DELIVERYCHALLANS' && retType != 'PROFORMAINVOICES' && retType != 'ESTIMATES' && retType != 'PurchaseOrder' && retType != 'Unclaimed' && retType != 'GSTRA'){
		statusOptions = $('#multiselect'+retTypeCode+'1 option:selected');
		stoptions = $('#multiselect'+retTypeCode+'1');
	}
	var supplyTypeOptions = new Array();
	//var subSupplyTypeOptions = new Array();
	var recStatusOptions = new Array();
	var portalStatusOptions = new Array();
	var actionOptions = new Array();
	var typeOptions = $('#multiselect'+retTypeCode+'2 option:selected');
	var userOptions = $('#multiselect'+retTypeCode+'3 option:selected');
	var vendorOptions = $('#multiselect'+retTypeCode+'4 option:selected');
	var branchOptions = $('#multiselect'+retTypeCode+'5 option:selected');
	var verticalOptions = $('#multiselect'+retTypeCode+'6 option:selected');
	var reverseChargeOptions = $('#multiselect'+retTypeCode+'7 option:selected');
	var reconOptions = $('#multiselect'+retTypeCode+'8 option:selected');
	supplyTypeOptions = $('#multiselect'+retTypeCode+'9 option:selected');
	docTypeOptions = $('#multiselect'+retTypeCode+'10 option:selected');
	recStatusOptions = $('#multiselect'+retTypeCode+'11 option:selected');
	portalStatusOptions = $('#multiselect'+retTypeCode+'12 option:selected');
	actionOptions = $('#multiselect'+retTypeCode+'13 option:selected');
	var gstr2aFilingStatusOptions = $('#multiselect'+retTypeCode+'14 option:selected');
	var subSupplyTypeOptions = $('#multiselect'+retTypeCode+'15 option:selected');
	var customFieldOptions1 = $('#multiselect'+retTypeCode+'16 option:selected');
	var customFieldOptions2 = $('#multiselect'+retTypeCode+'17 option:selected');
	var customFieldOptions3 = $('#multiselect'+retTypeCode+'18 option:selected');
	var customFieldOptions4 = $('#multiselect'+retTypeCode+'19 option:selected');
	var ewaybillStatusOptions = $('#multiselect'+retTypeCode+'20 option:selected');
	var sDate = $('#start-date').val();
	var eDate = $('#end-date').val();
	if(statusOptions.length > 0 || typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0 || reverseChargeOptions.length > 0 || supplyTypeOptions.length > 0 || docTypeOptions.length > 0 || recStatusOptions.length > 0 || portalStatusOptions.length > 0 || actionOptions.length > 0 || gstr2aFilingStatusOptions.length > 0 || reconOptions.length > 0 || subSupplyTypeOptions.length > 0 || customFieldOptions1.length > 0 || customFieldOptions2.length > 0 || customFieldOptions3.length > 0 || customFieldOptions4.length > 0 || ewaybillStatusOptions > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		if(retType == 'GSTR2A'){
			if(sDate != "" && eDate != ""){
				$('.normaltable .filter').css("display","block");
			}else{
				$('.normaltable .filter').css("display","none");
			}
		}else{
			$('.normaltable .filter').css("display","none");
		}
	}
	var statusArr=new Array();
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var branchArr=new Array();
	var verticalArr=new Array();
	var reverseChargeArr=new Array();
	var supplyTypeArr=new Array();
	var docTypeArr=new Array();
	var reconArr = new Array();
	var recStatusArr=new Array();
	var portalStatusArr=new Array();
	var actionArr=new Array();
	var gstr2aFilingStatusArr=new Array();
	var subSupplyTypeArr=new Array();
	var customFieldArr1=new Array();
	var customFieldArr2=new Array();
	var customFieldArr3=new Array();
	var customFieldArr4=new Array();
	var ewayStatusArr=new Array();
	var filterContent='';
	
	if(recStatusOptions.length > 0) {
		for(var i=0;i<recStatusOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+recStatusOptions[i].value+'<span data-val="'+recStatusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			recStatusArr.push(recStatusOptions[i].value);
		}
	}
	if(portalStatusOptions.length > 0) {
		for(var i=0;i<portalStatusOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+portalStatusOptions[i].text+'<span data-val="'+portalStatusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			portalStatusArr.push(portalStatusOptions[i].value);
		}
	}
	if(actionOptions.length > 0) {
		for(var i=0;i<actionOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+actionOptions[i].text+'<span data-val="'+actionOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			actionArr.push(actionOptions[i].value);
		}
	}
	if(statusOptions.length > 0) {
		for(var i=0;i<statusOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+statusOptions[i].text+'<span data-val="'+statusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			statusArr.push(statusOptions[i].value);
		}
	}
	if(typeOptions.length > 0) {
		for(var i=0;i<typeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+typeOptions[i].text+'<span data-val="'+typeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			typeArr.push(typeOptions[i].value);
		}
	}
	if(userOptions.length > 0) {
		for(var i=0;i<userOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+userOptions[i].value+'<span data-val="'+userOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			var userValue = userOptions[i].value;
			if(userValue.includes("&")){
				userValue = userValue.replace("&","-mgst-");
			}
			userArr.push(userValue);
		}
	}
	if(vendorOptions.length > 0) {
		for(var i=0;i<vendorOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+vendorOptions[i].value+'<span data-val="'+vendorOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			var vendorValue = vendorOptions[i].value;
			if(vendorValue.includes("&")){
				vendorValue = vendorValue.replace("&","-mgst-");
			}
			vendorArr.push(vendorValue);
		}
	}
	if(branchOptions.length > 0) {
		for(var i=0;i<branchOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+branchOptions[i].value+'<span data-val="'+branchOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			branchArr.push(branchOptions[i].value);
		}
	}
	if(verticalOptions.length > 0) {
		for(var i=0;i<verticalOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+verticalOptions[i].value+'<span data-val="'+verticalOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			verticalArr.push(verticalOptions[i].value);
		}
	}
	if(reverseChargeOptions.length > 0) {
		for(var i=0;i<reverseChargeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+reverseChargeOptions[i].value+'<span data-val="'+reverseChargeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			reverseChargeArr.push(reverseChargeOptions[i].value);
		}
	}
	if(supplyTypeOptions.length > 0) {
		for(var i=0;i<supplyTypeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+supplyTypeOptions[i].text+'<span data-val="'+supplyTypeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			supplyTypeArr.push(supplyTypeOptions[i].value);
		}
	}
	if(subSupplyTypeOptions.length > 0) {
		for(var i=0;i<subSupplyTypeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+subSupplyTypeOptions[i].text+'<span data-val="'+subSupplyTypeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			subSupplyTypeArr.push(subSupplyTypeOptions[i].value);
		}
	}
	if(docTypeOptions.length > 0) {
		for(var i=0;i<docTypeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+docTypeOptions[i].text+'<span data-val="'+docTypeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			docTypeArr.push(docTypeOptions[i].value);
		}
	}
	if(gstr2aFilingStatusOptions.length > 0){
		for(var i=0;i<gstr2aFilingStatusOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+gstr2aFilingStatusOptions[i].text+'<span data-val="'+gstr2aFilingStatusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			gstr2aFilingStatusArr.push(gstr2aFilingStatusOptions[i].value);
		}
	}
	if(reconOptions.length > 0){
		for(var i=0;i<reconOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+reconOptions[i].text+'<span data-val="'+reconOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			reconArr.push(reconOptions[i].value);
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
	if(ewaybillStatusOptions.length > 0) {
		for(var i=0;i<ewaybillStatusOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+ewaybillStatusOptions[i].text+'<span data-val="'+ewaybillStatusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			ewayStatusArr.push(ewaybillStatusOptions[i].value);
		}
	}
	
	if(retTypeCode == "GSTR2A"){
		if(sDate != "" && eDate != ""){
			filterContent += '<span data-role="tagsinput" placeholder="" class="btaginput btaginput_date">Date : '+sDate+' To '+eDate+'<span data-val="Date : '+sDate+' Between '+eDate+'" class="gstr2aInvDate" data-role="remove"></span></span>';
		}
	}
	$('#divFilters'+retTypeCode).html(filterContent);
	var booksOrReturns = '';
	if(retType != 'DELIVERYCHALLANS' && retType != 'PROFORMAINVOICES' && retType != 'ESTIMATES' && retType != 'PurchaseOrder' && retType != 'EWAYBILL' && retType != 'Unclaimed'){
		booksOrReturns = stoptions.data('booksOrReturns');
	}else{
		booksOrReturns = undefined;
	}
	reloadInvTable(retTypeCode, statusArr, typeArr, userArr,vendorArr,branchArr,verticalArr,reverseChargeArr, booksOrReturns,supplyTypeArr,docTypeArr,recStatusArr,portalStatusArr,actionArr,gstr2aFilingStatusArr,reconArr,subSupplyTypeArr,customFieldArr1,customFieldArr2,customFieldArr3,customFieldArr4,ewayStatusArr,sDate,eDate);
	reloadExcelUrls(retTypeCode, statusArr, typeArr, userArr,vendorArr,branchArr,verticalArr,reverseChargeArr, supplyTypeArr,docTypeArr,recStatusArr,portalStatusArr,actionArr,gstr2aFilingStatusArr,reconArr,subSupplyTypeArr,customFieldArr1,customFieldArr2,customFieldArr3,customFieldArr4,ewayStatusArr,sDate,eDate);
	//invoiceTable[retTypeCode].draw();
}
function reloadExcelUrls(retTypeCode, statusArr, typeArr, userArr,vendorArr,branchArr,verticalArr,reverseChargeArr, supplyTypeArr,docTypeArr,recStatusArr,portalStatusArr,actionArr,gstr2aFilingStatusArr,reconArr,subSupplyTypeArr,customFieldArr1,customFieldArr2,customFieldArr3,customFieldArr4,ewayStatusArr,sDate,eDate){
	var pUrl = invTableUrl[retTypeCode];
	var appd = '';
	if(statusArr.length > 0){
		appd+='&paymentStatus='+statusArr.join(',');
	}
	if(typeArr.length > 0){
		appd+='&invoiceType='+typeArr.join(',');
	}
	if(userArr.length > 0){
		appd+='&user='+userArr.join(',');
	}
	if(vendorArr.length > 0){
		appd+='&vendor='+vendorArr.join(',');
	}
	if(branchArr.length > 0){
		appd+='&branch='+branchArr.join(',');
	}
	if(verticalArr.length > 0){
		appd+='&vertical='+verticalArr.join(',');
	}
	if(reverseChargeArr.length > 0){
		appd+='&reverseCharge='+reverseChargeArr.join(',');
	}
	if(supplyTypeArr.length > 0){
		appd+='&supplyType='+supplyTypeArr.join(',');
	}
	if(docTypeArr.length > 0){
		appd+='&documentType='+docTypeArr.join(',');
	}
	if(gstr2aFilingStatusArr.length > 0){
		appd+='&gstr2aFilingStatus='+gstr2aFilingStatusArr.join(',');
	}
	if(reconArr.length > 0){
		appd+='&reconStatus='+reconArr.join(',');
	}
	if(subSupplyTypeArr.length > 0){
		appd+='&subSupplyType='+subSupplyTypeArr.join(',');
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
	if(ewayStatusArr.length > 0){
		appd+='&status='+ewayStatusArr.join(',');
	}
	if(sDate != ""){
		appd+='&fromtime='+sDate;
	}
	if(eDate != ""){
		appd+='&totime='+eDate;
	}
	pUrl += '&'+appd;
	var invoiceUrl = invTableDwnld_invoiceUrl[retTypeCode]+appd;
	var itemUrl = invTableDwnld_itemUrl[retTypeCode]+appd;
	var fullUrl = invTableDwnld_fullUrl[retTypeCode]+appd;
	$('#itemWiseUrl'+retTypeCode).attr('href',itemUrl);
	$('#invWiseUrl'+retTypeCode).attr('href',invoiceUrl);
	$('#fullDwnldUrl'+retTypeCode).attr('href',fullUrl);
}

function reloadInvTable(retTypeCode, statusArr, typeArr, userArr,vendorArr,branchArr,verticalArr,reverseChargeArr, booksOrReturns,supplyTypeArr,docTypeArr,recStatusArr,portalStatusArr,actionArr,gstr2aFilingStatusArr,reconArr,subSupplyTypeArr,customFieldArr1,customFieldArr2,customFieldArr3,customFieldArr4,ewayStatusArr,sDate,eDate){
	var pUrl = invTableUrl[retTypeCode];
	var appd = 'booksorReturns='+booksOrReturns;
	if(statusArr.length > 0){
		appd+='&paymentStatus='+statusArr.join(',');
	}
	if(typeArr.length > 0){
		appd+='&invoiceType='+typeArr.join(',');
	}
	if(userArr.length > 0){
		appd+='&user='+userArr.join(',');
	}
	if(vendorArr.length > 0){
		appd+='&vendor='+vendorArr.join(',');
	}
	if(branchArr.length > 0){
		appd+='&branch='+branchArr.join(',');
	}
	if(verticalArr.length > 0){
		appd+='&vertical='+verticalArr.join(',');
	}
	if(reverseChargeArr.length > 0){
		appd+='&reverseCharge='+reverseChargeArr.join(',');
	}
	if(supplyTypeArr.length > 0){
		appd+='&supplyType='+supplyTypeArr.join(',');
	}
	if(docTypeArr.length > 0){
		appd+='&documentType='+docTypeArr.join(',');
	}
	if(gstr2aFilingStatusArr.length > 0){
		appd+='&gstr2aFilingStatus='+gstr2aFilingStatusArr.join(',');
	}
	if(reconArr.length > 0){
		appd+='&reconStatus='+reconArr.join(',');
	}
	if(subSupplyTypeArr.length > 0){
		appd+='&subSupplyType='+subSupplyTypeArr.join(',');
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
	if(ewayStatusArr.length > 0){
		appd+='&status='+ewayStatusArr.join(',');
	}
	if(sDate != ""){
		appd+='&fromtime='+sDate;
	}
	if(eDate != ""){
		appd+='&totime='+eDate;
	}
	pUrl += '&'+appd;
	invTable[retTypeCode].ajax.url(pUrl).load();
}


function updateSelection(id, retType, status,invoicetype, chkBox) {
	if(selArray[retType.replace(" ", "_")] == undefined) {
		selArray[retType.replace(" ", "_")] = new Array();
	}
	if(retType == 'EWAYBILL'){
		
		if(chkBox.checked) {
			ewaybillArray.push(id);
		} else {
			var eArray=new Array();
			ewaybillArray.forEach(function(inv) {
				if(inv != id) {
					eArray.push(inv);
				}
			});
			ewaybillArray = eArray;
		}
		if(ewaybillArray.length == 1){
			$('#addtogstr1btn').removeClass('disabled');
			vehicleUpdateArray=new Array();
			vehicleUpdateArray.push(ewaybillArray);
			$('#vehicleupdate').removeClass('disabled');
		}else{
			$('#vehicleupdate').addClass('disabled');
		}
		
		if(ewaybillArray.length > 0){
			$('#deleteEwaybillInvoices').removeClass('disabled');
			$('.select_msg').text('You have Selected '+ewaybillArray.length+' Invoice(s)');
			$('#cancelAllEwaybills,#rejectAllEwaybills').removeClass('disabled');
			if($("span").hasClass("validateEbill")){
				$('#extendValidity').removeClass('disabled');
			}
		}else{
			$('#deleteEwaybillInvoices').addClass('disabled');
			$('.select_msg').text('');
			$('#cancelAllEwaybills,#rejectAllEwaybills').addClass('disabled');
			if($("span").hasClass("validateEbill")){
				$('#extendValidity').addClass('disabled');
			}
		}
	}
	if(retType == 'GSTR2' || retType == 'Purchase Register' || retType == 'Purchase_Register' || retType == 'Unclaimed'){
	
		if(chkBox.checked) {
			ITCclaimedArray.push(id);
		} else {
			var ITCArray=new Array();
			ITCclaimedArray.forEach(function(inv) {
				if(inv != id) {
					ITCArray.push(inv);
				}
			});
			ITCclaimedArray = ITCArray;
		}
		
	}
	if(retType == 'Purchase Register' || retType == 'PurchaseOrder'){
		if(chkBox.checked) {
			POArray.push(id);
			if(prchInvArray.indexOf(id) != -1){
				
			}else{
				prchInvArray.push(id);
			}
			
		} else {
			var pArray=new Array();
			var poArray = new Array();
			prchInvArray.forEach(function(inv) {
				if(inv != id) {
					pArray.push(inv);
				}
			});
			prchInvArray = pArray;
			prchInvArray.forEach(function(inv) {
				if(inv != id) {
					poArray.push(inv);
				}
			});
			prchInvArray = poArray;
		}
		var purchaseTableLength = $('#dbTable'+retType.replace(" ", "_")).dataTable().fnGetData().length;
		if(prchInvArray.length == purchaseTableLength){
			$('#check'+retType.replace(" ","_")).prop("checked",true);
		}else{
			$('#check'+retType.replace(" ","_")).prop("checked",false);
		}
		if(prchInvArray.length > 0){
			if(retType == 'PurchaseOrder'){
				$('#deletePurchaseOrderInvoices').removeClass('disabled');
			}else{
				$('#deletePurchaseInvoices').removeClass('disabled');
			}
			$('.select_msg').text('You have Selected '+prchInvArray.length+' Invoice(s)');
		}else{
			$('.select_msg').text('');
			if(retType == 'PurchaseOrder'){
				$('#deletePurchaseOrderInvoices').addClass('disabled');
			}else{
			$('#deletePurchaseInvoices').addClass('disabled');
			}
		}
		if(prchInvArray.length == 1){
			recArray=new Array();
			recArray.push(prchInvArray);
			if(invoicetype == "Advances" || invoicetype == "Advance Adjusted Detail"){
				$('.recordpayments').addClass('disabled');
			}else{
				if(invoicetype == "Credit Note"){
					$('.recordpayments').html('Add Receipt');
					$('.recordpayments').attr("onclick","updatePayment('GSTR1',\""+invoicetype+"\")");
				}
				$('.recordpayments').removeClass('disabled');
			}
		}else{
			$('.recordpayments').html('Add Payment');
			$('.recordpayments').attr("onclick","updatePayment('GSTR2',\""+invoicetype+"\")");
			$('.recordpayments').addClass('disabled');
		}
		if(retType == 'Purchase Register'){
			if(prchInvArray.length > 0){
				$('#itcUnclaimbtn').removeClass('disabled');
			}else{
				$('#itcUnclaimbtn').addClass('disabled');
			}
	    }
	}else if(retType == 'PROFORMAINVOICES'){
		if(chkBox.checked) {
			PIArray.push(id);
		} else {
			var pArray=new Array();
			PIArray.forEach(function(inv) {
				if(inv != id) {
					pArray.push(inv);
				}
			});
			PIArray = pArray;
		}
		if(PIArray.length > 0){
			$('#deleteProformaInvoices').removeClass('disabled');
			$('.select_msg').text('You have Selected '+PIArray.length+' Invoice(s)');
		}else{
			$('#deleteProformaInvoices').addClass('disabled');$('.select_msg').text('');
		}
		if(PIArray.length == 1){
			$('.books_email_btn').removeClass('disabled');
			$('.books_email_btn').addClass('em_btn'+retType);
			$('.em_btn'+retType).attr('onclick','EmailSendToCustomer(\''+id+'\',\''+retType+'\')');
		}else{
			$('.em_btn'+retType).removeAttr("onclick");
			$('.books_email_btn').addClass('disabled');
		}
	}else{
		if(chkBox.checked) {
			selInvArray.push(id);
			/* if(status != 'CANCELLED'){ */
				selArray[retType.replace(" ", "_")].push(id);
			/* } */
		} else {
			var tArray=new Array();
			selInvArray.forEach(function(inv) {
				if(inv != id) {
					tArray.push(inv);
				}
			});
			selInvArray = tArray;
			tArray=new Array();
			selArray[retType.replace(" ", "_")].forEach(function(inv) {
				if(inv != id) {
					tArray.push(inv);
				}
			});
			selArray[retType.replace(" ", "_")] = tArray;
		}
		var salesTableLength = $('#dbTable'+retType.replace(" ", "_")).dataTable().fnGetData().length;
		if(selInvArray.length == salesTableLength){
			$('#check'+retType.replace(" ","_")).prop("checked",true);
		}else{
			$('#check'+retType.replace(" ","_")).prop("checked",false);
		}
		if(selInvArray.length > 0){
			if(retType == 'DELIVERYCHALLANS'){
				$('#deleteDelChallanInvoices').removeClass('disabled');
			}else if(retType == 'ESTIMATES'){
				$('#deleteEstimateInvoices').removeClass('disabled');
			}else{
				$('#deleteSalesInvoices').removeClass('disabled');
				if(retType  != 'EWAYBILL' && otherreturnType != 'SalesRegister'){
					$('.select_msg').css("left","32%");
				}
			}
			$('.select_msg').text('You have Selected '+selInvArray.length+' Invoice(s)');
		}else{
			if(retType == 'DELIVERYCHALLANS'){
				$('#deleteDelChallanInvoices').addClass('disabled');
			}else if(retType == 'PROFORMAINVOICES'){
				$('#deleteProformaInvoices').addClass('disabled');
			}else if(retType == 'ESTIMATES'){
				$('#deleteEstimateInvoices').addClass('disabled');
			}else{
				$('#deleteSalesInvoices').addClass('disabled');
			}
			$('.select_msg').text('');
		}
		if(selInvArray.length == 1){
			recArray=new Array();
			recArray.push(selInvArray);
			var ret = 'GSTR1';
			if(invoicetype == "Advances" || invoicetype == "Advance Adjusted Detail"){
				$('.recordpayments').addClass('disabled');
			}else{
				if(invoicetype == "Credit Note"){
					$('.recordpayments').html('Add Payment');
					ret = 'GSTR2';
					$('.recordpayments').attr("onclick",'updatePayment(\''+ret+'\',\''+invoicetype+'\')');
				}
				$('.recordpayments').removeClass('disabled');
			}
			$('.books_email_btn').removeClass('disabled');
			$('.recordpayments').attr("onclick",'updatePayment(\''+ret+'\',\''+invoicetype+'\')');
			$('.books_email_btn').addClass('em_btn'+retType);
			$('.em_btn'+retType).attr('onclick','EmailSendToCustomer(\''+id+'\',\''+retType+'\')');
		}else{
			$('.recordpayments').html('Add Receipt');
			$('.recordpayments').addClass('disabled');
			$('.em_btn'+retType).removeAttr("onclick");
			$('.books_email_btn').addClass('disabled');
		}
	}
	if('${otperror}' == 'Y') {
		$('#idPermissionUpload_Invoice').addClass('disable');
	}
}

function updateITCEligibity(value, retType, invId) {
	var amt = $('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val();
	if(otherconfigdetails != ''){
		if(value == 'ip'){
			$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(otherconfigdetails.itcinput);
		}else if(value == 'is'){
			$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(otherconfigdetails.itcinputService);
		}else if(value == 'cp'){
			$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(otherconfigdetails.itcCapgood);
		}else{
			$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val("0");
		}
		//findITCValue(no);
	}else{
	if(value == 'ip' || value == 'cp') {
		if(amt == 0) {
			$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(100);
		}
	} else {
		$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(0);
	}
	}
}

function updateunclaimdetails(retType, invId,invtype){

var returnType = retType;
if(retType == 'Purchase_Register' || retType == 'Unclaimed'){
	returnType = 'Purchase Register';
}
$.ajax({
	url : contextPath+'/unclaimupdt/'+invId+'/'+returnType,
	async: false,
	cache: false,
	dataType:"json",
	contentType: 'application/json',
	success : function(response) {
	if(response > 0) {
		if(invtype == 'Credit Note' || invtype == 'Credit Note(UR)'){
			$('#itcVal'+retType.replace(" ", "_")+invId).html("-"+formatNumber(response.toFixed(2)));
		}else{
			$('#itcVal'+retType.replace(" ", "_")+invId).html(formatNumber(response.toFixed(2)));
		}
		invoiceArray[returnType.replace(" ", "_")].forEach(function(pInv) {
			if(pInv.id == invId) {
				pInv.totalitc = response;
			}
		});
	}else if(response == 0) {
		if(invtype == 'Credit Note' || invtype == 'Credit Note(UR)'){
			$('#itcVal'+retType.replace(" ", "_")+invId).html("-"+formatNumber(response.toFixed(2)));
		}else{
			$('#itcVal'+retType.replace(" ", "_")+invId).html(formatNumber(response.toFixed(2)));
		}
		invoiceArray[returnType.replace(" ", "_")].forEach(function(pInv) {
			if(pInv.id == invId) {
				pInv.totalitc = response;
			}
		});
	}		
	},
	error : function(e, status, error) {}
});
}

function updateITCType(retType, invId,invtype) {
	var type = $('#'+invId+retType.replace(" ", "_")+'itc_droptype').val();
	var amt = $('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val();
	var claimeddate = $('#'+invId+retType.replace(" ", "_")+'itc_claimeddate').val();
	var returnType = retType;
	if(retType == 'Unclaimed'){
		returnType = 'Purchase Register';
	}
	if(amt != null && amt>100){
		amt = '100';
	}	
	$.ajax({
		url : _getContextPath()+'/itcupdt/'+invId+'/'+returnType+'/'+type+'/'+amt+'/'+claimeddate,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response > 0) {
				if(invtype == 'Credit Note' || invtype == 'Credit Note(UR)'){
					$('#itcVal'+retType.replace(" ", "_")+invId).html("-"+formatNumber(response.toFixed(2)));
				}else{
					$('#itcVal'+retType.replace(" ", "_")+invId).html(formatNumber(response.toFixed(2)));
				}
				invoiceArray[returnType.replace(" ", "_")].forEach(function(pInv) {
					if(pInv.id == invId) {
						pInv.totalitc = response;
					}
				});
			}else if(response == 0) {
				if(invtype == 'Credit Note' || invtype == 'Credit Note(UR)'){
					$('#itcVal'+retType.replace(" ", "_")+invId).html("-"+formatNumber(response.toFixed(2)));
				}else{
					$('#itcVal'+retType.replace(" ", "_")+invId).html(formatNumber(response.toFixed(2)));
				}
				invoiceArray[returnType.replace(" ", "_")].forEach(function(pInv) {
					if(pInv.id == invId) {
						pInv.totalitc = response;
					}
				});
			}
		}
	});
}

function updateMainSelection(retType,filingstatus,chkBox) {
	$('#deleteDelChallanInvoices,#deleteProformaInvoices,#deleteEstimateInvoices,#deleteSalesInvoices,#deletePurchaseInvoices').addClass('disabled');
	if(retType == 'Purchase Register' || retType == 'Unclaimed' || retType == 'PurchaseOrder'){
		ITCclaimedArray = new Array();
		prchInvArray = new Array();
	}else if(retType == 'EWAYBILL'){
		ewaybillArray = new Array();
	}else{
		selInvArray = new Array();
		selArray[retType.replace(" ", "_")] = new Array();
	}
	var check = $('#check'+retType.replace(" ", "_")).prop("checked");
	      var rows = invTable[retType.replace(" ", "_")].rows().nodes();
	      if(check) {
	    	  if(retType == 'EWAYBILL'){
	    		  $('#addtogstr1btn').removeClass('disabled');
	    	  }
		      invTable[retType.replace(" ", "_")].rows().every(function () {
		    	  	var row = this.data();
		    	  	if(retType == 'Purchase Register' || retType == 'Unclaimed' || retType == 'PurchaseOrder'){
		    	  		ITCclaimedArray.push(row.userid);
		    	  	}else if(retType == 'EWAYBILL'){
		    	  		ewaybillArray.push(row.userid);
		    	  	}else{
		    	  		selInvArray.push(row.userid);
		    	  		selArray[retType.replace(" ", "_")].push(row.userid);
		    	  	}
		      });
	      }
	      prchInvArray = ITCclaimedArray;
	      if(retType == 'PROFORMAINVOICES'){
	    	  PIArray = selInvArray;
	      }else if(retType == 'PurchaseOrder'){
	    	  POArray = prchInvArray;
	      }
	      if(retType == 'Purchase Register' || retType == 'Unclaimed' || retType == 'PurchaseOrder'){
		      if(prchInvArray.length > 0){
		    	  if(retType == 'PurchaseOrder'){
		    		  $('#deletePurchaseOrderInvoices').removeClass('disabled');
		    	  }else{
		    		  $('#deletePurchaseInvoices').removeClass('disabled');
		    	  }
		    	  $('.select_msg').text('You have Selected '+prchInvArray.length+' Invoice(s)');
		    	  if(retType == 'Purchase Register'){
		    		  $('#itcUnclaimbtn').removeClass('disabled');
		    	  } 
		      }else{
		    	  $('.select_msg').text('');
		    	  $('#deletePurchaseInvoices,#deletePurchaseOrderInvoices').addClass('disabled');
		    	  $('#itcUnclaimbtn').addClass('disabled');
		      }
	      }
	      if(selInvArray.length > 0){
		      if(retType == 'DELIVERYCHALLANS'){
					$('#deleteDelChallanInvoices').removeClass('disabled');
				}else if(retType == 'PROFORMAINVOICES'){
					$('#deleteProformaInvoices').removeClass('disabled');
				}else if(retType == 'ESTIMATES'){
					$('#deleteEstimateInvoices').removeClass('disabled');
				}else{
					$('#deleteSalesInvoices').removeClass('disabled');
				}
		      $('.select_msg').text('You have Selected '+selInvArray.length+' Invoice(s)').css("left","32%");
	      }else{
	    	 $('.select_msg').text('').css("left","60%");
	      }
	      if(retType == 'EWAYBILL'){
	    	  if(ewaybillArray.length > 0){
				$('#deleteEwaybillInvoices').removeClass('disabled');
				$('.select_msg').text('You have Selected '+ewaybillArray.length+' Invoice(s)');
				$('#cancelAllEwaybills,#rejectAllEwaybills').removeClass('disabled');
				if($("span").hasClass("validateEbill")){
					$('#extendValidity').removeClass('disabled');
				}
	    	  }else{
				$('#deleteEwaybillInvoices').addClass('disabled');
				$('.select_msg').text('');
				$('#cancelAllEwaybills,#rejectAllEwaybills').addClass('disabled');
				if($("span").hasClass("validateEbill")){
					$('#extendValidity').addClass('disabled');
				}
	    	  }
	      }
	      $('input[type="checkbox"]', rows).prop('checked', check);
}

function invoiceviewByfp(id,varRetType, varRetTypeCode, booksOrReturns,pendinginv,tallyHsnSummary){
	var invviewtype = $('.invoiceview1').val();
	//$('#includeoldinv').prop("checked",false);
	var fpinvDateInvcheck = "false";
	if(invviewtype == 'Return Period'){
		fpinvDateInvcheck = "true";
	}else{
		fpinvDateInvcheck = "false";
	}
	$.ajax({
		url: contextPath+"/mdfyclntfpinvdateInvoicesView?clientid="+clientId+"&fpinvDateInv="+fpinvDateInvcheck,
		dataType: 'json',
		type: 'POST',
		cache: false,
		success : function(summary) {
		}
	});
	clearInvFiltersss(varRetType,booksOrReturns);
	var pUrl = invTableUrl[varRetType];
	invTable[varRetType].ajax.url(pUrl).load();
	//loadInvTable(id, clientId, varRetType,varRetTypeCode, month, year, varUserType, globaluser,booksOrReturns,pendinginv,tallyHsnSummary);
}

function invoiceviewByTrDate(invviewtype,id,varRetType, varRetTypeCode, booksOrReturns) {
	//var invviewtype = $('.invoiceview').val();
	var retType = $('#retType').val();
	
	var fpinvDateInvcheck = "false";
	if(invviewtype == 'Invoice Date'){
		fpinvDateInvcheck = "false";
	}else{
		fpinvDateInvcheck = "true";
	}
	$.ajax({
		url: contextPath+"/mdfyclntBillDateInvoicesView?clientid="+clientId+"&billDateInv="+fpinvDateInvcheck,
		dataType: 'json',
		type: 'POST',
		cache: false,
		success : function(summary) {otherconfigdetails = summary;}
	});
	clearInvFiltersss(varRetType,booksOrReturns);
	if(booksOrReturns != ""){
		var pUrl = invTableUrl[varRetType];
		invTable[varRetType].ajax.url(pUrl).load();
	}else{
		var pUrl = invTableUrl[varRetTypeCode];
		invTable[varRetTypeCode].ajax.url(pUrl).load();
	}
	//loadInvTable(id, clientId, varRetType,varRetTypeCode, month, year, varUserType, globaluser,booksOrReturns,"","");
}

function downloadFromGSTN(type){
	$('.permissionGSTN_Actions-Download_GSTR2A').addClass("btn-loader");
	 $('.permissionGSTN_Actions-Download_Purchases').addClass('btn-loader');
	 $('.permissionGSTN_Actions-Download_ANX2').addClass("btn-loader");
	 $('.permissionGSTN_Actions-Download_Sales').addClass('btn-loader'); 
	 $('.permissionGSTN_Actions-Download_GSTR2A').html("Downloading...");
	 $('.permissionGSTN_Actions-Download_Purchases').html("Downloading...");
	 $('.permissionGSTN_Actions-Download_Sales').html("Downloading...");
	 $('.permissionGSTN_Actions-Download_ANX2').html("Downloading...");
	$.ajax({
		url : _getContextPath()+'/subscriptionCheck/'+clientId+'/'+userId,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response.status_cd == '0') {
				$('.permissionGSTN_Actions-Download_GSTR2A').removeClass("btn-loader");
				 $('.permissionGSTN_Actions-Download_Purchases').removeClass('btn-loader');
				 $('.permissionGSTN_Actions-Download_Sales').removeClass('btn-loader');
				 $('.permissionGSTN_Actions-Download_ANX2').removeClass('btn-loader');
				 $('.permissionGSTN_Actions-Download_GSTR2A').html("Download From GSTIN");
				 $('.permissionGSTN_Actions-Download_Purchases').html("Download From GSTIN");
				 $('.permissionGSTN_Actions-Download_Sales').html("Download From GSTIN");
				 $('.permissionGSTN_Actions-Download_ANX2').html("Download From GSTIN");
				if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
					if(varUserType == 'suvidha' || varUserType == 'enterprise'){
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
					}else{
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+_getContextPath()+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
					}
				}else {
					errorNotification(response.status_desc);
				}
			}else{
				if(response.status_desc == "OTP_VERIFIED"){
					if(type == 'GSTR2A' || type == 'ANX2'){
						window.location.href = _getContextPath()+'/dwnldinv'+commonSuffix+'/'+clientId+'/'+type+'/'+month+'/'+year;
					}else if(type == 'ANX1'){
						window.location.href = _getContextPath()+'/dwnldAnx1inv'+commonSuffix+'/'+clientId+'/'+type+'/'+month+'/'+year;
					}else{
						window.location.href = _getContextPath()+'/syncinv'+commonSuffix+'/'+clientId+'/'+type+'/'+month+'/'+year;
					}
				}else{
					$('.permissionGSTN_Actions-Download_GSTR2A').removeClass("btn-loader");
					 $('.permissionGSTN_Actions-Download_Purchases').removeClass('btn-loader');
					 $('.permissionGSTN_Actions-Download_Sales').removeClass('btn-loader');
					 $('.permissionGSTN_Actions-Download_ANX2').removeClass('btn-loader');
					 $('.permissionGSTN_Actions-Download_GSTR2A').html("Download From GSTIN");
					 $('.permissionGSTN_Actions-Download_Purchases').html("Download From GSTIN");
					 $('.permissionGSTN_Actions-Download_Sales').html("Download From GSTIN");
					 $('.permissionGSTN_Actions-Download_ANX2').html("Download From GSTIN");
					errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
				}
			}
		}
	});
}
function reconcileGstr2a(){
	$('#reconcileTab').click();
	//$('.reconcile_btn').click();
	//reconcileMonthlyGstr2a();
	/*$('.reconcile_btn').addClass("btn-loader");
	$('.reconcile_btn').html("Reconciling...");
	$.ajax({
		url : _getContextPath()+'/subscriptionCheck/'+clientId+'/'+userId,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response.status_cd == '0') {
				$('.reconcile_btn').removeClass("btn-loader");
				 $('.reconcile_btn').html("Reconcile");
				if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
					if(varUserType == 'suvidha' || varUserType == 'enterprise'){
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
					}else{
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+contextPath+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
					}
				}else {
					errorNotification(response.status_desc);
				}
			}else{
					window.location.href = _getContextPath()+'/reconcileinv'+commonSuffix+'/'+clientId+'/GSTR2A/'+month+'/'+year;
			}
		}
	});	*/
}

function includeOldInvoices(elem,id,varRetType, varRetTypeCode, booksOrReturns,pendinginv,tallyhsnsummary) {
	var title = "On selection of this check box, All Pending / Not Filed invoices of previous months till now will show in the below list to file in the current month";
	var previousMonthsPendingInv = "presentMonthInv";
	if(elem.checked) {
		previousMonthsPendingInv = "previousMonthsPendingInv";
		title = "Show Present Month Invoices";
		$('#includeoldinv').attr("checked");
	}else{
		$('#includeoldinv').removeAttr("checked");
		previousMonthsPendingInv = "presentMonthInv";
		title = "On selection of this check box, All Pending / Not Filed invoices of previous months till now will show in the below list to file in the current month";
	}
	$('#include_old_invoices').attr('data-original-title',title);
	$.ajax({
		url: contextPath+"/mdfyclntPendingInvoicesView?clientid="+clientId+"&prevPendingInv="+previousMonthsPendingInv,
		dataType: 'json',
		type: 'POST',
		cache: false,
		success : function(summary) {}
	});
	pendinginv = previousMonthsPendingInv;
	clearInvFiltersss(varRetType,booksOrReturns);
	var pUrl = invTableUrl[varRetType];
	invTable[varRetType].ajax.url(pUrl).load();
	//loadInvTable(id, clientId, varRetType,varRetTypeCode, month, year, varUserType, globaluser,booksOrReturns,pendinginv,tallyhsnsummary);
}
function gstr3bmonthChange(id){
	var claimdate = $('#'+id).val().split("-");
	$('.'+id).html(mnths[parseInt(claimdate[1])-1]);
}
function excelreport(){
	$('a.btn.btn-blue.excel').addClass('btn-loader');
		window.setTimeout(function(){
		$('a.btn.btn-blue.excel').removeClass("btn-loader");
		}, 2000);
}
function itcclaim(){
	var p = $('.itc_amount').val();
	if(p<=100){
	}else{
		$('.itc_amount').val('100');
	}		
}

function subscriptioncheck(returntype){
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
			}else{
				if(response.status_desc == "OTP_VERIFIED"){
				}else{
					if(returntype != "EWAYBILL"){
						errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
					}
				}
			}
		}
	});
}
function updateInvoiceStatus(){
	$.ajax({
		url : _getContextPath()+'/otpexpiry/'+clientId,
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response) {
			if(response == "OTP_VERIFIED"){
				window.location.href = _getContextPath()+'/updateInvStatus'+commonSuffix+'/'+clientId+'/GSTR1/'+month+'/'+year+'?cstatus='+clitfilingStatus;
			}else{
				errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
			}
		}
	});
}

function performEbillinvImport(btn) {
	var templateType = $('#ebilltemplateType').val();
	var filename =  $('#ebillmessages').html();
	if(templateType != ''){
	$('#ebilltemplateType_Msg').html('');
	if(filename != ''){
		$(btn).addClass('btn-loader');		
		$('#ebillimportModalForm').ajaxSubmit( {
			url: $("#ebillimportModalForm").attr("action"),
			dataType: 'json',
			type: 'POST',
			cache: false,
			success: function(response) {
				$('#ebillmsgImportProgress').show();$(btn).removeClass('btn-loader');
				if(response && response.summaryList && response.summaryList.length > 0) {
					response.summaryList.forEach(function(inv) {$('#ebillimportSummaryBody').append("<tr><td>"+inv.name+"</td><td class='blocktxt'>"+inv.total+"</td><td class='blocktxt'>"+inv.totalinvs+"</td><td class='greentxt'>"+inv.success+"</td><td class='greentxt'>"+inv.invsuccess+"</td><td class='redtxt'>"+inv.failed+"</td><td class='redtxt'>"+inv.invfailed+"</td></tr>");});
					$('#ebillidImportSummary').show();$('#ebillidImportBody').hide();
					if(response.month) {month = response.month;	year = response.year;}
					if(response.showLink) {$('#ebillerrorXls').show();}
				} else if(response && response.error) {$('#ebillimportSummaryError').parent().show().html(response.error);}
			},
			error: function(e) {$('#ebillmsgImportProgress').hide();	$(btn).removeClass('btn-loader');$('#ebillimportSummaryError').parent().show().html("Something went wrong, Please try again");}
		});
	}else{$('#ebillimportSummaryError').parent().show().html("Please Select File");}
	}else{$('#ebilltemplateType_Msg').html('Please Select Template Type');}
}
function chooseebillfileSheets(){
	$('#ebillfileselect1')[0].click();
}
$('#ebillfileselect1').change(function(e){
	var fileName = e.target.files[0].name;
	$('#ebillmessages').html(fileName);
	$('#ebillimporterr').css('display','none');
});
function updateEbillSheets() {
	$('#ebillidSheet').show();$('.ebillsheets').hide();var invType = $('#ebilltemplateType').val();if(invType != 'tally'){invType = 'mastergst';}$('#'+invType+'Sheet').show();
}


$(document).on('mouseover','#tot_tax', function (event) {
	$(this).find('#tax_tot_drop1').css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"10%","margin-top":"5px"});
}).on('mouseleave','#tot_tax',  function(){
    	$(this).next().css("display","none");
    	$(this).find("#tax_tot_drop1").stop(true, true).delay(100).fadeOut(300);
}); 

function itcClaimedData(retType, invoiceid){
	var retTypeCode = retType.replace(" ", "_");
	$('#itcLoader').removeClass('d-none');
	$.ajax({
		url : _getContextPath()+'/itcClaimedData/'+retType+"/"+invoiceid,
		async: true,
		cache: false,
		contentType: 'application/json',
		success : function(responseinv) {
			$('#itcLoader').addClass('d-none');
			var itcclmddate = responseinv.dateofitcClaimed.split("-");
			var tddate = itcclmddate[0] + '-' + itcclmddate[1] + '-' + itcclmddate[2];
			$('#'+invoiceid+retTypeCode+"itc_droptype").val(responseinv.elg);
			$('#'+invoiceid+retTypeCode+"itc_claimeddate").val(tddate);
			$('#'+invoiceid+retTypeCode+"itc_dropamt").val(responseinv.elgpercent);
		},error: function(error) {
			$('#itcLoader').addClass('d-none');
		}
	});
}

	function okDate() {
		var sdate = $('#start-date').val();
		var edate = $('#end-date').val();
		if(sdate == ""){
			$('.sdate_msg').text("Please select start date");
			$('.edate_msg').text("");
		}
		if(edate == ""){
			$('.edate_msg').text("Please select end date");
			$('.sdate_msg').text("");
		}
		if(sdate == "" && edate == ""){
			$('.sdate_msg').text("Please select start date");
			$('.edate_msg').text("Please select end date");
		}
		if(sdate != "" && edate != ""){
			$('.sdate_msg,.edate_msg').text("");
			$('.invDate_dropdown').hide();
			applyInvFilters('GSTR2A');
		}
	}
	function cancelDate() {
		$('.invDate_dropdown').hide();
		$('#start-date').val("");
		$('#end-date').val("");
		$('.sdate_msg,.edate_msg').text("");
		var totalCount=$("#divFiltersGSTR2A > span").length;
		if(totalCount == 1){
			$(".filter").hide();
		}else{
			$('.btaginput_date').hide();
		}
		applyInvFilters('GSTR2A');
	}
	
	function clearITCValues(){
		$.ajax({
			url : contextPath+'/unclaimAllupdt/Purchase Register',
			type: "POST",
			data: JSON.stringify(prchInvArray),
			contentType: 'application/json',
			success : function(response) {
				location.reload(true);
			}, 	error:function(data){
	    	}	
		});
	}
	
	
	function add_expencerow(no){
		var table_len = $('#expencetablebody'+no+' tr').length;
		 var len = $(".categorymodule").length;
		 var applen = no;//len+1;
		 
		if(table_len == '1'){
			$('.expense_delete'+no+'1').css("display","block");
		}
		var tablen = table_len;
		rowno = table_len+1;
		var table3=document.getElementById("expencetable"+no+"");
		$('#expencetablebody'+no+'').append('<tr id="expencerow'+(rowno)+'"><td class=""><input type="text" id="exp_item_name'+applen+''+rowno+'" class="form-control" name="expenses['+(applen-1)+'].expensesList['+(rowno-1)+'].itemName" placeholder="Expense Item Name"></td><td class=""><input type="text" id="exp_item_quantity'+applen+''+rowno+'" class="form-control expqtyVal text-right" name="expenses['+(applen-1)+'].expensesList['+(rowno-1)+'].quantity" onkeyup="findExpenseTaxable('+applen+','+rowno+')"></td><td class=""><input type="text" id="exp_item_rate'+applen+''+rowno+'" class="form-control exprateVal text-right" name="expenses['+(applen-1)+'].expensesList['+(rowno-1)+'].rate" placeholder="Rate" onkeyup="findExpenseTaxable('+applen+','+rowno+')"></td><td class="tablegreybg"><input type="text" id="exp_item_total'+applen+''+rowno+'" class="form-control text-right" name="expenses['+(applen-1)+'].expensesList['+(rowno-1)+'].total" readonly></td><td align="center"><a href="javascript:void(0)" class="expense_delete expense_delete'+applen+''+rowno+'" onclick="deleteexpence_row('+applen+','+rowno+')"> <span class="fa fa-trash-o gstr2adeletefield"></span></a></td></tr>');
		$('#exp_item_quantity'+applen+rowno).val('1');
	}
	
	function deleteexpence_row(no,sno){
		var no = no.toString().slice(0,1);
		var table_len = $('#expencetablebody'+no+' tr').length;
		var tab=document.getElementById("expencetable"+no+"");
		tab.deleteRow(sno);
		rowno--;
		$("#expencetablebody"+no+" tr").each(function(index) {
			 $(this).attr('id',index+1);
			 var rowno = (index+1).toString();
			 var rownoo = (index).toString();
			 $(this).find('input').each (function() {
					var inputname1 = $(this).attr('class');
					var inputid1 = $(this).attr('id');
					var inputname = $(this).attr('name');
					var abcd = $(this).attr('onkeyup');
					var change = $(this).attr('onchange');
					if(inputid1 != undefined){
						if(rowno<10){
							inputid1 = inputid1.slice(0, -1);
			   	    		}else{
			   	    			inputid1 = inputid1.slice(0, -2);
			   	    		}
						inputid1 = inputid1+rowno;
						$(this).attr('id',inputid1);
					}
					if(abcd != undefined){
						abcd = replaceAt(abcd,21,rowno);
				   	    $(this).attr('onkeyup',abcd);
					}
					if(inputname != undefined){
						if(inputname.indexOf("expensesList[") >= 0) {
							if(rownoo == '9'){
								inputname = inputname.replace('10',' ');
							}
							inputname = replaceAt(inputname,25,rownoo);
							$(this).attr('name',inputname);
							
						}
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
					if(det != 'expense_delete'){
					}else{
						var abcd = $(this).attr('onclick');
				   	    abcd = replaceAt(abcd,20,rowno);
				   	    $(this).attr('onclick',abcd);
					}
				});
			
		});
		var dtable_len = $('#expencetablebody'+no+' tr').length;
		if(dtable_len == '1'){
			$('.expense_delete'+no+'1').css("display","none");
		}
		
	}
	function addExpenses(){
		$('.expenseCategory').focus();
		$('#addExpenseModal').modal('show');
		 $('#expense_id').val("").removeAttr("name");
		 $('#exp_item_quantity11').val('1');
		var options = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/getExpenditureList/"+clientId+"?query="+ phrase + "&format=json";
				},
				getValue: "groupname",
				
				list: {
					match: {
						enabled: true
					},
					onLoadEvent: function() {
						if($("#eac-container-expenseCategory1 ul").children().length == 0) {
							$("#remainexempt_categoryempty1").show();
						} else {
							$("#remainexempt_categoryempty1").hide();
						}
					},
					maxNumberOfElements: 10
				},
			};
		$('#expenseCategory1').easyAutocomplete(options);
	}
	function updateGroups(no){
		$('#groupModal').modal('show');
		$('#expgname').val($('#expenseCategory'+no).val());
	}
	function groupValidation(){
		var groupName = $('#expgname').val();
		var grpHeadName = $('#expheadname').val();
		var c=0;
		if(groupName ==""){
			$('.with-errors').html('');
			$('#expgroupName_Msg').text("Please Enter Group Name"); 
			c++;
		}else{
			$('#expgroupName_Msg').text(""); 
		}
		if(grpHeadName =="" || grpHeadName == null){
			$('.with-errors').html('');
			$('#expheadname_Msg').text("Please Enter Head name"); 
			c++;
		}else{
			$('#expheadname_Msg').text(""); 
		}
		return c==0 ;
	}
	function groupAdd(type){
		var groupName = $('#expgname').val();
		var grpHeadName = $('#expheadname').val();
		var groupObject = new Object();
		groupObject.groupname=groupName;
		groupObject.headname=grpHeadName;
		if(groupValidation){
			$.ajax({
				type: "POST",
				url: contextPath+"/cp_createGroup"+paymenturlSuffix+"/"+Paymenturlprefix,
				contentType : "application/json",
				data: JSON.stringify(groupObject),
				success: function(data) {
					$('#groupModal').modal('hide');
				},error: function(data) {}
			});
		}
	}
	$('#expgname').change(function() {
		var groupname=$('#expgname').val();
		$.ajax({
			type : "GET",
			async: false,
			contentType : "application/json",
			url: contextPath+"/groupnameexits/"+clientId+"/"+groupname,
			success : function(response) {
				if(response == 'success'){
					$('.expgmsg').text('Group Name Already Exists').show();
					$('#expbmsg').addClass('has-error has-danger');
					err=1;
				}else{
					err=0;
					$('.expgmsg').text('').hide();
					$('#expbmsg').removeClass('has-error has-danger');
				}
			}
		});
	});
	function findExpenseTaxable(no,sno){
		var q = document.getElementById('exp_item_quantity'+no+''+sno);
		var r = document.getElementById('exp_item_rate'+no+''+sno);
		var tx = document.getElementById('exp_item_total'+no+''+sno);
		$('#exp_item_total'+no+''+sno).val(0);
		if(q && r){
			if(q.value && r.value && tx) {
				var val = parseFloat(q.value)*parseFloat(r.value);
				$('#exp_item_total'+no+''+sno).val(val);
			}else{
				$('#exp_item_total'+no+''+sno).val(0);
			}
		}
		findGrandTotal(no,sno);
	}
	
	function findGrandTotal(no,sno){
		var table=document.getElementById('expencetable'+no);
		var iRows=rowno+1;
		var total = 0;
		for(var i=1;i<iRows;i++) {
			if(i == no) {
				var totalVal;
				totalVal = document.getElementById('exp_item_total'+no+''+i);
				if(totalVal && totalVal.value) {
					total+=parseFloat(totalVal.value);
				}
			}else {
				var totalVal;
				totalVal = document.getElementById('exp_item_total'+no+''+i);
				if(totalVal && totalVal.value) {
					total+=parseFloat(totalVal.value);
				}
			}
		}
		$('#expenseTot'+no).html(parseFloat(total));
		var expensestotal = 0;
		var len = $(".categorymodule").length;
		for(var i=1;i<=len;i++) {
			if($('#expenseTot'+i).html() != ""){
				expensestotal += parseInt($('#expenseTot'+i).html().replace(/,/g , ''));
			}
		}
		
		$('#idExpenseTotal').html(parseFloat(expensestotal));
		
		$(".indformat").each(function(){
		    $(this).html($(this).html().replace(/,/g , ''));
		});
		OSREC.CurrencyFormatter.formatAll({
			selector: '.indformat'
		});
	}
	function labelexpencesubmit(){
		$.ajax({
			type: "POST",
			url: $("#addExpenceModalForm").attr('action'),
			data: $("#addExpenceModalForm").serialize(),
			success: function(data) {
				location.reload(true);
			}
		});
	}
	function loadExpenseTable(id, clientId, varRetType,varRetTypeCode, month, year, userType, fullname,booksOrReturns){
		var pUrl = _getContextPath()+'/getExpenses/'+id+'/'+clientId+'/EXPENSES/'+month+'/'+year+'?booksOrReturns='+booksOrReturns+'&reportType=notreports';
		var urlSuffix = id+'/'+fullname+'/'+userType+'/'+clientId+'/'+varRetType;
		invTableUrl['EXPENSES'] = pUrl;
		if(invTable['EXPENSES']){
			invTable['EXPENSES'].clear();
			invTable['EXPENSES'].destroy();
		}
		invTable[varRetTypeCode] = $('#dbTableEXPENSES').DataTable({
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
		        	 resp.recordsTotal = resp.expenses.totalElements;
		        	 resp.recordsFiltered = resp.expenses.totalElements;
		        	 loadExpenseTotals('EXPENSES', resp.expensesAmount);
		        	 $('.updateExpensefilter_summary').show();
		        	 $('.updatefilter_summary').hide();
		        	 return resp.expenses.content ;
		         }
		     },
			"paging": true,
			'pageLength':10,
			"responsive": true,
			"searching": true,
			"order": [[3,'desc']],
			'columns': getExpenseColumns(id, clientId, 'EXPENSES', userType, month, year, booksOrReturns,urlSuffix),
			'columnDefs' : getInvColumnDefs('EXPENSES')
		});
		$('#invBody'+varRetTypeCode).on('click','tr', function(e){
			if (!$(e.target).closest('.nottoedit').length) {
				var dat = invTable['EXPENSES'].row($(this)).data();
				editExpensePopup('EXPENSES', dat.userid);
			}
		});
	}
	function getExpenseColumns(id, clientId, varRetType, userType, month, year, booksOrReturns,urlSuffix){
		var chkBx = {data: function ( data, type, row ) {
	 			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="expFilter'+varRetType+data.userid+'" onClick="updateExpSelection(\''+data.userid+'\', \''+varRetType+'\',this)"/><i class="helper"></i></label></div>';
		}};
		var category = {data:  function ( data, type, row ) {
			var category = ''; 
			for(var i=0;i<data.expenses.length;i++){
				var exp_category = data.expenses[i].category ? data.expenses[i].category : "";
				category +=  '<span class="text-left invoiceclk">'+exp_category+'</span></br>';
			}
			return category;
		}};
		
		var paymentMode = {data:  function ( data, type, row ) {
			var mode = data.paymentMode ? data.paymentMode : "";
			return '<span class="text-left invoiceclk">'+mode+'</span>';
		}};
		
		var paymentDate = {data:  function ( data, type, row ) {
			var date = data.paymentDate ? data.paymentDate : "";
			if(date != ""){
				return '<span class="text-left invoiceclk">'+(new Date(date)).toLocaleDateString('en-GB')+'</span>';
			}else{
				return '<span class="text-left invoiceclk"></span>';
			}
			}};
		var total = {data:  function ( data, type, row ) {
			var total = '';
			for(var i=0;i<data.expenses.length;i++){
				var totalamount = data.expenses[i].totalAmt ? data.expenses[i].totalAmt : 0.0;
				total += '<span class="text-left invoiceclk">'+formatNumber(totalamount.toFixed(2))+'</span></br>';
			}
			return total;
			}};
		return [chkBx,category , paymentMode,paymentDate, total,
	        {data: function ( data, type, row ) {
	     		 return '<a class="btn-edt" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a>';
	            }} 
	        ];
	}
	function loadExpenseTotals(varRetType, totalsData){
		$('#idExpCountEXPENSES').html(totalsData ? totalsData.totalTransactions : '0');
		$('#idExpTotAmtValEXPENSES').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
		$('#idTabTotalEXPENSES').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	}
	
	function initiateExpensesCallBacksForMultiSelect(varRetType, varRetTypeCode, booksOrReturns){
		var multiSelDefaultVals = ['','', '- Payment Mode -'];
		for(i=1;i<=2;i++){
			if(i == 1){
				$('#expMultiselectEXPENSES'+i).hide();
				continue;
			}
			multiselExpenserefresh('#expMultiselectEXPENSES'+i, multiSelDefaultVals[i], varRetType);
		}
	}
	function multiselExpenserefresh(idval, descVal, varRetType){
		$(idval).multiselect({
			nonSelectedText: descVal,
			includeSelectAllOption: true,
			onChange: function(){
				applyExpFilters(varRetType);
			},
			onSelectAll: function(){
				applyExpFilters(varRetType);
			},
			onDeselectAll: function(){
				applyExpFilters(varRetType);
			}
		});
	}
	
	function applyExpFilters(retTypeCode){
		var categoryOptions = $('#expMultiselect'+retTypeCode+'1 option:selected');
		var pModeOptions = $('#expMultiselect'+retTypeCode+'2 option:selected');
		var pDateOptions = $('#expMultiselect'+retTypeCode+'3 option:selected');
		var sDate = $('#expstartdate').val();
		var eDate = $('#expenddate').val();
		if(categoryOptions.length > 0 || pModeOptions.length > 0 || pDateOptions.length > 0){
			$('.normaltable .efilter').css("display","block");
		}else{
			if(sDate != "" && eDate != ""){
				$('.normaltable .efilter').css("display","block");
			}else{
				$('.normaltable .efilter').css("display","none");
			}
		}
		var filterContent="";
		var cArray = new Array();
		var pmArray = new Array();
		var pdArray = new Array();
		if(categoryOptions.length > 0) {
			for(var i=0;i<categoryOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+categoryOptions[i].value+'<span data-val="'+categoryOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				cArray.push(categoryOptions[i].value);
			}
		}
		if(pModeOptions.length > 0) {
			for(var i=0;i<pModeOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+pModeOptions[i].text+'<span data-val="'+pModeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				pmArray.push(pModeOptions[i].value);
			}
		}
			if(sDate != "" && eDate != ""){
				filterContent += '<span data-role="tagsinput" placeholder="" class="btaginput btaginput_edate">Date : '+sDate+' To '+eDate+'<span data-val="Date : '+sDate+' Between '+eDate+'" onclick="clearExp()" class="expensesPmntDate expFilterDate" data-role="remove"></span></span>';
			}
		$('#divExpFilters'+retTypeCode).html(filterContent);
		reloadExpTable(retTypeCode, cArray, pmArray, pdArray, sDate, eDate, undefined);
		reloadExpenseExcelUrls(retTypeCode, cArray, pmArray, pdArray, sDate, eDate, undefined);
	}
	function loadExpensesSupport(clientId, varRetType, varRetTypeCode, month, year, callback){
		var urlStr = _getContextPath()+'/getExpenses_category/'+clientId+'/'+varRetType+'/'+month+'/'+year;
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
	function loadCategoryInDropdown(response, varRetType, varRetTypeCode){
		var categoryMultiSelObj = $('#expMultiselect'+varRetTypeCode+'1');
		if (response.category.length > 0) {				
			response.category.forEach(function(response) {
				if(response != ''){
					categoryMultiSelObj.append($("<option></option>").attr("value",response).text(response));
				} 
			});
		}
		multiselExpenserefresh('#expMultiselect'+varRetTypeCode+'1', '- Category -', varRetType);
		$('#expMultiselect'+varRetTypeCode+'1').multiselect('rebuild');
	}
	function reloadExpTable(retTypeCode, cArray, pmArray, pdArray, sDate, eDate, booksOrReturns){
		var pUrl = invTableUrl[retTypeCode];
		var appd = 'booksorReturns='+booksOrReturns;
		if(cArray.length > 0){
			appd+='&category='+cArray.join(',');
		}
		if(pmArray.length > 0){
			appd+='&paymentMode='+pmArray.join(',');
		}
		if(sDate != ""){
			appd+='&fromtime='+sDate;
		}
		if(eDate != ""){
			appd+='&totime='+eDate;
		}
		pUrl += '&'+appd;
		invTable[retTypeCode].ajax.url(pUrl).load();
	}
	function reloadExpenseExcelUrls(retTypeCode, cArray, pmArray, pdArray, sDate, eDate, booksOrReturns){
		var pUrl = invTableUrl[retTypeCode];
		var appd = '?booksorReturns='+booksOrReturns;
		if(cArray.length > 0){
			appd+='&category='+cArray.join(',');
		}
		if(pmArray.length > 0){
			appd+='&paymentMode='+pmArray.join(',');
		}
		if(sDate != ""){
			appd+='&fromtime='+sDate;
		}
		if(eDate != ""){
			appd+='&totime='+eDate;
		}
		pUrl += '&'+appd;
		var invoiceUrl = invTableDwnld_invoiceUrl[retTypeCode]+appd;
		var itemUrl = invTableDwnld_itemUrl[retTypeCode]+appd;
		$('#expitemWiseUrl'+retTypeCode).attr('href',itemUrl);
		$('#expWiseUrl'+retTypeCode).attr('href',invoiceUrl);
	}
	function clearExpFilters(retType,booksOrReturns){
		for(i=1;i<=2;i++){
				/*if(i == 1){
					$('#expMultiselect'+retType+i).hide();
					continue;
				}*/
				$('#expMultiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
			}
		
		$('#divExpFilters'+retType.replace(" ", "_")).html('');
		$('.normaltable .efilter').css("display","none");
		$('.expFilterDate').css("display","none");$('.pmntDateDropdown').hide();
		$('#expstartdate').val("");$('.sdate_msg,.edate_msg').text("");
		$('#expenddate').val("");
		reloadExpTable(retType, new Array(), new Array(), new Array(),'','','');
		reloadExpenseExcelUrls(retType, new Array(), new Array(), new Array(),'','','');
	}
	function editExpensePopup(retType, expenseId){
		$('#addExpenseModal').modal('show');
		 getExpenseData(expenseId, retType, function(expense) {
		        //if (expense.expenseId == expenseId) {
			 		updateExpenseData(expense);
		        	$('#expense_pmntMode').val(expense.paymentMode);
		        	changePaymentMode();
		        	$('#expense_pmntDate').val(expense.paymentDate);
		        	if(expense.ledgerName != null && expense.ledgerName != ""){
		        		$('#expense_ledgerName').val(expense.ledgerName);
		        	}else{
		        		$('#expense_ledgerName').val("");
		        	}
		        	$('#expense_branch').val(expense.branchName);
		        	$('#exp_comments').val(expense.comments);
		        	$('#expense_id').val(expense.userid);
		        	$("form[name='addExpenceModalForm']").append('<input type="hidden" name="id" value="'+expense.userid+'">');
		        	rowno = 1;
		        		 for(var i = 0; i < expense.expenses.length; i++){
		                		 if (rowno < expense.expenses[i].expensesList.length) {
			                		 for (var j = 1; j < expense.expenses[i].expensesList.length; j++) {
			                			 if (expense.expenses[i].expensesList[j]) {
			                				 add_expencerow(j);
			                			 }
			                		 }
		                		 }
		                	 }
		                //}
		        			 var options = {
		        						url: function(phrase) {
		        							phrase = phrase.replace('(',"\\(");
		        							phrase = phrase.replace(')',"\\)");
		        							return contextPath+"/getExpenditureList/"+expense.clientid+"?query="+ phrase + "&format=json";
		        						},
		        						getValue: "groupname",
		        						
		        						list: {
		        							match: {
		        								enabled: true
		        							},
		        							onLoadEvent: function() {
		        								if($("#eac-container-expenseCategory1 ul").children().length == 0) {
		        									$("#remainexempt_categoryempty1").show();
		        								} else {
		        									$("#remainexempt_categoryempty1").hide();
		        								}
		        							},
		        							maxNumberOfElements: 10
		        						},
		        					};
		        				$('#expenseCategory1').easyAutocomplete(options);
		        				categoryNo = 1;
		        		 if (categoryNo < expense.expenses.length) {
		        			 for(var i = 0; i < expense.expenses.length-1; i++){
		        				 if (expense.expenses[i]) {
		        					 updatecategory();
	                			 }
		        			 }
		        		 }
		                for(var i = 0; i < expense.expenses.length; i++){
		                	var no = i +1;
		                	$('#expenseCategory'+no).val(expense.expenses[i].category);
		                	$('#expenseTot'+no).html(expense.expenses[i].totalAmt ? formatNumber(expense.expenses[i].totalAmt.toFixed(2)) : "0.00");
		                	for (var j = 1; j <= expense.expenses[i].expensesList.length; j++) {
		                		$('#exp_item_name' +no + j).val(expense.expenses[i].expensesList[j - 1].itemName ? expense.expenses[i].expensesList[j - 1].itemName : "");
		                		$('#exp_item_quantity' +no + j).val(expense.expenses[i].expensesList[j - 1].quantity ? expense.expenses[i].expensesList[j - 1].quantity : "0.00");
		                		$('#exp_item_rate' +no + j).val(expense.expenses[i].expensesList[j - 1].rate ? expense.expenses[i].expensesList[j - 1].rate : "0.00");
		                		$('#exp_item_total' +no + j).val(expense.expenses[i].expensesList[j - 1].total ? expense.expenses[i].expensesList[j - 1].total : "0.00");
		                	}
		                }
		                $("#idExpenseTotal").html(expense.totalAmount ? formatNumber(expense.totalAmount.toFixed(2)) : "0.00");
		        //}
		 });
	}
	function getExpenseData(expId, returnType, popudateExpData){
		var urlStr = _getContextPath()+'/getExpense/'+expId+'/'+returnType;
		$.ajax({
			url: urlStr,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				popudateExpData(response);
			}
		});
	}
	function okExpDate() {
		var date1 = $('.expPmntd1').val();
		var date2 = $('.expPmntd2').val();
		if(date1 == ""){
			$('.sdate_msg').text("Please select start date");
			$('.edate_msg').text("");
		}
		if(date2 == ""){
			$('.edate_msg').text("Please select end date");
			$('.sdate_msg').text("");
		}
		if(date1 == "" && date2 == ""){
			$('.sdate_msg').text("Please select start date");
			$('.edate_msg').text("Please select end date");
		}
		if(date1 != "" && date2 != ""){
			$('.sdate_msg,.edate_msg').text("");
			$('.pmntDateDropdown').hide();
			applyExpFilters('EXPENSES');
		}
	}
	function cancelExpDate() {
		$('.pmntDateDropdown').hide();
		$('.expstartdate').val("");
		$('.expenddate').val("");
		$('.sdate_msg,.edate_msg').text("");
		var totalCount=$("#divFiltersEXPENSES > span").length;
		if(totalCount == 1){
			$(".efilter").hide();
		}else{
			$('.btaginput_edate').hide();
		}
		applyExpFilters('EXPENSES');
	}
	function initializeExpensesRemoveAppliedFilters(varRetType, varRetTypeCode){
		$('#divExpFilters'+varRetTypeCode).on('click', '.deltag', function(e) {
			var val = $(this).data('val');
			for(i=1;i<=2;i++){
					//if(i == 1){
						$('#expMultiselect'+varRetTypeCode+i).multiselect('deselect', [val]);
						continue;
					//}
			}
			applyExpFilters('EXPENSES');
		});
	}
	function clearExp(){
		var totalCount=$("#divExpFiltersEXPENSES > span").length;
		if(totalCount == 1){
			$(".efilter").hide();
		}else{
			$('.btaginput_edate').hide();
		}
		$('.pmntDateDropdown').hide();
		$('.expstartdate').val("");
		$('.sdate_msg,.edate_msg').text("");
		$('.expenddate').val("");
		applyExpFilters('EXPENSES');
	}
	function clearExpFiltersss(retType,retTypeCode) {
		for(i=1;i<=2;i++){
			if(i == 1){
				$('#expMultiselect'+retType+i).hide();
				continue;
			}
			$('#expMultiselect'+retType+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		}
		$('#divExpFilters'+retType.replace(" ", "_")).html('');
		$('#expMultiselect'+retType.replace(" ", "_")).html('');
		$('.normaltable .efilter').css("display","none");
	}
	function updateExpSelection(id, retType, chkBox) {
		if(chkBox.checked){
			expensesArray.push(id);
		}else {
			var eArray=new Array();
			expensesArray.forEach(function(inv) {
				if(inv != id) {
					eArray.push(inv);
				}
			});
			expensesArray = eArray;
		}
		if(expensesArray.length > 0){
			$('#deleteExpensesInvoices').removeClass("disabled");
		}else{
			$('#deleteExpensesInvoices').addClass("disabled");
		}
	}
	function updateExpMainSelection(retType, chkBox){
		expensesArray = new Array();
		var check = $('#expCheckEXPENSES').prop("checked");
	      var rows = invTable['EXPENSES'].rows().nodes();
	      if(check) {
		      invTable['EXPENSES'].rows().every(function () {
		    	  	var row = this.data();
		    	  	expensesArray.push(row.userid);
		      });
		   }
	      if(expensesArray.length > 0){
				$('#deleteExpensesInvoices').removeClass("disabled");
			}else{
				$('#deleteExpensesInvoices').addClass("disabled");
			}
	      $('input[type="checkbox"]', rows).prop('checked', check);
	}
	function expensesDownloads(id, clientId, varRetType, otherreturnType, varRetTypeCode, month, year){
		var vaRtTyp=varRetType;
		var dwnldurl_expensewise = _getContextPath()+"/dwnldExpenseXls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/expensewise";
		var dwnldurl_itemwise = _getContextPath()+"/dwnldExpenseXls/"+id+"/"+clientId+"/"+varRetType+"/"+month+"/"+year+"/itemwise";
		invTableDwnld_invoiceUrl[varRetTypeCode] = dwnldurl_expensewise;
		invTableDwnld_itemUrl[varRetTypeCode] = dwnldurl_itemwise;
		$('#expitemWiseUrl'+varRetTypeCode).attr("href",dwnldurl_itemwise);
		$('#expWiseUrl'+varRetTypeCode).attr("href",dwnldurl_expensewise);
		rolesPermissions();
	}
	
	function closeExpenseModal(modalid){
		$('div.categorymodule').slice(1).remove();
		$('#expense_pmntMode,#expense_ledgerName,#expense_pmntDate,#expense_branch,.expenseCategory ').val('');
		$('#remainexempt_categoryempty1').css("display","none");
		$("#idExpenseTotal").html("0.00");
		$('#'+modalid).modal('hide');
		rowno=1;
		categoryNo = 1; 
	}
	
	function changePaymentMode(){
		var mode = $('#expense_pmntMode').val();
		var ledgerName="";
		if(mode != ""){
			$('.expense_pmntMode').val(mode);
			if(mode == "Cash"){
				ledgerName = "Cash in hand";
			}else if(mode == "Bank" || mode == "Cheque"){
				ledgerName = "Bank accounts";
			}
			var ledgers = {
					url: function(phrase) {
						return contextPath+"/getLedgersList/"+clientId+"/"+ledgerName+"?query="+ phrase + "&format=json";
					},
					getValue: "ledgerName",
					list: {
						onChooseEvent: function() {
						}
					}
			};
			$(".expense_ledgerName").easyAutocomplete(ledgers);
			$(".expense_ledgerName").val(mode);
		}
	}
	function updatecategory(){
		var len = $(".categorymodule").length;
		if(len >= 1){
			$('.expitem_delete1').css("display","inline-block");
		}
		var applen = len+1;
		$("#categorymodule"+len).after('<div class="row pl-3 pr-3 ml-1 mr-1 categorymodule" id="categorymodule'+applen+'"><div class="form-group col-md-12 col-sm-12"><span class="errormsg" id="itemNumber_Msg"></span><div class="row p-0"><p class="lable-txt col-md-3 p-0 mt-3">Expense Category</p><div class="col-md-6 pl-0 pr-0"><input type="text" class="expenseCategory'+applen+'" id="expenseCategory'+applen+'" name="expenses['+(applen-1)+'].category" required="required" data-error="Please enter more than 3 characters" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" placeholder="Category" /><div id="remainexempt_categoryempty'+applen+'" style="display:none"><div class="expenseGroupbox dbbox" style="width:100%;"><p>Please add new Category</p><input type="button" class="btn btn-sm btn-blue-dark" value="Add New Category" data-toggle="modal" onclick="updateGroups('+applen+')" style="width:45%;"></div></div><label for="input" class="control-label" ></label><div class="help-block with-errors"></div><i class="bar"></i> </div><div class="col-md-3 pl-3 pr-0" style="text-align: end;"><a href="javascript:void(0)" class="item_delete expitem_delete1" onclick="delete_category('+applen+')" style="vertical-align: bottom;line-height: 1px;"><span class="fa fa-trash-o gstr2adeletefield expensecategorydelete"></span></a></div></div></div><div class="col-md-12 mt-4 p-0"><table width="100%" id="expencetable'+applen+'" border="0" cellspacing="0" cellpadding="5" class="table-imports table table-sm table-bordered table-hover expencetable"><thead><tr><th class="text-center" width="50%">Expense Item Name</th><th class="text-center">Quantity</th><th class="text-center">Rate</th><th class="text-center">Total Amount</th><th></th></tr></thead><tbody id="expencetablebody'+applen+'"><tr id="expencerow'+applen+'"><td ><input type="text" class="form-control epxname'+applen+''+(applen-(applen-1))+'" id="exp_item_name'+applen+''+(applen-(applen-1))+'" placeholder="Expense Item Name" name="expenses['+(applen-1)+'].expensesList[0].itemName"></td><td ><input type="text" class="form-control text-right expqtyVal expqty'+applen+''+(applen-(applen-1))+'" id="exp_item_quantity'+applen+''+(applen-(applen-1))+'" name="expenses['+(applen-1)+'].expensesList[0].quantity" onkeyup="findExpenseTaxable('+applen+','+(applen-(applen-1))+')"></td><td ><input type="text" class="form-control  text-right exprateVal exprate'+applen+''+(applen-(applen-1))+'" placeholder="Rate" id="exp_item_rate'+applen+''+(applen-(applen-1))+'" name="expenses['+(applen-1)+'].expensesList[0].rate" onkeyup="findExpenseTaxable('+applen+','+(applen-(applen-1))+')"></td><td class="tablegreybg"><input type="text" class="form-control exptot'+applen+''+(applen-(applen-1))+' text-right indformat" id="exp_item_total'+applen+''+(applen-(applen-1))+'" name="expenses['+(applen-1)+'].expensesList[0].total" readonly></td><td align="center"><a href="javascript:void(0)" class="expense_delete expense_delete'+applen+''+(applen-(applen-1))+' expdel'+applen+''+(applen-(applen-1))+'" onclick="deleteexpence_row('+applen+','+(applen-1)+')" style="display:none;"> <span class="fa fa-trash-o gstr2adeletefield"></span></a></td></tr></tbody><tfoot><tr><td class="text-right tfootwitebg" colspan="3"><span class="add pull-left" id="addExpenseRow" onclick="add_expencerow('+applen+')"><i class="add-btn">+</i> Add another row</span>Total Amount</td><td class="text-right indformat" id="expenseTot'+applen+'" style="background-color: #fffbf5!important;">0.00</td><td class="tfootwitebg addbutton" style="display: table-cell;"> <span class="add add-btn" id="addrow" onclick="add_expencerow('+applen+')">+</span></td></tr></tfoot></table></div></div>')
		$('#exp_item_quantity'+applen+''+(applen-(applen-1))+'').val('1');
		expenseBranches();
		expenseCategories(applen);
	}
	function expenseCategories(no){
		var options = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/getExpenditureList/"+clientId+"?query="+ phrase + "&format=json";
				},
				getValue: "groupname",
				
				list: {
					match: {
						enabled: true
					},
					onLoadEvent: function() {
						if($("#eac-container-expenseCategory"+no+" ul").children().length == 0) {
							$("#remainexempt_categoryempty"+no).show();
						} else {
							$("#remainexempt_categoryempty"+no).hide();
						}
					},
					maxNumberOfElements: 10
				},
			};
		$('#expenseCategory'+no).easyAutocomplete(options);
	}
	var delno;
	function delete_category(delno){
		var myobj = document.getElementById("categorymodule"+delno);
		myobj.remove();
		$(".categorymodule").each(function(index) {
			var rowno = (index+1).toString();
			 var rownoo = (index).toString();
			var mainid = $(this).attr('id');
			if(mainid != undefined){
				if(rowno<10){
					mainid = mainid.slice(0, -1);
	   	    		}else{
	   	    			mainid = mainid.slice(0, -2);
	   	    		}
				mainid = mainid+rowno;
				$(this).attr('id',mainid);
			}
			
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
					inputid1 = inputid1+rownoo;
					$(this).attr('id',inputid1);
				}
				if(inputname1 != undefined){
					if(rowno<10){
						inputname1 = inputname1.slice(0, -1);
		   	    		}else{
		   	    			inputname1 = inputname1.slice(0, -2);
		   	    		}
					inputname1 = inputname1+rownoo;
					$(this).attr('class',inputname1);
				}
				if(inputname != undefined){
					if(inputname.indexOf("expenses[") >= 0) {
						if(rownoo == '9'){
							inputname = inputname.replace('10',' ');
						}
						inputname = replaceAt(inputname,9,rownoo);
						$(this).attr('name',inputname);
						
					}
				}
			});
			$(this).find('select').each (function() {
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
					if(inputname.indexOf("expenses[") >= 0) {
						if(rownoo == '9'){
							inputname = inputname.replace('10',' ');
						}
						inputname = replaceAt(inputname,9,rownoo);
						$(this).attr('name',inputname);
						
					}
				}
				var itcval = $(this).attr('class');
				if(itcval.indexOf("expense_pmntMode") >= 0) {
					var abcd = $(this).attr('onchange');
		   	    	abcd = replaceAt(abcd,18,rowno);
		   	    	$(this).attr('onchange',abcd);
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
				if(det != 'item_delete expitem_delete1'){
					var abcd = $(this).attr('onclick');
		   	    	abcd = replaceAt(abcd,9,rowno);
		   	    	$(this).attr('onclick',abcd);
				}else{
					var abcd = $(this).attr('onclick');
			   	    abcd = replaceAt(abcd,16,rowno);
			   	    $(this).attr('onclick',abcd);
				}
			});
			});
			var len = $(".categorymodule").length;
			if(len == '1'){
				$('.expitem_delete1').css("display","none");
				
			}
		}
	
	function extendValidity(){
		var invId = $('#invoiceid').val();
		$('#invoiceModal').css("z-index","1040");
		$('#extendEwayBillModal').modal('show');
		$('#btn_extend').attr('onclick', "extendEwayBillNo()");
		
	}
	function extendCodeSelection(){
		var extendremark = $('#extendreason').val();
		if(extendremark == "Natural Calamity"){
			$('#extendcode').val("1");
		}else if(extendremark == "Law and Order Situation"){
			$('#extendcode').val("2");
		}else if(extendremark == "Transshipment"){
			$('#extendcode').val("4");
		}else if(extendremark == "Accident"){
			$('#extendcode').val("5");
		}else if(extendremark == "Others"){
			$('#extendcode').val("99");
		}
	}

	function extendEwayBillNo(){
		var extendcode = $('#extendcode').val();
		var extendremark = $('#extendrmrks').val();
		var transitType = $('#transitType').val();
		var remainingDistance = $('#remainingDistance').val();
		if(accessDwnldewabillinv == "false"){
			$('#errorNotificationModal').modal("show");
			$('#invoiceModal').css("z-index","1033");
			$('.modal-backdrop.show').css("display","block");
		}else{
			$('#btn_extend').addClass("btn-loader");
			var extendewaybilldata=new Object();
			extendewaybilldata.extnRsnCode=extendcode;
			extendewaybilldata.extnRemarks=extendremark;
			extendewaybilldata.transitType= transitType;
			extendewaybilldata.remainingDistance = remainingDistance;
			extendewaybilldata.invoiceIds = ewaybillArray;
			if(extendcode != '' && extendremark != ''){
				$.ajax({
					url: contextPath+"/extendeBilllinv/"+urlSuffixs+"/"+Paymenturlprefix,
					type: "POST",
					data: JSON.stringify(extendewaybilldata),
					contentType: 'application/json',
					success : function(response) {
						var s1 = response;
						var s2 = s1.substr(1);
						if(response.length != 0){
							$('#extend_error').html(s2);
							$('#btn_extend').removeClass("btn-loader");
						}else{
							$('#extend_error').html("");
							location.reload(true);
							$('#btn_extend').removeClass("btn-loader");
						}
					}
				});
			}else{$('#btn_extend').removeClass("btn-loader");}
		}
		
	}
	function cancelWaybill(){
		$('#cancelEwayBillModal').modal('show');
		$('#btn_Cancel').attr('onclick', "cancelAllEwayBillInvoices()");
	}
	function cancelAllEwayBillInvoices(){
		var cancelcode = $('#cancelcode').val();
		var cancelremark = $('#cancelremark').val();
		if(accessDwnldewabillinv == "false"){
			$('#errorNotificationModal').modal("show");
			$('#invoiceModal').css("z-index","1033");
			$('.modal-backdrop.show').css("display","block");
		}else{
	    	$('#btn_Cancel').addClass("btn-loader");
			var cancelewaybilldata=new Object();
			cancelewaybilldata.cancelRsnCode=cancelcode;
			cancelewaybilldata.cancelRmrk=cancelremark;
			cancelewaybilldata.invoiceIds = ewaybillArray;
			if(cancelcode != '' && cancelremark != ''){
				$.ajax({
					url: contextPath+"/cancelAlleBilllinv/"+urlSuffixs+"/"+Paymenturlprefix,
					type: "POST",
					data: JSON.stringify(cancelewaybilldata),
					contentType: 'application/json',
					success : function(response) {
						var s1 = response;
						var s2 = s1.substr(1);
					if(response.length != 0){
						$('#cancel_error').html(s2);
						$('#btn_Cancel').removeClass("btn-loader");
					}else{
						$('#cancel_error').html("");
						location.reload(true);
						$('#btn_Cancel').removeClass("btn-loader");
					}
					}
				});
			}else{$('#btn_Cancel').removeClass("btn-loader");}
	   }
	}
	function rejectWaybill(){
		if(accessDwnldewabillinv == "false"){
			errorNotification('Your Access to the Eway Bill Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		}else{
			$('#rejectAllEwaybills').addClass("btn-loader");
			$.ajax({
				url: contextPath+"/rejectAlleBilllinv/"+urlSuffixs+"/"+Paymenturlprefix,
				type: "POST",
				data: JSON.stringify(ewaybillArray),
				contentType: 'application/json',
				success : function(response) {
					var s1 = response;
					var s2 = s1.substr(1);
				if(response.length != 0){
					errorNotification(s2);
					$('#rejectAllEwaybills').removeClass("btn-loader");
				}else{
					location.reload(true);
					$('#rejectAllEwaybills').removeClass("btn-loader");
				}
				},error:function(err){
					$('#rejectAllEwaybills').removeClass("btn-loader");
				}
			});
		}
	}