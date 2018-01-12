 var xmlHttp;

var createXMLHttpRequest = function(){
	if(window.XMLHttpRequest)
	{
		xmlHttp=new XMLHttpRequest();
		if (xmlHttp.overrideMimeType) {
			xmlHttp.overrideMimeType("text/xml");
		}
	}
	if(window.ActiveXObject)
	{
		try{
			xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
		}
		catch(e)
		{
			xmlHttp= new ActiveXObject("Microsoft.XMLHttp");
		}	
	}
	if(!xmlHttp)
	{
		window.alert("对不起，您的浏览器不支持XMLHttpRequest，请使用谷歌火狐等主流浏览器");
	}
} 

var handleStateChange = function(){
	if(xmlHttp.readyState==4)
	{
		if(xmlHttp.status==200)
		{
			var responseDiv = document.getElementById("ticketLabel");
			while(responseDiv.hasChildNodes())  //同时删除动画
		     {    
					responseDiv.removeChild(responseDiv.childNodes[0]);
		     }  
			
			var xmlfile=xmlHttp.responseXML;
			
			//var total = $(xmlfile).find("total")[0].childNodes[0].nodeValue;
			var total = xmlfile.getElementsByTagName("total")[0].childNodes[0].nodeValue;
			var totalNum=Number(total);
			
			ticketBox = document.createElement("div");
			ticketBox.setAttribute("class","container-fluid text-center bg-success");
			
			ticketBoxChild = document.createElement("div");
			ticketBoxChild.setAttribute("class","content text-center");
			ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("content")[0].childNodes[0].nodeValue;
			ticketBox.appendChild(ticketBoxChild);
			responseDiv.appendChild(ticketBox);
			
			//可以查询到结果之后的处理
			for(var i=0;i<totalNum;i++)
			{
				ticketBox = document.createElement("div");
				ticketBox.setAttribute("class","container-fluid text-center bg-info ticketBoxSeparator");
				ticketBox.setAttribute("id","result"+i);
				//出发时间
				ticketBoxChild = document.createElement("div");
				ticketBoxChild.setAttribute("class","col-md-3 col-xs-6 col-sm-6");
				ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("TimeDeparture")[i].childNodes[0].nodeValue;
				ticketBox.appendChild(ticketBoxChild);
				//出发机场
				ticketBoxChild = document.createElement("div");
				ticketBoxChild.setAttribute("class","col-md-3 col-xs-6 col-sm-6");
				ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("LocationDeparture")[i].childNodes[0].nodeValue;
				ticketBox.appendChild(ticketBoxChild);
				//到达时间
				ticketBoxChild = document.createElement("div");
				ticketBoxChild.setAttribute("class","col-md-3 col-xs-6 col-sm-6");
				ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("TimeArrive")[i].childNodes[0].nodeValue;
				ticketBox.appendChild(ticketBoxChild);
				//到达机场
				ticketBoxChild = document.createElement("div");
				ticketBoxChild.setAttribute("class","col-md-3 col-xs-6 col-sm-6");
				ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("LocationArrive")[i].childNodes[0].nodeValue;
				ticketBox.appendChild(ticketBoxChild);
				//中转城市 中转机场
				ticketBoxChild = document.createElement("div");
				ticketBoxChild.setAttribute("class","col-md-6 col-xs-12 col-sm-12");
				ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("TransferCity")[i].childNodes[0].nodeValue;
				ticketBox.appendChild(ticketBoxChild);
				//承运航空公司
				ticketBoxChild = document.createElement("div");
				ticketBoxChild.setAttribute("class","col-md-4 col-xs-6 col-sm-6");
				ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("NameCooperation")[i].childNodes[0].nodeValue;
				ticketBox.appendChild(ticketBoxChild);
				//总飞行时间
				ticketBoxChild = document.createElement("div");
				ticketBoxChild.setAttribute("class","col-md-4 col-xs-6 col-sm-6");
				ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("TimeBetween")[i].childNodes[0].nodeValue;
				ticketBox.appendChild(ticketBoxChild);
				//含税总价格
				ticketBoxChild = document.createElement("div");
				ticketBoxChild.setAttribute("class","col-md-4 col-xs-6 col-sm-6");
				ticketBoxChild.innerHTML=xmlfile.getElementsByTagName("ChangePrice")[i].childNodes[0].nodeValue;
				ticketBox.appendChild(ticketBoxChild);
				//购票链接
				ticketBoxChild = document.createElement("a");
				ticketUrl=xmlfile.getElementsByTagName("TicketUrl")[i].childNodes[0].nodeValue;
				ticketBoxChild.setAttribute("href",ticketUrl);
				ticketBoxChild.setAttribute("class","col-md-4 col-xs-6 col-sm-6");
				fromSite=xmlfile.getElementsByTagName("FromSite")[i].childNodes[0].nodeValue;
				ticketBoxChild.innerHTML="进入"+fromSite+"购票";
				ticketBox.appendChild(ticketBoxChild);
				
				responseDiv.appendChild(ticketBox);
			}
		}
	}
}
//创建loading动画
var showLoading = function (){
	var ticketLabel = document.getElementById("ticketLabel");
	while(ticketLabel.hasChildNodes())  //同时删除动画
    {    
		ticketLabel.removeChild(ticketLabel.childNodes[0]);
    }  
	spinner = document.createElement("div");
	spinner.setAttribute("class","spinner");
	for(var i=0;i<5;i++){
		rect = document.createElement("div");
		rect.setAttribute("class","rect"+i);
		spinner.appendChild(rect);
	}
	var ticketLabel = document.getElementById("ticketLabel");
	ticketLabel.appendChild(spinner);
}

var getSelectOption = function()
{
	var sortSelect = document.getElementById("sortSelect");
	// return sortSelect.selectIndex;//获得索引
	return sortSelect.selectedIndex;
}

var rank = function (choice0){
	createXMLHttpRequest();
	var url = "servlet/root_servlet";
	var queryString="";
	var fromcity = document.getElementById("fromcity").value;
	var tocity   = document.getElementById("tocity").value;
	var searchDepartureTime = document.getElementById("searchDepartureTime").value;
	// var searchArrivalTime = document.getElementById("searchArrivalTime").value;

	queryString=queryString+"fromcity="+fromcity+"&tocity="+tocity+"&searchDepartureTime="+searchDepartureTime+"&choice0="+choice0;

	xmlHttp.open("POST",url,true);
	xmlHttp.onreadystatechange=handleStateChange;
	xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
	xmlHttp.send(queryString);
	showLoading();//加载动画
}