<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
        <!--
        This XSL uses the browse_data XML from a call to WebSphereMonitor.getBrowseData() and transforms it into an HTML interface
        which allows the user to select from a hierarchical tree of data modules.  
        This tree is intended to be inserted into a WebSphere Solution page in SiteView.  See the SiteView classes
        appServerSolutionTemplatePage, monitorSetPage, and monitorSetTemplate for more info on the format of the name= and value= 
        attributes of the checkboxes.  
        This file could probably undergo some more refactoring to move all repeated code into named templates.  
        -->

        <!--  This initial template matches the root element (browse_data) and basically just tries to find the server element.
        The reason for the somewhat confusing xsl:choose logic is that, for network deployed versions of WebSphere, the browse tree
        will contain a "type=node" element between the cell and the server, whereas for standalone versions, the browse tree
        will just contain a cell node and a server node.  At some point I think the clarity of this entire XSL file could be 
        greatly improved by refactoring it to use callable templates with parameters, but right now there is not enough time for
        that kind of an undertaking.
        -->

        <xsl:template match="browse_data">
                <a name="top"/>
                <h3>Jump to a Module </h3>
                <ul>
                        <xsl:choose>
                                <xsl:when test="object[@type='cell']/object[@type='server']">
                                        <xsl:apply-templates select="object[@type='cell']/object[@type='server']" mode="contents"/>
                                </xsl:when>
                                <xsl:when test="object[@type='cell']/object[@type='node']/object[@type='server']">
                                        <xsl:apply-templates select="object[@type='cell']/object[@type='node']/object[@type='server']" mode="contents"/>
                                </xsl:when>
                                <xsl:otherwise>
                                        <xsl:element name="i">
                                                <xsl:text> No application servers were found on the given WebSphere host.</xsl:text>
                                        </xsl:element>
                                </xsl:otherwise>
                        </xsl:choose>
                </ul>


                <h3> Modules </h3>
                <ul>
                        <xsl:choose>
                                <xsl:when test="object[@type='cell']/object[@type='server']">
                                        <xsl:apply-templates select="object[@type='cell']/object[@type='server']" mode="tree"/>
                                </xsl:when>
                                <xsl:when test="object[@type='cell']/object[@type='node']/object[@type='server']">
                                        <xsl:apply-templates select="object[@type='cell']/object[@type='node']/object[@type='server']" mode="tree"/>
                                </xsl:when>
                                <xsl:otherwise>
                                        <xsl:element name="i">
                                                <xsl:text> No application modules could be located because no application servers were 
                                                        found on the given WebSphere host.</xsl:text>
                                        </xsl:element>
                                </xsl:otherwise>
                        </xsl:choose>
                </ul>
                <br></br>
                <a name="bottom"/>
        </xsl:template>

        <xsl:template match="object[@type='server']" mode="contents">
                <h4>Server: 
                        <xsl:element name="a">
                                <xsl:attribute name="href"> 
                                        #<xsl:value-of select="@type"/>
                                </xsl:attribute>
                                <xsl:value-of select="@type"/>
                        </xsl:element>
                </h4>
                <h4>  Runtime Modules </h4>
                <ul>
                        <xsl:for-each select="object[@type='JVM Runtime']" >
                                <xsl:call-template name="writeAutomaticSelectionsForContents"/>
                        </xsl:for-each>
                        <xsl:for-each select="object[@type='Transaction Manager']" >
                                <xsl:call-template name="writeAutomaticSelectionsForContents"/>
                        </xsl:for-each>
                        <xsl:apply-templates select="object[@type='Thread Pools']" mode="contents"/>
                        <xsl:apply-templates select="object[@type='JDBC Connection Pools']" mode="contents"/>
                        <xsl:apply-templates select="object[@type='Servlet Session Manager']" mode="contents"/>
                </ul>
                <h4>  Application Modules </h4>
                <ul>
                        <xsl:apply-templates select="object[@type='Enterprise Beans']" mode="contents"/>
                        <xsl:apply-templates select="object[@type='Web Applications']" mode="contents"/>
                </ul>
        </xsl:template>

        <!-- 
        For every object that has xsl:apply-templates called on it with the mode "contents", we want to 
        generate a link. The corresponding anchor that is pointed to by these links is generated by the "tree" mode templates.
        -->
        <xsl:template match="object" mode="contents">
                <li>
                        <xsl:element name="a">
                                <xsl:attribute name="href"> 
                                        #<xsl:value-of select="@type"/>
                                </xsl:attribute>
                                <xsl:value-of select="@type"/>
                        </xsl:element>
                </li>
        </xsl:template>

        <xsl:template match="object[@type='server']" mode="tree">
                <h4>Server: 
                        <xsl:value-of select="@name"/>
                </h4>
                <h4>  Runtime Modules </h4>
                <ul>
                        <xsl:for-each select="object[@type='JVM Runtime']" >
                                <xsl:call-template name="writeAutomaticSelectionsForTree" />
                        </xsl:for-each>
                        <xsl:for-each select="object[@type='Transaction Manager']" >
                                <xsl:call-template name="writeAutomaticSelectionsForTree"/>
                        </xsl:for-each>
                        <xsl:apply-templates select="object[@type='Thread Pools']" mode="tree"/>
                        <xsl:apply-templates select="object[@type='JDBC Connection Pools']" mode="tree"/>
                        <xsl:apply-templates select="object[@type='Servlet Session Manager']" mode="tree"/>
                </ul>
                <h4>  Application Modules </h4>
                <ul>
                        <xsl:apply-templates select="object[@type='Enterprise Beans']" mode="tree"/>
                        <xsl:apply-templates select="object[@type='Web Applications']" mode="tree"/>
                </ul>
        </xsl:template>

        <xsl:template match="object[@type='Thread Pools']" mode="tree">
                <li>
                        <xsl:element name="a">
                                <xsl:attribute name="name">
                                        <xsl:value-of select="@type"/>
                                </xsl:attribute>
                        </xsl:element>
                        <xsl:value-of select="@type"/>
                        <xsl:call-template name="up_down_arrows"/>
                </li>
                <ul>
                        <xsl:for-each select="child::object">
                                <li>
                                        <xsl:element name="input">
                                                <xsl:attribute name="type">checkbox</xsl:attribute>
                                                <xsl:attribute name="value">
                                                        <xsl:value-of select="@solutionsID"/> 
                                                </xsl:attribute>
                                                <xsl:attribute name="name">
                                                        <xsl:text>@Thread Pool@: </xsl:text>
                                                        <xsl:value-of select="@name"/>
                                                </xsl:attribute>
                                                <i><xsl:value-of select="@name"/></i>
                                        </xsl:element>
                                </li>
                        </xsl:for-each>
                </ul>
        </xsl:template>

        <xsl:template match="object[@type='Servlet Session Manager']" mode="tree">
                <li>
                        <xsl:element name="a">
                                <xsl:attribute name="name">
                                        <xsl:value-of select="@type"/>
                                </xsl:attribute>
                        </xsl:element>
                        <xsl:value-of select="@type"/>
                        <xsl:call-template name="up_down_arrows"/>
                </li>
                <ul>
                        <xsl:for-each select="child::object">
                                <li>
                                        <xsl:element name="input">
                                                <xsl:attribute name="type">checkbox</xsl:attribute>
                                                <xsl:attribute name="value">
                                                        <xsl:value-of select="@solutionsID"/> 
                                                </xsl:attribute>
                                                <xsl:attribute name="name">
                                                        <xsl:text>@Servlet Session Manager@: </xsl:text>
                                                        <xsl:value-of select="@name"/>
                                                </xsl:attribute>
                                                <i><xsl:value-of select="@name"/></i>
                                        </xsl:element>
                                </li>
                        </xsl:for-each>
                </ul>
        </xsl:template>

        <xsl:template match="object[@type='JDBC Connection Pools']" mode="tree">
                <li>
                        <xsl:element name="a">
                                <xsl:attribute name="name">
                                        <xsl:value-of select="@type"/>
                                </xsl:attribute>
                        </xsl:element>
                        <xsl:value-of select="@type"/>
                        <xsl:call-template name="up_down_arrows"/>
                </li>
                <ul>
                        <xsl:for-each select="child::object">
                                <li>
                                        <xsl:element name="input">
                                                <xsl:attribute name="type">checkbox</xsl:attribute>
                                                <xsl:attribute name="value">
                                                        <xsl:value-of select="@solutionsID"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="name">
                                                        <xsl:text>@JDBC Connection Pool@: </xsl:text>
                                                        <xsl:value-of select="@name"/>
                                                </xsl:attribute>
                                                <i><xsl:value-of select="@name"/></i>
                                        </xsl:element>
                                </li>
                        </xsl:for-each>
                </ul>
        </xsl:template>

        <xsl:template match="object[@type='Enterprise Beans']" mode="tree">
                <li>
                        <xsl:element name="a">
                                <xsl:attribute name="name">
                                        <xsl:value-of select="@type"/>
                                </xsl:attribute>
                        </xsl:element>
                        <xsl:value-of select="@type"/>
                        <xsl:call-template name="up_down_arrows"/>
                </li>
                <ul>
                        <xsl:for-each select="object">
                                <li>
                                        <xsl:element name="input">
                                                <xsl:attribute name="type">checkbox</xsl:attribute>
                                                <xsl:attribute name="value">
                                                        <xsl:value-of select="@solutionsID"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="name">
                                                        <xsl:text>@Enterprise Java Bean@: </xsl:text>
                                                        <xsl:value-of select="@name"/>
                                                </xsl:attribute>
                                                <i><xsl:value-of select="@name"/></i>
                                                <xsl:if test="count(child::object) > 0">
                                                        <xsl:text> (aggregated over all sub-modules) </xsl:text>
                                                        <xsl:call-template name="up_down_arrows"/>
                                                </xsl:if>
                                        </xsl:element>
                                        <xsl:apply-templates select="object" mode="EJBSubModules"/>
                                </li>
                        </xsl:for-each>
                </ul>

        </xsl:template>

        <!--
        The EJB sub-modules are a little bit funky, since the different types of EJBs support different metrics (even though they all
        appear in the same counter hierarchy).  What complicates things a little bit is that we must look at the EJB's parent object 
        to decide what type (Stateless Session, Entity, etc.) it is.  Hence the xsl:choose element within this template.  
        Also, since sub-modules of an EJB module may either be an individual bean or a "category" of beans, we include the text
        "(aggregated over all sub-modules)" to provide a clue to the user as to the meaning of a checkbox item in the tree that simply 
        says: "Entity EJB" and has several children EJBs.
        -->
        <xsl:template match="object" mode="EJBSubModules">
                <xsl:element name="ul">
                        <xsl:element name="li">
                                <xsl:element name="input">
                                        <xsl:attribute name="type">checkbox</xsl:attribute>
                                        <xsl:attribute name="value">
                                                <xsl:value-of select="@solutionsID"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="name">
                                                <xsl:text>@</xsl:text>
                                                <xsl:choose >
                                                        <xsl:when test="not((@type='Message Driven Bean') or 
                                                                (@type='Stateful Session Bean') or 
                                                                (@type='Stateless Session Bean') or 
                                                                (@type='Entity Bean'))" >
                                                                <xsl:value-of select="parent::*/@type"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                                <xsl:value-of select="@type"/>
                                                        </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:text>@: </xsl:text>
                                                <xsl:for-each select="ancestor::*">
                                                        <xsl:if test="@name">
                                                                <xsl:value-of select="@name"/>
                                                                <xsl:text>/</xsl:text>
                                                        </xsl:if>
                                                </xsl:for-each>
                                                <xsl:value-of select="@name"/>

                                        </xsl:attribute>
                                        <i><xsl:value-of select="@name"/></i>
                                        <xsl:if test="count(child::object) > 0">
                                                <xsl:text> (aggregated over all sub-modules) </xsl:text>
                                        </xsl:if>
                                </xsl:element>
                                <xsl:apply-templates select="object" mode="EJBSubModules"/>
                        </xsl:element>
                </xsl:element>
        </xsl:template>

        <xsl:template match="object[@type='Web Applications']" mode="tree">
                <li>
                        <xsl:element name="a">
                                <xsl:attribute name="name">
                                        <xsl:value-of select="@type"/>
                                </xsl:attribute>
                        </xsl:element>
                        <xsl:value-of select="@type"/>
                        <xsl:call-template name="up_down_arrows"/>
                </li>
                <ul>
                        <xsl:for-each select="child::object">
                                <li>
                                        <xsl:element name="input">
                                                <xsl:attribute name="type">checkbox</xsl:attribute>
                                                <xsl:attribute name="value">
                                                        <xsl:value-of select="@solutionsID"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="name">
                                                        <xsl:text>@Web Application@: </xsl:text>
                                                        <xsl:for-each select="ancestor::*">
                                                                <xsl:if test="@name">
                                                                        <xsl:value-of select="@name"/>
                                                                        <xsl:text>/</xsl:text>
                                                                </xsl:if>
                                                        </xsl:for-each>
                                                        <xsl:value-of select="@name"/>
                                                </xsl:attribute>
                                                <i><xsl:value-of select="@name"/></i>
                                                <xsl:call-template name="up_down_arrows"/>
                                        </xsl:element>
                                        <ul>
                                                <xsl:apply-templates select="object[@type='Servlets']" />
                                        </ul>
                                </li>
                        </xsl:for-each>
                </ul>
        </xsl:template>

        <xsl:template match="object[@type='Servlets']" >
                <xsl:for-each select="child::object">
                        <li>
                                <xsl:element name="input">
                                        <xsl:attribute name="type">checkbox</xsl:attribute>
                                        <xsl:attribute name="value">
                                                <xsl:value-of select="@solutionsID"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="name">
                                                <xsl:text>@Servlet@: </xsl:text>
                                                <xsl:value-of select="@name"/>
                                        </xsl:attribute>
                                        <i><xsl:value-of select="@name"/></i>
                                        (Servlet)
                                </xsl:element>
                        </li>
                </xsl:for-each>
        </xsl:template>

        <xsl:template name="writeAutomaticSelectionsForTree" >
                <xsl:element name="li">
                        <xsl:element name="a">
                                <xsl:attribute name="name">
                                        <xsl:value-of select="@name"/>
                                </xsl:attribute>
                                <xsl:value-of select="@name"/>
                        </xsl:element>

                        <xsl:element name="input">
                                <xsl:attribute name="type">hidden</xsl:attribute>
                                <xsl:attribute name="value">
                                        <xsl:value-of select="@solutionsID"/>
                                </xsl:attribute>
                                <xsl:attribute name="name">
                                        <xsl:text>@</xsl:text>
                                        <xsl:value-of select="@type"/>
                                        <xsl:text>@: </xsl:text>
                                        <xsl:for-each select="ancestor::*">
                                                <xsl:if test="@name">
                                                        <xsl:value-of select="@name"/>
                                                        <xsl:text>/</xsl:text>
                                                </xsl:if>
                                        </xsl:for-each>
                                        <xsl:value-of select="@name"/>

                                </xsl:attribute>
                        </xsl:element>

                        <xsl:text> (always monitored) </xsl:text>

                </xsl:element>
        </xsl:template>

        <xsl:template name="writeAutomaticSelectionsForContents" >
                <xsl:element name="li">
                        <xsl:element name="a">
                                <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@name"/>
                                </xsl:attribute>
                                <xsl:value-of select="@name"/>
                        </xsl:element>

                        <xsl:text> (always monitored) </xsl:text>
                </xsl:element>
        </xsl:template>

        <!-- This template is just outputs the up/down arrows with the appropriate links.  -->
        <xsl:template name="up_down_arrows" >
                <a href="#top" style="text-decoration: none;"><img src="/SiteView/htdocs/artwork/up.gif" border="0" title="To top of window" alt="To top of window"/></a>
                <a href="#bottom" style="text-decoration: none;"><img src="/SiteView/htdocs/artwork/down.gif" border="0" title="To bottom of window" alt="To bottom of window"/></a>
        </xsl:template>
</xsl:stylesheet>
