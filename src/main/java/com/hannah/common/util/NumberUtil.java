package com.hannah.common.util;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * @author longrm
 * @date 2012-6-5
 */
public class NumberUtil {

	public final static char POINT = '.';

	public final static char[] DIGIT_UPPER = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' };

	public final static char[] BIT_UNIT = { '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿' };	// '拾', '佰', '仟', '万'...

	public final static int ILLEGAL = -999999999;

	/**
	 * 将0~9数字转为大写
	 * @param n
	 * @return
	 */
	public static char digitToUpperCase(int n) {
		return DIGIT_UPPER[n];
	}

	/**
	 * 将大写数字转为0~9
	 * @param u
	 * @return
	 */
	public static int digitToLowerCase(char u) {
		for (int i = 0; i < DIGIT_UPPER.length; i++) {
			if (u == DIGIT_UPPER[i])
				return i;
		}
		return ILLEGAL;
	}

	/**
	 * 将位转为计量单位
	 * @param b
	 * @return 小于0时返回'\u0000'，否则返回相应的位
	 */
	public static char bitToUnit(int b) {
		if (b <= 0)
			return Character.MIN_VALUE;
		// 过拾（10^b）
		else {
			int exBit = (b - 1) % BIT_UNIT.length;
			return BIT_UNIT[exBit];
		}
	}

	/**
	 * 将计量单位转为位（小数位无单位）
	 * @param u
	 * @param priorBit
	 *            前面的整数位
	 * @return
	 */
	public static int unitToBit(char u, int priorBit) {
		// 过拾
		int bit = 0;
		bit += (priorBit - 1) / BIT_UNIT.length * BIT_UNIT.length; // 前一位超过了亿，补上
		// 从前一位开始循环
		int exBit = (priorBit - 1) % BIT_UNIT.length;
		for (int i = exBit + 1; i < BIT_UNIT.length; i++) {
			if (u == BIT_UNIT[i])
				return bit + i + 1;
		}
		// 没找到从新开始循环至exBit
		bit += BIT_UNIT.length;
		for (int i = 0; i <= exBit; i++) {
			if (u == BIT_UNIT[i])
				return bit + i + 1;
		}
		return ILLEGAL;
	}

	/**
	 * 将数字转换成中文大写
	 * @param number
	 * @return
	 */
	public static String toUpperCase(BigDecimal number) {
		String result = "";

		String str = String.valueOf(number.abs());
		int pointIndex = str.indexOf(".");
		pointIndex = pointIndex == -1 ? pointIndex : str.length() - 1 - pointIndex;
		// 从低位到高位
		for (int i = 0; i < str.length(); i++) {
			// 小数点
			if (i == pointIndex)
				result = "点" + result;
			else {
				int n = str.charAt(str.length() - 1 - i) - '0';
				// 小数位或个位没有计量单位
				if (i < pointIndex || i == pointIndex + 1)
					result = digitToUpperCase(n) + result;
				else if (pointIndex == -1)
					result = digitToUpperCase(n) + "" + bitToUnit(i) + result;
				else
					result = digitToUpperCase(n) + "" + bitToUnit(i - pointIndex - 1) + result;
			}
		}

		String decimalStr = pointIndex == -1 ? "" : result.substring(result.indexOf("点"));
		String integerStr = pointIndex == -1 ? result : result.substring(0, result.indexOf("点"));

		integerStr = integerStr.replaceAll("零仟", "零");
		integerStr = integerStr.replaceAll("零佰", "零");
		integerStr = integerStr.replaceAll("零拾", "零");
		integerStr = integerStr.replaceAll("零亿", "亿");
		integerStr = integerStr.replaceAll("零万", "万");
		integerStr = integerStr.replaceAll("零元", "元");
		integerStr = integerStr.replaceAll("零角", "零");
		integerStr = integerStr.replaceAll("零分", "零");

		integerStr = integerStr.replaceAll("零零", "零");
		integerStr = integerStr.replaceAll("零亿", "亿");
		integerStr = integerStr.replaceAll("零零", "零");
		integerStr = integerStr.replaceAll("零万", "万");
		integerStr = integerStr.replaceAll("零零", "零");
		integerStr = integerStr.replaceAll("零元", "元");
		integerStr = integerStr.replaceAll("亿万", "亿");

		integerStr = integerStr.replaceAll("零$", "");

		return (number.compareTo(BigDecimal.ZERO) == -1 ? "负" : "") + integerStr + decimalStr;
	}

	/**
	 * 将数字大写转为小写（繁体字不支持）
	 * @param upperNumber
	 * @return
	 */
	public static BigDecimal toLowerCase(String upperNumber) {
		BigDecimal result = BigDecimal.ZERO;

		int direction = 1;
		if (upperNumber.startsWith("负")) {
			direction = -1;
			upperNumber = upperNumber.substring(1);
		}

		// 处理小数位
		int pointIndex = upperNumber.indexOf("点");
		if (pointIndex != -1) {
			String decimalStr = "";
			String upperDecimal = upperNumber.substring(pointIndex + 1);
			for (int i = upperDecimal.length() - 1; i >= 0; i--) {
				char up = upperDecimal.charAt(i);
				int number = digitToLowerCase(up);
				if (number == ILLEGAL)
					throw new RuntimeException("非法的数字大写格式！");
				decimalStr = number + decimalStr;
			}
			decimalStr = "0." + decimalStr;
			result = new BigDecimal(decimalStr);
			// 整数
			upperNumber = upperNumber.substring(0, pointIndex);
		}

		String upperInteger = upperNumber.replaceAll("零", "");
		int bit = 0;
		int priorBit = 0;
		for (int i = upperInteger.length() - 1; i >= 0; i--) {
			// 计量单位
			priorBit = bit;
			int tempBit = 0;
			do {
				char u = upperInteger.charAt(i--);
				tempBit = unitToBit(u, bit);
				if (tempBit == ILLEGAL) {
					if (bit == priorBit) {
						// 个位没有计量单位
						if (bit == 0)
							break;
						else
							throw new RuntimeException("非法的数字大写格式！");
					}
				}
				// 过万过亿必须加上万、亿
				else if (tempBit - bit >= 8
						|| (tempBit % 4 != 0 && bit % 4 != 0 && tempBit % 4 <= bit % 4))
					throw new RuntimeException("非法的数字大写格式！");
				else
					bit = tempBit;
			} while (tempBit != ILLEGAL);
			// 数字
			char up = upperInteger.charAt(++i);
			int number = digitToLowerCase(up);
			if (number == ILLEGAL)
				throw new RuntimeException("非法的数字大写格式！");
			// 计算
			result = result.add(new BigDecimal(String.valueOf(number * Math.pow(10, bit))));
		}

		return result.multiply(new BigDecimal(direction));
	}

	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	public static boolean isOperator(char c) {
		switch (c) {
		case '+':
			return true;
		case '-':
			return true;
		case '*':
			return true;
		case '/':
			return true;
		default:
			return false;
		}
	}

	/**
	 * 获取运算符优先级
	 * @param operator
	 * @return
	 */
	public static int getOperatorLevel(char operator) {
		switch (operator) {
		case '+':
			return 1;
		case '-':
			return 1;
		case '*':
			return 2;
		case '/':
			return 2;
		default:
			return 0;
		}
	}

	/**
	 * 计算两个数运算结果
	 * @param d1
	 * @param d2
	 * @param operator
	 * @return
	 */
	public static BigDecimal calculate(BigDecimal d1, BigDecimal d2, char operator) {
		switch (operator) {
		case '+':
			return d1.add(d2);
		case '-':
			return d1.subtract(d2);
		case '*':
			return d1.multiply(d2);
		case '/':
			return d1.divide(d2, 10, BigDecimal.ROUND_HALF_EVEN);	// 最大精度为10
		default:
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 运行四则运算
	 * @param arithmetic
	 * @return
	 */
	public static BigDecimal calculateArithmetic(String arithmetic) {
		// 数字栈
		Stack<BigDecimal> numberStack = new Stack<BigDecimal>();
		// 运算符栈：+ - * / ( )
		Stack<Character> operatorStack = new Stack<Character>();
		// 记录数字的起始位置
		int start = 0;
		int end = 0;
		for (int i = 0; i < arithmetic.length(); i++) {
			char c = arithmetic.charAt(i);
			if (isDigit(c) || c == POINT) {
				if (start == end) {
					start = i;
					end = i + 1;
				} else
					end ++;
			} else {
				// 截取数字，压入数字栈
				if (start != end) {
					BigDecimal d = new BigDecimal(arithmetic.substring(start, end));
					numberStack.push(d);
					start = end;
				}
				if (isOperator(c)) {
					if (operatorStack.empty()) {
						operatorStack.push(c);
						continue;
					}
					char priorOperator = operatorStack.peek();
					// 如果当前运算符优先级等于小于栈顶的运算符优先级，那么取出数字栈栈顶的两个数字运算
					if (getOperatorLevel(c) <= getOperatorLevel(priorOperator)) {
						BigDecimal d1 = numberStack.pop();
						BigDecimal d2 = numberStack.pop();
						char operator = operatorStack.pop();
						// 新的运算结果压入数字栈
						numberStack.push(calculate(d1, d2, operator));
						operatorStack.push(c);
					} else
						operatorStack.push(c);
				} else if (c == '(')
					operatorStack.push(c);
				else if (c == ')') {
					char operator;
					// 循环运算直到找到 (
					while ((operator = operatorStack.pop()) != '(') {
						BigDecimal d1 = numberStack.pop();
						BigDecimal d2 = numberStack.pop();
						numberStack.push(calculate(d1, d2, operator));
					}
				} else if (c == ' ')
					continue;
				else
					throw new RuntimeException("char '" + c + "' is illegal!");
			}
		}
		// 最后一个数字
		if (start != end) {
			BigDecimal d = new BigDecimal(arithmetic.substring(start, end));
			numberStack.push(d);
		}
		// 运行最后的结果
		char operator;
		while (operatorStack.size() > 0) {
			BigDecimal d1 = numberStack.pop();
			BigDecimal d2 = numberStack.pop();
			operator = operatorStack.pop();
			numberStack.push(calculate(d1, d2, operator));
		}
		// 取出最后一个数字就是最终值
		if (numberStack.size() == 1 && operatorStack.size() == 0)
			return numberStack.peek();
		else
			throw new RuntimeException("arithmetic is illegal!\n" + "numberStack is: " + numberStack + 
					"\toperatorStack is: " + operatorStack);
	}

	/**
	 * 判断是否为质数
	 * @param x
	 * @return
	 */
	public static boolean isPrime(int x) {
		if (x <= 0)
			return false;

		if (x <= 7) {
			if (x == 1 || x == 2 || x == 3 || x == 5 || x == 7)
				return true;
			else
				return false;
		}

		if (x % 2 == 0)
			return false;
		if (x % 3 == 0)
			return false;
		if (x % 5 == 0)
			return false;

		int c = 7;
		// 最大的整除数为当前数的平方根的整数
		int end = (int) Math.sqrt(x);
		while (c <= end) {
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 6;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 6;
		}
		return true;
	}

}
