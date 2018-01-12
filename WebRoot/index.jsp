<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>公务机票推荐系统</title>
    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/index_css.css">
    <link rel="stylesheet" type="text/css" href="css/bootstrap-datetimepicker.min.css">
    

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
    <!-- start导航 -->
    <!-- <div class="top-navigation"> -->
      <nav class="navbar navbar-inverse navbottom">
        <div class="container-fluid">
          <!-- Brand and toggle get grouped for better mobile display -->
          <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/ticketsystem/index.jsp"><span class="glyphicon glyphicon-home" aria-hidden="true"></span></a>
          </div>

          
        <!-- Collect the nav links, forms, and other content for toggling -->
          <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav navbar-left">
              <li class="active"><a href="/ticketsystem/index.jsp">国际公务机票查询<span class="sr-only">(current)</span></a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
              <li><a href="/ticketsystem/thisweb.jsp">关于此站</a></li>
            </ul>
          </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
      </nav>
    <!-- end 导航 -->
      
      
      <div class="jumbotron">
        <div class="container">
          <div class="search">

            <div class="container">

              <!-- 地区选择 -->

              <form action="#" method="post" class="form-horizontal"  role="form">
                  
                    <div class="form-group col-md-6">
                      <label class="control-label" for="fromcity">出发城市</label>
                      <input type="text" class="form-control" id="fromcity" name="fromcity" placeholder="例如：北京">
                    </div>
                    <div class="form-group col-md-6">
                      <label class="control-label" for="tocity">到达城市</label>
                      <input type="text" class="form-control" id="tocity" name="tocity" placeholder="例如：首尔">
                    </div>
                    
                    
                    <div class="form-group col-md-6">
                      <label class="control-label" for="searchDepartureTime">出发时间</label>
                      <div class="input-group date form_date col-md-12" data-date="" data-date-format="" data-link-field="searchDepartureTime" data-link-format="yyyy-mm-dd" style="padding-right:500" id="datetimepicker">
                        <input type="text" class="form-control" name="searchDepartureTime" id="datetimepicker" readonly>
                        <span class="input-group-addon">
                          <span class="glyphicon glyphicon-remove"></span>
                        </span>
                        <span class="input-group-addon">
                          <span class="glyphicon glyphicon-th"></span>
                        </span>
                      </div>
                      <input type="hidden" id="searchDepartureTime" value="">
                    </div>
                              
                    <!--<div class="form-group">
                      <label class="sr-only" for="searchArrivalTime">回程时间</label>
                      <input type="text" class="form-control" id="searchArrivalTime" name="searchArrivalTime" placeholder="例如：2016-02-02">
                    </div> -->

                  <div class="form-group col-md-6">
                      <label class="control-label" for="selectSort">排序方式选择</label>
                      <select class="form-control" id="sortSelect">
                        <option>按起飞时间排序</option>
                        <option>按总飞行时间排序</option>
                        <option>按总价格排序</option>
                      </select>
                  </div>
                  

                  <input type="button" onclick="rank(getSelectOption());" name="searchButton" class="btn btn-info btn-lg btn-block col-md-3" value="搜索航班" />
                  
              </form>

            </div>

          </div>
        </div>

      </div>

      
      <div id="ticketLabel"></div>
 

      <!-- start 版权 -->
      <div class="footer">
        <div class="container">
          <div class="row">
            <div class="footer-notes">
              <div class="footerrow text-center">
                <h3 style="color:#FFFFFF;border-bottom:1px solid #337AB7;">友情链接</h3>
                <p><a href="http://yuweihuang.me">开发者博客</a></p>  
                <p><a href="http://www.tuniu.com/">途牛旅游网</a></p>
                <p><a href="http://www.ly.com/">同城旅游网</a></p>
                <p><a href="http://flights.ctrip.com/">携程旅游网</a></p>
                <!-- <p>
                  <span class="glyphicon glyphicon-share" aria-hidden="true"></span>
                </p> -->
              </div>
              <div class="footerrow text-center">
                <h3 style="color:#FFFFFF;border-bottom:1px solid #337AB7;">联系我们</h3>
                <p>huangyw95@foxmail.com</p>
                <p>daipilin@bupt.edu.cn</p>
                <p>1092662293@qq.com</p>
              </div>
              
            </div>
          </div>
          <div class="separator">
          </div>
          <div class="row">
            <div class="copyright text-center">
              <p>All right reserved • ICT 2016 • Made in Beijing</p>
            </div>
          </div>
        </div>
      </div>
      <!-- end版权 -->
    
    
    <script type="text/javascript" src="js/servletAjax.js"></script>
    <script src="js/jquery/jquery.js"></script>
    <script src="js/bootstrap/bootstrap.js"></script>
    <script src="js/bootstrap-datetimepicker.min.js"></script>
    <script src="js/bootstrap-datetimepicker.zh-CN.js"></script>
    <script src="js/datecontrol.js"></script>
    <!--工具方法-->
   
  </body>
</html>