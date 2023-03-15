<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook
        xmlns="urn:schemas-microsoft-com:office:spreadsheet"
        xmlns:o="urn:schemas-microsoft-com:office:office"
        xmlns:x="urn:schemas-microsoft-com:office:excel"
        xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
        xmlns:html="http://www.w3.org/TR/REC-html40"
        xmlns:dt="uuid:C2F41010-65B3-11d1-A29F-00AA00C14882">
    <DocumentProperties
            xmlns="urn:schemas-microsoft-com:office:office">
        <LastAuthor>chengzhengwen</LastAuthor>
        <Created>2023-03-15T02:34:41Z</Created>
        <LastSaved>2023-03-15T02:37:19Z</LastSaved>
    </DocumentProperties>
    <CustomDocumentProperties
            xmlns="urn:schemas-microsoft-com:office:office">
        <ICV dt:dt="string">1290C2D6E1BE419D91F1A989EC39D360</ICV>
        <KSOProductBuildVer dt:dt="string">2052-11.1.0.12980</KSOProductBuildVer>
    </CustomDocumentProperties>
    <ExcelWorkbook
            xmlns="urn:schemas-microsoft-com:office:excel">
        <WindowWidth>28125</WindowWidth>
        <WindowHeight>12090</WindowHeight>
        <ProtectStructure>False</ProtectStructure>
        <ProtectWindows>False</ProtectWindows>
    </ExcelWorkbook>
    <Styles>
        <Style ss:ID="Default" ss:Name="Normal">
            <Alignment ss:Vertical="Center"/>
            <Borders/>
            <Font ss:FontName="宋体" x:CharSet="134" ss:Size="11" ss:Color="#000000"/>
            <Interior/>
            <NumberFormat/>
            <Protection/>
        </Style>
        <Style ss:ID="s49"/>
        <Style ss:ID="s50">
            <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
        </Style>
        <Style ss:ID="s51">
            <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
        </Style>
    </Styles>
    <Worksheet ss:Name="Sheet1">
        <Table ss:ExpandedColumnCount="4" ss:ExpandedRowCount="3" x:FullColumns="1" x:FullRows="1" ss:DefaultColumnWidth="54" ss:DefaultRowHeight="13.5">
            <Row>
                <Cell ss:StyleID="s51" ss:MergeAcross="3">
                    <Data ss:Type="String">产品库存表</Data>
                </Cell>
            </Row>
            <Row>
                <Cell>
                    <Data ss:Type="String">产品名称</Data>
                </Cell>
                <Cell>
                    <Data ss:Type="String">产品编码</Data>
                </Cell>
                <Cell>
                    <Data ss:Type="String">型号</Data>
                </Cell>
                <Cell>
                    <Data ss:Type="String">单位</Data>
                </Cell>
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
    </Table>
    <WorksheetOptions
            xmlns="urn:schemas-microsoft-com:office:excel">
        <PageSetup>
            <Header x:Margin="0.3"/>
            <Footer x:Margin="0.3"/>
            <PageMargins x:Left="0.7" x:Right="0.7" x:Top="0.75" x:Bottom="0.75"/>
        </PageSetup>
        <Selected/>
        <TopRowVisible>0</TopRowVisible>
        <LeftColumnVisible>0</LeftColumnVisible>
        <PageBreakZoom>100</PageBreakZoom>
        <Panes>
            <Pane>
                <Number>3</Number>
                <ActiveRow>6</ActiveRow>
                <ActiveCol>2</ActiveCol>
                <RangeSelection>R7C3</RangeSelection>
            </Pane>
        </Panes>
        <ProtectObjects>False</ProtectObjects>
        <ProtectScenarios>False</ProtectScenarios>
    </WorksheetOptions>
</Worksheet>
</Workbook>