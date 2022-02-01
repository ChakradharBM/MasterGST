
function configureAmazonS3(){
	//$('#configureAwsS3Model #accessKey').val('');
	//$('#configureAwsS3Model #accessSecret').val('');
	$('#configureAwsS3Model .errormsg').html('');
	$('#configureAwsS3Model').modal('show');
}

function saveAwsS3Credentials(userId, name, userType, month, year){
	var errCt = 0;
	$('#configureAwsS3Model .errormsg').html('');
	if($('#configureAwsS3Model #accessKey').val().trim() == ''){
		errCt++;
		$('#configureAwsS3Model #accessKey_Msg').html('Please enter Access Key');
	}
	if($('#configureAwsS3Model #accessSecret').val().trim() == ''){
		errCt++;
		$('#configureAwsS3Model #accessSecret_Msg').html('Please enter Access Secret');
	}
	if($('#configureAwsS3Model #bucketName').val().trim() == ''){
		errCt++;
		$('#configureAwsS3Model #bucketName_Msg').html('Please enter Bucket Name');
	}
	if($('#configureAwsS3Model #regionName').val().trim() == '-1'){
		errCt++;
		$('#configureAwsS3Model #regionName_Msg').html('Please select Region Name');
	}
	if(errCt > 0){
		return false;
	}
	var cred_userid = $('#credential_userid').val();
	var dat = new Object;
	dat.storageType = 'aws';
	dat.clientId = userId;
	dat.userid = userId;
	dat.clientName = 'clientname';
	dat.accessKey = $('#configureAwsS3Model #accessKey').val().trim();
	dat.accessSecret = $('#configureAwsS3Model #accessSecret').val().trim();
	dat.bucketName = $('#configureAwsS3Model #bucketName').val().trim();
	dat.regionName = $('#configureAwsS3Model #regionName').val().trim();
	dat.groupName = $('#configureAwsS3Model #groupName').val().trim();
	var contextPath=_getContextPath();
	$.ajax({
		url: contextPath+'/saveAwsCredentials?_='+Math.random()+'&userid='+cred_userid,
		type:'POST',
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		data: JSON.stringify(dat),
		success : function(response) {
			$('#configureAwsS3Model #cancelBtn').click();
			
			if(cred_userid == undefined){
				location.href = contextPath+'/cp_acknowledgementUser/'+userId+'/'+name+'/'+userType+'/'+month+'/'+year;
			}else{
				location.href = contextPath+'/cp_acknowledgementSavedCredentials/'+userId+'/'+name+'/'+userType+'/'+month+'/'+year;
			}
		},error: function(error){
			$('#configureAwsS3Model #aws_error').html('Problem occured while adding the aws credentials');
		}
	});
	
}