<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:html="http://www.w3.org/TR/REC-html40">
 <DocumentProperties xmlns="urn:schemas-microsoft-com:office:office">
  <Author>xmy</Author>
  <LastAuthor>xmy</LastAuthor>
  <Created>2015-06-05T18:19:34Z</Created>
  <LastSaved>2023-03-15T05:18:52Z</LastSaved>
  <Version>16.00</Version>
 </DocumentProperties>
 <OfficeDocumentSettings xmlns="urn:schemas-microsoft-com:office:office">
  <AllowPNG/>
 </OfficeDocumentSettings>
 <ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">
  <WindowHeight>12648</WindowHeight>
  <WindowWidth>22260</WindowWidth>
  <WindowTopX>32767</WindowTopX>
  <WindowTopY>32767</WindowTopY>
  <ProtectStructure>False</ProtectStructure>
  <ProtectWindows>False</ProtectWindows>
 </ExcelWorkbook>
 <Styles>
  <Style ss:ID="Default" ss:Name="Normal">
   <Alignment ss:Vertical="Bottom"/>
   <Borders/>
   <Font ss:FontName="等线" x:CharSet="134" ss:Size="11" ss:Color="#000000"/>
   <Interior/>
   <NumberFormat/>
   <Protection/>
  </Style>
  <Style ss:ID="s62">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
  </Style>
  <Style ss:ID="s63">
   <Alignment ss:Vertical="Center"/>
  </Style>
 </Styles>
 <Worksheet ss:Name="Sheet1">
  <Table ss:ExpandedColumnCount="5" x:FullColumns="1"
   x:FullRows="1" ss:DefaultRowHeight="13.8">
   <Row>
    <Cell ss:MergeAcross="3" ss:StyleID="s62"><Data ss:Type="String">产品库存表</Data></Cell>
    <Cell ss:StyleID="s63"/>
   </Row>
   <Row>
    <Cell ss:StyleID="s63"><Data ss:Type="String">产品名称</Data></Cell>
    <Cell ss:StyleID="s63"><Data ss:Type="String">产品编码</Data></Cell>
    <Cell ss:StyleID="s63"><Data ss:Type="String">型号</Data></Cell>
    <Cell ss:StyleID="s63"><Data ss:Type="String">单位</Data></Cell>
   </Row>
   <#list products as product>
	<Row>
		<Cell>
			<Data ss:Type="String">${product.name!}</Data>
		</Cell>
		<Cell>
			<Data ss:Type="String">${product.number!}</Data>
		</Cell>
		<Cell>
			<Data ss:Type="String">${product.type!}</Data>
		</Cell>
		<Cell>
			<Data ss:Type="String">${product.unit!}</Data>
		</Cell>
	</Row>
</#list>
   <Row>
    <Cell ss:StyleID="s63"/>
    <Cell ss:StyleID="s63"/>
    <Cell ss:StyleID="s63"/>
    <Cell ss:StyleID="s63"/>
   </Row>
  </Table>
  <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
   <PageSetup>
    <Header x:Margin="0.3"/>
    <Footer x:Margin="0.3"/>
    <PageMargins x:Bottom="0.75" x:Left="0.7" x:Right="0.7" x:Top="0.75"/>
   </PageSetup>
   <Print>
    <ValidPrinterInfo/>
    <PaperSizeIndex>9</PaperSizeIndex>
    <HorizontalResolution>600</HorizontalResolution>
    <VerticalResolution>600</VerticalResolution>
   </Print>
   <Selected/>
   <Panes>
    <Pane>
     <Number>3</Number>
     <ActiveRow>2</ActiveRow>
    </Pane>
   </Panes>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
</Workbook>