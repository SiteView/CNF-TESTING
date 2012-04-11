<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <browse_data>
      <xsl:apply-templates />
    </browse_data>
  </xsl:template>

  <xsl:template match="o[o]">
    <object>
      <xsl:attribute name="name">
        <xsl:choose>
          <xsl:when test="@c='g'">
            <xsl:value-of select="concat('group: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='c'">
            <xsl:value-of select="concat('class: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='M'">
            <xsl:value-of select="concat('Method: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='S'">
            <xsl:value-of select="concat('SQL: ', @n)" />
          </xsl:when>
          <xsl:otherwise >
            <xsl:value-of select="concat(@c, ': ', @n)" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <xsl:if test="@d!=&quot;&quot;">
        <xsl:attribute name="desc">
          <xsl:value-of select="@d" />
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@t=1">
        <xsl:attribute name="bold">true</xsl:attribute>
      </xsl:if>

      <xsl:apply-templates />
    </object>
  </xsl:template>

  <xsl:template match="o[c]">
    <counter>
      <xsl:attribute name="name">
        <xsl:choose>
          <xsl:when test="@c='g'">
            <xsl:value-of select="concat('group: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='c'">
            <xsl:value-of select="concat('class: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='M'">
            <xsl:value-of select="concat('Method: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='S'">
            <xsl:value-of select="concat('SQL: ', @n)" />
          </xsl:when>
          <xsl:otherwise >
            <xsl:value-of select="concat(@c, ': ', @n)" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <xsl:if test="@d!=&quot;&quot;">
        <xsl:attribute name="desc">
          <xsl:value-of select="@d" />
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@t=1">
        <xsl:attribute name="bold">true</xsl:attribute>
      </xsl:if>

      <xsl:apply-templates />
    </counter>
  </xsl:template>

  <xsl:template match="o[o[@c=&quot;S&quot;]]">
    <object>
      <xsl:attribute name="name">
        <xsl:choose>
          <xsl:when test="@c='g'">
            <xsl:value-of select="concat('group: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='c'">
            <xsl:value-of select="concat('class: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='M'">
            <xsl:value-of select="concat('Method: ', @n)" />
          </xsl:when>
          <xsl:when test="@c='S'">
            <xsl:value-of select="concat('SQL: ', @n)" />
          </xsl:when>
          <xsl:otherwise >
            <xsl:value-of select="concat(@c, ': ', @n)" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <xsl:if test="@d!=&quot;&quot;">
        <xsl:attribute name="desc">
          <xsl:value-of select="@d" />
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@t=1">
        <xsl:attribute name="bold">true</xsl:attribute>
      </xsl:if>

      <xsl:apply-templates />
    </object>
  </xsl:template>

</xsl:stylesheet>
