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
	 * 获取int数组中最大值
	 */
	public static int getIntArrayMax(int[] arr) {
		int max = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > arr[max]) {
				max = i;
			}
		}
		return arr[max];
	}

	/**
	 * 获取int数组中最小值
	 */
	public static int getIntArrayMin(int[] arr) {
		int min = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < arr[min]) {
				min = i;
			}
		}
		return arr[min];
	}

	/**
	 * 获取int数组平均值
	 */
	public static int getIntArrayAvg(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}

	/**
	 * 获取double数组中最大值
	 */
	public static double getDoubleArrayMax(double[] arr) {
		Double maxResult = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > maxResult) {
				maxResult = arr[i];
			}
		}
		return maxResult;
	}

	/**
	 * 获取double数组中最小值
	 */
	public static double getDoubleArrayMin(double[] arr) {
		Double minResult = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < minResult) {
				minResult = arr[i];
			}
		}
		return minResult;
	}

	/**
	 * 获取double数组平均值
	 */
	public static double getDoubleArrayAvg(double[] arr) {
		Double sum = 0.0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}

}
