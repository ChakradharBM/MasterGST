<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Multi GSTIN Report</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/profile/multigstns.js" type="text/javascript"></script>
<style>
.active-cls{color:green!important}
.inactive-cls{color:red!important}
</style>
<script type="text/javascript">
var multigstin=new Array();var multigstinpublic=new Array();var validatearry=new Array(); var multigstnarry=new Array();var objidarry=new Array();var ipAddress;var table;
	function populateElement(gstn) {
		$('#gstnudet').modal('show');
		$.ajax({
			url: '${contextPath}/multiGtstinDetails/${id}/'+gstn,
			contentType: false,
			cache: false,
			success: function(retResponse) {
				if(retResponse) {
					Object.keys(retResponse).forEach(function(key) {
						$('.'+key).html(retResponse[key]);
						if(key == 'nba'){
							var nba = "";
							if(retResponse['nba'] != null){
								var nbalength = retResponse['nba'].length;
								Object.keys(retResponse['nba']).forEach(function(index,nkey){
									if (index == (nbalength - 1)) {
										nba += retResponse['nba'][nkey];
									}else{
										nba += retResponse['nba'][nkey]+",";	
									}
									$('.'+key).html(nba);
								});
							}
						}
						if(key == 'pradr'){
							$('.ntr').html(retResponse['pradr']['ntr']);
							Object.keys(retResponse['pradr']['addr']).forEach(function(key){
								if(retResponse['pradr']['addr'][key] != ''){
									if(key != 'pncd'){
										$('.'+key).html(retResponse['pradr']['addr'][key]+",");
									}else if(key == 'pncd'){
										$('.'+key).html(retResponse['pradr']['addr'][key]);
									}
								}
							});
						}
						if(key == 'adadr'){
							$('.antr').html(retResponse['adadr'][0]['ntr']);
							Object.keys(retResponse['adadr'][0]['addr']).forEach(function(key){
								if(retResponse['adadr'][0]['addr'][key] != ''){
									if(key != 'pncd'){
										$('.a'+key).html(retResponse['adadr'][0]['addr'][key]+",");
									}else if(key == 'pncd'){
										$('.a'+key).html(retResponse['adadr'][0]['addr'][key]);
									}
								}
							});
						}
					});
				}
			},
			error: function(e, status, error) {
				if(e.responseText) {
					$('.validform').text(e.responseText);
				}
			}
		});
		
	}
</script>
</head>
<body class="body-cls">
  <!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
		<!--- breadcrumb start -->
 		
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						 <li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li>
     					 <li class="breadcrumb-item active">Multi GSTIN Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>

        <!--- breadcrumb end -->
        <div class="db-ca-wrap">
            <div class="container">
				<div class="d-none" id="progress-bar" style="position: absolute;top: 35%;width:100%;text-align:center;z-index:1;"> 
						<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
						<p style="opacity: 1; font-weight: bolder;color: darkblue;margin-left: -20px;" id="progress-bar-text">We are downloading your data from GSTN, please wait...</p>
					</div>
                <div class="row multigstntable">
                    <!-- left side begin -->
                   
                    <!-- left side end -->

                    <!-- dashboard cp  begin -->
                    <h4 class="hdrtitle pull-right" style="width: 100%;"><a href="#" class="btn btn-blue-dark" data-toggle="modal" data-target="#editModal">Add GSTN</a><a href="#" class="btn btn-blue-dark permissionGeneral-Import_Templates"  data-toggle="modal" data-target="#importModal">Import</a><a href="#" class="btn btn-greendark validate-btn" style="padding: .25rem .5rem!important;" onclick="validateGSTnonew()">Validate</a><a href="${contextPath}/dwnldmultigstinsxls/${id}" class="btn btn-blue excel">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a><a href="#" onclick="showDeletePopup()" class="btn btn-blue disabled delete-btn">Delete</a></h4>
                    <div class="col-md-12 col-sm-12 customtable p-0">
                        <table id="dbGSTINTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                	<th class="text-center"><div class="checkbox"><label><input type="checkbox" id="checkallgstno" onclick="checkallgstno(this)"/><i class="helper"></i></label></div> </th>
                                    <th class="text-center">GSTN</th>
                                    <th class="text-center">Company Name</th>
									<th class="text-center">Taxpayer Type</th>
									<th class="text-center">Date of registration</th>
									<th class="text-center">Last Updated Date</th>
									<th class="text-center">Status</th>
									
                                </tr>
                            </thead>
                            <tbody id="multigstnbody">
                            
                            </tbody>
                        </table>
                    </div> 
  <!-- dashboard cp table end -->
</div>                    
                </div>
            </div>
        </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->	
<!-- Import Modal Start -->
<div class="modal fade" id="gstnudet" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>GST Number Details</h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                     <!-- row begin -->
			<div class="p-4" id="srcgsitn">
            <h5 class="gstininfo">Result based on GSTIN/UIN :<span class="gstin"> </span></h5>
            <div class="row colrow">
              <div class="colhr col-md-6 col-sm-12"> Legal Name of Business<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 lgnm"></div>
            
              <div class="colhr col-md-6 col-sm-12"> State Jurisdiction<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 stj">HYDER NAGAR -II</div>
           
              <div class="colhr col-md-6 col-sm-12"> Center Jurisdiction<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 ctj"></div>
           
              <div class="colhr col-md-6 col-sm-12"> Date of registration<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 rgdt"></div>
            
              <div class="colhr col-md-6 col-sm-12"> Constitution of Business<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 ctb"></div>
            
              <div class="colhr col-md-6 col-sm-12"> Taxpayer Type<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 dty"></div>
           
              <div class="colhr col-md-6 col-sm-12"> GSTIN / UIN Status<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 sts"></div>
           
              <div class="colhr col-md-6 col-sm-12"> Date of Cancellation<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 cxdt"></div>
            
              <div class="colhr col-md-6 col-sm-12"> Last Updated Date<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 lstupdt"></div>
            
              <div class="colhr col-md-6 col-sm-12"> Nature of Business Activities<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 nba"></div>
          
              <div class="colhr col-md-6 col-sm-12"> Nature of Pricipal Place of Business<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 ntr"></div>
           
              <div class="colhr col-md-6 col-sm-12"> Nature of Additional Place of Business<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 antr"></div>
           
              <div class="colhr col-md-6 col-sm-12"> State Jurisdiction Code<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 stjCd"></div>
           
              <div class="colhr col-md-6 col-sm-12"> Center Jurisdiction Code<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 ctjCd"></div>
           
              <div class="colhr col-md-6 col-sm-12"> Registration Trade Name<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12 tradeNam"></div>
           
              <div class="colhr col-md-6 col-sm-12"> Pricipal Place of Business Address<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-6 col-sm-12"><span class="bno"></span><span class="flno"></span><span class="bnm"></span><span class="st"></span><span class="loc"></span><span class="city"></span><span class="dst"></span><span class="stcd"></span>
				<span class="lt"></span><span class="lg"></span><span class="pncd"></span></div>
           
              <div class="colhr col-md-6 col-sm-12"> Additional Place of Business Address <span class="coln-txt pull-right">:</span></div>
              <div class="colcon col-md-6 col-sm-12"><span class="abno"></span><span class="aflno"></span><span class="abnm"></span><span class="ast"></span><span class="aloc"></span><span class="acity"></span><span class="adst"></span><span class="astcd"></span>
			<span class="alt"></span><span class="alg"></span><span class="apncd"></span></div>
            </div>
                    <!-- row end -->

                </div>

            </div>
            <div class="modal-footer">
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Close</a>
				</div>
        </div>
    </div>
    </div> 
    <!-- Import Modal End -->
	
		 <!-- Edit Modal Start -->
    <div class="modal fade" id="editModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Multi GST Numbers</h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                   
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" id="multiGSTIN_form" class="meterialform" name="userform" action="/cp_creategstin/${id}/${fullname}/${usertype}/${month}/${year}" enctype="multipart/form-data" > <!--  modelAttribute="multigstin" -->
                    <div class="row pl-5 pr-5 pt-2">
						 <div class="col-md-12 col-sm-12" id="selectGstinDiv">
						  <label for="allGSTINS" class="lable-txt mb-2">Enter all GST Numbers with comma seperation<span style="float: right;">:</span></label>
						  <span class="errorgstn"></span>
	                 	  <textarea type="text" class="allGSTINS" id="GSTINStext" name="gstnos" placeholder="Enter GST numbers" value="" style="border: 1px solid lightgray;width:100%;height:250px;min-height:100px"></textarea>
	                 </div>
	                </div>
					
							<input type="submit" class="btn btn-blue-dark multiGSTin_submit  pull-right" style="display:none" id="multiGSTINsave" value="Save"/>
					</form:form>
                    <!-- row end -->

                </div>
				<div class="modal-footer">
				<label for="multiGSTin_submit" class="btn btn-blue-dark m-0 gst_save" tabindex="0" onClick="GetAllGSTNo()">Add GST Numbers</label>
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
            </div>
        </div>
    </div>
    <div class="modal fade" id="importModal" role="dialog" aria-labelledby="importModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3>Import GST Numbers</h3>

                    </div>
                     <!-- row begin -->
					<div class="p-4">
					<form:form method="POST" class="meterialform" id="itemsimportform" action="${contextPath}/multigstnuploadFile/${id}/${fullname}/${usertype}/${month}/${year}" enctype="multipart/form-data" style="border:none!important;">
					<div class="row">


         <fieldset style="width:100%;" class="pl-5 pr-5 pt-2">
			          <label for="" class="lable-txt mb-0">Import GST Number Excel<span style="float: right;">:</span></label>
			          <a href="${contextPath}/static/mastergst/template/multiGSTNumer_template.xls" class="pull-right mt-1 ml-2 vt-align"><img src="${contextPath}/static/mastergst/images/master/excel-icon.png" class="vt-align"/> Download Template</a>
					 <span class="errormsg" id="multigsterrormsg" style="display:none!important;font-size:14px;"> please select a file</span>
			                  <div class="filedragwrap" onClick="choosegstinfileSheets()">
			              <div id="filedraggstno text-center" style="display: block;">
			                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
			                <div class="filedraginput"> <span class="choosefile importchoosefile" style="left:0%!important;">Choose File</span>
			              <input type="file" name="file" id="gstnoFile" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,.csv"  style="opacity:0!important;display:none"> 
			                </div>
			                <div class="ortxt"> --( OR )--</div>
			                <div id="filedraggstno">Drop file here</div>
			              </div>
			            </div>
			            </fieldset>
            			<div class="form-group col-md-12 col-sm-12" id="idSheetgstno" style="display:none;">
								<p class="lable-txt">File Name  :  <span id="messagesgstno"></span></p>
								<div class="">&nbsp;</div>
								</div>
						<div class="form-group col-4">
							<input type="hidden" name="category" value="<%=MasterGSTConstants.CUSTOMERS%>">
							<input type="button"  id="submitbutton6" class="btn btn-blue" onclick="performMgImport(this)" value="submit" style="background-color: #314999; color:white;width:61%!important;font-size: 18px;text-transform: uppercase;margin-left: 30px;"/>
						</div>
                    </div>
					</form:form>
                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
    </div>
    <!-- Edit Modal End -->
<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete GSTN </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete GSTN <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete GSTN</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script>
$('.multi-gst').addClass('active');
$('#importModal').submit(function(e) {
	  var err = 0;
	    if (!$('#messagesgstno').html()) {  
	      err = 1;
	    }
		  if (err != 0) {
		   $('.errormsg').css('display','block');
	    return false;
	  }
	});
	function choosegstinfileSheets(){
		$('#gstnoFile')[0].click();
	}
	function GetAllGSTNo(){
		var string  = $("#GSTINStext").val();
		OptionList = string.split(/\s+/).join(",")
		$.ajax({
            type: 'POST',
            url: "${contextPath}/cp_creategstin/${id}/${fullname}/${usertype}/${month}/${year}",
            data: {
            	"gstno":OptionList
            },
            success: function (result) {
            	window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";
            },
            error: function (result) {
            }
        });
	}
	function updateSelection(gstno,id,multigstnid,checkb){
		if(checkb.checked) {
			validatearry.push(id);
			multigstnarry.push(multigstnid);
		}else{
			var gArray=new Array();
			validatearry.forEach(function(no) {
				if(no != id) {
					gArray.push(id);
				}
			});
			validatearry = gArray;
			var multigstnArray=new Array();
			multigstnarry.forEach(function(no) {
				if(no != multigstnid) {
					multigstnArray.push(multigstnid);
				}
			});
			multigstnarry = multigstnArray;
		}
		var gstnoTableLength = $('#dbGSTINTable1').dataTable().fnGetData().length;
		if(validatearry.length == gstnoTableLength){
			$('#checkallgstno').prop("checked",true);
		}else{
			$('#checkallgstno').prop("checked",false);
		}
		if(validatearry.length >=1){
			$('.delete-btn').removeClass('disabled');
		}else{
			$('.delete-btn').addClass('disabled');
		}
	}
	function checkallgstno(checkb){
		var table = $('#dbGSTINTable1').DataTable();
		validatearry = new Array();
		multigstnarry = new Array();
		if(checkb.checked) {
			var rows = table.rows().nodes();
			$('#dbGSTINTable1').DataTable().rows().every(function () {
				var row = this.data();
				var gstn = row[1];
				validatearry.push(row.gstnid);
				multigstnarry.push(row.userid);
			});
			
			 $('input[type="checkbox"]', rows).prop('checked', checkb.checked);
			//$('#dbGSTINTable1').DataTable().draw();
		}else{
			var rows = table.rows().nodes();
			 $('input[type="checkbox"]', rows).prop('checked', checkb.checked);
		}
		if(validatearry.length >=1){
			$('.delete-btn').removeClass('disabled');
		}else{
			$('.delete-btn').addClass('disabled');
		}
	}
	function validateGSTno(){
		var accessMultiGSTNSearch='${user.accessMultiGSTNSearch}';
		if(accessMultiGSTNSearch == 'true'){
			if(validatearry.length > 0) {
				$.ajax({
					url: contextPath+"/validateSelectedgstnoss/${id}/"+month+"/"+year+"/"+validatearry,
					success : function(response) {
						window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";					
					}
				});
			} else {
				$('#dbGSTINTable1').DataTable().rows().every(function () {
					var row = this.data();
					var index = row[1];
					validatearry.push(index);
					row[0]='<div class="checkbox nottoedit" index="'+index+'"><label><input type="checkbox" checked onclick="updateSelection(\''+index+'\', this)"/><i class="helper"></i></label></div>';
					this.data(row);
					this.invalidate();
				});
				$.ajax({
					url: contextPath+"/validateSelectedgstnoss/${id}/"+month+"/"+year+"/"+validatearry,
					success : function(response) {
						window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";					
					}
				});
			}
		}else{			
			errorNotification('Your Access to the Multi GST Number Search Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		}
	}
	
	function validateGSTnonew(){
		var accessMultiGSTNSearch='${user.accessMultiGSTNSearch}';
		var table = $('#dbGSTINTable1').DataTable();
		if(accessMultiGSTNSearch == 'true'){
			$('#progress-bar').removeClass('d-none');
			$(".multigstntable").css('opacity','0.4');
			if(validatearry.length > 0) {
				if(validatearry.length <= 10){
					$('#progress-bar').removeClass('d-none').css("top","15%");
				}else if(validatearry.length <= 25){
					$('#progress-bar').removeClass('d-none').css("top","10%");
				}else if(validatearry.length <= 50){
					$('#progress-bar').removeClass('d-none').css("top","5%");
				}else if(validatearry.length <= 100){
					$('#progress-bar').removeClass('d-none').css("top","3%");
				}else{
					$('#progress-bar').removeClass('d-none').css("top","1%");
				}
				
				
					var arrayLength = validatearry.length;
					setTimeout(function(){
					for(var i = 0;i<validatearry.length;i++){
						var row = table.row('#row'+validatearry[i]).data();
						
						var index = row.gstin;
						$.ajax({
							url: contextPath+"/multigstn/${id}/"+month+"/"+year+"/"+index,
							async: false,
							beforeSend: function () {$("#progress-bar").removeClass('d-none');},
							success : function(response) {
							if(response.status_cd == '1') {
								if(response.data) {
									var tradename = response.data['tradeNam'];
									row[2] = ''+tradename+'';
									var dty = response.data['dty'];
									row[3] = ''+dty+'';
									var rgdt = response.data['rgdt'];
									row[4] = ''+rgdt+'';
									var lstupdt = response.data['lstupdt'];
									row[5] = ''+lstupdt+'';
									row[6] = '<span class="active-cls">VALID</span>';
								}
							}else{
								row[6] = '<span class="inactive-cls">'+response.status_desc+'</span>';
							}
							
								//window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}";					
							}
						});
						//table.row( '#row'+validatearry[i]).data(row).draw();
						arrayLength--;
						
						//table.fnUpdate(row, '#row'+validatearry[i], undefined, false);
					}
					if(arrayLength == 0){
						$('#progress-bar').addClass('d-none');
						$(".multigstntable").css('opacity','1');
						validatearry = new Array();
						window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";					
					}
				},1000);
			} else {
				$('#dbGSTINTable1').DataTable().rows().every(function () {
					var row = this.data();
					var gstn = row.gstin;
					var index = row.gstnid;
					validatearry.push(index);
					row[0]='<div class="checkbox nottoedit" index="'+index+'"><label><input type="checkbox" checked onclick="updateSelection(\''+gstn+'\',\''+index+'\', this)"/><i class="helper"></i></label></div>';
					this.data(row);
					this.invalidate();
				});
				$('#checkallgstno').prop("checked",true);
				var arrayLength = validatearry.length;
				setTimeout(function(){
					for(var i = 0;i<validatearry.length;i++){
						var row = table.row('#row'+validatearry[i]).data();
						var index = row.gstin;
						
						$.ajax({
							url: contextPath+"/multigstn/${id}/"+month+"/"+year+"/"+index,
							async: false,
							beforeSend: function () {$("#progress-bar").removeClass('d-none');},
							success : function(response) {
								
							if(response.status_cd == '1') {
								if(response.data) {
									var tradename = response.data['tradeNam'];
									row[2] = ''+tradename+'';
									var dty = response.data['dty'];
									row[3] = ''+dty+'';
									var rgdt = response.data['rgdt'];
									row[4] = ''+rgdt+'';
									var lstupdt = response.data['lstupdt'];
									row[5] = ''+lstupdt+'';
									row[6] = '<span class="active-cls">VALID</span>';
								}
							}else{
								row[6] = '<span class="inactive-cls">'+response.status_desc+'</span>';
							}
							
								//window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}";					
							}
						});
						//table.row( '#row'+validatearry[i]).data(row).draw();
						arrayLength--;
						
						//table.fnUpdate(row, '#row'+validatearry[i], undefined, false);
					}
					if(arrayLength == 0){
						$('#progress-bar').addClass('d-none');
						$(".multigstntable").css('opacity','1');
						validatearry = new Array();
						window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";					
					}
				},1000);

			}
			
		}else{			
			errorNotification('Your Access to the Multi GST Number Search Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		}
	}
	
	
	function showDeletePopup(){
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteProduct()");
	}
	function deleteProduct(){
		if(validatearry.length > 0) {
			$('#progress-bar-text').html("");
			$("#progress-bar").removeClass('d-none').css("top","0%");
			$(".multigstntable").css('opacity','0.4');
			var mObj=new Object();
		mObj.gstnid = validatearry;
		mObj.multigstin = multigstnarry;
		var selmt = new Array();
		selmt.push(mObj);
			setTimeout(function(){
			$.ajax({
				url: contextPath+"/deleteSelectedmultigstnos/${id}/"+month+"/"+year,
				type:'POST',
				async: false,
				cache: false,
				contentType: 'application/json',
				data: JSON.stringify(selmt),
				beforeSend: function () {$("#progress-bar").removeClass('d-none').css("top","0%");$(".multigstntable").css('opacity','0.4');},
					success : function(response) {
							window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";
						
					}
			});
			},1000);
			
		} else {
			deleteSelectedgstnos(new Array(),1,0);
		}
	}
	function deleteSelectedgstnos(dgstnarray,arrayLength,presentelement){
		$('#progress-bar-text').html("");
		$("#progress-bar").removeClass('d-none').css("top","0.5%");
		setTimeout(function(){
		$.ajax({
			url: contextPath+"/deleteSelectedgstnos/${id}/"+month+"/"+year+"/"+dgstnarray,
			beforeSend: function () {$("#progress-bar").removeClass('d-none').css("top","0%");$(".multigstntable").css('opacity','0.4');},
				success : function(response) {
					if(arrayLength == presentelement+1){
						window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";
					}
				}
		});
		},1000);
	}
	function performMgImport(btn) {
		$(btn).addClass('btn-loader');
		var filename =  $('#messagesgstno').html();
		if(filename != ''){
			$('#itemsimportform').ajaxSubmit( {
				url: $("#itemsimportform").attr("action"),
				dataType: 'json',
				type: 'POST',
				cache: false,
				success: function(response) {
					$(btn).removeClass('btn-loader');
					window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";
				},
				error: function(e) {
					$(btn).removeClass('btn-loader');
					window.location.href = "${contextPath}/cp_multiGtstin/${id}/${fullname}/${usertype}/${month}/${year}?type=multigstin";
				}
			});
		}else{$('#multigsterrormsg').css("display","block")}
	}
</script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map-gstno.js" type="text/javascript"></script>
</body>
<script type="text/javascript">
loadmultigstntablesTable('${id}', '${month}', '${year}', '${usertype}', '${fullname}');
</script>
</html>