<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<%@include file="/WEB-INF/views/includes/script.jsp" %>
<c:set var="varClientId" value="<%=UUID.randomUUID().toString()%>"/>
<c:set var="varClientSecret" value="<%=UUID.randomUUID().toString()%>"/>
<script type="text/javascript">
	$(function () {
		$('.radio input[type="radio"]').click(function(){
			if($(this).attr("value") == 'Sandbox') {
				$('#sectionProduction').hide();
				$('#sectionSandbox').show();
				$('#clientid').val('${varClientId}').prop('readonly', true);
				$('#clientsecret').val('${varClientSecret}').prop('readonly', true);
			} else if($(this).attr("value") == 'Production') {
				$('#sectionProduction').show();
				$('#sectionSandbox').hide();
			}
		});
	});
</script>
</head>
<body>
<%@include file="/WEB-INF/views/includes/app_header.jsp" %>
<div class="bodywrap">
  <div class="db-inner">
      <!-- begin content  -->
    <div class="container db-inner-txt">
      <div class="row">
        <div class="credentialwrap">
          <div class="row">
            <div class="col-md-3 col-sm-12">
              <!-- left profile list begin -->
              <ul class="profilelist">
                <li class="rowbdr">
                  <div class="icon-l"><span class="userimg"><img src="${contextPath}/static/mastergst/images/credentials/user1.jpg" alt="User" class="circle" /></span></div>
                  <div class="icon-r"><span class="usertxt"><c:out value="${fullname}"/></span></div>
                </li>
                <li>
                  <div class="icon-l"><span class="cred-sprite credicon1"></span></div>
                  <div class="icon-r"><span class="usertxt"><c:out value="${mail}"/></span></div>
                </li>
                <li>
                  <div class="icon-l"><span class="cred-sprite credicon2"></span></div>
                  <div class="icon-r"><span class="usertxt"><c:out value="${mobilenumber}"/></span></div>
                </li>
                <li>
                  <div class="icon-l"><span class="cred-sprite credicon5"></span></div>
                  <div class="icon-r"><span class="usertxt"><c:out value="${address}"/></span> </div>
                </li>
              </ul>
              <!-- left profile list end -->
            </div>
            <div class="col-md-1 col-sm-12">
              <div class="bdr-dashed"></div>
            </div>
			
			
            <div class="col-md-8 col-sm-12">
			<c:if test="${otheruser=='yes'}">
					<div class="comingsoon"> <h3 class="bluetxt">Something Awesome is coming soon</h3>
					<h6>We are building something very cool, Stay tuned and be patient. Your patient will be well paid.</h6>
					</div> 
			 </c:if>	
			
			<c:if test="${otheruser=='no'}">
              <!--   profile table begin -->
              <div class="customtable">
                <h3 class="mt-1">Profile <a href="" class="btn btn-bdr pull-right mb-4" data-toggle="modal" data-target="#create_credentials">Create Credentials</a></h3>
                <div class="customtable-hdr">
                  <h6>My Credentials List</h6>
                </div>
				<div class="clearfix">&nbsp;</div>
                <table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                  <thead>
                    <tr>
                      <th> <div class="checkbox">
                          <label>
                          <input type="checkbox" />
                          <i class="helper"></i></label>
                        </div></th>
                      <th>Stage</th>
                      <th>Name</th>
                      <th>Creation Date</th>
                      <th>Client ID</th>
                      <th>Client Street</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
				  
				    <c:forEach items="${luserkeys}" var="userkeys">
                    <tr>
                      <td><div class="checkbox">
                          <label>
                          <input type="checkbox" />
                          <i class="helper"></i> </label>
                        </div></td>
                      <td>${userkeys.stage}</td>
                      <td>${userkeys.keyname}</td>
                      <td>${userkeys.createdate}</td>
                      <td>${userkeys.clientid}</td>
                      <td>${userkeys.clientsecret}</td>
                  	  <td class="actionicons"><a href="deletekeys?id=<c:out value="${id}"/>&keyid=<c:out value="${userkeys.id}"/>"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" /></a></td>
                    </tr>
					 </c:forEach>
                  </tbody>
                </table>
              </div>
			  </c:if>	
              <!--   profile table end -->
            </div>
          </div>
        </div>
      </div>
    </div>
        <!-- end content  -->
  </div>
  <!-- Button trigger modal -->
  <!-- Create credentials Modal begin -->
  <c:if test="${otheruser=='no'}">
  <div class="modal fade" id="create_credentials" role="dialog" aria-labelledby="create_credentials" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="create_credentials">Create Credentials</h5>
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"><img src="${contextPath}/static/mastergst/images/credentials/closebtn.png" alt="Close" /></span> </button>
        </div>
        <div class="modal-body">
         
		  <form:form method="POST" class="meterialform" action="createcredentials" modelAttribute="userkeys">
			<div class="form-group form-group-inline">
				<div class="form-radio">
					<div class="radio">
						<label>
							<input name="stage" type="radio" value="Sandbox" checked />
							<i class="helper"></i>Sandbox</label>
					</div>
				</div>
				<!-- <div class="form-radio">
					<div class="radio">
						<label>
							<input name="stage" type="radio" value="Pre-Production" />
							<i class="helper"></i>Pre-Production</label>
					</div>
				</div> -->
				<div class="form-radio">
					<div class="radio">
						<label>
							<input name="stage" type="radio" value="Production" />
							<i class="helper"></i>Production</label>
					</div>
				</div>
			</div>
			<div class="alert alert-danger" style="display:none;" role="alert" id="sectionProduction"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2" />
			  <span>For production API access, Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.</span>
			</div>
			<div id="sectionSandbox">
            <div class="labletxt astrich">Name</div>
            <div class="form-group">
              <input type="text" required="required" name="keyname">
              <label for="input" class="control-label"></label>
              <i class="bar"></i> </div>
            <div class="labletxt astrich">Create Date</div>
            <div class="form-group">
              <input type="text" required="required" name="createdate" value=<%=strDate%>  readonly>
              <label for="input" class="control-label"></label>
              <i class="bar"></i> </div>
            <div class="labletxt astrich">Client ID</div>
            <div class="form-group">
              <input type="text" required="required" id="clientid" name="clientid" value="${varClientId}"  readonly>
              <label for="input" class="control-label"></label>
              <i class="bar"></i> </div>
            <div class="labletxt astrich">Client Secret</div>
            <div class="form-group">
              <input type="text" required="required" id="clientsecret" name="clientsecret" value="${varClientSecret}"  readonly>
              <label for="input" class="control-label"></label>
              <i class="bar"></i> </div>
			<div class="form-group">
			   <input type="hidden" name="hiddenid" value=<c:out value="${id}"/>>
			  <input type="submit" class=btn btn-lg btn-blue m-auto text-center" name="enrollbtn" id="enrollbtn" value="Submit">
			</div>
			</div>
			</form:form>
			  
          </form>
        </div>
       
      </div>
    </div>
  </div>
  </c:if>	
  <!-- Create credentials Modal end -->
</div>
</div>
<script src="${contextPath}/static/mastergst/js/common/mastergstdatatable.js" type="text/javascript"></script>
<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->

</body>
</html>
