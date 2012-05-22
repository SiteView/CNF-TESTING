package COM.dragonflow.itsm.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Xml.XmlElement;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;

public class DataManager {
	/**
	 * Get BusinessObject data from itsm database. 
	 * @param businessObjectName
	 * @return List<Map<String, String>>
	 */
	public static List<Map<String, String>> getBusinessObjectData(
			String businessObjectName) {
		ISiteviewApi siteviewApi = ConnectionBroker.get_SiteviewApi();
		BusinessObject bo;
		SiteviewQuery siteviewquery_interfaceTable = new SiteviewQuery();
		siteviewquery_interfaceTable.AddBusObQuery(businessObjectName,
				QueryInfoToGet.All);
//		 XmlElement xmlElementscanconfigid = siteviewquery_interfaceTable
//		 .get_CriteriaBuilder().FieldAndValueExpression("RecId",
//		 Operators.Equals, "00E129128B7749C9840D5ECDF8E4E694");
//		 siteviewquery_interfaceTable
//		 .set_BusObSearchCriteria(xmlElementscanconfigid);
		ICollection interfaceTableCollection = siteviewApi.get_BusObService()
				.get_SimpleQueryResolver()
				.ResolveQueryToBusObList(siteviewquery_interfaceTable);
		IEnumerator interfaceTableIEnum = interfaceTableCollection
				.GetEnumerator();
		List<Map<String, String>> dataMapList = new ArrayList<Map<String, String>>();
		Map<String, String> dataMap = new HashMap();
		while (interfaceTableIEnum.MoveNext()) {
			bo = (BusinessObject) interfaceTableIEnum.get_Current();
			String eccType = bo.GetField("EccType").get_NativeValue()
					.toString();
			dataMap.put("EccType", eccType);
			String recId = bo.GetField("RecId").get_NativeValue().toString();
			dataMap.put("RecId", recId);
			String lastModDateTime = bo.GetField("LastModDateTime")
					.get_NativeValue().toString();
			dataMap.put("LastModDateTime", lastModDateTime);
			String frequency = bo.GetField("frequency").get_NativeValue()
					.toString();
			dataMap.put("Frequency", frequency);
			dataMapList.add(dataMap);
		}
		return dataMapList;
	}
}
