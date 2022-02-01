<form:form method="POST" id="docForm" data-toggle="validator" class="meterialform invoiceform" name="docissform" modelAttribute="docIssue" action="${contextPath}/savedocissue/${id}/${fullname}/${usertype}/${client.id}/${docId}/${returntype}/${month}/${year}">
                <!-- table start -->
                <div class="customtable db-ca-view tabtable2">
                  <div class="customtable db-ca-view">
                        <div id="accordion" class="accordion" role="tablist" aria-multiselectable="true">
                     <div class=""> <a href="#" class="btn btn-greendark permissionUpload_Invoice pull-right <c:if test='${client.status eq statusSubmitted || client.status eq statusFiled}'>disable</c:if>" onclick="uploadDocIssue(this);">Upload All Docs to GSTN</a></div>
        <div class="card m-b-0" style="width:100%">
               <!--- 1 --->
                  <div class="card-header collapsed active"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse1" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 1}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">1. Invoices for outward supply</span>    <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span>
                </a>
            </div>            
                  <div id="collapse1" class="card-block collapse in show"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"   onClick="clickEdit(1, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>
               
               <div class="customtable db-ca-gst tabtable2">       
    	 <table id="dbTableUp1" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>              
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
						<c:set var="index" value="0" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 1}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="1"/><input type="text" class="form-control"  pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">   
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore1  mt-1">Addmore +</a></div>
                </div>
             </div>      
               <!--- 2 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse2" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 2}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">2. Invoices for inward supply from unregistered person</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span> </a>
            </div>            
                  <div id="collapse2" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(2, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>         
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp2" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>           
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 2}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="2"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">      
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore2 mt-1">Addmore +</a></div>                               
                </div>
             </div>       
              <!--- 3 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse3" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 3}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">3. Revised Invoice</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span></a>
            </div>            
                  <div id="collapse3" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(3, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>   
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp3" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>        
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 3}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="3"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">     
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore3 mt-1">Addmore +</a></div>                              
                </div>
             </div>   
             <!--- 4 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse4" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 4}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">4. Debit Note</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span> </a>
            </div>            
                  <div id="collapse4" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(4, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>   
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp4" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 4}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="4"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">     
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore4 mt-1">Addmore +</a></div>                                
                </div>
             </div>
             <!--- 5 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse5" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 5}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">5. Credit Note</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span></a>
            </div>            
                  <div id="collapse5" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(5, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>      
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp5" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>         
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 5}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="5"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">    
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore5 mt-1">Addmore +</a></div>
                </div>
             </div>
             <!--- 6 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse6" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 6}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">6. Receipt voucher</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span> </a>
            </div>            
                  <div id="collapse6" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(6, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>       
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp6" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>        
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 6}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="6"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">    
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore6 mt-1">Addmore +</a></div>
                </div>
             </div>     
             <!--- 7 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse7" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 7}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">7. Payment Voucher</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span> </a>
            </div>            
                  <div id="collapse7" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(7, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>    
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp7" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>           
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 7}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="7"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">     
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore7 mt-1">Addmore +</a></div>                               
                </div>
             </div>
         <!--- 8 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse8" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 8}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">8. Refund voucher</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span> </a>
            </div>            
                  <div id="collapse8" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(8, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>   
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp8" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead> 
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 8}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="8"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">   
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore8 mt-1">Addmore +</a></div>                             
                </div>
             </div>
                  <!--- 9 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse9" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 9}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">9. Delivery Challan for job work</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span></a>
            </div>            
                  <div id="collapse9" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(9, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div> 
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp9" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>    
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 9}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="9"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">         
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore9 mt-1">Addmore +</a></div>                                  
                </div>
             </div>            
                    <!--- 10 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse10" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 10}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">10. Delivery Challan for supply on approval</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span></a>
            </div>            
                  <div id="collapse10" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(10, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>        
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp10" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 10}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="10"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">    
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore10 mt-1">Addmore +</a></div>                                   
                </div>
             </div>            
                <!--- 11 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse11" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 11}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">11. Delivery Challan in case of liquid gas</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span></a>
            </div>            
                  <div id="collapse11" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(11, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>         
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp11" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>               
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 11}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="11"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">         
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore11 mt-1">Addmore +</a></div>                                   
                </div>
             </div>                        
             <!--- 12 --->
                  <div class="card-header collapsed"  role="tab"  data-toggle="collapse" data-parent="#accordion" href="#collapse12" aria-expanded="false">
                <a class="card-title">
				<c:set var="inttotal" value="0" />
				<c:set var="intcancel" value="0" />
				<c:set var="intnetissue" value="0" />
				<c:if test='${not empty docIssue.docDet}'>
				<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
				<c:if test='${item.docNum eq 12}'>
				<c:if test='${not empty item.docs}'>
				<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
				<c:set var="inttotal" value="${inttotal + doc.totnum}" />
				<c:set var="intcancel" value="${intcancel + doc.cancel}" />
				<c:set var="intnetissue" value="${intnetissue + doc.netIssue}" />
				</c:forEach>
				</c:if>
				</c:if>
				</c:forEach>
				</c:if>
                <span class="card-title-txt">12. Delivery Challan in cases other than by way of supply (excluding at S no. 9 to 11)</span> <span class="card-title-in-wrap"><span class="card-title-in">Total : <strong>${inttotal}</strong></span> <span class="card-title-in">Cancelled : <strong>${intcancel}</strong></span> <span class="card-title-in">Net Issued : <strong>${intnetissue}</strong></span></span></a>
            </div>            
                  <div id="collapse12" class="card-block collapse"  role="tabpanel" >                 
            <div class="group upload-btn"><span class="pull-right"> <a href="javascript:void();" class="btn btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"  onClick="clickEdit(12, '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a>  <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="javascript:void();" class="btn btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Cancel</a></span></div>          
               <div class="customtable db-ca-gst tabtable2">       
    	  <table id="dbTableUp12" class="dbTable row-border dataTable meterialform dbTableUp" cellspacing="0" width="100%">
                    <thead>            
                      <tr>
                        <th class="text-left">From serial number</th>
                        <th class="text-left">To serial number</th>
                        <th class="text-left">Total Number</th>
                        <th class="text-left">Cancelled</th>
                        <th class="text-left">Net issued</th>
                        <th></th>
                       </tr>
                    </thead>
                    <tbody>
                      <c:set var="index" value="${index + 1}" />
						<c:set var="docindex" value="0" />
						<c:if test='${not empty docIssue.docDet}'>
						<c:forEach items="${docIssue.docDet}" var="item" varStatus="loop">
						<c:if test='${item.docNum eq 12}'>
						<c:if test='${not empty item.docs}'>
						<c:forEach items="${item.docs}" var="doc" varStatus="loop1">
						<c:set var="docindex" value="${docindex + 1}" />
						  <tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${loop.index}].docNum" value="${item.docNum}"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].from" value="${doc.from}"> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${loop.index}].docs[${loop1.index}].to" value="${doc.to}"></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].totnum" value="${doc.totnum}"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].cancel" value="${doc.cancel}"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex}" name="docDet[${loop.index}].docs[${loop1.index}].netIssue" value="${doc.netIssue}" readonly="true"></td> 
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						  </tr>
						</c:forEach>
						</c:if>
						</c:if>
						</c:forEach>
						</c:if>
						<tr class="inter-state-supplies1">
							<td class="text-left"><input type="hidden" name="docDet[${index}].docNum" value="12"/><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].from" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td>
							<td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet[${index}].docs[${docindex}].to" <c:if test='${not empty invoiceSubmissionData}'>value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td>
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].totnum"></td> 
							<td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num${index+ docindex+1}" name="docDet[${index}].docs[${docindex}].cancel"></td> 
							<td class="text-left"><input type="text" class="form-control" id="net-issue${index+ docindex+1}" readonly="true" name="docDet[${index}].docs[${docindex}].netIssue"></td>
							<td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable </c:if>"></td>
						</tr>
                    </tbody>
                  </table>
         <div class=" addmorewrap">    
       <a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore addmore12 mt-1">Addmore +</a></div>
                </div>
             </div>
          <!--- end --->
        </div>
       </div>
                  </div>
                </div>
                <!-- table end -->
				</form:form>