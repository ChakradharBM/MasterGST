<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<div class="modal fade" id="addeTermModal" role="dialog" aria-labelledby="addeTermModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
			<div class="modal-header p-0">
				<button type="button" class="close" onclick="closeeTermModal('addeTermModal')" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Configure Terms</h3>
                    </div>
				</div>
				<div class="modal-body popupright ">
					<div class="row customtable pl-5 pr-2 pt-4">
		                <table class="row-border dataTable meterialform" id="econfigTermsTable" style="width:93%;">
		                <thead><tr><th>S.No</th><th>Term Name</th><th>Term Days</th><th></th></tr></thead>
		                <tbody id="eConfigTemsTable_body">
		                	<tr id="1">
		                		<td id="eno_row1" align="center">1</td>
								<td><input type="text" class="form-control" id="eterm_name1" name="termName" placeholder="Terms Name"/></td>
								<td><input type="text" class="form-control" id="eterm_days1" name="noOfDays" placeholder="Terms Days"/></td>
								<td align="center"><a href="javascript:void(0)" id="etermdelete_button1" class="eterm_delete" onclick="deleteeTerm(1)"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td>
							</tr>
		                </tbody>
		                <tfoot>
		                <tr>
			                <th colspan="3" class="tfootwitebg"><span class="add pull-left" id="eaddConfigrow1" onclick="addeTermsrow()" style="color:black;"><i class="add-btn">+</i> Add another row</span></th>
			                 <th class="tfootwitebg addCtbutton"> <span class="add add-btn" id="eaddConfigrow" onclick="addeTermsrow()">+</span></th>
		                </tr>
		                </tfoot>
		                </table>
					</div>
			</div>
			<div class="modal-footer" style="display:block;text-align:center;">
				<a type="button" class="btn  btn-blue-dark" id="eaddTerm_btn" onclick="saveePaymentTerms()">Save</a>
				<button type="button" class="btn  btn-blue-dark" onclick="closeeTermModal('addeTermModal')">Cancel</button>
			</div>
				
			</div>
		</div>
	</div>