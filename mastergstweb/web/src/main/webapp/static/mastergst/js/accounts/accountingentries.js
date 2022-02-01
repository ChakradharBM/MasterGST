//** ADD Group Script

$(function(){
	var options = {
			url : function(phrase) {
				phrase = phrase.replace('(', "\\(");
				phrase = phrase.replace(')', "\\)");
				return  _getContextPath()+"/headslist/" + clientid + "?query=" + phrase + "&format=json";
			},
			getValue : function(element) {
				return element.name;
			},
			list : {
				match : {
					enabled : true
				},
				onChooseEvent : function() {
					var data = $("#headname").getSelectedItemData();
					$('#headname').val(data.name);
					$('#gaction_groupname').val(data.name);
					$('#gaction_headname').val(data.headname);
					$('#gaction_path').val(data.path);
					if($("#gname").val() == undefined || $("#gname").val() == null){
						$('#headspath').text(data.path+"/");
					}else{
						$('#headspath').text(data.path+"/"+$("#gname").val());
					}
					$('.headspath').css('display','block');
				},
				onLoadEvent : function() {
					if ($("#eac-container-headname ul").children().length == 0) {
						$("#groupname_headnameempty").show();
					} else {
						$("#groupname_headnameempty").hide();
					}
				},
				maxNumberOfElements : 10
			},
	};
	$('#headname').easyAutocomplete(options);
	
	$('#gname').keyup(function() {
	
		var spath = $("#gname").val();
		var path = $("#gaction_path").val();
		$('#gaction_groupname').val($("#gname").val());
		if($("#gaction_path").val() == undefined || $("#gaction_path").val() == null){
			$('#headspath').text("/"+spath);
		}else{
			$('#headspath').text(path+"/"+spath);
		}
		$('.headspath').css('display','block');
	});
});

function groupNameEvent(){
	var spath = $("#gname").val();
	var path = $("#head_path").val();
	
	if($("#gname").val() == undefined || $("#gname").val() == null){
		$('#path').text(data.path+"/");
	}else{
		$('#path').text(data.path+"/"+$("#gname").val());
	}
	
	$('#headspath').text(path+"/"+spath);
	$('#headspath').css('display','block');
}


$('#subgname').change(function() {
	var groupname=$('#subgname').val();
	var clientid='<c:out value="${client.id}"/>';
	$.ajax({
		type : "GET",
		async: false,
		contentType : "application/json",
		url: "${contextPath}/subgroupnameexits/"+clientid+"/"+groupname,
		success : function(response) {
			if(response == 'success'){
				$('.subgrpmsg').text('Sub Group Name Already Exists').show();
				$('#bmsg').addClass('has-error has-danger');
				err=1;
			}else{
				err=0;
				$('.subgrpmsg').text('').hide();
				$('#bmsg').removeClass('has-error has-danger');
			}
		}
	});
});





