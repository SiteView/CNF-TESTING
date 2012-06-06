package SiteView.ecc.tools;

/**
 * <p>
 * int��������࣬����ȡ�����ֵ����Сֵ��ƽ��ֵ
 * </p>
 * 
 * @author zhongping.wang
 * 
 */
public class ArrayTool {
	public ArrayTool() {
	}

	/**
	 * ��ȡ���������ֵ
	 */
	public static int getMax(int[] arr) {
		int max = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > arr[max]) {
				max = i;
			}
		}
		return arr[max];
	}

	/**
	 * ��ȡ��������Сֵ
	 */
	public static int getMin(int[] arr) {
		int min = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < arr[min]) {
				min = i;
			}
		}
		return arr[min];
	}

	/**
	 * ��ȡ����ƽ��ֵ
	 */
	public static int getAvg(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}

}
