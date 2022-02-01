<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<div class="modal fade" id="groupModal" role="dialog" aria-labelledby="groupModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
			<div class="modal-header p-0">
			<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                     <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
             </button>
                 <div class="bluehdr" style="width:100%"><h3>ADD Group</h3></div>
			</div>
			    <div class="modal-body meterialform popupright bs-fancy-checks">
                 <!-- row begin -->
                  <form:form method="POST" data-toggle="validator" id="groupform" class="meterialform col-12 p-0" name="groupform" style="display:block">
                	<div class="row content p-5">
                	<p class="lable-txt astrich col-md-4" id="idName" style="margin-top: 10px;">Group Name</p>
                	<div class="form-group mt-1 col-md-7" id="expbmsg">
                            <span class="errormsg expgmsg" id="expgroupName_Msg" style="margin-top:-16px;"></span> <span class="errormsg grpmsg" id="grpName_Msg" style="margin-top:-16px;"></span>
                            <input type="text" id="expgname" class="expgname" name="groupname" required="required" data-error="Enter the group Name" onkeyup="updateGroupDetails()" placeholder="Group Name" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i>
                     </div>
                     <p class="lable-txt astrich col-md-4" style="margin-top: 24px;">Under the Head Name</p>
                     <div class="form-group col-md-7 mb-1" style="margin-top: 16px;">
                            <span class="errormsg hmsg" id="expheadname_Msg"></span><span class="errormsg expheadmsg" id="exphead_Msg" style="margin-top:-16px;"></span>
                           <!-- <input type="text" class="form-control expgname" name="headname" required="required" data-error="Enter the head Name" value="Expenses" readonly> -->
                           <select class="form-control expgname" id= "expheadname" name="headname" required="required" data-error="Enter the head Name">
                           <option value="">- Select -</option>
                           <option value="Direct expenses">Direct expenses</option>
                           <option value="Indirect expenses">Indirect expenses</option>
                           </select>
                             <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
                     </div>
                  </div>
                </form:form>
                 </div>
                 <div class="modal-footer text-center" style="display:block">
				<label for="expgroup_submit" class="btn btn-blue-dark m-0" tabindex="0" onClick="groupAdd('Group')">Save</label>
				<button type="button" class="btn btn-blue-dark " data-dismiss="modal" aria-label="Close">cancel</button>  
				</div>
                    <!-- row end -->
				</div>
            </div>
        </div>