<%@ attribute name="left" fragment="true"%>
<%@ attribute name="right" fragment="true"%>
<%@ attribute name="center" fragment="true"%>

<%@ attribute name="containerClass"%>
<%@ attribute name="centerClass"%>

<%@ attribute name="leftWidth"%>
<%@ attribute name="leftPadding"%>
<%@ attribute name="leftClass"%>

<%@ attribute name="rightWidth"%>
<%@ attribute name="rightPadding"%>
<%@ attribute name="rightClass"%>

<div class="threeColumnFixed ${containerClass}"> 
    <div class="colmid" style="margin-left:-${rightWidth}px;"> 
        <div class="colleft" style="left:${leftWidth + rightWidth}px;"> 
            <div class="col1wrap" style="right:${leftWidth}px;"> 
                <div class="col1 ${centerClass}" style="margin:0 ${rightWidth + rightPadding}px 0 ${leftWidth + leftPadding}px; "> 
                     <jsp:invoke fragment="center" />
                </div> 
            </div> 
            <div class="col2 ${leftClass}" style="width:${leftWidth}px; right:0px"> 
                <jsp:invoke fragment="left" />
            </div> 
            <div class="col3 ${rightClass}" style="width:${rightWidth}px; margin-right:0px;">            
                <jsp:invoke fragment="right" />
           	</div> 
		</div> 
	</div> 
	<jsp:doBody/>
</div> 
            