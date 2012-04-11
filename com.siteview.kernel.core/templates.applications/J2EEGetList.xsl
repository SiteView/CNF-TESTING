<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <browse_data>
      <xsl:apply-templates />
    </browse_data>
  </xsl:template>

  <xsl:template match="object[object]">
    <object>
      <xsl:attribute name="name">
        <xsl:value-of select="concat(@class, ': ', @name)" />
      </xsl:attribute>

      <xsl:if test="@desc!=&quot;&quot;">
        <xsl:attribute name="desc">
          <xsl:value-of select="@desc" />
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@touched=1">
        <xsl:attribute name="bold">true</xsl:attribute>
      </xsl:if>

      <xsl:apply-templates />
    </object>
  </xsl:template>

  <xsl:template match="object[@name=&quot;JDBC&quot;]/object/object[@class=&quot;Method&quot;]">
    <object>
      <xsl:attribute name="name">
        <xsl:value-of select="concat(@class, ': ', @name)" />
      </xsl:attribute>

      <xsl:if test="@desc!=&quot;&quot;">
        <xsl:attribute name="desc">
          <xsl:value-of select="@desc" />
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@touched=1">
        <xsl:attribute name="bold">true</xsl:attribute>
      </xsl:if>

      <xsl:apply-templates />
    </object>
  </xsl:template>

  <xsl:template match="object[@name!=&quot;JDBC&quot;]/object/object[counter]">
    <counter>
      <xsl:attribute name="name">
        <xsl:value-of select="concat(@class, ': ', @name)" />
      </xsl:attribute>

      <xsl:if test="@desc!=&quot;&quot;">
        <xsl:attribute name="desc">
          <xsl:value-of select="@desc" />
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@touched=1">
        <xsl:attribute name="bold">true</xsl:attribute>
      </xsl:if>

      <xsl:apply-templates />
    </counter>
  </xsl:template>

</xsl:stylesheet>
