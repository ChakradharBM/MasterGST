
var invoiceArray = new Object();
var expiryUsersTable = new Object();
var expiryUsersTableUrl = new Object();

function loadExpiryUsersDetails(id, type){
	
	var contextPath=_getContextPath();
	var pUrl =contextPath+'/expriyusers?id='+id+'&type='+type;
	if(expiryUsersTable[type]){
		expiryUsersTable[type].clear();
		expiryUsersTable[type].destroy();
	}
	//acknlTableUrl[pendingOrUpload] = pUrl;
	expiryUsersTable[type] = $('#dbTable_'+type).DataTable({
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
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		'columns': getExpiredUsersColumns(id, type),
		'columnDefs': [
			{
				"targets":  [4,5],
				className: 'dt-body-right'
			}
		]
	});
	$('#dbTable_'+type+' tbody').on('click', 'tr', function (e) {
		 if (!$(e.target).closest('.nottoedit').length) {
			var dat = expiryUsersTable[type].row($(this)).data();
			getAllUsersDetails(dat.user.email);
		}			
	});
	if(type == 'ALL_EXPIRED_USERS'){
		$(".tabtable div.toolbar").html('<h4>ALL Expired Users &nbsp;&nbsp;</h4>');		
	}else if(type == 'PAID_EXPIRED_USERS'){
		$(".tabtable div.toolbar").html('<h4>Paid Expired Users &nbsp;&nbsp;</h4>');		
	}else if(type == 'EXPIRED_IN_NEXT_45'){
		$(".tabtable div.toolbar").html('<h4>Renewal Users &nbsp;&nbsp;</h4>');
	}
}

function getExpiredUsersColumns(id, type){
	var name = {data:  function ( data, type, row ) {
		var fullname = '';
		if(data.user != undefined && data.user != ''){
			fullname = data.user.fullname;
		}
		return '<span class="text-left">'+fullname+'</span>';
	}};
	var email = {data:  function ( data, type, row ) {
		var emailid = '';
		if(data.user != undefined && data.user != ''){
			emailid = data.user.email;
		}
		return '<span class="text-left">'+emailid+'</span>';
	}};
	var mobileno = {data:  function ( data, type, row ) {
		var mobilenumber = '';
		if(data.user != undefined && data.user != ''){
			mobilenumber = data.user.mobilenumber;
		}
		return '<span class="text-left">'+mobilenumber+'</span>';
	}};
	var usertype = {data:  function ( data, type, row ) {
		var usrtyp = '';
		if(data.user != undefined && data.user != ''){
			usrtyp = data.user.type;
		}
		return '<span class="text-left">'+usrtyp+'</span>';
	}};
	var paidamt = {data: function ( data, type, row ) {
		var amount = 0.0;
		if(data.paidAmount){
			amount = data.paidAmount;
		}
	   	 return '<span class="ind_formats text-right">'+formatNumber(amount.toFixed(2))+'</span>';
    }};
	var startDate = {data: function ( data, type, row ) {
		var start = "";
		
		return '<span class="text-left">'+(new Date(data.registeredDate)).toLocaleDateString("en-GB")+'</span>';
	}};
	var endDate = {data: function ( data, type, row ) {
		var end = "";
		
		return '<span class="text-left">'+(new Date(data.expiryDate)).toLocaleDateString("en-GB")+'</span>';
	}};
	var asptype = {data:  function ( data, type, row ) {
		var atyp = '';
		var styp = '';
		if(data.user != undefined && data.user != ''){
			atyp = data.user.type;
		}
		if(atyp == 'aspdeveloper'){
			styp = data.apiType;
		}
		return '<span class="text-left">'+styp+'</span>';
	}};
	
	var action = {data: function ( data, type, row ) {
	   	return '<div class="button-type nottoedit"><button onClick="sendEmail(\''+data.docId+'\',\''+data.user.fullname+'\',\''+data.user.email+'\',\''+(new Date(data.expiryDate)).toLocaleDateString("en-GB")+'\')" class="datatablebody btn btn-success" data-toggle="modal" data-target="#sendMessageModal" role="button" style="padding: 4px 10px 4px 10px; font-size: 14px;">SEND</button></div>';
    }};
	return [name, email, mobileno, usertype, paidamt, startDate, endDate, asptype, action];
}
