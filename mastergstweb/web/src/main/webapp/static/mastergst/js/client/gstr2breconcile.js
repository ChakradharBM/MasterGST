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

function loadGstr2bReconcileUsersByClient(id, clientid, callback){
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

function loadGstr2bReconcileUsersInDropDown(response){
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

function loadGstr2bReconcileInvoiceSupport(id, clientId, month, year, callback){
	var urlStr = _getContextPath()+'/getGstr2bReconsileInvsSupport/'+clientId+'/'+month+'/'+year;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			callback(response);
			//loadGstr2bReconcileCustomersInDropdown(response)
		}
	});
}

function loadGstr2bReconcileCustomersInDropdown(response){
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

function loadGstr2bReconcileInvTable(id, clientid, month, year){
	var pUrl =_getContextPath()+'/reconsilegstr2binvs/'+id+'/'+clientid+'/'+month+'/'+year+'?returntype=GSTR2B';
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

function loadGstr2bReconcileSummary(id, clientid, month, year){
	var urlStr = _getContextPath()+'/getGstr2bReconcileSummary/'+clientid+'/'+month+'/'+year;
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
	$('#idGstr2bReconsilationTotAmtVal').html(totalsData ? '<i class="fa fa-rupee"></i>'+formatNumber(totalsData.totalAmount.toFixed(2)) : '<i class="fa fa-rupee"></i>0.00');
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
				$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2B\')');
			}
		}else{
			if(sendMsgCount == 1){
				if(gstnNotSelArray.length > 0){
					gstMatchArray = gstnNotSelArray;
				}
					$('.sendmessage').removeClass('disable');
					$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2B\')');
			}else{
				if(gstnNotSelArray.length > 0){
					$('.sendmessage').addClass('disable');
				}else{
					$('.sendmessage').removeClass('disable');
					$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2B\')');
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
	    	  window.location.href = contextPath+'/alliview'+commonturnOverSuffix+'/GSTR2/'+Paymenturlprefix+'?type=gstr2breconcile';
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
	msgArray = new Array();
	
	var rowss = new Array();
	var check = $(chkBox).prop("checked");
	if(chkBox.checked) {
		g2bReconcileTable.rows().every(function () {
			var row = this.data();
			if(row.origin == "GSTR2B"){
				var invStatus = row.gstr2b.gstr2bMatchingStatus;
				if(invStatus == '' || invStatus == null || invStatus == "null" || invStatus == "Not In Purchases"){
					msgArray.push(row.gstr2b.docId);
					rowss.push(this.node());
					var mObj=new Object();
					mObj.gstrId = row.gstr2b.docId;
					mObj.purchaseId = row.gstr2b.matchingId;
					selG2bMatchArray.push(mObj);
				}
			}
		});
	}else{
		msgArray = new Array();
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
