<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Custom Imports</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/mapimports/mapimports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/meterial-form.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/echarts/echarts.js"></script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/echarts/echartsTheme.js"></script>
<style>.astrich::after{ margin-left:-5px}</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>  
  <!--- breadcrumb start -->
<!--- breadcrumb start -->
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">Imports</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>

        <!--- breadcrumb end -->

	<div class="container-fluid">
  <!-- news list begin -->
  <div class="container">
    <div class="mapimport-wrap">
      <div class="subscriptwrap">
        <h3 class="hdr-txt mb-4">Mapping Fields</h3>
        <div class="stepwizard-wrap">
          <div class="stepwizard">
            <div class="stepwizard-row setup-panel online2">
              <div class="stepwizard-step"> <a href="#step-1" class="btn btn-primary btn-circle"><span class="snum"><span class="rounded-circle"> <i class="fa fa-upload"></i> </span>Upload Excel Sheet</span></a> </div>
              <div class="stepwizard-step"> <a href="#step-2" class="btn btn-default btn-circle" disabled="disabled"><span class="snum"> <span class="rounded-circle"> <i class="fa fa-mars-double"></i></span>Map Parameters</span></a> </div>
<!--               <div class="stepwizard-step"> <a href="#step-3" class="btn btn-default btn-circle" disabled="disabled"><span class="snum"><span class="rounded-circle"> <i class="fa fa-eye"></i> </span>Review Parameters</span></a> </div> -->
            </div>
          </div>
          <!-- stepy form -->
         <input type="hidden" id="mapuserId" name="userId" value="${id }"/>
       <input type="hidden" id="mapclientId" name="clientId" value="${client.id}"/>
       <input type="hidden" id="MapperId" name="MapperId" value=""/>
          <div class="stepwizard-iner">
          <!-- stepy form 1 -->
          <form id="upload"  class="form-inline " name="MappingFileForm" id="MappingFileForm" method="POST" enctype="multipart/form-data">
          <div class="setup-content" id="step-1" style="width:100%">
          <div class="meterialform col-md-12 row m-auto" style="width: 65%;">
          <p class="col-md-6 text-right pr-4">Import Mapper Type</p>
		  <div class="form-group-inline col-md-6 pl-0" style="display: inline-block;height:20px;;">
				<div class="form-radio">
					<div class="radio">
						<label><input name="simplifiedOrDetail" id="submiton" type="radio" value="Detailed" /><i class="helper"></i>Detailed</label>
					</div>
				</div>
				<div class="form-radio">
					<div class="radio">
						<label><input name="simplifiedOrDetail" id="subon" type="radio" value="Simplified" checked/><i class="helper"></i>Simplified</label>
					</div>
				</div>
			</div>
		  </div>
		  <!-- <div class="detail" style="display:block">IGST Rate,IGST Amount,CGST Rate,CGST Amount,SGST Rate,SGST Amount Fields are Available</div>
		  <div class="simplified" style="display:none">TaxRate Field is Available</div> -->
		  <div>
		  <span class="errormsg col-md-12 " id="MapperName_Msg" style="margin-top:-16px;width:51%"></span>
		   <div class="form-group col-md-12 m-auto"  style="width:50%">
		    <p class="col-sm-6 text-right" style="display:block">Import Mapping Name  <span class="astrich"></span></p>
           <input type="text" class="form-control col-md-6" id="MapperName" name="MapperName" aria-describedby="nameInput" placeholder="Enter Import Mapping Name">
          </div>
		  </div>
		  <div class="">&nbsp;</div>
		   <span class="errormsg col-md-12 " id="MapperType_Msg"  style="width:51%"></span>
          <div class="form-group col-md-12 col-sm-12 m-auto"   style="width:50%" >
                  <p class="col-md-6 text-right">Choose Invoice Template Type <span class="astrich"></span></p>
                  <select class="form-control col-md-6" id="MapperType" name="MapperType" >
					<option value="">- Choose invoice template -</option>
                    <option value="Sales">Sales Invoice</option>
                    <option value="Purchases">Purchase Invoice</option>
                    <option value="einvoice">E-invoice</option>
                  </select>
         </div>
         <div class="">&nbsp;</div>
         <div>
		  <span class="errormsg col-md-12 " id="MapperName_Msg"  style="width:51%" ></span>
		   <div class="form-group col-md-12 m-auto"   style="width:50%">
		    <p class="col-sm-6 text-right">Row Number for Header Fields <span class="astrich"></span></p>
           <input type="text" class="form-control col-md-6" id="skipRows" name="skipRows" aria-describedby="nameInput" placeholder="Enter Import Mapping Name" value="1">
          </div>
		  </div>
		  
          <div class="col-12 m-auto" style="display:block;width:55%" >
            <fieldset style="margin-top:  15px;">
            <div class="small text-right">* Please upload Excel format only(File Size Should be lessthan 1MB).</div>
            <div class="filedragwrap" onclick="choosefileImport()">
              <div  id="filedrag">
                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000" />
                <div class="filedraginput"> <span class="choosefile">Choose File</span>
                 <span class="errormsg" id="fileselect_Msg"></span>
                  <input type="file" id="fileselect" name="fileselect[]" multiple="multiple" onchange = "get_detail()"/>
                </div>
                <div class="ortxt"> --( OR )--</div>
                <div>Drop file here</div>
                <div id="submitbutton1">
                  <button type="submit">Upload Files</button>
                </div>
              </div>
            </div>
            </fieldset>
          <div class="form-group col-md-12 col-sm-12 m-auto">
            <div class="shadowbox" id="viewfiles">
              <h5>Uploaded/Selected Document Name</h5>
              <div id="messages1"> </div>
            </div>
          </div>
          <div class="form-group col-md-12 col-sm-12 m-auto">
            <div class="shadowbox" id="viewfiles1">
              <p class="ferrormsg" id="file_detail"></p>
            </div>
          </div>
        </div>
        <div class="form-group  col-sm-12 col-xs-12 mt-3 m-auto" style="display:block;text-align:center;">
          <button class="btn btn-blue cancelbut mr-1" type="button">Cancel</button>
		  <button class="btn btn-blue-dark nextBtn first_nxt" data-val="first" type="button" >Next</button>
        </div>
      </div>
          </form>
      
      <!-- stepy form 2 -->
       <form role="form" name="MappingFieldsForm" id="MappingFieldsForm" method="post" class="dbform bs-radio" enctype="multipart/form-data">
       
      <div class="setup-content" id="step-2">
        <div class="row">
          <div class="col-sm-12 customtable-wrap">
            <table id="pageTable" class="display row-border dataTable meterialform" cellspacing="0" cellpadding="10" width="100%">
              <thead>
                <tr>
                  <th class="hdr_w_25"><span class="tbl-hdr">GST Invoice Types</span></th>
                  <th class="hdr_w_35"><span class="tbl-hdr">Invoice Mapping Status</span></th>
                  <th class="hdr_w_40"><span class="tbl-hdr">Excel Sheet/Invoice Name</span></th>          
                </tr>
              </thead>
              <tbody>
              </tbody>
            </table>
          </div>

          <div class="form-group col-sm-12 col-xs-12 mt-3">
          <div class="customtable"style="margin-top:30px"><div class="meterialform"><div class="col-md-4 col-sm-12"><div class="form-check" ><div class="meterialform"><div class="checkbox" id="tcsvaldiv">
                        <label><input class="globaltemplate" id="globaltemplate" type="checkbox" name="globaltemplate"  onchange="globaltemplatecheckval()" value="false"><i class="helper"></i><strong> <span>Make it as Global template</span></strong></label>
             </div></div></div></div></div>
             <p>(On enable this, template is given to all Clients.)</p>
             </div>
        <button class="btn btn-blue-dark nextBtn pull-right second_nxt saveallmappings" type="button"  data-val="second" title="Please Configure atleast one sheet." disabled>Save</button>
			<button class="btn btn-blue pull-right cancelmapping mr-2" type="button">Cancel</button>
			<button class="btn btn-default prevBtn pull-right edit_pre mr-2" type="button" style="width:10%;color: #000!important; font-size: 14px; padding: 10px 30px; text-transform: uppercase;">BACK</button>
			<a href="javascript:void()" onclick="clearAllMappings('mapval')" class="btn btn-default pull-right mr-2">Clear All Mappings</a>
          </div>
        </div>
      </div>
      <!-- stepy form 3-->
      <div class="setup-content" id="step-3">
        <div class="row">
          <div class="col-sm-12 customtable-wrap">
            <table id="previewTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
               <thead>
                <tr>
                  <th  width="25%"><span class="tbl-hdr">GST Fields</span></th>
                  <th  width="50%"><span class="tbl-hdr">Mapping Status</span></th>
                  <th  width="25%"><span class="tbl-hdr">Excel Fields</span></th>          
                </tr>
              </thead>
              <tbody>
<%--                <c:forEach var="field" items="${fields}"> --%>
<!--                 <tr> -->
<%--                    <td>${field.name}</td> --%>
<%--                    <td><img src="${contextPath}/static/mastergst/images/master/arrow-right.png" alt="True" class="img-fluid"> <span class="action-img"><img src="${contextPath}/static/mastergst/images/master/false.png" alt="True"></span> <img src="${contextPath}/static/mastergst/images/master/arrow-left.png" alt="True" class="img-fluid"></td> --%>
<%--                	   <td id="${field.code}_val_preview">Fields 1</td> --%>
<!--                 </tr> -->
<%--                 </c:forEach> --%>
              </tbody>
            </table>
          </div>
          <div class="form-group  col-sm-12 col-xs-12  mt-3">
            <button class="btn btn-default prevBtn pull-left col-md-3 col-sm-12 preview_pre" type="button">Back</button>
            <button id="mapviewModal" class="btn btn-success pull-right col-md-3 col-sm-12" type="button"  onclick="submitMapper(this)">Confirm &amp;  Continue</button>
          </div>
        </div>
      </div>
    </form>
    </div>
  </div>
</div>
<!--- mapviewModal-wrap --->
<div class="mapviewModal-wrap">
  <div class="customtable">
    <h3 class="hdr-txt mb-3">Import Templates: 
      <button type="button" class="btn btn-blue-dark pull-right mb-2 create-new">Create New Import Mapping</button>
    </h3>
	<h6 class="mb-3" style="font-size:14px; font-weight: 300;">Using this tool, user can create his own custom Import Sales / Purchases Templates. </h6>
    <table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th> S.No. </th>
          <th>Import Mapping Name</th>
          <th>Type</th>
          <th>Creation Date</th>
		  <th>Configure Status</th>
          <th class="text-center">Action</th>
        </tr>
      </thead>
      <tbody>
      <c:if  test="${mapperslist ne null}">
      <c:forEach var="mapper" items="${mapperslist}" varStatus="lp">
      <tr id="row${mapper.id}">
          <td> ${lp.index+1} </td>
          <td>${mapper.mapperName}</td>
          <td>${mapper.mapperType}</td>
          <td><c:choose>
          		<c:when test="${not empty mapper.createdDate }">
          		${mapper.createdDate}
          		</c:when>
         	 	<c:otherwise>
         	 	${mapper.updatedDate}
         	 	</c:otherwise>
         	 </c:choose>
          </td>
		  <c:choose>
          		<c:when test="${mapper.isCompleted eq true}">
          		<td style="color:#008000">Configured</td>
          		</c:when>
         	 	<c:otherwise>
         	 	<td style="color:red">Not Configured</td>
         	 	</c:otherwise>
         	 </c:choose>
          <td align="center"><c:choose><c:when test="${id eq mapper.userId && client.id eq mapper.clientId}"><a href="javascript:void()" onclick="editMapper('${mapper.id}')"><img src="${contextPath}/static/mastergst/images/master/editdd.png" alt="Edit" /> </a> <a href="javascript:void()" onclick="showDeletePopup('${mapper.id}', '${mapper.mapperName}')" class="ml-1"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" /> </a> </c:when><c:otherwise><a href="javascript:void()" data-toggle="tooltip" title="Please Contact Your Admin User." disabled><img src="${contextPath}/static/mastergst/images/master/editdd.png" alt="Edit" /> </a> <a href="javascript:void()" class="ml-1" data-toggle="tooltip" title="Please Contact Your Admin User." disabled ><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" /> </a> </c:otherwise></c:choose></td>
        </tr>
        </c:forEach>
      </c:if>
      </tbody>
    </table>
  </div>
</div>
<!--- mapviewModal-wrap --->
</div>
<!-- news list end -->
</div>
<!-- bodycontainer page end -->
</div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
    
<div class="modal fade modal-right viewmodal model-wd" id="viewModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content model-wd">
      <div class="modal-body meterialform popupright bs-fancy-checks" style="margin-right:14px">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close" id="fild_modl_close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
         <div id="model_cont"></div>
         </div>
    </div>
  </div>
</div>
<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Mapper </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Mapper <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Mapper</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
    <script src="${contextPath}/static/mastergst/js/common/stepy-wizards.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map1.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function() {
	var clientIds = '<c:out value="${client.id}"/>';
	var type = '<c:out value="${type}"/>';
	if(type == 'savemapping'){
		successNotification('Saved Data Successfully!!');
	}
});
$('.btn.btn-primary').removeClass('prevstep');
$('.stepwizard-step .btn.btn-circle').on('click', function(){
	$(this).parent().prevAll().addClass('prevstep');
		$(this).parent().nextAll().removeClass('prevstep');
		$(this).parent().removeClass('prevstep');
});
$('.subscriptwrap').css('display', 'none');
$('.mapviewModal-wrap').css('display', 'block');
function choosefileImport(){
	$('#fileselect')[0].click();
}
function get_detail(){
 var size=$('#fileselect')[0].files[0].size;
 var file = Math.round((size / 1024));
	if(file > 1024){
		$("#file_detail").html("Please select a file less than 1MB");		
	}else{
		$("#file_detail").html("");
	}
 
}
$('.create-new').click(function(){
	$('saveallmappings').attr("disabled",true);
	$('#MapperName').val('');
	$('#MapperId').val('');
	$('#messages1').html('');
	$('#MapperName_Msg').html('');
	$('#MapperType_Msg').html('');
	$('#file_detail').html('');
	$('.edit_pre').attr('disabled', false);
	$('#fileselect').attr('files', []);
	mapTable.clear().draw();
	previewTable.clear().draw();
	$('.mapviewModal-wrap').css('display', 'none');
	$('.subscriptwrap').css('display', 'block');
});

$('.cancelbut').on('click', function(){
	$('.subscriptwrap').css('display', 'none');
	$('.mapviewModal-wrap').css('display', 'block');
});
$('.saveallmappings').click(function(){
	var mapperid = $('#MapperId').val();
	if(mapperid != ''){
		 var globaltemplate = $('#globaltemplate').val();
	 	$.ajax({
			url: '${contextPath}/saveGlobalTemplate/<c:out value="${id}"/>/'+mapperid+'/'+globaltemplate, 
			 method: 'GET',
			  processData: false,
			  contentType: 'application/json',
			success: function(result, textSt, xhr){
				window.location.href = '${contextPath}/imports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=savemapping';
			},
			error: function(result){
				window.location.href = '${contextPath}/imports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=savemapping';
			}
		});
	}else{
		window.location.href = '${contextPath}/imports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=savemapping';
	}
	
});
$('.cancelmapping').click(function(){
	window.location.href = '${contextPath}/imports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=cancelmapping';
});
$("input[name='simplifiedOrDetail']").click(function(){
	var status = $("input[name='simplifiedOrDetail']:checked").val();
	if(status == 'Detailed'){
		$('.detail').css("display","block");
		$('.simplified').css("display","none");
	}else{
		$('.detail').css("display","none");
		$('.simplified').css("display","block");
	}
});
function showDeletePopup(mapperid, name) {
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteMappers('"+mapperid+"')");
	}

 function deleteMappers(mapperid) {	
	var uri = contextPath +'/deletemapper/'+mapperid;
	  $.ajax({
		  url: uri, 
		  method: 'GET',
		  processData: false,
		  contentType: 'application/json',
		  success: function(result, textSt, xhr){
			  dtable.row( $('#row'+mapperid) ).remove().draw();
		  },
		  error: function(result){
			  
		  }
	  });
	}	
     </script>
  </body>
</html>