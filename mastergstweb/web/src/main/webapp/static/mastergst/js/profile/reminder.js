var remindersTable = new Object();
var remindersTableUrl = new Object();
var reminderArray = new Array();
var clientsArray = new Array();
var emailArray = new Array();
var mobileArray = new Array();
var ccMailArray = new Array();
var allCCmailArray = new Array();
function loadRemindersData(id, tabRefName, usrId, usrname, usremail, usrmobile, usrDetails){
	var contextPath=_getContextPath();
	var type="clients";
	if(tabRefName == "clientstab"){
		type="clients";
	}else if(tabRefName == "customerstab"){
		type="customers";
	}else if(tabRefName == "supplierstab"){
		type="suppliers";
	}
	reminderArray[tabRefName] = new Array();
	var pUrl =contextPath+'/cp_remindersdata/'+id+'?type='+type;	
	remindersTableUrl[tabRefName]=pUrl;
	if(tabRefName != 'clientstab'){
		pUrl += '&clientids='+clientsArray[tabRefName];		
	}
	if(remindersTable[tabRefName]){
		remindersTable[tabRefName].clear();
		remindersTable[tabRefName].destroy();
	}
	remindersTable[tabRefName] = $('#dbTableReminders_'+tabRefName).DataTable({
		//"dom": '<"toolbar">frtip',
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	$("#dbTableReminders_"+tabRefName+"_wrapper div.toolbar").html('<a href="#" class="mr-2 btn btn-blue-dark disable Rem_Btn_'+tabRefName+'" id="Rem_Btn" onclick="AllSendReminderModal(\'' + usrname + '\',\'' + usremail + '\',\'' + usrmobile + '\',\'' +tabRefName+ '\')" style="float:right;color:white;font-weight:100;">Send Reminders</a><a href="#" id="addSignLink" class="pr-2 addSignLink_'+tabRefName+'" onclick="ConfigureUserDetails(\'' + usrId + '\',\'' + usrname + '\',\'' + usremail + '\',\'' + usrmobile + '\')">Add Signature</a>');
	        	if(usrDetails == "") {
	        		$('.addSignLink_'+tabRefName).html("Add Signature");
	     		} else {
	     			$('.addSignLink_'+tabRefName).html("Edit Signature");
	     		}
	        	if(tabRefName == "clientstab"){
	        		 resp.recordsTotal = resp.clientstab.totalElements;
	        		 resp.recordsFiltered = resp.clientstab.totalElements;
	        		 return resp.clientstab.content ;	        		 
	        	}else if(tabRefName == "customerstab"){
	        		 resp.recordsTotal = resp.customerstab.totalElements;
	        		 resp.recordsFiltered = resp.customerstab.totalElements;
	        		 return resp.customerstab.content ;
	        	}else if(tabRefName == "supplierstab"){
	        		 resp.recordsTotal = resp.supplierstab.totalElements;
	        		 resp.recordsFiltered = resp.supplierstab.totalElements;
	        		 return resp.supplierstab.content ;
	        	} 
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		//"order": [[2,'desc']],
		//"ordering": true,
		'columnDefs': getColumnsDefs(tabRefName),
		'columns': getRemindersColumns(id, tabRefName, usrId, usrname, usremail, usrmobile,usrDetails)
	});
}
function getColumnsDefs(tabRefName) {
	if(tabRefName == 'clientstab' || tabRefName == "customerstab" || tabRefName == "supplierstab"){
		return  [
					{"targets": [7],className: 'dt-body-center'},
					{
						"targets": 0,
						"orderable": false
					}
					
				];
	}else{
		return  [
					{"targets": [6],className: 'dt-body-center'},
					{
						"targets": 0,
						"orderable": false
					}
					
				];
	}
}
function getRemindersColumns(id, tabRefName, usrId, usrname, usremail, usrmobile, usrDetails){
	var contextPath=_getContextPath();		
	
	var cemail = {data: function ( data, type, row ) {
		var email = "";
		if(data.email != undefined){
			email = data.email;
		}return '<span class="text-left">'+email+'</span>';
	}};
	var gstno = {data: function ( data, type, row ) {
		var gstnnumber = "";
		if(data.gstnnumber != undefined){
			gstnnumber = data.gstnnumber;
		}return '<span class="text-left">'+gstnnumber+'</span>';
	}};
	var mobileno = {data: function ( data, type, row ) {
		var mobilenumber = "";
		if(data.mobilenumber != undefined){
			mobilenumber = data.mobilenumber;
		}return '<span class="text-left">'+mobilenumber+'</span>';
	}};
	
	var docid = {data: function ( data, type, row ) {
		var docId = "";
		if(data.docId != undefined){
			docId = data.docId;
		}return '<span class="text-left">'+docId+'</span>';
	}};
	
	var chckbox = {data: function (data, type, row) {
		return '<div class="checkbox" index="'+data.docId+'"><label><input type="checkbox" class="chckBox_'+tabRefName+'" onclick="sendReminderSelection(\''+data.docId+'\',\''+tabRefName+'\',this)"/><i class="helper"></i></label></div>';
	}};
	if(tabRefName == "clientstab"){
		var cname = {data: function ( data, type, row ) {
			var businessname = "";
			if(data.businessname != undefined){
				businessname = data.businessname;
			}return '<span class="text-left">'+businessname+'</span>';
		}};
		var logo = {data: function (data, type, row) {
			var imgurl = contextPath+'/static/mastergst/images/master/defaultcompany.png';
			
			if(data.logoid != undefined){
				var imgurl = contextPath+'/getlogo/'+data.logoid;
			}
			return '<img src='+imgurl+' alt="Logo" class="imgsize-thumb" id="clntlogo" width="30px" height="30px" style="position: inherit">';
		}};
		var state = {data: function ( data, type, row ) {
			var statename = "";
			if(data.statename != undefined){
				statename = data.statename;
			}return '<span class="text-left">'+statename+'</span>';
		}};
		var reminderaction = {data: function ( data, type, row ) {
			//return '<button class="btn btn-blue-dark mr-1" onclick="reminderModal(\'' + data.docId + '\',\'' + data.businessname + '\',\'' + data.email + '\',\'' + data.mobilenumber + '\',\'' + usrname + '\',\'' + usremail + '\',\'' + usrmobile + '\',\'' + usrDetails + '\')" style="padding: 4px 10px 4px 10px;color:white;">Remind</button><a><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" class="remind_cmnt" onclick="AllReminders_view(\''+data.docId+'\')" style="width: 26px;"/></a>';
			return '<button class="btn btn-blue-dark mr-1" onclick="reminderModal(\'' + data.docId + '\',\'' + data.businessname + '\',\'' + data.email + '\',\'' + data.mobilenumber + '\',\'' + usrname + '\',\'' + usremail + '\',\'' + usrmobile + '\')" style="padding: 4px 10px 4px 10px;color:white;">Remind</button><a><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" class="remind_cmnt" onclick="AllReminders_view(\''+data.docId+'\')" style="width: 26px;"/></a>';
		}};
		return [chckbox, logo, cname, cemail, mobileno, gstno, state, reminderaction];		
	}else if(tabRefName == "customerstab" || tabRefName == "supplierstab"){
		var cname = {data: function ( data, type, row ) {
			var name = "";
			if(data.name != undefined){
				name = data.name;
			}return '<span class="text-left">'+name+'</span>';
		}};
		var cstate = {data: function ( data, type, row ) {
			var statename = "";
			if(data.state != undefined){
				statename = data.state;
			}return '<span class="text-left">'+statename+'</span>';
		}};
		var clntName = {data:  function ( data, type, row ) {
			var contactPerson = data.contactperson ? data.contactperson : "";
			if(contactPerson.length > 25){
				contactPerson = contactPerson.substring(0,25)+"...";
			}
			return '<span class="text-left invoiceclk">'+contactPerson+'</span>';
			}};
		var reminderaction = {data: function ( data, type, row ) {
			return '<button class="btn btn-blue-dark mr-1" onclick="reminderModal(\'' + data.docId + '\',\'' + data.name + '\',\'' + data.email + '\',\'' + data.mobilenumber + '\',\'' + usrname + '\',\'' + usremail + '\',\'' + usrmobile + '\')" style="padding: 4px 10px 4px 10px;color:white;">Remind</button><a><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" class="remind_cmnt" onclick="AllReminders_view(\''+data.docId+'\')" style="width: 26px;"/></a>';
		}};
		return [chckbox, clntName, cname, cemail, mobileno, gstno, cstate, reminderaction];
	}
	return [chckbox, logo, cname, cemail, mobileno, gstno, state, reminderaction];
}
function applyClient(tabRefName) {
	var pUrl = remindersTableUrl[tabRefName];
	clientsArray[tabRefName] =new Array();
	var clientOptions = $('#multeselectclient_'+tabRefName+' option:selected');
    if(clientOptions.length > 0) {
		for(var i=0;i<clientOptions.length;i++) {
	    	clientsArray[tabRefName].push(clientOptions[i].value);
		}
	}else{
		clientsArray[tabRefName].push('CLIENTS_NOTFOUND');
	}
    $('#clientsErrorMsg_'+tabRefName).html("");
    if(clientsArray[tabRefName].length == 0 || clientsArray[tabRefName][0]== 'CLIENTS_NOTFOUND'){
    	pUrl+='&clientids=';
    }else{
    	pUrl+='&clientids='+clientsArray[tabRefName];    	
    }
    remindersTable[tabRefName].ajax.url(pUrl).load();
  
 }