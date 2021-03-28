<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){

		pageList(1,2);

		//日历插件
		$("#search-startDate").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#search-endDate").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#edit-startDate").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#edit-endDate").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#addBtn").click(function () {

		    //引入日历控件
            $(".time").datetimepicker({
                minView: "month",
                language:  'zh-CN',
                format: 'yyyy-mm-dd',
                autoclose: true,
                todayBtn: true,
                pickerPosition: "bottom-left"
            });

		//走后台，获取List<User>,将模态窗口的所有者下拉框中的内容替换为User表中的name
		<!--temp-->
		$.ajax({
			url:"workbench/activity/getUserList.do",
			type:"get",
			dataType:"json",
			data:{},
			success:function (data) {
				var optionHtml = "<option></option>"

				$.each(data,function (index,element) {
					optionHtml += "<option value='"+element.id+"'>"+element.name+"</option>"
				})

				$("#create-owner").html(optionHtml)
				$("#create-owner").val("${sessionScope.user.id}")
			}
		})

			//在获取User后设置完成所有者下拉框后，为模态窗口绑定按钮单击事件
			//模态窗口打开【show：打开    hide关闭[隐藏]】
			$("#createActivityModal").modal("show")
		})

        //添加操作
        //点击保存按钮
        $("#saveBtn").click(function () {
        //发送AJAX请求，将数据存入tbl_Activity表中
			$.ajax({
				url:"workbench/activity/saveActivity.do",
				type:"post",
				dataType: "json",
				data:{
					"owner":$.trim($("#create-owner").val()),
					"name":$.trim($("#create-name").val()),
					"startDate":$.trim($("#create-startDate").val()),
					"endDate":$.trim($("#create-endDate").val()),
					"cost" : $.trim($("#create-cost").val()),
					"description" : $.trim($("#create-description").val())
				},
				success:function (data) {

					/*date{
                        "success":true/false
                    }*/
					if (data.success){

						//如果添加成功，清空模态窗口中的内容，并关闭模态窗口
						$("#activityAddForm")[0].reset();
						$("#createActivityModal").modal("hide");
						// pageList(1,2);
						/*
						* $("#activityPage").bs_pagination('getOption', 'currentPage'):
						* 		操作后停留在当前页
						*
						* $("#activityPage").bs_pagination('getOption', 'rowsPerPage')
						* 		操作后维持已经设置好的每页展现的记录数
						*
						* 这两个参数不需要我们进行任何的修改操作
						* 	直接使用即可
						* */

						//做完添加操作后，应该回到第一页，维持每页展现的记录数

						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

					}else{

						//jquery对象的reset方法不能使用，需要转换成dom对象，调用reset方法
						$("#activityAddForm")[0].reset();
						//如果添加失败，弹窗提示失败信息，并关闭模态窗口
						alert("添加失败")

					}

				}
			})
        })

		//市场活动首页“查询”单击按钮
		$("#searchBtn").click(function () {

			//将搜索框中的内容存储到隐藏域中
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			// pageList(1,2);
			pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
		})

		//复选框
		$("#allcheck").click(function () {
			$("input[name=xuanzhong]").prop("checked",this.checked)
		})

		//以下这种做法是不行的
		/*$("input[name=xz]").click(function () {
			alert(123);
		})*/

		//因为动态生成的元素，是不能够以普通绑定事件的形式来进行操作的
		/*
			动态生成的元素，我们要以【on方法】的形式来触发事件
			需要绑定元素的有效的外层元素:非动态元素
			语法：
				$(需要绑定元素的有效的外层元素).on(绑定事件的方式,需要绑定的元素的jquery对象,回调函数)
		 */
		$("#activityBody").on("click",$("input[name=xuanzhong]"),function () {
			$("#allcheck").prop("checked",$("input[name=xuanzhong]").length == $("input[name=xuanzhong]:checked").length);
		})



		$("#deleteBtn").click(function () {
			var $allcheck = $("input[name=xuanzhong]:checked")


			if (0==$allcheck.length){
				alert("请选择需要删除的信息")
			}else{

				//删除前进行确认
				if (confirm("确认删除？")){

					//拼接参数id
					var param = "";
					for (var i=0;i<$allcheck.length;i++){
						param += "id=" + $allcheck[i].value; //$($allcheck[i]).val()
						if (i<$allcheck.length-1){
							param += "&";
						}
					}
					//发送ajax请求
					$.ajax({
						url:"workbench/activity/delete.do",
						type:"get",
						dataType:"json",
						data: param,
						success:function (data) {
							//data需要一个boolean标签提示操作是否成功
							//data{"success":true/false}
							if (data.success){
								// pageList(1,2);
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							}else{
								alert("删除失败");
							}
						}
					})
				}
			}
		})


		//打开编辑的模态窗口
		$("#editBtn").click(function () {

			//走后台，获取用户列表和一条活动市场信息
			var $allcheck = $("input[name=xuanzhong]:checked");

			if ($allcheck.length == 0){
				alert("请选择需要修改的信息");
			}else if ($allcheck.length > 1){
				alert("请选择一条需要修改的信息");
			}else{
				//对象中只有一条信息
				var id = $allcheck.val();

				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					type:"get",
					dataType:"json",
					data:{"id":id},
					success:function (data) {
						/*data{
							"userList":userList,
							"activity":actiity
						}*/

						var html = "<option></option>";
						$.each(data.userList,function (index,element) {
							html += "<option value='"+element.id+"'>"+element.name+"</option>"
						})

						//下拉框
						$("#edit-owner").html(html);
						//为模态窗口赋默认值
						//$("#edit-id"): 隐藏域，存放id值，更新数据时需要使用
						$("#edit-id").val(data.activity.id);
						$("#edit-owner").val(data.activity.owner);
						$("#edit-name").val(data.activity.name);
						$("#edit-startDate").val(data.activity.startDate);
						$("#edit-endDate").val(data.activity.endDate);
						$("#edit-cost").val(data.activity.cost);
						$("#edit-description").val(data.activity.description);

						//打开修改模态窗口
						$("#editActivityModal").modal("show")
					}
				})
			}
		})

		//为更新按钮绑定事件
		$("#updateBtn").click(function(){

			//获取用户填入的参数以及放在隐藏域中的id
			var id = $("#edit-id").val();
			var owner = $.trim($("#edit-owner").val());
			var name = $.trim($("#edit-name").val());
			var startDate = $.trim($("#edit-startDate").val());
			var endDate = $.trim($("#edit-endDate").val());
			var cost = $.trim($("#edit-cost").val());
			var description = $.trim($("#edit-description").val());

			$.ajax({
				url:"workbench/activity/updateActivity.do",
				type:"post",
				dataType:"json",
				data:{
					"id":id,
					"owner":owner,
					"name":name,
					"startDate":startDate,
					"endDate":endDate,
					"cost":cost,
					"description":description
				},
				success:function (data) {
					/*data{
						"success":true/false
					}*/
					if (data.success){
						// pageList(1,2);
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						$("#editActivityModal").modal("hide")
					}else{
						alert("更新失败");
					}
				}
			})




		})

	});

	function pageList(pageNo,pageSize) {

		//换页和重新搜索后先去掉复选框的选中状态
		$("#allcheck").prop("checked",false);

		//取出隐藏域对象中的值重新赋给搜索框
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));
		$.ajax({
			url:"workbench/activity/pageList.do",
			type:"get",
			dataType:"json",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"name":$.trim($("#search-name").val()),
				"owner":$.trim($("#search-owner").val()),
				"startDate":$.trim($("#search-startDate").val()),
				"endDate":$.trim($("#search-endDate").val())
			},
			success:function (data) {

				/*data

				我们需要的：市场活动信息列表
						[{市场活动1},{2},{3}] List<Activity> aList
				一会分页插件需要的：查询出来的总记录数
				{"total":100} int total

				{"total":100,"dataList":[{市场活动1},{2},{3}]}*/
				var html = "";
				$.each(data.dataList,function (index,element) {
					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xuanzhong" value="'+ element.id +'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+element.id+'\';">'+ element.name +'</a></td>';
					html += '<td>'+ element.owner +'</td>';
					html += '<td>'+ element.startDate +'</td>';
					html += '<td>'+ element.endDate +'</td>';
					html += '</tr>';
				})

				$("#activityBody").html(html);

				//计算总页数
				var totalPages =
						(data.total % pageSize == 0) ? (data.total/pageSize) : parseInt(data.total/pageSize)+1;

				//数据处理结束后，向前端展示数据
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码【程序员提供的】
					rowsPerPage: pageSize, // 每页显示的记录条数【程序员提供的】
					maxRowsPerPage: 20, // 每页最多显示的记录条数【数值可设置更改】
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数【程序员提供的】

					visiblePageLinks: 3, // 显示几个卡片【可手动设置更改】

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//该回调函数是在：点击分页组件的时候触发
					onChangePage : function(event, data){
						//调用的pageList是程序员提供的，但 [data] 是分页组件自己的
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})

	}
	
</script>
</head>
<body>

	<!--创建隐藏域，存储搜索框中原本的内容-->
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: #ff0000;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate">
							</div>
							<label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn"<%--data-dismiss="modal"--%>>保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">

						<%--隐藏域，存放activity表的id--%>
						<input type="hidden" id="edit-id"/>
						<div class="form-group">
							<label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">

								</select>
							</div>
                            <label for="edit-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" >
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-startDate" >
							</div>
							<label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-endDate" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn"<%--data-dismiss="modal"--%>>更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn" <%--data-toggle="modal" data-target="#createActivityModal"--%> ><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn" <%--data-toggle="modal" data-target="#editActivityModal"--%> ><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>

			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="allcheck"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<%--手动添加的分页组件--%>
				<div id="activityPage"></div>
				<%--原有分页组件--%>
				<%--<div>
					<button type="button" class="btn btn-default" style="cursor: default;">共<b>50</b>条记录</button>
				</div>
				<div class="btn-group" style="position: relative;top: -34px; left: 110px;">
					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
							10
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#">20</a></li>
							<li><a href="#">30</a></li>
						</ul>
					</div>
					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
				</div>
				<div style="position: relative;top: -88px; left: 285px;">
					<nav>
						<ul class="pagination">
							<li class="disabled"><a href="#">首页</a></li>
							<li class="disabled"><a href="#">上一页</a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
							<li><a href="#">5</a></li>
							<li><a href="#">下一页</a></li>
							<li class="disabled"><a href="#">末页</a></li>
						</ul>
					</nav>
				</div>--%>
			</div>
			
		</div>
		
	</div>
</body>
</html>