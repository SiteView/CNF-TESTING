 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
 package COM.dragonflow.Utils;
 
 /**
  * Comment for <code></code>
  * 
  * @author
  * @version 0.0
  * 
  * 
  */
 
 import java.io.File;
 import java.io.StringReader;
 import java.util.ArrayList;
 
 import org.xml.sax.InputSource;
 
//Referenced classes of package COM.dragonflow.Utils:
//SoapRpc, FileUtils
 
 public class WSDLParser
 {
     
     private final String XMLSCHEMA_URL = "http://www.w3.org/2001/XMLSchema";
     private final String SOAPENC_URL = "http://schemas.xmlsoap.org/soap/encoding/";
     private final String WSDLENC_URL = "http://schemas.xmlsoap.org/wsdl/";
     private final String XMLSCHEMA_INSTANCE_URL = "http://www.w3.org/2001/XMLSchema-instance";
     private final String wsdl4jImpl = "com.ibm.wsdl.factory.WSDLFactoryImpl";
     private final int ARRAY_DISPLAY_SIZE = 2;
     private String _targetNamespace;
     private String _schemaTargetNamespace;
     private String _schemaNamespaceQualifier;
     private String _complexTypeNamespaceQualifier;
     private javax.wsdl.Definition _def;
     private javax.wsdl.factory.WSDLFactory _wsdlFactory;
     private javax.wsdl.xml.WSDLReader _wsdlReader;
     private final String LOCAL_NAMESPACE_PREFIX = "fw";
     private int _localNamespaceNumber;
     
     public WSDLParser()
     {
         _targetNamespace = null;
         _schemaTargetNamespace = null;
         _schemaNamespaceQualifier = null;
         _complexTypeNamespaceQualifier = null;
         _def = null;
         _wsdlFactory = null;
         _wsdlReader = null;
         _localNamespaceNumber = 100;
     }
     
     public void readWSDL(String s, StringBuffer stringbuffer)
     {
         long l = 200L;
         StringBuffer stringbuffer1 = new StringBuffer();
         try
         {
             if(_wsdlFactory == null || _wsdlReader == null)
             {
                 _wsdlFactory = javax.wsdl.factory.WSDLFactory.newInstance("com.ibm.wsdl.factory.WSDLFactoryImpl");
                 _wsdlReader = _wsdlFactory.newWSDLReader();
             }
         }
         catch(java.lang.Exception exception)
         {
             stringbuffer.append(" INTERNAL ERROR ENCOUNTERED WHILE READING WSDL FILE  ");
             stringbuffer.append(" WSDL Parser class com.ibm.wsdl.factory.WSDLFactoryImplnot found");
             return;
         }
         _wsdlReader.setFeature("javax.wsdl.importDocuments", true);
         if(s.indexOf("http") > -1)
         {
             long l1 = COM.dragonflow.Utils.SoapRpc.getWSDLContent(s, stringbuffer1, stringbuffer);
             if(l1 != 200L && l1 != 302L && l1 != 301L)
             {
                 stringbuffer.append(" " + COM.dragonflow.StandardMonitor.URLMonitor.lookupStatus(l1));
                 return;
             }
         } else
         {
             java.io.File file = new File(s);
             if(file.exists())
             {
                 stringbuffer1.append(COM.dragonflow.Utils.WSDLParser.getWSDLSource(s, stringbuffer));
                 if(stringbuffer.length() > 0)
                 {
                     return;
                 }
             } else
             {
                 stringbuffer.append(" SPECIFIED WSDL FILE DOES NOT EXIST ");
                 stringbuffer.append(" Please check that the file was specified correctly ");
                 return;
             }
         }
         String s1 = stringbuffer1.toString();
         int i = s1.indexOf("<?xml");
         if(i >= 0)
         {
             s1 = s1.substring(i);
         }
         try
         {
             java.io.StringReader stringreader = new StringReader(s1);
             _def = _wsdlReader.readWSDL(null, new InputSource(stringreader));
         }
         catch(java.lang.Exception exception1)
         {
             exception1.printStackTrace();
             stringbuffer.append(" NOT A CORRECT WSDL FILE  ");
             stringbuffer.append(" please refer to http://msdn.microsoft.com/xml/general/wsdl.asp ");
             stringbuffer.append(" for more info. ");
             stringbuffer.append(exception1.toString());
             return;
         }
         _targetNamespace = _def.getTargetNamespace();
         _schemaTargetNamespace = getSchemaTargetNamespace();
         _schemaNamespaceQualifier = getSchemaNamespaceQualifier();
     }
     
     public java.util.List getOperationNames(StringBuffer stringbuffer)
     {
         java.util.List list = getOperations(stringbuffer);
         if(stringbuffer.length() > 0)
         {
             return null;
         }
         String as[] = new String[list.size()];
         for(int i = 0; i < list.size(); i++)
         {
             javax.wsdl.Operation operation = (javax.wsdl.Operation)list.get(i);
             as[i] = operation.getName();
         }
         
         return java.util.Arrays.asList(as);
     }
     
     /**
      * CAUTION: Decompiled by hand.
      * 
      * @param stringbuffer
      * @return
      */
     public String getServiceEndpointURL(StringBuffer stringbuffer)
     {
         String s = null;
         if(_def == null)
         {
             stringbuffer.append(" Internal Error - WSDLParser.101 - Contact Support ");
             return null;
         }
         java.util.Map map = _def.getServices();
         javax.wsdl.Service service = (javax.wsdl.Service)map.values().iterator().next();
         map = service.getPorts();
         java.util.Iterator iterator = map.values().iterator();
         
         while (iterator.hasNext())
         {
             javax.wsdl.Port port = (javax.wsdl.Port)iterator.next();
             java.util.List list = port.getExtensibilityElements();
             java.util.Iterator iterator1 = list.iterator();
             java.lang.Object obj;
             while (iterator1.hasNext())
             {
                 obj = iterator1.next();
                 if (obj instanceof javax.wsdl.extensions.soap.SOAPAddress) {
                     javax.wsdl.extensions.soap.SOAPAddress soapaddress = (javax.wsdl.extensions.soap.SOAPAddress)obj;
                     s = soapaddress.getLocationURI();
                     if (s != null) {
                         break;
                     }
                 }
             }
         }
         
         
         if(s == null)
         {
             stringbuffer.append(" Unsupported service endpoint Address format in specified WSDL. ");
             stringbuffer.append(" This web service is not supported. ");
         }
         return s;
     }
     
     public java.util.List generateArgXMLforUI(String s, StringBuffer stringbuffer)
     {
         java.util.List list = getOperations(stringbuffer);
         if(stringbuffer.length() > 0)
         {
             return null;
         }
         javax.wsdl.Operation operation = null;
         java.util.Iterator iterator = list.iterator();
         while (iterator.hasNext())
         {
             operation = (javax.wsdl.Operation)iterator.next();
             if (operation.getName().equals(s)) {
                 break;
             }
         }
         
         javax.wsdl.Input input = operation.getInput();
         javax.wsdl.Message message = input.getMessage();
         if(message.isUndefined())
         {
             stringbuffer.append(" WSDL invalid, message " + message.getQName() + " is undefined. ");
             return null;
         }
         java.util.Map map = message.getParts();
         iterator = map.values().iterator();
         String as[] = new String[map.size()];
         boolean flag = map.size() > 1;
         int i = 0;
         while(iterator.hasNext()) 
         {
             javax.wsdl.Part part = (javax.wsdl.Part)iterator.next();
             as[i++] = generateAnArgforUI(part, flag);
         }
         return java.util.Arrays.asList(as);
     }
     
     /**
      * CAUTION: Decompiled by hand.
      * 
      * @param s
      * @param stringbuffer
      * @param stringbuffer1
      * @return
      */
     public String getOperationNamespace(String s, StringBuffer stringbuffer, StringBuffer stringbuffer1)
     {
         String s1 = null;
         javax.wsdl.BindingOperation bindingoperation = null;
         String s2 = null;
         java.util.Map map = _def.getBindings();
         java.util.Iterator iterator = map.values().iterator();
         
         while (iterator.hasNext())
         {
             javax.wsdl.Binding binding = (javax.wsdl.Binding)iterator.next();
             java.util.List list = binding.getBindingOperations();
             java.util.Iterator iterator1 = list.iterator();
             javax.wsdl.BindingOperation bindingoperation1;
             while (iterator1.hasNext())
             {
                 bindingoperation1 = (javax.wsdl.BindingOperation)iterator1.next();
                 if (bindingoperation1.getName().equals(s)) {
                     s2 = getSOAPActionFromBindingNode(bindingoperation1);
                     if(s2 != null)
                     {
                         bindingoperation = bindingoperation1;
                     }
                 }
             }
             if (bindingoperation != null) {
                 break;
             }
         }
         
         
         if(bindingoperation == null)
         {
             stringbuffer1.append(" Specified WSDL invalid - no bindingOperation found for " + s);
             return null;
         }
         if(s2 == null)
         {
             stringbuffer.append(" ");
         } else
         {
             stringbuffer.append(s2);
         }
         javax.wsdl.BindingInput bindinginput = bindingoperation.getBindingInput();
         java.util.List list1 = bindinginput.getExtensibilityElements();
         javax.wsdl.extensions.soap.SOAPBody soapbody = (javax.wsdl.extensions.soap.SOAPBody)list1.iterator().next();
         s1 = soapbody.getNamespaceURI();
         if(s1 == null || s1.equals(""))
         {
             s1 = _targetNamespace;
         }
         return s1;
     }
     
     private String getSOAPActionFromBindingNode(javax.wsdl.BindingOperation bindingoperation)
     {
         java.util.List list = bindingoperation.getExtensibilityElements();
         javax.wsdl.extensions.soap.SOAPOperation soapoperation = null;
         String s = null;
         java.util.Iterator iterator = list.iterator();
         if(iterator.hasNext())
         {
             java.lang.Object obj = iterator.next();
             if(obj instanceof javax.wsdl.extensions.soap.SOAPOperation)
             {
                 soapoperation = (javax.wsdl.extensions.soap.SOAPOperation)obj;
             }
         }
         if(soapoperation != null)
         {
             s = soapoperation.getSoapActionURI();
         }
         return s;
     }
     
     private java.util.List getOperations(StringBuffer stringbuffer)
     {
         if(_def == null)
         {
             stringbuffer.append(" Internal Error - WSDLParser.100 - Contact Support ");
             return null;
         }
         java.util.Map map = _def.getPortTypes();
         java.util.Iterator iterator = map.values().iterator();
         javax.wsdl.PortType porttype = (javax.wsdl.PortType)iterator.next();
         java.util.List list = porttype.getOperations();
         if(list == null)
         {
             stringbuffer.append(" No Operations Found in WSDL File ");
         }
         return list;
     }
     
     private String generateAnArgforUI(javax.wsdl.Part part, boolean flag)
     {
         StringBuffer stringbuffer = new StringBuffer();
         String s = part.getName();
         javax.xml.namespace.QName qname = part.getTypeName();
         if(qname == null)
         {
             qname = part.getElementName();
         }
         String s1 = qname.getNamespaceURI();
         if(isXmlSchemaEnc(s1))
         {
             stringbuffer.append(s + "(" + qname.getLocalPart() + ") =");
         } else
         {
             org.w3c.dom.Node node = findComplexTypeNode(qname.getLocalPart());
             if(node != null)
             {
                 stringbuffer.append(s + "[COMPLEX] =" + complexType2XML(node, s, true, null, flag));
             }
         }
         return stringbuffer.toString();
     }
     
     private String complexType2XML(org.w3c.dom.Node node, String s, boolean flag, String s1, boolean flag1)
     {
         StringBuffer stringbuffer = new StringBuffer();
         String s2 = s1;
         String s3 = "";
         org.w3c.dom.Node node1 = getFirstNonTextChildNodeOf(node);
         if(s1 == null)
         {
             s2 = generateNamespaceName();
             s3 = getTNSdef(s2);
         }
         String s4;
         try
         {
             s4 = changeArgNameIfArrayType(s, s2);
             if(flag1)
             {
                 stringbuffer.append("<" + s4 + " " + generateUriDefs(flag) + " " + s3 + " " + getComplexTypeDef(node, s2) + "> ");
             }
             if(elementsAreEqual(node1.getNodeName(), "sequence"))
             {
                 org.w3c.dom.NodeList nodelist = node1.getChildNodes();
                 java.util.List list = removeTextNodes(nodelist);
                 for(int i = 0; i < list.size(); i++)
                 {
                     org.w3c.dom.Node node3 = (org.w3c.dom.Node)list.get(i);
                     String s6 = getAttrValueFor("name", node3);
                     String s7 = getAttrValueFor("type", node3);
                     if(isComplexType(s7))
                     {
                         stringbuffer.append(complexType2XML(findComplexTypeNode(s7), s6, false, null, true));
                     } else
                     {
                         stringbuffer.append(generatePrimitiveArgXML(s6, s7));
                     }
                 }
                 
             } else
             {
                 org.w3c.dom.Node node2 = getComplexContentAttributeNode(node1);
                 String s5 = getAttrValueFor("arrayType", node2);
                 for(int j = 0; j < 2; j++)
                 {
                     if(isComplexType(s5))
                     {
                         stringbuffer.append(complexType2XML(findComplexTypeNode(s5), s5, false, s2, true));
                     } else
                     {
                         stringbuffer.append(generatePrimitiveArgXML(s, s5));
                     }
                 }
                 
             }
         }
         catch(java.lang.Exception exception)
         {
             exception.printStackTrace();
             error(exception.toString());
             return "";
         }
         if(flag1)
         {
             stringbuffer.append("</" + s4 + ">");
         }
         return stringbuffer.toString();
     }
     
     private String isPrimitiveTypeArray(org.w3c.dom.Node node)
     {
         org.w3c.dom.Node node1 = getFirstNonTextChildNodeOf(node);
         if(elementsAreEqual(node1.getNodeName(), "sequence"))
         {
             return null;
         }
         org.w3c.dom.Node node2 = getComplexContentAttributeNode(node1);
         String s = getAttrValueFor("arrayType", node2);
         if(s == null)
         {
             return null;
         }
         int i = s.indexOf(':');
         String s1 = s.substring(0, i);
         i = s.indexOf("[]");
         String s2 = s;
         if(i > 0)
         {
             s2 = s2.substring(0, i);
         }
         String s3 = getSchemaNamespaceQualifierInTypes();
         if(s3 != null && s3.equals(s1))
         {
             return s2;
         }
         s3 = getSchemaNamespaceQualifier();
         if(s3 != null && s3.equals(s1))
         {
             return s2;
         } else
         {
             return null;
         }
     }
     
     private String generateNamespaceName()
     {
         return "fw" + _localNamespaceNumber++;
     }
     
     private String generateUriDefs(boolean flag)
     {
         if(!flag)
         {
             return "";
         } else
         {
             String s = "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ";
             s = s + "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
             s = s + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
             return s;
         }
     }
     
     private String generatePrimitiveArgXML(String s, String s1)
     {
         String s2 = s;
         int i = s1.indexOf(':');
         String s3 = s1;
         if(i < 0)
         {
             s3 = "xsd:" + s1;
         }
         i = s3.indexOf("[]");
         if(i > 0)
         {
             s3 = s3.substring(0, i);
         }
         if(s != null)
         {
             s2 = s.trim();
         }
         return "<" + s2 + " xsi:type=\"" + s3 + "\">***</" + s2 + ">";
     }
     
     private boolean isSoapEnc(String s)
     {
         return s.equals("http://schemas.xmlsoap.org/soap/encoding/");
     }
     
     private boolean isXmlSchemaEnc(String s)
     {
         return namespacesEqualIgnoreYear("http://www.w3.org/2001/XMLSchema", s);
     }
     
     private boolean isWsdlSoapEnc(String s)
     {
         return s.equals("http://schemas.xmlsoap.org/wsdl/");
     }
     
     private boolean isSchemaInstanceEnc(String s)
     {
         return namespacesEqualIgnoreYear("http://www.w3.org/2001/XMLSchema-instance", s);
     }
     
     private boolean namespacesEqualIgnoreYear(String s, String s1)
     {
         if(s.equalsIgnoreCase(s1))
         {
             return true;
         }
         int i = s.indexOf("200");
         if(i < 0 && (i = s.indexOf("199")) < 0)
         {
             return false;
         }
         String s2 = s.substring(0, i);
         String s3 = s.substring(i + 4);
         if(s1.equals(s2 + "1999" + s3))
         {
             return true;
         }
         if(s1.equals(s2 + "2000" + s3))
         {
             return true;
         }
         return s1.equals(s2 + "2002" + s3);
     }
     
     private boolean isSchemaNamespace(String s)
     {
         String s1 = getSchemaTargetNamespace();
         if(s1 == null)
         {
             return false;
         }
         return s.equals(s1);
     }
     
     private boolean isComplexType(String s)
     {
         if(_targetNamespace == null)
         {
             return false;
         }
         int i = s.indexOf(':');
         if(i <= 0)
         {
             return false;
         }
         String s1 = s.substring(0, i);
         String s2 = _def.getNamespace(s1);
         if(s2 != null && s2.equals(_targetNamespace))
         {
             return true;
         }
         if(s2 != null)
         {
             String s3 = getSchemaTargetNamespace();
             if(s3 != null && s3.equals(s2))
             {
                 return true;
             }
         }
         s2 = getComplexTypeNamespaceQualifier();
         return s2 != null && s1.equals(s2);
     }
     
     private boolean isArrayType(org.w3c.dom.Node node)
     {
         org.w3c.dom.Node node1 = getFirstNonTextChildNodeOf(node);
         return elementsAreEqual(node1.getNodeName(), "complexContent");
     }
     
     private String getTNSdef(String s)
     {
         if(s == null)
         {
             s = "m";
         }
         return "xmlns:" + s + "=\"" + getSchemaTargetNamespace() + "\"";
     }
     
     private String getComplexTypeDef(org.w3c.dom.Node node, String s)
     {
         String s1 = null;
         Object obj = null;
         org.w3c.dom.NamedNodeMap namednodemap = node.getAttributes();
         org.w3c.dom.Node node1 = namednodemap.getNamedItem("name");
         if(isArrayType(node))
         {
             int i = node1.getNodeValue().indexOf("ArrayOf");
             String s2;
             if((s2 = isPrimitiveTypeArray(node)) != null)
             {
                 s1 = "soapenc:arrayType=\"" + s2 + "[" + 2 + "]\"";
             } else
             {
                 String s3 = node1.getNodeValue().substring(i + 7);
                 s1 = "soapenc:arrayType=\"" + s + ":" + s3 + "[" + 2 + "]\"";
             }
         } else
         {
             s1 = "xsi:type=\"" + s + ":" + node1.getNodeValue() + "\"";
         }
         return s1;
     }
     
     private String getSoapEncDef()
     {
         return "env:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"";
     }
     
     private String getSchemaTargetNamespace()
     {
         if(_schemaTargetNamespace != null)
         {
             return _schemaTargetNamespace;
         }
         javax.wsdl.Types types = _def.getTypes();
         if(types == null)
         {
             return null;
         } else
         {
             java.util.List list = types.getExtensibilityElements();
             javax.wsdl.extensions.UnknownExtensibilityElement unknownextensibilityelement = (javax.wsdl.extensions.UnknownExtensibilityElement)list.get(0);
             org.w3c.dom.Element element = unknownextensibilityelement.getElement();
             String s = element.getAttribute("targetNamespace");
             return s;
         }
     }
     
     private String getSchemaNamespaceQualifier()
     {
         String s = null;
         if(_schemaNamespaceQualifier != null)
         {
             return _schemaNamespaceQualifier;
         }
         s = getNamespaceQualifierFor("http://www.w3.org/2001/XMLSchema");
         if(s != null)
         {
             return s;
         } else
         {
             String s1 = getSchemaNamespaceQualifierInTypes();
             return s1;
         }
     }
     
     private String getSchemaNamespaceQualifierInTypes()
     {
         String s = null;
         javax.wsdl.Types types = _def.getTypes();
         if(types == null)
         {
             return null;
         }
         java.util.List list = types.getExtensibilityElements();
         javax.wsdl.extensions.UnknownExtensibilityElement unknownextensibilityelement = (javax.wsdl.extensions.UnknownExtensibilityElement)list.get(0);
         org.w3c.dom.Element element = unknownextensibilityelement.getElement();
         org.w3c.dom.NamedNodeMap namednodemap = element.getAttributes();
         int i = 0;
         do
         {
             if(i >= namednodemap.getLength())
             {
                 break;
             }
             org.w3c.dom.Node node = namednodemap.item(i);
             if(isXmlSchemaEnc(node.getNodeValue()))
             {
                 s = node.getLocalName();
                 break;
             }
             i++;
         } while(true);
         return s;
     }
     
     private String getComplexTypeNamespaceQualifier()
     {
         if(_complexTypeNamespaceQualifier != null)
         {
             return _complexTypeNamespaceQualifier;
         }
         javax.wsdl.Types types = _def.getTypes();
         if(types == null)
         {
             return null;
         }
         java.util.List list = types.getExtensibilityElements();
         javax.wsdl.extensions.UnknownExtensibilityElement unknownextensibilityelement = (javax.wsdl.extensions.UnknownExtensibilityElement)list.get(0);
         org.w3c.dom.Element element = unknownextensibilityelement.getElement();
         org.w3c.dom.NamedNodeMap namednodemap = element.getAttributes();
         for(int i = 0; i < namednodemap.getLength(); i++)
         {
             org.w3c.dom.Node node = namednodemap.item(i);
             String s = node.getNodeValue();
             String s1 = node.getPrefix();
             if(s1 == null || s1.equals(""))
             {
                 continue;
             }
             String s2 = node.getLocalName();
             String s3 = getNamespaceQualifierFor("http://schemas.xmlsoap.org/wsdl/");
             if(s3 != null && s3.equals(s1))
             {
                 return s2;
             }
             if(s.equals(getSchemaTargetNamespace()))
             {
                 return s2;
             }
             if(!isXmlSchemaEnc(s) && !isSoapEnc(s) && !isSchemaNamespace(s) && !isWsdlSoapEnc(s) && !isSchemaInstanceEnc(s))
             {
                 return s2;
             }
         }
         
         return null;
     }
     
     private String getNamespaceQualifierFor(String s)
     {
         java.util.Map map = _def.getNamespaces();
         for(java.util.Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
         {
             String s1 = (String)iterator.next();
             String s2 = (String)map.get(s1);
             if(namespacesEqualIgnoreYear(s2, s))
             {
                 return s1;
             }
         }
         
         return null;
     }
     
     private java.util.List removeTextNodes(org.w3c.dom.NodeList nodelist)
     {
         java.util.ArrayList arraylist = new ArrayList();
         for(int i = 0; i < nodelist.getLength(); i++)
         {
             org.w3c.dom.Node node = nodelist.item(i);
             if(node.getNodeType() != 3)
             {
                 arraylist.add(node);
             }
         }
         
         return java.util.Arrays.asList(arraylist.toArray());
     }
     
     private org.w3c.dom.Node findComplexTypeNode(String s)
     {
         javax.wsdl.Types types = _def.getTypes();
         java.util.List list = types.getExtensibilityElements();
         org.w3c.dom.Node node = null;
         String s1 = s;
         int i = s.indexOf('[');
         if(i > 0)
         {
             s1 = s.substring(0, i);
         }
         int j = 0;
         do
         {
             if(j >= list.size())
             {
                 break;
             }
             javax.wsdl.extensions.UnknownExtensibilityElement unknownextensibilityelement = (javax.wsdl.extensions.UnknownExtensibilityElement)list.get(j);
             org.w3c.dom.Element element = unknownextensibilityelement.getElement();
             org.w3c.dom.NodeList nodelist = smartGetElementsByTagName(element, "complexType");
             int k = 0;
             do
             {
                 if(k >= nodelist.getLength())
                 {
                     break;
                 }
                 org.w3c.dom.Node node1 = nodelist.item(k);
                 org.w3c.dom.Node node2 = node1.getAttributes().getNamedItem("name");
                 if(node2 == null)
                 {
                     convertFromFormat2(node1);
                     node2 = node1.getAttributes().getNamedItem("name");
                 }
                 if(elementsAreEqual(s1, node2.getNodeValue()))
                 {
                     node = node1;
                     break;
                 }
                 k++;
             } while(true);
             if(node != null)
             {
                 break;
             }
             j++;
         } while(true);
         if(node == null)
         {
             error("Could not find complexType: " + s1);
         }
         return node;
     }
     
     private void convertFromFormat2(org.w3c.dom.Node node)
     {
         org.w3c.dom.Node node1 = node.getParentNode();
         if(node1 == null)
         {
             return;
         }
         org.w3c.dom.NamedNodeMap namednodemap = node1.getAttributes();
         org.w3c.dom.Node node2 = namednodemap.getNamedItem("name");
         if(node2 != null)
         {
             org.w3c.dom.Element element = (org.w3c.dom.Element)node;
             element.setAttribute("name", node2.getNodeValue());
         }
     }
     
     private org.w3c.dom.NodeList smartGetElementsByTagName(org.w3c.dom.Element element, String s)
     {
         org.w3c.dom.NodeList nodelist = element.getElementsByTagName(s);
         if(nodelist.getLength() > 0)
         {
             return nodelist;
         }
         String s1 = element.getNodeName();
         int i = s1.indexOf(':');
         if(s.indexOf(':') < 0 && i > 0)
         {
             String s2 = s1.substring(0, i);
             org.w3c.dom.NodeList nodelist1 = element.getElementsByTagNameNS(s2, s);
             if(nodelist1.getLength() > 0)
             {
                 return nodelist1;
             }
             nodelist1 = element.getElementsByTagName(s2 + ":" + s);
             if(nodelist1.getLength() > 0)
             {
                 return nodelist1;
             }
         }
         return null;
     }
     
     private org.w3c.dom.Node getComplexContentAttributeNode(org.w3c.dom.Node node)
     {
         org.w3c.dom.Node node1 = getFirstNonTextChildNodeOf(node);
         if(node1 == null)
         {
             java.lang.System.out.println("Could not find <restriction> node under <complexContent>");
             return null;
         } else
         {
             node1 = getFirstNonTextChildNodeOf(node1);
             if(node1 == null);
             return node1;
         }
     }
     
     private org.w3c.dom.Node getFirstNonTextChildNodeOf(org.w3c.dom.Node node)
     {
         org.w3c.dom.Node node1 = null;
         org.w3c.dom.NodeList nodelist = node.getChildNodes();
         int i = 0;
         do
         {
             if(i >= nodelist.getLength())
             {
                 break;
             }
             node1 = nodelist.item(i);
             if(node1.getNodeType() != 3)
             {
                 break;
             }
             i++;
         } while(true);
         return node1;
     }
     
     private String getAttrValueFor(String s, org.w3c.dom.Node node)
     {
         String s1 = null;
         int i = s.indexOf(':');
         org.w3c.dom.NamedNodeMap namednodemap = node.getAttributes();
         int j = 0;
         do
         {
             if(j >= namednodemap.getLength())
             {
                 break;
             }
             org.w3c.dom.Node node1 = namednodemap.item(j);
             if(elementsAreEqual(s, node1.getNodeName()))
             {
                 s1 = node1.getNodeValue();
                 break;
             }
             j++;
         } while(true);
         return s1;
     }
     
     private boolean elementsAreEqual(String s, String s1)
     {
         String s2 = s;
         String s3 = s1;
         int i = s.indexOf(':');
         int j = s1.indexOf(':');
         if((i < 0) & (j >= 0))
         {
             s3 = s1.substring(j + 1);
         } else
             if((j < 0) & (i >= 0))
             {
                 s2 = s.substring(i + 1);
             }
         return s2.equals(s3);
     }
     
     private String changeArgNameIfArrayType(String s, String s1)
     {
         int i = s.indexOf('[');
         if(i < 0)
         {
             return s;
         }
         int j = s.indexOf(':');
         if(j < 0)
         {
             j = 0;
         } else
         {
             j++;
         }
         return s1 + ":" + s.substring(j, i);
     }
     
     private static String getWSDLSource(String s, StringBuffer stringbuffer)
     {
         String s1 = "";
         try
         {
             s1 = COM.dragonflow.Utils.FileUtils.readFile(s).toString();
         }
         catch(java.lang.Exception exception)
         {
             exception.printStackTrace();
             stringbuffer.append(" " + exception.toString());
         }
         return s1;
     }
     
     private void error(String s)
     {
         COM.dragonflow.Log.LogManager.log("Error", s);
     }
     
     static void main(String args[])
     {
         COM.dragonflow.Utils.WSDLParser wsdlparser = new WSDLParser();
         String s = null;
         String s1 = null;
         int i = args.length;
         if(i > 0)
         {
             s = args[0];
         }
         if(i > 1)
         {
             s1 = args[1];
         }
         wsdlparser.runTest(s, s1);
     }
     
     public void runTest(String s, String s1)
     {
         String s2 = "http://localhost:7001/web-services/UserProfile?WSDL";
         String s3 = "d:\\projects\\andy\\jokesite.wsdl";
         String s4 = s3;
         if(s != null)
         {
             s4 = s;
         }
         try
         {
             javax.wsdl.factory.WSDLFactory wsdlfactory = javax.wsdl.factory.WSDLFactory.newInstance("com.ibm.wsdl.factory.WSDLFactoryImpl");
             javax.wsdl.xml.WSDLReader wsdlreader = wsdlfactory.newWSDLReader();
             _def = wsdlreader.readWSDL(s4, "");
         }
         catch(java.lang.Exception exception)
         {
             exception.printStackTrace();
         }
         _targetNamespace = _def.getTargetNamespace();
         _schemaTargetNamespace = getSchemaTargetNamespace();
         java.util.Map map = _def.getPortTypes();
         java.util.Iterator iterator = map.values().iterator();
         java.util.List list = null;
         javax.wsdl.PortType porttype = (javax.wsdl.PortType)iterator.next();
         javax.xml.namespace.QName qname = porttype.getQName();
         java.lang.System.out.println("PortType: " + qname.toString());
         list = porttype.getOperations();
         String s5 = "getJoke";
         String s6 = null;
         if(s1 != null)
         {
             s5 = s1;
         }
         iterator = list.iterator();
         while (iterator.hasNext()) {
             javax.wsdl.Operation operation = (javax.wsdl.Operation)iterator.next();
             if(operation.getName().equals(s5))
             {
             java.lang.System.out.println("<m:" + s5 + " " + getTNSdef(null) + " " + getSoapEncDef() + " >");
             s6 = test_generateArgXML(operation.getInput());
             java.lang.System.out.println("</m:" + s5 + ">");
             break;
             }
         } 
         if(s6 != null)
         {
             java.lang.System.out.println("XML Request = " + s6);
         }
     }
     
     private String test_generateArgXML(javax.wsdl.Input input)
     {
         String s = null;
         javax.wsdl.Message message = input.getMessage();
         if(message.isUndefined())
         {
             java.lang.System.out.println("Message " + message.getQName() + " is undefined.");
             return s;
         }
         java.util.Map map = message.getParts();
         javax.wsdl.Part part;
         for(java.util.Iterator iterator = map.values().iterator(); iterator.hasNext(); test_generateAnArg(part))
         {
             part = (javax.wsdl.Part)iterator.next();
         }
         
         return s;
     }
     
     private void test_generateAnArg(javax.wsdl.Part part)
     {
         String s = part.getName();
         javax.xml.namespace.QName qname = part.getTypeName();
         if(qname == null)
         {
             qname = part.getElementName();
         }
         String s1 = qname.getNamespaceURI();
         if(isXmlSchemaEnc(s1))
         {
             java.lang.System.out.println(generatePrimitiveArgXML(s, qname.getLocalPart()));
             return;
         }
         org.w3c.dom.Node node = findComplexTypeNode(qname.getLocalPart());
         if(node != null)
         {
             java.lang.System.out.println(complexType2XML(node, s, true, null, true));
         }
     }
 }
