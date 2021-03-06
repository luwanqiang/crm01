<%@ page import="com.luwanqiang.crm.settings.domain.DicValue" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.luwanqiang.crm.workbench.domain.Tran" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

	//准备字典类型为stage的字典值列表
	List<DicValue> dicValueList = (List<DicValue>) application.getAttribute("stageList");

	//准备阶段与可能性之间的对应关系
	Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");

	//根据pMap准备pMap中的key集合
	Set<String> set = pMap.keySet();

	//准备：前面正常阶段和后面丢失阶段的分界点下标
	int point = 0;
	for (int i=0; i<dicValueList.size(); i++){

		//取得每一个字典值
		DicValue dicValue = dicValueList.get(i);
		String stage = dicValue.getValue();

		//根据字典值获取Map中的可能性值
		String possibility = pMap.get(stage);

		//当possibility为0时，表示前面正常阶段和后面丢失阶段的分界点
		//准备：前面正常阶段和后面丢失阶段的分界点下标
		if ("0".equals(possibility)){
			//取得分界点的下标
			point = i;

			break;
		}
	}


%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

<style type="text/css">
.mystage{
	font-size: 20px;
	vertical-align: middle;
	cursor: pointer;
}
.closingDate{
	font-size : 15px;
	cursor: pointer;
	vertical-align: middle;
}
</style>
	
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});
		
		
		//阶段提示框
		$(".mystage").popover({
            trigger:'manual',
            placement : 'bottom',
            html: 'true',
            animation: false
        }).on("mouseenter", function () {
                    var _this = this;
                    $(this).popover("show");
                    $(this).siblings(".popover").on("mouseleave", function () {
                        $(_this).popover('hide');
                    });
                }).on("mouseleave", function () {
                    var _this = this;
                    setTimeout(function () {
                        if (!$(".popover:hover").length) {
                            $(_this).popover("hide")
                        }
                    }, 100);
                });

		//动态展现交易历史列表
		showTranHistoryList();
	});
	
	function showTranHistoryList() {
		$.ajax({
			url:"workbench/transaction/getTranHistoryListByTranId.do",
			type:"get",
			dataType:"json",
			data:{
				"tranId":"${tran.id}"
			},
			success:function (data) {
				/*data{(交易记录1)(2)(3)}*/

				var html = "";
				$.each(data,function (index,element) {
					html += '<tr>';
					html += '<td>'+element.stage+'</td>';
					html += '<td>'+element.money+'</td>';
					html += '<td>'+element.possibility+'</td>';
					html += '<td>'+element.expectedDate+'</td>';
					html += '<td>'+element.createTime+'</td>';
					html += '<td>'+element.createBy+'</td>';
					html += '</tr>';

				})
				$("#tranHistoryTbody").html(html);
			}
		})
	}

	function changeStage(stage,i) {
        $.ajax({
            url:"workbench/transaction/changeStage.do",
            type:"post",
            dataType:"json",
            data:{
                "tranId":"${requestScope.tran.id}",
                "stage":stage,
                "money":"${tran.money}", //创建阶段历史
                "expectedDate":"${tran.expectedDate}" //创建阶段历史
            },
            success:function (data) {
                /*data{
                    "success":true/false,
                    "tran":tran({"stage":stage,"possibility":possibility,"editBy":editBy,"editTime":editTime})
                }*/
                if (data.success){
                    //更新页面中需要更新的部分
                    //更新详细信息页中的‘阶段’、‘可能性’、‘修改人’、‘修改时间’信息
                    $("#stage").html(data.tran.stage);
                    $("#possibility").html(data.tran.possibility);
                    $("#editBy").html(data.tran.editBy);
                    $("#editTime").html(data.tran.editTime);

                    //动态更新界面图标
					changeIcon(stage,i);
                }else {
                    alert("改变阶段失败");
                }
            }
        })
    }

    function changeIcon(stage,index1) {

		//准备当前阶段
		var currentStage = stage;

		//准备当前阶段可能性
		var currentPossibility = $("#possibility").html();

		//当前阶段的下标
		var index = index1;

		//前面正常阶段和后面丢失阶段的分界点下标
		var point = "<%=point%>";

		//当前可能性为0，则前七个都为黑圈，后两个一个为黑叉，一个为红叉
		if (currentPossibility == "0"){

			//遍历前七个
			for (var i=0; i<point; i++){

				//黑圈--------------
				//移除掉原有的样式
				$("#"+i).removeClass();
				//添加新样式
				$("#"+i).addClass("glyphicon glyphicon-record mystage");
				//为新样式赋予颜色
				$("#"+i).css("color","#000000");

			}

			//遍历后两个
			for (var i=point; i < <%=dicValueList.size()%>; i++){

				if (i == index){

					//红叉----------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					$("#"+i).css("color","#FF0000");

				}else {

					//黑叉----------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					$("#"+i).css("color","#000000");

				}
			}

		//当前可能性不为0，则后面两个一定为黑叉
		}else{

			//遍历前七个
			for (var i=0; i<point; i++){

				//i等于当前阶段下标时，当前阶段为绿标
				if (i == index){

					//绿标----------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-map-marker mystage");
					$("#"+i).css("color","#90F790");

				//遍历阶段下标小于当前阶段下标时
				//表明遍历的下标处于当前下标前面，所以因该为绿圈
				}else if (i < index){

					//绿圈----------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-ok-circle mystage");
					$("#"+i).css("color","#90F790");

				//位于当前阶段下标后面，业务还未达到，所以应该为黑圈
				}else {

					//黑圈----------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-record mystage");
					$("#"+i).css("color","#000000");

				}

			}

			//遍历后两个
			for (var i=point; i < <%=dicValueList.size()%>; i++){

				//黑叉-------------
				$("#"+i).removeClass();
				$("#"+i).addClass("glyphicon glyphicon-remove mystage");
				$("#"+i).css("color","#000000");

			}
		}

	}
	
	
	
</script>

</head>
<body>
	
	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${requestScope.tran.customerId}-${tran.name} <small>￥${tran.money}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" onclick="window.location.href='edit.html';"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>

	<!-- 阶段状态 -->
	<div style="position: relative; left: 40px; top: -50px;">
		阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

		<%
			//准备当前阶段[请求转发向当前页面传递了一个当前交易的Tran对象]
			Tran tran = (Tran) request.getAttribute("tran");
			String currentStage = tran.getStage();
			//获取当前阶段的可能性
			String currentPossibility = tran.getPossibility();
//			String currentPossibility = pMap.get(currentStage);

			//判断当前可能性
			//如果当前可能性为0，则当前阶段为后两个[08丢失的线索][09因竞争丢失关闭]已丢失的阶段
			//如果当前可能性为0，前7个一定是黑圈，后两个一个为黑叉，一个为红叉
			if ("0".equals(currentPossibility)){

				//遍历每一个阶段
				for(int i=0; i<dicValueList.size(); i++){

					DicValue dicValue = dicValueList.get(i);
					String listStage = dicValue.getValue();
					//每一个阶段的可能性[possibility]
					String listPossibility = pMap.get(listStage);

					//因为在最外层if判断中，可能性为0时已经确认前7个一定是黑圈，后两个一个为黑叉，一个为红叉
					//此时判断当前阶段可能性与遍历的可能性比较
					//如果遍历出来的可能性为0，则一定为后两个，其中一个为黑叉，一个为红叉
					if ("0".equals(currentPossibility)){

						//在确定前面七个都为黑圈，后两个中一个为黑叉，一个为红叉的前提下，
						//对当前交易的阶段和遍历出来的阶段对比
						//如果当前阶段与遍历的阶段一致，则当前阶段为红叉[因为此时一定会有一个红叉]
						if (listStage.equals(currentStage)){
//							------------红叉

		%>

		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #FF0000;"></span>
		-----------

		<%

						}else {
//							------------黑叉

		%>

		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #000000;"></span>
		-----------

		<%

						}
					//在确定前面七个都为黑圈的前提下
					//如果遍历出来的阶段可能性不为0，则此时遍历出来的阶段为黑圈
					//else里面是前七个阶段的黑圈
					}else {
//						----------------黑圈

		%>

		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #000000;"></span>
		-----------

		<%

					}
				}
				//当前阶段的可能性为0时的判断结束

			//此处是当前阶段的可能性不为0时的处理
			//此处理中可以确定当前阶段处于前7个，后两个一定为黑叉，表示当前交易正在进行
			}else {

				//准备一个当前阶段的下标index
				int index = 0;
				//遍历每一个阶段,找出当前阶段的下标
				for (int i=0; i<dicValueList.size(); i++) {

					DicValue dicValue = dicValueList.get(i);
					String listStage = dicValue.getValue();

					//如果当前阶段与遍历的阶段一致，取得这个阶段所对应的下标
					if (listStage.equals(currentStage)){
						index = i;
						break;
					}
				}
				//取当前阶段下标结束


				//遍历每一个阶段
				for (int i=0; i<dicValueList.size(); i++){

					DicValue dicValue = dicValueList.get(i);
					String listStage = dicValue.getValue();
					//每一个阶段的可能性[possibility]
					String listPossibility = pMap.get(listStage);

					//可能性为0时是后两个，但是在这个大的else处理中，已经确定后两个为黑叉
					if ("0".equals(listPossibility)){

//						-----------黑叉

		%>

		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #000000;"></span>
		-----------

		<%

					//前7个的分别判断
					}else{
						//这是当前的阶段
						if (i == index){

//							-------绿标[表示当前正处于这个阶段]

		%>

		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #90F790;"></span>
		-----------

		<%


						//i<index[当前遍历出来的阶段处于当前阶段的前面]，为绿圈
						}else if (i < index){

//							-------绿圈[当前遍历出来的阶段处于当前阶段的前面]

		%>

		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #90F790;"></span>
		-----------

		<%


						//i>index[当前遍历出来的阶段处于当前阶段的后面]，还没有达到，为黑圈
						}else{

//							-------黑圈[当前遍历出来的阶段处于当前阶段的后面]

		%>

		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #000000;"></span>
		-----------

		<%


						}

					}
					//前7个阶段判断结束

				}
				//当前阶段的可能性不为0时的判断结束
			}

		%>



		<%--<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="资质审查" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="价值建议" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="确定决策者" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom" data-content="提案/报价" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="谈判/复审"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="成交"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="丢失的线索"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="因竞争丢失关闭"></span>
		-------------%>
		<span class="closingDate">${tran.expectedDate}</span>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: 0px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.money}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}-${tran.name}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.expectedDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">客户名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${tran.stage}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">类型</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.type}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="possibility">${tran.possibility}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">来源</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.source}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.activityId}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">联系人名称</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.contactsId}</b></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${tran.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">${tran.editBy}&nbsp;&nbsp;</b><small id="editTime" style="font-size: 10px; color: gray;">${tran.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${tran.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					&nbsp;${tran.contactSummary}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 100px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>&nbsp;${tran.nextContactTime}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 100px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		
		<!-- 备注1 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<!-- 备注2 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>呵呵！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary">保存</button>
				</p>
			</form>
		</div>
	</div>
	
	<!-- 阶段历史 -->
	<div>
		<div style="position: relative; top: 100px; left: 40px;">
			<div class="page-header">
				<h4>阶段历史</h4>
			</div>
			<div style="position: relative;top: 0px;">
				<table id="activityTable" class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td>阶段</td>
							<td>金额</td>
							<td>可能性</td>
							<td>预计成交日期</td>
							<td>创建时间</td>
							<td>创建人</td>
						</tr>
					</thead>
					<tbody id="tranHistoryTbody">
						<%--<tr>
							<td>资质审查</td>
							<td>5,000</td>
							<td>10</td>
							<td>2017-02-07</td>
							<td>2016-10-10 10:10:10</td>
							<td>zhangsan</td>
						</tr>
						<tr>
							<td>需求分析</td>
							<td>5,000</td>
							<td>20</td>
							<td>2017-02-07</td>
							<td>2016-10-20 10:10:10</td>
							<td>zhangsan</td>
						</tr>
						<tr>
							<td>谈判/复审</td>
							<td>5,000</td>
							<td>90</td>
							<td>2017-02-07</td>
							<td>2017-02-09 10:10:10</td>
							<td>zhangsan</td>
						</tr>--%>
					</tbody>
				</table>
			</div>
			
		</div>
	</div>
	
	<div style="height: 200px;"></div>
	
</body>
</html>