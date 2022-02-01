var custTable = new Object();


function loadmultigstntablesTable(id, month, year, userType, fullname){
	var cUrl = _getContextPath()+'/getmultigstndetails/'+id+'/'+fullname+'/'+userType+'/'+month+'/'+year;
	custTable = $('#dbGSTINTable1').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, 500,-1], [10, 25, 50, 100,500, "All"] ],
	     "ajax": {
	         url: cUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 $('#checkallgstno').prop('checked', false);
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
		"order": [[2,'desc']],
		'columns': getCustomerColumns(id, userType, month, year),
		'columnDefs' : getCustomerColumnDefs(),
		"createdRow": function( row, data, dataIndex ) {
		      $(row).attr('id', 'row' + data.gstnid);
		  }
		 
	});
	
	
	$('#multigstnbody').on('click', 'tr', function (e) {
		var data = custTable.row($(this)).data();
		if (!$(e.target).closest('.nottoedit').length) {
			if(data.status == 'VALID'){
				populateElement(data.gstnid);
			}
		}
	} );
}

function getCustomerColumns(id, userType, month, year){
	var chkBx = {data: function ( data, type, row ) {
		return '<div class="checkbox nottoedit" index="'+data.gstnid+'"><label><input type="checkbox" onClick="updateSelection(\''+data.gstin+'\',\''+data.gstnid+'\',\''+data.userid+'\',this)"/><i class="helper"></i></label></div>';
	}};
	var gstnumber = {data:  function ( data, type, row ) {
		var gstin = data.gstin ? data.gstin : "";
		return ''+gstin+'';
		}};
	var tradeName = {data:  function ( data, type, row ) {
		var tradeNam = data.tradeNam ? data.tradeNam : "";
		return '<span class="text-left invoiceclk">'+tradeNam+'</span>';
		}};
	var duty = {data:  function ( data, type, row ) {
		var dty = data.dty ? data.dty : "";
		return '<span class="text-left invoiceclk">'+dty+'</span>';
		}};
	var registereddate = {data:  function ( data, type, row ) {
		var rgdt = data.rgdt ? data.rgdt : "";
		return '<span class="text-left invoiceclk">'+rgdt+'</span>';
		}};
	var lastupdateddate = {data:  function ( data, type, row ) {
		var lstupdt = data.lstupdt ? data.lstupdt : "";
		return '<span class="text-left invoiceclk">'+lstupdt+'</span>';
		}};
	var gstnostatus = {data:  function ( data, type, row ) {
		
		var cfscolor = (data.status == 'VALID') ? 'color:green!important' :  'color:red!important';
		var status = data.status ? data.status : "";
		return '<span class="text-left invoiceclk" style="'+cfscolor+'">'+status+'</span>';
		}};	
	return [chkBx , gstnumber,tradeName, duty, registereddate, lastupdateddate, gstnostatus];
}


function getCustomerColumnDefs(){
	return  [
		{
			"targets": 0,
			"orderable": false
		}
		
	];
}