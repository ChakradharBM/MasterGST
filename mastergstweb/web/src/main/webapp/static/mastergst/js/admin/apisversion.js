var apiVersionTable = new Object();

function loadApiVersionsInfo(tabRefName){
	var contextPath=_getContextPath();
	var pUrl =contextPath+'/getApisVersions?apiType='+tabRefName;	
	if(apiVersionTable[tabRefName]){
		apiVersionTable[tabRefName].clear();
		apiVersionTable[tabRefName].destroy();
	}
	apiVersionTable[tabRefName] = $('#gstApiDataTable_'+tabRefName).DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
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
		'columns': getApisColumns(tabRefName),
		'createdRow': function( row, data, dataIndex ) {
		      $(row).attr('data-docid', data.docid);
		 },
		'columnDefs': [
			{
				"targets":  [4,5],
				className: 'dt-body-center'
			}
		]
	});
}

function getApisColumns(tabRefName){
	var contextPath=_getContextPath();
	var docId = {data:  function ( data, type, row ) {
		var docid = '';
		if(data.docid != undefined){
			docid = data.docid;
		}
	   	return '<span class="text-left">'+docid+'</span>';
	}};
	var apiType = {data: function ( data, type, row ) {
		var apitype = "";
		if(data.type){
			apitype = data.type;
		}
		return '<span class="text-left">'+apitype+'</span>';
	}};
	var name = {data: function ( data, type, row ) {
		var apiname = "";
		if(data.name){
			apiname = data.name;
		}
		return '<span class="text-left">'+apiname+'</span>';
	}};
	var method = {data: function ( data, type, row ) {
		var apimethod = "";
		if(data.method){
			apimethod = data.method;
		}
		return '<span class="text-left">'+apimethod+'</span>';
	}};
	var sandboxVersion = {data: function ( data, type, row ) {
		var sandboxversion = "";
		if(data.sandboxVersion){
			sandboxversion = data.sandboxVersion;
		}
		return '<span class="text-left">'+sandboxversion+'</span>';
	}};
	var productionVersion = {data: function ( data, type, row ) {
		var prodVersion = "";
		if(data.productionVersion){
			prodVersion = data.productionVersion;
		}
		return '<span class="text-left">'+prodVersion+'</span>';
	}};
	var webimpl = {data: function ( data, type, row ) {
		var apiwebimpl = data.webimpl;
		return '<span class="text-left">'+apiwebimpl+'</span>';
	}};
	
	var action = {data: function ( data, type, row ) {
		return '<button class="btn btn-info btn-xs btn-edit" data-toggle="modal" data-target="#'+tabRefName+'Modal" onclick="editApis(\''+data.docid+'\',\''+tabRefName+'\')">Edit</button>';
	}};
	
	return [apiType, name, method, sandboxVersion, productionVersion, webimpl, action];
}

function editApis(docid, tabRefName){
	$('#apisVersinProgress-bar_'+tabRefName).removeClass('d-none');
	//$('#'+tabRefName+'Modal').show();
	$.ajax({
		url: _getContextPath()+'/apiversioninfo?docid='+docid,
		type:'GET',
		contentType: 'application/json',
		success : function(api) {
			if(api.webimpl){
				$('#'+tabRefName+'webimpl').val(1);
			}else{
				$('#'+tabRefName+'webimpl').val(0);
			}
			$('#'+tabRefName+'name').val(api.name);
			$('#'+tabRefName+'method').val(api.method);
			$('#'+tabRefName+'sVersion').val(api.sandboxVersion);
			$('#'+tabRefName+'pVersion').val(api.productionVersion);
			$('#'+tabRefName+'docid').val(api.docid);
			$('#apisVersinProgress-bar_'+tabRefName).addClass('d-none');
		},error: function(error){
		}
	});
}

function cancelApi(docid, tabRefName){
	apiVersionTable[tabRefName].ajax.reload();
}
function updateApi(tabRefName){
	$('#apisVersinProgress-bar_'+tabRefName).removeClass('d-none');
	var apiVersion = new Object;
	apiVersion.docid = $('#'+tabRefName+'docid').val();
	apiVersion.name = $('#'+tabRefName+'name').val();
	apiVersion.method = $('#'+tabRefName+'method').val();
	apiVersion.sandboxVersion = $('#'+tabRefName+'sVersion').val();
	apiVersion.productionVersion = $('#'+tabRefName+'pVersion').val();
	if($('#'+tabRefName+'webimpl').val() == 1){
		apiVersion.webimpl = true;
	}else{
		apiVersion.webimpl = false;
	}
	
	var contextPath=_getContextPath();
	$.ajax({
		url: contextPath+'/updateApiVersion?_='+Math.random(),
		type:'POST',
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		data: JSON.stringify(apiVersion),
		success : function(response) {
			apiVersionTable[tabRefName].ajax.reload();
			$('#'+tabRefName+'webimpl').val('');
			$('#'+tabRefName+'name').val('');
			$('#'+tabRefName+'method').val('');
			$('#'+tabRefName+'sVersion').val('');
			$('#'+tabRefName+'pVersion').val('');
			$('#'+tabRefName+'docid').val('');
			$('#'+tabRefName+'Modal').modal('hide');
			$('#apisVersinProgress-bar_'+tabRefName).addClass('d-none');
		},error: function(error){
			apiVersionTable[tabRefName].ajax.reload();
			$('#'+tabRefName+'webimpl').val('');
			$('#'+tabRefName+'name').val('');
			$('#'+tabRefName+'method').val('');
			$('#'+tabRefName+'sVersion').val('');
			$('#'+tabRefName+'pVersion').val('');
			$('#'+tabRefName+'docid').val('');
			$('#'+tabRefName+'Modal').modal('hide');
			$('#apisVersinProgress-bar_'+tabRefName).addClass('d-none');
		}
	});
}