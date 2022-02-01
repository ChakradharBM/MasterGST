<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script> 
<style>
div#dbTable1_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}
.modal-dialog{overflow-y: initial !important}
.modal-body{height: 80vh;overflow-y: auto;overflow-x: hidden;}
.add-filed-table input{padding: 7px;}
.add-filed-table th{font-size: 14px;font-weight: unset;}
.add-filed-table td{border: 1px solid lightgray;}
.add-filed-table tbody {display:block;height:250px;overflow:auto;}
.add-filed-table thead, .add-filed-table tbody tr {display:table;width:100%;table-layout:fixed;}
.add-filed-table{width:400px;}
#customTable tbody tr td:first-child{color:#354052;}
.add-filed-table tbody tr td:first-child{width:85%;}
</style>
<script>
	var bankDetails=new Array();
	var customTable;
	$(document).ready(function() {
		if(customTable) {
			customTable.destroy();
		}
		customTable = $('.cust_table').DataTable({
			dom: '<"toolbar"f>Blfrtip<"clear">', 	
			"pageLength": 10,
			"paging": true,
			"searching": true,
			"lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
			"responsive": true,
			"ordering": true,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		$(".customFieldTable div.toolbar").html('<a href="#" class="btn btn-blue-dark" data-toggle="modal" data-target="#addCustomFieldModal" onclick="populateElement()">Add</a> ');
	});
</script>
</head>
<body class="body-cls">
                <div class="row pl-3 pr-3">
                    <!-- dashboard cp  begin -->
                    <div class="col-md-12 col-sm-12 customtable customFieldTable">
                        <table id="customTable" class="display row-border dataTable meterialform cust_table" cellspacing="0" width="100%">
                            <thead>
                                <tr>
									<th class="text-center">Custom Field Name</th>
									<th class="text-center">Filed Type</th>
									<th class="text-center">Display In</th>
									<th class="text-center">Display in Print</th>
									<th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody id="customFieldsBody">
                            </tbody>
                        </table>
                    </div> 
                </div>
	 <!-- Add Modal Start -->
    <div class="modal fade" id="addCustomFieldModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="closeCustomModal()">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3 class="customFieldTxt" style="color:white;margin:0;">Add Custom Field</h3>
                    </div>
				</div>
                <div class="modal-body meterialform bs-fancy-checks">
                    <!-- row begin -->
					<%-- <form:form method="POST" data-toggle="validator" class="meterialform customform" id="customdetails" name="customform" action="${contextPath}/savecustomFields/${id}/${client.id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="customdetails"> --%>
					<span class="pl-5" id="custErrorMessage" style="color:red;font-size:14px;"></span>
					<span class="" id="dup_message" style="color:red;font-size:14px;"></span>
					<div class="row pl-5 pr-5 pt-2 pb-2">
					<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich">Custom Field Name</p>
                            <span class="errormsg" id="cfield_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control cFieldName" id="cFieldName" name="customFieldName" required="required" onblur="checkCustomFields()" data-error="Please Enter Custom Filed Name"  placeholder="Enter Custom Filed Name" value="" style="height:38px;"/>
                            <label for="cFieldName" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich">Custom Field Type</p>
                            <span class="errormsg" id="cfield_Msg" style="margin-top:-33px"></span>
                            <select class="form-control cfieldType" id="inputType" name="customFieldType" required="required" onblur="checkCustomFields()"><option value="">- Select -</option><option value="input">Input</option><option value="list">List</option><option value="radio">Radio button</option><option value="checkB">Check Box</option></select>
                            <label for="inputT" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich">Display Custom Field In </p>
                            <span class="errormsg" id="fieldIn_Msg" style="margin-top:-33px"></span>
                            <select class="form-control fieldIn" id="fieldIn" name="fieldIn" required="required" onblur="checkCustomFields()"><option value="">- Select -</option><option value="Sales">Sale Invoice</option><option value="Purchase">Purchase Invoice</option><option value="Ewaybill">E-WayBill </option><option value="E-invoice">E-invoice</option><option value="Items">Items</option></select>
                            <label for="fieldIn" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                           <div class="form-group col-md-6 row">
			                <div class="form-group col-md-7 pr-0 mb-1" style="margin: auto;"><p class="lable-txt">Display In Print :</p></div>
			                <div class="form-group col-md-5 pl-0 mb-3" style="margin: auto;">
			                <div class="form-check" style="margin: auto;">
			                <input class="form-check-input" onchange="" type="checkbox" id="print_check" name="displayInPrint" value="false">
			                <label for="displayinp"><span class="ui"></span>
			                </label>
			                </div>
							</div>
							<br>
							<p style="font-size:12px;margin-bottom: -17px;margin-left: 13px;">(On Enable this option, It will display in Print.)</p>
							</div>
							 <div class="form-group col-md-6 row mt-3">
			                <div class="form-group col-md-7 pr-0 mb-1" style="margin: auto;"><p class="lable-txt">Is It mandatory :</p></div>
			                <div class="form-group col-md-5 pl-0 mb-3" style="margin: auto;margin-bottom: 19px;">
			                <div class="form-check" style="margin: auto;">
			                <input class="form-check-input" onChange="" type="checkbox" id="mandatory_check" name="isMandatory" value="false">
			                <label for="displayinp"><span class="ui"></span>
			                </label>
			                </div>
							</div>
							</div>
							<div class="form-group col-md-6 row mt-3" style="margin-left:3%">
				                <div class="form-group col-md-7 pr-0 mb-1" style="margin: auto;"><p class="lable-txt">Display In Filters :</p></div>
				                <div class="form-group col-md-5 pl-0 mb-3" style="margin: auto;margin-bottom: 19px;">
					                <div class="form-check" style="margin: auto;">
						                <input class="form-check-input" onchange="" type="checkbox" id="filters_check" name="displayInFilters" value="false">
						                <label for="filters"><span class="ui"></span>
						                </label>
					                </div>
								</div>
							</div>
							<!--<div class="form-group col-md-6 row mt-3" id="duplicates_div" data-toggle="tooltip" title="On Selection Of this Check box, Duplicates Custom Fields are allowed for input type">
				                <div class="form-group col-md-7 pr-0  mb-1" style="margin: auto;"><p class="lable-txt">Allow Duplicates :</p></div>
				                <div class="form-group col-md-5 pl-0 mb-3" style="margin: auto;">
					                <div class="form-check" style="margin: auto;">
						                <input class="form-check-input" onchange="" type="checkbox" id="duplicates_check" name="isDuplicatesAllow" value="false">
						                <label for="duplicates"><span class="ui"></span>
						                </label>
					                </div>
								</div>
							</div>-->
							<div class="row">
							<div class="col-md-6 text-left"></div>
							<div class="col-md-6 text-left mt-4 newField d-none"><h4 onclick="addCustom()" class="btn btn-sm btn-blue-dark" id="add_fieldtext">Add New Field</h4></div>
							<div class="customtable col-md-12 Ftable d-none mb-0">
								<table class="row-border meterialform add-filed-table mb-0" cellspacing="0" style="width: 73%;">
									<thead>
										<tr>
											<th class="text-center field_label" style="width: 85%; padding: 5px;">Field Values</th>
											<th class="text-center">Action</th>
										</tr>
									</thead>
									<tbody id="add-filed-table-body">
										<tr>
											<td colspan="2" class="text-center nodata-msg">No data is available</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
                    </div>
                    <input type="hidden" id="customId"/>
                     <input type="hidden" id="customField_Name"/>
                     <input type="hidden" id="customField_Type"/>
					<%-- </form:form> --%>
                    <!-- row end -->
                </div>
				<div class="modal-footer text-center" style="display:block;border:1px solid lightgray;">
						<button type="button" class="btn btn-blue-dark m-0" id="custom_Save" onclick="customFieldssubmit()">Save</button>
						<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close" onclick="closeCustomModal()">Cancel</a>
					</div>
            </div>
        </div>
    </div>
    <!-- Add Modal End -->
	<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Custom Field Of this Row</h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete_custom" data-dismiss="modal">Delete</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
var valuesArray = new Array();var isExist = false;
$(document).ready(function() {
	$('#cpUploadNav').addClass('active');
	$( ".helpicon" ).hover(function() {
		$('.customData ').show();
	}, function() {
			$('.customData ').hide();
	});
});

$( ".cfieldType" ).change(function() {
	  var ctype = $('#inputType').val();
	  $('.add-filed-table tbody input').val('');
		if(ctype == 'list'){
			$('.Ftable,.newField').removeClass('d-none');
			$('.field_label').html('List Name')
			$('#add_fieldtext').html('Add List Item');
			$('#duplicates_div').addClass('d-none');
		}else if(ctype == 'radio'){
			$('#duplicates_div').addClass('d-none');
			$('.Ftable,.newField').removeClass('d-none');
			$('.field_label').html('Radio Button Name')
			$('#add_fieldtext').html('Add Radio Button');
		}else if(ctype == 'checkB'){
			$('#duplicates_div').addClass('d-none');
			$('.Ftable,.newField').removeClass('d-none');
			$('.field_label').html('Check Box Name')
			$('#add_fieldtext').html('Add Check Box');
		}else{
			$('.Ftable,.newField').addClass('d-none');
			$('#duplicates_div').removeClass('d-none');
		}
});

 var no=1;
function addCustom(){
$('.nodata-msg').hide();
	  $('.add-filed-table tbody').append('<tr class="customrow'+no+'"><td class="text-center"><input type="text" class="form-control" id="val'+no+'" name="fieldval" onchange="valuesChange('+no+')"></td><td class="text-center"><a href="#" class="nottoedit" onClick="removenewFielld('+no+')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>');
	   no=no+1;
}
 var delno;
function removenewFielld(delno){
	$('.customrow'+delno).remove();
	$("#add-filed-table-body tr").each(function(index) {
		var cls = $(this).attr('class');
		if(cls != undefined){
			var rowno = (index).toString();
			var rownoo = (index-1).toString();
			if(rowno<10){
				cls = cls.slice(0, -1);
			}else{
			   	cls = cls.slice(0, -2);
			}
			cls = cls+rowno;
			$(this).attr('class',cls);
		
		$(this).find('td').each (function() {
			var name = $(this).attr('id');
			if(name != undefined){
				if(rowno<10){
					name = name.slice(0, -1);
				}else{
				   	name = name.slice(0, -2);
				}
				name = name+rowno;
				$(this).attr('id',name);
			}else{
				$(this).find('input').each (function() {
					var inputname = $(this).attr('id');
					if(inputname != undefined){
						if(rowno<10){
							inputname = inputname.slice(0, -1);
						}else{
						   	inputname = inputname.slice(0, -2);
						}
						inputname = inputname+rowno;
						$(this).attr('id',inputname);
					}
					var abcd = $(this).attr('onchange');
					if(rowno<10){
						abcd = abcd.slice(0, -2);
					}else{
						abcd = abcd.slice(0, -3);
					}
					abcd = abcd+rowno+')';
					$(this).attr('onchange',abcd);
				});
				
				$(this).find('a').each (function() {
					var abcd = $(this).attr('onclick');
					if(rowno<10){
						abcd = abcd.slice(0, -2);
					}else{
						abcd = abcd.slice(0, -3);
					}
					abcd = abcd+rowno+')';
					$(this).attr('onclick',abcd);
				});
			}
		});
	}
	});
	var tableng = $('#add-filed-table-body tr').length;
	no = tableng;
	if(no == 1){
		$('.nodata-msg').show(); 
	}
}
</script>
</body>
</html>