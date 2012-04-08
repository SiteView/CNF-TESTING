<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
        <!--
        This XSL takes the browse_data XML returned from the getBrowseData() method of the WebLogic6xMonitor and turns it into a tree
        of checkboxes.  This tree is intended to be inserted into a WebLogic Solution page in SiteView.  See the SiteView classes
        appServerSolutionTemplatePage, monitorSetPage, and monitorSetTemplate for more info on the format of the name= and value= 
        attributes of the checkboxes.  
        I have tried to refactor this file before the initial check-in so that all re-usable code is contained in named templates, but
        I think there is probably some more that could be done.
        -->
        <xsl:template match="browse_data">
                <a name="top"/>
                <h3> Per-Server Statistics (always monitored)</h3>
                <xsl:element name="ul">
                        <xsl:for-each select="object[@type='ServerRuntime']" >
                                <xsl:call-template name="writeAutomaticSelections"/>
                        </xsl:for-each>
                        <xsl:for-each select="object[@type='LogBroadcasterRuntime']" >
                                <xsl:call-template name="writeAutomaticSelections"/>
                        </xsl:for-each>
                        <xsl:for-each select="descendant::object[@type='JTARuntime']" > 
                                <xsl:call-template name="writeAutomaticSelections"/>
                        </xsl:for-each>
                        <xsl:for-each select="descendant::object[@type='JVMRuntime']" >
                                <xsl:call-template name="writeAutomaticSelections"/>
                        </xsl:for-each>
                        <xsl:for-each select="descendant::object[@type='ClusterRuntime']" >
                                <xsl:call-template name="writeAutomaticSelections"/>
                        </xsl:for-each>
                </xsl:element>
                <h3> JDBC Pools </h3>
                <xsl:element name="ul">
                        <xsl:for-each select="descendant::object[@type='JDBCConnectionPoolRuntime']" > 
                                <xsl:call-template name="writeCheckboxItem">
                                        <xsl:with-param name="labelPrefix" select="'JDBCConnectionPoolRuntime'"/> 
                                        <xsl:with-param name="displaySuffix" select="' '"/> 
                                </xsl:call-template>
                        </xsl:for-each>
                </xsl:element>
                <h3> Thread Pools </h3>
                <xsl:element name="ul">
                        <xsl:for-each select="descendant::object[@type='ExecuteQueueRuntime']" > 
                                <xsl:call-template name="writeCheckboxItem">
                                        <xsl:with-param name="labelPrefix" select="'ExecuteQueueRuntime'"/> 
                                        <xsl:with-param name="displaySuffix" select="' '"/> 
                                </xsl:call-template>
                        </xsl:for-each>
                </xsl:element>
                <h3> EJBs, Web Apps, and Servlets (organized by Application) </h3>
                        <xsl:choose >
                                <xsl:when test="descendant::object[@type='ApplicationRuntime']" >
                                        <h4> Jump to an Application</h4>
                                        <xsl:element name="ul">
                                                <xsl:for-each select="descendant::object[@type='ApplicationRuntime']" >
                                                        <xsl:element name="li">
                                                                <xsl:element name="a">
                                                                        <xsl:attribute name="href"> 
                                                                                <xsl:text>#</xsl:text>
                                                                                <xsl:value-of select="@plainName"/>
                                                                        </xsl:attribute>
                                                                        <xsl:value-of select="@plainName"/>
                                                                </xsl:element>
                                                        </xsl:element>
                                                </xsl:for-each>
                                        </xsl:element>
                                        <xsl:element name="ul">
                                                <xsl:apply-templates select="object" mode="tree"/>
                                        </xsl:element>
                                </xsl:when>
                                <xsl:otherwise>
                                        <xsl:element name="i">
                                                <xsl:text>There do not seem to be any applications deployed on the WebLogic Server. </xsl:text> 
                                        </xsl:element>

                                </xsl:otherwise>
                        </xsl:choose>
                <a name="bottom"/>
        </xsl:template>

        <xsl:template match="object[@type='ApplicationRuntime']" mode="tree">
                <xsl:element name="li">
                        <xsl:element name="a">
                                <xsl:attribute name="name">
                                        <xsl:value-of select="@plainName"/>
                                </xsl:attribute>
                        </xsl:element>
                        <b>
                                <xsl:value-of select="@plainName"/>
                                <a href="#top" style="text-decoration: none;">
                                        <img src="/SiteView/htdocs/artwork/up.gif" border="0" title="To top of window" alt="To top of window"/>
                                </a>
                                <a href="#bottom" style="text-decoration: none;">
                                        <img src="/SiteView/htdocs/artwork/down.gif" border="0" title="To bottom of window" alt="To bottom of window"/>
                                </a>
                        </b>
                </xsl:element>
                <xsl:element name="ul">
                        <xsl:for-each select="child::object[@type='EJBComponentRuntime']">
                            <xsl:element name="i"> <xsl:value-of select="@plainName"/>  </xsl:element> <xsl:text> EJB Component</xsl:text>

                                <xsl:call-template name="writeSubEJBItems" >
                                        <xsl:with-param name="items" select="child::object[@type='EJBTransactionRuntime']" />
                                        <xsl:with-param name="title" select="'Per-EJB Transaction Statistics'" />
                                        <xsl:with-param name="labelPrefix" select="'EJBTransactionRuntime'" />
                                        <xsl:with-param name="displaySuffix" select="' '" />
                                </xsl:call-template>

                                <xsl:call-template name="writeSubEJBItems" >
                                        <xsl:with-param name="items" select="child::object[@type='EJBPoolRuntime']" />
                                        <xsl:with-param name="title" select="'Per-EJB Pool Statistics'" />
                                        <xsl:with-param name="labelPrefix" select="'EJBPoolRuntime'" />
                                        <xsl:with-param name="displaySuffix" select="' '" />
                                </xsl:call-template>


                                <xsl:call-template name="writeSubEJBItems" >
                                        <xsl:with-param name="items" select="child::object[@type='EJBCacheRuntime']" />
                                        <xsl:with-param name="title" select="'Per-EJB Cache Statistics'" />
                                        <xsl:with-param name="labelPrefix" select="'EJBCacheRuntime'" />
                                        <xsl:with-param name="displaySuffix" select="' '" />
                                </xsl:call-template>

                        </xsl:for-each>

                        <xsl:apply-templates select="child::object[@type='WebAppComponentRuntime']"/>
                </xsl:element>
        </xsl:template>

        <xsl:template match="child::object[@type='WebAppComponentRuntime']" >
                <xsl:element name="li">
                        <xsl:element name="input">
                                <xsl:attribute name="type">checkbox</xsl:attribute>
                                <xsl:attribute name="value">
                                        <xsl:value-of select="@id"/>
                                </xsl:attribute>
                                <xsl:attribute name="name">
                                        <xsl:text>@</xsl:text>
                                        <xsl:value-of select="@type"/>
                                        <xsl:text>@</xsl:text>
                                        <xsl:text>: </xsl:text>
                                        <xsl:value-of select="@plainName"/>
                                </xsl:attribute>
                                <i><xsl:value-of select="@plainName"/></i>
                                <xsl:text> Web Application</xsl:text>
                        </xsl:element>
                        <xsl:element name="ul">
                                <xsl:for-each select="child::object[@type='ServletRuntime']">
                                        <xsl:call-template name="writeCheckboxItem" >
                                                <xsl:with-param name="labelPrefix" select="'Servlet'"/>
                                                <xsl:with-param name="displaySuffix" select="' Servlet '"/>
                                        </xsl:call-template>
                                </xsl:for-each>
                        </xsl:element>
                </xsl:element>
        </xsl:template>

        <!--
        This template is used to output most of the selectable objects in the tree. It uses the "id=" as the value for
        the checkbox.  The name is constructed to be in the form expected by monitorSetTemplate class in SiteView.  That is,
        the FOREACH variable name is surrounded by '@' symbols and followed by a ':'.  The name of the object is then printed. 
        In this case, the object name is generated by looping over the ancestor objects and concatenating their names together, 
        separated by a '/'.
        There are also two paremeters, 'displaySuffix' and 'labelPrefix'.  'labelPrefix' will interpreted by SiteView as the 
        FOREACH variable name.  'displaySuffix' is optional: it contains text that should be displayed next to the checkbox.
        -->
        <xsl:template name="writeCheckboxItem" > 
                <xsl:param name="labelPrefix"/>
                <xsl:param name="displaySuffix"/>
                <xsl:element name="li">
                        <xsl:element name="input">
                                <xsl:attribute name="type">checkbox</xsl:attribute>
                                <xsl:attribute name="value">
                                        <xsl:value-of select="@id"/>
                                </xsl:attribute>
                                <xsl:attribute name="name">

                                        <xsl:text>@</xsl:text>
                                        <xsl:value-of select="$labelPrefix" />
                                        <xsl:text>@</xsl:text>
                                        <xsl:text>: </xsl:text>

                                        <!-- 
                                            Unfortunately, for now, we have to use the same tedious browseNames as in the regular WL monitor. 
                                            This is a bummer for readability/usability of the created monitors, but changing it will require
                                            a few code changes in the WL Monitor, since that monitor actually uses the browseNames (in addition to
                                            browseNameIds) to locate counters.
                                        -->

                                        <xsl:for-each select="ancestor::*">
                                                <xsl:if test="@name">
                                                        <xsl:value-of select="@name"/>
                                                        <xsl:text>/</xsl:text>
                                                </xsl:if>
                                        </xsl:for-each>
                                        <xsl:value-of select="@name" />

                                </xsl:attribute>

                                <i><xsl:value-of select="@plainName"/></i>
                                <xsl:value-of select="$displaySuffix"/>

                        </xsl:element>
                </xsl:element>
        </xsl:template>

        <!-- 
        This template is used to generated the hidden input elements for objects that will always be automatically
        selected for monitoring.  For example, ServerRuntime should always be monitored, so, rather than presenting 
        the user with a checkbox, we just write out the "ServerRuntime: ..." and put a "<INPUT TYPE=HIDDEN ...>" next to it.
        -->
        <xsl:template name="writeAutomaticSelections" >
                <xsl:element name="li">
                        <i>
                                <xsl:value-of select="@plainName"/>
                        </i>
                        <xsl:element name="input">
                                <xsl:attribute name="type">hidden</xsl:attribute>
                                <xsl:attribute name="value">
                                        <xsl:value-of select="@id"/>
                                </xsl:attribute>
                                <xsl:attribute name="name">
                                        <xsl:text>@</xsl:text>
                                        <xsl:value-of select="@type"/>
                                        <xsl:text>@</xsl:text>
                                        <xsl:text>: </xsl:text>
                                        <xsl:value-of select="@plainName"/>
                                </xsl:attribute>
                        </xsl:element>

                        <xsl:text> (</xsl:text>
                        <xsl:value-of select="@type"/>
                        <xsl:text> Statistics) </xsl:text>

                </xsl:element>
        </xsl:template>

        <!-- This template tries to encapsulate the code for generating any lists and checkboxes that should go underneath
        an EJBComponentRuntime checkbox.  It basically indents, adds a bold title, indents again, and then adds some 
        a checkbox for each item in the nodeset that is passed in (in the "items" parameter).
        -->
        <xsl:template name="writeSubEJBItems" >
                <xsl:param name="items"/>
                <xsl:param name="title"/>
                <xsl:param name="labelPrefix"/>
                <xsl:param name="displaySuffix"/>
                <xsl:if test="$items">
                        <xsl:element name="ul">
                                <xsl:value-of select="$title" />
                                <xsl:element name="ul">
                                <xsl:for-each select="$items">
                                        <xsl:call-template name="writeCheckboxItem" >
                                                <xsl:with-param name="labelPrefix" select="$labelPrefix"/>
                                                <xsl:with-param name="displaySuffix" select="$displaySuffix"/>
                                        </xsl:call-template>
                                </xsl:for-each>
                                </xsl:element>
                        </xsl:element>
                </xsl:if>

        </xsl:template>

</xsl:stylesheet>
