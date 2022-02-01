
function saveSmtpDetails(userId, name, userType, month, year){
	var errCt = 0;
	$('#smtpdetails .errormsg').html('');
	if($('#smtpdetails #host').val().trim() == ''){
		if(errCt++ == 0){
			$('#smtpdetails #host').focus();
		}
		$('#smtpdetails #host_Msg').html('Please enter host');
	}
	if($('#smtpdetails #port').val().trim() == ''){
		if(errCt++ == 0){
			$('#smtpdetails #port').focus();
		}
		$('#smtpdetails #port_Msg').html('Please enter port');
	}
	if($('#smtpdetails #from').val().trim() == ''){
		if(errCt++ == 0){
			$('#smtpdetails #from').focus();
		}
		$('#smtpdetails #from_Msg').html('Please enter from');
	}
	if($('#smtpdetails #username').val().trim() == ''){
		if(errCt++ == 0){
			$('#smtpdetails #username').focus();
		}
		$('#smtpdetails #username_Msg').html('Please enter user name');
	}
	if($('#smtpdetails #password').val().trim() == ''){
		if(errCt++ == 0){
			$('#smtpdetails #password').focus();
		}
		$('#smtpdetails #password_Msg').html('Please enter password');
	}
	if($('#smtpdetails #schedlueExpressionVal').val().trim() == ''){
		if(errCt++ == 0){
			$('#smtpdetails #schedlueExpressionVal').focus();
		}
		$('#smtpdetails #schedlueExpressionVal_Msg').html('Please select run for');
	}
	if($('#smtpdetails #toAddress').val().trim() == ''){
		if(errCt++ == 0){
			$('#smtpdetails #toAddress').focus();
		}
		$('#smtpdetails #toAddress_Msg').html('Please enter to address');
	}
	if($('#smtpdetails #ccAddress').val().trim() == ''){
		if(errCt++ == 0){
			$('#smtpdetails #ccAddress').focus();
		}
		$('#smtpdetails #ccAddress_Msg').html('Please enter cc address');
	}
	if(errCt > 0){
		return false;
	}
	var dat = new Object;
	dat.storageType = 'aws';
	dat.userId = userId;
	dat.host = $('#smtpdetails #host').val().trim();
	dat.port = $('#smtpdetails #port').val().trim();
	dat.auth = $('#smtpdetails #auth').val().trim();
	dat.from = $('#smtpdetails #from').val().trim();
	dat.schedlueExpressionVal = $('#smtpdetails #schedlueExpressionVal').val().trim();
	dat.username = $('#smtpdetails #username').val().trim();
	dat.password = $('#smtpdetails #password').val().trim();
	dat.toAddress = $('#smtpdetails #toAddress').val().trim();
	dat.ccAddress = $('#smtpdetails #ccAddress').val().trim();
	var contextPath=_getContextPath();
	$.ajax({
		url: contextPath+'/saveSmtpDetails?_='+Math.random(),
		type:'POST',
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		data: JSON.stringify(dat),
		success : function(response) {
			$('#smtpdetails #cancelBtn').click();
			location.href = contextPath+'/cp_smtp/'+userId+'/'+name+'/'+userType+'/'+month+'/'+year;
		},error: function(error){
			$('#smtpdetails #smtp_error').html('Problem occured while adding smtp details');
		}
	});
	
}

function editSmtpDetails(id){
	$.ajax({
		url: contextPath+'/smtpdetails/'+id+'?_='+Math.random(),
		type:'GET',
		contentType: 'application/json',
		success : function(response) {
			loadsmtpdetails(response.body);
			$('#addSmtpDetails').modal('show');
		},error: function(error){
			$('#smtpdetails #smtp_error').html('Problem occured while reading smtp details');
			$('#addSmtpDetails').modal('show');
		}
	});
}

function loadsmtpdetails(smtpdetailsObj){
	emptySmtpDetailsForm();
	$('#smtpdetails #docId').val(smtpdetailsObj.docId);
	$('#smtpdetails #clientId').val(smtpdetailsObj.clientId);
	$('#smtpdetails #host').val(smtpdetailsObj.host);
	$('#smtpdetails #port').val(smtpdetailsObj.port);
	$('#smtpdetails #auth').val(smtpdetailsObj.auth);
	$('#smtpdetails #from').val(smtpdetailsObj.from);
	$('#smtpdetails #schedlueExpressionVal').val(smtpdetailsObj.schedlueExpressionVal);
	$('#smtpdetails #username').val(smtpdetailsObj.username);
	$('#smtpdetails #password').val(smtpdetailsObj.password);
	$('#smtpdetails #toAddress').val(smtpdetailsObj.toAddress);
	$('#smtpdetails #ccAddress').val(smtpdetailsObj.ccAddress);
}
function emptySmtpDetailsForm(){
	$('#smtpdetails .form-control').val('');	
}