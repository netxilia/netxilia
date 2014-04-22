<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<h2>Workbook: ${model.id}</h2>
Datasource: <a href="javascript:nx.workbook.viewDatasource(${model.dataSourceConfiguration.id})">${model.dataSourceConfiguration.name}</a>