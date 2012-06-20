package SiteView.ecc.tools;

import java.text.DecimalFormat;

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
	 * ��ȡint���������ֵ
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
	 * ��ȡint��������Сֵ
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
	 * ��ȡint����ƽ��ֵ
	 */
	public static int getIntArrayAvg(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}

	/**
	 * ��ȡdouble���������ֵ
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
	 * ��ȡdouble��������Сֵ
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
	 * ��ȡdouble����ƽ��ֵ
	 */
	public static double getDoubleArrayAvg(double[] arr) {
		Double sum = 0.0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}
}
