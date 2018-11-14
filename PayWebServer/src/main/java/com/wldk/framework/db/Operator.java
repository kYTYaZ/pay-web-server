/**
 * 
 */
package com.wldk.framework.db;

import java.text.MessageFormat;
import java.util.StringTokenizer;

/**
 * 操作符常量定义<br>
 * 
 * @author Administrator
 * 
 */
public enum Operator {
	EQ("="), BETWEEN("between"), GE(">="), GT(">"), LE("<="), LT("<"), NE("<>"), ISNULL(
			"is null"), ISNOTNULL("is not null"), IN("in"), LIKE("like");
	public String op;

	Operator(String op) {
		this.op = op;
	}

	public String getOp() {
		return this.op;
	}

	public static Operator op(String op) {
		if (op == null || op.equals("")) {
			return Operator.EQ;
		} else if (op.trim().equalsIgnoreCase("eq")) {
			return Operator.EQ;
		} else if (op.trim().equalsIgnoreCase("ge")) {
			return Operator.GE;
		} else if (op.trim().equalsIgnoreCase("gt")) {
			return Operator.GT;
		} else if (op.trim().equalsIgnoreCase("le")) {
			return Operator.LE;
		} else if (op.trim().equalsIgnoreCase("lt")) {
			return Operator.LT;
		} else if (op.trim().equalsIgnoreCase("ne")) {
			return Operator.NE;
		} else if (op.trim().equalsIgnoreCase("isNull")) {
			return Operator.ISNULL;
		} else if (op.trim().equalsIgnoreCase("isNotNull")) {
			return Operator.ISNOTNULL;
		} else if (op.trim().equalsIgnoreCase("like")) {
			return Operator.LIKE;
		} else if (op.trim().equalsIgnoreCase("in")) {
			return Operator.IN;
		}
		return Operator.EQ;
	}

	public static String expression(Operator op, String field, String value) {
		StringBuffer sb = new StringBuffer();
		switch (op) {
		case EQ:
			sb.append(field).append(EQ.op).append("'").append(value).append(
					"' ").toString();
			break;
		case BETWEEN:
			StringTokenizer st = new StringTokenizer(value, "，, ");
			if (st.countTokens() > 1) {
				return MessageFormat.format(field
						+ " between ''{0}'' and ''{1}''", st.nextToken(), st
						.nextToken());
			}
			break;
		case GE:
			sb.append(field).append(GE.op).append("'").append(value).append(
					"' ").toString();
			break;
		case GT:
			sb.append(field).append(GT.op).append("'").append(value).append(
					"' ").toString();
			break;
		case LE:
			sb.append(field).append(LE.op).append("'").append(value).append(
					"' ").toString();
			break;
		case LT:
			sb.append(field).append(LT.op).append("'").append(value).append(
					"' ").toString();
			break;
		case NE:
			sb.append(field).append(NE.op).append("'").append(value).append(
					"' ").toString();
			break;
		case ISNULL:
			sb.append(field).append(" ").append(ISNULL.op).toString();
			break;
		case ISNOTNULL:
			sb.append(field).append(" ").append(ISNOTNULL.op).toString();
			break;
		case IN:
			sb.append(field).append(" ").append(IN.op).append(" (");
			st = new StringTokenizer(value, "，, ");
			while (st.hasMoreTokens()) {
				sb.append("'").append(st.nextToken()).append("'");
				if (st.countTokens() != 0) {
					sb.append(", ");
				}
			}
			sb.append(") ").toString();
			break;
		case LIKE:
			sb.append(field).append(" ").append(LIKE.op).append(" '%").append(
					value).append("%' ").toString();
			break;
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		// System.out.println(Operator.expression(Operator.BETWEEN, "status",
		// "0,1"));
		// System.out.println(Operator.GE.getOp());
	}
}
