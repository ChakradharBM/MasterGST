var apiUsagesExceedsTable = new Object();
var apiUsagesExceedsTableUrl = new Object();
function showComments(userid){
	$.ajax({
		url:_getContextPath()+"/getallcomments/"+userid+"?stage=",
		type:'GET',
		success : function(data) {
			$(".apiisexceedcommentsInfo").html("");
			$.each(data, function(key, commnt) {
				$(".apiisexceedcommentsInfo").append('<div class="leadscommentsTab mb-2" style="margin-right: 10px;"><strong><label class="label_txt">Added By : '+commnt.addedby+'</label></strong><strong><label style="float:right;">Date : '+commnt.commentDate+'</label></strong><br/>'+commnt.comments+'</div>');
			});
		}
	});
}

function loadApiUsageExceedsTable(tabRefName){
	if(apiUsagesExceedsTable[tabRefName]){
		apiUsagesExceedsTable[tabRefName].clear();
		apiUsagesExceedsTable[tabRefName].destroy();
	}
	var pUrl = _getContextPath()+"/getApiExceedsUsers?apis="+tabRefName+"&type=exceeds";
	
	apiUsagesExceedsTableUrl[tabRefName]=pUrl;
	apiUsagesExceedsTable[tabRefName] = $('#gstApiUsageExceedsDataTable_'+tabRefName).DataTable({
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
	        	 return resp.content;
	         }
	    },
	    "paging": true,
		"pageLength":10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		//"order": [[1,'desc']],
	    "columns": getApiUsagesExceedsColumns(tabRefName)
	});
	$('#gstApiUsageExceedsDataTable_'+tabRefName+' tbody').on('click', 'tr', function (e) {
		if (!$(e.target).closest('.nottoedit').length) {
			var dat = apiUsagesExceedsTable[tabRefName].row($(this)).data();
			getAllUsersDetails(dat.user.email);
		}
	});
}

function getApiUsagesExceedsColumns(tabName){
	var userid = {data:  function ( data, type, row ) {
		var usrid = data.userid ? data.userid : "";
			return '<span class="text-left invoiceclk">'+usrid+'</span>';
	}};
	var username = {data:  function ( data, type, row ) {
		var fullname = "";
		if(data.user !="" && data.user != null){
			fullname = data.user.fullname ? data.user.fullname : "";
		}
		return '<span class="text-left invoiceclk">'+fullname+'</span>';
	}};
	var email = {data:  function ( data, type, row ) {
		var mail = "";
		if(data.user !="" && data.user != null){
			mail = data.user.email;
		}
		return '<span class="text-left invoiceclk">'+mail+'</span>';
	}};
	var mobile = {data:  function ( data, type, row ) {
		var mobileno = ""
		if(data.user !="" && data.user != null){
			mobileno = data.user.mobilenumber;
		}
		return '<span class="text-left invoiceclk">'+mobileno+'</span>';
	}};
	var allowedInvoices = {data:  function ( data, type, row ) {
		var allowedInvs = data.allowedInvoices ? data.allowedInvoices : "";
			return '<span class="text-left invoiceclk">'+allowedInvs+'</span>';
	}};
	
	var processedInvoices = {data:  function ( data, type, row ) {
		var processedInvs = data.processedInvoices ? data.processedInvoices : "";
			return '<span class="text-left invoiceclk">'+processedInvs+'</span>';
	}};
	
	var exceedInvoices = {data:  function ( data, type, row ) {
		var exceedInvs = data.exceedInvoices ? data.exceedInvoices : 0;
		return '<span class="text-left invoiceclk">'+exceedInvs+'</span>';
	}};
	
	var createdDate = {data:  function ( data, type, row ) {
		var createdDt = data.registeredDate ? data.registeredDate : '';
		
		if(createdDt != ""){
			return '<span class="text-left invoiceclk">'+(new Date(createdDt)).toLocaleDateString('en-GB')+'</span>';
		}else{
			return '<span class="text-left invoiceclk"></span>';
		}
	}};
	
	var expiryDate = {data:  function ( data, type, row ) {
		var expiryDt = data.expiryDate ? data.expiryDate : '';
		if(expiryDt != ""){
			return '<span class="text-left invoiceclk">'+(new Date(expiryDt)).toLocaleDateString('en-GB')+'</span>';
		}else{
			return '<span class="text-left invoiceclk"></span>';
		}
	}};
	
	var commentsEdit = {data:  function ( data, type, row ) {
		return '<a href="#" onclick="sendRemindersExceedsInvs(\''+data.userid+'\')" class="btn btn-sm btn-blue-dark mr-3 nottoedit" data-toggle="modal" data-target="#exReminderModal" id="Rem_Btn" style="float:right;color:white;font-weight:100;">Remind</a><a href="#" class="nottoedit" id="" ><img style="width:26px;" data-toggle="modal" data-target="#ExpiryCommentsModal" class="cmntsimg nottoedit" onclick="showComments(\''+data.userid+'\')" src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a>';
    }};
	if(tabName == 'APIUSERS'){
		var apiname = {data:  function ( data, type, row ) {
			var apinme = data.apiType ? data.apiType : '';
			if(apinme != ""){
				return '<span class="text-left invoiceclk">'+apinme+'</span>';
			}else{
				return '<span class="text-left invoiceclk"></span>';
			}
		}};
		return [userid, username, email, mobile, apiname, createdDate, expiryDate, allowedInvoices, processedInvoices, exceedInvoices, commentsEdit];
	}else{
		var usrtype = {data:  function ( data, type, row ) {
			var type = ""
			if(data.user !="" && data.user != null){
				type = data.user.type;
			}
			return '<span class="text-left invoiceclk">'+type+'</span>';
		}};
		return [userid, username, email, mobile, usrtype, createdDate, expiryDate, allowedInvoices, processedInvoices, exceedInvoices, commentsEdit];
	}
}

function sendRemindersExceedsInvs(userid){
	$('#sendRemBtn').attr('onclick', "sendReminders(\'"+userid+"\')");
}

function sendReminders(userid){
	var commentsData=new Object();
	var subject = $("#remindersubject").val();
	var message = $("#remindermeassage").val();
	var flag = true;
	if(subject.trim() == "" || message.trim() == ""){
		$('#remindersErrorMsg').html('Reminders Subject or Message could not be empty');
		flag = false;
	}
	if(flag){
		var comments = subject+"#MGST#"+message;
		var createdate=new Date();
		var addedby= loginUser;
		$('#remindersErrorMsg').html('');
		commentsData.comments=comments;
		commentsData.commentDate=createdate;
		commentsData.addedby=addedby; 
		$('#sendRemBtn').addClass("btn-loader");
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : _getContextPath()+'/sendRemindercomments/'+userid+'?stage=',
			data : JSON.stringify(commentsData),
			dataType : 'json',
			success : function(data) {
				$("#remindersubject").val('');
				$("#remindermeassage").val('');
				$('#sendRemBtn').removeClass("btn-loader");
			},error:function(dat){
				$("#remindersubject").val('');
				$("#remindermeassage").val('');
				$('#sendRemBtn').removeClass("btn-loader");
			}
		});		
	}
}