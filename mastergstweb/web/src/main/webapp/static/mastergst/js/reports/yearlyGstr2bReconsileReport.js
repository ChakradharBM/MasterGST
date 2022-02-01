var g2bReconcileTable;
var g2bReconcileTableUrl;
var invSummaryTableg2bReconcileUrl = new Object();
var invTableMismatch;
var cpUsersResponse;
var isCpUsersResponseLoaded = false;
var mannualMatchtable;
var mannualViewMatchTable;
var mannualMatchTableUrl = new Object();
var totalTaxableValue = 0, totalIGST = 0, totalCGST = 0, totalSGST = 0, totalCESS = 0,  totalITCIGST = 0, totalITCCGST = 0, totalITCSGST = 0, totalITCCESS = 0,totalTax = 0, totalITC = 0, totalValue = 0, totalInvoices = 0, totalUploaded = 0, totalPending = 0, totalFailed = 0, totalExemptedValue = 0;var NotInJournals = 'NotInJournals';var hsnrowCount = 1;
var sendMsgCount=0;var invoiceArray=new Object(), msgArray = new Array(),irnCanArray=new Array(),gstMatchArray=new Array(),PIArray=new Array(),POArray=new Array(),supEmailids=new Array(),supCCEmailids=new Array(),gstnNotSelArray=new Array(),gstnArray=new Array(),sendMsgArray=new Array(),sendAllMsgsArray=new Array(), ewaybillArray = new Array(),vehicleUpdateArray = new Array(),vUpdateArray = new Array(),ITCclaimedArray = new Array(), selArray = new Object(), prchArray=new Array(), prchInvArray=new Array(), selInvArray=new Array(),Gstr2aArray=new Array(), MismatchArray=new Array(), selMatchArray=new Array(), mannualMatchArray = new Array(), mannuallMatchArray = new Array(),mannulaMatching = new Array(),mannuMatch = new Array(),mnmatch = new Array();var invoiceTable=new Object(), dbFilingTable, dbSendMsgTable,dbHSNFilingTable, dbDocIssueFilingTable, offLiabTable, purchaseTable,gstr2aTable,invTable,gstr2bMismatchTable,mannualMatchtable,mannualViewMatchTable,showInvTable,showInvPRTable;var ipAddress='',gstinnomatch = '', uploadResponse, gstSummary=null;

function loadYearlyGstr2bReconcileReportsUsersByClient(id, clientid, callback){
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
			//loadYearlyGstr2bReconcileReportsUsersInDropDown(response)
		}
	});
}

function loadYearlyGstr2bReconcileReportsUsersInDropDown(response){
	var usersMultiSelObj = $('#Gstr2bReconsilationMultiselect3')
	usersMultiSelObj.find('option').remove().end();
	usersMultiSelObj.append($("<option></option>").attr("value",globaluser).text(globaluser)); 
	if (response.length > 0) {				
		response.forEach(function(cp_user) {
			usersMultiSelObj.append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
		});
	}
	multiselrefreshGstr2bReconcile('#Gstr2bReconsilationMultiselect3', '- User -', varRetType);
	$('#Gstr2bReconsilationMultiselect3').multiselect('rebuild');
}

function loadYearlyGstr2bReconcileReportsInvoiceSupport(id, clientId, year, callback){
	var urlStr = _getContextPath()+'/getGstr2bReconsileInvsSupport/'+clientId+'/4/'+year+'?isYearly=Yearly';
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			callback(response);
			//loadYearlyGstr2bReconcileReportsCustomersInDropdown(response)
		}
	});
}

function loadYearlyGstr2bReconcileReportsCustomersInDropdown(response){
	var usersMultiSelObj = $('#Gstr2bReconsilationMultiselect4');
	usersMultiSelObj.find('option').remove().end();
	if (response.billedtoNames.length > 0) {				
		response.billedtoNames.forEach(function(customer) {
			if(customer != ''){
				usersMultiSelObj.append($("<option></option>").attr("value",customer).text(customer));
			} 
		});
	}
	var customerType ='- Suppliers -';
	multiselrefreshGstr2bReconcile('#Gstr2bReconsilationMultiselect4', customerType , varRetType);
	$('Gstr2bReconsilationMultiselect4').multiselect('rebuild');
}

function initiateCallBacksForMultiSelectGstr2bReconcile(){
	var multiSelDefaultVals = ['', '- Status -', '- Invoice Type -', '', '', '- Branches -', '- Verticals -'];	
	for(i = 1; i < 7; i++){
		if(i == 3 || i == 4){
			$('#Gstr2bReconsilationMultiselect'+i).hide();
			continue;
		}
		multiselrefreshGstr2bReconcile('#Gstr2bReconsilationMultiselect'+i, multiSelDefaultVals[i], varRetType);
	}
}
function multiselrefreshGstr2bReconcile(idval, descVal, varRetType){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyGstr2bReconcileFilters();
		},
		onSelectAll: function(){
			applyGstr2bReconcileFilters();
		},
		onDeselectAll: function(){
			applyGstr2bReconcileFilters();
		}
	});
}

function initializeRemoveAppliedFiltersGstr2bReconcile(){
	$('#divselGstr2bReconsilationInvArrayFilters').on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i=1;i<=6;i++){
			$('#Gstr2bReconsilationMultiselect'+i).multiselect('deselect', [val]);
		}
		applyGstr2bReconcileFilters();
	});
}

function applyGstr2bReconcileFilters() {
	var statusOptions = $('#Gstr2bReconsilationMultiselect1 option:selected');
	var typeOptions = $('#Gstr2bReconsilationMultiselect2 option:selected');
	var userOptions = $('#Gstr2bReconsilationMultiselect3 option:selected');
	var vendorOptions = $('#Gstr2bReconsilationMultiselect4 option:selected');
	var branchOptions = $('#Gstr2bReconsilationMultiselect5 option:selected');
	var verticalOptions = $('#Gstr2bReconsilationMultiselect6 option:selected');
	
	if(statusOptions.length > 0 || typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0){
		$('.gstr2bnormaltable .gstr2bfilter').css("display","block");
		$('.Gstr2bReconsilationfilter .dataTables_length').css("margin-left","0px");
	}else{
		$('.gstr2bnormaltable .gstr2bfilter').css("display","none");
		$('.Gstr2bReconsilationfilter .dataTables_length').css("margin-left","0px");
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
	$('#divselGstr2bReconsilationInvArrayFilters').html(filterContent);
	reloadGstr2bReconcileTable(statusArr, typeArr, userArr, vendorArr, branchArr, verticalArr);
}

function updateReconcileDetailsGSTR2B(value) {
	for(i=1;i<=6;i++){
		$('#Gstr2bReconsilationMultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#Gstr2bReconsilationMultiselect1').multiselect().find(':checkbox[value="'+value+'"]').attr('checked','checked');
	$('#Gstr2bReconsilationMultiselect1 option[value="'+value+'"]').attr("selected", 1);
	$('#Gstr2bReconsilationMultiselect1').multiselect('select',''+value+'').multiselect('updateButtonText');
	$('#Gstr2bReconsilationMultiselect1').multiselect('refresh');
	applyFiltersForMismatchTypeGSTR2B(value);
	$('#reconcile2bmodal').modal('hide');
}

function clearGstr2bReconsilationFilters(type) {
	$('.mannualMatching,.sendmessage').addClass('disable');
	$('#btnMisMatchAccept').addClass('disable');
	$('.Gstr2bReconsilationfilter .dataTables_length').css("margin-left","0px");
	for(i=1; i<7; i++){
		$('#Gstr2bReconsilationMultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');		
	}
	$('#divselGstr2bReconsilationInvArrayFilters').html('');
	$('.normaltable .filter').css("display","none");
	reloadGstr2bReconcileTable(new Array(), new Array(), new Array(),new Array(),new Array(),new Array());
}

function loadYearlyGstr2bReconcileReportsInvTable(id, clientid, year){
	var pUrl =_getContextPath()+'/reconsilegstr2binvs/'+id+'/'+clientid+'/4/'+year+'?returntype=GSTR2B&isYearly=Yearly';
	g2bReconcileTableUrl = pUrl;
	if(g2bReconcileTable){
		g2bReconcileTable.clear();
		g2bReconcileTable.destroy();
	}
	g2bReconcileTable = $('#gstr2bReconsileDbTable').DataTable({
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
	        	 loadGstr2bReconcileTotals(resp.amts);
	        	 return resp.mappedInvs;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[4,'desc']],
		'columns': getGstr2bReconcileInvColumns(clientId, year),
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

function getGstr2bReconcileInvColumns(clientId, year){
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
			if(data.origin == "GSTR2B"){
				return '<div class="checkbox nottoedit" index="'+data.purchaseRegister.docId+'"><label><input type="checkbox" id="invManFilter'+data.purchaseRegister.docId+'" onClick="updateGstr2bReconcileSelection(null, \''+data.purchaseRegister.docId+'\', \''+gstnnum+'\', this)"/><i class="helper"></i></label></div>';				
			}else{
				return '<div class="checkbox nottoedit" index="'+data.purchaseRegister.docId+'"><label><input type="checkbox" id="invManFilter'+data.purchaseRegister.docId+'" onClick="updateGstr2bReconcileSelection(\''+data.purchaseRegister.docId+'\', null, \''+gstnnum+'\', this)"/><i class="helper"></i></label></div>';				
			}
		}else if(data.origin == "GSTR2B"){
			if(data.gstr2b.b2b && data.gstr2b.b2b.length > 0){
				if(data.gstr2b.b2b[0].ctin != undefined){
					gstsnum = data.gstr2b.b2b[0].ctin;
				}
			}else if(data.gstr2b.cdn && data.gstr2b.cdn.length > 0){
				if(data.gstr2b.cdn[0].ctin != undefined){
					gstsnum = data.gstr2b.cdn[0].ctin;
				}
			}
			return '<div class="checkbox nottoedit" index="'+data.gstr2b.docId+'"><label><input type="checkbox" id="invManFilter'+data.gstr2b.docId+'" onClick="updateGstr2bReconcileSelection(null, \''+data.gstr2b.docId+'\', \''+gstsnum+'\', this)"/><i class="helper"></i></label></div>';
		}
    }};
	var info = {data: function ( data, type, row ) {
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		if(invStatus == 'Manual Matched'){
			return '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2B</span></div>';
		}else{
			return '<div style="color:#359045" onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><span class="f-11">BOOKS</span></div><div onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')" class="color-red tdline_2"><span class="f-11">GSTR2B</span></div>';			
		}
	 }};
	var itype = {data: function ( data, type, row ) {
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		/*if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				return '<span class="text-left invoiceclk">'+data.purchaseRegister.invtype+'</span>';
			}else{
				return '<span class="text-left invoiceclk">'+data.gstr2b.invtype+'</span>';
			}
		}else if(data.origin == "Purchase Register"){
			return '<span class="text-left invoiceclk" onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')">'+data.purchaseRegister.invtype+'</span>';						
		}else if(data.origin == "GSTR2B"){
			return '<span class="text-left invoiceclk" onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')">'+data.gstr2b.invtype+'</span>';			
		}*/
		
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
				if(data.gstr2b.cdn && data.gstr2b.cdn.length > 0){
					if(data.gstr2b.cdn[0].nt[0].ntty != undefined){
						ntType = data.gstr2b.cdn[0].nt[0].ntty;
						if(ntType  == "C"){
							data.gstr2b.invtype = "Credit Note";
						}else if(ntType  == "D"){
							data.gstr2b.invtype = "Debit Note";
						}else{
							data.gstr2b.invtype = "Credit/Debit Notes";
						}
					}
				}
				return '<span class="text-left invoiceclk">'+data.gstr2b.invtype+'</span>';
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
			return '<span class="text-left invoiceclk" onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')">'+data.purchaseRegister.invtype+'</span>';						
		}else if(data.origin == "GSTR2B"){
			if(data.gstr2b.cdn && data.gstr2b.cdn.length > 0){
				if(data.gstr2b.cdn[0].nt[0].ntty != undefined){
					ntType = data.gstr2b.cdn[0].nt[0].ntty;
					if(ntType  == "C"){
						data.gstr2b.invtype = "Credit Note";
					}else if(ntType  == "D"){
						data.gstr2b.invtype = "Debit Note";
					}else{
						data.gstr2b.invtype = "Credit/Debit Notes";
					}
				}
			}
			return '<span class="text-left invoiceclk" onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')">'+data.gstr2b.invtype+'</span>';			
		}
	 }};
	var billtoname = {data: function ( data, type, row ) {
		var billedtoname = '';
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		
		if(data.origin == "Purchase Register" && data.purchaseRegister.billedtoname != null){
			billedtoname = data.purchaseRegister.billedtoname;
		}else if(data.origin == "GSTR2B" && data.gstr2b.billedtoname != null){
			billedtoname = data.gstr2b.billedtoname;
			
		}
		
		if(billedtoname == "" && data.gstr2b != null && data.gstr2b.billedtoname != null){
			billedtoname = data.gstr2b.billedtoname;
		}
		
		if(invStatus == 'Manual Matched'){
			return '<span class="text-left invoiceclk">'+billedtoname+'</span>';			
		}else{
			return '<span class="text-left invoiceclk" onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')">'+billedtoname+'</span>';			
		}
	}};
	var fp = {data: function ( data, type, row ) {
		var rtp = '';
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.fp != undefined){
					rtp = data.purchaseRegister.fp;
				}
			}else{
				if(data.gstr2b.fp != undefined){
					rtp = data.gstr2b.fp;
				}
			}
			return '<div class="tdline_1">'+rtp+'</div><div class="tdline_2">-</div>'
		}else{
			if(data.origin == "Purchase Register" && data.gstr2b != null){
				if(data.purchaseRegister.fp != undefined){
					rtp = data.purchaseRegister.fp;
				}
				var gfp = '';
				if(data.gstr2b.fp != undefined){
					gfp = data.gstr2b.fp;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1">'+rtp+'</div><div class="tdline_2">'+gfp+'</div></span>';
			}else if(data.origin == "GSTR2B" && data.gstr2b != null){
				if(data.gstr2b.fp != undefined){
					rtp = data.gstr2b.fp;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+rtp+'</div></span>';
			}else{
				if(data.purchaseRegister.fp != undefined){
					rtp = data.purchaseRegister.fp;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">'+rtp+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var invsNo = {data:  function ( data, type, row ) {
		var invoiceno = '';
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.invoiceno != undefined){
					invoiceno = data.purchaseRegister.invoiceno;
				}
			}else{
				if(data.gstr2b.invoiceno != undefined){
					invoiceno = data.gstr2b.invoiceno;
				}
			}
			return '<div class="tdline_1">'+invoiceno+'</div><div class="tdline_2">-</div>';
		}else {
			if(data.origin == "Purchase Register" && data.gstr2b != null){
				if(data.purchaseRegister.invoiceno != undefined){
					invoiceno = data.purchaseRegister.invoiceno;
				}
				var invno = '';
				if(data.gstr2b.invoiceno != undefined){
					invno = data.gstr2b.invoiceno;
				}
				var colorclass = '';
				if(invStatus == 'Invoice No Mismatched' || invStatus == 'Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Probable Matched'){
					colorclass = roundOffClass;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+invoiceno+'</div><div class="tdline_2 '+colorclass+'">'+invno+'</div></span>';
			}else if(data.origin == "GSTR2B" && data.gstr2b != null){
				if(data.gstr2b.invoiceno != undefined){
					invno = data.gstr2b.invoiceno;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+invno+'</div></span>';
			}else{
				if(data.purchaseRegister.invoiceno != undefined){
					invoiceno = data.purchaseRegister.invoiceno;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">'+invoiceno+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var invDate = {data: function ( data, type, row ) {
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				return '<span><div class="tdline_1">'+(new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB")+'</div><div class="tdline_2">-</div></span>';
			}else{
				return '<span><div class="tdline_1">'+(new Date(data.gstr2b.dateofinvoice)).toLocaleDateString("en-GB")+'</div><div class="tdline_2">-</div></span>';
			}
		}else {
			if(data.origin == "Purchase Register" && data.gstr2b != null){
				var colorclass = '';
				if(invStatus == 'Invoice Date Mismatched' || invStatus == 'Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Round Off Matched'){
					if((new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB") != (new Date(data.gstr2b.dateofinvoice)).toLocaleDateString("en-GB")){
						colorclass = roundOffClass;
					}
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+(new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB")+'</div><div class="tdline_2 '+colorclass+'">'+(new Date(data.gstr2b.dateofinvoice)).toLocaleDateString("en-GB")+'</div></span>';
			}else if(data.origin == "GSTR2B" && data.gstr2b != null){
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+(new Date(data.gstr2b.dateofinvoice)).toLocaleDateString("en-GB")+'</div></span>';
			}else{
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">'+(new Date(data.purchaseRegister.dateofinvoice)).toLocaleDateString("en-GB")+'</div><div class="tdline_2 color-red">-</div></span>';
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
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
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
				docid = data.purchaseRegister.docId;
				return '<span class="" index="gstinno'+gstnnum+'"><div class="tdline_1">'+gstnnum+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal"  onClick="viewG2bMannualMatchedInvoices(\''+docid+'\',\'Purchase Register\')">Mannualy Matched with Multiple Invoices</a></div></span>';
			}else{
				if(data.gstr2b.b2b && data.gstr2b.b2b.length > 0){
					if(data.gstr2b.b2b[0].ctin != undefined){
						gstnnum = data.gstr2b.b2b[0].ctin;
					}
				}else if(data.gstr2b.cdn && data.gstr2b.cdn.length > 0){
					if(data.gstr2b.cdn[0].ctin != undefined){
						gstnnum = data.gstr2b.cdn[0].ctin;
					}
				}
				docid = data.gstr2b.docId;
				return '<span class="" index="gstinno'+gstnnum+'"><div class="tdline_1">'+gstnnum+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal"  onClick="viewG2bMannualMatchedInvoices(\''+docid+'\',\'GSTR2B\')">Mannualy Matched with Multiple Invoices</a></div></span>';
			}
		}else{
			if(data.origin == "Purchase Register" && data.gstr2b != null){
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
				if(data.gstr2b.b2b && data.gstr2b.b2b.length > 0){
					if(data.gstr2b.b2b[0].ctin != undefined){
						g2ctin = data.gstr2b.b2b[0].ctin;
					}
				}else if(data.gstr2b.cdn && data.gstr2b.cdn.length > 0){
					if(data.gstr2b.cdn[0].ctin != undefined){
						g2ctin = data.gstr2b.cdn[0].ctin
					}
				}
				var colorclass = '';
				if(invStatus == 'GST No Mismatched' || invStatus == 'Mismatched'){
					colorclass = redClass;
				}
				
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+gstnnum+'</div><div class="tdline_2 '+colorclass+'">'+g2ctin+'</div></span>';
			}else if(data.origin == "GSTR2B" && data.gstr2b != null){
				var g2ctin = '';
				if(data.gstr2b.b2b && data.gstr2b.b2b.length > 0){
					if(data.gstr2b.b2b[0].ctin != undefined){
						g2ctin = data.gstr2b.b2b[0].ctin;
					}
				}else if(data.gstr2b.cdn && data.gstr2b.cdn.length > 0){
					if(data.gstr2b.cdn[0].ctin != undefined){
						g2ctin = data.gstr2b.cdn[0].ctin;
					}
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2ctin+'</div></span>';
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
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">'+gstnnum+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}		
	}};
	var taxblamt = {data: function ( data, type, row ) {
		var totalTaxableAmt = 0.0;
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.totaltaxableamount){
					totalTaxableAmt = data.purchaseRegister.totaltaxableamount;
				}
			}else{
				if(data.gstr2b.totaltaxableamount){
					totalTaxableAmt = data.gstr2b.totaltaxableamount;
				}
			}
			return '<span><div class="tdline_1">'+formatNumber(totalTaxableAmt.toFixed(2))+'</div><div class="tdline_2">-</div></span>';			
		}else {
			if(data.origin == "Purchase Register" && data.gstr2b != null){
				if(data.purchaseRegister.totaltaxableamount){
					totalTaxableAmt = data.purchaseRegister.totaltaxableamount;
				}
				var gtotalTaxableAmt = 0.0;
				if(data.gstr2b.totaltaxableamount){
					gtotalTaxableAmt = data.gstr2b.totaltaxableamount;
				}
				var colorclass = '';
				if(invStatus == 'Tax Mismatched' || invStatus == 'Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Round Off Matched'){
					if(totalTaxableAmt != gtotalTaxableAmt){
						colorclass = roundOffClass;
					}
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+formatNumber(totalTaxableAmt.toFixed(2))+'</div><div class="tdline_2 '+colorclass+'">'+formatNumber(gtotalTaxableAmt.toFixed(2))+'</div></span>';
			}else if(data.origin == "GSTR2B" && data.gstr2b != null){
				if(data.gstr2b.totaltaxableamount){
					totalTaxableAmt = data.gstr2b.totaltaxableamount;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(totalTaxableAmt.toFixed(2))+'</div></span>';
			}else{
				if(data.purchaseRegister.totaltaxableamount){
					totalTaxableAmt = data.purchaseRegister.totaltaxableamount;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">'+formatNumber(totalTaxableAmt.toFixed(2))+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var totamt = {data: function ( data, type, row ) {
		var totalAmt = 0.0;
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.totalamount){
					totalAmt = data.purchaseRegister.totalamount;
				}
			}else{
				if(data.gstr2b.totalamount){
					totalAmt = data.gstr2b.totalamount;
				}
			}
			return '<span><div class="tdline_1">'+formatNumber(totalAmt.toFixed(2))+'</div><div class="tdline_2">-</div></span>';			
		}else {
			if(data.origin == "Purchase Register" && data.gstr2b != null){
				if(data.purchaseRegister.totalamount){
					totalAmt = data.purchaseRegister.totalamount;
				}
				var gtotalAmt = 0.0;
				if(data.gstr2b.totalamount){
					gtotalAmt = data.gstr2b.totalamount;
				}
				var colorclass = '';
				if(invStatus == 'Invoice Value Mismatched' || invStatus == 'Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Round Off Matched'){
					if(totalAmt != gtotalAmt){
						colorclass = roundOffClass;
					}
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+formatNumber(totalAmt.toFixed(2))+'</div><div class="tdline_2 '+colorclass+'">'+formatNumber(gtotalAmt.toFixed(2))+'</div></span>';
			}else if(data.origin == "GSTR2B" && data.gstr2b != null){
				if(data.gstr2b.totalamount){
					totalAmt = data.gstr2b.totalamount;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(totalAmt.toFixed(2))+'</div></span>';
			}else{
				if(data.purchaseRegister.totalamount){
					totalAmt = data.purchaseRegister.totalamount;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">'+formatNumber(totalAmt.toFixed(2))+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var totlTax = {data: function ( data, type, row ) {
		var totalTax = 0.0;
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}
		if(invStatus == 'Manual Matched'){
			if(data.origin == "Purchase Register"){
				if(data.purchaseRegister.totaltax){
					totalTax = data.purchaseRegister.totaltax;
				}
			}else{
				if(data.gstr2b.totaltax){
					totalTax = data.gstr2b.totaltax;
				}
			}
			return '<span><div class="tdline_1">'+formatNumber(totalTax.toFixed(2))+'</div><div class="tdline_2">-</div></span>';			
		}else{
			if(data.origin == "Purchase Register" && data.gstr2b != null){
				if(data.purchaseRegister.totaltax){
					totalTax = data.purchaseRegister.totaltax;
				}
				var gtotalTax = 0.0;
				if(data.gstr2b.totaltax){
					gtotalTax = data.gstr2b.totaltax;
				}
				var colorclass = '';
				if(invStatus == 'Tax Mismatched' || invStatus == 'Mismatched'){
					colorclass = redClass;
				}else if(invStatus == 'Round Off Matched'){
					colorclass = roundOffClass;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 '+colorclass+'">'+formatNumber(totalTax.toFixed(2))+'</div><div class="tdline_2 '+colorclass+'">'+formatNumber(gtotalTax.toFixed(2))+'</div></span>';
			}else if(data.origin == "GSTR2B" && data.gstr2b != null){
				if(data.gstr2b.totaltax){
					totalTax = data.gstr2b.totaltax;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(totalTax.toFixed(2))+'</div></span>';
			}else{
				if(data.purchaseRegister.totaltax){
					totalTax = data.purchaseRegister.totaltax;
				}
				return '<span onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')"><div class="tdline_1 color-red">'+formatNumber(totalTax.toFixed(2))+'</div><div class="tdline_2 color-red">-</div></span>';
			}
		}
	}};
	var notes = {data: function ( data, type, row ) {
		if(data.origin == "Purchase Register"){
			return '<div><a href="#" onclick="supComments(\''+data.purchaseRegister.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';			
		}else if(data.origin == "GSTR2B"){
			return '<div><a href="#" onclick="supComments(\''+data.gstr2b.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
		}
	}};
	var status = {data: function ( data, type, row ) {
		var invStatus = '';
		var docid='';
		if(data.origin == "Purchase Register"){
			invStatus = data.purchaseRegister.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In GSTR2B';
			}
			docid = data.purchaseRegister.docId;
		}else if(data.origin == "GSTR2B"){
			invStatus = data.gstr2b.gstr2bMatchingStatus;
			if(invStatus == '' || invStatus == null || invStatus == "null"){
				invStatus = 'Not In Purchases';
			}
			docid = data.gstr2b.docId;
		}else{
			invStatus = 'Not In Purchases';
		}
		if(invStatus == 'Manual Matched'){
			return '<span class="text-left invoiceclk bluetxt">'+invStatus+'</span>';			
		}else {
			return '<span class="text-left invoiceclk bluetxt" onClick="showReconsilationInv(\''+docid+'\',\''+clientId+'\',null,\''+invStatus+'\')">'+invStatus+'</span>';
		}
	}};
	return [chkBx, info, itype, billtoname, fp, invsNo, invDate, billtogtnn, totamt, taxblamt, totlTax, status, notes];
}

function reloadGstr2bReconcileTable(statusArr, typeArr, userArr, vendorArr, branchArr, verticalArr){
	var pUrl = g2bReconcileTableUrl;
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
	g2bReconcileTable.ajax.url(pUrl).load();
}

function updateReconcileDetailsGSTR2B(value) {
	applyFiltersForMismatchTypeGSTR2B(value);
	$('#reconcile2bmodal').modal('hide');
}

function applyFiltersForMismatchTypeGSTR2B(mismatchType) {
	for(i=1;i<7;i++){
		$('#Gstr2bReconsilationMultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#Gstr2bReconsilationMultiselect1').multiselect().find(':checkbox[value="'+mismatchType+'"]').attr('checked','checked');
	$('#Gstr2bReconsilationMultiselect1 option[value="'+mismatchType+'"]').attr("selected", 1);
	$('#Gstr2bReconsilationMultiselect1').multiselect('select',''+mismatchType+'').multiselect('updateButtonText');
	$('#Gstr2bReconsilationMultiselect1').multiselect('refresh');
	applyGstr2bReconcileFilters();
}

function loadYearlyGstr2bReconcileReportsReconcileSummary(id, clientid, year){
	var urlStr = _getContextPath()+'/getGstr2bReconcileSummary/'+clientid+'/4/'+year+'?isYearly=Yearly';
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			$('#RecMismatchedInvoices').html(response.MISMATCHED);
			$('#RecMatchedInvoices').html(response.MATCHED);
			$('#RecMatchedInvoicesInOtherMonths').html(response.MATCHED_IN_OTHER_MONTHS);
			$('#RecGSTnoMismatchedInvoices').html(response.GST_NO_MISMATCHED);
			$('#RecInvoiceValueMismatchedInvoices').html(response.INVOICE_VALUE_MISMATCHED);
			$('#RecTaxMismatchedInvoices').html(response.TAX_MISMATCHED);
			$('#RecInvoiceNoMismatchInvoices').html(response.INVOICE_NO_MISMATCHED);
			$('#RecRoundoffMismatchedInvoices').html(response.ROUNDOFF_MATCHED);
			$('#RecInvoiceDateMismatchedInvoices').html(response.INVOICE_DATE_MISMATCHED);
			$('#RecProbableMatchedInvoices').html(response.PROBABLE_MATCHED);
			$('#RecMannualMatchedInvoices').html(response.MANUAL_MATCHED);
			
			$('#RecNotInGstr2AInvoices').html(response.NOT_IN_PURCHASES);
			$('#RecNotInPurchasesInvoices').html(response.NOT_IN_GSTR2B);
			
		}
	});
}

function loadGstr2bReconcileTotals(totalsData){
	$('#idGstr2bReconsilationCount').html(totalsData ? totalsData.totalTransactions : '0');
	//$('#idGstr2bReconsilationTotAmtVal').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idGstr2bReconsilationTaxableVal').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxableAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idGstr2bReconsilationTaxVal').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalTaxAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idGstr2bReconsilationIGST').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalIGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idGstr2bReconsilationCGST').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idGstr2bReconsilationSGST').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalSGSTAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
	$('#idGstr2bReconsilationCESS').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalCESSAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
}

function updateGstr2bReconcileSelection(prid, g2id, gstin, chkBox) {
	var mObj = new Object();
	mObj.purchaseId = prid;
	mObj.gstrId = g2id;
	var id = prid;
	if(id == null){
		id = g2id;
	}
	if(chkBox.checked) {
		selG2bMatchArray.push(mObj);
		sendMsgArray.push(id);
		sendMsgCount++;
		if(gstMatchArray.length == 0){
			gstinnomatch = gstin;
			gstMatchArray.push(gstin);
			$('.sendmessage').removeClass('disable');
			$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2B\')');
		}else{
			if(jQuery.inArray(gstin, gstMatchArray ) == -1){
				gstnNotSelArray.push(gstin);
				$('.sendmessage').addClass('disable');
			}
		}
	} else {
		sendMsgCount--;
		var tArray=new Array();
		selG2bMatchArray.forEach(function(inv) {
			if(inv.purchaseId == prid && inv.gstrId == g2id) {
			} else {
				tArray.push(inv);
			}
		});
		selG2bMatchArray = tArray;
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
	if(selG2bMatchArray.length > 0) {
		$('#btnMisMatchAccept').removeClass('disable');
		$('#btnMisMatchReject').removeClass('disable');
		if(selG2bMatchArray.length == 1) {
			$('.g2bMannualMatching').removeClass('disable');
		}else{
			$('.g2bMannualMatching').addClass('disable');
		}
		$('.select_msg').text('You have Selected '+selG2bMatchArray.length+' Invoice(s)');
	} else {
		$('#btnMisMatchAccept').addClass('disable');
		$('#btnMisMatchReject').addClass('disable');
		$('.g2bMannualMatching').addClass('disable');
		$('.select_msg').text('');
		$('#checkMismatch').prop("checked",false);
	}
}

function invoiceviewByGstr2bReconcileTrDate(id,value,varRetType, varRetTypeCode, clientAddress) {
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
			initGstr2bReconcileTab();
		}
	});	
}

function verify2bReconsilation(id){
	$('#reconcile2bprocessModal').modal('show');
	$('#gstr2bverificationInfo,#gstr2bprocessInfo,#gstr2bprocessPrInfo,#gstr2bverificationPrInfo,#gstr2breconinitiatedby,.gstr2breconcompleted').text('');
	var fUrl = _getContextPath()+'/reconsilationverification/'+id;
	$.ajax({
		url: fUrl,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			$('#gstr2breconcompleted').text('');
			$('.gstr2breconcompleted').css("display","none");
			$('.gstr2breconprogress').css("display","block");
			if(response.monthlyoryearly != undefined){
				$('#gstr2breconrtntype').text(response.returntype);
				$('#gstr2breconinitiatedby').text(response.fullname);
				$('#gstr2bverificationType').text(response.returntype+" "+response.monthlyoryearly);
				$('#gstr2bverificationInfo').text(response.totalinvoices);
				$('#gstr2bprocessInfo').text(response.totalgstr2ainvoices);
				if(response.totalprinvoices != ""){
					$('.gstr2bprinfo').css("display","block");
					$('#gstr2bprocessPrInfo').text(response.totalprprocessedinvoices);
					$('#gstr2bverificationPrInfo').text(response.totalprinvoices);
				}
			}else{
				$('.gstr2breconcompleted').text('Reconciliation Completed');
				$('.gstr2breconcompleted').css("display","block");
				$('.gstr2breconprogress').css("display","none");
			}
		}
	});
}


var timer2b;
function execute2bQuery(id) {
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
	    	  clearTimeout(timer2b);
	      }
	    }
	  });
	  updateCall(id);
}

function updateCall(id){
	timer2b = setTimeout(function(){execute2bQuery(id)}, 1000);
}

function updateGstr2bMainMisMatchSelection(chkBox) {
	selG2bMatchArray = new Array();
	var rowss = new Array();
	var check = $(chkBox).prop("checked");
	if(chkBox.checked) {
		g2bReconcileTable.rows().every(function () {
			var row = this.data();
			if(row.origin == "GSTR2B"){
				var invStatus = row.gstr2b.gstr2bMatchingStatus;
				if(invStatus == '' || invStatus == null || invStatus == "null" || invStatus == "Not In Purchases"){
					rowss.push(this.node());
					var mObj=new Object();
					mObj.gstrId = row.gstr2b.docId;
					mObj.purchaseId = row.gstr2b.matchingId;
					selG2bMatchArray.push(mObj);
				}
			}
		});
	}else{
		g2bReconcileTable.rows().every(function () {
			var row = this.data();
			if(row.origin == "GSTR2B"){
				var invStatus = row.gstr2b.matchingStatus;
				if(invStatus == '' || invStatus == null || invStatus == "null" || invStatus == "Not In Purchases"){
					rowss.push(this.node());
				}
			}
		});
	}
	$('input[type="checkbox"]', rowss).prop('checked', check);
	if(chkBox.checked) {
		if(selG2bMatchArray.length>0){
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
	if(selG2bMatchArray.length > 0){
		$('.select_msg').text('You have Selected '+selG2bMatchArray.length+' Invoice(s)');
	}else{
		$('.select_msg').text('');
	}
}
function g2bMannualMatchingInv(clientid, monthlyOryearly){
	var returnType;
	var invoiceid;
	if(selG2bMatchArray[0].purchaseId){
		returnType = 'Purchase Register';
		invoiceid = selG2bMatchArray[0].purchaseId;
		$('#invhdr').html('Invoice In Purchases');
		$('#dinvhdr').html('Invoice In GSTR2B');
	}else{
		returnType = 'GSTR2B';
		invoiceid = selG2bMatchArray[0].gstrId;
		$('#invhdr').html('Invoice In GSTR2B');
		$('#dinvhdr').html('Invoice In Purchases');
	}
	if(mannualMatchtable){
		mannualMatchtable.clear();
		mannualMatchtable.destroy();
	}
	g2bManualMatchArr = new Array();
	$.ajax({
		url: contextPath+"/g2bMannualMatchingInvoice/"+invoiceid+"/"+returnType,
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
	/*if(returnType == 'GSTR2B'){
		returnType = 'Purchase Register';
	}else{
		returnType = 'GSTR2B';
	}*/
	var pUrl = contextPath+"/g2bInvoicesForMannualMatch/"+clientid+"/"+invoiceid+"/"+returnType+"/"+month+"/"+yer+"/"+monthlyOryearly;
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
		'columns': getG2bInvManualMatchColumns(returnType),
		'columnDefs' : getManualMatchInvColumnDefs()
	});
	$('#mannualHiddenInvoiceid').val(invoiceid);
	$('#mannualHiddenReturnType').val(returnType);
	$('#mannualMatch_table5_wrapper').css('width','100%');
	$('#message_send_btn').attr("onclick","updateG2bMannualMatchData(this,'yearly')");
}

function getG2bInvManualMatchColumns(retTyp){
	var chkBx = {data: function ( data, type, row ) {
		if(retTyp == 'GSTR2B'){
			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invManFilter'+data.docId+'" onClick="updateG2bMannulaMatchSelection(null,\''+data.docId+'\',this)"/><i class="helper"></i></label></div>';			
		}else{
			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invManFilter'+data.docId+'" onClick="updateG2bMannulaMatchSelection(\''+data.docId+'\', null,this)"/><i class="helper"></i></label></div>';
		}
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

function updateG2bMannualMatchData(btn,monthlyOrYearly) {
	var invoiceid = $('#mannualHiddenInvoiceid').val();
	var returnType = $('#mannualHiddenReturnType').val();
	$(btn).addClass('btn-loader');
	$.ajax({
		type: "POST",
		url: contextPath+"/g2bmannualMatchArray/"+returnType+"/"+invoiceid,
		async: false,
		cache: false,
		data: JSON.stringify(g2bManualMatchArr),
		contentType: 'application/json',
		success : function(response) {
			if(monthlyOrYearly == 'monthly'){
				window.location.href = contextPath+'/alliview'+commonturnOverSuffix+'/'+returnType+'/'+Paymenturlprefix+'?type=mmtch';
			}else{
				window.location.href = contextPath+'/reports'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=yearlyGstr2bRecocileReport';
			}
			
		},
		error : function(e, status, error) {if(e.responseText) {errorNotification(e.responseText);}}
	});
}

function updateG2bMannulaMatchSelection(prid, g2Id, chkBox) {
	$('#message_send_btn').removeAttr('onclick');	
	var mObj=new Object();
	mObj.purchaseId = prid;
	mObj.gstrId = g2Id;
	if(chkBox.checked) {
		g2bManualMatchArr.push(mObj);
	}
	$('#message_send_btn').attr('onclick',"updateG2bMannualMatchData(this,'yearly')");
}
function viewG2bMannualMatchedInvoices(invoiceid, returntype){
	if(returntype == 'Purchase Register'){
		$('#vinvhdr').html('Invoice In Purchases');
		$('#vdinvhdr').html('Invoice In GSTR2B');
	}else{
		$('#vinvhdr').html('Invoice In GSTR2B');
		$('#vdinvhdr').html('Invoice In Purchases');
		
	}
	var fUrl = contextPath+"/g2bMannualMatchingInvoice/"+invoiceid+"/"+returntype;
	$.ajax({
		url: fUrl,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			JSON.stringify(response);
			var invoice = updateInvoiceDetails(response);
			var maincontent = '<tr><td class="text-left">'+invoice.invoicetype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left">'+invoice.invoiceno+'</td><td class="text-left">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
				$('#mainvMatchInvoices').html(maincontent);
		},error:function(err){
			
		}
	});
	$.ajax({
		url: contextPath+"/viewMannualG2bMatchingInvoices/"+invoiceid+"/"+returntype,
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
