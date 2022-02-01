var invTableMismatchUrl;// = new Object();
var invSummaryTableMismatchUrl = new Object();
var invTableDwnld_invoiceUrl = new Object();
var invTableDwnld_itemUrl = new Object();
var invTableDwnld_fullUrl = new Object();
var invTableSummaryDownloadUrl = new Object();
var invTableMismatch;// = new Object();
var cpUsersResponse;
var isCpUsersResponseLoaded = false;
var mannualMatchtable;
var mannualViewMatchTable;
var mannualMatchTableUrl = new Object();
var totalTaxableValue = 0, totalIGST = 0, totalCGST = 0, totalSGST = 0, totalCESS = 0,  totalITCIGST = 0, totalITCCGST = 0, totalITCSGST = 0, totalITCCESS = 0,totalTax = 0, totalITC = 0, totalValue = 0, totalInvoices = 0, totalUploaded = 0, totalPending = 0, totalFailed = 0, totalExemptedValue = 0;var NotInJournals = 'NotInJournals';var hsnrowCount = 1;
var sendMsgCount=0;var invoiceArray=new Object(), msgArray = new Array(),irnCanArray=new Array(),gstMatchArray=new Array(),PIArray=new Array(),POArray=new Array(),supEmailids=new Array(),supCCEmailids=new Array(),gstnNotSelArray=new Array(),gstnArray=new Array(),sendMsgArray=new Array(),sendAllMsgsArray=new Array(), ewaybillArray = new Array(),vehicleUpdateArray = new Array(),vUpdateArray = new Array(),ITCclaimedArray = new Array(), selArray = new Object(), prchArray=new Array(), prchInvArray=new Array(), selInvArray=new Array(),Gstr2aArray=new Array(), MismatchArray=new Array(), selMatchArray=new Array(), mannualMatchArray = new Array(), mannuallMatchArray = new Array(),mannulaMatching = new Array(),mannuMatch = new Array(),mnmatch = new Array();var invoiceTable=new Object(), dbFilingTable, dbSendMsgTable,dbHSNFilingTable, dbDocIssueFilingTable, offLiabTable, purchaseTable,gstr2aTable,invTable,gstr2bMismatchTable,mannualMatchtable,mannualViewMatchTable,showInvTable,showInvPRTable;var ipAddress='',gstinnomatch = '', uploadResponse, gstSummary=null;
function updateReconsiletab(clientaddress){
	if(otherconfigdetails.enableTransDate == true){
		$('.invoiceview').val('Transaction Date');
	}else{
		$('.invoiceview').val('Invoice Date');
	}
	//userId, clientId, cMonth, cYear is calling client_header.jsp
	loadMismatchUsersByClient(userId, clientId, loadMismatchUsersInDropDown);
	loadMismatchInvoiceSupport(userId, clientId, cMonth, cYear, 'Monthly', loadMismatchCustomersInDropdown);
	loadMismatchInvTable(userId, clientId, cMonth, cYear, 'Monthly');
	initiateCallBacksForMultiSelectMismatch();
	initializeRemoveAppliedFiltersMismatch();
	loadReconcileSummary(userId, clientId, cMonth, cYear, 'Monthly');
}

function invoiceviewByRTrDate(id, value, varRetType, varRetTypeCode, clientAddress) {
	var invviewtype = value;var retType = $('#retType').val();
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
		success : function(summary) {
			otherconfigdetails = summary;
			//updateReconsiletab(clientAddress);
		}
	});
	
}

function loadMismatchUsersByClient(id, clientid, callback){
	var urlStr = _getContextPath()+'/cp_users/'+id+'/'+clientid
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			cpUsersResponse = response;
			isCpUsersResponseLoaded = true;
			callback(response);
			//loadMismatchUsersInDropDown(response)
		}
	});
}

function loadMismatchUsersInDropDown(response){
	var usersMultiSelObj = $('#MMmultiselect3')
	usersMultiSelObj.find('option').remove().end();
	usersMultiSelObj.append($("<option></option>").attr("value",globaluser).text(globaluser)); 
	if (response.length > 0) {				
		response.forEach(function(cp_user) {
			usersMultiSelObj.append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
		});
	}
	multiselrefresh_reports('#MMmultiselect3', '- User -', varRetType);
	$('#MMmultiselect3').multiselect('rebuild');
}

function loadMismatchInvoiceSupport(id, clientId, month, year, isMonthly, callback){
	var urlStr = _getContextPath()+'/getReconsileInvsSupport/'+clientId+'/'+month+'/'+year+'?isMonthly='+isMonthly;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			callback(response);
			//loadMismatchCustomersInDropdown(response)
		}
	});
}

function loadMismatchCustomersInDropdown(response){
	var usersMultiSelObj = $('#MMmultiselect4');
	usersMultiSelObj.find('option').remove().end();
	if (response.billedtoNames.length > 0) {				
		response.billedtoNames.forEach(function(customer) {
			if(customer != ''){
				usersMultiSelObj.append($("<option></option>").attr("value",customer).text(customer));
			} 
		});
	}
	var customerType ='- Suppliers -';
	multiselrefresh_reports('#MMmultiselect4', customerType , varRetType);
	$('#MMmultiselect4').multiselect('rebuild');
}

function initiateCallBacksForMultiSelectMismatch(){
	var multiSelDefaultVals = ['', '- Status -', '- Invoice Type -', '', '', '- Branches -', '- Verticals -'];	
	for(i=1;i<=6;i++){
		if(i == 3 || i == 4){
			$('#MMmultiselect'+i).hide();
			continue;
		}
		multiselrefresh_reports('#MMmultiselect'+i, multiSelDefaultVals[i], varRetType);
	}
}
function multiselrefresh_reports(idval, descVal, varRetType){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyReconcileFilters();
		},
		onSelectAll: function(){
			applyReconcileFilters();
		},
		onDeselectAll: function(){
			applyReconcileFilters();
		}
	});
}

function initializeRemoveAppliedFiltersMismatch(){
	$('#divMMFilters').on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i=1;i<=6;i++){
			$('#MMmultiselect'+i).multiselect('deselect', [val]);
		}
		applyReconcileFilters();
	});
}

function applyReconcileFilters() {
	var statusOptions = $('#MMmultiselect1 option:selected');
	var typeOptions = $('#MMmultiselect2 option:selected');
	var userOptions = $('#MMmultiselect3 option:selected');
	var vendorOptions = $('#MMmultiselect4 option:selected');
	var branchOptions = $('#MMmultiselect5 option:selected');
	var verticalOptions = $('#MMmultiselect6 option:selected');
	
	if(statusOptions.length > 0 || typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	
	var statusArr=new Array();
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var branchArr=new Array();
	var verticalArr=new Array();
	var filterContent='';
	if(statusOptions.length > 0) {
		for(var i=0;i<statusOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+statusOptions[i].text+'<span data-val="'+statusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
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
	$('#divMMFilters').html(filterContent);
	reloadReconcileTable(statusArr, typeArr, userArr, vendorArr, branchArr, verticalArr);
}

function updateReconcileFilterDetails(value) {
	for(i=1;i<=6;i++){
		$('#MMmultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#MMmultiselect1').multiselect().find(':checkbox[value="'+value+'"]').attr('checked','checked');
	$('#MMmultiselect1 option[value="'+value+'"]').attr("selected", 1);
	$('#MMmultiselect1').multiselect('select',''+value+'').multiselect('updateButtonText');
	$('#MMmultiselect1').multiselect('refresh');
	applyReconcileFilters();
	$('#reconcilemodal').modal('hide');
}

function clearMMFilters(){
	for(i=1;i<=6;i++){
		$('#MMmultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#divMMFilters').html('');
	$('.normaltable .filter').css("display","none");
	reloadReconcileTable(new Array(), new Array(), new Array(), new Array(), new Array(), new Array(),);
}

function loadMismatchInvTable(id, clientid, month, year, isMonthly){
	var pUrl =_getContextPath()+'/getInvs/'+clientid+'/'+month+'/'+year+'?returntype=Purchase Register&isMonthly='+isMonthly;
	invTableMismatchUrl = pUrl;
	if(invTableMismatch){
		invTableMismatch.clear();
		invTableMismatch.destroy();
	}
	invTableMismatch = $('#reconsileDbTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100], [10, 25, 50, 100] ],
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.total;
	        	 resp.recordsFiltered = resp.total;
	        	  loadMismatchTotals(resp.amts);
	        	 return resp.mappedInvs;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[4,'desc']],
		'columns': getMismatchInvColumns(clientId, year),
		'columnDefs':getReconcileColumnDefs()
	});
}

function getReconcileColumnDefs(){
	return  [
			{
				"targets": 0,
				"orderable": false
			}		
		];
}


function getMismatchInvColumns(clientId, year){
	var redClass=' color-red', roundOffClass=' color-submit';
	var chkBx = {data: function ( data, type, row ) {
		var gstnnum = '';
		var gstsnum = '';
		if(data.origin == "Purchase Register"){
			if(data.purchaseRegister.b2b && data.purchaseRegister.b2b.length > 0){
				if(data.purchaseRegister.b2b[0].ctin != undefined){
					gstnnum = data.purchaseRegister.b2b[0].ctin;
				}
			}else if(data.purchaseRegister.cdn && data.purchaseRegister.cdn.length > 0){
				if(data.purchaseRegister.cdn[0].ctin != undefined){
					gstnnum = data.purchaseRegister.cdn[0].ctin;
				}
			}
			if(data.origin == "GSTR2A"){
				return '<div class="checkbox nottoedit" index="'+data.purchaseRegister.userid+'"><label><input type="checkbox" id="invManFilter'+data.purchaseRegister.userid+'" onClick="updateMisMatchSelection(\''+data.purchaseRegister.userid+'\', \''+data.purchaseRegister.userid+'\', \''+gstnnum+'\', this)"/><i class="helper"></i></label></div>';				
			}else{
				return '<div class="checkbox nottoedit" index="'+data.purchaseRegister.userid+'"><label><input type="checkbox" id="invManFilter'+data.purchaseRegister.userid+'" onClick="updateMisMatchSelection(\''+data.purchaseRegister.userid+'\', null, \''+gstnnum+'\', this)"/><i class="helper"></i></label></div>';				
			}
		}else if(data.origin == "GSTR2A"){
			if(data.gstr2.b2b && data.gstr2.b2b.length > 0){
				if(data.gstr2.b2b[0].ctin != undefined){
					gstsnum = data.gstr2.b2b[0].ctin;
				}
			}else if(data.gstr2.cdn && data.gstr2.cdn.length > 0){
				if(data.gstr2.cdn[0].ctin != undefined){
					gstsnum = data.gstr2.cdn[0].ctin;
				}
			}
			return '<div class="checkbox nottoedit" index="'+data.gstr2.userid+'"><label><input type="checkbox" id="invManFilter'+data.gstr2.userid+'" onClick="updateMisMatchSelection(null, \''+data.gstr2.userid+'\', \''+gstsnum+'\', this)"/><i class="helper"></i></label></div>';
		}
    }};
	var info = {data: function ( data, type, row ) {
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
			return '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div>';
		}else{
			return '<div style="color:#359045" onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><span class="f-11">BOOKS</span></div><div onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')" class="color-red tdline_2"><span class="f-11">GSTR2A</span></div>';			
		}
	 }};
	var itype = {data: function ( data, type, row ) {
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
					
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.cdn && data.purchaseRegister.cdn.length > 0){
					if(data.purchaseRegister.cdn[0].nt[0].ntty != undefined){
						ntType = data.purchaseRegister.cdn[0].nt[0].ntty;
						if(ntType  == "C"){
							data.purchaseRegister.invtype = "Credit Note";
						}else if(ntType  == "D"){
							data.purchaseRegister.invtype = "Debit Note";
						}else{
							data.purchaseRegister.invtype = "Credit/Debit Notes";
						}
					}
				}
				return '<span class="text-left invoiceclk">'+data.purchaseRegister.invtype+'</span>';
			}else{
				if(data.gstr2.cdn && data.gstr2.cdn.length > 0){
					if(data.gstr2.cdn[0].nt[0].ntty != undefined){
						ntType = data.gstr2.cdn[0].nt[0].ntty;
						if(ntType  == "C"){
							data.gstr2.invtype = "Credit Note";
						}else if(ntType  == "D"){
							data.gstr2.invtype = "Debit Note";
						}else{
							data.gstr2.invtype = "Credit/Debit Notes";
						}
					}
				}
				return '<span class="text-left invoiceclk">'+data.gstr2.invtype+'</span>';
			}
		}else if(data.origin == "Purchase Register"){
			if(data.purchaseRegister.cdn && data.purchaseRegister.cdn.length > 0){
				if(data.purchaseRegister.cdn[0].nt[0].ntty != undefined){
					ntType = data.purchaseRegister.cdn[0].nt[0].ntty;
					if(ntType  == "C"){
						data.purchaseRegister.invtype = "Credit Note";
					}else if(ntType  == "D"){
						data.purchaseRegister.invtype = "Debit Note";
					}else{
						data.purchaseRegister.invtype = "Credit/Debit Notes";
					}
				}
			}
			return '<span class="text-left invoiceclk" onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')">'+data.purchaseRegister.invtype+'</span>';						
		}else if(data.origin == "GSTR2A"){
			if(data.gstr2.cdn && data.gstr2.cdn.length > 0){
				if(data.gstr2.cdn[0].nt[0].ntty != undefined){
					ntType = data.gstr2.cdn[0].nt[0].ntty;
					if(ntType  == "C"){
						data.gstr2.invtype = "Credit Note";
					}else if(ntType  == "D"){
						data.gstr2.invtype = "Debit Note";
					}else{
						data.gstr2.invtype = "Credit/Debit Notes";
					}
				}
			}
			return '<span class="text-left invoiceclk" onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')">'+data.gstr2.invtype+'</span>';			
		}
	 }};
	var billtoname = {data: function ( data, type, row ) {
		var billedtoname = '';
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		
		if(data.origin == "Purchase Register" && data.purchaseRegister.billedtoname != null){
			billedtoname = data.purchaseRegister.billedtoname;
		}else if(data.origin == "GSTR2A" && data.gstr2.billedtoname != null){
			billedtoname = data.gstr2.billedtoname;
			
		}
		
		if(billedtoname == "" && data.gstr2 != null && data.gstr2.billedtoname != null){
			billedtoname = data.gstr2.billedtoname;
		}
		
		if(invStatus == 'Manual Matched'){
			return '<span class="text-left invoiceclk">'+billedtoname+'</span>';			
		}else{
			return '<span class="text-left invoiceclk" onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')">'+billedtoname+'</span>';			
		}
	}};
	var fp = {data: function ( data, type, row ) {
		var rtp = '';
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.fp != undefined){
					rtp = data.purchaseRegister.fp;
				}
			}else{
				if(data.gstr2.fp != undefined){
					rtp = data.gstr2.fp;
				}
			}
			return '<div class="tdline_1">'+rtp+'</div><div class="tdline_2">-</div>'
		}else{
			if(data.origin == "Purchase Register" && data.gstr2 != null){
				if(data.purchaseRegister.fp != undefined){
					rtp = data.purchaseRegister.fp;
				}
				var gfp = '';
				if(data.gstr2.fp != undefined){
					gfp = data.gstr2.fp;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1">'+rtp+'</div><div class="tdline_2">'+gfp+'</div></span>';
			}else if(data.origin == "GSTR2A" && data.gstr2 != null){
				if(data.gstr2.fp != undefined){
					rtp = data.gstr2.fp;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+rtp+'</div></span>';
			}else{
				if(data.purchaseRegister.fp != undefined){
					rtp = data.purchaseRegister.fp;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">'+rtp+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var invsNo = {data:  function ( data, type, row ) {
		var invoiceno = '';
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.invoiceno != undefined){
					invoiceno = data.purchaseRegister.invoiceno;
				}
			}else{
				if(data.gstr2.invoiceno != undefined){
					invoiceno = data.gstr2.invoiceno;
				}
			}
			return '<div class="tdline_1">'+invoiceno+'</div><div class="tdline_2">-</div>';
		}else {
			if(data.origin == "Purchase Register" && data.gstr2 != null){
				if(data.purchaseRegister.invoiceno != undefined){
					invoiceno = data.purchaseRegister.invoiceno;
				}
				var invno = '';
				if(data.gstr2.invoiceno != undefined){
					invno = data.gstr2.invoiceno;
				}
				var colorclass = '';
				if(invStatus == 'Invoice No Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Probable Matched'){
					colorclass = roundOffClass;
				}else if(invStatus == 'Mismatched'){
					if(invoiceno != invno){
						colorclass = redClass;
					}
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+invoiceno+'</div><div class="tdline_2 '+colorclass+'">'+invno+'</div></span>';
			}else if(data.origin == "GSTR2A" && data.gstr2 != null){
				if(data.gstr2.invoiceno != undefined){
					invno = data.gstr2.invoiceno;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+invno+'</div></span>';
			}else{
				if(data.purchaseRegister.invoiceno != undefined){
					invoiceno = data.purchaseRegister.invoiceno;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">'+invoiceno+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var invDate = {data: function ( data, type, row ) {
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				return '<span><div class="tdline_1">'+(new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB")+'</div><div class="tdline_2">-</div></span>';
			}else{
				return '<span><div class="tdline_1">'+(new Date(data.gstr2.dateofinvoice)).toLocaleDateString("en-GB")+'</div><div class="tdline_2">-</div></span>';
			}
		}else {
			if(data.origin == "Purchase Register" && data.gstr2 != null){
				var colorclass = '';
				if(invStatus == 'Invoice Date Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Round Off Matched'){
					if((new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB") != (new Date(data.gstr2.dateofinvoice)).toLocaleDateString("en-GB")){
						colorclass = roundOffClass;
					}
				}else if(invStatus == 'Mismatched'){
					if((new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB") != (new Date(data.gstr2.dateofinvoice)).toLocaleDateString("en-GB")){
						colorclass = redClass;
					}
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+(new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB")+'</div><div class="tdline_2 '+colorclass+'">'+(new Date(data.gstr2.dateofinvoice)).toLocaleDateString("en-GB")+'</div></span>';
			}else if(data.origin == "GSTR2A" && data.gstr2 != null){
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+(new Date(data.gstr2.dateofinvoice)).toLocaleDateString("en-GB")+'</div></span>';
			}else{
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">'+(new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB")+'</div><div class="tdline_2 color-red">-</div></span>';
			}			
		}
	}};
	var billtogtnn = {data: function ( data, type, row ) {
		var invtype = data.invtype;
		var gstnnum= '';
		var cfs = '';
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.b2b && data.purchaseRegister.b2b.length > 0){
					if(data.purchaseRegister.b2b[0].ctin != undefined){
						gstnnum = data.purchaseRegister.b2b[0].ctin;
					}
				}else if(data.purchaseRegister.cdn && data.purchaseRegister.cdn.length > 0){
					if(data.purchaseRegister.cdn[0].ctin != undefined){
						gstnnum = data.purchaseRegister.cdn[0].ctin;
					}
				}
				return '<span class="" index="gstinno'+gstnnum+'"><div class="tdline_1">'+gstnnum+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal"  onClick="viewMannualMatchedInvoices(\''+docid+'\',\'Purchase Register\')">Mannualy Matched with Multiple Invoices</a></div></span>';
			}else{
				if(data.gstr2.b2b && data.gstr2.b2b.length > 0){
					if(data.gstr2.b2b[0].ctin != undefined){
						gstnnum = data.gstr2.b2b[0].ctin;
					}
				}else if(data.gstr2.cdn && data.gstr2.cdn.length > 0){
					if(data.gstr2.cdn[0].ctin != undefined){
						gstnnum = data.gstr2.cdn[0].ctin;
					}
				}
				return '<span class="" index="gstinno'+gstnnum+'"><div class="tdline_1">'+gstnnum+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal"  onClick="viewMannualMatchedInvoices(\''+docid+'\',\'GSTR2\')">Mannualy Matched with Multiple Invoices</a></div></span>';
			}
		}else{
			if(data.origin == "Purchase Register" && data.gstr2 != null){
				if(data.purchaseRegister.b2b && data.purchaseRegister.b2b.length > 0){
					if(data.purchaseRegister.b2b[0].ctin != undefined){
						gstnnum = data.purchaseRegister.b2b[0].ctin;
					}
				}else if(data.purchaseRegister.cdn && data.purchaseRegister.cdn.length > 0){
					if(data.purchaseRegister.cdn[0].ctin != undefined){
						gstnnum = data.purchaseRegister.cdn[0].ctin;
					}
				}
				var g2ctin = '';
				if(data.gstr2.b2b && data.gstr2.b2b.length > 0){
					if(data.gstr2.b2b[0].ctin != undefined){
						g2ctin = data.gstr2.b2b[0].ctin;
					}
				}else if(data.gstr2.cdn && data.gstr2.cdn.length > 0){
					if(data.gstr2.cdn[0].ctin != undefined){
						g2ctin = data.gstr2.cdn[0].ctin
					}
				}
				var colorclass = '';
				if(invStatus == 'GST No Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Mismatched'){
					if(gstnnum != g2ctin){
						colorclass = redClass;
					}
				}
				
				return '<span  index="gstinno'+gstnnum+'" onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+gstnnum+'</div><div class="tdline_2 '+colorclass+'">'+g2ctin+'</div></span>';
			}else if(data.origin == "GSTR2A" && data.gstr2 != null){
				var g2ctin = '';
				if(data.gstr2.b2b && data.gstr2.b2b.length > 0){
					if(data.gstr2.b2b[0].ctin != undefined){
						g2ctin = data.gstr2.b2b[0].ctin;
					}
				}else if(data.gstr2.cdn && data.gstr2.cdn.length > 0){
					if(data.gstr2.cdn[0].ctin != undefined){
						g2ctin = data.gstr2.cdn[0].ctin;
					}
				}
				return '<span  index="gstinno'+g2ctin+'" onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2ctin+'</div></span>';
			}else{
				if(data.purchaseRegister.b2b && data.purchaseRegister.b2b.length > 0){
					if(data.purchaseRegister.b2b[0].ctin != undefined){
						gstnnum = data.purchaseRegister.b2b[0].ctin;
					}
				}else if(data.purchaseRegister.cdn && data.purchaseRegister.cdn.length > 0){
					if(data.purchaseRegister.cdn[0].ctin != undefined){
						gstnnum = data.purchaseRegister.cdn[0].ctin;
					}
				}
				return '<span index="gstinno'+gstnnum+'" onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">'+gstnnum+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}		
	}};
	var taxblamt = {data: function ( data, type, row ) {
		var totalTaxableAmt = 0.0;
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.totaltaxableamount){
					totalTaxableAmt = data.purchaseRegister.totaltaxableamount;
				}
			}else{
				if(data.gstr2.totaltaxableamount){
					totalTaxableAmt = data.gstr2.totaltaxableamount;
				}
			}
			return '<span><div class="tdline_1">'+formatNumber(totalTaxableAmt.toFixed(2))+'</div><div class="tdline_2">-</div></span>';			
		}else {
			if(data.origin == "Purchase Register" && data.gstr2 != null){
				if(data.purchaseRegister.totaltaxableamount){
					totalTaxableAmt = data.purchaseRegister.totaltaxableamount;
				}
				var gtotalTaxableAmt = 0.0;
				if(data.gstr2.totaltaxableamount){
					gtotalTaxableAmt = data.gstr2.totaltaxableamount;
				}
				var colorclass = '';
				if(invStatus == 'Tax Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Round Off Matched'){
					if(totalTaxableAmt != gtotalTaxableAmt){
						colorclass = roundOffClass;
					}
				}else if(invStatus == 'Mismatched'){
					if(totalTaxableAmt != gtotalTaxableAmt){
						colorclass = redClass;
					}
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+formatNumber(totalTaxableAmt.toFixed(2))+'</div><div class="tdline_2 '+colorclass+'">'+formatNumber(gtotalTaxableAmt.toFixed(2))+'</div></span>';
			}else if(data.origin == "GSTR2A" && data.gstr2 != null){
				if(data.gstr2.totaltaxableamount){
					totalTaxableAmt = data.gstr2.totaltaxableamount;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(totalTaxableAmt.toFixed(2))+'</div></span>';
			}else{
				if(data.purchaseRegister.totaltaxableamount){
					totalTaxableAmt = data.purchaseRegister.totaltaxableamount;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">'+formatNumber(totalTaxableAmt.toFixed(2))+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var totamt = {data: function ( data, type, row ) {
		var totalAmt = 0.0;
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.totalamount){
					totalAmt = data.purchaseRegister.totalamount;
				}
			}else{
				if(data.gstr2.totalamount){
					totalAmt = data.gstr2.totalamount;
				}
			}
			return '<span><div class="tdline_1">'+formatNumber(totalAmt.toFixed(2))+'</div><div class="tdline_2">-</div></span>';			
		}else {
			if(data.origin == "Purchase Register" && data.gstr2 != null){
				if(data.purchaseRegister.totalamount){
					totalAmt = data.purchaseRegister.totalamount;
				}
				var gtotalAmt = 0.0;
				if(data.gstr2.totalamount){
					gtotalAmt = data.gstr2.totalamount;
				}
				var colorclass = '';
				if(invStatus == 'Invoice Value Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Round Off Matched'){
					if(totalAmt != gtotalAmt){
						colorclass = roundOffClass;
					}
				}else if(invStatus == 'Mismatched'){
					if(totalAmt != gtotalAmt){
						colorclass = redClass;
					}
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+formatNumber(totalAmt.toFixed(2))+'</div><div class="tdline_2 '+colorclass+'">'+formatNumber(gtotalAmt.toFixed(2))+'</div></span>';
			}else if(data.origin == "GSTR2A" && data.gstr2 != null){
				if(data.gstr2.totalamount){
					totalAmt = data.gstr2.totalamount;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(totalAmt.toFixed(2))+'</div></span>';
			}else{
				if(data.purchaseRegister.totalamount){
					totalAmt = data.purchaseRegister.totalamount;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">'+formatNumber(totalAmt.toFixed(2))+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var totlTax = {data: function ( data, type, row ) {
		var totalTax = 0.0;
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.totaltax){
					totalTax = data.purchaseRegister.totaltax;
				}
			}else{
				if(data.gstr2.totaltax){
					totalTax = data.gstr2.totaltax;
				}
			}
			return '<span><div class="tdline_1">'+formatNumber(totalTax.toFixed(2))+'</div><div class="tdline_2">-</div></span>';			
		}else{
			if(data.origin == "Purchase Register" && data.gstr2 != null){
				if(data.purchaseRegister.totaltax){
					totalTax = data.purchaseRegister.totaltax;
				}
				var gtotalTax = 0.0;
				if(data.gstr2.totaltax){
					gtotalTax = data.gstr2.totaltax;
				}
				var colorclass = '';
				if(invStatus == 'Tax Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Round Off Matched'){
					if(totalTax != gtotalTax){
						colorclass = roundOffClass;
					}
				}else if(invStatus == 'Mismatched'){
					if(totalTax != gtotalTax){
						colorclass = redClass;
					}
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+formatNumber(totalTax.toFixed(2))+'</div><div class="tdline_2 '+colorclass+'">'+formatNumber(gtotalTax.toFixed(2))+'</div></span>';
			}else if(data.origin == "GSTR2A" && data.gstr2 != null){
				if(data.gstr2.totaltax){
					totalTax = data.gstr2.totaltax;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(totalTax.toFixed(2))+'</div></span>';
			}else{
				if(data.purchaseRegister.totaltax){
					totalTax = data.purchaseRegister.totaltax;
				}
				return '<span onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')"><div class="tdline_1 color-red">'+formatNumber(totalTax.toFixed(2))+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var notes = {data: function ( data, type, row ) {
		if(data.origin == "Purchase Register"){
			return '<div><a href="#" onclick="supComments(\''+data.purchaseRegister.userid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';			
		}else if(data.origin == "GSTR2A"){
			return '<div><a href="#" onclick="supComments(\''+data.gstr2.userid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
		}
	}};
	var status = {data: function ( data, type, row ) {
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR 2A';
			}
			docid = data.purchaseRegister.userid;
		}else if(data.origin == "GSTR2A"){
			invStatus = data.gstr2.matchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2.userid;
		}else{
			invStatus = 'Not In Purchases';
		}
		if(invStatus == 'Manual Matched'){
			return '<span class="text-left invoiceclk bluetxt" index="mismatchStatus'+invStatus+'">'+invStatus+'</span>';			
		}else {
			return '<span class="text-left invoiceclk bluetxt" index="mismatchStatus'+invStatus+'" onClick="showMismatchInv(\''+docid+'\',\''+invStatus+'\')">'+invStatus+'</span>';
		}
	}};
	return [chkBx, info, itype, billtoname, fp, invsNo, invDate, billtogtnn, totamt, taxblamt, totlTax, status, notes];
}

function reloadReconcileTable(statusArr, typeArr, userArr, vendorArr, branchArr, verticalArr){
	var pUrl = invTableMismatchUrl;
	var appd = ''
	if(statusArr.length > 0){
		appd+='&documentType='+statusArr.join(',');
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
		appd+='&branch='+branchArr;
	}
	if(verticalArr.length > 0){
		appd+='&vertical='+verticalArr.join(',');
	}
	pUrl += appd;
	invTableMismatch.ajax.url(pUrl).load();
}

function loadReconcileSummary(id, clientId, month, year, isMonthly){
	var urlStr = _getContextPath()+'/getReconcileSummary/'+clientId+'/'+month+'/'+year+'?isMonthly='+isMonthly;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			$('#totalMismatchedInvoices').html(response.MISMATCHED);
			$('#totalMatchedInvoices').html(response.MATCHED);
			$('#totalMatchedInvoicesInOtherMonths').html(response.MATCHED_IN_OTHER_MONTHS);
			$('#totalGSTnoMismatchedInvoices').html(response.GST_NO_MISMATCHED);
			$('#totalInvoiceValueMismatchedInvoices').html(response.INVOICE_VALUE_MISMATCHED);
			$('#totalTaxMismatchedInvoices').html(response.TAX_MISMATCHED);
			$('#totalInvoiceNoMismatchInvoices').html(response.INVOICE_NO_MISMATCHED);
			$('#totalRoundoffMismatchedInvoices').html(response.ROUNDOFF_MATCHED);
			$('#totalInvoiceDateMismatchedInvoices').html(response.INVOICE_DATE_MISMATCHED);
			$('#totalProbableMatchedInvoices').html(response.PROBABLE_MATCHED);
			$('#totalMannualMatchedInvoices').html(response.MANUAL_MATCHED);
			
			$('#totalNotInGstr2AInvoices').html(response.NOT_IN_PURCHASES);
			$('#totalNotInPurchasesInvoices').html(response.NOT_IN_GSTR2A);
		}
	});
}

function loadMismatchTotals(totalsData){
	$('#idMMCount').html(totalsData ? totalsData.totalTransactions : '0');
	$('#idMMTaxableVal').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idMMTaxVal').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idTotAmtVal').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idMMIGST').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalIGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idMMCGST').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idMMSGST').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalSGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idMMCESS').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCESSAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
}
function showMismatchInv(id,mstatus){
	var content = '';var content1 = '';var branch;var billedtoname;var pbilledtoname;
	$('#showMismatchInvModal').modal("show");
	$('#recgstr2a').html('Record in GSTR2A');
	$.ajax({
		type: "GET",
		url: contextPath+"/mismatchInvoice_details/"+mstatus+"/"+id,
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response){
				if(mstatus == 'Not In GSTR 2A'){
					if(response.PurchaseRegisterinvoice != null){
						if(response.PurchaseRegisterinvoice.branch == '' || response.PurchaseRegisterinvoice.branch == null){branch = "-";}else{branch=response.PurchaseRegisterinvoice.branch;}
						if(response.PurchaseRegisterinvoice.billedtoname == '' || response.PurchaseRegisterinvoice.billedtoname == null){pbilledtoname = "";}else{pbilledtoname=response.PurchaseRegisterinvoice.billedtoname;}
						content += '<tr><td class="text-left">'+response.PurchaseRegisterinvoice.invtype+'</td><td class="text-left">'+pbilledtoname+'</td><td class="text-left">'+response.PurchaseRegisterinvoice.invoiceno+'</td><td>'+formatDate(response.PurchaseRegisterinvoice.dateofinvoice)+'</td><td class="text-left">'+response.PurchaseRegisterinvoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.PurchaseRegisterinvoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.PurchaseRegisterinvoice.totaltax).toFixed(2))+'</td><td class="text-left">'+branch+'</td><td class="text-left">'+mstatus+'</td></tr>';
					}
					if(showInvTable) {
						showInvTable.clear().destroy();
					}
					if(showInvPRTable) {
						showInvPRTable.clear().destroy();
					}
					
					$('#showInvDetailTable_Body').html(content);
				}else if(mstatus == 'Not In Purchases'){
						if(response.gstr2invoice != null){
							if(response.gstr2invoice.billedtoname == '' || response.gstr2invoice.billedtoname == null){billedtoname = "";}else{billedtoname=response.gstr2invoice.billedtoname;}
							content += '<tr><td class="text-left">'+response.gstr2invoice.invtype+'</td><td class="text-left">'+billedtoname+'</td><td class="text-left">'+response.gstr2invoice.invoiceno+'</td><td>'+formatDate(response.gstr2invoice.dateofinvoice)+'</td><td class="text-left">'+response.gstr2invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2invoice.totaltax).toFixed(2))+'</td><td class="text-left">'+mstatus+'</td></tr>';
						}
					if(showInvTable) {
						showInvTable.clear().destroy();
					}
					if(showInvPRTable) {
						showInvPRTable.clear().destroy();
					}
					$('#showInvDetailTable_Body1').html(content);
				}else{
					if(response.gstr2invoice != null){
						if(response.gstr2invoice.billedtoname == '' || response.gstr2invoice.billedtoname == null){billedtoname = "";}else{billedtoname=response.gstr2invoice.billedtoname;}
						content += '<tr><td class="text-left">'+response.gstr2invoice.invtype+'</td><td class="text-left">'+billedtoname+'</td><td class="text-left">'+response.gstr2invoice.invoiceno+'</td><td>'+formatDate(response.gstr2invoice.dateofinvoice)+'</td><td class="text-left">'+response.gstr2invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2invoice.totaltax).toFixed(2))+'</td><td class="text-left">'+mstatus+'</td></tr>';
					}
					if(response.PurchaseRegisterinvoice != null){
						if(response.PurchaseRegisterinvoice.billedtoname == '' || response.PurchaseRegisterinvoice.billedtoname == null){pbilledtoname = "";}else{pbilledtoname=response.PurchaseRegisterinvoice.billedtoname;}
						if(response.PurchaseRegisterinvoice.branch == '' || response.PurchaseRegisterinvoice.branch == null){branch = "-";}else{branch=response.PurchaseRegisterinvoice.branch;}
						content1 +='<tr><td class="text-left">'+response.PurchaseRegisterinvoice.invtype+'</td><td class="text-left">'+pbilledtoname+'</td><td class="text-left">'+response.PurchaseRegisterinvoice.invoiceno+'</td><td>'+formatDate(response.PurchaseRegisterinvoice.dateofinvoice)+'</td><td class="text-left">'+response.PurchaseRegisterinvoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.PurchaseRegisterinvoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.PurchaseRegisterinvoice.totaltax).toFixed(2))+'</td><td class="text-left"><span>'+branch+'</span></td><td class="text-left">'+mstatus+'</td></tr>';
					}
					
					if(showInvTable) {
						showInvTable.clear().destroy();
					}
					
					if(showInvPRTable) {
						showInvPRTable.clear().destroy();
					}
					$('#showInvDetailTable_Body1').html(content);
					$('#showInvDetailTable_Body').html(content1);
					
				}
				showInvTable = $('#showInvDetailTable1').DataTable({
					"dom": '<"toolbar">frtip',
					"paging": false,
					"searching": false,
					"responsive": true
					
				});
				showInvPRTable = $('#showInvDetailTable').DataTable({
					"dom": '<"toolbar">frtip',
					"paging": false,
					"searching": false,
					"responsive": true
					
				});
		},error:function(err){}
	});
}
function supComments(id){
	$('#supCommentsModal').modal('show');
	$('.suppliercommentsTab').html("");
	$('#nocomments_sup').text("");
	$('#addComment').attr("onclick","addSupplierComments(\""+id+"\")");
	$.ajax({
		url: contextPath+"/suppliercomments/"+id,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			if(response.length == 0){
				$('#nocomments_sup').text("No Comments Added Yet, Please add your Comments");
			}
			for(var i=0;i<response.length;i++){
				$('#nocomments_sup').text("");
				//$('.suppliercommentsTab').append('<div class="supcommentsTab mb-2 mr-2">'+response[i].supcomments+'<br/><strong>'+response[i].addedby+'</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+formatDate(response[i].commentDate)+'</strong></div>');
				$('.suppliercommentsTab').append('<div class="supcommentsTab mb-2 mr-2"><strong><label class="label_txt">Added By : '+response[i].addedby+'</label></strong><strong><label style="float:right;">Date : '+formatDate(response[i].commentDate)+'</label></strong><br/>'+response[i].supcomments+'</div>');
			}
		},error:function(err){
		}
	});
}
function addSupplierComments(invoiceid){
	var comments = $('#sup_comments').val();
	if(comments != ""){
		$.ajax({
			url: contextPath+"/saveSupplierComments/"+invoiceid+""+urlSuffixs,
			method:"POST",
			contentType: 'application/x-www-form-urlencoded',
			data: {
				'comments': comments
			},
			success : function(response){
				$('.suppliercommentsTab').html("");
				for(var i=0;i<response.length;i++){
					$('.suppliercommentsTab').append('<div class="supcommentsTab mb-2 mr-2"><strong><label class="label_txt">Added By : '+response[i].addedby+'</label></strong><strong><label style="float:right;">Date : '+formatDate(response[i].commentDate)+'</label></strong><br/>'+response[i].supcomments+'</div>');
				}
					$('#sup_comments').val("");
			},error:function(err){
			}
		});
	}
}

function updateMisMatchSelection(id, gstrId, gstin, chkBox) {
	var mObj=new Object();
	mObj.purchaseId = id;
	mObj.gstrId = gstrId;
	if(chkBox.checked) {
		selMatchArray.push(mObj);
		sendMsgArray.push(id);
		sendMsgCount++;
		if(gstMatchArray.length == 0){
			gstinnomatch = gstin;
			gstMatchArray.push(gstin);
			$('.sendmessage').removeClass('disable');
			$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2\')');
		}else{
			if(jQuery.inArray(gstin, gstMatchArray ) == -1){
				gstnNotSelArray.push(gstin);
				$('.sendmessage').addClass('disable');
			}
		}
	} else {
		sendMsgCount--;
		var tArray=new Array();
		selMatchArray.forEach(function(inv) {
			if(inv.purchaseId == id && inv.gstrId == gstrId) {
			} else {
				tArray.push(inv);
			}
		});
		selMatchArray = tArray;
		
		var mArray=new Array();
		sendMsgArray.forEach(function(inv) {
			if(inv != id) {
				mArray.push(inv);
			}
		});
		sendMsgArray = mArray;
	
		if(jQuery.inArray(gstin, gstMatchArray ) == -1){
			gstnNotSelArray.splice(gstnNotSelArray.indexOf(gstin), 1);
			if(gstnNotSelArray.length > 0){
				$('.sendmessage').addClass('disable');
			}else{
				$('.sendmessage').removeClass('disable');
				$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2\')');
			}
		}else{
			if(sendMsgCount == 1){
				if(gstnNotSelArray.length > 0){
					gstMatchArray = gstnNotSelArray;
				}
				$('.sendmessage').removeClass('disable');
				$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2\')');
			}else{
				if(gstnNotSelArray.length > 0){
					$('.sendmessage').addClass('disable');
				}else{
					$('.sendmessage').removeClass('disable');
					$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2\')');
				}
			}
		}
		if(sendMsgCount == 0){
			$('.sendmessage').addClass('disable');
			gstMatchArray=new Array();
			gstnNotSelArray=new Array();
		}
	}
	if(selMatchArray.length > 0) {
		$('#btnMisMatchAccept').removeClass('disable');
		$('#btnMisMatchReject').removeClass('disable');
		if(selMatchArray.length == 1) {
			$('.mannualMatching').removeClass('disable');
		}else{
			$('.mannualMatching').addClass('disable');
		}
		$('.select_msg').text('You have Selected '+selMatchArray.length+' Invoice(s)');
	} else {
		$('#btnMisMatchAccept').addClass('disable');
		$('#btnMisMatchReject').addClass('disable');
		$('.mannualMatching').addClass('disable');
		$('.select_msg').text('');
		$('#checkMismatch').prop("checked",false);
	}
}

showInvTable = $('#showInvDetailTable1').DataTable({
	"dom": '<"toolbar">frtip',
	"paging": false,
	"searching": false,
	"responsive": true
	
});
showInvPRTable = $('#showInvDetailTable').DataTable({
	"dom": '<"toolbar">frtip',
	"paging": false,
	"searching": false,
	"responsive": true
	
});

function viewMannualMatchedInvoices(invoiceid, returntype){
	if(returntype == 'Purchase Register'){
		$('#vinvhdr').html('Invoice In Purchases');
		$('#vdinvhdr').html('Invoice In GSTR2A');
	}else{
		$('#vinvhdr').html('Invoice In GSTR2A');
		$('#vdinvhdr').html('Invoice In Purchases');
	}
	
	$.ajax({
		url: contextPath+"/mannualMatchingInvoice/"+invoiceid+"/"+returntype,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			JSON.stringify(response);
			var invoice = updateInvoiceDetails(response);
			var maincontent = '<tr><td class="text-left">'+invoice.invoicetype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left">'+invoice.invoiceno+'</td><td class="text-left">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
			$('#mainvMatchInvoices').html(maincontent);
		},error:function(err){}
	});	
	$.ajax({
		url: contextPath+"/viewMannualMatchingInvoices/"+invoiceid+"/"+returntype,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			var content = '';
			var vmannulaMatching = new Array();
			var fyList = null;
			if(response.content) {
				fyList = response.content;
			} else {
				fyList = response;
			}
			if(fyList instanceof Array) {
				if(fyList.length > 0) {
					fyList.forEach(function(fyInv){
						JSON.stringify(fyInv);
						var fyInvoice = updateInvoiceDetails(fyInv);
						vmannulaMatching.push(fyInvoice);
					});
				}
			}
			if(mannualViewMatchTable) {
				mannualViewMatchTable.clear().destroy();
			}
			if(vmannulaMatching == null || vmannulaMatching == undefined || vmannulaMatching.length == 0) {
			} else {
				vmannulaMatching.forEach(function(invoice) {
					content += '<tr><td class="text-left">'+invoice.invtype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left">'+invoice.invoiceno+'</td><td class="text-left">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
				});
				$('#viewMannulaMatchInvoices').html(content);
			}
			mannualViewMatchTable = $('#viewmannualMatch_table5').DataTable({
				"paging": true,
				"searching": true,
				"lengthMenu": [ [7,10, 25, 50, 100, 500], [7,10, 25, 50, 100, 500] ],
				"responsive": true,
				"ordering": true,
				"pageLength": 7,
				"language": {
					"search": "_INPUT_",
					"searchPlaceholder": "Search..."
				}
			});
			$('#viewmannualMatch_table5_wrapper').css('width','100%');
		},error:function(err){}
	});
}


function mannualMatchingInv(clientid,monthlyOryearly){
	var returnType;
	var invoiceid;
	if(selMatchArray[0].purchaseId){
		returnType = 'Purchase Register';
		invoiceid = selMatchArray[0].purchaseId;
		$('#invhdr').html('Invoice In Purchases');
		$('#dinvhdr').html('Invoice In GSTR2A');
	}else{
		returnType = 'GSTR2';
		invoiceid = selMatchArray[0].gstrId;
		$('#invhdr').html('Invoice In GSTR2A');
		$('#dinvhdr').html('Invoice In Purchases');
	}
	if(mannualMatchtable){
		mannualMatchtable.clear();
		mannualMatchtable.destroy();
	}
	$.ajax({
		url: contextPath+"/mannualMatchingInvoice/"+invoiceid+"/"+returnType,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			JSON.stringify(response);
			var invoice = updateInvoiceDetails(response);
			var maincontent = '<tr><td class="text-left">'+invoice.invoicetype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left" id="manInvoiceNo">'+invoice.invoiceno+'</td><td class="text-left" id="manInvoiceDate">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left" id="manGSTIN">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
				
				$('#mainMatchInvoices').html(maincontent);
		},error:function(err){
			
		}
	});
	var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
	var yer = year;
	if(monthlyOryearly == 'yearly'){
		yer = yrs;
	}else{
		yer = year;
	}
	
	
	var pUrl = contextPath+"/getInvoicesForMannualMatch/"+clientid+"/"+invoiceid+"/"+returnType+"/"+month+"/"+yer+"/"+monthlyOryearly;
	mannualMatchTableUrl["Mannualmatch"] = pUrl;
	mannualMatchtable = $('#mannualMatch_table5').DataTable({
		
		"dom": '<"toolbar"f>lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 return resp.invoices.content;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[4,'desc']],
		'columns': getInvManualMatchColumns(),
		'columnDefs' : getManualMatchInvColumnDefs()
	});
	$('#mannualHiddenInvoiceid').val(invoiceid);
	$('#mannualHiddenReturnType').val(returnType);
	$('#mannualMatch_table5_wrapper').css('width','100%');
	$('#message_send_btn').attr("onclick","updateMannualMatchData(this,'yearly')");	
}

function updateMannualMatchData(btn,monthlyOrYearly) {
	var invoiceid = $('#mannualHiddenInvoiceid').val();
	var returnType = $('#mannualHiddenReturnType').val();
	$(btn).addClass('btn-loader');
	$.ajax({
		type: "POST",
		url: contextPath+"/mannualMatchArray/"+returnType+"/"+invoiceid,
		async: false,
		cache: false,
		data: JSON.stringify(mannualMatchArray),
		contentType: 'application/json',
		success : function(response) {
			window.location.href = contextPath+'/alliview'+commonturnOverSuffix+'/GSTR2/'+Paymenturlprefix+'?type=mmtch';
		},
		error : function(e, status, error) {if(e.responseText) {errorNotification(e.responseText);}}
	});
}

function sendSuppliermessage(retType){
	$('#emailerrormsg').addClass('d-none');
	$('#supplier_fullnameerrormsg').addClass('d-none');
	$('#meassagedetail').html('We thank you for your valuable contribution to our business .&#13;&#10;&#13;&#10;You must be aware that for availing GST credit on inward supplies, one of the conditions is that the invoices should appear in GSTR-2A. {Ref. Sec 16 & rule 36 of the CGST}.&#13;&#10; As a part of this compliance procedure, we have reconciled our books/ purchase register with Form GSTR-2A as made available to us by GSTN (generated based on GSTR-1 filed by you) for the period 2019. &#13;&#10;&#13;&#10;In this relation, we have observed non-reporting / mismatch of below invoices in GSTR-1 by you:');
	$('#subject').val("Mismatch In GSTR2A Invoices");
	$.ajax({
		type: "POST",
		url: contextPath+"/invoice_details/"+retType,
		async: false,
		cache: false,
		data: JSON.stringify(sendMsgArray),
		contentType: 'application/json',
		success : function(response){
			if(response.invoice.length >0){
				if(dbSendMsgTable){
					dbSendMsgTable.clear().destroy();
				}
			}
			var count=1;
			for(var i=0; i<response.invoice.length; i++){
				if(count <= 10){
					$('#sendMsgsBody').append('<tr><td><div class="checkbox" index="'+response.invoice[i].revchargetype+'"><label><input type="checkbox" class="send_msgInv" id="invFilterGSTR2'+response.invoice[i].revchargetype+'" onClick="updateMsgSelection(\''+response.invoice[i].revchargetype+'\', \'GSTR2\',this)" checked/><i class="helper"></i></label></div></td><td class="text-left">'+response.invoice[i].referenceNumber+'</td><td class="text-left">'+response.invoice[i].invoiceno+'</td><td class="text-left">'+formatDate(response.invoice[i].dateofinvoice)+'</td><td class="text-left">'+response.invoice[i].b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.invoice[i].totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.invoice[i].totalamount).toFixed(2))+'</td></tr>');
					sendAllMsgsArray.push(response.invoice[i].revchargetype);
				}else{
					$('#sendMsgsBody').append('<tr><td><div class="checkbox" index="'+response.invoice[i].revchargetype+'"><label><input type="checkbox" class="send_msgInv" id="invFilterGSTR2'+response.invoice[i].revchargetype+'" onClick="updateMsgSelection(\''+response.invoice[i].revchargetype+'\', \'GSTR2\',this)"/><i class="helper"></i></label></div></td><td class="text-left">'+response.invoice[i].referenceNumber+'</td><td class="text-left">'+response.invoice[i].invoiceno+'</td><td class="text-left">'+formatDate(response.invoice[i].dateofinvoice)+'</td><td class="text-left">'+response.invoice[i].b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.invoice[i].totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.invoice[i].totalamount).toFixed(2))+'</td></tr>');
				}
				count++;
				if(response.invoice[i].toAddr2 == '' || response.invoice[i].toAddr2 == null ){
						$("#supplier_emailid").prop("readonly",false);
						$('#emailerrormsg').removeClass('d-none');
				}else{
						$("#supplier_emailid").val(response.invoice[i].toAddr2);
						//$("#supplier_emailid").prop("readonly",true);
				}
				if(response.invoice[i].toAddr1 == '' || response.invoice[i].toAddr1 == null ){
					$('#supplier_fullnameerrormsg').removeClass('d-none');
					$('#emailerrormsg').addClass('d-none');
				}else{
					$('#supplier_fullname').val(response.invoice[i].toAddr1);
				}
			}
			dbSendMsgTable = $('#msgDetailTable').DataTable({
				"dom": '<"toolbar">frtip',
				"pageLength": 10,
				"paging": true,
				"searching": false,
				"responsive": true,
				"order": [[2,'desc']]
			});
			$('#msgDetailTable_text').html('<h6>Attached Invoices</h6>');
		},error:function(err){
			
		}
		
	});
}

function getInvManualMatchColumns(){
	var chkBx = {data: function ( data, type, row ) {
      	 			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invManFilter'+data.userid+'" onClick="updateMannulaMatchSelection(\''+data.userid+'\', null,this)"/><i class="helper"></i></label></div>';
    			}};
	
	var itype = {data: function ( data, type, row ) {
			     	return '<span class="text-left invoiceclk ">'+data.invtype+'</span>';
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
					if(data.b2b && data.b2b.length > 0){
						if(data.b2b[0].ctin != undefined){
							gstnnum = data.b2b[0].ctin;
						}
					}else if(data.cdn && data.cdn.length > 0){
						if(data.cdn[0].ctin != undefined){
							gstnnum = data.cdn[0].ctin;
						}
					}
					 return '<span class="text-left invoiceclk ">'+gstnnum+'</span>';
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
					var totalTaxableAmt = 0.0;
					if(data.totaltaxableamount){
						totalTaxableAmt = data.totaltaxableamount;
					}
				   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
				    }};
	var totlTax = {data: function ( data, type, row ) {
					var totalTax = 0.0;
					if(data.totaltax){
						totalTax = data.totaltax;
					}
				   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(totalTax.toFixed(2))+'</span>';
				    }};
	return [chkBx , itype, billtoname,invsNo, invDate, billtogtnn,taxblamt,  totlTax];
}
function getManualMatchInvColumnDefs(){
	return  [
		{
			"targets": 0,
			"orderable": false
		}		
	];
}

function updateMannulaMatchSelection(id, gstrId, chkBox) {
	
	var mObj=new Object();
	mObj.purchaseId = id;
	mObj.gstrId = gstrId;
	if(chkBox.checked) {
		mannualMatchArray.push(mObj);
	} else {
		var tArray=new Array();
		mannualMatchArray.forEach(function(inv) {
			if(inv.purchaseId == id) {
			} else {
				tArray.push(inv);
			}
		});
		mannualMatchArray = tArray;
	}
}

function updateMainMannualSelection(chkBox) {
	mannualMatchArray = new Array();
	if(mannulaMatching) {
		if(chkBox.checked) {
			mannualMatchtable.rows().every(function () {
				var row = this.data();
				var index = $(row[0]).attr('index');
				row[0]='<div class="checkbox" index="'+index+'"><label><input type="checkbox" checked id="invManFilter'+index+'" onclick="updateMannulaMatchSelection(null, \''+index+'\', this)"/><i class="helper"></i></label></div>';			
				this.data(row);
				this.invalidate();
			});
		}else{
			mannualMatchtable.rows().every(function () {
				var row = this.data();
				var index = $(row[0]).attr('index');
				row[0]='<div class="checkbox" index="'+index+'"><label><input type="checkbox" id="invManFilter'+index+'" onclick="updateMannulaMatchSelection(null, \''+index+'\', this)"/><i class="helper"></i></label></div>';
				this.data(row);
				this.invalidate();
			});
		}
		mannulaMatching.forEach(function(invoice) {
			if(chkBox.checked) {
				var mObj=new Object();
				mObj.purchaseId = invoice.id;mObj.gstrId = invoice.matchingId;
				mannualMatchArray.push(mObj);
			}
		});
	}
}
function mannualMatchInvoiceFiltera(arrGSTNo, arrInvoiceNumber, arrInvoiceDate) {
	var pUrl = mannualMatchTableUrl["Mannualmatch"];
	var appd = '';
	if(arrGSTNo.length > 0){
		appd+='&gstno='+arrGSTNo.join(',');
	}
	if(arrInvoiceNumber.length > 0){
		appd+='&invoiceno='+arrInvoiceNumber.join(',');
	}
	if(arrInvoiceDate.length > 0){
		appd+='&dateofInvoice='+arrInvoiceDate.join(',');
	}
	pUrl += '?'+appd;
	mannualMatchtable.ajax.url(pUrl).load();
}

function updateInvoiceDetails(invoice,clientAddress) {
	var totalIGST1 = 0, totalCGST1 = 0, totalSGST1 = 0, totalCESS1 = 0,  totalITCIGST1 = 0, totalITCCGST1 = 0, totalITCSGST1 = 0, totalITCCESS1 = 0, totalinvitc = 0, totalExempted = 0;
	var rettype = 'GSTR2';
	invoice.id = invoice.userid;
	invoice.invoicetype = invoice.invtype;
	invoice.subSupplyType = invoice.subSupplyType;
	invoice.transDistance = invoice.transDistance;
	if(invoice.supplyType == null){
		invoice.supplyType = '';
	}
	if(invoice.eBillNo == null){
		invoice.eBillNo = '';
	}
	if(invoice.invoiceno == null){
		invoice.invoiceno = '';
	}
	if(invoice.paymentStatus == null){
		invoice.paymentStatus = '';
	}
	if(invoice.clientAddress == null){
		invoice.clientAddress = clientAddress;
	}
	if(invoice.invoiceEcomGSTIN == null){
		invoice.invoiceEcomGSTIN = '';
	}
	if(invoice.tdstcsenable == null){
		invoice.tdstcsenable = 'false';
	}
	if(invoice.section == null){
		invoice.section = '';
	}
	if(invoice.tcstdspercentage == null){
		invoice.tcstdspercentage = 0.00;
	}
	if(invoice.tcstdsAmount == null){
		invoice.tcstdsAmount = 0.00;
	}
	if(invoice.netAmount == null){
		invoice.netAmount = 0.00;
	}
	if(invoice.revchargetype == null){
		invoice.revchargetype = 'Regular';
	}
	if(invoice.ledgerName == null){
		invoice.ledgerName = '';
	}
	if(invoice.dealerType == null){
		invoice.dealerType = '';
	}
	if(invoice.vendorName == null){
		invoice.vendorName = '';
	}
	invoice.serialnoofinvoice = invoice.invoiceno;
	if(invoice.b2b && invoice.b2b.length > 0) {
		if(invoice.b2b[0].ctin == null){
			invoice.b2b[0].ctin = '';
		}
		if(invoice.b2b[0].cfs == null){
			invoice.b2b[0].cfs = '';
		}
		invoice.cfs = invoice.b2b[0].cfs; 
		invoice.billedtogstin = invoice.b2b[0].ctin;
		if(invoice.b2b[0].inv && invoice.b2b[0].inv.length > 0) {
			invoice.address = invoice.b2b[0].inv[0].address;
			invoice.invTyp = invoice.b2b[0].inv[0].invTyp;
		}
	} else {
		invoice.billedtogstin = '';
		invoice.address = '';
	}
	if(invoice.pi && invoice.pi.length > 0){
		var expDate = invoice.pi[0].expirydate;
		expDate = expDate.split('-'); 
		invoice.expirydateofinvoice = expDate[0] + "/" + expDate[1] + "/" + expDate[2];
	}else if(invoice.est && invoice.est.length > 0){
		var expDate = invoice.est[0].expirydate;
		expDate = expDate.split('-');
		invoice.expirydateofinvoice = expDate[0] + "/" + expDate[1] + "/" + expDate[2];
	}
	if(invoice.po && invoice.po.length > 0){
		var deliveryDate = invoice.po[0].deliverydate;
		deliveryDate = deliveryDate.split('-');
		invoice.deliverydate = deliveryDate[0] + "/" + deliveryDate[1] + "/" + deliveryDate[2];
	}
	
	if(invoice.dc && invoice.dc.length > 0){
		invoice.challantype = invoice.dc[0].challanType;
	}
	
	if(invoice.diffPercent == null){
		invoice.diffPercent = 'No';
	}
	if(invoice.billDate != null){
		var billDate = new Date(invoice.billDate);
		var day = billDate.getDate() + "";var month = (billDate.getMonth() + 1) + "";var year = billDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.billDate = day + "/" + month + "/" + year;
	}
	if(invoice.dueDate != null){
		var dDate = new Date(invoice.dueDate);
		var day = dDate.getDate() + "";var month = (dDate.getMonth() + 1) + "";	var year = dDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dueDate = day + "/" + month + "/" + year;
	}else{invoice.dueDate = "";}
	if(invoice.dateofinvoice != null){
		var invDate = new Date(invoice.dateofinvoice);
		var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dateofinvoice = day + "/" + month + "/" + year;
	}else{invoice.dateofinvoice = "";}
	if(invoice.dateofitcClaimed != null){
		var invDate = new Date(invoice.dateofitcClaimed);
		var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";var year = invDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dateofitcClaimed = day + "/" + month + "/" + year;
	}else{invoice.dateofitcClaimed = "";}
	if(invoice.eBillDate != null){
		var eDate = new Date(invoice.eBillDate);var day = eDate.getDate() + "";var month = (eDate.getMonth() + 1) + "";var year = eDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.eBillDate = day + "/" + month + "/" + year;
	}else{invoice.eBillDate = "";}
	if(invoice.bankDetails) {
		if(invoice.bankDetails.bankname == '' && invoice.bankDetails.accountnumber == '' && invoice.bankDetails.branchname == '' && invoice.bankDetails.ifsccode == ''){
			invoice.bankdetails = 'false';
		}else{
			invoice.bankdetails = 'true';
		}	
		invoice.bankname = invoice.bankDetails.bankname;
		invoice.accountnumber = invoice.bankDetails.accountnumber;
		invoice.branchname = invoice.bankDetails.branchname;
		invoice.ifsccode = invoice.bankDetails.ifsccode;
		if(invoice.bankDetails.accountName == null){
			invoice.accountname = '';
		}else{
			invoice.accountname = invoice.bankDetails.accountName;
		}
		} else {
		invoice.bankdetails = 'false';
	}
	if(invoice.fullname == null) {
		invoice.fullname = '';
	}
	if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
		invoice.invoiceCustomerIdAndBilledToName = '';
	}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
		invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
	}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
		invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
	}
	if(invoice.invoiceCustomerId == null) {
		invoice.invoiceCustomerId = '';
	}
	if(invoice.billedtoname == null) {
		invoice.billedtoname = '';
	}
	if(invoice.branch == null) {
		invoice.branch = '';
	}
	if(invoice.vertical == null) {
		invoice.vertical = '';
	}
	if(invoice.totaltaxableamount) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
					if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalTaxableValue += invoice.totaltaxableamount;
					}else{
						totalTaxableValue-=invoice.totaltaxableamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalTaxableValue-=invoice.totaltaxableamount;
				}else{
					totalTaxableValue+=invoice.totaltaxableamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalTaxableValue+=invoice.totaltaxableamount;
					}else{
						totalTaxableValue-=invoice.totaltaxableamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalTaxableValue-=invoice.totaltaxableamount;
					}else{
						totalTaxableValue+=invoice.totaltaxableamount;
					}
				}
				
			}else{
				totalTaxableValue+=invoice.totaltaxableamount;
			}
		}
	} else {
		invoice.totaltaxableamount = 0.00;
	}
	if(invoice.totaltax) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
					totalTax+=invoice.totaltax;
				}else{
					totalTax-=invoice.totaltax;
				}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalTax-=invoice.totaltax;
				}else{
					totalTax+=invoice.totaltax;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalTax+=invoice.totaltax;
					}else{
						totalTax-=invoice.totaltax;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalTax-=invoice.totaltax;
					}else{
						totalTax+=invoice.totaltax;
					}
				}
			}else{
				totalTax+=invoice.totaltax;
			}
		}
	} else {
		invoice.totaltax = 0.00;
	}
	if(invoice.totalamount) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalValue-=invoice.totalamount;
				}else{
					totalValue+=invoice.totalamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
					}else{
						totalValue+=invoice.totalamount;
					}
				}
			}else{
				totalValue+=invoice.totalamount;
			}
		}
	} else {
		invoice.totalamount = invoice.totaltax + invoice.totaltaxableamount;
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalValue-=invoice.totalamount;
				}else{
					totalValue+=invoice.totalamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
					}else{
						totalValue+=invoice.totalamount;
					}
				}
			}else{
				totalValue+=invoice.totalamount;
			}
		}
		
	}
	if(('B2C' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0) || ('B2CSA' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0)) {
		invoice.splyTy = invoice.b2cs[0].splyTy;
		invoice.ecommercegstin = invoice.b2cs[0].etin;
	}
	if(('Advances' == invoice.invtype && invoice.at && invoice.at.length > 0) || ('ATA' == invoice.invtype && invoice.at && invoice.at.length > 0)) {
		invoice.splyTy = invoice.at[0].splyTy;
	}else if(('Advances' == invoice.invtype && invoice.txi && invoice.txi.length > 0) || ('ATA' == invoice.invtype && invoice.txi && invoice.txi.length > 0)){
		invoice.splyTy = invoice.txi[0].splyTy;
	}
	if(('Advance Adjusted Detail' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0) || ('TXPA' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0)) {
		invoice.splyTy = invoice.txpd[0].splyTy;
		invoice.invtype = 'Adv. Adjustments';
	}
	if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
		invoice.voucherNumber = invoice.cdn[0].nt[0].inum;
		invoice.ntty = invoice.cdn[0].nt[0].ntty;
		invoice.rsn = invoice.cdn[0].nt[0].rsn;
		invoice.pGst = invoice.cdn[0].nt[0].pGst;
		if(invoice.cdn[0].cfs == null){
			invoice.cdn[0].cfs = '';
		}
		invoice.cfs = invoice.cdn[0].cfs; 
		if(invoice.cdn[0].nt[0].idt != null){
			invoice.voucherDate = invoice.cdn[0].nt[0].idt;
		}else{
			invoice.voucherDate = '';
		}
		if(invoice.cdn[0].nt[0].ntty == 'C'){
			invoice.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			invoice.invtype = 'Debit Note';
		}
		
	} else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
		invoice.voucherNumber = invoice.cdnr[0].nt[0].inum;
		invoice.ntty = invoice.cdnr[0].nt[0].ntty;
		invoice.rsn = invoice.cdnr[0].nt[0].rsn;
		invoice.pGst = invoice.cdnr[0].nt[0].pGst;
		if(invoice.cdnr[0].nt[0].idt != null){
			invoice.voucherDate = invoice.cdnr[0].nt[0].idt;
		}else{
			invoice.voucherDate = '';
		}
		if(invoice.cdnr[0].nt[0].ntty == 'C'){
			invoice.invtype = 'Credit Note';
		}else if(invoice.cdnr[0].nt[0].ntty == 'D'){
			invoice.invtype = 'Debit Note';
		}
	}
	if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
		invoice.voucherNumber = invoice.cdnur[0].inum;
		if(invoice.cdnur[0].idt != null){
			invoice.voucherDate = invoice.cdnur[0].idt;
		}else{
			invoice.voucherDate = '';
		}
		invoice.ntty = invoice.cdnur[0].ntty;
		invoice.rsn = invoice.cdnur[0].rsn;
		invoice.pGst = invoice.cdnur[0].pGst;
		invoice.cdnurtyp = invoice.cdnur[0].typ;
		if(invoice.cdnur[0].ntty == 'C'){
			invoice.invtype = 'Credit Note(UR)';
		}else if(invoice.cdnur[0].ntty == 'D'){
			invoice.invtype = 'Debit Note(UR)';
		}
	}
	if(('Exports' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0) || ('EXPA' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0)) {
		invoice.portcode = invoice.exp[0].inv[0].sbpcode;
		invoice.shippingBillNumber = invoice.exp[0].inv[0].sbnum;
		invoice.exportType = invoice.exp[0].expTyp;
		if(invoice.exp[0].inv[0].sbdt != null && invoice.exp[0].inv[0].sbdt != undefined){
			invoice.shippingBillDate = formatDate(invoice.exp[0].inv[0].sbdt);
		}else{
			invoice.shippingBillDate = '';
		}
	}
	if(('ISD' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0) || ('ISDA' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0)) {
		invoice.isdDocty = invoice.isd[0].doclist[0].isdDocty;
		invoice.documentNumber = invoice.isd[0].doclist[0].docnum;
		if(invoice.isd[0].doclist[0].docdt != null && invoice.isd[0].doclist[0].docdt != undefined){
			invoice.documentDate = formatDate(invoice.isd[0].doclist[0].docdt);
		}else{
			invoice.documentDate = '';
		}
	}
	if('Import Goods' == invoice.invtype && invoice.impGoods && invoice.impGoods.length > 0) {
		invoice.isSez = invoice.impGoods[0].isSez;
		invoice.stin = invoice.impGoods[0].stin;
		invoice.impBillNumber = invoice.impGoods[0].boeNum;
		if(invoice.impGoods[0].boeDt != null){
			invoice.impBillDate = formatDate(invoice.impGoods[0].boeDt);
		}else{
			invoice.impBillDate = '';
		}
		invoice.boeVal = invoice.impGoods[0].boeVal;
		invoice.impPortcode = invoice.impGoods[0].portCode;
	}
	if(invoice.advOriginalInvoiceNumber == null) {
		invoice.advOriginalInvoiceNumber = 0.00;
	}
	if(invoice.advPCustname == null) {
		invoice.advPCustname = "";
	}
	if(invoice.advPInvamt == null) {
		invoice.advPInvamt = 0.00;
	}
	if(invoice.advPIgstamt == null) {
		invoice.advPIgstamt = 0.00;
	}
	if(invoice.advPCgstamt == null) {
		invoice.advPCgstamt = 0.00;
	}
	if(invoice.advPSgstamt == null) {
		invoice.advPSgstamt = 0.00;
	}
	if(invoice.VehiclListDetails){
		if(VehiclListDetails.enteredDate != null){
			var eDate = new Date(VehiclListDetails.enteredDate);var day = eDate.getDate() + "";var month = (eDate.getMonth() + 1) + "";var year = eDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			VehiclListDetails.enteredDate = day + "/" + month + "/" + year;
		}else{invoice.enteredDate = "";}
		if(VehiclListDetails.transDocDate != null){
			var tdDate = new Date(VehiclListDetails.transDocDate);var day = tdDate.getDate() + "";var month = (tdDate.getMonth() + 1) + "";var year = tdDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			VehiclListDetails.transDocDate = day + "/" + month + "/" + year;
		}else{invoice.transDocDate = "";}
		invoice.VehiclListDetails.forEach(function(VehiclListDetails) {
			VehiclListDetails.enteredDate = VehiclListDetails.enteredDate;
			VehiclListDetails.transDocDate = VehiclListDetails.transDocDate;
			VehiclListDetails.vehicleNo = VehiclListDetails.vehicleNo;
			VehiclListDetails.fromPlace = VehiclListDetails.fromPlace;
			VehiclListDetails.fromState = VehiclListDetails.fromState;
			VehiclListDetails.tripshtNo = VehiclListDetails.tripshtNo;
			VehiclListDetails.userGSTINTransin = VehiclListDetails.userGSTINTransin;
			VehiclListDetails.transMode = VehiclListDetails.transMode;
		if(VehiclListDetails.transDocNo){VehiclListDetails.transDocNo = VehiclListDetails.transDocNo;}
		VehiclListDetails.groupNo = VehiclListDetails.groupNo;
		});
	}
	if(invoice.items) {
		invoice.items.forEach(function(item) {
			if(item.rate == null) {
				if(item.igstrate) {
					item.rate = item.igstrate;
				} else if(item.cgstrate) {
					item.rate = 2*item.cgstrate;
				}else{
					item.rate = 0;
				}
				
			}
			if(item.hsn) {
				item.code = item.hsn;
				if(item.hsn.indexOf(':') > 0) {
					item.hsn=item.hsn.substring(0,item.hsn.indexOf(' :'));
				}
			}
			if(item.igstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdna && invoice.cdna.length > 0 && invoice.cdna[0].nt && invoice.cdna[0].nt.length > 0)) {
						if('CDNA' == invoice.invtype){
							if(invoice.cdna[0].nt[0].ntty == 'D'){
								totalIGST += item.igstamount;
								totalIGST1 += item.igstamount;
							}else{
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}
						}else{
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalIGST += item.igstamount;
								totalIGST1 += item.igstamount;
							}else{
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}
						}
					}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
						if('CDNA' == invoice.invtype){
							if(invoice.cdnra[0].nt[0].ntty == 'C'){
								totalIGST -= item.igstamount;
								totalIGST1 -= item.igstamount;
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}
						}else{
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalIGST -= item.igstamount;
								totalIGST1 -= item.igstamount;
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}else{
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}
						}
					}else{
						totalIGST+=item.igstamount;
						totalIGST1 +=item.igstamount;
					}
				}
			} else {
				item.igstamount = 0.00;
			}
			if(item.cgstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdna && invoice.cdna.length > 0 && invoice.cdna[0].nt && invoice.cdna[0].nt.length > 0)) {
						if('CDNA' == invoice.invtype){
							if(invoice.cdna[0].nt[0].ntty == 'D'){
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}
						}else{
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}
						}
					}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
						if('CDNA' == invoice.invtype){
							if(invoice.cdnra[0].nt[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}
						}else{
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}
						}
					}else{
						totalCGST+=item.cgstamount;
						totalCGST1 +=item.cgstamount;
					}
				}
			} else {
				item.cgstamount = 0.00;
			}
			if(item.sgstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdna && invoice.cdna.length > 0 && invoice.cdna[0].nt && invoice.cdna[0].nt.length > 0)) {
							if('CDNA' == invoice.invtype){	
								if(invoice.cdna[0].nt[0].ntty == 'D'){
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}else{
									totalSGST-=item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}
							}else{
								if(invoice.cdn[0].nt[0].ntty == 'D'){
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}else{
									totalSGST-=item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}	
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
							if('CDNA' == invoice.invtype){
								if(invoice.cdnra[0].nt[0].ntty == 'C'){
									totalSGST -= item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}else{
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}
							}else{
								if(invoice.cdnr[0].nt[0].ntty == 'C'){
									totalSGST -= item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}else{
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalSGST-=item.sgstamount;
								totalSGST1 -=item.sgstamount;
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}
						}
						}else{
							totalSGST+=item.sgstamount;
							totalSGST1 +=item.sgstamount;
						}
				}
			} else {
				item.sgstamount = 0.00;
			}
			if(item.cessamount) {
				if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdna && invoice.cdna.length > 0 && invoice.cdna[0].nt && invoice.cdna[0].nt.length > 0)) {
							if('CDNA' == invoice.invtype){
								if(invoice.cdna[0].nt[0].ntty == 'D'){
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}
							}else{
								if(invoice.cdn[0].nt[0].ntty == 'D'){
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
							if('CDNA' == invoice.invtype){
								if(invoice.cdnra[0].nt[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}
							}else{
								if(invoice.cdnr[0].nt[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}
							}
						}else{
							totalCESS+=item.cessamount;
							totalCESS1 +=item.cessamount;
						}
				}
			} else {
				item.cessamount = 0.00;
			}
			if(item.exmepted != null && item.quantity != null){
				if(invoice.gstStatus != 'CANCELLED'){
					
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
						if('CDNA' == invoice.invtype){
							if(invoice.cdnra[0].nt[0].ntty == 'C'){
								totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
								totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							}else{
								totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
								totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							}
						}else{
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
								totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							}else{
								totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
								totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(invoice.cdnur[0].ntty == 'C'){
							totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
						}else{
							totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						}
					}else{
						totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
					}
					
					
				}
			}
			
			if(item.discount == null) {
				item.discount = 0.00;
			}
			if(item.advreceived == null) {
				item.advreceived = 0.00;
			}
			if(item.advReceiptNo == null){
				item.advReceiptNo = "";
			}
			if(item.advReceiptDate == null){
				item.advReceiptDate = "";
			}
			if(item.advStateName == null){
				item.advStateName = "";
			}
			if(item.advReceivedAmount == null){
				item.advReceivedAmount = 0.00;
			}
			if(item.advAdjustableAmount == null){
				item.advAdjustableAmount = 0.00;
			}
			if(item.advadjustedAmount == null){
				item.advadjustedAmount = 0.00;
			}
			if(item.igstavltax) {
				totalinvitc+= item.igstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCIGST+=item.igstavltax;
				totalITCIGST1 +=item.igstavltax;
				}
			} else {
				item.igstavltax = 0.00;
			}
			if(item.cgstavltax) {
				totalinvitc+= item.cgstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCCGST+=item.cgstavltax;
				totalITCCGST1 +=item.cgstavltax;
				}
			} else {
				item.cgstavltax = 0.00;
			}
			if(item.sgstavltax) {
				totalinvitc+= item.sgstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCSGST+=item.sgstavltax;
				totalITCSGST1 +=item.sgstavltax;
				}
			} else {
				item.sgstavltax = 0.00;
			}
			if(item.cessavltax) {
				totalinvitc+= item.cessavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCCESS+=item.cessavltax;
				totalITCCESS1 +=item.cessavltax;
				}
			} else {
				item.cessavltax = 0.00;
			}
			if(item.type) {
			} else {
				item.type = '';
			}
		});
	}
	invoice.totalitc = totalinvitc;
	if(invoice.totalitc) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(invoice.invtype != 'Advances' && invoice.invtype != 'Adv. Adjustments' && invoice.invtype != 'ISD' && invoice.invtype != 'ITC Reversal' && invoice.invtype != 'Nil Supplies'){
				totalITC+=invoice.totalitc;
			}
		}
	} else {
		invoice.totalitc = 0.00;
	}
	invoice.igstamount = totalIGST1;
	invoice.cgstamount = totalCGST1;
	invoice.sgstamount = totalSGST1;
	invoice.cessamount = totalCESS1;
	invoice.igstavltax = totalITCIGST1;
	invoice.cgstavltax = totalITCCGST1;
	invoice.sgstavltax = totalITCSGST1;
	invoice.cessavltax = totalITCCESS1;
	invoice.totalExempted = totalExempted; 
	return invoice;
}
function verifyReconsilation(id){
	$('#reconcileprocessModal').modal('show');
	$('#verificationInfo,#processInfo,#processPrInfo,#verificationPrInfo,#reconinitiatedby,.reconcompleted').text('');
	var fUrl = _getContextPath()+'/reconsilationverification/'+id;
	$.ajax({
		url: fUrl,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			$('.reconcompleted').text('');
			$('.reconcompleted').css("display","none");
			$('.reconprogress').css("display","block");
			if(response.monthlyoryearly != undefined){
				$('#gstr2areconrtntype').text(response.returntype);
				$('#reconinitiatedby').text(response.fullname);
				$('#verificationType').text(response.returntype+" "+response.monthlyoryearly);
				$('#verificationInfo').text(response.totalinvoices);
				$('#processInfo').text(response.totalgstr2ainvoices);
				if(response.totalprinvoices != ""){
					$('.prinfo').css("display","block");
					$('#processPrInfo').text(response.totalprprocessedinvoices);
					$('#verificationPrInfo').text(response.totalprinvoices);
				}
			}else{
				$('.reconcompleted').text('Reconciliation Completed');
				$('.reconcompleted').css("display","block");
				$('.reconprogress').css("display","none");
			}
			
		}
	});
}


var timer;
function executeQuery(id) {
	var fUrl = _getContextPath()+'/reconsilationcompleted/'+id;
	  $.ajax({
	    url: fUrl,
	    success: function(data) {
	      if(data == 'Completed'){
	    	  window.location.href = contextPath+'/alliview'+commonturnOverSuffix+'/GSTR2/'+Paymenturlprefix+'?type=mmtch';
	    	  $('.processverification,.rccn').addClass("d-none");
	    	  $('.reconcile_btn').removeClass("btn-loader");
	    	  $('.reconcile_btn').html("Reconcile Now");
	    	  $('.reconcile_btn').removeClass("d-none");
	    	  $('.prinfo').css("display","none");
	    	  clearTimeout(timer);
	      }
	    }
	  });
	  updateCall(id);
	}

	function updateCall(id){
		timer = setTimeout(function(){executeQuery(id)}, 1000);
	}
	
	
	function updateMainMisMatchSelection(chkBox) {
		selMatchArray = new Array();
		msgArray = new Array();
		
		var rowss = new Array();
		var check = $(chkBox).prop("checked");
		if(chkBox.checked) {
			invTableMismatch.rows().every(function () {
				var row = this.data();
				if(row.origin == "GSTR2A"){
					var invStatus = row.gstr2.matchingStatus;
					if(invStatus == '' || invStatus == null || invStatus == "null" || invStatus == "Not In Purchases"){
						msgArray.push(row.gstr2.userid);
						rowss.push(this.node());
						var mObj=new Object();
						mObj.gstrId = row.gstr2.userid;
						mObj.purchaseId = row.gstr2.matchingId;
						selMatchArray.push(mObj);
					}
				}
			});
		}else{
			msgArray = new Array();
			invTableMismatch.rows().every(function () {
				var row = this.data();
				if(row.origin == "GSTR2A"){
					var invStatus = row.gstr2.matchingStatus;
					if(invStatus == '' || invStatus == null || invStatus == "null" || invStatus == "Not In Purchases"){
						rowss.push(this.node());
					}
				}
			});
		}
		$('input[type="checkbox"]', rowss).prop('checked', check);
		if(chkBox.checked) {
			if(selMatchArray.length>0){
				$('#btnMisMatchAccept').removeClass('disable');
				$('#btnMisMatchReject').removeClass('disable');
			}else{
				$('#btnMisMatchAccept').addClass('disable');
				$('#btnMisMatchReject').addClass('disable');
			}
			
		} else {
			$('#btnMisMatchAccept').addClass('disable');
			$('#btnMisMatchReject').addClass('disable');
		}
		if(msgArray.length > 0){
			$('.select_msg').text('You have Selected '+msgArray.length+' Invoice(s)');
		}else{
			$('.select_msg').text('');
		}
	}
