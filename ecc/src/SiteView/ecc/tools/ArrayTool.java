package SiteView.ecc.tools;

/**
 * <p>
 * int数组操作类，用于取出最大值、最小值、平均值
 * </p>
 * 
 * @author zhongping.wang
 * 
 */
public class ArrayTool {
	public ArrayTool() {
	}

	/**
	 * 获取数组中最大值
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
	 * 获取数组中最小值
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
	 * 获取数组平均值
	 */
	public static int getAvg(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}

}
