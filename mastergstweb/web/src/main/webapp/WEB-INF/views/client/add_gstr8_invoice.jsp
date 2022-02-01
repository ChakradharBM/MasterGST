<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
   <head>
      <title>MasterGST - GST Software |File GSTR8</title>
      <%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
      <script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
      <script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
      <script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
      <script src="${contextPath}/static/mastergst/js/client/gstr8.js" type="text/javascript"></script>
      
      <link rel="stylesheet" href="${contextPath}/static/mastergst/css/client/gstr8.css" media="all" />
      <c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
      <c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
      <c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
   </head>
   <body class="body-cls suplies-body">
      <!-- header page begin -->
      <%@include file="/WEB-INF/views/includes/client_header.jsp"%>
      <!--- breadcrumb start -->
      <div class="breadcrumbwrap nav-bread">
         <div class="container">
            <div class="row">
               <div class="col-md-12 col-sm-12">
                  <ol class="breadcrumb">
                     <li class="breadcrumb-item">
                        <a href="#" class="urllink" link="${contextPath}/cdb/${id}/${fullname}/${usertype}">
                        <c:choose>
                           <c:when test="${usertype eq userCA || usertype eq userTaxP}">Clients</c:when>
                           <c:otherwise>Business</c:otherwise>
                        </c:choose>
                        </a>
                     </li>
                     <li class="breadcrumb-item">
                        <a href="#" class="urllink" link="${contextPath}/ccdb/${id}/${fullname}/${usertype}/${clientid}?type=change">
                        <c:choose>
                           <c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when>
                           <c:otherwise>${client.businessname}</c:otherwise>
                        </c:choose>
                        </a>
                     </li>
                     <li class="breadcrumb-item">${returntype}</li>
                  </ol>
                  	<span class="datetimetxt"> 
						<input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  
					</span>
                 	<span class="retutprdntxt">
						Return Period: 
					</span>
					<span class="dropdown chooseteam">
					  <span id="fillingoption"><b>Filing Option:</b> <span id="filing_option">Monthly</span></span>
					</span>
                  <div class="retresp"></div>
               </div>
            </div>
         </div>
      </div>
      <!--- breadcrumb end -->
      <div class="db-ca-wrap db-ca-gst-wrap">
         <div class="container" style="min-height: 400px">
            <div class="row">
               <div class="col-md-12 col-sm-12">
	               <div class="col-md-12 col-sm-12">
					<a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark permissionUpload_Invoice pull-right ml-2" onclick="uploadInvoice('${id}', '${usertype}', '${clientid}', '${returntype}', '${month}', '${year}')">Upload to GSTIN</a>
					<!-- <a href="#" class="btn btn-greendark permissionFile_Invoice pull-right ml-2"  data-toggle="modal" data-target="#fileReturnModal" id="idTrueCopyBtn">File GSTR8 with DSC</a>  
					<a href="#" class="btn btn-greendark permissionFile_Invoice pull-right ml-2" onclick="evcFilingOTP()" id="idEVCBtn">File GSTR8 with EVC</a> -->
					</div>
                  <div class="gstr-info-tabs">
                     <ul class="nav nav-tabs" role="tablist">
                        <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#gtab1" role="tab">GSTR 8</a></li>
                     </ul>
                     <!-- Tab panes -->
                     <div class="tab-content">
                        <!-- Tab panes 1-->
                        <div class="tab-pane active" id="gtab1" role="tabpane1">
                              <div class="group upload-btn"></div>
                              <div class="col-md-12 col-sm-12">
                                 <div class="gstr-info-tabs">
                                    <ul class="nav nav-tabs" role="tablist">
                                       <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#main_tab1" role="tab"><span class="serial-num">1</span>Part II</a></li>
                                       <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#main_tab2" role="tab"><span class="serial-num">2</span>Part III</a></li>
                                       <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#main_tab3" role="tab"><span class="serial-num">3</span>Part IV</a></li>
                                       <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#main_tab4" role="tab"><span class="serial-num">4</span>Part V</a></li>
                                       <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#main_tab7" role="tab"><span class="serial-num">5</span>Part VI</a></li>
                                       <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#main_tab5" role="tab"><span class="serial-num">6</span>Part VII</a></li>
                                       <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#main_tab6" role="tab"><span class="serial-num">7</span>Part VIII</a></li>
                                    </ul>
                                    <!-- Tab panes -->
                                    <div class="tab-content">
                                       <!-- Tab panes 1-->
                                       <div class="tab-pane active" id="main_tab1" role="tabpane1">
				                           <form:form method="POST" id="gstr8Form" data-toggle="validator" class="meterialform invoiceform" name="gstr8invoceform" action="${contextPath}/saveecominvoice/${returntype}/${usertype}/${month}/${year}" modelAttribute="invoice">
                                          	<div class="panel panel-default m-b-0">
                                             <!--- 3.1 --->
                                             <div class="group upload-btn">
                                                <div class="mb-2"> <span class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_1"> Help Guide</span><span class="pull-right"> <a href="#" class="btn btn-sm btn-blue-dark tpone-edit " onClick="clickEdit('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Edit</a>
                                                   <a href="#" class="btn btn-sm btn-blue-dark tpone-save" style="display: none" onClick="clickSave('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Save</a>
                                                   <a href="#" class="btn btn-sm btn-blue-dark tpone-cancel" style="display: none" onClick="clickCancel('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Cancel</a></span> 
                                                </div>
                                             </div>
                                             <div id="accordion" class="inneracco">
                                                <div class="card">
                                                   <div class="card-header" id="headinginnerOne">
                                                      <h5 class="mb-0">
                                                         <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerOne" aria-expanded="true" aria-controls="collapseinnerOne">3. Details of supplies made through e-commerce operator</button>
                                                      </h5>
                                                   </div>
                                                   <div id="collapseinnerOne" class="collapse show" aria-labelledby="headinginnerOne" data-parent="#accordion">
                                                      <div class="card-body p-2">
                                                         <div class="customtable db-ca-gst tabtable1 mt-2">
                                                            <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                                               <thead>
                                                                  <tr>
                                                                     <th class="text-left" rowspan="2">GSTIN of the supplier</th>
                                                                     <th class="text-center" colspan="3">Details of supplies made which attract TCS</th>
                                                                     <th class="text-center" colspan="3">Amount of tax collected at source</th>
                                                                     <th class="text-center" rowspan="2">action</th>
                                                                  </tr>
                                                                  <tr>
                                                                     <th class="text-left">Gross value of supplies made</th>
                                                                     <th class="text-left">Value of supplies returned</th>
                                                                     <th class="text-left">Net amount liable for TCS</th>
                                                                     <th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
                                                                     <th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
                                                                     <th class="text-left">State /UT Ta(<i class="fa fa-rupee"></i>)</th>
                                                                  </tr>
                                                               </thead>
                                                               <tbody>
                                                                  <table class="display row-border dataTable meterialform dbTable1a" id="table3a">
                                                                     <tr>
                                                                        <td class="text-left" colspan="8">3A. Supplies made to registered persons</td>
                                                                     </tr>
                                                                     <c:if test="${empty gstr8Inv.tcsR}">
	                                                                     <tr>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsR[0].stin" class="form-control tpone-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsR[0].supR" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsR[0].retsupR" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsR[0].amt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsR[0].iamt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" class="form-control tpone-input" name="tcsR[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" class="form-control tpone-input" name="tcsR[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('1a')">+</a></td>
	                                                                     </tr>
                                                                     </c:if>
                                                                     <c:if test="${not empty gstr8Inv.tcsR}">
                                                                     	<c:forEach items="${gstr8Inv.tcsR}" var="item" varStatus="loop">
	                                                                    	<tr>
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                        <input type="text" readonly="true" name="tcsR[${loop.index}].stin" value="${item.stin}" class="form-control tpone-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" />
		                                                                        <div class="help-block with-errors"></div>
		                                                                     </td>
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                        <input type="text" readonly="true" name="tcsR[${loop.index}].supR"  value="${item.supR}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                        <div class="help-block with-errors"></div>
		                                                                     </td>
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                        <input type="text" readonly="true" name="tcsR[${loop.index}].retsupR" value="${item.retsupR}"  class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                        <div class="help-block with-errors"></div>
		                                                                     </td>
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                        <input type="text" readonly="true" name="tcsR[${loop.index}].amt" value="${item.amt}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                        <div class="help-block with-errors"></div>
		                                                                     </td>
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                        <input type="text" readonly="true" name="tcsR[${loop.index}].iamt" value="${item.iamt}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                        <div class="help-block with-errors"></div>
		                                                                     </td>
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                        <input type="text" readonly="true" name="tcsR[${loop.index}].camt" value="${item.camt}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                        <div class="help-block with-errors"></div>
		                                                                     </td>
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                        <input type="text" readonly="true" name="tcsR[${loop.index}].samt" value="${item.samt}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                        <div class="help-block with-errors"></div>
		                                                                     </td>
		                                                                  	 <c:if test='${loop.index == 0}'>
			                                                                     <td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('1a')">+</a></td>
			                                                                 </c:if>
			                                                                 <c:if test='${loop.index > 0}'>
				                                                                 <td class="delmorewrap1" style="display:none;"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem('${loop.index}','1a')" class="delrow"></td>
			                                                                 </c:if>
		                                                                  </tr>
                                                                   		</c:forEach>
                                                                  	</c:if>
                                                                    
                                                                  </table>
                                                                  <table class="display row-border dataTable meterialform dbTable1b" id="table3b">
                                                                     <tr>
                                                                        <td class="text-left" colspan="8">3B. Supplies made to unregistered persons</td>
                                                                     </tr>
                                                                     <c:if test="${empty gstr8Inv.tcsU}">
	                                                                     <tr>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsU[0].stin" class="form-control tpone-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsU[0].supU" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsU[0].retsupU" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsU[0].amt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsU[0].iamt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsU[0].camt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsU[0].samt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('1b')">+</a></td>
	                                                                     </tr>
	                                                                    </c:if>
	                                                                    <c:if test="${not empty gstr8Inv.tcsU}">
	                                                                    	<c:forEach items="${gstr8Inv.tcsU}" var="item" varStatus="loop">
	                                                                    		<tr>
			                                                                        <td class="text-right form-group gst-3b-error">
			                                                                           <input type="text" readonly="true" name="tcsU[${loop.index}].stin" class="form-control tpone-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" />
			                                                                           <div class="help-block with-errors"></div>
			                                                                        </td>
			                                                                        <td class="text-right form-group gst-3b-error">
			                                                                           <input type="text" readonly="true" name="tcsU[${loop.index}].supU" value="${item.supU}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
			                                                                           <div class="help-block with-errors"></div>
			                                                                        </td>
			                                                                        <td class="text-right form-group gst-3b-error">
			                                                                           <input type="text" readonly="true" name="tcsU[${loop.index}].retsupU" value="${item.retsupU}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
			                                                                           <div class="help-block with-errors"></div>
			                                                                        </td>
			                                                                        <td class="text-right form-group gst-3b-error">
			                                                                           <input type="text" readonly="true" name="tcsU[${loop.index}].amt" value="${item.amt}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
			                                                                           <div class="help-block with-errors"></div>
			                                                                        </td>
			                                                                        <td class="text-right form-group gst-3b-error">
			                                                                           <input type="text" readonly="true" name="tcsU[${loop.index}].iamt" value="${item.iamt}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
			                                                                           <div class="help-block with-errors"></div>
			                                                                        </td>
			                                                                        <td class="text-right form-group gst-3b-error">
			                                                                           <input type="text" readonly="true" name="tcsU[${loop.index}].camt" value="${item.camt}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
			                                                                           <div class="help-block with-errors"></div>
			                                                                        </td>
			                                                                        <td class="text-right form-group gst-3b-error">
			                                                                           <input type="text" readonly="true" name="tcsU[${loop.index}].samt" value="${item.samt}" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
			                                                                           <div class="help-block with-errors"></div>
			                                                                        </td>
			                                                                        <c:if test='${loop.index == 0}'>
				                                                                        <td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('1b')">+</a></td>
			                                                                        </c:if>
			                                                                         <c:if test='${loop.index > 0}'>
				                                                                        <td class="delmorewrap1" style="display:none;"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem('${loop.index}','1b')" class="delrow"></td>
			                                                                        </c:if>
			                                                                     </tr>
			                                                                  </c:forEach>
	                                                                    </c:if>
                                                                  </table>
                                                               </tbody>
                                                            </table>
                                                         </div>
                                                      </div>
                                                   </div>
                                                </div>
                                             </div>
                                          </div>
                                          <div class="col-sm-12 mt-4 text-center">
			                                 <c:if test="${not empty gstr8Inv && not empty gstr8Inv.id}">
			                                    <input type="hidden" name="id" value="${gstr8Inv.id}"> 
			                                 </c:if>
			                                 <input type="hidden" name="userid" value="${id}">
			                                 <input type="hidden" name="fullname" value="${fullname}">
			                                 <input type="hidden" name="clientid" value="${clientid}"> 
			                              </div>
                                       </form:form>
                                       </div>
                                       <div class="tab-pane" id="main_tab2" role="tabpanel">
	                                       <form:form method="POST" id="gstr8FormA" data-toggle="validator" class="meterialform invoiceform" name="gstr8invoceform" action="${contextPath}/saveecomainvoice/${returntype}/${usertype}/${month}/${year}" modelAttribute="invoice">
	                                          	<div class="group upload-btn">
		                                             <div class="mb-2">
		                                                <div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_2">Help Guide</div>
		                                                <span class="pull-right"> <a href="#" class="btn btn-sm btn-blue-dark tptwo-edit" onClick="clickEdit('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>
		                                                <a href="#" class="btn btn-sm  btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Save</a>
		                                                <a href="#" class="btn btn-sm  btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap',2);">Cancel</a></span> 
		                                             </div>
	                                          	</div>
                                          	<div id="accordion" class="inneracco">
                                             <div class="card">
                                                <div class="card-header" id="headinginnerThree">
                                                   <h5 class="mb-0">
                                                      <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerThree" aria-expanded="true"
                                                         aria-controls="collapseinnerThree">4. Amendments to details of supplies in respect of any earlier statement</button>
                                                   </h5>
                                                </div>
                                                <div id="collapseinnerThree" class="collapse show" aria-labelledby="headinginnerThree" data-parent="#accordion">
                                                   <div class="card-body p-2">
                                                      <div class="customtable db-ca-gst tabtable2 mt-2">
                                                         <table id="dbTable2" class="dbTable2 display row-border dataTable meterialform" cellspacing="0" width="100%">
                                                            <thead>
                                                               <tr>
                                                                  <th class="text-center" colspan="2">Original details </th>
                                                                  <th class="text-center" colspan="7">Revised details </th>
                                                               </tr>
                                                               <tr>
                                                                  <th class="text-left" rowspan="2">Month</th>
                                                                  <th class="text-left" rowspan="2">GSTIN of supplier </th>
                                                                  <th class="text-left" rowspan="2">GSTIN of supplier </th>
                                                                  <th class="text-center" colspan="3">Details of supplies made which attract TCS </th>
                                                                  <th class="text-center" colspan="3">Amount of tax collected at source </th>
                                                               </tr>
                                                               <tr>
                                                                  <th class="text-left">Gross value of supplies made(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Value of supply returned(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Net amount liable for TCS(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Central Tax(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>) </th>
                                                               </tr>
                                                            </thead>
                                                            <tbody>
                                                               <tr class="">
                                                                  <td class="text-left" colspan="9">4A. Supplies made to registered persons </td>
                                                               </tr>
                                                               <table class="display row-border dataTable meterialform dbTable2a" id="table4a">
                                                                	<c:if test="${empty gstr8Inv.tcsaR}">
		                                                                <tr>
	                                                                     <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaR[0].stin" class="form-control tptwo-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaR[0].supR" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaR[0].retsupR" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaR[0].amt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaR[0].iamt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" class="form-control tptwo-input" name="tcsaR[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" class="form-control tptwo-input" name="tcsaR[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('2a')">+</a></td>
	                                                                  </tr>
	                                                                  </c:if>
	                                                                  <c:if test="${not empty gstr8Inv.tcsaR}">
	                                                                    <c:forEach items="${gstr8Inv.tcsaR}" var="item" varStatus="loop">
		                                                                    <tr>
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaR[${loop.index}].stin" value="${item.stin}" class="form-control tptwo-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaR[${loop.index}].supR" value="${item.supR}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaR[${loop.index}].retsupR" value="${item.retsupR}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaR[${loop.index}].amt" value="${item.amt}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaR[${loop.index}].iamt" value="${item.iamt}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaR[${loop.index}].camt" value="${item.camt}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaR[${loop.index}].samt" value="${item.samt}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <c:if test='${loop.index == 0}'>
			                                                                        <td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('2a')">+</a></td>
		                                                                        </c:if>
 	                                                                         	<c:if test='${loop.index > 0}'>
				                                                                    <td class="delmorewrap1" style="display:none;"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem('${loop.index}','2a')" class="delrow"></td>
			                                                                    </c:if>
		                                                                  </tr>
	                                                                  </c:forEach>
	                                                                  </c:if>
                                                               </table>
                                                               <table class="display row-border dataTable meterialform dbTable2b" id="table4b">
                                                                  <tr class="">
                                                                     <td class="text-left" colspan="9">4A. Supplies made to registered persons </td>
                                                                  </tr>
                                                                  	<c:if test="${empty gstr8Inv.tcsaU}">
                                                                  		<tr class="dbTable_2b">
	                                                                     <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaU[${loop.index}].stin" class="form-control tptwo-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaU[${loop.index}].supU" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaU[${loop.index}].retsupU" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaU[${loop.index}].amt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" name="tcsaU[${loop.index}].iamt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" class="form-control tptwo-input" name="tcsaU[${loop.index}].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="text-right form-group gst-3b-error">
	                                                                           <input type="text" readonly="true" class="form-control tptwo-input" name="tcsaU[${loop.index}].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
	                                                                           <div class="help-block with-errors"></div>
	                                                                        </td>
	                                                                        <td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('2b')">+</a></td>
	                                                                  </tr>
	                                                                 </c:if>
	                                                                 <c:if test="${not empty gstr8Inv.tcsaU}">
	                                                                 	<c:forEach items="${gstr8Inv.tcsaU}" var="item" varStatus="loop">
	                                                                  		<tr class="dbTable_2b">
		                                                                     <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaU[0].stin" value="${item.stin}" class="form-control tptwo-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaU[0].supU" value="${item.supU}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaU[0].retsupU" value="${item.retsupU}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaU[0].amt" value="${item.amt}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaU[0].iamt" value="${item.iamt}" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaU[0].camt" value="${item.camt}"class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                        <td class="text-right form-group gst-3b-error">
		                                                                           <input type="text" readonly="true" name="tcsaU[0].samt"  value="${item.samt}"class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
		                                                                           <div class="help-block with-errors"></div>
		                                                                        </td>
		                                                                       	 <c:if test='${loop.index == 0}'>
			                                                                        <td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('2b')">+</a></td>
		                                                                        </c:if>
 	                                                                         	<c:if test='${loop.index > 0}'>
				                                                                    <td class="delmorewrap1" style="display:none;"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem('${loop.index}','2b')" class="delrow"></td>
			                                                                    </c:if>
		                                                                       
		                                                                  </tr>
	                                                                  </c:forEach>
	                                                                 </c:if>
                                                               </table>
                                                            </tbody>
                                                         </table>
                                                      </div>
                                                   </div>
                                                </div>
                                             </div>
                                          </div>
                                          <div class="col-sm-12 mt-4 text-center">
		                                 <c:if test="${not empty gstr8Inv && not empty gstr8Inv.id}">
		                                    <input type="hidden" name="id" value="${gstr8Inv.id}"> 
		                                 </c:if>
		                                 <input type="hidden" name="userid" value="${id}">
		                                 <input type="hidden" name="fullname" value="${fullname}">
		                                 <input type="hidden" name="clientid" value="${clientid}"> 
		                              </div>
                                       </form:form>
                                       </div>
                                       <div class="tab-pane" id="main_tab3" role="tabpanel">
                                          <div class="group upload-btn">
                                             <div class="mb-2">
                                                <div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_3">Help Guide</div>
                                                <span class="pull-right"> <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpthree-edit"
                                                   onClick="clickEdit('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input');">Edit</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpthree-cancel"
                                                   onClick="clickSave('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input');">Save</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpthree-cancel"
                                                   onClick="clickCancel('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input','',3);">Cancel</a></span> 
                                             </div>
                                          </div>
                                          <div class="customtable db-ca-gst tabtable3 mt-2">
                                             <table id="dbTable6" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                                <thead>
                                                   <tr>
                                                      <th class="text-left" rowspan="2">On account of</th>
                                                      <th class="text-left" rowspan="2">Amount in default </th>
                                                      <th class="text-center" colspan="3">Amount of interest </th>
                                                   </tr>
                                                   <tr>
                                                      <th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>) </th>
                                                      <th class="text-left">Central Tax(<i class="fa fa-rupee"></i>) </th>
                                                      <th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>) </th>
                                                   </tr>
                                                </thead>
                                                <tbody>
                                                   <tr>
                                                      <td class="text-left">Late payment of TCS amount</td>
                                                      <td class="text-right form-group gst-3b-error">
                                                         <input type="text" class="form-control tpthree-input " pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                         <div class="help-block with-errors"></div>
                                                      </td>
                                                      <td class="text-right form-group gst-3b-error">
                                                         <input type="text" class="form-control tpthree-input " pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                         <div class="help-block with-errors"></div>
                                                      </td>
                                                      <td class="text-right form-group gst-3b-error">
                                                         <input type="text" class="form-control tpthree-input " pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                         <div class="help-block with-errors"></div>
                                                      </td>
                                                      <td class="text-right form-group gst-3b-error">
                                                         <input type="text" class="form-control tpthree-input " pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                         <div class="help-block with-errors"></div>
                                                      </td>
                                                   </tr>
                                                </tbody>
                                             </table>
                                          </div>
                                       </div>
                                       <div class="tab-pane" id="main_tab4" role="tabpanel">
                                          <div class="group upload-btn">
                                             <div class="mb-2 row"> <span class="col-md-10"></span><span class="col-md-2 pull-right"><span
                                                class="helpguide pull-right" data-toggle="modal"
                                                data-target="#helpguideModal_4"> Help Guide</span><span class="pull-right"><a href="#"
                                                class="btn btn-sm  btn-blue-dark tpfour-edit"
                                                onClick="clickEdit('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input');">Edit</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpfour-cancel"
                                                   onClick="clickSave('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input');">Save</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpfour-cancel"
                                                   onClick="clickCancel('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input','',4);">Cancel</a></span></span>
                                             </div>
                                          </div>
                                          <div id="accordion" class="inneracco">
                                             <div class="card">
                                                <div class="card-header" id="headinginnerSix">
                                                   <h5 class="mb-0">
                                                      <button class="btn acco-btn" data-toggle="collapse"
                                                         data-target="#collapseinnerSix" aria-expanded="true"
                                                         aria-controls="collapseinnerSix">6. Tax
                                                      payable and paid</button>
                                                   </h5>
                                                </div>
                                                <div id="collapseinnerSix" class="collapse show" aria-labelledby="headinginnerSix" data-parent="#accordion">
                                                   <div class="card-body p-2">
                                                      <div class="customtable db-ca-gst tabtable9 mt-2">
                                                         <table id="dbTable7" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                                            <thead>
                                                               <tr>
                                                                  <th class="text-left">Description</th>
                                                                  <th class="text-left">Tax payable(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Amount paid(<i class="fa fa-rupee"></i>) </th>
                                                               </tr>
                                                            </thead>
                                                            <tbody>
                                                               <tr>
                                                                  <td class="text-left">(a) Integrated Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpfour-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpfour-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td class="text-left">(b) Central Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpfour-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpfour-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td class="text-left">(c) State / UT Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpfour-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpfour-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                            </tbody>
                                                         </table>
                                                      </div>
                                                   </div>
                                                </div>
                                             </div>
                                          </div>
                                       </div>
                                       <div class="tab-pane" id="main_tab7" role="tabpanel">
                                          <div class="group upload-btn">
                                             <div class="mb-2 row"> <span class="col-md-10"></span><span class="col-md-2 pull-right"><span
                                                class="helpguide pull-right" data-toggle="modal"
                                                data-target="#helpguideModal_4"> Help Guide</span><span class="pull-right"><a href="#"
                                                class="btn btn-sm  btn-blue-dark tpten-edit"
                                                onClick="clickEdit('.tpten-save', '.tpten-cancel', '.tpten-edit','.tpten-input');">Edit</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpten-cancel"
                                                   onClick="clickSave('.tpten-save', '.tpten-cancel', '.tpten-edit','.tpten-input');">Save</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpten-cancel"
                                                   onClick="clickCancel('.tpten-save', '.tpten-cancel', '.tpten-edit','.tpten-input','',4);">Cancel</a></span></span>
                                             </div>
                                          </div>
                                          <div id="accordion" class="inneracco">
                                             <div class="card">
                                                <div class="card-header" id="headinginnerSeven">
                                                   <h5 class="mb-0">
                                                      <button class="btn acco-btn collapsed"
                                                         data-toggle="collapse"
                                                         data-target="#collapseinnerSeven" aria-expanded="true"
                                                         aria-controls="collapseinnerSeven">7.
                                                      Interest payable and paid</button>
                                                   </h5>
                                                </div>
                                                <div id="collapseinnerSeven" class="collapse show" aria-labelledby="headinginnerSeven" data-parent="#accordion">
                                                   <div class="card-body p-2">
                                                      <div class="customtable db-ca-gst tabtable4 mt-2">
                                                         <table id="dbTable8" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                                            <thead>
                                                               <tr>
                                                                  <th class="text-left">Description</th>
                                                                  <th class="text-left">Amount of interest payable (<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Amount paid(<i class="fa fa-rupee"></i>) </th>
                                                               </tr>
                                                            </thead>
                                                            <tbody>
                                                               <tr>
                                                                  <td>(a)Integrated Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input class="form-control tpten-input" type="text" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input class="form-control tpten-input" type="text" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td>(b)Central Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input class="form-control tpten-input" type="text" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input class="form-control tpten-input" type="text" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td>(c)State/UT Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input class="form-control tpten-input" type="text" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input class="form-control tpten-input" type="text" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                            </tbody>
                                                         </table>
                                                      </div>
                                                   </div>
                                                </div>
                                             </div>
                                          </div>
                                       </div>
                                       <div class="tab-pane" id="main_tab5" role="tabpanel">
                                          <!--- 6 ---->
                                          <div class="group upload-btn">
                                             <div class="mb-2">
                                                <div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_3">Help Guide</div>
                                                <span class="pull-right"> <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpsix-edit"
                                                   onClick="clickEdit('.tpsix-save', '.tpsix-cancel', '.tpsix-edit','.tpsix-input');">Edit</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpsix-cancel"
                                                   onClick="clickSave('.tpsix-save', '.tpsix-cancel', '.tpsix-edit','.tpsix-input');">Save</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpsix-cancel"
                                                   onClick="clickCancel('.tpsix-save', '.tpsix-cancel', '.tpsix-edit','.tpsix-input','',6);">Cancel</a></span> 
                                             </div>
                                          </div>
                                          <div id="accordion" class="inneracco">
                                             <div class="card">
                                                <div class="card-header" id="headinginnerEight">
                                                   <h5 class="mb-0">
                                                      <button class="btn acco-btn" data-toggle="collapse"
                                                         data-target="#collapseinnerEight" aria-expanded="true"
                                                         aria-controls="collapseinnerEight">8. Refund
                                                      claimed from electronic cash ledger</button>
                                                   </h5>
                                                </div>
                                                <div id="collapseinnerEight" class="collapse show" aria-labelledby="headinginnerEight" data-parent="#accordion">
                                                   <div class="card-body p-2">
                                                      <div class="customtable db-ca-gst tabtable3 mt-2">
                                                         <table id="dbTable9" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                                            <thead>
                                                               <tr>
                                                                  <th class="text-left">Description</th>
                                                                  <th class="text-left">Tax(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Interest(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Penalty(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Other(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Debit Entry Nos.</th>
                                                               </tr>
                                                            </thead>
                                                            <tbody>
                                                               <tr>
                                                                  <td class="text-left">(a) Integrated tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td class="text-left">(b) Central Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td class="text-left">(c) State/UT Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td class="text-left" colspan="3">Bank Account Details (Drop Down) </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpsix-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                            </tbody>
                                                         </table>
                                                      </div>
                                                   </div>
                                                </div>
                                             </div>
                                          </div>
                                       </div>
                                       <div class="tab-pane" id="main_tab6" role="tabpanel">
                                          <!--- 6 ---->
                                          <div class="group upload-btn">
                                             <div class="mb-2">
                                                <div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_3">Help Guide</div>
                                                <span class="pull-right"> <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpseven-edit"
                                                   onClick="clickEdit('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input');">Edit</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpseven-cancel"
                                                   onClick="clickSave('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input');">Save</a>
                                                <a href="#"
                                                   class="btn btn-sm  btn-blue-dark tpseven-cancel"
                                                   onClick="clickCancel('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input','',6);">Cancel</a></span> 
                                             </div>
                                          </div>
                                          <div id="accordion" class="inneracco">
                                             <div class="card">
                                                <div class="card-header" id="headinginnerEight">
                                                   <h5 class="mb-0">
                                                      <button class="btn acco-btn" data-toggle="collapse"
                                                         data-target="#collapseinnerEight" aria-expanded="true"
                                                         aria-controls="collapseinnerEight">9. Debit
                                                      entries in cash ledger for TCS/interest payment [to be
                                                      populated after payment of tax and submissions of
                                                      return]</button>
                                                   </h5>
                                                </div>
                                                <div id="collapseinnerEight" class="collapse show" aria-labelledby="headinginnerEight" data-parent="#accordion">
                                                   <div class="card-body p-2">
                                                      <div class="customtable db-ca-gst tabtable3 mt-2">
                                                         <table id="dbTable9" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                                            <thead>
                                                               <tr>
                                                                  <th class="text-left">Description</th>
                                                                  <th class="text-left">Tax paid in cash(<i class="fa fa-rupee"></i>) </th>
                                                                  <th class="text-left">Interest(<i class="fa fa-rupee"></i>) </th>
                                                               </tr>
                                                            </thead>
                                                            <tbody>
                                                               <tr>
                                                                  <td class="text-left">(a) Integrated tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpseven-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpseven-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td class="text-left">(b) Central Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpseven-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpseven-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                               <tr>
                                                                  <td class="text-left">(c) State/UT Tax</td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpseven-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                                  <td class="text-right form-group gst-3b-error">
                                                                     <input type="text" readonly="true" class="form-control tpseven-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" />
                                                                     <div class="help-block with-errors"></div>
                                                                  </td>
                                                               </tr>
                                                            </tbody>
                                                         </table>
                                                      </div>
                                                   </div>
                                                </div>
                                             </div>
                                          </div>
                                       </div>
                                    </div>
                                 </div>
                              </div>
                              
                        </div>
                     </div>
                     <!-- dashboard left block end -->
                  </div>
                  <!-- Dashboard body end -->
               </div>
            </div>
            <!-- db-ca-wrap end -->
         </div>
      </div>
      <!-- footer begin here -->
      <%@include file="/WEB-INF/views/includes/footer.jsp"%>
      <!-- footer end here -->
     <!-- downloadOtpModal Start -->
      <div class="modal fade" id="downloadOtpModal" role="dialog" aria-labelledby="downloadOtpModal" aria-hidden="true">
         <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
               <div class="modal-body">
                  <button type="button" id="downloadOtpModalClose" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img
                     src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png"
                     alt="Close" /></span> </button>
                  <div class="invoice-hdr bluehdr">
                     <h3>Verify OTP</h3>
                  </div>
                  <div class="group upload-btn p-4" style="min-height: 600px;">
                     <div class="formboxwrap">
                        <h3>Filing GST Made Simple, & Pay your Tax easily</h3>
                        <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
                        <div class="col-md-12 col-sm-12 m-auto">
                           <div class="formbox otpbox">
                              <form class="meterialform" id="dwnldOtpEntryForm" data-toggle="validator">
                                 <div class="whitebg">
                                    <h2>Verify Mobile Number</h2>
                                    <h6>OTP has been sent to your GSTIN registered mobile
                                       number & e-mail, Please enter the same below
                                    </h6>
                                    <!-- serverside error begin -->
                                    <div class="errormsg"></div>
                                    <!-- serverside error end -->
                                    <div class="col-sm-12 otp_form_input">
                                       <div class="group upload-btn">
                                          <div class="errormsg" id="otp_Msg"></div>
                                          <div class="group upload-btn"></div>
                                          <input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp1" required="required" data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="1" placeholder="0" />
                                          <div class="help-block with-errors"></div>
                                          <input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp2" required="required" data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="2" placeholder="0" />
                                          <div class="help-block with-errors"></div>
                                          <input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp3" required="required" data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="3" placeholder="0" />
                                          <div class="help-block with-errors"></div>
                                          <input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp4" required="required" data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="4" placeholder="0" />
                                          <div class="help-block with-errors"></div>
                                          <input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp5" required="required" data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="5" placeholder="0" />
                                          <div class="help-block with-errors"></div>
                                          <input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp6" required="required" data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="6" placeholder="0" />
                                          <div class="help-block with-errors"></div>
                                       </div>
                                       <h6>
                                          Didn't receive OTP? <a href="">try again</a>
                                       </h6>
                                    </div>
                                 </div>
                                 <div class="p-2 text-center">
                                    <p> <a href="#" onClick="validateDownloadOtp()" class="btn btn-lg btn-blue btn-verify">Verify OTP</a> </p>
                                 </div>
                              </form>
                           </div>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
      <!-- downloadOtpModal End -->
     
      <div class="modal fade" id="fileReturnModal" tabindex="-1" role="dialog" aria-labelledby="fileReturnModal" aria-hidden="true">
         <div class="modal-dialog col-6 modal-center" role="document">
            <div class="modal-content">
               <div class="modal-body">
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img
                     src="${contextPath}/static/mastergst/images/master/closeicon-blue.png"
                     alt="Close"></span> </button>
                  <div class="invoice-hdr bluehdr">
                     <h3>Pre-requisites for DSC Filing</h3>
                  </div>
                  <div class="group upload-btn p-2 steptext-wrap">
                     <ul>
                        <li><span class="steptext-desc">1) Install Digital
                           Signature Software, <a
                              href="https://files.truecopy.in/downloads/lh/latest/DSCSignerLH.msi">Click
                           Here</a> to download & install
                           </span> 
                        </li>
                        <li><span class="steptext-desc">2) Make sure Digital
                           Signature software is running in your system</span> 
                        </li>
                        <li><span class="steptext-desc">3) Make Sure ePass
                           Application is Running in your System</span> 
                        </li>
                        <li><span class="steptext-desc">3) Login to ePass
                           Application</span> 
                        </li>
                     </ul>
                     <ul>
                        <li>==================================================</li>
                     </ul>
                     <ul>
                        <li><span class="steptext">Step 1:</span> <span class="steptext-desc"> Certificate verification</span></li>
                        <li><span class="steptext"> Step 2:</span> <span class="steptext-desc"> Sign the invoices</span></li>
                        <li><span class="steptext">Step 3:</span> <span class="steptext-desc"> Filing of invoices</span></li>
                     </ul>
                  </div>
               </div>
               <div class="modal-footer">
                  <button type="button" class="btn btn-secondary" onClick="trueCopyFiling()" data-dismiss="modal">File Now</button>
                  <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
               </div>
            </div>
         </div>
      </div>
      <form id="certsignForm" name="certsignForm" method="POST" enctype="multipart/form-data"></form>
      <!-- evcOtpModal Start -->
      <div class="modal fade" id="evcOtpModal" role="dialog" aria-labelledby="evcOtpModal" aria-hidden="true">
         <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
               <div class="modal-body">
                  <button type="button" id="evcOtpModalClose" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img
                     src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png"
                     alt="Close" /></span> </button>
                  <div class="invoice-hdr bluehdr">
                     <h3>Submit EVC OTP</h3>
                  </div>
                  <div class="group upload-btn p-4" style="min-height: 600px;">
                     <div class="formboxwrap">
                        <h3>Filing GST Made Simple, & Pay your Tax easily</h3>
                        <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
                        <div class="col-md-12 col-sm-12 m-auto">
                           <div class="formbox otpbox">
                              <form class="meterialform" id="evcOtpEntryForm" data-toggle="validator">
                                 <div class="whitebg">
                                    <h2>Verify EVC OTP</h2>
                                    <h6>OTP has been sent to your GSTN registered mobile number & e-mail, Please enter the same below</h6>
                                    <!-- serverside error begin -->
                                    <div class="errormsg"></div>
                                    <!-- serverside error end -->
                                    <div class="col-sm-12">
                                       <div class="group upload-btn">
                                          <div class="errormsg"></div>
                                          <div class="group upload-btn"></div>
                                          <input type="text" class="evcotp" id="evcotp1" required="required" data-minlength="4" maxlength="6" pattern="[a-zA-Z0-9]+" data-error="Please enter valid otp number" tabindex="1" placeholder="0" />
                                          <div class="help-block with-errors"></div>
                                       </div>
                                       <h6>Didn't receive OTP? <a href="">try again</a></h6>
                                    </div>
                                 </div>
                                 <div class="p-2 text-center">
                                    <p> <a href="#" onClick="fileEVC()" class="btn btn-lg btn-blue btn-verify">Submit</a> </p>
                                 </div>
                              </form>
                           </div>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
      <!-- evcOtpModal End -->
      <script src="${contextPath}/static/mastergst/js/jquery/jquery.formula.js" type="text/javascript"></script>
      <script type="text/javascript">
      	function updateReturnPeriod(eDate) {
    	  	var month = eDate.getMonth() + 1;
    	  	var year = eDate.getFullYear();
    	  	window.location.href = '${contextPath}/addecominvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${clientid}"/>/' + month + '/' + year;
    	}
      </script>
   </body>
</html>
