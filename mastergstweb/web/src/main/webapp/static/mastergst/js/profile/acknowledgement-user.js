var clientidsArray_pendingTab=new Array();
var clientidsArray_uploadTab=new Array();
var clientidsArray_reportsTab=new Array();
var invoiceArray = new Object();
var acknlTable = new Object();
var acknlTableUrl = new Object();
var credentialsTable = new Object();
$(function() {
	$('.acknowledgementhead').addClass('active');
		
	var date = new Date();
	var day = date.getDate();
	var month = date.getMonth()+1;
	var year = date.getFullYear();
	var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
	var customValue = day+ '-'+((''+month).length<2 ? '0' : '') + month + '-' + year;
	
	var date = $('#monthlyvalue_pendingTab').datepicker({
		autoclose: true,
		viewMode: 1,
		minViewMode: 1,
		format: 'mm-yyyy'
	}).on('changeDate', function(ev) {
		month = ev.date.getMonth()+1;year = ev.date.getFullYear();
		returntypeView('pendingTab');
	});
	var date = $('#monthlyvalue_uploadTab').datepicker({
		autoclose: true,
		viewMode: 1,
		minViewMode: 1,
		format: 'mm-yyyy'
	}).on('changeDate', function(ev) {
		month = ev.date.getMonth()+1;year = ev.date.getFullYear();
		returntypeView('uploadTab');
	});
	$('#fromvalue_pendingTab').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();mnt = ev.date.getMonth()+1;yr = ev.date.getFullYear();
		$('.fromtime_pendingTab').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		returntypeView('pendingTab');
	});
	$('#fromvalue_uploadTab').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();mnt = ev.date.getMonth()+1;yr = ev.date.getFullYear();
		$('.fromtime_uploadTab').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		returntypeView('uploadTab');
	});
	$('#tovalue_pendingTab').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();mnt = ev.date.getMonth()+1;yr = ev.date.getFullYear();
		$('.totime_pendingTab').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		returntypeView('pendingTab');
	});
	$('#tovalue_uploadTab').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();mnt = ev.date.getMonth()+1;yr = ev.date.getFullYear();
		$('.totime_uploadTab').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		returntypeView('uploadTab');
	});
	$('#monthlyvalue_pendingTab, #monthlyvalue_uploadTab').datepicker('update', dateValue);
	$('.time_pendingTab, .time_uploadTab').datepicker('update', customValue);
	
	$('.multeselectclient_pendingTab, .multeselectclient_uploadTab, .multeselectclient_reportsTab').multiselect({
		nonSelectedText: '- client/business Name -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {},
		onSelectAll: function() {},
		onDeselectAll: function() {}
	}).multiselect('selectAll', false)
	.multiselect('updateButtonText');
	var clientOptionspendingTab = $('.multeselectclient_pendingTab option:selected');
	if(clientOptionspendingTab.length > 0) {
	   	for(var i=0;i<clientOptionspendingTab.length;i++) {
	   		clientidsArray_pendingTab.push(clientOptionspendingTab[i].value);
		}
	}
	var clientOptionsuploadTab = $('.multeselectclient_uploadTab option:selected');
	if(clientOptionsuploadTab.length > 0) {
	   	for(var i=0;i<clientOptionsuploadTab.length;i++) {
	   		clientidsArray_uploadTab.push(clientOptionsuploadTab[i].value);
		}
	}
	var clientOptionsreportsTab = $('.multeselectclient_reportsTab option:selected');
	if(clientOptionsreportsTab.length > 0) {
	   	for(var i=0;i<clientOptionsreportsTab.length;i++) {
	   		clientidsArray_reportsTab.push(clientOptionsreportsTab[i].value);
		}
	}
	
	$('.multeselectclient_pendingTab').change(function(){
		clientidsArray_pendingTab =new Array();
		var clientOptions = $('.multeselectclient_pendingTab option:selected');
		if(clientOptions.length > 0) {
			for(var i=0;i<clientOptions.length;i++) {
				clientidsArray_pendingTab.push(clientOptions[i].value);	
			}
		}
		$('#clientErrMsg_pendingTab').css('display','none');
		if(clientidsArray_pendingTab.length == 0){
			$('#clientErrMsg_pendingTab').css('display','inline');
		}else{
			returntypeView('pendingTab');
		}
	});
	$('.multeselectclient_uploadTab').change(function(){
		clientidsArray_uploadTab =new Array();
		var clientOptions = $('.multeselectclient_uploadTab option:selected');
		if(clientOptions.length > 0) {
			for(var i=0;i<clientOptions.length;i++) {
				clientidsArray_uploadTab.push(clientOptions[i].value);	
			}
		}
		$('#clientErrMsg_uploadTab').css('display','none');
		if(clientidsArray_uploadTab.length == 0){
			$('#clientErrMsg_uploadTab').css('display','inline');
		}else{
			returntypeView('uploadTab');
		}
	});
	$('.multeselectclient_reportsTab').change(function(){
		clientidsArray_reportsTab =new Array();
		var clientOptions = $('.multeselectclient_reportsTab option:selected');
		if(clientOptions.length > 0) {
			for(var i=0;i<clientOptions.length;i++) {
				clientidsArray_reportsTab.push(clientOptions[i].value);	
			}
		}
		$('#clientErrMsg_reportsTab').css('display','none');
		if(clientidsArray_reportsTab.length == 0){
			$('#clientErrMsg_reportsTab').css('display','inline');
		}else{
			returntypeView('reportsTab');
		}
	});
});
function loadCredentialsTable(id, retType, month, year){
	var pUrl =contextPath+'/getStorageCredentials/'+id+'/'+retType+"/"+month+'/'+year;
	credentialsTable = $('#dbTableCredentials_savedCredentialsTab').DataTable({
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
	        	 $(".customtable div.toolbar").html('').append('<a href="#" class="btn btn-bluedark" id="configaws_btn" data-toggle="modal" data-target="#configureAwsS3Model">Add</a>');
	        	 return resp.content;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[0,'desc']],
		'columns': getCredentialsColumns(id, retType, month, year),
		
	});
	$('#credentialsBody').on('click','tr', function(e){
		if (!$(e.target).closest('.nottoedit').length) {
			var dat = credentialsTable.row($(this)).data();
			editCredentialsPopup(dat.userid);
		}

});
}
function editCredentialsPopup(credentailId){
	$('#configureAwsS3Model').modal("show");
	getCredentialsData(credentailId, function(credential) {
		 if (credential.userid == credentailId) {
			 $('#accessKey').val(credential.accessKey);
			 $('#accessSecret').val(credential.accessSecret);
			 $('#bucketName').val(credential.bucketName);
			 $('#regionName').val(credential.regionName);
			 $('#groupName').val(credential.groupName);
			 $('#credential_userid').val(credential.userid);
			 $("form[name='credentialsform']").append('<input type="hidden" name="id" value="'+credential.userid+'">');
		 }
	});
}
function getCredentialsData(credentailId, populateCredentialsData){
	var urlStr = _getContextPath()+'/getCredentials/'+credentailId;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			populateCredentialsData(response);
		}
	});
}
function getCredentialsColumns(id, retType, month, year){
	var clientname = {data:  function ( data, type, row ) {
		var clientName = data.clientName ? data.clientName : "";
		if(clientName.length > 25){
			clientName = clientName.substring(0,25)+"...";
		}
		return '<span class="text-left invoiceclk">'+clientName+'</span>';
		}};
	var accessKey = {data:  function ( data, type, row ) {
		var accesskey = data.accessKey ? data.accessKey : "";
		return '<span class="text-left invoiceclk">'+accesskey+'</span>';
		}};
	
	var accessSecret = {data:  function ( data, type, row ) {
		var accesssecret = data.accessSecret ? data.accessSecret : "";
		return '<span class="text-left invoiceclk">'+accesssecret+'</span>';
		}};
	
	var bucketName = {data:  function ( data, type, row ) {
		var bucketname = data.bucketName ? data.bucketName : "";
		return '<span class="text-left invoiceclk">'+bucketname+'</span>';
		}};
	
	var regionName = {data:  function ( data, type, row ) {
		var regionname = data.regionName ? data.regionName : "";
		return '<span class="text-left invoiceclk">'+regionname+'</span>';
		}};
	
	var groupName = {data:  function ( data, type, row ) {
		var groupname = data.groupName ? data.groupName : "";
		return '<span class="text-left invoiceclk">'+groupname+'</span>';
		}};
	
	return [clientname, accessKey , accessSecret,bucketName, regionName,groupName,
        {data: function ( data, type, row ) {
     		 return '<a class="btn-edt" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a>';
            }} 
        ];
}
function loadAcknowledgemtUserInvoices(id, retType, month, year, pendingOrUpload, rtype, tabRefName){
	var contextPath=_getContextPath();
	var clientidsArray=new Array();
	var xlstype= "pending"
	if(tabRefName == 'uploadTab'){
		clientidsArray=clientidsArray_uploadTab;
		xlstype="upload"
	}else{
		clientidsArray=clientidsArray_pendingTab;
	}
	var pUrl =contextPath+'/getUsersAcknldgeInvs/'+id+'/'+retType+"/"+month+'/'+year+'?pendingOrUpload='+pendingOrUpload+'&clientids='+clientidsArray;		
	var dUrl =contextPath+'/getXlsUsersAcknldgeInvs/'+id+'/'+retType+"/"+month+'/'+year+'?pendingOrUpload='+pendingOrUpload+'&clientids='+clientidsArray;
	if(rtype == 'custom' || rtype == 'Custom' ){
		//month eq fromtime & year eq totime
		pUrl =contextPath+'/getUsersCustomAcknldgeInvs/'+id+'/'+retType+"/"+month+'/'+year+'?pendingOrUpload='+pendingOrUpload+'&clientids='+clientidsArray;
		dUrl =contextPath+'/getXlsUsersCustomAcknldgeInvs/'+id+'/'+retType+"/"+month+'/'+year+'?pendingOrUpload='+pendingOrUpload+'&clientids='+clientidsArray;
	}
	if(acknlTable[tabRefName]){
		acknlTable[tabRefName].clear();
		acknlTable[tabRefName].destroy();
	}
	//acknlTableUrl[pendingOrUpload] = pUrl;
	acknlTable[tabRefName] = $('#dbTableAcknowlegement_'+tabRefName).DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 $("#dbTableAcknowlegement_"+tabRefName+"_wrapper div.toolbar").html('<a href="'+dUrl+'" class="btn btn-blue mr-2">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
	        	 resp.recordsTotal = resp.totalElements;
	        	 resp.recordsFiltered = resp.totalElements;
	        	 return resp.content;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		'columns': getInvColumns(id, retType, month, year, pendingOrUpload,tabRefName),
		'columnDefs': [
			{
				"targets":  [4, 5],
				className: 'dt-body-right'
			}
		],
		'initComplete' : function(){if(tabRefName == 'pendingTab'){filedrga(id, contextPath);}}
	});
	
}

function filechoose(tabRefName,id,contextPath){
	if(tabRefName == 'pendingTab'){filedrga(id, contextPath);}
}

function getInvColumns(id, varRetType, month, year, pendingOrUpload, tabRefName){
	var contextPath=_getContextPath();
	var varRetTypeCode = varRetType.replace(" ", "_");
	var invsNo = {data:  function ( data, type, row ) {
					var invoiceno = '';
					if(data.invoiceno != undefined){
						invoiceno = data.invoiceno;
					}
			      	 return '<span class="text-left">'+invoiceno+'</span>';
			    }};
	var invDate = {data: function ( data, type, row ) {
		var dateofinvoice = "";
		
		return '<span class="text-left">'+(new Date(data.dateofinvoice)).toLocaleDateString("en-GB")+'</span>';
	}};
	var taxblamt = {data: function ( data, type, row ) {
		var totalTaxableAmt = 0.0;
		if(data.totaltaxableamount){
			totalTaxableAmt = data.totaltaxableamount;
		}
	   	 return '<span class="ind_formats text-right">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
	    }};
	var totalamt = {data: function ( data, type, row ) {
		var totalAmount = 0.0;
		if(data.totalamount){
			totalAmount = data.totalamount;
		}
	   	 return '<span class="ind_formats text-right">'+formatNumber(totalAmount.toFixed(2))+'</span>';
    }};
	var billtoname = {data: function ( data, type, row ) {
						var billedtoname = '';
						if(data.billedtoname != undefined){
							billedtoname = data.billedtoname;
						}
				      	 return '<span class="text-left">'+billedtoname+'</span>';
				    }};
	var customerid = {data: function ( data, type, row ) {
		var custmerid = "";
		if(data.invoiceCustomerId){
			custmerid = data.invoiceCustomerId;
		}
	   	 return '<span class="ind_formats text-left">'+custmerid+'</span>';
	}};
	
	
	if(tabRefName == 'uploadTab'){
		var s3Date = {data: function ( data, type, row ) {
			return new Date(data.s3attachementDate).toLocaleDateString("en-GB");
		}};
		var dayoftaken = {data: function ( data, type, row ) {
        	var getDates = diffDays(new Date(data.dateofinvoice), new Date(data.s3attachementDate));
        	return '<div class="text-center"><span id="'+data.userid+'">'+getDates+'</span></div>';
        }};
        var dayofsubmission = {data: function ( data, type, row ) {
        	var invSubmsionDate = new Date(data.s3attachementDate);
        	
        	var day = invSubmsionDate.getDate() + "";var month = (invSubmsionDate.getMonth() + 1) + "";	var year = invSubmsionDate.getFullYear() + "";
        	var invsdate= day+"-"+month+"-"+year;
        	return '<span class="td_calender"><input type="text" class="form-control ml-1 mr-1 dofsub_value_'+data.userid+'" onclick="dofsubpicker(\''+data.userid+'\')" id="dofsub_value1" value="'+invsdate+'" style="padding: 3px;width: 95px;display: inline-block;padding: 4px 10px;padding-left: 2px;"><i class="fa fa-calendar" aria-hidden="true" style="position: absolute;margin-left: -20px;margin-top: 7px;" onclick="dofsubpicker(\''+data.userid+'\')"></i></span>';
        }};
        var attachmentsview ={data: function ( data, type, row ) {
           	return '<a href="#" class="pull-right"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" data-toggle="modal" data-invId="'+data.invoiceno+'" data-tabname="'+tabRefName+'" onclick="deleteAttModel(\''+data.invoiceno+'\',\''+tabRefName+'\', \''+data.userid+'\')" style="margin-top: -6px; margin-left:5px;"></a><span class="pull-right"><a href="#" onclick="viewInvoiceAttachment(\''+data.invoiceno+'\', \''+tabRefName+'\', \''+data.userid+'\')" >View</a></span>';
        }};
		return [invsNo, invDate, billtoname, customerid, taxblamt, totalamt, dayofsubmission, dayoftaken, attachmentsview];
	}else{
		var attachment = {data: function ( data, type, row ) {
			return '<fieldset style="display:inline-block;width: 100%;text-align: center;"><div><label for="fileselect_'+data.invoiceno+'" class="f-12 btn btn-blue-dark" style="padding: 4px 10px 4px 10px;font-size:11px">Attach Files</label><span style="display:none"><input type="file" class="fileselect" id="fileselect_'+data.invoiceno+'" data-id = "'+data.userid+'"data-tabname="'+tabRefName+'" name="fileselect_'+data.invoiceno+'" /></span></div></fieldset>';
		}};
		
		return [invsNo, invDate, billtoname, customerid, taxblamt, totalamt, attachment];		
	}
}
function deleteAttModel(invNo, tabName,invoiceId){
	$('#deleteModal #invoiceNo').val(invNo);
	$('#deleteModal #invoiceId').val(invoiceId);
	$('#deleteModal #tabName').val(tabName);
	$('#deleteModal #scanner').html("");
	$('#deleteModal').modal('show');
}
function dofsubpicker(invid){
	$('.dofsub_value_'+invid).datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();mnt = ev.date.getMonth()+1;yr = ev.date.getFullYear();
		var updatedDate= ((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
		$('.dofsub_value').val(updatedDate);
		$('#ackprogress-bar_uploadTab').removeClass('d-none');
		updateinvTakenDays(invid,updatedDate);
	});
	$('.dofsub_value_'+invid).datepicker('show');
}

function updateinvTakenDays(invid,updatedDate){
	var contextPath=_getContextPath();
	$.ajax({
		url: contextPath+"/updateacknwldgmntsubmissiondate/"+invid+"/"+updatedDate,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		//beforeSend: function () {$('#monthProcess').text('Processing...');},
		success : function(invoice) {
			$('#'+invid).html(diffDays(new Date(invoice.dateofinvoice), new Date(invoice.s3attachementDate)));
			//successNotification('Successfully <b>'+invoice.invoiceno+' </b> Update Date of Submission');
			$('#ackprogress-bar_uploadTab').addClass('d-none');
		},error: function(error){
			console.log(error+"error");
			$('#ackprogress-bar_uploadTab').addClass('d-none');
		}
	});
}
function diffDays(invdate, s3attdate) {
	var oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds
    // Take the difference between the dates and divide by milliseconds per day.
    // Round to nearest whole number to deal with DST.
	
    return Math.round(Math.abs(invdate - s3attdate) / oneDay);
}

function filedrga(userid, contextPath){
	this.contextPath = contextPath;
	this.userid = userid;
	
	// getElementById
	function $id(id) {
		return document.getElementById(id);
	}
	// output information
	function Output(msg) {
		var m = $id("filemessages");
		m.innerHTML = msg + m.innerHTML;
	}
	// file drag hover
	function FileDragHover(e) {	
		e.stopPropagation();
		e.preventDefault();
		e.target.className = (e.type == "dragover" ? "hover" : "");
	}
	// file selection
	function FileSelectHandler(e) {
		$("#attachModal").modal("show");
		$('.modal-backdrop').removeClass("modal-backdrop");
		// cancel event and hover styling
		FileDragHover(e);
		var eleId = $(this).attr('id');
		var tabName = $(this).data('tabname');
		var invoiceid = $(this).data('id');
		var invno = eleId.substr(11);
		// fetch FileList object
		var files = e.target.files || e.dataTransfer.files;
		// process all File objects
		for (var i = 0, f; f = files[i]; i++) {
			ParseFile(f);
			UploadFile(f, invno, tabName,invoiceid);
			break;
		}
	}
	// output file information
	function ParseFile(file) {
		var filename='';
		if(file.name.length >= 32){
			filename = file.name.substr(0, 25)+"....   ." +file.name.substr(file.name.lastIndexOf('.') + 1);
		}else{
			filename =file.name;
		}
		Output(
			"<p style='border-bottom:1px solid lightgray;padding:15px;margin-bottom: 0;'><strong>" + filename+
			"</strong> type: <strong>" + file.type +
			"</strong> &nbsp;&nbsp;&nbsp;&nbsp; size: <strong>" + file.size +
			"</strong> bytes<span class='loadercls' style='background-color:white;font-size: 22px;margin-left: 15px;float:right'><img src='"+contextPath+"/static/mastergst/images/master/loader-red.gif' style='width: 25px;float: right;margin-top: 5px;'/></span></p>"
		);
		//setTimeout(function(){
		 //   $('.loadercls').html('').append('<i class="fa fa-check-circle mr-2" style="color: green;" aria-hidden="true"></i><i class="fa fa-trash" aria-hidden="true"></i>')
		//},5000);
	}	
	function UploadFile(file, invNo, tabName,invoiceid) {
		var xhr = new XMLHttpRequest();
		if (file.size <= $id("MAX_FILE_SIZE").value) {
			// create progress bar
			//var o = $id("progress");
			//var progress = o.appendChild(document.createElement("p"));
			//progress.appendChild(document.createTextNode("upload " + file.name));
			// progress bar
			xhr.upload.addEventListener("progress", function(e) {
				var pc = parseInt(100 - (e.loaded / e.total * 100));
				//progress.style.backgroundPosition = pc + "% 0";
			}, false);
			// file received/failed
			xhr.onreadystatechange = function(e) {
				if (xhr.readyState == 4) {
					//progress.className = (xhr.status == 200 ? "success" : "failure");
					 $('.loadercls').html('').append('<i class="fa fa-check-circle mr-2" style="color: green;" aria-hidden="true"></i>');//<i class="fa fa-trash" aria-hidden="true"></i>
					acknlTable[tabName].ajax.reload(reloadCall, false);
				}
			};
			var formData = new FormData(document.getElementById('AttachmentForm'));
			formData.append('invoiceNo',invNo);
			formData.append('clientId',$('#multeselectclient_'+tabName).val());
			formData.append('invoiceId',invoiceid);
			formData.append('file',file);
			// start upload
			xhr.open("POST", this.contextPath+'/uploadinvoiceattachment', true);
			xhr.setRequestHeader("X_FILENAME", file.name);
			xhr.send(formData);
		}
	}
	function reloadCall(){
		filedrga(this.userid, this.contextPath);
	}
	// initialize
	function Init() {	
		var fileselect = $id("fileselect"),
			filedrag = $id("filedrag"),
			submitbutton = $id("submitbutton");
		// file select
		//fileselect.addEventListener("change", FileSelectHandler, false);
		if(document.querySelector('.fileselect')){
			var deleteLink = document.querySelectorAll('.fileselect');
			for (var i = 0; i < deleteLink.length; i++) {
			    deleteLink[i].addEventListener('change', FileSelectHandler, false);
			}
			//document.querySelector('.fileselect').addEventListener("click", FileSelectHandler, false);
		}
		/*if(document.querySelector('.fileselect')){
			document.querySelector('.fileselect').addEventListener("change", FileSelectHandler, false);
		}*/
		// is XHR2 available?
		var xhr = new XMLHttpRequest();
		if (filedrag && xhr.upload) {
			// file drop
			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragHover, false);
			filedrag.addEventListener("drop", FileSelectHandler, false);
			filedrag.style.display = "block";
			// remove submit button
			submitbutton.style.display = "none";
		}
	}
	
	// call initialization file
	if (window.File && window.FileList && window.FileReader) {
		Init();
	}
}

function deleteInvoiceAttachment(){
	var tabName = $('#deleteModal #tabName').val();
	var formData = new FormData();
	formData.append('returnType','Sales');
	formData.append('invoiceNo',$('#deleteModal #invoiceNo').val());
	formData.append('invoiceId',$('#deleteModal #invoiceId').val());
	formData.append('clientId', $('#multeselectclient_'+tabName).val()[0]);
	var contextPath=_getContextPath();
	$('#deleteModal #scanner').html("<span class='loadercls'><img src='"+contextPath+"/static/mastergst/images/master/loader-red.gif' style='width: 25px;float: right;margin-top: 5px;'/></span>");
	$.ajax({
		url: contextPath+'/deleteinvoiceattachment?_='+Math.random(),
		data: formData,
		type:'POST',
		cache: false,
	    contentType: false,
	    processData: false,
		success : function(response) {
			acknlTable[tabName].ajax.reload();
			$('#deleteModal #scanner').html("");
			$('#deleteModal .closemdl').click();
		}, error: function(error){
			$('#deleteModal #scanner').html("");
			$('#deleteModal .closemdl').click();
		}
	});
}

function viewInvoiceAttachment(invNo, tabName,invoiceId){
	var clientId = $('#multeselectclient_'+tabName).val()[0];
	var url = _getContextPath()+'/viewinvoiceattachment?returnType=Sales&clientId='+clientId+'&invoiceNo='+invNo+'&invoiceId='+invoiceId;
	window.open(url, 'Attachment for Invoice No:'+invNo, "width=600,height=500");
}

