<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>	



					{title:"<s:message code="label.entity.id" text="Id"/>", name:"id", canFilter:false,canEdit:false},
        			{title:"<s:message code="label.entity.name" text="Name"/>", name:"name",canEdit:false},
        			{title:"<s:message code="label.product.available" text="Available"/>", name:"available",type:"boolean",canEdit:true},
        			{title:"<s:message code="label.entity.details" text="Details"/>", name: "buttonField", align: "center",canFilter:false,canSort:false, canReorder:false}
