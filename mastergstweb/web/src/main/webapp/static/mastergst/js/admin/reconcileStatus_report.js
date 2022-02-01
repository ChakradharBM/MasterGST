var reconcileInfoTable;// = new Object();
var reconcileInfoTableUrl;// = new Object();

function loadReconcileInfo(id){
	if(reconcileInfoTable){
		reconcileInfoTable.clear();
		reconcileInfoTable.destroy();
	}
	var pUrl = _getContextPath()+"/getReconcileTempUsers/"+id+"?_="+Math.random();
	reconcileInfoTable = $('#reconcileStatusDbTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		"processing": true,
		"serverSide": true,
	    "ajax": {
			url: pUrl,
	        type: 'GET',
	        contentType: 'application/json; charset=utf-8',
	        dataType: "json",
	        'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.data.totalElements;
	        	 resp.recordsFiltered = resp.data.totalElements;
	        	 return resp.data.content;
	         }
	    },
	    "paging": true,
		"pageLength":10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
	    "columns": getReconcileUsersColumns(),
	    'columnDefs': [
			{
				"targets":  [4, 5,6],
				className: 'dt-body-right'
			}
		]
	});
}

function updatetime(dat){
	var dt = "";
		var createdDt1 = new Date(dat) ;
		var month = createdDt1.getMonth() + 1; 
		var day = createdDt1.getDate();
		var year = createdDt1.getFullYear();
		//var createdDt = new Date(dat.starttime).toLocaleTimeString();
		
		var hours = createdDt1.getHours();
		var minutes = createdDt1.getMinutes();
		var ampm = hours >= 12 ? 'PM' : 'AM';
		hours = hours % 12;
		  //hours = hours ? hours : 12; // the hour '0' should be '12'
		minutes = minutes < 10 ? '0'+minutes : minutes;
		var seconds = createdDt1.getSeconds();
		seconds = seconds < 10 ? '0'+seconds : seconds;
		var strTime = hours + ':' + minutes + ':' + seconds + ' ' + ampm;
		dt = day+'-'+month+'-'+year+' '+strTime;
	return dt;
}
function getReconcileUsersColumns(){
	
	var fullname = {data:  function ( data, type, row ) {
		var name = '';
		if(data.user.fullname != undefined){
			name = data.user.fullname;
		}
      	return '<span class="text-left">'+name+'</span>';
    }};
	var email = {data:  function ( data, type, row ) {
		var emid = '';
		if(data.user.email != undefined){
			emid = data.user.email;
		}
      	return '<span class="text-left">'+emid+'</span>';
    }};
	var mobilenumber = {data:  function ( data, type, row ) {
		var mobileno = '';
		if(data.user.mobilenumber != undefined){
			mobileno = data.user.mobilenumber;
		}
      	return '<span class="text-left">'+mobileno+'</span>';
    }};
	var type = {data:  function ( data, type, row ) {
		var usertype = '';
		if(data.user.type != undefined){
			usertype = data.user.type;
		}
      	return '<span class="text-left">'+usertype+'</span>';
    }};
	var totalgstr2acounts = {data:  function ( data, type, row ) {
		
		var process = '0';
		if(data.totalinvoices != undefined){
			process = data.totalinvoices;
		}
      	return '<span class="text-right">'+process+'</span>';
    }};
	var gstr2acounts = {data:  function ( data, type, row ) {
		
		var process = '0';
		if(data.processedgstr2ainvoices != undefined){
			process = data.processedgstr2ainvoices;
		}
      	return '<span class="text-right">'+process+'</span>';
    }};
	var totalprcounts = {data:  function ( data, type, row ) {
		
		var process = '0';
		
		if(data.totalpurchaseinvoices != undefined){
			process = data.totalpurchaseinvoices;
		}
      	return '<span class="text-right">'+process+'</span>';
    }};
	var prcounts = {data:  function ( data, type, row ) {
		
		var process = '0';
		
		if(data.processedpurchaseinvoices != undefined){
			process = data.processedpurchaseinvoices;
		}
      	return '<span class="text-right">'+process+'</span>';
    }};
	
	var createdDate = {data:  function ( data, type, row ) {
		return '<span class="text-right">'+updatetime(data.createdDate)+'</span>';
    }};
	
	return [fullname, email, type, createdDate, totalgstr2acounts,gstr2acounts,totalprcounts,prcounts];
}
